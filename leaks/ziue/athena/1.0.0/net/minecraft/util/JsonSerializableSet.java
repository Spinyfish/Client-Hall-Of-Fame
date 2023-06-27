package net.minecraft.util;

import com.google.common.collect.*;
import com.google.gson.*;
import java.util.*;

public class JsonSerializableSet extends ForwardingSet<String> implements IJsonSerializable
{
    private final Set<String> underlyingSet;
    
    public JsonSerializableSet() {
        this.underlyingSet = (Set<String>)Sets.newHashSet();
    }
    
    public void fromJson(final JsonElement json) {
        if (json.isJsonArray()) {
            for (final JsonElement jsonelement : json.getAsJsonArray()) {
                this.add((Object)jsonelement.getAsString());
            }
        }
    }
    
    public JsonElement getSerializableElement() {
        final JsonArray jsonarray = new JsonArray();
        for (final String s : this) {
            jsonarray.add((JsonElement)new JsonPrimitive(s));
        }
        return (JsonElement)jsonarray;
    }
    
    protected Set<String> delegate() {
        return this.underlyingSet;
    }
}
