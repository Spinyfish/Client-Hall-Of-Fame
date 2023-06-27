package net.minecraft.util;

import com.google.gson.*;

public interface IJsonSerializable
{
    void fromJson(final JsonElement p0);
    
    JsonElement getSerializableElement();
}
