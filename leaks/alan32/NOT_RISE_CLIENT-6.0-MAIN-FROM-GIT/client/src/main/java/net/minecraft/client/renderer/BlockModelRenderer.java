package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.optifine.BetterSnow;
import net.optifine.CustomColors;
import net.optifine.model.BlockModelCustomizer;
import net.optifine.model.ListQuadsOverlay;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.shaders.Shaders;

import java.util.BitSet;
import java.util.List;

public class BlockModelRenderer {
    private static float aoLightValueOpaque = 0.2F;
    private static boolean separateAoLightValue = false;
    private static final EnumWorldBlockLayer[] OVERLAY_LAYERS = new EnumWorldBlockLayer[]{EnumWorldBlockLayer.CUTOUT, EnumWorldBlockLayer.CUTOUT_MIPPED, EnumWorldBlockLayer.TRANSLUCENT};

    public BlockModelRenderer() {
        if (Reflector.ForgeModContainer_forgeLightPipelineEnabled.exists()) {
            Reflector.setFieldValue(Reflector.ForgeModContainer_forgeLightPipelineEnabled, false);
        }
    }

    public boolean renderModel(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final IBlockState blockStateIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn) {
        final Block block = blockStateIn.getBlock();
        block.setBlockBoundsBasedOnState(blockAccessIn, blockPosIn);
        return this.renderModel(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, true);
    }

