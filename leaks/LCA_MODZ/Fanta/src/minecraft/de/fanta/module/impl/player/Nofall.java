package de.fanta.module.impl.player;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventPacket;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.PlayerMoveEvent;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.module.impl.combat.TestAura;
import de.fanta.module.impl.movement.AntiVoid;
import de.fanta.module.impl.movement.Speed;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.TimeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class Nofall extends Module {

	TimeUtil time = new TimeUtil();

	public Nofall() {
		super("Nofall", 0, Type.Player, Color.cyan);
		this.settings.add(new Setting("Modes",
				new DropdownBox("Vanilla", new String[] { "Vanilla", "Hypixel", "Intave", "AAC", "Verus" })));
	}

	@Override
	public void onEvent(Event event) {
		BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 3D, mc.thePlayer.posZ);
		Block block = mc.theWorld.getBlockState(pos).getBlock();

		double x = mc.thePlayer.posX;
		double y = mc.thePlayer.posY;
		double z = mc.thePlayer.posZ;
		switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {

		case "Vanilla":
			if (event instanceof EventPreMotion) {
				if (mc.thePlayer.fallDistance > 0.0F) {
					mc.thePlayer.sendQueue.addToSendQueue((Packet) new C03PacketPlayer(true));
				}
			}
			break;
		case "Hypixel":

			if (!Client.INSTANCE.moduleManager.getModule("Flight").isState()) {
				if (mc.thePlayer.fallDistance > 3.0) {
					mc.thePlayer.onGround = true;
				}
			}
			break;
		case "Verus":
			if(Client.INSTANCE.moduleManager.getModule("Speed").isState()|| Client.INSTANCE.moduleManager.getModule("Flight").isState() && TestAura.target != null)return;
				if (mc.thePlayer.fallDistance > 2) {
					mc.thePlayer.motionY = 0;
					mc.thePlayer.onGround = true;
					if (event instanceof EventTick) {
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
					}
					mc.thePlayer.fallDistance = 0;
				}
			

			// }
//			if (!Client.INSTANCE.moduleManager.getModule("Flight").isState()) {
//				if (!mc.thePlayer.onGround) {
//					if (mc.thePlayer.fallDistance > 3.1) {
//						mc.thePlayer.onGround = true;
//						if (mc.thePlayer.hurtTime !=0) {
//							mc.thePlayer.motionY = 0F;
//							mc.thePlayer.onGround = false;
//						}
//					}
//				
//				}
//			}else {
//				mc.thePlayer.onGround = false;
//			}
			break;
		case "AAC":

			if (!mc.thePlayer.onGround) {
				mc.thePlayer.motionY -= 0.04;
			}
			if (mc.thePlayer.fallDistance > 1) {

				if (time.hasReached(10) && block.getMaterial() == Material.air) {
					mc.thePlayer.onGround = true;
					time.reset();
				}

			}
			break;
		case "Intave":

			if (!mc.thePlayer.onGround) {
				mc.thePlayer.motionY -= 0.04;
			}
			if (mc.thePlayer.fallDistance > 1) {

				if (time.hasReached(10) && block.getMaterial() == Material.air) {
					// if()
					// mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));

					mc.thePlayer.motionY = 0;

					mc.thePlayer.setPosition(x, y - 0.4, z);

					// ((EventPacket) event).setPacket(c03);

					// mc.getNetHandler().addToSendQueue(new
					// C03PacketPlayer.C04PacketPlayerPosition(x, y - 2, z, true));
					time.reset();
				}
				if (block.getMaterial() != Material.air) {
					mc.thePlayer.motionY -= 5;
					mc.getNetHandler().getNetworkManager()
							.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Double.NaN, y, Double.NaN, true));

				}

				if (mc.thePlayer.onGround && time.hasReached(100)) {
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
					mc.getNetHandler().getNetworkManager()
							.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Double.NaN, y, Double.NaN, false));
					mc.thePlayer.setPosition(x, y, z);
					time.reset();
				}

			}
			break;

		}

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
