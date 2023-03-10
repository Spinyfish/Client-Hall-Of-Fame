/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 */
package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class EntityAINearestAttackableTarget<T extends EntityLivingBase>
extends EntityAITarget {
    protected final Class<T> targetClass;
    private final int targetChance;
    protected final Sorter theNearestAttackableTargetSorter;
    protected Predicate<? super T> targetEntitySelector;
    protected EntityLivingBase targetEntity;

    public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
        this(creature, classTarget, checkSight, false);
    }

    public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
        this(creature, classTarget, 10, checkSight, onlyNearby, null);
    }

    public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, final Predicate<? super T> targetSelector) {
        super(creature, checkSight, onlyNearby);
        this.targetClass = classTarget;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new Sorter(creature);
        this.setMutexBits(1);
        this.targetEntitySelector = new Predicate<T>(){

            public boolean apply(T p_apply_1_) {
                if (targetSelector != null && !targetSelector.apply(p_apply_1_)) {
                    return false;
                }
                if (p_apply_1_ instanceof EntityPlayer) {
                    double d0 = EntityAINearestAttackableTarget.this.getTargetDistance();
                    if (((Entity)p_apply_1_).isSneaking()) {
                        d0 *= (double)0.8f;
                    }
                    if (((Entity)p_apply_1_).isInvisible()) {
                        float f = ((EntityPlayer)p_apply_1_).getArmorVisibility();
                        if (f < 0.1f) {
                            f = 0.1f;
                        }
                        d0 *= (double)(0.7f * f);
                    }
                    if ((double)((Entity)p_apply_1_).getDistanceToEntity(EntityAINearestAttackableTarget.this.taskOwner) > d0) {
                        return false;
                    }
                }
                return EntityAINearestAttackableTarget.this.isSuitableTarget((EntityLivingBase)p_apply_1_, false);
            }
        };
    }

    @Override
    public boolean shouldExecute() {
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
        }
        double d0 = this.getTargetDistance();
        List<T> list = this.taskOwner.worldObj.getEntitiesWithinAABB(this.targetClass, this.taskOwner.getEntityBoundingBox().expand(d0, 4.0, d0), Predicates.and(this.targetEntitySelector, EntitySelectors.NOT_SPECTATING));
        Collections.sort(list, this.theNearestAttackableTargetSorter);
        if (list.isEmpty()) {
            return false;
        }
        this.targetEntity = (EntityLivingBase)list.get(0);
        return true;
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }

    public static class Sorter
    implements Comparator<Entity> {
        private final Entity theEntity;

        public Sorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
        }

        @Override
        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d1;
            double d0 = this.theEntity.getDistanceSqToEntity(p_compare_1_);
            return d0 < (d1 = this.theEntity.getDistanceSqToEntity(p_compare_2_)) ? -1 : (d0 > d1 ? 1 : 0);
        }
    }
}

