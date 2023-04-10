package crispy.features.hacks.impl.movement;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.movement.EventStrafe;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.combat.Aura;
import crispy.features.hacks.impl.misc.Disabler;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import crispy.util.player.BlockUtil;
import crispy.util.player.PlayerUtil;
import crispy.util.player.SpeedUtils;
import crispy.util.time.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import org.lwjgl.input.Keyboard;

@HackInfo(name = "Speed", key = Keyboard.KEY_X, category = Category.MOVEMENT)
public class Speed extends Hack {

    private final TimeHelper timer = new TimeHelper();
    ModeValue mode = new ModeValue("Mode", "Vanilla", "Vanilla", "NCP", "Verus", "Kauri", "Matrix (5.30)", "Matrix (6.0)", "Matrix (6.2.2)", "AAC (4.0)", "Larkus (LATEST)", "Ness", "Redesky", "Mineplex", "Mineplex LowHop", "KitX", "Watchdog");
    BooleanValue YportVerus = new BooleanValue("YPort", false, () -> mode.getMode().equalsIgnoreCase("Verus"));

    boolean jumped;
    private double speed, motionY;
    private int count;
    private boolean collided;
    private int stair, stage;
    private boolean shouldslow, half;
    private float air, groundTicks;
    private double lastDist;
    private double lastDistance;

    @Override
    public void onEnable() {
        speed = 0;
        stair = 0;
        timer.reset();

        air = 0;
        count = 0;

        groundTicks = 0;
        shouldslow = false;
        if (Minecraft.theWorld != null) {
            jumped = false;


            motionY = mc.thePlayer.motionY;
            half = mc.thePlayer.posY != (int) mc.thePlayer.posY;
            collided = mc.thePlayer.isCollidedHorizontally;
            mc.thePlayer.speedInAir = 0.02F;
            if (mode.getMode().equalsIgnoreCase("Matrix (6.2.2)")) {
                if (mc.thePlayer.isSprinting()) {
                    mc.thePlayer.setSprinting(false);
                }
            }


        }
        stage = 2;
        count = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (Minecraft.theWorld != null) {
            Timer.timerSpeed = 1;
            mc.thePlayer.speedInAir = 0.02F;
        }
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {

            Packet packet = ((EventPacket) e).getPacket();
            if (mode.getMode().equalsIgnoreCase("Mineplex")) {
                if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                    if (mc.thePlayer.ticksExisted % 5 == 0) {

                        mc.thePlayer.sendQueue.addToSendNoEvent(new C0CPacketInput());
                        mc.thePlayer.sendQueue.addToSendNoEvent(new C00PacketKeepAlive());
                    }
                }
            }
            if (mode.getMode().equalsIgnoreCase("Matrix (6.2.2)")) {

                if (packet instanceof S08PacketPlayerPosLook) {
                    mc.thePlayer.speedInAir = 0.02f;
                    timer.reset();
                }
            }
        }

