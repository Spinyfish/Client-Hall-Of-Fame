package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class ItemEditableBook extends Item {
    public ItemEditableBook() {
        this.setMaxStackSize(1);
    }

    public static boolean validBookTagContents(final NBTTagCompound nbt) {
        if (!ItemWritableBook.isNBTValid(nbt)) {
            return false;
        } else if (!nbt.hasKey("title", 8)) {
            return false;
        } else {
            final String s = nbt.getString("title");
            return s != null && s.length() <= 32 && nbt.hasKey("author", 8);
        }
    }

    /**
     * Gets the generation of the book (how many times it has been cloned)
     *
     * @param book The book to get the generation of
     */
    public static int getGeneration(final ItemStack book) {
        return book.getTagCompound().getInteger("generation");
    }

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

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param tooltip  All lines to display in the Item's tooltip. This is a List of Strings.
     * @param advanced Whether the setting "Advanced tooltips" is enabled
     */
    public void addInformation(final ItemStack stack, final EntityPlayer playerIn, final List<String> tooltip, final boolean advanced) {
        if (stack.hasTagCompound()) {
            final NBTTagCompound nbttagcompound = stack.getTagCompound();
            final String s = nbttagcompound.getString("author");

            if (!StringUtils.isNullOrEmpty(s)) {
                tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("book.byAuthor", new Object[]{s}));
            }

            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("book.generation." + nbttagcompound.getInteger("generation")));
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
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
                        IChatComponent ichatcomponent;

                        try {
                            ichatcomponent = IChatComponent.Serializer.jsonToComponent(s);
                            ichatcomponent = ChatComponentProcessor.processComponent(player, ichatcomponent, player);
                        } catch (final Exception var9) {
                            ichatcomponent = new ChatComponentText(s);
                        }

                        nbttaglist.set(i, new NBTTagString(IChatComponent.Serializer.componentToJson(ichatcomponent)));
                    }

                    nbttagcompound.setTag("pages", nbttaglist);

                    if (player instanceof EntityPlayerMP && player.getCurrentEquippedItem() == stack) {
                        final Slot slot = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
                        ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, slot.slotNumber, stack));
                    }
                }
            }
        }
    }

    public boolean hasEffect(final ItemStack stack) {
        return true;
    }
}
