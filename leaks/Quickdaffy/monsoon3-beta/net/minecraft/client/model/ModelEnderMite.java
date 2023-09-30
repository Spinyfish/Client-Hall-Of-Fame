/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelEnderMite
extends ModelBase {
    private static final int[][] field_178716_a = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
    private static final int[][] field_178714_b = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
    private static final int field_178715_c = field_178716_a.length;
    private final ModelRenderer[] field_178713_d = new ModelRenderer[field_178715_c];

    public ModelEnderMite() {
        float f = -3.5f;
        for (int i = 0; i < this.field_178713_d.length; ++i) {
            this.field_178713_d[i] = new ModelRenderer(this, field_178714_b[i][0], field_178714_b[i][1]);
            this.field_178713_d[i].addBox((float)field_178716_a[i][0] * -0.5f, 0.0f, (float)field_178716_a[i][2] * -0.5f, field_178716_a[i][0], field_178716_a[i][1], field_178716_a[i][2]);
            this.field_178713_d[i].setRotationPoint(0.0f, 24 - field_178716_a[i][1], f);
            if (i >= this.field_178713_d.length - 1) continue;
            f += (float)(field_178716_a[i][2] + field_178716_a[i + 1][2]) * 0.5f;
        }
    }

    @Override
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        for (int i = 0; i < this.field_178713_d.length; ++i) {
            this.field_178713_d[i].render(scale);
        }
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn) {
        for (int i = 0; i < this.field_178713_d.length; ++i) {
            this.field_178713_d[i].rotateAngleY = MathHelper.cos(p_78087_3_ * 0.9f + (float)i * 0.15f * (float)Math.PI) * (float)Math.PI * 0.01f * (float)(1 + Math.abs(i - 2));
            this.field_178713_d[i].rotationPointX = MathHelper.sin(p_78087_3_ * 0.9f + (float)i * 0.15f * (float)Math.PI) * (float)Math.PI * 0.1f * (float)Math.abs(i - 2);
        }
    }
}

