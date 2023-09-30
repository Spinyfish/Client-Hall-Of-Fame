package dev.echo.module.impl.movement;

import dev.echo.Echo;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.TickEvent;
import dev.echo.listener.event.impl.network.PacketSendEvent;
import dev.echo.listener.event.impl.player.EntityAction;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.listener.event.impl.player.MoveEvent;
import dev.echo.listener.event.impl.player.StrafeEvent;
import dev.echo.listener.event.impl.render.Render3DEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.impl.combat.KillAura;
import dev.echo.module.impl.combat.TargetStrafe;
import dev.echo.module.impl.render.HUDMod;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.misc.Random;
import dev.echo.utils.player.*;
import dev.echo.utils.render.RenderUtil;
import dev.echo.utils.server.PacketUtils;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
public final class Speed extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog",
            "Watchdog", "BHop", "Intave", "Verus", "BlocksMC", "Vulcan","uwu", "Vulcan Exploit");
    private final NumberSetting speed = new NumberSetting("Speed", 1, 10, 1, 0.1);
    private double speed2;

    private boolean wasCollided;

    private boolean prevOnGround;

    private int counter, ticks, offGroundTicks, ticksSinceVelocity;

    private boolean takingVelocity, wasTakingVelocity;
    private double velocityX, velocityY, velocityZ;
    private double velocityDist;

    private float lastDirection;

    private float lastYaw;

    private double motionX, motionY, motionZ;

    private double actualX, actualY, actualZ, lastActualX, lastActualY, lastActualZ;

    private boolean actualGround;

    private boolean started, firstJumpDone;

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

            case"uwu": {
                event.setX(actualX);
                event.setY(actualY);
                event.setZ(actualZ);
                event.setOnGround(actualGround);

                float direction = RotationUtils.getRotationsToPosition(lastActualX, lastActualY, lastActualZ, mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)[0];


                    final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                    final float gcd = f * f * f * 1.2F;

                    final float deltaYaw = direction - lastYaw;

                    final float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);

                    direction = lastYaw + fixedDeltaYaw;

                    lastYaw = direction;
                    event.setYaw(direction);
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
                        MovementUtils.strafe(MovementUtils.getSpeed() * 1.01);
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
    public Listener<Render3DEvent> onRender3D = e -> {
        if(mode.is("uwu")) {
            if(mc.gameSettings.thirdPersonView > 0) {
                RenderUtil.prepareBoxRender(3.25F, HUDMod.getClientColors().getFirst().getRed(), HUDMod.getClientColors().getFirst().getGreen(), HUDMod.getClientColors().getFirst().getBlue(), 0.8F);
                RenderUtil.renderCustomPlayerBox(mc.getRenderManager(), mc.timer.elapsedPartialTicks, actualX, actualY, actualZ, lastActualX, lastActualY, lastActualZ);
                RenderUtil.stopBoxRender();
            }
        }
    };

    @Link
    public Listener<MoveEvent> moveEventListener = event -> {
        float direction = MathHelper.wrapAngleTo180_float(MovementUtils.getPlayerDirection2());
        if(mode.is("uwu")) {
            double distance = Math.hypot(mc.thePlayer.posX - actualX, mc.thePlayer.posZ - actualZ);


            if(mc.thePlayer.onGround && MovementUtils.isMoving()) {
                MovementUtils.jump(event);
            }

            if(!started) {
                speed2 = 0.65;
                started = true;
            } else {
                    double baseSpeed = 0.33 + MovementUtils.getSpeedAmplifier() * 0.02;

                    if(mc.thePlayer.onGround) {
                        speed2 = 0.33 + baseSpeed;
                    } else {
                        speed2 = Math.min(speed2 - baseSpeed * distance * 0.15, baseSpeed);
                    }

                    speed2 = Math.max(speed2, 0.2);
            }

            MovementUtils.setSpeed(event, MovementUtils.getBaseMoveSpeed());

            lastDirection = direction;
        }
    };

    @Link
    public Listener<EntityAction> entityActionListener = event -> {
        if(mode.is("uwu")) {
            lastActualX = actualX;
            lastActualY = actualY;
            lastActualZ = actualZ;

            float direction = RotationUtils.getRotationsToPosition(lastActualX, lastActualY, lastActualZ, mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)[0];

            float gcd = RotationUtils.getGCD();

            float yawDiff = (direction - lastYaw);

            float fixedYawDiff = yawDiff - (yawDiff % gcd);

            direction = lastYaw + fixedYawDiff;

            float dir = direction * 0.017453292F;

            float friction = MovementUtils.getFriction(actualX, actualY, actualZ) * 0.91F;

            if (actualGround) {
                motionY = (double) mc.thePlayer.getJumpUpwardsMotion();

                if (mc.thePlayer.isPotionActive(Potion.jump)) {
                    motionY += (double) ((float) (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
                }

                if (!wasCollided) {
                    motionX -= (double) (MathHelper.sin(dir) * 0.2F);
                    motionZ += (double) (MathHelper.cos(dir) * 0.2F);
                }
            }

            float aa = 0.16277136F / (friction * friction * friction);

            float attributeSpeed;

            mc.thePlayer.setSprinting(!wasCollided);

            if (actualGround) {
                attributeSpeed = mc.thePlayer.getAIMoveSpeed() * aa;
            } else {
                attributeSpeed = wasCollided ? 0.02F : 0.026F;
            }

            boolean oldActualGround = actualGround;

            float forward = 0.98F;
            float strafe = 0F;

            float thing = strafe * strafe + forward * forward;

            if (thing >= 1.0E-4F) {
                thing = MathHelper.sqrt_float(thing);

                if (thing < 1.0F) {
                    thing = 1.0F;
                }

                thing = attributeSpeed / thing;
                strafe = strafe * thing;
                forward = forward * thing;
                float f1 = MathHelper.sin(direction * (float) Math.PI / 180.0F);
                float f2 = MathHelper.cos(direction * (float) Math.PI / 180.0F);
                motionX += (double) (strafe * f2 - forward * f1);
                motionZ += (double) (forward * f2 + strafe * f1);
            }

            // if(groundStrafe.isEnabled() && actualGround) {
            if (actualGround) {
                double speed = Math.hypot(motionX, motionZ);

                motionX = -Math.sin(Math.toRadians(direction)) * speed;
                motionZ = Math.cos(Math.toRadians(direction)) * speed;
            }
            //}

            double clientX = mc.thePlayer.posX;
            double clientY = mc.thePlayer.posY;
            double clientZ = mc.thePlayer.posZ;

            double clientMotionX = mc.thePlayer.motionX;
            double clientMotionY = mc.thePlayer.motionY;
            double clientMotionZ = mc.thePlayer.motionZ;

            boolean clientGround = mc.thePlayer.onGround;

            mc.thePlayer.setPosition(actualX, actualY, actualZ);

            mc.thePlayer.onGround = actualGround;

            mc.thePlayer.moveEntityNoEvent(motionX, motionY, motionZ);

            boolean collided = mc.thePlayer.isCollidedHorizontally;

            motionX = mc.thePlayer.posX - lastActualX;
            motionY = mc.thePlayer.posY - lastActualY;
            motionZ = mc.thePlayer.posZ - lastActualZ;

            actualX = mc.thePlayer.posX;
            actualY = mc.thePlayer.posY;
            actualZ = mc.thePlayer.posZ;

            actualGround = mc.thePlayer.onGround;

            mc.thePlayer.setPosition(clientX, clientY, clientZ);
            mc.thePlayer.onGround = clientGround;

            mc.thePlayer.motionX = clientMotionX;
            mc.thePlayer.motionY = clientMotionY;
            mc.thePlayer.motionZ = clientMotionZ;

            if (oldActualGround) {
                motionX *= friction * 0.91F;
                motionZ *= friction * 0.91F;
            } else {
                motionX *= 0.91F;
                motionZ *= 0.91F;
            }

            motionY -= 0.08;
            this.motionY *= 0.9800000190734863D;

            if (Math.abs(motionX) < 0.005) {
                motionX = 0;
            }

            if (Math.abs(motionY) < 0.005) {
                motionY = 0;
            }

            if (Math.abs(motionZ) < 0.005) {
                motionZ = 0;
            }
            event.setSprinting(!wasCollided);

            mc.thePlayer.setSprinting(true);

            event.setSneaking(false);

            wasCollided = collided;
        }
    };


    @Override
    public void onEnable() {
        prevOnGround = false;
        speed2 = 0.28;
        wasCollided = false;
        ticks = offGroundTicks = counter = 0;

        ticksSinceVelocity = Integer.MAX_VALUE;

        started = firstJumpDone = false;

        takingVelocity = wasTakingVelocity = false;

        motionX = mc.thePlayer.motionX;
        motionY = mc.thePlayer.motionY;
        motionZ = mc.thePlayer.motionZ;

        actualX = mc.thePlayer.posX;
        actualY = mc.thePlayer.posY;
        actualZ = mc.thePlayer.posZ;

        actualGround = mc.thePlayer.onGround;

        lastDirection = MovementUtils.getPlayerDirection();

        lastYaw = mc.thePlayer.rotationYaw;

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
        if(mode.is("uwu")) {
            mc.thePlayer.setPosition(actualX, actualY, actualZ);
            mc.thePlayer.motionX = motionX;
            mc.thePlayer.motionY = motionY;
            mc.thePlayer.motionZ = motionZ;

            mc.thePlayer.onGround = actualGround;
        }

        super.onDisable();
    }
}