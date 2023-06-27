package net.minecraft.client.resources.data;

import net.minecraft.util.*;
import java.lang.reflect.*;
import com.google.gson.*;

public class IMetadataSerializer
{
    private final IRegistry<String, Registration<? extends IMetadataSection>> metadataSectionSerializerRegistry;
    private final GsonBuilder gsonBuilder;
    private Gson gson;
    
    public IMetadataSerializer() {
        this.metadataSectionSerializerRegistry = new RegistrySimple<String, Registration<? extends IMetadataSection>>();
        (this.gsonBuilder = new GsonBuilder()).registerTypeHierarchyAdapter((Class)IChatComponent.class, (Object)new IChatComponent.Serializer());
        this.gsonBuilder.registerTypeHierarchyAdapter((Class)ChatStyle.class, (Object)new ChatStyle.Serializer());
        this.gsonBuilder.registerTypeAdapterFactory((TypeAdapterFactory)new EnumTypeAdapterFactory());
    }
    
    public <T extends IMetadataSection> void registerMetadataSectionType(final IMetadataSectionSerializer<T> p_110504_1_, final Class<T> p_110504_2_) {
        this.metadataSectionSerializerRegistry.putObject(p_110504_1_.getSectionName(), new Registration<IMetadataSection>((IMetadataSectionSerializer)p_110504_1_, (Class)p_110504_2_));
        this.gsonBuilder.registerTypeAdapter((Type)p_110504_2_, (Object)p_110504_1_);
        this.gson = null;
    }
    
    public <T extends IMetadataSection> T parseMetadataSection(final String p_110503_1_, final JsonObject p_110503_2_) {
        if (p_110503_1_ == null) {
            throw new IllegalArgumentException("Metadata section name cannot be null");
        }
        if (!p_110503_2_.has(p_110503_1_)) {
            return null;
        }
        if (!p_110503_2_.get(p_110503_1_).isJsonObject()) {
            throw new IllegalArgumentException("Invalid metadata for '" + p_110503_1_ + "' - expected object, found " + p_110503_2_.get(p_110503_1_));
        }
        final Registration<?> registration = this.metadataSectionSerializerRegistry.getObject(p_110503_1_);
        if (registration == null) {
            throw new IllegalArgumentException("Don't know how to handle metadata section '" + p_110503_1_ + "'");
        }
        return (T)this.getGson().fromJson((JsonElement)p_110503_2_.getAsJsonObject(p_110503_1_), (Class)registration.field_110500_b);
    }
    
    private Gson getGson() {
        if (this.gson == null) {
            this.gson = this.gsonBuilder.create();
        }
        return this.gson;
    }
    
    class Registration<T extends IMetadataSection>
    {
        final IMetadataSectionSerializer<T> field_110502_a;
        final Class<T> field_110500_b;
        
        private Registration(final IMetadataSectionSerializer<T> p_i1305_2_, final Class<T> p_i1305_3_) {
            this.field_110502_a = p_i1305_2_;
            this.field_110500_b = p_i1305_3_;
        }
    }
}
