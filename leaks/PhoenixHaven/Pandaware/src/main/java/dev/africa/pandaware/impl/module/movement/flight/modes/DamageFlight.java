package dev.africa.pandaware.impl.module.movement.flight.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S01PacketJoinGame;

public class DamageFlight extends ModuleMode<FlightModule> {
    private final BooleanSetting jump = new BooleanSetting("Jump", true);
    private final BooleanSetting clip = new BooleanSetting("Clip", false);
    private final BooleanSetting startOnGround = new BooleanSetting("Start on ground", true);
    private final BooleanSetting groundSpoof = new BooleanSetting("GroundSpoof", false);
    private final BooleanSetting allowYChanges = new BooleanSetting("Allow Y changes", false);
    private final BooleanSetting waitForDamage = new BooleanSetting("Wait for damage", false);
    private final BooleanSetting manualDamage = new BooleanSetting("Manual damage", false, this.waitForDamage::getValue);
    private final NumberSetting motion = new NumberSetting("Motion", 0.42, 0, 0);
    private final NumberSetting speed = new NumberSetting("Speed", 10, 0.1, 2, 0.1);
    private final NumberSetting deceleration = new NumberSetting("Deceleration", 10, 0, 1, 0.1);

    public DamageFlight(String name, FlightModule parent) {
        super(name, parent);

        this.registerSettings(
                this.speed,
                this.deceleration,
                this.motion,
                this.manualDamage,
                this.jump,
                this.clip,
                this.startOnGround,
                this.groundSpoof,
                this.allowYChanges,
                this.waitForDamage
        );
    }

    private double moveSpeed;
    private double lastDistance;
    private int stage;
    private boolean canFly;

    @Override
    public void onEnable() {
        this.moveSpeed = MovementUtils.getBaseMoveSpeed();
        this.lastDistance = MovementUtils.getBaseMoveSpeed();
        this.stage = 0;
        this.canFly = false;

        if (!PlayerUtils.isMathGround() && this.startOnGround.getValue() && !this.manualDamage.getValue()) {
            Client.getInstance().getNotificationManager().addNotification(Notification.Type.NOTIFY, "Please start on ground", 2);
            this.getParent().toggle(false);
        }

        if ((mc.thePlayer.onGround || !this.startOnGround.getValue()) && !this.manualDamage.getValue()) {
            double motion = 3.2;
            motion += PlayerUtils.getJumpBoostMotion();

            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX, mc.thePlayer.posY + motion, mc.thePlayer.posZ, false
            ));
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false
            ));
            if (this.clip.getValue()) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX, mc.thePlayer.posY - 0.42f, mc.thePlayer.posZ, true
                ));
            }
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true
            ));
        } else {
            this.stage = 3;
        }
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (this.waitForDamage.getValue() && mc.thePlayer.hurtTime > 0) {
            this.canFly = true;
        }
        if (this.waitForDamage.getValue() && !this.canFly) return;
        if (event.getEventState() == Event.EventState.PRE) {
            if (this.groundSpoof.getValue()) {
                event.setOnGround(true);
            }

            this.lastDistance = MovementUtils.getLastDistance();
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (this.waitForDamage.getValue() && !this.canFly) return;
        if (this.allowYChanges.getValue()) {
            event.y = mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? this.speed.getValue().doubleValue()
                    : mc.gameSettings.keyBindSneak.isKeyDown() ? -this.speed.getValue().doubleValue() : (this.stage % 2 == 0 ?
                    this.motion.getValue().doubleValue() : -this.motion.getValue().doubleValue());
        } else if (!this.allowYChanges.getValue() && this.motion.getValue().doubleValue() > 0) {
            event.y = (this.stage % 2 == 0 ? this.motion.getValue().doubleValue() : -this.motion.getValue().doubleValue());
        }

        switch (this.stage) {
            case 0:
                if (mc.thePlayer.onGround && this.jump.getValue()) {
                    double motion = 0.42F;
                    motion += PlayerUtils.getJumpBoostMotion();

                    event.y = mc.thePlayer.motionY = motion;
                }

                this.moveSpeed = MovementUtils.getBaseMoveSpeed() * this.speed.getValue().doubleValue();
                break;

            case 1:
                this.moveSpeed = this.lastDistance - (0.09 * (this.lastDistance - MovementUtils.getBaseMoveSpeed()));
                break;

            default:
                if (this.deceleration.getValue().floatValue() > 0) {
                    this.moveSpeed = lastDistance - lastDistance / (this.deceleration.getValue().floatValue() * 100);
                } else {
                    this.moveSpeed = this.speed.getValue().floatValue();
                }
                break;
        }

        this.moveSpeed = Math.max(this.moveSpeed, MovementUtils.getBaseMoveSpeed());
        MovementUtils.strafe(event, this.moveSpeed);

        this.stage++;
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S01PacketJoinGame) {
            this.getParent().toggle(false);
        }
    };
}
