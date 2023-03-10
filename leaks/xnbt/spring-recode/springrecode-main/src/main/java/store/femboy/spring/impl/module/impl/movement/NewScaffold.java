package store.femboy.spring.impl.module.impl.movement;

import best.azura.eventbus.core.EventPriority;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.event.impl.EventMotion;
import store.femboy.spring.impl.event.impl.EventUpdate;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.settings.impl.BooleanSetting;
import store.femboy.spring.impl.module.settings.impl.ModeSetting;
import store.femboy.spring.impl.module.settings.impl.NumberSettings;
import store.femboy.spring.impl.util.*;
import store.femboy.spring.impl.util.corrosionutils.ScaffoldUtil;

import java.util.Arrays;
import java.util.List;

public class NewScaffold extends Module {

    public static final List<Block> invalidBlocks = Arrays.asList(
            Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava,
            Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane,
            Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore,
            Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.ender_chest, Blocks.torch,
            Blocks.redstone_torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox,
            Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore,
            Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
            Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.furnace, Blocks.lit_furnace, Blocks.crafting_table,
            Blocks.acacia_fence, Blocks.acacia_fence_gate, Blocks.birch_fence, Blocks.birch_fence_gate, Blocks.dark_oak_fence, Blocks.dark_oak_fence_gate,
            Blocks.jungle_fence, Blocks.jungle_fence_gate, Blocks.oak_fence, Blocks.oak_fence_gate,
            Blocks.acacia_door, Blocks.birch_door, Blocks.dark_oak_door, Blocks.iron_door, Blocks.jungle_door, Blocks.oak_door, Blocks.spruce_door,
            Blocks.rail, Blocks.activator_rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.brewing_stand, Blocks.red_flower, Blocks.yellow_flower,
            Blocks.flower_pot, Blocks.beacon
    );

    final TimeUtil timer = new TimeUtil(), placeTimer = new TimeUtil();

    AnchorUtil.PlaceData currentPlaceData;

    boolean switched, didSwitch = false;
    float yaw, pitch;

    int currentSlot = -1, lastSlot = -1, firstSlot = -1;

    private final BooleanSetting noSwing = new BooleanSetting("No Swing", false);
    private final BooleanSetting silent = new BooleanSetting("Auto Switch", true);
    private final BooleanSetting safeWalk = new BooleanSetting("Safe Walk", false);

    private final BooleanSetting noSprint = new BooleanSetting("Anti Sprint", false);
    private final BooleanSetting vulcanFix = new BooleanSetting("Vulcan Fix", false);

    private final NumberSettings placeDelay = new NumberSettings("Place Delay", 120, 20, 1500, 5);
    private final ModeSetting placeMode = new ModeSetting("Place mode", "PRE", "PRE", "POST");

    public NewScaffold() {
        super("Scaffold (new)", "Sex", Keyboard.KEY_Z, Category.MOVEMENT);
        addSettings(noSwing, silent, safeWalk, noSprint, placeMode);
    }

    @EventHandler(EventPriority.HIGHER)
    public final Listener<EventMotion> onUpdate = e ->{
        if (noSprint.enabled) {
            mc.gameSettings.keyBindSprint.pressed = false;
        }

        if (mc.thePlayer.isDead || mc.thePlayer.ticksExisted < 7) return;

        if (getBlocksAmount() > 0) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ);
            currentPlaceData = AnchorUtil.getData();

            Rotation rotation = new Rotation(mc.thePlayer.posX, mc.thePlayer.posY-mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ, mc.thePlayer.rotationYaw - MathUtil.getRandFloat(170f, 190f), MathUtil.getRandFloat(70f, 90f));

            mc.thePlayer.rotationYawHead = rotation.getRotationYaw();
            mc.thePlayer.renderYawOffset = rotation.getRotationYaw();
            mc.thePlayer.rotationPitchHead = rotation.getRotationPitch();

