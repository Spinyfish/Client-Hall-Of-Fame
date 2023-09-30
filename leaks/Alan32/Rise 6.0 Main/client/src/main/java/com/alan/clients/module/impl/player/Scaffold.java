package com.alan.clients.module.impl.player;

import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.player.BadPacketsComponent;
import com.alan.clients.component.impl.player.BlinkComponent;
import com.alan.clients.component.impl.player.RotationComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.component.impl.player.rotationcomponent.MovementFix;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.movement.Speed;
import com.alan.clients.module.impl.player.scaffold.sprint.*;
import com.alan.clients.module.impl.player.scaffold.tower.*;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.MoveInputEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.util.RayCastUtil;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.EnumFacingOffset;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.util.player.SlotUtil;
import com.alan.clients.util.rotation.RotationUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.util.vector.Vector3d;
import com.alan.clients.value.impl.*;
import net.minecraft.block.BlockAir;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

/**
 * @author Alan
 * @since ??/??/21
 */

@Rise
@ModuleInfo(name = "module.player.scaffold.name", description = "module.player.scaffold.description", category = Category.PLAYER)
public class Scaffold extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Normal"))
            .add(new SubMode("Snap"))
            .add(new SubMode("Telly"))
            .add(new SubMode("UPDATED-NCP"))
            .setDefault("Normal");

    private final ModeValue rayCast = new ModeValue("Ray Cast", this)
            .add(new SubMode("Off"))
            .add(new SubMode("Normal"))
            .add(new SubMode("Strict"))
            .setDefault("Off");

    private final ModeValue sprint = new ModeValue("Sprint", this)
            .add(new SubMode("Normal"))
            .add(new DisabledSprint("Disabled", this))
            .add(new LegitSprint("Legit", this))
            .add(new BypassSprint("Bypass", this))
            .add(new VulcanSprint("Vulcan", this))
            .add(new MatrixSprint("Matrix", this))
            .add(new WatchdogSprint("Watchdog", this))
            .setDefault("Normal");

    private final ModeValue tower = new ModeValue("Tower", this)
            .add(new SubMode("Disabled"))
            .add(new VulcanTower("Vulcan", this))
            .add(new VanillaTower("Vanilla", this))
            .add(new NormalTower("Normal", this))
            .add(new AirJumpTower("Air Jump", this))
            .add(new WatchdogTower("Watchdog", this))
            .add(new MMCTower("MMC", this))
            .add(new NCPTower("NCP", this))
            .add(new MatrixTower("Matrix", this))
            .add(new LegitTower("Legit", this))
            .setDefault("Disabled");

    private final ModeValue sameY = new ModeValue("Same Y", this)
            .add(new SubMode("Off"))
            .add(new SubMode("On"))
            .add(new SubMode("Auto Jump"))
            .setDefault("Off");

    private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation Speed", this, 5, 10, 0, 10, 1);
    private final BoundsNumberValue placeDelay = new BoundsNumberValue("Place Delay", this, 0, 0, 0, 5, 1);
    private final NumberValue timer = new NumberValue("Timer", this, 1, 0.1, 10, 0.1);

    public final BooleanValue movementCorrection = new BooleanValue("Movement Correction", this, false);
    public final BooleanValue safeWalk = new BooleanValue("Safe Walk", this, true);
    private final BooleanValue sneak = new BooleanValue("Sneak", this, false);
    public final BoundsNumberValue startSneaking = new BoundsNumberValue("Start Sneaking", this, 0, 0, 0, 5, 1, () -> !sneak.getValue());
    public final BoundsNumberValue stopSneaking = new BoundsNumberValue("Stop Sneaking", this, 0, 0, 0, 5, 1, () -> !sneak.getValue());
    public final NumberValue sneakEvery = new NumberValue("Sneak every x blocks", this, 1, 1, 10, 1, () -> !sneak.getValue());

    public final NumberValue sneakingSpeed = new NumberValue("Sneaking Speed", this, 0.2, 0.2, 1, 0.05, () -> !sneak.getValue());

    private final BooleanValue render = new BooleanValue("Render", this, true);

    private final BooleanValue advanced = new BooleanValue("Advanced", this, false);

    public final ModeValue yawOffset = new ModeValue("Yaw Offset", this, () -> !advanced.getValue())
            .add(new SubMode("0"))
            .add(new SubMode("45"))
            .add(new SubMode("-45"))
            .setDefault("0");

    public final BooleanValue ignoreSpeed = new BooleanValue("Ignore Speed Effect", this, false, () -> !advanced.getValue());
    public final BooleanValue upSideDown = new BooleanValue("Up Side Down", this, false, () -> !advanced.getValue());

    private Vec3 targetBlock;
    private EnumFacingOffset enumFacing;
    private BlockPos blockFace;
    private float targetYaw, targetPitch;
    private int ticksOnAir, sneakingTicks;
    private double startY;
    private float forward, strafe;
    private int placements;
    private boolean incrementedPlacements;

    @Override
    protected void onEnable() {
        targetYaw = mc.thePlayer.rotationYaw - 180;
        targetPitch = 90;

        startY = Math.floor(mc.thePlayer.posY);
        targetBlock = null;

        this.sneakingTicks = -1;

    }

    @Override
    protected void onDisable() {
        mc.gameSettings.keyBindSneak.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
        mc.gameSettings.keyBindJump.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));

        BlinkComponent.blinking = false;

        // This is a temporary patch
        SlotComponent.setSlot(mc.thePlayer.inventory.currentItem);
    }

    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {

        final Packet<?> packet = event.getPacket();

        if (packet instanceof S2FPacketSetSlot) {
            final S2FPacketSetSlot wrapper = ((S2FPacketSetSlot) packet);

            if (wrapper.func_149174_e() == null) {
                event.setCancelled(true);
            } else {
                try {
                    int slot = wrapper.func_149173_d() - 36;
                    if (slot < 0) return;
                    final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(slot);
                    final Item item = wrapper.func_149174_e().getItem();

                    if ((itemStack == null && wrapper.func_149174_e().stackSize <= 6 && item instanceof ItemBlock && !SlotUtil.blacklist.contains(((ItemBlock) item).getBlock())) ||
                            itemStack != null && Math.abs(Objects.requireNonNull(itemStack).stackSize - wrapper.func_149174_e().stackSize) <= 6 ||
                            wrapper.func_149174_e() == null) {
                        event.setCancelled(true);
                    }
                } catch (ArrayIndexOutOfBoundsException exception) {
                    exception.printStackTrace();
                }
            }
        }
    };

    public void calculateSneaking(MoveInputEvent moveInputEvent) {
        forward = moveInputEvent.getForward();
        strafe = moveInputEvent.getStrafe();

        if (!this.sneak.getValue()) {
            return;
        }

        double speed = this.sneakingSpeed.getValue().doubleValue();

        if (speed <= 0.2) {
            return;
        }

        moveInputEvent.setSneakSlowDownMultiplier(speed);
    }

    public void calculateSneaking() {
        mc.gameSettings.keyBindSneak.setPressed(false);

        if (!MoveUtil.isMoving()) {
            return;
        }

        this.sneakingTicks--;

        if (sneakingTicks < 0) incrementedPlacements = false;

        if (!this.sneak.getValue()) {
            return;
        }

        int ahead = (int) MathUtil.getRandom(startSneaking.getValue().intValue(), startSneaking.getSecondValue().intValue());
        int place = (int) MathUtil.getRandom(placeDelay.getValue().intValue(), placeDelay.getSecondValue().intValue());
        int after = (int) MathUtil.getRandom(stopSneaking.getValue().intValue(), stopSneaking.getSecondValue().intValue());

        if (this.ticksOnAir > 0) {
            this.sneakingTicks = (int) (Math.ceil((after + (place - this.ticksOnAir)) / this.sneakingSpeed.getValue().doubleValue()));
        }

        if (this.sneakingTicks >= 0 || (ahead == 5 && after == 5)) {
            if (placements % sneakEvery.getValue().intValue() == 0) {
                mc.gameSettings.keyBindSneak.setPressed(true);
            }

            if (!incrementedPlacements) placements++;
            incrementedPlacements = true;
            return;
        }

        if (ahead == 0 && place == 0 && this.ticksOnAir > 0) {
            this.sneakingTicks = 1;
            return;
        }

        if (PlayerUtil.blockRelativeToPlayer(mc.thePlayer.motionX * ahead * sneakingSpeed.getValue().doubleValue(), MoveUtil.HEAD_HITTER_MOTION, mc.thePlayer.motionZ * ahead * sneakingSpeed.getValue().doubleValue()) instanceof BlockAir) {
            this.sneakingTicks = (int) Math.floor((5 + place + after) / this.sneakingSpeed.getValue().doubleValue());
            placements++;
        }
    }

    public void calculateRotations() {
        float yawOffset = Float.parseFloat(String.valueOf(this.yawOffset.getValue().getName()));

        /* Calculating target rotations */
        switch (mode.getValue().getName()) {
            case "Normal":
                if (ticksOnAir > 0 && !RayCastUtil.overBlock(RotationComponent.rotations, enumFacing.getEnumFacing(), blockFace, rayCast.getValue().getName().equals("Strict"))) {
                    getRotations(Float.parseFloat(String.valueOf(this.yawOffset.getValue().getName())));
                }
                break;

            case "UPDATED-NCP":

                if (ticksOnAir > 0 && !RayCastUtil.overBlock(RotationComponent.rotations, enumFacing.getEnumFacing(), blockFace, rayCast.getValue().getName().equals("Strict"))) {
                    getRotations(Float.parseFloat(String.valueOf(this.yawOffset.getValue().getName())));
                }

                targetPitch = 69;
                break;

            case "Snap":
                getRotations(yawOffset);

                if (!(ticksOnAir > 0 && !RayCastUtil.overBlock(RotationComponent.rotations, enumFacing.getEnumFacing(), blockFace, rayCast.getValue().getName().equals("Strict")))) {
                    targetYaw = (float) (Math.toDegrees(MoveUtil.direction(mc.thePlayer.rotationYaw, forward, strafe))) + yawOffset;
                }
                break;

            case "Telly":
                if (mc.thePlayer.offGroundTicks >= 3) {
                    if (!RayCastUtil.overBlock(RotationComponent.rotations, enumFacing.getEnumFacing(), blockFace, rayCast.getValue().getName().equals("Strict"))) {
                        getRotations(yawOffset);
                    }
                } else {
                    getRotations(Float.parseFloat(String.valueOf(this.yawOffset.getValue().getName())));
                    targetYaw = mc.thePlayer.rotationYaw - yawOffset;
                }
                break;
        }

        /* Randomising slightly */
//        if (Math.random() > 0.8 && targetPitch > 50) {
//            final Vector2f random = new Vector2f((float) (Math.random() - 0.5), (float) (Math.random() / 2));
//
//            if (ticksOnAir <= 0 || RayCastUtil.overBlock(new Vector2f(targetYaw + random.x, targetPitch + random.y), enumFacing.getEnumFacing(),
//                    blockFace, rayCast.getValue().getName().equals("Strict"))) {
//
//                targetYaw += random.x;
//                targetPitch += random.y;
//            }
//        }

        /* Smoothing rotations */
        final double minRotationSpeed = this.rotationSpeed.getValue().doubleValue();
        final double maxRotationSpeed = this.rotationSpeed.getSecondValue().doubleValue();
        float rotationSpeed = (float) MathUtil.getRandom(minRotationSpeed, maxRotationSpeed);

        if (rotationSpeed != 0) {
            RotationComponent.setRotations(new Vector2f(targetYaw, targetPitch), rotationSpeed, movementCorrection.getValue() ? MovementFix.NORMAL : MovementFix.OFF);
        }
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (targetBlock == null || enumFacing == null || blockFace == null) {
            return;
        }

        mc.thePlayer.hideSneakHeight.reset();

        // Timer
        if (timer.getValue().floatValue() != 1) mc.timer.timerSpeed = timer.getValue().floatValue();
    };

    public void runMode() {
        switch (this.mode.getValue().getName()) {
            case "Telly": {
                if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
                    mc.thePlayer.jump();
                }
            }
        }
    }

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {

        mc.thePlayer.safeWalk = this.safeWalk.getValue();

        // Getting ItemSlot
        SlotComponent.setSlot(SlotUtil.findBlock(), render.getValue());

        //Used to detect when to place a block, if over air, allow placement of blocks
        if (PlayerUtil.blockRelativeToPlayer(0, upSideDown.getValue() ? 2 : -1, 0) instanceof BlockAir) {
            ticksOnAir++;
        } else {
            ticksOnAir = 0;
        }

        this.calculateSneaking();

        // Gets block to place
        targetBlock = PlayerUtil.getPlacePossibility(0, upSideDown.getValue() ? 3 : 0, 0);

        if (targetBlock == null) {
            return;
        }

        //Gets EnumFacing
        enumFacing = PlayerUtil.getEnumFacing(targetBlock);

        if (enumFacing == null) {
            return;
        }

        final BlockPos position = new BlockPos(targetBlock.xCoord, targetBlock.yCoord, targetBlock.zCoord);

        blockFace = position.add(enumFacing.getOffset().xCoord, enumFacing.getOffset().yCoord, enumFacing.getOffset().zCoord);

        if (blockFace == null || enumFacing == null) {
            return;
        }

        this.calculateRotations();

        if (targetBlock == null || enumFacing == null || blockFace == null) {
            return;
        }

        if (this.sameY.getValue().getName().equals("Auto Jump")) {
            mc.gameSettings.keyBindJump.setPressed((mc.thePlayer.onGround && MoveUtil.isMoving()) || mc.gameSettings.keyBindJump.isPressed());
        }

        // Same Y
        final boolean sameY = ((!this.sameY.getValue().getName().equals("Off") || this.getModule(Speed.class).isEnabled()) && !mc.gameSettings.keyBindJump.isKeyDown()) && MoveUtil.isMoving();

        if (startY - 1 != Math.floor(targetBlock.yCoord) && sameY) {
            return;
        }

        if (mc.thePlayer.inventory.alternativeCurrentItem == SlotComponent.getItemIndex()) {
            if (!BadPacketsComponent.bad(false, true, false, false, true) &&
                    ticksOnAir > MathUtil.getRandom(placeDelay.getValue().intValue(), placeDelay.getSecondValue().intValue()) &&
                    (RayCastUtil.overBlock(enumFacing.getEnumFacing(), blockFace, rayCast.getValue().getName().equals("Strict")) || rayCast.getValue().getName().equals("Off"))) {

                Vec3 hitVec = this.getHitVec();

                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, SlotComponent.getItemStack(), blockFace, enumFacing.getEnumFacing(), hitVec)) {
                    PacketUtil.send(new C0APacketAnimation());
                }

                mc.rightClickDelayTimer = 0;
                ticksOnAir = 0;

                assert SlotComponent.getItemStack() != null;
                if (SlotComponent.getItemStack() != null && SlotComponent.getItemStack().stackSize == 0) {
                    mc.thePlayer.inventory.mainInventory[SlotComponent.getItemIndex()] = null;
                }
            } else if (Math.random() > 0.92 && mc.rightClickDelayTimer <= 0) {
//                ChatUtil.display("Drag: " + Math.random());
                PacketUtil.send(new C08PacketPlayerBlockPlacement(SlotComponent.getItemStack()));
                mc.rightClickDelayTimer = 0;
            }
        }

        //For Same Y
        if (mc.thePlayer.onGround || (mc.gameSettings.keyBindJump.isKeyDown() && !MoveUtil.isMoving())) {
            startY = Math.floor(mc.thePlayer.posY);
        }

        if (mc.thePlayer.posY < startY) {
            startY = mc.thePlayer.posY;
        }
    };

    @EventLink()
    public final Listener<MoveInputEvent> onMove = this::calculateSneaking;

    public void getRotations(final float yawOffset) {
        boolean found = false;
        for (float possibleYaw = mc.thePlayer.rotationYaw - 180 + yawOffset; possibleYaw <= mc.thePlayer.rotationYaw + 360 - 180 && !found; possibleYaw += 45) {
            for (float possiblePitch = 90; possiblePitch > 30 && !found; possiblePitch -= possiblePitch > (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 60 : 80) ? 1 : 10) {
                if (RayCastUtil.overBlock(new Vector2f(possibleYaw, possiblePitch), enumFacing.getEnumFacing(), blockFace, true)) {
                    targetYaw = possibleYaw;
                    targetPitch = possiblePitch;
                    found = true;
                }
            }
        }

        if (!found) {
            final Vector2f rotations = RotationUtil.calculate(
                    new Vector3d(blockFace.getX(), blockFace.getY(), blockFace.getZ()), enumFacing.getEnumFacing());

            targetYaw = rotations.x;
            targetPitch = rotations.y;
        }
    }


    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {
        this.runMode();

        if (!Objects.equals(yawOffset.getValue().getName(), "0") && !movementCorrection.getValue()) {
            MoveUtil.useDiagonalSpeed();
        }
    };

    public Vec3 getHitVec() {
        /* Correct HitVec */
        Vec3 hitVec = new Vec3(blockFace.getX() + Math.random(), blockFace.getY() + Math.random(), blockFace.getZ() + Math.random());

        final MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationComponent.rotations, mc.playerController.getBlockReachDistance());

        switch (enumFacing.getEnumFacing()) {
            case DOWN:
                hitVec.yCoord = blockFace.getY() + 0;
                break;

            case UP:
                hitVec.yCoord = blockFace.getY() + 1;
                break;

            case NORTH:
                hitVec.zCoord = blockFace.getZ() + 0;
                break;

            case EAST:
                hitVec.xCoord = blockFace.getX() + 1;
                break;

            case SOUTH:
                hitVec.zCoord = blockFace.getZ() + 1;
                break;

            case WEST:
                hitVec.xCoord = blockFace.getX() + 0;
                break;
        }

        if (movingObjectPosition != null && movingObjectPosition.getBlockPos().equals(blockFace) &&
                movingObjectPosition.sideHit == enumFacing.getEnumFacing()) {
            hitVec = movingObjectPosition.hitVec;
        }

        return hitVec;
    }
}