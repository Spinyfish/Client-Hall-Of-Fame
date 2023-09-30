package frapppyz.cutefurry.pics.modules.impl.combat;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.event.impl.SendPacket;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HvH extends Mod {
    public HvH() {
        super("HvH", "HvH lol", 0, Category.COMBAT);
    }

    private EntityLivingBase entity;

    private List<EntityLivingBase> getValidTargets() {
        List<EntityLivingBase> targets = new ArrayList<>(mc.theWorld.loadedEntityList).stream()
                .filter(EntityLivingBase.class::isInstance)
                .map(entity -> (EntityLivingBase) entity).sorted(Comparator.comparingDouble(entity -> entity.hurtTime)).collect(Collectors.toList());

        targets.removeIf(t -> t == mc.thePlayer || mc.thePlayer.getDistanceToEntity(t) >= 6 || !(t instanceof EntityPlayer) || Antibot.bots.contains(t) && Wrapper.getModManager().getModByName("Antibot").isToggled());
        return targets;
    }

    private EntityLivingBase getNearest() {
        return getValidTargets().stream().findFirst().orElse(null);
    }

    public void onEvent(Event e){

        if(e instanceof Update){
            if(mc.thePlayer.getHealth() < 12 && mc.thePlayer.ticksExisted % 3 == 0){
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem + 1));
                PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(null));
                for(int i = 0; i < 35; i++){
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer(mc.thePlayer.onGround));
                }
                PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging());
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
            if(mc.thePlayer.isEating()){
                for(int i =0; i < 10; i++){
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer(mc.thePlayer.onGround));
                }
            }
        }
    }
}
