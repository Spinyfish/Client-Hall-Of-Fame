package crispy.features.hacks.impl.movement;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.EventDirection;
import crispy.features.event.impl.movement.EventCollide;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.combat.Aura;
import crispy.features.hacks.impl.misc.AntiVoid;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import crispy.util.player.BlockUtil;
import crispy.util.player.PlayerUtil;
import crispy.util.player.SpeedUtils;
import crispy.util.time.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Timer;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;

import java.util.ArrayList;

@HackInfo(name = "Fly", category = Category.MOVEMENT)
public class Fly extends Hack {

    private final TimeHelper timer = new TimeHelper();
    private final ArrayList<Packet> packets = new ArrayList<>();
    ModeValue mode = new ModeValue("Mode", "Vanilla", "Vanilla", "AntiKick", "Matrix (6.0)", "AAC 4.0", "Larkus", "Larkusv2", "Collide", "Dev", "Redesky", "RedeskyTP", "RedeskyInf", "WatchCat", "Damage", "Kraken", "Mineplex", "Zonecraft", "Vulcan");
    NumberValue<Double> flySpeed = new NumberValue<Double>("Speed", 1D, 0.3D, 6.5D, () -> mode.getMode().equalsIgnoreCase("Damage") || mode.getMode().equalsIgnoreCase("Vanilla") || mode.getMode().equalsIgnoreCase("AAC") || mode.getMode().equalsIgnoreCase("AntiKick"));
    BooleanValue bobbing = new BooleanValue("Bobbing", false);
    BooleanValue antiKickPacket = new BooleanValue("Anti Kick Packet", true, () -> mode.getMode().equalsIgnoreCase("AntiKick"));
    BooleanValue silent = new BooleanValue("Silent", false, () -> mode.getMode().equalsIgnoreCase("Mineplex"));
    ModeValue type = new ModeValue("Damage Type", "Normal", () -> mode.getMode().equalsIgnoreCase("Damage"), "Normal", "Verus");
    boolean jumped;
    boolean VerusCollision;
    AxisAlignedBB box;
    private double speedVerus, motionY, speed;
    private int stair, stage, count;
    private boolean collided, half;
    private boolean shouldslow;
    private int air;
    private double flyHeight;

    public static int airSlot() {
        for (int j = 0; j < 8; ++j) {
            if (Minecraft.getMinecraft().thePlayer.inventory.mainInventory[j] == null) {
                return j;
            }
        }
        Crispy.addChatMessage("Clear a hotbar slot.");
        return -10;
    }

