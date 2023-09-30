/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 */
package net.minecraft.nbt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;

public final class NBTUtil {
    public static GameProfile readGameProfileFromNBT(NBTTagCompound compound) {
        UUID uuid;
        String s = null;
        String s1 = null;
        if (compound.hasKey("Name", 8)) {
            s = compound.getString("Name");
        }
        if (compound.hasKey("Id", 8)) {
            s1 = compound.getString("Id");
        }
        if (StringUtils.isNullOrEmpty(s) && StringUtils.isNullOrEmpty(s1)) {
            return null;
        }
        try {
            uuid = UUID.fromString(s1);
        }
        catch (Throwable var12) {
            uuid = null;
        }
        GameProfile gameprofile = new GameProfile(uuid, s);
        if (compound.hasKey("Properties", 10)) {
            NBTTagCompound nbttagcompound = compound.getCompoundTag("Properties");
            for (String s2 : nbttagcompound.getKeySet()) {
                NBTTagList nbttaglist = nbttagcompound.getTagList(s2, 10);
                for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                    NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                    String s3 = nbttagcompound1.getString("Value");
                    if (nbttagcompound1.hasKey("Signature", 8)) {
                        gameprofile.getProperties().put((Object)s2, (Object)new Property(s2, s3, nbttagcompound1.getString("Signature")));
                        continue;
                    }
                    gameprofile.getProperties().put((Object)s2, (Object)new Property(s2, s3));
                }
            }
        }
        return gameprofile;
    }

    public static NBTTagCompound writeGameProfile(NBTTagCompound tagCompound, GameProfile profile) {
        if (!StringUtils.isNullOrEmpty(profile.getName())) {
            tagCompound.setString("Name", profile.getName());
        }
        if (profile.getId() != null) {
            tagCompound.setString("Id", profile.getId().toString());
        }
        if (!profile.getProperties().isEmpty()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            for (String s : profile.getProperties().keySet()) {
                NBTTagList nbttaglist = new NBTTagList();
                for (Property property : profile.getProperties().get((Object)s)) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setString("Value", property.getValue());
                    if (property.hasSignature()) {
                        nbttagcompound1.setString("Signature", property.getSignature());
                    }
                    nbttaglist.appendTag(nbttagcompound1);
                }
                nbttagcompound.setTag(s, nbttaglist);
            }
            tagCompound.setTag("Properties", nbttagcompound);
        }
        return tagCompound;
    }

    public static boolean func_181123_a(NBTBase p_181123_0_, NBTBase p_181123_1_, boolean p_181123_2_) {
        if (p_181123_0_ == p_181123_1_) {
            return true;
        }
        if (p_181123_0_ == null) {
            return true;
        }
        if (p_181123_1_ == null) {
            return false;
        }
        if (!p_181123_0_.getClass().equals(p_181123_1_.getClass())) {
            return false;
        }
        if (p_181123_0_ instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)p_181123_0_;
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)p_181123_1_;
            for (String s : nbttagcompound.getKeySet()) {
                NBTBase nbtbase1 = nbttagcompound.getTag(s);
                if (NBTUtil.func_181123_a(nbtbase1, nbttagcompound1.getTag(s), p_181123_2_)) continue;
                return false;
            }
            return true;
        }
        if (p_181123_0_ instanceof NBTTagList && p_181123_2_) {
            NBTTagList nbttaglist = (NBTTagList)p_181123_0_;
            NBTTagList nbttaglist1 = (NBTTagList)p_181123_1_;
            if (nbttaglist.tagCount() == 0) {
                return nbttaglist1.tagCount() == 0;
            }
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTBase nbtbase = nbttaglist.get(i);
                boolean flag = false;
                for (int j = 0; j < nbttaglist1.tagCount(); ++j) {
                    if (!NBTUtil.func_181123_a(nbtbase, nbttaglist1.get(j), p_181123_2_)) continue;
                    flag = true;
                    break;
                }
                if (flag) continue;
                return false;
            }
            return true;
        }
        return p_181123_0_.equals(p_181123_1_);
    }
}