    public boolean renderModel(final IBlockAccess blockAccessIn, IBakedModel modelIn, final IBlockState blockStateIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn, final boolean checkSides) {
        final boolean flag = Minecraft.isAmbientOcclusionEnabled() && blockStateIn.getBlock().getLightValue() == 0 && modelIn.isAmbientOcclusion();

        try {
            if (Config.isShaders()) {
                SVertexBuilder.pushEntity(blockStateIn, blockPosIn, blockAccessIn, worldRendererIn);
            }

            final RenderEnv renderenv = worldRendererIn.getRenderEnv(blockStateIn, blockPosIn);
            modelIn = BlockModelCustomizer.getRenderModel(modelIn, blockStateIn, renderenv);
            final boolean flag1 = flag ? this.renderModelSmooth(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides) : this.renderModelFlat(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides);

            if (flag1) {
                this.renderOverlayModels(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides, 0L, renderenv, flag);
            }

            if (Config.isShaders()) {
                SVertexBuilder.popEntity(worldRendererIn);
            }

            return flag1;
        } catch (final Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, blockPosIn, blockStateIn);
            crashreportcategory.addCrashSection("Using AO", flag);
            throw new ReportedException(crashreport);
        }
    }

    public boolean renderModelAmbientOcclusion(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final Block blockIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn, final boolean checkSides) {
        final IBlockState iblockstate = blockAccessIn.getBlockState(blockPosIn);
        return this.renderModelSmooth(blockAccessIn, modelIn, iblockstate, blockPosIn, worldRendererIn, checkSides);
    }

    private boolean renderModelSmooth(final IBlockAccess p_renderModelSmooth_1_, final IBakedModel p_renderModelSmooth_2_, final IBlockState p_renderModelSmooth_3_, final BlockPos p_renderModelSmooth_4_, final WorldRenderer p_renderModelSmooth_5_, final boolean p_renderModelSmooth_6_) {
        boolean flag = false;
        final Block block = p_renderModelSmooth_3_.getBlock();
        final RenderEnv renderenv = p_renderModelSmooth_5_.getRenderEnv(p_renderModelSmooth_3_, p_renderModelSmooth_4_);
        final EnumWorldBlockLayer enumworldblocklayer = p_renderModelSmooth_5_.getBlockLayer();

        for (final EnumFacing enumfacing : EnumFacing.VALUES) {
            List<BakedQuad> list = p_renderModelSmooth_2_.getFaceQuads(enumfacing);

            if (!list.isEmpty()) {
                final BlockPos blockpos = p_renderModelSmooth_4_.offset(enumfacing);

                if (!p_renderModelSmooth_6_ || block.shouldSideBeRendered(p_renderModelSmooth_1_, blockpos, enumfacing)) {
                    list = BlockModelCustomizer.getRenderQuads(list, p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, enumfacing, enumworldblocklayer, 0L, renderenv);
                    this.renderQuadsSmooth(p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, p_renderModelSmooth_5_, list, renderenv);
                    flag = true;
                }
            }
        }

        List<BakedQuad> list1 = p_renderModelSmooth_2_.getGeneralQuads();

        if (list1.size() > 0) {
            list1 = BlockModelCustomizer.getRenderQuads(list1, p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, null, enumworldblocklayer, 0L, renderenv);
            this.renderQuadsSmooth(p_renderModelSmooth_1_, p_renderModelSmooth_3_, p_renderModelSmooth_4_, p_renderModelSmooth_5_, list1, renderenv);
            flag = true;
        }

        return flag;
    }

    public boolean renderModelStandard(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final Block blockIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn, final boolean checkSides) {
        final IBlockState iblockstate = blockAccessIn.getBlockState(blockPosIn);
        return this.renderModelFlat(blockAccessIn, modelIn, iblockstate, blockPosIn, worldRendererIn, checkSides);
    }

    public boolean renderModelFlat(final IBlockAccess p_renderModelFlat_1_, final IBakedModel p_renderModelFlat_2_, final IBlockState p_renderModelFlat_3_, final BlockPos p_renderModelFlat_4_, final WorldRenderer p_renderModelFlat_5_, final boolean p_renderModelFlat_6_) {
        boolean flag = false;
        final Block block = p_renderModelFlat_3_.getBlock();
        final RenderEnv renderenv = p_renderModelFlat_5_.getRenderEnv(p_renderModelFlat_3_, p_renderModelFlat_4_);
        final EnumWorldBlockLayer enumworldblocklayer = p_renderModelFlat_5_.getBlockLayer();

        for (final EnumFacing enumfacing : EnumFacing.VALUES) {
            List<BakedQuad> list = p_renderModelFlat_2_.getFaceQuads(enumfacing);

            if (!list.isEmpty()) {
                final BlockPos blockpos = p_renderModelFlat_4_.offset(enumfacing);

                if (!p_renderModelFlat_6_ || block.shouldSideBeRendered(p_renderModelFlat_1_, blockpos, enumfacing)) {
                    final int i = block.getMixedBrightnessForBlock(p_renderModelFlat_1_, blockpos);
                    list = BlockModelCustomizer.getRenderQuads(list, p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, enumfacing, enumworldblocklayer, 0L, renderenv);
                    this.renderQuadsFlat(p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, enumfacing, i, false, p_renderModelFlat_5_, list, renderenv);
                    flag = true;
                }
            }
        }

        List<BakedQuad> list1 = p_renderModelFlat_2_.getGeneralQuads();

        if (list1.size() > 0) {
            list1 = BlockModelCustomizer.getRenderQuads(list1, p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, null, enumworldblocklayer, 0L, renderenv);
            this.renderQuadsFlat(p_renderModelFlat_1_, p_renderModelFlat_3_, p_renderModelFlat_4_, null, -1, true, p_renderModelFlat_5_, list1, renderenv);
            flag = true;
        }

        return flag;
    }

    private void renderQuadsSmooth(final IBlockAccess p_renderQuadsSmooth_1_, final IBlockState p_renderQuadsSmooth_2_, final BlockPos p_renderQuadsSmooth_3_, final WorldRenderer p_renderQuadsSmooth_4_, final List<BakedQuad> p_renderQuadsSmooth_5_, final RenderEnv p_renderQuadsSmooth_6_) {
        final Block block = p_renderQuadsSmooth_2_.getBlock();
        final float[] afloat = p_renderQuadsSmooth_6_.getQuadBounds();
        final BitSet bitset = p_renderQuadsSmooth_6_.getBoundsFlags();
        final BlockModelRenderer.AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = p_renderQuadsSmooth_6_.getAoFace();
        double d0 = p_renderQuadsSmooth_3_.getX();
        double d1 = p_renderQuadsSmooth_3_.getY();
        double d2 = p_renderQuadsSmooth_3_.getZ();
        final Block.EnumOffsetType block$enumoffsettype = block.getOffsetType();

        if (block$enumoffsettype != Block.EnumOffsetType.NONE) {
            final long i = MathHelper.getPositionRandom(p_renderQuadsSmooth_3_);
            d0 += ((double) ((float) (i >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
            d2 += ((double) ((float) (i >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;

            if (block$enumoffsettype == Block.EnumOffsetType.XYZ) {
                d1 += ((double) ((float) (i >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
            }
        }

        for (final BakedQuad bakedquad : p_renderQuadsSmooth_5_) {
            this.fillQuadBounds(block, bakedquad.getVertexData(), bakedquad.getFace(), afloat, bitset);
            blockmodelrenderer$ambientocclusionface.updateVertexBrightness(p_renderQuadsSmooth_1_, block, p_renderQuadsSmooth_3_, bakedquad.getFace(), afloat, bitset);

            if (bakedquad.getSprite().isEmissive) {
                blockmodelrenderer$ambientocclusionface.setMaxBlockLight();
            }

            if (p_renderQuadsSmooth_4_.isMultiTexture()) {
                p_renderQuadsSmooth_4_.addVertexData(bakedquad.getVertexDataSingle());
            } else {
                p_renderQuadsSmooth_4_.addVertexData(bakedquad.getVertexData());
            }

            p_renderQuadsSmooth_4_.putSprite(bakedquad.getSprite());
            p_renderQuadsSmooth_4_.putBrightness4(blockmodelrenderer$ambientocclusionface.vertexBrightness[0], blockmodelrenderer$ambientocclusionface.vertexBrightness[1], blockmodelrenderer$ambientocclusionface.vertexBrightness[2], blockmodelrenderer$ambientocclusionface.vertexBrightness[3]);
            final int j = CustomColors.getColorMultiplier(bakedquad, p_renderQuadsSmooth_2_, p_renderQuadsSmooth_1_, p_renderQuadsSmooth_3_, p_renderQuadsSmooth_6_);

            if (!bakedquad.hasTintIndex() && j == -1) {
                if (separateAoLightValue) {
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], 4);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], 3);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], 2);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], 1);
                } else {
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], 4);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], 3);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], 2);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], 1);
                }
            } else {
                int k;

                if (j != -1) {
                    k = j;
                } else {
                    k = block.colorMultiplier(p_renderQuadsSmooth_1_, p_renderQuadsSmooth_3_, bakedquad.getTintIndex());
                }

                if (EntityRenderer.anaglyphEnable) {
                    k = TextureUtil.anaglyphColor(k);
                }

                final float f = (float) (k >> 16 & 255) / 255.0F;
                final float f1 = (float) (k >> 8 & 255) / 255.0F;
                final float f2 = (float) (k & 255) / 255.0F;

                if (separateAoLightValue) {
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(f, f1, f2, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], 4);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(f, f1, f2, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], 3);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(f, f1, f2, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], 2);
                    p_renderQuadsSmooth_4_.putColorMultiplierRgba(f, f1, f2, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], 1);
                } else {
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0] * f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0] * f2, 4);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1] * f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1] * f2, 3);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2] * f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2] * f2, 2);
                    p_renderQuadsSmooth_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3] * f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3] * f2, 1);
                }
            }

            p_renderQuadsSmooth_4_.putPosition(d0, d1, d2);
        }
    }

    private void fillQuadBounds(final Block blockIn, final int[] vertexData, final EnumFacing facingIn, final float[] quadBounds, final BitSet boundsFlags) {
        float f = 32.0F;
        float f1 = 32.0F;
        float f2 = 32.0F;
        float f3 = -32.0F;
        float f4 = -32.0F;
        float f5 = -32.0F;
        final int i = vertexData.length / 4;

        for (int j = 0; j < 4; ++j) {
            final float f6 = Float.intBitsToFloat(vertexData[j * i]);
            final float f7 = Float.intBitsToFloat(vertexData[j * i + 1]);
            final float f8 = Float.intBitsToFloat(vertexData[j * i + 2]);
            f = Math.min(f, f6);
            f1 = Math.min(f1, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.max(f3, f6);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
        }

        if (quadBounds != null) {
            quadBounds[EnumFacing.WEST.getIndex()] = f;
            quadBounds[EnumFacing.EAST.getIndex()] = f3;
            quadBounds[EnumFacing.DOWN.getIndex()] = f1;
            quadBounds[EnumFacing.UP.getIndex()] = f4;
            quadBounds[EnumFacing.NORTH.getIndex()] = f2;
            quadBounds[EnumFacing.SOUTH.getIndex()] = f5;
            final int k = EnumFacing.VALUES.length;
            quadBounds[EnumFacing.WEST.getIndex() + k] = 1.0F - f;
            quadBounds[EnumFacing.EAST.getIndex() + k] = 1.0F - f3;
            quadBounds[EnumFacing.DOWN.getIndex() + k] = 1.0F - f1;
            quadBounds[EnumFacing.UP.getIndex() + k] = 1.0F - f4;
            quadBounds[EnumFacing.NORTH.getIndex() + k] = 1.0F - f2;
            quadBounds[EnumFacing.SOUTH.getIndex() + k] = 1.0F - f5;
        }

        switch (facingIn) {
            case DOWN:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f1 < 1.0E-4F || blockIn.isFullCube()) && f1 == f4);
                break;

            case UP:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f4 > 0.9999F || blockIn.isFullCube()) && f1 == f4);
                break;

            case NORTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, (f2 < 1.0E-4F || blockIn.isFullCube()) && f2 == f5);
                break;

            case SOUTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, (f5 > 0.9999F || blockIn.isFullCube()) && f2 == f5);
                break;

            case WEST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f < 1.0E-4F || blockIn.isFullCube()) && f == f3);
                break;

            case EAST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f3 > 0.9999F || blockIn.isFullCube()) && f == f3);
        }
    }

    private void renderQuadsFlat(final IBlockAccess p_renderQuadsFlat_1_, final IBlockState p_renderQuadsFlat_2_, final BlockPos p_renderQuadsFlat_3_, final EnumFacing p_renderQuadsFlat_4_, int p_renderQuadsFlat_5_, final boolean p_renderQuadsFlat_6_, final WorldRenderer p_renderQuadsFlat_7_, final List<BakedQuad> p_renderQuadsFlat_8_, final RenderEnv p_renderQuadsFlat_9_) {
        final Block block = p_renderQuadsFlat_2_.getBlock();
        final BitSet bitset = p_renderQuadsFlat_9_.getBoundsFlags();
        double d0 = p_renderQuadsFlat_3_.getX();
        double d1 = p_renderQuadsFlat_3_.getY();
        double d2 = p_renderQuadsFlat_3_.getZ();
        final Block.EnumOffsetType block$enumoffsettype = block.getOffsetType();

        if (block$enumoffsettype != Block.EnumOffsetType.NONE) {
            final int i = p_renderQuadsFlat_3_.getX();
            final int j = p_renderQuadsFlat_3_.getZ();
            long k = (i * 3129871L) ^ (long) j * 116129781L;
            k = k * k * 42317861L + k * 11L;
            d0 += ((double) ((float) (k >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
            d2 += ((double) ((float) (k >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;

            if (block$enumoffsettype == Block.EnumOffsetType.XYZ) {
                d1 += ((double) ((float) (k >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
            }
        }

        for (final BakedQuad bakedquad : p_renderQuadsFlat_8_) {
            if (p_renderQuadsFlat_6_) {
                this.fillQuadBounds(block, bakedquad.getVertexData(), bakedquad.getFace(), null, bitset);
                p_renderQuadsFlat_5_ = bitset.get(0) ? block.getMixedBrightnessForBlock(p_renderQuadsFlat_1_, p_renderQuadsFlat_3_.offset(bakedquad.getFace())) : block.getMixedBrightnessForBlock(p_renderQuadsFlat_1_, p_renderQuadsFlat_3_);
            }

            if (bakedquad.getSprite().isEmissive) {
                p_renderQuadsFlat_5_ |= 240;
            }

            if (p_renderQuadsFlat_7_.isMultiTexture()) {
                p_renderQuadsFlat_7_.addVertexData(bakedquad.getVertexDataSingle());
            } else {
                p_renderQuadsFlat_7_.addVertexData(bakedquad.getVertexData());
            }

            p_renderQuadsFlat_7_.putSprite(bakedquad.getSprite());
            p_renderQuadsFlat_7_.putBrightness4(p_renderQuadsFlat_5_, p_renderQuadsFlat_5_, p_renderQuadsFlat_5_, p_renderQuadsFlat_5_);
            final int i1 = CustomColors.getColorMultiplier(bakedquad, p_renderQuadsFlat_2_, p_renderQuadsFlat_1_, p_renderQuadsFlat_3_, p_renderQuadsFlat_9_);

            if (bakedquad.hasTintIndex() || i1 != -1) {
                int l;

                if (i1 != -1) {
                    l = i1;
                } else {
                    l = block.colorMultiplier(p_renderQuadsFlat_1_, p_renderQuadsFlat_3_, bakedquad.getTintIndex());
                }

                if (EntityRenderer.anaglyphEnable) {
                    l = TextureUtil.anaglyphColor(l);
                }

                final float f = (float) (l >> 16 & 255) / 255.0F;
                final float f1 = (float) (l >> 8 & 255) / 255.0F;
                final float f2 = (float) (l & 255) / 255.0F;
                p_renderQuadsFlat_7_.putColorMultiplier(f, f1, f2, 4);
                p_renderQuadsFlat_7_.putColorMultiplier(f, f1, f2, 3);
                p_renderQuadsFlat_7_.putColorMultiplier(f, f1, f2, 2);
                p_renderQuadsFlat_7_.putColorMultiplier(f, f1, f2, 1);
            }

            p_renderQuadsFlat_7_.putPosition(d0, d1, d2);
        }
    }

    public void renderModelBrightnessColor(final IBakedModel bakedModel, final float p_178262_2_, final float p_178262_3_, final float p_178262_4_, final float p_178262_5_) {
        for (final EnumFacing enumfacing : EnumFacing.VALUES) {
            this.renderModelBrightnessColorQuads(p_178262_2_, p_178262_3_, p_178262_4_, p_178262_5_, bakedModel.getFaceQuads(enumfacing));
        }

        this.renderModelBrightnessColorQuads(p_178262_2_, p_178262_3_, p_178262_4_, p_178262_5_, bakedModel.getGeneralQuads());
    }

    public void renderModelBrightness(final IBakedModel p_178266_1_, final IBlockState p_178266_2_, final float p_178266_3_, final boolean p_178266_4_) {
        final Block block = p_178266_2_.getBlock();
        block.setBlockBoundsForItemRender();
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        int i = block.getRenderColor(block.getStateForEntityRender(p_178266_2_));

        if (EntityRenderer.anaglyphEnable) {
            i = TextureUtil.anaglyphColor(i);
        }

        final float f = (float) (i >> 16 & 255) / 255.0F;
        final float f1 = (float) (i >> 8 & 255) / 255.0F;
        final float f2 = (float) (i & 255) / 255.0F;

        if (!p_178266_4_) {
            GlStateManager.color(p_178266_3_, p_178266_3_, p_178266_3_, 1.0F);
        }

        this.renderModelBrightnessColor(p_178266_1_, p_178266_3_, f, f1, f2);
    }

    private void renderModelBrightnessColorQuads(final float p_178264_1_, final float p_178264_2_, final float p_178264_3_, final float p_178264_4_, final List<BakedQuad> p_178264_5_) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        for (final BakedQuad bakedquad : p_178264_5_) {
            worldrenderer.begin(7, DefaultVertexFormats.ITEM);
            worldrenderer.addVertexData(bakedquad.getVertexData());
            worldrenderer.putSprite(bakedquad.getSprite());

            if (bakedquad.hasTintIndex()) {
                worldrenderer.putColorRGB_F4(p_178264_2_ * p_178264_1_, p_178264_3_ * p_178264_1_, p_178264_4_ * p_178264_1_);
            } else {
                worldrenderer.putColorRGB_F4(p_178264_1_, p_178264_1_, p_178264_1_);
            }

            final Vec3i vec3i = bakedquad.getFace().getDirectionVec();
            worldrenderer.putNormal((float) vec3i.getX(), (float) vec3i.getY(), (float) vec3i.getZ());
            tessellator.draw();
        }
    }

    public static float fixAoLightValue(final float p_fixAoLightValue_0_) {
        return p_fixAoLightValue_0_ == 0.2F ? aoLightValueOpaque : p_fixAoLightValue_0_;
    }

    public static void updateAoLightValue() {
        aoLightValueOpaque = 1.0F - Config.getAmbientOcclusionLevel() * 0.8F;
        separateAoLightValue = Config.isShaders() && Shaders.isSeparateAo();
    }

    private void renderOverlayModels(final IBlockAccess p_renderOverlayModels_1_, final IBakedModel p_renderOverlayModels_2_, final IBlockState p_renderOverlayModels_3_, final BlockPos p_renderOverlayModels_4_, final WorldRenderer p_renderOverlayModels_5_, final boolean p_renderOverlayModels_6_, final long p_renderOverlayModels_7_, final RenderEnv p_renderOverlayModels_9_, final boolean p_renderOverlayModels_10_) {
        if (p_renderOverlayModels_9_.isOverlaysRendered()) {
            for (final EnumWorldBlockLayer enumworldblocklayer : OVERLAY_LAYERS) {
                final ListQuadsOverlay listquadsoverlay = p_renderOverlayModels_9_.getListQuadsOverlay(enumworldblocklayer);

                if (listquadsoverlay.size() > 0) {
                    final RegionRenderCacheBuilder regionrendercachebuilder = p_renderOverlayModels_9_.getRegionRenderCacheBuilder();

                    if (regionrendercachebuilder != null) {
                        final WorldRenderer worldrenderer = regionrendercachebuilder.getWorldRendererByLayer(enumworldblocklayer);

                        if (!worldrenderer.isDrawing()) {
                            worldrenderer.begin(7, DefaultVertexFormats.BLOCK);
                            worldrenderer.setTranslation(p_renderOverlayModels_5_.getXOffset(), p_renderOverlayModels_5_.getYOffset(), p_renderOverlayModels_5_.getZOffset());
                        }

                        for (int j = 0; j < listquadsoverlay.size(); ++j) {
                            final BakedQuad bakedquad = listquadsoverlay.getQuad(j);
                            final List<BakedQuad> list = listquadsoverlay.getListQuadsSingle(bakedquad);
                            final IBlockState iblockstate = listquadsoverlay.getBlockState(j);

                            if (bakedquad.getQuadEmissive() != null) {
                                listquadsoverlay.addQuad(bakedquad.getQuadEmissive(), iblockstate);
                            }

                            p_renderOverlayModels_9_.reset(iblockstate, p_renderOverlayModels_4_);

                            if (p_renderOverlayModels_10_) {
                                this.renderQuadsSmooth(p_renderOverlayModels_1_, iblockstate, p_renderOverlayModels_4_, worldrenderer, list, p_renderOverlayModels_9_);
                            } else {
                                final int k = iblockstate.getBlock().getMixedBrightnessForBlock(p_renderOverlayModels_1_, p_renderOverlayModels_4_.offset(bakedquad.getFace()));
                                this.renderQuadsFlat(p_renderOverlayModels_1_, iblockstate, p_renderOverlayModels_4_, bakedquad.getFace(), k, false, worldrenderer, list, p_renderOverlayModels_9_);
                            }
                        }
                    }

                    listquadsoverlay.clear();
                }
            }
        }

        if (Config.isBetterSnow() && !p_renderOverlayModels_9_.isBreakingAnimation() && BetterSnow.shouldRender(p_renderOverlayModels_1_, p_renderOverlayModels_3_, p_renderOverlayModels_4_)) {
            final IBakedModel ibakedmodel = BetterSnow.getModelSnowLayer();
            final IBlockState iblockstate1 = BetterSnow.getStateSnowLayer();
            this.renderModel(p_renderOverlayModels_1_, ibakedmodel, iblockstate1, p_renderOverlayModels_4_, p_renderOverlayModels_5_, p_renderOverlayModels_6_);
        }
    }

    public static class AmbientOcclusionFace {
        private final float[] vertexColorMultiplier;
        private final int[] vertexBrightness;

        public AmbientOcclusionFace() {
            this(null);
        }

        public AmbientOcclusionFace(final BlockModelRenderer p_i46235_1_) {
            this.vertexColorMultiplier = new float[4];
            this.vertexBrightness = new int[4];
        }

        public void setMaxBlockLight() {
            final int i = 240;
            this.vertexBrightness[0] |= i;
            this.vertexBrightness[1] |= i;
            this.vertexBrightness[2] |= i;
            this.vertexBrightness[3] |= i;
            this.vertexColorMultiplier[0] = 1.0F;
            this.vertexColorMultiplier[1] = 1.0F;
            this.vertexColorMultiplier[2] = 1.0F;
            this.vertexColorMultiplier[3] = 1.0F;
        }

        public void updateVertexBrightness(final IBlockAccess blockAccessIn, final Block blockIn, final BlockPos blockPosIn, final EnumFacing facingIn, final float[] quadBounds, final BitSet boundsFlags) {
            final BlockPos blockpos = boundsFlags.get(0) ? blockPosIn.offset(facingIn) : blockPosIn;
            final BlockModelRenderer.EnumNeighborInfo blockmodelrenderer$enumneighborinfo = BlockModelRenderer.EnumNeighborInfo.getNeighbourInfo(facingIn);
            final BlockPos blockpos1 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[0]);
            final BlockPos blockpos2 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[1]);
            final BlockPos blockpos3 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
            final BlockPos blockpos4 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
            final int i = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos1);
            final int j = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos2);
            final int k = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos3);
            final int l = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos4);
            final float f = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos1).getBlock().getAmbientOcclusionLightValue());
            final float f1 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos2).getBlock().getAmbientOcclusionLightValue());
            final float f2 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos3).getBlock().getAmbientOcclusionLightValue());
            final float f3 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos4).getBlock().getAmbientOcclusionLightValue());
            final boolean flag = blockAccessIn.getBlockState(blockpos1.offset(facingIn)).getBlock().isTranslucent();
            final boolean flag1 = blockAccessIn.getBlockState(blockpos2.offset(facingIn)).getBlock().isTranslucent();
            final boolean flag2 = blockAccessIn.getBlockState(blockpos3.offset(facingIn)).getBlock().isTranslucent();
            final boolean flag3 = blockAccessIn.getBlockState(blockpos4.offset(facingIn)).getBlock().isTranslucent();
            final float f4;
            final int i1;

            if (!flag2 && !flag) {
                f4 = f;
                i1 = i;
            } else {
                final BlockPos blockpos5 = blockpos1.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
                f4 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos5).getBlock().getAmbientOcclusionLightValue());
                i1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos5);
            }

            final int j1;
            final float f26;

            if (!flag3 && !flag) {
                f26 = f;
                j1 = i;
            } else {
                final BlockPos blockpos6 = blockpos1.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
                f26 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos6).getBlock().getAmbientOcclusionLightValue());
                j1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos6);
            }

            final int k1;
            final float f27;

            if (!flag2 && !flag1) {
                f27 = f1;
                k1 = j;
            } else {
                final BlockPos blockpos7 = blockpos2.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
                f27 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos7).getBlock().getAmbientOcclusionLightValue());
                k1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos7);
            }

            final int l1;
            final float f28;

            if (!flag3 && !flag1) {
                f28 = f1;
                l1 = j;
            } else {
                final BlockPos blockpos8 = blockpos2.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
                f28 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos8).getBlock().getAmbientOcclusionLightValue());
                l1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos8);
            }

            int i3 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn);

            if (boundsFlags.get(0) || !blockAccessIn.getBlockState(blockPosIn.offset(facingIn)).getBlock().isOpaqueCube()) {
                i3 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn.offset(facingIn));
            }

            float f5 = boundsFlags.get(0) ? blockAccessIn.getBlockState(blockpos).getBlock().getAmbientOcclusionLightValue() : blockAccessIn.getBlockState(blockPosIn).getBlock().getAmbientOcclusionLightValue();
            f5 = BlockModelRenderer.fixAoLightValue(f5);
            final BlockModelRenderer.VertexTranslations blockmodelrenderer$vertextranslations = BlockModelRenderer.VertexTranslations.getVertexTranslations(facingIn);

            if (boundsFlags.get(1) && blockmodelrenderer$enumneighborinfo.field_178289_i) {
                final float f29 = (f3 + f + f26 + f5) * 0.25F;
                final float f30 = (f2 + f + f4 + f5) * 0.25F;
                final float f31 = (f2 + f1 + f27 + f5) * 0.25F;
                final float f32 = (f3 + f1 + f28 + f5) * 0.25F;
                final float f10 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[1].field_178229_m];
                final float f11 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[3].field_178229_m];
                final float f12 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[5].field_178229_m];
                final float f13 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[7].field_178229_m];
                final float f14 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[1].field_178229_m];
                final float f15 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[3].field_178229_m];
                final float f16 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[5].field_178229_m];
                final float f17 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[7].field_178229_m];
                final float f18 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[1].field_178229_m];
                final float f19 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[3].field_178229_m];
                final float f20 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[5].field_178229_m];
                final float f21 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[7].field_178229_m];
                final float f22 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[1].field_178229_m];
                final float f23 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[3].field_178229_m];
                final float f24 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[5].field_178229_m];
                final float f25 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[7].field_178229_m];
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178191_g] = f29 * f10 + f30 * f11 + f31 * f12 + f32 * f13;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178200_h] = f29 * f14 + f30 * f15 + f31 * f16 + f32 * f17;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178201_i] = f29 * f18 + f30 * f19 + f31 * f20 + f32 * f21;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178198_j] = f29 * f22 + f30 * f23 + f31 * f24 + f32 * f25;
                final int i2 = this.getAoBrightness(l, i, j1, i3);
                final int j2 = this.getAoBrightness(k, i, i1, i3);
                final int k2 = this.getAoBrightness(k, j, k1, i3);
                final int l2 = this.getAoBrightness(l, j, l1, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178191_g] = this.getVertexBrightness(i2, j2, k2, l2, f10, f11, f12, f13);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178200_h] = this.getVertexBrightness(i2, j2, k2, l2, f14, f15, f16, f17);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178201_i] = this.getVertexBrightness(i2, j2, k2, l2, f18, f19, f20, f21);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178198_j] = this.getVertexBrightness(i2, j2, k2, l2, f22, f23, f24, f25);
            } else {
                final float f6 = (f3 + f + f26 + f5) * 0.25F;
                final float f7 = (f2 + f + f4 + f5) * 0.25F;
                final float f8 = (f2 + f1 + f27 + f5) * 0.25F;
                final float f9 = (f3 + f1 + f28 + f5) * 0.25F;
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178191_g] = this.getAoBrightness(l, i, j1, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178200_h] = this.getAoBrightness(k, i, i1, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178201_i] = this.getAoBrightness(k, j, k1, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178198_j] = this.getAoBrightness(l, j, l1, i3);
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178191_g] = f6;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178200_h] = f7;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178201_i] = f8;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178198_j] = f9;
            }
        }

        private int getAoBrightness(int p_147778_1_, int p_147778_2_, int p_147778_3_, final int p_147778_4_) {
            if (p_147778_1_ == 0) {
                p_147778_1_ = p_147778_4_;
            }

            if (p_147778_2_ == 0) {
                p_147778_2_ = p_147778_4_;
            }

            if (p_147778_3_ == 0) {
                p_147778_3_ = p_147778_4_;
            }

            return p_147778_1_ + p_147778_2_ + p_147778_3_ + p_147778_4_ >> 2 & 16711935;
        }

        private int getVertexBrightness(final int p_178203_1_, final int p_178203_2_, final int p_178203_3_, final int p_178203_4_, final float p_178203_5_, final float p_178203_6_, final float p_178203_7_, final float p_178203_8_) {
            final int i = (int) ((float) (p_178203_1_ >> 16 & 255) * p_178203_5_ + (float) (p_178203_2_ >> 16 & 255) * p_178203_6_ + (float) (p_178203_3_ >> 16 & 255) * p_178203_7_ + (float) (p_178203_4_ >> 16 & 255) * p_178203_8_) & 255;
            final int j = (int) ((float) (p_178203_1_ & 255) * p_178203_5_ + (float) (p_178203_2_ & 255) * p_178203_6_ + (float) (p_178203_3_ & 255) * p_178203_7_ + (float) (p_178203_4_ & 255) * p_178203_8_) & 255;
            return i << 16 | j;
        }
    }

    public enum EnumNeighborInfo {
        DOWN(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.5F, false, new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0]),
        UP(new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH}, 1.0F, false, new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0]),
        NORTH(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
        SOUTH(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
        WEST(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
        EAST(new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

        private final EnumFacing[] field_178276_g;
        private final float field_178288_h;
        private final boolean field_178289_i;
        private final BlockModelRenderer.Orientation[] field_178286_j;
        private final BlockModelRenderer.Orientation[] field_178287_k;
        private final BlockModelRenderer.Orientation[] field_178284_l;
        private final BlockModelRenderer.Orientation[] field_178285_m;
        private static final BlockModelRenderer.EnumNeighborInfo[] field_178282_n = new BlockModelRenderer.EnumNeighborInfo[6];

        EnumNeighborInfo(final EnumFacing[] p_i46236_3_, final float p_i46236_4_, final boolean p_i46236_5_, final BlockModelRenderer.Orientation[] p_i46236_6_, final BlockModelRenderer.Orientation[] p_i46236_7_, final BlockModelRenderer.Orientation[] p_i46236_8_, final BlockModelRenderer.Orientation[] p_i46236_9_) {
            this.field_178276_g = p_i46236_3_;
            this.field_178288_h = p_i46236_4_;
            this.field_178289_i = p_i46236_5_;
            this.field_178286_j = p_i46236_6_;
            this.field_178287_k = p_i46236_7_;
            this.field_178284_l = p_i46236_8_;
            this.field_178285_m = p_i46236_9_;
        }

        public static BlockModelRenderer.EnumNeighborInfo getNeighbourInfo(final EnumFacing p_178273_0_) {
            return field_178282_n[p_178273_0_.getIndex()];
        }

        static {
            field_178282_n[EnumFacing.DOWN.getIndex()] = DOWN;
            field_178282_n[EnumFacing.UP.getIndex()] = UP;
            field_178282_n[EnumFacing.NORTH.getIndex()] = NORTH;
            field_178282_n[EnumFacing.SOUTH.getIndex()] = SOUTH;
            field_178282_n[EnumFacing.WEST.getIndex()] = WEST;
            field_178282_n[EnumFacing.EAST.getIndex()] = EAST;
        }
    }

    public enum Orientation {
        DOWN(EnumFacing.DOWN, false),
        UP(EnumFacing.UP, false),
        NORTH(EnumFacing.NORTH, false),
        SOUTH(EnumFacing.SOUTH, false),
        WEST(EnumFacing.WEST, false),
        EAST(EnumFacing.EAST, false),
        FLIP_DOWN(EnumFacing.DOWN, true),
        FLIP_UP(EnumFacing.UP, true),
        FLIP_NORTH(EnumFacing.NORTH, true),
        FLIP_SOUTH(EnumFacing.SOUTH, true),
        FLIP_WEST(EnumFacing.WEST, true),
        FLIP_EAST(EnumFacing.EAST, true);

        private final int field_178229_m;

        Orientation(final EnumFacing p_i46233_3_, final boolean p_i46233_4_) {
            this.field_178229_m = p_i46233_3_.getIndex() + (p_i46233_4_ ? EnumFacing.values().length : 0);
        }
    }

    enum VertexTranslations {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        private final int field_178191_g;
        private final int field_178200_h;
        private final int field_178201_i;
        private final int field_178198_j;
        private static final BlockModelRenderer.VertexTranslations[] field_178199_k = new BlockModelRenderer.VertexTranslations[6];

        VertexTranslations(final int p_i46234_3_, final int p_i46234_4_, final int p_i46234_5_, final int p_i46234_6_) {
            this.field_178191_g = p_i46234_3_;
            this.field_178200_h = p_i46234_4_;
            this.field_178201_i = p_i46234_5_;
            this.field_178198_j = p_i46234_6_;
        }

        public static BlockModelRenderer.VertexTranslations getVertexTranslations(final EnumFacing p_178184_0_) {
            return field_178199_k[p_178184_0_.getIndex()];
        }

        static {
            field_178199_k[EnumFacing.DOWN.getIndex()] = DOWN;
            field_178199_k[EnumFacing.UP.getIndex()] = UP;
            field_178199_k[EnumFacing.NORTH.getIndex()] = NORTH;
            field_178199_k[EnumFacing.SOUTH.getIndex()] = SOUTH;
            field_178199_k[EnumFacing.WEST.getIndex()] = WEST;
            field_178199_k[EnumFacing.EAST.getIndex()] = EAST;
        }
    }
}
