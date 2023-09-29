package net.minecraft.client.model;

import alos.stella.Stella;
import alos.stella.module.modules.visual.CustomModel;
import alos.stella.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class ModelPlayer extends ModelBiped
{
    public ModelRenderer bipedLeftArmwear;
    public ModelRenderer bipedRightArmwear;
    public ModelRenderer bipedLeftLegwear;
    public ModelRenderer bipedRightLegwear;
    public ModelRenderer bipedBodyWear;
    private ModelRenderer bipedCape;
    private ModelRenderer bipedDeadmau5Head;
    private final ModelRenderer rabbitHead;
    private final ModelRenderer rabbitRleg;
    private final ModelRenderer rabbitBone;
    private final ModelRenderer rabbitLarm;
    private final ModelRenderer rabbitRarm;
    private final ModelRenderer rabbitLleg;
    private final ModelRenderer right_leg;
    private final ModelRenderer left_leg;
    private final ModelRenderer body;
    private final ModelRenderer eye;
    private boolean smallArms;

    public ModelPlayer(float p_i46304_1_, boolean p_i46304_2_)
    {
        super(p_i46304_1_, 0.0F, 64, 64);
        this.smallArms = p_i46304_2_;
        this.bipedDeadmau5Head = new ModelRenderer(this, 24, 0);
        this.bipedDeadmau5Head.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, p_i46304_1_);
        this.bipedCape = new ModelRenderer(this, 0, 0);
        this.bipedCape.setTextureSize(64, 32);
        this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, p_i46304_1_);

        if (p_i46304_2_)
        {
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
        }
        else
        {
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
        (this.rabbitBone = new ModelRenderer(this)).setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rabbitBone.cubeList.add(new ModelBox(this.rabbitBone, 28, 45, -5.0F, -13.0F, -5.0F, 10, 11, 8, 0.0F, false));
        (this.rabbitRleg = new ModelRenderer(this)).setRotationPoint(-3.0F, -2.0F, -1.0F);
        this.rabbitBone.addChild(this.rabbitRleg);
        this.rabbitRleg.cubeList.add(new ModelBox(this.rabbitRleg, 0, 0, -2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F, false));
        (this.rabbitLarm = new ModelRenderer(this)).setRotationPoint(5.0F, -13.0F, -1.0F);
        setRotationAngle(this.rabbitLarm, 0.0F, 0.0F, -0.0873F);
        this.rabbitBone.addChild(this.rabbitLarm);
        this.rabbitLarm.cubeList.add(new ModelBox(this.rabbitLarm, 0, 0, 0.0F, 0.0F, -2.0F, 2, 8, 4, 0.0F, false));
        (this.rabbitRarm = new ModelRenderer(this)).setRotationPoint(-5.0F, -13.0F, -1.0F);
        setRotationAngle(this.rabbitRarm, 0.0F, 0.0F, 0.0873F);
        this.rabbitBone.addChild(this.rabbitRarm);
        this.rabbitRarm.cubeList.add(new ModelBox(this.rabbitRarm, 0, 0, -2.0F, 0.0F, -2.0F, 2, 8, 4, 0.0F, false));
        (this.rabbitLleg = new ModelRenderer(this)).setRotationPoint(3.0F, -2.0F, -1.0F);
        this.rabbitBone.addChild(this.rabbitLleg);
        this.rabbitLleg.cubeList.add(new ModelBox(this.rabbitLleg, 0, 0, -2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F, false));
        (this.rabbitHead = new ModelRenderer(this)).setRotationPoint(0.0F, -14.0F, -1.0F);
        this.rabbitBone.addChild(this.rabbitHead);
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 0, 0, -3.0F, 0.0F, -4.0F, 6, 1, 6, 0.0F, false));
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 56, 0, -5.0F, -9.0F, -5.0F, 2, 3, 2, 0.0F, false));
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 56, 0, 3.0F, -9.0F, -5.0F, 2, 3, 2, 0.0F, true));
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 0, 45, -4.0F, -11.0F, -4.0F, 8, 11, 8, 0.0F, false));
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 46, 0, 1.0F, -20.0F, 0.0F, 3, 9, 1, 0.0F, false));
        this.rabbitHead.cubeList.add(new ModelBox(this.rabbitHead, 46, 0, -4.0F, -20.0F, 0.0F, 3, 9, 1, 0.0F, false));
        this.right_leg = new ModelRenderer(this);
        this.right_leg.setRotationPoint(2.0F, 18.0F, 0.0F);
        this.right_leg.setTextureOffset(13, 0).addBox(-5.9F, 0.0F, -1.5F, 3, 6, 3);
        this.left_leg = new ModelRenderer(this);
        this.left_leg.setRotationPoint(-2.0F, 18.0F, 0.0F);
        this.left_leg.setTextureOffset(0, 0).addBox(2.9F, 0.0F, -1.5F, 3, 6, 3, 0.0F);
        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.setTextureOffset(34, 8).addBox(-4.0F, 6.0F, -3.0F, 8, 12, 6);
        this.body.setTextureOffset(15, 10).addBox(-3.0F, 9.0F, 3.0F, 6, 8, 3);
        this.body.setTextureOffset(26, 0).addBox(-3.0F, 5.0F, -3.0F, 6, 1, 6);
        this.eye = new ModelRenderer(this);
        this.eye.setTextureOffset(0, 10).addBox(-3.0F, 7.0F, -4.0F, 6, 4, 1);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        GlStateManager.pushMatrix();
        if ((!Stella.moduleManager.getModule(CustomModel.class).onlyMe.get() || entityIn == (Minecraft.getMinecraft()).thePlayer) && Stella.moduleManager.getModule(CustomModel.class).getState() && !CustomModel.modelMode.isMode("None")) {
            if (Stella.moduleManager.getModule(CustomModel.class).getState() && CustomModel.modelMode.get().equals("Rabbit")) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.25D, 1.25D, 1.25D);
                GlStateManager.translate(0.0D, -0.3D, 0.0D);
                this.rabbitHead.rotateAngleX = this.bipedHead.rotateAngleX;
                this.rabbitHead.rotateAngleY = this.bipedHead.rotateAngleY;
                this.rabbitHead.rotateAngleZ = this.bipedHead.rotateAngleZ;
                this.rabbitLarm.rotateAngleX = this.bipedLeftArm.rotateAngleX;
                this.rabbitLarm.rotateAngleY = this.bipedLeftArm.rotateAngleY;
                this.rabbitLarm.rotateAngleZ = this.bipedLeftArm.rotateAngleZ;
                this.rabbitRarm.rotateAngleX = this.bipedRightArm.rotateAngleX;
                this.rabbitRarm.rotateAngleY = this.bipedRightArm.rotateAngleY;
                this.rabbitRarm.rotateAngleZ = this.bipedRightArm.rotateAngleZ;
                this.rabbitRleg.rotateAngleX = this.bipedRightLeg.rotateAngleX;
                this.rabbitRleg.rotateAngleY = this.bipedRightLeg.rotateAngleY;
                this.rabbitRleg.rotateAngleZ = this.bipedRightLeg.rotateAngleZ;
                this.rabbitLleg.rotateAngleX = this.bipedLeftLeg.rotateAngleX;
                this.rabbitLleg.rotateAngleY = this.bipedLeftLeg.rotateAngleY;
                this.rabbitLleg.rotateAngleZ = this.bipedLeftLeg.rotateAngleZ;
                this.rabbitBone.render(scale);
                GlStateManager.popMatrix();
            }else if (CustomModel.modelMode.get().equals("Amogus")) {
                this.bipedHead.rotateAngleY = p_78088_5_ * 0.017453292F;
                this.bipedHead.rotateAngleX = p_78088_6_ * 0.017453292F;
                this.bipedBody.rotateAngleY = 0.0F;
                this.bipedRightArm.rotationPointZ = 0.0F;
                this.bipedRightArm.rotationPointX = -5.0F;
                this.bipedLeftArm.rotationPointZ = 0.0F;
                this.bipedLeftArm.rotationPointX = 5.0F;
                float f = 1.0F;
                if (f < 1.0F)
                    f = 1.0F;
                this.bipedRightArm.rotateAngleX = MathHelper.cos(p_78088_2_ * 0.6662F + 3.1415927F) * 2.0F * p_78088_3_ * 0.5F / f;
                this.bipedLeftArm.rotateAngleX = MathHelper.cos(p_78088_2_ * 0.6662F) * 2.0F * p_78088_3_ * 0.5F / f;
                this.bipedRightArm.rotateAngleZ = 0.0F;
                this.bipedLeftArm.rotateAngleZ = 0.0F;
                this.right_leg.rotateAngleX = MathHelper.cos(p_78088_2_ * 0.6662F) * 1.4F * p_78088_3_ / f;
                this.left_leg.rotateAngleX = MathHelper.cos(p_78088_2_ * 0.6662F + 3.1415927F) * 1.4F * p_78088_3_ / f;
                this.right_leg.rotateAngleY = 0.0F;
                this.left_leg.rotateAngleY = 0.0F;
                this.right_leg.rotateAngleZ = 0.0F;
                this.left_leg.rotateAngleZ = 0.0F;
                int bodyCustomColor = new Color(CustomModel.r.get(), CustomModel.g.get(), CustomModel.b.get()).getRGB();
                int bodyColor = 0;
                switch (CustomModel.bodyColor.get()) {
                    case "Client":
                        bodyColor = Color.magenta.getRGB();
                        break;
                    case "Custom":
                        bodyColor = bodyCustomColor;
                        break;
                    case "Rainbow":
                        bodyColor = ColorUtils.rainbow(30, 1.0F, 1.0F).getRGB();
                        break;
                }
                int eyeCustomColor = new Color(CustomModel.r.get(), CustomModel.g.get(), CustomModel.b.get()).getRGB();
                int eyeColor = 0;
                switch (CustomModel.eyeColor.get()) {
                    case "Client":
                        eyeColor = Color.magenta.getRGB();
                        break;
                    case "Custom":
                        eyeColor = eyeCustomColor;
                        break;
                    case "Rainbow":
                        eyeColor = ColorUtils.rainbow(30, 1.0F, 1.0F).getRGB();
                        break;
                }
                int legsCustomColor = new Color(CustomModel.r.get(), CustomModel.g.get(), CustomModel.b.get()).getRGB();
                int legsColor = 0;
                switch (CustomModel.legsColor.get()) {
                    case "Client":
                        legsColor = Color.magenta.getRGB();
                        break;
                    case "Custom":
                        legsColor = legsCustomColor;
                        break;
                    case "Rainbow":
                        legsColor = ColorUtils.rainbow(30, 1.0F, 1.0F).getRGB();
                        break;
                }
                if (this.isChild) {
                    GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
                    this.body.render(scale);
                    this.left_leg.render(scale);
                    this.right_leg.render(scale);
                } else {
                    GlStateManager.translate(0.0D, -0.8D, 0.0D);
                    GlStateManager.scale(1.8D, 1.6D, 1.6D);
                    alos.stella.utils.render.ColorUtils.color(bodyColor);
                    GlStateManager.translate(0.0D, 0.15D, 0.0D);
                    this.body.render(scale);
                    alos.stella.utils.render.ColorUtils.color(eyeColor);
                    this.eye.render(scale);
                    alos.stella.utils.render.ColorUtils.color(legsColor);
                    GlStateManager.translate(0.0D, -0.15D, 0.0D);
                    this.left_leg.render(scale);
                    this.right_leg.render(scale);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        } else {
            super.render(entityIn, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale);

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
        }

            GlStateManager.popMatrix();
    }

    public void renderDeadmau5Head(float p_178727_1_)
    {
        copyModelAngles(this.bipedHead, this.bipedDeadmau5Head);
        this.bipedDeadmau5Head.rotationPointX = 0.0F;
        this.bipedDeadmau5Head.rotationPointY = 0.0F;
        this.bipedDeadmau5Head.render(p_178727_1_);
    }

    public void renderCape(float p_178728_1_)
    {
        this.bipedCape.render(p_178728_1_);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
        copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
        copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
        copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
        copyModelAngles(this.bipedBody, this.bipedBodyWear);
    }

    public void renderRightArm()
    {
        this.bipedRightArm.render(0.0625F);
        this.bipedRightArmwear.render(0.0625F);
    }

    public void renderLeftArm()
    {
        this.bipedLeftArm.render(0.0625F);
        this.bipedLeftArmwear.render(0.0625F);
    }

    public void setInvisible(boolean invisible)
    {
        super.setInvisible(invisible);
        this.bipedLeftArmwear.showModel = invisible;
        this.bipedRightArmwear.showModel = invisible;
        this.bipedLeftLegwear.showModel = invisible;
        this.bipedRightLegwear.showModel = invisible;
        this.bipedBodyWear.showModel = invisible;
        this.bipedCape.showModel = invisible;
        this.bipedDeadmau5Head.showModel = invisible;
    }

    public void postRenderArm(float scale)
    {
        if (this.smallArms)
        {
            ++this.bipedRightArm.rotationPointX;
            this.bipedRightArm.postRender(scale);
            --this.bipedRightArm.rotationPointX;
        }
        else
        {
            this.bipedRightArm.postRender(scale);
        }
    }
    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
