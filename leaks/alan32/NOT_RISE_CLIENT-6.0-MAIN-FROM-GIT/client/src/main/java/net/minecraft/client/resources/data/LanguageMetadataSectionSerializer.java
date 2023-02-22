package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.Language;
import net.minecraft.util.JsonUtils;

import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.Set;

public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer<LanguageMetadataSection> {
    public LanguageMetadataSection deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
        final Set<Language> set = Sets.newHashSet();

        for (final Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            final String s = entry.getKey();
            final JsonObject jsonobject1 = JsonUtils.getJsonObject(entry.getValue(), "language");
            final String s1 = JsonUtils.getString(jsonobject1, "region");
            final String s2 = JsonUtils.getString(jsonobject1, "name");
            final boolean flag = JsonUtils.getBoolean(jsonobject1, "bidirectional", false);

            if (s1.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + s + "'->region: empty value");
            }

            if (s2.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + s + "'->name: empty value");
            }

            if (!set.add(new Language(s, s1, s2, flag))) {
                throw new JsonParseException("Duplicate language->'" + s + "' defined");
            }
        }

        return new LanguageMetadataSection(set);
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName() {
        return "language";
    }
}
