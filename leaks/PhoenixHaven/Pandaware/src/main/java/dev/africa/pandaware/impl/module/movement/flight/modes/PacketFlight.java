package dev.africa.pandaware.impl.module.movement.flight.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.game.TickEvent;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.player.MovementUtils;
import lombok.var;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class PacketFlight extends ModuleMode<FlightModule> {
    private final BooleanSetting randomOffset = new BooleanSetting("Random offset", false);
    private final BooleanSetting freeze = new BooleanSetting("Freeze", true);
    private final BooleanSetting freezeY = new BooleanSetting("Freeze Y", false);

    private final NumberSetting xMultiply = new NumberSetting("X multiply", 100, -100, 0, 2);
    private final NumberSetting yMultiply = new NumberSetting("Y multiply", 100, -100, 0, 2);
    private final NumberSetting zMultiply = new NumberSetting("Z multiply", 100, -100, 0, 2);
    private final NumberSetting backSpeed = new NumberSetting("Back speed", 10, 0, 0);
    private final NumberSetting yMotion = new NumberSetting("Y motion", 4, -4, 0, 0.1);
    private final NumberSetting speed = new NumberSetting("Speed", 10, 0.1, 1, 0.1);
    private final NumberSetting timer = new NumberSetting("Timer", 10, 0.1, 1, 0.1);

    private final NumberSetting teleportTicks = new NumberSetting("Teleport ticks", 50, 1, 2, 1);

    public PacketFlight(String name, FlightModule parent) {

        super(name, parent);

        this.registerSettings(
                this.speed,
                this.timer,
                this.randomOffset,
                this.freeze,
                this.freezeY,
                this.xMultiply,
                this.yMultiply,
                this.zMultiply,
                this.backSpeed,
                this.yMotion,
                this.teleportTicks
        );
    }

    private boolean laggedBack;

    @Override
    public void onEnable() {
        this.laggedBack = false;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
    }

    @EventHandler
    EventCallback<TickEvent> onTick = event -> {
        mc.timer.timerSpeed = this.timer.getValue().floatValue();
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && mc.thePlayer != null && mc.thePlayer.ticksExisted > 20) {
            var packet = (S08PacketPlayerPosLook) event.getPacket();

            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                    packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false
            ));

            mc.thePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());

            this.laggedBack = true;

            event.cancel();
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (this.freeze.getValue()) {
            MovementUtils.strafe(event, 0);
        }

        if (this.laggedBack && this.backSpeed.getValue().doubleValue() > 0) {
            MovementUtils.strafe(event, -this.backSpeed.getValue().doubleValue());
            this.laggedBack = false;
        }
    };

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        event.cancel();

        mc.thePlayer.onGround = true;
        mc.thePlayer.motionY = 0;

        if (mc.thePlayer.ticksExisted % this.teleportTicks.getValue().intValue() == 0) {
            double speed = (MovementUtils.isMoving() ? this.speed.getValue().doubleValue() : 0);

            double radiansYaw = Math.toRadians(MovementUtils.getDirection());
            double xMove = -Math.sin(radiansYaw) * speed;
            double zMove = Math.cos(radiansYaw) * speed;

            double motion = this.freezeY.getValue() ? 0 : this.yMotion.getValue().doubleValue();
            double yMove = (mc.gameSettings.keyBindJump.isKeyDown() ? this.speed.getValue().doubleValue()
                    : mc.gameSettings.keyBindSneak.isKeyDown() ? -this.speed.getValue().doubleValue() : motion);

            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX + xMove,
                    mc.thePlayer.posY + yMove,
                    mc.thePlayer.posZ + zMove,
                    false
            ));
        }

        double x = 10 * (this.xMultiply.getValue().doubleValue() * (this.randomOffset.getValue() ? Math.random() : 1));
        double y = 10 * (this.yMultiply.getValue().doubleValue() * (this.randomOffset.getValue() ? Math.random() : 1));
        double z = 10 * (this.zMultiply.getValue().doubleValue() * (this.randomOffset.getValue() ? Math.random() : 1));

        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z, false));
    };
}
