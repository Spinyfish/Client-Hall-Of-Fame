package v4n1ty.module.player;

import de.Hero.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import v4n1ty.V4n1ty;
import v4n1ty.module.Category;
import v4n1ty.module.Module;
import v4n1ty.utils.misc.TimerUtils;

import java.util.ArrayList;

public class AutoArmor extends Module {
    private int[] boots;
    private int[] helmet;
    private int[] leggings;
    TimerUtils timer = new TimerUtils();
    private int delay;
    private boolean best;
    private int[] chestplate;

    public AutoArmor() {
        super("AutoArmor", Keyboard.KEY_H, Category.COMBAT);
    }

    @Override
    public void setup() {
        V4n1ty.settingsManager.rSetting(new Setting("Delay", this, 150, 0, 1000, true));
    }

    public static boolean isChestInventory() {
        return Minecraft.getMinecraft().thePlayer.openContainer != null && Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest;
    }

    public static int checkProtection(ItemStack var0) {
        return EnchantmentHelper.getEnchantmentLevel(0, var0);
    }

    public int getFreeSlot() {
        for(int var1 = 35; var1 > 0; --var1) {
            ItemStack var2 = this.mc.thePlayer.inventory.getStackInSlot(var1);
            if (var2 == null) {
                return var1;
            }
        }

        return -1;
    }

    public void onUpdate() {
        if (this.isToggled()){
            if (!this.isChestInventory() && mc.currentScreen instanceof GuiInventory) {
                for (int var2 = 0; var2 < 36; ++var2) {
                    ItemStack var3 = this.mc.thePlayer.inventory.getStackInSlot(var2);
                    if (var3 != null && var3.getItem() instanceof ItemArmor) {
                        ItemArmor var4 = (ItemArmor) this.mc.thePlayer.inventory.getStackInSlot(var2).getItem();
                        int var5 = 0;
                        int var6 = 0;
                        int var7 = 0;
                        if (this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) != null) {
                            ItemArmor var8 = (ItemArmor) this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType).getItem();
                            ItemStack var9 = this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType);
                            var5 = var8.getArmorMaterial().getDamageReductionAmount(var4.armorType);
                            var5 += checkProtection(this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType));
                            var6 = var9.getItemDamage();
                            var7 = var4.getArmorMaterial().getDamageReductionAmount(var4.armorType);
                            var7 += checkProtection(this.mc.thePlayer.inventory.getStackInSlot(var2));
                        }

                        if (this.getFreeSlot() != -1 && this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) != null && (var7 > var5 || var7 == var5 && var3.getItemDamage() < var6)) {
                            if (var2 < 9) {
                                var2 += 36;
                            }

                            this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, 5 + var4.armorType, 0, 4, this.mc.thePlayer);
                            this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, var2, 0, 1, this.mc.thePlayer);
                        }

                        if (this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) == null && this.timer.hasTimeElapsed((long) V4n1ty.settingsManager.getSettingByName("Delay").getValDouble(), true)) {
                            if (var2 < 9) {
                                var2 += 36;
                            }

                            this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, var2, 0, 1, this.mc.thePlayer);
                        }
                    }
                }
            }
        }
    }
}