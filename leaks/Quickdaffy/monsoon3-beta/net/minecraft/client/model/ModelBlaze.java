/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBlaze
extends ModelBase {
    private ModelRenderer[] blazeSticks = new ModelRenderer[12];
    private ModelRenderer blazeHead;

    public ModelBlaze() {
        for (int i = 0; i < this.blazeSticks.length; ++i) {
            this.blazeSticks[i] = new ModelRenderer(this, 0, 16);
            this.blazeSticks[i].addBox(0.0f, 0.0f, 0.0f, 2, 8, 2);
        }
        this.blazeHead = new ModelRenderer(this, 0, 0);
        this.blazeHead.addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
    }

    @Override
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.blazeHead.render(scale);
        for (int i = 0; i < this.blazeSticks.length; ++i) {
            this.blazeSticks[i].render(scale);
        }
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn) {
        float f = p_78087_3_ * (float)Math.PI * -0.1f;
        for (int i = 0; i < 4; ++i) {
            this.blazeSticks[i].rotationPointY = -2.0f + MathHelper.cos(((float)(i * 2) + p_78087_3_) * 0.25f);
            this.blazeSticks[i].rotationPointX = MathHelper.cos(f) * 9.0f;
            this.blazeSticks[i].rotationPointZ = MathHelper.sin(f) * 9.0f;
            f += 1.0f;
        }
        f = 0.7853982f + p_78087_3_ * (float)Math.PI * 0.03f;
        for (int j = 4; j < 8; ++j) {
            this.blazeSticks[j].rotationPointY = 2.0f + MathHelper.cos(((float)(j * 2) + p_78087_3_) * 0.25f);
            this.blazeSticks[j].rotationPointX = MathHelper.cos(f) * 7.0f;
            this.blazeSticks[j].rotationPointZ = MathHelper.sin(f) * 7.0f;
            f += 1.0f;
        }
        f = 0.47123894f + p_78087_3_ * (float)Math.PI * -0.05f;
        for (int k = 8; k < 12; ++k) {
            this.blazeSticks[k].rotationPointY = 11.0f + MathHelper.cos(((float)k * 1.5f + p_78087_3_) * 0.5f);
            this.blazeSticks[k].rotationPointX = MathHelper.cos(f) * 5.0f;
            this.blazeSticks[k].rotationPointZ = MathHelper.sin(f) * 5.0f;
            f += 1.0f;
        }
        this.blazeHead.rotateAngleY = p_78087_4_ / 57.295776f;
        this.blazeHead.rotateAngleX = p_78087_5_ / 57.295776f;
    }
}

