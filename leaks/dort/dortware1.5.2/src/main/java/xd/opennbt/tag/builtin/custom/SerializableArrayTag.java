package niggerlib.opennbt.tag.builtin.custom;

import niggerlib.opennbt.tag.builtin.Tag;

import java.io.*;

/**
 * A tag containing an array of serializable objects.
 */
public class SerializableArrayTag extends Tag {
    private Serializable[] value;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public SerializableArrayTag(String name) {
        this(name, new Serializable[0]);
    }

    /**
     * Creates a tag with the specified name.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public SerializableArrayTag(String name, Serializable[] value) {
        super(name);
        this.value = value;
    }

    @Override
    public Serializable[] getValue() {
        return this.value.clone();
    }

    /**
     * Sets the value of this tag.
     *
     * @param value New value of this tag.
     */
    public void setValue(Serializable[] value) {
        if (value == null) {
            return;
        }

        this.value = value.clone();
    }

    /**
     * Gets a value in this tag's array.
     *
     * @param index Index of the value.
     * @return The value at the given index.
     */
    public Serializable getValue(int index) {
        return this.value[index];
    }

    /**
     * Sets a value in this tag's array.
     *
     * @param index Index of the value.
     * @param value Value to set.
     */
    public void setValue(int index, Serializable value) {
        this.value[index] = value;
    }

    /**
     * Gets the length of this tag's array.
     *
     * @return This tag's array length.
     */
    public int length() {
        return this.value.length;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = new Serializable[in.readInt()];
        ObjectInputStream str = new ObjectInputStream(new DataInputInputStream(in));
        for (int index = 0; index < this.value.length; index++) {
            try {
                this.value[index] = (Serializable) str.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException("Class not found while reading SerializableArrayTag!", e);
            }
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        ObjectOutputStream str = new ObjectOutputStream(new DataOutputOutputStream(out));
        for (int index = 0; index < this.value.length; index++) {
            str.writeObject(this.value[index]);
        }
    }

    @Override
    public SerializableArrayTag clone() {
        return new SerializableArrayTag(this.getName(), this.getValue());
    }

    private static class DataInputInputStream extends InputStream {
        private DataInput in;

        public DataInputInputStream(DataInput in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            return this.in.readUnsignedByte();
        }

        @Override
        public int read(byte[] b) throws IOException {
            this.in.readFully(b);
            return b.length;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            this.in.readFully(b, off, len);
            return len;
        }

        @Override
        public long skip(long l) throws IOException {
            return this.in.skipBytes((int) l);
        }

        @Override
        public int available() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public synchronized void mark(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void reset() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean markSupported() {
            return false;
        }
    }

    private static class DataOutputOutputStream extends OutputStream {
        private DataOutput out;

        public DataOutputOutputStream(DataOutput out) {
            this.out = out;
        }

        @Override
        public void write(int b) throws IOException {
            this.out.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.out.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.out.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }
}
