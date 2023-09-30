package alos.stella.module.modules.player;

import alos.stella.event.EventState;
import alos.stella.event.EventTarget;
import alos.stella.event.events.MotionEvent;
import alos.stella.event.events.UpdateEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.utils.GUIDetectionComponent;
import alos.stella.utils.timer.StopWatch;
import alos.stella.value.BoolValue;
import alos.stella.value.IntegerValue;
import alos.stella.value.ListValue;
import net.minecraft.block.*;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.optifine.util.MathUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@ModuleInfo(name = "ChestStealer", description = "ChestStealer", category = ModuleCategory.PLAYER)
public class ChestStealer extends Module {

    public final ListValue modeValue = new ListValue("Mode", new String[]{"Normal", "ExtraPackets"}, "Normal");
    public IntegerValue maxDelay = new IntegerValue("MaxDelay", 100, 50, 500);
    public IntegerValue minDelay = new IntegerValue("MinDelay", 100, 50, 500);
    public BoolValue ignoreTrash = new BoolValue("IgnoreTrash", false);

    private final alos.stella.utils.timer.StopWatch stopwatch = new StopWatch();
    private long nextClick;
    private int lastClick;
    private int lastSteal;
    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (modeValue.get().equalsIgnoreCase("ExtraPackets")) {
	    	if (mc.currentScreen instanceof GuiChest) {
	            GuiChest chest = (GuiChest) mc.currentScreen;
	            int rows = chest.inventoryRows * 9;
	            for (int i = 0; i < rows; i++) { 
	                Slot slot = chest.inventorySlots.getSlot(i);
	                if (slot.getHasStack()) {
	                    mc.thePlayer.sendQueue.addToSendQueue(new C0EPacketClickWindow(chest.inventorySlots.windowId, i, 0, 1, slot.getStack(), (short) 1));
	                }
	            }
	            mc.thePlayer.closeScreen();
	        }
	    }
    }

    @EventTarget
    public void onMotion(MotionEvent event){
        if (modeValue.get().equalsIgnoreCase("Normal")) {
        	if (event.getEventState() == EventState.PRE) {

            if (mc.currentScreen instanceof GuiChest) {
                final ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;

                if (GUIDetectionComponent.inGUI() || !this.stopwatch.finished(this.nextClick)) {
                    return;
                }

                this.lastSteal++;

                for (int i = 0; i < container.inventorySlots.size(); i++) {
                    final ItemStack stack = container.getLowerChestInventory().getStackInSlot(i);

                    if (stack == null || this.lastSteal <= 1) {
                        continue;
                    }

                    if (this.ignoreTrash.getValue() && !useful(stack)) {
                        continue;
                    }

                    this.nextClick = Math.round(MathUtils.getRandom(this.minDelay.getValue(), this.maxDelay.getValue()));
                    mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                    this.stopwatch.reset();
                    this.lastClick = 0;
                    return;
                }

                this.lastClick++;

                if (this.lastClick > 1) {
                    mc.thePlayer.closeScreen();
                }
            } else {
                this.lastClick = 0;
                this.lastSteal = 0;
            }
        }
        }
    }

    public boolean useful(final ItemStack stack) {
        final Item item = stack.getItem();

        if (item instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) item;
            return ItemPotion.isSplash(stack.getMetadata()) && goodPotion(potion.getEffects(stack).get(0).getPotionID());
        }

        if (item instanceof ItemBlock) {
            final Block block = ((ItemBlock) item).getBlock();
            if (block instanceof BlockGlass || block instanceof BlockStainedGlass || (block.isFullBlock() && !(block instanceof BlockTNT || block instanceof BlockSlime || block instanceof BlockFalling))) {
                return true;
            }
        }

        return item instanceof ItemSword ||
                item instanceof ItemTool ||
                item instanceof ItemArmor ||
                item instanceof ItemFood ||
                WHITELISTED_ITEMS.contains(item);
    }

    public boolean goodPotion(final int id) {
        return GOOD_POTIONS.containsKey(id);
    }

    private final List<Item> WHITELISTED_ITEMS = Arrays.asList(Items.fishing_rod, Items.water_bucket, Items.bucket, Items.arrow, Items.bow, Items.snowball, Items.egg, Items.ender_pearl);

    private final HashMap<Integer, Integer> GOOD_POTIONS = new HashMap<Integer, Integer>() {{
        put(6, 1); // Instant Health
        put(10, 2); // Regeneration
        put(11, 3); // Resistance
        put(21, 4); // Health Boost
        put(22, 5); // Absorption
        put(23, 6); // Saturation
        put(5, 7); // Strength
        put(1, 8); // Speed
        put(12, 9); // Fire Resistance
        put(14, 10); // Invisibility
        put(3, 11); // Haste
        put(13, 12); // Water Breathing
    }};

}
