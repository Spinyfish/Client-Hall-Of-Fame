/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.optifine.reflect.Reflector;
import net.optifine.util.IntegratedServerUtils;

public class TileEntityUtils {
    public static String getTileEntityName(IBlockAccess blockAccess, BlockPos blockPos) {
        TileEntity tileentity = blockAccess.getTileEntity(blockPos);
        return TileEntityUtils.getTileEntityName(tileentity);
    }

    public static String getTileEntityName(TileEntity te) {
        if (!(te instanceof IWorldNameable)) {
            return null;
        }
        IWorldNameable iworldnameable = (IWorldNameable)((Object)te);
        TileEntityUtils.updateTileEntityName(te);
        return !iworldnameable.hasCustomName() ? null : iworldnameable.getCommandSenderName();
    }

    public static void updateTileEntityName(TileEntity te) {
        BlockPos blockpos = te.getPos();
        String s = TileEntityUtils.getTileEntityRawName(te);
        if (s == null) {
            String s1 = TileEntityUtils.getServerTileEntityRawName(blockpos);
            s1 = Config.normalize(s1);
            TileEntityUtils.setTileEntityRawName(te, s1);
        }
    }

    public static String getServerTileEntityRawName(BlockPos blockPos) {
        TileEntity tileentity = IntegratedServerUtils.getTileEntity(blockPos);
        return tileentity == null ? null : TileEntityUtils.getTileEntityRawName(tileentity);
    }

    public static String getTileEntityRawName(TileEntity te) {
        IWorldNameable iworldnameable;
        if (te instanceof TileEntityBeacon) {
            return (String)Reflector.getFieldValue(te, Reflector.TileEntityBeacon_customName);
        }
        if (te instanceof TileEntityBrewingStand) {
            return (String)Reflector.getFieldValue(te, Reflector.TileEntityBrewingStand_customName);
        }
        if (te instanceof TileEntityEnchantmentTable) {
            return (String)Reflector.getFieldValue(te, Reflector.TileEntityEnchantmentTable_customName);
        }
        if (te instanceof TileEntityFurnace) {
            return (String)Reflector.getFieldValue(te, Reflector.TileEntityFurnace_customName);
        }
        if (te instanceof IWorldNameable && (iworldnameable = (IWorldNameable)((Object)te)).hasCustomName()) {
            return iworldnameable.getCommandSenderName();
        }
        return null;
    }

    public static boolean setTileEntityRawName(TileEntity te, String name) {
        if (te instanceof TileEntityBeacon) {
            return Reflector.setFieldValue(te, Reflector.TileEntityBeacon_customName, name);
        }
        if (te instanceof TileEntityBrewingStand) {
            return Reflector.setFieldValue(te, Reflector.TileEntityBrewingStand_customName, name);
        }
        if (te instanceof TileEntityEnchantmentTable) {
            return Reflector.setFieldValue(te, Reflector.TileEntityEnchantmentTable_customName, name);
        }
        if (te instanceof TileEntityFurnace) {
            return Reflector.setFieldValue(te, Reflector.TileEntityFurnace_customName, name);
        }
        if (te instanceof TileEntityChest) {
            ((TileEntityChest)te).setCustomName(name);
            return true;
        }
        if (te instanceof TileEntityDispenser) {
            ((TileEntityDispenser)te).setCustomName(name);
            return true;
        }
        if (te instanceof TileEntityHopper) {
            ((TileEntityHopper)te).setCustomName(name);
            return true;
        }
        return false;
    }
}

