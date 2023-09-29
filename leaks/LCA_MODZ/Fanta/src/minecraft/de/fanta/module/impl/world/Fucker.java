package de.fanta.module.impl.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockOre;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.Color;

import org.lwjgl.input.Keyboard;



import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.Rotations;
import de.fanta.utils.TimeUtil;

public class Fucker extends Module {
	public Fucker() {
		super("Fucker", 0, Type.World, Color.red);
		this.settings.add(new Setting("Delay", new Slider(1, 5000, 1, 2)));
		this.settings.add(new Setting("Range", new Slider(1, 4.5, 1, 4.5)));
		this.settings.add(new Setting("Rotations", new CheckBox(false)));
		this.settings.add(new Setting("NoSwing", new CheckBox(false)));

	}

	public static BlockPos pos;
	TimeUtil time = new TimeUtil();
	double delaY;
	double range;

	@Override
	public void onEvent(Event event) {
		if (event instanceof EventReceivedPacket) {
			range = ((Slider) this.getSetting("Range").getSetting()).curValue;
			for (int y = (int) range; y >= -range; --y) {
				for (int x = (int) -range; x <= range; ++x) {
					for (int z = (int) -range; z <= range; ++z) {
						int posX = (int) (mc.thePlayer.posX - 0.5 + x);
						int posZ = (int) (mc.thePlayer.posZ - 0.5 + z);
						int posY = (int) (mc.thePlayer.posY - 0.5 + y);
						pos = new BlockPos(posX, posY, posZ);
						Block block = mc.theWorld.getBlockState(pos).getBlock();
						if (block instanceof BlockBed || block instanceof BlockCake
								|| block instanceof BlockDragonEgg) {
							final PlayerControllerMP playerController = mc.playerController;
							final long timeLeft = (long) (PlayerControllerMP.curBlockDamageMP / 2.0f);
							// this.facing = rotations(pos);
							delaY = ((Slider) this.getSetting("Delay").getSetting()).curValue;
							
							if (time.hasReached((long) delaY)) {
								time.reset();
								if (((CheckBox) this.getSetting("NoSwing").getSetting()).state) {
									 mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
								} else {
									mc.thePlayer.swingItem();
								}
								mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
										C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));

								mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
										C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
								mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
							}

						}
					}
				}
			}
		}
		if (event instanceof EventPreMotion) {
			for (int y = (int) 4.5F; y >= -4.5F; --y) {
				for (int x = (int) -4.5F; x <= 4.5F; ++x) {
					for (int z = (int) -4.5F; z <= 4.5F; ++z) {
						int posX = (int) (mc.thePlayer.posX - 0.5 + x);
						int posZ = (int) (mc.thePlayer.posZ - 0.5 + z);
						int posY = (int) (mc.thePlayer.posY - 0.5 + y);
						pos = new BlockPos(posX, posY, posZ);
						Block block = mc.theWorld.getBlockState(pos).getBlock();
						if (block instanceof BlockBed || block instanceof BlockCake || block instanceof BlockOre) {
							mc.thePlayer.rotationYawHead = Rotations.yaw;
							mc.thePlayer.rotationPitchHead = Rotations.pitch;
							((EventPreMotion) event).setPitch(Rotations.pitch);
							((EventPreMotion) event).setYaw(Rotations.yaw);
							lookAtPos(pos.getX() + .5, pos.getY() - .5, pos.getZ() + .5);
						}
					}
				}
			}
		}
	}

	public boolean isRotations() {
		return (boolean) getSetting("Rotations").getSetting().getValue();
	}

	public static void lookAtPos(double x, double y, double z) {
		double dirx = mc.thePlayer.posX - x;
		double diry = mc.thePlayer.posY - y;
		double dirz = mc.thePlayer.posZ - z;
		double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
		dirx /= len;
		diry /= len;
		dirz /= len;
		float yaw = (float) Math.atan2(dirz, dirx);
		float pitch = (float) Math.asin(diry);
		// yaw = player.field_70125_A
		// pitch = player.field_70177_z
		pitch = (float) (pitch * 180.0D / Math.PI);
		yaw = (float) (yaw * 180.0D / Math.PI);
		yaw += 90.0D;
		final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= yaw % f3;
		pitch -= pitch % (f3 * f2);
		Rotations.setYaw(yaw, 180F);
		Rotations.setPitch(pitch, 90F);
	}

}
