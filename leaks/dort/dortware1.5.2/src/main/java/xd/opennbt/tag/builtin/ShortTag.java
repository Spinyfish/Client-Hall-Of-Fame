package niggerlib.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A tag containing a short.
 */
public class ShortTag extends Tag {
    private short value;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public ShortTag(String name) {
        this(name, (short) 0);
    }

    /**
     * Creates a tag with the specified name.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public ShortTag(String name, short value) {
        super(name);
        this.value = value;
    }

    @Override
    public Short getValue() {
        return this.value;
    }

    /**
     * Sets the value of this tag.
     *
     * @param value New value of this tag.
     */
    public void setValue(short value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readShort();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeShort(this.value);
    }

    @Override
    public ShortTag clone() {
        return new ShortTag(this.getName(), this.getValue());
    }
}
