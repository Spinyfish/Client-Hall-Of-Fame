/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.shaders.BlockAliases;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;

public class CustomBlockLayers {
    private static EnumWorldBlockLayer[] renderLayers = null;
    public static boolean active = false;

    public static EnumWorldBlockLayer getRenderLayer(IBlockState blockState) {
        if (renderLayers == null) {
            return null;
        }
        if (blockState.getBlock().isOpaqueCube()) {
            return null;
        }
        if (!(blockState instanceof BlockStateBase)) {
            return null;
        }
        BlockStateBase blockstatebase = (BlockStateBase)blockState;
        int i = blockstatebase.getBlockId();
        return i > 0 && i < renderLayers.length ? renderLayers[i] : null;
    }

    public static void update() {
        PropertiesOrdered propertiesordered;
        renderLayers = null;
        active = false;
        ArrayList<EnumWorldBlockLayer> list = new ArrayList<EnumWorldBlockLayer>();
        String s = "optifine/block.properties";
        Properties properties = ResUtils.readProperties(s, "CustomBlockLayers");
        if (properties != null) {
            CustomBlockLayers.readLayers(s, properties, list);
        }
        if (Config.isShaders() && (propertiesordered = BlockAliases.getBlockLayerPropertes()) != null) {
            String s1 = "shaders/block.properties";
            CustomBlockLayers.readLayers(s1, propertiesordered, list);
        }
        if (!list.isEmpty()) {
            renderLayers = list.toArray(new EnumWorldBlockLayer[list.size()]);
            active = true;
        }
    }

    private static void readLayers(String pathProps, Properties props, List<EnumWorldBlockLayer> list) {
        Config.dbg("CustomBlockLayers: " + pathProps);
        CustomBlockLayers.readLayer("solid", EnumWorldBlockLayer.SOLID, props, list);
        CustomBlockLayers.readLayer("cutout", EnumWorldBlockLayer.CUTOUT, props, list);
        CustomBlockLayers.readLayer("cutout_mipped", EnumWorldBlockLayer.CUTOUT_MIPPED, props, list);
        CustomBlockLayers.readLayer("translucent", EnumWorldBlockLayer.TRANSLUCENT, props, list);
    }

    private static void readLayer(String name, EnumWorldBlockLayer layer, Properties props, List<EnumWorldBlockLayer> listLayers) {
        ConnectedParser connectedparser;
        MatchBlock[] amatchblock;
        String s = "layer." + name;
        String s1 = props.getProperty(s);
        if (s1 != null && (amatchblock = (connectedparser = new ConnectedParser("CustomBlockLayers")).parseMatchBlocks(s1)) != null) {
            for (int i = 0; i < amatchblock.length; ++i) {
                MatchBlock matchblock = amatchblock[i];
                int j = matchblock.getBlockId();
                if (j <= 0) continue;
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

    public static boolean isActive() {
        return active;
    }
}

