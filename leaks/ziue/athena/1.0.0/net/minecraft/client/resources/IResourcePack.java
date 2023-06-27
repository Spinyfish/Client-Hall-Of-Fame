package net.minecraft.client.resources;

import net.minecraft.util.*;
import java.io.*;
import java.util.*;
import net.minecraft.client.resources.data.*;
import java.awt.image.*;

public interface IResourcePack
{
    InputStream getInputStream(final ResourceLocation p0) throws IOException;
    
    boolean resourceExists(final ResourceLocation p0);
    
    Set<String> getResourceDomains();
    
     <T extends IMetadataSection> T getPackMetadata(final IMetadataSerializer p0, final String p1) throws IOException;
    
    BufferedImage getPackImage() throws IOException;
    
    String getPackName();
}
