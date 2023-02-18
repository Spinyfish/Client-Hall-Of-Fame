/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.inventory.GuiInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemAppleGold
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemPotion
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.C0DPacketCloseWindow
 *  net.minecraft.network.play.client.C16PacketClientStatus
 *  net.minecraft.network.play.client.C16PacketClientStatus$EnumState
 *  net.minecraft.network.play.server.S2DPacketOpenWindow
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.service.event.impl.move.EventUpdate
 *  vip.astroline.client.service.event.impl.packet.EventReceivePacket
 *  vip.astroline.client.service.event.impl.packet.EventSendPacket
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.combat.KillAura
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.storage.utils.other.InventoryUtils
 *  vip.astroline.client.storage.utils.other.InventoryUtils$ClickType
 *  vip.astroline.client.storage.utils.other.WindowClickRequest
 */
package vip.astroline.client.service.module.impl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import vip.astroline.client.Astroline;
import vip.astroline.client.service.event.impl.move.EventUpdate;
import vip.astroline.client.service.event.impl.packet.EventReceivePacket;
import vip.astroline.client.service.event.impl.packet.EventSendPacket;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.combat.KillAura;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.storage.utils.other.InventoryUtils;
import vip.astroline.client.storage.utils.other.WindowClickRequest;

public class InvManager
extends Module {
    public static BooleanValue ignoreItemsWithCustomName = new BooleanValue("InvManager", "Ignore Custom Name", Boolean.valueOf(true));
    public static BooleanValue autoArmorProperty = new BooleanValue("InvManager", "Auto Armor", Boolean.valueOf(true));
    public static BooleanValue sortItemsProperty = new BooleanValue("InvManager", "Sort Items", Boolean.valueOf(true));
    public static BooleanValue dropItemsProperty = new BooleanValue("InvManager", "Drop Items", Boolean.valueOf(true));
    public static BooleanValue whileOpen = new BooleanValue("InvManager", "While Open", Boolean.valueOf(false));
    public static FloatValue delayProperty = new FloatValue("InvManager", "Delay", 80.0f, 10.0f, 500.0f, 10.0f, "ms");
    private final int[] bestArmorPieces = new int[4];
    private final List<Integer> trash = new ArrayList<Integer>();
    private final int[] bestToolSlots = new int[3];
    private final List<Integer> gappleStackSlots = new ArrayList<Integer>();
    private int bestSwordSlot;
    private int bestBowSlot;
    private final List<WindowClickRequest> clickRequests = new ArrayList<WindowClickRequest>();
    private boolean serverOpen;
    private boolean clientOpen;
    private int ticksSinceLastClick;
    private boolean nextTickCloseInventory;
    private KillAura aura;

    public InvManager() {
        super("InvManager", Category.Player, 0, false);
    }

    @EventTarget
    private void onPacketReceive(EventReceivePacket event) {
        Packet packet = event.getPacket();
        if (!(packet instanceof S2DPacketOpenWindow)) return;
        this.clientOpen = false;
        this.serverOpen = false;
    }

    @EventTarget
    private void onPacket(EventSendPacket event) {
        Packet packet = event.getPacket();
        if (packet instanceof C16PacketClientStatus) {
            C16PacketClientStatus clientStatus = (C16PacketClientStatus)packet;
            if (clientStatus.getStatus() != C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) return;
            this.clientOpen = true;
            this.serverOpen = true;
        } else {
            if (!(packet instanceof C0DPacketCloseWindow)) return;
            C0DPacketCloseWindow packetCloseWindow = (C0DPacketCloseWindow)packet;
            if (packetCloseWindow.getWindowId() != InvManager.mc.thePlayer.inventoryContainer.windowId) return;
            this.clientOpen = false;
            this.serverOpen = false;
        }
    }

    @EventTarget
    private void onWindowClick(WindowClickRequest event) {
        this.ticksSinceLastClick = 0;
    }

    private boolean dropItem(List<Integer> listOfSlots) {
        if (dropItemsProperty.getValue() == false) return false;
        if (listOfSlots.isEmpty()) return false;
        int slot = listOfSlots.remove(0);
        InventoryUtils.windowClick((Minecraft)mc, (int)slot, (int)1, (InventoryUtils.ClickType)InventoryUtils.ClickType.DROP_ITEM);
        return true;
    }

    public List<WindowClickRequest> getClickRequests() {
        return this.clickRequests;
    }

    /*
     * Unable to fully structure code
     */
    @EventTarget
    private void onUpdate(EventUpdate event) {
        block23: {
            ++this.ticksSinceLastClick;
            if ((double)this.ticksSinceLastClick < Math.floor(InvManager.delayProperty.getValue().floatValue() / 3.0f)) {
                return;
            }
            if (Astroline.INSTANCE.moduleManager.getModule("KillAura").isToggled() && KillAura.target != null) {
                if (this.nextTickCloseInventory) {
                    this.nextTickCloseInventory = false;
                }
                this.close();
                return;
            }
            if (this.clientOpen == false) return;
            this.clear();
            for (slot = 5; slot < 45; ++slot) {
                stack = InvManager.mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                if (stack == null) continue;
                if (InvManager.ignoreItemsWithCustomName.getValue().booleanValue() && stack.hasDisplayName()) continue;
                if (stack.getItem() instanceof ItemSword) {
                    if (InventoryUtils.isBestSword((EntityPlayerSP)InvManager.mc.thePlayer, (ItemStack)stack)) {
                        this.bestSwordSlot = slot;
                        continue;
                    }
                }
                if (stack.getItem() instanceof ItemTool) {
                    if (InventoryUtils.isBestTool((EntityPlayerSP)InvManager.mc.thePlayer, (ItemStack)stack)) {
                        toolType = InventoryUtils.getToolType((ItemStack)stack);
                        if (toolType == -1 || slot == this.bestToolSlots[toolType]) continue;
                        this.bestToolSlots[toolType] = slot;
                        continue;
                    }
                }
                if (stack.getItem() instanceof ItemArmor) {
                    if (InventoryUtils.isBestArmor((EntityPlayerSP)InvManager.mc.thePlayer, (ItemStack)stack)) {
                        armor = (ItemArmor)stack.getItem();
                        pieceSlot = this.bestArmorPieces[armor.armorType];
                        if (pieceSlot != -1 && slot == pieceSlot) continue;
                        this.bestArmorPieces[armor.armorType] = slot;
                        continue;
                    }
                }
                if (stack.getItem() instanceof ItemBow) {
                    if (InventoryUtils.isBestBow((EntityPlayerSP)InvManager.mc.thePlayer, (ItemStack)stack)) {
                        if (slot == this.bestBowSlot) continue;
                        this.bestBowSlot = slot;
                        continue;
                    }
                }
                if (stack.getItem() instanceof ItemAppleGold) {
                    this.gappleStackSlots.add(slot);
                    continue;
                }
                if (this.trash.contains(slot) || InvManager.isValidStack(stack)) continue;
                this.trash.add(slot);
            }
            if (this.trash.isEmpty()) break block23;
            if (InvManager.dropItemsProperty.getValue().booleanValue()) ** GOTO lbl-1000
        }
        if (this.equipArmor(false) || this.sortItems(false) || !this.clickRequests.isEmpty()) lbl-1000:
        // 2 sources

        {
            v0 = true;
        } else {
            v0 = busy = false;
        }
        if (!busy) {
            if (this.nextTickCloseInventory) {
                this.close();
                this.nextTickCloseInventory = false;
            } else {
                this.nextTickCloseInventory = true;
            }
            return;
        }
        waitUntilNextTick = this.serverOpen == false;
        this.open();
        if (this.nextTickCloseInventory) {
            this.nextTickCloseInventory = false;
        }
        if (waitUntilNextTick) {
            return;
        }
        if (!this.clickRequests.isEmpty()) {
            request = this.clickRequests.remove(0);
            request.performRequest();
            request.onCompleted();
            return;
        }
        if (this.equipArmor(true)) {
            return;
        }
        if (this.dropItem(this.trash)) {
            return;
        }
        this.sortItems(true);
    }

    private boolean sortItems(boolean moveItems) {
        if (sortItemsProperty.getValue() == false) return false;
        if (this.bestSwordSlot != -1 && this.bestSwordSlot != 36) {
            if (!moveItems) return true;
            this.putItemInSlot(36, this.bestSwordSlot);
            this.bestSwordSlot = 36;
            return true;
        }
        if (this.bestBowSlot != -1 && this.bestBowSlot != 38) {
            if (!moveItems) return true;
            this.putItemInSlot(38, this.bestBowSlot);
            this.bestBowSlot = 38;
            return true;
        }
        if (!this.gappleStackSlots.isEmpty()) {
            this.gappleStackSlots.sort(Comparator.comparingInt(slot -> InvManager.mc.thePlayer.inventoryContainer.getSlot((int)slot.intValue()).getStack().stackSize));
            int bestGappleSlot = this.gappleStackSlots.get(0);
            if (bestGappleSlot != 37) {
                if (!moveItems) return true;
                this.putItemInSlot(37, bestGappleSlot);
                this.gappleStackSlots.set(0, 37);
                return true;
            }
        }
        int[] toolSlots = new int[]{39, 40, 41};
        int[] nArray = this.bestToolSlots;
        int n = nArray.length;
        int n2 = 0;
        while (n2 < n) {
            int toolSlot = nArray[n2];
            if (toolSlot != -1) {
                int type = InventoryUtils.getToolType((ItemStack)InvManager.mc.thePlayer.inventoryContainer.getSlot(toolSlot).getStack());
                if (type != -1 && toolSlot != toolSlots[type]) {
                    if (!moveItems) return true;
                    this.putToolsInSlot(type, toolSlots);
                    return true;
                }
            }
            ++n2;
        }
        return false;
    }

    private boolean equipArmor(boolean moveItems) {
        if (autoArmorProperty.getValue() == false) return false;
        int i = 0;
        while (i < this.bestArmorPieces.length) {
            int piece = this.bestArmorPieces[i];
            if (piece != -1) {
                int armorPieceSlot = i + 5;
                ItemStack stack = InvManager.mc.thePlayer.inventoryContainer.getSlot(armorPieceSlot).getStack();
                if (stack == null) {
                    if (!moveItems) return true;
                    InventoryUtils.windowClick((Minecraft)mc, (int)piece, (int)0, (InventoryUtils.ClickType)InventoryUtils.ClickType.SHIFT_CLICK);
                    return true;
                }
            }
            ++i;
        }
        return false;
    }

    private void putItemInSlot(int slot, int slotIn) {
        InventoryUtils.windowClick((Minecraft)mc, (int)slotIn, (int)(slot - 36), (InventoryUtils.ClickType)InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    private void putToolsInSlot(int tool, int[] toolSlots) {
        int toolSlot = toolSlots[tool];
        InventoryUtils.windowClick((Minecraft)mc, (int)this.bestToolSlots[tool], (int)(toolSlot - 36), (InventoryUtils.ClickType)InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
        this.bestToolSlots[tool] = toolSlot;
    }

    private static boolean isValidStack(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock && InventoryUtils.isStackValidToPlace((ItemStack)stack)) {
            return true;
        }
        if (stack.getItem() instanceof ItemPotion && InventoryUtils.isBuffPotion((ItemStack)stack)) {
            return true;
        }
        if (!(stack.getItem() instanceof ItemFood)) return InventoryUtils.isGoodItem((Item)stack.getItem());
        if (!InventoryUtils.isGoodFood((ItemStack)stack)) return InventoryUtils.isGoodItem((Item)stack.getItem());
        return true;
    }

    public void onEnable() {
        this.ticksSinceLastClick = 0;
        this.serverOpen = this.clientOpen = InvManager.mc.currentScreen instanceof GuiInventory;
        super.onEnable();
    }

    public void onDisable() {
        this.close();
        this.clear();
        this.clickRequests.clear();
        super.onDisable();
    }

    private void open() {
        if (this.clientOpen) return;
        if (this.serverOpen) return;
        InvManager.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        this.serverOpen = true;
    }

    private void close() {
        if (this.clientOpen) return;
        if (!this.serverOpen) return;
        InvManager.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0DPacketCloseWindow(InvManager.mc.thePlayer.inventoryContainer.windowId));
        this.serverOpen = false;
    }

    private void clear() {
        this.trash.clear();
        this.bestBowSlot = -1;
        this.bestSwordSlot = -1;
        this.gappleStackSlots.clear();
        Arrays.fill(this.bestArmorPieces, -1);
        Arrays.fill(this.bestToolSlots, -1);
    }
}
