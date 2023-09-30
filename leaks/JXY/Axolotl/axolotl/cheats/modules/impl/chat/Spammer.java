package axolotl.cheats.modules.impl.chat;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.BooleanSetting;
import axolotl.cheats.settings.NumberSetting;
import axolotl.util.ChatUtil;
import axolotl.util.Timer;

public class Spammer extends Module {

	public BooleanSetting randomization = new BooleanSetting("Randomization", true);
	public NumberSetting spamSpeed = new NumberSetting("Spam Speed", 50f, 5f, 100f, 5f);
	public NumberSetting randomAmount = new NumberSetting("Length", 15, 50, 5, 5);
	
	public Timer timer = new Timer();
	
	public Spammer() {
		super("Spammer", Category.CHAT, true);
		this.addSettings(randomization, spamSpeed, randomAmount);
	}
	
	public void onEvent(Event e) {
		
		if(e instanceof EventUpdate && e.eventType == EventType.PRE) {
			
			if(timer.hasTimeElapsed((long)(100 - spamSpeed.getNumberValue()) * 50, true)) {
				
				mc.thePlayer.sendChatMessage(getMessageToSend());
				
			}
			
		}
 		
	}
	
	public String getMessageToSend() {
		return ChatUtil.random((int)randomAmount.getNumberValue()) + (randomization.isEnabled() ? " - [" + ChatUtil.random(3) + "]" : "");
	}
	
}
