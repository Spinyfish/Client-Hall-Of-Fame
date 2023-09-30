package net.optifine;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

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

    public static void updateIcons(final TextureMap textureMap) {
        spritesLoaded = false;
        modelsLoaded = false;
        loadProperties(textureMap);
    }

    public static void update() {
        if (spritesLoaded) {
            modelCubeGrass = BlockModelUtils.makeModelCube(spriteGrass, 0);

            if (grassMultilayer) {
                final IBakedModel ibakedmodel = BlockModelUtils.makeModelCube(spriteGrassSide, -1);
                modelCubeGrass = BlockModelUtils.joinModelsCube(ibakedmodel, modelCubeGrass);
            }

            modelCubeMycelium = BlockModelUtils.makeModelCube(spriteMycelium, -1);
            modelCubePodzol = BlockModelUtils.makeModelCube(spritePodzol, 0);
            modelCubeSnow = BlockModelUtils.makeModelCube(spriteSnow, -1);
            modelsLoaded = true;
        }
    }

    private static void loadProperties(final TextureMap textureMap) {
        betterGrass = true;
        betterMycelium = true;
        betterPodzol = true;
        betterGrassSnow = true;
        betterMyceliumSnow = true;
        betterPodzolSnow = true;
        spriteGrass = textureMap.registerSprite(new ResourceLocation("blocks/grass_top"));
        spriteGrassSide = textureMap.registerSprite(new ResourceLocation("blocks/grass_side"));
        spriteMycelium = textureMap.registerSprite(new ResourceLocation("blocks/mycelium_top"));
        spritePodzol = textureMap.registerSprite(new ResourceLocation("blocks/dirt_podzol_top"));
        spriteSnow = textureMap.registerSprite(new ResourceLocation("blocks/snow"));
        spritesLoaded = true;
        final String s = "optifine/bettergrass.properties";

        try {
            final ResourceLocation resourcelocation = new ResourceLocation(s);

            if (!Config.hasResource(resourcelocation)) {
                return;
            }

            final InputStream inputstream = Config.getResourceStream(resourcelocation);

            if (inputstream == null) {
                return;
            }

            final boolean flag = Config.isFromDefaultResourcePack(resourcelocation);

            if (flag) {
                Config.dbg("BetterGrass: Parsing default configuration " + s);
            } else {
                Config.dbg("BetterGrass: Parsing configuration " + s);
            }

            final Properties properties = new PropertiesOrdered();
            properties.load(inputstream);
            betterGrass = getBoolean(properties, "grass", true);
            betterMycelium = getBoolean(properties, "mycelium", true);
            betterPodzol = getBoolean(properties, "podzol", true);
            betterGrassSnow = getBoolean(properties, "grass.snow", true);
            betterMyceliumSnow = getBoolean(properties, "mycelium.snow", true);
            betterPodzolSnow = getBoolean(properties, "podzol.snow", true);
            grassMultilayer = getBoolean(properties, "grass.multilayer", false);
            spriteGrass = registerSprite(properties, "texture.grass", "blocks/grass_top", textureMap);
            spriteGrassSide = registerSprite(properties, "texture.grass_side", "blocks/grass_side", textureMap);
            spriteMycelium = registerSprite(properties, "texture.mycelium", "blocks/mycelium_top", textureMap);
            spritePodzol = registerSprite(properties, "texture.podzol", "blocks/dirt_podzol_top", textureMap);
            spriteSnow = registerSprite(properties, "texture.snow", "blocks/snow", textureMap);
        } catch (final IOException ioexception) {
            Config.warn("Error reading: " + s + ", " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
        }
    }

    private static TextureAtlasSprite registerSprite(final Properties props, final String key, final String textureDefault, final TextureMap textureMap) {
        String s = props.getProperty(key);

        if (s == null) {
            s = textureDefault;
        }

        final ResourceLocation resourcelocation = new ResourceLocation("textures/" + s + ".png");

        if (!Config.hasResource(resourcelocation)) {
            Config.warn("BetterGrass texture not found: " + resourcelocation);
            s = textureDefault;
        }

        final ResourceLocation resourcelocation1 = new ResourceLocation(s);
        final TextureAtlasSprite textureatlassprite = textureMap.registerSprite(resourcelocation1);
        return textureatlassprite;
    }

    public static List getFaceQuads(final IBlockAccess blockAccess, final IBlockState blockState, final BlockPos blockPos, final EnumFacing facing, final List quads) {
        if (facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
            if (!modelsLoaded) {
                return quads;
            } else {
                final Block block = blockState.getBlock();
                return block instanceof BlockMycelium ? getFaceQuadsMycelium(blockAccess, blockState, blockPos, facing, quads) : (block instanceof BlockDirt ? getFaceQuadsDirt(blockAccess, blockState, blockPos, facing, quads) : (block instanceof BlockGrass ? getFaceQuadsGrass(blockAccess, blockState, blockPos, facing, quads) : quads));
            }
        } else {
            return quads;
        }
    }

    private static List getFaceQuadsMycelium(final IBlockAccess blockAccess, final IBlockState blockState, final BlockPos blockPos, final EnumFacing facing, final List quads) {
        final Block block = blockAccess.getBlockState(blockPos.up()).getBlock();
        final boolean flag = block == Blocks.snow || block == Blocks.snow_layer;

        if (Config.isBetterGrassFancy()) {
            if (flag) {
                if (betterMyceliumSnow && getBlockAt(blockPos, facing, blockAccess) == Blocks.snow_layer) {
                    return modelCubeSnow.getFaceQuads(facing);
                }
            } else if (betterMycelium && getBlockAt(blockPos.down(), facing, blockAccess) == Blocks.mycelium) {
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

    private static List getFaceQuadsDirt(final IBlockAccess blockAccess, final IBlockState blockState, final BlockPos blockPos, final EnumFacing facing, final List quads) {
        final Block block = getBlockAt(blockPos, EnumFacing.UP, blockAccess);

        if (blockState.getValue(BlockDirt.VARIANT) != BlockDirt.DirtType.PODZOL) {
            return quads;
        } else {
            final boolean flag = block == Blocks.snow || block == Blocks.snow_layer;

            if (Config.isBetterGrassFancy()) {
                if (flag) {
                    if (betterPodzolSnow && getBlockAt(blockPos, facing, blockAccess) == Blocks.snow_layer) {
                        return modelCubeSnow.getFaceQuads(facing);
                    }
                } else if (betterPodzol) {
                    final BlockPos blockpos = blockPos.down().offset(facing);
                    final IBlockState iblockstate = blockAccess.getBlockState(blockpos);

                    if (iblockstate.getBlock() == Blocks.dirt && iblockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL) {
                        return modelCubePodzol.getFaceQuads(facing);
                    }
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
    }

    private static List getFaceQuadsGrass(final IBlockAccess blockAccess, final IBlockState blockState, final BlockPos blockPos, final EnumFacing facing, final List quads) {
        final Block block = blockAccess.getBlockState(blockPos.up()).getBlock();
        final boolean flag = block == Blocks.snow || block == Blocks.snow_layer;

        if (Config.isBetterGrassFancy()) {
            if (flag) {
                if (betterGrassSnow && getBlockAt(blockPos, facing, blockAccess) == Blocks.snow_layer) {
                    return modelCubeSnow.getFaceQuads(facing);
                }
            } else if (betterGrass && getBlockAt(blockPos.down(), facing, blockAccess) == Blocks.grass) {
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

    private static Block getBlockAt(final BlockPos blockPos, final EnumFacing facing, final IBlockAccess blockAccess) {
        final BlockPos blockpos = blockPos.offset(facing);
        final Block block = blockAccess.getBlockState(blockpos).getBlock();
        return block;
    }

    private static boolean getBoolean(final Properties props, final String key, final boolean def) {
        final String s = props.getProperty(key);
        return s == null ? def : Boolean.parseBoolean(s);
    }
}
