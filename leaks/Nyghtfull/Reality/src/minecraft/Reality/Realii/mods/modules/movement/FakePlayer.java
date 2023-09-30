/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.movement;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPacketSend;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakePlayer
extends Module {
    private EntityOtherPlayerMP blinkEntity;
    private List<Packet> packetList = new ArrayList<Packet>();

    public FakePlayer(){
        super("FakePlayer", ModuleType.World);
    }

    @Override
    public void onEnable() {
        if (this.mc.thePlayer == null) {
            return;
        }
        this.blinkEntity = new EntityOtherPlayerMP(this.mc.theWorld, new GameProfile(new UUID(69L, 96L), "Nyghtfull"));
        this.blinkEntity.inventory = this.mc.thePlayer.inventory;
        this.blinkEntity.inventoryContainer = this.mc.thePlayer.inventoryContainer;
        this.blinkEntity.setPositionAndRotation(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch);
        this.blinkEntity.rotationYawHead = this.mc.thePlayer.rotationYawHead;
        this.mc.theWorld.addEntityToWorld(this.blinkEntity.getEntityId(), this.blinkEntity);
    }
    
    @Override
    public void onDisable() {
        this.packetList.clear();
        if(blinkEntity!=null) {
            this.mc.theWorld.removeEntityFromWorld(this.blinkEntity.getEntityId());
        }
    }
}

   