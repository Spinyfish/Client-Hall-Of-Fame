/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.optifine.model.BlockModelUtils;
import net.optifine.util.PropertiesOrdered;

public class BetterGrass {
    private static boolean betterGrass = true;
    private static boolean betterMycelium = true;
    private static boolean betterPodzol = true;
    private static boolean betterGrassSnow = true;
    private static boolean betterMyceliumSnow = true;
    private static boolean betterPodzolSnow = true;
    private static boolean grassMultilayer = false;
    private static TextureAtlasSprite spriteGrass = null;
    private static TextureAtlasSprite spriteGrassSide = null;
    private static TextureAtlasSprite spriteMycelium = null;
    private static TextureAtlasSprite spritePodzol = null;
    private static TextureAtlasSprite spriteSnow = null;
    private static boolean spritesLoaded = false;
    private static IBakedModel modelCubeGrass = null;
    private static IBakedModel modelCubeMycelium = null;
    private static IBakedModel modelCubePodzol = null;
    private static IBakedModel modelCubeSnow = null;
    private static boolean modelsLoaded = false;
    private static final String TEXTURE_GRASS_DEFAULT = "blocks/grass_top";
    private static final String TEXTURE_GRASS_SIDE_DEFAULT = "blocks/grass_side";
    private static final String TEXTURE_MYCELIUM_DEFAULT = "blocks/mycelium_top";
    private static final String TEXTURE_PODZOL_DEFAULT = "blocks/dirt_podzol_top";
    private static final String TEXTURE_SNOW_DEFAULT = "blocks/snow";

    public static void updateIcons(TextureMap textureMap) {
        spritesLoaded = false;
        modelsLoaded = false;
        BetterGrass.loadProperties(textureMap);
    }

    public static void update() {
        if (spritesLoaded) {
            modelCubeGrass = BlockModelUtils.makeModelCube(spriteGrass, 0);
            if (grassMultilayer) {
                IBakedModel ibakedmodel = BlockModelUtils.makeModelCube(spriteGrassSide, -1);
                modelCubeGrass = BlockModelUtils.joinModelsCube(ibakedmodel, modelCubeGrass);
            }
            modelCubeMycelium = BlockModelUtils.makeModelCube(spriteMycelium, -1);
            modelCubePodzol = BlockModelUtils.makeModelCube(spritePodzol, 0);
            modelCubeSnow = BlockModelUtils.makeModelCube(spriteSnow, -1);
            modelsLoaded = true;
        }
    }

    private static void loadProperties(TextureMap textureMap) {
        betterGrass = true;
        betterMycelium = true;
        betterPodzol = true;
        betterGrassSnow = true;
        betterMyceliumSnow = true;
        betterPodzolSnow = true;
        spriteGrass = textureMap.registerSprite(new ResourceLocation(TEXTURE_GRASS_DEFAULT));
        spriteGrassSide = textureMap.registerSprite(new ResourceLocation(TEXTURE_GRASS_SIDE_DEFAULT));
        spriteMycelium = textureMap.registerSprite(new ResourceLocation(TEXTURE_MYCELIUM_DEFAULT));
        spritePodzol = textureMap.registerSprite(new ResourceLocation(TEXTURE_PODZOL_DEFAULT));
        spriteSnow = textureMap.registerSprite(new ResourceLocation(TEXTURE_SNOW_DEFAULT));
        spritesLoaded = true;
        String s = "optifine/bettergrass.properties";
        try {
            ResourceLocation resourcelocation = new ResourceLocation(s);
            if (!Config.hasResource(resourcelocation)) {
                return;
            }
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return;
            }
            boolean flag = Config.isFromDefaultResourcePack(resourcelocation);
            if (flag) {
                Config.dbg("BetterGrass: Parsing default configuration " + s);
            } else {
                Config.dbg("BetterGrass: Parsing configuration " + s);
            }
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(inputstream);
            betterGrass = BetterGrass.getBoolean(properties, "grass", true);
            betterMycelium = BetterGrass.getBoolean(properties, "mycelium", true);
            betterPodzol = BetterGrass.getBoolean(properties, "podzol", true);
            betterGrassSnow = BetterGrass.getBoolean(properties, "grass.snow", true);
            betterMyceliumSnow = BetterGrass.getBoolean(properties, "mycelium.snow", true);
            betterPodzolSnow = BetterGrass.getBoolean(properties, "podzol.snow", true);
            grassMultilayer = BetterGrass.getBoolean(properties, "grass.multilayer", false);
            spriteGrass = BetterGrass.registerSprite(properties, "texture.grass", TEXTURE_GRASS_DEFAULT, textureMap);
            spriteGrassSide = BetterGrass.registerSprite(properties, "texture.grass_side", TEXTURE_GRASS_SIDE_DEFAULT, textureMap);
            spriteMycelium = BetterGrass.registerSprite(properties, "texture.mycelium", TEXTURE_MYCELIUM_DEFAULT, textureMap);
            spritePodzol = BetterGrass.registerSprite(properties, "texture.podzol", TEXTURE_PODZOL_DEFAULT, textureMap);
            spriteSnow = BetterGrass.registerSprite(properties, "texture.snow", TEXTURE_SNOW_DEFAULT, textureMap);
        }
        catch (IOException ioexception) {
            Config.warn("Error reading: " + s + ", " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
        }
    }

