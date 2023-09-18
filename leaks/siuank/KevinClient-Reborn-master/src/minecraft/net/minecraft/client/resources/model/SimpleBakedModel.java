package net.minecraft.client.resources.model;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class SimpleBakedModel implements IBakedModel
{
    protected List<BakedQuad> generalQuads;
    protected List<List<BakedQuad>> faceQuads;
    protected boolean ambientOcclusion;
    protected boolean gui3d;
    protected TextureAtlasSprite texture;
    protected ItemCameraTransforms cameraTransforms;

    public SimpleBakedModel(List<BakedQuad> generalQuadsIn, List<List<BakedQuad>> faceQuadsIn, boolean ambientOcclusionIn, boolean gui3dIn, TextureAtlasSprite textureIn, ItemCameraTransforms cameraTransformsIn)
    {
        this.generalQuads = generalQuadsIn;
        this.faceQuads = faceQuadsIn;
        this.ambientOcclusion = ambientOcclusionIn;
        this.gui3d = gui3dIn;
        this.texture = textureIn;
        this.cameraTransforms = cameraTransformsIn;
    }

    public List<BakedQuad> getFaceQuads(EnumFacing facing)
    {
        return this.faceQuads.get(facing.ordinal());
    }

    public List<BakedQuad> getGeneralQuads()
    {
        return this.generalQuads;
    }

    public boolean isAmbientOcclusion()
    {
        return this.ambientOcclusion;
    }

    public boolean isGui3d()
    {
        return this.gui3d;
    }

    public boolean isBuiltInRenderer()
    {
        return false;
    }

    public TextureAtlasSprite getParticleTexture()
    {
        return this.texture;
    }

    public ItemCameraTransforms getItemCameraTransforms()
    {
        return this.cameraTransforms;
    }

    public static class Builder
    {
        private List<BakedQuad> builderGeneralQuads;
        private List<List<BakedQuad>> builderFaceQuads;
        private boolean builderAmbientOcclusion;
        private TextureAtlasSprite builderTexture;
        private boolean builderGui3d;
        private ItemCameraTransforms builderCameraTransforms;

        public Builder(ModelBlock model)
        {
            this(model.isAmbientOcclusion(), model.isGui3d(), model.getAllTransforms());
        }

        public Builder(IBakedModel bakedModel, TextureAtlasSprite texture)
        {
            this(bakedModel.isAmbientOcclusion(), bakedModel.isGui3d(), bakedModel.getItemCameraTransforms());
            this.builderTexture = bakedModel.getParticleTexture();

            for (EnumFacing enumfacing : EnumFacing.values())
            {
                this.addFaceBreakingFours(bakedModel, texture, enumfacing);
            }

            this.addGeneralBreakingFours(bakedModel, texture);
        }

        private void addFaceBreakingFours(IBakedModel bakedModel, TextureAtlasSprite texture, EnumFacing facing)
        {
            for (BakedQuad bakedquad : bakedModel.getFaceQuads(facing))
            {
                this.addFaceQuad(facing, new BreakingFour(bakedquad, texture));
            }
        }

        private void addGeneralBreakingFours(IBakedModel p_177647_1_, TextureAtlasSprite texture)
        {
            for (BakedQuad bakedquad : p_177647_1_.getGeneralQuads())
            {
                this.addGeneralQuad(new BreakingFour(bakedquad, texture));
            }
        }

        private Builder(boolean ambientOcclusion, boolean gui3d, ItemCameraTransforms cameraTransforms)
        {
            this.builderGeneralQuads = Lists.<BakedQuad>newArrayList();
            this.builderFaceQuads = Lists.<List<BakedQuad>>newArrayListWithCapacity(6);

            for (EnumFacing enumfacing : EnumFacing.values())
            {
                this.builderFaceQuads.add(Lists.<BakedQuad>newArrayList());
            }

            this.builderAmbientOcclusion = ambientOcclusion;
            this.builderGui3d = gui3d;
            this.builderCameraTransforms = cameraTransforms;
        }

        public SimpleBakedModel.Builder addFaceQuad(EnumFacing facing, BakedQuad quad)
        {
            this.builderFaceQuads.get(facing.ordinal()).add(quad);
            return this;
        }

        public SimpleBakedModel.Builder addGeneralQuad(BakedQuad quad)
        {
            this.builderGeneralQuads.add(quad);
            return this;
        }

        public SimpleBakedModel.Builder setTexture(TextureAtlasSprite texture)
        {
            this.builderTexture = texture;
            return this;
        }

        public IBakedModel makeBakedModel()
        {
            if (this.builderTexture == null)
            {
                throw new RuntimeException("Missing particle!");
            }
            else
            {
                return new SimpleBakedModel(this.builderGeneralQuads, this.builderFaceQuads, this.builderAmbientOcclusion, this.builderGui3d, this.builderTexture, this.builderCameraTransforms);
            }
        }
    }
}
