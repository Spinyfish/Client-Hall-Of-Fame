/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.JZlibDecoder;
import io.netty.handler.codec.compression.JZlibEncoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibEncoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ZlibCodecFactory {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ZlibCodecFactory.class);
    private static final boolean noJdkZlibDecoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibDecoder", true);

    public static ZlibEncoder newZlibEncoder(int compressionLevel) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(compressionLevel);
        }
        return new JdkZlibEncoder(compressionLevel);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(wrapper);
        }
        return new JdkZlibEncoder(wrapper);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(wrapper, compressionLevel);
        }
        return new JdkZlibEncoder(wrapper, compressionLevel);
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(wrapper, compressionLevel, windowBits, memLevel);
        }
        return new JdkZlibEncoder(wrapper, compressionLevel);
    }

    public static ZlibEncoder newZlibEncoder(byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(dictionary);
        }
        return new JdkZlibEncoder(dictionary);
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel, byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(compressionLevel, dictionary);
        }
        return new JdkZlibEncoder(compressionLevel, dictionary);
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(compressionLevel, windowBits, memLevel, dictionary);
        }
        return new JdkZlibEncoder(compressionLevel, dictionary);
    }

    public static ZlibDecoder newZlibDecoder() {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
            return new JZlibDecoder();
        }
        return new JdkZlibDecoder();
    }

    public static ZlibDecoder newZlibDecoder(ZlibWrapper wrapper) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
            return new JZlibDecoder(wrapper);
        }
        return new JdkZlibDecoder(wrapper);
    }

    public static ZlibDecoder newZlibDecoder(byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
            return new JZlibDecoder(dictionary);
        }
        return new JdkZlibDecoder(dictionary);
    }

    private ZlibCodecFactory() {
    }

    static {
        logger.debug("-Dio.netty.noJdkZlibDecoder: {}", (Object)noJdkZlibDecoder);
    }
}

