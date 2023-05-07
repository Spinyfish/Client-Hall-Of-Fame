package client.module.impl.movement.flight;


import client.Client;
import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;
import client.event.impl.motion.UpdateEvent;
import client.event.impl.packet.PacketReceiveEvent;
import client.event.impl.packet.PacketSendEvent;
import client.module.impl.exploit.Disabler;
import client.module.impl.movement.Flight;
import client.util.chat.ChatUtil;
import client.util.player.MoveUtil;
import client.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class VulcanFlight extends Mode<Flight> {
    private int ticks, wasos, offGroundTicks, onGroundTicks, hypixelTicks, pearlSlot = -1;
    public VulcanFlight(String name, Flight parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        hypixelTicks = 0;
    }
    @Override
    public void onDisable() {
        super.onDisable();
        hypixelTicks = 0;
        mc.thePlayer.motionY = -0.09800000190735147;
        MoveUtil.stop();
        MoveUtil.strafe(0.1);
    }

    @EventLink()
    public final Listener<UpdateEvent> onUpdate = event -> {

    };

    @EventLink()
    public final Listener<MotionEvent> onMotion = event -> {
        mc.thePlayer.motionY = 0.4641593749554431f;
        mc.thePlayer.prevChasingPosY = mc.thePlayer.lastReportedPosY;
        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 1F : mc.gameSettings.keyBindSneak.isKeyDown() ? -1F : 0.0;
        mc.thePlayer.isAirBorne = true;
        MoveUtil.strafe(0.2753);

        /*
        ChatUtil.display(hypixelTicks);
        hypixelTicks++;
        // mc.thePlayer.posY = startingLocationY;
        if (hypixelTicks == 1) {
            //ChatUtil.display("3");
            event.setPosY(event.getPosY() - 0.2);
        }


        if (hypixelTicks < 1) {

            //ChatUtil.display("2");
            //mc.thePlayer.motionY = 3.7;
            //  mc.thePlayer.motionY = 1f;
            mc.thePlayer.motionY = 0;
            MoveUtil.strafe(3.4);
        }



        if (hypixelTicks == 3) {
            //ChatUtil.display("1");
            // mc.thePlayer.motionY = 0.00f;
            MoveUtil.strafe(0.42);
            mc.thePlayer.motionY = 0.24813599859094576 - 0.313605186719;
        }


        if (hypixelTicks > 6 && mc.thePlayer.ticksExisted % 5   == 0) {
            MoveUtil.strafe(0.40);


            mc.thePlayer.motionY = 0.0181;
            //ChatUtil.display("4");
        }




        if (!MoveUtil.isMoving()) MoveUtil.stop();

         */
        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 1F : mc.gameSettings.keyBindSneak.isKeyDown() ? -1F : 0.0;
        MoveUtil.strafe(1.40);
    };



    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {

    };
}