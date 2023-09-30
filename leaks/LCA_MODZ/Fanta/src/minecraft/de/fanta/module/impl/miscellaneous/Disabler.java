package de.fanta.module.impl.miscellaneous;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventNoClip;
import de.fanta.events.listeners.EventPacket;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.EventUpdate;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class Disabler extends Module {
	public Disabler() {
		super("Disabler", 0, Type.Misc, Color.WHITE);
		this.settings.add(new Setting("NoClip", new CheckBox(false)));

		this.settings.add(new Setting("NoFall Test", new CheckBox(false)));
		this.settings.add(new Setting("InGround", new CheckBox(false)));
		this.settings.add(new Setting("AdjusttoGroundState", new CheckBox(false)));
		this.settings.add(new Setting("CancelFlag", new CheckBox(false)));
		this.settings.add(new Setting("Transaction", new CheckBox(false)));
		this.settings.add(new Setting("(Transaction)Multiply", new CheckBox(false)));
		this.settings.add(new Setting("TransactionSend", new CheckBox(false)));
		this.settings.add(new Setting("KeepAlive", new CheckBox(false)));
		this.settings.add(new Setting("(KeepAlive)Multiply", new CheckBox(false)));

		this.settings.add(new Setting("Modes", new DropdownBox("VerusSemi", new String[] { "VerusSemi", "BlocksMC" })));

	}

	public ArrayList<Packet> packets = new ArrayList<>();
	public LinkedList packetQueue = new LinkedList();
	public int state, state2, state3;
	public int stage, stage2, stage3;
	public TimeUtil helper = new TimeUtil();
	public TimeUtil helper2 = new TimeUtil();

	@Override
	public void onEnable() {
		super.onEnable();

		mc.thePlayer.ticksExisted = 0;
	}

	public void onDisable() {
		if (packets != null && packets.size() > 0)
			packets.clear();
		if (packetQueue != null && packetQueue.size() > 0)
			packetQueue.clear();

		helper.setLastMS();
		helper2.setLastMS();

		state = 0;
		state2 = 0;
		state3 = 0;
		stage = 0;
		stage2 = 0;
		stage3 = 0;
		super.onDisable();
	}

	@Override
	public void onEvent(Event event) {

		if (event instanceof EventReceivedPacket) {

			switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {

			case "VerusSemi":
				if (((CheckBox) this.getSetting("NoClip").getSetting()).state) {
					if (event instanceof EventNoClip) {
						((EventNoClip) event).noClip = true;
					}
				}
				if (!Killaura.hasTarget()) {
					
					Packet p = EventReceivedPacket.INSTANCE.getPacket();
					if (p instanceof C04PacketPlayerPosition) {
						event.setCancelled(true);
					}
					if (p instanceof S32PacketConfirmTransaction) {
						S32PacketConfirmTransaction packet = (S32PacketConfirmTransaction) p;
						if (p instanceof C0FPacketConfirmTransaction) {
							C0FPacketConfirmTransaction packetConfirmTransaction = (C0FPacketConfirmTransaction) p;
							mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(Integer.MAX_VALUE,
									packetConfirmTransaction.getUid(), true));
									event.setCancelled(true);
						}
						if (packet.getActionNumber() <= 0) {
							EventReceivedPacket.INSTANCE.setCancelled(true);
						}
						

					}

				
						if (mc.thePlayer.ticksExisted % 30 == 0) {
							//mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction());
						
					}

					if (mc.thePlayer.ticksExisted % 25 == 0) {
						// mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive());
						// mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive());

						//sendAbilities();
					}

					if (p instanceof C0FPacketConfirmTransaction) {
						EventReceivedPacket.INSTANCE.setCancelled(true);
					}
					if (p instanceof C00PacketKeepAlive) {
						EventReceivedPacket.INSTANCE.setCancelled(true);
					}

				}
				break;
			}
		}
	}
	// }

	// }

	static void sendAbilities() {
		PlayerCapabilities capabilities = new PlayerCapabilities();
		capabilities.isFlying = true;
		capabilities.allowFlying = true;
		capabilities.isCreativeMode = true;

		mc.thePlayer.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(capabilities));
	}

	public void sendPacketSilent(Packet packet) {
		mc.getNetHandler().getNetworkManager().sendPacket(packet, null);
	}

}
