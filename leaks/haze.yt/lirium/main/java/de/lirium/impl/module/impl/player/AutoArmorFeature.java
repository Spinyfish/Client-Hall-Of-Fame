package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.GuiHandleEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import lombok.Getter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleFeature.Info(name = "Auto Armor", category = ModuleFeature.Category.PLAYER, description = "Automatically equips armor")
public class AutoArmorFeature extends ModuleFeature {

    private final HashMap<ItemArmor, Integer> pendingSlots = new HashMap<>();

    private final HashMap<ItemArmor, Integer> toDeleteSlots = new HashMap<>();

    @Getter
    private final TimeHelper timeHelper = new TimeHelper(), openTimeHelper = new TimeHelper();

    @Getter
    private final TimeHelper heqTimer = new TimeHelper();

    @Value(name = "No Inventory")
    final CheckBox noInventory = new CheckBox(false);

    @Value(name = "Hotbar Support")
    final CheckBox hotbar = new CheckBox(true);

    @Value(name = "Try to replace")
    final CheckBox tryToReplace = new CheckBox(false, new Dependency<>(hotbar, true));

    @Value(name = "Open Delay")
    final SliderSetting<Long> openDelay = new SliderSetting<>(120L, 1L, 1000L, new Dependency<>(noInventory, false));

    @Value(name = "Equip Delay")
    final SliderSetting<Long> equipDelay = new SliderSetting<>(120L, 1L, 1000L);

    @Value(name = "Hotbar Equip delay")
    final SliderSetting<Long> heq = new SliderSetting<>(120L, 1L, 1000L, new Dependency<>(hotbar, true));


    @EventHandler
    public final Listener<GuiHandleEvent> guiHandleEventListener = e -> {

        int[] bestArmorSlots = new int[4];
        int[] bestArmorValues = new int[4];


        final InventoryPlayer inventory = mc.player.inventory;

        for (int type = 0; type < 4; type++) {
            bestArmorSlots[type] = -1;

            ItemStack stack = inventory.armorItemInSlot(type);
            if (!(stack.getItem() instanceof ItemArmor)) continue;

            ItemArmor item = (ItemArmor) stack.getItem();
            bestArmorValues[type] = getArmorValue(item, stack);
        }

        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);

            if (!(stack.getItem() instanceof ItemArmor)) continue;

            final ItemArmor item = (ItemArmor) stack.getItem();
            final int armorType = item.armorType.getIndex(), armorValue = getArmorValue(item, stack);

            if (armorValue > bestArmorValues[armorType]) {
                bestArmorSlots[armorType] = slot;
                bestArmorValues[armorType] = armorValue;
            }
        }


        int[] bestArmorSlotsHotbar = new int[4];
        int[] bestArmorValuesHotbar = new int[4];

        if (!noInventory.getValue() && hotbar.getValue()) {
            for (int i = 0; i < 9; ++i) {
                final ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                if (itemStack.getItem() instanceof ItemArmor) {
                    final ItemArmor item = (ItemArmor) itemStack.getItem();
                    final ItemStack stack = inventory.getStackInSlot(i);

                    if (!(stack.getItem() instanceof ItemArmor)) continue;

                    final int armorType = item.armorType.getIndex(), armorValue = getArmorValue(item, stack);
                    final boolean allowed = (tryToReplace.getValue() || getPlayer().inventory.armorItemInSlot(armorType).getItem().isAir());
                    if (allowed) {
                        if (armorValue > bestArmorValues[armorType] && allowed) {
                            bestArmorSlotsHotbar[armorType] = i;
                            bestArmorValuesHotbar[armorType] = armorValue;
                        }
                        pendingSlots.put(item, i);
                    }
                }
            }
        }

        final ArrayList<Integer> types = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(types);

        if (!noInventory.getValue() && hotbar.getValue()) {
            for (int type = 0; type < 4; type++) {
                bestArmorValuesHotbar[type] = -1;

                ItemStack stack = inventory.armorItemInSlot(type);
                if (!(stack.getItem() instanceof ItemArmor)) continue;

                final boolean allowed = (tryToReplace.getValue() || getPlayer().inventory.armorItemInSlot(type).getItem().isAir());
                ItemArmor item = (ItemArmor) stack.getItem();
                if (allowed)
                    bestArmorValuesHotbar[type] = getArmorValue(item, stack);
            }
            if (!(mc.currentScreen instanceof GuiContainer)) {
                if (heqTimer.hasReached(heq.getValue())) {
                    for (final int slot : pendingSlots.values()) {
                        final ItemStack stack = inventory.getStackInSlot(slot);

                        if (!(stack.getItem() instanceof ItemArmor)) continue;

                        final ItemArmor item = (ItemArmor) stack.getItem();
                        final int armorType = item.armorType.getIndex(), armorValue = getArmorValue(item, stack);

                        if (armorValue > bestArmorValuesHotbar[armorType]) {
                            bestArmorSlotsHotbar[armorType] = slot;
                            bestArmorValuesHotbar[armorType] = armorValue;
                        } else {
                            pendingSlots.remove(item, slot);
                        }

                        for (int type : types) {
                            final int slot2 = bestArmorSlotsHotbar[type];
                            if (slot2 == -1) {
                                continue;
                            }
                            if (inventory.getFirstEmptyStack() == -1)
                                continue;
                            if (slot2 <= 9) {
                                getPlayer().inventory.currentItem = slot2;
                                mc.rightClickMouse();
                            }
                        }

                        pendingSlots.remove(item, slot);

                    }
                    heqTimer.reset();
                }
            }
        }

        if (!noInventory.getValue() && !(mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiContainerCreative)) {
            openTimeHelper.reset();
            return;
        }

        if (!noInventory.getValue() && !openTimeHelper.hasReached(openDelay.getValue()))
            return;

        for (int type : types) {
            int slot = bestArmorSlots[type];
            if (slot == -1) {
                continue;
            }

            if (inventory.getFirstEmptyStack() == -1)
                continue;

            if (slot < 9) {
                slot += 36;
            }

            mc.playerController.windowClick(0, 8 - type, 0, ClickType.QUICK_MOVE, mc.player);

            if (timeHelper.hasReached(equipDelay.getValue())) {
                mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, mc.player);
                timeHelper.reset();
            }
            break;
        }
    };

    private int getArmorValue(final ItemArmor itemArmor, final ItemStack itemStack) {
        final int damageReduceAmount = itemArmor.damageReduceAmount;

        //Multiplier = 1.2
        final int armorToughness = (int) itemArmor.toughness;

        final int armorType = itemArmor.getArmorMaterial().getDamageReductionAmount(EntityEquipmentSlot.LEGS);

        final Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);

        Enchantment protection = Enchantments.PROTECTION;

        assert protection != null;

        final DamageSource dmgSource = DamageSource.causePlayerDamage(mc.player);

        final int prtLvl = EnchantmentHelper.getEnchantmentLevel(protection, itemStack);

        final AtomicInteger score = new AtomicInteger(protection.calcModifierDamage(prtLvl, dmgSource));

        for (int enchantmentsScore : enchantments.values())
            score.addAndGet(enchantmentsScore);

        return damageReduceAmount * 5 + score.get() * 3 + armorToughness + armorType;

    }

}
