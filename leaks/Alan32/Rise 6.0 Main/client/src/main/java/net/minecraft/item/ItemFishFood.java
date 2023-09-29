package net.minecraft.item;

import com.google.common.collect.Maps;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class ItemFishFood extends ItemFood {
    /**
     * Indicates whether this fish is "cooked" or not.
     */
    private final boolean cooked;

    public ItemFishFood(final boolean cooked) {
        super(0, 0.0F, false);
        this.cooked = cooked;
    }

    public int getHealAmount(final ItemStack stack) {
        final ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
        return this.cooked && itemfishfood$fishtype.canCook() ? itemfishfood$fishtype.getCookedHealAmount() : itemfishfood$fishtype.getUncookedHealAmount();
    }

    public float getSaturationModifier(final ItemStack stack) {
        final ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
        return this.cooked && itemfishfood$fishtype.canCook() ? itemfishfood$fishtype.getCookedSaturationModifier() : itemfishfood$fishtype.getUncookedSaturationModifier();
    }

    public String getPotionEffect(final ItemStack stack) {
        return ItemFishFood.FishType.byItemStack(stack) == ItemFishFood.FishType.PUFFERFISH ? PotionHelper.pufferfishEffect : null;
    }

    protected void onFoodEaten(final ItemStack stack, final World worldIn, final EntityPlayer player) {
        final ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);

        if (itemfishfood$fishtype == ItemFishFood.FishType.PUFFERFISH) {
            player.addPotionEffect(new PotionEffect(Potion.poison.id, 1200, 3));
            player.addPotionEffect(new PotionEffect(Potion.hunger.id, 300, 2));
            player.addPotionEffect(new PotionEffect(Potion.confusion.id, 300, 1));
        }

        super.onFoodEaten(stack, worldIn, player);
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     *
     * @param subItems The List of sub-items. This is a List of ItemStacks.
     */
    public void getSubItems(final Item itemIn, final CreativeTabs tab, final List<ItemStack> subItems) {
        for (final ItemFishFood.FishType itemfishfood$fishtype : ItemFishFood.FishType.values()) {
            if (!this.cooked || itemfishfood$fishtype.canCook()) {
                subItems.add(new ItemStack(this, 1, itemfishfood$fishtype.getMetadata()));
            }
        }
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(final ItemStack stack) {
        final ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
        return this.getUnlocalizedName() + "." + itemfishfood$fishtype.getUnlocalizedName() + "." + (this.cooked && itemfishfood$fishtype.canCook() ? "cooked" : "raw");
    }

    public enum FishType {
        COD(0, "cod", 2, 0.1F, 5, 0.6F),
        SALMON(1, "salmon", 2, 0.1F, 6, 0.8F),
        CLOWNFISH(2, "clownfish", 1, 0.1F),
        PUFFERFISH(3, "pufferfish", 1, 0.1F);

        private static final Map<Integer, ItemFishFood.FishType> META_LOOKUP = Maps.newHashMap();
        private final int meta;
        private final String unlocalizedName;
        private final int uncookedHealAmount;
        private final float uncookedSaturationModifier;
        private final int cookedHealAmount;
        private final float cookedSaturationModifier;
        private boolean cookable = false;

        FishType(final int meta, final String unlocalizedName, final int uncookedHeal, final float uncookedSaturation, final int cookedHeal, final float cookedSaturation) {
            this.meta = meta;
            this.unlocalizedName = unlocalizedName;
            this.uncookedHealAmount = uncookedHeal;
            this.uncookedSaturationModifier = uncookedSaturation;
            this.cookedHealAmount = cookedHeal;
            this.cookedSaturationModifier = cookedSaturation;
            this.cookable = true;
        }

        FishType(final int meta, final String unlocalizedName, final int uncookedHeal, final float uncookedSaturation) {
            this.meta = meta;
            this.unlocalizedName = unlocalizedName;
            this.uncookedHealAmount = uncookedHeal;
            this.uncookedSaturationModifier = uncookedSaturation;
            this.cookedHealAmount = 0;
            this.cookedSaturationModifier = 0.0F;
            this.cookable = false;
        }

        public int getMetadata() {
            return this.meta;
        }

        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }

        public int getUncookedHealAmount() {
            return this.uncookedHealAmount;
        }

        public float getUncookedSaturationModifier() {
            return this.uncookedSaturationModifier;
        }

        public int getCookedHealAmount() {
            return this.cookedHealAmount;
        }

        public float getCookedSaturationModifier() {
            return this.cookedSaturationModifier;
        }

        public boolean canCook() {
            return this.cookable;
        }

        public static ItemFishFood.FishType byMetadata(final int meta) {
            final ItemFishFood.FishType itemfishfood$fishtype = META_LOOKUP.get(Integer.valueOf(meta));
            return itemfishfood$fishtype == null ? COD : itemfishfood$fishtype;
        }

        public static ItemFishFood.FishType byItemStack(final ItemStack stack) {
            return stack.getItem() instanceof ItemFishFood ? byMetadata(stack.getMetadata()) : COD;
        }

        static {
            for (final ItemFishFood.FishType itemfishfood$fishtype : values()) {
                META_LOOKUP.put(Integer.valueOf(itemfishfood$fishtype.getMetadata()), itemfishfood$fishtype);
            }
        }
    }
}
