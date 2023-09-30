package felix.module.impl.world;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketSend;
import felix.events.player.EventCollide;
import felix.events.player.EventMotionUpdate;
import felix.events.render.EventModelUpdate;
import felix.events.render.EventRender2D;
import felix.module.Module;
import felix.module.impl.player.Safewalk;
import felix.util.other.InventoryUtils;
import felix.util.other.TimeHelper;
import felix.util.player.MovementUtils;
import felix.util.player.RotationUtils;
import felix.util.visual.RenderUtil;
import felix.value.impl.BooleanValue;
import felix.value.impl.ColorValue;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;
import javafx.scene.transform.Scale;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.apache.commons.lang3.RandomUtils;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Scaffold2 extends Module {

    public static final BlockPos[] BLOCK_POSITIONS = new BlockPos[]{
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, -1),
            new BlockPos(0, 0, 1)};

    public static final EnumFacing[] FACINGS = new EnumFacing[]{
            EnumFacing.EAST,
            EnumFacing.WEST,
            EnumFacing.SOUTH,
            EnumFacing.NORTH};

    public final BooleanValue swingProperty = new BooleanValue("Swing", true);
    public final BooleanValue keepYProperty = new BooleanValue("Keep-y", true);
    public final BooleanValue safeWalkProperty = new BooleanValue("Safe walk", false);
    public final BooleanValue towerProperty = new BooleanValue("Tower", true);
    public final NumberValue<Double> maxTowerBlocksProperty = new NumberValue<Double>("Tower max dist", 3d, 1d, 6d, 1d);
    public final BooleanValue blockCountBarProperty = new BooleanValue("Block count", true);
    public final BooleanValue collideProperty = new BooleanValue("Collision", true);
    public final EnumValue<CancelSprintMode> cancelSprintProperty = new EnumValue<>("Cancel sprint", CancelSprintMode.CLIENT);
    public final ColorValue blockBarColor = new ColorValue("Bar color", 0xFFFF0000);
    public final NumberValue<Double> delayTicksProperty = new NumberValue<Double>("Delay ticks", 1d, 1d, 5d, 1d);
    public final NumberValue<Double> blockSlotProperty = new NumberValue<Double>("Override slot", 9d, 1d, 9d, 1d);
    private final List<Packet> packets = new LinkedList<>();
    public final TimeHelper clickTimer = new TimeHelper();
    public final TimeHelper pulseDelay = new TimeHelper();
    public int blockCount;
    public int originalHotBarSlot;
    public int bestBlockStack;
    public BlockData data;
    public float[] angles;
    public int placeCounter;
    public int ticksSincePlace;
    public int lastPos;
    public boolean towering;

    public Scaffold2() {
        super("Scaff is old", 0, ModuleCategory.WORLD);
        this.addValues(swingProperty, keepYProperty, safeWalkProperty, towerProperty, maxTowerBlocksProperty, blockCountBarProperty, collideProperty, cancelSprintProperty, blockBarColor, delayTicksProperty, blockSlotProperty);
    }


    @Handler
    public void a(EventPacketSend event){
        if(cancelSprintProperty.getValue() != CancelSprintMode.OFF) {
            if (event.getPacket() instanceof C03PacketPlayer) {
                if (this.placeCounter == 0) {
                    C03PacketPlayer nigger = (C03PacketPlayer) event.getPacket();
                    event.setCancelled(true);
                    if (nigger.isMoving())
                        packets.add(nigger);
                } else if (this.placeCounter > 0) {
                    if (pulseDelay.isDelayComplete(20)) {
                        for (Packet p : packets) {
                            mc.getNetHandler().addToSendQueueNoEvent(p);
                        }
                        packets.clear();
                        pulseDelay.reset();
                    } else {
                        event.setCancelled(true);
                        packets.add(event.getPacket());
                    }
                }
            }
        }
    }


    @Handler
    public void a(EventRender2D event) {
        if (blockCountBarProperty.getValue()) {
            ScaledResolution sr = new ScaledResolution(mc);
            int blockCount = 0;
            for (int i = 0; i < 45; ++i) {
                if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
                    continue;
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (!(is.getItem() instanceof ItemBlock) || !InventoryUtils.isValidBlock(((ItemBlock) item).getBlock(), false))
                    continue;
                blockCount += is.stackSize;
            }
            int color = new Color(255, 0, 0).getRGB();
            int bgcolor = new Color(1,1,1).getRGB();
            if (blockCount >= 64 && 128 > blockCount) {
                color = new Color(255, 255, 0).getRGB();
            } else if (blockCount >= 128) {
                color = new Color(0, 255, 0).getRGB();
            }

            GlStateManager.pushMatrix();
            mc.fontRendererObj.drawString(Integer.toString(blockCount), (sr.getScaledWidth() >> 1)  - mc.fontRendererObj.getStringWidth(Integer.toString(blockCount)) / 2 + 1, (sr.getScaledHeight() >> 1) - 15, bgcolor);
            mc.fontRendererObj.drawString(Integer.toString(blockCount), (sr.getScaledWidth() >> 1)  - mc.fontRendererObj.getStringWidth(Integer.toString(blockCount)) / 2 - 1, (sr.getScaledHeight() >> 1) - 15, bgcolor);
            mc.fontRendererObj.drawString(Integer.toString(blockCount), (sr.getScaledWidth() >> 1)  - mc.fontRendererObj.getStringWidth(Integer.toString(blockCount)) / 2, (sr.getScaledHeight() >> 1) - 15 + 1, bgcolor);
            mc.fontRendererObj.drawString(Integer.toString(blockCount), (sr.getScaledWidth() >> 1)  - mc.fontRendererObj.getStringWidth(Integer.toString(blockCount)) / 2, (sr.getScaledHeight() >> 1) - 15 - 1, bgcolor);
            mc.fontRendererObj.drawString(Integer.toString(blockCount), (sr.getScaledWidth() >> 1)  - mc.fontRendererObj.getStringWidth(Integer.toString(blockCount)) / 2, (sr.getScaledHeight() >> 1) - 15, color);
            GlStateManager.popMatrix();
        }
    }

    @Handler
    public void a(EventMotionUpdate event) {
        if (this.cancelSprintProperty.getValue() != CancelSprintMode.OFF && mc.thePlayer.isSprinting()) {
            mc.thePlayer.setSprinting(false);

            if (this.cancelSprintProperty.getValue() == CancelSprintMode.CLIENT) {
                this.mc.thePlayer.setSprinting(false);

                if (this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    this.mc.thePlayer.motionX /= 1.4;
                    this.mc.thePlayer.motionZ /= 1.4;
                }
            }
        }
    };

    @Handler
    public void aa(EventCollide event) {
        if (this.collideProperty.getValue() && event.getBlock() instanceof BlockAir && event.getY() < mc.thePlayer.posY) {
            event.setBoundingBox(new AxisAlignedBB((double) event.getX(), (double) event.getY(), (double) event.getZ(), event.getX() + 1.0, mc.thePlayer.posY, event.getZ() + 1.0));
        }
    };

    @Handler
    public void aa(EventMotionUpdate event) {
        mc.timer.timerSpeed = 1.99f;
        if (event.getType() == EventMotionUpdate.Type.PRE) {
            this.updateBlockCount();

            this.data = null;

            this.bestBlockStack = findBestBlockStack(InventoryUtils.ONLY_HOT_BAR_BEGIN, InventoryUtils.END);

            if (this.bestBlockStack == -1 && clickTimer.isDelayComplete(250L)) {
                this.bestBlockStack = findBestBlockStack(InventoryUtils.EXCLUDE_ARMOR_BEGIN, InventoryUtils.ONLY_HOT_BAR_BEGIN);

                if (this.bestBlockStack == -1) {
                    return;
                }

                boolean override = true;
                for (int i = InventoryUtils.END - 1; i >= InventoryUtils.ONLY_HOT_BAR_BEGIN; i--) {
                    final ItemStack stack = InventoryUtils.getStackInSlot(i);

                    if (!InventoryUtils.isValid(stack, true)) {
                        InventoryUtils.windowClick(this.bestBlockStack, i - InventoryUtils.ONLY_HOT_BAR_BEGIN,
                                InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                        this.bestBlockStack = i;
                        override = false;
                        break;
                    }
                }

                if (override) {
                    final int blockSlot = blockSlotProperty.getValue().intValue() - 1;
                    InventoryUtils.windowClick(bestBlockStack, blockSlot,
                            InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                    this.bestBlockStack = blockSlot + InventoryUtils.ONLY_HOT_BAR_BEGIN;
                }
            }

            if (this.bestBlockStack >= InventoryUtils.ONLY_HOT_BAR_BEGIN) {
                final BlockPos blockUnder = this.getBlockUnder();
                BlockData data = this.getBlockData(blockUnder);

                if (data == null)
                    data = this.getBlockData(blockUnder.add(0, -1, 0));

                if (data != null && this.bestBlockStack >= 36) {
                    if (validateReplaceable(data) && data.hitVec != null) {
                        this.angles = this.getRotations(data);
                    } else {
                        data = null;
                    }
                }

                if (this.angles != null) {
                    RotationUtils.rotate(event, this.angles, 90.0F, false);
                } else {
                    // TODO :: Persistent rotations
                }

                this.data = data;
            }
        } else if (this.data != null && this.bestBlockStack >= InventoryUtils.ONLY_HOT_BAR_BEGIN) {
            final EntityPlayerSP player = this.mc.thePlayer;

            if (++this.ticksSincePlace < this.delayTicksProperty.getValue()) return;

            final Vec3 lookingVec = this.isLookingAtBlock(this.data, event.getYaw(), event.getPitch());

            if (lookingVec == null) return;

            player.inventory.currentItem = this.bestBlockStack - InventoryUtils.ONLY_HOT_BAR_BEGIN;
            if (this.mc.playerController.onPlayerRightClick(player, Minecraft.getMinecraft().theWorld,
                    player.getCurrentEquippedItem(),
                    this.data.pos, this.data.face, lookingVec)) {
                this.placeCounter++;

                this.towering = this.towerProperty.getValue() && this.mc.gameSettings.keyBindJump.isKeyDown();

                if (this.towering && MovementUtils.isOnGround(0.0626) &&
                        (this.placeCounter % this.maxTowerBlocksProperty.getValue().intValue() != 0)) {
                    player.motionY = 0.42F - 0.000454352838557992;
                }

                if (this.swingProperty.getValue()) player.swingItem();
                else mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());

                this.ticksSincePlace = 0;
            }
        }
    };

    public Vec3 isLookingAtBlock(final BlockData data, final float yaw, final float pitch) {
        final Vec3 src = this.mc.thePlayer.getPositionEyes(1.0F);
        final Vec3 rotationVec = Entity.getVectorForRotation(pitch, yaw);
        final float reach = this.mc.playerController.getBlockReachDistance();
        final Vec3 dest = src.addVector(rotationVec.xCoord * reach, rotationVec.yCoord * reach, rotationVec.zCoord * reach);
        final MovingObjectPosition rayTraceResult = this.mc.theWorld.rayTraceBlocks(src, dest, false, false, true);
        if (rayTraceResult == null) return null;
        if (rayTraceResult.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return null;
        final BlockPos dstPos = data.pos;
        final BlockPos rayDstPos = rayTraceResult.getBlockPos();
        if (rayDstPos.getX() != dstPos.getX() ||
                rayDstPos.getY() != dstPos.getY() ||
                rayDstPos.getZ() != dstPos.getZ()) return null;
        if (rayTraceResult.sideHit != data.face) return null;
        return rayTraceResult.hitVec;
    }

    public static int findBestBlockStack(int start, int end) {
        int bestSlot = -1;
        int blockCount = -1;

        for (int i = end - 1; i >= start; --i) {
            ItemStack stack = InventoryUtils.getStackInSlot(i);

            if (stack != null &&
                    stack.getItem() instanceof ItemBlock &&
                    InventoryUtils.isGoodBlockStack(stack)) {
                if (stack.stackSize > blockCount) {
                    bestSlot = i;
                    blockCount = stack.stackSize;
                }
            }
        }

        return bestSlot;
    }

    public BlockPos getBlockUnder() {
        final EntityPlayerSP player = this.mc.thePlayer;
        final boolean useLastPos = this.keepYProperty.getValue() && !towering;
        final double playerPos = player.posY - 1.0;
        if (!useLastPos)
            lastPos = (int) player.posY;
        return new BlockPos(player.posX, useLastPos ? Math.min(lastPos, playerPos) : playerPos, player.posZ);
    }

    public float[] getRotations(final BlockData data) {
        final EntityPlayerSP player = this.mc.thePlayer;

        final Vec3 hitVec = data.hitVec;

        final double xDif = hitVec.xCoord - player.posX;
        final double zDif = hitVec.zCoord - player.posZ;

        final double yDif = hitVec.yCoord - (player.posY + player.getEyeHeight());
        final double xzDist = StrictMath.sqrt(xDif * xDif + zDif * zDif);

        return new float[]{
                (float) (StrictMath.atan2(zDif, xDif) * 180.0D / StrictMath.PI) - 90.0F,
                (float) (-(StrictMath.atan2(yDif, xzDist) * 180.0D / StrictMath.PI))
        };
    }

    public boolean validateBlockRange(final BlockData data) {
        final Vec3 pos = data.hitVec;
        if (pos == null)
            return false;
        final EntityPlayerSP player = this.mc.thePlayer;
        final double x = (pos.xCoord - player.posX);
        final double y = (pos.yCoord - (player.posY + player.getEyeHeight()));
        final double z = (pos.zCoord - player.posZ);
        final float reach = this.mc.playerController.getBlockReachDistance();
        return StrictMath.sqrt(x * x + y * y + z * z) <= reach;
    }

    public static boolean validateReplaceable(final BlockData data) {
        final BlockPos pos = data.pos.offset(data.face);
        final World world = Minecraft.getMinecraft().theWorld;
        return world.getBlockState(pos)
                .getBlock()
                .isReplaceable(world, pos);
    }

    public BlockData getBlockData(final BlockPos pos) {
        final BlockPos[] blockPositions = BLOCK_POSITIONS;
        final EnumFacing[] facings = FACINGS;
        final WorldClient world = Minecraft.getMinecraft().theWorld;

        // 1 of the 4 directions around player
        for (int i = 0; i < blockPositions.length; i++) {
            final BlockPos blockPos = pos.add(blockPositions[i]);
            if (InventoryUtils.isValidBlock(world.getBlockState(blockPos).getBlock(), false)) {
                final BlockData data = new BlockData(blockPos, facings[i]);
                if (validateBlockRange(data))
                    return data;
            }
        }

        // 2 Blocks Under e.g. When jumping
        final BlockPos posBelow = pos.add(0, -1, 0);
        if (InventoryUtils.isValidBlock(world.getBlockState(posBelow).getBlock(), false)) {
            final BlockData data = new BlockData(posBelow, EnumFacing.UP);
            if (validateBlockRange(data))
                return data;
        }

        // 2 Block extension & diagonal
        for (BlockPos blockPosition : blockPositions) {
            final BlockPos blockPos = pos.add(blockPosition);
            for (int i = 0; i < blockPositions.length; i++) {
                final BlockPos blockPos1 = blockPos.add(blockPositions[i]);
                if (InventoryUtils.isValidBlock(world.getBlockState(blockPos1).getBlock(), false)) {
                    final BlockData data = new BlockData(blockPos1, facings[i]);
                    if (validateBlockRange(data))
                        return data;
                }
            }
        }

        // 3 Block extension
        for (final BlockPos blockPosition : blockPositions) {
            final BlockPos blockPos = pos.add(blockPosition);
            for (final BlockPos position : blockPositions) {
                final BlockPos blockPos1 = blockPos.add(position);
                for (int i = 0; i < blockPositions.length; i++) {
                    final BlockPos blockPos2 = blockPos1.add(blockPositions[i]);
                    if (InventoryUtils.isValidBlock(world.getBlockState(blockPos2).getBlock(), false)) {
                        final BlockData data = new BlockData(blockPos2, facings[i]);
                        if (validateBlockRange(data))
                            return data;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void onEnable() {
        this.blockCount = 0;
        packets.clear();
        this.placeCounter = 0;
        this.ticksSincePlace = 0;
        this.lastPos = (int) this.mc.thePlayer.posY;
        this.originalHotBarSlot = this.mc.thePlayer.inventory.currentItem;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.angles = null;
        mc.timer.timerSpeed = 1.0f;
        packets.clear();
        this.mc.thePlayer.inventory.currentItem = this.originalHotBarSlot;
        super.onDisable();
    }

    public boolean isRotating() {
        return this.angles != null;
    }

    public void updateBlockCount() {
        this.blockCount = 0;

        for (int i = InventoryUtils.EXCLUDE_ARMOR_BEGIN; i < InventoryUtils.END; i++) {
            final ItemStack stack = InventoryUtils.getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof ItemBlock &&
                    InventoryUtils.isGoodBlockStack(stack))
                this.blockCount += stack.stackSize;
        }
    }

    public enum CancelSprintMode {
        SERVER,
        CLIENT,
        OFF
    }

    public static class BlockData {
        public final BlockPos pos;
        public final Vec3 hitVec;
        public final EnumFacing face;

        public BlockData(BlockPos pos, EnumFacing face) {
            this.pos = pos;
            this.face = face;
            this.hitVec = getHitVec();
        }

        public Vec3 getHitVec() {
            final Vec3i directionVec = this.face.getDirectionVec();
            final Minecraft mc = Minecraft.getMinecraft();

            double x;
            double z;

            final boolean negative = this.face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;

            final double absX = Math.abs(mc.thePlayer.posX);
            double xOffset = absX - (int) absX;

            final double absZ = Math.abs(mc.thePlayer.posZ);
            double zOffset = absZ - (int) absZ;

            switch (this.face.getAxis()) {
                case Z:
                    if (mc.thePlayer.posX < 0) {
                        xOffset = 1.0F - xOffset;
                    }

                    x = directionVec.getX() * xOffset;
                    z = directionVec.getZ() * xOffset;
                    break;
                case X:
                    if (mc.thePlayer.posZ < 0) {
                        zOffset = 1.0F - zOffset;
                    }

                    x = directionVec.getX() * zOffset;
                    z = directionVec.getZ() * zOffset;
                    break;
                default:
                    x = directionVec.getX() * 0.5;
                    z = directionVec.getX() * 0.5;
            }

            if (negative) {
                x = -x;
                z = -z;
            }

            final Vec3 hitVec = new Vec3(this.pos).addVector(x + z, directionVec.getY() * 0.5D, x + z);

            final Vec3 src = mc.thePlayer.getPositionEyes(1.0F);
            final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(src,
                    hitVec,
                    false,
                    false,
                    true);

            if (obj == null || obj.hitVec == null || obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
                return null;

            switch (this.face.getAxis()) {
                case Z:
                    obj.hitVec = new Vec3(obj.hitVec.xCoord, obj.hitVec.yCoord, Math.round(obj.hitVec.zCoord));
                    break;
                case X:
                    obj.hitVec = new Vec3(Math.round(obj.hitVec.xCoord), obj.hitVec.yCoord, obj.hitVec.zCoord);
                    break;
            }

            if (this.face != EnumFacing.DOWN && this.face != EnumFacing.UP) {
                final IBlockState blockState = mc.theWorld.getBlockState(obj.getBlockPos());
                final Block blockAtPos = blockState.getBlock();

                double blockFaceOffset;

                blockFaceOffset = RandomUtils.nextDouble(0.1, 0.3);

                if (blockAtPos instanceof BlockSlab && !((BlockSlab) blockAtPos).isDouble()) {
                    final BlockSlab.EnumBlockHalf half = blockState.getValue(BlockSlab.HALF);

                    if (half != BlockSlab.EnumBlockHalf.TOP) {
                        blockFaceOffset += 0.5;
                    }
                }

                obj.hitVec = obj.hitVec.addVector(0.0D, -blockFaceOffset, 0.0D);
            }

            return obj.hitVec;
        }
    }

}
