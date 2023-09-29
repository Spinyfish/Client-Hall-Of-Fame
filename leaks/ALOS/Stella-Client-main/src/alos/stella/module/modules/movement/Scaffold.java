package alos.stella.module.modules.movement;

import alos.stella.event.EventState;
import alos.stella.event.EventTarget;
import alos.stella.event.events.MotionEvent;
import alos.stella.event.events.PacketEvent;
import alos.stella.event.events.Render2DEvent;
import alos.stella.event.events.UpdateEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.ui.client.font.Fonts;
import alos.stella.utils.MovementUtils;
import alos.stella.utils.RotationUtils;
import alos.stella.utils.ColorUtils;
import alos.stella.utils.timer.TimerUtils;
import alos.stella.value.BoolValue;
import alos.stella.value.IntegerValue;
import alos.stella.value.ListValue;
import net.minecraft.block.*;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "Scaffold", description = "Autoplace", category = ModuleCategory.MOVEMENT)
public class Scaffold extends Module {
    public final ListValue modeValue = new ListValue("RotationMode", new String[]{"Auto", "Custom"}, "Auto");

    private final IntegerValue customYaw = new IntegerValue("Yaw", 180, 0, 180);
    private final IntegerValue customPitch = new IntegerValue("Pitch", 90, -90, 90);
    private final ListValue sprintModeValue = new ListValue("SprintMode", new String[]{
            "Vanilla", "NoPacket", "None"
    }, "Vanilla");
    private final BoolValue swingValue = new BoolValue("Swing", false);
    private final BoolValue towerEnabled = new BoolValue("Tower", false);
    private final ListValue towerModeValue = new ListValue("TowerMode", new String[]{
            "Hypixel", "NCP"
    }, "Hypixel", () -> towerEnabled.get());

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
    private BlockData blockData;

    private int slot, towerTick;

    private int towerTicks;

    private final TimerUtils timer = new TimerUtils();

    float rot = 0.0f;


    public void onEnable() {
        posY = MathHelper.floor_double(mc.thePlayer.posY);
    }


