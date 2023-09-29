package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemPotion extends Item {
    private final Map<Integer, List<PotionEffect>> effectCache = Maps.newHashMap();
    private static final Map<List<PotionEffect>, Integer> SUB_ITEMS_CACHE = Maps.newLinkedHashMap();

    public ItemPotion() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    public List<PotionEffect> getEffects(final ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
            final List<PotionEffect> list1 = Lists.newArrayList();
            final NBTTagList nbttaglist = stack.getTagCompound().getTagList("CustomPotionEffects", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                final NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                final PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound);

                if (potioneffect != null) {
                    list1.add(potioneffect);
                }
            }

            return list1;
        } else {
            List<PotionEffect> list = this.effectCache.get(Integer.valueOf(stack.getMetadata()));

            if (list == null) {
                list = PotionHelper.getPotionEffects(stack.getMetadata(), false);
                this.effectCache.put(Integer.valueOf(stack.getMetadata()), list);
            }

            return list;
        }
    }

    public List<PotionEffect> getEffects(final int meta) {
        List<PotionEffect> list = this.effectCache.get(Integer.valueOf(meta));

        if (list == null) {
            list = PotionHelper.getPotionEffects(meta, false);
            this.effectCache.put(Integer.valueOf(meta), list);
        }

        return list;
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(final ItemStack stack, final World worldIn, final EntityPlayer playerIn) {
        if (!playerIn.capabilities.isCreativeMode) {
            --stack.stackSize;
        }

        if (!worldIn.isRemote) {
            final List<PotionEffect> list = this.getEffects(stack);

            if (list != null) {
                for (final PotionEffect potioneffect : list) {
                    playerIn.addPotionEffect(new PotionEffect(potioneffect));
                }
            }
        }

        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);

        if (!playerIn.capabilities.isCreativeMode) {
            if (stack.stackSize <= 0) {
                return new ItemStack(Items.glass_bottle);
            }

            playerIn.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
        }

        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(final ItemStack stack) {
        return 32;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(final ItemStack stack) {
        return EnumAction.DRINK;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(final ItemStack itemStackIn, final World worldIn, final EntityPlayer playerIn) {
        if (isSplash(itemStackIn.getMetadata())) {
            if (!playerIn.capabilities.isCreativeMode) {
                --itemStackIn.stackSize;
            }

            worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(new EntityPotion(worldIn, playerIn, itemStackIn));
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            return itemStackIn;
        } else {
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
            return itemStackIn;
        }
    }

    /**
     * returns wether or not a potion is a throwable splash potion based on damage value
     */
    public static boolean isSplash(final int meta) {
        return (meta & 16384) != 0;
    }

    public int getColorFromDamage(final int meta) {
        return PotionHelper.getLiquidColor(meta, false);
    }

    public int getColorFromItemStack(final ItemStack stack, final int renderPass) {
        return renderPass > 0 ? 16777215 : this.getColorFromDamage(stack.getMetadata());
    }

    public boolean isEffectInstant(final int meta) {
        final List<PotionEffect> list = this.getEffects(meta);

        if (list != null && !list.isEmpty()) {
            for (final PotionEffect potioneffect : list) {
                if (Potion.potionTypes[potioneffect.getPotionID()].isInstant()) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public String getItemStackDisplayName(final ItemStack stack) {
        if (stack.getMetadata() == 0) {
            return StatCollector.translateToLocal("item.emptyPotion.name").trim();
        } else {
            String s = "";

            if (isSplash(stack.getMetadata())) {
                s = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
            }

            final List<PotionEffect> list = Items.potionitem.getEffects(stack);

            if (list != null && !list.isEmpty()) {
                String s2 = list.get(0).getEffectName();
                s2 = s2 + ".postfix";
                return s + StatCollector.translateToLocal(s2).trim();
            } else {
                final String s1 = PotionHelper.getPotionPrefix(stack.getMetadata());
                return StatCollector.translateToLocal(s1).trim() + " " + super.getItemStackDisplayName(stack);
            }
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param tooltip  All lines to display in the Item's tooltip. This is a List of Strings.
     * @param advanced Whether the setting "Advanced tooltips" is enabled
     */
    public void addInformation(final ItemStack stack, final EntityPlayer playerIn, final List<String> tooltip, final boolean advanced) {
        if (stack.getMetadata() != 0) {
            final List<PotionEffect> list = Items.potionitem.getEffects(stack);
            final Multimap<String, AttributeModifier> multimap = HashMultimap.create();

            if (list != null && !list.isEmpty()) {
                for (final PotionEffect potioneffect : list) {
                    String s1 = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
                    final Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    final Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();

                    if (map != null && map.size() > 0) {
                        for (final Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
                            final AttributeModifier attributemodifier = entry.getValue();
                            final AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                            multimap.put(entry.getKey().getAttributeUnlocalizedName(), attributemodifier1);
                        }
                    }

                    if (potioneffect.getAmplifier() > 0) {
                        s1 = s1 + " " + StatCollector.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
                    }

                    if (potioneffect.getDuration() > 20) {
                        s1 = s1 + " (" + Potion.getDurationString(potioneffect) + ")";
                    }

                    if (potion.isBadEffect()) {
                        tooltip.add(EnumChatFormatting.RED + s1);
                    } else {
                        tooltip.add(EnumChatFormatting.GRAY + s1);
                    }
                }
            } else {
                final String s = StatCollector.translateToLocal("potion.empty").trim();
                tooltip.add(EnumChatFormatting.GRAY + s);
            }

            if (!multimap.isEmpty()) {
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));

                for (final Entry<String, AttributeModifier> entry1 : multimap.entries()) {
                    final AttributeModifier attributemodifier2 = entry1.getValue();
                    final double d0 = attributemodifier2.getAmount();
                    double d1;

                    if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2) {
                        d1 = attributemodifier2.getAmount();
                    } else {
                        d1 = attributemodifier2.getAmount() * 100.0D;
                    }

                    if (d0 > 0.0D) {
                        tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), new Object[]{ItemStack.DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + entry1.getKey())}));
                    } else if (d0 < 0.0D) {
                        d1 = d1 * -1.0D;
                        tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), new Object[]{ItemStack.DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + entry1.getKey())}));
                    }
                }
            }
        }
    }

    public boolean hasEffect(final ItemStack stack) {
        final List<PotionEffect> list = this.getEffects(stack);
        return list != null && !list.isEmpty();
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     *
     * @param subItems The List of sub-items. This is a List of ItemStacks.
     */
    public void getSubItems(final Item itemIn, final CreativeTabs tab, final List<ItemStack> subItems) {
        super.getSubItems(itemIn, tab, subItems);

        if (SUB_ITEMS_CACHE.isEmpty()) {
            for (int i = 0; i <= 15; ++i) {
                for (int j = 0; j <= 1; ++j) {
                    final int lvt_6_1_;

                    if (j == 0) {
                        lvt_6_1_ = i | 8192;
                    } else {
                        lvt_6_1_ = i | 16384;
                    }

                    for (int l = 0; l <= 2; ++l) {
                        int i1 = lvt_6_1_;

                        if (l != 0) {
                            if (l == 1) {
                                i1 = lvt_6_1_ | 32;
                            } else if (l == 2) {
                                i1 = lvt_6_1_ | 64;
                            }
                        }

                        final List<PotionEffect> list = PotionHelper.getPotionEffects(i1, false);

                        if (list != null && !list.isEmpty()) {
                            SUB_ITEMS_CACHE.put(list, Integer.valueOf(i1));
                        }
                    }
                }
            }
        }

        final Iterator iterator = SUB_ITEMS_CACHE.values().iterator();

        while (iterator.hasNext()) {
            final int j1 = ((Integer) iterator.next()).intValue();
            subItems.add(new ItemStack(itemIn, 1, j1));
        }
    }
}
