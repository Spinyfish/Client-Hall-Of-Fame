package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class SimpleResource implements IResource {
    private final Map<String, IMetadataSection> mapMetadataSections = Maps.newHashMap();
    private final String resourcePackName;
    private final ResourceLocation srResourceLocation;
    private final InputStream resourceInputStream;
    private final InputStream mcmetaInputStream;
    private final IMetadataSerializer srMetadataSerializer;
    private boolean mcmetaJsonChecked;
    private JsonObject mcmetaJson;

    public SimpleResource(final String resourcePackNameIn, final ResourceLocation srResourceLocationIn, final InputStream resourceInputStreamIn, final InputStream mcmetaInputStreamIn, final IMetadataSerializer srMetadataSerializerIn) {
        this.resourcePackName = resourcePackNameIn;
        this.srResourceLocation = srResourceLocationIn;
        this.resourceInputStream = resourceInputStreamIn;
        this.mcmetaInputStream = mcmetaInputStreamIn;
        this.srMetadataSerializer = srMetadataSerializerIn;
    }

    public ResourceLocation getResourceLocation() {
        return this.srResourceLocation;
    }

    public InputStream getInputStream() {
        return this.resourceInputStream;
    }

    public boolean hasMetadata() {
        return this.mcmetaInputStream != null;
    }

    public <T extends IMetadataSection> T getMetadata(final String p_110526_1_) {
        if (!this.hasMetadata()) {
            return null;
        } else {
            if (this.mcmetaJson == null && !this.mcmetaJsonChecked) {
                this.mcmetaJsonChecked = true;
                BufferedReader bufferedreader = null;

                try {
                    bufferedreader = new BufferedReader(new InputStreamReader(this.mcmetaInputStream));
                    this.mcmetaJson = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
                } finally {
                    IOUtils.closeQuietly(bufferedreader);
                }
            }

            T t = (T) this.mapMetadataSections.get(p_110526_1_);

            if (t == null) {
                t = this.srMetadataSerializer.parseMetadataSection(p_110526_1_, this.mcmetaJson);
            }

            return t;
        }
    }

    public String getResourcePackName() {
        return this.resourcePackName;
    }

    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof SimpleResource)) {
            return false;
        } else {
            final SimpleResource simpleresource = (SimpleResource) p_equals_1_;

            if (this.srResourceLocation != null) {
                if (!this.srResourceLocation.equals(simpleresource.srResourceLocation)) {
                    return false;
                }
            } else if (simpleresource.srResourceLocation != null) {
                return false;
            }

            if (this.resourcePackName != null) {
                return this.resourcePackName.equals(simpleresource.resourcePackName);
            } else return simpleresource.resourcePackName == null;
        }
    }

    public int hashCode() {
        int i = this.resourcePackName != null ? this.resourcePackName.hashCode() : 0;
        i = 31 * i + (this.srResourceLocation != null ? this.srResourceLocation.hashCode() : 0);
        return i;
    }
}
