/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;

public class ModelIronGolem
extends ModelBase {
    public ModelRenderer ironGolemHead;
    public ModelRenderer ironGolemBody;
    public ModelRenderer ironGolemRightArm;
    public ModelRenderer ironGolemLeftArm;
    public ModelRenderer ironGolemLeftLeg;
    public ModelRenderer ironGolemRightLeg;

    public ModelIronGolem() {
        this(0.0f);
    }

    public ModelIronGolem(float p_i1161_1_) {
        this(p_i1161_1_, -7.0f);
    }

    public ModelIronGolem(float p_i46362_1_, float p_i46362_2_) {
        int i = 128;
        int j = 128;
        this.ironGolemHead = new ModelRenderer(this).setTextureSize(i, j);
        this.ironGolemHead.setRotationPoint(0.0f, 0.0f + p_i46362_2_, -2.0f);
        this.ironGolemHead.setTextureOffset(0, 0).addBox(-4.0f, -12.0f, -5.5f, 8, 10, 8, p_i46362_1_);
        this.ironGolemHead.setTextureOffset(24, 0).addBox(-1.0f, -5.0f, -7.5f, 2, 4, 2, p_i46362_1_);
        this.ironGolemBody = new ModelRenderer(this).setTextureSize(i, j);
        this.ironGolemBody.setRotationPoint(0.0f, 0.0f + p_i46362_2_, 0.0f);
        this.ironGolemBody.setTextureOffset(0, 40).addBox(-9.0f, -2.0f, -6.0f, 18, 12, 11, p_i46362_1_);
        this.ironGolemBody.setTextureOffset(0, 70).addBox(-4.5f, 10.0f, -3.0f, 9, 5, 6, p_i46362_1_ + 0.5f);
        this.ironGolemRightArm = new ModelRenderer(this).setTextureSize(i, j);
        this.ironGolemRightArm.setRotationPoint(0.0f, -7.0f, 0.0f);
        this.ironGolemRightArm.setTextureOffset(60, 21).addBox(-13.0f, -2.5f, -3.0f, 4, 30, 6, p_i46362_1_);
        this.ironGolemLeftArm = new ModelRenderer(this).setTextureSize(i, j);
        this.ironGolemLeftArm.setRotationPoint(0.0f, -7.0f, 0.0f);
        this.ironGolemLeftArm.setTextureOffset(60, 58).addBox(9.0f, -2.5f, -3.0f, 4, 30, 6, p_i46362_1_);
        this.ironGolemLeftLeg = new ModelRenderer(this, 0, 22).setTextureSize(i, j);
        this.ironGolemLeftLeg.setRotationPoint(-4.0f, 18.0f + p_i46362_2_, 0.0f);
        this.ironGolemLeftLeg.setTextureOffset(37, 0).addBox(-3.5f, -3.0f, -3.0f, 6, 16, 5, p_i46362_1_);
        this.ironGolemRightLeg = new ModelRenderer(this, 0, 22).setTextureSize(i, j);
        this.ironGolemRightLeg.mirror = true;
        this.ironGolemRightLeg.setTextureOffset(60, 0).setRotationPoint(5.0f, 18.0f + p_i46362_2_, 0.0f);
        this.ironGolemRightLeg.addBox(-3.5f, -3.0f, -3.0f, 6, 16, 5, p_i46362_1_);
    }

    @Override
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.ironGolemHead.render(scale);
        this.ironGolemBody.render(scale);
        this.ironGolemLeftLeg.render(scale);
        this.ironGolemRightLeg.render(scale);
        this.ironGolemRightArm.render(scale);
        this.ironGolemLeftArm.render(scale);
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn) {
        this.ironGolemHead.rotateAngleY = p_78087_4_ / 57.295776f;
        this.ironGolemHead.rotateAngleX = p_78087_5_ / 57.295776f;
        this.ironGolemLeftLeg.rotateAngleX = -1.5f * this.func_78172_a(p_78087_1_, 13.0f) * p_78087_2_;
        this.ironGolemRightLeg.rotateAngleX = 1.5f * this.func_78172_a(p_78087_1_, 13.0f) * p_78087_2_;
        this.ironGolemLeftLeg.rotateAngleY = 0.0f;
        this.ironGolemRightLeg.rotateAngleY = 0.0f;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime) {
        EntityIronGolem entityirongolem = (EntityIronGolem)entitylivingbaseIn;
        int i = entityirongolem.getAttackTimer();
        if (i > 0) {
            this.ironGolemRightArm.rotateAngleX = -2.0f + 1.5f * this.func_78172_a((float)i - partialTickTime, 10.0f);
            this.ironGolemLeftArm.rotateAngleX = -2.0f + 1.5f * this.func_78172_a((float)i - partialTickTime, 10.0f);
        } else {
            int j = entityirongolem.getHoldRoseTick();
            if (j > 0) {
                this.ironGolemRightArm.rotateAngleX = -0.8f + 0.025f * this.func_78172_a(j, 70.0f);
                this.ironGolemLeftArm.rotateAngleX = 0.0f;
            } else {
                this.ironGolemRightArm.rotateAngleX = (-0.2f + 1.5f * this.func_78172_a(p_78086_2_, 13.0f)) * p_78086_3_;
                this.ironGolemLeftArm.rotateAngleX = (-0.2f - 1.5f * this.func_78172_a(p_78086_2_, 13.0f)) * p_78086_3_;
            }
        }
    }

    private float func_78172_a(float p_78172_1_, float p_78172_2_) {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5f) - p_78172_2_ * 0.25f) / (p_78172_2_ * 0.25f);
    }
}

