/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.Cleaner0;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.UnsafeAtomicIntegerFieldUpdater;
import io.netty.util.internal.UnsafeAtomicLongFieldUpdater;
import io.netty.util.internal.UnsafeAtomicReferenceFieldUpdater;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import sun.misc.Unsafe;

final class PlatformDependent0 {
    private static final InternalLogger logger;
    private static final Unsafe UNSAFE;
    private static final boolean BIG_ENDIAN;
    private static final long ADDRESS_FIELD_OFFSET;
    private static final long UNSAFE_COPY_THRESHOLD = 0x100000L;
    private static final boolean UNALIGNED;

    static boolean hasUnsafe() {
        return UNSAFE != null;
    }

    static void throwException(Throwable t) {
        UNSAFE.throwException(t);
    }

    static void freeDirectBuffer(ByteBuffer buffer) {
        Cleaner0.freeDirectBuffer(buffer);
    }

    static long directBufferAddress(ByteBuffer buffer) {
        return PlatformDependent0.getLong(buffer, ADDRESS_FIELD_OFFSET);
    }

    static long arrayBaseOffset() {
        return UNSAFE.arrayBaseOffset(byte[].class);
    }

    static Object getObject(Object object, long fieldOffset) {
        return UNSAFE.getObject(object, fieldOffset);
    }

    static Object getObjectVolatile(Object object, long fieldOffset) {
        return UNSAFE.getObjectVolatile(object, fieldOffset);
    }

    static int getInt(Object object, long fieldOffset) {
        return UNSAFE.getInt(object, fieldOffset);
    }

    private static long getLong(Object object, long fieldOffset) {
        return UNSAFE.getLong(object, fieldOffset);
    }

    static long objectFieldOffset(Field field) {
        return UNSAFE.objectFieldOffset(field);
    }

    static byte getByte(long address) {
        return UNSAFE.getByte(address);
    }

    static short getShort(long address) {
        if (UNALIGNED) {
            return UNSAFE.getShort(address);
        }
        if (BIG_ENDIAN) {
            return (short)(PlatformDependent0.getByte(address) << 8 | PlatformDependent0.getByte(address + 1L) & 0xFF);
        }
        return (short)(PlatformDependent0.getByte(address + 1L) << 8 | PlatformDependent0.getByte(address) & 0xFF);
    }

    static int getInt(long address) {
        if (UNALIGNED) {
            return UNSAFE.getInt(address);
        }
        if (BIG_ENDIAN) {
            return PlatformDependent0.getByte(address) << 24 | (PlatformDependent0.getByte(address + 1L) & 0xFF) << 16 | (PlatformDependent0.getByte(address + 2L) & 0xFF) << 8 | PlatformDependent0.getByte(address + 3L) & 0xFF;
        }
        return PlatformDependent0.getByte(address + 3L) << 24 | (PlatformDependent0.getByte(address + 2L) & 0xFF) << 16 | (PlatformDependent0.getByte(address + 1L) & 0xFF) << 8 | PlatformDependent0.getByte(address) & 0xFF;
    }

    static long getLong(long address) {
        if (UNALIGNED) {
            return UNSAFE.getLong(address);
        }
        if (BIG_ENDIAN) {
            return (long)PlatformDependent0.getByte(address) << 56 | ((long)PlatformDependent0.getByte(address + 1L) & 0xFFL) << 48 | ((long)PlatformDependent0.getByte(address + 2L) & 0xFFL) << 40 | ((long)PlatformDependent0.getByte(address + 3L) & 0xFFL) << 32 | ((long)PlatformDependent0.getByte(address + 4L) & 0xFFL) << 24 | ((long)PlatformDependent0.getByte(address + 5L) & 0xFFL) << 16 | ((long)PlatformDependent0.getByte(address + 6L) & 0xFFL) << 8 | (long)PlatformDependent0.getByte(address + 7L) & 0xFFL;
        }
        return (long)PlatformDependent0.getByte(address + 7L) << 56 | ((long)PlatformDependent0.getByte(address + 6L) & 0xFFL) << 48 | ((long)PlatformDependent0.getByte(address + 5L) & 0xFFL) << 40 | ((long)PlatformDependent0.getByte(address + 4L) & 0xFFL) << 32 | ((long)PlatformDependent0.getByte(address + 3L) & 0xFFL) << 24 | ((long)PlatformDependent0.getByte(address + 2L) & 0xFFL) << 16 | ((long)PlatformDependent0.getByte(address + 1L) & 0xFFL) << 8 | (long)PlatformDependent0.getByte(address) & 0xFFL;
    }