    private static TextureAtlasSprite registerSprite(Properties props, String key, String textureDefault, TextureMap textureMap) {
        ResourceLocation resourcelocation;
        String s = props.getProperty(key);
        if (s == null) {
            s = textureDefault;
        }
        if (!Config.hasResource(resourcelocation = new ResourceLocation("textures/" + s + ".png"))) {
            Config.warn("BetterGrass texture not found: " + resourcelocation);
            s = textureDefault;
        }
        ResourceLocation resourcelocation1 = new ResourceLocation(s);
        TextureAtlasSprite textureatlassprite = textureMap.registerSprite(resourcelocation1);
        return textureatlassprite;
    }

    public static List getFaceQuads(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, List quads) {
        if (facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
            if (!modelsLoaded) {
                return quads;
            }
            Block block = blockState.getBlock();
            return block instanceof BlockMycelium ? BetterGrass.getFaceQuadsMycelium(blockAccess, blockState, blockPos, facing, quads) : (block instanceof BlockDirt ? BetterGrass.getFaceQuadsDirt(blockAccess, blockState, blockPos, facing, quads) : (block instanceof BlockGrass ? BetterGrass.getFaceQuadsGrass(blockAccess, blockState, blockPos, facing, quads) : quads));
        }
        return quads;
    }

    private static List getFaceQuadsMycelium(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, List quads) {
        boolean flag;
        Block block = blockAccess.getBlockState(blockPos.up()).getBlock();
        boolean bl = flag = block == Blocks.snow || block == Blocks.snow_layer;
        if (Config.isBetterGrassFancy()) {
            if (flag) {
                if (betterMyceliumSnow && BetterGrass.getBlockAt(blockPos, facing, blockAccess) == Blocks.snow_layer) {
                    return modelCubeSnow.getFaceQuads(facing);
                }
            } else if (betterMycelium && BetterGrass.getBlockAt(blockPos.down(), facing, blockAccess) == Blocks.mycelium) {
                return modelCubeMycelium.getFaceQuads(facing);
            }
        } else if (flag) {
            if (betterMyceliumSnow) {
                return modelCubeSnow.getFaceQuads(facing);
            }
        } else if (betterMycelium) {
            return modelCubeMycelium.getFaceQuads(facing);
        }
        return quads;
    }

    private static List getFaceQuadsDirt(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, List quads) {
        boolean flag;
        Block block = BetterGrass.getBlockAt(blockPos, EnumFacing.UP, blockAccess);
        if (blockState.getValue(BlockDirt.VARIANT) != BlockDirt.DirtType.PODZOL) {
            return quads;
        }
        boolean bl = flag = block == Blocks.snow || block == Blocks.snow_layer;
        if (Config.isBetterGrassFancy()) {
            BlockPos blockpos;
            IBlockState iblockstate;
            if (flag) {
                if (betterPodzolSnow && BetterGrass.getBlockAt(blockPos, facing, blockAccess) == Blocks.snow_layer) {
                    return modelCubeSnow.getFaceQuads(facing);
                }
            } else if (betterPodzol && (iblockstate = blockAccess.getBlockState(blockpos = blockPos.down().offset(facing))).getBlock() == Blocks.dirt && iblockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL) {
                return modelCubePodzol.getFaceQuads(facing);
            }
        } else if (flag) {
            if (betterPodzolSnow) {
                return modelCubeSnow.getFaceQuads(facing);
            }
        } else if (betterPodzol) {
            return modelCubePodzol.getFaceQuads(facing);
        }
        return quads;
    }

    private static List getFaceQuadsGrass(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, List quads) {
        boolean flag;
        Block block = blockAccess.getBlockState(blockPos.up()).getBlock();
        boolean bl = flag = block == Blocks.snow || block == Blocks.snow_layer;
        if (Config.isBetterGrassFancy()) {
            if (flag) {
                if (betterGrassSnow && BetterGrass.getBlockAt(blockPos, facing, blockAccess) == Blocks.snow_layer) {
                    return modelCubeSnow.getFaceQuads(facing);
                }
            } else if (betterGrass && BetterGrass.getBlockAt(blockPos.down(), facing, blockAccess) == Blocks.grass) {
                return modelCubeGrass.getFaceQuads(facing);
            }
        } else if (flag) {
            if (betterGrassSnow) {
                return modelCubeSnow.getFaceQuads(facing);
            }
        } else if (betterGrass) {
            return modelCubeGrass.getFaceQuads(facing);
        }
        return quads;
    }

    private static Block getBlockAt(BlockPos blockPos, EnumFacing facing, IBlockAccess blockAccess) {
        BlockPos blockpos = blockPos.offset(facing);
        Block block = blockAccess.getBlockState(blockpos).getBlock();
        return block;
    }

    private static boolean getBoolean(Properties props, String key, boolean def) {
        String s = props.getProperty(key);
        return s == null ? def : Boolean.parseBoolean(s);
    }
}

