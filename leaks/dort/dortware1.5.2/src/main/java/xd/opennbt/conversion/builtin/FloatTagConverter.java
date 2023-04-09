package niggerlib.opennbt.conversion.builtin;

import niggerlib.opennbt.conversion.TagConverter;
import niggerlib.opennbt.tag.builtin.FloatTag;

/**
 * A converter that converts between FloatTag and float.
 */
public class FloatTagConverter implements TagConverter<FloatTag, Float> {
    @Override
    public Float convert(FloatTag tag) {
        return tag.getValue();
    }

    @Override
    public FloatTag convert(String name, Float value) {
        return new FloatTag(name, value);
    }
}
