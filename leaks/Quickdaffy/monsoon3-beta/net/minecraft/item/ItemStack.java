/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multimap
 */
package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public final class ItemStack {
    public static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.###");
    public int stackSize;
    public int animationsToGo;
    private Item item;
    private NBTTagCompound stackTagCompound;
    private int itemDamage;
    private EntityItemFrame itemFrame;
    private Block canDestroyCacheBlock = null;
    private boolean canDestroyCacheResult = false;
    private Block canPlaceOnCacheBlock = null;
    private boolean canPlaceOnCacheResult = false;

    public ItemStack(Block blockIn) {
        this(blockIn, 1);
    }

    public ItemStack(Block blockIn, int amount) {
        this(blockIn, amount, 0);
    }

    public ItemStack(Block blockIn, int amount, int meta) {
        this(Item.getItemFromBlock(blockIn), amount, meta);
    }

    public ItemStack(Item itemIn) {
        this(itemIn, 1);
    }

    public ItemStack(Item itemIn, int amount) {
        this(itemIn, amount, 0);
    }

    public ItemStack(Item itemIn, int amount, int meta) {
        this.item = itemIn;
        this.stackSize = amount;
        this.itemDamage = meta;
        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }
    }

    public static ItemStack loadItemStackFromNBT(NBTTagCompound nbt) {
        ItemStack itemstack = new ItemStack();
        itemstack.readFromNBT(nbt);
        return itemstack.getItem() != null ? itemstack : null;
    }

    private ItemStack() {
    }

    public ItemStack splitStack(int amount) {
        ItemStack itemstack = new ItemStack(this.item, amount, this.itemDamage);
        if (this.stackTagCompound != null) {
            itemstack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }
        this.stackSize -= amount;
        return itemstack;
    }

    public Item getItem() {
        return this.item;
    }

    public boolean onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        boolean flag = this.getItem().onItemUse(this, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
        if (flag) {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }
        return flag;
    }

    public float getStrVsBlock(Block blockIn) {
        return this.getItem().getStrVsBlock(this, blockIn);
    }

    public ItemStack useItemRightClick(World worldIn, EntityPlayer playerIn) {
        return this.getItem().onItemRightClick(this, worldIn, playerIn);
    }

    public ItemStack onItemUseFinish(World worldIn, EntityPlayer playerIn) {
        return this.getItem().onItemUseFinish(this, worldIn, playerIn);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        ResourceLocation resourcelocation = Item.itemRegistry.getNameForObject(this.item);
        nbt.setString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        nbt.setByte("Count", (byte)this.stackSize);
        nbt.setShort("Damage", (short)this.itemDamage);
        if (this.stackTagCompound != null) {
            nbt.setTag("tag", this.stackTagCompound);
        }
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.item = nbt.hasKey("id", 8) ? Item.getByNameOrId(nbt.getString("id")) : Item.getItemById(nbt.getShort("id"));
        this.stackSize = nbt.getByte("Count");
        this.itemDamage = nbt.getShort("Damage");
        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }
        if (nbt.hasKey("tag", 10)) {
            this.stackTagCompound = nbt.getCompoundTag("tag");
            if (this.item != null) {
                this.item.updateItemStackNBT(this.stackTagCompound);
            }
        }
    }

    public int getMaxStackSize() {
        return this.getItem().getItemStackLimit();
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
    }

    public boolean isItemStackDamageable() {
        return this.item == null ? false : (this.item.getMaxDamage() <= 0 ? false : !this.hasTagCompound() || !this.getTagCompound().getBoolean("Unbreakable"));
    }

    public boolean getHasSubtypes() {
        return this.item.getHasSubtypes();
    }

    public boolean isItemDamaged() {
        return this.isItemStackDamageable() && this.itemDamage > 0;
    }

    public int getItemDamage() {
        return this.itemDamage;
    }

    public int getMetadata() {
        return this.itemDamage;
    }

    public void setItemDamage(int meta) {
        this.itemDamage = meta;
        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }
    }

    public int getMaxDamage() {
        return this.item.getMaxDamage();
    }

    public boolean attemptDamageItem(int amount, Random rand) {
        if (!this.isItemStackDamageable()) {
            return false;
        }
        if (amount > 0) {
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, this);
            int j = 0;
            for (int k = 0; i > 0 && k < amount; ++k) {
                if (!EnchantmentDurability.negateDamage(this, i, rand)) continue;
                ++j;
            }
            if ((amount -= j) <= 0) {
                return false;
            }
        }
        this.itemDamage += amount;
        return this.itemDamage > this.getMaxDamage();
    }

    public void damageItem(int amount, EntityLivingBase entityIn) {
        if ((!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).capabilities.isCreativeMode) && this.isItemStackDamageable() && this.attemptDamageItem(amount, entityIn.getRNG())) {
            entityIn.renderBrokenItemStack(this);
            --this.stackSize;
            if (entityIn instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer)entityIn;
                entityplayer.triggerAchievement(StatList.objectBreakStats[Item.getIdFromItem(this.item)]);
                if (this.stackSize == 0 && this.getItem() instanceof ItemBow) {
                    entityplayer.destroyCurrentEquippedItem();
                }
            }
            if (this.stackSize < 0) {
                this.stackSize = 0;
            }
            this.itemDamage = 0;
        }
    }

    public void hitEntity(EntityLivingBase entityIn, EntityPlayer playerIn) {
        boolean flag = this.item.hitEntity(this, entityIn, playerIn);
        if (flag) {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }
    }

    public void onBlockDestroyed(World worldIn, Block blockIn, BlockPos pos, EntityPlayer playerIn) {
        boolean flag = this.item.onBlockDestroyed(this, worldIn, blockIn, pos, playerIn);
        if (flag) {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }
    }

    public boolean canHarvestBlock(Block blockIn) {
        return this.item.canHarvestBlock(blockIn);
    }

    public boolean interactWithEntity(EntityPlayer playerIn, EntityLivingBase entityIn) {
        return this.item.itemInteractionForEntity(this, playerIn, entityIn);
    }

    public ItemStack copy() {
        ItemStack itemstack = new ItemStack(this.item, this.stackSize, this.itemDamage);
        if (this.stackTagCompound != null) {
            itemstack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }
        return itemstack;
    }

    public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
        return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? (stackA.stackTagCompound == null && stackB.stackTagCompound != null ? false : stackA.stackTagCompound == null || stackA.stackTagCompound.equals(stackB.stackTagCompound)) : false);
    }

    public static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB) {
        return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemStackEqual(stackB) : false);
    }

    private boolean isItemStackEqual(ItemStack other) {
        return this.stackSize != other.stackSize ? false : (this.item != other.item ? false : (this.itemDamage != other.itemDamage ? false : (this.stackTagCompound == null && other.stackTagCompound != null ? false : this.stackTagCompound == null || this.stackTagCompound.equals(other.stackTagCompound))));
    }

    public static boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
        return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemEqual(stackB) : false);
    }

    public boolean isItemEqual(ItemStack other) {
        return other != null && this.item == other.item && this.itemDamage == other.itemDamage;
    }

    public String getUnlocalizedName() {
        return this.item.getUnlocalizedName(this);
    }

    public static ItemStack copyItemStack(ItemStack stack) {
        return stack == null ? null : stack.copy();
    }

    public String toString() {
        return this.stackSize + "x" + this.item.getUnlocalizedName() + "@" + this.itemDamage;
    }

    public void updateAnimation(World worldIn, Entity entityIn, int inventorySlot, boolean isCurrentItem) {
        if (this.animationsToGo > 0) {
            --this.animationsToGo;
        }
        this.item.onUpdate(this, worldIn, entityIn, inventorySlot, isCurrentItem);
    }

    public void onCrafting(World worldIn, EntityPlayer playerIn, int amount) {
        playerIn.addStat(StatList.objectCraftStats[Item.getIdFromItem(this.item)], amount);
        this.item.onCreated(this, worldIn, playerIn);
    }

    public boolean getIsItemStackEqual(ItemStack p_179549_1_) {
        return this.isItemStackEqual(p_179549_1_);
    }

    public int getMaxItemUseDuration() {
        return this.getItem().getMaxItemUseDuration(this);
    }

    public EnumAction getItemUseAction() {
        return this.getItem().getItemUseAction(this);
    }

    public void onPlayerStoppedUsing(World worldIn, EntityPlayer playerIn, int timeLeft) {
        this.getItem().onPlayerStoppedUsing(this, worldIn, playerIn, timeLeft);
    }

    public boolean hasTagCompound() {
        return this.stackTagCompound != null;
    }

    public NBTTagCompound getTagCompound() {
        return this.stackTagCompound;
    }

    public NBTTagCompound getSubCompound(String key, boolean create) {
        if (this.stackTagCompound != null && this.stackTagCompound.hasKey(key, 10)) {
            return this.stackTagCompound.getCompoundTag(key);
        }
        if (create) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            this.setTagInfo(key, nbttagcompound);
            return nbttagcompound;
        }
        return null;
    }

    public NBTTagList getEnchantmentTagList() {
        return this.stackTagCompound == null ? null : this.stackTagCompound.getTagList("ench", 10);
    }

    public void setTagCompound(NBTTagCompound nbt) {
        this.stackTagCompound = nbt;
    }

    public String getDisplayName() {
        NBTTagCompound nbttagcompound;
        String s = this.getItem().getItemStackDisplayName(this);
        if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10) && (nbttagcompound = this.stackTagCompound.getCompoundTag("display")).hasKey("Name", 8)) {
            s = nbttagcompound.getString("Name");
        }
        return s;
    }

    public ItemStack setStackDisplayName(String displayName) {
        if (this.stackTagCompound == null) {
            this.stackTagCompound = new NBTTagCompound();
        }
        if (!this.stackTagCompound.hasKey("display", 10)) {
            this.stackTagCompound.setTag("display", new NBTTagCompound());
        }
        this.stackTagCompound.getCompoundTag("display").setString("Name", displayName);
        return this;
    }

    public void clearCustomName() {
        if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10)) {
            NBTTagCompound nbttagcompound = this.stackTagCompound.getCompoundTag("display");
            nbttagcompound.removeTag("Name");
            if (nbttagcompound.hasNoTags()) {
                this.stackTagCompound.removeTag("display");
                if (this.stackTagCompound.hasNoTags()) {
                    this.setTagCompound(null);
                }
            }
        }
    }

    public boolean hasDisplayName() {
        return this.stackTagCompound == null ? false : (!this.stackTagCompound.hasKey("display", 10) ? false : this.stackTagCompound.getCompoundTag("display").hasKey("Name", 8));
    }

    public List<String> getTooltip(EntityPlayer playerIn, boolean advanced) {
        NBTTagList nbttaglist3;
        NBTTagList nbttaglist2;
        Multimap<String, AttributeModifier> multimap;
        ArrayList list = Lists.newArrayList();
        String s = this.getDisplayName();
        if (this.hasDisplayName()) {
            s = (Object)((Object)EnumChatFormatting.ITALIC) + s;
        }
        s = s + (Object)((Object)EnumChatFormatting.RESET);
        if (advanced) {
            String s1 = "";
            if (s.length() > 0) {
                s = s + " (";
                s1 = ")";
            }
            int i = Item.getIdFromItem(this.item);
            s = this.getHasSubtypes() ? s + String.format("#%04d/%d%s", i, this.itemDamage, s1) : s + String.format("#%04d%s", i, s1);
        } else if (!this.hasDisplayName() && this.item == Items.filled_map) {
            s = s + " #" + this.itemDamage;
        }
        list.add(s);
        int i1 = 0;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("HideFlags", 99)) {
            i1 = this.stackTagCompound.getInteger("HideFlags");
        }
        if ((i1 & 0x20) == 0) {
            this.item.addInformation(this, playerIn, list, advanced);
        }
        if (this.hasTagCompound()) {
            NBTTagList nbttaglist;
            if ((i1 & 1) == 0 && (nbttaglist = this.getEnchantmentTagList()) != null) {
                for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                    short k = nbttaglist.getCompoundTagAt(j).getShort("id");
                    short l = nbttaglist.getCompoundTagAt(j).getShort("lvl");
                    if (Enchantment.getEnchantmentById(k) == null) continue;
                    list.add(Enchantment.getEnchantmentById(k).getTranslatedName(l));
                }
            }
            if (this.stackTagCompound.hasKey("display", 10)) {
                NBTTagList nbttaglist1;
                NBTTagCompound nbttagcompound = this.stackTagCompound.getCompoundTag("display");
                if (nbttagcompound.hasKey("color", 3)) {
                    if (advanced) {
                        list.add("Color: #" + Integer.toHexString(nbttagcompound.getInteger("color")).toUpperCase());
                    } else {
                        list.add((Object)((Object)EnumChatFormatting.ITALIC) + StatCollector.translateToLocal("item.dyed"));
                    }
                }
                if (nbttagcompound.getTagId("Lore") == 9 && (nbttaglist1 = nbttagcompound.getTagList("Lore", 8)).tagCount() > 0) {
                    for (int j1 = 0; j1 < nbttaglist1.tagCount(); ++j1) {
                        list.add((Object)((Object)EnumChatFormatting.DARK_PURPLE) + "" + (Object)((Object)EnumChatFormatting.ITALIC) + nbttaglist1.getStringTagAt(j1));
                    }
                }
            }
        }
        if (!(multimap = this.getAttributeModifiers()).isEmpty() && (i1 & 2) == 0) {
            list.add("");
            for (Map.Entry entry : multimap.entries()) {
                AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
                double d0 = attributemodifier.getAmount();
                if (attributemodifier.getID() == Item.itemModifierUUID) {
                    d0 += (double)EnchantmentHelper.func_152377_a(this, EnumCreatureAttribute.UNDEFINED);
                }
                double d1 = attributemodifier.getOperation() != 1 && attributemodifier.getOperation() != 2 ? d0 : d0 * 100.0;
                if (d0 > 0.0) {
                    list.add((Object)((Object)EnumChatFormatting.BLUE) + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier.getOperation(), DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())));
                    continue;
                }
                if (!(d0 < 0.0)) continue;
                list.add((Object)((Object)EnumChatFormatting.RED) + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier.getOperation(), DECIMALFORMAT.format(d1 *= -1.0), StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())));
            }
        }
        if (this.hasTagCompound() && this.getTagCompound().getBoolean("Unbreakable") && (i1 & 4) == 0) {
            list.add((Object)((Object)EnumChatFormatting.BLUE) + StatCollector.translateToLocal("item.unbreakable"));
        }
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9) && (i1 & 8) == 0 && (nbttaglist2 = this.stackTagCompound.getTagList("CanDestroy", 8)).tagCount() > 0) {
            list.add("");
            list.add((Object)((Object)EnumChatFormatting.GRAY) + StatCollector.translateToLocal("item.canBreak"));
            for (int k1 = 0; k1 < nbttaglist2.tagCount(); ++k1) {
                Block block = Block.getBlockFromName(nbttaglist2.getStringTagAt(k1));
                if (block != null) {
                    list.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + block.getLocalizedName());
                    continue;
                }
                list.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + "missingno");
            }
        }
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9) && (i1 & 0x10) == 0 && (nbttaglist3 = this.stackTagCompound.getTagList("CanPlaceOn", 8)).tagCount() > 0) {
            list.add("");
            list.add((Object)((Object)EnumChatFormatting.GRAY) + StatCollector.translateToLocal("item.canPlace"));
            for (int l1 = 0; l1 < nbttaglist3.tagCount(); ++l1) {
                Block block1 = Block.getBlockFromName(nbttaglist3.getStringTagAt(l1));
                if (block1 != null) {
                    list.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + block1.getLocalizedName());
                    continue;
                }
                list.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + "missingno");
            }
        }
        if (advanced) {
            if (this.isItemDamaged()) {
                list.add("Durability: " + (this.getMaxDamage() - this.getItemDamage()) + " / " + this.getMaxDamage());
            }
            list.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + Item.itemRegistry.getNameForObject(this.item).toString());
            if (this.hasTagCompound()) {
                list.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + "NBT: " + this.getTagCompound().getKeySet().size() + " tag(s)");
            }
        }
        return list;
    }

    public boolean hasEffect() {
        return this.getItem().hasEffect(this);
    }

    public EnumRarity getRarity() {
        return this.getItem().getRarity(this);
    }

    public boolean isItemEnchantable() {
        return !this.getItem().isItemTool(this) ? false : !this.isItemEnchanted();
    }

    public void addEnchantment(Enchantment ench, int level) {
        if (this.stackTagCompound == null) {
            this.setTagCompound(new NBTTagCompound());
        }
        if (!this.stackTagCompound.hasKey("ench", 9)) {
            this.stackTagCompound.setTag("ench", new NBTTagList());
        }
        NBTTagList nbttaglist = this.stackTagCompound.getTagList("ench", 10);
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setShort("id", (short)ench.effectId);
        nbttagcompound.setShort("lvl", (byte)level);
        nbttaglist.appendTag(nbttagcompound);
    }

    public boolean isItemEnchanted() {
        return this.stackTagCompound != null && this.stackTagCompound.hasKey("ench", 9);
    }

    public void setTagInfo(String key, NBTBase value) {
        if (this.stackTagCompound == null) {
            this.setTagCompound(new NBTTagCompound());
        }
        this.stackTagCompound.setTag(key, value);
    }

    public boolean canEditBlocks() {
        return this.getItem().canItemEditBlocks();
    }

    public boolean isOnItemFrame() {
        return this.itemFrame != null;
    }

    public void setItemFrame(EntityItemFrame frame) {
        this.itemFrame = frame;
    }

    public EntityItemFrame getItemFrame() {
        return this.itemFrame;
    }

    public int getRepairCost() {
        return this.hasTagCompound() && this.stackTagCompound.hasKey("RepairCost", 3) ? this.stackTagCompound.getInteger("RepairCost") : 0;
    }

    public void setRepairCost(int cost) {
        if (!this.hasTagCompound()) {
            this.stackTagCompound = new NBTTagCompound();
        }
        this.stackTagCompound.setInteger("RepairCost", cost);
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers() {
        HashMultimap multimap;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("AttributeModifiers", 9)) {
            multimap = HashMultimap.create();
            NBTTagList nbttaglist = this.stackTagCompound.getTagList("AttributeModifiers", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                AttributeModifier attributemodifier = SharedMonsterAttributes.readAttributeModifierFromNBT(nbttagcompound);
                if (attributemodifier == null || attributemodifier.getID().getLeastSignificantBits() == 0L || attributemodifier.getID().getMostSignificantBits() == 0L) continue;
                multimap.put((Object)nbttagcompound.getString("AttributeName"), (Object)attributemodifier);
            }
        } else {
            multimap = this.getItem().getItemAttributeModifiers();
        }
        return multimap;
    }

    public void setItem(Item newItem) {
        this.item = newItem;
    }

    public IChatComponent getChatComponent() {
        ChatComponentText chatcomponenttext = new ChatComponentText(this.getDisplayName());
        if (this.hasDisplayName()) {
            chatcomponenttext.getChatStyle().setItalic(true);
        }
        IChatComponent ichatcomponent = new ChatComponentText("[").appendSibling(chatcomponenttext).appendText("]");
        if (this.item != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            this.writeToNBT(nbttagcompound);
            ichatcomponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(nbttagcompound.toString())));
            ichatcomponent.getChatStyle().setColor(this.getRarity().rarityColor);
        }
        return ichatcomponent;
    }

    public boolean canDestroy(Block blockIn) {
        if (blockIn == this.canDestroyCacheBlock) {
            return this.canDestroyCacheResult;
        }
        this.canDestroyCacheBlock = blockIn;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9)) {
            NBTTagList nbttaglist = this.stackTagCompound.getTagList("CanDestroy", 8);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                Block block = Block.getBlockFromName(nbttaglist.getStringTagAt(i));
                if (block != blockIn) continue;
                this.canDestroyCacheResult = true;
                return true;
            }
        }
        this.canDestroyCacheResult = false;
        return false;
    }

    public boolean canPlaceOn(Block blockIn) {
        if (blockIn == this.canPlaceOnCacheBlock) {
            return this.canPlaceOnCacheResult;
        }
        this.canPlaceOnCacheBlock = blockIn;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9)) {
            NBTTagList nbttaglist = this.stackTagCompound.getTagList("CanPlaceOn", 8);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                Block block = Block.getBlockFromName(nbttaglist.getStringTagAt(i));
                if (block != blockIn) continue;
                this.canPlaceOnCacheResult = true;
                return true;
            }
        }
        this.canPlaceOnCacheResult = false;
        return false;
    }
}

