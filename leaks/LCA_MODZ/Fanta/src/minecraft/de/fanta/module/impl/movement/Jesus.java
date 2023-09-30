package de.fanta.module.impl.movement;

import java.awt.Color;

import de.fanta.events.Event;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.module.impl.miscellaneous.NullCrasher;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.TimeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class Jesus extends Module {
	public Jesus() {
		super("Jesus", 0, Type.Movement, Color.red);

		this.settings.add(
				new Setting("Modes", new DropdownBox("Solid", new String[] { "Solid", "Intave", "NCP", "Verus" })));

	}

	@Override
	public void onEvent(Event event) {

		if (event instanceof EventTick) {
			BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.01D, mc.thePlayer.posZ);
			Block block = mc.theWorld.getBlockState(pos).getBlock();
			float yaw = (float) Math.toRadians(mc.thePlayer.rotationYaw);
			switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {
			case "Solid":

				///NullCrasher.pingThreadCrasher("79.218.86.122:25565", 25565, 10, 20);

				break;
			case "Intave":

				float xZ = (float) (-Math.sin(yaw) * 0.5);
				float zZ = (float) (Math.cos(yaw) * 0.5);

				if (block.getMaterial() == Material.water) {
					mc.thePlayer.motionY = 0;
					mc.thePlayer.onGround = true;

					// mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));
					// mc.thePlayer.setPosition(mc.thePlayer.posX + xZ, mc.thePlayer.posY,
					// mc.thePlayer.posZ + zZ);
					if (mc.thePlayer.moveForward > 0) {
						mc.thePlayer.motionX = -Math.sin(yaw) * 0.04;
						mc.thePlayer.motionZ = Math.cos(yaw) * 0.04;
					}
				}
				break;
			case "NCP":
				BlockPos pos1 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.03D, mc.thePlayer.posZ);
				Block block1 = mc.theWorld.getBlockState(pos1).getBlock();
				if (block1.getMaterial() == Material.water) {
					mc.thePlayer.motionY += 0.02;
					mc.thePlayer.onGround = true;
				}

				break;
			case "Verus":
				if (block.getMaterial() == Material.water) {
					mc.thePlayer.cameraYaw = 0.1f;
					mc.thePlayer.motionY = 0;
					mc.thePlayer.onGround = true;

					// mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));
					// mc.thePlayer.setPosition(mc.thePlayer.posX + xZ, mc.thePlayer.posY,
					// mc.thePlayer.posZ + zZ);
					if (mc.thePlayer.moveForward > 0) {
						Speed.setSpeed(0.5);
					}
				}
				break;
			}
		}
	}
}