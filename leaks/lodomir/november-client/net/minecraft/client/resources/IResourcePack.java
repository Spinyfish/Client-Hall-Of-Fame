/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public interface IResourcePack {
    public InputStream getInputStream(ResourceLocation var1) throws IOException;

    public boolean resourceExists(ResourceLocation var1);

    public Set<String> getResourceDomains();

    public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer var1, String var2) throws IOException;

    public BufferedImage getPackImage() throws IOException;

    public String getPackName();
}

