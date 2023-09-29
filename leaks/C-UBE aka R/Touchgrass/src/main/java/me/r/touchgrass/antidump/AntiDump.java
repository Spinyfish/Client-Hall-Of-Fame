package me.r.touchgrass.antidump;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import sun.management.VMManagement;
import sun.misc.Unsafe;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class AntiDump {

    private static final Unsafe unsafe;
    private static Method findNative;
    private static ClassLoader classLoader;

    private static boolean ENABLE;

    private static final String[] naughtyFlags = {
            "-XBootclasspath",
            "-javaagent",
            "-Xdebug",
            "-agentlib",
            "-Xrunjdwp",
            "-Xnoagent",
            "-verbose",
            "-DproxySet",
            "-DproxyHost",
            "-DproxyPort",
            "-Djavax.net.ssl.trustStore",
            "-Djavax.net.ssl.trustStorePassword"
    };

    /* UnsafeProvider */
    static {
        Unsafe ref;
        try {
            Class<?> clazz = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = clazz.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            ref = (Unsafe) theUnsafe.get(null);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            ref = null;
        }

        unsafe = ref;
    }

    /* CookieFuckery */
    public static void check() {
        if (!ENABLE) return;
        try {
            Field jvmField = ManagementFactory.getRuntimeMXBean().getClass().getDeclaredField("jvm");
            jvmField.setAccessible(true);
            VMManagement jvm = (VMManagement) jvmField.get(ManagementFactory.getRuntimeMXBean());
            List<String> inputArguments = jvm.getVmArguments();

            for (String arg : naughtyFlags) {
                for (String inputArgument : inputArguments) {
                    if (inputArgument.contains(arg)) {
                        System.out.println("Found illegal program arguments!");
                        dumpDetected();
                    }
                }
            }
            try {
                byte[] bytes = createDummyClass("java/lang/instrument/Instrumentation");
                unsafe.defineClass("java.lang.instrument.Instrumentation", bytes, 0, bytes.length, null, null);
            } catch (Throwable e) {
                e.printStackTrace();
                dumpDetected();
            }
            if (isClassLoaded("sun.instrument.InstrumentationImpl")) {
                System.out.println("Found sun.instrument.InstrumentationImpl!");
                dumpDetected();
            }

            byte[] bytes = createDummyClass("me/r/touchgrass/MaliciousClassFilter");
            unsafe.defineClass("me.r.touchgrass.MaliciousClassFilter", bytes, 0, bytes.length, null, null); // Change this.
            System.setProperty("sun.jvm.hotspot.tools.jcore.filter", "me.r.touchgrass.MaliciousClassFilter"); // Change this.

            disassembleStruct();

        } catch (Throwable e) {
            e.printStackTrace();
            dumpDetected();
        }
    }

    private static boolean isClassLoaded(@SuppressWarnings("SameParameterValue") String clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
        m.setAccessible(true);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ClassLoader scl = ClassLoader.getSystemClassLoader();
        return m.invoke(cl, clazz) != null || m.invoke(scl, clazz) != null;
    }


    /* DummyClassProvider */
    private static byte[] createDummyClass(String name) {
        ClassNode classNode = new ClassNode();
        classNode.name = name.replace('.', '/');
        classNode.access = ACC_PUBLIC;
        classNode.version = V1_8;
        classNode.superName = "java/lang/Object";

        List<MethodNode> methods = new ArrayList<>();
        MethodNode methodNode = new MethodNode(ACC_PUBLIC + ACC_STATIC, "<clinit>", "()V", null, null);

        InsnList insn = new InsnList();
        insn.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        insn.add(new LdcInsnNode("Nice try"));
        insn.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        insn.add(new TypeInsnNode(NEW, "java/lang/Throwable"));
        insn.add(new InsnNode(DUP));
        insn.add(new LdcInsnNode("owned"));
        insn.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/Throwable", "<init>", "(Ljava/lang/String;)V", false));
        insn.add(new InsnNode(ATHROW));

        methodNode.instructions = insn;

        methods.add(methodNode);
        classNode.methods = methods;

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static void dumpDetected() {
        try {
            unsafe.putAddress(0, 0);
        } catch (Exception e) {}
        FMLCommonHandler.instance().exitJava(0, false); // Shutdown.
        Error error = new Error();
        error.setStackTrace(new StackTraceElement[]{});
        throw error;
    }

    /* StructDissasembler */
    private static void resolveClassLoader() throws NoSuchMethodException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            String vmName = System.getProperty("java.vm.name");
            String dll = vmName.contains("Client VM") ? "/bin/client/jvm.dll" : "/bin/server/jvm.dll";
            try {
                System.load(System.getProperty("java.home") + dll);
            } catch (UnsatisfiedLinkError e) {
                throw new RuntimeException(e);
            }
            classLoader = AntiDump.class.getClassLoader();
        } else {
            classLoader = null;
        }

        findNative = ClassLoader.class.getDeclaredMethod("findNative", ClassLoader.class, String.class);

        try {
            Class<?> cls = ClassLoader.getSystemClassLoader().loadClass("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            unsafe.putObjectVolatile(cls, unsafe.staticFieldOffset(logger), null);
        } catch (Throwable t) {}

        findNative.setAccessible(true);
    }

    private static void setupIntrospection() throws Throwable {
        resolveClassLoader();
    }

    public static void disassembleStruct() {
        try {
            setupIntrospection();
            long entry = getSymbol("gHotSpotVMStructs");
            unsafe.putLong(entry, 0);
        } catch (Throwable t) {
            t.printStackTrace();
            dumpDetected();
        }
    }

    private static long getSymbol(String symbol) throws InvocationTargetException, IllegalAccessException {
        long address = (Long) findNative.invoke(null, classLoader, symbol);
        if (address == 0)
            throw new NoSuchElementException(symbol);

        return unsafe.getLong(address);
    }

    private static String getString(long addr) {
        if (addr == 0) {
            return null;
        }

        char[] chars = new char[40];
        int offset = 0;
        for (byte b; (b = unsafe.getByte(addr + offset)) != 0; ) {
            if (offset >= chars.length) chars = Arrays.copyOf(chars, offset * 2);
            chars[offset++] = (char) b;
        }

        return new String(chars, 0, offset);
    }

    private static void readStructs(Map<String, Set<Object[]>> structs) throws InvocationTargetException, IllegalAccessException {
        long entry = getSymbol("gHotSpotVMStructs");
        long typeNameOffset = getSymbol("gHotSpotVMStructEntryTypeNameOffset");
        long fieldNameOffset = getSymbol("gHotSpotVMStructEntryFieldNameOffset");
        long typeStringOffset = getSymbol("gHotSpotVMStructEntryTypeStringOffset");
        long isStaticOffset = getSymbol("gHotSpotVMStructEntryIsStaticOffset");
        long offsetOffset = getSymbol("gHotSpotVMStructEntryOffsetOffset");
        long addressOffset = getSymbol("gHotSpotVMStructEntryAddressOffset");
        long arrayStride = getSymbol("gHotSpotVMStructEntryArrayStride");

        for (; ; entry += arrayStride) {
            String typeName = getString(unsafe.getLong(entry + typeNameOffset));
            String fieldName = getString(unsafe.getLong(entry + fieldNameOffset));
            if (fieldName == null) break;

            String typeString = getString(unsafe.getLong(entry + typeStringOffset));
            boolean isStatic = unsafe.getInt(entry + isStaticOffset) != 0;
            long offset = unsafe.getLong(entry + (isStatic ? addressOffset : offsetOffset));

            Set<Object[]> fields = structs.get(typeName);
            if (fields == null) structs.put(typeName, fields = new HashSet<>());
            fields.add(new Object[]{fieldName, typeString, offset, isStatic});
        }
        long address = (Long) findNative.invoke(null, classLoader, 2);
        if (address == 0)
            throw new NoSuchElementException("");

        unsafe.getLong(address);
    }

    private static void readTypes(Map<String, Object[]> types, Map<String, Set<Object[]>> structs) throws InvocationTargetException, IllegalAccessException {
        long entry = getSymbol("gHotSpotVMTypes");
        long typeNameOffset = getSymbol("gHotSpotVMTypeEntryTypeNameOffset");
        long superclassNameOffset = getSymbol("gHotSpotVMTypeEntrySuperclassNameOffset");
        long isOopTypeOffset = getSymbol("gHotSpotVMTypeEntryIsOopTypeOffset");
        long isIntegerTypeOffset = getSymbol("gHotSpotVMTypeEntryIsIntegerTypeOffset");
        long isUnsignedOffset = getSymbol("gHotSpotVMTypeEntryIsUnsignedOffset");
        long sizeOffset = getSymbol("gHotSpotVMTypeEntrySizeOffset");
        long arrayStride = getSymbol("gHotSpotVMTypeEntryArrayStride");

        for (; ; entry += arrayStride) {
            String typeName = getString(unsafe.getLong(entry + typeNameOffset));
            if (typeName == null) break;

            String superclassName = getString(unsafe.getLong(entry + superclassNameOffset));
            boolean isOop = unsafe.getInt(entry + isOopTypeOffset) != 0;
            boolean isInt = unsafe.getInt(entry + isIntegerTypeOffset) != 0;
            boolean isUnsigned = unsafe.getInt(entry + isUnsignedOffset) != 0;
            int size = unsafe.getInt(entry + sizeOffset);

            Set<Object[]> fields = structs.get(typeName);
            types.put(typeName, new Object[]{typeName, superclassName, size, isOop, isInt, isUnsigned, fields});
        }
    }
}