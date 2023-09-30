package dev.africa.pandaware.impl.module.movement.flight.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.player.MovementUtils;
import net.minecraft.potion.Potion;

public class VerusFlight extends ModuleMode<FlightModule> {
    public VerusFlight(String name, FlightModule parent) {
        super(name, parent);
    }

    private double lastY;
    private int stage;
    private int ticks;
    private boolean shouldFly;

    @Override
    public void onEnable() {
        this.lastY = mc.thePlayer.posY;
        this.stage = 0;
        this.ticks = 0;
        this.shouldFly = false;
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setPosition(mc.thePlayer.posX, this.lastY, mc.thePlayer.posZ);
        this.stage = 0;
        this.ticks = 0;
        this.shouldFly = false;
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE && !mc.thePlayer.movementInput.jump) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, Math.ceil(mc.thePlayer.posY), mc.thePlayer.posZ);

            event.setOnGround(true);

            switch (this.stage++) {
                case 2:
                case 1: {
                    event.setOnGround(false);
                    event.setY(event.getY() + 0.419999986886978);
                    break;
                }

                case 4:
                case 3: {
                    event.setOnGround(false);
                    event.setY(event.getY() + 0.341599985361099);
                    break;
                }

                case 6:
                case 5: {
                    event.setOnGround(false);
                    event.setY(event.getY() + 0.186367980844497);
                    break;
                }
            }
            this.lastY = event.getY();

            this.stage++;

            if (stage >= 24) {
                stage = 0;
                MovementUtils.strafe(MovementUtils.getBaseMoveSpeed());
            }
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (!MovementUtils.isMoving()) {
            this.ticks = 0;
            mc.thePlayer.fallDistance = 0;
            this.shouldFly = true;
        } else if (!mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindForward.pressed &&
                (mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindLeft.pressed)) {
            mc.thePlayer.setSprinting(false);
        }

        if (!mc.thePlayer.movementInput.jump) {
            event.y = mc.thePlayer.motionY = 0;

            if (this.stage > 1) {
                this.shouldFly = false;
            }

            if (stage >= 7 && MovementUtils.isMoving() && !this.shouldFly) {
                float add = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.04f : -0.04f;

                MovementUtils.strafe(event, MovementUtils.getSpeed(event) + ((this.ticks % 2 == 0 ? 0.12f : 0.11f) + add));
                MovementUtils.strafe(event, MovementUtils.getSpeed(event) - RandomUtils.nextFloat(0.012f, 0.02f));
            }

            MovementUtils.strafe(event);
        } else {
            if (mc.thePlayer.ticksExisted % 4 != 0) {
                event.y = mc.thePlayer.motionY = 0.42F;
                MovementUtils.strafe(event, MovementUtils.getBaseMoveSpeed());
            }

            this.lastY = mc.thePlayer.posY;
        }
    };
}