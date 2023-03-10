/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import optifine.BlockPosM;
import optifine.Config;
import optifine.Reflector;

public abstract class EntityLiving
extends EntityLivingBase {
    public int livingSoundTime;
    protected int experienceValue;
    private EntityLookHelper lookHelper;
    protected EntityMoveHelper moveHelper;
    protected EntityJumpHelper jumpHelper;
    private EntityBodyHelper bodyHelper;
    protected PathNavigate navigator;
    protected final EntityAITasks tasks;
    protected final EntityAITasks targetTasks;
    private EntityLivingBase attackTarget;
    private EntitySenses senses;
    private ItemStack[] equipment = new ItemStack[5];
    protected float[] equipmentDropChances = new float[5];
    private boolean canPickUpLoot;
    private boolean persistenceRequired;
    private boolean isLeashed;
    private Entity leashedToEntity;
    private NBTTagCompound leashNBTTag;
    private static final String __OBFID = "CL_00001550";
    public int randomMobsId = 0;
    public BiomeGenBase spawnBiome = null;
    public BlockPos spawnPosition = null;

    public EntityLiving(World worldIn) {
        super(worldIn);
        this.tasks = new EntityAITasks(worldIn != null && worldIn.theProfiler != null ? worldIn.theProfiler : null);
        this.targetTasks = new EntityAITasks(worldIn != null && worldIn.theProfiler != null ? worldIn.theProfiler : null);
        this.lookHelper = new EntityLookHelper(this);
        this.moveHelper = new EntityMoveHelper(this);
        this.jumpHelper = new EntityJumpHelper(this);
        this.bodyHelper = new EntityBodyHelper(this);
        this.navigator = this.getNewNavigator(worldIn);
        this.senses = new EntitySenses(this);
        for (int i = 0; i < this.equipmentDropChances.length; ++i) {
            this.equipmentDropChances[i] = 0.085f;
        }
        UUID uuid = this.getUniqueID();
        long j = uuid.getLeastSignificantBits();
        this.randomMobsId = (int)(j & Integer.MAX_VALUE);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
    }

    protected PathNavigate getNewNavigator(World worldIn) {
        return new PathNavigateGround(this, worldIn);
    }

    public EntityLookHelper getLookHelper() {
        return this.lookHelper;
    }

    public EntityMoveHelper getMoveHelper() {
        return this.moveHelper;
    }

    public EntityJumpHelper getJumpHelper() {
        return this.jumpHelper;
    }

    public PathNavigate getNavigator() {
        return this.navigator;
    }

    public EntitySenses getEntitySenses() {
        return this.senses;
    }

    public EntityLivingBase getAttackTarget() {
        return this.attackTarget;
    }

    public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
        this.attackTarget = entitylivingbaseIn;
        Reflector.callVoid(Reflector.ForgeHooks_onLivingSetAttackTarget, this, entitylivingbaseIn);
    }

    public boolean canAttackClass(Class cls) {
        return cls != EntityGhast.class;
    }

    public void eatGrassBonus() {
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(15, (byte)0);
    }

    public int getTalkInterval() {
        return 80;
    }

    public void playLivingSound() {
        String s = this.getLivingSound();
        if (s != null) {
            this.playSound(s, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("mobBaseTick");
        if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
            this.livingSoundTime = -this.getTalkInterval();
            this.playLivingSound();
        }
        this.worldObj.theProfiler.endSection();
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        if (this.experienceValue > 0) {
            int i = this.experienceValue;
            ItemStack[] aitemstack = this.getInventory();
            for (int j = 0; j < aitemstack.length; ++j) {
                if (aitemstack[j] == null || !(this.equipmentDropChances[j] <= 1.0f)) continue;
                i += 1 + this.rand.nextInt(3);
            }
            return i;
        }
        return this.experienceValue;
    }

    public void spawnExplosionParticle() {
        if (this.worldObj.isRemote) {
            for (int i = 0; i < 20; ++i) {
                double d0 = this.rand.nextGaussian() * 0.02;
                double d1 = this.rand.nextGaussian() * 0.02;
                double d2 = this.rand.nextGaussian() * 0.02;
                double d3 = 10.0;
                this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width - d0 * d3, this.posY + (double)(this.rand.nextFloat() * this.height) - d1 * d3, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width - d2 * d3, d0, d1, d2, new int[0]);
            }
        } else {
            this.worldObj.setEntityState(this, (byte)20);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 20) {
            this.spawnExplosionParticle();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void onUpdate() {
        if (Config.isSmoothWorld() && this.canSkipUpdate()) {
            this.onUpdateMinimal();
        } else {
            super.onUpdate();
            if (!this.worldObj.isRemote) {
                this.updateLeashedState();
            }
        }
    }

    @Override
    protected float func_110146_f(float p_110146_1_, float p_110146_2_) {
        this.bodyHelper.updateRenderAngles();
        return p_110146_2_;
    }

    protected String getLivingSound() {
        return null;
    }

    protected Item getDropItem() {
        return null;
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        Item item = this.getDropItem();
        if (item != null) {
            int i = this.rand.nextInt(3);
            if (p_70628_2_ > 0) {
                i += this.rand.nextInt(p_70628_2_ + 1);
            }
            for (int j = 0; j < i; ++j) {
                this.dropItem(item, 1);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("CanPickUpLoot", this.canPickUpLoot());
        tagCompound.setBoolean("PersistenceRequired", this.persistenceRequired);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.equipment.length; ++i) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            if (this.equipment[i] != null) {
                this.equipment[i].writeToNBT(nbttagcompound);
            }
            nbttaglist.appendTag(nbttagcompound);
        }
        tagCompound.setTag("Equipment", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        for (int j = 0; j < this.equipmentDropChances.length; ++j) {
            nbttaglist1.appendTag(new NBTTagFloat(this.equipmentDropChances[j]));
        }
        tagCompound.setTag("DropChances", nbttaglist1);
        tagCompound.setBoolean("Leashed", this.isLeashed);
        if (this.leashedToEntity != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            if (this.leashedToEntity instanceof EntityLivingBase) {
                nbttagcompound1.setLong("UUIDMost", this.leashedToEntity.getUniqueID().getMostSignificantBits());
                nbttagcompound1.setLong("UUIDLeast", this.leashedToEntity.getUniqueID().getLeastSignificantBits());
            } else if (this.leashedToEntity instanceof EntityHanging) {
                BlockPos blockpos = ((EntityHanging)this.leashedToEntity).getHangingPosition();
                nbttagcompound1.setInteger("X", blockpos.getX());
                nbttagcompound1.setInteger("Y", blockpos.getY());
                nbttagcompound1.setInteger("Z", blockpos.getZ());
            }
            tagCompound.setTag("Leash", nbttagcompound1);
        }
        if (this.isAIDisabled()) {
            tagCompound.setBoolean("NoAI", this.isAIDisabled());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        if (tagCompund.hasKey("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot(tagCompund.getBoolean("CanPickUpLoot"));
        }
        this.persistenceRequired = tagCompund.getBoolean("PersistenceRequired");
        if (tagCompund.hasKey("Equipment", 9)) {
            NBTTagList nbttaglist = tagCompund.getTagList("Equipment", 10);
            for (int i = 0; i < this.equipment.length; ++i) {
                this.equipment[i] = ItemStack.loadItemStackFromNBT(nbttaglist.getCompoundTagAt(i));
            }
        }
        if (tagCompund.hasKey("DropChances", 9)) {
            NBTTagList nbttaglist1 = tagCompund.getTagList("DropChances", 5);
            for (int j = 0; j < nbttaglist1.tagCount(); ++j) {
                this.equipmentDropChances[j] = nbttaglist1.getFloatAt(j);
            }
        }
        this.isLeashed = tagCompund.getBoolean("Leashed");
        if (this.isLeashed && tagCompund.hasKey("Leash", 10)) {
            this.leashNBTTag = tagCompund.getCompoundTag("Leash");
        }
        this.setNoAI(tagCompund.getBoolean("NoAI"));
    }

    public void setMoveForward(float p_70657_1_) {
        this.moveForward = p_70657_1_;
    }

    @Override
    public void setAIMoveSpeed(float speedIn) {
        super.setAIMoveSpeed(speedIn);
        this.setMoveForward(speedIn);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.worldObj.theProfiler.startSection("looting");
        if (!this.worldObj.isRemote && this.canPickUpLoot() && !this.dead && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
            for (EntityItem entityitem : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0, 0.0, 1.0))) {
                if (entityitem.isDead || entityitem.getEntityItem() == null || entityitem.cannotPickup()) continue;
                this.updateEquipmentIfNeeded(entityitem);
            }
        }
        this.worldObj.theProfiler.endSection();
    }

    protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
        ItemStack itemstack = itemEntity.getEntityItem();
        int i = EntityLiving.getArmorPosition(itemstack);
        if (i > -1) {
            boolean flag = true;
            ItemStack itemstack1 = this.getEquipmentInSlot(i);
            if (itemstack1 != null) {
                if (i == 0) {
                    if (itemstack.getItem() instanceof ItemSword && !(itemstack1.getItem() instanceof ItemSword)) {
                        flag = true;
                    } else if (itemstack.getItem() instanceof ItemSword && itemstack1.getItem() instanceof ItemSword) {
                        ItemSword itemsword = (ItemSword)itemstack.getItem();
                        ItemSword itemsword1 = (ItemSword)itemstack1.getItem();
                        flag = itemsword.getDamageVsEntity() != itemsword1.getDamageVsEntity() ? itemsword.getDamageVsEntity() > itemsword1.getDamageVsEntity() : itemstack.getMetadata() > itemstack1.getMetadata() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
                    } else {
                        flag = itemstack.getItem() instanceof ItemBow && itemstack1.getItem() instanceof ItemBow ? itemstack.hasTagCompound() && !itemstack1.hasTagCompound() : false;
                    }
                } else if (itemstack.getItem() instanceof ItemArmor && !(itemstack1.getItem() instanceof ItemArmor)) {
                    flag = true;
                } else if (itemstack.getItem() instanceof ItemArmor && itemstack1.getItem() instanceof ItemArmor) {
                    ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
                    ItemArmor itemarmor1 = (ItemArmor)itemstack1.getItem();
                    flag = itemarmor.damageReduceAmount != itemarmor1.damageReduceAmount ? itemarmor.damageReduceAmount > itemarmor1.damageReduceAmount : itemstack.getMetadata() > itemstack1.getMetadata() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
                } else {
                    flag = false;
                }
            }
            if (flag && this.func_175448_a(itemstack)) {
                EntityPlayer entityplayer;
                if (itemstack1 != null && this.rand.nextFloat() - 0.1f < this.equipmentDropChances[i]) {
                    this.entityDropItem(itemstack1, 0.0f);
                }
                if (itemstack.getItem() == Items.diamond && itemEntity.getThrower() != null && (entityplayer = this.worldObj.getPlayerEntityByName(itemEntity.getThrower())) != null) {
                    entityplayer.triggerAchievement(AchievementList.diamondsToYou);
                }
                this.setCurrentItemOrArmor(i, itemstack);
                this.equipmentDropChances[i] = 2.0f;
                this.persistenceRequired = true;
                this.onItemPickup(itemEntity, 1);
                itemEntity.setDead();
            }
        }
    }

    protected boolean func_175448_a(ItemStack stack) {
        return true;
    }

    protected boolean canDespawn() {
        return true;
    }

    protected void despawnEntity() {
        Object object = null;
        Object object1 = Reflector.getFieldValue(Reflector.Event_Result_DEFAULT);
        Object object2 = Reflector.getFieldValue(Reflector.Event_Result_DENY);
        if (this.persistenceRequired) {
            this.entityAge = 0;
        } else if ((this.entityAge & 0x1F) == 31 && (object = Reflector.call(Reflector.ForgeEventFactory_canEntityDespawn, this)) != object1) {
            if (object == object2) {
                this.entityAge = 0;
            } else {
                this.setDead();
            }
        } else {
            EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, -1.0);
            if (entityplayer != null) {
                double d0 = entityplayer.posX - this.posX;
                double d1 = entityplayer.posY - this.posY;
                double d2 = entityplayer.posZ - this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (this.canDespawn() && d3 > 16384.0) {
                    this.setDead();
                }
                if (this.entityAge > 600 && this.rand.nextInt(800) == 0 && d3 > 1024.0 && this.canDespawn()) {
                    this.setDead();
                } else if (d3 < 1024.0) {
                    this.entityAge = 0;
                }
            }
        }
    }

    @Override
    protected final void updateEntityActionState() {
        ++this.entityAge;
        this.worldObj.theProfiler.startSection("checkDespawn");
        this.despawnEntity();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("sensing");
        this.senses.clearSensingCache();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("targetSelector");
        this.targetTasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("goalSelector");
        this.tasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("navigation");
        this.navigator.onUpdateNavigation();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("mob tick");
        this.updateAITasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("controls");
        this.worldObj.theProfiler.startSection("move");
        this.moveHelper.onUpdateMoveHelper();
        this.worldObj.theProfiler.endStartSection("look");
        this.lookHelper.onUpdateLook();
        this.worldObj.theProfiler.endStartSection("jump");
        this.jumpHelper.doJump();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.endSection();
    }

    protected void updateAITasks() {
    }

    public int getVerticalFaceSpeed() {
        return 40;
    }

    public void faceEntity(Entity entityIn, float p_70625_2_, float p_70625_3_) {
        double d2;
        double d0 = entityIn.posX - this.posX;
        double d1 = entityIn.posZ - this.posZ;
        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entityIn;
            d2 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
        } else {
            d2 = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0 - (this.posY + (double)this.getEyeHeight());
        }
        double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f = (float)(MathHelper.func_181159_b(d1, d0) * 180.0 / Math.PI) - 90.0f;
        float f1 = (float)(-(MathHelper.func_181159_b(d2, d3) * 180.0 / Math.PI));
        this.rotationPitch = this.updateRotation(this.rotationPitch, f1, p_70625_3_);
        this.rotationYaw = this.updateRotation(this.rotationYaw, f, p_70625_2_);
    }

    private float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_) {
        float f = MathHelper.wrapAngleTo180_float(p_70663_2_ - p_70663_1_);
        if (f > p_70663_3_) {
            f = p_70663_3_;
        }
        if (f < -p_70663_3_) {
            f = -p_70663_3_;
        }
        return p_70663_1_ + f;
    }

    public boolean getCanSpawnHere() {
        return true;
    }

    public boolean isNotColliding() {
        return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this) && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
    }

    public float getRenderSizeModifier() {
        return 1.0f;
    }

    public int getMaxSpawnedInChunk() {
        return 4;
    }

    @Override
    public int getMaxFallHeight() {
        if (this.getAttackTarget() == null) {
            return 3;
        }
        int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33f);
        if ((i -= (3 - this.worldObj.getDifficulty().getDifficultyId()) * 4) < 0) {
            i = 0;
        }
        return i + 3;
    }

    @Override
    public ItemStack getHeldItem() {
        return this.equipment[0];
    }

    @Override
    public ItemStack getEquipmentInSlot(int slotIn) {
        return this.equipment[slotIn];
    }

    @Override
    public ItemStack getCurrentArmor(int slotIn) {
        return this.equipment[slotIn + 1];
    }

    @Override
    public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
        this.equipment[slotIn] = stack;
    }

    @Override
    public ItemStack[] getInventory() {
        return this.equipment;
    }

    @Override
    protected void dropEquipment(boolean p_82160_1_, int p_82160_2_) {
        for (int i = 0; i < this.getInventory().length; ++i) {
            boolean flag;
            ItemStack itemstack = this.getEquipmentInSlot(i);
            boolean bl = flag = this.equipmentDropChances[i] > 1.0f;
            if (itemstack == null || !p_82160_1_ && !flag || !(this.rand.nextFloat() - (float)p_82160_2_ * 0.01f < this.equipmentDropChances[i])) continue;
            if (!flag && itemstack.isItemStackDamageable()) {
                int j = Math.max(itemstack.getMaxDamage() - 25, 1);
                int k = itemstack.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(j) + 1);
                if (k > j) {
                    k = j;
                }
                if (k < 1) {
                    k = 1;
                }
                itemstack.setItemDamage(k);
            }
            this.entityDropItem(itemstack, 0.0f);
        }
    }

    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        if (this.rand.nextFloat() < 0.15f * difficulty.getClampedAdditionalDifficulty()) {
            float f;
            int i = this.rand.nextInt(2);
            float f2 = f = this.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.1f : 0.25f;
            if (this.rand.nextFloat() < 0.095f) {
                ++i;
            }
            if (this.rand.nextFloat() < 0.095f) {
                ++i;
            }
            if (this.rand.nextFloat() < 0.095f) {
                ++i;
            }
            for (int j = 3; j >= 0; --j) {
                Item item;
                ItemStack itemstack = this.getCurrentArmor(j);
                if (j < 3 && this.rand.nextFloat() < f) break;
                if (itemstack != null || (item = EntityLiving.getArmorItemForSlot(j + 1, i)) == null) continue;
                this.setCurrentItemOrArmor(j + 1, new ItemStack(item));
            }
        }
    }

    public static int getArmorPosition(ItemStack stack) {
        if (stack.getItem() != Item.getItemFromBlock(Blocks.pumpkin) && stack.getItem() != Items.skull) {
            if (stack.getItem() instanceof ItemArmor) {
                switch (((ItemArmor)stack.getItem()).armorType) {
                    case 0: {
                        return 4;
                    }
                    case 1: {
                        return 3;
                    }
                    case 2: {
                        return 2;
                    }
                    case 3: {
                        return 1;
                    }
                }
            }
            return 0;
        }
        return 4;
    }

    public static Item getArmorItemForSlot(int armorSlot, int itemTier) {
        switch (armorSlot) {
            case 4: {
                if (itemTier == 0) {
                    return Items.leather_helmet;
                }
                if (itemTier == 1) {
                    return Items.golden_helmet;
                }
                if (itemTier == 2) {
                    return Items.chainmail_helmet;
                }
                if (itemTier == 3) {
                    return Items.iron_helmet;
                }
                if (itemTier == 4) {
                    return Items.diamond_helmet;
                }
            }
            case 3: {
                if (itemTier == 0) {
                    return Items.leather_chestplate;
                }
                if (itemTier == 1) {
                    return Items.golden_chestplate;
                }
                if (itemTier == 2) {
                    return Items.chainmail_chestplate;
                }
                if (itemTier == 3) {
                    return Items.iron_chestplate;
                }
                if (itemTier == 4) {
                    return Items.diamond_chestplate;
                }
            }
            case 2: {
                if (itemTier == 0) {
                    return Items.leather_leggings;
                }
                if (itemTier == 1) {
                    return Items.golden_leggings;
                }
                if (itemTier == 2) {
                    return Items.chainmail_leggings;
                }
                if (itemTier == 3) {
                    return Items.iron_leggings;
                }
                if (itemTier == 4) {
                    return Items.diamond_leggings;
                }
            }
            case 1: {
                if (itemTier == 0) {
                    return Items.leather_boots;
                }
                if (itemTier == 1) {
                    return Items.golden_boots;
                }
                if (itemTier == 2) {
                    return Items.chainmail_boots;
                }
                if (itemTier == 3) {
                    return Items.iron_boots;
                }
                if (itemTier != 4) break;
                return Items.diamond_boots;
            }
        }
        return null;
    }

    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
        float f = difficulty.getClampedAdditionalDifficulty();
        if (this.getHeldItem() != null && this.rand.nextFloat() < 0.25f * f) {
            EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItem(), (int)(5.0f + f * (float)this.rand.nextInt(18)));
        }
        for (int i = 0; i < 4; ++i) {
            ItemStack itemstack = this.getCurrentArmor(i);
            if (itemstack == null || !(this.rand.nextFloat() < 0.5f * f)) continue;
            EnchantmentHelper.addRandomEnchantment(this.rand, itemstack, (int)(5.0f + f * (float)this.rand.nextInt(18)));
        }
    }

    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        this.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05, 1));
        return livingdata;
    }

    public boolean canBeSteered() {
        return false;
    }

    public void enablePersistence() {
        this.persistenceRequired = true;
    }

    public void setEquipmentDropChance(int slotIn, float chance) {
        this.equipmentDropChances[slotIn] = chance;
    }

    public boolean canPickUpLoot() {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean canPickup) {
        this.canPickUpLoot = canPickup;
    }

    public boolean isNoDespawnRequired() {
        return this.persistenceRequired;
    }

    @Override
    public final boolean interactFirst(EntityPlayer playerIn) {
        if (this.getLeashed() && this.getLeashedToEntity() == playerIn) {
            this.clearLeashed(true, !playerIn.capabilities.isCreativeMode);
            return true;
        }
        ItemStack itemstack = playerIn.inventory.getCurrentItem();
        if (itemstack != null && itemstack.getItem() == Items.lead && this.allowLeashing()) {
            if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed()) {
                this.setLeashedToEntity(playerIn, true);
                --itemstack.stackSize;
                return true;
            }
            if (((EntityTameable)this).isOwner(playerIn)) {
                this.setLeashedToEntity(playerIn, true);
                --itemstack.stackSize;
                return true;
            }
        }
        return this.interact(playerIn) ? true : super.interactFirst(playerIn);
    }

    protected boolean interact(EntityPlayer player) {
        return false;
    }

    protected void updateLeashedState() {
        if (this.leashNBTTag != null) {
            this.recreateLeash();
        }
        if (this.isLeashed) {
            if (!this.isEntityAlive()) {
                this.clearLeashed(true, true);
            }
            if (this.leashedToEntity == null || this.leashedToEntity.isDead) {
                this.clearLeashed(true, true);
            }
        }
    }

    public void clearLeashed(boolean sendPacket, boolean dropLead) {
        if (this.isLeashed) {
            this.isLeashed = false;
            this.leashedToEntity = null;
            if (!this.worldObj.isRemote && dropLead) {
                this.dropItem(Items.lead, 1);
            }
            if (!this.worldObj.isRemote && sendPacket && this.worldObj instanceof WorldServer) {
                ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, null));
            }
        }
    }

    public boolean allowLeashing() {
        return !this.getLeashed() && !(this instanceof IMob);
    }

    public boolean getLeashed() {
        return this.isLeashed;
    }

    public Entity getLeashedToEntity() {
        return this.leashedToEntity;
    }

    public void setLeashedToEntity(Entity entityIn, boolean sendAttachNotification) {
        this.isLeashed = true;
        this.leashedToEntity = entityIn;
        if (!this.worldObj.isRemote && sendAttachNotification && this.worldObj instanceof WorldServer) {
            ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, this.leashedToEntity));
        }
    }

    private void recreateLeash() {
        if (this.isLeashed && this.leashNBTTag != null) {
            if (this.leashNBTTag.hasKey("UUIDMost", 4) && this.leashNBTTag.hasKey("UUIDLeast", 4)) {
                UUID uuid = new UUID(this.leashNBTTag.getLong("UUIDMost"), this.leashNBTTag.getLong("UUIDLeast"));
                for (EntityLivingBase entitylivingbase : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(10.0, 10.0, 10.0))) {
                    if (!entitylivingbase.getUniqueID().equals(uuid)) continue;
                    this.leashedToEntity = entitylivingbase;
                    break;
                }
            } else if (this.leashNBTTag.hasKey("X", 99) && this.leashNBTTag.hasKey("Y", 99) && this.leashNBTTag.hasKey("Z", 99)) {
                BlockPos blockpos = new BlockPos(this.leashNBTTag.getInteger("X"), this.leashNBTTag.getInteger("Y"), this.leashNBTTag.getInteger("Z"));
                EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForPosition(this.worldObj, blockpos);
                if (entityleashknot == null) {
                    entityleashknot = EntityLeashKnot.createKnot(this.worldObj, blockpos);
                }
                this.leashedToEntity = entityleashknot;
            } else {
                this.clearLeashed(false, true);
            }
        }
        this.leashNBTTag = null;
    }

    @Override
    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        int i;
        if (inventorySlot == 99) {
            i = 0;
        } else {
            i = inventorySlot - 100 + 1;
            if (i < 0 || i >= this.equipment.length) {
                return false;
            }
        }
        if (itemStackIn == null || EntityLiving.getArmorPosition(itemStackIn) == i || i == 4 && itemStackIn.getItem() instanceof ItemBlock) {
            this.setCurrentItemOrArmor(i, itemStackIn);
            return true;
        }
        return false;
    }

    @Override
    public boolean isServerWorld() {
        return super.isServerWorld() && !this.isAIDisabled();
    }

    public void setNoAI(boolean disable) {
        this.dataWatcher.updateObject(15, (byte)(disable ? 1 : 0));
    }

    public boolean isAIDisabled() {
        return this.dataWatcher.getWatchableObjectByte(15) != 0;
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        if (this.noClip) {
            return false;
        }
        BlockPosM blockposm = new BlockPosM(0, 0, 0);
        for (int i = 0; i < 8; ++i) {
            double d0 = this.posX + (double)(((float)((i >> 0) % 2) - 0.5f) * this.width * 0.8f);
            double d1 = this.posY + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
            double d2 = this.posZ + (double)(((float)((i >> 2) % 2) - 0.5f) * this.width * 0.8f);
            blockposm.setXyz(d0, d1 + (double)this.getEyeHeight(), d2);
            if (!this.worldObj.getBlockState(blockposm).getBlock().isVisuallyOpaque()) continue;
            return true;
        }
        return false;
    }

    private boolean canSkipUpdate() {
        double d1;
        if (this.isChild()) {
            return false;
        }
        if (this.hurtTime > 0) {
            return false;
        }
        if (this.ticksExisted < 20) {
            return false;
        }
        World world = this.getEntityWorld();
        if (world == null) {
            return false;
        }
        if (world.playerEntities.size() != 1) {
            return false;
        }
        Entity entity = world.playerEntities.get(0);
        double d0 = Math.max(Math.abs(this.posX - entity.posX) - 16.0, 0.0);
        double d2 = d0 * d0 + (d1 = Math.max(Math.abs(this.posZ - entity.posZ) - 16.0, 0.0)) * d1;
        return !this.isInRangeToRenderDist(d2);
    }

    private void onUpdateMinimal() {
        float f;
        ++this.entityAge;
        if (this instanceof EntityMob && (f = this.getBrightness(1.0f)) > 0.5f) {
            this.entityAge += 2;
        }
        this.despawnEntity();
    }

    public static enum SpawnPlacementType {
        ON_GROUND("ON_GROUND", 0),
        IN_AIR("IN_AIR", 1),
        IN_WATER("IN_WATER", 2);

        private static final SpawnPlacementType[] $VALUES;
        private static final String __OBFID = "CL_00002255";

        private SpawnPlacementType(String p_i18_3_, int p_i18_4_) {
        }

        static {
            $VALUES = new SpawnPlacementType[]{ON_GROUND, IN_AIR, IN_WATER};
        }
    }
}

