package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;

public class EntityPotion extends EntityThrowable {
    /**
     * The damage value of the thrown potion that this EntityPotion represents.
     */
    private ItemStack potionDamage;

    public EntityPotion(final World worldIn) {
        super(worldIn);
    }

    public EntityPotion(final World worldIn, final EntityLivingBase throwerIn, final int meta) {
        this(worldIn, throwerIn, new ItemStack(Items.potionitem, 1, meta));
    }

    public EntityPotion(final World worldIn, final EntityLivingBase throwerIn, final ItemStack potionDamageIn) {
        super(worldIn, throwerIn);
        this.potionDamage = potionDamageIn;
    }

    public EntityPotion(final World worldIn, final double x, final double y, final double z, final int p_i1791_8_) {
        this(worldIn, x, y, z, new ItemStack(Items.potionitem, 1, p_i1791_8_));
    }

    public EntityPotion(final World worldIn, final double x, final double y, final double z, final ItemStack potionDamageIn) {
        super(worldIn, x, y, z);
        this.potionDamage = potionDamageIn;
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    protected float getGravityVelocity() {
        return 0.05F;
    }

    protected float getVelocity() {
        return 0.5F;
    }

    protected float getInaccuracy() {
        return -20.0F;
    }

    /**
     * Sets the PotionEffect by the given id of the potion effect.
     */
    public void setPotionDamage(final int potionId) {
        if (this.potionDamage == null) {
            this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
        }

        this.potionDamage.setItemDamage(potionId);
    }

    /**
     * Returns the damage value of the thrown potion that this EntityPotion represents.
     */
    public int getPotionDamage() {
        if (this.potionDamage == null) {
            this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
        }

        return this.potionDamage.getMetadata();
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(final MovingObjectPosition p_70184_1_) {
        if (!this.worldObj.isRemote) {
            final List<PotionEffect> list = Items.potionitem.getEffects(this.potionDamage);

            if (list != null && !list.isEmpty()) {
                final AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D);
                final List<EntityLivingBase> list1 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

                if (!list1.isEmpty()) {
                    for (final EntityLivingBase entitylivingbase : list1) {
                        final double d0 = this.getDistanceSqToEntity(entitylivingbase);

                        if (d0 < 16.0D) {
                            double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

                            if (entitylivingbase == p_70184_1_.entityHit) {
                                d1 = 1.0D;
                            }

                            for (final PotionEffect potioneffect : list) {
                                final int i = potioneffect.getPotionID();

                                if (Potion.potionTypes[i].isInstant()) {
                                    Potion.potionTypes[i].affectEntity(this, this.getThrower(), entitylivingbase, potioneffect.getAmplifier(), d1);
                                } else {
                                    final int j = (int) (d1 * (double) potioneffect.getDuration() + 0.5D);

                                    if (j > 20) {
                                        entitylivingbase.addPotionEffect(new PotionEffect(i, j, potioneffect.getAmplifier()));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            this.worldObj.playAuxSFX(2002, new BlockPos(this), this.getPotionDamage());
            this.setDead();
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(final NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);

        if (tagCompund.hasKey("Potion", 10)) {
            this.potionDamage = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("Potion"));
        } else {
            this.setPotionDamage(tagCompund.getInteger("potionValue"));
        }

        if (this.potionDamage == null) {
            this.setDead();
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(final NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        if (this.potionDamage != null) {
            tagCompound.setTag("Potion", this.potionDamage.writeToNBT(new NBTTagCompound()));
        }
    }
}
