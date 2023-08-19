/**
 * @project hakarware
 * @author CodeMan
 * @at 27.07.23, 02:25
 */

package cc.swift.module.impl.player;

import cc.swift.Swift;
import cc.swift.events.*;
import cc.swift.module.Module;
import cc.swift.util.ChatUtil;
import cc.swift.util.player.MovementUtil;
import cc.swift.util.player.RotationUtil;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.concurrent.ThreadLocalRandom;

public final class ScaffoldModule extends Module {

    public final ModeValue<RotationMode> rotationMode = new ModeValue<>("Rotation Mode", RotationMode.values());
    public final BooleanValue towerCenter = new BooleanValue("Tower Center", false);
    public final ModeValue<TowerMoveMode> towerMoveMode = new ModeValue<>("TowerMove", TowerMoveMode.values());
    public final DoubleValue towerMoveSpeed = new DoubleValue("TowerMove Speed", 0.5, 0, 1, 0.01).setDependency(() -> towerMoveMode.getValue() == ScaffoldModule.TowerMoveMode.VANILLA);
    public final DoubleValue towerMoveMotion = new DoubleValue("TowerMove Motion", 0.5, 0, 1, 0.01).setDependency(() -> towerMoveMode.getValue() == ScaffoldModule.TowerMoveMode.VANILLA);
    public final ModeValue<TowerMode> towerMode = new ModeValue<>("Tower", TowerMode.values());
    public final DoubleValue towerSpeed = new DoubleValue("Tower Motion", 0.5, 0, 1, 0.01).setDependency(() -> towerMode.getValue() == ScaffoldModule.TowerMode.VANILLA);
    public final BooleanValue keepY = new BooleanValue("Keep Y", false);
    public final DoubleValue minDelay = new DoubleValue("Min Delay", 0D, 0, 1000, 5);
    public final DoubleValue maxDelay = new DoubleValue("Max Delay", 0D, 0, 1000, 5);
    public final ModeValue<SprintMode> sprintMode = new ModeValue<>("Sprint", SprintMode.values());

    private BlockData blockData;
    private double startY;
    private int oldSlot, offGroundTicks;
    private long nextTime;
    private float[] rotations;

    public ScaffoldModule() {
        super("Scaffold", Category.PLAYER);
        this.registerValues(this.rotationMode, this.towerCenter, this.towerMoveMode, this.towerMoveSpeed, this.towerMoveMotion, this.towerMode, this.towerSpeed, this.keepY, this.minDelay, this.maxDelay, this.sprintMode);
    }

    @Handler
    public final Listener<RotationEvent> rotationEventListener = event -> {
        if (this.rotationMode.getValue() != RotationMode.OFF) {
            Vec3 eyesPos = RotationUtil.getEyePosition();
            switch (rotationMode.getValue()) {
                case NORMAL:
                    if (this.blockData != null) {
                        this.rotations = RotationUtil.getRotationsToVector(eyesPos, this.blockData.hitVec);
                    }
                    break;

                case STATIC:
                    if (this.blockData != null) {
                        this.rotations = RotationUtil.getRotationsToVector(eyesPos, this.blockData.hitVec);
                    }
                    if (this.rotations != null) {
                        this.rotations[0] = mc.thePlayer.rotationYaw + 180;
                    }
                    break;

                case HYPIXEL:
                    if (this.blockData != null) {
                        this.rotations = RotationUtil.getRotationsToVector(eyesPos, this.blockData.hitVec);
                        this.rotations[0] = MovementUtil.getDirection() + 180;
                    }
                    break;
            }


            if (this.rotations == null) return;

            RotationUtil.fixGCD(this.rotations, new float[]{Swift.INSTANCE.getRotationHandler().getLastYaw(), Swift.INSTANCE.getRotationHandler().getLastPitch()});

            event.setYaw(this.rotations[0]);
            event.setPitch(this.rotations[1]);
        }
    };

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (!MovementUtil.isOnGround())
            offGroundTicks++;
        else
            offGroundTicks = 0;

        if (this.minDelay.getValue() > this.maxDelay.getValue())
            this.maxDelay.setValue(this.minDelay.getValue());

