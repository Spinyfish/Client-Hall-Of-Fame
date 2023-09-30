package dev.africa.pandaware.impl.module.movement.flight.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.player.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class BlockdropFlight extends ModuleMode<FlightModule> {
    public BlockdropFlight(String name, FlightModule parent) {
        super(name, parent);
    }

    private boolean lastValue;
    private boolean teleport;

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = event.getPacket();
            event.cancel();

            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                    packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false
            ));

            mc.thePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());
            mc.timer.timerSpeed = 5f;

            this.lastValue = !this.lastValue;
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        MovementUtils.strafe(event, 0);
    };

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        mc.thePlayer.motionY = 0;
        mc.thePlayer.onGround = true;

        double radiansYaw = Math.toRadians(MovementUtils.getDirection());
        double xMove = -Math.sin(radiansYaw) * 20;
        double zMove = Math.cos(radiansYaw) * 20;

        double yMove = (mc.gameSettings.keyBindJump.isKeyDown() ? 1.7
                : mc.gameSettings.keyBindSneak.isKeyDown() ? -2.5 : 0);

        // allows you to move forward, without this it doesn't function

        /*
         * Higher the tick, faster you will move
         * Lower the tick, slower you will move
         */

        if (mc.thePlayer.ticksExisted % 45 == 0) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX / 2,
                    mc.thePlayer.posY / 2,
                    mc.thePlayer.posZ / 2,
                    false
            ));

            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    true
            ));
            return;
        }

        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX + xMove,
                mc.thePlayer.posY + (!this.teleport ? 5 : (this.lastValue ? .04f : 0)) + yMove
                        + RandomUtils.nextFloat(0.03f, .05f),
                mc.thePlayer.posZ + zMove,
                false
        ));

        if (!this.teleport) {
            this.teleport = true;
        }
    };
}
