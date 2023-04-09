package niggerlib.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A tag containing an integer.
 */
public class IntTag extends Tag {
    private int value;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public IntTag(String name) {
        this(name, 0);
    }

    /**
     * Creates a tag with the specified name.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public IntTag(String name, int value) {
        super(name);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    /**
     * Sets the value of this tag.
     *
     * @param value New value of this tag.
     */
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readInt();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value);
    }

    @Override
    public IntTag clone() {
        return new IntTag(this.getName(), this.getValue());
    }
}
