package client.module.impl.movement.flight;

import client.event.Event;
import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;
import client.event.impl.packet.PacketSendEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.util.chat.ChatUtil;
import client.util.player.MoveUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleInfo(name = "PikaFly", description = "", category = Category.MOVEMENT, autoEnabled = false)
public class PikaFly extends Module {
    int ticks = 0;
    boolean cancelFlag = false;
    boolean jumped = false;
    public void onEnable() {
        if(!mc.thePlayer.onGround) {
            //onAttemptDisable()
        }
        jumped = false;
        mc.timer.timerSpeed = 0.5f;
        ticks = 0;
    }
    public void onDisable() {


        mc.timer.timerSpeed = 1f;

    }
    @EventLink
    public final Listener<MotionEvent> onMotion = event -> {


        mc.timer.timerSpeed = 0.5f;
        if (mc.thePlayer.fallDistance > 0 && ticks % 2 == 0 && mc.thePlayer.fallDistance < 2.2)
            mc.thePlayer.motionY += 0.14;

        ticks++;

        ChatUtil.display(ticks);
        switch (ticks) {
            case 1 : {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0484, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                MoveUtil.strafe(7.9f);
                mc.thePlayer.motionY = 0.51999998688698;
                cancelFlag = true;

            }

            case 2 : {
                mc.thePlayer.motionY += 0.6;
                MoveUtil.strafe(2.9f);
            }


            case 3 : {
                mc.thePlayer.motionY -= 0.3;
                MoveUtil.strafe(1.56f);
            }
            case 4 : {

                mc.thePlayer.motionY += 0.1;
                MoveUtil.strafe(1.29f);
                mc.thePlayer.onGround = true;
            }

            case 5 : MoveUtil.strafe(0.99f); mc.thePlayer.motionY += 0.1;

            case 6 : MoveUtil.strafe(0.4f); mc.thePlayer.motionY -= 0.1;
            case 40 : {

                mc.thePlayer.motionY -= 0.2;
                //mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            //    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0484, mc.thePlayer.posZ, false));
              //  mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                MoveUtil.strafe(0.96f);
                ChatUtil.display("Final Mom");
            }
        }
    };
    @EventLink
    public final Listener<PacketSendEvent> onPacket = event -> {
        final Packet packet = event.getPacket();
        if (packet instanceof S08PacketPlayerPosLook && cancelFlag) {
            cancelFlag = false;
            event.setCancelled(true);
        }
    };
}

