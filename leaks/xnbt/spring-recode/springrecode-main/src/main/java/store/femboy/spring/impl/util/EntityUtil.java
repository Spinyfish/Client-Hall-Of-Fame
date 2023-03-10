package store.femboy.spring.impl.util;


import gay.sukumi.irc.ChatClient;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;
import store.femboy.spring.impl.module.impl.combat.KillAura;

public final class EntityUtil {

    static Minecraft mc = Minecraft.getMinecraft();
    private static Entity EntityArmorStand;
    private final MathUtils utilMath = new MathUtils();

    public static EntityLivingBase getClosestEntity(double range) {
        EntityLivingBase target = null;
        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (canAttack(entity) && !(entity == EntityArmorStand) && entity instanceof EntityLivingBase && !(mc.getNetHandler().getPlayerInfo(entity.getUniqueID()) == null)) {
                if (KillAura.ircFriends.isEnabled() && ChatClient.INSTANCE.getUserByMcName(EnumChatFormatting.getTextWithoutFormattingCodes(entity.getName())) == null){
                    double currentDist = mc.thePlayer.getDistanceToEntity(entity);
                    if (currentDist <= range) {
                        range = currentDist;
                        target = (EntityLivingBase) entity;
                    }
                } else if (!KillAura.ircFriends.isEnabled()){
                    double currentDist = mc.thePlayer.getDistanceToEntity(entity);
                    if (currentDist <= range) {
                        range = currentDist;
                        target = (EntityLivingBase) entity;
                    }
                }
            }
        }
        return target;
    }

    public static boolean canAttack(Entity entity) {
        return entity != mc.thePlayer && entity.isEntityAlive() && mc.thePlayer != null && Minecraft.getMinecraft().theWorld != null && mc.thePlayer.ticksExisted > 30 && entity.ticksExisted > 15;
    }

    public double getHealthPercentage(EntityLivingBase e) {
        return utilMath.round(e.getHealth() / e.getMaxHealth() * 100, 1);
    }
}
