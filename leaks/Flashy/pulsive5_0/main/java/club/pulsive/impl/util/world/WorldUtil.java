package club.pulsive.impl.util.world;

import club.pulsive.api.main.Pulsive;
import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.impl.module.impl.combat.Aura;
import club.pulsive.impl.util.player.PlayerUtil;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class WorldUtil implements MinecraftUtil {

    private static double lastRaycastRange = -1;

    public static List<EntityLivingBase> getLivingEntities(Predicate<EntityLivingBase> validator) {
        List<EntityLivingBase> entities = new ArrayList<>();
        if(mc.theWorld == null) return entities;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase e = (EntityLivingBase) entity;
                if (validator.apply(e))
                    entities.add(e);
            }
        }
        return entities;
    }

    public static List<EntityLivingBase> getEntities(final double range, final boolean players, final boolean nonPlayers, final boolean dead, final boolean invisibles, final boolean ignoreTeammates) {
        // Returns the list of entities
        return mc.theWorld.loadedEntityList
                // Stream our entity list.
                .stream()

                // Only get living entities so we don't have to check for items on ground etc.
                .filter(entity -> entity instanceof EntityLivingBase)

                // Map our entities to entity living base as we have filtered out none living entities.
                .map(entity -> ((EntityLivingBase) entity))

                // Only get the entities we can attack.
                .filter(entity -> {
                    if (entity instanceof EntityPlayer && !players) return false;

                    if (!(entity instanceof EntityPlayer) && !nonPlayers) return false;

                    if (entity.isInvisible() && !invisibles) return false;

                    if (entity.isDead && !dead) return false;

                    if (entity.deathTime != 0 && !dead) return false;

                    if (entity.ticksExisted < 2) return false;

                    return mc.thePlayer != entity;
                })

                // Do a proper distance calculation to get entities we can reach.
                .filter(entity -> {
                    // DO NOT TOUCH THIS VALUE ITS CALCULATED WITH MATH
                    final double girth = 0.5657;

                    // See if the other entity is in our range.
                    return mc.thePlayer.getDistanceToEntity(entity) - girth < range;
                })

                // Sort out potential targets with the algorithm provided as a setting.
                .sorted(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceSqToEntity(entity)))

                // Sort out all the specified targets.
                .sorted(Comparator.comparing(entity -> entity instanceof EntityPlayer))

                // Get the possible targets and put them in a list.
                .collect(Collectors.toList());
    }

    public static boolean checkPing(EntityPlayer entityPlayer) {
        NetworkPlayerInfo info = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(entityPlayer.getUniqueID());
        return info != null && info.getResponseTime() >= 1;
    }

    public static double lastRaycastRange() {
        return lastRaycastRange;
    }
}
