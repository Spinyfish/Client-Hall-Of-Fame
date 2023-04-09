package niggerlib.opennbt.conversion.builtin.custom;

import niggerlib.opennbt.conversion.TagConverter;
import niggerlib.opennbt.tag.builtin.custom.FloatArrayTag;

/**
 * A converter that converts between FloatArrayTag and float[].
 */
public class FloatArrayTagConverter implements TagConverter<FloatArrayTag, float[]> {
    @Override
    public float[] convert(FloatArrayTag tag) {
        return tag.getValue();
    }

    @Override
    public FloatArrayTag convert(String name, float[] value) {
        return new FloatArrayTag(name, value);
    }
}
