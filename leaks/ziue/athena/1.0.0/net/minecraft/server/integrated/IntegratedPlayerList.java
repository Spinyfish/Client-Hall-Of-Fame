package net.minecraft.server.integrated;

import net.minecraft.server.management.*;
import net.minecraft.nbt.*;
import net.minecraft.server.*;
import net.minecraft.entity.player.*;
import java.net.*;
import com.mojang.authlib.*;

public class IntegratedPlayerList extends ServerConfigurationManager
{
    private NBTTagCompound hostPlayerData;
    
    public IntegratedPlayerList(final IntegratedServer p_i1314_1_) {
        super(p_i1314_1_);
        this.setViewDistance(10);
    }
    
    @Override
    protected void writePlayerData(final EntityPlayerMP playerIn) {
        if (playerIn.getName().equals(this.getServerInstance().getServerOwner())) {
            playerIn.writeToNBT(this.hostPlayerData = new NBTTagCompound());
        }
        super.writePlayerData(playerIn);
    }
    
    @Override
    public String allowUserToConnect(final SocketAddress address, final GameProfile profile) {
        return (profile.getName().equalsIgnoreCase(this.getServerInstance().getServerOwner()) && this.getPlayerByUsername(profile.getName()) != null) ? "That name is already taken." : super.allowUserToConnect(address, profile);
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
