/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBTIO {
    public static CompoundTag readFile(String path) throws IOException {
        return NBTIO.readFile(new File(path));
    }

    public static CompoundTag readFile(File file) throws IOException {
        return NBTIO.readFile(file, true, false);
    }

    public static CompoundTag readFile(String path, boolean compressed, boolean littleEndian) throws IOException {
        return NBTIO.readFile(new File(path), compressed, littleEndian);
    }

    public static CompoundTag readFile(File file, boolean compressed, boolean littleEndian) throws IOException {
        CompoundTag tag;
        InputStream in = new FileInputStream(file);
        if (compressed) {
            in = new GZIPInputStream(in);
        }
        if (!((tag = NBTIO.readTag(in, littleEndian)) instanceof CompoundTag)) {
            throw new IOException("Root tag is not a CompoundTag!");
        }
        return tag;
    }

    public static void writeFile(CompoundTag tag, String path) throws IOException {
        NBTIO.writeFile(tag, new File(path));
    }

    public static void writeFile(CompoundTag tag, File file) throws IOException {
        NBTIO.writeFile(tag, file, true, false);
    }

    public static void writeFile(CompoundTag tag, String path, boolean compressed, boolean littleEndian) throws IOException {
        NBTIO.writeFile(tag, new File(path), compressed, littleEndian);
    }

    public static void writeFile(CompoundTag tag, File file, boolean compressed, boolean littleEndian) throws IOException {
        if (!file.exists()) {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        OutputStream out = new FileOutputStream(file);
        if (compressed) {
            out = new GZIPOutputStream(out);
        }
        NBTIO.writeTag(out, tag, littleEndian);
        out.close();
    }

    public static CompoundTag readTag(InputStream in) throws IOException {
        return NBTIO.readTag(in, false);
    }

    public static CompoundTag readTag(InputStream in, boolean littleEndian) throws IOException {
        return NBTIO.readTag((DataInput)((Object)(littleEndian ? new LittleEndianDataInputStream(in) : new DataInputStream(in))));
    }

    public static CompoundTag readTag(DataInput in) throws IOException {
        byte id = in.readByte();
        if (id != 10) {
            throw new IOException(String.format("Expected root tag to be a CompoundTag, was %s", id));
        }
        in.skipBytes(in.readUnsignedShort());
        CompoundTag tag = new CompoundTag();
        tag.read(in);
        return tag;
    }

    public static void writeTag(OutputStream out, CompoundTag tag) throws IOException {
        NBTIO.writeTag(out, tag, false);
    }

    public static void writeTag(OutputStream out, CompoundTag tag, boolean littleEndian) throws IOException {
        NBTIO.writeTag((DataOutput)((Object)(littleEndian ? new LittleEndianDataOutputStream(out) : new DataOutputStream(out))), tag);
    }

    public static void writeTag(DataOutput out, CompoundTag tag) throws IOException {
        out.writeByte(10);
        out.writeUTF("");
        tag.write(out);
    }

    private static final class LittleEndianDataOutputStream
    extends FilterOutputStream
    implements DataOutput {
        private LittleEndianDataOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public synchronized void write(int b) throws IOException {
            this.out.write(b);
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) throws IOException {
            this.out.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            this.out.flush();
        }

        @Override
        public void writeBoolean(boolean b) throws IOException {
            this.out.write(b ? 1 : 0);
        }

        @Override
        public void writeByte(int b) throws IOException {
            this.out.write(b);
        }

        @Override
        public void writeShort(int s) throws IOException {
            this.out.write(s & 0xFF);
            this.out.write(s >>> 8 & 0xFF);
        }

        @Override
        public void writeChar(int c) throws IOException {
            this.out.write(c & 0xFF);
            this.out.write(c >>> 8 & 0xFF);
        }

        @Override
        public void writeInt(int i) throws IOException {
            this.out.write(i & 0xFF);
            this.out.write(i >>> 8 & 0xFF);
            this.out.write(i >>> 16 & 0xFF);
            this.out.write(i >>> 24 & 0xFF);
        }

        @Override
        public void writeLong(long l) throws IOException {
            this.out.write((int)(l & 0xFFL));
            this.out.write((int)(l >>> 8 & 0xFFL));
            this.out.write((int)(l >>> 16 & 0xFFL));
            this.out.write((int)(l >>> 24 & 0xFFL));
            this.out.write((int)(l >>> 32 & 0xFFL));
            this.out.write((int)(l >>> 40 & 0xFFL));
            this.out.write((int)(l >>> 48 & 0xFFL));
            this.out.write((int)(l >>> 56 & 0xFFL));
        }

        @Override
        public void writeFloat(float f) throws IOException {
            this.writeInt(Float.floatToIntBits(f));
        }

        @Override
        public void writeDouble(double d) throws IOException {
            this.writeLong(Double.doubleToLongBits(d));
        }

        @Override
        public void writeBytes(String s) throws IOException {
            int len = s.length();
            for (int index = 0; index < len; ++index) {
                this.out.write((byte)s.charAt(index));
            }
        }

        @Override
        public void writeChars(String s) throws IOException {
            int len = s.length();
            for (int index = 0; index < len; ++index) {
                char c = s.charAt(index);
                this.out.write(c & 0xFF);
                this.out.write(c >>> 8 & 0xFF);
            }
        }

        @Override
        public void writeUTF(String s) throws IOException {
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            this.writeShort(bytes.length);
            this.write(bytes);
        }
    }

    private static final class LittleEndianDataInputStream
    extends FilterInputStream
    implements DataInput {
        private LittleEndianDataInputStream(InputStream in) {
            super(in);
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.in.read(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return this.in.read(b, off, len);
        }

        @Override
        public void readFully(byte[] b) throws IOException {
            this.readFully(b, 0, b.length);
        }

        @Override
        public void readFully(byte[] b, int off, int len) throws IOException {
            int read;
            if (len < 0) {
                throw new IndexOutOfBoundsException();
            }
            for (int pos = 0; pos < len; pos += read) {
                read = this.in.read(b, off + pos, len - pos);
                if (read >= 0) continue;
                throw new EOFException();
            }
        }

        @Override
        public int skipBytes(int n) throws IOException {
            int total;
            int skipped = 0;
            for (total = 0; total < n && (skipped = (int)this.in.skip(n - total)) > 0; total += skipped) {
            }
            return total;
        }

        @Override
        public boolean readBoolean() throws IOException {
            int val = this.in.read();
            if (val < 0) {
                throw new EOFException();
            }
            return val != 0;
        }

        @Override
        public byte readByte() throws IOException {
            int val = this.in.read();
            if (val < 0) {
                throw new EOFException();
            }
            return (byte)val;
        }

        @Override
        public int readUnsignedByte() throws IOException {
            int val = this.in.read();
            if (val < 0) {
                throw new EOFException();
            }
            return val;
        }

        @Override
        public short readShort() throws IOException {
            int b2;
            int b1 = this.in.read();
            if ((b1 | (b2 = this.in.read())) < 0) {
                throw new EOFException();
            }
            return (short)(b1 | b2 << 8);
        }

        @Override
        public int readUnsignedShort() throws IOException {
            int b2;
            int b1 = this.in.read();
            if ((b1 | (b2 = this.in.read())) < 0) {
                throw new EOFException();
            }
            return b1 | b2 << 8;
        }

        @Override
        public char readChar() throws IOException {
            int b2;
            int b1 = this.in.read();
            if ((b1 | (b2 = this.in.read())) < 0) {
                throw new EOFException();
            }
            return (char)(b1 | b2 << 8);
        }

        @Override
        public int readInt() throws IOException {
            int b4;
            int b3;
            int b2;
            int b1 = this.in.read();
            if ((b1 | (b2 = this.in.read()) | (b3 = this.in.read()) | (b4 = this.in.read())) < 0) {
                throw new EOFException();
            }
            return b1 | b2 << 8 | b3 << 16 | b4 << 24;
        }

        @Override
        public long readLong() throws IOException {
            long b8;
            long b7;
            long b6;
            long b5;
            long b4;
            long b3;
            long b2;
            long b1 = this.in.read();
            if ((b1 | (b2 = (long)this.in.read()) | (b3 = (long)this.in.read()) | (b4 = (long)this.in.read()) | (b5 = (long)this.in.read()) | (b6 = (long)this.in.read()) | (b7 = (long)this.in.read()) | (b8 = (long)this.in.read())) < 0L) {
                throw new EOFException();
            }
            return b1 | b2 << 8 | b3 << 16 | b4 << 24 | b5 << 32 | b6 << 40 | b7 << 48 | b8 << 56;
        }

        @Override
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(this.readInt());
        }

        @Override
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(this.readLong());
        }

        @Override
        public String readLine() throws IOException {
            throw new UnsupportedOperationException("Use readUTF.");
        }

        @Override
        public String readUTF() throws IOException {
            byte[] bytes = new byte[this.readUnsignedShort()];
            this.readFully(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}

