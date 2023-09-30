/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.config;

import net.minecraft.block.state.BlockStateBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.MatchBlock;

public class Matches {
    public static boolean block(BlockStateBase blockStateBase, MatchBlock[] matchBlocks) {
        if (matchBlocks == null) {
            return true;
        }
        for (int i = 0; i < matchBlocks.length; ++i) {
            MatchBlock matchblock = matchBlocks[i];
            if (!matchblock.matches(blockStateBase)) continue;
            return true;
        }
        return false;
    }

    public static boolean block(int blockId, int metadata, MatchBlock[] matchBlocks) {
        if (matchBlocks == null) {
            return true;
        }
        for (int i = 0; i < matchBlocks.length; ++i) {
            MatchBlock matchblock = matchBlocks[i];
            if (!matchblock.matches(blockId, metadata)) continue;
            return true;
        }
        return false;
    }

    public static boolean blockId(int blockId, MatchBlock[] matchBlocks) {
        if (matchBlocks == null) {
            return true;
        }
        for (int i = 0; i < matchBlocks.length; ++i) {
            MatchBlock matchblock = matchBlocks[i];
            if (matchblock.getBlockId() != blockId) continue;
            return true;
        }
        return false;
    }

    public static boolean metadata(int metadata, int[] metadatas) {
        if (metadatas == null) {
            return true;
        }
        for (int i = 0; i < metadatas.length; ++i) {
            if (metadatas[i] != metadata) continue;
            return true;
        }
        return false;
    }

    public static boolean sprite(TextureAtlasSprite sprite, TextureAtlasSprite[] sprites) {
        if (sprites == null) {
            return true;
        }
        for (int i = 0; i < sprites.length; ++i) {
            if (sprites[i] != sprite) continue;
            return true;
        }
        return false;
    }

    public static boolean biome(BiomeGenBase biome, BiomeGenBase[] biomes) {
        if (biomes == null) {
            return true;
        }
        for (int i = 0; i < biomes.length; ++i) {
            if (biomes[i] != biome) continue;
            return true;
        }
        return false;
    }
}

