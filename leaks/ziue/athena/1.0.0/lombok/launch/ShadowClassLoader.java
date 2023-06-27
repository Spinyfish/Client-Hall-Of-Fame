package lombok.launch;

import java.util.concurrent.*;
import java.util.jar.*;
import java.net.*;
import java.io.*;
import java.util.zip.*;
import java.util.*;

class ShadowClassLoader extends ClassLoader
{
    private static final String SELF_NAME = "lombok/launch/ShadowClassLoader.class";
    private static final ConcurrentMap<String, Class<?>> highlanderMap;
    private final String SELF_BASE;
    private final File SELF_BASE_FILE;
    private final int SELF_BASE_LENGTH;
    private final List<File> override;
    private final String sclSuffix;
    private final List<String> parentExclusion;
    private final List<String> highlanders;
    private final Set<ClassLoader> prependedParentLoaders;
    private final Map<String, Object> mapJarPathToTracker;
    private static final Map<Object, String> mapTrackerToJarPath;
    private static final Map<Object, Set<String>> mapTrackerToJarContents;
    private Map<String, Boolean> fileRootCache;
    private Map<String, Boolean> jarLocCache;
    
    static {
        highlanderMap = new ConcurrentHashMap<String, Class<?>>();
        mapTrackerToJarPath = new WeakHashMap<Object, String>();
        mapTrackerToJarContents = new WeakHashMap<Object, Set<String>>();
    }
    
    public void prependParent(final ClassLoader loader) {
        if (loader == null) {
            return;
        }
        if (loader == this.getParent()) {
            return;
        }
        this.prependedParentLoaders.add(loader);
    }
    
    ShadowClassLoader(final ClassLoader source, final String sclSuffix, final String selfBase, final List<String> parentExclusion, final List<String> highlanders) {
        super(source);
        this.override = new ArrayList<File>();
        this.parentExclusion = new ArrayList<String>();
        this.highlanders = new ArrayList<String>();
        this.prependedParentLoaders = Collections.newSetFromMap(new IdentityHashMap<ClassLoader, Boolean>());
        this.mapJarPathToTracker = new HashMap<String, Object>();
        this.fileRootCache = new HashMap<String, Boolean>();
        this.jarLocCache = new HashMap<String, Boolean>();
        this.sclSuffix = sclSuffix;
        if (parentExclusion != null) {
            for (String pe : parentExclusion) {
                pe = pe.replace(".", "/");
                if (!pe.endsWith("/")) {
                    pe = String.valueOf(pe) + "/";
                }
                this.parentExclusion.add(pe);
            }
        }
        if (highlanders != null) {
            for (final String hl : highlanders) {
                this.highlanders.add(hl);
            }
        }
        if (selfBase != null) {
            this.SELF_BASE = selfBase;
            this.SELF_BASE_LENGTH = selfBase.length();
        }
        else {
            final URL sclClassUrl = ShadowClassLoader.class.getResource("ShadowClassLoader.class");
            final String sclClassStr = (sclClassUrl == null) ? null : sclClassUrl.toString();
            if (sclClassStr == null || !sclClassStr.endsWith("lombok/launch/ShadowClassLoader.class")) {
                final ClassLoader cl = ShadowClassLoader.class.getClassLoader();
                throw new RuntimeException("ShadowLoader can't find itself. SCL loader type: " + ((cl == null) ? "*NULL*" : cl.getClass().toString()));
            }
            this.SELF_BASE_LENGTH = sclClassStr.length() - "lombok/launch/ShadowClassLoader.class".length();
            final String decoded = urlDecode(sclClassStr.substring(0, this.SELF_BASE_LENGTH));
            this.SELF_BASE = decoded;
        }
        if (this.SELF_BASE.startsWith("jar:file:") && this.SELF_BASE.endsWith("!/")) {
            this.SELF_BASE_FILE = new File(this.SELF_BASE.substring(9, this.SELF_BASE.length() - 2));
        }
        else if (this.SELF_BASE.startsWith("file:")) {
            this.SELF_BASE_FILE = new File(this.SELF_BASE.substring(5));
        }
        else {
            this.SELF_BASE_FILE = new File(this.SELF_BASE);
        }
        final String scl = System.getProperty("shadow.override." + sclSuffix);
        if (scl != null && !scl.isEmpty()) {
            String[] split;
            for (int length = (split = scl.split("\\s*" + ((File.pathSeparatorChar == 59) ? ";" : ":") + "\\s*")).length, i = 0; i < length; ++i) {
                final String part = split[i];
                if (part.endsWith("/*") || part.endsWith(String.valueOf(File.separator) + "*")) {
                    this.addOverrideJarDir(part.substring(0, part.length() - 2));
                }
                else {
                    this.addOverrideClasspathEntry(part);
                }
            }
        }
    }
    
