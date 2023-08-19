package cc.swift.module.impl.player;

import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;


public class InventoryManagerModule extends Module {

    public final ModeValue<InventoryManagerModule.Mode> mode = new ModeValue<>("Mode", InventoryManagerModule.Mode.values());

    public final DoubleValue delay = new DoubleValue("Delay", 50D, 0, 1000, 5);
    public final BooleanValue throwTrash = new BooleanValue("Throw Trash", false);
    public final BooleanValue autoArmor = new BooleanValue("Auto Armor", false);
    public final BooleanValue doInvPackets = new BooleanValue("Send C16 & C0D", false).setDependency(() -> mode.getValue() == Mode.SILENT);

    public final DoubleValue swordSlot = new DoubleValue("Sword Slot", 1.0, 1, 9, 1);
    public final DoubleValue pickSlot = new DoubleValue("Pick Slot", 2.0, 1, 9, 1);
    public final DoubleValue axeSlot = new DoubleValue("Axe Slot", 3.0, 1, 9, 1);
    public final DoubleValue bowSlot = new DoubleValue("Bow Slot", 4.0, 1, 9, 1);

    private double nextMove;

    private Slot bestSword, bestPick, bestAxe, bestBow;

    public InventoryManagerModule() {
        super("InventoryManager", Category.PLAYER);
        this.registerValues(this.mode, this.delay, this.throwTrash, this.autoArmor, this.doInvPackets, this.swordSlot, this.pickSlot, this.axeSlot, this.bowSlot);
    }

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        boolean invOpen = mc.currentScreen instanceof GuiInventory;

        if (!invOpen && this.mode.getValue() != Mode.SILENT)
            return;

        Container playerInventory = mc.thePlayer.inventoryContainer;

        for (Slot slot : playerInventory.inventorySlots) {
            ItemStack slotStack = slot.getStack();

            if (slotStack == null)
                continue;

            Item slotItem = slotStack.getItem();

            if (slotItem instanceof ItemSword) {
                if (bestSword == null || getSwordDamage(slotStack) > getSwordDamage(bestSword.getStack()))
                    bestSword = slot;

            } else if (slotItem instanceof ItemPickaxe) {
                if (bestPick == null || getToolEfficiency(slotStack, Blocks.stone) > getToolEfficiency(bestPick.getStack(), Blocks.stone))
                    bestPick = slot;

            } else if (slotItem instanceof ItemAxe) {
                if (bestAxe == null || getToolEfficiency(slotStack, Blocks.log) > getToolEfficiency(bestAxe.getStack(), Blocks.log))
                    bestAxe = slot;

            } else if (slotItem instanceof ItemBow) {
                if (bestBow == null || getBowStrength(slotStack) > getBowStrength(bestBow.getStack()))
                    bestBow = slot;

            } else if (throwTrash.getValue() && (
                    slotItem instanceof ItemEgg
                    || slotItem instanceof ItemSnowball
                    || slotItem instanceof ItemExpBottle
                    || slotItem instanceof ItemFishingRod)) {

            }
        }

        if (doInvPackets.getValue() && !invOpen)
            mc.getNetHandler().getNetworkManager().sendPacket(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));

        if (bestSword != null && bestSword.slotNumber - 36 != swordSlot.getValue().intValue() - 1)
            sortHotbar(bestSword.slotNumber, swordSlot.getValue().intValue() - 1);

        if (bestPick != null && bestPick.slotNumber - 36 != pickSlot.getValue().intValue() - 1)
            sortHotbar(bestPick.slotNumber, pickSlot.getValue().intValue() - 1);

        if (bestAxe != null && bestAxe.slotNumber - 36 != axeSlot.getValue().intValue() - 1)
            sortHotbar(bestAxe.slotNumber, axeSlot.getValue().intValue() - 1);

        if (bestBow != null && bestBow.slotNumber - 36 != bowSlot.getValue().intValue() - 1)
            sortHotbar(bestBow.slotNumber, bowSlot.getValue().intValue() - 1);

        if (doInvPackets.getValue() && !invOpen)
            mc.getNetHandler().getNetworkManager().sendPacket(new C0DPacketCloseWindow(playerInventory.windowId));
    };

    private void sortHotbar(int itemSlot, int slot) {
        if (System.currentTimeMillis() >= nextMove) {
            mc.playerController.windowClick(0, itemSlot, slot, 2, mc.thePlayer);
            nextMove = System.currentTimeMillis() + delay.getValue();
        }
    }

    private double getSwordDamage(ItemStack swordStack) {
        if (swordStack == null || !(swordStack.getItem() instanceof ItemSword))
            return 0.0;

        ItemSword sword = (ItemSword) swordStack.getItem();
        return sword.attackDamage + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, swordStack) * 1.25;
    }

    private float getToolEfficiency(ItemStack toolStack, Block testBlock) {
        if (toolStack == null || !(toolStack.getItem() instanceof ItemTool))
            return 0F;

        ItemTool tool = (ItemTool) toolStack.getItem();
        float efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, toolStack);
        float efficiencyBonus = efficiencyLevel * 1.75F;
        return tool.getStrVsBlock(toolStack, testBlock) + efficiencyBonus;
    }

    private float getBowStrength(ItemStack bowStack) {
        if (bowStack == null || !(bowStack.getItem() instanceof ItemTool))
            return 0F;

        return EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, bowStack);
    }

    public enum Mode {
        SILENT,
        OPEN
    }
}
