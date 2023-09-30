package de.fanta.module.impl.miscellaneous;

import net.minecraft.block.BlockAir;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.plaf.synth.SynthOptionPaneUI;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventPacket;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.EventUpdate;
import de.fanta.module.Module;
import de.fanta.module.Module.Type;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.RandomUtil;
import de.fanta.utils.TimeUtil;

public class FullAntiVerus extends Module {
	public FullAntiVerus() {
		super("FullAntiVerus", 0, Type.Misc, Color.GREEN);
	}

	public ArrayList<Packet> packets = new ArrayList<>();
	public LinkedList packetQueue = new LinkedList();
	public int state, state2, state3;
	public int stage, stage2, stage3;
	public TimeUtil timer = new TimeUtil();
	public TimeUtil timer2 = new TimeUtil();
	private boolean inGround = true;
	private boolean cancelFlag = true;
	private boolean transaction = true;
	private boolean transactionMultiply = true;
	private boolean transactionSend = true;

	public void onEvent(Event e) {
		if (e instanceof EventUpdate) {
			boolean send = transactionSend;
			if (send) {
				while (packetQueue.size() > 22) {
					sendPacketSilent((Packet) packetQueue.poll());
					e.setCancelled(true);
				}
			}
		}
		if (e instanceof EventPacket) {
			
			if (mc.thePlayer.ticksExisted % 2 == 1) {
				//e.setCancelled(true);
			}else {
				e.setCancelled(false);
			}
			if (mc.thePlayer.ticksExisted % 2 == 1) {
				if (e instanceof EventRender3D) {
					//mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction());
				}
			} else {
				//e.setCancelled(true);
			}
			if (mc.thePlayer != null && mc.thePlayer.ticksExisted == 0)
				packetQueue.clear();
			Packet packet = ((EventPacket) e).getPacket();
			if (packet instanceof S08PacketPlayerPosLook) {
				if (cancelFlag) {
					//Disabler.sendAbilities();
					e.setCancelled(true);
					C0FPacketConfirmTransaction packetConfirmTransaction = (C0FPacketConfirmTransaction) packet;
					mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(Integer.MAX_VALUE,
							packetConfirmTransaction.getUid(), true));
					double x = ((S08PacketPlayerPosLook) packet).getX() - mc.thePlayer.posX;
					double y = ((S08PacketPlayerPosLook) packet).getY() - mc.thePlayer.posY;
					double z = ((S08PacketPlayerPosLook) packet).getZ() - mc.thePlayer.posZ;
					double diff = Math.sqrt(x * x + y * y + z * z);
					e.setCancelled(true);
					if (diff <= 0.5F) {
						mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(
								((S08PacketPlayerPosLook) packet).getX(), ((S08PacketPlayerPosLook) packet).getY(),
								((S08PacketPlayerPosLook) packet).getZ(), ((S08PacketPlayerPosLook) packet).getYaw(),
								((S08PacketPlayerPosLook) packet).getPitch(), false));
						e.setCancelled(true);
					}
				}
			} else if (packet instanceof C03PacketPlayer) {
				if (inGround) {
					{
//						if (packet instanceof C04PacketPlayerPosition) {
//							e.setCancelled(true);
//						}
//						if (packet instanceof C09PacketHeldItemChange&& mc.thePlayer.ticksExisted % 3 == 0) {
//							e.setCancelled(true);
//						}
						
						if (packet instanceof C13PacketPlayerAbilities) {
							//e.setCancelled(true);
						}
						
//						if (packet instanceof C01PacketPing && mc.thePlayer.ticksExisted % 2 == 0) {
//							e.setCancelled(true);
//						}
//						
						if (packet instanceof C05PacketPlayerLook) {
							//e.setCancelled(true);
						}
						
//						if (packet instanceof C0CPacketInput  && mc.thePlayer.ticksExisted % 2 == 1) {
//							e.setCancelled(true);
//						}
//						if (packet instanceof C03PacketPlayer&& mc.thePlayer.ticksExisted % 10 == 0) {
//							e.setCancelled(true);
//						}
						if (packet instanceof C05PacketPlayerLook) {
							//e.setCancelled(true);
						}
						
						if (packet instanceof C13PacketPlayerAbilities) {
							if (!mc.thePlayer.isEating()) {
							//	e.setCancelled(true);
							}
						}
						
						if (mc.thePlayer.ticksExisted % 85 == 0) {
							//Disabler.sendAbilities();
							sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
									mc.thePlayer.posY, mc.thePlayer.posZ, false));
							sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
									mc.thePlayer.posY -90, mc.thePlayer.posZ, true));
							sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
								mc.thePlayer.posY, mc.thePlayer.posZ, false));
							//if (mc.thePlayer.ticksExisted % 3 == 0) {
								e.setCancelled(true);
							//}
						}
					}
				}
			} else if (packet instanceof C0FPacketConfirmTransaction) {
				boolean c0f = transaction;
				boolean c0fMultiply = transactionMultiply;
				if (c0f) {
					if (c0fMultiply) {
						for (int i = 0; i < 1; i++)
							packetQueue.add(packet);
							e.setCancelled(true);
					} else {
						packetQueue.add(packet);
					}
					e.setCancelled(true);
				}
			}
		}
	}

	public void sendPacketSilent(Packet packet) {
		mc.getNetHandler().getNetworkManager().sendPacket(packet, null);
	}

	@Override
	public void onDisable() {
		if (packets != null && packets.size() > 0)
			packets.clear();
		if (packetQueue != null && packetQueue.size() > 0)
			packetQueue.clear();
		this.timer.reset();
		this.timer2.reset();
		state = 0;
		state2 = 0;
		state3 = 0;
		stage = 0;
		stage2 = 0;
		stage3 = 0;
	}

	@Override
	public void onEnable() {
		mc.thePlayer.ticksExisted = 0;
	}

	public boolean isBlockUnder() {
		for (int i = (int) mc.thePlayer.posY; i >= 0; --i) {
			BlockPos position = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);

			if (!(mc.theWorld.getBlockState(position).getBlock() instanceof BlockAir)) {
				return true;
			}
		}
		return false;
	}
}
