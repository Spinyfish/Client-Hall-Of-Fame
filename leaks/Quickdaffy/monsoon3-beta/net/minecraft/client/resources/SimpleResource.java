/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class SimpleResource
implements IResource {
    private final Map<String, IMetadataSection> mapMetadataSections = Maps.newHashMap();
    private final String resourcePackName;
    private final ResourceLocation srResourceLocation;
    private final InputStream resourceInputStream;
    private final InputStream mcmetaInputStream;
    private final IMetadataSerializer srMetadataSerializer;
    private boolean mcmetaJsonChecked;
    private JsonObject mcmetaJson;

    public SimpleResource(String resourcePackNameIn, ResourceLocation srResourceLocationIn, InputStream resourceInputStreamIn, InputStream mcmetaInputStreamIn, IMetadataSerializer srMetadataSerializerIn) {
        this.resourcePackName = resourcePackNameIn;
        this.srResourceLocation = srResourceLocationIn;
        this.resourceInputStream = resourceInputStreamIn;
        this.mcmetaInputStream = mcmetaInputStreamIn;
        this.srMetadataSerializer = srMetadataSerializerIn;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return this.srResourceLocation;
    }

    @Override
    public InputStream getInputStream() {
        return this.resourceInputStream;
    }

    @Override
    public boolean hasMetadata() {
        return this.mcmetaInputStream != null;
    }

    @Override
    public <T extends IMetadataSection> T getMetadata(String p_110526_1_) {
        IMetadataSection t;
        if (!this.hasMetadata()) {
            return (T)((IMetadataSection)null);
        }
        if (this.mcmetaJson == null && !this.mcmetaJsonChecked) {
            this.mcmetaJsonChecked = true;
            BufferedReader bufferedreader = null;
            try {
                bufferedreader = new BufferedReader(new InputStreamReader(this.mcmetaInputStream));
                this.mcmetaJson = new JsonParser().parse((Reader)bufferedreader).getAsJsonObject();
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(bufferedreader);
                throw throwable;
            }
            IOUtils.closeQuietly((Reader)bufferedreader);
        }
        if ((t = this.mapMetadataSections.get(p_110526_1_)) == null) {
            t = this.srMetadataSerializer.parseMetadataSection(p_110526_1_, this.mcmetaJson);
        }
        return (T)t;
    }

    @Override
    public String getResourcePackName() {
        return this.resourcePackName;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof SimpleResource)) {
            return false;
        }
        SimpleResource simpleresource = (SimpleResource)p_equals_1_;
        if (this.srResourceLocation != null ? !this.srResourceLocation.equals(simpleresource.srResourceLocation) : simpleresource.srResourceLocation != null) {
            return false;
        }
        return !(this.resourcePackName != null ? !this.resourcePackName.equals(simpleresource.resourcePackName) : simpleresource.resourcePackName != null);
    }

    public int hashCode() {
        int i = this.resourcePackName != null ? this.resourcePackName.hashCode() : 0;
        i = 31 * i + (this.srResourceLocation != null ? this.srResourceLocation.hashCode() : 0);
        return i;
    }
}

