package axolotl.cheats.modules.impl.player;

import axolotl.Axolotl;
import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.MoveEvent;
import axolotl.cheats.modules.Module;
import axolotl.cheats.modules.impl.combat.Aura;
import axolotl.cheats.settings.ModeSetting;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "NCP", "Watchdog");
	
	public NoSlow() {
		super("NoSlow", Category.PLAYER, true);
		this.addSettings(mode);
		this.setSpecialSetting(mode);
	}

	public void onEvent(Event e){

		if(e instanceof MoveEvent) {
			if(mode.getMode().equalsIgnoreCase("NCP") || mode.getMode().equalsIgnoreCase("Watchdog")) {
				if(mc.thePlayer.isUsingItem()) {
					if(mc.thePlayer.ticksExisted % 3 == 0) {
						mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
					}else{
						mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
					}
				}
			}

		}

	}
	
}
