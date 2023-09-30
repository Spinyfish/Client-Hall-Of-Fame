/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.item;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityMinecartTNT
extends EntityMinecart {
    private int minecartTNTFuse = -1;

    public EntityMinecartTNT(World worldIn) {
        super(worldIn);
    }

    public EntityMinecartTNT(World worldIn, double p_i1728_2_, double p_i1728_4_, double p_i1728_6_) {
        super(worldIn, p_i1728_2_, p_i1728_4_, p_i1728_6_);
    }

    @Override
    public EntityMinecart.EnumMinecartType getMinecartType() {
        return EntityMinecart.EnumMinecartType.TNT;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.tnt.getDefaultState();
    }

    @Override
    public void onUpdate() {
        double d0;
        super.onUpdate();
        if (this.minecartTNTFuse > 0) {
            --this.minecartTNTFuse;
            this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0, new int[0]);
        } else if (this.minecartTNTFuse == 0) {
            this.explodeCart(this.motionX * this.motionX + this.motionZ * this.motionZ);
        }
        if (this.isCollidedHorizontally && (d0 = this.motionX * this.motionX + this.motionZ * this.motionZ) >= (double)0.01f) {
            this.explodeCart(d0);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        EntityArrow entityarrow;
        Entity entity = source.getSourceOfDamage();
        if (entity instanceof EntityArrow && (entityarrow = (EntityArrow)entity).isBurning()) {
            this.explodeCart(entityarrow.motionX * entityarrow.motionX + entityarrow.motionY * entityarrow.motionY + entityarrow.motionZ * entityarrow.motionZ);
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void killMinecart(DamageSource p_94095_1_) {
        super.killMinecart(p_94095_1_);
        double d0 = this.motionX * this.motionX + this.motionZ * this.motionZ;
        if (!p_94095_1_.isExplosion() && this.worldObj.getGameRules().getGameRuleBooleanValue("doEntityDrops")) {
            this.entityDropItem(new ItemStack(Blocks.tnt, 1), 0.0f);
        }
        if (p_94095_1_.isFireDamage() || p_94095_1_.isExplosion() || d0 >= (double)0.01f) {
            this.explodeCart(d0);
        }
    }

    protected void explodeCart(double p_94103_1_) {
        if (!this.worldObj.isRemote) {
            double d0 = Math.sqrt(p_94103_1_);
            if (d0 > 5.0) {
                d0 = 5.0;
            }
            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)(4.0 + this.rand.nextDouble() * 1.5 * d0), true);
            this.setDead();
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        if (distance >= 3.0f) {
            float f = distance / 10.0f;
            this.explodeCart(f * f);
        }
        super.fall(distance, damageMultiplier);
    }

    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
        if (receivingPower && this.minecartTNTFuse < 0) {
            this.ignite();
        }
    }

    @Override
    public void handleHealthUpdate(byte id) {
        if (id == 10) {
            this.ignite();
        } else {
            super.handleHealthUpdate(id);
        }
    }

    public void ignite() {
        this.minecartTNTFuse = 80;
        if (!this.worldObj.isRemote) {
            this.worldObj.setEntityState(this, (byte)10);
            if (!this.isSilent()) {
                this.worldObj.playSoundAtEntity(this, "game.tnt.primed", 1.0f, 1.0f);
            }
        }
    }

    public int getFuseTicks() {
        return this.minecartTNTFuse;
    }

    public boolean isIgnited() {
        return this.minecartTNTFuse > -1;
    }

    @Override
    public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn) {
        return !this.isIgnited() || !BlockRailBase.isRailBlock(blockStateIn) && !BlockRailBase.isRailBlock(worldIn, pos.up()) ? super.getExplosionResistance(explosionIn, worldIn, pos, blockStateIn) : 0.0f;
    }

    @Override
    public boolean verifyExplosion(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn, float p_174816_5_) {
        return !this.isIgnited() || !BlockRailBase.isRailBlock(blockStateIn) && !BlockRailBase.isRailBlock(worldIn, pos.up()) ? super.verifyExplosion(explosionIn, worldIn, pos, blockStateIn, p_174816_5_) : false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        if (tagCompund.hasKey("TNTFuse", 99)) {
            this.minecartTNTFuse = tagCompund.getInteger("TNTFuse");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("TNTFuse", this.minecartTNTFuse);
    }
}

