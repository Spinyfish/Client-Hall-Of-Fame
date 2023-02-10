/**
 * @project Myth
 * @author Skush/Duzey
 * @at 05.08.2022
 */
package dev.myth.features.movement;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.logger.Logger;
import dev.myth.api.utils.MovementUtil;
import dev.myth.api.utils.PlayerUtil;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.events.CollisonEvent;
import dev.myth.events.MoveEvent;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.optifine.util.MathUtils;
import org.lwjgl.util.vector.Matrix;

import java.util.concurrent.CopyOnWriteArrayList;

@Feature.Info(
        name = "Flight",
        description = "Allows you to fly",
        category = Feature.Category.MOVEMENT
)
public class FlightFeature extends Feature {


    /**
     * Settings
     */
    public final EnumSetting<FlightMode> flightMode = new EnumSetting<>("Mode", FlightMode.VANILLA);

    /* Fly Types */
    public final EnumSetting<MatrixMode> matrixMode = new EnumSetting<>("Type", MatrixMode.BLINK).addDependency(() -> flightMode.is(FlightMode.MATRIX));
    public final EnumSetting<VerusMode> verusMode = new EnumSetting<>("VerusType", VerusMode.BOW).addDependency(() -> flightMode.is(FlightMode.VERUS)).setDisplayName("Type");

    /**
     * Vanilla
     */
    public final NumberSetting vanSpeed = new NumberSetting("VanillaSpeed", 0.1, 0.0, 1.0, 0.01)
            .addDependency(() -> this.flightMode.getValue() == FlightMode.VANILLA).setDisplayName("Speed");

    /**
     * Motion
     */
    public final NumberSetting motionSpeed = new NumberSetting("MotionSpeed", 0.25, 0.0, 5.0, 0.01).addDependency(
            () -> this.flightMode.getValue() == FlightMode.MOTION || this.flightMode.getValue() == FlightMode.BLINK_FLAG || this.flightMode.getValue() == FlightMode.VERUS).setDisplayName("Speed");

    /**
     * Old NCP
     */
    public final NumberSetting oldNcpSpeed = new NumberSetting("OldNcpSpeed", 3, 1, 10, 0.01).addDependency(
            () -> this.flightMode.getValue() == FlightMode.OLD_NCP).setDisplayName("Speed");
    public final BooleanSetting oldNcpDamage = new BooleanSetting("OldNcpDamage", true).addDependency(
            () -> this.flightMode.getValue() == FlightMode.OLD_NCP).setDisplayName("Damage");
    //    public final BooleanSetting oldNcpWait = new BooleanSetting("OldNcpWait", true).addDependency(
//            () -> this.flightMode.getValue() == FlightMode.OLD_NCP && this.oldNcpDamage.getValue()).setDisplayName("Wait");
    public final NumberSetting oldNcpTimerTicks = new NumberSetting("OldNcpTimerTicks", 10, 0, 50, 1).addDependency(
            () -> this.flightMode.getValue() == FlightMode.OLD_NCP).setDisplayName("TimerTicks").addValueAlias(0, "Off");
    public final NumberSetting oldNcpTimerBoost = new NumberSetting("OldNcpTimerBoost", 3, 1, 5, 0.1).addDependency(
            () -> this.flightMode.getValue() == FlightMode.OLD_NCP && this.oldNcpTimerTicks.getValue() > 0).setDisplayName("TimerBoost");

