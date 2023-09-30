/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.PotionEffect;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.module.combat.AutoPot;

public class InventoryManager
extends Module {
    private final Timer timer = new Timer();
    private int lastSlot;
    public Setting<Double> delay = new Setting<Double>("Delay", 50.0).minimum(0.0).maximum(500.0).incrementation(5.0).describedBy("The delay between each inventory update.");
    public Setting<Boolean> open = new Setting<Boolean>("Open Inventory", false).describedBy("Open the inventory.");
    public Setting<Boolean> spoof = new Setting<Boolean>("Spoof Inventory", false).describedBy("Spoof having your inventory open server-side. Can cause desyncing.");
    public Setting<Boolean> whileNotMoving = new Setting<Boolean>("While not Moving", false).describedBy("Only manage your inventory if you aren't moving.");
    public Setting<Boolean> moveBlocksToHotbar = new Setting<Boolean>("Move blocks to hotbar", true).describedBy("Whether to move blocks to your hotbar.");
    public Setting<Boolean> movePotionsToHotbar = new Setting<Boolean>("Move potions to hotbar", true).describedBy("Whether to move potions to your hotbar.");
    public List<String> junk = Arrays.asList("stick", "egg", "string", "cake", "mushroom", "flint", "dyePowder", "feather", "chest", "snowball", "fish", "enchant", "exp", "shears", "anvil", "torch", "seeds", "leather", "reeds", "skull", "record", "piston", "snow", "bottle", "poison", "shield", "web", "chest");
    private boolean inventoryOpen = false;
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        if (this.timer.hasTimeElapsed((long)this.delay.getValue().doubleValue(), true)) {
            if (!(!this.isInventoryOpen() || this.player.isMoving() && this.whileNotMoving.getValue().booleanValue())) {
                if (this.mc.currentScreen == null || this.mc.currentScreen instanceof GuiContainer && ((GuiContainer)this.mc.currentScreen).inventorySlots == this.mc.thePlayer.inventoryContainer) {
                    for (int i = 9; i < 45; ++i) {
                        int j;
                        Slot slot;
                        if (this.lastSlot >= 45) {
                            this.lastSlot = 0;
                        }
                        while (i <= this.lastSlot) {
                            ++i;
                        }
                        this.lastSlot = i;
                        try {
                            slot = this.mc.thePlayer.inventoryContainer.getSlot(i);
                        }
                        catch (Exception ex) {
                            continue;
                        }
                        if (!slot.getHasStack()) continue;
                        ItemStack stack = slot.getStack();
                        Item item = stack.getItem();
                        if (item instanceof ItemSword && this.isBestSword(i)) {
                            this.openInv();
                            this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, i, 0, 2, this.mc.thePlayer);
                            this.closeInv();
                            return;
                        }
                        if (item instanceof ItemSword && !this.isBestSword(i)) {
                            this.dropItem(i);
                        }
                        if (item instanceof ItemTool && !this.isBestTool(i)) {
                            this.dropItem(i);
                            return;
                        }
                        if (this.isJunk(i) && !(item instanceof ItemArmor)) {
                            this.dropItem(i);
                            return;
                        }
                        if (item instanceof ItemPotion && this.movePotionsToHotbar.getValue().booleanValue()) {
                            ItemPotion potion = (ItemPotion)item;
                            if (!ItemPotion.isSplash(stack.getMetadata())) continue;
                            boolean shouldMove = false;
                            for (PotionEffect potionEffect : potion.getEffects(stack.getMetadata())) {
                                if (!Wrapper.getModule(AutoPot.class).isValidEffect(potionEffect)) continue;
                                shouldMove = true;
                            }
                            if (shouldMove) {
                                this.openInv();
                                this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, i, 0, 1, this.mc.thePlayer);
                                this.closeInv();
                            }
                            return;
                        }
                        int blocksInHotbar = 0;
                        for (j = 0; j < 9; ++j) {
                            if (blocksInHotbar > 128) continue;
                            try {
                                if (!this.mc.thePlayer.inventoryContainer.getSlot(j + 36).getHasStack() || !(this.mc.thePlayer.inventoryContainer.getSlot(j + 36).getStack().getItem() instanceof ItemBlock)) continue;
                                blocksInHotbar += this.mc.thePlayer.inventoryContainer.getSlot((int)(j + 36)).getStack().stackSize;
                                continue;
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                        }
                        if (item instanceof ItemBlock && stack.stackSize > 8 && i < 36 && this.moveBlocksToHotbar.getValue().booleanValue() && blocksInHotbar < 96) {
                            this.openInv();
                            this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, i, 0, 1, this.mc.thePlayer);
                            this.closeInv();
                            return;
                        }
                        if (item.getItemStackLimit() <= 1 || i >= 36) continue;
                        for (j = 0; j < 9; ++j) {
                            try {
                                ItemStack jStack;
                                Item jItem;
                                if (!this.mc.thePlayer.inventoryContainer.getSlot(j + 36).getHasStack() || !(jItem = (jStack = this.mc.thePlayer.inventoryContainer.getSlot(j + 36).getStack()).getItem()).getClass().equals(item.getClass()) || jStack.stackSize >= 64) continue;
                                this.openInv();
                                this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, i, 0, 1, this.mc.thePlayer);
                                this.closeInv();
                                return;
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                        }
                    }
                }
            } else {
                this.lastSlot = 0;
            }
        }
    };
    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        Packet<INetHandlerPlayServer> packet;
        if (e.getPacket() instanceof C16PacketClientStatus && ((C16PacketClientStatus)(packet = (C16PacketClientStatus)e.getPacket())).getStatus().equals((Object)C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)) {
            this.inventoryOpen = true;
        }
        if (e.getPacket() instanceof C0DPacketCloseWindow && ((C0DPacketCloseWindow)(packet = (C0DPacketCloseWindow)e.getPacket())).getWindowId() == this.mc.thePlayer.inventoryContainer.windowId) {
            this.inventoryOpen = false;
        }
    };

    public InventoryManager() {
        super("Manager", "Manages your Inventory", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.timer.reset();
        this.lastSlot = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lastSlot = 0;
    }

    private boolean isJunk(int slotid) {
        Slot slot = this.mc.thePlayer.inventoryContainer.getSlot(slotid);
        ItemStack stack = slot.getStack();
        if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof ItemBanner) {
                return true;
            }
            if (stack.getDisplayName().toLowerCase().contains("tnt")) {
                return true;
            }
            for (String shortName : this.junk) {
                if (stack.getDisplayName().toLowerCase().contains(shortName)) {
                    return true;
                }
                if (!item.getUnlocalizedName().toLowerCase().contains(shortName)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isBestTool(int slotid) {
        Slot slot = this.mc.thePlayer.inventoryContainer.getSlot(slotid);
        ItemStack compareStack = slot.getStack();
        for (int i = 9; i < 45; ++i) {
            Slot slot2 = this.mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack2 = slot2.getStack();
            if (stack2 == null || compareStack == stack2 || !(stack2.getItem() instanceof ItemTool)) continue;
            ItemTool item = (ItemTool)stack2.getItem();
            ItemTool compare = (ItemTool)compareStack.getItem();
            if (item.getClass() != compare.getClass() || !(compare.getStrVsBlock(stack2, this.preferredBlock(item.getClass())) <= item.getStrVsBlock(compareStack, this.preferredBlock(compare.getClass())))) continue;
            return false;
        }
        return true;
    }

    private boolean isBestSword(int slotid) {
        Slot slot = this.mc.thePlayer.inventoryContainer.getSlot(slotid);
        ItemStack compareStack = slot.getStack();
        for (int i = 9; i < 45; ++i) {
            Slot slot2 = this.mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot2.getStack();
            if (stack == null || compareStack == stack || !(stack.getItem() instanceof ItemSword) || !(compareStack.getItem() instanceof ItemSword)) continue;
            ItemSword item = (ItemSword)stack.getItem();
            ItemSword compare = (ItemSword)compareStack.getItem();
            if (item.getClass() != compare.getClass() || !(compare.attackDamage + this.getSwordStrength(compareStack) <= item.attackDamage + this.getSwordStrength(stack))) continue;
            return false;
        }
        return true;
    }

    private boolean dropItem(int slot) {
        this.openInv();
        Slot slo2t = this.mc.thePlayer.inventoryContainer.getSlot(slot);
        ItemStack stack = slo2t.getStack();
        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, this.mc.thePlayer);
        this.closeInv();
        return true;
    }

    private Block preferredBlock(Class clazz) {
        return clazz == ItemPickaxe.class ? Blocks.cobblestone : (clazz == ItemAxe.class ? Blocks.log : Blocks.dirt);
    }

    private float getSwordStrength(ItemStack stack) {
        return (!(stack.getItem() instanceof ItemSword) ? 0.0f : (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f) + (!(stack.getItem() instanceof ItemSword) ? 0.0f : (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack));
    }

    private int checkProtection(ItemStack item) {
        return EnchantmentHelper.getEnchantmentLevel(0, item);
    }

    private void openInv() {
        if (!this.inventoryOpen && this.spoof.getValue().booleanValue()) {
            PacketUtil.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        }
        this.inventoryOpen = true;
    }

    private void closeInv() {
        if (this.inventoryOpen && this.spoof.getValue().booleanValue()) {
            PacketUtil.sendPacketNoEvent(new C0DPacketCloseWindow(this.mc.thePlayer.inventoryContainer.windowId));
        }
        this.inventoryOpen = false;
    }

    private boolean isInventoryOpen() {
        if (this.open.getValue().booleanValue()) {
            return this.mc.currentScreen instanceof GuiInventory;
        }
        return true;
    }
}

