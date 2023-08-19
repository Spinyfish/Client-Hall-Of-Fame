package cc.swift.module.impl.movement;

import cc.swift.events.MovementInputEvent;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.util.player.MovementUtil;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Spectre12
 * @project Swift
 * @at 25.07.23, 8:31 AM
 */
public final class FlightModule extends Module {

    public final BooleanValue viewBobbing = new BooleanValue("ViewBobbing", false); // This gave me a headache
    public final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.VANILLA, Mode.OLD_NCP, Mode.CUBECRAFT);
    public final DoubleValue speed = new DoubleValue("Speed", 1.0, 0.1, 10.0, 0.1);
    public final DoubleValue vspeed = new DoubleValue("Vertical Speed", 1.0, 0.1, 10.0, 0.1).setDependency(() -> mode.getValue() == Mode.VANILLA);
    public final BooleanValue stop = new BooleanValue("Stop", false);
    public final BooleanValue stopVertical = new BooleanValue("Vertical Stop", false);
    public final BooleanValue allowSneak = new BooleanValue("Allow Sneaking", false);
    // keep it as spoof it overlaps with the mode if you dont. Its clear what it does when u cycle through the modes
    public final ModeValue<SpoofMode> spoofGround = new ModeValue<>("Spoof", SpoofMode.values());

    public FlightModule() {
        super("Flight", Category.MOVEMENT);
        registerValues(this.viewBobbing, this.mode, this.speed, this.vspeed, this.stop, this.stopVertical, this.allowSneak, this.spoofGround);
    }

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateWalkingEventListener = event -> {
        if (this.viewBobbing.getValue() && MovementUtil.isMoving())
            mc.thePlayer.cameraYaw = 0.1f;

        switch (mode.getValue()) {
            case VANILLA:
                // TODO: This is bad.
                mc.thePlayer.motionY = 0;
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY = this.vspeed.getValue();
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = -this.vspeed.getValue();
                }
                MovementUtil.setSpeed(MovementUtil.isMoving() ? this.speed.getValue() : 0);
                break;

            case OLD_NCP:
                mc.thePlayer.jumpMovementFactor = 3.7f;
                MovementUtil.setSpeed(0);
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 11, mc.thePlayer.posZ, false));
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 3.7 * .5 : mc.thePlayer.movementInput.sneak ? -3.7 * .5 : 0;
                break;

            case CUBECRAFT:
                mc.thePlayer.motionY = 0;
                event.setY(mc.thePlayer.posY + 1 / 64F);
                event.setOnGround(true);
                MovementUtil.setSpeed(MovementUtil.isMoving() ? this.speed.getValue() : 0);
                break;
        }
        if (this.spoofGround.getValue() != SpoofMode.OFF) {
            event.setOnGround(this.spoofGround.getValue() == SpoofMode.ON_GROUND);
        }
    };

    @Handler
    public final Listener<MovementInputEvent> movementInputEventListener = event -> {
        if (!this.allowSneak.getValue())
            event.setSneak(false);
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        if (this.stop.getValue())
            MovementUtil.setSpeed(0);
        if (this.stopVertical.getValue())
            mc.thePlayer.motionY = 0;
    }

    enum Mode {
        VANILLA, OLD_NCP, CUBECRAFT, BMC
    }

    enum SpoofMode {
        OFF, ON_GROUND, OFF_GROUND
    }
}

