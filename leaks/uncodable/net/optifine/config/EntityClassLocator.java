package net.optifine.config;

import net.minecraft.util.ResourceLocation;
import net.optifine.util.EntityUtils;

public class EntityClassLocator implements IObjectLocator {
   @Override
   public Object getObject(ResourceLocation loc) {
      return EntityUtils.getEntityClassByName(loc.getResourcePath());
   }
}
