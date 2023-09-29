package net.optifine;

import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.shaders.BlockAliases;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CustomBlockLayers {
    private static EnumWorldBlockLayer[] renderLayers = null;
    public static boolean active = false;

    public static EnumWorldBlockLayer getRenderLayer(final IBlockState blockState) {
        if (renderLayers == null) {
            return null;
        } else if (blockState.getBlock().isOpaqueCube()) {
            return null;
        } else if (!(blockState instanceof BlockStateBase)) {
            return null;
        } else {
            final BlockStateBase blockstatebase = (BlockStateBase) blockState;
            final int i = blockstatebase.getBlockId();
            return i > 0 && i < renderLayers.length ? renderLayers[i] : null;
        }
    }

    public static void update() {
        renderLayers = null;
        active = false;
        final List<EnumWorldBlockLayer> list = new ArrayList();
        final String s = "optifine/block.properties";
        final Properties properties = ResUtils.readProperties(s, "CustomBlockLayers");

        if (properties != null) {
            readLayers(s, properties, list);
        }

        if (Config.isShaders()) {
            final PropertiesOrdered propertiesordered = BlockAliases.getBlockLayerPropertes();

            if (propertiesordered != null) {
                final String s1 = "shaders/block.properties";
                readLayers(s1, propertiesordered, list);
            }
        }

        if (!list.isEmpty()) {
            renderLayers = list.toArray(new EnumWorldBlockLayer[list.size()]);
            active = true;
        }
    }

    private static void readLayers(final String pathProps, final Properties props, final List<EnumWorldBlockLayer> list) {
        Config.dbg("CustomBlockLayers: " + pathProps);
        readLayer("solid", EnumWorldBlockLayer.SOLID, props, list);
        readLayer("cutout", EnumWorldBlockLayer.CUTOUT, props, list);
        readLayer("cutout_mipped", EnumWorldBlockLayer.CUTOUT_MIPPED, props, list);
        readLayer("translucent", EnumWorldBlockLayer.TRANSLUCENT, props, list);
    }

    private static void readLayer(final String name, final EnumWorldBlockLayer layer, final Properties props, final List<EnumWorldBlockLayer> listLayers) {
        final String s = "layer." + name;
        final String s1 = props.getProperty(s);

        if (s1 != null) {
            final ConnectedParser connectedparser = new ConnectedParser("CustomBlockLayers");
            final MatchBlock[] amatchblock = connectedparser.parseMatchBlocks(s1);

            if (amatchblock != null) {
                for (int i = 0; i < amatchblock.length; ++i) {
                    final MatchBlock matchblock = amatchblock[i];
                    final int j = matchblock.getBlockId();

                    if (j > 0) {
                        while (listLayers.size() < j + 1) {
                            listLayers.add(null);
                        }

                        if (listLayers.get(j) != null) {
                            Config.warn("CustomBlockLayers: Block layer is already set, block: " + j + ", layer: " + name);
                        }

                        listLayers.set(j, layer);
                    }
                }
            }
        }
    }

    public static boolean isActive() {
        return active;
    }
}
