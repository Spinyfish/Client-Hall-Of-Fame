package alos.stella.module.modules.movement;

import alos.stella.event.EventTarget;
import alos.stella.event.events.PacketEvent;
import alos.stella.event.events.UpdateEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.utils.MovementUtils;
import alos.stella.utils.timer.TimerUtils;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.*;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "PikaFly", description = "PikaFly", category = ModuleCategory.MOVEMENT)
public class PikaFly extends Module {
    private final List<Block> blacklisted = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava,
            Blocks.flowing_lava, Blocks.enchanting_table, Blocks.ender_chest, Blocks.sand, Blocks.tnt,
            Blocks.yellow_flower, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars,
            Blocks.crafting_table, Blocks.snow_layer, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore,
            Blocks.emerald_ore, Blocks.chest, Blocks.torch, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2,
            Blocks.double_stone_slab2, Blocks.double_wooden_slab, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock,
            Blocks.gold_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.redstone_ore,
            Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate,
            Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.cactus,
            Blocks.oak_stairs, Blocks.stone_brick_stairs, Blocks.nether_brick_stairs, Blocks.stone_stairs,
            Blocks.brick_stairs, Blocks.sandstone_stairs, Blocks.lever, Blocks.activator_rail, Blocks.rail,
            Blocks.spruce_stairs, Blocks.detector_rail, Blocks.golden_rail, Blocks.furnace, Blocks.ladder,
            Blocks.acacia_stairs, Blocks.double_stone_slab2, Blocks.dark_oak_stairs, Blocks.birch_stairs,
            Blocks.jungle_stairs, Blocks.quartz_stairs, Blocks.oak_fence, Blocks.redstone_torch, Blocks.iron_trapdoor,
            Blocks.trapdoor, Blocks.tripwire_hook, Blocks.hopper, Blocks.acacia_fence_gate, Blocks.birch_fence_gate,
            Blocks.dark_oak_fence_gate, Blocks.jungle_fence_gate, Blocks.spruce_fence_gate, Blocks.oak_fence_gate,
            Blocks.dispenser, Blocks.sapling, Blocks.tallgrass, Blocks.deadbush, Blocks.web, Blocks.red_flower,
            Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.nether_brick_fence, Blocks.vine, Blocks.double_plant,
            Blocks.flower_pot, Blocks.beacon, Blocks.pumpkin, Blocks.lit_pumpkin);

    private double posY;
    private BlockData data;

    private int slot, towerTick;

    private int towerTicks;

    private final TimerUtils timer = new TimerUtils();

    private float
            pitch;

    private float yaw;

    float rot = 0.0f;


    public void onEnable() {
        posY = MathHelper.floor_double(mc.thePlayer.posY);
    }


    public void onDisable() {
        mc.timer.timerSpeed = 1;
        mc.thePlayer.capabilities.isFlying = false;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.thePlayer.motionY -= 0.42;
        MovementUtils.strafe(0.2f);;
        if (getAllBlockCount() <= 0)
            return;

        posY = mc.thePlayer.posY;


        if (this.getBlockCountHotBar() <= 0) {
            final int spoofSlot = this.getBestSpoofSlot();
            this.getBlock(spoofSlot);
        }

        this.data = ((this
                .getBlockData(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)) == null)
                ? this.getBlockData(
                new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ).up(1))
                : this.getBlockData(
                new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.0, mc.thePlayer.posZ)));
        this.slot = getBlockSlot();
        mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack();

        if (this.data == null || this.slot == -1 || this.getBlockCountHotBar() <= 0
                || (MovementUtils.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown())) {
            return;
        }
        if (mc.thePlayer.onGround) {
            towerTicks = 0;
        }
    }

    @EventTarget
    public void post(UpdateEvent e) {
        if (slot == -1) {
            timer.reset();
            return;
        }

        final int last = mc.thePlayer.inventory.currentItem;
        mc.thePlayer.inventory.currentItem = this.slot;
        if (this.data != null) {
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(),
                    this.data.getBlockPos(), this.data.getEnumFacing(),
                    getVec3(this.data.getBlockPos(), this.data.getEnumFacing()))) {
                if (mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem).getItem() != null
                        && mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem)
                        .getItem() instanceof ItemBlock
                        && !mc.isSingleplayer()) {
                    mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem).getItem();
                }
                    mc.thePlayer.swingItem();
                }
            }
        mc.thePlayer.inventory.currentItem = last;

        if (mc.thePlayer.movementInput.moveForward > 0.0f) {
            rot = 180.0f;
        }
        if (mc.thePlayer.movementInput.moveStrafe > 0.0f) {
            rot = -120.0f;
        }
        if (mc.thePlayer.movementInput.moveStrafe < 0.0f) {
            rot = 120.0f;
        }
        if (mc.thePlayer.movementInput.moveForward == 0.0f) {
            rot = 180.0f;
        }
        if (mc.thePlayer.movementInput.moveStrafe > 0.0f) {
            rot = -90.0f;
        }
        if (mc.thePlayer.movementInput.moveStrafe < 0.0f) {
            rot = 90.0f;
        }
        if (mc.thePlayer.movementInput.moveStrafe > 0.0f) {
            rot = -45.0f;
        }
        if (mc.thePlayer.movementInput.moveStrafe < 0.0f) {
            rot = 45.0f;
        }

        setYaw(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - rot);
        setPitch(-80.5f);

        if (getYaw() != 999.0f) {
            setYaw(getYaw());
        }
        if (getPitch() != 999.0f) {
            setPitch(getPitch());
        }

    }
    public static double randomNumber(final double max, final double min) {
        return Math.random() * (max - min) + min;
    }

    public static Vec3 getVec3(final BlockPos pos, final EnumFacing face) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += randomNumber(0.3, -0.3);
            z += randomNumber(0.3, -0.3);
        } else {
            y += randomNumber(0.3, -0.3);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += randomNumber(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += randomNumber(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }

    public boolean isBlockAccessible(Block paramBlock) {
        if (paramBlock.getMaterial().isReplaceable()) {
            return ((!(paramBlock instanceof BlockSnow))) || (!(paramBlock.getBlockBoundsMaxY() > 0.125D));
        }
        return false;
    }

    private boolean isValid(Item item) {
        if (!(item instanceof ItemBlock)) {
            return false;
        } else {
            ItemBlock iBlock = (ItemBlock) item;
            Block block = iBlock.getBlock();
            return !blacklisted.contains(block);
        }
    }

    public int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack()
                    && mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemBlock) {
                return i;
            }
        }
        return -1;
    }

    public int getAllBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; i++) {
            blockCount += (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                    && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock)
                    ? mc.thePlayer.inventoryContainer.getSlot(i).getStack().stackSize
                    : 0;
        }
        return blockCount;
    }

    public int getHotBarBlockCount() {
        int blockCount = 0;
        for (int i = 36; i < 45; i++) {
            blockCount += (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                    && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock)
                    ? mc.thePlayer.inventoryContainer.getSlot(i).getStack().stackSize
                    : 0;
        }
        return blockCount;
    }

    public void swap(int currentSlot, int targetSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, currentSlot, targetSlot, 2,
                mc.thePlayer);
    }

    void getBlock(final int hotbarSlot) {
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                    && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemBlock) {
                    final ItemBlock block = (ItemBlock) is.getItem();
                    if (this.isValid((Item) block)) {
                        if (36 + hotbarSlot != i) {
                            this.swap(i, hotbarSlot);
                            break;
                        }
                        break;
                    }
                }
            }
        }
    }

    private int getBlockCountHotBar() {
        int blockCount = 0;

        for (int i = 36; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();

                if (is.getItem() instanceof ItemBlock && !this.blacklisted.contains(((ItemBlock) item).getBlock())) {
                    blockCount += is.stackSize;
                }
            }
        }

        return blockCount;
    }

    int getBestSpoofSlot() {
        int spoofSlot = 5;
        for (int i = 36; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                spoofSlot = i - 36;
                break;
            }
        }
        return spoofSlot;
    }

    private BlockData getBlockData(BlockPos pos) {
        if (this.isPosValid(pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos2 = pos.add(-1, 0, 0);
        if (this.isPosValid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos3 = pos.add(1, 0, 0);
        if (this.isPosValid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos4 = pos.add(0, 0, 1);
        if (this.isPosValid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos5 = pos.add(0, 0, -1);
        if (this.isPosValid(pos5.add(0, -1, 0))) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos5.add(-1, 0, 0))) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos5.add(1, 0, 0))) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos5.add(0, 0, 1))) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos5.add(0, 0, -1))) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(-2, 0, 0);
        if (this.isPosValid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(2, 0, 0);
        if (this.isPosValid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(0, 0, 2);
        if (this.isPosValid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(0, 0, -2);
        if (this.isPosValid(pos5.add(0, -1, 0))) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos5.add(-1, 0, 0))) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos5.add(1, 0, 0))) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos5.add(0, 0, 1))) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos5.add(0, 0, -1))) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos6 = pos.add(0, -1, 0);
        if (this.isPosValid(pos6.add(0, -1, 0))) {
            return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos6.add(-1, 0, 0))) {
            return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos6.add(1, 0, 0))) {
            return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos6.add(0, 0, 1))) {
            return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos6.add(0, 0, -1))) {
            return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos7 = pos6.add(1, 0, 0);
        if (this.isPosValid(pos7.add(0, -1, 0))) {
            return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos7.add(-1, 0, 0))) {
            return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos7.add(1, 0, 0))) {
            return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos7.add(0, 0, 1))) {
            return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos7.add(0, 0, -1))) {
            return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos8 = pos6.add(-1, 0, 0);
        if (this.isPosValid(pos8.add(0, -1, 0))) {
            return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos8.add(-1, 0, 0))) {
            return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos8.add(1, 0, 0))) {
            return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos8.add(0, 0, 1))) {
            return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos8.add(0, 0, -1))) {
            return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos9 = pos6.add(0, 0, 1);
        if (this.isPosValid(pos9.add(0, -1, 0))) {
            return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos9.add(-1, 0, 0))) {
            return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos9.add(1, 0, 0))) {
            return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos9.add(0, 0, 1))) {
            return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos9.add(0, 0, -1))) {
            return new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos10 = pos6.add(0, 0, -1);
        if (this.isPosValid(pos10.add(0, -1, 0))) {
            return new BlockData(pos10.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosValid(pos10.add(-1, 0, 0))) {
            return new BlockData(pos10.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosValid(pos10.add(1, 0, 0))) {
            return new BlockData(pos10.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosValid(pos10.add(0, 0, 1))) {
            return new BlockData(pos10.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosValid(pos10.add(0, 0, -1))) {
            return new BlockData(pos10.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    private boolean isPosValid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isVisuallyOpaque()
                || block instanceof BlockLadder || block instanceof BlockCarpet || block instanceof BlockSnow
                || block instanceof BlockSkull) && !block.getMaterial().isLiquid()
                && !(block instanceof BlockContainer);
    }

    private static class BlockData {
        private Vec3 vec;
        private final BlockPos pos;
        private final EnumFacing facing;

        public BlockData(BlockPos pos, EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
            this.vec = getHitVec();
        }

        private Vec3 getHitVec() {
            Vec3i directionVec = facing.getDirectionVec();
            double x = directionVec.getX() * 0.5D;
            double z = directionVec.getZ() * 0.5D;

            if (facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
                x = -x;
                z = -z;
            }

            Vec3 hitVec = (new Vec3(this.pos)).addVector(x + z, directionVec.getY() * 0.5D, x + z);

            Vec3 src = Minecraft.getMinecraft().thePlayer.getPositionEyes(1.0F);
            MovingObjectPosition obj = Minecraft.getMinecraft().theWorld.rayTraceBlocks(src, hitVec, false, false,
                    true);

            if (obj == null || obj.hitVec == null || obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
                return null;
            }
            if (facing != EnumFacing.DOWN && facing != EnumFacing.UP) {
                obj.hitVec = obj.hitVec.addVector(0.0D, -0.2D, 0.0D);
            }
            return obj.hitVec;
        }

        public Vec3 getVec() {
            return vec;
        }

        public void setVec(Vec3 vec) {
            this.vec = vec;
        }

        public BlockPos getBlockPos() {
            return pos;
        }

        public EnumFacing getEnumFacing() {
            return facing;
        }
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (mc.thePlayer == null)
            return;

        final Packet<?> packet = event.getPacket();

    }
}
