/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityPotion
extends EntityThrowable {
    private ItemStack potionDamage;

    public EntityPotion(World worldIn) {
        super(worldIn);
    }

    public EntityPotion(World worldIn, EntityLivingBase throwerIn, int meta) {
        this(worldIn, throwerIn, new ItemStack(Items.potionitem, 1, meta));
    }

    public EntityPotion(World worldIn, EntityLivingBase throwerIn, ItemStack potionDamageIn) {
        super(worldIn, throwerIn);
        this.potionDamage = potionDamageIn;
    }

    public EntityPotion(World worldIn, double x, double y, double z, int p_i1791_8_) {
        this(worldIn, x, y, z, new ItemStack(Items.potionitem, 1, p_i1791_8_));
    }

    public EntityPotion(World worldIn, double x, double y, double z, ItemStack potionDamageIn) {
        super(worldIn, x, y, z);
        this.potionDamage = potionDamageIn;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.05f;
    }

    @Override
    protected float getVelocity() {
        return 0.5f;
    }

    @Override
    protected float getInaccuracy() {
        return -20.0f;
    }

    public void setPotionDamage(int potionId) {
        if (this.potionDamage == null) {
            this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
        }
        this.potionDamage.setItemDamage(potionId);
    }

    public int getPotionDamage() {
        if (this.potionDamage == null) {
            this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
        }
        return this.potionDamage.getMetadata();
    }

    @Override
    protected void onImpact(MovingObjectPosition p_70184_1_) {
        if (!this.worldObj.isRemote) {
            AxisAlignedBB axisalignedbb;
            List<EntityLivingBase> list1;
            List<PotionEffect> list = Items.potionitem.getEffects(this.potionDamage);
            if (list != null && !list.isEmpty() && !(list1 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb = this.getEntityBoundingBox().expand(4.0, 2.0, 4.0))).isEmpty()) {
                for (EntityLivingBase entitylivingbase : list1) {
                    double d0 = this.getDistanceSqToEntity(entitylivingbase);
                    if (!(d0 < 16.0)) continue;
                    double d1 = 1.0 - Math.sqrt(d0) / 4.0;
                    if (entitylivingbase == p_70184_1_.entityHit) {
                        d1 = 1.0;
                    }
                    for (PotionEffect potioneffect : list) {
                        int i = potioneffect.getPotionID();
                        if (Potion.potionTypes[i].isInstant()) {
                            Potion.potionTypes[i].affectEntity(this, this.getThrower(), entitylivingbase, potioneffect.getAmplifier(), d1);
                            continue;
                        }
                        int j = (int)(d1 * (double)potioneffect.getDuration() + 0.5);
                        if (j <= 20) continue;
                        entitylivingbase.addPotionEffect(new PotionEffect(i, j, potioneffect.getAmplifier()));
                    }
                }
            }
            this.worldObj.playAuxSFX(2002, new BlockPos(this), this.getPotionDamage());
            this.setDead();
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
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

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        if (this.potionDamage != null) {
            tagCompound.setTag("Potion", this.potionDamage.writeToNBT(new NBTTagCompound()));
        }
    }
}

