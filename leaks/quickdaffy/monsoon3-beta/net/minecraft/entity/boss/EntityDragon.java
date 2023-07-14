/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.entity.boss;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityDragon
extends EntityLiving
implements IBossDisplayData,
IEntityMultiPart,
IMob {
    public double targetX;
    public double targetY;
    public double targetZ;
    public double[][] ringBuffer = new double[64][3];
    public int ringBufferIndex = -1;
    public EntityDragonPart[] dragonPartArray;
    public EntityDragonPart dragonPartHead = new EntityDragonPart(this, "head", 6.0f, 6.0f);
    public EntityDragonPart dragonPartBody = new EntityDragonPart(this, "body", 8.0f, 8.0f);
    public EntityDragonPart dragonPartTail1 = new EntityDragonPart(this, "tail", 4.0f, 4.0f);
    public EntityDragonPart dragonPartTail2 = new EntityDragonPart(this, "tail", 4.0f, 4.0f);
    public EntityDragonPart dragonPartTail3 = new EntityDragonPart(this, "tail", 4.0f, 4.0f);
    public EntityDragonPart dragonPartWing1 = new EntityDragonPart(this, "wing", 4.0f, 4.0f);
    public EntityDragonPart dragonPartWing2 = new EntityDragonPart(this, "wing", 4.0f, 4.0f);
    public float prevAnimTime;
    public float animTime;
    public boolean forceNewTarget;
    public boolean slowed;
    private Entity target;
    public int deathTicks;
    public EntityEnderCrystal healingEnderCrystal;

    public EntityDragon(World worldIn) {
        super(worldIn);
        this.dragonPartArray = new EntityDragonPart[]{this.dragonPartHead, this.dragonPartBody, this.dragonPartTail1, this.dragonPartTail2, this.dragonPartTail3, this.dragonPartWing1, this.dragonPartWing2};
        this.setHealth(this.getMaxHealth());
        this.setSize(16.0f, 8.0f);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.targetY = 100.0;
        this.ignoreFrustumCheck = true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    public double[] getMovementOffsets(int p_70974_1_, float p_70974_2_) {
        if (this.getHealth() <= 0.0f) {
            p_70974_2_ = 0.0f;
        }
        p_70974_2_ = 1.0f - p_70974_2_;
        int i = this.ringBufferIndex - p_70974_1_ * 1 & 0x3F;
        int j = this.ringBufferIndex - p_70974_1_ * 1 - 1 & 0x3F;
        double[] adouble = new double[3];
        double d0 = this.ringBuffer[i][0];
        double d1 = MathHelper.wrapAngleTo180_double(this.ringBuffer[j][0] - d0);
        adouble[0] = d0 + d1 * (double)p_70974_2_;
        d0 = this.ringBuffer[i][1];
        d1 = this.ringBuffer[j][1] - d0;
        adouble[1] = d0 + d1 * (double)p_70974_2_;
        adouble[2] = this.ringBuffer[i][2] + (this.ringBuffer[j][2] - this.ringBuffer[i][2]) * (double)p_70974_2_;
        return adouble;
    }

    @Override
    public void onLivingUpdate() {
        if (this.worldObj.isRemote) {
            float f = MathHelper.cos(this.animTime * (float)Math.PI * 2.0f);
            float f1 = MathHelper.cos(this.prevAnimTime * (float)Math.PI * 2.0f);
            if (f1 <= -0.3f && f >= -0.3f && !this.isSilent()) {
                this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0f, 0.8f + this.rand.nextFloat() * 0.3f, false);
            }
        }
        this.prevAnimTime = this.animTime;
        if (this.getHealth() <= 0.0f) {
            float f11 = (this.rand.nextFloat() - 0.5f) * 8.0f;
            float f13 = (this.rand.nextFloat() - 0.5f) * 4.0f;
            float f14 = (this.rand.nextFloat() - 0.5f) * 8.0f;
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX + (double)f11, this.posY + 2.0 + (double)f13, this.posZ + (double)f14, 0.0, 0.0, 0.0, new int[0]);
        } else {
            this.updateDragonEnderCrystal();
            float f10 = 0.2f / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0f + 1.0f);
            this.animTime = this.slowed ? (this.animTime += f10 * 0.5f) : (this.animTime += (f10 *= (float)Math.pow(2.0, this.motionY)));
            this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
            if (this.isAIDisabled()) {
                this.animTime = 0.5f;
            } else {
                if (this.ringBufferIndex < 0) {
                    for (int i = 0; i < this.ringBuffer.length; ++i) {
                        this.ringBuffer[i][0] = this.rotationYaw;
                        this.ringBuffer[i][1] = this.posY;
                    }
                }
                if (++this.ringBufferIndex == this.ringBuffer.length) {
                    this.ringBufferIndex = 0;
                }
                this.ringBuffer[this.ringBufferIndex][0] = this.rotationYaw;
                this.ringBuffer[this.ringBufferIndex][1] = this.posY;
                if (this.worldObj.isRemote) {
                    if (this.newPosRotationIncrements > 0) {
                        double d10 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
                        double d0 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
                        double d1 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
                        double d2 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
                        this.rotationYaw = (float)((double)this.rotationYaw + d2 / (double)this.newPosRotationIncrements);
                        this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
                        --this.newPosRotationIncrements;
                        this.setPosition(d10, d0, d1);
                        this.setRotation(this.rotationYaw, this.rotationPitch);
                    }
                } else {
                    double d11 = this.targetX - this.posX;
                    double d12 = this.targetY - this.posY;
                    double d13 = this.targetZ - this.posZ;
                    double d14 = d11 * d11 + d12 * d12 + d13 * d13;
                    if (this.target != null) {
                        this.targetX = this.target.posX;
                        this.targetZ = this.target.posZ;
                        double d3 = this.targetX - this.posX;
                        double d5 = this.targetZ - this.posZ;
                        double d7 = Math.sqrt(d3 * d3 + d5 * d5);
                        double d8 = (double)0.4f + d7 / 80.0 - 1.0;
                        if (d8 > 10.0) {
                            d8 = 10.0;
                        }
                        this.targetY = this.target.getEntityBoundingBox().minY + d8;
                    } else {
                        this.targetX += this.rand.nextGaussian() * 2.0;
                        this.targetZ += this.rand.nextGaussian() * 2.0;
                    }
                    if (this.forceNewTarget || d14 < 100.0 || d14 > 22500.0 || this.isCollidedHorizontally || this.isCollidedVertically) {
                        this.setNewTarget();
                    }
                    d12 /= (double)MathHelper.sqrt_double(d11 * d11 + d13 * d13);
                    float f17 = 0.6f;
                    d12 = MathHelper.clamp_double(d12, -f17, f17);
                    this.motionY += d12 * (double)0.1f;
                    this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
                    double d4 = 180.0 - MathHelper.func_181159_b(d11, d13) * 180.0 / Math.PI;
                    double d6 = MathHelper.wrapAngleTo180_double(d4 - (double)this.rotationYaw);
                    if (d6 > 50.0) {
                        d6 = 50.0;
                    }
                    if (d6 < -50.0) {
                        d6 = -50.0;
                    }
                    Vec3 vec3 = new Vec3(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ).normalize();
                    double d15 = -MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0f);
                    Vec3 vec31 = new Vec3(MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0f), this.motionY, d15).normalize();
                    float f5 = ((float)vec31.dotProduct(vec3) + 0.5f) / 1.5f;
                    if (f5 < 0.0f) {
                        f5 = 0.0f;
                    }
                    this.randomYawVelocity *= 0.8f;
                    float f6 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0f + 1.0f;
                    double d9 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0 + 1.0;
                    if (d9 > 40.0) {
                        d9 = 40.0;
                    }
                    this.randomYawVelocity = (float)((double)this.randomYawVelocity + d6 * ((double)0.7f / d9 / (double)f6));
                    this.rotationYaw += this.randomYawVelocity * 0.1f;
                    float f7 = (float)(2.0 / (d9 + 1.0));
                    float f8 = 0.06f;
                    this.moveFlying(0.0f, -1.0f, f8 * (f5 * f7 + (1.0f - f7)));
                    if (this.slowed) {
                        this.moveEntity(this.motionX * (double)0.8f, this.motionY * (double)0.8f, this.motionZ * (double)0.8f);
                    } else {
                        this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    }
                    Vec3 vec32 = new Vec3(this.motionX, this.motionY, this.motionZ).normalize();
                    float f9 = ((float)vec32.dotProduct(vec31) + 1.0f) / 2.0f;
                    f9 = 0.8f + 0.15f * f9;
                    this.motionX *= (double)f9;
                    this.motionZ *= (double)f9;
                    this.motionY *= (double)0.91f;
                }
                this.renderYawOffset = this.rotationYaw;
                this.dragonPartHead.height = 3.0f;
                this.dragonPartHead.width = 3.0f;
                this.dragonPartTail1.height = 2.0f;
                this.dragonPartTail1.width = 2.0f;
                this.dragonPartTail2.height = 2.0f;
                this.dragonPartTail2.width = 2.0f;
                this.dragonPartTail3.height = 2.0f;
                this.dragonPartTail3.width = 2.0f;
                this.dragonPartBody.height = 3.0f;
                this.dragonPartBody.width = 5.0f;
                this.dragonPartWing1.height = 2.0f;
                this.dragonPartWing1.width = 4.0f;
                this.dragonPartWing2.height = 3.0f;
                this.dragonPartWing2.width = 4.0f;
                float f12 = (float)(this.getMovementOffsets(5, 1.0f)[1] - this.getMovementOffsets(10, 1.0f)[1]) * 10.0f / 180.0f * (float)Math.PI;
                float f2 = MathHelper.cos(f12);
                float f15 = -MathHelper.sin(f12);
                float f3 = this.rotationYaw * (float)Math.PI / 180.0f;
                float f16 = MathHelper.sin(f3);
                float f4 = MathHelper.cos(f3);
                this.dragonPartBody.onUpdate();
                this.dragonPartBody.setLocationAndAngles(this.posX + (double)(f16 * 0.5f), this.posY, this.posZ - (double)(f4 * 0.5f), 0.0f, 0.0f);
                this.dragonPartWing1.onUpdate();
                this.dragonPartWing1.setLocationAndAngles(this.posX + (double)(f4 * 4.5f), this.posY + 2.0, this.posZ + (double)(f16 * 4.5f), 0.0f, 0.0f);
                this.dragonPartWing2.onUpdate();
                this.dragonPartWing2.setLocationAndAngles(this.posX - (double)(f4 * 4.5f), this.posY + 2.0, this.posZ - (double)(f16 * 4.5f), 0.0f, 0.0f);
                if (!this.worldObj.isRemote && this.hurtTime == 0) {
                    this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.getEntityBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0)));
                    this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.getEntityBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0)));
                    this.attackEntitiesInList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.getEntityBoundingBox().expand(1.0, 1.0, 1.0)));
                }
                double[] adouble1 = this.getMovementOffsets(5, 1.0f);
                double[] adouble = this.getMovementOffsets(0, 1.0f);
                float f18 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0f - this.randomYawVelocity * 0.01f);
                float f19 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0f - this.randomYawVelocity * 0.01f);
                this.dragonPartHead.onUpdate();
                this.dragonPartHead.setLocationAndAngles(this.posX + (double)(f18 * 5.5f * f2), this.posY + (adouble[1] - adouble1[1]) * 1.0 + (double)(f15 * 5.5f), this.posZ - (double)(f19 * 5.5f * f2), 0.0f, 0.0f);
                for (int j = 0; j < 3; ++j) {
                    EntityDragonPart entitydragonpart = null;
                    if (j == 0) {
                        entitydragonpart = this.dragonPartTail1;
                    }
                    if (j == 1) {
                        entitydragonpart = this.dragonPartTail2;
                    }
                    if (j == 2) {
                        entitydragonpart = this.dragonPartTail3;
                    }
                    double[] adouble2 = this.getMovementOffsets(12 + j * 2, 1.0f);
                    float f20 = this.rotationYaw * (float)Math.PI / 180.0f + this.simplifyAngle(adouble2[0] - adouble1[0]) * (float)Math.PI / 180.0f * 1.0f;
                    float f21 = MathHelper.sin(f20);
                    float f22 = MathHelper.cos(f20);
                    float f23 = 1.5f;
                    float f24 = (float)(j + 1) * 2.0f;
                    entitydragonpart.onUpdate();
                    entitydragonpart.setLocationAndAngles(this.posX - (double)((f16 * f23 + f21 * f24) * f2), this.posY + (adouble2[1] - adouble1[1]) * 1.0 - (double)((f24 + f23) * f15) + 1.5, this.posZ + (double)((f4 * f23 + f22 * f24) * f2), 0.0f, 0.0f);
                }
                if (!this.worldObj.isRemote) {
                    this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.getEntityBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartBody.getEntityBoundingBox());
                }
            }
        }
    }

    private void updateDragonEnderCrystal() {
        if (this.healingEnderCrystal != null) {
            if (this.healingEnderCrystal.isDead) {
                if (!this.worldObj.isRemote) {
                    this.attackEntityFromPart(this.dragonPartHead, DamageSource.setExplosionSource(null), 10.0f);
                }
                this.healingEnderCrystal = null;
            } else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 1.0f);
            }
        }
        if (this.rand.nextInt(10) == 0) {
            float f = 32.0f;
            List<EntityEnderCrystal> list = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.getEntityBoundingBox().expand(f, f, f));
            EntityEnderCrystal entityendercrystal = null;
            double d0 = Double.MAX_VALUE;
            for (EntityEnderCrystal entityendercrystal1 : list) {
                double d1 = entityendercrystal1.getDistanceSqToEntity(this);
                if (!(d1 < d0)) continue;
                d0 = d1;
                entityendercrystal = entityendercrystal1;
            }
            this.healingEnderCrystal = entityendercrystal;
        }
    }

    private void collideWithEntities(List<Entity> p_70970_1_) {
        double d0 = (this.dragonPartBody.getEntityBoundingBox().minX + this.dragonPartBody.getEntityBoundingBox().maxX) / 2.0;
        double d1 = (this.dragonPartBody.getEntityBoundingBox().minZ + this.dragonPartBody.getEntityBoundingBox().maxZ) / 2.0;
        for (Entity entity : p_70970_1_) {
            if (!(entity instanceof EntityLivingBase)) continue;
            double d2 = entity.posX - d0;
            double d3 = entity.posZ - d1;
            double d4 = d2 * d2 + d3 * d3;
            entity.addVelocity(d2 / d4 * 4.0, 0.2f, d3 / d4 * 4.0);
        }
    }

    private void attackEntitiesInList(List<Entity> p_70971_1_) {
        for (int i = 0; i < p_70971_1_.size(); ++i) {
            Entity entity = p_70971_1_.get(i);
            if (!(entity instanceof EntityLivingBase)) continue;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0f);
            this.applyEnchantments(this, entity);
        }
    }

    private void setNewTarget() {
        this.forceNewTarget = false;
        ArrayList list = Lists.newArrayList(this.worldObj.playerEntities);
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            if (!((EntityPlayer)iterator.next()).isSpectator()) continue;
            iterator.remove();
        }
        if (this.rand.nextInt(2) == 0 && !list.isEmpty()) {
            this.target = (Entity)list.get(this.rand.nextInt(list.size()));
        } else {
            double d2;
            double d1;
            double d0;
            boolean flag;
            do {
                this.targetX = 0.0;
                this.targetY = 70.0f + this.rand.nextFloat() * 50.0f;
                this.targetZ = 0.0;
                this.targetX += (double)(this.rand.nextFloat() * 120.0f - 60.0f);
                this.targetZ += (double)(this.rand.nextFloat() * 120.0f - 60.0f);
            } while (!(flag = (d0 = this.posX - this.targetX) * d0 + (d1 = this.posY - this.targetY) * d1 + (d2 = this.posZ - this.targetZ) * d2 > 100.0));
            this.target = null;
        }
    }

    private float simplifyAngle(double p_70973_1_) {
        return (float)MathHelper.wrapAngleTo180_double(p_70973_1_);
    }

    private boolean destroyBlocksInAABB(AxisAlignedBB p_70972_1_) {
        int i = MathHelper.floor_double(p_70972_1_.minX);
        int j = MathHelper.floor_double(p_70972_1_.minY);
        int k = MathHelper.floor_double(p_70972_1_.minZ);
        int l = MathHelper.floor_double(p_70972_1_.maxX);
        int i1 = MathHelper.floor_double(p_70972_1_.maxY);
        int j1 = MathHelper.floor_double(p_70972_1_.maxZ);
        boolean flag = false;
        boolean flag1 = false;
        for (int k1 = i; k1 <= l; ++k1) {
            for (int l1 = j; l1 <= i1; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    Block block = this.worldObj.getBlockState(blockpos).getBlock();
                    if (block.getMaterial() == Material.air) continue;
                    if (block != Blocks.barrier && block != Blocks.obsidian && block != Blocks.end_stone && block != Blocks.bedrock && block != Blocks.command_block && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                        flag1 = this.worldObj.setBlockToAir(blockpos) || flag1;
                        continue;
                    }
                    flag = true;
                }
            }
        }
        if (flag1) {
            double d0 = p_70972_1_.minX + (p_70972_1_.maxX - p_70972_1_.minX) * (double)this.rand.nextFloat();
            double d1 = p_70972_1_.minY + (p_70972_1_.maxY - p_70972_1_.minY) * (double)this.rand.nextFloat();
            double d2 = p_70972_1_.minZ + (p_70972_1_.maxZ - p_70972_1_.minZ) * (double)this.rand.nextFloat();
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d0, d1, d2, 0.0, 0.0, 0.0, new int[0]);
        }
        return flag;
    }

    @Override
    public boolean attackEntityFromPart(EntityDragonPart dragonPart, DamageSource source, float p_70965_3_) {
        if (dragonPart != this.dragonPartHead) {
            p_70965_3_ = p_70965_3_ / 4.0f + 1.0f;
        }
        float f = this.rotationYaw * (float)Math.PI / 180.0f;
        float f1 = MathHelper.sin(f);
        float f2 = MathHelper.cos(f);
        this.targetX = this.posX + (double)(f1 * 5.0f) + (double)((this.rand.nextFloat() - 0.5f) * 2.0f);
        this.targetY = this.posY + (double)(this.rand.nextFloat() * 3.0f) + 1.0;
        this.targetZ = this.posZ - (double)(f2 * 5.0f) + (double)((this.rand.nextFloat() - 0.5f) * 2.0f);
        this.target = null;
        if (source.getEntity() instanceof EntityPlayer || source.isExplosion()) {
            this.attackDragonFrom(source, p_70965_3_);
        }
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage()) {
            this.attackDragonFrom(source, amount);
        }
        return false;
    }

    protected boolean attackDragonFrom(DamageSource source, float amount) {
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onKillCommand() {
        this.setDead();
    }

    @Override
    protected void onDeathUpdate() {
        ++this.deathTicks;
        if (this.deathTicks >= 180 && this.deathTicks <= 200) {
            float f = (this.rand.nextFloat() - 0.5f) * 8.0f;
            float f1 = (this.rand.nextFloat() - 0.5f) * 4.0f;
            float f2 = (this.rand.nextFloat() - 0.5f) * 8.0f;
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + (double)f, this.posY + 2.0 + (double)f1, this.posZ + (double)f2, 0.0, 0.0, 0.0, new int[0]);
        }
        boolean flag = this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot");
        if (!this.worldObj.isRemote) {
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0 && flag) {
                int k;
                for (int i = 1000; i > 0; i -= k) {
                    k = EntityXPOrb.getXPSplit(i);
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, k));
                }
            }
            if (this.deathTicks == 1) {
                this.worldObj.playBroadcastSound(1018, new BlockPos(this), 0);
            }
        }
        this.moveEntity(0.0, 0.1f, 0.0);
        this.renderYawOffset = this.rotationYaw += 20.0f;
        if (this.deathTicks == 200 && !this.worldObj.isRemote) {
            if (flag) {
                int l;
                for (int j = 2000; j > 0; j -= l) {
                    l = EntityXPOrb.getXPSplit(j);
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, l));
                }
            }
            this.generatePortal(new BlockPos(this.posX, 64.0, this.posZ));
            this.setDead();
        }
    }

    private void generatePortal(BlockPos pos) {
        int i = 4;
        double d0 = 12.25;
        double d1 = 6.25;
        for (int j = -1; j <= 32; ++j) {
            for (int k = -4; k <= 4; ++k) {
                for (int l = -4; l <= 4; ++l) {
                    double d2 = k * k + l * l;
                    if (!(d2 <= 12.25)) continue;
                    BlockPos blockpos = pos.add(k, j, l);
                    if (j < 0) {
                        if (!(d2 <= 6.25)) continue;
                        this.worldObj.setBlockState(blockpos, Blocks.bedrock.getDefaultState());
                        continue;
                    }
                    if (j > 0) {
                        this.worldObj.setBlockState(blockpos, Blocks.air.getDefaultState());
                        continue;
                    }
                    if (d2 > 6.25) {
                        this.worldObj.setBlockState(blockpos, Blocks.bedrock.getDefaultState());
                        continue;
                    }
                    this.worldObj.setBlockState(blockpos, Blocks.end_portal.getDefaultState());
                }
            }
        }
        this.worldObj.setBlockState(pos, Blocks.bedrock.getDefaultState());
        this.worldObj.setBlockState(pos.up(), Blocks.bedrock.getDefaultState());
        BlockPos blockpos1 = pos.up(2);
        this.worldObj.setBlockState(blockpos1, Blocks.bedrock.getDefaultState());
        this.worldObj.setBlockState(blockpos1.west(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST));
        this.worldObj.setBlockState(blockpos1.east(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST));
        this.worldObj.setBlockState(blockpos1.north(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH));
        this.worldObj.setBlockState(blockpos1.south(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH));
        this.worldObj.setBlockState(pos.up(3), Blocks.bedrock.getDefaultState());
        this.worldObj.setBlockState(pos.up(4), Blocks.dragon_egg.getDefaultState());
    }

    @Override
    protected void despawnEntity() {
    }

    @Override
    public Entity[] getParts() {
        return this.dragonPartArray;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public World getWorld() {
        return this.worldObj;
    }

    @Override
    protected String getLivingSound() {
        return "mob.enderdragon.growl";
    }

    @Override
    public String getHurtSound() {
        return "mob.enderdragon.hit";
    }

    @Override
    public float getSoundVolume() {
        return 5.0f;
    }
}

