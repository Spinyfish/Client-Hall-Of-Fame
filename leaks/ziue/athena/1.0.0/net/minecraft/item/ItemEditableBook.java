package net.minecraft.item;

import java.util.*;
import net.minecraft.world.*;
import net.minecraft.stats.*;
import net.minecraft.command.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.*;
import net.minecraft.nbt.*;
import net.minecraft.inventory.*;

public class ItemEditableBook extends Item
{
    public ItemEditableBook() {
        this.setMaxStackSize(1);
    }
    
    public static boolean validBookTagContents(final NBTTagCompound nbt) {
        if (!ItemWritableBook.isNBTValid(nbt)) {
            return false;
        }
        if (!nbt.hasKey("title", 8)) {
            return false;
        }
        final String s = nbt.getString("title");
        return s != null && s.length() <= 32 && nbt.hasKey("author", 8);
    }
    
    public static int getGeneration(final ItemStack book) {
        return book.getTagCompound().getInteger("generation");
    }
    
    @Override
    public String getItemStackDisplayName(final ItemStack stack) {
        if (stack.hasTagCompound()) {
            final NBTTagCompound nbttagcompound = stack.getTagCompound();
            final String s = nbttagcompound.getString("title");
            if (!StringUtils.isNullOrEmpty(s)) {
                return s;
            }
        }
        return super.getItemStackDisplayName(stack);
    }
    
    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer playerIn, final List<String> tooltip, final boolean advanced) {
        if (stack.hasTagCompound()) {
            final NBTTagCompound nbttagcompound = stack.getTagCompound();
            final String s = nbttagcompound.getString("author");
            if (!StringUtils.isNullOrEmpty(s)) {
                tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("book.byAuthor", s));
            }
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("book.generation." + nbttagcompound.getInteger("generation")));
        }
    }
    
    @Override
    public ItemStack onItemRightClick(final ItemStack itemStackIn, final World worldIn, final EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            this.resolveContents(itemStackIn, playerIn);
        }
        playerIn.displayGUIBook(itemStackIn);
        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
        return itemStackIn;
    }
    
    private void resolveContents(final ItemStack stack, final EntityPlayer player) {
        if (stack != null && stack.getTagCompound() != null) {
            final NBTTagCompound nbttagcompound = stack.getTagCompound();
            if (!nbttagcompound.getBoolean("resolved")) {
                nbttagcompound.setBoolean("resolved", true);
                if (validBookTagContents(nbttagcompound)) {
                    final NBTTagList nbttaglist = nbttagcompound.getTagList("pages", 8);
                    for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                        final String s = nbttaglist.getStringTagAt(i);
                        IChatComponent lvt_7_1_;
                        try {
                            lvt_7_1_ = IChatComponent.Serializer.jsonToComponent(s);
                            lvt_7_1_ = ChatComponentProcessor.processComponent(player, lvt_7_1_, player);
                        }
                        catch (Exception var9) {
                            lvt_7_1_ = new ChatComponentText(s);
                        }
                        nbttaglist.set(i, new NBTTagString(IChatComponent.Serializer.componentToJson(lvt_7_1_)));
                    }
                    nbttagcompound.setTag("pages", nbttaglist);
                    if (player instanceof EntityPlayerMP && player.getCurrentEquippedItem() == stack) {
                        final Slot slot = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
                        ((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, slot.slotNumber, stack));
                    }
                }
            }
        }
    }
    
    @Override
    public boolean hasEffect(final ItemStack stack) {
        return true;
    }
}
