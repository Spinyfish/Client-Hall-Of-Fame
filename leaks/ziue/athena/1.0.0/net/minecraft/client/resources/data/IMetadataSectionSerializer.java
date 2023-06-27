package net.minecraft.client.resources.data;

import com.google.gson.*;

public interface IMetadataSectionSerializer<T extends IMetadataSection> extends JsonDeserializer<T>
{
    String getSectionName();
}
