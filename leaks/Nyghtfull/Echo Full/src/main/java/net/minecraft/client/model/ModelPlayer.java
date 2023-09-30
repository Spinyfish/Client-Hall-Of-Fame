package net.minecraft.client.model;

import dev.echo.module.impl.render.CustomModel;
import dev.echo.utils.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelPlayer extends ModelBiped {
    public ModelRenderer bipedLeftArmwear;
    public ModelRenderer bipedRightArmwear;
    public ModelRenderer bipedLeftLegwear;
    public ModelRenderer bipedRightLegwear;
    public ModelRenderer bipedBodyWear;
    private final ModelRenderer bipedCape;
    private final ModelRenderer bipedDeadmau5Head;
    private final boolean smallArms;
    private final ModelRenderer body;
    private ModelRenderer eye;
    private final ModelRenderer left_leg;
    private final ModelRenderer right_leg;

    private final ModelRenderer rabbitBone;
    private final ModelRenderer rabbitRleg;
    private final ModelRenderer rabbitLarm;
    public final ModelRenderer rabbitRarm;
    private final ModelRenderer rabbitLleg;
    private final ModelRenderer rabbitHead;


    public ModelPlayer(float p_i46304_1_, boolean p_i46304_2_) {
        super(p_i46304_1_, 0.0F, 64, 64);

        (this.rabbitBone = new ModelRenderer(this)).setRotationPoint(0.0F, 24.f, 0.f);
        this.rabbitBone.cubeList.add(new ModelBox(this.rabbitBone, 28, 45, -5.f, -13.f, -5.f, 10, 11, 8, 0.f, false));
        (this.rabbitRleg = new ModelRenderer(this)).setRotationPoint(-3.f, -2.f, -1.f);
        this.rabbitBone.addChild(this.rabbitRleg);
        this.rabbitRleg.cubeList.add(new ModelBox(this.rabbitRleg, 0, 0, -2.f, 0.f, -2.f, 4, 2, 4, 0.f, false));
        (this.rabbitLarm = new ModelRenderer(this)).setRotationPoint(5.f, -13.f, -1.f);
        this.setRotationAngle(this.rabbitLarm, 0.f, 0.f, -.0873f);
        this.rabbitBone.addChild(this.rabbitLarm);
        this.rabbitLarm.cubeList.add(new ModelBox(this.rabbitLarm, 0, 0, 0.f, 0.f, -2.f, 2, 8, 4, 0.f, false));
        (this.rabbitRarm = new ModelRenderer(this)).setRotationPoint(-5.f, -13.f, -1.f);
        this.setRotationAngle(this.rabbitRarm, 0.f, .0f, .0873f);
        this.rabbitBone.addChild(this.rabbitRarm);
        this.rabbitRarm.cubeList.add(new ModelBox(this.rabbitRarm, 0, 0, -2.f, 0.f, -2.f, 2, 8, 4, 0.f, false));
        (this.rabbitLleg = new ModelRenderer(this)).setRotationPoint(3.f, -2.f, -1.f);
        this.rabbitBone.addChild(this.rabbitLleg);
        this.rabbitLleg.cubeList.add(new ModelBox(this.rabbitLleg, 0, 0, -2.f, 0.f, -2.f, 4, 2, 4, 0.f, false));
        (this.rabbitHead = new ModelRenderer(this)).setRotationPoint(0.f, -14.f, -1.f);
        this.rabbitBone.addChild(this.rabbitHead);
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 0, 0, -3.f, 0.f, -4.f, 6, 1, 6, 0, false));
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 56, 0, -5.f, -9.f, -5.f, 2, 3, 2, 0.f, false));
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 56, 0, 3.f, -9.f, -5.f, 2, 3, 2, 0.f, true));
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 0, 45, -4.f, -11.f, -4.f, 8, 11, 8, 0.f, false));
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 46, 0, 1.f, -20.f, 0.f, 3, 9, 1, 0.f, false));
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 46, 0, -4.f, -20.f, 0.f, 3, 9, 1, 0.f, false));

        this.body = new ModelRenderer(this);
        eye = new ModelRenderer(this);
        this.body.setRotationPoint(0.f, 0.f, 0.f);
        this.eye.setTextureOffset(0, 10).addBox(-3.0F, 7.0F, -4.0F, 6, 4, 1);
        this.body.setTextureOffset(34, 8).addBox(-4.f, 6.f, -3.f, 8, 12, 6);
        this.body.setTextureOffset(15, 10).addBox(-3.f, 9.f, 3.f, 6, 8, 3);
        this.body.setTextureOffset(26, 0).addBox(-3.f, 5.f, -3.f, 6, 1, 6);
        this.left_leg = new ModelRenderer(this);
        this.left_leg.setRotationPoint(-2.f, 18.f, 0.f);
        this.left_leg.setTextureOffset(0, 0).addBox(2.9f, 0.f, -1.5f, 3, 6, 3, 0.f);
        this.right_leg = new ModelRenderer(this);
        this.right_leg.setRotationPoint(2.f, 18.f, 0.f);
        this.right_leg.setTextureOffset(13, 0).addBox(-5.9f, 0.f, -1.5f, 3, 6, 3);


        this.smallArms = p_i46304_2_;
        this.bipedDeadmau5Head = new ModelRenderer(this, 24, 0);
        this.bipedDeadmau5Head.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, p_i46304_1_);
        this.bipedCape = new ModelRenderer(this, 0, 0);
        this.bipedCape.setTextureSize(64, 32);
        this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, p_i46304_1_);

        if (p_i46304_2_) {
            this.bipedLeftArm = new ModelRenderer(this, 32, 48);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, p_i46304_1_);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
            this.bipedRightArm = new ModelRenderer(this, 40, 16);
            this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, p_i46304_1_);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
            this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, p_i46304_1_ + 0.25F);
            this.bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
            this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
            this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, p_i46304_1_ + 0.25F);
            this.bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
        } else {
            this.bipedLeftArm = new ModelRenderer(this, 32, 48);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, p_i46304_1_);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, p_i46304_1_ + 0.25F);
            this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
            this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, p_i46304_1_ + 0.25F);
            this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
        }

        this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i46304_1_);
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i46304_1_ + 0.25F);
        this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i46304_1_ + 0.25F);
        this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.bipedBodyWear = new ModelRenderer(this, 16, 32);
        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, p_i46304_1_ + 0.25F);
        this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
    }


    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        if (CustomModel.enabled) {
            switch (CustomModel.model.getMode()) {
                case "Among Us":
                    bipedHead.rotateAngleY = netHeadYaw * .017453292f;
                    bipedHead.rotateAngleX = headPitch * .017453292f;

                    bipedBody.rotateAngleY = 0;
                    bipedRightArm.rotationPointZ = 0;
                    bipedRightArm.rotationPointX = -5.f;
                    bipedLeftArm.rotationPointZ = 0;
                    bipedLeftArm.rotationPointX = 5.f;
                    float f = 1.f;

                    bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * .6662f + MathHelper.PI) * 2 * limbSwingAmount * .5f / f;
                    bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * .6662f) * 2.f * limbSwingAmount * .5f / f;
                    bipedRightArm.rotateAngleZ = 0.f;
                    bipedLeftArm.rotateAngleZ = 0.f;
                    right_leg.rotateAngleX = MathHelper.cos(limbSwing * .6662f) * 1.4f * limbSwingAmount / f;
                    left_leg.rotateAngleX = MathHelper.cos(limbSwing * .6662f + MathHelper.PI) * 1.4f * limbSwingAmount / f;
                    right_leg.rotateAngleY = 0.f;
                    left_leg.rotateAngleY = 0.f;
                    right_leg.rotateAngleZ = 0.f;
                    left_leg.rotateAngleZ = 0.f;
                    RenderUtil.color(CustomModel.getColor(entityIn).getRGB());
                    if (isChild) {
                        GlStateManager.scale(.5, .5, .5);
                        GlStateManager.translate(0, 24. * scale, 0);
                        body.render(scale);
                        left_leg.render(scale);
                        right_leg.render(scale);
                    } else {
                        GlStateManager.translate(0, -.9, 0);
                        GlStateManager.scale(1.8, 1.6, 1.6);
                        GlStateManager.translate(0, .14, 0);
                        body.render(scale);
                        RenderUtil.color(0xff00ffff);
                        eye.render(scale);
                        RenderUtil.color(CustomModel.getColor(entityIn).getRGB());
                        GlStateManager.translate(0, -.15, 0);
                        left_leg.render(scale);
                        right_leg.render(scale);
                        GlStateManager.color(1, 1, 1, 1);
                    }
                    GlStateManager.popMatrix();
                    break;
                case "Rabbit":
                    RenderUtil.color(0xFFFFFFFF);
                    GlStateManager.scale(1.25, 1.25, 1.25);
                    GlStateManager.translate(0, -.3, 0);
                    rabbitHead.rotateAngleX = bipedHead.rotateAngleX;
                    rabbitHead.rotateAngleY = bipedHead.rotateAngleY;
                    rabbitHead.rotateAngleZ = bipedHead.rotateAngleZ;
                    rabbitLarm.rotateAngleX = bipedLeftArm.rotateAngleX;
                    rabbitLarm.rotateAngleY = bipedLeftArm.rotateAngleY;
                    rabbitLarm.rotateAngleZ = bipedLeftArm.rotateAngleZ;
                    rabbitRarm.rotateAngleX = bipedRightArm.rotateAngleX;
                    rabbitRarm.rotateAngleY = bipedRightArm.rotateAngleY;
                    rabbitRarm.rotateAngleZ = bipedRightArm.rotateAngleZ;
                    rabbitRleg.rotateAngleX = bipedRightLeg.rotateAngleX;
                    rabbitRleg.rotateAngleY = bipedRightLeg.rotateAngleY;
                    rabbitRleg.rotateAngleZ = bipedRightLeg.rotateAngleZ;
                    rabbitLleg.rotateAngleX = bipedLeftLeg.rotateAngleX;
                    rabbitLleg.rotateAngleY = bipedLeftLeg.rotateAngleY;
                    rabbitLleg.rotateAngleZ = bipedLeftLeg.rotateAngleZ;
                    rabbitBone.render(scale);
                    GlStateManager.popMatrix();
                    break;
            }
        } else {
            super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            if (this.isChild) {
                float f = 2.0F;
                GlStateManager.scale(1.0F / f, 1.0F / f, 1.0F / f);
                GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
                this.bipedLeftLegwear.render(scale);
                this.bipedRightLegwear.render(scale);
                this.bipedLeftArmwear.render(scale);
                this.bipedRightArmwear.render(scale);
                this.bipedBodyWear.render(scale);
            } else {
                if (entityIn.isSneaking()) {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
                }

                this.bipedLeftLegwear.render(scale);
                this.bipedRightLegwear.render(scale);
                this.bipedLeftArmwear.render(scale);
                this.bipedRightArmwear.render(scale);
                this.bipedBodyWear.render(scale);
            }

            GlStateManager.popMatrix();
        }
    }

    public void renderDeadmau5Head(float p_178727_1_) {
        copyModelAngles(this.bipedHead, this.bipedDeadmau5Head);
        this.bipedDeadmau5Head.rotationPointX = 0.0F;
        this.bipedDeadmau5Head.rotationPointY = 0.0F;
        this.bipedDeadmau5Head.render(p_178727_1_);
    }

    public void renderCape(float p_178728_1_) {
        this.bipedCape.render(p_178728_1_);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
        copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
        copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
        copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
        copyModelAngles(this.bipedBody, this.bipedBodyWear);
    }

    public void renderRightArm() {
        this.bipedRightArm.render(0.0625F);
        this.bipedRightArmwear.render(0.0625F);
    }

    public void renderLeftArm() {
        this.bipedLeftArm.render(0.0625F);
        this.bipedLeftArmwear.render(0.0625F);
    }

    public void setInvisible(boolean invisible) {
        super.setInvisible(invisible);
        this.bipedLeftArmwear.showModel = invisible;
        this.bipedRightArmwear.showModel = invisible;
        this.bipedLeftLegwear.showModel = invisible;
        this.bipedRightLegwear.showModel = invisible;
        this.bipedBodyWear.showModel = invisible;
        this.bipedCape.showModel = invisible;
        this.bipedDeadmau5Head.showModel = invisible;
    }

    public void postRenderArm(float scale) {
        if (this.smallArms) {
            ++this.bipedRightArm.rotationPointX;
            this.bipedRightArm.postRender(scale);
            --this.bipedRightArm.rotationPointX;
        } else {
            this.bipedRightArm.postRender(scale);
        }
    }
}
