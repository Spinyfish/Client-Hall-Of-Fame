package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityParticleEmitter extends EntityFX
{
    private Entity attachedEntity;
    private int age;
    private int lifetime;
    private EnumParticleTypes particleTypes;

    public EntityParticleEmitter(World worldIn, Entity p_i46279_2_, EnumParticleTypes particleTypesIn)
    {
        super(worldIn, p_i46279_2_.posX, p_i46279_2_.getEntityBoundingBox().minY + (double)(p_i46279_2_.height / 2.0F), p_i46279_2_.posZ, p_i46279_2_.motionX, p_i46279_2_.motionY, p_i46279_2_.motionZ);
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
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {}

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        for (int var1 = 0; var1 < 16; ++var1)
        {
            double var2 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            double var4 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            double var6 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);

            if (var2 * var2 + var4 * var4 + var6 * var6 <= 1.0D)
            {
                double var8 = this.attachedEntity.posX + var2 * (double)this.attachedEntity.width / 4.0D;
                double var10 = this.attachedEntity.getEntityBoundingBox().minY + (double)(this.attachedEntity.height / 2.0F) + var4 * (double)this.attachedEntity.height / 4.0D;
                double var12 = this.attachedEntity.posZ + var6 * (double)this.attachedEntity.width / 4.0D;
                this.worldObj.spawnParticle(this.particleTypes, false, var8, var10, var12, var2, var4 + 0.2D, var6, new int[0]);
            }
        }

        ++this.age;

        if (this.age >= this.lifetime)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 3;
    }
}
