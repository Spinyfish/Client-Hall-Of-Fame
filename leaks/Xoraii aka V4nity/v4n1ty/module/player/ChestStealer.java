package v4n1ty.module.player;

import de.Hero.settings.Setting;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import org.lwjgl.input.Keyboard;
import v4n1ty.V4n1ty;
import v4n1ty.module.Category;
import v4n1ty.module.Module;
import v4n1ty.utils.misc.TimerUtils;

public class ChestStealer extends Module {
    TimerUtils timer = new TimerUtils();

    @Override
    public void setup() {
        V4n1ty.settingsManager.rSetting(new Setting("Delay", this, 150, 0, 1000, true));
    }

    public void onUpdate() {
        if(this.isToggled()){
            if (this.mc.thePlayer != null && this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer instanceof ContainerChest) {
                ContainerChest chest = (ContainerChest) this.mc.thePlayer.openContainer;
                this.mc.thePlayer.motionX = 0.0;
                this.mc.thePlayer.motionZ = 0.0;
                for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); ++i) {
                    if (chest.getLowerChestInventory().getStackInSlot(i) != null && this.isGoodChest() && (V4n1ty.settingsManager.getSettingByName("Delay").getValDouble() <= 0.0 || this.timer.hasTimeElapsed((long) V4n1ty.settingsManager.getSettingByName("Delay").getValDouble(), true))) {
                        this.shiftClick(i, chest.windowId);
                    }
                    if (i < chest.getLowerChestInventory().getSizeInventory()) continue;
                    this.mc.thePlayer.closeScreen();
                }
                if (chest.getInventory().isEmpty()) {
                    this.mc.thePlayer.closeScreen();
                }
                int max = chest.inventorySlots.size() == 90 ? 54 : 27;
                for (int i = 0; i < max; ++i) {
                    if (!chest.getSlot(i).getHasStack()) continue;
                    return;
                }
                this.mc.thePlayer.closeScreen();
            }
        }
    }

    public ChestStealer() {
        super("ChestStealer", Keyboard.KEY_H, Category.PLAYER);
    }

    public void shiftClick(int slotId, int windowId) {
        ItemStack itemstack = this.mc.thePlayer.openContainer.slotClick(slotId, 0, 1, this.mc.thePlayer);
        short short1 = this.mc.thePlayer.openContainer.getNextTransactionID(this.mc.thePlayer.inventory);
        mc.thePlayer.sendQueue.addToSendQueue(new C0EPacketClickWindow(windowId, slotId, 0, 1, itemstack, short1));
    }

    public boolean isGoodChest() {
        if (this.mc.currentScreen != null && this.mc.currentScreen instanceof GuiChest) {
            GuiChest currentChest = (GuiChest)this.mc.currentScreen;
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("game")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("select")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("compass")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("select")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("teleport")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("hypixel")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("play")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("skywars")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("bedwars")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("cakewars")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("lobby")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("mode")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("shop")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("map")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("cosmetic")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("duel")) {
                return false;
            }
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("menu")) {
                return false;
            }
            return !currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("clique");
        }
        return false;
    }
}