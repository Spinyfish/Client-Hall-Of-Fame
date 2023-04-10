package crispy.features.hacks.impl.movement;

import arithmo.gui.altmanager.Colors;
import crispy.features.event.Event;
import crispy.features.event.impl.movement.EventStrafe;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.event.impl.render.EventRenderGui;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.combat.Aura;
import crispy.fonts.decentfont.FontUtil;
import crispy.util.player.PlayerUtil;
import crispy.util.render.gui.RenderUtil;
import crispy.util.rotation.LookUtils;
import crispy.util.rotation.Rotation;
import crispy.util.time.TimeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.Timer;
import net.minecraft.util.*;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@HackInfo(name = "BlockFly", category = Category.MOVEMENT)
public class Scaffold extends Hack {
    public static float sYaw;
    public static float sPitch;
    public static Rotation scaffoldRot = new Rotation(999, 999);
    public static boolean isPlaceTick;
    private final TimeHelper boost = new TimeHelper();
    private final TimeHelper placeTimer = new TimeHelper();
    private final List<Block> noobBlocks;
    private final TimeHelper scaffoldTimer = new TimeHelper();
    private final TimeHelper zitterTimer = new TimeHelper();
    ModeValue rotations = new ModeValue("Rotations", "Always", "Always", "Place", "Static", "GodBridge", "None");
    ModeValue tower = new ModeValue("Tower Mode", "None", "NCP", "None");
    ModeValue blockOverlay = new ModeValue("Block Overlay", "None", "Text", "Crispy v2", "None");
    NumberValue<Long> placeDelay = new NumberValue<Long>("Place Delay", 0L, 0L, 1000L);
    BooleanValue watchdogold = new BooleanValue("Watchdog Old", false);

    BooleanValue down = new BooleanValue("Downwards", false);
    BooleanValue swing = new BooleanValue("Swing", true);
    BooleanValue spoof = new BooleanValue("Spoof", false);
    BooleanValue strafe = new BooleanValue("Move Fix", true);
    BooleanValue timer = new BooleanValue("Timer Boost", false);
    BooleanValue zitter = new BooleanValue("Jitter", false);
    BooleanValue sprint = new BooleanValue("Sprint", true);
    BooleanValue keepY = new BooleanValue("Keep Y", false);
    BooleanValue autoJump = new BooleanValue("Auto Jump", false);
    public BooleanValue disableAura = new BooleanValue("Disable Aura", true);
    NumberValue<Float> timerSpeed = new NumberValue<Float>("Timer Speed", 1.5f, 1.5f, 10f, timer::getObject);
    private BlockData blockData;
    private boolean zitterDirection;
    private double posY;


