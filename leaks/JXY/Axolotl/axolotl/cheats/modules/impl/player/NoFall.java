package axolotl.cheats.modules.impl.player;

import axolotl.cheats.events.*;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.ModeSetting;
import axolotl.util.PlayerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module {

	public boolean work = true;
	public int value0 = 0, value1 = 0, value2 = 0;

	public ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Spartan", "NoGround", "Flappy");
	
	public NoFall() {
		super("NoFall", Category.PLAYER, true);
		this.addSettings(mode);
		this.setSpecialSetting(mode);
	}
	
	public void onEvent(Event e) {
		if(!work)return;
		
		
		if(e instanceof EventPacket && e.eventType == EventType.PRE) {
			switch(mode.getMode()) {
				case "NoGround":
					if(((EventPacket) e).getPacket() instanceof C03PacketPlayer) {
						((C03PacketPlayer)((EventPacket) e).getPacket()).onGround = false;
					}
					break;
				default:
					break;
			}
		} else if (e instanceof MoveEvent && e.eventType == EventType.PRE) {
			switch(mode.getMode()) {

				case "Spartan":
					if (mc.thePlayer.fallDistance - ((mc.thePlayer.motionY - 0.08) * 0.98) > 1.9) {
						if (PlayerUtil.getPlayerHeight() + mc.thePlayer.motionY < 2 && value0 < 2) {
							mc.thePlayer.motionY = 0.01;
							mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
							value0++;
							break;
						}
					} else {
						value0 = 0;
					}
					break;

				case "Flappy":
					if (mc.thePlayer.fallDistance >= 2) {
						if (mc.thePlayer.fallDistance >= 12) {
							((MoveEvent) e).setOnGround(true);
							mc.thePlayer.onGround = true;
							mc.thePlayer.fallDistance = 0;
							mc.thePlayer.setPosition(mc.thePlayer.posX, -1, mc.thePlayer.posZ);
							int h = PlayerUtil.getPlayerHeight();
							if (h < 30)
								mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY) - h, mc.thePlayer.posZ, true));
							return;
						}
						((MoveEvent) e).setCancelled(true);
						((MoveEvent) e).setOnGround(true);
						vClip(0.015625);
					}
					break;

				case "Packet":
					if (mc.thePlayer.fallDistance > 2) {
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
					}
					break;

				default:
					break;
			}
		}
	}

	public void onEnable() {
		value0 = 0;
		work = true;
		value1 = 0;
		value2 = 0;
	}

}
