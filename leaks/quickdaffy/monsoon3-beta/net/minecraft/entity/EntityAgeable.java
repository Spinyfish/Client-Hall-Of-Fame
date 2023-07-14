/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAgeable
extends EntityCreature {
    protected int growingAge;
    protected int field_175502_b;
    protected int field_175503_c;
    private float ageWidth = -1.0f;
    private float ageHeight;

    public EntityAgeable(World worldIn) {
        super(worldIn);
    }

    public abstract EntityAgeable createChild(EntityAgeable var1);

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        if (itemstack != null && itemstack.getItem() == Items.spawn_egg) {
            EntityAgeable entityageable;
            Class<? extends Entity> oclass;
            if (!this.worldObj.isRemote && (oclass = EntityList.getClassFromID(itemstack.getMetadata())) != null && this.getClass() == oclass && (entityageable = this.createChild(this)) != null) {
                entityageable.setGrowingAge(-24000);
                entityageable.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0f, 0.0f);
                this.worldObj.spawnEntityInWorld(entityageable);
                if (itemstack.hasDisplayName()) {
                    entityageable.setCustomNameTag(itemstack.getDisplayName());
                }
                if (!player.capabilities.isCreativeMode) {
                    --itemstack.stackSize;
                    if (itemstack.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(12, (byte)0);
    }

    public int getGrowingAge() {
        return this.worldObj.isRemote ? (int)this.dataWatcher.getWatchableObjectByte(12) : this.growingAge;
    }

    public void func_175501_a(int p_175501_1_, boolean p_175501_2_) {
        int i;
        int j = i = this.getGrowingAge();
        if ((i += p_175501_1_ * 20) > 0) {
            i = 0;
            if (j < 0) {
                this.onGrowingAdult();
            }
        }
        int k = i - j;
        this.setGrowingAge(i);
        if (p_175501_2_) {
            this.field_175502_b += k;
            if (this.field_175503_c == 0) {
                this.field_175503_c = 40;
            }
        }
        if (this.getGrowingAge() == 0) {
            this.setGrowingAge(this.field_175502_b);
        }
    }

    public void addGrowth(int growth) {
        this.func_175501_a(growth, false);
    }

    public void setGrowingAge(int age) {
        this.dataWatcher.updateObject(12, (byte)MathHelper.clamp_int(age, -1, 1));
        this.growingAge = age;
        this.setScaleForAge(this.isChild());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Age", this.getGrowingAge());
        tagCompound.setInteger("ForcedAge", this.field_175502_b);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setGrowingAge(tagCompund.getInteger("Age"));
        this.field_175502_b = tagCompund.getInteger("ForcedAge");
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.worldObj.isRemote) {
            if (this.field_175503_c > 0) {
                if (this.field_175503_c % 4 == 0) {
                    this.worldObj.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, 0.0, 0.0, 0.0, new int[0]);
                }
                --this.field_175503_c;
            }
            this.setScaleForAge(this.isChild());
        } else {
            int i = this.getGrowingAge();
            if (i < 0) {
                this.setGrowingAge(++i);
                if (i == 0) {
                    this.onGrowingAdult();
                }
            } else if (i > 0) {
                this.setGrowingAge(--i);
            }
        }
    }

    protected void onGrowingAdult() {
    }

    @Override
    public boolean isChild() {
        return this.getGrowingAge() < 0;
    }

    public void setScaleForAge(boolean p_98054_1_) {
        this.setScale(p_98054_1_ ? 0.5f : 1.0f);
    }

    @Override
    protected final void setSize(float width, float height) {
        boolean flag = this.ageWidth > 0.0f;
        this.ageWidth = width;
        this.ageHeight = height;
        if (!flag) {
            this.setScale(1.0f);
        }
    }

    protected final void setScale(float scale) {
        super.setSize(this.ageWidth * scale, this.ageHeight * scale);
    }
}

