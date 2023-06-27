package net.minecraft.client.resources;

import java.util.*;

public interface IReloadableResourceManager extends IResourceManager
{
    void reloadResources(final List<IResourcePack> p0);
    
    void registerReloadListener(final IResourceManagerReloadListener p0);
}