        if (mc.gameSettings.keyBindJump.isKeyDown() && !MovementUtil.isMoving()) {
            if (towerCenter.getValue() && this.blockData != null) {
                mc.thePlayer.setPosition(Math.floor(mc.thePlayer.posX) + 0.500, mc.thePlayer.posY, Math.floor(mc.thePlayer.posZ) + 0.500);
            }
            switch (towerMode.getValue()) {
                case VANILLA:
                    if (this.blockData == null) return;
                    mc.thePlayer.motionY = towerSpeed.getValue();
                    break;

                case NCP:
                    if (this.blockData == null) return;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
                    mc.thePlayer.motionY = 0.42F;
                    break;
            }
        }

        if (mc.gameSettings.keyBindJump.isKeyDown() && MovementUtil.isMoving()) {
            switch (towerMoveMode.getValue()) {
                case VANILLA:
                    if (this.blockData == null) return;

                    MovementUtil.setSpeed(towerMoveSpeed.getValue());
                    mc.thePlayer.motionY = towerMoveMotion.getValue();
                    break;

                case HYPIXEL:
                    if (mc.thePlayer.isPotionActive(1)) {
                        mc.thePlayer.motionX *= 0.718;
                        mc.thePlayer.motionZ *= 0.718;
                    }
                    if (this.blockData == null) return;
                    mc.gameSettings.keyBindSprint.pressed = false;
                    mc.thePlayer.setSprinting(false);
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
                        mc.thePlayer.jump();
                    }
                    if (offGroundTicks == 3) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
                        mc.thePlayer.motionY = -0.34;
                    }
                    break;
            }
        }

        if (this.blockData == null) return;

        if (System.currentTimeMillis() < this.nextTime) return;

        double minDelayValue = this.minDelay.getValue(), maxDelayValue = this.maxDelay.getValue();
        double delayValue = minDelayValue == maxDelayValue ? minDelayValue : ThreadLocalRandom.current().nextDouble(minDelayValue, maxDelayValue);
        this.nextTime = System.currentTimeMillis() + (long) (delayValue);

        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), this.blockData.blockPos, this.blockData.enumFacing, this.blockData.hitVec)) {
            mc.thePlayer.swingItem();
            //ChatUtil.printChatMessage(this.blockData.hitVec);
        }
    };

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = event -> {

        switch (sprintMode.getValue()) {
            case NONE:
                mc.gameSettings.keyBindSprint.pressed = false;
                mc.thePlayer.setSprinting(false);
                break;

            case NORMAL:
                mc.gameSettings.keyBindSprint.pressed = true;
                break;

            case SILENT:
                event.setSprinting(false);
                break;

            case CAN_SPRINT:
                boolean shouldSprint = Math.abs(MathHelper.wrapAngleTo180_float(this.rotations[0] - mc.thePlayer.rotationYaw)) < 45;

                mc.gameSettings.keyBindSprint.pressed = shouldSprint;

                if (!shouldSprint && mc.thePlayer.isSprinting())
                    mc.thePlayer.setSprinting(false);
                break;

            case HYPIXEL:
                mc.thePlayer.setSprinting(false);
                mc.gameSettings.keyBindSprint.pressed = false;
                if (MovementUtil.isOnGround() && MovementUtil.isMoving())
                    MovementUtil.setSpeed(0.1105 + Math.random() / 2000);
                break;
            case VULCAN:
                event.setSprinting(false);
                if (mc.thePlayer.ticksExisted % 25 == 0) {
                    event.setSneaking(true);
                }

                break;
        }

        if (event.getState() != EventState.PRE) return;
        int bestSlot = -1;
        int bestSize = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock) || !((ItemBlock) itemStack.getItem()).getBlock().isFullBlock() || ((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockContainer)
                continue;

            if (itemStack.stackSize > bestSize) {
                bestSlot = i;
                bestSize = itemStack.stackSize;
                break;
            }
        }

        if (bestSlot == -1) return;

        mc.thePlayer.inventory.currentItem = bestSlot;

        this.blockData = findBlockData();

