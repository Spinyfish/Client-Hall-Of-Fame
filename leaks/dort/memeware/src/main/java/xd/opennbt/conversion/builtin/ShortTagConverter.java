package niggerlib.opennbt.conversion.builtin;

import niggerlib.opennbt.conversion.TagConverter;
import niggerlib.opennbt.tag.builtin.ShortTag;

/**
 * A converter that converts between ShortTag and short.
 */
public class ShortTagConverter implements TagConverter<ShortTag, Short> {
    @Override
    public Short convert(ShortTag tag) {
        return tag.getValue();
    }

    @Override
    public ShortTag convert(String name, Short value) {
        return new ShortTag(name, value);
    }
}
