package niggerlib.opennbt.tag.builtin.custom;

import niggerlib.opennbt.tag.builtin.Tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A tag containing a string array.
 */
public class StringArrayTag extends Tag {
    private String[] value;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public StringArrayTag(String name) {
        this(name, new String[0]);
    }

    /**
     * Creates a tag with the specified name.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public StringArrayTag(String name, String[] value) {
        super(name);
        this.value = value;
    }

    @Override
    public String[] getValue() {
        return this.value.clone();
    }

    /**
     * Sets the value of this tag.
     *
     * @param value New value of this tag.
     */
    public void setValue(String[] value) {
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
    public String getValue(int index) {
        return this.value[index];
    }

    /**
     * Sets a value in this tag's array.
     *
     * @param index Index of the value.
     * @param value Value to set.
     */
    public void setValue(int index, String value) {
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
        this.value = new String[in.readInt()];
        for (int index = 0; index < this.value.length; index++) {
            this.value[index] = in.readUTF();
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (int index = 0; index < this.value.length; index++) {
            out.writeUTF(this.value[index]);
        }
    }

    @Override
    public StringArrayTag clone() {
        return new StringArrayTag(this.getName(), this.getValue());
    }
}
