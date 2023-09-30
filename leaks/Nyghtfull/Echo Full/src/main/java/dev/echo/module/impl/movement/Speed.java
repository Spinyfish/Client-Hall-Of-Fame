package dev.echo.module.impl.movement;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.TickEvent;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.listener.event.impl.player.MoveEvent;
import dev.echo.listener.event.impl.player.StrafeEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.impl.combat.TargetStrafe;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.player.MoveUtil;
import dev.echo.utils.player.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
public final class Speed extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog",
            "Watchdog", "BHop", "Intave", "Verus", "BlocksMC", "Vulcan", "Vulcan Exploit", "VulcanLow");
    private final NumberSetting speed = new NumberSetting("Speed", 1, 10, 1, 0.1);

    public Speed() {
        super("Speed", Category.MOVEMENT, "Makes you go faster");
        speed.addParent(mode, modeSetting -> modeSetting.is("BHop"));
        this.addSettings(mode, speed);
    }

    public int onTicks, offTicks;
    @Link
    public Listener<TickEvent> tickEventListener = (event) -> {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        this.onTicks = mc.thePlayer.onGround ? ++this.onTicks : 0;
        this.offTicks = mc.thePlayer.onGround ? 0 : ++this.offTicks;
    };

    @Link
    public Listener<MotionEvent> motionEventListener = event -> {
        this.setSuffix(mode.getMode());

        switch (mode.getMode()) {
            case "Intave": {
                mc.gameSettings.keyBindJump.pressed = true;

                if (offTicks >= 10 && offTicks % 5 == 0) {
                    MovementUtils.setSpeed(
                            MovementUtils.getSpeed()
                    );
                }

                break;
            }
            case "BHop": {
                if (MovementUtils.isMoving()) {
                    MovementUtils.setSpeed(speed.getValue() / 4);
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                }
                break;
            }
            case "Watchdog": {
                if (MovementUtils.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        MovementUtils.strafe(MovementUtils.getSpeed());
                        mc.thePlayer.jump();
                    }
                }
                break;
            }
            case "BlocksMC": {
                if (MovementUtils.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                    if (mc.thePlayer.hurtTime == 0) {
                        MovementUtils.strafe(0.27);
                    } else {
                        MovementUtils.strafe(0.4);
                    }
                }
                break;
            }
            case "Vulcan": {
                if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    mc.thePlayer.jump();
                    MoveUtil.strafe(0.44);
                }
            }
            break;
            case "Vulcan Exploit": {
                // these are safe values
                // might flag cuz i didnt test too much
                // lower if it flags
                MovementUtils.setSpeed(0.5); // max is 2
                mc.timer.timerSpeed = 1.2f;  // you can use as much as you want xd
                if (mc.thePlayer.getDistance(mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosY, mc.thePlayer.lastReportedPosZ) <= 7.5) {
                    event.setCancelled(true);
                }
            }
            break;
            case "VulcanLow": {
                if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    mc.thePlayer.jump();
                }
                switch (mc.thePlayer.offGroundTicks) {
                    case 5:
                        mc.thePlayer.motionY = -0.2;
                        break;
                }
            }
            break;
        }
    };

    @Link
    public Listener<StrafeEvent> moveUpdateEventListener = e -> {
        if (mode.is("Verus")) {
            if (mc.thePlayer.isSprinting()) {
                if (!MovementUtils.hasAppliedSpeedII(mc.thePlayer)) {
                    if (!mc.thePlayer.onGround) {
                        if (!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                            MovementUtils.setSpeed(0.37f);
                            //0.36 might be the max value
                        } else {
                            MovementUtils.setSpeed(0.43f);
                        }
                    }
                } else {
                    MovementUtils.setSpeed(0.49f);
                    //0.48
                    // but its 49?
                }
                if (!MovementUtils.hasAppliedSpeedII(mc.thePlayer)) {
                    if (mc.thePlayer.onGround) {
                        if (!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                            MovementUtils.setSpeed(0.55f);
                            //0.50
                        } else {
                            if (mc.gameSettings.keyBindForward.isPressed()) {
                                MovementUtils.setSpeed(0.63f);
                            } else {
                                //  MovementUtils.strafe(MovementUtils.getBaseMoveSpeed() - 0.2f);
                            }
                        }
                    }
                } else {
                    if (mc.thePlayer.onGround) {
                        MovementUtils.setSpeed(0.73f);
                        //0.80 kindo flags
                        //0.79 kindo flags :skull:
                        //0.78 might flag
                        //0.77 kindo flags
                        //0.76 wtf
                        //0.75 f
                        //0.73 safe
                        //0.72
                        //0.70
                        //might be able to go a bit more
                    }

                }
            } else {
                MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed());
            }
            //  if(mc.thePlayer.onGround) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.42f;
            }
        }
    };

    @Link
    public Listener<MoveEvent> moveEventListener = TargetStrafe::strafe;


    @Override
    public void onEnable() {
        mc.timer.timerSpeed = 1f;
        if (mode.is("Vulcan Exploit")) {
            // funny packet
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;

        mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(
                mc.gameSettings.keyBindJump.getKeyCode()
        );

        super.onDisable();
    }
}