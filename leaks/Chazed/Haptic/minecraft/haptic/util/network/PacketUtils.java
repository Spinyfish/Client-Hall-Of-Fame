package haptic.util.network;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import haptic.util.base.BaseUtil;
import haptic.util.misc.HotbarUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PacketUtils implements BaseUtil {
	
	public static void sendPacket(Packet packet) {
		if(mc.thePlayer != null) {
			mc.thePlayer.sendQueue.addToSendQueue(packet);
		}
	}
	
	public static void sendPacketNoEvent(Packet packet) {
		if(mc.thePlayer != null) {
			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
		}
	}
	
	public static void sendBlocking(boolean callEvent, boolean placement) {
		if(mc.thePlayer == null)
			return;
		
		if(placement) {
			C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0);
			if(callEvent) {
				sendPacket(packet);
			} else {
				sendPacketNoEvent(packet);
			}
		} else {
			C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem());
			if(callEvent) {
				sendPacket(packet);
			} else {
				sendPacketNoEvent(packet);
			}
		}
	}
	
	public static void releaseUseItem(boolean callEvent) {
		if(mc.thePlayer == null)
			return;
		
		C07PacketPlayerDigging packet = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);
		if(callEvent) {
			sendPacket(packet);
		} else {
			sendPacketNoEvent(packet);
		}
	}
	
	public static void releaseUseItem(long delay) {
		if(mc.thePlayer == null)
			return;
		
		C07PacketPlayerDigging packet = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);
		PacketSleepThread thread = new PacketSleepThread(packet, delay);
		thread.run();
	}
	
	public static void startSprinting() {
		sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.START_SPRINTING));
	}
	
	public static void stopSprinting() {
		sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
	}
	
	public static void startSneaking() {
		sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.START_SNEAKING));
	}
	
	public static void stopSneaking() {
		sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SNEAKING));
	}
	
	public static void verusRightClick() {
		sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ), 0, HotbarUtil.getAirSlot() != -1 ? mc.thePlayer.inventory.getStackInSlot(HotbarUtil.getAirSlot()) : mc.thePlayer.getHeldItem(), 0.5F, 0.5F, 0.5F));
	}
	
	public static boolean isPacketNoMoveNoRotate(Packet p) {
		return p instanceof C03PacketPlayer && !(p instanceof C04PacketPlayerPosition) && !(p instanceof C05PacketPlayerLook) && !(p instanceof C06PacketPlayerPosLook);
	}
	
	public static boolean isPingRelatedPacket(Packet p) {
		return p instanceof C0FPacketConfirmTransaction || p instanceof C00PacketKeepAlive || p instanceof C01PacketPing;
	}

}