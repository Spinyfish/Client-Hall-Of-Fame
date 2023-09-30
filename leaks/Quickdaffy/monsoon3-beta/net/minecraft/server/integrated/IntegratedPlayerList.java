/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.ServerConfigurationManager;

public class IntegratedPlayerList
extends ServerConfigurationManager {
    private NBTTagCompound hostPlayerData;

    public IntegratedPlayerList(IntegratedServer p_i1314_1_) {
        super(p_i1314_1_);
        this.setViewDistance(10);
    }

    @Override
    protected void writePlayerData(EntityPlayerMP playerIn) {
        if (playerIn.getCommandSenderName().equals(this.getServerInstance().getServerOwner())) {
            this.hostPlayerData = new NBTTagCompound();
            playerIn.writeToNBT(this.hostPlayerData);
        }
        super.writePlayerData(playerIn);
    }

    @Override
    public String allowUserToConnect(SocketAddress address, GameProfile profile) {
        return profile.getName().equalsIgnoreCase(this.getServerInstance().getServerOwner()) && this.getPlayerByUsername(profile.getName()) != null ? "That name is already taken." : super.allowUserToConnect(address, profile);
    }

    @Override
    public IntegratedServer getServerInstance() {
        return (IntegratedServer)super.getServerInstance();
    }

    @Override
    public NBTTagCompound getHostPlayerData() {
        return this.hostPlayerData;
    }
}

