package axolotl.cheats.modules.impl.combat;

import axolotl.cheats.events.EventType;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import axolotl.Axolotl;
import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.NumberSetting;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class Reach extends Module {

	// NOT WORKING CURRENTLY

	public NumberSetting range = new NumberSetting("Range", 3.1f, 3.1f, 6f, 0.1f);
	
	public Reach() {
		super("Reach", Category.COMBAT, true);
		this.addSettings(range);
		this.setSpecialSetting(range);
	}
	
	boolean lastTickHit = false;
	
	public void onEvent(Event e) {
		
		if(e instanceof EventUpdate && e.eventType == EventType.PRE) {
			
			try {
				int key = mc.gameSettings.keyBindAttack.getKeyCode();
				if(mc.gameSettings.keyBindAttack.getKeyCode() == -101)key = -1;
				if(mc.gameSettings.keyBindAttack.getKeyCode() == -102)key = -2;
				reach(key);
			} catch(Exception e1) {
				Axolotl.INSTANCE.sendMessage(e1);
			}
			
		}
		
	}

	private void reach(int key) {
		if(key < 0 ? Mouse.isButtonDown(Math.abs(key)) : Keyboard.isKeyDown(key)) {

			if(!lastTickHit) {

				mc.entityRenderer.getMouseOver(1.0F, true);

				if(mc.objectMouseOver.entityHit != null) {

					double dis = mc.objectMouseOver.entityHit.getDistanceToEntity(mc.thePlayer) + 0.3;

					if(dis > 3.0F) {
						if(dis <= range.getNumberValue()) {
							mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(mc.objectMouseOver.entityHit, C02PacketUseEntity.Action.ATTACK));
						}
					}

				}

			}

			lastTickHit = true;

		} else lastTickHit = false;
	}

}
