package axolotl.cheats.modules.impl.chat;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.NumberSetting;
import axolotl.util.Timer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;

public class AutoGroomer extends Module {

	public String[] groom = new String[]{"mm~ keep sucking", "yes... daddy~", "hey bbg~", ";) what's up darling", "Cum in me... Nya~", "Ugh... Harder... TwT~", "I wanna fuck you so bad~"};
	public NumberSetting spamSpeed = new NumberSetting("Groom Speed", 50f, 5f, 100f, 5f);

	public Timer timer = new Timer();

	public AutoGroomer() {
		super("AutoGroomer", Category.CHAT, true);
		this.addSettings(spamSpeed);
	}
	
	public void onEvent(Event e) {
		
		if(e instanceof EventUpdate && e.eventType == EventType.PRE) {
			
			if(timer.hasTimeElapsed((long)(100 - spamSpeed.getNumberValue()) * 50, true)) {
				
				Object[] obj = mc.theWorld.playerEntities.toArray();

				if(obj.length > 1) {

					Object object = obj[(int)Math.floor(Math.random() * obj.length)];

					while(object instanceof EntityPlayerSP) {
						object = obj[(int)Math.floor(Math.random() * obj.length)];
					}

					EntityOtherPlayerMP player = ((EntityOtherPlayerMP)object);
					mc.thePlayer.sendChatMessage("/msg " + player.getName() + " " + getMessageToSend());
				}
				
			}
			
		}
 		
	}
	
	public String getMessageToSend() {
		return groom[(int)Math.floor(Math.random() * groom.length)];
	}
	
}
