package net.minecraft.client.resources;

import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;

public interface IResource {
    ResourceLocation func_177241_a();

    InputStream getInputStream();

    boolean hasMetadata();

    IMetadataSection getMetadata(String var1);

    String func_177240_d();
}
