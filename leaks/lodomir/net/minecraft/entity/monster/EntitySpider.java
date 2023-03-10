/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntitySpider
extends EntityMob {
    public EntitySpider(World worldIn) {
        super(worldIn);
        this.setSize(1.4f, 0.9f);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4f));
        this.tasks.addTask(4, new AISpiderAttack(this, EntityPlayer.class));
        this.tasks.addTask(4, new AISpiderAttack(this, EntityIronGolem.class));
        this.tasks.addTask(5, new EntityAIWander(this, 0.8));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget((EntityCreature)this, false, new Class[0]));
        this.targetTasks.addTask(2, new AISpiderTarget<EntityPlayer>(this, EntityPlayer.class));
        this.targetTasks.addTask(3, new AISpiderTarget<EntityIronGolem>(this, EntityIronGolem.class));
    }

    @Override
    public double getMountedYOffset() {
        return this.height * 0.5f;
    }

    @Override
    protected PathNavigate getNewNavigator(World worldIn) {
        return new PathNavigateClimber(this, worldIn);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte(0));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            this.setBesideClimbableBlock(this.isCollidedHorizontally);
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3f);
    }

    @Override
    protected String getLivingSound() {
        return "mob.spider.say";
    }

    @Override
    protected String getHurtSound() {
        return "mob.spider.say";
    }

    @Override
    protected String getDeathSound() {
        return "mob.spider.death";
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound("mob.spider.step", 0.15f, 1.0f);
    }

    @Override
    protected Item getDropItem() {
        return Items.string;
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        super.dropFewItems(p_70628_1_, p_70628_2_);
        if (p_70628_1_ && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + p_70628_2_) > 0)) {
            this.dropItem(Items.spider_eye, 1);
        }
    }

    @Override
    public boolean isOnLadder() {
        return this.isBesideClimbableBlock();
    }

    @Override
    public void setInWeb() {
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        return potioneffectIn.getPotionID() == Potion.poison.id ? false : super.isPotionApplicable(potioneffectIn);
    }

    public boolean isBesideClimbableBlock() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean p_70839_1_) {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);
        b0 = p_70839_1_ ? (byte)(b0 | 1) : (byte)(b0 & 0xFFFFFFFE);
        this.dataWatcher.updateObject(16, b0);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        int i;
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        if (this.worldObj.rand.nextInt(100) == 0) {
            EntitySkeleton entityskeleton = new EntitySkeleton(this.worldObj);
            entityskeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0f);
            entityskeleton.onInitialSpawn(difficulty, null);
            this.worldObj.spawnEntityInWorld(entityskeleton);
            entityskeleton.mountEntity(this);
        }
        if (livingdata == null) {
            livingdata = new GroupData();
            if (this.worldObj.getDifficulty() == EnumDifficulty.HARD && this.worldObj.rand.nextFloat() < 0.1f * difficulty.getClampedAdditionalDifficulty()) {
                ((GroupData)livingdata).func_111104_a(this.worldObj.rand);
            }
        }
        if (livingdata instanceof GroupData && (i = ((GroupData)livingdata).potionEffectId) > 0 && Potion.potionTypes[i] != null) {
            this.addPotionEffect(new PotionEffect(i, Integer.MAX_VALUE));
        }
        return livingdata;
    }

    @Override
    public float getEyeHeight() {
        return 0.65f;
    }

    public static class GroupData
    implements IEntityLivingData {
        public int potionEffectId;

        public void func_111104_a(Random rand) {
            int i = rand.nextInt(5);
            if (i <= 1) {
                this.potionEffectId = Potion.moveSpeed.id;
            } else if (i <= 2) {
                this.potionEffectId = Potion.damageBoost.id;
            } else if (i <= 3) {
                this.potionEffectId = Potion.regeneration.id;
            } else if (i <= 4) {
                this.potionEffectId = Potion.invisibility.id;
            }
        }
    }

    static class AISpiderTarget<T extends EntityLivingBase>
    extends EntityAINearestAttackableTarget {
        public AISpiderTarget(EntitySpider p_i45818_1_, Class<T> classTarget) {
            super((EntityCreature)p_i45818_1_, classTarget, true);
        }

        @Override
        public boolean shouldExecute() {
            float f = this.taskOwner.getBrightness(1.0f);
            return f >= 0.5f ? false : super.shouldExecute();
        }
    }

    static class AISpiderAttack
    extends EntityAIAttackOnCollide {
        public AISpiderAttack(EntitySpider p_i45819_1_, Class<? extends Entity> targetClass) {
            super(p_i45819_1_, targetClass, 1.0, true);
        }

        @Override
        public boolean continueExecuting() {
            float f = this.attacker.getBrightness(1.0f);
            if (f >= 0.5f && this.attacker.getRNG().nextInt(100) == 0) {
                this.attacker.setAttackTarget(null);
                return false;
            }
            return super.continueExecuting();
        }

        @Override
        protected double func_179512_a(EntityLivingBase attackTarget) {
            return 4.0f + attackTarget.width;
        }
    }
}

