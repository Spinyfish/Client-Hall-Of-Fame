/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.server.management.UserListEntry;

public class UserListBans
extends UserList<GameProfile, UserListBansEntry> {
    public UserListBans(File bansFile) {
        super(bansFile);
    }

    @Override
    protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
        return new UserListBansEntry(entryData);
    }

    public boolean isBanned(GameProfile profile) {
        return this.hasEntry(profile);
    }

    @Override
    public String[] getKeys() {
        String[] astring = new String[this.getValues().size()];
        int i = 0;
        for (UserListBansEntry userlistbansentry : this.getValues().values()) {
            astring[i++] = ((GameProfile)userlistbansentry.getValue()).getName();
        }
        return astring;
    }

    @Override
    protected String getObjectKey(GameProfile obj) {
        return obj.getId().toString();
    }

    public GameProfile isUsernameBanned(String username) {
        for (UserListBansEntry userlistbansentry : this.getValues().values()) {
            if (!username.equalsIgnoreCase(((GameProfile)userlistbansentry.getValue()).getName())) continue;
            return (GameProfile)userlistbansentry.getValue();
        }
        return null;
    }
}

