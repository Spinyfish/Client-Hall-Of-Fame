/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.TimeUtils;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.util.DamageSource;

public final class InvManager
extends Module {
    private static int bestSwordSlot;
    private static int bestPickaxeSlot;
    private static int bestBowSlot;
    private static int bestBlockSlot;
    private static int bestGapSlot;
    private static int[] bestArmorDamageReducment;
    private static int[] bestArmorSlot;
    private final List<Integer> allSwords = new ArrayList<Integer>();
    private final List<Integer> allBows = new ArrayList<Integer>();
    private final List<Integer> allPickaxes = new ArrayList<Integer>();
    private final List<Integer>[] allArmors = new List[4];
    private final List<Integer> allBlocks = new ArrayList<Integer>();
    private final List<Integer> trash = new ArrayList<Integer>();
    private final TimeUtils delayTimer = new TimeUtils();
    private boolean invOpen;
    public NumberSetting delay = new NumberSetting("Delay", 0.0, 300.0, 75.0, 25.0);
    public NumberSetting maxBlocks = new NumberSetting("Max Blocks", 0.0, 8.0, 4.0, 1.0);
    public BooleanSetting sort = new BooleanSetting("Sort", true);
    public BooleanSetting autoArmor = new BooleanSetting("Auto Armor", true);
    public BooleanSetting random = new BooleanSetting("Random", false);
    public BooleanSetting openInv = new BooleanSetting("Open Inv", true);
    public BooleanSetting keepArmor = new BooleanSetting("Keep Armor", false);
    public BooleanSetting onlyStill = new BooleanSetting("When Standing", false);

    public InvManager() {
        super("InvManager", 0, Category.PLAYER);
        this.addSetting(this.delay);
        this.addSetting(this.maxBlocks);
        this.addSetting(this.sort);
        this.addSetting(this.autoArmor);
        this.addSetting(this.random);
        this.addSetting(this.openInv);
        this.addSetting(this.keepArmor);
        this.addSetting(this.onlyStill);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        this.setSuffix(String.valueOf(this.delay.getValue()));
        this.closeInvServerSide();
        if (Objects.requireNonNull(InvManager.mc.currentScreen instanceof GuiChest).booleanValue()) {
            return;
        }
        if (this.onlyStill.isEnabled() && (InvManager.mc.gameSettings.keyBindJump.isKeyDown() || InvManager.mc.gameSettings.keyBindForward.isKeyDown() || InvManager.mc.gameSettings.keyBindLeft.isKeyDown() || InvManager.mc.gameSettings.keyBindBack.isKeyDown() || InvManager.mc.gameSettings.keyBindRight.isKeyDown())) {
            return;
        }
        this.searchForItems();
        this.searchForBestArmor();
        this.searchForTrash();
        if (!this.keepArmor.isEnabled()) {
            for (int slot : this.trash) {
                if (this.hasNoDelay()) {
                    return;
                }
                try {
                    this.openInvServerSide();
                    InvManager.mc.playerController.windowClick(InvManager.mc.thePlayer.inventoryContainer.windowId, slot < 9 ? slot + 36 : slot, 1, 4, InvManager.mc.thePlayer);
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    // empty catch block
                }
                this.delayTimer.reset();
            }
        }
        if (this.autoArmor.isEnabled()) {
            for (int i = 0; i < 4; ++i) {
                int slot;
                if (bestArmorSlot[i] == -1) continue;
                int bestSlot = bestArmorSlot[i];
                ItemStack oldArmor = InvManager.mc.thePlayer.inventory.armorItemInSlot(i);
                if (this.hasNoDelay()) {
                    return;
                }
                if (oldArmor != null && oldArmor.getItem() != null && !this.keepArmor.isEnabled()) {
                    this.openInvServerSide();
                    InvManager.mc.playerController.windowClick(InvManager.mc.thePlayer.inventoryContainer.windowId, 8 - i, 0, 4, InvManager.mc.thePlayer);
                    this.delayTimer.reset();
                }
                int n = slot = bestSlot < 9 ? bestSlot + 36 : bestSlot;
                if (this.hasNoDelay()) {
                    return;
                }
                this.openInvServerSide();
                InvManager.mc.playerController.windowClick(InvManager.mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, InvManager.mc.thePlayer);
                this.delayTimer.reset();
            }
        }
        if (this.sort.isEnabled()) {
            int slot;
            boolean switchSlotSword = false;
            boolean switchSlotBow = true;
            int switchSlotPickaxe = 2;
            int switchSlotBlock = 8;
            int switchGap = 7;
            if (bestSwordSlot != -1 && bestSwordSlot != 0) {
                int n = slot = bestSwordSlot < 9 ? bestSwordSlot + 36 : bestSwordSlot;
                if (this.hasNoDelay() && slot != 0) {
                    return;
                }
                this.openInvServerSide();
                try {
                    InvManager.mc.playerController.windowClick(InvManager.mc.thePlayer.inventoryContainer.windowId, slot, 0, 2, InvManager.mc.thePlayer);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.delayTimer.reset();
            }
            if (bestBowSlot != -1 && bestBowSlot != 1) {
                int n = slot = bestBowSlot < 9 ? bestBowSlot + 36 : bestBowSlot;
                if (this.hasNoDelay() && slot != 1) {
                    return;
                }
                this.openInvServerSide();
                try {
                    InvManager.mc.playerController.windowClick(InvManager.mc.thePlayer.inventoryContainer.windowId, slot, 1, 2, InvManager.mc.thePlayer);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.delayTimer.reset();
            }
            if (bestPickaxeSlot != -1 && bestPickaxeSlot != 2) {
                int n = slot = bestPickaxeSlot < 9 ? bestPickaxeSlot + 36 : bestPickaxeSlot;
                if (this.hasNoDelay() && slot != 2) {
                    return;
                }
                this.openInvServerSide();
                try {
                    InvManager.mc.playerController.windowClick(InvManager.mc.thePlayer.inventoryContainer.windowId, slot, 2, 2, InvManager.mc.thePlayer);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.delayTimer.reset();
            }
            if (bestBlockSlot != -1 && bestBlockSlot != 8) {
                int n = slot = bestBlockSlot < 9 ? bestBlockSlot + 36 : bestBlockSlot;
                if (InvManager.mc.thePlayer.inventory.getStackInSlot(8) != null && InvManager.mc.thePlayer.inventory.getStackInSlot(8).getItem() instanceof ItemBlock) {
                    return;
                }
                if (this.hasNoDelay() && slot != 8) {
                    return;
                }
                this.openInvServerSide();
                try {
                    InvManager.mc.playerController.windowClick(InvManager.mc.thePlayer.inventoryContainer.windowId, slot, 8, 2, InvManager.mc.thePlayer);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.delayTimer.reset();
            }
            if (bestGapSlot != -1 && bestGapSlot != 7) {
                int n = slot = bestGapSlot < 9 ? bestGapSlot + 36 : bestGapSlot;
                if (this.hasNoDelay() && slot != 7) {
                    return;
                }
                this.openInvServerSide();
                try {
                    InvManager.mc.playerController.windowClick(InvManager.mc.thePlayer.inventoryContainer.windowId, slot, 7, 2, InvManager.mc.thePlayer);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.delayTimer.reset();
            }
        }
    }

    private void openInvServerSide() {
        if (!this.invOpen && !this.openInv.isEnabled()) {
            InvManager.mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(InvManager.mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
            this.invOpen = true;
        }
    }

    private void closeInvServerSide() {
        if (this.invOpen && !this.openInv.isEnabled()) {
            InvManager.mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
            this.invOpen = false;
        }
    }

    private boolean hasNoDelay() {
        return !this.delayTimer.hasReached(this.delay.getValueInt()) || !(InvManager.mc.currentScreen instanceof GuiInventory) && this.openInv.isEnabled();
    }

    private void searchForTrash() {
        this.trash.clear();
        for (int i = 0; i < this.allArmors.length; ++i) {
            List<Integer> armorItem = this.allArmors[i];
            if (armorItem == null) continue;
            int finalI = i;
            armorItem.stream().filter(slot -> slot != bestArmorSlot[finalI]).forEach(this.trash::add);
        }
        this.allBows.stream().filter(slot -> slot != bestBowSlot).forEach(this.trash::add);
        this.allSwords.stream().filter(slot -> slot != bestSwordSlot).forEach(this.trash::add);
        this.allPickaxes.stream().filter(slot -> slot != bestPickaxeSlot).forEach(this.trash::add);
        int blockStacks = this.allBlocks.size();
        for (int slot2 : this.allBlocks) {
            if (blockStacks <= Math.round(this.maxBlocks.getValueInt() + 1)) break;
            this.trash.add(slot2);
            --blockStacks;
        }
        if (this.random.isEnabled()) {
            Collections.shuffle(this.trash);
        } else {
            Collections.sort(this.trash);
        }
    }

    private void searchForBestArmor() {
        ItemArmor armor;
        ItemStack itemStack;
        int i;
        bestArmorDamageReducment = new int[4];
        bestArmorSlot = new int[4];
        Arrays.fill(bestArmorDamageReducment, -1);
        Arrays.fill(bestArmorSlot, -1);
        for (i = 0; i < bestArmorSlot.length; ++i) {
            int currentProtection;
            itemStack = InvManager.mc.thePlayer.inventory.armorItemInSlot(i);
            this.allArmors[i] = new ArrayList<Integer>();
            if (itemStack == null || itemStack.getItem() == null || !(itemStack.getItem() instanceof ItemArmor)) continue;
            armor = (ItemArmor)itemStack.getItem();
            InvManager.bestArmorDamageReducment[i] = currentProtection = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{itemStack}, DamageSource.generic);
        }
        for (i = 0; i < 36; ++i) {
            itemStack = InvManager.mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || itemStack.getItem() == null || !(itemStack.getItem() instanceof ItemArmor)) continue;
            armor = (ItemArmor)itemStack.getItem();
            int armorType = 3 - armor.armorType;
            this.allArmors[armorType].add(i);
            int slotProtectionLevel = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{itemStack}, DamageSource.generic);
            if (bestArmorDamageReducment[armorType] >= slotProtectionLevel) continue;
            InvManager.bestArmorDamageReducment[armorType] = slotProtectionLevel;
            InvManager.bestArmorSlot[armorType] = i;
        }
    }

    private void searchForItems() {
        bestSwordSlot = -1;
        bestBowSlot = -1;
        bestPickaxeSlot = -1;
        bestBlockSlot = -1;
        bestGapSlot = -1;
        this.allSwords.clear();
        this.allBows.clear();
        this.allPickaxes.clear();
        this.allBlocks.clear();
        float bestSwordDamage = -1.0f;
        float bestSwordDurability = -1.0f;
        float bestPickaxeEfficiency = -1.0f;
        float bestPickaxeDurability = -1.0f;
        float bestBowDurability = -1.0f;
        int bestBowDamage = -1;
        int bestBlockSize = -1;
        int gapStackSize = -1;
        for (int i = 0; i < 36; ++i) {
            int level;
            ItemStack itemStack = InvManager.mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || itemStack.getItem() == null) continue;
            if (itemStack.getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword)itemStack.getItem();
                level = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
                float damageLevel = (float)((double)sword.getDamageVsEntity() + (double)level * 1.25);
                this.allSwords.add(i);
                if (bestSwordDamage < damageLevel) {
                    bestSwordDamage = damageLevel;
                    bestSwordDurability = sword.getDamageVsEntity();
                    bestSwordSlot = i;
                }
                if (damageLevel == bestSwordDamage && sword.getDamageVsEntity() < bestSwordDurability) {
                    bestSwordDurability = sword.getDamageVsEntity();
                    bestSwordSlot = i;
                }
            }
            if (itemStack.getItem() instanceof ItemPickaxe) {
                ItemPickaxe pickaxe = (ItemPickaxe)itemStack.getItem();
                this.allPickaxes.add(i);
                float efficiencyLevel = this.getPickaxeEfficiency(pickaxe);
                if (bestPickaxeEfficiency < efficiencyLevel) {
                    bestPickaxeEfficiency = efficiencyLevel;
                    bestPickaxeDurability = pickaxe.getMaxDamage();
                    bestPickaxeSlot = i;
                }
                if (efficiencyLevel == bestPickaxeEfficiency && (float)pickaxe.getMaxDamage() < bestPickaxeDurability) {
                    bestPickaxeDurability = pickaxe.getMaxDamage();
                    bestPickaxeSlot = i;
                }
            }
            if (itemStack.getItem() instanceof ItemBow) {
                ItemBow bow = (ItemBow)itemStack.getItem();
                level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack);
                this.allBows.add(i);
                if (bestBowDamage < level || bestBowDamage == -1 && level == 0) {
                    bestBowDamage = level;
                    bestBowDurability = bow.getMaxDamage();
                    bestBowSlot = i;
                }
                if (level == bestBowDamage && (float)bow.getMaxDamage() < bestBowDurability) {
                    bestBowDurability = bow.getMaxDamage();
                    bestBowSlot = i;
                }
            }
            if (itemStack.getItem() instanceof ItemBlock) {
                ItemBlock block = (ItemBlock)itemStack.getItem();
                if (block.getBlock() == Blocks.web || block.getBlock() == Blocks.bed || block.getBlock() == Blocks.noteblock || block.getBlock() == Blocks.cactus || block.getBlock() == Blocks.cake || block.getBlock() == Blocks.anvil || block.getBlock() == Blocks.skull || block.getBlock() instanceof BlockDoor || block.getBlock() instanceof BlockFlower || block.getBlock() instanceof BlockCarpet) continue;
                this.allBlocks.add(i);
                if (bestBlockSize < itemStack.stackSize) {
                    bestBlockSize = itemStack.stackSize;
                    bestBlockSlot = i;
                }
            }
            if (!(itemStack.getItem() instanceof ItemAppleGold) || gapStackSize >= itemStack.stackSize) continue;
            gapStackSize = itemStack.stackSize;
            bestGapSlot = i;
        }
    }

    private float getPickaxeEfficiency(ItemPickaxe pickaxe) {
        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, new ItemStack(pickaxe));
        switch (level) {
            case 1: {
                level = 30;
                break;
            }
            case 2: {
                level = 69;
                break;
            }
            case 3: {
                level = 120;
                break;
            }
            case 4: {
                level = 186;
                break;
            }
            case 5: {
                level = 271;
                break;
            }
            default: {
                level = 0;
            }
        }
        return pickaxe.getToolMaterial().getEfficiencyOnProperMaterial() + (float)level;
    }
}

