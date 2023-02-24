package me.kansio.client.modules.impl.movement.flight.misc;

import me.kansio.client.event.impl.BlockCollisionEvent;
import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.flight.FlightMode;
import me.kansio.client.utils.network.PacketUtil;
import me.kansio.client.utils.player.PlayerUtil;
import me.kansio.client.utils.player.TimerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;

public class Mush extends FlightMode {
    double speedy = 2.5;
    boolean blinking = false;
    private ArrayList<? extends C03PacketPlayer> c03Packets = new ArrayList<>();

    public Mush() {
        super("Mush");
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.ticksExisted % 18 == 0) {
            stopBlink();
        }

        if (mc.thePlayer.isMoving()) {
            if (mc.timer.timerSpeed > 1) {
                mc.timer.timerSpeed -= 0.01;
            }

            if (speedy > 0.22) {
                speedy -= 0.01;
            }
        } else {
            TimerUtil.Reset();
            speedy = 0;
        }
    }

    @Override
    public void onMove(MoveEvent event) {
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.motionY = 1;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.thePlayer.motionY = -1;
        }
        PlayerUtil.setMotion(Math.max(speedy, PlayerUtil.getVerusBaseSpeed()));
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (blinking) {
            if (event.getPacket() instanceof C03PacketPlayer) {
                c03Packets.add(event.getPacket());
            }
        }
    }

    @Override
    public void onEnable() {
        speedy = getFlight().getSpeed().getValue();
        mc.timer.timerSpeed = getFlight().getTimer().getValue().floatValue();
        blinking = getFlight().getBlink().getValue();
    }

    public void stopBlink() {
        for (C03PacketPlayer packetPlayer : c03Packets) {
            PacketUtil.sendPacketNoEvent(packetPlayer);
        }
        c03Packets.clear();
        blinking = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        stopBlink();
    }

    @Override
    public void onCollide(BlockCollisionEvent event) {
        if (event.getBlock() instanceof BlockAir) {
            if (mc.thePlayer.isSneaking())
                return;
            double x = event.getX();
            double y = event.getY();
            double z = event.getZ();
            if (y < mc.thePlayer.posY) {
                event.setAxisAlignedBB(AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1.0F, 5).offset(x, y, z));
            }
        }
    }

}