    public void onDisable() {
        mc.timer.timerSpeed = 1;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (getAllBlockCount() <= 0)
            return;

        posY = mc.thePlayer.posY;


        if (this.getBlockCountHotBar() <= 0) {
            final int spoofSlot = this.getBestSpoofSlot();
            this.getBlock(spoofSlot);
        }

        this.blockData = ((this
                .getBlockData(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)) == null)
                ? this.getBlockData(
                new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ).down(1))
                : this.getBlockData(
                new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)));
        this.slot = getBlockSlot();
        mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack();

        if (this.blockData == null || this.slot == -1 || this.getBlockCountHotBar() <= 0
                || (MovementUtils.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown())) {
            return;
        }
        if (mc.thePlayer.onGround) {
            towerTicks = 0;
        }
    }

    @EventTarget
    public void post(MotionEvent e) {
            mc.thePlayer.rotationYawHead = e.getYaw();
            mc.thePlayer.renderYawOffset = e.getYaw();
            mc.thePlayer.rotationPitchHead = e.getPitch();
            if (slot == -1) {
                timer.reset();
                return;
            }

            switch (sprintModeValue.get()) {
                case "Vanilla":
                case "NoPacket": {
                    mc.thePlayer.setSprinting(true);
                    break;
                }
                case "None": {
                    mc.thePlayer.setSprinting(false);
                    break;
                }
            }

            if (towerMoving()) {
                mc.thePlayer.setSprinting(false);
                mc.thePlayer.cameraYaw = 0;
                mc.thePlayer.cameraPitch = 0;
                switch (towerModeValue.get().toLowerCase()) {
                    case "ncp": {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.isAirBorne = true;
                            mc.thePlayer.triggerAchievement(StatList.jumpStat);
                            towerTicks = 0;
                        }
                        int IntPosY = (int) mc.thePlayer.posY;
                        if (mc.thePlayer.posY - IntPosY < 0.05) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, IntPosY, mc.thePlayer.posZ);
                            mc.thePlayer.motionY = 0.42;
                            towerTicks = 1;
                        } else if (towerTicks == 1) {
                            mc.thePlayer.motionY = 0.34;
                            towerTicks++;
                        } else if (towerTicks == 2) {
                            mc.thePlayer.motionY = 0.25;
                            towerTicks++;
                        }
                        break;
                    }
                    case "hypixel": {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.isAirBorne = true;
                            mc.thePlayer.triggerAchievement(StatList.jumpStat);
                            mc.thePlayer.motionY = 0.42;
                        } else if (mc.thePlayer.motionY < 0.23) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                            mc.thePlayer.onGround = true;
                            mc.thePlayer.motionY = 0.42;
                        }
                        break;
                    }
                }
            }

            final int last = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.inventory.currentItem = this.slot;
            if (this.blockData != null) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(),
                        this.blockData.position, this.blockData.facing,
                        getVec3(this.blockData.position, this.blockData.facing))) {
                    if (mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem).getItem() != null
                            && mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem)
                            .getItem() instanceof ItemBlock
                            && !mc.isSingleplayer()) {
                        mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem).getItem();
                    }
                }
            }
            mc.thePlayer.inventory.currentItem = last;

            switch (modeValue.get()) {
                case "Custom": {
                    if (mc.thePlayer.movementInput.moveForward == 0.0f) {
                        rot = customYaw.get();
                    }
                    if (mc.thePlayer.movementInput.moveForward > 0.0f) {
                        rot = customYaw.get();
                    }
                    if (mc.thePlayer.movementInput.moveForward < 0.0f) {
                        rot = -customYaw.get();
                    }
                    if (mc.thePlayer.movementInput.moveForward > 0.0 && mc.thePlayer.movementInput.moveStrafe > 0.0) {
                        rot = customYaw.get() + 45;
                    }
                    if (mc.thePlayer.movementInput.moveForward > 0.0 && mc.thePlayer.movementInput.moveStrafe < 0.0) {
                        rot = customYaw.get() - 45;
                    }
                    if (mc.thePlayer.movementInput.moveForward < 0.0 && mc.thePlayer.movementInput.moveStrafe > 0.0) {
                        rot = customYaw.get() + 135;
                    }
                    if (mc.thePlayer.movementInput.moveForward < 0.0 && mc.thePlayer.movementInput.moveStrafe < 0.0) {
                        rot = customYaw.get() - 135;
                    }

                    e.setYaw(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - rot);
                    e.setPitch(customPitch.get());

                    if (e.getYaw() != 999.0f) {
                        e.setYaw(e.getYaw());
                    }
                    if (e.getPitch() != 999.0f) {
                        e.setPitch(e.getPitch());

                    }
                    break;
                }
                case "Auto": {
                    if (blockData != null) {
                        EntityPig entity = new EntityPig(mc.theWorld);
                        entity.posX = blockData.position.getX() + 0.5;
                        entity.posY = blockData.position.getY() + 0.5;
                        entity.posZ = blockData.position.getZ() + 0.5;
                        float[] rots = RotationUtils.getAngles(entity);
                        e.yaw = rots[0];
                        e.pitch = rots[1];
                    }
                }
                break;
            }
        if (e.getEventState() == EventState.PRE) {
            if (swingValue.get()) {
                mc.thePlayer.swingItem();
            } else {
                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
            }
        }
    }
    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        int infoWidth2 = Fonts.minecraftFont.getStringWidth(getAllBlockCount() + "");
        int c = ColorUtils.getColor(255, 0, 0, 150);
        if (getAllBlockCount() >= 64 && 128 > getAllBlockCount()) {
            c = ColorUtils.getColor(255, 255, 0, 150);
        } else if (getAllBlockCount() >= 128) {
            c = ColorUtils.getColor(0, 255, 0, 150);
        }
        Fonts.minecraftFont.drawString(getAllBlockCount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2) - 1, scaledResolution.getScaledHeight() / 2 - 36, 0xff000000, false);
        Fonts.minecraftFont.drawString(getAllBlockCount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2) + 1, scaledResolution.getScaledHeight() / 2 - 36, 0xff000000, false);
        Fonts.minecraftFont.drawString(getAllBlockCount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 35, 0xff000000, false);
        Fonts.minecraftFont.drawString(getAllBlockCount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 37, 0xff000000, false);
        Fonts.minecraftFont.drawString(getAllBlockCount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 36, c, false);

    }

    private boolean towerMoving() {
        return towerEnabled.get() && Keyboard.isKeyDown(Keyboard.KEY_SPACE);
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

        private static BlockPos position = null;
        private EnumFacing facing;

        public BlockData(BlockPos position, EnumFacing facing) {
            this.position = position;
            this.facing = facing;
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (mc.thePlayer == null)
            return;

        final Packet<?> packet = event.getPacket();

        // Sprint
        if (sprintModeValue.get().equalsIgnoreCase("silent")) {
            if (packet instanceof C0BPacketEntityAction &&
                    (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING || ((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING))
                event.cancelEvent();
        }
    }
    public float getYawBackward() {
        float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);

        MovementInput input = mc.thePlayer.movementInput;
        float strafe = input.moveStrafe, forward = input.moveForward;

        if (forward != 0) {
            if (strafe < 0) {
                yaw += forward < 0 ? 135 : 45;
            } else if (strafe > 0) {
                yaw -= forward < 0 ? 135 : 45;
            } else if (strafe == 0 && forward < 0) {
                yaw -= 180;
            }

        } else {
            if (strafe < 0) {
                yaw += 90;
            } else if (strafe > 0) {
                yaw -= 90;
            }
        }

        return MathHelper.wrapAngleTo180_float(yaw - 180);
    }

}
