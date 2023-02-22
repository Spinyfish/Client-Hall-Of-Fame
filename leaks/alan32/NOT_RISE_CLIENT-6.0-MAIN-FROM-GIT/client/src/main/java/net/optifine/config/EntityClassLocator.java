package net.optifine.config;

import net.minecraft.util.ResourceLocation;
import net.optifine.util.EntityUtils;

public class EntityClassLocator implements IObjectLocator {
    public Object getObject(final ResourceLocation loc) {
        final Class oclass = EntityUtils.getEntityClassByName(loc.getResourcePath());
        return oclass;
    }
}
