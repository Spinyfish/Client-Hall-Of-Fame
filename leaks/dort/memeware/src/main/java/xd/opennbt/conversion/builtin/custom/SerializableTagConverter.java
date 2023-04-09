package niggerlib.opennbt.conversion.builtin.custom;

import niggerlib.opennbt.conversion.TagConverter;
import niggerlib.opennbt.tag.builtin.custom.SerializableTag;

import java.io.Serializable;

/**
 * A converter that converts between SerializableTag and Serializable.
 */
public class SerializableTagConverter implements TagConverter<SerializableTag, Serializable> {
    @Override
    public Serializable convert(SerializableTag tag) {
        return tag.getValue();
    }

    @Override
    public SerializableTag convert(String name, Serializable value) {
        return new SerializableTag(name, value);
    }
}
