/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Explosion {
    private final boolean isFlaming;
    private final boolean isSmoking;
    private final Random explosionRNG = new Random();
    private final World worldObj;
    private final double explosionX;
    private final double explosionY;
    private final double explosionZ;
    private final Entity exploder;
    private final float explosionSize;
    private final List<BlockPos> affectedBlockPositions = Lists.newArrayList();
    private final Map<EntityPlayer, Vec3> playerKnockbackMap = Maps.newHashMap();

    public Explosion(World worldIn, Entity p_i45752_2_, double p_i45752_3_, double p_i45752_5_, double p_i45752_7_, float p_i45752_9_, List<BlockPos> p_i45752_10_) {
        this(worldIn, p_i45752_2_, p_i45752_3_, p_i45752_5_, p_i45752_7_, p_i45752_9_, false, true, p_i45752_10_);
    }

    public Explosion(World worldIn, Entity p_i45753_2_, double p_i45753_3_, double p_i45753_5_, double p_i45753_7_, float p_i45753_9_, boolean p_i45753_10_, boolean p_i45753_11_, List<BlockPos> p_i45753_12_) {
        this(worldIn, p_i45753_2_, p_i45753_3_, p_i45753_5_, p_i45753_7_, p_i45753_9_, p_i45753_10_, p_i45753_11_);
        this.affectedBlockPositions.addAll(p_i45753_12_);
    }

    public Explosion(World worldIn, Entity p_i45754_2_, double p_i45754_3_, double p_i45754_5_, double p_i45754_7_, float size, boolean p_i45754_10_, boolean p_i45754_11_) {
        this.worldObj = worldIn;
        this.exploder = p_i45754_2_;
        this.explosionSize = size;
        this.explosionX = p_i45754_3_;
        this.explosionY = p_i45754_5_;
        this.explosionZ = p_i45754_7_;
        this.isFlaming = p_i45754_10_;
        this.isSmoking = p_i45754_11_;
    }

    public void doExplosionA() {
        HashSet set = Sets.newHashSet();
        int i = 16;
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j != 0 && j != 15 && k != 0 && k != 15 && l != 0 && l != 15) continue;
                    double d0 = (float)j / 15.0f * 2.0f - 1.0f;
                    double d1 = (float)k / 15.0f * 2.0f - 1.0f;
                    double d2 = (float)l / 15.0f * 2.0f - 1.0f;
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    d0 /= d3;
                    d1 /= d3;
                    d2 /= d3;
                    double d4 = this.explosionX;
                    double d6 = this.explosionY;
                    double d8 = this.explosionZ;
                    float f1 = 0.3f;
                    for (float f = this.explosionSize * (0.7f + this.worldObj.rand.nextFloat() * 0.6f); f > 0.0f; f -= 0.22500001f) {
                        BlockPos blockpos = new BlockPos(d4, d6, d8);
                        IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
                        if (iblockstate.getBlock().getMaterial() != Material.air) {
                            float f2 = this.exploder != null ? this.exploder.getExplosionResistance(this, this.worldObj, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(null);
                            f -= (f2 + 0.3f) * 0.3f;
                        }
                        if (f > 0.0f && (this.exploder == null || this.exploder.verifyExplosion(this, this.worldObj, blockpos, iblockstate, f))) {
                            set.add(blockpos);
                        }
                        d4 += d0 * (double)0.3f;
                        d6 += d1 * (double)0.3f;
                        d8 += d2 * (double)0.3f;
                    }
                }
            }
        }
        this.affectedBlockPositions.addAll(set);
        float f3 = this.explosionSize * 2.0f;
        int k1 = MathHelper.floor_double(this.explosionX - (double)f3 - 1.0);
        int l1 = MathHelper.floor_double(this.explosionX + (double)f3 + 1.0);
        int i2 = MathHelper.floor_double(this.explosionY - (double)f3 - 1.0);
        int i1 = MathHelper.floor_double(this.explosionY + (double)f3 + 1.0);
        int j2 = MathHelper.floor_double(this.explosionZ - (double)f3 - 1.0);
        int j1 = MathHelper.floor_double(this.explosionZ + (double)f3 + 1.0);
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB(k1, i2, j2, l1, i1, j1));
        Vec3 vec3 = new Vec3(this.explosionX, this.explosionY, this.explosionZ);
        for (int k2 = 0; k2 < list.size(); ++k2) {
            double d9;
            double d7;
            double d5;
            double d13;
            double d12;
            Entity entity = list.get(k2);
            if (entity.isImmuneToExplosions() || !((d12 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / (double)f3) <= 1.0) || (d13 = (double)MathHelper.sqrt_double((d5 = entity.posX - this.explosionX) * d5 + (d7 = entity.posY + (double)entity.getEyeHeight() - this.explosionY) * d7 + (d9 = entity.posZ - this.explosionZ) * d9)) == 0.0) continue;
            d5 /= d13;
            d7 /= d13;
            d9 /= d13;
            double d14 = this.worldObj.getBlockDensity(vec3, entity.getEntityBoundingBox());
            double d10 = (1.0 - d12) * d14;
            entity.attackEntityFrom(DamageSource.setExplosionSource(this), (int)((d10 * d10 + d10) / 2.0 * 8.0 * (double)f3 + 1.0));
            double d11 = EnchantmentProtection.func_92092_a(entity, d10);
            entity.motionX += d5 * d11;
            entity.motionY += d7 * d11;
            entity.motionZ += d9 * d11;
            if (!(entity instanceof EntityPlayer) || ((EntityPlayer)entity).capabilities.disableDamage) continue;
            this.playerKnockbackMap.put((EntityPlayer)entity, new Vec3(d5 * d10, d7 * d10, d9 * d10));
        }
    }

    public void doExplosionB(boolean spawnParticles) {
        this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0f, (1.0f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2f) * 0.7f);
        if (this.explosionSize >= 2.0f && this.isSmoking) {
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0, new int[0]);
        } else {
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0, new int[0]);
        }
        if (this.isSmoking) {
            for (BlockPos blockpos : this.affectedBlockPositions) {
                Block block = this.worldObj.getBlockState(blockpos).getBlock();
                if (spawnParticles) {
                    double d0 = (float)blockpos.getX() + this.worldObj.rand.nextFloat();
                    double d1 = (float)blockpos.getY() + this.worldObj.rand.nextFloat();
                    double d2 = (float)blockpos.getZ() + this.worldObj.rand.nextFloat();
                    double d3 = d0 - this.explosionX;
                    double d4 = d1 - this.explosionY;
                    double d5 = d2 - this.explosionZ;
                    double d6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;
                    double d7 = 0.5 / (d6 / (double)this.explosionSize + 0.1);
                    this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.explosionX * 1.0) / 2.0, (d1 + this.explosionY * 1.0) / 2.0, (d2 + this.explosionZ * 1.0) / 2.0, d3 *= (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)), d4 *= d7, d5 *= d7, new int[0]);
                    this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
                }
                if (block.getMaterial() == Material.air) continue;
                if (block.canDropFromExplosion(this)) {
                    block.dropBlockAsItemWithChance(this.worldObj, blockpos, this.worldObj.getBlockState(blockpos), 1.0f / this.explosionSize, 0);
                }
                this.worldObj.setBlockState(blockpos, Blocks.air.getDefaultState(), 3);
                block.onBlockDestroyedByExplosion(this.worldObj, blockpos, this);
            }
        }
        if (this.isFlaming) {
            for (BlockPos blockpos1 : this.affectedBlockPositions) {
                if (this.worldObj.getBlockState(blockpos1).getBlock().getMaterial() != Material.air || !this.worldObj.getBlockState(blockpos1.down()).getBlock().isFullBlock() || this.explosionRNG.nextInt(3) != 0) continue;
                this.worldObj.setBlockState(blockpos1, Blocks.fire.getDefaultState());
            }
        }
    }

    public Map<EntityPlayer, Vec3> getPlayerKnockbackMap() {
        return this.playerKnockbackMap;
    }

    public EntityLivingBase getExplosivePlacedBy() {
        return this.exploder == null ? null : (this.exploder instanceof EntityTNTPrimed ? ((EntityTNTPrimed)this.exploder).getTntPlacedBy() : (this.exploder instanceof EntityLivingBase ? (EntityLivingBase)this.exploder : null));
    }

    public void func_180342_d() {
        this.affectedBlockPositions.clear();
    }

    public List<BlockPos> getAffectedBlockPositions() {
        return this.affectedBlockPositions;
    }
}

