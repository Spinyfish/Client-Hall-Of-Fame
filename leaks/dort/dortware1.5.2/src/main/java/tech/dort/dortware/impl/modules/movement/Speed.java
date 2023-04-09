package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.MovementEvent;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.movement.MotionUtils;
import tech.dort.dortware.impl.utils.networking.PacketUtil;

/**
 * @author Dort
 */
public class Speed extends Module {

    private final EnumValue<Mode> mode = new EnumValue<>("Mode", this, Mode.values());
    private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 10, SliderUnit.BPT);
    private final BooleanValue flagCheck = new BooleanValue("Flag Check", this, true);
    private double movementSpeed;
    private boolean doSlow;

    public Speed(ModuleData moduleData) {
        super(moduleData);
        register(mode, speed, flagCheck);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        Mode mode = this.mode.getValue();
        switch (mode) {
            case WATCHDOG: {
                if (event.isPre()) {
                    double y = Math.round(mc.thePlayer.posY / 0.015625) * 0.015625;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ);
                }
                break;
            }
        }
    }

    @Subscribe
    public void onMove(MovementEvent event) {
        Mode mode = this.mode.getValue();
        double speed = this.speed.getCastedValue();
        switch (mode) {
            case WATCHDOG: {
                if (mc.thePlayer.isCollidedHorizontally || !mc.thePlayer.isMovingOnGround())
                    movementSpeed = 0.32;
                if (mc.thePlayer.isMovingOnGround()) {
                    mc.timer.timerSpeed = 0.75f;
                    if (mc.thePlayer.ticksExisted % 5 == 0) {
                        if (movementSpeed < 0.5) movementSpeed = 0.8;
                        else movementSpeed += 0.5;
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.4F, mc.thePlayer.posZ, true));
                    } else if (!mc.thePlayer.isMoving()) {
                        movementSpeed = 0.45;
                    }
                    movementSpeed = Math.max(movementSpeed, 0.4);
                    movementSpeed -= movementSpeed / 44;
                    movementSpeed = Math.min(speed, movementSpeed);
                    MotionUtils.setMotion(event, movementSpeed);
                }
                break;
            }
            case MINEPLEX: {
                mc.timer.timerSpeed = 0.9f;
                if (mc.thePlayer.isMovingOnGround()) {
                    if (movementSpeed < 0.5) movementSpeed = 0.8;
                    else movementSpeed += 0.5;
                    event.setMotionY(mc.thePlayer.motionY = 0.42F);
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42F, mc.thePlayer.posZ, true));
                } else if (!mc.thePlayer.isMoving()) {
                    movementSpeed = 0.45;
                }
                movementSpeed = Math.max(movementSpeed, 0.4);
                movementSpeed -= movementSpeed / 44;
                movementSpeed = Math.min(speed, movementSpeed);
                if (mc.thePlayer.isCollidedHorizontally || !mc.thePlayer.isMoving())
                    movementSpeed = 0.32;
                MotionUtils.setMotion(event, movementSpeed);
                break;
            }

            case VANILLA:
                MotionUtils.setMotion(event, speed);
                break;

            case VANILLA_HOP:
                if (mc.thePlayer.isMovingOnGround()) {
                    event.setMotionY(mc.thePlayer.motionY = MotionUtils.getMotion(0.42F));
                }
                MotionUtils.setMotion(event, speed);
                break;
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && flagCheck.getValue()) {
            this.toggle();
        }
    }

    @Override
    public void onEnable() {
        movementSpeed = 0;
        doSlow = false;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getSuffix() {
        String mode = this.mode.getValue().getDisplayName();
        return " \2477" + mode;
    }

    public enum Mode implements INameable {
        VANILLA("Vanilla"), VANILLA_HOP("Vanilla Hop"), MINEPLEX("Mineplex"), WATCHDOG("Watchdog");
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}