    /**
     * Blink Flag
     */
    private final CopyOnWriteArrayList<Packet<? extends INetHandler>> packetQueue = new CopyOnWriteArrayList<>();
    private boolean damaged, can, jumped;
    private double moveSpeed, lastDist;
    private int ticks, slotId, i = 0;
    private long delay, lastTime;
    private int level, wait;
    private Vec3 lastPos;
    private float[] lastRotations;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.getState() == EventState.PRE) {
            lastDist = MovementUtil.getDist(getPlayer().posX, getPlayer().posZ, getPlayer().lastTickPosX, getPlayer().lastTickPosZ);
        }

        switch (this.flightMode.getValue()) {
            case SPARTAN:
                if (event.getState() == EventState.PRE) {

                    if (getPlayer().onGround)
                        getPlayer().jump();

                    if (getPlayer().fallDistance > 1)
                        getPlayer().motionY = -((getPlayer().posY) - Math.floor(getPlayer().posY));

                    if (getPlayer().motionY == 0) {
                        getPlayer().jump();

                        getPlayer().onGround = true;
                        event.setOnGround(true);
                        getPlayer().fallDistance = 0;
                    }

//                    if(getPlayer().ticksExisted % 2 == 0) {
//                        event.setOnGround(getPlayer().onGround = true);
//                        MC.thePlayer.jump();
//                    }
//                    getPlayer().motionY = 0.0;
//                    getPlayer().motionY = -((getPlayer().posY) - MathUtil.round(getPlayer().posY, 1 / 64F));
                }
                break;
            case MATRIX:
                switch (matrixMode.getValue()) {
                    case GLIDE:
                        if (!getPlayer().onGround) {
                            setTimer(getPlayer().ticksExisted % 4 == 0 ? 1 : 0.06F);
                            if (System.currentTimeMillis() - lastTime > delay) {
                                getPlayer().motionY = -0.005;
                                delay += Math.random() * 10;
                                delay = Math.max(delay, 1000);
                                lastTime = System.currentTimeMillis();
                            }
                        } else if (event.getState() == EventState.PRE && getPlayer().onGround)
                            getPlayer().jump();
                        break;
                    case BLINK:
                        getPlayer().motionY = 0.0;
                        setTimer(2f);
                        break;
                }
                break;
            case VERUS:
                switch (verusMode.getValue()) {
                    case BOW:
                        if (getPlayer().hurtTime == 9) {
                            damaged = true;
                        }

                        if (!damaged) {
                            MovementUtil.stopWalk();
                            switch (getPlayer().ticksExisted - ticks) {
                                case 3:
                                    event.setPitch(-89.5f);
                                    break;
                                case 4:
                                    sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                                    break;
                                case 5:
                                    if (i != slotId) {
                                        sendPacket(new C09PacketHeldItemChange(slotId));
                                    }
                                    break;
                            }
                        } else {
                            MovementUtil.resumeWalk();
                        }
                        break;
                }
                break;
            case DAMAGE:
                if (getPlayer().hurtTime > 0 && !this.damaged) {
                    level = 0;
                    damaged = true;
                    setTimer(0.5f);
                    getPlayer().motionY = -0.25;
                }
                if (!MovementUtil.isMoving() && !this.damaged) {
                    MovementUtil.setMotion(0.0);
                    getPlayer().ticksExisted = 0;
                }
                if (this.damaged) {
                    if (level == 0) {
                        getPlayer().jump();
                    } else {
                        getPlayer().motionY = 0.0;
                        if (getPlayer().hurtTime > 0) {
                            getPlayer().motionY -= 0.01544567680;
                            MovementUtil.setMotion(1);
                        } else {
                            MovementUtil.setMotion(2);
                        }
                    }
                    event.setOnGround(true);
                    ++this.ticks;
                }
                ++level;
                break;
            case VANILLA:
                getPlayer().capabilities.setFlySpeed(this.vanSpeed.getValue().floatValue());
                getPlayer().capabilities.isFlying = true;
                break;
            case DEV:
//                if (event.getState() == EventState.POST) {
//                    double[] yawPos = MovementUtil.yawPos(MovementUtil.getDirection(), 25);
//                    double yOff = 0;
//                    if (getGameSettings().keyBindJump.isKeyDown()) {
//                        yOff += 4;
//                    } else if (getGameSettings().keyBindSneak.isKeyDown()) {
//                        yOff -= 4;
//                    }
//
//                    for (int i = 0; i < 3; i++) {
//                        sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY, getPlayer().posZ, true));
//                    }
//                    for (int i = 0; i < 7; i++) {
//                        sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX + yawPos[0], getPlayer().posY + yOff, getPlayer().posZ + yawPos[1], true));
//                    }
//                }
                if (event.getState() == EventState.PRE) {
                    for (int i = 0; i < 4; i++) {
                        sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(lastPos.xCoord, lastPos.yCoord, lastPos.zCoord, lastRotations[0], lastRotations[1], false));
                    }
                    if (i > 7) event.setPosY(event.getPosY() + 2);
                } else if (event.getState() == EventState.POST) {
                    for (int i = 0; i < 5; i++) {
                        sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(getX(), getY() + (this.i > 7 && i < 2 ? 2 : 0), getZ(), getYaw(), getPitch(), true));
                    }
//                    for (int i = 0; i < 2; i++) {
//                        sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(getX(), getY(), getZ(), getYaw(), getPitch(), false));
//                    }
                }
                break;
            case BLINK_FLAG:
            case MOTION:

                double y = 0;
