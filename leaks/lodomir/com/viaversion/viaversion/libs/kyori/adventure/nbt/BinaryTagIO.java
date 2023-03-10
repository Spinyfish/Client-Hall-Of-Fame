/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagReaderImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagTypes;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTagWriterImpl;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.CompoundBinaryTag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import org.jetbrains.annotations.NotNull;

public final class BinaryTagIO {
    private BinaryTagIO() {
    }

    @NotNull
    public static Reader unlimitedReader() {
        return BinaryTagReaderImpl.UNLIMITED;
    }

    @NotNull
    public static Reader reader() {
        return BinaryTagReaderImpl.DEFAULT_LIMIT;
    }

    @NotNull
    public static Reader reader(long sizeLimitBytes) {
        if (sizeLimitBytes <= 0L) {
            throw new IllegalArgumentException("The size limit must be greater than zero");
        }
        return new BinaryTagReaderImpl(sizeLimitBytes);
    }

    @NotNull
    public static Writer writer() {
        return BinaryTagWriterImpl.INSTANCE;
    }

    @Deprecated
    @NotNull
    public static CompoundBinaryTag readPath(@NotNull Path path) throws IOException {
        return BinaryTagIO.reader().read(path);
    }

    @Deprecated
    @NotNull
    public static CompoundBinaryTag readInputStream(@NotNull InputStream input) throws IOException {
        return BinaryTagIO.reader().read(input);
    }

    @Deprecated
    @NotNull
    public static CompoundBinaryTag readCompressedPath(@NotNull Path path) throws IOException {
        return BinaryTagIO.reader().read(path, Compression.GZIP);
    }

    @Deprecated
    @NotNull
    public static CompoundBinaryTag readCompressedInputStream(@NotNull InputStream input) throws IOException {
        return BinaryTagIO.reader().read(input, Compression.GZIP);
    }

    @Deprecated
    @NotNull
    public static CompoundBinaryTag readDataInput(@NotNull DataInput input) throws IOException {
        return BinaryTagIO.reader().read(input);
    }

    @Deprecated
    public static void writePath(@NotNull CompoundBinaryTag tag, @NotNull Path path) throws IOException {
        BinaryTagIO.writer().write(tag, path);
    }

    @Deprecated
    public static void writeOutputStream(@NotNull CompoundBinaryTag tag, @NotNull OutputStream output) throws IOException {
        BinaryTagIO.writer().write(tag, output);
    }

    @Deprecated
    public static void writeCompressedPath(@NotNull CompoundBinaryTag tag, @NotNull Path path) throws IOException {
        BinaryTagIO.writer().write(tag, path, Compression.GZIP);
    }

    @Deprecated
    public static void writeCompressedOutputStream(@NotNull CompoundBinaryTag tag, @NotNull OutputStream output) throws IOException {
        BinaryTagIO.writer().write(tag, output, Compression.GZIP);
    }

    @Deprecated
    public static void writeDataOutput(@NotNull CompoundBinaryTag tag, @NotNull DataOutput output) throws IOException {
        BinaryTagIO.writer().write(tag, output);
    }

    static {
        BinaryTagTypes.COMPOUND.id();
    }

    public static abstract class Compression {
        public static final Compression NONE = new Compression(){

            @Override
            @NotNull
            InputStream decompress(@NotNull InputStream is) {
                return is;
            }

            @Override
            @NotNull
            OutputStream compress(@NotNull OutputStream os) {
                return os;
            }

            public String toString() {
                return "Compression.NONE";
            }
        };
        public static final Compression GZIP = new Compression(){

            @Override
            @NotNull
            InputStream decompress(@NotNull InputStream is) throws IOException {
                return new GZIPInputStream(is);
            }

            @Override
            @NotNull
            OutputStream compress(@NotNull OutputStream os) throws IOException {
                return new GZIPOutputStream(os);
            }

            public String toString() {
                return "Compression.GZIP";
            }
        };
        public static final Compression ZLIB = new Compression(){

            @Override
            @NotNull
            InputStream decompress(@NotNull InputStream is) {
                return new InflaterInputStream(is);
            }

            @Override
            @NotNull
            OutputStream compress(@NotNull OutputStream os) {
                return new DeflaterOutputStream(os);
            }

            public String toString() {
                return "Compression.ZLIB";
            }
        };

        @NotNull
        abstract InputStream decompress(@NotNull InputStream var1) throws IOException;

        @NotNull
        abstract OutputStream compress(@NotNull OutputStream var1) throws IOException;
    }

    public static interface Writer {
        default public void write(@NotNull CompoundBinaryTag tag, @NotNull Path path) throws IOException {
            this.write(tag, path, Compression.NONE);
        }

        public void write(@NotNull CompoundBinaryTag var1, @NotNull Path var2, @NotNull Compression var3) throws IOException;

        default public void write(@NotNull CompoundBinaryTag tag, @NotNull OutputStream output) throws IOException {
            this.write(tag, output, Compression.NONE);
        }

        public void write(@NotNull CompoundBinaryTag var1, @NotNull OutputStream var2, @NotNull Compression var3) throws IOException;

        public void write(@NotNull CompoundBinaryTag var1, @NotNull DataOutput var2) throws IOException;

        default public void writeNamed( @NotNull Map.Entry<String, CompoundBinaryTag> tag, @NotNull Path path) throws IOException {
            this.writeNamed(tag, path, Compression.NONE);
        }

        public void writeNamed( @NotNull Map.Entry<String, CompoundBinaryTag> var1, @NotNull Path var2, @NotNull Compression var3) throws IOException;

        default public void writeNamed( @NotNull Map.Entry<String, CompoundBinaryTag> tag, @NotNull OutputStream output) throws IOException {
            this.writeNamed(tag, output, Compression.NONE);
        }

        public void writeNamed( @NotNull Map.Entry<String, CompoundBinaryTag> var1, @NotNull OutputStream var2, @NotNull Compression var3) throws IOException;

        public void writeNamed( @NotNull Map.Entry<String, CompoundBinaryTag> var1, @NotNull DataOutput var2) throws IOException;
    }

    public static interface Reader {
        @NotNull
        default public CompoundBinaryTag read(@NotNull Path path) throws IOException {
            return this.read(path, Compression.NONE);
        }

        @NotNull
        public CompoundBinaryTag read(@NotNull Path var1, @NotNull Compression var2) throws IOException;

        @NotNull
        default public CompoundBinaryTag read(@NotNull InputStream input) throws IOException {
            return this.read(input, Compression.NONE);
        }

        @NotNull
        public CompoundBinaryTag read(@NotNull InputStream var1, @NotNull Compression var2) throws IOException;

        @NotNull
        public CompoundBinaryTag read(@NotNull DataInput var1) throws IOException;

        default public  @NotNull Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull Path path) throws IOException {
            return this.readNamed(path, Compression.NONE);
        }

        public  @NotNull Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull Path var1, @NotNull Compression var2) throws IOException;

        default public  @NotNull Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull InputStream input) throws IOException {
            return this.readNamed(input, Compression.NONE);
        }

        public  @NotNull Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull InputStream var1, @NotNull Compression var2) throws IOException;

        public  @NotNull Map.Entry<String, CompoundBinaryTag> readNamed(@NotNull DataInput var1) throws IOException;
    }
}

