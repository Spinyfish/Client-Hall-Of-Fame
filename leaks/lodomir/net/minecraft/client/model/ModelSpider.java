/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelSpider
extends ModelBase {
    public ModelRenderer spiderHead;
    public ModelRenderer spiderNeck;
    public ModelRenderer spiderBody;
    public ModelRenderer spiderLeg1;
    public ModelRenderer spiderLeg2;
    public ModelRenderer spiderLeg3;
    public ModelRenderer spiderLeg4;
    public ModelRenderer spiderLeg5;
    public ModelRenderer spiderLeg6;
    public ModelRenderer spiderLeg7;
    public ModelRenderer spiderLeg8;

    public ModelSpider() {
        float f = 0.0f;
        int i = 15;
        this.spiderHead = new ModelRenderer(this, 32, 4);
        this.spiderHead.addBox(-4.0f, -4.0f, -8.0f, 8, 8, 8, f);
        this.spiderHead.setRotationPoint(0.0f, i, -3.0f);
        this.spiderNeck = new ModelRenderer(this, 0, 0);
        this.spiderNeck.addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6, f);
        this.spiderNeck.setRotationPoint(0.0f, i, 0.0f);
        this.spiderBody = new ModelRenderer(this, 0, 12);
        this.spiderBody.addBox(-5.0f, -4.0f, -6.0f, 10, 8, 12, f);
        this.spiderBody.setRotationPoint(0.0f, i, 9.0f);
        this.spiderLeg1 = new ModelRenderer(this, 18, 0);
        this.spiderLeg1.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, f);
        this.spiderLeg1.setRotationPoint(-4.0f, i, 2.0f);
        this.spiderLeg2 = new ModelRenderer(this, 18, 0);
        this.spiderLeg2.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, f);
        this.spiderLeg2.setRotationPoint(4.0f, i, 2.0f);
        this.spiderLeg3 = new ModelRenderer(this, 18, 0);
        this.spiderLeg3.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, f);
        this.spiderLeg3.setRotationPoint(-4.0f, i, 1.0f);
        this.spiderLeg4 = new ModelRenderer(this, 18, 0);
        this.spiderLeg4.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, f);
        this.spiderLeg4.setRotationPoint(4.0f, i, 1.0f);
        this.spiderLeg5 = new ModelRenderer(this, 18, 0);
        this.spiderLeg5.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, f);
        this.spiderLeg5.setRotationPoint(-4.0f, i, 0.0f);
        this.spiderLeg6 = new ModelRenderer(this, 18, 0);
        this.spiderLeg6.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, f);
        this.spiderLeg6.setRotationPoint(4.0f, i, 0.0f);
        this.spiderLeg7 = new ModelRenderer(this, 18, 0);
        this.spiderLeg7.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, f);
        this.spiderLeg7.setRotationPoint(-4.0f, i, -1.0f);
        this.spiderLeg8 = new ModelRenderer(this, 18, 0);
        this.spiderLeg8.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, f);
        this.spiderLeg8.setRotationPoint(4.0f, i, -1.0f);
    }

    @Override
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.spiderHead.render(scale);
        this.spiderNeck.render(scale);
        this.spiderBody.render(scale);
        this.spiderLeg1.render(scale);
        this.spiderLeg2.render(scale);
        this.spiderLeg3.render(scale);
        this.spiderLeg4.render(scale);
        this.spiderLeg5.render(scale);
        this.spiderLeg6.render(scale);
        this.spiderLeg7.render(scale);
        this.spiderLeg8.render(scale);
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn) {
        this.spiderHead.rotateAngleY = p_78087_4_ / 57.295776f;
        this.spiderHead.rotateAngleX = p_78087_5_ / 57.295776f;
        float f = 0.7853982f;
        this.spiderLeg1.rotateAngleZ = -f;
        this.spiderLeg2.rotateAngleZ = f;
        this.spiderLeg3.rotateAngleZ = -f * 0.74f;
        this.spiderLeg4.rotateAngleZ = f * 0.74f;
        this.spiderLeg5.rotateAngleZ = -f * 0.74f;
        this.spiderLeg6.rotateAngleZ = f * 0.74f;
        this.spiderLeg7.rotateAngleZ = -f;
        this.spiderLeg8.rotateAngleZ = f;
        float f1 = -0.0f;
        float f2 = 0.3926991f;
        this.spiderLeg1.rotateAngleY = f2 * 2.0f + f1;
        this.spiderLeg2.rotateAngleY = -f2 * 2.0f - f1;
        this.spiderLeg3.rotateAngleY = f2 * 1.0f + f1;
        this.spiderLeg4.rotateAngleY = -f2 * 1.0f - f1;
        this.spiderLeg5.rotateAngleY = -f2 * 1.0f + f1;
        this.spiderLeg6.rotateAngleY = f2 * 1.0f - f1;
        this.spiderLeg7.rotateAngleY = -f2 * 2.0f + f1;
        this.spiderLeg8.rotateAngleY = f2 * 2.0f - f1;
        float f3 = -(MathHelper.cos(p_78087_1_ * 0.6662f * 2.0f + 0.0f) * 0.4f) * p_78087_2_;
        float f4 = -(MathHelper.cos(p_78087_1_ * 0.6662f * 2.0f + (float)Math.PI) * 0.4f) * p_78087_2_;
        float f5 = -(MathHelper.cos(p_78087_1_ * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * p_78087_2_;
        float f6 = -(MathHelper.cos(p_78087_1_ * 0.6662f * 2.0f + 4.712389f) * 0.4f) * p_78087_2_;
        float f7 = Math.abs(MathHelper.sin(p_78087_1_ * 0.6662f + 0.0f) * 0.4f) * p_78087_2_;
        float f8 = Math.abs(MathHelper.sin(p_78087_1_ * 0.6662f + (float)Math.PI) * 0.4f) * p_78087_2_;
        float f9 = Math.abs(MathHelper.sin(p_78087_1_ * 0.6662f + 1.5707964f) * 0.4f) * p_78087_2_;
        float f10 = Math.abs(MathHelper.sin(p_78087_1_ * 0.6662f + 4.712389f) * 0.4f) * p_78087_2_;
        this.spiderLeg1.rotateAngleY += f3;
        this.spiderLeg2.rotateAngleY += -f3;
        this.spiderLeg3.rotateAngleY += f4;
        this.spiderLeg4.rotateAngleY += -f4;
        this.spiderLeg5.rotateAngleY += f5;
        this.spiderLeg6.rotateAngleY += -f5;
        this.spiderLeg7.rotateAngleY += f6;
        this.spiderLeg8.rotateAngleY += -f6;
        this.spiderLeg1.rotateAngleZ += f7;
        this.spiderLeg2.rotateAngleZ += -f7;
        this.spiderLeg3.rotateAngleZ += f8;
        this.spiderLeg4.rotateAngleZ += -f8;
        this.spiderLeg5.rotateAngleZ += f9;
        this.spiderLeg6.rotateAngleZ += -f9;
        this.spiderLeg7.rotateAngleZ += f10;
        this.spiderLeg8.rotateAngleZ += -f10;
    }
}

