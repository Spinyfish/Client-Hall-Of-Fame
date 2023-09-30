/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;

public class AutoArmor
extends Module {
    public boolean isRunning;
    Timer timer = new Timer();
    public Setting<Float> delay = new Setting<Float>("Delay", Float.valueOf(50.0f)).minimum(Float.valueOf(0.0f)).maximum(Float.valueOf(500.0f)).incrementation(Float.valueOf(5.0f)).describedBy("The delay between each armor swap.");
    public Setting<Boolean> open = new Setting<Boolean>("Open Inventory", false).describedBy("If enabled, the inventory will be opened");
    public Setting<Boolean> spoof = new Setting<Boolean>("Spoof Inventory", false).describedBy("Spoof having your inventory open server-side. Can cause desyncing.");
    public Setting<Boolean> whileNotMoving = new Setting<Boolean>("While not Moving", false).describedBy("Only manage your inventory if you aren't moving.");
    private boolean inventoryOpen = false;
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        if (!(this.isChestInventory() || !this.isInventoryOpen() || this.player.isMoving() && this.whileNotMoving.getValue().booleanValue())) {
            this.isRunning = true;
            for (int i = 0; i < 36; ++i) {
                ItemStack item = this.mc.thePlayer.inventory.getStackInSlot(i);
                if (item == null || !(item.getItem() instanceof ItemArmor)) continue;
                ItemArmor armour = (ItemArmor)this.mc.thePlayer.inventory.getStackInSlot(i).getItem();
                int equippedReduction = 0;
                int equippedDur = 0;
                int checkReduction = 0;
                if (this.mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType) != null) {
                    ItemArmor equippedArmor = (ItemArmor)this.mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType).getItem();
                    ItemStack equippedItemStack = this.mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType);
                    equippedReduction = equippedArmor.getArmorMaterial().getDamageReductionAmount(armour.armorType);
                    equippedReduction = AutoArmor.checkProtection(this.mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType)) + equippedReduction;
                    equippedDur = equippedItemStack.getItemDamage();
                    checkReduction = armour.getArmorMaterial().getDamageReductionAmount(armour.armorType);
                    checkReduction = AutoArmor.checkProtection(this.mc.thePlayer.inventory.getStackInSlot(i)) + checkReduction;
                }
                if (this.getFreeSlot() != -1 && this.mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType) != null) {
                    if (checkReduction > equippedReduction || checkReduction == equippedReduction && item.getItemDamage() < equippedDur) {
                        if (i < 9) {
                            i += 36;
                        }
                        this.openInv();
                        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, 5 + armour.armorType, 0, 4, this.mc.thePlayer);
                        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, i, 0, 1, this.mc.thePlayer);
                        this.closeInv();
                    } else {
                        if (i < 9) {
                            i += 36;
                        }
                        this.openInv();
                        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, i, 1, 4, this.mc.thePlayer);
                        this.closeInv();
                        return;
                    }
                }
                if (this.mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType) != null || !this.timer.hasTimeElapsed((long)this.delay.getValue().doubleValue(), true)) continue;
                if (i < 9) {
                    i += 36;
                }
                this.openInv();
                this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, i, 0, 1, this.mc.thePlayer);
                this.closeInv();
            }
            this.isRunning = false;
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

    public AutoArmor() {
        super("Auto Armor", "Automatically equips armor", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.isRunning = false;
    }

    public int getFreeSlot() {
        for (int i = 35; i > 0; --i) {
            ItemStack item = this.mc.thePlayer.inventory.getStackInSlot(i);
            if (item != null) continue;
            return i;
        }
        return -1;
    }

    public static int checkProtection(ItemStack item) {
        return EnchantmentHelper.getEnchantmentLevel(0, item);
    }

    public boolean isChestInventory() {
        return this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer instanceof ContainerChest;
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

