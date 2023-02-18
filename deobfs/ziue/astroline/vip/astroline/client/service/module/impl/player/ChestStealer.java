/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiChest
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.EnumCreatureAttribute
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ContainerChest
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemEnderPearl
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemPotion
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  vip.astroline.client.service.event.impl.move.EventPostUpdate
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.storage.utils.other.DelayTimer
 *  vip.astroline.client.storage.utils.other.TimeHelper
 */
package vip.astroline.client.service.module.impl.player;

import java.util.Optional;
import java.util.Random;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.apache.commons.lang3.ArrayUtils;
import vip.astroline.client.service.event.impl.move.EventPostUpdate;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.storage.utils.other.DelayTimer;
import vip.astroline.client.storage.utils.other.TimeHelper;

public class ChestStealer
extends Module {
    public static boolean isChest;
    private FloatValue firstItemDelay = new FloatValue("ChestStealer", "First Item Delay", 30.0f, 0.0f, 1000.0f, 10.0f);
    private FloatValue delay = new FloatValue("ChestStealer", "Delay", 30.0f, 0.0f, 1000.0f, 10.0f);
    private BooleanValue trash = new BooleanValue("ChestStealer", "Trash", Boolean.valueOf(false));
    private BooleanValue tools = new BooleanValue("ChestStealer", "Tools", Boolean.valueOf(false));
    private BooleanValue bow = new BooleanValue("ChestStealer", "Bow", Boolean.valueOf(false));
    private final int[] itemHelmet = new int[]{298, 302, 306, 310, 314};
    private final int[] itemChestplate = new int[]{299, 303, 307, 311, 315};
    private final int[] itemLeggings = new int[]{300, 304, 308, 312, 316};
    private final int[] itemBoots = new int[]{301, 305, 309, 313, 317};
    public static TimeHelper openGuiHelper;
    public static DelayTimer time;
    int nextDelay = 0;

    public ChestStealer() {
        super("ChestStealer", Category.Player, 0, false);
    }

    @EventTarget
    public void onUpdate(EventPostUpdate event) {
        if (!GuiChest.firstItem.isDelayComplete((double)this.firstItemDelay.getValueState())) {
            return;
        }
        if (ChestStealer.mc.thePlayer.openContainer == null) return;
        if (!(ChestStealer.mc.thePlayer.openContainer instanceof ContainerChest)) return;
        ContainerChest c = (ContainerChest)ChestStealer.mc.thePlayer.openContainer;
        if (this.isChestEmpty(c) && openGuiHelper.isDelayComplete(800.0) && time.isDelayComplete(400.0)) {
            ChestStealer.mc.thePlayer.closeScreen();
        }
        int i = 0;
        while (i < c.getLowerChestInventory().getSizeInventory()) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null && time.isDelayComplete((double)this.nextDelay) && (this.itemIsUseful(c, i) || this.trash.getValueState())) {
                this.nextDelay = (int)((double)this.delay.getValueState() * ChestStealer.getRandomDoubleInRange(0.75, 1.25));
                if (new Random().nextInt(100) <= 80) {
                    ChestStealer.mc.playerController.windowClick(c.windowId, i, 0, 1, (EntityPlayer)ChestStealer.mc.thePlayer);
                    time.reset();
                }
            }
            ++i;
        }
    }

    private boolean isChestEmpty(ContainerChest c) {
        int i = 0;
        while (i < c.getLowerChestInventory().getSizeInventory()) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null) {
                if (this.itemIsUseful(c, i)) return false;
                if (this.trash.getValueState()) {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }

    public static boolean isStealing() {
        return !time.isDelayComplete(200.0);
    }

    public static double getRandomDoubleInRange(double minDouble, double maxDouble) {
        return minDouble >= maxDouble ? minDouble : new Random().nextDouble() * (maxDouble - minDouble) + minDouble;
    }

    private boolean itemIsUseful(ContainerChest c, int i) {
        ItemStack itemStack = c.getLowerChestInventory().getStackInSlot(i);
        Item item = itemStack.getItem();
        if ((item instanceof ItemAxe || item instanceof ItemPickaxe) && this.tools.getValueState()) {
            return true;
        }
        if (item instanceof ItemFood) {
            return true;
        }
        if ((item instanceof ItemBow || item == Items.arrow) && this.bow.getValue().booleanValue()) {
            return true;
        }
        if (item instanceof ItemSword && this.isBestSword(c, itemStack)) {
            return true;
        }
        if (item instanceof ItemArmor && this.isBestArmor(c, itemStack)) {
            return true;
        }
        if (item instanceof ItemBlock) {
            return true;
        }
        if (!(item instanceof ItemPotion)) return item instanceof ItemEnderPearl;
        return true;
    }

    private boolean isBestSword(ContainerChest c, ItemStack item) {
        float tempdamage;
        int i;
        float swordDamage1 = this.getSwordDamage(item);
        float swordDamage2 = 0.0f;
        for (i = 0; i < 45; ++i) {
            if (!ChestStealer.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !((tempdamage = this.getSwordDamage(ChestStealer.mc.thePlayer.inventoryContainer.getSlot(i).getStack())) >= swordDamage2)) continue;
            swordDamage2 = tempdamage;
        }
        for (i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) == null || !((tempdamage = this.getSwordDamage(c.getLowerChestInventory().getStackInSlot(i))) >= swordDamage2)) continue;
            swordDamage2 = tempdamage;
        }
        return swordDamage1 == swordDamage2;
    }

    private float getSwordDamage(ItemStack itemStack) {
        float damage = 0.0f;
        Optional attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();
        if (!attributeModifier.isPresent()) return damage + EnchantmentHelper.func_152377_a((ItemStack)itemStack, (EnumCreatureAttribute)EnumCreatureAttribute.UNDEFINED);
        damage = (float)((AttributeModifier)attributeModifier.get()).getAmount();
        return damage + EnchantmentHelper.func_152377_a((ItemStack)itemStack, (EnumCreatureAttribute)EnumCreatureAttribute.UNDEFINED);
    }

    private boolean isBestArmor(ContainerChest c, ItemStack item) {
        float temppro;
        int i;
        float itempro1 = ((ItemArmor)item.getItem()).damageReduceAmount;
        float itempro2 = 0.0f;
        if (ChestStealer.isContain(this.itemHelmet, Item.getIdFromItem((Item)item.getItem()))) {
            for (i = 0; i < 45; ++i) {
                if (!ChestStealer.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !ChestStealer.isContain(this.itemHelmet, Item.getIdFromItem((Item)ChestStealer.mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem())) || !((temppro = (float)((ItemArmor)ChestStealer.mc.thePlayer.inventoryContainer.getSlot((int)i).getStack().getItem()).damageReduceAmount) > itempro2)) continue;
                itempro2 = temppro;
            }
            for (i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) == null || !ChestStealer.isContain(this.itemHelmet, Item.getIdFromItem((Item)c.getLowerChestInventory().getStackInSlot(i).getItem())) || !((temppro = (float)((ItemArmor)c.getLowerChestInventory().getStackInSlot((int)i).getItem()).damageReduceAmount) > itempro2)) continue;
                itempro2 = temppro;
            }
        }
        if (ChestStealer.isContain(this.itemChestplate, Item.getIdFromItem((Item)item.getItem()))) {
            for (i = 0; i < 45; ++i) {
                if (!ChestStealer.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !ChestStealer.isContain(this.itemChestplate, Item.getIdFromItem((Item)ChestStealer.mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem())) || !((temppro = (float)((ItemArmor)ChestStealer.mc.thePlayer.inventoryContainer.getSlot((int)i).getStack().getItem()).damageReduceAmount) > itempro2)) continue;
                itempro2 = temppro;
            }
            for (i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) == null || !ChestStealer.isContain(this.itemChestplate, Item.getIdFromItem((Item)c.getLowerChestInventory().getStackInSlot(i).getItem())) || !((temppro = (float)((ItemArmor)c.getLowerChestInventory().getStackInSlot((int)i).getItem()).damageReduceAmount) > itempro2)) continue;
                itempro2 = temppro;
            }
        }
        if (ChestStealer.isContain(this.itemLeggings, Item.getIdFromItem((Item)item.getItem()))) {
            for (i = 0; i < 45; ++i) {
                if (!ChestStealer.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !ChestStealer.isContain(this.itemLeggings, Item.getIdFromItem((Item)ChestStealer.mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem())) || !((temppro = (float)((ItemArmor)ChestStealer.mc.thePlayer.inventoryContainer.getSlot((int)i).getStack().getItem()).damageReduceAmount) > itempro2)) continue;
                itempro2 = temppro;
            }
            for (i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) == null || !ChestStealer.isContain(this.itemLeggings, Item.getIdFromItem((Item)c.getLowerChestInventory().getStackInSlot(i).getItem())) || !((temppro = (float)((ItemArmor)c.getLowerChestInventory().getStackInSlot((int)i).getItem()).damageReduceAmount) > itempro2)) continue;
                itempro2 = temppro;
            }
        }
        if (!ChestStealer.isContain(this.itemBoots, Item.getIdFromItem((Item)item.getItem()))) return itempro1 == itempro2;
        for (i = 0; i < 45; ++i) {
            if (!ChestStealer.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !ChestStealer.isContain(this.itemBoots, Item.getIdFromItem((Item)ChestStealer.mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem())) || !((temppro = (float)((ItemArmor)ChestStealer.mc.thePlayer.inventoryContainer.getSlot((int)i).getStack().getItem()).damageReduceAmount) > itempro2)) continue;
            itempro2 = temppro;
        }
        for (i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) == null || !ChestStealer.isContain(this.itemBoots, Item.getIdFromItem((Item)c.getLowerChestInventory().getStackInSlot(i).getItem())) || !((temppro = (float)((ItemArmor)c.getLowerChestInventory().getStackInSlot((int)i).getItem()).damageReduceAmount) > itempro2)) continue;
            itempro2 = temppro;
        }
        return itempro1 == itempro2;
    }

    public static boolean isContain(int[] arr, int targetValue) {
        return ArrayUtils.contains(arr, targetValue);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    static {
        openGuiHelper = new TimeHelper();
        time = new DelayTimer();
    }
}
