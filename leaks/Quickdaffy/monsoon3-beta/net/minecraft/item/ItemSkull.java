/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemSkull
extends Item {
    private static final String[] skullTypes = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};

    public ItemSkull() {
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (side == EnumFacing.DOWN) {
            return false;
        }
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        boolean flag = block.isReplaceable(worldIn, pos);
        if (!flag) {
            if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid()) {
                return false;
            }
            pos = pos.offset(side);
        }
        if (!playerIn.canPlayerEdit(pos, side, stack)) {
            return false;
        }
        if (!Blocks.skull.canPlaceBlockAt(worldIn, pos)) {
            return false;
        }
        if (!worldIn.isRemote) {
            TileEntity tileentity;
            worldIn.setBlockState(pos, Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, side), 3);
            int i = 0;
            if (side == EnumFacing.UP) {
                i = MathHelper.floor_double((double)(playerIn.rotationYaw * 16.0f / 360.0f) + 0.5) & 0xF;
            }
            if ((tileentity = worldIn.getTileEntity(pos)) instanceof TileEntitySkull) {
                TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
                if (stack.getMetadata() == 3) {
                    GameProfile gameprofile = null;
                    if (stack.hasTagCompound()) {
                        NBTTagCompound nbttagcompound = stack.getTagCompound();
                        if (nbttagcompound.hasKey("SkullOwner", 10)) {
                            gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                        } else if (nbttagcompound.hasKey("SkullOwner", 8) && nbttagcompound.getString("SkullOwner").length() > 0) {
                            gameprofile = new GameProfile((UUID)null, nbttagcompound.getString("SkullOwner"));
                        }
                    }
                    tileentityskull.setPlayerProfile(gameprofile);
                } else {
                    tileentityskull.setType(stack.getMetadata());
                }
                tileentityskull.setSkullRotation(i);
                Blocks.skull.checkWitherSpawn(worldIn, pos, tileentityskull);
            }
            --stack.stackSize;
        }
        return true;
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < skullTypes.length; ++i) {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int i = stack.getMetadata();
        if (i < 0 || i >= skullTypes.length) {
            i = 0;
        }
        return super.getUnlocalizedName() + "." + skullTypes[i];
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getMetadata() == 3 && stack.hasTagCompound()) {
            NBTTagCompound nbttagcompound;
            if (stack.getTagCompound().hasKey("SkullOwner", 8)) {
                return StatCollector.translateToLocalFormatted("item.skull.player.name", stack.getTagCompound().getString("SkullOwner"));
            }
            if (stack.getTagCompound().hasKey("SkullOwner", 10) && (nbttagcompound = stack.getTagCompound().getCompoundTag("SkullOwner")).hasKey("Name", 8)) {
                return StatCollector.translateToLocalFormatted("item.skull.player.name", nbttagcompound.getString("Name"));
            }
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public boolean updateItemStackNBT(NBTTagCompound nbt) {
        super.updateItemStackNBT(nbt);
        if (nbt.hasKey("SkullOwner", 8) && nbt.getString("SkullOwner").length() > 0) {
            GameProfile gameprofile = new GameProfile((UUID)null, nbt.getString("SkullOwner"));
            gameprofile = TileEntitySkull.updateGameprofile(gameprofile);
            nbt.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
            return true;
        }
        return false;
    }
}