            int oldSlot = mc.thePlayer.inventory.currentItem;
            if (e.isPre()) {
                e.setYaw(rotation.getRotationYaw());
                e.setPitch(rotation.getRotationPitch());
                yaw = rotation.getRotationYaw();
                pitch = rotation.getRotationPitch();
                mc.thePlayer.inventory.currentItem = ScaffoldUtil.getSlot();
                if (getBlocksAmount() <= 0 && getBlocksAmountInv() > 0) {
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
                            getSlotInv(), getFreeSlot(), 2, mc.thePlayer);
                    currentSlot = -1;
                }
            }

            mc.thePlayer.inventory.currentItem = oldSlot;

            boolean canPlace = currentPlaceData != null && currentPlaceData.getPos() != null && currentPlaceData.getFacing() != null && getBlocksAmount() > 0 && !mc.theWorld.isAirBlock(currentPlaceData.getPos());
            boolean isValidPos = mc.theWorld.isAirBlock(pos.add(0, -1, 0));

            if ((placeMode.is("PRE") && e.isPre()) || (placeMode.is("POST") && e.isPost())) {
                if (isValidPos && ScaffoldUtil.getSlot() != -1) {
                    if (canPlace) {
                        if (!switched && silent.isEnabled() && getSlot() != mc.thePlayer.inventory.currentItem && (lastSlot != ScaffoldUtil.getSlot() || lastSlot == -1)) {
                            int newSlot = ScaffoldUtil.getSlot();


                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(newSlot));

                            this.switched = true;
                            this.lastSlot = newSlot;
                        }
                        //  ChatUtil.sendChatMessageWithoutPrefix("§7[§dS§7] §f" + String.format("lastSlot=%d, currentSlot=%d, getSlot=%d", lastSlot, currentSlot, getSlot()) + "");
                        final Vec3i dirVec = currentPlaceData.getFacing().getDirectionVec();
                        final Vec3 hitVec = new Vec3(currentPlaceData.getPos()).addVector(Math.max(dirVec.getX(), -dirVec.getX()), Math.max(dirVec.getY(), -dirVec.getY()), Math.max(dirVec.getZ(), -dirVec.getZ()));

                        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventoryContainer.getSlot(36 + ScaffoldUtil.getSlot()).getStack(), currentPlaceData.getPos(), currentPlaceData.getFacing(), hitVec)) {
                            if (noSwing.isEnabled())
                                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                            else
                                mc.thePlayer.swingItem();
                        }
                        lastSlot = currentSlot;
                    }
                }
            }
        }
    };

    @Override
    public void onEnable() {
        firstSlot = mc.thePlayer.inventory.currentItem;
        this.switched = false;
        this.timer.reset();
        this.placeTimer.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSneak.pressed = false;
        mc.timer.timerSpeed = 1.0f;
        mc.thePlayer.inventory.currentItem = firstSlot;
        currentSlot = -1;
        lastSlot = -1;
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(firstSlot));
        super.onDisable();

    }

    // Please dont hardcode rots like this baby girl :3
    // Do some of da trigonometry tomfoolery
    float[] getRotations(final EnumFacing facing) {
        float addYaw = 180;
        if (currentPlaceData == null || currentPlaceData.getPos() == null || currentPlaceData.getFacing() == null)
            return new float[]{mc.thePlayer.rotationYaw + addYaw, 82.44f};
        float calcYaw;
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ);
        if (!mc.theWorld.isAirBlock(playerPos.add(1, -1, 1))) {
            calcYaw = 135;
            pitch = 78.44f;
        } else if (!mc.theWorld.isAirBlock(playerPos.add(-1, -1, -1))) {
            calcYaw = -45;
            pitch = 78.44f;
        } else if (!mc.theWorld.isAirBlock(playerPos.add(-1, -1, 1))) {
            calcYaw = -135;
            pitch = 78.44f;
        } else if (!mc.theWorld.isAirBlock(playerPos.add(1, -1, -1))) {
            calcYaw = 45;
            pitch = 78.44f;
        } else if (!mc.theWorld.isAirBlock(playerPos.add(-1, -1, 0))) {
            calcYaw = -90;
            pitch = 80.44f;
        } else if (!mc.theWorld.isAirBlock(playerPos.add(1, -1, 0))) {
            calcYaw = 90;
            pitch = 80.44f;
        } else if (!mc.theWorld.isAirBlock(playerPos.add(0, -1, 1))) {
            // rotation issue
            calcYaw = 180;
            pitch = 80f;
        } else if (!mc.theWorld.isAirBlock(playerPos.add(0, -1, -1))) {
            calcYaw = 0;
            pitch = 80.44f;
        } else calcYaw = this.yaw - addYaw;
        this.yaw = calcYaw;
        return new float[]{this.yaw + addYaw, this.pitch};
    }

    int getFreeSlot() {
        if (mc.thePlayer.getHeldItem() == null) return mc.thePlayer.inventory.currentItem;
        else for (int i = 0; i < 9; i++) if (mc.thePlayer.inventory.getStackInSlot(i) == null) return i;
        return mc.thePlayer.inventory.currentItem;
    }

    int getBlocksAmount() {
        int amount = 0;
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !invalidBlocks.contains(Block.getBlockFromItem(mc.thePlayer.inventory.getStackInSlot(i).getItem()))) {
                amount += mc.thePlayer.inventory.getStackInSlot(i).stackSize;
            }
        }
        return amount;
    }

    int getBlocksAmountInv() {
        int amount = 0;
        for (int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !invalidBlocks.contains(Block.getBlockFromItem(mc.thePlayer.inventory.getStackInSlot(i).getItem()))) {
                amount += mc.thePlayer.inventory.getStackInSlot(i).stackSize;
            }
        }
        return amount;
    }

    int getSlot() {
        if (silent.isEnabled()) {
            int latestSlot = -1;
            int latestStackSize = 64;
            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock && !invalidBlocks.contains(Block.getBlockFromItem(mc.thePlayer.getHeldItem().getItem()))) {
                return mc.thePlayer.inventory.currentItem;
            } else {
                for (int i = 0; i < 9; i++) {
                    if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !invalidBlocks.contains(Block.getBlockFromItem(mc.thePlayer.inventory.getStackInSlot(i).getItem())))
                        if (mc.thePlayer.inventory.getStackInSlot(i).stackSize < latestStackSize && mc.thePlayer.inventory.getStackInSlot(i).stackSize > 0) {
                            latestStackSize = mc.thePlayer.inventory.getStackInSlot(i).stackSize;
                            latestSlot = i;
                        }
                }
            }
            if (latestSlot != -1) return latestSlot;
            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock && !invalidBlocks.contains(Block.getBlockFromItem(mc.thePlayer.getHeldItem().getItem()))) {
                return mc.thePlayer.inventory.currentItem;
            } else {
                for (int i = 0; i < 9; i++) {
                    if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !invalidBlocks.contains(Block.getBlockFromItem(mc.thePlayer.inventory.getStackInSlot(i).getItem())))
                        return i;
                }
            }
        }
        return mc.thePlayer.inventory.currentItem;
    }

    int getSlotInv() {
        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock && !invalidBlocks.contains(Block.getBlockFromItem(mc.thePlayer.getHeldItem().getItem()))) {
            return mc.thePlayer.inventory.currentItem;
        } else {
            for (int i = 9; i < mc.thePlayer.inventory.getSizeInventory(); i++)
                if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !invalidBlocks.contains(Block.getBlockFromItem(mc.thePlayer.inventory.getStackInSlot(i).getItem())))
                    return i;
        }
        return mc.thePlayer.inventory.currentItem;
    }

}

