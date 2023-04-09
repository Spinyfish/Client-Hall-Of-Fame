package niggerlib.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A tag containing a double.
 */
public class DoubleTag extends Tag {
    private double value;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public DoubleTag(String name) {
        this(name, 0);
    }

    /**
     * Creates a tag with the specified name.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public DoubleTag(String name, double value) {
        super(name);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return this.value;
    }

    /**
     * Sets the value of this tag.
     *
     * @param value New value of this tag.
     */
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readDouble();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(this.value);
    }

    @Override
    public DoubleTag clone() {
        return new DoubleTag(this.getName(), this.getValue());
    }
}
