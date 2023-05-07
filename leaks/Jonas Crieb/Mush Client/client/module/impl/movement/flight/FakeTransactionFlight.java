package client.module.impl.movement.flight;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;
import client.event.impl.motion.StrafeEvent;
import client.event.impl.packet.PacketSendEvent;
import client.module.impl.movement.Flight;
import client.util.chat.ChatUtil;
import client.util.player.MoveUtil;
import client.util.player.PlayerUtil;
import client.value.Mode;
import client.value.impl.NumberValue;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import tv.twitch.chat.Chat;

public class FakeTransactionFlight extends Mode<Flight> {
int ticks;


    public FakeTransactionFlight(String name, Flight parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ticks = 0;
    }
    @EventLink()
    public final Listener<PacketSendEvent> onPacketSend = event -> {

    };
    @EventLink()
    public final Listener<MotionEvent> onMotion = event -> {
        if (!MoveUtil.isMoving()) {
            MoveUtil.stop();
        }



        mc.thePlayer.motionY = 0;
        if (mc.thePlayer.ticksExisted % 5 == 0) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - -1.1576965e+16, mc.thePlayer.posZ, mc.thePlayer.onGround));
        }



    };
}
