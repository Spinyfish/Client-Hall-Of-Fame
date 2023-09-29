package net.optifine;

import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RandomEntityProperties {
    public String name = null;
    public String basePath = null;
    public ResourceLocation[] resourceLocations = null;
    public RandomEntityRule[] rules = null;

    public RandomEntityProperties(final String path, final ResourceLocation[] variants) {
        final ConnectedParser connectedparser = new ConnectedParser("RandomEntities");
        this.name = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.resourceLocations = variants;
    }

    public RandomEntityProperties(final Properties props, final String path, final ResourceLocation baseResLoc) {
        final ConnectedParser connectedparser = new ConnectedParser("RandomEntities");
        this.name = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.rules = this.parseRules(props, path, baseResLoc, connectedparser);
    }

    public ResourceLocation getTextureLocation(final ResourceLocation loc, final IRandomEntity randomEntity) {
        if (this.rules != null) {
            for (int i = 0; i < this.rules.length; ++i) {
                final RandomEntityRule randomentityrule = this.rules[i];

                if (randomentityrule.matches(randomEntity)) {
                    return randomentityrule.getTextureLocation(loc, randomEntity.getId());
                }
            }
        }

        if (this.resourceLocations != null) {
            final int j = randomEntity.getId();
            final int k = j % this.resourceLocations.length;
            return this.resourceLocations[k];
        } else {
            return loc;
        }
    }

    private RandomEntityRule[] parseRules(final Properties props, final String pathProps, final ResourceLocation baseResLoc, final ConnectedParser cp) {
        final List list = new ArrayList();
        final int i = props.size();

        for (int j = 0; j < i; ++j) {
            final int k = j + 1;
            String s = props.getProperty("textures." + k);

            if (s == null) {
                s = props.getProperty("skins." + k);
            }

            if (s != null) {
                final RandomEntityRule randomentityrule = new RandomEntityRule(props, pathProps, baseResLoc, k, s, cp);

                if (randomentityrule.isValid(pathProps)) {
                    list.add(randomentityrule);
                }
            }
        }

        final RandomEntityRule[] arandomentityrule = (RandomEntityRule[]) list.toArray(new RandomEntityRule[list.size()]);
        return arandomentityrule;
    }

    public boolean isValid(final String path) {
        if (this.resourceLocations == null && this.rules == null) {
            Config.warn("No skins specified: " + path);
            return false;
        } else {
            if (this.rules != null) {
                for (int i = 0; i < this.rules.length; ++i) {
                    final RandomEntityRule randomentityrule = this.rules[i];

                    if (!randomentityrule.isValid(path)) {
                        return false;
                    }
                }
            }

            if (this.resourceLocations != null) {
                for (int j = 0; j < this.resourceLocations.length; ++j) {
                    final ResourceLocation resourcelocation = this.resourceLocations[j];

                    if (!Config.hasResource(resourcelocation)) {
                        Config.warn("Texture not found: " + resourcelocation.getResourcePath());
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public boolean isDefault() {
        return this.rules == null && this.resourceLocations == null;
    }
}
