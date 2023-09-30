/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.player;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.misc.EventCollideWithBlock;
import Reality.Realii.event.events.world.EventPacketRecieve;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class Freecam
extends Module {
    private EntityOtherPlayerMP copy;
    private double x;
    private double y;
    private double z;

    public Freecam(){
        super("FreeCam", ModuleType.Ghost);
    }


    @Override
    public void onEnable() {
        this.copy = new EntityOtherPlayerMP(this.mc.theWorld, this.mc.thePlayer.getGameProfile());
        this.copy.clonePlayer(this.mc.thePlayer, true);
        this.copy.setLocationAndAngles(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch);
        this.copy.rotationYawHead = this.mc.thePlayer.rotationYawHead;
        this.copy.setEntityId(-1337);
        this.copy.setSneaking(this.mc.thePlayer.isSneaking());
        this.mc.theWorld.addEntityToWorld(this.copy.getEntityId(), this.copy);
        this.x = this.mc.thePlayer.posX;
        this.y = this.mc.thePlayer.posY;
        this.z = this.mc.thePlayer.posZ;
    }

    @EventHandler
    private void onPreMotion(EventPreUpdate e) {
        this.mc.thePlayer.capabilities.isFlying = true;
        this.mc.thePlayer.noClip = true;
        this.mc.thePlayer.capabilities.setFlySpeed(0.1f);
        e.setCancelled(true);
    }

    @EventHandler
    private void onPacketSend(EventPacketRecieve e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onBB(EventCollideWithBlock e) {
        e.setBoundingBox(null);
    }

    @Override
    public void onDisable() {
        this.mc.thePlayer.setSpeed(0.0);
        this.mc.thePlayer.setLocationAndAngles(this.copy.posX, this.copy.posY, this.copy.posZ, this.copy.rotationYaw, this.copy.rotationPitch);
        this.mc.thePlayer.rotationYawHead = this.copy.rotationYawHead;
        this.mc.theWorld.removeEntityFromWorld(this.copy.getEntityId());
        this.mc.thePlayer.setSneaking(this.copy.isSneaking());
        this.copy = null;
        this.mc.renderGlobal.loadRenderers();
        this.mc.thePlayer.setPosition(this.x, this.y, this.z);
        this.mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.01, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
        this.mc.thePlayer.capabilities.isFlying = false;
        this.mc.thePlayer.noClip = false;
        this.mc.theWorld.removeEntityFromWorld(-1);
    }
}

