package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class EnchantmentThorns extends Enchantment {
    public EnchantmentThorns(final int p_i45764_1_, final ResourceLocation p_i45764_2_, final int p_i45764_3_) {
        super(p_i45764_1_, p_i45764_2_, p_i45764_3_, EnumEnchantmentType.ARMOR_TORSO);
        this.setName("thorns");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(final int enchantmentLevel) {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(final int enchantmentLevel) {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel() {
        return 3;
    }

    /**
     * Determines if this enchantment can be applied to a specific ItemStack.
     *
     * @param stack The ItemStack that is attempting to become enchanted with with enchantment.
     */
    public boolean canApply(final ItemStack stack) {
        return stack.getItem() instanceof ItemArmor || super.canApply(stack);
    }

    /**
     * Whenever an entity that has this enchantment on one of its associated items is damaged this method will be
     * called.
     *
     * @param user     An instance of the entity that is associated with this enchantment.
     * @param attacker An instance of the entity that has attacked the using entity.
     * @param level    The level of the enchantment used.
     */
    public void onUserHurt(final EntityLivingBase user, final Entity attacker, final int level) {
        final Random random = user.getRNG();
        final ItemStack itemstack = EnchantmentHelper.getEnchantedItem(Enchantment.thorns, user);

        if (func_92094_a(level, random)) {
            if (attacker != null) {
                attacker.attackEntityFrom(DamageSource.causeThornsDamage(user), (float) func_92095_b(level, random));
                attacker.playSound("damage.thorns", 0.5F, 1.0F);
            }

            if (itemstack != null) {
                itemstack.damageItem(3, user);
            }
        } else if (itemstack != null) {
            itemstack.damageItem(1, user);
        }
    }

    public static boolean func_92094_a(final int p_92094_0_, final Random p_92094_1_) {
        return p_92094_0_ > 0 && p_92094_1_.nextFloat() < 0.15F * (float) p_92094_0_;
    }

    public static int func_92095_b(final int p_92095_0_, final Random p_92095_1_) {
        return p_92095_0_ > 10 ? p_92095_0_ - 10 : 1 + p_92095_1_.nextInt(4);
    }
}
