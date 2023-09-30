/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelEnderman
extends ModelBiped {
    public boolean isCarrying;
    public boolean isAttacking;

    public ModelEnderman(float p_i46305_1_) {
        super(0.0f, -14.0f, 64, 32);
        float f = -14.0f;
        this.bipedHeadwear = new ModelRenderer(this, 0, 16);
        this.bipedHeadwear.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, p_i46305_1_ - 0.5f);
        this.bipedHeadwear.setRotationPoint(0.0f, 0.0f + f, 0.0f);
        this.bipedBody = new ModelRenderer(this, 32, 16);
        this.bipedBody.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, p_i46305_1_);
        this.bipedBody.setRotationPoint(0.0f, 0.0f + f, 0.0f);
        this.bipedRightArm = new ModelRenderer(this, 56, 0);
        this.bipedRightArm.addBox(-1.0f, -2.0f, -1.0f, 2, 30, 2, p_i46305_1_);
        this.bipedRightArm.setRotationPoint(-3.0f, 2.0f + f, 0.0f);
        this.bipedLeftArm = new ModelRenderer(this, 56, 0);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0f, -2.0f, -1.0f, 2, 30, 2, p_i46305_1_);
        this.bipedLeftArm.setRotationPoint(5.0f, 2.0f + f, 0.0f);
        this.bipedRightLeg = new ModelRenderer(this, 56, 0);
        this.bipedRightLeg.addBox(-1.0f, 0.0f, -1.0f, 2, 30, 2, p_i46305_1_);
        this.bipedRightLeg.setRotationPoint(-2.0f, 12.0f + f, 0.0f);
        this.bipedLeftLeg = new ModelRenderer(this, 56, 0);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-1.0f, 0.0f, -1.0f, 2, 30, 2, p_i46305_1_);
        this.bipedLeftLeg.setRotationPoint(2.0f, 12.0f + f, 0.0f);
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn) {
        super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entityIn);
        this.bipedHead.showModel = true;
        float f = -14.0f;
        this.bipedBody.rotateAngleX = 0.0f;
        this.bipedBody.rotationPointY = f;
        this.bipedBody.rotationPointZ = -0.0f;
        this.bipedRightLeg.rotateAngleX -= 0.0f;
        this.bipedLeftLeg.rotateAngleX -= 0.0f;
        this.bipedRightArm.rotateAngleX = (float)((double)this.bipedRightArm.rotateAngleX * 0.5);
        this.bipedLeftArm.rotateAngleX = (float)((double)this.bipedLeftArm.rotateAngleX * 0.5);
        this.bipedRightLeg.rotateAngleX = (float)((double)this.bipedRightLeg.rotateAngleX * 0.5);
        this.bipedLeftLeg.rotateAngleX = (float)((double)this.bipedLeftLeg.rotateAngleX * 0.5);
        float f1 = 0.4f;
        if (this.bipedRightArm.rotateAngleX > f1) {
            this.bipedRightArm.rotateAngleX = f1;
        }
        if (this.bipedLeftArm.rotateAngleX > f1) {
            this.bipedLeftArm.rotateAngleX = f1;
        }
        if (this.bipedRightArm.rotateAngleX < -f1) {
            this.bipedRightArm.rotateAngleX = -f1;
        }
        if (this.bipedLeftArm.rotateAngleX < -f1) {
            this.bipedLeftArm.rotateAngleX = -f1;
        }
        if (this.bipedRightLeg.rotateAngleX > f1) {
            this.bipedRightLeg.rotateAngleX = f1;
        }
        if (this.bipedLeftLeg.rotateAngleX > f1) {
            this.bipedLeftLeg.rotateAngleX = f1;
        }
        if (this.bipedRightLeg.rotateAngleX < -f1) {
            this.bipedRightLeg.rotateAngleX = -f1;
        }
        if (this.bipedLeftLeg.rotateAngleX < -f1) {
            this.bipedLeftLeg.rotateAngleX = -f1;
        }
        if (this.isCarrying) {
            this.bipedRightArm.rotateAngleX = -0.5f;
            this.bipedLeftArm.rotateAngleX = -0.5f;
            this.bipedRightArm.rotateAngleZ = 0.05f;
            this.bipedLeftArm.rotateAngleZ = -0.05f;
        }
        this.bipedRightArm.rotationPointZ = 0.0f;
        this.bipedLeftArm.rotationPointZ = 0.0f;
        this.bipedRightLeg.rotationPointZ = 0.0f;
        this.bipedLeftLeg.rotationPointZ = 0.0f;
        this.bipedRightLeg.rotationPointY = 9.0f + f;
        this.bipedLeftLeg.rotationPointY = 9.0f + f;
        this.bipedHead.rotationPointZ = -0.0f;
        this.bipedHead.rotationPointY = f + 1.0f;
        this.bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX;
        this.bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY;
        this.bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ;
        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
        this.bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ;
        if (this.isAttacking) {
            float f2 = 1.0f;
            this.bipedHead.rotationPointY -= f2 * 5.0f;
        }
    }
}

