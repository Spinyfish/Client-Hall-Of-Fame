package com.alan.clients.module.impl.render.appleskin;

import com.alan.clients.util.interfaces.InstanceAccess;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

/**
 * @author Auth
 * @since 02/07/2022
 */

@UtilityClass
public class AppleSkinHelper implements InstanceAccess {

    public FoodValues getFoodValues(final ItemStack stack) {
        final ItemFood food = (ItemFood) stack.getItem();
        final int hunger = food != null ? food.getHealAmount(stack) : 0;
        final float saturationModifier = food != null ? food.getSaturationModifier(stack) : 0;

        return new FoodValues(hunger, saturationModifier);
    }

    public static boolean isRottenFood(final ItemStack stack) {
        if (!(stack.getItem() instanceof ItemFood)) {
            return false;
        }

        final ItemFood food = (ItemFood) stack.getItem();

        if (food.getPotionEffect(stack) != null) {
            return Potion.potionTypes[food.getPotionId()].isBadEffect();
        }

        return false;
    }
}