//        if (this.rotationMode.getValue() != RotationMode.OFF) {
//            Vec3 eyesPos = RotationUtil.getEyePosition();
//            switch (rotationMode.getValue()) {
//                case NORMAL:
//                    if (this.blockData != null) {
//                        this.rotations = RotationUtil.getRotationsToVector(eyesPos, this.blockData.hitVec);
//                    }
//                    break;
//
//                case STATIC:
//                    if (this.blockData != null) {
//                        this.rotations = RotationUtil.getRotationsToVector(eyesPos, this.blockData.hitVec);
//                    }
//                    if (this.rotations != null) {
//                        this.rotations[0] = mc.thePlayer.rotationYaw + 180;
//                    }
//                    break;
//
//                case HYPIXEL:
//                    if (this.blockData != null) {
//                        this.rotations = RotationUtil.getRotationsToVector(eyesPos, this.blockData.hitVec);
//                        this.rotations[0] = MovementUtil.getDirection() + 180;
//                    }
//                    break;
//            }
//
//
//            if (this.rotations == null) return;
//
//            RotationUtil.fixGCD(this.rotations, new float[]{event.getLastYaw(), event.getLastPitch()});
//
//            event.setYaw(this.rotations[0]);
//            event.setPitch(this.rotations[1]);
//        }
    };

    @Override
    public void onEnable() {
        if (mc.thePlayer != null) {
            this.startY = mc.thePlayer.posY;
            this.oldSlot = mc.thePlayer.inventory.currentItem;

            this.rotations = new float[] {mc.thePlayer.rotationYaw + 180, 80};
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.inventory.currentItem = this.oldSlot;
    }

    public BlockData findBlockData() {
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, keepY.getValue() ? startY - 1 : mc.thePlayer.posY - 1D, mc.thePlayer.posZ);

        if (mc.theWorld.getBlockState(playerPos).getBlock().getMaterial().isReplaceable()) {
            for (EnumFacing enumFacing : EnumFacing.values()) {
                if (enumFacing == EnumFacing.UP) continue;
                BlockPos offsetPos = playerPos.offset(enumFacing);
                if (!mc.theWorld.getBlockState(offsetPos).getBlock().getMaterial().isReplaceable()) {
                    return new BlockData(offsetPos, enumFacing.getOpposite());
                }
            }
            for (EnumFacing enumFacing : EnumFacing.values()) {
                if (enumFacing == EnumFacing.UP) continue;
                BlockPos offsetPos = playerPos.offset(enumFacing);
                if (mc.theWorld.getBlockState(offsetPos).getBlock().getMaterial().isReplaceable()) {
                    for (EnumFacing enumFacing2 : EnumFacing.values()) {
                        if (enumFacing2 == EnumFacing.UP) continue;
                        BlockPos offsetPos2 = offsetPos.offset(enumFacing2);
                        if (!mc.theWorld.getBlockState(offsetPos2).getBlock().getMaterial().isReplaceable()) {
                            return new BlockData(offsetPos2, enumFacing2.getOpposite());
                        }
                    }
                }
            }
        }

        return null;
    }

    enum RotationMode {
        OFF, NORMAL, STATIC, HYPIXEL
    }

    enum TowerMode {
        NONE, VANILLA, NCP
    }

    enum TowerMoveMode {
        NONE, VANILLA, HYPIXEL
    }

    enum SprintMode {
        NONE, NORMAL, SILENT, CAN_SPRINT, HYPIXEL, VULCAN
    }

    static class BlockData {
        private final BlockPos blockPos;
        private final EnumFacing enumFacing;
        private final Vec3 hitVec;

        public BlockData(BlockPos blockPos, EnumFacing enumFacing) {
            this.blockPos = blockPos;
            this.enumFacing = enumFacing;
            this.hitVec = new Vec3(blockPos).addVector(0.5, 0.5, 0.5).addVector(enumFacing.getDirectionVec().getX() * 0.5, enumFacing.getDirectionVec().getY() * 0.5, enumFacing.getDirectionVec().getZ() * 0.5);
        }
    }
}