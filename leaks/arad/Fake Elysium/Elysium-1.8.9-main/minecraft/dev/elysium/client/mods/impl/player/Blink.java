package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.Event;
import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventPacket;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.BlockPos;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Blink extends Mod {

    public EntityPlayer dummy;
    public Queue<Packet> packetQueue = new ConcurrentLinkedDeque<>();

    public Blink() {
        super("Blink","Simulates lag spikes", Category.PLAYER);
    }

    public void onEnable() {
        packetQueue.clear();
        packetQueue.add(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 1, 0));
        packetQueue.add(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 3, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));

        dummy = new EntityOtherPlayerMP(mc.theWorld, mc.session.getProfile());
        mc.theWorld.addEntityToWorld(69420692, dummy); dummy.inventory.copyInventory(mc.thePlayer.inventory);
        dummy.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        dummy.renderYawOffset = mc.thePlayer.renderYawOffset; dummy.rotationYawHead = mc.thePlayer.rotationYawHead;
    }

    public void onDisable() {
        for(Packet p : packetQueue) {
            mc.thePlayer.sendQueue.addToSilentSendQueue(new C0FPacketConfirmTransaction());
            mc.thePlayer.sendQueue.addToSilentSendQueue(new C00PacketKeepAlive());
            mc.thePlayer.sendQueue.addToSilentSendQueue(new C0FPacketConfirmTransaction());
            mc.thePlayer.sendQueue.addToSilentSendQueue(new C00PacketKeepAlive());
            mc.thePlayer.sendQueue.addToSilentSendQueue(p);
            mc.thePlayer.sendQueue.addToSilentSendQueue(new C00PacketKeepAlive());
            mc.thePlayer.sendQueue.addToSilentSendQueue(new C0FPacketConfirmTransaction());
            mc.thePlayer.sendQueue.addToSilentSendQueue(new C00PacketKeepAlive());
            mc.thePlayer.sendQueue.addToSilentSendQueue(new C0FPacketConfirmTransaction());
        }

        mc.thePlayer.setPosition(dummy.posX, dummy.posY-2.5, dummy.posZ);
        mc.thePlayer.posY += 2.5;
        if(dummy != null)
            mc.theWorld.removeEntity(dummy);
        packetQueue.clear();
    }

    @EventTarget
    public void onEventPacket(EventPacket e) {
        if(mc.thePlayer.ticksExisted < 1 || e.getDirection() == Event.EventDirection.INCOMING) return;
        packetQueue.add(e.getPacket());
        e.setCancelled(true);
    }
}
