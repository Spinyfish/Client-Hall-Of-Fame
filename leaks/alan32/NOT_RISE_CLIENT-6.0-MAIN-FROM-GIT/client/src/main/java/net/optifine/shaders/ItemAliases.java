package net.optifine.shaders;

import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ItemAliases {
    private static int[] itemAliases = null;
    private static boolean updateOnResourcesReloaded;
    private static final int NO_ALIAS = Integer.MIN_VALUE;

    public static int getItemAliasId(final int itemId) {
        if (itemAliases == null) {
            return itemId;
        } else if (itemId >= 0 && itemId < itemAliases.length) {
            final int i = itemAliases[itemId];
            return i == Integer.MIN_VALUE ? itemId : i;
        } else {
            return itemId;
        }
    }

    public static void resourcesReloaded() {
        if (updateOnResourcesReloaded) {
            updateOnResourcesReloaded = false;
            update(Shaders.getShaderPack());
        }
    }

    public static void update(final IShaderPack shaderPack) {
        reset();

        if (shaderPack != null) {
            if (Reflector.Loader_getActiveModList.exists() && Config.getResourceManager() == null) {
                Config.dbg("[Shaders] Delayed loading of item mappings after resources are loaded");
                updateOnResourcesReloaded = true;
            } else {
                final List<Integer> list = new ArrayList();
                final String s = "/shaders/item.properties";
                final InputStream inputstream = shaderPack.getResourceAsStream(s);

                if (inputstream != null) {
                    loadItemAliases(inputstream, s, list);
                }

                loadModItemAliases(list);

                if (list.size() > 0) {
                    itemAliases = toArray(list);
                }
            }
        }
    }

    private static void loadModItemAliases(final List<Integer> listItemAliases) {
        final String[] astring = ReflectorForge.getForgeModIds();

        for (int i = 0; i < astring.length; ++i) {
            final String s = astring[i];

            try {
                final ResourceLocation resourcelocation = new ResourceLocation(s, "shaders/item.properties");
                final InputStream inputstream = Config.getResourceStream(resourcelocation);
                loadItemAliases(inputstream, resourcelocation.toString(), listItemAliases);
            } catch (final IOException var6) {
            }
        }
    }

    private static void loadItemAliases(InputStream in, final String path, final List<Integer> listItemAliases) {
        if (in != null) {
            try {
                in = MacroProcessor.process(in, path);
                final Properties properties = new PropertiesOrdered();
                properties.load(in);
                in.close();
                Config.dbg("[Shaders] Parsing item mappings: " + path);
                final ConnectedParser connectedparser = new ConnectedParser("Shaders");

                for (final Object e : properties.keySet()) {
                    final String s = (String) e;
                    final String s1 = properties.getProperty(s);
                    final String s2 = "item.";

                    if (!s.startsWith(s2)) {
                        Config.warn("[Shaders] Invalid item ID: " + s);
                    } else {
                        final String s3 = StrUtils.removePrefix(s, s2);
                        final int i = Config.parseInt(s3, -1);

                        if (i < 0) {
                            Config.warn("[Shaders] Invalid item alias ID: " + i);
                        } else {
                            final int[] aint = connectedparser.parseItems(s1);

                            if (aint != null && aint.length >= 1) {
                                for (int j = 0; j < aint.length; ++j) {
                                    final int k = aint[j];
                                    addToList(listItemAliases, k, i);
                                }
                            } else {
                                Config.warn("[Shaders] Invalid item ID mapping: " + s + "=" + s1);
                            }
                        }
                    }
                }
            } catch (final IOException var15) {
                Config.warn("[Shaders] Error reading: " + path);
            }
        }
    }

    private static void addToList(final List<Integer> list, final int index, final int val) {
        while (list.size() <= index) {
            list.add(Integer.valueOf(Integer.MIN_VALUE));
        }

        list.set(index, Integer.valueOf(val));
    }

    private static int[] toArray(final List<Integer> list) {
        final int[] aint = new int[list.size()];

        for (int i = 0; i < aint.length; ++i) {
            aint[i] = list.get(i).intValue();
        }

        return aint;
    }

    public static void reset() {
        itemAliases = null;
    }
}
