/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.RandomUtils
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;
import wtf.monsoon.api.event.Event;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPostMotion;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.event.EventUpdate;

public class Scaffold
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.NORMAL).describedBy("The mode of the scaffold.");
    private final Setting<Rotations> rotations = new Setting<Rotations>("Rotations", Rotations.NORMAL).describedBy("The rotations of the scaffold.");
    private final Setting<String> placementSettings = new Setting<String>("Placement Settings", "Placement Settings").describedBy("Configure block placement.");
    private final Setting<PlaceMode> placeMode = new Setting<PlaceMode>("PlaceMode", PlaceMode.PRE).describedBy("The place mode of the scaffold.").childOf(this.placementSettings);
    private final Setting<VecMode> vecMode = new Setting<VecMode>("Vec3 Mode", VecMode.ZERO).describedBy("The mode of the Vec3s.").childOf(this.placementSettings);
    private final Setting<ItemMode> itemMode = new Setting<ItemMode>("ItemMode", ItemMode.SPOOF).describedBy("The item mode of the scaffold.").childOf(this.placementSettings);
    private final Setting<Long> placeDelay = new Setting<Long>("Place Delay", 50L).minimum(50L).maximum(1000L).incrementation(1L).describedBy("The amount of times to attack per second").childOf(this.placementSettings);
    private final Setting<String> movementSettings = new Setting<String>("Movement Settings", "Movement Settings").describedBy("Configure your movement while scaffolding.");
    private final Setting<Boolean> sprint = new Setting<Boolean>("Allow Sprinting", true).describedBy("Whether or not to allow sprinting.").childOf(this.movementSettings);
    private final Setting<Double> timer = new Setting<Double>("TimerBoost", 1.0).minimum(1.0).maximum(2.0).incrementation(0.1).describedBy("The timer boost of the scaffold.").childOf(this.movementSettings);
    private final Setting<Boolean> keepY = new Setting<Boolean>("KeepY", false).describedBy("The blocks will stay at the same Y coordinate. Useful for using speed while scaffolding.").childOf(this.movementSettings);
    private final Setting<Boolean> autoJump = new Setting<Boolean>("AutoJump", false).describedBy("Automatically jumps while Keep Y is enabled.").childOf(this.keepY);
    private final Setting<TowerMode> towerMode = new Setting<TowerMode>("Tower Mode", TowerMode.MOTION).describedBy("The mode of the tower.").childOf(this.movementSettings);
    private final Setting<Boolean> towerMove = new Setting<Boolean>("Tower Move", false).describedBy("Allows you to go up faster and easier.").childOf(this.movementSettings);
    private final Setting<String> bypassSettings = new Setting<String>("Bypass Settings", "Bypass Settings").describedBy("Configure settings relating to bypassing.");
    private final Setting<Boolean> sprintFix = new Setting<Boolean>("Sprint Fix", false).describedBy("Helps with bypassing some anticheats.").childOf(this.bypassSettings);
    private final Setting<Boolean> gcdFix = new Setting<Boolean>("GCD Fix", false).describedBy("Whether to enable a GCD fix.").childOf(this.bypassSettings);
    private BlockInfo info;
    private int lastSlot;
    private int oldSlot;
    private int sprintTicks;
    private int towerTicks;
    private ItemStack stackToPlace;
    private float finalRotationYaw;
    private float finalRotationPitch;
    private float blockYaw;
    private boolean sneaking;
    private boolean isPlacing = false;
    public double yCoordinate;
    private Timer placeTimer = new Timer();
    private int blockCount = 0;
    @EventLink
    public final Listener<EventRender2D> eventRender2DListener = e -> {
        this.blockCount = this.getBlockCount();
        FontRenderer fr = this.mc.fontRendererObj;
        ScaledResolution sr = e.getSr();
        String s = String.valueOf(this.blockCount);
        float percentage = (float)Math.min(this.blockCount, 256) / 256.0f / 3.0f;
        int l1 = sr.getScaledWidth() / 2 - fr.getStringWidth(s) / 2;
        int i1 = sr.getScaledHeight() / 2 - fr.getHeight() - 10;
        fr.drawString(s, (float)(l1 + 1), (float)i1, 0);
        fr.drawString(s, (float)(l1 - 1), (float)i1, 0);
        fr.drawString(s, (float)l1, (float)(i1 + 1), 0);
        fr.drawString(s, (float)l1, (float)(i1 - 1), 0);
        fr.drawString(s, (float)l1, (float)i1, new Color(Color.HSBtoRGB(percentage, 1.0f, 1.0f)));
    };
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = event -> {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) {
            this.toggle();
        }
        this.info = this.keepY.getValue() != false && !this.mc.thePlayer.movementInput.jump ? this.getDiagonalBlockInfo(new BlockPos(this.mc.thePlayer.posX, this.yCoordinate - 1.0, this.mc.thePlayer.posZ)) : this.getDiagonalBlockInfo(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ));
        this.mc.getTimer().timerSpeed = this.mode.getValue() == Mode.WATCHDOG ? (this.mc.thePlayer.onGround && this.mc.thePlayer.ticksExisted % 3 != 0 ? this.timer.getValue().floatValue() : 1.0f) : this.timer.getValue().floatValue();
        if (this.info == null || this.info.pos == null) {
            return;
        }
        ++this.sprintTicks;
    };
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (this.info == null || this.info.pos == null) {
            return;
        }
        if (!this.isReplaceable(this.info)) {
            return;
        }
        this.setRotations((EventPreMotion)e);
        if (this.sneaking) {
            PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        }
        if (this.mc.thePlayer.onGround) {
            this.yCoordinate = this.mc.thePlayer.posY;
            if (this.keepY.getValue().booleanValue() && this.autoJump.getValue().booleanValue() && this.player.isMoving()) {
                this.mc.thePlayer.jump();
            }
        }
        if (this.mode.getValue() != Mode.WATCHDOG) {
            this.mc.thePlayer.setSprinting(this.sprint.getValue() != false && this.player.isMoving());
        }
        this.stackToPlace = this.setStackToPlace();
        if (this.placeMode.getValue() == PlaceMode.PRE && !this.mode.getValue().equals((Object)Mode.WATCHDOG)) {
            this.info = this.keepY.getValue() != false && !this.mc.thePlayer.movementInput.jump ? this.getDiagonalBlockInfo(new BlockPos(this.mc.thePlayer.posX, this.yCoordinate - 1.0, this.mc.thePlayer.posZ)) : this.getDiagonalBlockInfo(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ));
            if (this.info.pos != null) {
                this.placeBlock();
            }
        } else if (this.mode.getValue().equals((Object)Mode.WATCHDOG) && !this.mc.gameSettings.keyBindJump.isKeyDown()) {
            this.info = this.keepY.getValue() != false && !this.mc.thePlayer.movementInput.jump ? this.getDiagonalBlockInfo(new BlockPos(this.mc.thePlayer.posX, this.yCoordinate - 1.0, this.mc.thePlayer.posZ)) : this.getDiagonalBlockInfo(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ));
            if (this.info.pos != null) {
                this.placeBlock();
            }
        }
        this.towerMotion((Event)e);
    };
    @EventLink
    public final Listener<EventPostMotion> eventPostMotionListener = e -> {
        if (this.placeMode.getValue() == PlaceMode.POST && !this.mode.getValue().equals((Object)Mode.WATCHDOG)) {
            this.info = this.keepY.getValue() != false && !this.mc.thePlayer.movementInput.jump ? this.getDiagonalBlockInfo(new BlockPos(this.mc.thePlayer.posX, this.yCoordinate - 1.0, this.mc.thePlayer.posZ)) : this.getDiagonalBlockInfo(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ));
            if (this.info.pos != null) {
                this.placeBlock();
            }
        } else if (this.mode.getValue().equals((Object)Mode.WATCHDOG) && this.mc.gameSettings.keyBindJump.isKeyDown()) {
            this.info = this.keepY.getValue() != false && !this.mc.thePlayer.movementInput.jump ? this.getDiagonalBlockInfo(new BlockPos(this.mc.thePlayer.posX, this.yCoordinate - 1.0, this.mc.thePlayer.posZ)) : this.getDiagonalBlockInfo(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ));
            if (this.info.pos != null) {
                this.placeBlock();
            }
        }
    };
    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        this.towerMotion((Event)e);
        if (this.mode.getValue() == Mode.WATCHDOG && this.player.isMoving()) {
            if (this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                this.mc.thePlayer.setSprinting(true);
                e.setX(this.mc.thePlayer.motionX / 1.7);
                e.setZ(this.mc.thePlayer.motionZ / 1.7);
            } else {
                this.mc.thePlayer.setSprinting(true);
                e.setX(this.mc.thePlayer.motionX / 1.3);
                e.setZ(this.mc.thePlayer.motionZ / 1.3);
            }
        }
    };
    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if (e.getDirection() == EventPacket.Direction.RECEIVE && this.itemMode.getValue() == ItemMode.SPOOF && e.packet instanceof S2FPacketSetSlot) {
            this.lastSlot = ((S2FPacketSetSlot)e.getPacket()).func_149173_d();
            this.stackToPlace = this.setStackToPlace();
        }
        if (e.getDirection() == EventPacket.Direction.SEND) {
            Packet<INetHandlerPlayServer> packet;
            if (this.itemMode.getValue() == ItemMode.SPOOF && e.getPacket() instanceof C09PacketHeldItemChange) {
                e.setCancelled(true);
            }
            if (this.mode.getValue() == Mode.WATCHDOG && e.getPacket() instanceof C0BPacketEntityAction && (((C0BPacketEntityAction)(packet = (C0BPacketEntityAction)e.getPacket())).getAction() == C0BPacketEntityAction.Action.START_SPRINTING || ((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING)) {
                e.setCancelled(true);
            }
            if (this.mode.getValue() == Mode.VERUS && e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                packet = (C08PacketPlayerBlockPlacement)e.getPacket();
                ((C08PacketPlayerBlockPlacement)packet).setFacingX(0.0f);
                ((C08PacketPlayerBlockPlacement)packet).setFacingY(0.0f);
                ((C08PacketPlayerBlockPlacement)packet).setFacingZ(0.0f);
            }
        }
    };

    public Scaffold() {
        super("Scaffold", "Automatically places blocks below you.", Category.PLAYER);
        this.setMetadata(() -> StringUtil.formatEnum(this.mode.getValue()));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.lastSlot = this.mc.thePlayer.inventory.currentItem;
        this.oldSlot = this.mc.thePlayer.inventory.currentItem;
        this.blockYaw = this.mc.thePlayer.rotationYaw - 180.0f;
        this.sprintTicks = 0;
        this.towerTicks = 0;
        this.sneaking = false;
        this.isPlacing = false;
        this.yCoordinate = this.mc.thePlayer.posY;
        this.placeTimer.reset();
        this.blockCount = this.getBlockCount();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lastSlot = this.mc.thePlayer.inventory.currentItem;
        this.mc.gameSettings.keyBindSneak.pressed = false;
        this.mc.getTimer().timerSpeed = 1.0f;
        if (this.itemMode.getValue() == ItemMode.SPOOF) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
        } else {
            this.mc.thePlayer.inventory.currentItem = this.oldSlot;
        }
    }

    private void placeBlock() {
        boolean placed = false;
        if (this.sprintFix.getValue().booleanValue() && this.mc.thePlayer.isSprinting()) {
            PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        }
        if (this.canPlace()) {
            placed = this.sendPlace();
        }
        if (placed) {
            PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
            new Thread(this::stopPlacing).start();
        }
    }

    private boolean sendPlace() {
        boolean placed = this.mc.playerController.onPlayerRightClick(this.mc.thePlayer, this.mc.theWorld, this.getPlacingItem(), this.info.getPos(), this.info.getFacing(), this.getVec3(this.info));
        if (placed) {
            // empty if block
        }
        this.isPlacing = placed;
        return placed;
    }

    private boolean canPlace() {
        boolean correctMotion = this.player.isMoving() || this.mc.thePlayer.motionY != 0.0;
        boolean correctBlockStuff = this.isReplaceable(this.info);
        boolean timerReady = this.placeTimer.hasTimeElapsed(this.placeDelay.getValue(), true);
        return correctMotion && correctBlockStuff;
    }

    private ItemStack getPlacingItem() {
        return this.itemMode.getValue() == ItemMode.SPOOF ? this.stackToPlace : (this.mc.thePlayer.getHeldItem() != null ? this.mc.thePlayer.getHeldItem() : null);
    }

    private Vec3 getVec3(BlockInfo info) {
        switch (this.vecMode.getValue()) {
            case DIR: {
                Vec3 eyesPos = new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.getEntityBoundingBox().minY + (double)this.mc.thePlayer.getEyeHeight(), this.mc.thePlayer.posZ);
                Vec3 rotationVector = this.getVectorForRotation(this.finalRotationYaw, this.finalRotationPitch);
                Vec3 vec = eyesPos.addVector(rotationVector.x * 4.0, rotationVector.y * 4.0, rotationVector.z * 4.0);
                return vec;
            }
            case POS: {
                return new Vec3(info.getPos().getX(), info.getPos().getY(), info.getPos().getZ());
            }
            case STRICT: {
                BlockPos pos = info.getPos();
                EnumFacing face = info.getFacing();
                double x = (double)pos.getX() + 0.5;
                double y = (double)pos.getY() + 0.5;
                double z = (double)pos.getZ() + 0.5;
                if (face != EnumFacing.UP && face != EnumFacing.DOWN) {
                    y += 0.5;
                } else {
                    x += 0.3;
                    z += 0.3;
                }
                if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
                    z += 0.15;
                }
                if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
                    x += 0.15;
                }
                return new Vec3(x, y, z);
            }
            case LEGIT: {
                double x1 = (float)info.getPos().getX() + 0.5f + 0.25f * (float)info.getFacing().getDirectionVec().getX();
                double y1 = (float)info.getPos().getY() + 0.5f + 0.25f * (float)info.getFacing().getDirectionVec().getY();
                double z1 = (float)info.getPos().getZ() + 0.5f + 0.25f * (float)info.getFacing().getDirectionVec().getZ();
                return new Vec3(x1, y1, z1);
            }
        }
        return new Vec3(0.0, 0.0, 0.0);
    }

    private Vec3 getVectorForRotation(float yaw, float pitch) {
        float yawCos = MathHelper.cos(-yaw * ((float)Math.PI / 180) - (float)Math.PI);
        float yawSin = MathHelper.sin(-yaw * ((float)Math.PI / 180) - (float)Math.PI);
        float pitchCos = -MathHelper.cos(-pitch * ((float)Math.PI / 180));
        float pitchSin = MathHelper.sin(-pitch * ((float)Math.PI / 180));
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    private void towerMotion(Event event) {
        switch (this.towerMode.getValue()) {
            case MOTION: {
                if (!(event instanceof EventMove)) break;
                EventMove e = (EventMove)event;
                if (!this.canTower(1.0)) break;
                this.mc.thePlayer.motionY = 0.42f;
                e.setY(0.42f);
                break;
            }
            case NCP: {
                if (!(event instanceof EventMove)) break;
                EventMove e = (EventMove)event;
                if (this.mc.thePlayer.onGround) {
                    this.towerTicks = 0;
                }
                if (!this.canTower(1.1)) break;
                int position = (int)this.mc.thePlayer.posY;
                if (this.mc.thePlayer.posY - (double)position < 0.05) {
                    this.mc.thePlayer.posY = position;
                    this.mc.thePlayer.motionY = 0.42f;
                    e.setY(0.42f);
                    this.towerTicks = 1;
                    break;
                }
                if (this.towerTicks == 1) {
                    this.mc.thePlayer.motionY = 0.34f;
                    e.setY(0.34f);
                    ++this.towerTicks;
                    break;
                }
                if (this.towerTicks != 2) break;
                this.mc.thePlayer.motionY = 0.25;
                e.setY(0.25);
                ++this.towerTicks;
                break;
            }
            case WATCHDOG: {
                if (!(event instanceof EventMove)) break;
                EventMove e = (EventMove)event;
                if (this.mc.thePlayer.onGround) {
                    this.towerTicks = 0;
                }
                if (!this.mc.gameSettings.keyBindJump.isKeyDown() || this.player.isMoving() && !this.towerMove.getValue().booleanValue()) break;
                int position = (int)this.mc.thePlayer.posY;
                if (this.mc.thePlayer.posY - (double)position < 0.05) {
                    this.mc.thePlayer.posY = position;
                    this.mc.thePlayer.motionY = 0.42f;
                    e.setY(0.42f);
                    this.towerTicks = 1;
                    break;
                }
                if (this.towerTicks == 1) {
                    this.mc.thePlayer.motionY = 0.34f;
                    e.setY(0.34f);
                    ++this.towerTicks;
                    break;
                }
                if (this.towerTicks != 2) break;
                this.mc.thePlayer.motionY = 0.25;
                e.setY(0.25);
                ++this.towerTicks;
                break;
            }
            case POSITION: {
                if (!(event instanceof EventPreMotion) || this.mc.thePlayer.ticksExisted % 10 != 0) break;
                for (int i = 0; i < 5; ++i) {
                    this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.2, this.mc.thePlayer.posZ);
                }
                break;
            }
            case SMALLHOP: {
                if (!(event instanceof EventPreMotion) || this.mc.thePlayer.ticksExisted % 10 != 0) break;
                this.mc.thePlayer.motionY = 0.2f;
                break;
            }
            case VERUS: {
                if (!(event instanceof EventPreMotion) || this.mc.thePlayer.ticksExisted % 2 != 0) break;
                this.mc.thePlayer.jump();
                break;
            }
        }
    }

    private boolean canTower(double down) {
        return this.towerMode.getValue() != TowerMode.NONE && this.mc.gameSettings.keyBindJump.isKeyDown() && (!this.player.isMoving() || this.towerMove.getValue() != false) && !(this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - down, this.mc.thePlayer.posZ)).getBlock() instanceof BlockAir);
    }

    private void setRotations(EventPreMotion e) {
        switch (this.rotations.getValue()) {
            case NORMAL: {
                float[] rots = this.getRotations(this.info);
                float yaw = this.processRotation(rots[0]);
                float pitch = this.processRotation(rots[1]);
                this.mc.thePlayer.renderYawOffset = this.finalRotationYaw = yaw;
                this.mc.thePlayer.rotationYawHead = this.finalRotationYaw;
                e.setYaw(this.finalRotationYaw);
                this.mc.thePlayer.rotationPitchHead = this.finalRotationPitch = pitch;
                e.setPitch(this.finalRotationPitch);
                break;
            }
            case AAC: {
                float[] rots = this.getAACRotations();
                float yaw = rots[0];
                float pitch = this.processRotation(rots[1]);
                this.mc.thePlayer.renderYawOffset = this.finalRotationYaw = yaw;
                this.mc.thePlayer.rotationYawHead = this.finalRotationYaw;
                e.setYaw(this.finalRotationYaw);
                this.mc.thePlayer.rotationPitchHead = this.finalRotationPitch = pitch;
                e.setPitch(this.finalRotationPitch);
                break;
            }
            case BRUTE_FORCE: {
                float[] rots = this.getBruteForceRotations(this.info);
                float yaw = this.processRotation(rots[0]);
                float pitch = this.processRotation(rots[1]);
                this.mc.thePlayer.renderYawOffset = this.finalRotationYaw = yaw;
                this.mc.thePlayer.rotationYawHead = this.finalRotationYaw;
                e.setYaw(this.finalRotationYaw);
                this.mc.thePlayer.rotationPitchHead = this.finalRotationPitch = pitch;
                e.setPitch(this.finalRotationPitch);
                break;
            }
            case SNAP: {
                if (this.isPlacing) {
                    float[] rots = this.getRotations(this.info);
                    float yaw = this.processRotation(rots[0]);
                    float pitch = this.processRotation(rots[1]);
                    this.mc.thePlayer.renderYawOffset = this.finalRotationYaw = yaw;
                    this.mc.thePlayer.rotationYawHead = this.finalRotationYaw;
                    e.setYaw(this.finalRotationYaw);
                    this.mc.thePlayer.rotationPitchHead = this.finalRotationPitch = pitch;
                    e.setPitch(this.finalRotationPitch);
                    break;
                }
                this.mc.thePlayer.renderYawOffset = this.finalRotationYaw = this.mc.thePlayer.rotationYaw;
                this.mc.thePlayer.rotationYawHead = this.finalRotationYaw;
                e.setYaw(this.finalRotationYaw);
                this.mc.thePlayer.rotationPitchHead = this.finalRotationPitch = this.mc.thePlayer.rotationPitch;
                e.setPitch(this.finalRotationPitch);
                break;
            }
            case WATCHDOG: {
                float[] rots = this.getAACRotations();
                float yaw = this.processRotation(MovementUtil.getDirection() - 150.0f);
                float pitch = this.processRotation(rots[1]);
                this.mc.thePlayer.renderYawOffset = this.finalRotationYaw = yaw;
                this.mc.thePlayer.rotationYawHead = this.finalRotationYaw;
                e.setYaw(this.finalRotationYaw);
                this.mc.thePlayer.rotationPitchHead = this.finalRotationPitch = pitch;
                e.setPitch(this.finalRotationPitch);
            }
        }
    }

    private float[] getRotations(BlockInfo info) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) {
            this.toggle();
        }
        float yaw = 0.0f;
        float pitch = 90.0f;
        Vec3 eyes = this.mc.thePlayer.getPositionEyes(RandomUtils.nextFloat((float)2.997f, (float)3.997f));
        Vec3 position = new Vec3((double)info.getPos().getX() + 0.49, (double)info.getPos().getY() + 0.49, (double)info.getPos().getZ() + 0.49).add(new Vec3(info.getFacing().getDirectionVec()));
        Vec3 resultPosition = position.subtract(eyes);
        yaw = (float)Math.toDegrees(Math.atan2(resultPosition.z, resultPosition.x)) - 90.0f;
        pitch = (float)(-Math.toDegrees(Math.atan2(resultPosition.y, Math.hypot(resultPosition.x, resultPosition.z))));
        return new float[]{yaw, pitch};
    }

    private float[] getAACRotations() {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) {
            this.toggle();
        }
        float clientYaw = MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYaw);
        float diff = (int)((this.blockYaw - clientYaw) / 45.0f);
        float yaw = clientYaw + diff * 45.0f;
        float pitch = !this.mc.thePlayer.onGround || this.mc.gameSettings.keyBindJump.isKeyDown() ? 90.0f : (MovementUtil.isGoingDiagonally() ? 83.0f : 81.5f);
        return new float[]{yaw, pitch};
    }

    private float[] getBruteForceRotations(BlockInfo info) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) {
            this.toggle();
        }
        float yaw = 0.0f;
        float pitch = this.mc.thePlayer.onGround ? 82.0f : 90.0f;
        for (int i = -180; i < 180; ++i) {
            Vec3 src = this.mc.thePlayer.getPositionEyes(1.0f);
            Vec3 rotationVec = Entity.getVectorForRotation(pitch, i);
            Vec3 dest = src.addVector(rotationVec.x * 3.0, rotationVec.y * 3.0, rotationVec.z * 3.0);
            IBlockState blockState = this.mc.theWorld.getBlockState(info.pos);
            AxisAlignedBB bb = blockState.getBlock().getCollisionBoundingBox(this.mc.theWorld, info.pos, blockState);
            if (bb.calculateIntercept(src, dest) == null) continue;
            yaw = i;
            break;
        }
        return new float[]{yaw, pitch};
    }

    public BlockInfo getDiagonalBlockInfo(BlockPos pos) {
        BlockPos up = new BlockPos(0, -1, 0);
        BlockPos east = new BlockPos(-1, 0, 0);
        BlockPos west = new BlockPos(1, 0, 0);
        BlockPos north = new BlockPos(0, 0, 1);
        BlockPos south = new BlockPos(0, 0, -1);
        if (this.canPlaceAt(pos.add(up))) {
            return new BlockInfo(pos.add(up), EnumFacing.UP);
        }
        if (this.canPlaceAt(pos.add(east))) {
            this.blockYaw = 90.0f;
            return new BlockInfo(pos.add(east), EnumFacing.EAST);
        }
        if (this.canPlaceAt(pos.add(west))) {
            this.blockYaw = -90.0f;
            return new BlockInfo(pos.add(west), EnumFacing.WEST);
        }
        if (this.canPlaceAt(pos.add(south))) {
            this.blockYaw = 180.0f;
            return new BlockInfo(pos.add(south), EnumFacing.SOUTH);
        }
        if (this.canPlaceAt(pos.add(north))) {
            this.blockYaw = 0.0f;
            return new BlockInfo(pos.add(north), EnumFacing.NORTH);
        }
        BlockPos[] positions = new BlockPos[]{east, west, south, north};
        BlockInfo data = null;
        for (BlockPos offset : positions) {
            data = this.getBlockInfo(pos.add(offset));
            if (data == null) continue;
            return data;
        }
        for (BlockPos offset1 : positions) {
            for (BlockPos offset2 : positions) {
                data = this.getBlockInfo(pos.add(offset1).add(offset2));
                if (data == null) continue;
                return data;
            }
        }
        for (BlockPos offset1 : positions) {
            for (BlockPos offset2 : positions) {
                for (BlockPos offset3 : positions) {
                    data = this.getBlockInfo(pos.add(offset1).add(offset2).add(offset3));
                    if (data == null) continue;
                    return data;
                }
            }
        }
        return new BlockInfo(pos, EnumFacing.DOWN);
    }

    public BlockInfo getBlockInfo(BlockPos pos) {
        if (this.mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air) {
            return new BlockInfo(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air) {
            this.blockYaw = 90.0f;
            return new BlockInfo(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air) {
            this.blockYaw = -90.0f;
            new BlockInfo(pos.add(1, 0, 0), EnumFacing.WEST);
        } else {
            if (this.mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air) {
                this.blockYaw = 180.0f;
                return new BlockInfo(pos.add(0, 0, -1), EnumFacing.SOUTH);
            }
            if (this.mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) {
                this.blockYaw = 0.0f;
                return new BlockInfo(pos.add(0, 0, 1), EnumFacing.NORTH);
            }
        }
        return null;
    }

    public boolean canPlaceAt(BlockPos pos) {
        return this.mc.theWorld.getBlockState(pos).getBlock() != Blocks.air;
    }

    private ItemStack setStackToPlace() {
        ItemStack block = this.mc.thePlayer.getCurrentEquippedItem();
        if (block != null && block.getItem() != null && !(block.getItem() instanceof ItemBlock)) {
            block = null;
        }
        int slot = this.lastSlot;
        for (int g = 0; g < 9; g = (int)((short)(g + 1))) {
            if (!this.mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack() || !this.isValidBlock(this.mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack()) || block != null || this.mc.thePlayer.inventoryContainer.getSlot((int)(g + 36)).getStack().stackSize <= 0) continue;
            slot = g;
            block = this.mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack();
        }
        if (this.lastSlot != slot) {
            if (this.itemMode.getValue() == ItemMode.SWITCH) {
                this.mc.thePlayer.inventory.currentItem = slot;
            } else {
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
            }
            this.lastSlot = slot;
        }
        return block;
    }

    private boolean isValidBlock(ItemStack stack) {
        return stack.getItem() instanceof ItemBlock && !((ItemBlock)stack.getItem()).getBlock().getLocalizedName().toLowerCase().contains("chest") && !((ItemBlock)stack.getItem()).getBlock().getLocalizedName().toLowerCase().contains("table") && !((ItemBlock)stack.getItem()).getBlock().getLocalizedName().toLowerCase().contains("tnt") && !((ItemBlock)stack.getItem()).getBlock().getLocalizedName().toLowerCase().contains("slab");
    }

    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            Slot slot;
            try {
                slot = this.mc.thePlayer.inventoryContainer.getSlot(i);
            }
            catch (Exception ex) {
                continue;
            }
            if (!slot.getHasStack()) continue;
            ItemStack stack = slot.getStack();
            Item item = stack.getItem();
            if (!this.isValidBlock(stack)) continue;
            blockCount += stack.stackSize;
        }
        return blockCount;
    }

    private void stopPlacing() {
        try {
            Thread.sleep(100L);
            this.isPlacing = false;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private float processRotation(float value) {
        float toReturn = value;
        if (this.gcdFix.getValue().booleanValue()) {
            double m = 0.005 * (double)this.mc.gameSettings.mouseSensitivity;
            double gcd = m * m * m * 1.2;
            toReturn = (float)((double)toReturn - (double)toReturn % gcd);
            return toReturn;
        }
        return toReturn;
    }

    private boolean isReplaceable(BlockInfo info) {
        return this.mc.theWorld.getBlockState(info.pos).getBlock().canCollideCheck(this.mc.theWorld.getBlockState(info.pos), false);
    }

    public Setting<String> getPlacementSettings() {
        return this.placementSettings;
    }

    public Setting<Long> getPlaceDelay() {
        return this.placeDelay;
    }

    public Setting<String> getMovementSettings() {
        return this.movementSettings;
    }

    public Setting<String> getBypassSettings() {
        return this.bypassSettings;
    }

    public Setting<Boolean> getGcdFix() {
        return this.gcdFix;
    }

    private class BlockInfo {
        private BlockPos pos;
        private EnumFacing facing;

        public BlockInfo(BlockPos position, EnumFacing face) {
            this.pos = position;
            this.facing = face;
        }

        public BlockPos getPos() {
            if (this.pos == null) {
                return new BlockPos(Scaffold.this.mc.thePlayer.posX, Scaffold.this.mc.thePlayer.posY - 1.0, Scaffold.this.mc.thePlayer.posZ);
            }
            return this.pos;
        }

        public EnumFacing getFacing() {
            return this.facing;
        }
    }

    static enum VecMode {
        ZERO,
        POS,
        LEGIT,
        DIR,
        STRICT;

    }

    static enum TowerMode {
        NONE,
        MOTION,
        POSITION,
        NCP,
        WATCHDOG,
        SMALLHOP,
        VERUS;

    }

    static enum ItemMode {
        SWITCH,
        SPOOF;

    }

    static enum PlaceMode {
        PRE,
        POST;

    }

    static enum Rotations {
        NORMAL,
        AAC,
        BRUTE_FORCE,
        SNAP,
        NONE,
        WATCHDOG;

    }

    static enum Mode {
        NORMAL,
        WATCHDOG,
        VERUS;

    }
}

