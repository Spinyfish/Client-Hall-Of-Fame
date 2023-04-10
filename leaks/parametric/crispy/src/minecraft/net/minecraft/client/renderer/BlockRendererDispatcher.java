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
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import shadersmod.client.SVertexBuilder;

public class BlockRendererDispatcher implements IResourceManagerReloadListener
{
    private BlockModelShapes blockModelShapes;
    private final GameSettings gameSettings;
    private final BlockModelRenderer blockModelRenderer = new BlockModelRenderer();
    private final ChestRenderer chestRenderer = new ChestRenderer();
    private final BlockFluidRenderer fluidRenderer = new BlockFluidRenderer();

    public BlockRendererDispatcher(BlockModelShapes blockModelShapesIn, GameSettings gameSettingsIn)
    {
        this.blockModelShapes = blockModelShapesIn;
        this.gameSettings = gameSettingsIn;
    }

    public BlockModelShapes getBlockModelShapes()
    {
        return this.blockModelShapes;
    }

    public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IBlockAccess blockAccess)
    {
        Block var5 = state.getBlock();
        int var6 = var5.getRenderType();

        if (var6 == 3)
        {
            state = var5.getActualState(state, blockAccess, pos);
            IBakedModel var7 = this.blockModelShapes.getModelForState(state);

            if (Reflector.ISmartBlockModel.isInstance(var7))
            {
                IBlockState var15 = (IBlockState)Reflector.call(var5, Reflector.ForgeBlock_getExtendedState, new Object[] {state, blockAccess, pos});
                EnumWorldBlockLayer[] arr$ = EnumWorldBlockLayer.values();
                int len$ = arr$.length;

                for (int i$ = 0; i$ < len$; ++i$)
                {
                    EnumWorldBlockLayer layer = arr$[i$];

                    if (Reflector.callBoolean(var5, Reflector.ForgeBlock_canRenderInLayer, new Object[] {layer}))
                    {
                        Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, new Object[] {layer});
                        IBakedModel targetLayer = (IBakedModel)Reflector.call(var7, Reflector.ISmartBlockModel_handleBlockState, new Object[] {var15});
                        IBakedModel damageModel = (new SimpleBakedModel.Builder(targetLayer, texture)).makeBakedModel();
                        this.blockModelRenderer.renderModel(blockAccess, damageModel, state, pos, Tessellator.getInstance().getWorldRenderer());
                    }
                }

                return;
            }

            IBakedModel var8 = (new SimpleBakedModel.Builder(var7, texture)).makeBakedModel();
            this.blockModelRenderer.renderModel(blockAccess, var8, state, pos, Tessellator.getInstance().getWorldRenderer());
        }
    }

    public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, WorldRenderer worldRendererIn)
    {
        try
        {
            int var8 = state.getBlock().getRenderType();

            if (var8 == -1)
            {
                return false;
            }
            else
            {
                switch (var8)
                {
                    case 1:
                        if (Config.isShaders())
                        {
                            SVertexBuilder.pushEntity(state, pos, blockAccess, worldRendererIn);
                        }

                        boolean var61 = this.fluidRenderer.renderFluid(blockAccess, state, pos, worldRendererIn);

                        if (Config.isShaders())
                        {
                            SVertexBuilder.popEntity(worldRendererIn);
                        }

                        return var61;

                    case 2:
                        return false;

                    case 3:
                        IBakedModel var71 = this.getModelFromBlockState(state, blockAccess, pos);

                        if (Config.isShaders())
                        {
                            SVertexBuilder.pushEntity(state, pos, blockAccess, worldRendererIn);
                        }

                        boolean flag3 = this.blockModelRenderer.renderModel(blockAccess, var71, state, pos, worldRendererIn);

                        if (Config.isShaders())
                        {
                            SVertexBuilder.popEntity(worldRendererIn);
                        }

                        return flag3;

                    default:
                        return false;
                }
            }
        }
        catch (Throwable var9)
        {
            CrashReport var6 = CrashReport.makeCrashReport(var9, "Tesselating block in world");
            CrashReportCategory var7 = var6.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(var7, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
            throw new ReportedException(var6);
        }
    }

    public BlockModelRenderer getBlockModelRenderer()
    {
        return this.blockModelRenderer;
    }

    private IBakedModel getBakedModel(IBlockState state, BlockPos pos)
    {
        IBakedModel var3 = this.blockModelShapes.getModelForState(state);

        if (pos != null && this.gameSettings.allowBlockAlternatives && var3 instanceof WeightedBakedModel)
        {
            var3 = ((WeightedBakedModel)var3).getAlternativeModel(MathHelper.getPositionRandom(pos));
        }

        return var3;
    }

    public IBakedModel getModelFromBlockState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        Block var4 = state.getBlock();

        if (worldIn.getWorldType() != WorldType.DEBUG_WORLD)
        {
            try
            {
                state = var4.getActualState(state, worldIn, pos);
            }
            catch (Exception var7)
            {
                ;
            }
        }

        IBakedModel var5 = this.blockModelShapes.getModelForState(state);

        if (pos != null && this.gameSettings.allowBlockAlternatives && var5 instanceof WeightedBakedModel)
        {
            var5 = ((WeightedBakedModel)var5).getAlternativeModel(MathHelper.getPositionRandom(pos));
        }

        if (Reflector.ISmartBlockModel.isInstance(var5))
        {
            IBlockState extendedState = (IBlockState)Reflector.call(var4, Reflector.ForgeBlock_getExtendedState, new Object[] {state, worldIn, pos});
            var5 = (IBakedModel)Reflector.call(var5, Reflector.ISmartBlockModel_handleBlockState, new Object[] {extendedState});
        }

        return var5;
    }

    public void renderBlockBrightness(IBlockState state, float brightness)
    {
        int var3 = state.getBlock().getRenderType();

        if (var3 != -1)
        {
            switch (var3)
            {
                case 1:
                default:
                    break;

                case 2:
                    this.chestRenderer.renderChestBrightness(state.getBlock(), brightness);
                    break;

                case 3:
                    IBakedModel var4 = this.getBakedModel(state, (BlockPos)null);
                    this.blockModelRenderer.renderModelBrightness(var4, state, brightness, true);
            }
        }
    }

    public boolean isRenderTypeChest(Block p_175021_1_, int p_175021_2_)
    {
        if (p_175021_1_ == null)
        {
            return false;
        }
        else
        {
            int var3 = p_175021_1_.getRenderType();
            return var3 == 3 ? false : var3 == 2;
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.fluidRenderer.initAtlasSprites();
    }
}
