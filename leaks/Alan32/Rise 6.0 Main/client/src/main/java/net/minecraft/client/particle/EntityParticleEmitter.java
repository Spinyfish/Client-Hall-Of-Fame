package net.minecraft.client.particle;

import com.alan.clients.util.interfaces.InstanceAccess;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityParticleEmitter extends EntityFX implements InstanceAccess {
    private final Entity attachedEntity;
    private int age;
    private final int lifetime;
    private final EnumParticleTypes particleTypes;

    public EntityParticleEmitter(final World worldIn, final Entity p_i46279_2_, final EnumParticleTypes particleTypesIn) {
        super(worldIn, p_i46279_2_.posX, p_i46279_2_.getEntityBoundingBox().minY + (double) (p_i46279_2_.height / 2.0F), p_i46279_2_.posZ, p_i46279_2_.motionX, p_i46279_2_.motionY, p_i46279_2_.motionZ);
        this.attachedEntity = p_i46279_2_;
        this.lifetime = 3;
        this.particleTypes = particleTypesIn;
        this.onUpdate();
    }

    /**
     * Renders the particle
     *
     * @param worldRendererIn The WorldRenderer instance
     */
    public void renderParticle(final WorldRenderer worldRendererIn, final Entity entityIn, final float partialTicks, final float p_180434_4_, final float p_180434_5_, final float p_180434_6_, final float p_180434_7_, final float p_180434_8_) {
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        for (int i = 0; i < 16; ++i) {
            final double d0 = this.rand.nextFloat() * 2.0F - 1.0F;
            final double d1 = this.rand.nextFloat() * 2.0F - 1.0F;
            final double d2 = this.rand.nextFloat() * 2.0F - 1.0F;

            if (d0 * d0 + d1 * d1 + d2 * d2 <= 1.0D) {
                final double d3 = this.attachedEntity.posX + d0 * (double) this.attachedEntity.width / 4.0D;
                final double d4 = this.attachedEntity.getEntityBoundingBox().minY + (double) (this.attachedEntity.height / 2.0F) + d1 * (double) this.attachedEntity.height / 4.0D;
                final double d5 = this.attachedEntity.posZ + d2 * (double) this.attachedEntity.width / 4.0D;
                this.worldObj.spawnParticle(this.particleTypes, false, d3, d4, d5, d0, d1 + 0.2D, d2);
            }
        }

        ++this.age;

        if (this.age >= this.lifetime) {
            this.setDead();
        }
    }

    public int getFXLayer() {
        return 3;
    }
}
