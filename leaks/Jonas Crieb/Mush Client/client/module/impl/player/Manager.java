package client.module.impl.player;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;
import client.event.impl.motion.UpdateEvent;
import client.event.impl.packet.PacketReceiveEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.util.player.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemSword;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
@ModuleInfo(name = "Manager", description = "", category = Category.PLAYER, autoEnabled = false)
public class Manager extends Module {
    private int[] chestplate, leggings, boots, helmet;
    private int delay;
    private boolean best;


        public void setup() {
            chestplate = new int[] {311, 307, 315, 303, 299};
            leggings = new int[] {312, 308, 316, 304, 300};
            boots = new int[] {313, 309, 317, 305, 301};
            helmet = new int[] {310, 306, 314, 302, 298};
            delay = 0;
            best = true;
        }

    @EventLink()
    public final Listener<UpdateEvent> onUpdate = event -> {
        setup();
            autoArmor();
            betterArmor();
        };

        public void autoArmor() {
            if(best)
                return;
            int item = -1;
            delay += 1;
            if(delay >= 10) {
                if(mc.thePlayer.inventory.armorInventory[0] == null) {
                    int[] boots;
                    int length = (boots = this.boots).length;
                    for(int i =0; i < length; i++) {
                        int id = boots[i];
                        if(getItem(id) != -1) {
                            item = getItem(id);
                            break;
                        }
                    }
                }
                if(mc.thePlayer.inventory.armorInventory[1] == null) {
                    int[] leggings;
                    int length = (leggings = this.leggings).length;
                    for(int i = 0; i < length; i++) {
                        int id = leggings[i];
                        if(getItem(id) != -1) {
                            item = getItem(id);
                            break;
                        }
                    }
                }
                if(mc.thePlayer.inventory.armorInventory[2] == null) {
                    int[] chestplate;
                    int length = (chestplate = this.chestplate).length;
                    for(int i = 0; i < length; i++) {
                        int id = chestplate[i];
                        if(getItem(id) != -1) {
                            item = getItem(id);
                            break;
                        }
                    }
                }
                if(mc.thePlayer.inventory.armorInventory[3] == null) {
                    int[] helmet;
                    int length = (helmet = this.helmet).length;
                    for(int i = 0; i < length; i++) {
                        int id = helmet[i];
                        if(getItem(id) != -1) {
                            item = getItem(id);
                            break;
                        }
                    }
                }
                if(item != -1) {
                    mc.playerController.windowClick(0, item, 0, 1, mc.thePlayer);
                    delay = 0;
                }
            }
        }

        public void betterArmor() {
            if (!best)
                return;
            delay += 1;
            if (delay >= 10 && (mc.thePlayer.openContainer == null || mc.thePlayer.openContainer.windowId == 0)) {
                boolean switchArmor = false;
                int item = -1;
                int[] array;
                int i;
                if (mc.thePlayer.inventory.armorInventory[0] == null) {
                    int j = (array = this.boots).length;
                    for (i = 0; i < j; i++) {
                        int id = array[i];
                        if (getItem(id) != -1) {
                            item = getItem(id);
                            break;
                        }
                    }
                }
                if (isBetterArmor(0, this.boots)) {
                    item = 8;
                    switchArmor = true;
                }
                if (mc.thePlayer.inventory.armorInventory[3] == null) {
                    int j = (array = this.helmet).length;
                    for (i = 0; i < j; i++) {
                        int id = array[i];
                        if (getItem(id) != -1) {
                            item = getItem(id);
                            break;
                        }
                    }
                }
                if (isBetterArmor(3, this.helmet)) {
                    item = 5;
                    switchArmor = true;
                }
                if (mc.thePlayer.inventory.armorInventory[1] == null) {
                    int j = (array = this.leggings).length;
                    for (i = 0; i < j; i++) {
                        int id = array[i];
                        if (getItem(id) != -1) {
                            item = getItem(id);
                            break;
                        }
                    }
                }
                if (isBetterArmor(1, this.leggings)) {
                    item = 7;
                    switchArmor = true;
                }
                if (mc.thePlayer.inventory.armorInventory[2] == null) {
                    int j = (array = this.chestplate).length;
                    for (i = 0; i < j; i++) {
                        int id = array[i];
                        if (getItem(id) != -1) {
                            item = getItem(id);
                            break;
                        }
                    }
                }
                if (isBetterArmor(2, this.chestplate)) {
                    item = 6;
                    switchArmor = true;
                }
                boolean b = false;
                ItemStack[] stackArray;
                int k = (stackArray = mc.thePlayer.inventory.mainInventory).length;
                for (int j = 0; j < k; j++) {
                    ItemStack stack = stackArray[j];
                    if (stack == null) {
                        b = true;
                        break;
                    }
                }
                switchArmor = switchArmor && !b;
                if (item != -1) {
                    mc.playerController.windowClick(0, item, 0, switchArmor ? 4 : 1, mc.thePlayer);
                    delay = 0;
                }

            }

        }
    public static boolean isBetterArmor(int slot, int[] armorType) {
        if(Minecraft.getMinecraft().thePlayer.inventory.armorInventory[slot] != null) {
            int currentIndex = 0;
            int invIndex = 0;
            int finalCurrentIndex = -1;
            int finalInvIndex = -1;
            int[] array;
            int j = (array = armorType).length;
            for(int i = 0; i < j; i++) {
                int armor = array[i];
                if(Item.getIdFromItem(Minecraft.getMinecraft().thePlayer.inventory.armorInventory[slot].getItem()) == armor) {
                    finalCurrentIndex = currentIndex;
                    break;
                }
                currentIndex++;
            }
            j = (array = armorType).length;
            for(int i = 0; i < j; i++) {
                int armor = array[i];
                if(getItem(armor) != -1) {
                    finalInvIndex = invIndex;
                    break;
                }
                invIndex++;
            }
            if(finalInvIndex > -1)
                return finalInvIndex < finalCurrentIndex;
        }
        return false;
    }

    public static int getItem(int id) {
        for(int i = 9; i < 45; i++) {
            ItemStack item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
            if(item != null && Item.getIdFromItem(item.getItem()) == id)
                return i;
        }
        return -1;
    }
}


