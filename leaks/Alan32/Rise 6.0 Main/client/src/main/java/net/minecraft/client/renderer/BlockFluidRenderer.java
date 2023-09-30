package net.minecraft.client.renderer;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.optifine.CustomColors;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;

public class BlockFluidRenderer {
    private final TextureAtlasSprite[] atlasSpritesLava = new TextureAtlasSprite[2];
    private final TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];

    public BlockFluidRenderer() {
        this.initAtlasSprites();
    }

    protected void initAtlasSprites() {
        final TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        this.atlasSpritesLava[0] = texturemap.getAtlasSprite("minecraft:blocks/lava_still");
        this.atlasSpritesLava[1] = texturemap.getAtlasSprite("minecraft:blocks/lava_flow");
        this.atlasSpritesWater[0] = texturemap.getAtlasSprite("minecraft:blocks/water_still");
        this.atlasSpritesWater[1] = texturemap.getAtlasSprite("minecraft:blocks/water_flow");
    }

    public boolean renderFluid(final IBlockAccess blockAccess, final IBlockState blockStateIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn) {
        boolean flag2;

        try {
            if (Config.isShaders()) {
                SVertexBuilder.pushEntity(blockStateIn, blockPosIn, blockAccess, worldRendererIn);
            }

            final BlockLiquid blockliquid = (BlockLiquid) blockStateIn.getBlock();
            blockliquid.setBlockBoundsBasedOnState(blockAccess, blockPosIn);
            final TextureAtlasSprite[] atextureatlassprite = blockliquid.getMaterial() == Material.lava ? this.atlasSpritesLava : this.atlasSpritesWater;
            final RenderEnv renderenv = worldRendererIn.getRenderEnv(blockStateIn, blockPosIn);
            final int i = CustomColors.getFluidColor(blockAccess, blockStateIn, blockPosIn, renderenv);
            final float f = (float) (i >> 16 & 255) / 255.0F;
            final float f1 = (float) (i >> 8 & 255) / 255.0F;
            final float f2 = (float) (i & 255) / 255.0F;
            final boolean flag = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.up(), EnumFacing.UP);
            final boolean flag1 = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.down(), EnumFacing.DOWN);
            final boolean[] aboolean = renderenv.getBorderFlags();
            aboolean[0] = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.north(), EnumFacing.NORTH);
            aboolean[1] = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.south(), EnumFacing.SOUTH);
            aboolean[2] = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.west(), EnumFacing.WEST);
            aboolean[3] = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.east(), EnumFacing.EAST);

            if (flag || flag1 || aboolean[0] || aboolean[1] || aboolean[2] || aboolean[3]) {
                flag2 = false;
                final float f3 = 0.5F;
                final float f4 = 1.0F;
                final float f5 = 0.8F;
                final float f6 = 0.6F;
                final Material material = blockliquid.getMaterial();
                float f7 = this.getFluidHeight(blockAccess, blockPosIn, material);
                float f8 = this.getFluidHeight(blockAccess, blockPosIn.south(), material);
                float f9 = this.getFluidHeight(blockAccess, blockPosIn.east().south(), material);
                float f10 = this.getFluidHeight(blockAccess, blockPosIn.east(), material);
                final double d0 = blockPosIn.getX();
                final double d1 = blockPosIn.getY();
                final double d2 = blockPosIn.getZ();
                final float f11 = 0.001F;

                if (flag) {
                    flag2 = true;
                    TextureAtlasSprite textureatlassprite = atextureatlassprite[0];
                    final float f12 = (float) BlockLiquid.getFlowDirection(blockAccess, blockPosIn, material);

                    if (f12 > -999.0F) {
                        textureatlassprite = atextureatlassprite[1];
                    }

                    worldRendererIn.setSprite(textureatlassprite);
                    f7 -= f11;
                    f8 -= f11;
                    f9 -= f11;
                    f10 -= f11;
                    final float f13;
                    final float f14;
                    final float f15;
                    final float f16;
                    final float f17;
                    final float f18;
                    final float f19;
                    final float f20;

                    if (f12 < -999.0F) {
                        f13 = textureatlassprite.getInterpolatedU(0.0D);
                        f17 = textureatlassprite.getInterpolatedV(0.0D);
                        f14 = f13;
                        f18 = textureatlassprite.getInterpolatedV(16.0D);
                        f15 = textureatlassprite.getInterpolatedU(16.0D);
                        f19 = f18;
                        f16 = f15;
                        f20 = f17;
                    } else {
                        final float f21 = MathHelper.sin(f12) * 0.25F;
                        final float f22 = MathHelper.cos(f12) * 0.25F;
                        final float f23 = 8.0F;
                        f13 = textureatlassprite.getInterpolatedU(8.0F + (-f22 - f21) * 16.0F);
                        f17 = textureatlassprite.getInterpolatedV(8.0F + (-f22 + f21) * 16.0F);
                        f14 = textureatlassprite.getInterpolatedU(8.0F + (-f22 + f21) * 16.0F);
                        f18 = textureatlassprite.getInterpolatedV(8.0F + (f22 + f21) * 16.0F);
                        f15 = textureatlassprite.getInterpolatedU(8.0F + (f22 + f21) * 16.0F);
                        f19 = textureatlassprite.getInterpolatedV(8.0F + (f22 - f21) * 16.0F);
                        f16 = textureatlassprite.getInterpolatedU(8.0F + (f22 - f21) * 16.0F);
                        f20 = textureatlassprite.getInterpolatedV(8.0F + (-f22 - f21) * 16.0F);
                    }

                    final int k2 = blockliquid.getMixedBrightnessForBlock(blockAccess, blockPosIn);
                    final int l2 = k2 >> 16 & 65535;
                    final int i3 = k2 & 65535;
                    final float f24 = f4 * f;
                    final float f25 = f4 * f1;
                    final float f26 = f4 * f2;
                    worldRendererIn.pos(d0 + 0.0D, d1 + (double) f7, d2 + 0.0D).func_181666_a(f24, f25, f26, 1.0F).tex(f13, f17).func_181671_a(l2, i3).endVertex();
                    worldRendererIn.pos(d0 + 0.0D, d1 + (double) f8, d2 + 1.0D).func_181666_a(f24, f25, f26, 1.0F).tex(f14, f18).func_181671_a(l2, i3).endVertex();
                    worldRendererIn.pos(d0 + 1.0D, d1 + (double) f9, d2 + 1.0D).func_181666_a(f24, f25, f26, 1.0F).tex(f15, f19).func_181671_a(l2, i3).endVertex();
                    worldRendererIn.pos(d0 + 1.0D, d1 + (double) f10, d2 + 0.0D).func_181666_a(f24, f25, f26, 1.0F).tex(f16, f20).func_181671_a(l2, i3).endVertex();

                    if (blockliquid.func_176364_g(blockAccess, blockPosIn.up())) {
                        worldRendererIn.pos(d0 + 0.0D, d1 + (double) f7, d2 + 0.0D).func_181666_a(f24, f25, f26, 1.0F).tex(f13, f17).func_181671_a(l2, i3).endVertex();
                        worldRendererIn.pos(d0 + 1.0D, d1 + (double) f10, d2 + 0.0D).func_181666_a(f24, f25, f26, 1.0F).tex(f16, f20).func_181671_a(l2, i3).endVertex();
                        worldRendererIn.pos(d0 + 1.0D, d1 + (double) f9, d2 + 1.0D).func_181666_a(f24, f25, f26, 1.0F).tex(f15, f19).func_181671_a(l2, i3).endVertex();
                        worldRendererIn.pos(d0 + 0.0D, d1 + (double) f8, d2 + 1.0D).func_181666_a(f24, f25, f26, 1.0F).tex(f14, f18).func_181671_a(l2, i3).endVertex();
                    }
                }

                if (flag1) {
                    worldRendererIn.setSprite(atextureatlassprite[0]);
                    final float f35 = atextureatlassprite[0].getMinU();
                    final float f36 = atextureatlassprite[0].getMaxU();
                    final float f37 = atextureatlassprite[0].getMinV();
                    final float f38 = atextureatlassprite[0].getMaxV();
                    final int l1 = blockliquid.getMixedBrightnessForBlock(blockAccess, blockPosIn.down());
                    final int i2 = l1 >> 16 & 65535;
                    final int j2 = l1 & 65535;
                    final float f41 = FaceBakery.getFaceBrightness(EnumFacing.DOWN);
                    worldRendererIn.pos(d0, d1, d2 + 1.0D).func_181666_a(f * f41, f1 * f41, f2 * f41, 1.0F).tex(f35, f38).func_181671_a(i2, j2).endVertex();
                    worldRendererIn.pos(d0, d1, d2).func_181666_a(f * f41, f1 * f41, f2 * f41, 1.0F).tex(f35, f37).func_181671_a(i2, j2).endVertex();
                    worldRendererIn.pos(d0 + 1.0D, d1, d2).func_181666_a(f * f41, f1 * f41, f2 * f41, 1.0F).tex(f36, f37).func_181671_a(i2, j2).endVertex();
                    worldRendererIn.pos(d0 + 1.0D, d1, d2 + 1.0D).func_181666_a(f * f41, f1 * f41, f2 * f41, 1.0F).tex(f36, f38).func_181671_a(i2, j2).endVertex();
                    flag2 = true;
                }

                for (int i1 = 0; i1 < 4; ++i1) {
                    int j1 = 0;
                    int k1 = 0;

                    if (i1 == 0) {
                        --k1;
                    }

                    if (i1 == 1) {
                        ++k1;
                    }

                    if (i1 == 2) {
                        --j1;
                    }

                    if (i1 == 3) {
                        ++j1;
                    }

                    final BlockPos blockpos = blockPosIn.add(j1, 0, k1);
                    final TextureAtlasSprite textureatlassprite1 = atextureatlassprite[1];
                    worldRendererIn.setSprite(textureatlassprite1);

                    if (aboolean[i1]) {
                        final float f39;
                        final float f40;
                        final double d3;
                        final double d4;
                        final double d5;
                        final double d6;

                        if (i1 == 0) {
                            f39 = f7;
                            f40 = f10;
                            d3 = d0;
                            d5 = d0 + 1.0D;
                            d4 = d2 + (double) f11;
                            d6 = d2 + (double) f11;
                        } else if (i1 == 1) {
                            f39 = f9;
                            f40 = f8;
                            d3 = d0 + 1.0D;
                            d5 = d0;
                            d4 = d2 + 1.0D - (double) f11;
                            d6 = d2 + 1.0D - (double) f11;
                        } else if (i1 == 2) {
                            f39 = f8;
                            f40 = f7;
                            d3 = d0 + (double) f11;
                            d5 = d0 + (double) f11;
                            d4 = d2 + 1.0D;
                            d6 = d2;
                        } else {
                            f39 = f10;
                            f40 = f9;
                            d3 = d0 + 1.0D - (double) f11;
                            d5 = d0 + 1.0D - (double) f11;
                            d4 = d2;
                            d6 = d2 + 1.0D;
                        }

                        flag2 = true;
                        final float f42 = textureatlassprite1.getInterpolatedU(0.0D);
                        final float f27 = textureatlassprite1.getInterpolatedU(8.0D);
                        final float f28 = textureatlassprite1.getInterpolatedV((1.0F - f39) * 16.0F * 0.5F);
                        final float f29 = textureatlassprite1.getInterpolatedV((1.0F - f40) * 16.0F * 0.5F);
                        final float f30 = textureatlassprite1.getInterpolatedV(8.0D);
                        final int j = blockliquid.getMixedBrightnessForBlock(blockAccess, blockpos);
                        final int k = j >> 16 & 65535;
                        final int l = j & 65535;
                        final float f31 = i1 < 2 ? FaceBakery.getFaceBrightness(EnumFacing.NORTH) : FaceBakery.getFaceBrightness(EnumFacing.WEST);
                        final float f32 = f4 * f31 * f;
                        final float f33 = f4 * f31 * f1;
                        final float f34 = f4 * f31 * f2;
                        worldRendererIn.pos(d3, d1 + (double) f39, d4).func_181666_a(f32, f33, f34, 1.0F).tex(f42, f28).func_181671_a(k, l).endVertex();
                        worldRendererIn.pos(d5, d1 + (double) f40, d6).func_181666_a(f32, f33, f34, 1.0F).tex(f27, f29).func_181671_a(k, l).endVertex();
                        worldRendererIn.pos(d5, d1 + 0.0D, d6).func_181666_a(f32, f33, f34, 1.0F).tex(f27, f30).func_181671_a(k, l).endVertex();
                        worldRendererIn.pos(d3, d1 + 0.0D, d4).func_181666_a(f32, f33, f34, 1.0F).tex(f42, f30).func_181671_a(k, l).endVertex();
                        worldRendererIn.pos(d3, d1 + 0.0D, d4).func_181666_a(f32, f33, f34, 1.0F).tex(f42, f30).func_181671_a(k, l).endVertex();
                        worldRendererIn.pos(d5, d1 + 0.0D, d6).func_181666_a(f32, f33, f34, 1.0F).tex(f27, f30).func_181671_a(k, l).endVertex();
                        worldRendererIn.pos(d5, d1 + (double) f40, d6).func_181666_a(f32, f33, f34, 1.0F).tex(f27, f29).func_181671_a(k, l).endVertex();
                        worldRendererIn.pos(d3, d1 + (double) f39, d4).func_181666_a(f32, f33, f34, 1.0F).tex(f42, f28).func_181671_a(k, l).endVertex();
                    }
                }

                worldRendererIn.setSprite(null);
                final boolean flag3 = flag2;
                return flag3;
            }

            flag2 = false;
        } finally {
            if (Config.isShaders()) {
                SVertexBuilder.popEntity(worldRendererIn);
            }
        }

        return flag2;
    }

    private float getFluidHeight(final IBlockAccess blockAccess, final BlockPos blockPosIn, final Material blockMaterial) {
        int i = 0;
        float f = 0.0F;

        for (int j = 0; j < 4; ++j) {
            final BlockPos blockpos = blockPosIn.add(-(j & 1), 0, -(j >> 1 & 1));

            if (blockAccess.getBlockState(blockpos.up()).getBlock().getMaterial() == blockMaterial) {
                return 1.0F;
            }

            final IBlockState iblockstate = blockAccess.getBlockState(blockpos);
            final Material material = iblockstate.getBlock().getMaterial();

            if (material != blockMaterial) {
                if (!material.isSolid()) {
                    ++f;
                    ++i;
                }
            } else {
                final int k = iblockstate.getValue(BlockLiquid.LEVEL).intValue();

                if (k >= 8 || k == 0) {
                    f += BlockLiquid.getLiquidHeightPercent(k) * 10.0F;
                    i += 10;
                }

                f += BlockLiquid.getLiquidHeightPercent(k);
                ++i;
            }
        }

        return 1.0F - f / (float) i;
    }
}
