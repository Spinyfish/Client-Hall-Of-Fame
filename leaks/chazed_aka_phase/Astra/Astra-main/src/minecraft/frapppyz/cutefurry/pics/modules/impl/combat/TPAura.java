package frapppyz.cutefurry.pics.modules.impl.combat;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Motion;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.notifications.Type;
import frapppyz.cutefurry.pics.util.PacketUtil;
import frapppyz.cutefurry.pics.util.RotUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TPAura extends Mod {
    private EntityLivingBase entity;
    
    private Mode ticks = new Mode("Tick Delay", "1", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20");
    private Mode tpDist = new Mode("TP distance", "1", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    
    public TPAura() {
        super("TPaura", "TP hit TP nigs", 0, Category.COMBAT);
        addSettings(ticks, tpDist);
    }

    private List<EntityLivingBase> getValidTargets() {
        List<EntityLivingBase> targets = new ArrayList<>(mc.theWorld.loadedEntityList).stream()
                .filter(EntityLivingBase.class::isInstance)
                .map(entity -> (EntityLivingBase) entity).sorted(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.thePlayer))).collect(Collectors.toList());

        targets.removeIf(t -> t == mc.thePlayer || mc.thePlayer.getDistanceToEntity(t) >= 50 || t instanceof EntityArmorStand);
        return targets;
    }

    private EntityLivingBase getNearest() {
        return getValidTargets().stream().findFirst().orElse(null);
    }

    public void onEnable(){
        entity = null;
    }

    public void onEvent(Event e){
        if(e instanceof Motion){
            entity = getNearest();

            if(entity != null){
                ((Motion) e).setYaw(RotUtil.getRots(entity)[0]);
                ((Motion) e).setPitch(RotUtil.getRots(entity)[1]);

                if(mc.thePlayer.ticksExisted % Double.parseDouble(ticks.getMode()) == 0){
                    for(int i = 0; i < mc.thePlayer.getDistanceToEntity(entity)/Double.parseDouble(tpDist.getMode()); i++){
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + -Math.sin(Math.toRadians(((Motion) e).getYaw())) * Double.parseDouble(tpDist.getMode())*i, mc.thePlayer.posY, mc.thePlayer.posZ + Math.cos(Math.toRadians(((Motion) e).getYaw())) * Double.parseDouble(tpDist.getMode())*i, true));
                        Wrapper.getLogger().info(String.valueOf(mc.thePlayer.posX + -Math.sin(Math.toRadians(((Motion) e).getYaw()) * Double.parseDouble(tpDist.getMode())*i)) + " - " + String.valueOf(mc.thePlayer.posZ + Math.cos(Math.toRadians(((Motion) e).getYaw())) * Double.parseDouble(tpDist.getMode())*i));
                    }
                    Wrapper.getNotifications().postNotification("TPAura", "Hit \"" + entity.getName() + "\"!", Math.round(mc.thePlayer.getDistanceToEntity(entity)) + " blocks away!", Type.INFO, (long) (Double.parseDouble(ticks.getMode())*50 - 50));
                    PacketUtil.sendPacketNoEvent(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                    mc.thePlayer.swingItem();
                    for(int i = (int) (mc.thePlayer.getDistanceToEntity(entity)/Double.parseDouble(tpDist.getMode())); i>0; i--){
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + -Math.sin(Math.toRadians(((Motion) e).getYaw())) * Double.parseDouble(tpDist.getMode())*i, mc.thePlayer.posY, mc.thePlayer.posZ + Math.cos(Math.toRadians(((Motion) e).getYaw())) * Double.parseDouble(tpDist.getMode())*i, true));
                        Wrapper.getLogger().info(String.valueOf(mc.thePlayer.posX + -Math.sin(Math.toRadians(((Motion) e).getYaw()) * Double.parseDouble(tpDist.getMode())*i)) + " - " + String.valueOf(mc.thePlayer.posZ + Math.cos(Math.toRadians(((Motion) e).getYaw())) * Double.parseDouble(tpDist.getMode())*i));
                    }
                }
            }
        }
    }
}