        if (e instanceof EventStrafe) {
            EventStrafe eventStrafe = (EventStrafe) e;
            if (mode.getMode().equalsIgnoreCase("NCP") && !SpeedUtils.isOnGround(0.05)) {
                if (TargetStrafe.canStrafe()) {
                    eventStrafe.setCancelled(true);
                }


            }
        }
        if (e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            setDisplayName(getName() + " \2477" + mode.getMode());
            switch (mode.getMode()) {
                case "Vanilla": {
                    if (PlayerUtil.isMoving2()) {
                        SpeedUtils.setMotion(1);
                        if (SpeedUtils.isOnGround(0.0005)) {
                            mc.thePlayer.motionY = 0.42;
                            mc.thePlayer.jump();
                        }
                    } else {
                        SpeedUtils.setMotion(0);
                    }

                    break;
                }
                case "NCP": {
                    if (PlayerUtil.isMoving2()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            speed = 0.32;
                        }
                        speed -= speed / 100;
                        SpeedUtils.setMotion(speed);
                    }
                    break;
                }
                case "Watchdog": {
                    if (PlayerUtil.isMoving2()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.4199999998f;
                            speed = SpeedUtils.defaultSpeed() * 1.2f;
                        }
                        speed -= speed / 100;
                        SpeedUtils.setMotion(speed);
                    }
                    break;
                }
                case "Matrix (5.30)": {
                    if (count == 0 && PlayerUtil.isMoving2() && mc.thePlayer.onGround) {
                        count++;

                        mc.thePlayer.jump();


                    }
                    if (!SpeedUtils.isOnGround(0.05)) {
                        count = 0;
                    }
                    SpeedUtils.setMotion(SpeedUtils.getSpeed());

                    break;
                }
                case "AAC (4.0)": {
                    if (!event.isPre()) {
                        if (count == 0 && PlayerUtil.isMoving2() && SpeedUtils.isOnGround(0.05)) {
                            count++;
                            mc.thePlayer.jump();

                        }
                        if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance <= 0.1) {
                            mc.thePlayer.speedInAir = (float) 0.02;
                            Timer.timerSpeed = (float) 1;
                        }
                        if (!SpeedUtils.isOnGround(0.05)) {
                            count = 0;
                        }
                        if (mc.thePlayer.fallDistance > 0.1 && mc.thePlayer.fallDistance < 1.3) {
                            mc.thePlayer.speedInAir = (float) 0.0204;
                            Timer.timerSpeed = (float) 1;
                        }
                        if (SpeedUtils.isOnGround(0.1) && count > 1) {

                        }
                    }
                    break;
                }
                case "KitX": {
                    if (PlayerUtil.isMoving2()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            speed = 0.35;
                        }
                        speed -= speed / 100;
                        SpeedUtils.setMotion(speed);

                    }
                    break;
                }

                case "Matrix (6.0)": {
                    if (mc.thePlayer.onGround) {
                        jumped = true;
                        event.sneak = true;
                    }
                    if (!jumped)
                        return;
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.42;
                    }
                    if (mc.thePlayer.ticksExisted % 2 != 0 && !mc.thePlayer.onGround) {

                        event.sneak = false;
                        mc.thePlayer.speedInAir = 0.21f;
                        SpeedUtils.setMotion(SpeedUtils.getSpeed());
                    } else if (!mc.thePlayer.onGround) {
                        event.sneak = true;
                    }
                    break;
                }


                case "Matrix (6.2.2)": {

                    if (mc.thePlayer.onGround) {
                        jumped = true;
                        event.sneak = true;
                    }
                    if (!jumped) {
                        return;
                    }
                    if (SpeedUtils.isOnGround(0.05)) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.03;
                        }

                        if (mc.thePlayer.ticksExisted % 2 == 0) {

                            event.sneak = false;
                            if (timer.hasReached(1000)) {
                                Timer.timerSpeed = 1f;

                            } else {
                                if (timer.hasReached(0)) {

                                    mc.thePlayer.speedInAir += 0.03f;
                                }
                            }
                            SpeedUtils.setMotion(SpeedUtils.getSpeed());
                        } else if (!mc.thePlayer.onGround) {
                            event.sneak = true;
                        }
                    } else {
                        mc.thePlayer.speedInAir = 0.02f;
                        timer.reset();
                    }
                    break;
                }

                case "Larkus (LATEST)": {
                    Disabler disabler = Crispy.INSTANCE.getHackManager().getHack(Disabler.class);
                    if (disabler.modeValue.getMode().equalsIgnoreCase("Larkus") && disabler.isEnabled()) {
                        if (mc.thePlayer.onGround) {
                            SpeedUtils.setMotion(SpeedUtils.getSpeed() * 1.1);
                        }
                    } else {
                        toggle();
                        NotificationPublisher.queue("Larkus Speed", "This mode requires you to have larkus disabler on!", NotificationType.WARNING, 10000);
                    }
                    break;
                }

                case "Ness": {
                    if (PlayerUtil.isMoving2()) {
                        SpeedUtils.setMotion(SpeedUtils.defaultSpeed());
                    }
                    if (mc.thePlayer.ticksExisted % 3 == 0) {
                        Timer.timerSpeed = 1.2f;

                    } else {
                        Timer.timerSpeed = 1.0f;
                    }
                    SpeedUtils.setMotion(0.32);
                    if (SpeedUtils.isOnGround(0.0005)) {
                        speed = 0.34;
                        SpeedUtils.setMotion(speed);
                        if (mc.thePlayer.onGround) {
                            SpeedUtils.setMotion(speed);
                            mc.thePlayer.motionY = -0.006;
                        }
                        SpeedUtils.setMotion(speed);

                    }
                    break;
                }
                case "Verus": {
                    if (YportVerus.getObject()) {
                        if (PlayerUtil.isMoving2()) {
                            SpeedUtils.setMotion(SpeedUtils.defaultSpeed());

                            if (SpeedUtils.isOnGround(0.05)) {
                                SpeedUtils.setMotion(0.32);

                                mc.thePlayer.motionY = 0.4199998688697815;
                                mc.thePlayer.jump();

                            } else {
                                if (SpeedUtils.isOnGround(0.5)) {
                                    mc.thePlayer.motionY -= 0.0399998688697815;
                                }
                            }

                        }
                    } else {
                        if (PlayerUtil.isMoving2()) {
                            if (SpeedUtils.isOnGround(0.0005)) {

                                mc.thePlayer.motionY = 0.4199998688697815;
                                mc.thePlayer.jump();
                                count++;
                                speed = 0.35;
                            }
                        } else {
                            count = 0;

                        }


                        SpeedUtils.setMotion(speed);
                    }
                    break;

                }
                case "Kauri": {
                    if (PlayerUtil.isMoving2()) {
                        if (SpeedUtils.isOnGround(0.5)) {
                            mc.thePlayer.motionY -= 0.9;
                        }
                        if (SpeedUtils.isOnGround(0.005)) {
                            mc.thePlayer.motionY = 1;
                            mc.thePlayer.jump();
                            SpeedUtils.setMotion(0.48);
                        }
                    }
                    break;
                }
                case "Redesky": {
                    if (PlayerUtil.isMoving2() && SpeedUtils.isOnGround(0.05)) {
                        if (timer.hasReached(100)) {
                            int rand = Aura.randomNumber(1, 0);
                            switch (rand) {
                                case 0:
                                    Timer.timerSpeed = 1.5f;
                                    break;
                                case 1:
                                    Timer.timerSpeed = 1.1f;
                                    break;
                            }
                            timer.reset();
                        }

                    } else {
                        Timer.timerSpeed = 1;
                    }

                    break;
                }
                case "Mineplex LowHop": {
                    if (!mc.thePlayer.onGround && !SpeedUtils.isOnGround(0.01) && air > 0) {
                        final BlockPos blockPos = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.getEntityBoundingBox().minY - 1.0, this.mc.thePlayer.posZ);
                        final Vec3 vec = new Vec3(blockPos).addVector(0.4000000059604645, 0.4000000059604645, 0.4000000059604645).add(new Vec3(EnumFacing.UP.getDirectionVec()));
                        if (this.mc.thePlayer.ticksExisted % 4 == 0) {
                            this.mc.playerController.onPlayerRightClick(this.mc.thePlayer, Minecraft.theWorld, null, blockPos, EnumFacing.UP, new Vec3(vec.xCoord * 0.4000000059604645, vec.yCoord * 0.4000000059604645, vec.zCoord * 0.4000000059604645));
                        }
                        air++;
                        if (mc.thePlayer.isCollidedVertically) {
                            air = 0;
                        }
                        if (mc.thePlayer.isCollidedHorizontally && !collided) {
                            collided = true;
                        }
                        double speed = half ? 0.3 - air / 120 : 0.57 - air / 120;
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;
                        motionY -= 0.0400000000001;
                        Timer.timerSpeed = 1;
                        if (air > 24) {
                            motionY += 0.08;
                            speed = 0.2225;

                        }
                        if (air == 12) {
                        }
                        if (speed < 0.3)
                            speed = 0.30;
                        if (collided)
                            speed = 0.1873;
                        mc.thePlayer.motionY = motionY;

                        SpeedUtils.setMotion(speed);
                    } else {
                        if (air > 0) {
                            air = 0;
                        }
                    }

                    if (mc.thePlayer.onGround && SpeedUtils.isOnGround(0.01) && mc.thePlayer.isCollidedVertically && (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0)) {

                        double groundspeed = 0;
                        collided = mc.thePlayer.isCollidedHorizontally;
                        groundTicks++;
                        mc.thePlayer.motionX *= groundspeed;
                        mc.thePlayer.motionZ *= groundspeed;

                        half = mc.thePlayer.posY != (int) mc.thePlayer.posY;
                        mc.thePlayer.motionY = 0.2499999;
                        Timer.timerSpeed = 1;
                        air = 1;
                        motionY = mc.thePlayer.motionY;
                    }
                    break;
                }


                case "Mineplex": {
                    motionY -= 0.0250000000001;
                    if (Speed.airSlot() == -10) {
                        SpeedUtils.setMotion(0);
                        return;
                    }
                    if (!mc.thePlayer.onGround && !SpeedUtils.isOnGround(0.01) && air > 0) {
                        air++;
                        if (!PlayerUtil.isMoving2()) {
                            speed -= 0.7;

                        }

                        if (mc.thePlayer.isCollidedVertically) {
                            air = 0;
                        }
                        if (mc.thePlayer.isCollidedHorizontally && !collided) {
                            collided = true;
                        }
                      // final BlockPos blockPos = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.getEntityBoundingBox().minY - 1.0, this.mc.thePlayer.posZ);
                      // final Vec3 vec = new Vec3(blockPos).addVector(0.4000000059604645, 0.4000000059604645, 0.4000000059604645).add(new Vec3(EnumFacing.UP.getDirectionVec()));
                      // if (this.mc.thePlayer.ticksExisted % 3 == 0) {
                      //     this.mc.playerController.onPlayerRightClick(this.mc.thePlayer, Minecraft.theWorld, null, blockPos, EnumFacing.UP, new Vec3(vec.xCoord * 0.4000000059604645, vec.yCoord * 0.4000000059604645, vec.zCoord * 0.4000000059604645));
                      // }
                        speed -= speed / 120;
                        if (speed < 0.35) {
                            speed = 0.35;
                        }
                        if (speed > 1.55) {
                            speed -= 0.02;

                        }
                        if (collided)
                            speed = 0.2873;
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;

                        Timer.timerSpeed = 1;
                        if (air < 14) {
                            mc.thePlayer.sendQueue.addToSendNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                            BlockUtil.placeHeldItemUnderPlayer();
                            //Crispy.addChatMessage("working?");
                        }

                       // Crispy.addChatMessage(speed + " Air = " + air);


                        mc.thePlayer.motionY = motionY;

                        SpeedUtils.setMotion(speed);
                    } else {

                        if (air > 0) {
                            air = 0;
                        }
                    }

                    if (mc.thePlayer.onGround && SpeedUtils.isOnGround(0.01) && mc.thePlayer.isCollidedVertically && (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0)) {
                        if (mc.thePlayer.ticksExisted % 3 == 0) {
                            mc.thePlayer.sendQueue.addToSendNoEvent(new C09PacketHeldItemChange(Speed.airSlot()));
                            BlockUtil.placeHeldItemUnderPlayer();

                        } else {
                            mc.thePlayer.sendQueue.addToSendNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        }
                        count++;
                        double groundspeed = 0;
                        collided = mc.thePlayer.isCollidedHorizontally;
                        groundTicks++;
                        speed += 0.21;
                        mc.thePlayer.motionX *= groundspeed;
                        mc.thePlayer.motionZ *= groundspeed;
                        half = mc.thePlayer.posY != (int) mc.thePlayer.posY;
                        mc.thePlayer.motionY = 0.42;
                        air = 1;

                        motionY = mc.thePlayer.motionY;
                    }
                    break;
                }
            }

        }
    }

    public static int airSlot() {
        for (int j = 0; j < 8; ++j) {
            if (Minecraft.getMinecraft().thePlayer.inventory.mainInventory[j] == null) {
                return j;
            }
        }
        Crispy.addChatMessage("Clear a hotbar slot.");
        return -10;
    }
}