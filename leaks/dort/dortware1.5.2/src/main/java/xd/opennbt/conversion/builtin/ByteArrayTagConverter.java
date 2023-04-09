package niggerlib.opennbt.conversion.builtin;

import niggerlib.opennbt.conversion.TagConverter;
import niggerlib.opennbt.tag.builtin.ByteArrayTag;

/**
 * A converter that converts between ByteArrayTag and byte[].
 */
public class ByteArrayTagConverter implements TagConverter<ByteArrayTag, byte[]> {
    @Override
    public byte[] convert(ByteArrayTag tag) {
        return tag.getValue();
    }

    @Override
    public ByteArrayTag convert(String name, byte[] value) {
        return new ByteArrayTag(name, value);
    }
}
