package me.kansio.client.utils.combat;

import me.kansio.client.Client;
import me.kansio.client.utils.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class FightUtil extends Util {

    public static boolean canHit(double chance) {
        return Math.random() <= chance;
    }

    public static List<EntityLivingBase> getMultipleTargets(double range, boolean players, boolean friends, boolean animals, boolean walls, boolean mobs, boolean invis) {
        List<EntityLivingBase> list = new ArrayList<>();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase))
                continue;
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            if (entityLivingBase == mc.thePlayer ||
                    mc.thePlayer.getDistanceToEntity(entityLivingBase) > range
                    || !entityLivingBase.canEntityBeSeen(mc.thePlayer) && !walls
                    || entityLivingBase.isDead
                    || entityLivingBase instanceof EntityArmorStand
                    || entityLivingBase instanceof EntityVillager
                    || entityLivingBase instanceof EntityAnimal && !animals
                    || entityLivingBase instanceof EntitySquid && !animals
                    || entityLivingBase instanceof EntityPlayer && !players
                    || entityLivingBase instanceof EntityMob && !mobs
                    || entityLivingBase instanceof EntitySlime && !mobs
                    || Client.getInstance().getFriendManager().isFriend(entityLivingBase.getName()) && !friends
                    || entityLivingBase.isInvisible() && !invis) continue;
            if (list.size() > 5)
                continue;
            list.add(entityLivingBase);
        }
        return list;
    }

    public static boolean isValid(EntityLivingBase entityLivingBase, double range, boolean invis, boolean players, boolean animals, boolean mobs) {
        return !(mc.thePlayer.getDistanceToEntity(entityLivingBase) > range
                || entityLivingBase.isDead
                || entityLivingBase instanceof EntityArmorStand
                || entityLivingBase instanceof EntityVillager
                || entityLivingBase instanceof EntityPlayer && !players
                || entityLivingBase instanceof EntityAnimal && !animals
                || entityLivingBase instanceof EntityMob && !mobs
                || entityLivingBase.isInvisible() && !invis
                || entityLivingBase == mc.thePlayer);
    }

    public static boolean isOnSameTeam(EntityLivingBase entity) {
        if (entity.getTeam() != null && mc.thePlayer.getTeam() != null) {
            Team team1 = entity.getTeam();
            Team team2 = mc.thePlayer.getTeam();

            if (entity.getName().contains("UPGRADES")) {
                return false;
            }

            if (entity.getName().contains("SHOP")) {
                return false;
            }

            return team1 == team2;
        }
        return false;
    }
}