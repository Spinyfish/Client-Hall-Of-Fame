/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  org.apache.commons.lang3.RandomUtils
 */
package lodomir.dev.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import lodomir.dev.November;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.event.impl.player.EventCollideBlock;
import lodomir.dev.event.impl.player.EventMove;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.event.impl.player.EventStrafe;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.ui.notification.Notification;
import lodomir.dev.ui.notification.NotificationManager;
import lodomir.dev.ui.notification.NotificationType;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.player.BlockUtils;
import lodomir.dev.utils.player.MovementUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;

public class Fly
extends Module {
    private long groundTimer;
    double posYBeforeJump;
    double posYAfterJump;
    private BlockPos currentPos;
    private boolean rotated = false;
    private EnumFacing currentFacing;
    public static float pitch;
    private double startY;
    private boolean wasRidingEntity = false;
    private boolean canFly = false;
    private int flyTicks = 0;
    private boolean usedBow;
    private int stage;
    private double moveSpeed;
    private double lastDistance;
    private boolean jumped;
    private boolean startedOnGround;
    final ArrayList<Packet> packets = new ArrayList();
    public TimeUtils timer = new TimeUtils();
    public static NumberSetting speed;
    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Motion", "Creative", "Dash", "Damage", "BlockDrop", "Glide", "Vulcan", "Vulcan2", "Bow", "Air Walk", "Air Jump", "Redesky", "Funcraft", "Craftplay", "Minemora", "Hycraft", "Verus Damage", "Verus", "Blocksmc", "On Damage", "Test", "Packet");
    private final NumberSetting bob = new NumberSetting("Bobbing", 0.0, 0.1, 0.0, 0.01);
    public BooleanSetting nokick = new BooleanSetting("No Kick", false);
    public BooleanSetting stop = new BooleanSetting("Stop on disable", true);

    public Fly() {
        super("Flight", 0, Category.MOVEMENT);
        this.addSettings(speed, this.mode, this.bob, this.nokick, this.stop);
    }

    public static double roundToOnGround(double posY) {
        return posY - posY % 0.015625;
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
        if (!(this.mode.isMode("Vanilla") || this.mode.isMode("Funcraft") || this.mode.isMode("Motion") || this.mode.isMode("Damage") || this.mode.isMode("Minemora") || this.mode.isMode("Verus Damage") || this.mode.isMode("Bow") || this.mode.isMode("On Damage"))) {
            speed.setVisible(false);
        } else {
            speed.setVisible(true);
        }
    }

    @Subscribe
    public void onUpdate(lodomir.dev.event.impl.game.EventUpdate e) {
        this.setSuffix(this.mode.getMode());
        if (MovementUtils.isMoving()) {
            Fly.mc.thePlayer.cameraYaw = (float)this.bob.getValue();
        }
        if (this.nokick.isEnabled()) {
            this.handleVanillaKickBypass();
        }
        switch (this.mode.getMode()) {
            case "Vanilla": {
                this.vanilla();
                break;
            }
            case "Motion": {
                this.motion();
                break;
            }
            case "Damage": {
                this.damage();
                break;
            }
            case "Dash": {
                this.dash();
                break;
            }
            case "Glide": {
                this.glide();
                break;
            }
            case "Craftplay": {
                this.craftplay();
                break;
            }
            case "Air Walk": {
                this.airwalk();
                break;
            }
            case "Air Jump": {
                this.airjump();
                break;
            }
            case "Funcraft": {
                this.funcraft();
                break;
            }
            case "Redesky": {
                this.redesky();
                break;
            }
            case "Minemora": {
                this.minemora();
                break;
            }
            case "Hycraft": {
                this.hycraft();
                break;
            }
            case "Verus": {
                this.verus();
                break;
            }
            case "Blocksmc": {
                this.blocksmc();
                break;
            }
            case "On Damage": {
                this.ondamage();
                break;
            }
            case "Test": {
                this.mineplay();
                break;
            }
            case "Packet": {
                this.packet();
                break;
            }
            case "Verus Damage": {
                this.verusdmg();
                break;
            }
            case "Bow": {
                this.bow();
            }
        }
    }

    private void dash() {
        Fly.mc.thePlayer.motionY = 0.0;
        Fly.mc.thePlayer.onGround = true;
        double xMove = -Math.sin(MovementUtils.getDirection()) * 7.0;
        double zMove = Math.cos(MovementUtils.getDirection()) * 7.0;
        if (Fly.mc.thePlayer.ticksExisted % 20 == 0) {
            Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX + xMove, Fly.mc.thePlayer.posY - 4.0, Fly.mc.thePlayer.posZ + zMove);
        }
    }

    private void funcraft() {
    }

    private void redesky() {
        if (Fly.mc.thePlayer.isRiding()) {
            this.wasRidingEntity = true;
        } else if (this.wasRidingEntity) {
            this.flyTicks = 0;
            this.canFly = true;
            this.wasRidingEntity = false;
        }
        if (this.canFly) {
            if (Fly.mc.thePlayer.onGround) {
                Fly.mc.thePlayer.motionY = 0.5;
            } else {
                if (this.flyTicks > 6) {
                    Fly.mc.thePlayer.capabilities.isFlying = true;
                }
                if (!MovementUtils.isMoving() && this.flyTicks > 10) {
                    Fly.mc.thePlayer.capabilities.isFlying = false;
                }
            }
            if (this.flyTicks > 35) {
                Fly.mc.timer.timerSpeed = 1.0f;
            }
            if (this.flyTicks > 45 || Fly.mc.thePlayer.onGround && this.flyTicks > 20 || Fly.mc.gameSettings.keyBindSneak.isKeyDown() && this.flyTicks > 15) {
                this.canFly = false;
                Fly.mc.timer.timerSpeed = 1.0f;
            }
            if (Fly.mc.gameSettings.keyBindSneak.isKeyDown() && this.flyTicks > 15) {
                Fly.mc.gameSettings.keyBindSneak.pressed = false;
            }
        }
        if (this.flyTicks < 100) {
            ++this.flyTicks;
        }
    }

    private void bow() {
        if (this.usedBow) {
            Fly.mc.thePlayer.motionY = 0.0;
            if (MovementUtils.isMoving()) {
                MovementUtils.strafe(speed.getValueFloat());
            } else {
                MovementUtils.stop();
            }
        }
    }

    @Override
    @Subscribe
    public void onMove(EventMove e) {
        switch (this.mode.getMode()) {
            case "Vulcan": {
                break;
            }
            case "Funcraft": {
                e.y = this.startedOnGround ? 0.0 : -1.0E-5;
                this.lastDistance = MovementUtils.getLastDistance();
                e.setOnGround(true);
                e.setY(e.getY() + (double)RandomUtils.nextFloat((float)1.0E-9f, (float)1.0E-5f));
                break;
            }
            case "Packet": {
                break;
            }
            case "Verus": {
                if (Fly.mc.thePlayer.ticksExisted % 3 == 0 && Fly.mc.thePlayer.onGround) {
                    MovementUtils.strafe(0.69f);
                    e.setY(0.41999998688698);
                    if (Fly.mc.thePlayer.ticksExisted % 4 == 0) {
                        Fly.mc.thePlayer.motionY = 0.42f;
                        MovementUtils.strafe(0.3f);
                    }
                    if (Fly.mc.thePlayer.ticksExisted % 5 == 0) {
                        e.setY(-0.42f);
                    }
                    Fly.mc.thePlayer.motionY = -(Fly.mc.thePlayer.posY - Fly.roundToOnGround(Fly.mc.thePlayer.posY));
                    break;
                }
                if (Fly.mc.thePlayer.onGround) {
                    MovementUtils.strafe(1.01f);
                    break;
                }
                MovementUtils.strafe(0.41f);
                break;
            }
            case "Hycraft": {
                Fly.mc.thePlayer.motionY = 0.0;
                MovementUtils.strafe((float)this.getFlightSpeed());
            }
        }
    }

    private final void vanilla() {
        double d = Fly.mc.gameSettings.keyBindJump.isKeyDown() ? (double)speed.getValueFloat() : (Fly.mc.thePlayer.motionY = Fly.mc.gameSettings.keyBindSneak.isKeyDown() ? (double)(-speed.getValueFloat()) : Math.random() / 1000.0);
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(speed.getValueFloat());
        } else {
            MovementUtils.stop();
        }
    }

    private final void verusdmg() {
        this.sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY - 1.0, Fly.mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(Fly.mc.theWorld, new BlockPos(-1, -1, -1))), 0.0f, 1.0f, 0.0f));
        Fly.mc.thePlayer.motionY = 0.0;
        if (Fly.mc.gameSettings.keyBindJump.isKeyDown() && !Fly.mc.gameSettings.keyBindSneak.isKeyDown()) {
            if (Fly.mc.thePlayer.ticksExisted % 2 == 0) {
                Fly.mc.thePlayer.motionY = 0.42f;
            }
            MovementUtils.strafe((float)this.getFlightSpeed());
        } else if (MovementUtils.isMoving()) {
            if (Fly.mc.thePlayer.hurtTime > 0) {
                MovementUtils.strafe(speed.getValueFloat());
            } else {
                MovementUtils.strafe((float)this.getFlightSpeed());
            }
        } else {
            MovementUtils.stop();
        }
    }

    private final void airwalk() {
        MovementUtils.setMotion(this.getFlightSpeed());
        Fly.mc.thePlayer.motionY = 0.0;
        MovementUtils.strafe();
    }

    private void craftplay() {
        Fly.mc.gameSettings.keyBindForward.pressed = Fly.mc.thePlayer.ticksExisted % 4 == 0;
        if (MovementUtils.isMoving()) {
            MovementUtils.forward(speed.getValueFloat());
        } else {
            MovementUtils.stop();
        }
        Fly.mc.thePlayer.motionY = Fly.mc.gameSettings.keyBindJump.isKeyDown() ? -(Fly.mc.thePlayer.posY - Fly.roundToOnGround(Fly.mc.thePlayer.posY + 0.5)) : -(Fly.mc.thePlayer.posY - Fly.roundToOnGround(Fly.mc.thePlayer.posY));
        if (Fly.mc.thePlayer.posY % 0.015625 < 0.005) {
            Fly.mc.thePlayer.onGround = true;
        }
    }

    @Override
    @Subscribe
    public void onStrafe(EventStrafe event) {
        if (this.mode.isMode("Dash")) {
            event.setCancelled(true);
        }
        if (this.mode.isMode("Bow") && Fly.mc.thePlayer.onGround && !this.usedBow) {
            event.setCancelled(true);
        } else if (this.mode.isMode("BlockDrop")) {
            event.setCancelled(true);
        }
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        switch (this.mode.getMode()) {
            case "Funcraft": {
                switch (this.stage) {
                    case 0: {
                        for (int i = 0; i < 4; ++i) {
                            Fly.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY, Fly.mc.thePlayer.posZ, true));
                        }
                        double motion = 0.42f;
                        motion += MovementUtils.getJumpMotion((float)motion);
                        event.y = Fly.mc.thePlayer.motionY = motion;
                        this.moveSpeed = MovementUtils.getBaseMoveSpeed() * (double)(Fly.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 2.05f : 1.85f);
                        break;
                    }
                    case 1: {
                        this.moveSpeed *= (double)2.6f;
                        break;
                    }
                    case 2: {
                        this.moveSpeed = this.lastDistance - 0.09 * (this.lastDistance - MovementUtils.getBaseMoveSpeed());
                        break;
                    }
                    default: {
                        this.moveSpeed = this.lastDistance - this.lastDistance / 119.0;
                    }
                }
                if (this.jumped) {
                    this.moveSpeed = 0.0;
                }
                this.moveSpeed = Math.max(this.moveSpeed, speed.getValue());
                MovementUtils.strafe((float)this.moveSpeed);
                ++this.stage;
                break;
            }
            case "Creative": {
                Fly.mc.thePlayer.capabilities.isFlying = true;
                break;
            }
            case "BlockDrop": {
                Fly.mc.thePlayer.motionY = 0.0;
                Fly.mc.thePlayer.onGround = true;
                double xMove = -Math.sin(MovementUtils.getDirection()) * 10.0;
                double zMove = Math.cos(MovementUtils.getDirection()) * 10.0;
                if (Fly.mc.thePlayer.ticksExisted % 20 == 0) {
                    this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Fly.mc.thePlayer.posX / 2.0, Fly.mc.thePlayer.posY / 2.0, Fly.mc.thePlayer.posZ / 2.0, false));
                    this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY, Fly.mc.thePlayer.posZ, true));
                    return;
                }
                this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Fly.mc.thePlayer.posX + xMove, Fly.mc.thePlayer.posY, Fly.mc.thePlayer.posZ + zMove, false));
                break;
            }
            case "Vulcan2": {
                Fly.mc.thePlayer.motionY = 0.0;
                double moveSpeed = 0.18;
                double flyspeed = 0.6;
                if (MovementUtils.isMoving()) {
                    MovementUtils.strafe(0.6f);
                    moveSpeed += 0.6;
                } else {
                    MovementUtils.stop();
                }
                if (Fly.mc.gameSettings.keyBindJump.isKeyDown()) {
                    Fly.mc.thePlayer.motionY = 0.6;
                    moveSpeed += 0.6;
                } else if (Fly.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    Fly.mc.thePlayer.motionY = -0.6;
                    moveSpeed += 0.6;
                } else {
                    Fly.mc.thePlayer.motionY = 0.0 - Math.random() / 100.0;
                }
                if (Fly.mc.thePlayer.onGround && !MovementUtils.isMoving()) {
                    NotificationManager.show(new Notification(NotificationType.SUCCESS, "Disabled Fly", "Landing successful", 4));
                    this.setEnabled(false);
                } else if (!MovementUtils.isMoving() && !Fly.mc.gameSettings.keyBindJump.isKeyDown() && Fly.mc.gameSettings.keyBindSneak.isKeyDown() && Fly.mc.thePlayer.ticksExisted % 2 == 0) {
                    Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, this.calculateGround(), Fly.mc.thePlayer.posZ);
                }
                moveSpeed += Fly.mc.thePlayer.getDistance(Fly.mc.thePlayer.lastTickPosX, Fly.mc.thePlayer.lastTickPosY, Fly.mc.thePlayer.lastTickPosZ);
                break;
            }
            case "Vulcan": {
                break;
            }
            case "Minemora": {
                if (Fly.mc.thePlayer.hurtTime > 0 || Fly.mc.thePlayer.getCurrentEquippedItem() == null || !(Fly.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow) || MovementUtils.isOverVoid() || !MovementUtils.isOnGround(1.0) || MovementUtils.isMoving()) break;
                pitch = -90.0f;
                event.setPitch(pitch);
                break;
            }
            case "Bow": {
                if (Fly.mc.thePlayer.hurtTime > 0 || Fly.mc.thePlayer.getCurrentEquippedItem() == null || !(Fly.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow) || MovementUtils.isOverVoid() || !MovementUtils.isOnGround(1.0)) break;
                pitch = -90.0f;
                event.setPitch(pitch);
                break;
            }
            case "Verus": {
                if (!Fly.mc.gameSettings.keyBindJump.isKeyDown() || Fly.mc.thePlayer.ticksExisted % 2 != 0) break;
                Fly.mc.thePlayer.motionY = 0.42f;
                MovementUtils.strafe(0.3f);
                break;
            }
            case "Blocksmc": {
                if (!Fly.mc.thePlayer.onGround && !MovementUtils.isMoving()) {
                    this.rotated = false;
                    this.currentPos = null;
                    this.currentFacing = null;
                    BlockPos pos = new BlockPos(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY - 1.0, Fly.mc.thePlayer.posZ);
                    if (Fly.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
                        this.setBlockAndFacing(pos);
                        if (this.currentPos != null) {
                            float[] facing = BlockUtils.getDirectionToBlock(this.currentPos.getX(), this.currentPos.getY(), this.currentPos.getZ(), this.currentFacing);
                            float yaw = facing[0];
                            float pitch = Math.min(90.0f, facing[1] + 9.0f);
                            this.rotated = true;
                            event.setYaw(yaw);
                            event.setPitch(pitch);
                            Fly.mc.thePlayer.rotationYawHead = yaw;
                            Fly.mc.thePlayer.renderYawOffset = yaw;
                            Fly.mc.thePlayer.rotationPitchHead = pitch;
                        }
                    }
                    if (this.currentPos != null && !MovementUtils.isMoving()) {
                        if (event.isOnGround()) {
                            Fly.mc.thePlayer.jump();
                        }
                        if (Fly.mc.thePlayer.getCurrentEquippedItem() != null && Fly.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock && Fly.mc.playerController.onPlayerRightClick(Fly.mc.thePlayer, Fly.mc.theWorld, Fly.mc.thePlayer.getCurrentEquippedItem(), this.currentPos, this.currentFacing, new Vec3(this.currentPos.getX(), this.currentPos.getY(), this.currentPos.getZ()))) {
                            this.timer.setLastMS();
                            if (Fly.mc.thePlayer.ticksExisted % 2 == 0) {
                                Fly.mc.thePlayer.swingItem();
                            }
                            this.timer.reset();
                        }
                    }
                }
                super.onPreMotion(event);
            }
        }
    }

    @Override
    @Subscribe
    public void onBlockCollide(EventCollideBlock event) {
        switch (this.mode.getMode()) {
            case "Vulcan": 
            case "Verus": {
                if (!(event.getBlock() instanceof BlockAir) || Fly.mc.thePlayer.isSneaking()) break;
                double x = event.getX();
                double y = event.getY();
                double z = event.getZ();
                if (!(y < Fly.mc.thePlayer.posY)) break;
                event.setCollisionBoundingBox(AxisAlignedBB.fromBounds(-30.0, -1.0, -30.0, 30.0, 1.0, 30.0).offset(x, y, z));
            }
        }
        super.onBlockCollide(event);
    }

    private void hycraft() {
    }

    private final void vulcan() {
    }

    private final void blocksmc() {
        if (!Fly.mc.thePlayer.isJumping) {
            this.posYBeforeJump = Fly.mc.thePlayer.posY;
        }
        if (Fly.mc.thePlayer.onGround && Fly.mc.thePlayer.ticksExisted % 8 == 0) {
            if (this.posYBeforeJump == Fly.mc.thePlayer.posY) {
                Fly.mc.thePlayer.jump();
                this.posYAfterJump = Fly.mc.thePlayer.posY;
            } else if (this.posYAfterJump != this.posYBeforeJump) {
                // empty if block
            }
        }
        if (!MovementUtils.isOnGround(1.0) || MovementUtils.isOverVoid() && !MovementUtils.isMoving()) {
            MovementUtils.setMotion(this.getFlightSpeed());
            Fly.mc.thePlayer.motionY = 0.0;
            MovementUtils.strafe();
        }
    }

    private final void damage() {
        double d = Fly.mc.gameSettings.keyBindJump.isKeyDown() ? (double)speed.getValueFloat() : (Fly.mc.thePlayer.motionY = Fly.mc.gameSettings.keyBindSneak.isKeyDown() ? (double)(-speed.getValueFloat()) : Math.random() / 1000.0);
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(speed.getValueFloat());
        } else {
            MovementUtils.stop();
        }
    }

    private final void glide() {
        if (Fly.mc.thePlayer.fallDistance > 0.0f && Fly.mc.thePlayer.motionY < 0.0) {
            Fly.mc.thePlayer.motionY = -0.25;
            Fly.mc.thePlayer.jumpMovementFactor *= 1.12337f;
        }
    }

    private final void verus() {
    }

    private final void airjump() {
        if (!Fly.mc.gameSettings.keyBindSneak.isKeyDown()) {
            if (Fly.mc.gameSettings.keyBindJump.isKeyDown()) {
                if (Fly.mc.thePlayer.ticksExisted % 2 == 0) {
                    Fly.mc.thePlayer.motionY = 0.42f;
                }
            } else {
                if (Fly.mc.thePlayer.onGround) {
                    Fly.mc.thePlayer.jump();
                }
                if (Fly.mc.thePlayer.fallDistance > 1.0f) {
                    Fly.mc.thePlayer.motionY = -(Fly.mc.thePlayer.posY - Math.floor(Fly.mc.thePlayer.posY));
                }
                if (Fly.mc.thePlayer.motionY == 0.0) {
                    Fly.mc.thePlayer.jump();
                    Fly.mc.thePlayer.onGround = true;
                    Fly.mc.thePlayer.fallDistance = 0.0f;
                }
            }
        }
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        ArrayList packets = new ArrayList();
        switch (this.mode.getMode()) {
            case "Vulcan": {
                if (!(event.getPacket() instanceof S08PacketPlayerPosLook)) break;
                November.Log("You've got teleported");
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)event.getPacket();
                event.setCancelled(true);
                this.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
                Fly.mc.thePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());
                break;
            }
            case "BlockDrop": {
                if (!(event.getPacket() instanceof S08PacketPlayerPosLook)) break;
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)event.getPacket();
                event.setCancelled(true);
                this.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
                Fly.mc.thePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());
                break;
            }
            case "Vulcan2": {
                if (event.getPacket() instanceof S08PacketPlayerPosLook && this.timer.hasReached(1000L)) {
                    S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)event.getPacket();
                    if (Fly.mc.thePlayer.getDistanceSq(packet.getX(), packet.getY(), packet.getY()) <= 9.5) {
                        this.sendPacketSilent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.yaw, packet.pitch, false));
                        event.setCancelled(true);
                    }
                    this.timer.reset();
                }
                if (!(event.getPacket() instanceof C0BPacketEntityAction)) break;
                C0BPacketEntityAction c0B = (C0BPacketEntityAction)event.getPacket();
                if (c0B.getAction().equals((Object)C0BPacketEntityAction.Action.START_SPRINTING)) {
                    if (EntityPlayerSP.serverSprintState) {
                        this.sendPacketSilent(new C0BPacketEntityAction(Fly.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                        EntityPlayerSP.serverSprintState = false;
                    }
                    event.setCancelled(true);
                }
                if (!c0B.getAction().equals((Object)C0BPacketEntityAction.Action.STOP_SPRINTING)) break;
                event.setCancelled(true);
                break;
            }
            case "Airwalk": {
                if (!(event.getPacket() instanceof C03PacketPlayer)) break;
                C03PacketPlayer packet = (C03PacketPlayer)event.getPacket();
                packet.onGround = true;
                break;
            }
            case "Verus Damage": {
                if (!(event.getPacket() instanceof C03PacketPlayer) || !(event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) || !(event.getPacket() instanceof C08PacketPlayerBlockPlacement)) break;
                event.setCancelled(true);
            }
        }
    }

    private final void packet() {
        this.sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY - 1.0, Fly.mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(Fly.mc.theWorld, new BlockPos(-1, -1, -1))), 0.0f, 1.0f, 0.0f));
        if (Fly.mc.gameSettings.keyBindJump.isKeyDown() && !Fly.mc.gameSettings.keyBindSneak.isKeyDown()) {
            if (Fly.mc.thePlayer.ticksExisted % 2 == 0) {
                Fly.mc.thePlayer.motionY = 0.42f;
            }
        } else {
            MovementUtils.setMotion(MovementUtils.getBaseMoveSpeed());
            Fly.mc.thePlayer.motionY = 0.0;
        }
        if (Fly.mc.gameSettings.keyBindSneak.isKeyDown()) {
            // empty if block
        }
    }

    private final void ondamage() {
        if (Fly.mc.thePlayer.hurtTime > 0) {
            MovementUtils.strafe(speed.getValueFloat());
            Fly.mc.thePlayer.motionY = 0.0;
        }
    }

    private final void minemora() {
        if (Fly.mc.thePlayer.motionY <= -0.08) {
            Fly.mc.thePlayer.motionY = 0.0;
        }
        if (MovementUtils.isMoving()) {
            MovementUtils.setMotion(Fly.mc.thePlayer.hurtTime > 0 ? (double)speed.getValueFloat() : 0.2);
        } else {
            MovementUtils.stop();
        }
    }

    @Override
    public void onEnable() {
        this.usedBow = false;
        this.packets.clear();
        this.startY = Fly.mc.thePlayer.posY;
        switch (this.mode.getMode()) {
            case "Funcraft": {
                this.stage = 0;
                this.lastDistance = this.moveSpeed = MovementUtils.getBaseMoveSpeed();
                this.jumped = !Fly.mc.thePlayer.onGround;
                this.startedOnGround = Fly.mc.thePlayer.getAir() == 0;
                break;
            }
            case "Hycraft": {
                if (Fly.mc.thePlayer.onGround) {
                    Fly.mc.thePlayer.setPositionAndUpdate(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY - 0.1, Fly.mc.thePlayer.posZ);
                    break;
                }
                Fly.mc.thePlayer.motionY = 0.0;
                Fly.mc.thePlayer.setPositionAndUpdate(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY, Fly.mc.thePlayer.posZ);
                break;
            }
            case "Packet": {
                break;
            }
            case "Damage": {
                this.sendPacket(new C03PacketPlayer());
                this.sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + 1.5, Fly.mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(Fly.mc.theWorld, new BlockPos(-1, -1, -1))), 0.0f, 0.94f, 0.0f));
                this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + 3.05, Fly.mc.thePlayer.posZ, false));
                this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY, Fly.mc.thePlayer.posZ, false));
                this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + (double)0.42f, Fly.mc.thePlayer.posZ, true));
                break;
            }
            case "Verus Damage": {
                this.sendPacketSilent(new C08PacketPlayerBlockPlacement(new BlockPos(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY - 1.5, Fly.mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(Fly.mc.theWorld, new BlockPos(-1, -1, -1))), 0.0f, 0.94f, 0.0f));
                this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + 3.05, Fly.mc.thePlayer.posZ, false));
                this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY, Fly.mc.thePlayer.posZ, false));
                this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + (double)0.42f, Fly.mc.thePlayer.posZ, true));
                break;
            }
            case "Mineplay": {
                break;
            }
            case "Minemora": {
                if (Fly.mc.thePlayer.ticksExisted >= 9) {
                    Fly.mc.timer.timerSpeed = 1.0f;
                }
                if (Fly.mc.thePlayer.ticksExisted >= 1) break;
                Fly.mc.thePlayer.ticksExisted = 1;
                break;
            }
            case "Bow": {
                if (Fly.mc.thePlayer.ticksExisted >= 9) {
                    Fly.mc.timer.timerSpeed = 1.0f;
                }
                if (Fly.mc.thePlayer.ticksExisted < 1) {
                    Fly.mc.thePlayer.ticksExisted = 1;
                }
                if (Fly.mc.thePlayer.hurtTime <= 0) break;
                this.usedBow = true;
                break;
            }
            case "Vulcan2": {
                Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, Math.round(Fly.mc.thePlayer.posY + 0.5), Fly.mc.thePlayer.posZ);
                NotificationManager.show(new Notification(NotificationType.WARNING, "Vulcan Fly", "To land go on ground and stand still", 1));
            }
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.stop.isEnabled()) {
            MovementUtils.stop();
        }
        this.usedBow = false;
        for (Packet p : this.packets) {
            if (mc.isSingleplayer()) continue;
            this.sendPacketSilent(p);
        }
        this.packets.clear();
        Fly.mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }

    private final void mineplay() {
        MovementUtils.strafe((float)MovementUtils.getBaseMoveSpeed());
        Fly.mc.thePlayer.motionY = 0.0;
        if (Fly.mc.thePlayer.ticksExisted % 19 == 0) {
            Fly.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Fly.mc.thePlayer.serverPosX, Fly.mc.thePlayer.posY + 10.0, Fly.mc.thePlayer.serverPosZ, true));
        }
    }

    private final void motion() {
        if (!Fly.mc.gameSettings.keyBindSneak.isKeyDown()) {
            if (MovementUtils.isMoving()) {
                MovementUtils.strafe(speed.getValueFloat());
            } else {
                MovementUtils.stop();
            }
            Fly.mc.thePlayer.motionY = Fly.mc.gameSettings.keyBindJump.isKeyDown() ? -(Fly.mc.thePlayer.posY - Fly.roundToOnGround(Fly.mc.thePlayer.posY + 0.5)) : -(Fly.mc.thePlayer.posY - Fly.roundToOnGround(Fly.mc.thePlayer.posY));
            if (Fly.mc.thePlayer.posY % 0.015625 < 0.005) {
                Fly.mc.thePlayer.onGround = true;
            }
        }
    }

    private void handleVanillaKickBypass() {
        double posY;
        if (System.currentTimeMillis() - this.groundTimer < 1000L) {
            return;
        }
        double x = Fly.mc.thePlayer.posX;
        double y = Fly.mc.thePlayer.posY;
        double z = Fly.mc.thePlayer.posZ;
        double ground = this.calculateGround();
        for (posY = y; posY > ground; posY -= 8.0) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, posY, z, true));
            if (posY - 8.0 < ground) break;
        }
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, ground, z, true));
        for (posY = ground; posY < y; posY += 8.0) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, posY, z, true));
            if (posY + 8.0 > y) break;
        }
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
        this.groundTimer = System.currentTimeMillis();
    }

    public double calculateGround() {
        double y = Fly.mc.thePlayer.posY;
        AxisAlignedBB playerBoundingBox = Fly.mc.thePlayer.getEntityBoundingBox();
        double blockHeight = 1.0;
        for (double ground = y; ground > 0.0; ground -= blockHeight) {
            AxisAlignedBB customBox = new AxisAlignedBB(playerBoundingBox.maxX, ground + blockHeight, playerBoundingBox.maxZ, playerBoundingBox.minX, ground, playerBoundingBox.minZ);
            if (!Fly.mc.theWorld.checkBlockCollision(customBox)) continue;
            if (blockHeight <= 0.05) {
                return ground + blockHeight;
            }
            ground += blockHeight;
            blockHeight = 0.05;
        }
        return 0.0;
    }

    public double getFlightSpeed() {
        double baseSpeed = 0.262;
        if (Fly.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Fly.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.15 * (double)amplifier;
        }
        return baseSpeed;
    }

    private void setBlockAndFacing(BlockPos var1) {
        if (Fly.mc.theWorld.getBlockState(var1.add(0, -1, 0)).getBlock() != Blocks.air) {
            this.currentPos = var1.add(0, -1, 0);
            this.currentFacing = EnumFacing.UP;
        } else if (Fly.mc.theWorld.getBlockState(var1.add(-1, 0, 0)).getBlock() != Blocks.air) {
            this.currentPos = var1.add(-1, 0, 0);
            this.currentFacing = EnumFacing.EAST;
        } else if (Fly.mc.theWorld.getBlockState(var1.add(1, 0, 0)).getBlock() != Blocks.air) {
            this.currentPos = var1.add(1, 0, 0);
            this.currentFacing = EnumFacing.WEST;
        } else if (Fly.mc.theWorld.getBlockState(var1.add(0, 0, -1)).getBlock() != Blocks.air) {
            this.currentPos = var1.add(0, 0, -1);
            this.currentFacing = EnumFacing.SOUTH;
        } else if (Fly.mc.theWorld.getBlockState(var1.add(0, 0, 1)).getBlock() != Blocks.air) {
            this.currentPos = var1.add(0, 0, 1);
            this.currentFacing = EnumFacing.NORTH;
        } else {
            this.currentPos = null;
            this.currentFacing = null;
        }
    }

    static {
        speed = new NumberSetting("Speed", 0.1, 10.0, 1.0, 0.1);
    }
}

