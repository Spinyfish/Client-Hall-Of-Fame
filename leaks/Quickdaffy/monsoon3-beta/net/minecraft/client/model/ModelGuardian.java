/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ModelGuardian
extends ModelBase {
    private ModelRenderer guardianBody;
    private ModelRenderer guardianEye;
    private ModelRenderer[] guardianSpines;
    private ModelRenderer[] guardianTail;

    public ModelGuardian() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.guardianSpines = new ModelRenderer[12];
        this.guardianBody = new ModelRenderer(this);
        this.guardianBody.setTextureOffset(0, 0).addBox(-6.0f, 10.0f, -8.0f, 12, 12, 16);
        this.guardianBody.setTextureOffset(0, 28).addBox(-8.0f, 10.0f, -6.0f, 2, 12, 12);
        this.guardianBody.setTextureOffset(0, 28).addBox(6.0f, 10.0f, -6.0f, 2, 12, 12, true);
        this.guardianBody.setTextureOffset(16, 40).addBox(-6.0f, 8.0f, -6.0f, 12, 2, 12);
        this.guardianBody.setTextureOffset(16, 40).addBox(-6.0f, 22.0f, -6.0f, 12, 2, 12);
        for (int i = 0; i < this.guardianSpines.length; ++i) {
            this.guardianSpines[i] = new ModelRenderer(this, 0, 0);
            this.guardianSpines[i].addBox(-1.0f, -4.5f, -1.0f, 2, 9, 2);
            this.guardianBody.addChild(this.guardianSpines[i]);
        }
        this.guardianEye = new ModelRenderer(this, 8, 0);
        this.guardianEye.addBox(-1.0f, 15.0f, 0.0f, 2, 2, 1);
        this.guardianBody.addChild(this.guardianEye);
        this.guardianTail = new ModelRenderer[3];
        this.guardianTail[0] = new ModelRenderer(this, 40, 0);
        this.guardianTail[0].addBox(-2.0f, 14.0f, 7.0f, 4, 4, 8);
        this.guardianTail[1] = new ModelRenderer(this, 0, 54);
        this.guardianTail[1].addBox(0.0f, 14.0f, 0.0f, 3, 3, 7);
        this.guardianTail[2] = new ModelRenderer(this);
        this.guardianTail[2].setTextureOffset(41, 32).addBox(0.0f, 14.0f, 0.0f, 2, 2, 6);
        this.guardianTail[2].setTextureOffset(25, 19).addBox(1.0f, 10.5f, 3.0f, 1, 9, 9);
        this.guardianBody.addChild(this.guardianTail[0]);
        this.guardianTail[0].addChild(this.guardianTail[1]);
        this.guardianTail[1].addChild(this.guardianTail[2]);
    }

    public int func_178706_a() {
        return 54;
    }

    @Override
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.guardianBody.render(scale);
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn) {
        EntityGuardian entityguardian = (EntityGuardian)entityIn;
        float f = p_78087_3_ - (float)entityguardian.ticksExisted;
        this.guardianBody.rotateAngleY = p_78087_4_ / 57.295776f;
        this.guardianBody.rotateAngleX = p_78087_5_ / 57.295776f;
        float[] afloat = new float[]{1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f};
        float[] afloat1 = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f};
        float[] afloat2 = new float[]{0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f};
        float[] afloat3 = new float[]{0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f};
        float[] afloat4 = new float[]{-8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f};
        float[] afloat5 = new float[]{8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f};
        float f1 = (1.0f - entityguardian.func_175469_o(f)) * 0.55f;
        for (int i = 0; i < 12; ++i) {
            this.guardianSpines[i].rotateAngleX = (float)Math.PI * afloat[i];
            this.guardianSpines[i].rotateAngleY = (float)Math.PI * afloat1[i];
            this.guardianSpines[i].rotateAngleZ = (float)Math.PI * afloat2[i];
            this.guardianSpines[i].rotationPointX = afloat3[i] * (1.0f + MathHelper.cos(p_78087_3_ * 1.5f + (float)i) * 0.01f - f1);
            this.guardianSpines[i].rotationPointY = 16.0f + afloat4[i] * (1.0f + MathHelper.cos(p_78087_3_ * 1.5f + (float)i) * 0.01f - f1);
            this.guardianSpines[i].rotationPointZ = afloat5[i] * (1.0f + MathHelper.cos(p_78087_3_ * 1.5f + (float)i) * 0.01f - f1);
        }
        this.guardianEye.rotationPointZ = -8.25f;
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        if (entityguardian.hasTargetedEntity()) {
            entity = entityguardian.getTargetedEntity();
        }
        if (entity != null) {
            Vec3 vec3 = entity.getPositionEyes(0.0f);
            Vec3 vec31 = entityIn.getPositionEyes(0.0f);
            double d0 = vec3.y - vec31.y;
            this.guardianEye.rotationPointY = d0 > 0.0 ? 0.0f : 1.0f;
            Vec3 vec32 = entityIn.getLook(0.0f);
            vec32 = new Vec3(vec32.x, 0.0, vec32.z);
            Vec3 vec33 = new Vec3(vec31.x - vec3.x, 0.0, vec31.z - vec3.z).normalize().rotateYaw(1.5707964f);
            double d1 = vec32.dotProduct(vec33);
            this.guardianEye.rotationPointX = MathHelper.sqrt_float((float)Math.abs(d1)) * 2.0f * (float)Math.signum(d1);
        }
        this.guardianEye.showModel = true;
        float f2 = entityguardian.func_175471_a(f);
        this.guardianTail[0].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.05f;
        this.guardianTail[1].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.1f;
        this.guardianTail[1].rotationPointX = -1.5f;
        this.guardianTail[1].rotationPointY = 0.5f;
        this.guardianTail[1].rotationPointZ = 14.0f;
        this.guardianTail[2].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.15f;
        this.guardianTail[2].rotationPointX = 0.5f;
        this.guardianTail[2].rotationPointY = 0.5f;
        this.guardianTail[2].rotationPointZ = 6.0f;
    }
}