    static void putOrderedObject(Object object, long address, Object value) {
        UNSAFE.putOrderedObject(object, address, value);
    }

    static void putByte(long address, byte value) {
        UNSAFE.putByte(address, value);
    }

    static void putShort(long address, short value) {
        if (UNALIGNED) {
            UNSAFE.putShort(address, value);
        } else if (BIG_ENDIAN) {
            PlatformDependent0.putByte(address, (byte)(value >>> 8));
            PlatformDependent0.putByte(address + 1L, (byte)value);
        } else {
            PlatformDependent0.putByte(address + 1L, (byte)(value >>> 8));
            PlatformDependent0.putByte(address, (byte)value);
        }
    }

    static void putInt(long address, int value) {
        if (UNALIGNED) {
            UNSAFE.putInt(address, value);
        } else if (BIG_ENDIAN) {
            PlatformDependent0.putByte(address, (byte)(value >>> 24));
            PlatformDependent0.putByte(address + 1L, (byte)(value >>> 16));
            PlatformDependent0.putByte(address + 2L, (byte)(value >>> 8));
            PlatformDependent0.putByte(address + 3L, (byte)value);
        } else {
            PlatformDependent0.putByte(address + 3L, (byte)(value >>> 24));
            PlatformDependent0.putByte(address + 2L, (byte)(value >>> 16));
            PlatformDependent0.putByte(address + 1L, (byte)(value >>> 8));
            PlatformDependent0.putByte(address, (byte)value);
        }
    }

    static void putLong(long address, long value) {
        if (UNALIGNED) {
            UNSAFE.putLong(address, value);
        } else if (BIG_ENDIAN) {
            PlatformDependent0.putByte(address, (byte)(value >>> 56));
            PlatformDependent0.putByte(address + 1L, (byte)(value >>> 48));
            PlatformDependent0.putByte(address + 2L, (byte)(value >>> 40));
            PlatformDependent0.putByte(address + 3L, (byte)(value >>> 32));
            PlatformDependent0.putByte(address + 4L, (byte)(value >>> 24));
            PlatformDependent0.putByte(address + 5L, (byte)(value >>> 16));
            PlatformDependent0.putByte(address + 6L, (byte)(value >>> 8));
            PlatformDependent0.putByte(address + 7L, (byte)value);
        } else {
            PlatformDependent0.putByte(address + 7L, (byte)(value >>> 56));
            PlatformDependent0.putByte(address + 6L, (byte)(value >>> 48));
            PlatformDependent0.putByte(address + 5L, (byte)(value >>> 40));
            PlatformDependent0.putByte(address + 4L, (byte)(value >>> 32));
            PlatformDependent0.putByte(address + 3L, (byte)(value >>> 24));
            PlatformDependent0.putByte(address + 2L, (byte)(value >>> 16));
            PlatformDependent0.putByte(address + 1L, (byte)(value >>> 8));
            PlatformDependent0.putByte(address, (byte)value);
        }
    }

    static void copyMemory(long srcAddr, long dstAddr, long length) {
        while (length > 0L) {
            long size = Math.min(length, 0x100000L);
            UNSAFE.copyMemory(srcAddr, dstAddr, size);
            length -= size;
            srcAddr += size;
            dstAddr += size;
        }
    }

    static void copyMemory(Object src, long srcOffset, Object dst, long dstOffset, long length) {
        while (length > 0L) {
            long size = Math.min(length, 0x100000L);
            UNSAFE.copyMemory(src, srcOffset, dst, dstOffset, size);
            length -= size;
            srcOffset += size;
            dstOffset += size;
        }
    }