    public Scaffold() {
        this.noobBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence);
    }

    public static int getEmptyHotbarSlot() {
        for (int k = 0; k < 9; ++k) {
            if (Minecraft.getMinecraft().thePlayer.inventory.mainInventory[k] == null) {
                return k;
            }
        }
        return -1;
    }

    @Override
    public void onEnable() {
        boost.reset();
        if (this.mc.thePlayer != null) {
            zitterTimer.reset();

            if (timer.getObject()) {
                Timer.timerSpeed = timerSpeed.getObject();
            }
        } else {
            this.toggle();
        }
        sYaw = 999;
        posY = mc.thePlayer.posY;
        sPitch = 999;
        scaffoldTimer.reset();
        if (autoJump.getObject()) {
            mc.gameSettings.keyBindJump.pressed = true;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (autoJump.getObject()) {
            mc.gameSettings.keyBindJump.pressed = false;
        }
        isPlaceTick = false;
        sYaw = 999;
        Timer.timerSpeed = 1;
        scaffoldTimer.reset();
        sPitch = 999;
        zitterTimer.reset();
        scaffoldRot = new Rotation(999, 999);
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventPacket) {

            Packet packet = ((EventPacket) e).getPacket();
            if (watchdogold.getObject()) {
                if (packet instanceof C0FPacketConfirmTransaction) {
                    e.setCancelled(true);
                }
                if (packet instanceof C13PacketPlayerAbilities) {
                    e.setCancelled(true);
                }
                if (packet instanceof C00PacketKeepAlive) {
                    e.setCancelled(true);
                }
            }
        }
        if (e instanceof EventUpdate) {
            if (autoJump.getObject()) {
                mc.gameSettings.keyBindJump.pressed = PlayerUtil.isMoving2();
            }
            if (!keepY.getObject()) {
                posY = mc.thePlayer.posY;
            } else {
                if (mc.thePlayer.posY - posY < 0 || !PlayerUtil.isMoving2()) {
                    posY = mc.thePlayer.posY;
                }
            }
            if (boost.hasReached(1500)) {
                Timer.timerSpeed = 1;
            }
            EventUpdate eventUpdate = (EventUpdate) e;
            if (!strafe.getObject()) {
                update(eventUpdate.isPre());
            } else if (sPitch != 999 && sYaw != 999) {
                mc.thePlayer.setSprinting(false);
                mc.gameSettings.keyBindSprint.pressed = false;
            }
            if (!sprint.getObject()) {
                mc.thePlayer.setSprinting(false);
                mc.gameSettings.keyBindSprint.pressed = false;
            }
            if (zitter.getObject()) {
                if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) {
                    mc.gameSettings.keyBindRight.pressed = false;
                }
                if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) {
                    mc.gameSettings.keyBindLeft.pressed = false;
                }
                if (zitterTimer.hasReached(100)) {
                    zitterDirection = !zitterDirection;
                    zitterTimer.reset();
                }
                if (zitterDirection) {
                    mc.gameSettings.keyBindRight.pressed = true;
                    mc.gameSettings.keyBindLeft.pressed = false;
                } else {
                    mc.gameSettings.keyBindRight.pressed = false;
                    mc.gameSettings.keyBindLeft.pressed = true;
                }
            }

        } else if (e instanceof EventStrafe) {
            if (strafe.getObject()) {
                EventStrafe eventStrafe = (EventStrafe) e;
                update(eventStrafe.isPre());
                if (scaffoldRot.getYaw() == 999 && scaffoldRot.getPitch() == 999)
                    return;
                if (Aura.target != null)
                    return;
                e.setCancelled(true);

                LookUtils.applyStrafeToPlayer(eventStrafe, scaffoldRot.getYaw());

            }
        } else if (e instanceof EventRenderGui) {
            switch (blockOverlay.getMode()) {
                case "Text": {
                    ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                    int blockCount = getBlockCount();
                    int color = Colors.getColor(255, 0, 0);
                    if (blockCount >= 64 && 128 > blockCount) {
                        color = Colors.getColor(255, 255, 0);
                    } else if (blockCount >= 128) {
                        color = Colors.getColor(0, 255, 0);
                    }
                    if (blockCount == 0) {
                        sYaw = 999;
                        sPitch = 999;
                    }
                    FontUtil.cleanSmall.drawString(blockCount + " blocks", ScaledResolution.getScaledWidth() / 2 + 17 - FontUtil.cleanSmall.getStringWidth(blockCount + "") / 2, ScaledResolution.getScaledHeight() / 2 - 5, Colors.getColor(237, 234, 231));
                    break;
                }
                case "Crispy v2": {


                    FontUtil.cleanSmall.drawString(getBlockCount() + " blocks", ScaledResolution.getScaledWidth() / 2 + 17 - FontUtil.cleanSmall.getStringWidth(getBlockCount() + "") / 2, ScaledResolution.getScaledHeight() / 2 - 3, Colors.getColor(237, 234, 231));

                    Gui.drawRect(ScaledResolution.getScaledWidth() / 2 + 17 - FontUtil.cleanSmall.getStringWidth(getBlockCount() + "") / 2 - 2, ScaledResolution.getScaledHeight() / 2 - 8, ScaledResolution.getScaledWidth() / 2 + 17 + FontUtil.cleanSmall.getStringWidth("blocks " + getBlockCount()), ScaledResolution.getScaledHeight() / 2 + 6, new Color(0, 0, 0, 100).getRGB());
                    RenderUtil.drawLine(ScaledResolution.getScaledWidth() / 2 + 17 - FontUtil.cleanSmall.getStringWidth(getBlockCount() + "") / 2 - 2, ScaledResolution.getScaledHeight() / 2 - 8, ScaledResolution.getScaledWidth() / 2 + 17 + FontUtil.cleanSmall.getStringWidth("blocks " + getBlockCount()), ScaledResolution.getScaledHeight() / 2 - 8, 3f, getStaticRainbow(3000, 1));
                    break;
                }
            }

        }
    }

    public void update(boolean isPre) {

        this.setDisplayName(getName() + " \2477" + rotations.getModes()[rotations.getObject()]);
        final int slot = this.getSlot();

        this.blockData = this.getBlockData();
        if (blockData == null) {
            sYaw = 999;
            sPitch = 999;
        }
        if (this.blockData == null) {
            return;
        }


        if (sYaw != 999 && sPitch != 999) {
            scaffoldRot.setYaw(sYaw);
            scaffoldRot.setPitch(sPitch);
        }
        if (rotations.getMode().equalsIgnoreCase("Always")) {
            float[] rotations = getRotations(blockData.blockPos, blockData.enumFacing);
            float[] fixedRot = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, rotations[0], rotations[1]);

            scaffoldRot.setYaw(fixedRot[0]);
            scaffoldRot.setPitch(fixedRot[1]);
        } else if (rotations.getMode().equalsIgnoreCase("GodBridge")) {
            float yaw = scaffoldRot.getYaw();
            if (mc.gameSettings.keyBindForward.pressed) {
                yaw = mc.thePlayer.rotationYaw - 180;
            } else if (mc.gameSettings.keyBindRight.pressed) {
                yaw = mc.thePlayer.rotationYaw - 90;
            } else if (mc.gameSettings.keyBindLeft.pressed) {
                yaw = mc.thePlayer.rotationYaw + 90;
            } else if (mc.gameSettings.keyBindBack.pressed) {
                yaw = mc.thePlayer.rotationYaw;
            } else if (mc.gameSettings.keyBindJump.pressed) {
                yaw = mc.thePlayer.rotationYaw;
            }
            final float targetYaw = MathHelper.wrapAngleTo180_float(scaffoldRot.getYaw() - yaw);
            float yawFactor = targetYaw / 3;
            yaw += yawFactor;

            scaffoldRot.setYaw(yaw);
            scaffoldRot.setPitch((float) (83.7 + Math.random()));


        }
        if (isPre) {
            if (slot == -1) {
                this.moveBlocksToHotbar();
            }


        } else if (slot != -1 && this.blockData != null) {


            if (placeTimer.hasReached(placeDelay.getObject())) {
                if (mc.gameSettings.keyBindJump.pressed && !PlayerUtil.isMoving2() && !tower.getMode().equalsIgnoreCase("None")) {
                    BlockPos underPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
                    Block underBlock = Minecraft.theWorld.getBlockState(underPos).getBlock();
                    mc.thePlayer.onGround = false;
                    if (!PlayerUtil.isMoving2()) {
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;

                    }
                    if (underBlock.getMaterial() == Material.air) {
                        mc.thePlayer.motionY = 0.4196;
                        mc.thePlayer.motionX *= 0.75;
                        mc.thePlayer.motionZ *= 0.75;

                    }

                }

                placeTimer.reset();

                final int currentSlot = this.mc.thePlayer.inventory.currentItem;
                this.mc.thePlayer.inventory.currentItem = slot;

                if (this.getPlaceBlock(this.blockData.getPosition(), this.blockData.getFacing())) {


                    float pitch = scaffoldRot.getPitch();
                    float yaw = scaffoldRot.getYaw();
                    switch (rotations.getMode()) {
                        case "Static": {
                            yaw = 0.0f;
                            pitch = 90;
                            if (this.blockData.getFacing().getName().equalsIgnoreCase("north")) {
                                yaw = 0.0f;
                            }
                            if (this.blockData.getFacing().getName().equalsIgnoreCase("south")) {
                                yaw = 180.0f;
                            }
                            if (this.blockData.getFacing().getName().equalsIgnoreCase("west")) {
                                yaw = -90.0f;
                            }
                            if (this.blockData.getFacing().getName().equalsIgnoreCase("east")) {
                                yaw = 90.0f;
                            }
                            break;
                        }
                        case "Place": {
                            float[] rotations = getRotations(blockData.blockPos, blockData.enumFacing);
                            float[] fixedRot = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, rotations[0], rotations[1]);
                            scaffoldRot.setYaw(fixedRot[0]);
                            scaffoldRot.setPitch(fixedRot[1]);
                            break;
                        }
                    }
                    if (!rotations.getMode().equalsIgnoreCase("Place") && !rotations.getMode().equalsIgnoreCase("None") && !rotations.getMode().equalsIgnoreCase("GodBridge")) {
                        sYaw = yaw;
                        sPitch = pitch;
                    }
                    if (spoof.getObject()) {
                        this.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentSlot));
                    }
                }
                if (spoof.getObject()) {
                    this.mc.thePlayer.inventory.currentItem = currentSlot;
                }
            }
        }

    }

    private boolean getPlaceBlock(final BlockPos pos, final EnumFacing facing) {
        final Vec3 eyesPos = new Vec3(this.mc().thePlayer.posX, this.mc().thePlayer.posY + this.mc().thePlayer.getEyeHeight(), this.mc().thePlayer.posZ);
        final Vec3i data = this.blockData.getFacing().getDirectionVec();
        if (Minecraft.theWorld.isAirBlock(new BlockPos(data.getX(), data.getY(), data.getZ())) && this.mc().playerController.onPlayerRightClick(this.mc().thePlayer, Minecraft.theWorld, this.mc().thePlayer.getHeldItem(), pos, facing, new Vec3(this.blockData.getPosition().getX() + data.getX() * 0.5, this.blockData.getPosition().getY() + data.getY() * 0.5, this.blockData.getPosition().getZ() + data.getZ() * 0.5))) {
            if (swing.getObject()) {
                this.mc().thePlayer.swingItem();
            } else {
                this.mc().thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            }
            return true;
        }

        return false;
    }

    public float[] getRotations(BlockPos block, EnumFacing face) {
        double x = block.getX() + 0.5 - mc().thePlayer.posX + (double) face.getFrontOffsetX() / 2;
        double z = block.getZ() + 0.5 - mc().thePlayer.posZ + (double) face.getFrontOffsetZ() / 2;
        double y = (block.getY() + 0.5);
        double d1 = mc().thePlayer.posY + mc().thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0D / Math.PI);
        if (yaw < 0.0F) {
            yaw += 360f;
        }
        return new float[]{yaw, pitch};
    }

    private BlockData getBlockData() {
        final EnumFacing[] invert = {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
        double yValue = 0.0;
        if (Keyboard.isKeyDown(this.mc().gameSettings.keyBindSneak.getKeyCode()) && !this.mc().gameSettings.keyBindJump.isPressed() && down.getObject() && this.mc().thePlayer.onGround) {
            KeyBinding.setKeyBindState(this.mc().gameSettings.keyBindSneak.getKeyCode(), false);
            --yValue;
        }
        BlockPos playerpos = keepY.getObject() ? new BlockPos(mc.thePlayer.posX, posY, mc.thePlayer.posZ).offset(EnumFacing.DOWN).add(0.0, yValue, 0.0) : new BlockPos(this.mc().thePlayer.getPositionVector()).offset(EnumFacing.DOWN).add(0.0, yValue, 0.0);

        final List<EnumFacing> facingVals = Arrays.asList(EnumFacing.values());
        for (int i = 0; i < facingVals.size(); ++i) {
            if (Minecraft.theWorld.getBlockState(playerpos.offset(facingVals.get(i))).getBlock().getMaterial() != Material.air) {
                return new BlockData(playerpos.offset(facingVals.get(i)), invert[facingVals.get(i).ordinal()]);
            }
        }
        final BlockPos[] addons = {new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1)};
        for (int length2 = addons.length, j = 0; j < length2; ++j) {
            final BlockPos offsetPos = playerpos.add(addons[j].getX(), 0, addons[j].getZ());
            if (Minecraft.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir) {
                for (int k = 0; k < EnumFacing.values().length; ++k) {
                    if (Minecraft.theWorld.getBlockState(offsetPos.offset(EnumFacing.values()[k])).getBlock().getMaterial() != Material.air) {
                        return new BlockData(offsetPos.offset(EnumFacing.values()[k]), invert[EnumFacing.values()[k].ordinal()]);
                    }
                }
            }
        }
        return null;
    }

    private int getSlot() {
        for (int k = 0; k < 9; ++k) {
            final ItemStack itemStack = this.mc().thePlayer.inventory.mainInventory[k];
            if (itemStack != null && this.isValid(itemStack) && itemStack.stackSize >= 1) {
                return k;
            }
        }
        return -1;
    }

    private void moveBlocksToHotbar() {
        boolean added = false;

        if (getEmptyHotbarSlot() != -1) {
            for (int k = 0; k < this.mc().thePlayer.inventory.mainInventory.length; ++k) {
                if (k > 8 && !added) {
                    final ItemStack itemStack = this.mc().thePlayer.inventory.mainInventory[k];
                    if (itemStack != null && this.isValid(itemStack)) {
                        this.mc().playerController.windowClick(this.mc().thePlayer.inventoryContainer.windowId, k, 0, 1, this.mc().thePlayer);
                        added = true;
                    }
                }
            }
        }
    }

    private boolean isValid(final ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemBlock) {
            boolean isBad = false;
            final ItemBlock block = (ItemBlock) itemStack.getItem();
            for (int i = 0; i < this.noobBlocks.size(); ++i) {
                if (block.getBlock().equals(this.noobBlocks.get(i))) {
                    isBad = true;
                }
            }
            return !isBad;
        }
        return false;
    }

    private int getBlockCount() {
        int count = 0;
        for (int k = 0; k < this.mc().thePlayer.inventory.mainInventory.length; ++k) {
            final ItemStack itemStack = this.mc().thePlayer.inventory.mainInventory[k];
            if (itemStack != null && this.isValid(itemStack) && itemStack.stackSize >= 1) {
                count += itemStack.stackSize;
            }
        }
        return count;
    }

    private class BlockData {
        private final BlockPos blockPos;
        private final EnumFacing enumFacing;

        private BlockData(final BlockPos blockPos, final EnumFacing enumFacing) {
            this.blockPos = blockPos;
            this.enumFacing = enumFacing;
        }

        private EnumFacing getFacing() {
            return this.enumFacing;
        }

        private BlockPos getPosition() {
            return this.blockPos;
        }
    }


    private int getStaticRainbow(int speed, int offset) {
        float hue = 5000 + (System.currentTimeMillis() + offset) % speed;
        hue /= 5000;
        return Color.getHSBColor(hue, 0.65f, (float) .9).getRGB();
    }

}