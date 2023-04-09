package tech.dort.dortware.impl.modules.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.time.Stopwatch;

public class AutoArmor extends Module {

    private final NumberValue delay = new NumberValue("Delay", this, 150, 25, 500, SliderUnit.MS, true);

    public AutoArmor(ModuleData moduleData) {
        super(moduleData);
        register(delay);
    }

    private final Stopwatch timer = new Stopwatch();

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (!AutoPot.isPotting()) {
            mc.playerController.updateController();
            equipArmor(timer, delay.getValue().intValue());
        }
    }

    public static boolean equipArmor(Stopwatch stopwatch, int delay) {

        for (int i = 9; i < 45; i++) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
                continue;

            ItemStack stackInSlot = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (!(stackInSlot.getItem() instanceof ItemArmor))
                continue;

            if (getArmorItemsEquipSlot(stackInSlot, false) == -1)
                continue;

            if (mc.thePlayer.getEquipmentInSlot(getArmorItemsEquipSlot(stackInSlot, true)) == null) {
                if (stopwatch.timeElapsed(delay)) {
                    mc.thePlayer.sendQueue.addToSendQueue(
                            new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));

                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0,
                            mc.thePlayer);
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
                            getArmorItemsEquipSlot(stackInSlot, false), 0, 0, mc.thePlayer);

                    stopwatch.resetTime();
                    return true;
                }
            } else {
                ItemStack stackInEquipmentSlot = mc.thePlayer
                        .getEquipmentInSlot(getArmorItemsEquipSlot(stackInSlot, true));
                if (compareProtection(stackInSlot, stackInEquipmentSlot) == stackInSlot) {
                    System.out.println("Stack in slot : " + stackInSlot.getUnlocalizedName());
                    if (stopwatch.timeElapsed(delay)) {
                        int finalI1 = i;
                        mc.thePlayer.sendQueue.addToSendQueue(
                                new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));

                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, finalI1, 0, 0,
                                mc.thePlayer);
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
                                getArmorItemsEquipSlot(stackInSlot, false), 0, 0, mc.thePlayer);
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, finalI1, 0, 0,
                                mc.thePlayer);

                        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                        stopwatch.resetTime();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getArmorItemsEquipSlot(ItemStack stack, boolean equipmentSlot) {
        if (stack.getUnlocalizedName().contains("helmet"))
            return equipmentSlot ? 4 : 5;
        if (stack.getUnlocalizedName().contains("chestplate"))
            return equipmentSlot ? 3 : 6;
        if (stack.getUnlocalizedName().contains("leggings"))
            return equipmentSlot ? 2 : 7;
        if (stack.getUnlocalizedName().contains("boots"))
            return equipmentSlot ? 1 : 8;
        return -1;
    }

    public static ItemStack compareProtection(ItemStack item1, ItemStack item2) {
        if (!(item1.getItem() instanceof ItemArmor) && !(item2.getItem() instanceof ItemArmor))
            return null;

        if (!(item1.getItem() instanceof ItemArmor))
            return item2;

        if (!(item2.getItem() instanceof ItemArmor))
            return item1;

        if (getArmorProtection(item1) > getArmorProtection(item2)) {
            return item1;
        } else if (getArmorProtection(item2) > getArmorProtection(item1)) {
            return item2;
        }

        return null;
    }

    public static double getArmorProtection(ItemStack armorStack) {
        if (!(armorStack.getItem() instanceof ItemArmor))
            return 0.0;

        final ItemArmor armorItem = (ItemArmor) armorStack.getItem();
        final double protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.field_180310_c.effectId,
                armorStack);

        return armorItem.damageReduceAmount + ((6 + protectionLevel * protectionLevel) * 0.75 / 3);

    }

    @Override
    public String getSuffix() {
        return " \2477" + delay.getValue().intValue();
    }
}