//                if (getPlayer().ticksExisted % 8 == 0) {
//                    y = 0.7;
//                } else {
//                    y = -0.1;
//                }
                getPlayer().motionY = getGameSettings().keyBindJump.isKeyDown() ?
                        this.motionSpeed.getValue() : getGameSettings().keyBindSneak.isKeyDown() ? -this.motionSpeed.getValue() : y;

                /*if(i < 7) {
                    event.setPosY(event.getPosY() - 1);
                }*/ /*else {
                    if(!MovementUtil.isMoving()) {
                        event.setPosX(event.getPosX() + 0.1 * Math.random());
                        event.setPosZ(event.getPosZ() + 0.1 * Math.random());
                    }
                }*/

//                if(getPlayer().ticksExisted % 40 == 0) {
//                }
//                event.setPosY(event.getPosY() - getPlayer().ticksExisted % 40 == 0 ? 0.1 : -0.01);

                break;
        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = packetEvent -> {
        final Packet<? extends INetHandler> packet = packetEvent.getPacket();

        switch (this.flightMode.getValue()) {
            case MOTION: {
                if (packet instanceof S08PacketPlayerPosLook && MC.getNetHandler().doneLoadingTerrain) {
                    S08PacketPlayerPosLook packet1 = (S08PacketPlayerPosLook) packet;
                    Vec3 vec3 = new Vec3(packet1.getX(), packet1.getY(), packet1.getZ());

                    if (vec3.distanceTo(lastPos) < 0.1) i++;
//                    else i = 0;

//                    packetEvent.setCancelled(true);

//                    doLog("i: " + i);

                    lastPos = vec3;
                }
                break;
            }
            case DEV:
                if (packet instanceof S08PacketPlayerPosLook && MC.getNetHandler().doneLoadingTerrain) {
                    packetEvent.setCancelled(true);
                    S08PacketPlayerPosLook packet1 = (S08PacketPlayerPosLook) packet;
//                    getPlayer().setPosition(packet1.getX(), packet1.getY(), packet1.getZ());
//                    sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(packet1.getX(), packet1.getY(), packet1.getZ(), packet1.getYaw(), packet1.getPitch(), true));

                    Vec3 vec3 = new Vec3(packet1.getX(), packet1.getY(), packet1.getZ());

                    if (vec3.distanceTo(lastPos) < 0.1) i++;
                    else i = 0;

                    lastPos = vec3;
                    lastRotations = new float[]{packet1.getYaw(), packet1.getPitch()};

                    double distance = getPlayer().getDistance(packet1.getX(), packet1.getY(), packet1.getZ());

                    if (distance > 40 && i > 10) {
//                        getPlayer().setPosition(packet1.getX(), packet1.getY(), packet1.getZ());
                    }

                    doLog("Distance: " + distance);

//                    if(distance == 0) toggle();
                }
                break;
            case DAMAGE:
                if (!damaged)
                    return;
                if (packet instanceof C0FPacketConfirmTransaction) {
                    C0FPacketConfirmTransaction c0f = packetEvent.getPacket();
                    if ((double) c0f.getWindowId() < 5.0) {
                        packetEvent.setCancelled(true);
                        sendPacket(new C0FPacketConfirmTransaction(ThreadLocalRandom.current().nextInt(100, 400), (short) 500, true));
                    }
                }

                if (packet instanceof C03PacketPlayer) {
                    C03PacketPlayer c03 = packetEvent.getPacket();
                    c03.onGround = true;

                    if (getPlayer().ticksExisted <= 15)
                        return;
                    packetEvent.setCancelled(getPlayer().ticksExisted % 3 == 0);
                }
                break;
            case MATRIX:
                switch (matrixMode.getValue()) {
                    case BLINK:
                        if (packet instanceof C03PacketPlayer) {
                            this.packetQueue.add(packet);
                            packetEvent.setCancelled(true);
                        }
                        break;
                }
                break;
            case BLINK_FLAG:
                if (packet instanceof C03PacketPlayer || packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
                    this.packetQueue.add(packet);
                    packetEvent.setCancelled(true);
                }
                break;
        }
    };

    @Handler
    public final Listener<MoveEvent> moveEventListener = moveEvent -> {

        switch (this.flightMode.getValue()) {
            case VERUS:
                switch (verusMode.getValue()) {
                    case BOW:
                        if (damaged) {
                            if (getPlayer().onGround) {
                                getPlayer().jump();
                                moveEvent.setY(getPlayer().motionY = 0.42F);
                            } else {
                                if (getPlayer().hurtTime > 2) {
                                    moveEvent.setY(getPlayer().motionY = 0.0);
                                }
                                if (getPlayer().hurtTime > 0) {
                                    MovementUtil.setSpeed(moveEvent, motionSpeed.getValue() * 2);
                                } else {
                                    toggle();
                                }
                            }
                            ++ticks;
                        }
                        break;
                }
                break;
            case BLINK_FLAG:
            case MOTION:
                if (MovementUtil.isMoving()/* && i > 7*/)
                    MovementUtil.setSpeed(moveEvent, this.motionSpeed.getValue());
                else {
                    moveEvent.setX(getPlayer().motionX = 0);
                    moveEvent.setZ(getPlayer().motionZ = 0);
//                    if(i < 8)
//                        moveEvent.setY(getPlayer().motionY = 0);
//                    doLog(ticks);
//                    ticks++;
                }
                break;
            case HYPIXEL:
                moveEvent.setY(getPlayer().motionY = 0);
                moveEvent.setX(getPlayer().motionX = 0);
                moveEvent.setZ(getPlayer().motionZ = 0);

                if (getPlayer().ticksExisted % 15 == 0) {
                    MC.timer.timerSpeed = 0.75f;
                    double yaw = Math.toRadians(getPlayer().rotationYaw);
                    double horizontal = 7.5 + (Math.random() * 0.4);
                    getPlayer().setPosition(getPlayer().posX + -Math.sin(yaw) * horizontal, getPlayer().posY - 1.8, getPlayer().posZ + Math.cos(yaw) * horizontal);
                } else {
                    MC.timer.timerSpeed = 0.5f;
                }
                break;
            case OLD_NCP:
                double baseMoveSpeed = MovementUtil.getBaseMoveSpeed();
                if (damaged) {
                    if (level == 0) {
                        moveSpeed = baseMoveSpeed;
                        if (ticks == 2) level++;
                        moveEvent.setY(getPlayer().motionY = 0);
                    } else if (level == 1) {
                        if (!jumped) {
                            MovementUtil.fakeJump();
                            moveEvent.setY(getPlayer().motionY = 0.42F);
                            moveSpeed = baseMoveSpeed * oldNcpSpeed.getValue() * 2;
                            ticks = 0;
                            jumped = true;
                        } else {
                            if (oldNcpTimerTicks.getValue() != 0) {
                                if (ticks < oldNcpTimerTicks.getValue()) {
                                    setTimer(oldNcpTimerBoost.getValue().floatValue());
                                }
                                if (ticks == oldNcpTimerTicks.getValue()) {
                                    setTimer(1);
                                }
                            }
                            if (ticks < 4) {
                                moveSpeed = moveSpeed - moveSpeed / MovementUtil.BUNNY_DIV_FRICTION;
                            } else {
                                moveSpeed = lastDist - lastDist / 30;
                            }
                            moveEvent.setY(getPlayer().motionY = -1E-5);
                        }
                    }
                    MovementUtil.setSpeed(moveEvent, Math.max(moveSpeed, baseMoveSpeed));
                    ticks++;
                } else {
                    if (MovementUtil.isOnGround()) {
                        if (oldNcpDamage.getValue() && !can) {
                            double minFall = PlayerUtil.getMinFallDistForDamage();
                            float offset = 0.0625F;
                            for (int i = 0; i < minFall / offset + 1; i++) {
                                sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY + offset, getPlayer().posZ, false));
                                sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY, getPlayer().posZ, false));
                            }
                            sendPacket(new C03PacketPlayer(true));
//                            PlayerUtil.damagePlayer();
                            doLog("Damaged");
                            can = true;
                        }
                        /*if (getPlayer().hurtTime == 9 || !oldNcpDamage.getValue()) */
                        damaged = true;
                        MovementUtil.setSpeed(moveEvent, 0);
                    }
                }
                break;
            case DEV:
                moveEvent.setY(getPlayer().motionY = 0);
                if (getGameSettings().keyBindJump.isKeyDown()) {
                    moveEvent.setY(getPlayer().motionY += 0.5);
                }
                if (getGameSettings().keyBindSneak.isKeyDown()) {
                    moveEvent.setY(getPlayer().motionY -= 0.5);
                }
                if (MovementUtil.isMoving()) MovementUtil.setSpeed(moveEvent, 2);
                else {
                    moveEvent.setX(getPlayer().motionX = 0);
                    moveEvent.setZ(getPlayer().motionZ = 0);
                }
                break;
        }
    };

    @Handler
    public final Listener<CollisonEvent> collisonEventListener = event -> {
        switch (flightMode.getValue()) {
            case DAMAGE:
                if (event.getBlock() instanceof net.minecraft.block.BlockAir && event.getY() < getPlayer().posY)
                    event.setAxisAlignedBB(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1.0D, getPlayer().posY, event.getZ() + 1.0D));
                break;
        }
    };

    @Override
    public void onEnable() {

        if (getPlayer() == null) {
            setEnabled(false);
            return;
        }

        super.onEnable();

        SpeedFeature speedFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(SpeedFeature.class);

        if (speedFeature != null && speedFeature.isEnabled()) {
            speedFeature.toggle();
        }

        wait = 0;
        level = 0;
        damaged = false;
        can = false;
        jumped = false;
        ticks = 0;
        moveSpeed = 0;
        lastDist = 0;
        lastTime = System.currentTimeMillis();
        i = 0;

        lastPos = new Vec3(getPlayer().posX, getPlayer().posY, getPlayer().posZ);
        lastRotations = new float[]{getPlayer().rotationYaw, getPlayer().rotationPitch};

        switch (this.flightMode.getValue()) {
            case DAMAGE:
                sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY + 3.001, getPlayer().posZ, false));
                sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY, getPlayer().posZ, false));
                sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY, getPlayer().posZ, true));
                break;
            case BLINK_FLAG:
                sendPacketUnlogged(new C03PacketPlayer.C06PacketPlayerPosLook(getX(), getY(), getZ(), getYaw(), getPitch(), false));
                sendPacketUnlogged(new C03PacketPlayer.C04PacketPlayerPosition(getX(), getY() - 20.0D, getZ(), getPlayer().onGround));
                sendPacketUnlogged(new C03PacketPlayer.C06PacketPlayerPosLook(getX(), getY(), getZ(), getYaw(), getPitch(), false));
                break;
            case VERUS:
                switch (verusMode.getValue()) {
                    case BOW:
                        if (!getPlayer().inventory.hasItem(Items.arrow)) {
                            Logger.doLog("You need Arrows");
                            toggle();
                            return;
                        }

                        ItemStack itemStack = null;

                        for (i = 0; i < 9; i++) {
                            itemStack = getPlayer().inventory.mainInventory[i];
                            if (itemStack != null && itemStack.getItem() instanceof ItemBow)
                                break;
                        }

                        if (i == 9) {
                            Logger.doLog("You need a bow");
                            toggle();
                            return;
                        } else {
                            slotId = getPlayer().inventory.currentItem;
                            if (i != slotId) {
                                sendPacket(new C09PacketHeldItemChange(i));
                            }
                            ticks = getPlayer().ticksExisted;
                            sendPacket(new C08PacketPlayerBlockPlacement(itemStack));
                        }
                        break;

                }
                break;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        setTimer(1.0F);
        wait = 0;
        level = 0;
        damaged = false;
        can = false;
        MovementUtil.resumeWalk();
        switch (this.flightMode.getValue()) {
            case MOTION:
            case OLD_NCP:
                getPlayer().motionX = 0.0D;
                getPlayer().motionY = 0.0D;
                getPlayer().motionZ = 0.0D;
                break;
            case BLINK_FLAG:
                getPlayer().motionX = 0.0D;
                getPlayer().motionY = 0.0D;
                getPlayer().motionZ = 0.0D;

                sendPacketUnlogged(new C03PacketPlayer.C06PacketPlayerPosLook(getX(), getY(), getZ(), getYaw(), getPitch(), false));
                sendPacketUnlogged(new C03PacketPlayer.C04PacketPlayerPosition(getX(), getY() - 20.0D, getZ(), getPlayer().onGround));
                sendPacketUnlogged(new C03PacketPlayer.C06PacketPlayerPosLook(getX(), getY(), getZ(), getYaw(), getPitch(), false));
                break;
        }

        if (this.flightMode.is(FlightMode.BLINK_FLAG) || this.flightMode.is(FlightMode.MATRIX) && this.matrixMode.is(MatrixMode.BLINK) && !this.packetQueue.isEmpty()) {
            this.packetQueue.forEach(this::sendPacketUnlogged);
            this.packetQueue.clear();
        }

        getPlayer().capabilities.isFlying = false;
        getPlayer().capabilities.setFlySpeed(0.05F);
    }

    @Override
    public String getSuffix() {
        return flightMode.getValue().toString();
    }

    public enum FlightMode {
        VANILLA("Vanilla"),
        MOTION("Motion"),
        OLD_NCP("Old NCP"),
        MATRIX("Matrix"),
        BLINK_FLAG("Blink Flag"),
        VERUS("Verus"),
        SPARTAN("Spartan"),
        HYPIXEL("Hypixel"),
        DAMAGE("Damage"),
        DEV("Dev");

        private final String name;

        FlightMode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum VerusMode {
        BOW("Bow");

        private final String name;

        VerusMode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum MatrixMode {
        GLIDE("Glide"),
        BLINK("Blink");

        private final String name;

        MatrixMode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
