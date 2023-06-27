package net.minecraft.creativetab;

import net.minecraft.item.*;
import java.util.*;
import net.minecraft.enchantment.*;
import net.minecraft.init.*;
import net.minecraft.block.*;

public abstract class CreativeTabs
{
    public static final CreativeTabs[] creativeTabArray;
    public static final CreativeTabs tabBlock;
    public static final CreativeTabs tabDecorations;
    public static final CreativeTabs tabRedstone;
    public static final CreativeTabs tabTransport;
    public static final CreativeTabs tabMisc;
    public static final CreativeTabs tabAllSearch;
    public static final CreativeTabs tabFood;
    public static final CreativeTabs tabTools;
    public static final CreativeTabs tabCombat;
    public static final CreativeTabs tabBrewing;
    public static final CreativeTabs tabMaterials;
    public static final CreativeTabs tabInventory;
    private final int tabIndex;
    private final String tabLabel;
    private String theTexture;
    private boolean hasScrollbar;
    private boolean drawTitle;
    private EnumEnchantmentType[] enchantmentTypes;
    private ItemStack iconItemStack;
    
    public CreativeTabs(final int index, final String label) {
        this.theTexture = "items.png";
        this.hasScrollbar = true;
        this.drawTitle = true;
        this.tabIndex = index;
        this.tabLabel = label;
        CreativeTabs.creativeTabArray[index] = this;
    }
    
    public int getTabIndex() {
        return this.tabIndex;
    }
    
    public String getTabLabel() {
        return this.tabLabel;
    }
    
    public String getTranslatedTabLabel() {
        return "itemGroup." + this.getTabLabel();
    }
    
    public ItemStack getIconItemStack() {
        if (this.iconItemStack == null) {
            this.iconItemStack = new ItemStack(this.getTabIconItem(), 1, this.getIconItemDamage());
        }
        return this.iconItemStack;
    }
    
    public abstract Item getTabIconItem();
    
    public int getIconItemDamage() {
        return 0;
    }
    
    public String getBackgroundImageName() {
        return this.theTexture;
    }
    
    public CreativeTabs setBackgroundImageName(final String texture) {
        this.theTexture = texture;
        return this;
    }
    
    public boolean drawInForegroundOfTab() {
        return this.drawTitle;
    }
    
    public CreativeTabs setNoTitle() {
        this.drawTitle = false;
        return this;
    }
    
    public boolean shouldHidePlayerInventory() {
        return this.hasScrollbar;
    }
    
    public CreativeTabs setNoScrollbar() {
        this.hasScrollbar = false;
        return this;
    }
    
    public int getTabColumn() {
        return this.tabIndex % 6;
    }
    
    public boolean isTabInFirstRow() {
        return this.tabIndex < 6;
    }
    
    public EnumEnchantmentType[] getRelevantEnchantmentTypes() {
        return this.enchantmentTypes;
    }
    
    public CreativeTabs setRelevantEnchantmentTypes(final EnumEnchantmentType... types) {
        this.enchantmentTypes = types;
        return this;
    }
    
    public boolean hasRelevantEnchantmentType(final EnumEnchantmentType enchantmentType) {
        if (this.enchantmentTypes == null) {
            return false;
        }
        for (final EnumEnchantmentType enumenchantmenttype : this.enchantmentTypes) {
            if (enumenchantmenttype == enchantmentType) {
                return true;
            }
        }
        return false;
    }
    
    public void displayAllReleventItems(final List<ItemStack> p_78018_1_) {
        for (final Item item : Item.itemRegistry) {
            if (item != null && item.getCreativeTab() == this) {
                item.getSubItems(item, this, p_78018_1_);
            }
        }
        if (this.getRelevantEnchantmentTypes() != null) {
            this.addEnchantmentBooksToList(p_78018_1_, this.getRelevantEnchantmentTypes());
        }
    }
    
    public void addEnchantmentBooksToList(final List<ItemStack> itemList, final EnumEnchantmentType... enchantmentType) {
        for (final Enchantment enchantment : Enchantment.enchantmentsBookList) {
            if (enchantment != null && enchantment.type != null) {
                boolean flag = false;
                for (int i = 0; i < enchantmentType.length && !flag; ++i) {
                    if (enchantment.type == enchantmentType[i]) {
                        flag = true;
                    }
                }
                if (flag) {
                    itemList.add(Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, enchantment.getMaxLevel())));
                }
            }
        }
    }
    
    static {
        creativeTabArray = new CreativeTabs[12];
        tabBlock = new CreativeTabs(0, "buildingBlocks") {
            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(Blocks.brick_block);
            }
        };
        tabDecorations = new CreativeTabs(1, "decorations") {
            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(Blocks.double_plant);
            }
            
            @Override
            public int getIconItemDamage() {
                return BlockDoublePlant.EnumPlantType.PAEONIA.getMeta();
            }
        };
        tabRedstone = new CreativeTabs(2, "redstone") {
            @Override
            public Item getTabIconItem() {
                return Items.redstone;
            }
        };
        tabTransport = new CreativeTabs(3, "transportation") {
            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(Blocks.golden_rail);
            }
        };
        tabMisc = new CreativeTabs(4, "misc") {
            @Override
            public Item getTabIconItem() {
                return Items.lava_bucket;
            }
        }.setRelevantEnchantmentTypes(EnumEnchantmentType.ALL);
        tabAllSearch = new CreativeTabs(5, "search") {
            @Override
            public Item getTabIconItem() {
                return Items.compass;
            }
        }.setBackgroundImageName("item_search.png");
        tabFood = new CreativeTabs(6, "food") {
            @Override
            public Item getTabIconItem() {
                return Items.apple;
            }
        };
        tabTools = new CreativeTabs(7, "tools") {
            @Override
            public Item getTabIconItem() {
                return Items.iron_axe;
            }
        }.setRelevantEnchantmentTypes(EnumEnchantmentType.DIGGER, EnumEnchantmentType.FISHING_ROD, EnumEnchantmentType.BREAKABLE);
        tabCombat = new CreativeTabs(8, "combat") {
            @Override
            public Item getTabIconItem() {
                return Items.golden_sword;
            }
        }.setRelevantEnchantmentTypes(EnumEnchantmentType.ARMOR, EnumEnchantmentType.ARMOR_FEET, EnumEnchantmentType.ARMOR_HEAD, EnumEnchantmentType.ARMOR_LEGS, EnumEnchantmentType.ARMOR_TORSO, EnumEnchantmentType.BOW, EnumEnchantmentType.WEAPON);
        tabBrewing = new CreativeTabs(9, "brewing") {
            @Override
            public Item getTabIconItem() {
                return Items.potionitem;
            }
        };
        tabMaterials = new CreativeTabs(10, "materials") {
            @Override
            public Item getTabIconItem() {
                return Items.stick;
            }
        };
        tabInventory = new CreativeTabs(11, "inventory") {
            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(Blocks.chest);
            }
        }.setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
    }
}
