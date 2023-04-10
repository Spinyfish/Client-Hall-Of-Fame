package crispy.util.player;

import lombok.experimental.UtilityClass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

@UtilityClass
public class ArmorUtils {

    public double getProtectionValue(ItemStack stack) {
        return !(stack.getItem() instanceof ItemArmor) ? 0.0D : (double) ((ItemArmor) stack.getItem()).damageReduceAmount + (double) ((100 - ((ItemArmor) stack.getItem()).damageReduceAmount * 4) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 4) * 0.0075D;
    }

}
