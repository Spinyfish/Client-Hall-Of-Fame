/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventBlockCollide;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPostMotion;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.Aura;

public class Flight
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.MOTION).describedBy("The mode of the flight.");
    private final Setting<VerusMode> verusMode = new Setting<VerusMode>("Verus Mode", VerusMode.AIRWALK).describedBy("The mode of the Verus flight.").visibleWhen(() -> this.mode.getValue() == Mode.VERUS);
    private final Setting<Boolean> fastVerus = new Setting<Boolean>("Fast", false).describedBy("Whether to go faster on Verus.").visibleWhen(() -> this.verusMode.getValue() == VerusMode.BRUE);
    private final Setting<Double> speed = new Setting<Double>("Speed", this.player.getBaseMoveSpeed()).minimum(0.05).maximum(2.0).incrementation(0.05).describedBy("The speed of the flight.").visibleWhen(() -> this.mode.getValue() == Mode.MOTION || this.mode.getValue() == Mode.NORULES || this.mode.getValue() == Mode.CUBECRAFT || this.mode.getValue() == Mode.TUBNET);
    private final Setting<Float> timerSpeed = new Setting<Float>("Timer Speed", Float.valueOf(1.0f)).minimum(Float.valueOf(1.0f)).maximum(Float.valueOf(3.0f)).incrementation(Float.valueOf(0.05f)).describedBy("The timer speed during flight.").visibleWhen(() -> this.mode.getValue() == Mode.NORULES);
    private final Setting<String> ncpLatestSettings = new Setting<String>("NCP Latest Settings", "NCP Latest Settings").describedBy("Settings for mode NCP latest.").visibleWhen(() -> this.mode.getValue() == Mode.N_C_P_LATEST);
    private final Setting<Float> motionYSetting = new Setting<Float>("Motion Y", Float.valueOf(0.275f)).minimum(Float.valueOf(-0.0525f)).maximum(Float.valueOf(0.3f)).incrementation(Float.valueOf(5.0E-4f)).describedBy("The Motion Y during flight.").childOf(this.ncpLatestSettings);
    private final Setting<Double> ncpLatestSpeed = new Setting<Double>("Speed", 9.75).minimum(1.0).maximum(10.0).incrementation(0.05).describedBy("The speed of the flight.").childOf(this.ncpLatestSettings);
    private final Setting<Float> ncpLatestTimerSpeed = new Setting<Float>("Timer Speed", Float.valueOf(0.8f)).minimum(Float.valueOf(0.1f)).maximum(Float.valueOf(1.5f)).incrementation(Float.valueOf(0.1f)).describedBy("The timer speed during flight.").childOf(this.ncpLatestSettings);
    private int watchdogStage;
    private double funcraftSpeed;
    private float timerF;
    private float y = 0.0f;
    private final Timer timer = new Timer();
    private int ticks;
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;
    private float lastYaw;
    private float lastPitch;
    private final Queue<Packet<?>> packetList = new ConcurrentLinkedQueue();
    public final Listener<EventPacket> eventPacket = e -> {
        switch (this.mode.getValue()) {
            case ZONECRAFT: {
                if (e.direction != EventPacket.Direction.SEND) break;
                e.setCancelled(true);
                this.packetList.add(e.getPacket());
                break;
            }
            case TUBNET: {
                if (!(e.getPacket() instanceof S08PacketPlayerPosLook) || this.mc.thePlayer.ticksExisted <= 100) break;
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)e.getPacket();
                PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(packet.getX(), packet.getY(), packet.getZ(), true));
                e.setCancelled(true);
                break;
            }
            case WATCHDOG: {
                if (!(e.getPacket() instanceof S08PacketPlayerPosLook)) break;
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)e.getPacket();
                double px = packet.getX();
                double py = packet.getY();
                double pz = packet.getZ();
                float yaw = packet.getYaw();
                float pitch = packet.getPitch();
                this.packetList.add(new C03PacketPlayer.C06PacketPlayerPosLook(px, py, pz, packet.getPitch(), packet.getPitch(), false));
                this.lastPosX = px;
                this.lastPosY = py;
                this.lastPosZ = pz;
                this.lastYaw = yaw;
                this.lastPitch = pitch;
                e.setCancelled(this.mc.thePlayer.getDistance(this.lastPosX, this.lastPosY, this.lastPosZ) <= 10.0);
            }
        }
    };
    @EventLink
    public final Listener<EventPreMotion> eventPreMotion = e -> {
        block0 : switch (this.mode.getValue()) {
            case MOTION: {
                this.player.setSpeed(this.speed.getValue());
                this.mc.thePlayer.motionY = this.mc.gameSettings.keyBindJump.isKeyDown() ? this.speed.getValue() : (this.mc.gameSettings.keyBindSneak.isKeyDown() ? -this.speed.getValue().doubleValue() : 0.0);
                break;
            }
            case FUNCRAFT: {
                if (this.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    this.mc.thePlayer.motionY = -0.2f;
                }
                if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
                    this.mc.thePlayer.motionY = 0.2f;
                }
                if (this.timer.hasTimeElapsed(350L, true)) {
                    this.funcraftSpeed -= (double)0.0046f;
                    if (this.funcraftSpeed <= 0.15) {
                        this.funcraftSpeed = 0.15f;
                    }
                }
                this.mc.getTimer().timerSpeed = 1.0f;
                if (this.mc.thePlayer.isCollidedVertically) {
                    this.player.jump();
                } else {
                    if (!this.mc.gameSettings.keyBindJump.pressed || !this.mc.gameSettings.keyBindSneak.pressed) {
                        this.mc.thePlayer.motionY = 1.0E-4;
                    }
                    this.mc.thePlayer.jumpMovementFactor = 0.0f;
                    this.player.setSpeed(Math.max(this.player.getBaseMoveSpeed(), (this.funcraftSpeed -= this.funcraftSpeed / 159.0) + 0.0));
                }
                double y1 = this.mc.thePlayer.posY - (this.player.isOnGround() ? 0.0 : 1.0E-10);
                this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, y1, this.mc.thePlayer.posZ);
                break;
            }
            case VULCAN: {
                System.out.println("sex");
                this.player.setSpeed(this.player.getBaseMoveSpeed() * 4.0);
                this.mc.thePlayer.motionY = this.mc.gameSettings.keyBindJump.isKeyDown() ? this.player.getBaseMoveSpeed() * 4.0 : (this.mc.gameSettings.keyBindSneak.isKeyDown() ? -(this.player.getBaseMoveSpeed() * 4.0) : 0.0);
                break;
            }
            case VERUS: {
                switch (this.verusMode.getValue()) {
                    case AIRWALK: {
                        this.player.setOnGround(true);
                        e.setOnGround(this.mc.thePlayer.ticksExisted % 2 == 0);
                        this.mc.thePlayer.motionY = 0.0;
                        e.setY(Math.round(this.mc.thePlayer.posY));
                        this.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(this.mc.thePlayer.prevPosX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.prevPosZ), 1, new ItemStack(Blocks.stone), 1.0f, 1.0f, 1.0f));
                        break block0;
                    }
                }
                break;
            }
            case NORULES: {
                this.player.setOnGround(true);
                e.setOnGround(this.mc.thePlayer.ticksExisted % 2 == 0);
                this.mc.thePlayer.motionY = 0.0;
                e.setY(Math.round(this.mc.thePlayer.posY));
                if (this.mc.thePlayer.ticksExisted % 2 == 0) {
                    this.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(this.mc.thePlayer.prevPosX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.prevPosZ), 1, new ItemStack(Blocks.stone), 1.0f, 1.0f, 1.0f));
                }
                if (this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    this.player.setSpeed(this.speed.getValue());
                }
                this.mc.getTimer().timerSpeed = this.timerSpeed.getValue().floatValue();
                this.mc.thePlayer.cameraYaw = 0.07f;
                break;
            }
            case ZONECRAFT: {
                if (this.mc.thePlayer.posY < (double)this.y) {
                    e.setY(this.y);
                    this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.y, this.mc.thePlayer.posZ);
                    this.player.jump();
                    this.player.setOnGround(true);
                    e.setOnGround(true);
                    break;
                }
                if (!this.player.isOnGround()) break;
                this.y = (float)this.mc.thePlayer.posY;
                this.player.jump();
                break;
            }
            case TUBNET: {
                double tubnetSpeed = this.speed.getValue() * 2.25;
                this.player.setSpeed(tubnetSpeed);
                this.mc.thePlayer.motionY = this.mc.gameSettings.keyBindJump.isKeyDown() ? tubnetSpeed : (this.mc.gameSettings.keyBindSneak.isKeyDown() ? -tubnetSpeed : 0.0);
                this.mc.getTimer().timerSpeed = 0.75f;
                break;
            }
            case PACKET: {
                double yaw = Math.toRadians(this.mc.thePlayer.rotationYaw);
                double x = -Math.sin(yaw) * (double)0.27f;
                double z = Math.cos(yaw) * (double)0.27f;
                this.mc.thePlayer.motionX = 0.0;
                this.mc.thePlayer.motionZ = 0.0;
                this.mc.thePlayer.motionY = 0.0;
                this.mc.getTimer().timerSpeed = 1.1f;
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.motionY, this.mc.thePlayer.motionZ + z, false));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.motionY - 490.0, this.mc.thePlayer.motionZ + z, true));
                this.mc.thePlayer.posX += x;
                this.mc.thePlayer.posZ += z;
                break;
            }
            case WATCHDOG: {
                if (!(this.mc.thePlayer.getDistance(this.lastPosX, this.lastPosY, this.lastPosZ) >= 10.0)) break;
                while (!this.packetList.isEmpty()) {
                    PacketUtil.sendPacketNoEvent(this.packetList.poll());
                }
                break;
            }
            case N_C_P_LATEST: {
                if (!this.player.isOnGround()) {
                    this.mc.getTimer().timerSpeed = this.ncpLatestTimerSpeed.getValue().floatValue();
                    if (this.ticks == 0) {
                        this.mc.thePlayer.motionY = this.motionYSetting.getValue().floatValue();
                        this.player.setSpeed(this.ncpLatestSpeed.getValue());
                    }
                    ++this.ticks;
                    break;
                }
                this.player.jump();
                this.mc.getTimer().timerSpeed = 0.2f;
                break;
            }
            case CUBECRAFT: {
                e.setOnGround(true);
                this.mc.thePlayer.cameraYaw = 0.071f;
            }
        }
    };
    @EventLink
    public final Listener<EventPostMotion> eventPostMotionListener = e -> {
        switch (this.mode.getValue()) {
            case WATCHDOG: {
                if (this.mc.thePlayer.onGround) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, false));
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 11.0, this.mc.thePlayer.posZ, false));
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, false));
                    this.mc.thePlayer.motionY = 0.42f;
                    this.mc.thePlayer.fallDistance = 1.0f;
                } else if (this.mc.thePlayer.fallDistance > 1.0f) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, false));
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 11.0, this.mc.thePlayer.posZ, false));
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, false));
                    this.mc.thePlayer.motionY = -1.32438575E-6;
                    this.mc.getTimer().timerSpeed = 0.6f;
                    for (int j = 0; j < (this.mc.thePlayer.ticksExisted % 6 >= 4 ? 8 : 1); ++j) {
                        if (this.mc.thePlayer.movementInput.jump) continue;
                        this.mc.thePlayer.moveEntity(this.mc.thePlayer.motionX * 0.95, -3.458946587368E-8, this.mc.thePlayer.motionZ * 0.95);
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, false));
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, false));
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 11.0, this.mc.thePlayer.posZ, false));
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, false));
                    }
                }
                if (!this.mc.thePlayer.movementInput.jump) break;
                this.mc.thePlayer.motionY = 0.42f;
            }
        }
    };
    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        switch (this.mode.getValue()) {
            case VERUS: {
                switch (this.verusMode.getValue()) {
                    case BRUE: {
                        if (this.ticks % 14 == 0 && this.player.isOnGround()) {
                            this.player.setSpeed((EventMove)e, 1.0);
                            e.setY(0.42f);
                            this.mc.thePlayer.motionY = -(this.mc.thePlayer.posY - (this.mc.thePlayer.posY - this.mc.thePlayer.posY % 0.015625));
                        } else if (this.fastVerus.getValue().booleanValue()) {
                            if (this.player.isOnGround()) {
                                this.player.setSpeed((EventMove)e, this.player.getBaseMoveSpeed() * (double)2.65f);
                            } else {
                                this.player.setSpeed((EventMove)e, 0.41);
                            }
                        } else if (this.player.isOnGround()) {
                            if (this.mc.thePlayer.moveStrafing == 0.0f) {
                                float multiplier = (float)(1.0 + (this.mc.thePlayer.motionY < 0.0 ? this.mc.thePlayer.motionY * -6.0 : this.mc.thePlayer.motionY * 6.0));
                                if (this.mc.thePlayer.hurtTime > 0) {
                                    this.player.setSpeed((EventMove)e, this.player.getBaseMoveSpeed() * (double)1.7f);
                                } else {
                                    this.player.setSpeed((EventMove)e, this.player.getBaseMoveSpeed() * (double)multiplier);
                                }
                            } else {
                                this.player.setSpeed((EventMove)e, (double)(this.player.getSpeed() * 0.9f));
                            }
                        } else {
                            this.player.setSpeed((EventMove)e, 0.41);
                        }
                        ++this.ticks;
                        break;
                    }
                }
            }
            case WATCHDOG: {
                this.player.setSpeed((EventMove)e, this.player.getBaseMoveSpeed());
                break;
            }
            case CUBECRAFT: {
                this.player.setSpeed((EventMove)e, this.speed.getValue());
                this.mc.thePlayer.motionY = Wrapper.getModule(Aura.class).isEnabled() && Wrapper.getModule(Aura.class).getTarget() != null ? 0.0 : (this.mc.gameSettings.keyBindJump.isKeyDown() ? this.speed.getValue() : (this.mc.gameSettings.keyBindSneak.isKeyDown() ? -this.speed.getValue().doubleValue() : 0.0));
                e.setY(this.mc.thePlayer.motionY);
            }
        }
    };
    @EventLink
    public final Listener<EventBlockCollide> eventBlockCollideListener = e -> {
        switch (this.mode.getValue()) {
            case VERUS: {
                switch (this.verusMode.getValue()) {
                    case BRUE: {
                        if (!(e.getBlock() instanceof BlockAir) || this.mc.thePlayer.isSneaking()) return;
                        double x = e.getX();
                        double y = e.getY();
                        double z = e.getZ();
                        if (!(y < this.mc.thePlayer.posY)) return;
                        e.setCollisionBoundingBox(AxisAlignedBB.fromBounds(-15.0, -1.0, -15.0, 15.0, 1.0, 15.0).offset(x, y, z));
                        return;
                    }
                }
                return;
            }
        }
    };

    public Flight() {
        super("Flight", "Fly", Category.MOVEMENT);
        this.setMetadata(() -> StringUtil.formatEnum(this.mode.getValue()));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.timer.reset();
        switch (this.mode.getValue()) {
            case FUNCRAFT: {
                this.mc.thePlayer.fallDistance = 4.0f;
                this.funcraftSpeed = this.mc.thePlayer.isCollidedVertically ? 1.5 : (double)0.3f;
                if (this.player.isOnGround()) {
                    this.player.jump();
                }
                this.mc.thePlayer.isCollidedVertically = false;
                this.timerF = 1.5f;
                break;
            }
            case VERUS: {
                if (this.verusMode.getValue() != VerusMode.BRUE) break;
                this.ticks = 0;
                this.player.setSpeed(0.0);
                this.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
                break;
            }
            case WATCHDOG: {
                this.lastPosX = this.mc.thePlayer.posX;
                this.lastPosY = this.mc.thePlayer.posY;
                this.lastPosZ = this.mc.thePlayer.posZ;
                this.lastYaw = this.mc.thePlayer.rotationYaw;
                this.lastPitch = this.mc.thePlayer.rotationPitch;
                this.packetList.clear();
                break;
            }
            case N_C_P_LATEST: {
                break;
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.mc.getTimer().timerSpeed = 1.0f;
        switch (this.mode.getValue()) {
            case ZONECRAFT: {
                this.packetList.forEach(this.mc.thePlayer.sendQueue::addToSendQueue);
                this.packetList.clear();
                break;
            }
            case N_C_P_LATEST: {
                this.ticks = 0;
                this.mc.getTimer().timerSpeed = 1.0f;
                break;
            }
            case FUNCRAFT: 
            case CUBECRAFT: {
                this.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
                this.player.setSpeed(0.0);
                break;
            }
            case WATCHDOG: {
                while (!this.packetList.isEmpty()) {
                    PacketUtil.sendPacketNoEvent(this.packetList.poll());
                }
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(this.lastPosX, this.lastPosY, this.lastPosZ, this.lastYaw, this.lastPitch, false));
                this.mc.getTimer().timerSpeed = 1.0f;
                this.mc.thePlayer.motionX = 0.0;
                this.mc.thePlayer.motionZ = 0.0;
                this.mc.thePlayer.setPosition(this.mc.thePlayer.lastTickPosX, this.mc.thePlayer.lastTickPosY, this.mc.thePlayer.lastTickPosZ);
            }
        }
    }

    private static enum VerusMode {
        BRUE,
        AIRWALK;

    }

    private static enum Mode {
        MOTION,
        FUNCRAFT,
        VULCAN,
        VERUS,
        ZONECRAFT,
        NORULES,
        TUBNET,
        CUBECRAFT,
        PACKET,
        WATCHDOG,
        N_C_P_LATEST;

    }
}