    static <U, W> AtomicReferenceFieldUpdater<U, W> newAtomicReferenceFieldUpdater(Class<U> tclass, String fieldName) throws Exception {
        return new UnsafeAtomicReferenceFieldUpdater(UNSAFE, tclass, fieldName);
    }

    static <T> AtomicIntegerFieldUpdater<T> newAtomicIntegerFieldUpdater(Class<?> tclass, String fieldName) throws Exception {
        return new UnsafeAtomicIntegerFieldUpdater(UNSAFE, tclass, fieldName);
    }

    static <T> AtomicLongFieldUpdater<T> newAtomicLongFieldUpdater(Class<?> tclass, String fieldName) throws Exception {
        return new UnsafeAtomicLongFieldUpdater(UNSAFE, tclass, fieldName);
    }

    static ClassLoader getClassLoader(final Class<?> clazz) {
        if (System.getSecurityManager() == null) {
            return clazz.getClassLoader();
        }
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return clazz.getClassLoader();
            }
        });
    }

    static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }

    static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        }
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return ClassLoader.getSystemClassLoader();
            }
        });
    }

    static int addressSize() {
        return UNSAFE.addressSize();
    }

    static long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }

    static void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }

    private PlatformDependent0() {
    }

    static {
        Unsafe unsafe;
        Field addressField;
        logger = InternalLoggerFactory.getInstance(PlatformDependent0.class);
        BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
        ByteBuffer direct = ByteBuffer.allocateDirect(1);
        try {
            addressField = Buffer.class.getDeclaredField("address");
            addressField.setAccessible(true);
            if (addressField.getLong(ByteBuffer.allocate(1)) != 0L) {
                addressField = null;
            } else if (addressField.getLong(direct) == 0L) {
                addressField = null;
            }
        }
        catch (Throwable t) {
            addressField = null;
        }
        logger.debug("java.nio.Buffer.address: {}", (Object)(addressField != null ? "available" : "unavailable"));
        if (addressField != null) {
            try {
                Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                unsafe = (Unsafe)unsafeField.get(null);
                logger.debug("sun.misc.Unsafe.theUnsafe: {}", (Object)(unsafe != null ? "available" : "unavailable"));
                try {
                    if (unsafe != null) {
                        unsafe.getClass().getDeclaredMethod("copyMemory", Object.class, Long.TYPE, Object.class, Long.TYPE, Long.TYPE);
                        logger.debug("sun.misc.Unsafe.copyMemory: available");
                    }
                }
                catch (NoSuchMethodError t) {
                    logger.debug("sun.misc.Unsafe.copyMemory: unavailable");
                    throw t;
                }
                catch (NoSuchMethodException e) {
                    logger.debug("sun.misc.Unsafe.copyMemory: unavailable");
                    throw e;
                }
            }
            catch (Throwable cause) {
                unsafe = null;
            }
        } else {
            unsafe = null;
        }
        UNSAFE = unsafe;
        if (unsafe == null) {
            ADDRESS_FIELD_OFFSET = -1L;
            UNALIGNED = false;
        } else {
            boolean unaligned;
            ADDRESS_FIELD_OFFSET = PlatformDependent0.objectFieldOffset(addressField);
            try {
                Class<?> bitsClass = Class.forName("java.nio.Bits", false, ClassLoader.getSystemClassLoader());
                Method unalignedMethod = bitsClass.getDeclaredMethod("unaligned", new Class[0]);
                unalignedMethod.setAccessible(true);
                unaligned = Boolean.TRUE.equals(unalignedMethod.invoke(null, new Object[0]));
            }
            catch (Throwable t) {
                String arch = SystemPropertyUtil.get("os.arch", "");
                unaligned = arch.matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
            }
            UNALIGNED = unaligned;
            logger.debug("java.nio.Bits.unaligned: {}", (Object)UNALIGNED);
        }
    }
}

