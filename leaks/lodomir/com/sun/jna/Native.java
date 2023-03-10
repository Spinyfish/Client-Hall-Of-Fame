/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.Callback;
import com.sun.jna.CallbackReference;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.Function;
import com.sun.jna.IntegerType;
import com.sun.jna.JNIEnv;
import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.MethodResultContext;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ToNativeContext;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.Version;
import com.sun.jna.WString;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

public final class Native
implements Version {
    public static final String DEFAULT_ENCODING = Charset.defaultCharset().name();
    public static boolean DEBUG_LOAD = Boolean.getBoolean("jna.debug_load");
    public static boolean DEBUG_JNA_LOAD = Boolean.getBoolean("jna.debug_load.jna");
    static String jnidispatchPath = null;
    private static final Map<Class<?>, Map<String, Object>> typeOptions = new WeakHashMap();
    private static final Map<Class<?>, Reference<?>> libraries = new WeakHashMap();
    private static final String _OPTION_ENCLOSING_LIBRARY = "enclosing-library";
    private static final Callback.UncaughtExceptionHandler DEFAULT_HANDLER;
    private static Callback.UncaughtExceptionHandler callbackExceptionHandler;
    public static final int POINTER_SIZE;
    public static final int LONG_SIZE;
    public static final int WCHAR_SIZE;
    public static final int SIZE_T_SIZE;
    public static final int BOOL_SIZE;
    private static final int TYPE_VOIDP = 0;
    private static final int TYPE_LONG = 1;
    private static final int TYPE_WCHAR_T = 2;
    private static final int TYPE_SIZE_T = 3;
    private static final int TYPE_BOOL = 4;
    static final int MAX_ALIGNMENT;
    static final int MAX_PADDING;
    private static final Object finalizer;
    static final String JNA_TMPLIB_PREFIX = "jna";
    private static Map<Class<?>, long[]> registeredClasses;
    private static Map<Class<?>, NativeLibrary> registeredLibraries;
    static final int CB_HAS_INITIALIZER = 1;
    private static final int CVT_UNSUPPORTED = -1;
    private static final int CVT_DEFAULT = 0;
    private static final int CVT_POINTER = 1;
    private static final int CVT_STRING = 2;
    private static final int CVT_STRUCTURE = 3;
    private static final int CVT_STRUCTURE_BYVAL = 4;
    private static final int CVT_BUFFER = 5;
    private static final int CVT_ARRAY_BYTE = 6;
    private static final int CVT_ARRAY_SHORT = 7;
    private static final int CVT_ARRAY_CHAR = 8;
    private static final int CVT_ARRAY_INT = 9;
    private static final int CVT_ARRAY_LONG = 10;
    private static final int CVT_ARRAY_FLOAT = 11;
    private static final int CVT_ARRAY_DOUBLE = 12;
    private static final int CVT_ARRAY_BOOLEAN = 13;
    private static final int CVT_BOOLEAN = 14;
    private static final int CVT_CALLBACK = 15;
    private static final int CVT_FLOAT = 16;
    private static final int CVT_NATIVE_MAPPED = 17;
    private static final int CVT_NATIVE_MAPPED_STRING = 18;
    private static final int CVT_NATIVE_MAPPED_WSTRING = 19;
    private static final int CVT_WSTRING = 20;
    private static final int CVT_INTEGER_TYPE = 21;
    private static final int CVT_POINTER_TYPE = 22;
    private static final int CVT_TYPE_MAPPER = 23;
    private static final int CVT_TYPE_MAPPER_STRING = 24;
    private static final int CVT_TYPE_MAPPER_WSTRING = 25;
    private static final int CVT_OBJECT = 26;
    private static final int CVT_JNIENV = 27;
    static final int CB_OPTION_DIRECT = 1;
    static final int CB_OPTION_IN_DLL = 2;
    private static final ThreadLocal<Memory> nativeThreadTerminationFlag;
    private static final Map<Thread, Pointer> nativeThreads;

    @Deprecated
    public static float parseVersion(String v) {
        return Float.parseFloat(v.substring(0, v.lastIndexOf(".")));
    }

    static boolean isCompatibleVersion(String expectedVersion, String nativeVersion) {
        String[] expectedVersionParts = expectedVersion.split("\\.");
        String[] nativeVersionParts = nativeVersion.split("\\.");
        if (expectedVersionParts.length < 3 || nativeVersionParts.length < 3) {
            return false;
        }
        int expectedMajor = Integer.parseInt(expectedVersionParts[0]);
        int nativeMajor = Integer.parseInt(nativeVersionParts[0]);
        int expectedMinor = Integer.parseInt(expectedVersionParts[1]);
        int nativeMinor = Integer.parseInt(nativeVersionParts[1]);
        if (expectedMajor != nativeMajor) {
            return false;
        }
        return expectedMinor <= nativeMinor;
    }

    private static void dispose() {
        CallbackReference.disposeAll();
        Memory.disposeAll();
        NativeLibrary.disposeAll();
        Native.unregisterAll();
        jnidispatchPath = null;
        System.setProperty("jna.loaded", "false");
    }

    static boolean deleteLibrary(File lib) {
        if (lib.delete()) {
            return true;
        }
        Native.markTemporaryFile(lib);
        return false;
    }

    private Native() {
    }

    private static native void initIDs();

    public static synchronized native void setProtected(boolean var0);

    public static synchronized native boolean isProtected();

    @Deprecated
    public static void setPreserveLastError(boolean enable) {
    }

    @Deprecated
    public static boolean getPreserveLastError() {
        return true;
    }

    public static long getWindowID(Window w) throws HeadlessException {
        return AWT.getWindowID(w);
    }

    public static long getComponentID(Component c) throws HeadlessException {
        return AWT.getComponentID(c);
    }

    public static Pointer getWindowPointer(Window w) throws HeadlessException {
        return new Pointer(AWT.getWindowID(w));
    }

    public static Pointer getComponentPointer(Component c) throws HeadlessException {
        return new Pointer(AWT.getComponentID(c));
    }

    static native long getWindowHandle0(Component var0);

    public static Pointer getDirectBufferPointer(Buffer b) {
        long peer = Native._getDirectBufferPointer(b);
        return peer == 0L ? null : new Pointer(peer);
    }

    private static native long _getDirectBufferPointer(Buffer var0);

    public static String toString(byte[] buf) {
        return Native.toString(buf, Native.getDefaultStringEncoding());
    }

    public static String toString(byte[] buf, String encoding) {
        int len = buf.length;
        for (int index = 0; index < len; ++index) {
            if (buf[index] != 0) continue;
            len = index;
            break;
        }
        if (len == 0) {
            return "";
        }
        if (encoding != null) {
            try {
                return new String(buf, 0, len, encoding);
            }
            catch (UnsupportedEncodingException e) {
                System.err.println("JNA Warning: Encoding '" + encoding + "' is unsupported");
            }
        }
        System.err.println("JNA Warning: Decoding with fallback " + System.getProperty("file.encoding"));
        return new String(buf, 0, len);
    }

    public static String toString(char[] buf) {
        int len = buf.length;
        for (int index = 0; index < len; ++index) {
            if (buf[index] != '\u0000') continue;
            len = index;
            break;
        }
        if (len == 0) {
            return "";
        }
        return new String(buf, 0, len);
    }

    public static List<String> toStringList(char[] buf) {
        return Native.toStringList(buf, 0, buf.length);
    }

    public static List<String> toStringList(char[] buf, int offset, int len) {
        ArrayList<String> list = new ArrayList<String>();
        int lastPos = offset;
        int maxPos = offset + len;
        for (int curPos = offset; curPos < maxPos; ++curPos) {
            if (buf[curPos] != '\u0000') continue;
            if (lastPos == curPos) {
                return list;
            }
            String value = new String(buf, lastPos, curPos - lastPos);
            list.add(value);
            lastPos = curPos + 1;
        }
        if (lastPos < maxPos) {
            String value = new String(buf, lastPos, maxPos - lastPos);
            list.add(value);
        }
        return list;
    }

    public static <T> T loadLibrary(Class<T> interfaceClass) {
        return Native.loadLibrary(null, interfaceClass);
    }

    public static <T> T loadLibrary(Class<T> interfaceClass, Map<String, ?> options) {
        return Native.loadLibrary(null, interfaceClass, options);
    }

    public static <T> T loadLibrary(String name, Class<T> interfaceClass) {
        return Native.loadLibrary(name, interfaceClass, Collections.emptyMap());
    }

    public static <T> T loadLibrary(String name, Class<T> interfaceClass, Map<String, ?> options) {
        if (!Library.class.isAssignableFrom(interfaceClass)) {
            throw new IllegalArgumentException("Interface (" + interfaceClass.getSimpleName() + ") of library=" + name + " does not extend " + Library.class.getSimpleName());
        }
        Library.Handler handler = new Library.Handler(name, interfaceClass, options);
        ClassLoader loader = interfaceClass.getClassLoader();
        Object proxy = Proxy.newProxyInstance(loader, new Class[]{interfaceClass}, (InvocationHandler)handler);
        Native.cacheOptions(interfaceClass, options, proxy);
        return interfaceClass.cast(proxy);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void loadLibraryInstance(Class<?> cls) {
        Map<Class<?>, Reference<?>> map = libraries;
        synchronized (map) {
            if (cls != null && !libraries.containsKey(cls)) {
                try {
                    Field[] fields = cls.getFields();
                    for (int i = 0; i < fields.length; ++i) {
                        Field field = fields[i];
                        if (field.getType() != cls || !Modifier.isStatic(field.getModifiers())) continue;
                        libraries.put(cls, new WeakReference<Object>(field.get(null)));
                        break;
                    }
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("Could not access instance of " + cls + " (" + e + ")");
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Class<?> findEnclosingLibraryClass(Class<?> cls) {
        Class<?> declaring;
        Class<?> fromDeclaring;
        if (cls == null) {
            return null;
        }
        Map<Class<?>, Reference<?>> map = libraries;
        synchronized (map) {
            if (typeOptions.containsKey(cls)) {
                Map<String, Object> libOptions = typeOptions.get(cls);
                Class enclosingClass = (Class)libOptions.get(_OPTION_ENCLOSING_LIBRARY);
                if (enclosingClass != null) {
                    return enclosingClass;
                }
                return cls;
            }
        }
        if (Library.class.isAssignableFrom(cls)) {
            return cls;
        }
        if (Callback.class.isAssignableFrom(cls)) {
            cls = CallbackReference.findCallbackClass(cls);
        }
        if ((fromDeclaring = Native.findEnclosingLibraryClass(declaring = cls.getDeclaringClass())) != null) {
            return fromDeclaring;
        }
        return Native.findEnclosingLibraryClass(cls.getSuperclass());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map<String, Object> getLibraryOptions(Class<?> type) {
        Map<String, Object> libraryOptions;
        Map<Class<?>, Reference<?>> map = libraries;
        synchronized (map) {
            libraryOptions = typeOptions.get(type);
            if (libraryOptions != null) {
                return libraryOptions;
            }
        }
        Class<?> mappingClass = Native.findEnclosingLibraryClass(type);
        if (mappingClass != null) {
            Native.loadLibraryInstance(mappingClass);
        } else {
            mappingClass = type;
        }
        Map<Class<?>, Reference<?>> map2 = libraries;
        synchronized (map2) {
            libraryOptions = typeOptions.get(mappingClass);
            if (libraryOptions != null) {
                typeOptions.put(type, libraryOptions);
                return libraryOptions;
            }
            try {
                Field field = mappingClass.getField("OPTIONS");
                field.setAccessible(true);
                libraryOptions = (Map<String, Object>)field.get(null);
                if (libraryOptions == null) {
                    throw new IllegalStateException("Null options field");
                }
            }
            catch (NoSuchFieldException e) {
                libraryOptions = Collections.emptyMap();
            }
            catch (Exception e) {
                throw new IllegalArgumentException("OPTIONS must be a public field of type java.util.Map (" + e + "): " + mappingClass);
            }
            libraryOptions = new HashMap<String, Object>(libraryOptions);
            if (!libraryOptions.containsKey("type-mapper")) {
                libraryOptions.put("type-mapper", Native.lookupField(mappingClass, "TYPE_MAPPER", TypeMapper.class));
            }
            if (!libraryOptions.containsKey("structure-alignment")) {
                libraryOptions.put("structure-alignment", Native.lookupField(mappingClass, "STRUCTURE_ALIGNMENT", Integer.class));
            }
            if (!libraryOptions.containsKey("string-encoding")) {
                libraryOptions.put("string-encoding", Native.lookupField(mappingClass, "STRING_ENCODING", String.class));
            }
            libraryOptions = Native.cacheOptions(mappingClass, libraryOptions, null);
            if (type != mappingClass) {
                typeOptions.put(type, libraryOptions);
            }
            return libraryOptions;
        }
    }

    private static Object lookupField(Class<?> mappingClass, String fieldName, Class<?> resultClass) {
        try {
            Field field = mappingClass.getField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        }
        catch (NoSuchFieldException e) {
            return null;
        }
        catch (Exception e) {
            throw new IllegalArgumentException(fieldName + " must be a public field of type " + resultClass.getName() + " (" + e + "): " + mappingClass);
        }
    }

    public static TypeMapper getTypeMapper(Class<?> cls) {
        Map<String, Object> options = Native.getLibraryOptions(cls);
        return (TypeMapper)options.get("type-mapper");
    }

    public static String getStringEncoding(Class<?> cls) {
        Map<String, Object> options = Native.getLibraryOptions(cls);
        String encoding = (String)options.get("string-encoding");
        return encoding != null ? encoding : Native.getDefaultStringEncoding();
    }

    public static String getDefaultStringEncoding() {
        return System.getProperty("jna.encoding", DEFAULT_ENCODING);
    }

    public static int getStructureAlignment(Class<?> cls) {
        Integer alignment = (Integer)Native.getLibraryOptions(cls).get("structure-alignment");
        return alignment == null ? 0 : alignment;
    }

    static byte[] getBytes(String s) {
        return Native.getBytes(s, Native.getDefaultStringEncoding());
    }

    static byte[] getBytes(String s, String encoding) {
        if (encoding != null) {
            try {
                return s.getBytes(encoding);
            }
            catch (UnsupportedEncodingException e) {
                System.err.println("JNA Warning: Encoding '" + encoding + "' is unsupported");
            }
        }
        System.err.println("JNA Warning: Encoding with fallback " + System.getProperty("file.encoding"));
        return s.getBytes();
    }

    public static byte[] toByteArray(String s) {
        return Native.toByteArray(s, Native.getDefaultStringEncoding());
    }

    public static byte[] toByteArray(String s, String encoding) {
        byte[] bytes = Native.getBytes(s, encoding);
        byte[] buf = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, buf, 0, bytes.length);
        return buf;
    }

    public static char[] toCharArray(String s) {
        char[] chars = s.toCharArray();
        char[] buf = new char[chars.length + 1];
        System.arraycopy(chars, 0, buf, 0, chars.length);
        return buf;
    }

    private static void loadNativeDispatchLibrary() {
        if (!Boolean.getBoolean("jna.nounpack")) {
            try {
                Native.removeTemporaryFiles();
            }
            catch (IOException e) {
                System.err.println("JNA Warning: IOException removing temporary files: " + e.getMessage());
            }
        }
        String libName = System.getProperty("jna.boot.library.name", "jnidispatch");
        String bootPath = System.getProperty("jna.boot.library.path");
        if (bootPath != null) {
            StringTokenizer dirs = new StringTokenizer(bootPath, File.pathSeparator);
            while (dirs.hasMoreTokens()) {
                String ext;
                String orig;
                String dir = dirs.nextToken();
                File file = new File(new File(dir), System.mapLibraryName(libName).replace(".dylib", ".jnilib"));
                String path = file.getAbsolutePath();
                if (DEBUG_JNA_LOAD) {
                    System.out.println("Looking in " + path);
                }
                if (file.exists()) {
                    try {
                        if (DEBUG_JNA_LOAD) {
                            System.out.println("Trying " + path);
                        }
                        System.setProperty("jnidispatch.path", path);
                        System.load(path);
                        jnidispatchPath = path;
                        if (DEBUG_JNA_LOAD) {
                            System.out.println("Found jnidispatch at " + path);
                        }
                        return;
                    }
                    catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                        // empty catch block
                    }
                }
                if (!Platform.isMac()) continue;
                if (path.endsWith("dylib")) {
                    orig = "dylib";
                    ext = "jnilib";
                } else {
                    orig = "jnilib";
                    ext = "dylib";
                }
                path = path.substring(0, path.lastIndexOf(orig)) + ext;
                if (DEBUG_JNA_LOAD) {
                    System.out.println("Looking in " + path);
                }
                if (!new File(path).exists()) continue;
                try {
                    if (DEBUG_JNA_LOAD) {
                        System.out.println("Trying " + path);
                    }
                    System.setProperty("jnidispatch.path", path);
                    System.load(path);
                    jnidispatchPath = path;
                    if (DEBUG_JNA_LOAD) {
                        System.out.println("Found jnidispatch at " + path);
                    }
                    return;
                }
                catch (UnsatisfiedLinkError ex) {
                    System.err.println("File found at " + path + " but not loadable: " + ex.getMessage());
                }
            }
        }
        if (!Boolean.getBoolean("jna.nosys")) {
            try {
                if (DEBUG_JNA_LOAD) {
                    System.out.println("Trying (via loadLibrary) " + libName);
                }
                System.loadLibrary(libName);
                if (DEBUG_JNA_LOAD) {
                    System.out.println("Found jnidispatch on system path");
                }
                return;
            }
            catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                // empty catch block
            }
        }
        if (Boolean.getBoolean("jna.noclasspath")) {
            throw new UnsatisfiedLinkError("Unable to locate JNA native support library");
        }
        Native.loadNativeDispatchLibraryFromClasspath();
    }

    private static void loadNativeDispatchLibraryFromClasspath() {
        try {
            String libName = "/com/sun/jna/" + Platform.RESOURCE_PREFIX + "/" + System.mapLibraryName("jnidispatch").replace(".dylib", ".jnilib");
            File lib = Native.extractFromResourcePath(libName, Native.class.getClassLoader());
            if (lib == null && lib == null) {
                throw new UnsatisfiedLinkError("Could not find JNA native support");
            }
            if (DEBUG_JNA_LOAD) {
                System.out.println("Trying " + lib.getAbsolutePath());
            }
            System.setProperty("jnidispatch.path", lib.getAbsolutePath());
            System.load(lib.getAbsolutePath());
            jnidispatchPath = lib.getAbsolutePath();
            if (DEBUG_JNA_LOAD) {
                System.out.println("Found jnidispatch at " + jnidispatchPath);
            }
            if (Native.isUnpacked(lib) && !Boolean.getBoolean("jnidispatch.preserve")) {
                Native.deleteLibrary(lib);
            }
        }
        catch (IOException e) {
            throw new UnsatisfiedLinkError(e.getMessage());
        }
    }

    static boolean isUnpacked(File file) {
        return file.getName().startsWith(JNA_TMPLIB_PREFIX);
    }

    public static File extractFromResourcePath(String name) throws IOException {
        return Native.extractFromResourcePath(name, null);
    }

    public static File extractFromResourcePath(String name, ClassLoader loader) throws IOException {
        URL url;
        String resourcePath;
        boolean DEBUG;
        boolean bl = DEBUG = DEBUG_LOAD || DEBUG_JNA_LOAD && name.indexOf("jnidispatch") != -1;
        if (loader == null && (loader = Thread.currentThread().getContextClassLoader()) == null) {
            loader = Native.class.getClassLoader();
        }
        if (DEBUG) {
            System.out.println("Looking in classpath from " + loader + " for " + name);
        }
        String libname = name.startsWith("/") ? name : NativeLibrary.mapSharedLibraryName(name);
        String string = resourcePath = name.startsWith("/") ? name : Platform.RESOURCE_PREFIX + "/" + libname;
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }
        if ((url = loader.getResource(resourcePath)) == null && resourcePath.startsWith(Platform.RESOURCE_PREFIX)) {
            url = loader.getResource(libname);
        }
        if (url == null) {
            String path = System.getProperty("java.class.path");
            if (loader instanceof URLClassLoader) {
                path = Arrays.asList(((URLClassLoader)loader).getURLs()).toString();
            }
            throw new IOException("Native library (" + resourcePath + ") not found in resource path (" + path + ")");
        }
        if (DEBUG) {
            System.out.println("Found library resource at " + url);
        }
        File lib = null;
        if (url.getProtocol().toLowerCase().equals("file")) {
            try {
                lib = new File(new URI(url.toString()));
            }
            catch (URISyntaxException e) {
                lib = new File(url.getPath());
            }
            if (DEBUG) {
                System.out.println("Looking in " + lib.getAbsolutePath());
            }
            if (!lib.exists()) {
                throw new IOException("File URL " + url + " could not be properly decoded");
            }
        } else if (!Boolean.getBoolean("jna.nounpack")) {
            InputStream is = loader.getResourceAsStream(resourcePath);
            if (is == null) {
                throw new IOException("Can't obtain InputStream for " + resourcePath);
            }
            FileOutputStream fos = null;
            try {
                int count;
                File dir = Native.getTempDir();
                lib = File.createTempFile(JNA_TMPLIB_PREFIX, Platform.isWindows() ? ".dll" : null, dir);
                if (!Boolean.getBoolean("jnidispatch.preserve")) {
                    lib.deleteOnExit();
                }
                fos = new FileOutputStream(lib);
                byte[] buf = new byte[1024];
                while ((count = is.read(buf, 0, buf.length)) > 0) {
                    fos.write(buf, 0, count);
                }
            }
            catch (IOException e) {
                throw new IOException("Failed to create temporary file for " + name + " library: " + e.getMessage());
            }
            finally {
                try {
                    is.close();
                }
                catch (IOException iOException) {}
                if (fos != null) {
                    try {
                        fos.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
        return lib;
    }

    private static native int sizeof(int var0);

    private static native String getNativeVersion();

    private static native String getAPIChecksum();

    public static native int getLastError();

    public static native void setLastError(int var0);

    public static Library synchronizedLibrary(final Library library) {
        Class<?> cls = library.getClass();
        if (!Proxy.isProxyClass(cls)) {
            throw new IllegalArgumentException("Library must be a proxy class");
        }
        InvocationHandler ih = Proxy.getInvocationHandler(library);
        if (!(ih instanceof Library.Handler)) {
            throw new IllegalArgumentException("Unrecognized proxy handler: " + ih);
        }
        final Library.Handler handler = (Library.Handler)ih;
        InvocationHandler newHandler = new InvocationHandler(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                NativeLibrary nativeLibrary = handler.getNativeLibrary();
                synchronized (nativeLibrary) {
                    return handler.invoke(library, method, args);
                }
            }
        };
        return (Library)Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), newHandler);
    }

    public static String getWebStartLibraryPath(String libName) {
        if (System.getProperty("javawebstart.version") == null) {
            return null;
        }
        try {
            ClassLoader cl = Native.class.getClassLoader();
            Method m = AccessController.doPrivileged(new PrivilegedAction<Method>(){

                @Override
                public Method run() {
                    try {
                        Method m = ClassLoader.class.getDeclaredMethod("findLibrary", String.class);
                        m.setAccessible(true);
                        return m;
                    }
                    catch (Exception e) {
                        return null;
                    }
                }
            });
            String libpath = (String)m.invoke((Object)cl, libName);
            if (libpath != null) {
                return new File(libpath).getParent();
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    static void markTemporaryFile(File file) {
        try {
            File marker = new File(file.getParentFile(), file.getName() + ".x");
            marker.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static File getTempDir() throws IOException {
        File jnatmp;
        String prop = System.getProperty("jna.tmpdir");
        if (prop != null) {
            jnatmp = new File(prop);
            jnatmp.mkdirs();
        } else {
            File tmp = new File(System.getProperty("java.io.tmpdir"));
            jnatmp = new File(tmp, "jna-" + System.getProperty("user.name").hashCode());
            jnatmp.mkdirs();
            if (!jnatmp.exists() || !jnatmp.canWrite()) {
                jnatmp = tmp;
            }
        }
        if (!jnatmp.exists()) {
            throw new IOException("JNA temporary directory '" + jnatmp + "' does not exist");
        }
        if (!jnatmp.canWrite()) {
            throw new IOException("JNA temporary directory '" + jnatmp + "' is not writable");
        }
        return jnatmp;
    }

    static void removeTemporaryFiles() throws IOException {
        File dir = Native.getTempDir();
        FilenameFilter filter = new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".x") && name.startsWith(Native.JNA_TMPLIB_PREFIX);
            }
        };
        File[] files = dir.listFiles(filter);
        for (int i = 0; files != null && i < files.length; ++i) {
            File marker = files[i];
            String name = marker.getName();
            name = name.substring(0, name.length() - 2);
            File target = new File(marker.getParentFile(), name);
            if (target.exists() && !target.delete()) continue;
            marker.delete();
        }
    }

    public static int getNativeSize(Class<?> type, Object value) {
        if (type.isArray()) {
            int len = Array.getLength(value);
            if (len > 0) {
                Object o = Array.get(value, 0);
                return len * Native.getNativeSize(type.getComponentType(), o);
            }
            throw new IllegalArgumentException("Arrays of length zero not allowed: " + type);
        }
        if (Structure.class.isAssignableFrom(type) && !Structure.ByReference.class.isAssignableFrom(type)) {
            return Structure.size(type, (Structure)value);
        }
        try {
            return Native.getNativeSize(type);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The type \"" + type.getName() + "\" is not supported: " + e.getMessage());
        }
    }

    public static int getNativeSize(Class<?> cls) {
        if (NativeMapped.class.isAssignableFrom(cls)) {
            cls = NativeMappedConverter.getInstance(cls).nativeType();
        }
        if (cls == Boolean.TYPE || cls == Boolean.class) {
            return 4;
        }
        if (cls == Byte.TYPE || cls == Byte.class) {
            return 1;
        }
        if (cls == Short.TYPE || cls == Short.class) {
            return 2;
        }
        if (cls == Character.TYPE || cls == Character.class) {
            return WCHAR_SIZE;
        }
        if (cls == Integer.TYPE || cls == Integer.class) {
            return 4;
        }
        if (cls == Long.TYPE || cls == Long.class) {
            return 8;
        }
        if (cls == Float.TYPE || cls == Float.class) {
            return 4;
        }
        if (cls == Double.TYPE || cls == Double.class) {
            return 8;
        }
        if (Structure.class.isAssignableFrom(cls)) {
            if (Structure.ByValue.class.isAssignableFrom(cls)) {
                return Structure.size(cls);
            }
            return POINTER_SIZE;
        }
        if (Pointer.class.isAssignableFrom(cls) || Platform.HAS_BUFFERS && Buffers.isBuffer(cls) || Callback.class.isAssignableFrom(cls) || String.class == cls || WString.class == cls) {
            return POINTER_SIZE;
        }
        throw new IllegalArgumentException("Native size for type \"" + cls.getName() + "\" is unknown");
    }

    public static boolean isSupportedNativeType(Class<?> cls) {
        if (Structure.class.isAssignableFrom(cls)) {
            return true;
        }
        try {
            return Native.getNativeSize(cls) != 0;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void setCallbackExceptionHandler(Callback.UncaughtExceptionHandler eh) {
        callbackExceptionHandler = eh == null ? DEFAULT_HANDLER : eh;
    }

    public static Callback.UncaughtExceptionHandler getCallbackExceptionHandler() {
        return callbackExceptionHandler;
    }

    public static void register(String libName) {
        Native.register(Native.findDirectMappedClass(Native.getCallingClass()), libName);
    }

    public static void register(NativeLibrary lib) {
        Native.register(Native.findDirectMappedClass(Native.getCallingClass()), lib);
    }

    static Class<?> findDirectMappedClass(Class<?> cls) {
        Method[] methods;
        for (Method m : methods = cls.getDeclaredMethods()) {
            if ((m.getModifiers() & 0x100) == 0) continue;
            return cls;
        }
        int idx = cls.getName().lastIndexOf("$");
        if (idx != -1) {
            String name = cls.getName().substring(0, idx);
            try {
                return Native.findDirectMappedClass(Class.forName(name, true, cls.getClassLoader()));
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        throw new IllegalArgumentException("Can't determine class with native methods from the current context (" + cls + ")");
    }

    static Class<?> getCallingClass() {
        Class<?>[] context = new SecurityManager(){

            @Override
            public Class<?>[] getClassContext() {
                return super.getClassContext();
            }
        }.getClassContext();
        if (context == null) {
            throw new IllegalStateException("The SecurityManager implementation on this platform is broken; you must explicitly provide the class to register");
        }
        if (context.length < 4) {
            throw new IllegalStateException("This method must be called from the static initializer of a class");
        }
        return context[3];
    }

    public static void setCallbackThreadInitializer(Callback cb, CallbackThreadInitializer initializer) {
        CallbackReference.setCallbackThreadInitializer(cb, initializer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void unregisterAll() {
        Map<Class<?>, long[]> map = registeredClasses;
        synchronized (map) {
            for (Map.Entry<Class<?>, long[]> e : registeredClasses.entrySet()) {
                Native.unregister(e.getKey(), e.getValue());
            }
            registeredClasses.clear();
        }
    }

    public static void unregister() {
        Native.unregister(Native.findDirectMappedClass(Native.getCallingClass()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void unregister(Class<?> cls) {
        Map<Class<?>, long[]> map = registeredClasses;
        synchronized (map) {
            long[] handles = registeredClasses.get(cls);
            if (handles != null) {
                Native.unregister(cls, handles);
                registeredClasses.remove(cls);
                registeredLibraries.remove(cls);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean registered(Class<?> cls) {
        Map<Class<?>, long[]> map = registeredClasses;
        synchronized (map) {
            return registeredClasses.containsKey(cls);
        }
    }

    private static native void unregister(Class<?> var0, long[] var1);

    static String getSignature(Class<?> cls) {
        if (cls.isArray()) {
            return "[" + Native.getSignature(cls.getComponentType());
        }
        if (cls.isPrimitive()) {
            if (cls == Void.TYPE) {
                return "V";
            }
            if (cls == Boolean.TYPE) {
                return "Z";
            }
            if (cls == Byte.TYPE) {
                return "B";
            }
            if (cls == Short.TYPE) {
                return "S";
            }
            if (cls == Character.TYPE) {
                return "C";
            }
            if (cls == Integer.TYPE) {
                return "I";
            }
            if (cls == Long.TYPE) {
                return "J";
            }
            if (cls == Float.TYPE) {
                return "F";
            }
            if (cls == Double.TYPE) {
                return "D";
            }
        }
        return "L" + Native.replace(".", "/", cls.getName()) + ";";
    }

    static String replace(String s1, String s2, String str) {
        StringBuilder buf = new StringBuilder();
        while (true) {
            int idx;
            if ((idx = str.indexOf(s1)) == -1) break;
            buf.append(str.substring(0, idx));
            buf.append(s2);
            str = str.substring(idx + s1.length());
        }
        buf.append(str);
        return buf.toString();
    }

    private static int getConversion(Class<?> type, TypeMapper mapper, boolean allowObjects) {
        if (type == Boolean.class) {
            type = Boolean.TYPE;
        } else if (type == Byte.class) {
            type = Byte.TYPE;
        } else if (type == Short.class) {
            type = Short.TYPE;
        } else if (type == Character.class) {
            type = Character.TYPE;
        } else if (type == Integer.class) {
            type = Integer.TYPE;
        } else if (type == Long.class) {
            type = Long.TYPE;
        } else if (type == Float.class) {
            type = Float.TYPE;
        } else if (type == Double.class) {
            type = Double.TYPE;
        } else if (type == Void.class) {
            type = Void.TYPE;
        }
        if (mapper != null) {
            FromNativeConverter fromNative = mapper.getFromNativeConverter(type);
            ToNativeConverter toNative = mapper.getToNativeConverter(type);
            if (fromNative != null) {
                Class<?> nativeType = fromNative.nativeType();
                if (nativeType == String.class) {
                    return 24;
                }
                if (nativeType == WString.class) {
                    return 25;
                }
                return 23;
            }
            if (toNative != null) {
                Class<?> nativeType = toNative.nativeType();
                if (nativeType == String.class) {
                    return 24;
                }
                if (nativeType == WString.class) {
                    return 25;
                }
                return 23;
            }
        }
        if (Pointer.class.isAssignableFrom(type)) {
            return 1;
        }
        if (String.class == type) {
            return 2;
        }
        if (WString.class.isAssignableFrom(type)) {
            return 20;
        }
        if (Platform.HAS_BUFFERS && Buffers.isBuffer(type)) {
            return 5;
        }
        if (Structure.class.isAssignableFrom(type)) {
            if (Structure.ByValue.class.isAssignableFrom(type)) {
                return 4;
            }
            return 3;
        }
        if (type.isArray()) {
            switch (type.getName().charAt(1)) {
                case 'Z': {
                    return 13;
                }
                case 'B': {
                    return 6;
                }
                case 'S': {
                    return 7;
                }
                case 'C': {
                    return 8;
                }
                case 'I': {
                    return 9;
                }
                case 'J': {
                    return 10;
                }
                case 'F': {
                    return 11;
                }
                case 'D': {
                    return 12;
                }
            }
        }
        if (type.isPrimitive()) {
            return type == Boolean.TYPE ? 14 : 0;
        }
        if (Callback.class.isAssignableFrom(type)) {
            return 15;
        }
        if (IntegerType.class.isAssignableFrom(type)) {
            return 21;
        }
        if (PointerType.class.isAssignableFrom(type)) {
            return 22;
        }
        if (NativeMapped.class.isAssignableFrom(type)) {
            Class<?> nativeType = NativeMappedConverter.getInstance(type).nativeType();
            if (nativeType == String.class) {
                return 18;
            }
            if (nativeType == WString.class) {
                return 19;
            }
            return 17;
        }
        if (JNIEnv.class == type) {
            return 27;
        }
        return allowObjects ? 26 : -1;
    }

    public static void register(Class<?> cls, String libName) {
        NativeLibrary library = NativeLibrary.getInstance(libName, Collections.singletonMap("classloader", cls.getClassLoader()));
        Native.register(cls, library);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void register(Class<?> cls, NativeLibrary lib) {
        Method[] methods = cls.getDeclaredMethods();
        ArrayList<Method> mlist = new ArrayList<Method>();
        Map<String, Object> options = lib.getOptions();
        TypeMapper mapper = (TypeMapper)options.get("type-mapper");
        boolean allowObjects = Boolean.TRUE.equals(options.get("allow-objects"));
        options = Native.cacheOptions(cls, options, null);
        for (Method m : methods) {
            if ((m.getModifiers() & 0x100) == 0) continue;
            mlist.add(m);
        }
        long[] handles = new long[mlist.size()];
        for (int i = 0; i < handles.length; ++i) {
            long rtype;
            long closure_rtype;
            Method method = (Method)mlist.get(i);
            String sig = "(";
            Class<?> rclass = method.getReturnType();
            Class<?>[] ptypes = method.getParameterTypes();
            long[] atypes = new long[ptypes.length];
            long[] closure_atypes = new long[ptypes.length];
            int[] cvt = new int[ptypes.length];
            ToNativeConverter[] toNative = new ToNativeConverter[ptypes.length];
            FromNativeConverter fromNative = null;
            int rcvt = Native.getConversion(rclass, mapper, allowObjects);
            boolean throwLastError = false;
            switch (rcvt) {
                case -1: {
                    throw new IllegalArgumentException(rclass + " is not a supported return type (in method " + method.getName() + " in " + cls + ")");
                }
                case 23: 
                case 24: 
                case 25: {
                    fromNative = mapper.getFromNativeConverter(rclass);
                    closure_rtype = Structure.FFIType.get(rclass.isPrimitive() ? rclass : Pointer.class).peer;
                    rtype = Structure.FFIType.get(fromNative.nativeType()).peer;
                    break;
                }
                case 17: 
                case 18: 
                case 19: 
                case 21: 
                case 22: {
                    closure_rtype = Structure.FFIType.get(Pointer.class).peer;
                    rtype = Structure.FFIType.get(NativeMappedConverter.getInstance(rclass).nativeType()).peer;
                    break;
                }
                case 3: 
                case 26: {
                    closure_rtype = rtype = Structure.FFIType.get(Pointer.class).peer;
                    break;
                }
                case 4: {
                    closure_rtype = Structure.FFIType.get(Pointer.class).peer;
                    rtype = Structure.FFIType.get(rclass).peer;
                    break;
                }
                default: {
                    closure_rtype = rtype = Structure.FFIType.get(rclass).peer;
                }
            }
            block19: for (int t = 0; t < ptypes.length; ++t) {
                int conversionType;
                Class<?> type = ptypes[t];
                sig = sig + Native.getSignature(type);
                cvt[t] = conversionType = Native.getConversion(type, mapper, allowObjects);
                if (conversionType == -1) {
                    throw new IllegalArgumentException(type + " is not a supported argument type (in method " + method.getName() + " in " + cls + ")");
                }
                if (conversionType == 17 || conversionType == 18 || conversionType == 19 || conversionType == 21) {
                    type = NativeMappedConverter.getInstance(type).nativeType();
                } else if (conversionType == 23 || conversionType == 24 || conversionType == 25) {
                    toNative[t] = mapper.getToNativeConverter(type);
                }
                switch (conversionType) {
                    case 4: 
                    case 17: 
                    case 18: 
                    case 19: 
                    case 21: 
                    case 22: {
                        atypes[t] = Structure.FFIType.get(type).peer;
                        closure_atypes[t] = Structure.FFIType.get(Pointer.class).peer;
                        continue block19;
                    }
                    case 23: 
                    case 24: 
                    case 25: {
                        closure_atypes[t] = Structure.FFIType.get(type.isPrimitive() ? type : Pointer.class).peer;
                        atypes[t] = Structure.FFIType.get(toNative[t].nativeType()).peer;
                        continue block19;
                    }
                    case 0: {
                        closure_atypes[t] = atypes[t] = Structure.FFIType.get(type).peer;
                        continue block19;
                    }
                    default: {
                        closure_atypes[t] = atypes[t] = Structure.FFIType.get(Pointer.class).peer;
                    }
                }
            }
            sig = sig + ")";
            sig = sig + Native.getSignature(rclass);
            Class<?>[] etypes = method.getExceptionTypes();
            for (int e = 0; e < etypes.length; ++e) {
                if (!LastErrorException.class.isAssignableFrom(etypes[e])) continue;
                throwLastError = true;
                break;
            }
            Function f = lib.getFunction(method.getName(), method);
            try {
                handles[i] = Native.registerMethod(cls, method.getName(), sig, cvt, closure_atypes, atypes, rcvt, closure_rtype, rtype, method, f.peer, f.getCallingConvention(), throwLastError, toNative, fromNative, f.encoding);
                continue;
            }
            catch (NoSuchMethodError e) {
                throw new UnsatisfiedLinkError("No method " + method.getName() + " with signature " + sig + " in " + cls);
            }
        }
        Map<Class<?>, long[]> map = registeredClasses;
        synchronized (map) {
            registeredClasses.put(cls, handles);
            registeredLibraries.put(cls, lib);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Map<String, Object> cacheOptions(Class<?> cls, Map<String, ?> options, Object proxy) {
        HashMap<String, Object> libOptions = new HashMap<String, Object>(options);
        libOptions.put(_OPTION_ENCLOSING_LIBRARY, cls);
        Map<Class<?>, Reference<?>> map = libraries;
        synchronized (map) {
            typeOptions.put(cls, libOptions);
            if (proxy != null) {
                libraries.put(cls, new WeakReference<Object>(proxy));
            }
            if (!cls.isInterface() && Library.class.isAssignableFrom(cls)) {
                Class<?>[] ifaces;
                for (Class<?> ifc : ifaces = cls.getInterfaces()) {
                    if (!Library.class.isAssignableFrom(ifc)) continue;
                    Native.cacheOptions(ifc, libOptions, proxy);
                    break;
                }
            }
        }
        return libOptions;
    }

    private static native long registerMethod(Class<?> var0, String var1, String var2, int[] var3, long[] var4, long[] var5, int var6, long var7, long var9, Method var11, long var12, int var14, boolean var15, ToNativeConverter[] var16, FromNativeConverter var17, String var18);

    private static NativeMapped fromNative(Class<?> cls, Object value) {
        return (NativeMapped)NativeMappedConverter.getInstance(cls).fromNative(value, new FromNativeContext(cls));
    }

    private static NativeMapped fromNative(Method m, Object value) {
        Class<?> cls = m.getReturnType();
        return (NativeMapped)NativeMappedConverter.getInstance(cls).fromNative(value, new MethodResultContext(cls, null, null, m));
    }

    private static Class<?> nativeType(Class<?> cls) {
        return NativeMappedConverter.getInstance(cls).nativeType();
    }

    private static Object toNative(ToNativeConverter cvt, Object o) {
        return cvt.toNative(o, new ToNativeContext());
    }

    private static Object fromNative(FromNativeConverter cvt, Object o, Method m) {
        return cvt.fromNative(o, new MethodResultContext(m.getReturnType(), null, null, m));
    }

    public static native long ffi_prep_cif(int var0, int var1, long var2, long var4);

    public static native void ffi_call(long var0, long var2, long var4, long var6);

    public static native long ffi_prep_closure(long var0, ffi_callback var2);

    public static native void ffi_free_closure(long var0);

    static native int initialize_ffi_type(long var0);

    public static void main(String[] args) {
        String version;
        String title;
        String DEFAULT_TITLE = "Java Native Access (JNA)";
        String DEFAULT_VERSION = "4.5.1";
        String DEFAULT_BUILD = "4.5.1 (package information missing)";
        Package pkg = Native.class.getPackage();
        String string = title = pkg != null ? pkg.getSpecificationTitle() : "Java Native Access (JNA)";
        if (title == null) {
            title = "Java Native Access (JNA)";
        }
        String string2 = version = pkg != null ? pkg.getSpecificationVersion() : "4.5.1";
        if (version == null) {
            version = "4.5.1";
        }
        title = title + " API Version " + version;
        System.out.println(title);
        String string3 = version = pkg != null ? pkg.getImplementationVersion() : "4.5.1 (package information missing)";
        if (version == null) {
            version = "4.5.1 (package information missing)";
        }
        System.out.println("Version: " + version);
        System.out.println(" Native: " + Native.getNativeVersion() + " (" + Native.getAPIChecksum() + ")");
        System.out.println(" Prefix: " + Platform.RESOURCE_PREFIX);
    }

    static synchronized native void freeNativeCallback(long var0);

    static synchronized native long createNativeCallback(Callback var0, Method var1, Class<?>[] var2, Class<?> var3, int var4, int var5, String var6);

    static native int invokeInt(Function var0, long var1, int var3, Object[] var4);

    static native long invokeLong(Function var0, long var1, int var3, Object[] var4);

    static native void invokeVoid(Function var0, long var1, int var3, Object[] var4);

    static native float invokeFloat(Function var0, long var1, int var3, Object[] var4);

    static native double invokeDouble(Function var0, long var1, int var3, Object[] var4);

    static native long invokePointer(Function var0, long var1, int var3, Object[] var4);

    private static native void invokeStructure(Function var0, long var1, int var3, Object[] var4, long var5, long var7);

    static Structure invokeStructure(Function function, long fp, int callFlags, Object[] args, Structure s) {
        Native.invokeStructure(function, fp, callFlags, args, s.getPointer().peer, s.getTypeInfo().peer);
        return s;
    }

    static native Object invokeObject(Function var0, long var1, int var3, Object[] var4);

    static long open(String name) {
        return Native.open(name, -1);
    }

    static native long open(String var0, int var1);

    static native void close(long var0);

    static native long findSymbol(long var0, String var2);

    static native long indexOf(Pointer var0, long var1, long var3, byte var5);

    static native void read(Pointer var0, long var1, long var3, byte[] var5, int var6, int var7);

    static native void read(Pointer var0, long var1, long var3, short[] var5, int var6, int var7);

    static native void read(Pointer var0, long var1, long var3, char[] var5, int var6, int var7);

    static native void read(Pointer var0, long var1, long var3, int[] var5, int var6, int var7);

    static native void read(Pointer var0, long var1, long var3, long[] var5, int var6, int var7);

    static native void read(Pointer var0, long var1, long var3, float[] var5, int var6, int var7);

    static native void read(Pointer var0, long var1, long var3, double[] var5, int var6, int var7);

    static native void write(Pointer var0, long var1, long var3, byte[] var5, int var6, int var7);

    static native void write(Pointer var0, long var1, long var3, short[] var5, int var6, int var7);

    static native void write(Pointer var0, long var1, long var3, char[] var5, int var6, int var7);

    static native void write(Pointer var0, long var1, long var3, int[] var5, int var6, int var7);

    static native void write(Pointer var0, long var1, long var3, long[] var5, int var6, int var7);

    static native void write(Pointer var0, long var1, long var3, float[] var5, int var6, int var7);

    static native void write(Pointer var0, long var1, long var3, double[] var5, int var6, int var7);

    static native byte getByte(Pointer var0, long var1, long var3);

    static native char getChar(Pointer var0, long var1, long var3);

    static native short getShort(Pointer var0, long var1, long var3);

    static native int getInt(Pointer var0, long var1, long var3);

    static native long getLong(Pointer var0, long var1, long var3);

    static native float getFloat(Pointer var0, long var1, long var3);

    static native double getDouble(Pointer var0, long var1, long var3);

    static Pointer getPointer(long addr) {
        long peer = Native._getPointer(addr);
        return peer == 0L ? null : new Pointer(peer);
    }

    private static native long _getPointer(long var0);

    static native String getWideString(Pointer var0, long var1, long var3);

    static String getString(Pointer pointer, long offset) {
        return Native.getString(pointer, offset, Native.getDefaultStringEncoding());
    }

    static String getString(Pointer pointer, long offset, String encoding) {
        byte[] data = Native.getStringBytes(pointer, pointer.peer, offset);
        if (encoding != null) {
            try {
                return new String(data, encoding);
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
        }
        return new String(data);
    }

    static native byte[] getStringBytes(Pointer var0, long var1, long var3);

    static native void setMemory(Pointer var0, long var1, long var3, long var5, byte var7);

    static native void setByte(Pointer var0, long var1, long var3, byte var5);

    static native void setShort(Pointer var0, long var1, long var3, short var5);

    static native void setChar(Pointer var0, long var1, long var3, char var5);

    static native void setInt(Pointer var0, long var1, long var3, int var5);

    static native void setLong(Pointer var0, long var1, long var3, long var5);

    static native void setFloat(Pointer var0, long var1, long var3, float var5);

    static native void setDouble(Pointer var0, long var1, long var3, double var5);

    static native void setPointer(Pointer var0, long var1, long var3, long var5);

    static native void setWideString(Pointer var0, long var1, long var3, String var5);

    static native ByteBuffer getDirectByteBuffer(Pointer var0, long var1, long var3, long var5);

    public static native long malloc(long var0);

    public static native void free(long var0);

    @Deprecated
    public static native ByteBuffer getDirectByteBuffer(long var0, long var2);

    public static void detach(boolean detach) {
        Thread thread = Thread.currentThread();
        if (detach) {
            nativeThreads.remove(thread);
            Pointer p = nativeThreadTerminationFlag.get();
            Native.setDetachState(true, 0L);
        } else if (!nativeThreads.containsKey(thread)) {
            Pointer p = nativeThreadTerminationFlag.get();
            nativeThreads.put(thread, p);
            Native.setDetachState(false, p.peer);
        }
    }

    static Pointer getTerminationFlag(Thread t) {
        return nativeThreads.get(t);
    }

    private static native void setDetachState(boolean var0, long var1);

    static {
        callbackExceptionHandler = DEFAULT_HANDLER = new Callback.UncaughtExceptionHandler(){

            @Override
            public void uncaughtException(Callback c, Throwable e) {
                System.err.println("JNA: Callback " + c + " threw the following exception:");
                e.printStackTrace();
            }
        };
        Native.loadNativeDispatchLibrary();
        if (!Native.isCompatibleVersion("5.2.0", Native.getNativeVersion())) {
            String LS = System.getProperty("line.separator");
            throw new Error(LS + LS + "There is an incompatible JNA native library installed on this system" + LS + "Expected: " + "5.2.0" + LS + "Found:    " + Native.getNativeVersion() + LS + (jnidispatchPath != null ? "(at " + jnidispatchPath + ")" : System.getProperty("java.library.path")) + "." + LS + "To resolve this issue you may do one of the following:" + LS + " - remove or uninstall the offending library" + LS + " - set the system property jna.nosys=true" + LS + " - set jna.boot.library.path to include the path to the version of the " + LS + "   jnidispatch library included with the JNA jar file you are using" + LS);
        }
        POINTER_SIZE = Native.sizeof(0);
        LONG_SIZE = Native.sizeof(1);
        WCHAR_SIZE = Native.sizeof(2);
        SIZE_T_SIZE = Native.sizeof(3);
        BOOL_SIZE = Native.sizeof(4);
        Native.initIDs();
        if (Boolean.getBoolean("jna.protected")) {
            Native.setProtected(true);
        }
        MAX_ALIGNMENT = Platform.isSPARC() || Platform.isWindows() || Platform.isLinux() && (Platform.isARM() || Platform.isPPC() || Platform.isMIPS()) || Platform.isAIX() || Platform.isAndroid() ? 8 : LONG_SIZE;
        MAX_PADDING = Platform.isMac() && Platform.isPPC() ? 8 : MAX_ALIGNMENT;
        System.setProperty("jna.loaded", "true");
        finalizer = new Object(){

            protected void finalize() {
                Native.dispose();
            }
        };
        registeredClasses = new WeakHashMap();
        registeredLibraries = new WeakHashMap();
        nativeThreadTerminationFlag = new ThreadLocal<Memory>(){

            @Override
            protected Memory initialValue() {
                Memory m = new Memory(4L);
                m.clear();
                return m;
            }
        };
        nativeThreads = Collections.synchronizedMap(new WeakHashMap());
    }

    private static class AWT {
        private AWT() {
        }

        static long getWindowID(Window w) throws HeadlessException {
            return AWT.getComponentID(w);
        }

        static long getComponentID(Object o) throws HeadlessException {
            if (GraphicsEnvironment.isHeadless()) {
                throw new HeadlessException("No native windows when headless");
            }
            Component c = (Component)o;
            if (c.isLightweight()) {
                throw new IllegalArgumentException("Component must be heavyweight");
            }
            if (!c.isDisplayable()) {
                throw new IllegalStateException("Component must be displayable");
            }
            if (Platform.isX11() && System.getProperty("java.version").startsWith("1.4") && !c.isVisible()) {
                throw new IllegalStateException("Component must be visible");
            }
            return Native.getWindowHandle0(c);
        }
    }

    private static class Buffers {
        private Buffers() {
        }

        static boolean isBuffer(Class<?> cls) {
            return Buffer.class.isAssignableFrom(cls);
        }
    }

    public static interface ffi_callback {
        public void invoke(long var1, long var3, long var5);
    }
}