    @Override
    public void onEnable() {
        jumped = false;

        air = 0;
        VerusCollision = false;
        if (mode.getMode().equalsIgnoreCase("Larkus")) {
            NotificationPublisher.queue("Frog time!", "Contribe: \"Man just flew wtf\"", NotificationType.INFO, 5000);
        }
        if (mode.getMode().equalsIgnoreCase("Collide")) {
            VerusCollision = true;
        }
        packets.clear();
        if (mode.getMode().equalsIgnoreCase("ZoneCraft")) {

            mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.42f, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        }
        /*
        Hypixel dmg method
         */
        //for(int i = 0; i < 55; i++) {
        //    mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,  mc.thePlayer.posY + 0.03125, mc.thePlayer.posZ, false));
        //    mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,  mc.thePlayer.posY - 0.03125, mc.thePlayer.posZ, false));
        //}
        if (mode.getMode().equalsIgnoreCase("Damage")) {

            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.42f, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            if (type.getMode().equalsIgnoreCase("Verus")) {

                mc.thePlayer.motionY = 0.42;
            }
        }
        if (Minecraft.theWorld != null) {
            motionY = mc.thePlayer.motionY;
            half = mc.thePlayer.posY != (int) mc.thePlayer.posY;
            collided = mc.thePlayer.isCollidedHorizontally;
            mc.thePlayer.speedInAir = 0.02F;
            jumped = false;
            speed = SpeedUtils.getSpeed();
            flyHeight = mc.thePlayer.posY;
            shouldslow = false;
            if(type.getMode().equalsIgnoreCase("Vulcan")) mc.gameSettings.keyBindJump.pressed = false;
        }
        timer.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (Minecraft.theWorld != null) {
            mc.thePlayer.speedInAir = 0.02f;
            mc.gameSettings.keyBindJump.pressed = false;
            count = 0;
            if (!mode.getMode().contains("Redesky") && !mode.getMode().equalsIgnoreCase("Mineplex")) {
                mc.thePlayer.motionY = 0;
                mc.thePlayer.motionZ = 0;

                mc.thePlayer.motionX = 0;
            }
            Timer.timerSpeed = 1;
            if (!SpeedUtils.isOnGround(0.05)) {
                Crispy.INSTANCE.getHackManager().getHack(AntiVoid.class).setFlagged(true);
            }
            for(Packet packet : packets) {
                mc.thePlayer.sendQueue.addToSendNoEvent(packet);
            }
            packets.clear();
        }
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {

            Packet packet = ((EventPacket) e).getPacket();
            switch (mode.getMode()) {
                case "Collide": {
                    if (packet instanceof C0FPacketConfirmTransaction) {
                        e.setCancelled(true);
                    }
                    break;
                }
                case "Damage": {
                    if (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
                        e.setCancelled(true);
                    }
                    break;
                }
                case "Dev": {
                    if (packet instanceof C03PacketPlayer) {
                        if (mc.thePlayer.ticksExisted % 2 == 0) {
                            e.setCancelled(true);
                        }
                    }
                    break;
                }
                case "Vulcan": {
                    break;
                }
                case "Zonecraft": {
                    Timer.timerSpeed = 0.3f;

                    if (packet instanceof S08PacketPlayerPosLook) {
                        if (Minecraft.theWorld != null) {
                            S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) packet;
                            double x = s08PacketPlayerPosLook.getX();
                            double y = s08PacketPlayerPosLook.getY();
                            double z = s08PacketPlayerPosLook.getZ();
                            double distancex = Math.abs(Math.abs(mc.thePlayer.posX) - Math.abs(x));
                            double distancey = Math.abs(Math.abs(mc.thePlayer.posY) - Math.abs(y));
                            double distancez = Math.abs(Math.abs(mc.thePlayer.posZ) - Math.abs(z));

                            double distance = Math.sqrt(distancex * distancex + distancey * distancey + distancez * distancez);
                            if (distance > 20) {

                                e.setCancelled(false);
                            } else {
                                e.setCancelled(true);
                                mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, s08PacketPlayerPosLook.getYaw(), s08PacketPlayerPosLook.getPitch(), false));
                            }
                        }
                    }
                    break;
                }
                case "AAC 4.0": {

                    if (packet instanceof S08PacketPlayerPosLook) {
                        if (Minecraft.theWorld != null) {
                            S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) packet;
                            double x = s08PacketPlayerPosLook.getX();
                            double y = s08PacketPlayerPosLook.getY();
                            double z = s08PacketPlayerPosLook.getZ();
                            double distancex = Math.abs(Math.abs(mc.thePlayer.posX) - Math.abs(x));
                            double distancey = Math.abs(Math.abs(mc.thePlayer.posY) - Math.abs(y));
                            double distancez = Math.abs(Math.abs(mc.thePlayer.posZ) - Math.abs(z));

                            double distance = Math.sqrt(distancex * distancex + distancey * distancey + distancez * distancez);
                            if (distance > 50) {
                                e.setCancelled(false);
                            } else {
                                e.setCancelled(true);
                                mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, s08PacketPlayerPosLook.getYaw(), s08PacketPlayerPosLook.getPitch(), true));
                            }
                        }
                    }
                    break;
                }
                case "Redesky": {
                    if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition || packet instanceof C03PacketPlayer.C05PacketPlayerLook) {


                        double forward = mc.thePlayer.movementInput.moveForward;
                        double strafe = mc.thePlayer.movementInput.moveStrafe;
                        float yaw = mc.thePlayer.rotationYaw;
                        if ((forward == 0.0D) && (strafe == 0.0D)) {
                            mc.thePlayer.motionX = 0;
                            mc.thePlayer.motionZ = 0;
                        } else {
                            if (forward != 0.0D) {
                                if (strafe > 0.0D) {
                                    yaw += (forward > 0.0D ? -45 : 45);
                                } else if (strafe < 0.0D) {
                                    yaw += (forward > 0.0D ? 45 : -45);
                                }
                                strafe = 0.0D;
                                if (forward > 0.0D) {
                                    forward = 1;
                                } else if (forward < 0.0D) {
                                    forward = -1;
                                }
                            }
                        }
                        e.setCancelled(true);
                        packets.add(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX + (forward * 3 * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * 3 * Math.sin(Math.toRadians(yaw + 90F))), mc.thePlayer.posY, mc.thePlayer.posZ + (forward * 3 * Math.sin(Math.toRadians(yaw + 90)) - strafe * 3 * Math.cos(Math.toRadians(yaw + 90))), mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));


                    }
                    break;
                }


            }
        }
        if (e instanceof EventCollide) {
            EventCollide event = (EventCollide) e;
            if ((mode.getMode().equalsIgnoreCase("Vulcan") && VerusCollision) ||mode.getMode().equalsIgnoreCase("Collide") || (mode.getMode().equalsIgnoreCase("Damage") && type.getMode().equalsIgnoreCase("Verus"))) {
                double x = event.getPosX();
                double y = event.getPosY();
                double z = event.getPosZ();
                event.setBoundingBox(AxisAlignedBB.fromBounds(-50, -1, -50, 50, 1.0F, 50).offset(x, y, z));

            }
        }
        if (e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            setDisplayName(getName() + " \2477" + mode.getMode());
            if (!mc.thePlayer.onGround && bobbing.getObject()) {
                mc.thePlayer.cameraYaw = 0.1f;
            } else if (!bobbing.getObject()) {
                mc.thePlayer.cameraYaw = 0;
            }
            switch (mode.getMode()) {
                case "Vulcan": {
                    mc.thePlayer.posY = mc.thePlayer.prevPosY;
                    mc.thePlayer.cameraPitch = 0;

                    mc.gameSettings.keyBindJump.pressed = true;
                    if(this.mc.getRenderViewEntity().getEntityBoundingBox().minY < flyHeight) {
                        VerusCollision = true;
                    } else {
                        VerusCollision = false;
                    }
                    SpeedUtils.setMotion(SpeedUtils.getSpeed());
                    break;
                }
                case "Damage": {
                    if (type.getMode().equalsIgnoreCase("Normal")) {
                        mc.thePlayer.motionY = 0;

                        if (mc.gameSettings.keyBindJump.pressed) {
                            mc.thePlayer.motionY += flySpeed.getObject();
                        } else if (mc.gameSettings.keyBindSneak.pressed) {
                            mc.thePlayer.motionY -= flySpeed.getObject();
                        }
                        mc.thePlayer.speedInAir = 0.07f;
                        SpeedUtils.setMotion(flySpeed.getObject());
                        event.ground = true;
                    } else if (type.getMode().equalsIgnoreCase("Verus")) {
                        count++;
                        if (mc.thePlayer.hurtTime == 5) {
                            jumped = true;
                            speedVerus = flySpeed.getObject();

                        }
                        if (jumped) {
                            event.ground = true;

                            SpeedUtils.setMotion(speedVerus);
                            if (jumped) {
                                Timer.timerSpeed = 1;
                            }
                        }
                    }

                    break;
                }
                case "Vanilla":
                case "AAC 4.0": {
                    mc.thePlayer.motionY = 0;
                    if (mc.gameSettings.keyBindJump.pressed) {
                        mc.thePlayer.motionY += flySpeed.getObject();
                    } else if (mc.gameSettings.keyBindSneak.pressed) {
                        mc.thePlayer.motionY -= flySpeed.getObject();
                    }
                    mc.thePlayer.speedInAir = 0.07f;

                    SpeedUtils.setMotion(flySpeed.getObject());
                    break;
                }
                case "Kraken": {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                    mc.thePlayer.moveForward = 2f;
                    mc.thePlayer.motionY = 0f;
                    break;
                }
                case "Mineplex": {
                    if (Speed.airSlot() == -10) {
                        SpeedUtils.setMotion(0);
                        return;
                    }
                    mc.thePlayer.setSprinting(true);
                    final boolean ready = speed > 1.83;
                    if (jumped) {
                        motionY -= 0.0210000000001;
                    } else {
                        motionY -= 0.190000000001;
                        Timer.timerSpeed = 1f;


                    }

                    if (!SpeedUtils.isOnGround(0.01) && air > 0) {
                        if (silent.getObject()) {
                            mc.thePlayer.posY = mc.thePlayer.prevPosY;
                            mc.thePlayer.cameraPitch = 0;
                        }
                        air++;
                        if (mc.thePlayer.isCollidedVertically) {
                            air = 0;
                        }
                        if (mc.thePlayer.isCollidedHorizontally && !collided) {
                            collided = true;
                        }

                        speed -= speed / 122;
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;

                        Timer.timerSpeed = 1;

                        if (air == 12) {
                            mc.thePlayer.motionY = 0;
                        }
                        if (air < 14) {
                            mc.thePlayer.sendQueue.addToSendNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                            BlockUtil.placeHeldItemUnderPlayer();
                            //Crispy.addChatMessage("working?");

                        }
                        if (speed < 0.45)
                            speed = 0.45;
                        if (collided)
                            speed = 0.2873;

                        mc.thePlayer.motionY = motionY;

                        if (jumped) {
                            SpeedUtils.setMotion(speed);
                        } else {
                            SpeedUtils.setMotion(shouldslow ? -speed : speed);
                        }

                    } else {

                        if (air > 0) {
                            air = 0;
                        }
                    }
                    if (timer.hasReached(5)) {
                        shouldslow = !shouldslow;
                        timer.reset();
                    }

                    if (mc.thePlayer.onGround && SpeedUtils.isOnGround(0.01) && mc.thePlayer.isCollidedVertically && (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0)) {
                        if (ready) {
                            jumped = true;
                        }
                        count++;
                        if (mc.thePlayer.ticksExisted % 3 == 0) {
                            mc.thePlayer.sendQueue.addToSendNoEvent(new C09PacketHeldItemChange(Speed.airSlot()));
                            BlockUtil.placeHeldItemUnderPlayer();

                        } else {
                            mc.thePlayer.sendQueue.addToSendNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        }
                        collided = mc.thePlayer.isCollidedHorizontally;
                        speed += 0.22;
                        mc.thePlayer.motionX *= 0;
                        mc.thePlayer.motionZ *= 0;
                        half = mc.thePlayer.posY != (int) mc.thePlayer.posY;
                        mc.thePlayer.motionY = 0.42;
                        air = 1;
                        motionY = mc.thePlayer.motionY;
                    }
                    break;
                }
                case "Zonecraft": {
                    event.ground = true;

                    mc.thePlayer.motionY = 0;
                    if (mc.gameSettings.keyBindJump.pressed) {
                        mc.thePlayer.motionY += flySpeed.getObject();
                    } else if (mc.gameSettings.keyBindSneak.pressed) {
                        mc.thePlayer.motionY -= flySpeed.getObject();
                    }
                    mc.thePlayer.speedInAir = 0.07f;

                    SpeedUtils.setMotion(flySpeed.getObject());
                    break;
                }
                case "Collide": {
                    event.setYaw(MathHelper.wrapAngleTo180_float(Aura.randomNumber(360, -360)));
                    event.setPitch(Aura.randomNumber(90, -90));
                    mc.thePlayer.setSprinting(false);
                    if (event.isPre()) {
                        stair++;
                        if (stair <= 7) {

                            VerusCollision = false;
                        } else {
                            speedVerus = 0.37;
                            VerusCollision = true;
                        }
                        if (VerusCollision && mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            stair = 0;
                        } else if (!VerusCollision) {
                            mc.thePlayer.motionY = 0;
                            event.ground = true;
                        }
                        speedVerus -= speedVerus / 200;
                        if(mc.thePlayer.ticksExisted % 5 == 0 ) {
                            SpeedUtils.setMotion(0);
                        } else {
                            SpeedUtils.setMotion(Math.max(speedVerus, 0.15));

                        }

                    }
                    break;
                }

                case "AntiKick": {
                    SpeedUtils.setMotion(flySpeed.getObject());
                    if (mc.gameSettings.keyBindJump.pressed) {
                        mc.thePlayer.motionY = flySpeed.getObject();
                    } else if (mc.gameSettings.keyBindSneak.pressed) {
                        mc.thePlayer.motionY = -flySpeed.getObject();
                    } else {
                        if (mc.thePlayer.ticksExisted % 2 == 0) {
                            mc.thePlayer.posY = mc.thePlayer.prevPosY;
                            mc.thePlayer.cameraPitch = mc.thePlayer.prevCameraPitch;

                            mc.thePlayer.motionY = 0.04;

                        }
                    }
                    if(antiKickPacket.getObject()) {
                        if(timer.hasReached(100)) {
                            updateFlyHeight();
                            goToGround();
                            timer.reset();
                        }
                    }
                    break;
                }

                case "Matrix (6.0)": {
                    if (!jumped && mc.thePlayer.onGround) {
                        jumped = true;
                        event.sneak = true;
                    }
                    if (timer.hasReached(500)) {


                        if (count < 16) {
                            count++;

                        }
                        timer.reset();
                    }
                    mc.thePlayer.speedInAir = 0.03f;

                    if (!PlayerUtil.isMoving()) {
                        count = 0;
                    }
                    event.sneak = false;
                    SpeedUtils.setMotion(SpeedUtils.getSpeed());
                    if (mc.gameSettings.keyBindJump.pressed) {
                        mc.thePlayer.motionY = 0.41;
                    } else if (mc.gameSettings.keyBindSneak.pressed) {
                        mc.thePlayer.motionY = -0.4;
                    } else {
                        if (mc.thePlayer.ticksExisted % 2 == 0) {
                            mc.thePlayer.motionY = 0.02;

                        } else {
                            event.sneak = true;
                        }

                    }
                    break;
                }
                case "Larkus": {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY += 0.42, mc.thePlayer.posZ);
                    break;
                }
                case "Larkusv2": {
                    Timer.timerSpeed = 0.1f;
                    mc.thePlayer.motionY = +0.01;
                    SpeedUtils.setPositionNoUp(5);
                    break;
                }
                case "Redesky": {
                    mc.thePlayer.motionY = 0;
                    if (mc.gameSettings.keyBindJump.pressed) {
                        mc.thePlayer.motionY += flySpeed.getObject();
                    } else if (mc.gameSettings.keyBindSneak.pressed) {
                        mc.thePlayer.motionY -= flySpeed.getObject();
                    }
                    mc.thePlayer.speedInAir = 0.07f;

                    SpeedUtils.setMotion(flySpeed.getObject());
                    if (timer.hasReached(1000)) {
                        timer.reset();
                        for (Packet packet : packets) mc.thePlayer.sendQueue.addToSendNoEvent(packet);
                    }
                    break;
                }
                case "RedeskyTP": {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                    if (mc.thePlayer.motionY < 0) {
                        Timer.timerSpeed = 0.4f;

                        mc.thePlayer.speedInAir = 0.07f;
                        mc.thePlayer.motionY = 0.00;
                        if (mc.thePlayer.ticksExisted % 10 == 0) {

                            Timer.timerSpeed = 10; //25

                        }
                    }
                    break;
                }
                case "Dev": {
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.onGround = true;
                    break;
                }
                case "RedeskyInf": {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                    if (mc.thePlayer.motionY < 0) {
                        mc.thePlayer.motionY = 0.005;
                        mc.thePlayer.speedInAir = 0.02f;
                        Timer.timerSpeed = 0.05f;
                    }
                    break;
                }
                case "WatchCat": {
                    SpeedUtils.setMotion(5);
                    if (mc.thePlayer.ticksExisted % 6 == 0) {
                        mc.thePlayer.motionY = 0;

                        if (mc.gameSettings.keyBindJump.pressed) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 10, mc.thePlayer.posZ);
                        }
                    }
                    break;
                }
            }
        }
    }

    public void updateFlyHeight() {
        double h = 1.0D;
        AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox().expand(0.0625D, 0.0625D, 0.0625D);

        for (this.flyHeight = 0.0D; this.flyHeight < mc.thePlayer.posY; this.flyHeight += h) {
            AxisAlignedBB nextBox = box.offset(0.0D, -this.flyHeight, 0.0D);
            if (Minecraft.theWorld.checkBlockCollision(nextBox)) {
                if (h < 0.0625D) {
                    break;
                }

                this.flyHeight -= h;
                h /= 2.0D;
            }
        }

    }

    public void goToGround() {
        if (this.flyHeight <= 300.0D) {
            double minY = mc.thePlayer.posY - this.flyHeight;
            if (minY > 0.0D) {
                double y = mc.thePlayer.posY;

                C03PacketPlayer.C04PacketPlayerPosition packet;
                while (y > minY) {
                    y -= 100.0D;
                    if (y < minY) {
                        y = minY;
                    }

                    packet = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true);
                    mc.thePlayer.sendQueue.addToSendNoEvent(packet);
                }

                y = minY;
                //No going upward

            }
        }
    }
}