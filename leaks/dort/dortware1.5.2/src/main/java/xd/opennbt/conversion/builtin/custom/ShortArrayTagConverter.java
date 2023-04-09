package niggerlib.opennbt.conversion.builtin.custom;

import niggerlib.opennbt.conversion.TagConverter;
import niggerlib.opennbt.tag.builtin.custom.ShortArrayTag;

/**
 * A converter that converts between ShortArrayTag and short[].
 */
public class ShortArrayTagConverter implements TagConverter<ShortArrayTag, short[]> {
    @Override
    public short[] convert(ShortArrayTag tag) {
        return tag.getValue();
    }

    @Override
    public ShortArrayTag convert(String name, short[] value) {
        return new ShortArrayTag(name, value);
    }
}
