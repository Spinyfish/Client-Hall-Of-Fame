package net.minecraft.item;

import com.google.common.base.Predicates;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;

import java.util.List;

public class ItemArmor extends Item {
    /**
     * Holds the 'base' maxDamage that each armorType have.
     */
    private static final int[] maxDamageArray = new int[]{11, 16, 15, 13};
    public static final String[] EMPTY_SLOT_NAMES = new String[]{"minecraft:items/empty_armor_slot_helmet", "minecraft:items/empty_armor_slot_chestplate", "minecraft:items/empty_armor_slot_leggings", "minecraft:items/empty_armor_slot_boots"};
    private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem() {
        protected ItemStack dispenseStack(final IBlockSource source, final ItemStack stack) {
            final BlockPos blockpos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            final int i = blockpos.getX();
            final int j = blockpos.getY();
            final int k = blockpos.getZ();
            final AxisAlignedBB axisalignedbb = new AxisAlignedBB(i, j, k, i + 1, j + 1, k + 1);
            final List<EntityLivingBase> list = source.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, Predicates.and(EntitySelectors.NOT_SPECTATING, new EntitySelectors.ArmoredMob(stack)));

            if (list.size() > 0) {
                final EntityLivingBase entitylivingbase = list.get(0);
                final int l = entitylivingbase instanceof EntityPlayer ? 1 : 0;
                final int i1 = EntityLiving.getArmorPosition(stack);
                final ItemStack itemstack = stack.copy();
                itemstack.stackSize = 1;
                entitylivingbase.setCurrentItemOrArmor(i1 - l, itemstack);

                if (entitylivingbase instanceof EntityLiving) {
                    ((EntityLiving) entitylivingbase).setEquipmentDropChance(i1, 2.0F);
                }

                --stack.stackSize;
                return stack;
            } else {
                return super.dispenseStack(source, stack);
            }
        }
    };

    /**
     * Stores the armor type: 0 is helmet, 1 is plate, 2 is legs and 3 is boots
     */
    public final int armorType;

    /**
     * Holds the amount of damage that the armor reduces at full durability.
     */
    public final int damageReduceAmount;

    /**
     * Used on RenderPlayer to select the correspondent armor to be rendered on the player: 0 is cloth, 1 is chain, 2 is
     * iron, 3 is diamond and 4 is gold.
     */
    public final int renderIndex;

    /**
     * The EnumArmorMaterial used for this ItemArmor
     */
    private final ItemArmor.ArmorMaterial material;

    public ItemArmor(final ItemArmor.ArmorMaterial material, final int renderIndex, final int armorType) {
        this.material = material;
        this.armorType = armorType;
        this.renderIndex = renderIndex;
        this.damageReduceAmount = material.getDamageReductionAmount(armorType);
        this.setMaxDamage(material.getDurability(armorType));
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabCombat);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserBehavior);
    }

    public int getColorFromItemStack(final ItemStack stack, final int renderPass) {
        if (renderPass > 0) {
            return 16777215;
        } else {
            int i = this.getColor(stack);

            if (i < 0) {
                i = 16777215;
            }

            return i;
        }
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability() {
        return this.material.getEnchantability();
    }

    /**
     * Return the armor material for this armor item.
     */
    public ItemArmor.ArmorMaterial getArmorMaterial() {
        return this.material;
    }

    /**
     * Return whether the specified armor ItemStack has a color.
     */
    public boolean hasColor(final ItemStack stack) {
        return this.material == ArmorMaterial.LEATHER && (stack.hasTagCompound() && (stack.getTagCompound().hasKey("display", 10) && stack.getTagCompound().getCompoundTag("display").hasKey("color", 3)));
    }

    /**
     * Return the color for the specified armor ItemStack.
     */
    public int getColor(final ItemStack stack) {
        if (this.material != ItemArmor.ArmorMaterial.LEATHER) {
            return -1;
        } else {
            final NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (nbttagcompound != null) {
                final NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

                if (nbttagcompound1 != null && nbttagcompound1.hasKey("color", 3)) {
                    return nbttagcompound1.getInteger("color");
                }
            }

            return 10511680;
        }
    }

    /**
     * Remove the color from the specified armor ItemStack.
     */
    public void removeColor(final ItemStack stack) {
        if (this.material == ItemArmor.ArmorMaterial.LEATHER) {
            final NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (nbttagcompound != null) {
                final NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

                if (nbttagcompound1.hasKey("color")) {
                    nbttagcompound1.removeTag("color");
                }
            }
        }
    }

    /**
     * Sets the color of the specified armor ItemStack
     */
    public void setColor(final ItemStack stack, final int color) {
        if (this.material != ItemArmor.ArmorMaterial.LEATHER) {
            throw new UnsupportedOperationException("Can't dye non-leather!");
        } else {
            NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (nbttagcompound == null) {
                nbttagcompound = new NBTTagCompound();
                stack.setTagCompound(nbttagcompound);
            }

            final NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

            if (!nbttagcompound.hasKey("display", 10)) {
                nbttagcompound.setTag("display", nbttagcompound1);
            }

            nbttagcompound1.setInteger("color", color);
        }
    }

    /**
     * Return whether this item is repairable in an anvil.
     *
     * @param toRepair The ItemStack to be repaired
     * @param repair   The ItemStack that should repair this Item (leather for leather armor, etc.)
     */
    public boolean getIsRepairable(final ItemStack toRepair, final ItemStack repair) {
        return this.material.getRepairItem() == repair.getItem() || super.getIsRepairable(toRepair, repair);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(final ItemStack itemStackIn, final World worldIn, final EntityPlayer playerIn) {
        final int i = EntityLiving.getArmorPosition(itemStackIn) - 1;
        final ItemStack itemstack = playerIn.getCurrentArmor(i);

        if (itemstack == null) {
            playerIn.setCurrentItemOrArmor(i, itemStackIn.copy());
            itemStackIn.stackSize = 0;
        }

        return itemStackIn;
    }

    public enum ArmorMaterial {
        LEATHER("leather", 5, new int[]{1, 3, 2, 1}, 15),
        CHAIN("chainmail", 15, new int[]{2, 5, 4, 1}, 12),
        IRON("iron", 15, new int[]{2, 6, 5, 2}, 9),
        GOLD("gold", 7, new int[]{2, 5, 3, 1}, 25),
        DIAMOND("diamond", 33, new int[]{3, 8, 6, 3}, 10);

        private final String name;
        private final int maxDamageFactor;
        private final int[] damageReductionAmountArray;
        private final int enchantability;

        ArmorMaterial(final String name, final int maxDamage, final int[] reductionAmounts, final int enchantability) {
            this.name = name;
            this.maxDamageFactor = maxDamage;
            this.damageReductionAmountArray = reductionAmounts;
            this.enchantability = enchantability;
        }

        public int getDurability(final int armorType) {
            return ItemArmor.maxDamageArray[armorType] * this.maxDamageFactor;
        }

        public int getDamageReductionAmount(final int armorType) {
            return this.damageReductionAmountArray[armorType];
        }

        public int getEnchantability() {
            return this.enchantability;
        }

        public Item getRepairItem() {
            return this == LEATHER ? Items.leather : (this == CHAIN ? Items.iron_ingot : (this == GOLD ? Items.gold_ingot : (this == IRON ? Items.iron_ingot : (this == DIAMOND ? Items.diamond : null))));
        }

        public String getName() {
            return this.name;
        }
    }
}
