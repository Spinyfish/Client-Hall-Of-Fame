/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityCow
extends EntityAnimal {
    public EntityCow(World worldIn) {
        super(worldIn);
        this.setSize(0.9f, 1.3f);
        ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 2.0));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0));
        this.tasks.addTask(3, new EntityAITempt(this, 1.25, Items.wheat, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.25));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2f);
    }

    @Override
    protected String getLivingSound() {
        return "mob.cow.say";
    }

    @Override
    public String getHurtSound() {
        return "mob.cow.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.cow.hurt";
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound("mob.cow.step", 0.15f, 1.0f);
    }

    @Override
    public float getSoundVolume() {
        return 0.4f;
    }

    @Override
    protected Item getDropItem() {
        return Items.leather;
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        int i = this.rand.nextInt(3) + this.rand.nextInt(1 + p_70628_2_);
        for (int j = 0; j < i; ++j) {
            this.dropItem(Items.leather, 1);
        }
        i = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + p_70628_2_);
        for (int k = 0; k < i; ++k) {
            if (this.isBurning()) {
                this.dropItem(Items.cooked_beef, 1);
                continue;
            }
            this.dropItem(Items.beef, 1);
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        if (itemstack != null && itemstack.getItem() == Items.bucket && !player.capabilities.isCreativeMode && !this.isChild()) {
            if (itemstack.stackSize-- == 1) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.milk_bucket));
            } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.milk_bucket))) {
                player.dropPlayerItemWithRandomChoice(new ItemStack(Items.milk_bucket, 1, 0), false);
            }
            return true;
        }
        return super.interact(player);
    }

    @Override
    public EntityCow createChild(EntityAgeable ageable) {
        return new EntityCow(this.worldObj);
    }

    @Override
    public float getEyeHeight() {
        return this.height;
    }
}

