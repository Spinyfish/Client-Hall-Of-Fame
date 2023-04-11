package byron.Mono.module.impl.combat;

import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

@ModuleInterface(name = "AutoArmor", description = "Automatically equips armor.", category = Category.Combat)
public class AutoArmor extends Module {
    private int[] boots;
    private int[] helmet;
    private int[] leggings;
    private int delay;
    private boolean best;
    private int[] chestplate;


    public boolean isChestInventory() {
        return mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest;
    }

    public static int checkProtection(ItemStack var0) {
        return EnchantmentHelper.getEnchantmentLevel(0, var0);
    }

    public int getFreeSlot() {

        Minecraft var10000 = mc;
        if (Minecraft.currentScreen instanceof GuiInventory) {
            for (int var1 = 35; var1 > 0; --var1) {
                ItemStack var2 = mc.thePlayer.inventory.getStackInSlot(var1);
                if (var2 == null) {
                    return var1;
                }
            }
        }


        return -1;
    }


    int var2;
    ItemStack var3;
    ItemArmor var4;
    int var5;
    int var6;
    int var7;
    ItemArmor var8;
    ItemStack var9;
    Minecraft var10000 = mc;

    @Subscribe
    public void onUpdate(EventUpdate e)
    {
        if(mc.currentScreen instanceof GuiInventory)
        {
            if (!this.isChestInventory()) {
                for (var2 = 0; var2 < 36; ++var2) {
                    var3 = mc.thePlayer.inventory.getStackInSlot(var2);
                    if (var3 != null && var3.getItem() instanceof ItemArmor) {
                        var4 = (ItemArmor) mc.thePlayer.inventory.getStackInSlot(var2).getItem();
                        var5 = 0;
                        var6 = 0;
                        var7 = 0;
                        if (mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) != null) {
                            var8 = (ItemArmor) mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType).getItem();
                            var9 = mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType);
                            var5 = var8.getArmorMaterial().getDamageReductionAmount(var4.armorType);
                            var5 += checkProtection(mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType));
                            var6 = var9.getItemDamage();
                            var7 = var4.getArmorMaterial().getDamageReductionAmount(var4.armorType);
                            var7 += checkProtection(mc.thePlayer.inventory.getStackInSlot(var2));
                        }

                        if (this.getFreeSlot() != -1 && mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) != null && (var7 > var5 || var7 == var5 && var3.getItemDamage() < var6)) {
                            if (var2 < 9) {
                                var2 += 36;
                            }

                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 5 + var4.armorType, 0, 4, mc.thePlayer);
                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, var2, 0, 1, mc.thePlayer);
                        }

                        if (mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) == null) {
                            if (var2 < 9) {
                                var2 += 36;
                            }

                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, var2, 0, 1, mc.thePlayer);
                        }
                    }
                }

                return;
            }
        }
        if(this.isChestInventory())
        {
            for (var2 = 0; var2 < 36; ++var2) {
                var3 = mc.thePlayer.inventory.getStackInSlot(var2);
                if (var3 != null && var3.getItem() instanceof ItemArmor) {
                    var4 = (ItemArmor) mc.thePlayer.inventory.getStackInSlot(var2).getItem();
                    var5 = 0;
                    var6 = 0;
                    var7 = 0;
                    if (mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) != null) {
                        var8 = (ItemArmor) mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType).getItem();
                        var9 = mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType);
                        var5 = var8.getArmorMaterial().getDamageReductionAmount(var4.armorType);
                        var5 += checkProtection(mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType));
                        var6 = var9.getItemDamage();
                        var7 = var4.getArmorMaterial().getDamageReductionAmount(var4.armorType);
                        var7 += checkProtection(mc.thePlayer.inventory.getStackInSlot(var2));
                    }

                    if (this.getFreeSlot() != -1 && mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) != null && (var7 > var5 || var7 == var5 && var3.getItemDamage() < var6)) {
                        if (var2 < 9) {
                            var2 += 36;
                        }

                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 5 + var4.armorType, 0, 4, mc.thePlayer);
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, var2, 0, 1, mc.thePlayer);
                    }

                    if (mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) == null) {
                        if (var2 < 9) {
                            var2 += 36;
                        }

                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, var2, 0, 1, mc.thePlayer);
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


}