    private Set<String> getOrMakeJarListing(final String absolutePathToJar) {
        synchronized (ShadowClassLoader.mapTrackerToJarPath) {
            final Object ourTracker = this.mapJarPathToTracker.get(absolutePathToJar);
            if (ourTracker != null) {
                // monitorexit(ShadowClassLoader.mapTrackerToJarPath)
                return ShadowClassLoader.mapTrackerToJarContents.get(ourTracker);
            }
            for (final Map.Entry<Object, String> entry : ShadowClassLoader.mapTrackerToJarPath.entrySet()) {
                if (entry.getValue().equals(absolutePathToJar)) {
                    final Object otherTracker = entry.getKey();
                    this.mapJarPathToTracker.put(absolutePathToJar, otherTracker);
                    // monitorexit(ShadowClassLoader.mapTrackerToJarPath)
                    return ShadowClassLoader.mapTrackerToJarContents.get(otherTracker);
                }
            }
            final Object newTracker = new Object();
            final Set<String> jarMembers = this.getJarMemberSet(absolutePathToJar);
            ShadowClassLoader.mapTrackerToJarContents.put(newTracker, jarMembers);
            ShadowClassLoader.mapTrackerToJarPath.put(newTracker, absolutePathToJar);
            this.mapJarPathToTracker.put(absolutePathToJar, newTracker);
            // monitorexit(ShadowClassLoader.mapTrackerToJarPath)
            return jarMembers;
        }
    }
    
