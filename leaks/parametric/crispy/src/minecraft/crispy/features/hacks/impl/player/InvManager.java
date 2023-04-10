package crispy.features.hacks.impl.player;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.player.ArmorUtils;
import crispy.util.time.Timer;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.NumberValue;

import java.util.Arrays;
import java.util.List;


@HackInfo(name = "InvManager", category = Category.PLAYER)
public class InvManager extends Hack {
    public List<String> junk = Arrays.asList("stick", "egg", "string", "cake", "mushroom", "flint", "dyePowder", "feather", "chest", "snowball",
            "fish", "enchant", "exp", "shears", "anvil", "torch", "seeds", "leather", "reeds", "skull", "record", "piston", "snow");

    enum armor {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS
    }
    public boolean Delay = false;
    public static Timer timer = new Timer();
    public boolean inventoryOpen;
    public static boolean isDone = false;
    public static BooleanValue clean = new BooleanValue("Clean Inventory", true);
    public static BooleanValue spoof = new BooleanValue("Spoof Inventory", true);
    public static NumberValue<Long> delay = new NumberValue<Long>("Delay", 200L, 10L, 1000L);



    @Override
    public void onEvent(Event e) {

        if(e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();
        } else if(e instanceof EventUpdate) {
            setDisplayName(getName() + " \2477" + delay.getObject());
            if (!(mc.thePlayer.openContainer instanceof ContainerChest)) {
                if(spoof.getObject() && !(mc.currentScreen instanceof GuiInventory))
                    return;
                int slotID = -1;
                double maxProt = -1;
                int switchArmor = -1;

                for (int i = 9; i < 45; ++i) {
                    ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (stack != null && (this.canEquip(stack) || this.betterCheck(stack) && !this.canEquip(stack))) {
                        if (this.betterCheck(stack) && switchArmor == -1) {
                            switchArmor = this.betterSwap(stack);
                        }

                        double protValue = getProtectionValue(stack);
                        if (protValue >= maxProt) {
                            slotID = i;
                            maxProt = protValue;
                        }
                    }
                }
                if (slotID != -1 && Crispy.invCooldownElapsed(delay.getObject())) {
                    if (switchArmor != -1) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 4 + switchArmor, 0, 0, mc.thePlayer);
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, -999, 0, 0, mc.thePlayer);
                    }

                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotID, 0, 1, mc.thePlayer);
                    if (!(mc.currentScreen instanceof GuiInventory)) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.openContainer.windowId));
                    }
                    Crispy.invCooldownElapsed(0);
                }
                if(clean.getObject()) {
                if ((mc.currentScreen == null || (mc.currentScreen instanceof GuiContainer && ((GuiContainer) mc.currentScreen).inventorySlots == mc.thePlayer.inventoryContainer)) && purgeUnusedArmor() && purgeUnusedTools() && purgeJunk() && manageSword()) {
                    //Logger.ingameInfo("DONE");
                    if (hotbarHasSpace())
                        manageHotbar();
                }
                }
            }
        }
    }
    /**
     * <p>getSwordStrength.</p>
     *
     * @param stack a {@link net.minecraft.item.ItemStack} object.
     * @return a float.
     */
    public static float getSwordStrength(ItemStack stack) {
        return (!(stack.getItem() instanceof ItemSword) ? 0.0F : (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F) + (!(stack.getItem() instanceof ItemSword) ? 0.0F : (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1F);
    }


    /**
     * {@inheritDoc}
     */



    private boolean manageSword() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null) {
                Item item = stack.getItem();
                if (!stack.getDisplayName().toLowerCase().contains("(right click)") && item instanceof ItemSword && Crispy.invCooldownElapsed(delay.getObject())) {
                    moveToHotbarSlot1(i);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * <p>purgeUnusedArmor.</p>
     *
     * @return a boolean.
     */
    public boolean purgeUnusedArmor() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null) {
                Item item = stack.getItem();

                if (item instanceof ItemArmor) {
                    if (!isBestArmor(stack) && Crispy.invCooldownElapsed(delay.getObject())) {
                        purge(i);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * <p>purgeUnusedTools.</p>
     *
     * @return a boolean.
     */
    public boolean purgeUnusedTools() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null) {
                Item item = stack.getItem();

                if (item instanceof ItemTool) {
                    if (!stack.getDisplayName().toLowerCase().contains("(right click)") && !isBestTool(stack) && Crispy.invCooldownElapsed(delay.getObject())) {
                        purge(i);
                        return false;
                    }
                }
                if (item instanceof ItemSword) {
                    if (!stack.getDisplayName().toLowerCase().contains("(right click)") && !isBestSword(stack) && Crispy.invCooldownElapsed(delay.getObject())) {
                        purge(i);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * <p>purgeJunk.</p>
     *
     * @return a boolean.
     */
    public boolean purgeJunk() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null) {
                Item item = stack.getItem();

                for (String shortName : junk) {
                    if (!stack.getDisplayName().toLowerCase().contains("(right click)") && item.getUnlocalizedName().contains(shortName) && Crispy.invCooldownElapsed(delay.getObject())) {
                        purge(i);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * <p>manageHotbar.</p>
     */
    public void manageHotbar() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null) {
                Item item = stack.getItem();

                if (!stack.getDisplayName().toLowerCase().contains("(right click)") &&
                        ((item instanceof ItemPickaxe && hotbarNeedsItem(ItemPickaxe.class)) || (item instanceof ItemAxe && hotbarNeedsItem(ItemAxe.class)) || (item instanceof ItemSword && hotbarNeedsItem(ItemSword.class)) ||
                                (item instanceof ItemAppleGold && hotbarNeedsItem(ItemAppleGold.class)) || (item instanceof ItemEnderPearl && hotbarNeedsItem(ItemEnderPearl.class)) || (item instanceof ItemBlock && (((ItemBlock) item).getBlock().isFullCube()) &&
                                !hotbarHasBlocks())) &&
                        !hotbar && Crispy.invCooldownElapsed(delay.getObject())) {
                    moveToHotbar(i);
                    return;
                }
            }
        }
        //for (int i = 9; i < 45; i++) {

        //}
    }

    /**
     * <p>hotbarHasSpace.</p>
     *
     * @return a boolean.
     */
    public boolean hotbarHasSpace() {
        for (int i = 36; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);

            if (slot.getStack() == null)
                return true;
        }
        return false;
    }

    public boolean hotbarNeedsItem(Class<?> type) {
        for (int i = 36; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);

            if (type.isInstance(slot.getStack()))
                return false;
        }
        return true;
    }

    public boolean hotbarHasBlocks() {
        for (int i = 36; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);

            if (slot.getStack() != null && slot.getStack().getItem() instanceof ItemBlock && ((ItemBlock) slot.getStack().getItem()).getBlock().isFullCube())
                return true;
        }
        return false;
    }

    /**
     * <p>isBestTool.</p>
     *
     * @param compareStack a {@link net.minecraft.item.ItemStack} object.
     * @return a boolean.
     */
    public boolean isBestTool(ItemStack compareStack) {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null && compareStack != stack && stack.getItem() instanceof ItemTool) {
                ItemTool item = (ItemTool) stack.getItem();
                ItemTool compare = (ItemTool) compareStack.getItem();
                if (item.getClass() == compare.getClass()) {
                    if (compare.getStrVsBlock(stack, preferredBlock(item.getClass())) <= item.getStrVsBlock(compareStack, preferredBlock(compare.getClass())))
                        return false;
                }
            }
        }

        return true;
    }

    /**
     * <p>isBestSword.</p>
     *
     * @param compareStack a {@link net.minecraft.item.ItemStack} object.
     * @return a boolean.
     */
    public boolean isBestSword(ItemStack compareStack) {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null && compareStack != stack && stack.getItem() instanceof ItemSword) {
                ItemSword item = (ItemSword) stack.getItem();
                ItemSword compare = (ItemSword) compareStack.getItem();
                if (item.getClass() == compare.getClass()) {
                    if (compare.attackDamage + getSwordStrength(compareStack) <= item.attackDamage + getSwordStrength(stack))
                        return false;
                }
            }
        }

        return true;
    }

    /**
     * <p>preferredBlock.</p>
     *
     * @param clazz a {@link Class} object.
     * @return a {@link net.minecraft.block.Block} object.
     */
    public Block preferredBlock(Class clazz) {
        return clazz == ItemPickaxe.class ? Blocks.cobblestone : clazz == ItemAxe.class ? Blocks.log : Blocks.dirt;
    }

    /**
     * <p>isBestArmor.</p>
     *
     * @param compareStack a {@link net.minecraft.item.ItemStack} object.
     * @return a boolean.
     */
    public boolean isBestArmor(ItemStack compareStack) {
        for (int i = 0; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null && compareStack != stack && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor) stack.getItem();
                ItemArmor compare = (ItemArmor) compareStack.getItem();
                if (item.armorType == compare.armorType) {
                    if (ArmorUtils.getProtectionValue(compareStack) <= ArmorUtils.getProtectionValue(stack))
                        return false;
                }
            }
        }

        return true;
    }

    public boolean has(Item item, int count) { //WIP
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);

            if (slot.getStack() != null && (slot.getStack().getItem().equals(item)))
                count -= slot.getStack().stackSize;
        }
        return count >= 0;
    }

    /**
     * <p>moveToHotbar.</p>
     *
     * @param slot a int.
     */
    public void moveToHotbar(int slot) {

        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);


    }

    public void moveToHotbarSlot1(int slot) {

        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 2, mc.thePlayer);

    }

    /**
     * <p>purge.</p>
     *
     * @param slot a int.
     */
    public void purge(int slot) {

        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
        // Logger.ingameInfo(mc.thePlayer.ticksExisted+"");

    }

    /**
     * <p>openInvPacket.</p>
     */
    public void openInvPacket() {
        if (!inventoryOpen)
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));

        inventoryOpen = true;
    }

    /**
     * <p>closeInvPacket.</p>
     */
    public void closeInvPacket() {
        if (inventoryOpen)
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));

        inventoryOpen = false;
    }
    /*
    Autoarmor
     */
    private int[] chestplate, leggings, boots, helmet;
    private boolean best;

    public static double getProtectionValue(ItemStack stack) {
        return !(stack.getItem() instanceof ItemArmor) ? 0.0D : (double) ((ItemArmor) stack.getItem()).damageReduceAmount + (double) ((100 - ((ItemArmor) stack.getItem()).damageReduceAmount * 4) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 4) * 0.0075D;
    }
    //Hello


    public boolean betterCheck(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor) {
            if (mc.thePlayer.getEquipmentInSlot(1) != null && stack.getUnlocalizedName().contains("boots") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(1)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(1).getItem()).damageReduceAmount) {
                return true;
            }

            if (mc.thePlayer.getEquipmentInSlot(2) != null && stack.getUnlocalizedName().contains("leggings") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(2)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(2).getItem()).damageReduceAmount) {
                return true;
            }

            if (mc.thePlayer.getEquipmentInSlot(3) != null && stack.getUnlocalizedName().contains("chestplate") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(3)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(3).getItem()).damageReduceAmount) {
                return true;
            }

            return mc.thePlayer.getEquipmentInSlot(4) != null && stack.getUnlocalizedName().contains("helmet") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(4)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(4).getItem()).damageReduceAmount;
        }

        return false;
    }

    private int betterSwap(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor) {
            if (mc.thePlayer.getEquipmentInSlot(4) != null && stack.getUnlocalizedName().contains("helmet") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(4)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(4).getItem()).damageReduceAmount) {
                return 1;
            }

            if (mc.thePlayer.getEquipmentInSlot(3) != null && stack.getUnlocalizedName().contains("chestplate") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(3)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(3).getItem()).damageReduceAmount) {
                return 2;
            }

            if (mc.thePlayer.getEquipmentInSlot(2) != null && stack.getUnlocalizedName().contains("leggings") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(2)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(2).getItem()).damageReduceAmount) {
                return 3;
            }

            if (mc.thePlayer.getEquipmentInSlot(1) != null && stack.getUnlocalizedName().contains("boots") && getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(1)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(1).getItem()).damageReduceAmount) {
                return 4;
            }
        }

        return -1;
    }

    private boolean canEquip(ItemStack stack) {
        return mc.thePlayer.getEquipmentInSlot(1) == null && stack.getUnlocalizedName().contains("boots") || mc.thePlayer.getEquipmentInSlot(2) == null && stack.getUnlocalizedName().contains("leggings") || mc.thePlayer.getEquipmentInSlot(3) == null && stack.getUnlocalizedName().contains("chestplate") || mc.thePlayer.getEquipmentInSlot(4) == null && stack.getUnlocalizedName().contains("helmet");
    }
}
