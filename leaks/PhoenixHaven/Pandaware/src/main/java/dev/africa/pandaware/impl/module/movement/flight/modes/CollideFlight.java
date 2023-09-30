package dev.africa.pandaware.impl.module.movement.flight.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.CollisionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.var;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;

public class CollideFlight extends ModuleMode<FlightModule> {
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.NORMAL);

    private double startY;
    private int stage;

    public CollideFlight(String name, FlightModule parent) {
        super(name, parent);

        this.registerSettings(this.mode);
    }

    @EventHandler
    EventCallback<CollisionEvent> onCollision = event -> {
        if (mc.gameSettings.keyBindSneak.isKeyDown() && mc.thePlayer.posY <= startY) {
            startY = Math.floor(mc.thePlayer.posY);
        } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
            startY = Math.ceil(mc.thePlayer.posY);
        }
        if (mc.thePlayer != null && !mc.thePlayer.isSneaking() && event.getBlockPos().getY() <= startY - 1) {
            event.setCollisionBox(new AxisAlignedBB(-100, -2, -100, 100, 1, 100)
                    .offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ()));
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        switch (this.mode.getValue()) {
            case JUMP:
                if (PlayerUtils.isMathGround()) {
                    double speedAmplifier = (mc.thePlayer.isPotionActive(Potion.moveSpeed)
                            ? ((mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * 0.15) : 0);

                    mc.gameSettings.keyBindJump.pressed = false;
                    event.y = mc.thePlayer.motionY = 0.42f;
                    MovementUtils.strafe(event, 0.49 + speedAmplifier);
                } else {
                    MovementUtils.strafe(event, MovementUtils.getSpeed());
                }
                break;
            case YPORT:
                if (MovementUtils.isMoving()) {
                    if (mc.thePlayer.isCollidedHorizontally) {
                        if (PlayerUtils.isMathGround()) {
                            this.stage = 0;
                            event.y = mc.thePlayer.motionY = 0.42F;
                        }
                    } else {
                        double speedAmplifier = (mc.thePlayer.isPotionActive(Potion.moveSpeed)
                                ? ((mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * 0.2) : 0);

                        mc.gameSettings.keyBindJump.pressed = false;
                        double baseSpeed = MovementUtils.getBaseMoveSpeed() * 0.9;

                        if (mc.thePlayer.onGround) {
                            this.stage++;
                            double sped = 2 + speedAmplifier;
                            if (this.stage < 2) {
                                sped -= 0.9;
                            }

                            MovementUtils.strafe(event, baseSpeed * sped);
                            event.y = 0.42f;
                            mc.thePlayer.motionY = 0;
                        } else {
                            if (mc.thePlayer.getAirTicks() == 1) {
                                mc.thePlayer.motionY = -0.078400001525879;
                            }
                        }

                        MovementUtils.strafe();
                    }
                } else {
                    this.stage = 0;
                }
                break;
        }
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (this.mode.getValue() == Mode.SILENT_ACCEPT) {
            if (event.getPacket() instanceof S08PacketPlayerPosLook && mc.thePlayer != null &&
                    mc.thePlayer.ticksExisted > 15) {
                var packet = (S08PacketPlayerPosLook) event.getPacket();

                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                        packet.getX(), packet.getY(), packet.getZ(),
                        packet.getYaw(), packet.getPitch(), false
                ));

                event.cancel();
            }
        }
    };

    @Override
    public void onEnable() {
        startY = Math.floor(mc.thePlayer.posY);
        stage = 0;
    }

    @Override
    public void onDisable() {
        if (mode.getValue() == Mode.JUMP) {
            mc.gameSettings.keyBindJump.pressed = false;
        }
    }

    @AllArgsConstructor
    private enum Mode {
        NORMAL("Normal"),
        JUMP("Jump"),
        SILENT_ACCEPT("Silent Accept"),
        YPORT("Yport");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
