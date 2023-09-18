package v4n1ty.utils.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;

public class AuraUtil {

    public static EntityLivingBase getTarget(double range) {
        EntityLivingBase entityLivingBase = null;
        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList)
        {
            if(Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity) < range && shouldAttack((EntityLivingBase)entity)) {
                range = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
                entityLivingBase = (EntityLivingBase)entity;
            }
        }
        return entityLivingBase;
    }


    public static boolean shouldAttack(EntityLivingBase target) {
        return target != Minecraft.getMinecraft().thePlayer && (target.isEntityAlive() && Minecraft.getMinecraft().thePlayer.canEntityBeSeen(target) && !target.isInvisible());
    }

    public static void attack(EntityLivingBase target) {
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        //   Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().thePlayer, target);

    }

}
