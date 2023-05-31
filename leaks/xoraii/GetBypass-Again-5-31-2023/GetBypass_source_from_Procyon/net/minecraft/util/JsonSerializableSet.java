// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.util;

import java.util.Collection;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonArray;
import java.util.Iterator;
import com.google.gson.JsonElement;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.common.collect.ForwardingSet;

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
