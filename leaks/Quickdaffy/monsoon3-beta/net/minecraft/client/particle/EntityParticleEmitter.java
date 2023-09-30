/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityParticleEmitter
extends EntityFX {
    private Entity attachedEntity;
    private int age;
    private int lifetime;
    private EnumParticleTypes particleTypes;

    public EntityParticleEmitter(World worldIn, Entity p_i46279_2_, EnumParticleTypes particleTypesIn) {
        super(worldIn, p_i46279_2_.posX, p_i46279_2_.getEntityBoundingBox().minY + (double)(p_i46279_2_.height / 2.0f), p_i46279_2_.posZ, p_i46279_2_.motionX, p_i46279_2_.motionY, p_i46279_2_.motionZ);
        this.attachedEntity = p_i46279_2_;
        this.lifetime = 3;
        this.particleTypes = particleTypesIn;
        this.onUpdate();
    }

    @Override
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
    }

    @Override
    public void onUpdate() {
        for (int i = 0; i < 16; ++i) {
            double d2;
            double d1;
            double d0 = this.rand.nextFloat() * 2.0f - 1.0f;
            if (!(d0 * d0 + (d1 = (double)(this.rand.nextFloat() * 2.0f - 1.0f)) * d1 + (d2 = (double)(this.rand.nextFloat() * 2.0f - 1.0f)) * d2 <= 1.0)) continue;
            double d3 = this.attachedEntity.posX + d0 * (double)this.attachedEntity.width / 4.0;
            double d4 = this.attachedEntity.getEntityBoundingBox().minY + (double)(this.attachedEntity.height / 2.0f) + d1 * (double)this.attachedEntity.height / 4.0;
            double d5 = this.attachedEntity.posZ + d2 * (double)this.attachedEntity.width / 4.0;
            this.worldObj.spawnParticle(this.particleTypes, false, d3, d4, d5, d0, d1 + 0.2, d2, new int[0]);
        }
        ++this.age;
        if (this.age >= this.lifetime) {
            this.setDead();
        }
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}

