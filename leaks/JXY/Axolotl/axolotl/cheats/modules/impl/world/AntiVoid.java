package axolotl.cheats.modules.impl.world;

import axolotl.cheats.events.*;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.ModeSetting;
import axolotl.util.PacketUtils;
import baritone.api.event.events.PacketEvent;
import net.minecraft.block.material.Material;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public class AntiVoid extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "Watchdog");

	public AntiVoid() {

		super("AntiVoid", Category.WORLD, true);
		this.addSettings(mode);
		this.setSpecialSetting(mode);
	}

	private Vec3 lastground;
	
	public void onEvent(Event e) {
		
		if(e instanceof EventUpdate && e.eventType == EventType.PRE) {

			switch(mode.getMode()) {
				case "Normal":
					if (mc.thePlayer.onGround) {
						lastground = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
					}

					if (mc.thePlayer.fallDistance > 2) {

						if (isInVoid())
							mc.thePlayer.setPosition(lastground.x, lastground.y, lastground.z);

					}
				default:
					break;
			}
		} else if (e instanceof EventPacket) {

			switch(mode.getMode()) {
				case "Watchdog":
					if(((EventPacket) e).sendType == 1) {
						if (isInVoid() || (!mc.thePlayer.onGround && wasInVoid)) {
							wasInVoid = true;
							if (mc.thePlayer.fallDistance > 2) {
								mc.thePlayer.setPosition(lastground.x, lastground.y, lastground.z);
								packetList.clear();
								break;
							}
							((EventPacket) e).setCancelled(true);
							packetList.add(((EventPacket) e).getPacket());
						} else {
							wasInVoid = false;
							if (mc.thePlayer.onGround) {
								if (!packetList.isEmpty()) {
									for (Packet p : packetList) {
										PacketUtils.sendPacketNoEvent(p);
									}
									packetList.clear();
								}
								lastground = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
							}
						}
					}
					break;
				default:
					break;
			}
		}
		
	}

	public boolean wasInVoid;

	public List<Packet> packetList = new ArrayList();

	private boolean isInVoid() {
		for(int i=0;i<20;i++) {

			if(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - (i + 1), mc.thePlayer.posZ)).getBlock().getMaterial() != Material.air)return false;

		}

		return true;
	}

}
