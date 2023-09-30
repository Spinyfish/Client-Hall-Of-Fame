/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.entity;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import wtf.monsoon.Wrapper;

public class EntityFakePlayer
extends EntityOtherPlayerMP {
    public EntityFakePlayer() {
        super(Wrapper.getMinecraft().theWorld, Wrapper.getMinecraft().thePlayer.getGameProfile());
        this.copyLocationAndAnglesFrom(Wrapper.getMinecraft().thePlayer);
        this.inventory.copyInventory(Wrapper.getMinecraft().thePlayer.inventory);
        this.rotationYawHead = Wrapper.getMinecraft().thePlayer.rotationYawHead;
        this.renderYawOffset = Wrapper.getMinecraft().thePlayer.renderYawOffset;
        this.rotationPitchHead = Wrapper.getMinecraft().thePlayer.rotationPitchHead;
        this.chasingPosX = this.posX;
        this.chasingPosY = this.posY;
        this.chasingPosZ = this.posZ;
        Wrapper.getMinecraft().theWorld.addEntityToWorld(this.getEntityId(), this);
    }

    public void updateLocation() {
        this.copyLocationAndAnglesFrom(Wrapper.getMinecraft().thePlayer);
        this.inventory.copyInventory(Wrapper.getMinecraft().thePlayer.inventory);
        this.rotationYawHead = Wrapper.getMinecraft().thePlayer.rotationYawHead;
        this.renderYawOffset = Wrapper.getMinecraft().thePlayer.renderYawOffset;
        this.rotationPitchHead = Wrapper.getMinecraft().thePlayer.rotationPitchHead;
        this.chasingPosX = this.posX;
        this.chasingPosY = this.posY;
        this.chasingPosZ = this.posZ;
        Wrapper.getMinecraft().theWorld.addEntityToWorld(this.getEntityId(), this);
    }

    public void despawn() {
        Wrapper.getMinecraft().theWorld.removeEntityFromWorld(this.getEntityId());
    }
}

