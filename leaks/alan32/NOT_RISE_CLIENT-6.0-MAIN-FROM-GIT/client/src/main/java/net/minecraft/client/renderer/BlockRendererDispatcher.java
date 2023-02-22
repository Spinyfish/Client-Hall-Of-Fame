package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;

public class BlockRendererDispatcher implements IResourceManagerReloadListener {
    private final BlockModelShapes blockModelShapes;
    private final GameSettings gameSettings;
    private final BlockModelRenderer blockModelRenderer = new BlockModelRenderer();
    private final ChestRenderer chestRenderer = new ChestRenderer();
    private final BlockFluidRenderer fluidRenderer = new BlockFluidRenderer();

    public BlockRendererDispatcher(final BlockModelShapes blockModelShapesIn, final GameSettings gameSettingsIn) {
        this.blockModelShapes = blockModelShapesIn;
        this.gameSettings = gameSettingsIn;
    }

    public BlockModelShapes getBlockModelShapes() {
        return this.blockModelShapes;
    }

    public void renderBlockDamage(IBlockState state, final BlockPos pos, final TextureAtlasSprite texture, final IBlockAccess blockAccess) {
        final Block block = state.getBlock();
        final int i = block.getRenderType();

        if (i == 3) {
            state = block.getActualState(state, blockAccess, pos);
            final IBakedModel ibakedmodel = this.blockModelShapes.getModelForState(state);
            final IBakedModel ibakedmodel1 = (new SimpleBakedModel.Builder(ibakedmodel, texture)).makeBakedModel();
            this.blockModelRenderer.renderModel(blockAccess, ibakedmodel1, state, pos, Tessellator.getInstance().getWorldRenderer());
        }
    }

    public boolean renderBlock(final IBlockState state, final BlockPos pos, final IBlockAccess blockAccess, final WorldRenderer worldRendererIn) {
        try {
            final int i = state.getBlock().getRenderType();

            if (i == -1) {
                return false;
            } else {
                switch (i) {
                    case 1:
                        return this.fluidRenderer.renderFluid(blockAccess, state, pos, worldRendererIn);

                    case 2:
                        return false;

                    case 3:
                        final IBakedModel ibakedmodel = this.getModelFromBlockState(state, blockAccess, pos);
                        return this.blockModelRenderer.renderModel(blockAccess, ibakedmodel, state, pos, worldRendererIn);

                    default:
                        return false;
                }
            }
        } catch (final Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
            throw new ReportedException(crashreport);
        }
    }

    public BlockModelRenderer getBlockModelRenderer() {
        return this.blockModelRenderer;
    }

    private IBakedModel getBakedModel(final IBlockState state, final BlockPos pos) {
        IBakedModel ibakedmodel = this.blockModelShapes.getModelForState(state);

        if (pos != null && this.gameSettings.allowBlockAlternatives && ibakedmodel instanceof WeightedBakedModel) {
            ibakedmodel = ((WeightedBakedModel) ibakedmodel).getAlternativeModel(MathHelper.getPositionRandom(pos));
        }

        return ibakedmodel;
    }

    public IBakedModel getModelFromBlockState(IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        final Block block = state.getBlock();

        if (worldIn.getWorldType() != WorldType.DEBUG_WORLD) {
            try {
                state = block.getActualState(state, worldIn, pos);
            } catch (final Exception var6) {
            }
        }

        IBakedModel ibakedmodel = this.blockModelShapes.getModelForState(state);

        if (pos != null && this.gameSettings.allowBlockAlternatives && ibakedmodel instanceof WeightedBakedModel) {
            ibakedmodel = ((WeightedBakedModel) ibakedmodel).getAlternativeModel(MathHelper.getPositionRandom(pos));
        }

        return ibakedmodel;
    }

    public void renderBlockBrightness(final IBlockState state, final float brightness) {
        final int i = state.getBlock().getRenderType();

        if (i != -1) {
            switch (i) {
                case 1:
                default:
                    break;

                case 2:
                    this.chestRenderer.renderChestBrightness(state.getBlock(), brightness);
                    break;

                case 3:
                    final IBakedModel ibakedmodel = this.getBakedModel(state, null);
                    this.blockModelRenderer.renderModelBrightness(ibakedmodel, state, brightness, true);
            }
        }
    }

    public boolean isRenderTypeChest(final Block p_175021_1_, final int p_175021_2_) {
        if (p_175021_1_ == null) {
            return false;
        } else {
            final int i = p_175021_1_.getRenderType();
            return i != 3 && i == 2;
        }
    }

    public void onResourceManagerReload(final IResourceManager resourceManager) {
        this.fluidRenderer.initAtlasSprites();
    }
}
