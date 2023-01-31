package net.minecraft.client.resources;

import java.util.List;

public interface IReloadableResourceManager extends IResourceManager {
   void reloadResources(List<IResourcePack> var1);

   void registerReloadListener(IResourceManagerReloadListener var1);
}
