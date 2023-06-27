package net.minecraft.client.resources;

import net.minecraft.util.*;
import java.io.*;
import net.minecraft.client.resources.data.*;

public interface IResource
{
    ResourceLocation getResourceLocation();
    
    InputStream getInputStream();
    
    boolean hasMetadata();
    
     <T extends IMetadataSection> T getMetadata(final String p0);
    
    String getResourcePackName();
}
