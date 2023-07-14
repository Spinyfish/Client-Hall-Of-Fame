/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Maps
 */
package net.minecraft.entity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPlayerHurtSound;
import wtf.monsoon.impl.module.movement.Sprint;
import wtf.monsoon.impl.module.visual.BlockAnimations;

public abstract class EntityLivingBase
extends Entity {
    private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier sprintingSpeedBoostModifier = new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.3f, 2).setSaved(false);
    private BaseAttributeMap attributeMap;
    private final CombatTracker _combatTracker = new CombatTracker(this);
    private final Map<Integer, PotionEffect> activePotionsMap = Maps.newHashMap();
    private final ItemStack[] previousEquipment = new ItemStack[5];
    public boolean isSwingInProgress;
    public int swingProgressInt;
    public int arrowHitTimer;
    public int hurtTime;
    public int maxHurtTime;
    public float attackedAtYaw;
    public int deathTime;
    public float prevSwingProgress;
    public float swingProgress;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public int maxHurtResistantTime = 20;
    public float prevCameraPitch;
    public float cameraPitch;
    public float field_70769_ao;
    public float field_70770_ap;
    public float renderYawOffset;
    public float prevRenderYawOffset;
    public float rotationYawHead;
    public float rotationPitchHead;
    public float prevRotationYawHead;
    public float prevRotationPitchHead;
    public float jumpMovementFactor = 0.02f;
    protected EntityPlayer attackingPlayer;
    protected int recentlyHit;
    protected boolean dead;
    protected int entityAge;
    protected float prevOnGroundSpeedFactor;
    protected float onGroundSpeedFactor;
    protected float movedDistance;
    protected float prevMovedDistance;
    protected float field_70741_aB;
    protected int scoreValue;
    protected float lastDamage;
    protected boolean isJumping;
    public float moveStrafing;
    public float moveForward;
    protected float randomYawVelocity;
    protected int newPosRotationIncrements;
    protected double newPosX;
    protected double newPosY;
    protected double newPosZ;
    protected double newRotationYaw;
    protected double newRotationPitch;
    private boolean potionsNeedUpdate = true;
    private EntityLivingBase entityLivingToAttack;
    private int revengeTimer;
    private EntityLivingBase lastAttacker;
    private int lastAttackerTime;
    private float landMovementFactor;
    private int jumpTicks;
    private float absorptionAmount;

    @Override
    public void onKillCommand() {
        this.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
    }

    public EntityLivingBase(World worldIn) {
        super(worldIn);
        this.applyEntityAttributes();
        this.setHealth(this.getMaxHealth());
        this.preventEntitySpawning = true;
        this.field_70770_ap = (float)((Math.random() + 1.0) * (double)0.01f);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.field_70769_ao = (float)Math.random() * 12398.0f;
        this.rotationYawHead = this.rotationYaw = (float)(Math.random() * Math.PI * 2.0);
        this.rotationPitchHead = this.rotationPitch;
        this.stepHeight = 0.6f;
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(7, 0);
        this.dataWatcher.addObject(8, (byte)0);
        this.dataWatcher.addObject(9, (byte)0);
        this.dataWatcher.addObject(6, Float.valueOf(1.0f));
    }

    protected void applyEntityAttributes() {
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
        if (!this.isInWater()) {
            this.handleWaterMovement();
        }
        if (!this.worldObj.isRemote && this.fallDistance > 3.0f && onGroundIn) {
            IBlockState iblockstate = this.worldObj.getBlockState(pos);
            Block block = iblockstate.getBlock();
            float f = MathHelper.ceiling_float_int(this.fallDistance - 3.0f);
            if (block.getMaterial() != Material.air) {
                double d0 = Math.min(0.2f + f / 15.0f, 10.0f);
                if (d0 > 2.5) {
                    d0 = 2.5;
                }
                int i = (int)(150.0 * d0);
                ((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, i, 0.0, 0.0, 0.0, (double)0.15f, Block.getStateId(iblockstate));
            }
        }
        super.updateFallState(y, onGroundIn, blockIn, pos);
    }

    public boolean canBreatheUnderwater() {
        return false;
    }

    @Override
    public void onEntityUpdate() {
        boolean flag1;
        this.prevSwingProgress = this.swingProgress;
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("livingEntityBaseTick");
        boolean flag = this instanceof EntityPlayer;
        if (this.isEntityAlive()) {
            double d0;
            if (this.isEntityInsideOpaqueBlock()) {
                this.attackEntityFrom(DamageSource.inWall, 1.0f);
            } else if (flag && !this.worldObj.getWorldBorder().contains(this.getEntityBoundingBox()) && (d0 = this.worldObj.getWorldBorder().getClosestDistance(this) + this.worldObj.getWorldBorder().getDamageBuffer()) < 0.0) {
                this.attackEntityFrom(DamageSource.inWall, Math.max(1, MathHelper.floor_double(-d0 * this.worldObj.getWorldBorder().getDamageAmount())));
            }
        }
        if (this.isImmuneToFire() || this.worldObj.isRemote) {
            this.extinguish();
        }
        boolean bl = flag1 = flag && ((EntityPlayer)this).capabilities.disableDamage;
        if (this.isEntityAlive()) {
            if (this.isInsideOfMaterial(Material.water)) {
                if (!(this.canBreatheUnderwater() || this.isPotionActive(Potion.waterBreathing.id) || flag1)) {
                    this.setAir(this.decreaseAirSupply(this.getAir()));
                    if (this.getAir() == -20) {
                        this.setAir(0);
                        for (int i = 0; i < 8; ++i) {
                            float f = this.rand.nextFloat() - this.rand.nextFloat();
                            float f1 = this.rand.nextFloat() - this.rand.nextFloat();
                            float f2 = this.rand.nextFloat() - this.rand.nextFloat();
                            this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f2, this.motionX, this.motionY, this.motionZ, new int[0]);
                        }
                        this.attackEntityFrom(DamageSource.drown, 2.0f);
                    }
                }
                if (!this.worldObj.isRemote && this.isRiding() && this.ridingEntity instanceof EntityLivingBase) {
                    this.mountEntity(null);
                }
            } else {
                this.setAir(300);
            }
        }
        if (this.isEntityAlive() && this.isWet()) {
            this.extinguish();
        }
        this.prevCameraPitch = this.cameraPitch;
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP)) {
            --this.hurtResistantTime;
        }
        if (this.getHealth() <= 0.0f) {
            this.onDeathUpdate();
        }
        if (this.recentlyHit > 0) {
            --this.recentlyHit;
        } else {
            this.attackingPlayer = null;
        }
        if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive()) {
            this.lastAttacker = null;
        }
        if (this.entityLivingToAttack != null) {
            if (!this.entityLivingToAttack.isEntityAlive()) {
                this.setRevengeTarget(null);
            } else if (this.ticksExisted - this.revengeTimer > 100) {
                this.setRevengeTarget(null);
            }
        }
        this.updatePotionEffects();
        this.prevMovedDistance = this.movedDistance;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYawHead = this.rotationYawHead;
        this.prevRotationPitchHead = this.rotationPitchHead;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.worldObj.theProfiler.endSection();
    }

    public boolean isChild() {
        return false;
    }

    protected void onDeathUpdate() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && this.canDropLoot() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                int j;
                for (int i = this.getExperiencePoints(this.attackingPlayer); i > 0; i -= j) {
                    j = EntityXPOrb.getXPSplit(i);
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                }
            }
            this.setDead();
            for (int k = 0; k < 20; ++k) {
                double d2 = this.rand.nextGaussian() * 0.02;
                double d0 = this.rand.nextGaussian() * 0.02;
                double d1 = this.rand.nextGaussian() * 0.02;
                this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d2, d0, d1, new int[0]);
            }
        }
    }

    protected boolean canDropLoot() {
        return !this.isChild();
    }

    protected int decreaseAirSupply(int p_70682_1_) {
        int i = EnchantmentHelper.getRespiration(this);
        return i > 0 && this.rand.nextInt(i + 1) > 0 ? p_70682_1_ : p_70682_1_ - 1;
    }

    protected int getExperiencePoints(EntityPlayer player) {
        return 0;
    }

    protected boolean isPlayer() {
        return false;
    }

    public Random getRNG() {
        return this.rand;
    }

    public EntityLivingBase getAITarget() {
        return this.entityLivingToAttack;
    }

    public int getRevengeTimer() {
        return this.revengeTimer;
    }

    public void setRevengeTarget(EntityLivingBase livingBase) {
        this.entityLivingToAttack = livingBase;
        this.revengeTimer = this.ticksExisted;
    }

    public EntityLivingBase getLastAttacker() {
        return this.lastAttacker;
    }

    public int getLastAttackerTime() {
        return this.lastAttackerTime;
    }

    public void setLastAttacker(Entity entityIn) {
        this.lastAttacker = entityIn instanceof EntityLivingBase ? (EntityLivingBase)entityIn : null;
        this.lastAttackerTime = this.ticksExisted;
    }

    public int getAge() {
        return this.entityAge;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setFloat("HealF", this.getHealth());
        tagCompound.setShort("Health", (short)Math.ceil(this.getHealth()));
        tagCompound.setShort("HurtTime", (short)this.hurtTime);
        tagCompound.setInteger("HurtByTimestamp", this.revengeTimer);
        tagCompound.setShort("DeathTime", (short)this.deathTime);
        tagCompound.setFloat("AbsorptionAmount", this.getAbsorptionAmount());
        for (ItemStack itemstack : this.getInventory()) {
            if (itemstack == null) continue;
            this.attributeMap.removeAttributeModifiers(itemstack.getAttributeModifiers());
        }
        tagCompound.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));
        for (ItemStack itemstack1 : this.getInventory()) {
            if (itemstack1 == null) continue;
            this.attributeMap.applyAttributeModifiers(itemstack1.getAttributeModifiers());
        }
        if (!this.activePotionsMap.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            for (PotionEffect potioneffect : this.activePotionsMap.values()) {
                nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }
            tagCompound.setTag("ActiveEffects", nbttaglist);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.setAbsorptionAmount(tagCompund.getFloat("AbsorptionAmount"));
        if (tagCompund.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isRemote) {
            SharedMonsterAttributes.func_151475_a(this.getAttributeMap(), tagCompund.getTagList("Attributes", 10));
        }
        if (tagCompund.hasKey("ActiveEffects", 9)) {
            NBTTagList nbttaglist = tagCompund.getTagList("ActiveEffects", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound);
                if (potioneffect == null) continue;
                this.activePotionsMap.put(potioneffect.getPotionID(), potioneffect);
            }
        }
        if (tagCompund.hasKey("HealF", 99)) {
            this.setHealth(tagCompund.getFloat("HealF"));
        } else {
            NBTBase nbtbase = tagCompund.getTag("Health");
            if (nbtbase == null) {
                this.setHealth(this.getMaxHealth());
            } else if (nbtbase.getId() == 5) {
                this.setHealth(((NBTTagFloat)nbtbase).getFloat());
            } else if (nbtbase.getId() == 2) {
                this.setHealth(((NBTTagShort)nbtbase).getShort());
            }
        }
        this.hurtTime = tagCompund.getShort("HurtTime");
        this.deathTime = tagCompund.getShort("DeathTime");
        this.revengeTimer = tagCompund.getInteger("HurtByTimestamp");
    }

    protected void updatePotionEffects() {
        boolean flag1;
        Iterator<Integer> iterator = this.activePotionsMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer integer = iterator.next();
            PotionEffect potioneffect = this.activePotionsMap.get(integer);
            if (!potioneffect.onUpdate(this)) {
                if (this.worldObj.isRemote) continue;
                iterator.remove();
                this.onFinishedPotionEffect(potioneffect);
                continue;
            }
            if (potioneffect.getDuration() % 600 != 0) continue;
            this.onChangedPotionEffect(potioneffect, false);
        }
        if (this.potionsNeedUpdate) {
            if (!this.worldObj.isRemote) {
                this.updatePotionMetadata();
            }
            this.potionsNeedUpdate = false;
        }
        int i = this.dataWatcher.getWatchableObjectInt(7);
        boolean bl = flag1 = this.dataWatcher.getWatchableObjectByte(8) > 0;
        if (i > 0) {
            boolean flag = false;
            if (!this.isInvisible()) {
                flag = this.rand.nextBoolean();
            } else {
                boolean bl2 = flag = this.rand.nextInt(15) == 0;
            }
            if (flag1) {
                flag &= this.rand.nextInt(5) == 0;
            }
            if (flag && i > 0) {
                double d0 = (double)(i >> 16 & 0xFF) / 255.0;
                double d1 = (double)(i >> 8 & 0xFF) / 255.0;
                double d2 = (double)(i >> 0 & 0xFF) / 255.0;
                this.worldObj.spawnParticle(flag1 ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB, this.posX + (this.rand.nextDouble() - 0.5) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5) * (double)this.width, d0, d1, d2, new int[0]);
            }
        }
    }

    protected void updatePotionMetadata() {
        if (this.activePotionsMap.isEmpty()) {
            this.resetPotionEffectMetadata();
            this.setInvisible(false);
        } else {
            int i = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
            this.dataWatcher.updateObject(8, (byte)(PotionHelper.getAreAmbient(this.activePotionsMap.values()) ? 1 : 0));
            this.dataWatcher.updateObject(7, i);
            this.setInvisible(this.isPotionActive(Potion.invisibility.id));
        }
    }

    protected void resetPotionEffectMetadata() {
        this.dataWatcher.updateObject(8, (byte)0);
        this.dataWatcher.updateObject(7, 0);
    }

    public void clearActivePotions() {
        Iterator<Integer> iterator = this.activePotionsMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer integer = iterator.next();
            PotionEffect potioneffect = this.activePotionsMap.get(integer);
            if (this.worldObj.isRemote) continue;
            iterator.remove();
            this.onFinishedPotionEffect(potioneffect);
        }
    }

    public Collection<PotionEffect> getActivePotionEffects() {
        return this.activePotionsMap.values();
    }

    public boolean isPotionActive(int potionId) {
        return this.activePotionsMap.containsKey(potionId);
    }

    public boolean isPotionActive(Potion potionIn) {
        return this.activePotionsMap.containsKey(potionIn.id);
    }

    public PotionEffect getActivePotionEffect(Potion potionIn) {
        return this.activePotionsMap.get(potionIn.id);
    }

    public void addPotionEffect(PotionEffect potioneffectIn) {
        if (this.isPotionApplicable(potioneffectIn)) {
            if (this.activePotionsMap.containsKey(potioneffectIn.getPotionID())) {
                this.activePotionsMap.get(potioneffectIn.getPotionID()).combine(potioneffectIn);
                this.onChangedPotionEffect(this.activePotionsMap.get(potioneffectIn.getPotionID()), true);
            } else {
                this.activePotionsMap.put(potioneffectIn.getPotionID(), potioneffectIn);
                this.onNewPotionEffect(potioneffectIn);
            }
        }
    }

    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        int i;
        return this.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD || (i = potioneffectIn.getPotionID()) != Potion.regeneration.id && i != Potion.poison.id;
    }

    public boolean isEntityUndead() {
        return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    public void removePotionEffectClient(int potionId) {
        this.activePotionsMap.remove(potionId);
    }

    public void removePotionEffect(int potionId) {
        PotionEffect potioneffect = this.activePotionsMap.remove(potionId);
        if (potioneffect != null) {
            this.onFinishedPotionEffect(potioneffect);
        }
    }

    protected void onNewPotionEffect(PotionEffect id) {
        this.potionsNeedUpdate = true;
        if (!this.worldObj.isRemote) {
            Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
        }
    }

    protected void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_) {
        this.potionsNeedUpdate = true;
        if (p_70695_2_ && !this.worldObj.isRemote) {
            Potion.potionTypes[id.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), id.getAmplifier());
            Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
        }
    }

    protected void onFinishedPotionEffect(PotionEffect p_70688_1_) {
        this.potionsNeedUpdate = true;
        if (!this.worldObj.isRemote) {
            Potion.potionTypes[p_70688_1_.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), p_70688_1_.getAmplifier());
        }
    }

    public void heal(float healAmount) {
        float f = this.getHealth();
        if (f > 0.0f) {
            this.setHealth(f + healAmount);
        }
    }

    public final float getHealth() {
        return this.dataWatcher.getWatchableObjectFloat(6);
    }

    public void setHealth(float health) {
        this.dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp_float(health, 0.0f, this.getMaxHealth())));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        if (this.worldObj.isRemote) {
            return false;
        }
        this.entityAge = 0;
        if (this.getHealth() <= 0.0f) {
            return false;
        }
        if (source.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
            return false;
        }
        if ((source == DamageSource.anvil || source == DamageSource.fallingBlock) && this.getEquipmentInSlot(4) != null) {
            this.getEquipmentInSlot(4).damageItem((int)(amount * 4.0f + this.rand.nextFloat() * amount * 2.0f), this);
            amount *= 0.75f;
        }
        this.limbSwingAmount = 1.5f;
        boolean flag = true;
        if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0f) {
            if (amount <= this.lastDamage) {
                return false;
            }
            this.damageEntity(source, amount - this.lastDamage);
            this.lastDamage = amount;
            flag = false;
        } else {
            this.lastDamage = amount;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(source, amount);
            this.maxHurtTime = 10;
            this.hurtTime = 10;
        }
        this.attackedAtYaw = 0.0f;
        Entity entity = source.getEntity();
        if (entity != null) {
            EntityWolf entitywolf;
            if (entity instanceof EntityLivingBase) {
                this.setRevengeTarget((EntityLivingBase)entity);
            }
            if (entity instanceof EntityPlayer) {
                this.recentlyHit = 100;
                this.attackingPlayer = (EntityPlayer)entity;
            } else if (entity instanceof EntityWolf && (entitywolf = (EntityWolf)entity).isTamed()) {
                this.recentlyHit = 100;
                this.attackingPlayer = null;
            }
        }
        if (flag) {
            this.worldObj.setEntityState(this, (byte)2);
            if (source != DamageSource.drown) {
                this.setBeenAttacked();
            }
            if (entity != null) {
                double d1 = entity.posX - this.posX;
                double d0 = entity.posZ - this.posZ;
                while (d1 * d1 + d0 * d0 < 1.0E-4) {
                    d1 = (Math.random() - Math.random()) * 0.01;
                    d0 = (Math.random() - Math.random()) * 0.01;
                }
                this.attackedAtYaw = (float)(MathHelper.func_181159_b(d0, d1) * 180.0 / Math.PI - (double)this.rotationYaw);
                this.knockBack(entity, amount, d1, d0);
            } else {
                this.attackedAtYaw = (int)(Math.random() * 2.0) * 180;
            }
        }
        if (this.getHealth() <= 0.0f) {
            String s = this.getDeathSound();
            if (flag && s != null) {
                this.playSound(s, this.getSoundVolume(), this.getSoundPitch());
            }
            this.onDeath(source);
        } else {
            String s1 = this.getHurtSound();
            if (flag && s1 != null) {
                this.playSound(s1, this.getSoundVolume(), this.getSoundPitch());
            }
        }
        return true;
    }

    public void renderBrokenItemStack(ItemStack stack) {
        this.playSound("random.break", 0.8f, 0.8f + this.worldObj.rand.nextFloat() * 0.4f);
        for (int i = 0; i < 5; ++i) {
            Vec3 vec3 = new Vec3(((double)this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3 = vec3.rotatePitch(-this.rotationPitch * (float)Math.PI / 180.0f);
            vec3 = vec3.rotateYaw(-this.rotationYaw * (float)Math.PI / 180.0f);
            double d0 = (double)(-this.rand.nextFloat()) * 0.6 - 0.3;
            Vec3 vec31 = new Vec3(((double)this.rand.nextFloat() - 0.5) * 0.3, d0, 0.6);
            vec31 = vec31.rotatePitch(-this.rotationPitch * (float)Math.PI / 180.0f);
            vec31 = vec31.rotateYaw(-this.rotationYaw * (float)Math.PI / 180.0f);
            vec31 = vec31.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
            this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05, vec3.z, Item.getIdFromItem(stack.getItem()));
        }
    }

    public void onDeath(DamageSource cause) {
        Entity entity = cause.getEntity();
        EntityLivingBase entitylivingbase = this.func_94060_bK();
        if (this.scoreValue >= 0 && entitylivingbase != null) {
            entitylivingbase.addToPlayerScore(this, this.scoreValue);
        }
        if (entity != null) {
            entity.onKillEntity(this);
        }
        this.dead = true;
        this.getCombatTracker().reset();
        if (!this.worldObj.isRemote) {
            int i = 0;
            if (entity instanceof EntityPlayer) {
                i = EnchantmentHelper.getLootingModifier((EntityLivingBase)entity);
            }
            if (this.canDropLoot() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                this.dropFewItems(this.recentlyHit > 0, i);
                this.dropEquipment(this.recentlyHit > 0, i);
                if (this.recentlyHit > 0 && this.rand.nextFloat() < 0.025f + (float)i * 0.01f) {
                    this.addRandomDrop();
                }
            }
        }
        this.worldObj.setEntityState(this, (byte)3);
    }

    protected void dropEquipment(boolean p_82160_1_, int p_82160_2_) {
    }

    public void knockBack(Entity entityIn, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
        if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
            this.isAirBorne = true;
            float f = MathHelper.sqrt_double(p_70653_3_ * p_70653_3_ + p_70653_5_ * p_70653_5_);
            float f1 = 0.4f;
            this.motionX /= 2.0;
            this.motionY /= 2.0;
            this.motionZ /= 2.0;
            this.motionX -= p_70653_3_ / (double)f * (double)f1;
            this.motionY += (double)f1;
            this.motionZ -= p_70653_5_ / (double)f * (double)f1;
            if (this.motionY > (double)0.4f) {
                this.motionY = 0.4f;
            }
        }
    }

    public String getHurtSound() {
        return "game.neutral.hurt";
    }

    protected String getDeathSound() {
        return "game.neutral.die";
    }

    protected void addRandomDrop() {
    }

    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
    }

    public boolean isOnLadder() {
        int k;
        int j;
        int i = MathHelper.floor_double(this.posX);
        Block block = this.worldObj.getBlockState(new BlockPos(i, j = MathHelper.floor_double(this.getEntityBoundingBox().minY), k = MathHelper.floor_double(this.posZ))).getBlock();
        return !(block != Blocks.ladder && block != Blocks.vine || this instanceof EntityPlayer && ((EntityPlayer)this).isSpectator());
    }

    @Override
    public boolean isEntityAlive() {
        return !this.isDead && this.getHealth() > 0.0f;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(distance, damageMultiplier);
        PotionEffect potioneffect = this.getActivePotionEffect(Potion.jump);
        float f = potioneffect != null ? (float)(potioneffect.getAmplifier() + 1) : 0.0f;
        int i = MathHelper.ceiling_float_int((distance - 3.0f - f) * damageMultiplier);
        if (i > 0) {
            this.playSound(this.getFallSoundString(i), 1.0f, 1.0f);
            this.attackEntityFrom(DamageSource.fall, i);
            int j = MathHelper.floor_double(this.posX);
            int k = MathHelper.floor_double(this.posY - (double)0.2f);
            int l = MathHelper.floor_double(this.posZ);
            Block block = this.worldObj.getBlockState(new BlockPos(j, k, l)).getBlock();
            if (block.getMaterial() != Material.air) {
                Block.SoundType block$soundtype = block.stepSound;
                this.playSound(block$soundtype.getStepSound(), block$soundtype.getVolume() * 0.5f, block$soundtype.getFrequency() * 0.75f);
            }
        }
    }

    protected String getFallSoundString(int damageValue) {
        return damageValue > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
    }

    @Override
    public void performHurtAnimation() {
        this.maxHurtTime = 10;
        this.hurtTime = 10;
        this.attackedAtYaw = 0.0f;
    }

    public int getTotalArmorValue() {
        int i = 0;
        for (ItemStack itemstack : this.getInventory()) {
            if (itemstack == null || !(itemstack.getItem() instanceof ItemArmor)) continue;
            int j = ((ItemArmor)itemstack.getItem()).damageReduceAmount;
            i += j;
        }
        return i;
    }

    protected void damageArmor(float p_70675_1_) {
    }

    protected float applyArmorCalculations(DamageSource source, float damage) {
        if (!source.isUnblockable()) {
            int i = 25 - this.getTotalArmorValue();
            float f = damage * (float)i;
            this.damageArmor(damage);
            damage = f / 25.0f;
        }
        return damage;
    }

    protected float applyPotionDamageCalculations(DamageSource source, float damage) {
        if (source.isDamageAbsolute()) {
            return damage;
        }
        if (this.isPotionActive(Potion.resistance) && source != DamageSource.outOfWorld) {
            int i = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
            int j = 25 - i;
            float f = damage * (float)j;
            damage = f / 25.0f;
        }
        if (damage <= 0.0f) {
            return 0.0f;
        }
        int k = EnchantmentHelper.getEnchantmentModifierDamage(this.getInventory(), source);
        if (k > 20) {
            k = 20;
        }
        if (k > 0 && k <= 20) {
            int l = 25 - k;
            float f1 = damage * (float)l;
            damage = f1 / 25.0f;
        }
        return damage;
    }

    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        if (!this.isEntityInvulnerable(damageSrc)) {
            damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
            float f = damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
            damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0f);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - damageAmount));
            if (damageAmount != 0.0f) {
                float f1 = this.getHealth();
                this.setHealth(f1 - damageAmount);
                this.getCombatTracker().trackDamage(damageSrc, f1, damageAmount);
                this.setAbsorptionAmount(this.getAbsorptionAmount() - damageAmount);
            }
        }
    }

    public CombatTracker getCombatTracker() {
        return this._combatTracker;
    }

    public EntityLivingBase func_94060_bK() {
        return this._combatTracker.func_94550_c() != null ? this._combatTracker.func_94550_c() : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null));
    }

    public final float getMaxHealth() {
        return (float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
    }

    public final int getArrowCountInEntity() {
        return this.dataWatcher.getWatchableObjectByte(9);
    }

    public final void setArrowCountInEntity(int count) {
        this.dataWatcher.updateObject(9, (byte)count);
    }

    public int getArmSwingAnimationEnd() {
        return (int)(this.isPotionActive(Potion.digSpeed) ? (double)(6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1) : (double)(this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6) * Wrapper.getModule(BlockAnimations.class).speed.getValue());
    }

    public void swingItem() {
        if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
            this.swingProgressInt = -1;
            this.isSwingInProgress = true;
            if (this.worldObj instanceof WorldServer) {
                ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S0BPacketAnimation(this, 0));
            }
        }
    }

    public void swingNoPacket() {
        if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
            this.swingProgressInt = -1;
            this.isSwingInProgress = true;
            if (this.worldObj instanceof WorldServer) {
                ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S0BPacketAnimation(this, 0));
            }
        }
    }

    @Override
    public void handleHealthUpdate(byte id) {
        if (id == 2) {
            this.limbSwingAmount = 1.5f;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.maxHurtTime = 10;
            this.hurtTime = 10;
            this.attackedAtYaw = 0.0f;
            String s = this.getHurtSound();
            if (s != null) {
                EventPlayerHurtSound event = new EventPlayerHurtSound(this);
                Wrapper.getEventBus().post(event);
                if (!event.isCancelled()) {
                    this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
                }
            }
            this.attackEntityFrom(DamageSource.generic, 0.0f);
        } else if (id == 3) {
            String s1 = this.getDeathSound();
            if (s1 != null) {
                this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
            }
            this.setHealth(0.0f);
            this.onDeath(DamageSource.generic);
        } else {
            super.handleHealthUpdate(id);
        }
    }

    @Override
    protected void kill() {
        this.attackEntityFrom(DamageSource.outOfWorld, 4.0f);
    }

    protected void updateArmSwingProgress() {
        int i = this.getArmSwingAnimationEnd();
        if (this.isSwingInProgress) {
            ++this.swingProgressInt;
            if (this.swingProgressInt >= i) {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        } else {
            this.swingProgressInt = 0;
        }
        this.swingProgress = (float)this.swingProgressInt / (float)i;
    }

    public IAttributeInstance getEntityAttribute(IAttribute attribute) {
        return this.getAttributeMap().getAttributeInstance(attribute);
    }

    public BaseAttributeMap getAttributeMap() {
        if (this.attributeMap == null) {
            this.attributeMap = new ServersideAttributeMap();
        }
        return this.attributeMap;
    }

    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    public abstract ItemStack getHeldItem();

    public abstract ItemStack getEquipmentInSlot(int var1);

    public abstract ItemStack getCurrentArmor(int var1);

    @Override
    public abstract void setCurrentItemOrArmor(int var1, ItemStack var2);

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (iattributeinstance.getModifier(sprintingSpeedBoostModifierUUID) != null) {
            iattributeinstance.removeModifier(sprintingSpeedBoostModifier);
        }
        if (sprinting) {
            iattributeinstance.applyModifier(sprintingSpeedBoostModifier);
        }
    }

    @Override
    public abstract ItemStack[] getInventory();

    public float getSoundVolume() {
        return 1.0f;
    }

    public float getSoundPitch() {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.5f : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f;
    }

    protected boolean isMovementBlocked() {
        return this.getHealth() <= 0.0f;
    }

    public void dismountEntity(Entity p_110145_1_) {
        double d0 = p_110145_1_.posX;
        double d1 = p_110145_1_.getEntityBoundingBox().minY + (double)p_110145_1_.height;
        double d2 = p_110145_1_.posZ;
        int i = 1;
        for (int j = -i; j <= i; ++j) {
            for (int k = -i; k < i; ++k) {
                if (j == 0 && k == 0) continue;
                int l = (int)(this.posX + (double)j);
                int i1 = (int)(this.posZ + (double)k);
                AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().offset(j, 1.0, k);
                if (!this.worldObj.func_147461_a(axisalignedbb).isEmpty()) continue;
                if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(l, (int)this.posY, i1))) {
                    this.setPositionAndUpdate(this.posX + (double)j, this.posY + 1.0, this.posZ + (double)k);
                    return;
                }
                if (!World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(l, (int)this.posY - 1, i1)) && this.worldObj.getBlockState(new BlockPos(l, (int)this.posY - 1, i1)).getBlock().getMaterial() != Material.water) continue;
                d0 = this.posX + (double)j;
                d1 = this.posY + 1.0;
                d2 = this.posZ + (double)k;
            }
        }
        this.setPositionAndUpdate(d0, d1, d2);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return false;
    }

    protected float getJumpUpwardsMotion() {
        return 0.42f;
    }

    protected void jump() {
        this.motionY = this.getJumpUpwardsMotion();
        if (this.isPotionActive(Potion.jump)) {
            this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f);
        }
        if (this.isSprinting()) {
            Sprint sprint = Wrapper.getModule(Sprint.class);
            float f = (sprint.isEnabled() && sprint.omni.getValue() != false ? MovementUtil.getDirection() : this.rotationYaw) * ((float)Math.PI / 180);
            this.motionX -= (double)(MathHelper.sin(f) * 0.2f);
            this.motionZ += (double)(MathHelper.cos(f) * 0.2f);
        }
        this.isAirBorne = true;
    }

    protected void jump(EventMove eventMove) {
        this.motionY = this.getJumpUpwardsMotion();
        eventMove.setY(this.motionY);
        if (this.isPotionActive(Potion.jump)) {
            eventMove.setY(this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f));
        }
        if (this.isSprinting()) {
            Sprint sprint = Wrapper.getModule(Sprint.class);
            float f = (sprint.isEnabled() && sprint.omni.getValue() != false ? MovementUtil.getDirection() : this.rotationYaw) * ((float)Math.PI / 180);
            eventMove.setX(this.motionX -= (double)(MathHelper.sin(f) * 0.2f));
            eventMove.setZ(this.motionZ += (double)(MathHelper.cos(f) * 0.2f));
        }
        this.isAirBorne = true;
    }

    protected void updateAITick() {
        this.motionY += (double)0.04f;
    }

    protected void handleJumpLava() {
        this.motionY += (double)0.04f;
    }

    public void moveEntityWithHeading(float strafe, float forward) {
        if (this.isServerWorld()) {
            if (!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
                if (!this.isInLava() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
                    float f4 = 0.91f;
                    if (this.onGround) {
                        f4 = this.worldObj.getBlockState((BlockPos)new BlockPos((int)MathHelper.floor_double((double)this.posX), (int)(MathHelper.floor_double((double)this.getEntityBoundingBox().minY) - 1), (int)MathHelper.floor_double((double)this.posZ))).getBlock().slipperiness * 0.91f;
                    }
                    float f = 0.16277136f / (f4 * f4 * f4);
                    float f5 = this.onGround ? this.getAIMoveSpeed() * f : this.jumpMovementFactor;
                    this.moveFlying(strafe, forward, f5);
                    f4 = 0.91f;
                    if (this.onGround) {
                        f4 = this.worldObj.getBlockState((BlockPos)new BlockPos((int)MathHelper.floor_double((double)this.posX), (int)(MathHelper.floor_double((double)this.getEntityBoundingBox().minY) - 1), (int)MathHelper.floor_double((double)this.posZ))).getBlock().slipperiness * 0.91f;
                    }
                    if (this.isOnLadder()) {
                        boolean flag;
                        float f6 = 0.15f;
                        this.motionX = MathHelper.clamp_double(this.motionX, -f6, f6);
                        this.motionZ = MathHelper.clamp_double(this.motionZ, -f6, f6);
                        this.fallDistance = 0.0f;
                        if (this.motionY < -0.15) {
                            this.motionY = -0.15;
                        }
                        boolean bl = flag = this.isSneaking() && this instanceof EntityPlayer;
                        if (flag && this.motionY < 0.0) {
                            this.motionY = 0.0;
                        }
                    }
                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    if (this.isCollidedHorizontally && this.isOnLadder()) {
                        this.motionY = 0.2;
                    }
                    this.motionY = !(!this.worldObj.isRemote || this.worldObj.isBlockLoaded(new BlockPos((int)this.posX, 0, (int)this.posZ)) && this.worldObj.getChunkFromBlockCoords(new BlockPos((int)this.posX, 0, (int)this.posZ)).isLoaded()) ? (this.posY > 0.0 ? -0.1 : 0.0) : (this.motionY -= 0.08);
                    this.motionY *= (double)0.98f;
                    this.motionX *= (double)f4;
                    this.motionZ *= (double)f4;
                } else {
                    double d1 = this.posY;
                    this.moveFlying(strafe, forward, 0.02f);
                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    this.motionX *= 0.5;
                    this.motionY *= 0.5;
                    this.motionZ *= 0.5;
                    this.motionY -= 0.02;
                    if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double)0.6f - this.posY + d1, this.motionZ)) {
                        this.motionY = 0.3f;
                    }
                }
            } else {
                double d0 = this.posY;
                float f1 = 0.8f;
                float f2 = 0.02f;
                float f3 = EnchantmentHelper.getDepthStriderModifier(this);
                if (f3 > 3.0f) {
                    f3 = 3.0f;
                }
                if (!this.onGround) {
                    f3 *= 0.5f;
                }
                if (f3 > 0.0f) {
                    f1 += (0.54600006f - f1) * f3 / 3.0f;
                    f2 += (this.getAIMoveSpeed() * 1.0f - f2) * f3 / 3.0f;
                }
                this.moveFlying(strafe, forward, f2);
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= (double)f1;
                this.motionY *= (double)0.8f;
                this.motionZ *= (double)f1;
                this.motionY -= 0.02;
                if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double)0.6f - this.posY + d0, this.motionZ)) {
                    this.motionY = 0.3f;
                }
            }
        }
        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d2 = this.posX - this.prevPosX;
        double d3 = this.posZ - this.prevPosZ;
        float f7 = MathHelper.sqrt_double(d2 * d2 + d3 * d3) * 4.0f;
        if (f7 > 1.0f) {
            f7 = 1.0f;
        }
        this.limbSwingAmount += (f7 - this.limbSwingAmount) * 0.4f;
        this.limbSwing += this.limbSwingAmount;
    }

    public float getAIMoveSpeed() {
        return this.landMovementFactor;
    }

    public void setAIMoveSpeed(float speedIn) {
        this.landMovementFactor = speedIn;
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        this.setLastAttacker(entityIn);
        return false;
    }

    public boolean isPlayerSleeping() {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            int i = this.getArrowCountInEntity();
            if (i > 0) {
                if (this.arrowHitTimer <= 0) {
                    this.arrowHitTimer = 20 * (30 - i);
                }
                --this.arrowHitTimer;
                if (this.arrowHitTimer <= 0) {
                    this.setArrowCountInEntity(i - 1);
                }
            }
            for (int j = 0; j < 5; ++j) {
                ItemStack itemstack = this.previousEquipment[j];
                ItemStack itemstack1 = this.getEquipmentInSlot(j);
                if (ItemStack.areItemStacksEqual(itemstack1, itemstack)) continue;
                ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S04PacketEntityEquipment(this.getEntityId(), j, itemstack1));
                if (itemstack != null) {
                    this.attributeMap.removeAttributeModifiers(itemstack.getAttributeModifiers());
                }
                if (itemstack1 != null) {
                    this.attributeMap.applyAttributeModifiers(itemstack1.getAttributeModifiers());
                }
                this.previousEquipment[j] = itemstack1 == null ? null : itemstack1.copy();
            }
            if (this.ticksExisted % 20 == 0) {
                this.getCombatTracker().reset();
            }
        }
        this.onLivingUpdate();
        double d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f = (float)(d0 * d0 + d1 * d1);
        float f1 = this.renderYawOffset;
        float f2 = 0.0f;
        this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
        float f3 = 0.0f;
        if (f > 0.0025000002f) {
            f3 = 1.0f;
            f2 = (float)Math.sqrt(f) * 3.0f;
            f1 = (float)MathHelper.func_181159_b(d1, d0) * 180.0f / (float)Math.PI - 90.0f;
        }
        if (this.swingProgress > 0.0f) {
            f1 = this.rotationYaw;
        }
        if (!this.onGround) {
            f3 = 0.0f;
        }
        this.onGroundSpeedFactor += (f3 - this.onGroundSpeedFactor) * 0.3f;
        this.worldObj.theProfiler.startSection("headTurn");
        f2 = this.func_110146_f(f1, f2);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("rangeChecks");
        while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
            this.prevRotationYaw -= 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
            this.prevRotationYaw += 360.0f;
        }
        while (this.renderYawOffset - this.prevRenderYawOffset < -180.0f) {
            this.prevRenderYawOffset -= 360.0f;
        }
        while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0f) {
            this.prevRenderYawOffset += 360.0f;
        }
        while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
            this.prevRotationPitch -= 360.0f;
        }
        while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
            this.prevRotationPitch += 360.0f;
        }
        while (this.rotationYawHead - this.prevRotationYawHead < -180.0f) {
            this.prevRotationYawHead -= 360.0f;
        }
        while (this.rotationYawHead - this.prevRotationYawHead >= 180.0f) {
            this.prevRotationYawHead += 360.0f;
        }
        this.worldObj.theProfiler.endSection();
        this.movedDistance += f2;
    }

    protected float func_110146_f(float p_110146_1_, float p_110146_2_) {
        boolean flag;
        float f = MathHelper.wrapAngleTo180_float(p_110146_1_ - this.renderYawOffset);
        this.renderYawOffset += f * 0.3f;
        float f1 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
        boolean bl = flag = f1 < -90.0f || f1 >= 90.0f;
        if (f1 < -75.0f) {
            f1 = -75.0f;
        }
        if (f1 >= 75.0f) {
            f1 = 75.0f;
        }
        this.renderYawOffset = this.rotationYaw - f1;
        if (f1 * f1 > 2500.0f) {
            this.renderYawOffset += f1 * 0.2f;
        }
        if (flag) {
            p_110146_2_ *= -1.0f;
        }
        return p_110146_2_;
    }

    public void onLivingUpdate() {
        if (this.jumpTicks > 0) {
            --this.jumpTicks;
        }
        if (this.newPosRotationIncrements > 0) {
            double d0 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
            double d1 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
            double d2 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
            double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.newPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        } else if (!this.isServerWorld()) {
            this.motionX *= 0.98;
            this.motionY *= 0.98;
            this.motionZ *= 0.98;
        }
        if (Math.abs(this.motionX) < 0.005) {
            this.motionX = 0.0;
        }
        if (Math.abs(this.motionY) < 0.005) {
            this.motionY = 0.0;
        }
        if (Math.abs(this.motionZ) < 0.005) {
            this.motionZ = 0.0;
        }
        this.worldObj.theProfiler.startSection("ai");
        if (this.isMovementBlocked()) {
            this.isJumping = false;
            this.moveStrafing = 0.0f;
            this.moveForward = 0.0f;
            this.randomYawVelocity = 0.0f;
        } else if (this.isServerWorld()) {
            this.worldObj.theProfiler.startSection("newAi");
            this.updateEntityActionState();
            this.worldObj.theProfiler.endSection();
        }
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("jump");
        if (this.isJumping) {
            if (this.isInWater()) {
                this.updateAITick();
            } else if (this.isInLava()) {
                this.handleJumpLava();
            } else if (this.onGround && this.jumpTicks == 0) {
                this.jump();
                this.jumpTicks = 10;
            }
        } else {
            this.jumpTicks = 0;
        }
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("travel");
        this.moveStrafing *= 0.98f;
        this.moveForward *= 0.98f;
        this.randomYawVelocity *= 0.9f;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("push");
        if (!this.worldObj.isRemote) {
            this.collideWithNearbyEntities();
        }
        this.worldObj.theProfiler.endSection();
    }

    protected void updateEntityActionState() {
    }

    protected void collideWithNearbyEntities() {
        List<Entity> list = this.worldObj.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(0.2f, 0.0, 0.2f), (Predicate<? super Entity>)Predicates.and(EntitySelectors.NOT_SPECTATING, (Predicate)new Predicate<Entity>(){

            public boolean apply(Entity p_apply_1_) {
                return p_apply_1_.canBePushed();
            }
        }));
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                this.collideWithEntity(entity);
            }
        }
    }

    protected void collideWithEntity(Entity p_82167_1_) {
        p_82167_1_.applyEntityCollision(this);
    }

    @Override
    public void mountEntity(Entity entityIn) {
        if (this.ridingEntity != null && entityIn == null) {
            if (!this.worldObj.isRemote) {
                this.dismountEntity(this.ridingEntity);
            }
            if (this.ridingEntity != null) {
                this.ridingEntity.riddenByEntity = null;
            }
            this.ridingEntity = null;
        } else {
            super.mountEntity(entityIn);
        }
    }

    @Override
    public void updateRidden() {
        super.updateRidden();
        this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
        this.onGroundSpeedFactor = 0.0f;
        this.fallDistance = 0.0f;
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
        this.newPosX = x;
        this.newPosY = y;
        this.newPosZ = z;
        this.newRotationYaw = yaw;
        this.newRotationPitch = pitch;
        this.newPosRotationIncrements = posRotationIncrements;
    }

    public void setJumping(boolean p_70637_1_) {
        this.isJumping = p_70637_1_;
    }

    public void onItemPickup(Entity p_71001_1_, int p_71001_2_) {
        if (!p_71001_1_.isDead && !this.worldObj.isRemote) {
            EntityTracker entitytracker = ((WorldServer)this.worldObj).getEntityTracker();
            if (p_71001_1_ instanceof EntityItem) {
                entitytracker.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }
            if (p_71001_1_ instanceof EntityArrow) {
                entitytracker.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }
            if (p_71001_1_ instanceof EntityXPOrb) {
                entitytracker.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }
        }
    }

    public boolean canEntityBeSeen(Entity entityIn) {
        return this.worldObj.rayTraceBlocks(new Vec3(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), new Vec3(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ)) == null;
    }

    @Override
    public Vec3 getLookVec() {
        return this.getLook(1.0f);
    }

    @Override
    public Vec3 getLook(float partialTicks) {
        if (partialTicks == 1.0f) {
            return EntityLivingBase.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
        }
        float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
        float f1 = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
        return EntityLivingBase.getVectorForRotation(f, f1);
    }

    public float getSwingProgress(float partialTickTime) {
        float f = this.swingProgress - this.prevSwingProgress;
        if (f < 0.0f) {
            f += 1.0f;
        }
        return this.prevSwingProgress + f * partialTickTime;
    }

    public boolean isServerWorld() {
        return !this.worldObj.isRemote;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public boolean canBePushed() {
        return !this.isDead;
    }

    @Override
    protected void setBeenAttacked() {
        this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
    }

    @Override
    public float getRotationYawHead() {
        return this.rotationYawHead;
    }

    @Override
    public void setRotationYawHead(float rotation) {
        this.rotationYawHead = rotation;
    }

    @Override
    public void func_181013_g(float p_181013_1_) {
        this.renderYawOffset = p_181013_1_;
    }

    public float getAbsorptionAmount() {
        return this.absorptionAmount;
    }

    public void setAbsorptionAmount(float amount) {
        if (amount < 0.0f) {
            amount = 0.0f;
        }
        this.absorptionAmount = amount;
    }

    public Team getTeam() {
        return this.worldObj.getScoreboard().getPlayersTeam(this.getUniqueID().toString());
    }

    public boolean isOnSameTeam(EntityLivingBase otherEntity) {
        return this.isOnTeam(otherEntity.getTeam());
    }

    public boolean isOnTeam(Team p_142012_1_) {
        return this.getTeam() != null ? this.getTeam().isSameTeam(p_142012_1_) : false;
    }

    public void sendEnterCombat() {
    }

    public void sendEndCombat() {
    }

    protected void markPotionsDirty() {
        this.potionsNeedUpdate = true;
    }
}

