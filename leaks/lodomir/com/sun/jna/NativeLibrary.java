/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.Function;
import com.sun.jna.FunctionMapper;
import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class NativeLibrary {
    private long handle;
    private final String libraryName;
    private final String libraryPath;
    private final Map<String, Function> functions = new HashMap<String, Function>();
    final int callFlags;
    private String encoding;
    final Map<String, ?> options;
    private static final Map<String, Reference<NativeLibrary>> libraries = new HashMap<String, Reference<NativeLibrary>>();
    private static final Map<String, List<String>> searchPaths = Collections.synchronizedMap(new HashMap());
    private static final List<String> librarySearchPath = new ArrayList<String>();
    private static final int DEFAULT_OPEN_OPTIONS = -1;

    private static String functionKey(String name, int flags, String encoding) {
        return name + "|" + flags + "|" + encoding;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private NativeLibrary(String libraryName, String libraryPath, long handle, Map<String, ?> options) {
        int callingConvention;
        this.libraryName = this.getLibraryName(libraryName);
        this.libraryPath = libraryPath;
        this.handle = handle;
        Object option = options.get("calling-convention");
        this.callFlags = callingConvention = option instanceof Number ? ((Number)option).intValue() : 0;
        this.options = options;
        this.encoding = (String)options.get("string-encoding");
        if (this.encoding == null) {
            this.encoding = Native.getDefaultStringEncoding();
        }
        if (Platform.isWindows() && "kernel32".equals(this.libraryName.toLowerCase())) {
            Map<String, Function> map = this.functions;
            synchronized (map) {
                Function f = new Function(this, "GetLastError", 63, this.encoding){

                    @Override
                    Object invoke(Object[] args, Class<?> returnType, boolean b, int fixedArgs) {
                        return Native.getLastError();
                    }

                    @Override
                    Object invoke(Method invokingMethod, Class<?>[] paramTypes, Class<?> returnType, Object[] inArgs, Map<String, ?> options) {
                        return Native.getLastError();
                    }
                };
                this.functions.put(NativeLibrary.functionKey("GetLastError", this.callFlags, this.encoding), f);
            }
        }
    }

    private static int openFlags(Map<String, ?> options) {
        Object opt = options.get("open-flags");
        if (opt instanceof Number) {
            return ((Number)opt).intValue();
        }
        return -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static NativeLibrary loadLibrary(String libraryName, Map<String, ?> options) {
        long handle;
        String libraryPath;
        block51: {
            List<String> customPaths;
            if (Native.DEBUG_LOAD) {
                System.out.println("Looking for library '" + libraryName + "'");
            }
            boolean isAbsolutePath = new File(libraryName).isAbsolute();
            ArrayList<String> searchPath = new ArrayList<String>();
            int openFlags = NativeLibrary.openFlags(options);
            String webstartPath = Native.getWebStartLibraryPath(libraryName);
            if (webstartPath != null) {
                if (Native.DEBUG_LOAD) {
                    System.out.println("Adding web start path " + webstartPath);
                }
                searchPath.add(webstartPath);
            }
            if ((customPaths = searchPaths.get(libraryName)) != null) {
                List<String> list = customPaths;
                synchronized (list) {
                    searchPath.addAll(0, customPaths);
                }
            }
            if (Native.DEBUG_LOAD) {
                System.out.println("Adding paths from jna.library.path: " + System.getProperty("jna.library.path"));
            }
            searchPath.addAll(NativeLibrary.initPaths("jna.library.path"));
            libraryPath = NativeLibrary.findLibraryPath(libraryName, searchPath);
            handle = 0L;
            try {
                if (Native.DEBUG_LOAD) {
                    System.out.println("Trying " + libraryPath);
                }
                handle = Native.open(libraryPath, openFlags);
            }
            catch (UnsatisfiedLinkError e) {
                if (Native.DEBUG_LOAD) {
                    System.out.println("Adding system paths: " + librarySearchPath);
                }
                searchPath.addAll(librarySearchPath);
            }
            try {
                if (handle == 0L) {
                    libraryPath = NativeLibrary.findLibraryPath(libraryName, searchPath);
                    if (Native.DEBUG_LOAD) {
                        System.out.println("Trying " + libraryPath);
                    }
                    if ((handle = Native.open(libraryPath, openFlags)) == 0L) {
                        throw new UnsatisfiedLinkError("Failed to load library '" + libraryName + "'");
                    }
                }
            }
            catch (UnsatisfiedLinkError e) {
                if (Platform.isAndroid()) {
                    try {
                        if (Native.DEBUG_LOAD) {
                            System.out.println("Preload (via System.loadLibrary) " + libraryName);
                        }
                        System.loadLibrary(libraryName);
                        handle = Native.open(libraryPath, openFlags);
                    }
                    catch (UnsatisfiedLinkError e2) {
                        e = e2;
                    }
                } else if (Platform.isLinux() || Platform.isFreeBSD()) {
                    if (Native.DEBUG_LOAD) {
                        System.out.println("Looking for version variants");
                    }
                    if ((libraryPath = NativeLibrary.matchLibrary(libraryName, searchPath)) != null) {
                        if (Native.DEBUG_LOAD) {
                            System.out.println("Trying " + libraryPath);
                        }
                        try {
                            handle = Native.open(libraryPath, openFlags);
                        }
                        catch (UnsatisfiedLinkError e2) {
                            e = e2;
                        }
                    }
                } else if (Platform.isMac() && !libraryName.endsWith(".dylib")) {
                    if (Native.DEBUG_LOAD) {
                        System.out.println("Looking for matching frameworks");
                    }
                    if ((libraryPath = NativeLibrary.matchFramework(libraryName)) != null) {
                        try {
                            if (Native.DEBUG_LOAD) {
                                System.out.println("Trying " + libraryPath);
                            }
                            handle = Native.open(libraryPath, openFlags);
                        }
                        catch (UnsatisfiedLinkError e2) {
                            e = e2;
                        }
                    }
                } else if (Platform.isWindows() && !isAbsolutePath) {
                    if (Native.DEBUG_LOAD) {
                        System.out.println("Looking for lib- prefix");
                    }
                    if ((libraryPath = NativeLibrary.findLibraryPath("lib" + libraryName, searchPath)) != null) {
                        if (Native.DEBUG_LOAD) {
                            System.out.println("Trying " + libraryPath);
                        }
                        try {
                            handle = Native.open(libraryPath, openFlags);
                        }
                        catch (UnsatisfiedLinkError e2) {
                            e = e2;
                        }
                    }
                }
                if (handle == 0L) {
                    try {
                        File embedded = Native.extractFromResourcePath(libraryName, (ClassLoader)options.get("classloader"));
                        try {
                            handle = Native.open(embedded.getAbsolutePath(), openFlags);
                            libraryPath = embedded.getAbsolutePath();
                        }
                        finally {
                            if (Native.isUnpacked(embedded)) {
                                Native.deleteLibrary(embedded);
                            }
                        }
                    }
                    catch (IOException e2) {
                        e = new UnsatisfiedLinkError(e2.getMessage());
                    }
                }
                if (handle != 0L) break block51;
                throw new UnsatisfiedLinkError("Unable to load library '" + libraryName + "': " + e.getMessage());
            }
        }
        if (Native.DEBUG_LOAD) {
            System.out.println("Found library '" + libraryName + "' at " + libraryPath);
        }
        return new NativeLibrary(libraryName, libraryPath, handle, options);
    }

    static String matchFramework(String libraryName) {
        File framework = new File(libraryName);
        if (framework.isAbsolute()) {
            if (libraryName.indexOf(".framework") != -1 && framework.exists()) {
                return framework.getAbsolutePath();
            }
            if ((framework = new File(new File(framework.getParentFile(), framework.getName() + ".framework"), framework.getName())).exists()) {
                return framework.getAbsolutePath();
            }
        } else {
            String[] PREFIXES = new String[]{System.getProperty("user.home"), "", "/System"};
            String suffix = libraryName.indexOf(".framework") == -1 ? libraryName + ".framework/" + libraryName : libraryName;
            for (int i = 0; i < PREFIXES.length; ++i) {
                String libraryPath = PREFIXES[i] + "/Library/Frameworks/" + suffix;
                if (!new File(libraryPath).exists()) continue;
                return libraryPath;
            }
        }
        return null;
    }

    private String getLibraryName(String libraryName) {
        String suffix;
        int suffixStart;
        String simplified = libraryName;
        String BASE = "---";
        String template = NativeLibrary.mapSharedLibraryName("---");
        int prefixEnd = template.indexOf("---");
        if (prefixEnd > 0 && simplified.startsWith(template.substring(0, prefixEnd))) {
            simplified = simplified.substring(prefixEnd);
        }
        if ((suffixStart = simplified.indexOf(suffix = template.substring(prefixEnd + "---".length()))) != -1) {
            simplified = simplified.substring(0, suffixStart);
        }
        return simplified;
    }

    public static final NativeLibrary getInstance(String libraryName) {
        return NativeLibrary.getInstance(libraryName, Collections.emptyMap());
    }

    public static final NativeLibrary getInstance(String libraryName, ClassLoader classLoader) {
        return NativeLibrary.getInstance(libraryName, Collections.singletonMap("classloader", classLoader));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final NativeLibrary getInstance(String libraryName, Map<String, ?> libraryOptions) {
        HashMap options = new HashMap(libraryOptions);
        if (options.get("calling-convention") == null) {
            options.put("calling-convention", 0);
        }
        if ((Platform.isLinux() || Platform.isFreeBSD() || Platform.isAIX()) && Platform.C_LIBRARY_NAME.equals(libraryName)) {
            libraryName = null;
        }
        Map<String, Reference<NativeLibrary>> map = libraries;
        synchronized (map) {
            NativeLibrary library;
            Reference<NativeLibrary> ref = libraries.get(libraryName + options);
            NativeLibrary nativeLibrary = library = ref != null ? ref.get() : null;
            if (library == null) {
                library = libraryName == null ? new NativeLibrary("<process>", null, Native.open(null, NativeLibrary.openFlags(options)), options) : NativeLibrary.loadLibrary(libraryName, options);
                ref = new WeakReference<NativeLibrary>(library);
                libraries.put(library.getName() + options, ref);
                File file = library.getFile();
                if (file != null) {
                    libraries.put(file.getAbsolutePath() + options, ref);
                    libraries.put(file.getName() + options, ref);
                }
            }
            return library;
        }
    }

    public static final synchronized NativeLibrary getProcess() {
        return NativeLibrary.getInstance(null);
    }

    public static final synchronized NativeLibrary getProcess(Map<String, ?> options) {
        return NativeLibrary.getInstance(null, options);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final void addSearchPath(String libraryName, String path) {
        Map<String, List<String>> map = searchPaths;
        synchronized (map) {
            List<String> customPaths = searchPaths.get(libraryName);
            if (customPaths == null) {
                customPaths = Collections.synchronizedList(new ArrayList());
                searchPaths.put(libraryName, customPaths);
            }
            customPaths.add(path);
        }
    }

    public Function getFunction(String functionName) {
        return this.getFunction(functionName, this.callFlags);
    }

    Function getFunction(String name, Method method) {
        String prefix;
        FunctionMapper mapper = (FunctionMapper)this.options.get("function-mapper");
        if (mapper != null) {
            name = mapper.getFunctionName(this, method);
        }
        if (name.startsWith(prefix = System.getProperty("jna.profiler.prefix", "$$YJP$$"))) {
            name = name.substring(prefix.length());
        }
        int flags = this.callFlags;
        Class<?>[] etypes = method.getExceptionTypes();
        for (int i = 0; i < etypes.length; ++i) {
            if (!LastErrorException.class.isAssignableFrom(etypes[i])) continue;
            flags |= 0x40;
        }
        return this.getFunction(name, flags);
    }

    public Function getFunction(String functionName, int callFlags) {
        return this.getFunction(functionName, callFlags, this.encoding);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Function getFunction(String functionName, int callFlags, String encoding) {
        if (functionName == null) {
            throw new NullPointerException("Function name may not be null");
        }
        Map<String, Function> map = this.functions;
        synchronized (map) {
            String key = NativeLibrary.functionKey(functionName, callFlags, encoding);
            Function function = this.functions.get(key);
            if (function == null) {
                function = new Function(this, functionName, callFlags, encoding);
                this.functions.put(key, function);
            }
            return function;
        }
    }

    public Map<String, ?> getOptions() {
        return this.options;
    }

    public Pointer getGlobalVariableAddress(String symbolName) {
        try {
            return new Pointer(this.getSymbolAddress(symbolName));
        }
        catch (UnsatisfiedLinkError e) {
            throw new UnsatisfiedLinkError("Error looking up '" + symbolName + "': " + e.getMessage());
        }
    }

    long getSymbolAddress(String name) {
        if (this.handle == 0L) {
            throw new UnsatisfiedLinkError("Library has been unloaded");
        }
        return Native.findSymbol(this.handle, name);
    }

    public String toString() {
        return "Native Library <" + this.libraryPath + "@" + this.handle + ">";
    }

    public String getName() {
        return this.libraryName;
    }

    public File getFile() {
        if (this.libraryPath == null) {
            return null;
        }
        return new File(this.libraryPath);
    }

    protected void finalize() {
        this.dispose();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void disposeAll() {
        LinkedHashSet<Reference<NativeLibrary>> values;
        Map<String, Reference<NativeLibrary>> map = libraries;
        synchronized (map) {
            values = new LinkedHashSet<Reference<NativeLibrary>>(libraries.values());
        }
        for (Reference reference : values) {
            NativeLibrary lib = (NativeLibrary)reference.get();
            if (lib == null) continue;
            lib.dispose();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispose() {
        HashSet<String> keys = new HashSet<String>();
        Object object = libraries;
        synchronized (object) {
            for (Map.Entry<String, Reference<NativeLibrary>> e : libraries.entrySet()) {
                Reference<NativeLibrary> ref = e.getValue();
                if (ref.get() != this) continue;
                keys.add(e.getKey());
            }
            for (String k : keys) {
                libraries.remove(k);
            }
        }
        object = this;
        synchronized (object) {
            if (this.handle != 0L) {
                Native.close(this.handle);
                this.handle = 0L;
            }
        }
    }

    private static List<String> initPaths(String key) {
        String value = System.getProperty(key, "");
        if ("".equals(value)) {
            return Collections.emptyList();
        }
        StringTokenizer st = new StringTokenizer(value, File.pathSeparator);
        ArrayList<String> list = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String path = st.nextToken();
            if ("".equals(path)) continue;
            list.add(path);
        }
        return list;
    }

    private static String findLibraryPath(String libName, List<String> searchPath) {
        if (new File(libName).isAbsolute()) {
            return libName;
        }
        String name = NativeLibrary.mapSharedLibraryName(libName);
        for (String path : searchPath) {
            File file = new File(path, name);
            if (file.exists()) {
                return file.getAbsolutePath();
            }
            if (!Platform.isMac() || !name.endsWith(".dylib") || !(file = new File(path, name.substring(0, name.lastIndexOf(".dylib")) + ".jnilib")).exists()) continue;
            return file.getAbsolutePath();
        }
        return name;
    }

    static String mapSharedLibraryName(String libName) {
        if (Platform.isMac()) {
            if (libName.startsWith("lib") && (libName.endsWith(".dylib") || libName.endsWith(".jnilib"))) {
                return libName;
            }
            String name = System.mapLibraryName(libName);
            if (name.endsWith(".jnilib")) {
                return name.substring(0, name.lastIndexOf(".jnilib")) + ".dylib";
            }
            return name;
        }
        if (Platform.isLinux() || Platform.isFreeBSD() ? NativeLibrary.isVersionedName(libName) || libName.endsWith(".so") : (Platform.isAIX() ? libName.startsWith("lib") : Platform.isWindows() && (libName.endsWith(".drv") || libName.endsWith(".dll")))) {
            return libName;
        }
        return System.mapLibraryName(libName);
    }

    private static boolean isVersionedName(String name) {
        int so;
        if (name.startsWith("lib") && (so = name.lastIndexOf(".so.")) != -1 && so + 4 < name.length()) {
            for (int i = so + 4; i < name.length(); ++i) {
                char ch = name.charAt(i);
                if (Character.isDigit(ch) || ch == '.') continue;
                return false;
            }
            return true;
        }
        return false;
    }

    static String matchLibrary(final String libName, List<String> searchPath) {
        File lib = new File(libName);
        if (lib.isAbsolute()) {
            searchPath = Arrays.asList(lib.getParent());
        }
        FilenameFilter filter = new FilenameFilter(){

            @Override
            public boolean accept(File dir, String filename) {
                return (filename.startsWith("lib" + libName + ".so") || filename.startsWith(libName + ".so") && libName.startsWith("lib")) && NativeLibrary.isVersionedName(filename);
            }
        };
        LinkedList<File> matches = new LinkedList<File>();
        for (String path : searchPath) {
            File[] files = new File(path).listFiles(filter);
            if (files == null || files.length <= 0) continue;
            matches.addAll(Arrays.asList(files));
        }
        double bestVersion = -1.0;
        String bestMatch = null;
        for (File f : matches) {
            String path = f.getAbsolutePath();
            String ver = path.substring(path.lastIndexOf(".so.") + 4);
            double version = NativeLibrary.parseVersion(ver);
            if (!(version > bestVersion)) continue;
            bestVersion = version;
            bestMatch = path;
        }
        return bestMatch;
    }

    static double parseVersion(String ver) {
        double v = 0.0;
        double divisor = 1.0;
        int dot = ver.indexOf(".");
        while (ver != null) {
            String num;
            if (dot != -1) {
                num = ver.substring(0, dot);
                ver = ver.substring(dot + 1);
                dot = ver.indexOf(".");
            } else {
                num = ver;
                ver = null;
            }
            try {
                v += (double)Integer.parseInt(num) / divisor;
            }
            catch (NumberFormatException e) {
                return 0.0;
            }
            divisor *= 100.0;
        }
        return v;
    }

    private static String getMultiArchPath() {
        String cpu = Platform.ARCH;
        String kernel = Platform.iskFreeBSD() ? "-kfreebsd" : (Platform.isGNU() ? "" : "-linux");
        String libc = "-gnu";
        if (Platform.isIntel()) {
            cpu = Platform.is64Bit() ? "x86_64" : "i386";
        } else if (Platform.isPPC()) {
            cpu = Platform.is64Bit() ? "powerpc64" : "powerpc";
        } else if (Platform.isARM()) {
            cpu = "arm";
            libc = "-gnueabi";
        } else if (Platform.ARCH.equals("mips64el")) {
            libc = "-gnuabi64";
        }
        return cpu + kernel + libc;
    }

    private static ArrayList<String> getLinuxLdPaths() {
        ArrayList<String> ldPaths = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec("/sbin/ldconfig -p");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String buffer = "";
            while ((buffer = reader.readLine()) != null) {
                String path;
                int startPath = buffer.indexOf(" => ");
                int endPath = buffer.lastIndexOf(47);
                if (startPath == -1 || endPath == -1 || startPath >= endPath || ldPaths.contains(path = buffer.substring(startPath + 4, endPath))) continue;
                ldPaths.add(path);
            }
            reader.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        return ldPaths;
    }

    static {
        if (Native.POINTER_SIZE == 0) {
            throw new Error("Native library not initialized");
        }
        String webstartPath = Native.getWebStartLibraryPath("jnidispatch");
        if (webstartPath != null) {
            librarySearchPath.add(webstartPath);
        }
        if (System.getProperty("jna.platform.library.path") == null && !Platform.isWindows()) {
            String platformPath = "";
            String sep = "";
            String archPath = "";
            if (Platform.isLinux() || Platform.isSolaris() || Platform.isFreeBSD() || Platform.iskFreeBSD()) {
                archPath = (Platform.isSolaris() ? "/" : "") + Pointer.SIZE * 8;
            }
            String[] paths = new String[]{"/usr/lib" + archPath, "/lib" + archPath, "/usr/lib", "/lib"};
            if (Platform.isLinux() || Platform.iskFreeBSD() || Platform.isGNU()) {
                String multiArchPath = NativeLibrary.getMultiArchPath();
                paths = new String[]{"/usr/lib/" + multiArchPath, "/lib/" + multiArchPath, "/usr/lib" + archPath, "/lib" + archPath, "/usr/lib", "/lib"};
            }
            if (Platform.isLinux()) {
                ArrayList<String> ldPaths = NativeLibrary.getLinuxLdPaths();
                for (int i = paths.length - 1; 0 <= i; --i) {
                    int found = ldPaths.indexOf(paths[i]);
                    if (found != -1) {
                        ldPaths.remove(found);
                    }
                    ldPaths.add(0, paths[i]);
                }
                paths = ldPaths.toArray(new String[ldPaths.size()]);
            }
            for (int i = 0; i < paths.length; ++i) {
                File dir = new File(paths[i]);
                if (!dir.exists() || !dir.isDirectory()) continue;
                platformPath = platformPath + sep + paths[i];
                sep = File.pathSeparator;
            }
            if (!"".equals(platformPath)) {
                System.setProperty("jna.platform.library.path", platformPath);
            }
        }
        librarySearchPath.addAll(NativeLibrary.initPaths("jna.platform.library.path"));
    }
}