    private Set<String> getJarMemberSet(final String absolutePathToJar) {
        try {
            final int shiftBits = 1;
            final JarFile jar = new JarFile(absolutePathToJar);
            int jarSizePower2 = Integer.highestOneBit(jar.size());
            if (jarSizePower2 != jar.size()) {
                jarSizePower2 <<= 1;
            }
            if (jarSizePower2 == 0) {
                jarSizePower2 = 1;
            }
            final Set<String> jarMembers = new HashSet<String>(jarSizePower2 >> shiftBits, (float)(1 << shiftBits));
            try {
                final Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    final JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.isDirectory()) {
                        continue;
                    }
                    jarMembers.add(jarEntry.getName());
                }
            }
            catch (Exception ex) {
                return jarMembers;
            }
            finally {
                jar.close();
            }
            jar.close();
            return jarMembers;
        }
        catch (Exception ex2) {
            return Collections.emptySet();
        }
    }
    
    private URL getResourceFromLocation(final String name, final String altName, final File location) {
        if (location.isDirectory()) {
            try {
                if (altName != null) {
                    final File f = new File(location, altName);
                    if (f.isFile() && f.canRead()) {
                        return f.toURI().toURL();
                    }
                }
                final File f = new File(location, name);
                if (f.isFile() && f.canRead()) {
                    return f.toURI().toURL();
                }
                return null;
            }
            catch (MalformedURLException ex) {
                return null;
            }
        }
        if (!location.isFile() || !location.canRead()) {
            return null;
        }
        File absoluteFile;
        try {
            absoluteFile = location.getCanonicalFile();
        }
        catch (Exception ex2) {
            absoluteFile = location.getAbsoluteFile();
        }
        final Set<String> jarContents = this.getOrMakeJarListing(absoluteFile.getAbsolutePath());
        final String absoluteUri = absoluteFile.toURI().toString();
        try {
            if (jarContents.contains(altName)) {
                return new URI("jar:" + absoluteUri + "!/" + altName).toURL();
            }
        }
        catch (Exception ex3) {}
        try {
            if (jarContents.contains(name)) {
                return new URI("jar:" + absoluteUri + "!/" + name).toURL();
            }
        }
        catch (Exception ex4) {}
        return null;
    }
    
    private boolean partOfShadow(final String item, final String name) {
        return !name.startsWith("java/") && !name.startsWith("sun/") && (this.inOwnBase(item, name) || this.isPartOfShadowSuffix(item, name, this.sclSuffix));
    }
    
    private boolean inOwnBase(final String item, final String name) {
        return item != null && (item.length() == this.SELF_BASE_LENGTH + name.length() && this.SELF_BASE.regionMatches(0, item, 0, this.SELF_BASE_LENGTH));
    }
    
    private static boolean sclFileContainsSuffix(final InputStream in, final String suffix) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            line = line.trim();
            if (!line.isEmpty()) {
                if (line.charAt(0) != '#') {
                    if (line.equals(suffix)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static String urlDecode(final String in) {
        final String plusFixed = in.replaceAll("\\+", "%2B");
        try {
            return URLDecoder.decode(plusFixed, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new InternalError("UTF-8 not supported");
        }
    }
    
    private boolean isPartOfShadowSuffixFileBased(final String fileRoot, final String suffix) {
        final String key = String.valueOf(fileRoot) + "::" + suffix;
        final Boolean existing = this.fileRootCache.get(key);
        if (existing != null) {
            return existing;
        }
        final File f = new File(String.valueOf(fileRoot) + "/META-INF/ShadowClassLoader");
        try {
            final FileInputStream fis = new FileInputStream(f);
            try {
                final boolean v = sclFileContainsSuffix(fis, suffix);
                this.fileRootCache.put(key, v);
                return v;
            }
            finally {
                fis.close();
            }
        }
        catch (FileNotFoundException ex) {
            this.fileRootCache.put(key, false);
            return false;
        }
        catch (IOException ex2) {
            this.fileRootCache.put(key, false);
            return false;
        }
    }
    
    private boolean isPartOfShadowSuffixJarBased(String jarLoc, final String suffix) {
        final String key = String.valueOf(jarLoc) + "::" + suffix;
        final Boolean existing = this.jarLocCache.get(key);
        if (existing != null) {
            return existing;
        }
        if (jarLoc.startsWith("file:/")) {
            jarLoc = urlDecode(jarLoc.substring(5));
        }
        try {
            final FileInputStream jar = new FileInputStream(jarLoc);
            try {
                final ZipInputStream zip = new ZipInputStream(jar);
                try {
                    while (true) {
                        final ZipEntry entry = zip.getNextEntry();
                        if (entry == null) {
                            this.jarLocCache.put(key, false);
                            return false;
                        }
                        if (!"META-INF/ShadowClassLoader".equals(entry.getName())) {
                            continue;
                        }
                        final boolean v = sclFileContainsSuffix(zip, suffix);
                        this.jarLocCache.put(key, v);
                        return v;
                    }
                }
                finally {
                    zip.close();
                }
            }
            finally {
                jar.close();
            }
        }
        catch (FileNotFoundException ex) {
            this.jarLocCache.put(key, false);
            return false;
        }
        catch (IOException ex2) {
            this.jarLocCache.put(key, false);
            return false;
        }
    }
    
    private boolean isPartOfShadowSuffix(String url, final String name, final String suffix) {
        if (url == null) {
            return false;
        }
        if (url.startsWith("file:/")) {
            url = urlDecode(url.substring(5));
            if (url.length() <= name.length() || !url.endsWith(name) || url.charAt(url.length() - name.length() - 1) != '/') {
                return false;
            }
            final String fileRoot = url.substring(0, url.length() - name.length() - 1);
            return this.isPartOfShadowSuffixFileBased(fileRoot, suffix);
        }
        else {
            if (!url.startsWith("jar:")) {
                return false;
            }
            final int sep = url.indexOf(33);
            if (sep == -1) {
                return false;
            }
            final String jarLoc = url.substring(4, sep);
            return this.isPartOfShadowSuffixJarBased(jarLoc, suffix);
        }
    }
    
    @Override
    public Enumeration<URL> getResources(final String name) throws IOException {
        String altName = null;
        if (name.endsWith(".class")) {
            altName = String.valueOf(name.substring(0, name.length() - 6)) + ".SCL." + this.sclSuffix;
        }
        final Vector<URL> vector = new Vector<URL>();
        for (final File ce : this.override) {
            final URL url = this.getResourceFromLocation(name, altName, ce);
            if (url != null) {
                vector.add(url);
            }
        }
        if (this.override.isEmpty()) {
            final URL fromSelf = this.getResourceFromLocation(name, altName, this.SELF_BASE_FILE);
            if (fromSelf != null) {
                vector.add(fromSelf);
            }
        }
        final Enumeration<URL> sec = super.getResources(name);
        while (sec.hasMoreElements()) {
            final URL item = sec.nextElement();
            if (this.isPartOfShadowSuffix(item.toString(), name, this.sclSuffix)) {
                vector.add(item);
            }
        }
        if (altName != null) {
            final Enumeration<URL> tern = super.getResources(altName);
            while (tern.hasMoreElements()) {
                final URL item2 = tern.nextElement();
                if (this.isPartOfShadowSuffix(item2.toString(), altName, this.sclSuffix)) {
                    vector.add(item2);
                }
            }
        }
        return vector.elements();
    }
    
    @Override
    public URL getResource(final String name) {
        return this.getResource_(name, false);
    }
    
    private URL getResource_(final String name, final boolean noSuper) {
        String altName = null;
        if (name.endsWith(".class")) {
            altName = String.valueOf(name.substring(0, name.length() - 6)) + ".SCL." + this.sclSuffix;
        }
        for (final File ce : this.override) {
            final URL url = this.getResourceFromLocation(name, altName, ce);
            if (url != null) {
                return url;
            }
        }
        if (!this.override.isEmpty()) {
            if (noSuper) {
                return null;
            }
            if (altName != null) {
                try {
                    final URL res = this.getResourceSkippingSelf(altName);
                    if (res != null) {
                        return res;
                    }
                }
                catch (IOException ex) {}
            }
            try {
                return this.getResourceSkippingSelf(name);
            }
            catch (IOException ex2) {
                return null;
            }
        }
        final URL url2 = this.getResourceFromLocation(name, altName, this.SELF_BASE_FILE);
        if (url2 != null) {
            return url2;
        }
        if (altName != null) {
            final URL res2 = super.getResource(altName);
            if (res2 != null && (!noSuper || this.partOfShadow(res2.toString(), altName))) {
                return res2;
            }
        }
        final URL res2 = super.getResource(name);
        if (res2 != null && (!noSuper || this.partOfShadow(res2.toString(), name))) {
            return res2;
        }
        return null;
    }
    
    private boolean exclusionListMatch(final String name) {
        for (final String pe : this.parentExclusion) {
            if (name.startsWith(pe)) {
                return true;
            }
        }
        return false;
    }
    
    private URL getResourceSkippingSelf(final String name) throws IOException {
        URL candidate = super.getResource(name);
        if (candidate == null) {
            return null;
        }
        if (!this.partOfShadow(candidate.toString(), name)) {
            return candidate;
        }
        final Enumeration<URL> en = super.getResources(name);
        while (en.hasMoreElements()) {
            candidate = en.nextElement();
            if (!this.partOfShadow(candidate.toString(), name)) {
                return candidate;
            }
        }
        return null;
    }
    
    public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        final Class<?> alreadyLoaded = this.findLoadedClass(name);
        if (alreadyLoaded != null) {
            return alreadyLoaded;
        }
        if (this.highlanders.contains(name)) {
            final Class<?> c = ShadowClassLoader.highlanderMap.get(name);
            if (c != null) {
                return c;
            }
        }
        final String fileNameOfClass = String.valueOf(name.replace(".", "/")) + ".class";
        URL res = this.getResource_(fileNameOfClass, true);
        if (res == null && !this.exclusionListMatch(fileNameOfClass)) {
            try {
                for (final ClassLoader pre : this.prependedParentLoaders) {
                    try {
                        final Class<?> loadClass = pre.loadClass(name);
                        if (loadClass != null) {
                            return loadClass;
                        }
                        continue;
                    }
                    catch (Throwable t) {}
                }
                return super.loadClass(name, resolve);
            }
            catch (ClassNotFoundException cnfe) {
                res = this.getResource_("secondaryLoading.SCL." + this.sclSuffix + "/" + name.replace(".", "/") + ".SCL." + this.sclSuffix, true);
                if (res == null) {
                    throw cnfe;
                }
            }
        }
        if (res == null) {
            throw new ClassNotFoundException(name);
        }
        return this.urlToDefineClass(name, res, resolve);
    }
    
    private Class<?> urlToDefineClass(final String name, final URL res, final boolean resolve) throws ClassNotFoundException {
        int p = 0;
        byte[] b;
        try {
            final InputStream in = res.openStream();
            try {
                b = new byte[65536];
                while (true) {
                    final int r = in.read(b, p, b.length - p);
                    if (r == -1) {
                        break;
                    }
                    p += r;
                    if (p != b.length) {
                        continue;
                    }
                    final byte[] nb = new byte[b.length * 2];
                    System.arraycopy(b, 0, nb, 0, p);
                    b = nb;
                }
            }
            finally {
                in.close();
            }
            in.close();
        }
        catch (IOException e) {
            throw new ClassNotFoundException("I/O exception reading class " + name, e);
        }
        Class<?> c;
        try {
            c = this.defineClass(name, b, 0, p);
        }
        catch (LinkageError e2) {
            if (this.highlanders.contains(name)) {
                final Class<?> alreadyDefined = ShadowClassLoader.highlanderMap.get(name);
                if (alreadyDefined != null) {
                    return alreadyDefined;
                }
            }
            try {
                c = this.findLoadedClass(name);
            }
            catch (LinkageError linkageError) {
                throw e2;
            }
            if (c == null) {
                throw e2;
            }
        }
        if (this.highlanders.contains(name)) {
            final Class<?> alreadyDefined2 = ShadowClassLoader.highlanderMap.putIfAbsent(name, c);
            if (alreadyDefined2 != null) {
                c = alreadyDefined2;
            }
        }
        if (resolve) {
            this.resolveClass(c);
        }
        return c;
    }
    
    public void addOverrideJarDir(final String dir) {
        final File f = new File(dir);
        File[] listFiles;
        for (int length = (listFiles = f.listFiles()).length, i = 0; i < length; ++i) {
            final File j = listFiles[i];
            if (j.getName().toLowerCase().endsWith(".jar") && j.canRead() && j.isFile()) {
                this.override.add(j);
            }
        }
    }
    
    public void addOverrideClasspathEntry(final String entry) {
        this.override.add(new File(entry));
    }
}
