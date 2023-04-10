package crispy.util.player;

import crispy.features.hacks.impl.combat.AntiBot;
import crispy.util.MinecraftUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

@UtilityClass
public class KillAuraUtils implements MinecraftUtil {


    public EntityLivingBase getClosest(double range) {
        double dist = range;
        EntityLivingBase target = null;
        for (Object object : Minecraft.theWorld.loadedEntityList) {

            Entity entity = (Entity) object;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) entity;
                if (canAttack(player, dist)) {
                    double currentDist = mc.thePlayer.getDistanceToEntity(player);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target = player;
                    }
                }
            }
        }
        return target;
    }


    private boolean canAttack(EntityLivingBase player, double dist) {


        if (!(player instanceof EntityPlayer))
            return false;
        if (AntiBot.getInvalid().contains(player) || player.isPlayerSleeping())
            return false;
        return player != mc.thePlayer && mc.thePlayer.getDistanceToEntity(player) <= dist;
    }


}
