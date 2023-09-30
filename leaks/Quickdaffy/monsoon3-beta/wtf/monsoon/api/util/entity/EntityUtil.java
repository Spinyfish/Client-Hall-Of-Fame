/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;

public class EntityUtil {
    public static boolean isPassive(Entity entityIn) {
        if (entityIn instanceof EntityWolf) {
            return !((EntityWolf)entityIn).isAngry();
        }
        if (entityIn instanceof EntityIronGolem) {
            return ((EntityIronGolem)entityIn).getAITarget() == null;
        }
        return entityIn instanceof EntityAgeable || entityIn instanceof EntityAmbientCreature || entityIn instanceof EntitySquid;
    }

    public static boolean isHostile(Entity entityIn) {
        return entityIn instanceof EntityMob && !EntityUtil.isNeutral(entityIn) || entityIn instanceof EntitySpider;
    }

    public static boolean isNeutral(Entity entityIn) {
        return entityIn instanceof EntityPigZombie && !((EntityPigZombie)entityIn).isAngry() || entityIn instanceof EntityWolf && !((EntityWolf)entityIn).isAngry() || entityIn instanceof EntityEnderman && ((EntityEnderman)entityIn).isScreaming();
    }

    public static Vec3 getInterpolatedPosition(Entity entityIn) {
        return new Vec3(entityIn.lastTickPosX, entityIn.lastTickPosY, entityIn.lastTickPosZ).add(EntityUtil.getInterpolatedAmount(entityIn, Minecraft.getMinecraft().getTimer().renderPartialTicks));
    }

    private static Vec3 getInterpolatedAmount(Entity entity, float partialTicks) {
        return new Vec3((entity.posX - entity.lastTickPosX) * (double)partialTicks, (entity.posY - entity.lastTickPosY) * (double)partialTicks, (entity.posZ - entity.lastTickPosZ) * (double)partialTicks);
    }

    public static EnumChatFormatting getTextColourFromEntityHealth(EntityLivingBase entity) {
        float health = EntityUtil.getTotalHealth(entity);
        if (health > 20.0f) {
            return EnumChatFormatting.YELLOW;
        }
        if (health <= 20.0f && health > 15.0f) {
            return EnumChatFormatting.GREEN;
        }
        if (health <= 15.0f && health > 10.0f) {
            return EnumChatFormatting.GOLD;
        }
        if (health <= 10.0f && health > 5.0f) {
            return EnumChatFormatting.RED;
        }
        if (health <= 5.0f) {
            return EnumChatFormatting.DARK_RED;
        }
        return EnumChatFormatting.GRAY;
    }

    public static float getTotalHealth(EntityLivingBase entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }
}

