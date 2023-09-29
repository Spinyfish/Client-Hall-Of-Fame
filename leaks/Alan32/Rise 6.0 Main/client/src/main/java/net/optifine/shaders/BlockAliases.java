package net.optifine.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
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

public class BlockAliases {
    private static BlockAlias[][] blockAliases = null;
    private static PropertiesOrdered blockLayerPropertes = null;
    private static boolean updateOnResourcesReloaded;

    public static int getBlockAliasId(final int blockId, final int metadata) {
        if (blockAliases == null) {
            return blockId;
        } else if (blockId >= 0 && blockId < blockAliases.length) {
            final BlockAlias[] ablockalias = blockAliases[blockId];

            if (ablockalias == null) {
                return blockId;
            } else {
                for (int i = 0; i < ablockalias.length; ++i) {
                    final BlockAlias blockalias = ablockalias[i];

                    if (blockalias.matches(blockId, metadata)) {
                        return blockalias.getBlockAliasId();
                    }
                }

                return blockId;
            }
        } else {
            return blockId;
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
            if (Reflector.Loader_getActiveModList.exists() && Minecraft.getMinecraft().getResourcePackRepository() == null) {
                Config.dbg("[Shaders] Delayed loading of block mappings after resources are loaded");
                updateOnResourcesReloaded = true;
            } else {
                final List<List<BlockAlias>> list = new ArrayList();
                final String s = "/shaders/block.properties";
                final InputStream inputstream = shaderPack.getResourceAsStream(s);

                if (inputstream != null) {
                    loadBlockAliases(inputstream, s, list);
                }

                loadModBlockAliases(list);

                if (list.size() > 0) {
                    blockAliases = toArrays(list);
                }
            }
        }
    }

    private static void loadModBlockAliases(final List<List<BlockAlias>> listBlockAliases) {
        final String[] astring = ReflectorForge.getForgeModIds();

        for (int i = 0; i < astring.length; ++i) {
            final String s = astring[i];

            try {
                final ResourceLocation resourcelocation = new ResourceLocation(s, "shaders/block.properties");
                final InputStream inputstream = Config.getResourceStream(resourcelocation);
                loadBlockAliases(inputstream, resourcelocation.toString(), listBlockAliases);
            } catch (final IOException var6) {
            }
        }
    }

    private static void loadBlockAliases(InputStream in, final String path, final List<List<BlockAlias>> listBlockAliases) {
        if (in != null) {
            try {
                in = MacroProcessor.process(in, path);
                final Properties properties = new PropertiesOrdered();
                properties.load(in);
                in.close();
                Config.dbg("[Shaders] Parsing block mappings: " + path);
                final ConnectedParser connectedparser = new ConnectedParser("Shaders");

                for (final Object e : properties.keySet()) {
                    final String s = (String) e;
                    final String s1 = properties.getProperty(s);

                    if (s.startsWith("layer.")) {
                        if (blockLayerPropertes == null) {
                            blockLayerPropertes = new PropertiesOrdered();
                        }

                        blockLayerPropertes.put(s, s1);
                    } else {
                        final String s2 = "block.";

                        if (!s.startsWith(s2)) {
                            Config.warn("[Shaders] Invalid block ID: " + s);
                        } else {
                            final String s3 = StrUtils.removePrefix(s, s2);
                            final int i = Config.parseInt(s3, -1);

                            if (i < 0) {
                                Config.warn("[Shaders] Invalid block ID: " + s);
                            } else {
                                final MatchBlock[] amatchblock = connectedparser.parseMatchBlocks(s1);

                                if (amatchblock != null && amatchblock.length >= 1) {
                                    final BlockAlias blockalias = new BlockAlias(i, amatchblock);
                                    addToList(listBlockAliases, blockalias);
                                } else {
                                    Config.warn("[Shaders] Invalid block ID mapping: " + s + "=" + s1);
                                }
                            }
                        }
                    }
                }
            } catch (final IOException var14) {
                Config.warn("[Shaders] Error reading: " + path);
            }
        }
    }

    private static void addToList(final List<List<BlockAlias>> blocksAliases, final BlockAlias ba) {
        final int[] aint = ba.getMatchBlockIds();

        for (int i = 0; i < aint.length; ++i) {
            final int j = aint[i];

            while (j >= blocksAliases.size()) {
                blocksAliases.add(null);
            }

            List<BlockAlias> list = blocksAliases.get(j);

            if (list == null) {
                list = new ArrayList();
                blocksAliases.set(j, list);
            }

            final BlockAlias blockalias = new BlockAlias(ba.getBlockAliasId(), ba.getMatchBlocks(j));
            list.add(blockalias);
        }
    }

    private static BlockAlias[][] toArrays(final List<List<BlockAlias>> listBlocksAliases) {
        final BlockAlias[][] ablockalias = new BlockAlias[listBlocksAliases.size()][];

        for (int i = 0; i < ablockalias.length; ++i) {
            final List<BlockAlias> list = listBlocksAliases.get(i);

            if (list != null) {
                ablockalias[i] = list.toArray(new BlockAlias[list.size()]);
            }
        }

        return ablockalias;
    }

    public static PropertiesOrdered getBlockLayerPropertes() {
        return blockLayerPropertes;
    }

    public static void reset() {
        blockAliases = null;
        blockLayerPropertes = null;
    }
}
