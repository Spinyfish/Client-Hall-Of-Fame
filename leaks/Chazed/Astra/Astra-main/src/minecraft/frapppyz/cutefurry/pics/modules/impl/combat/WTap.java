package frapppyz.cutefurry.pics.modules.impl.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Move;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.event.impl.SendPacket;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.util.MoveUtil;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class WTap extends Mod {
    public WTap() {
        super("Wtap", "What do you think monkey", 0, Category.COMBAT);
    }

    public void onEvent(Event event){
        if(event instanceof Render){
            this.setSuffix("Rage");
        }
        if(event instanceof Move){
            if(Killaura.entity != null){
                if(mc.thePlayer.getDistanceToEntity(Killaura.entity) < 5 && mc.thePlayer.ticksExisted % 9 != 0){
                    event.setCancelled(true);
                    ((Move) event).setX(mc.thePlayer.motionX = 0);
                    ((Move) event).setY(mc.thePlayer.motionY = 0);
                    ((Move) event).setZ(mc.thePlayer.motionZ = 0);
                }
            }
        }
        if(event instanceof SendPacket){
            if(Killaura.entity != null){
                if(mc.thePlayer.getDistanceToEntity(Killaura.entity) < 5 && ((SendPacket) event).getPacket() instanceof C03PacketPlayer && mc.thePlayer.ticksExisted % 9 != 0){
                    event.setCancelled(true);
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
                    }
            }
        }
    }
}
