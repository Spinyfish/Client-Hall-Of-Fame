package intentions.modules.chat;

import intentions.Client;
import intentions.modules.Module;

public class AutoGG extends Module {

	public AutoGG() {
		super("AutoGG", 0, Category.CHAT, "Automatically says GG when you win", false);
	}
	
	public void onUpdate() {
		if(this.toggled) {
			
			this.toggle();
			Client.addChatMessage("Sorry this is in beta.");
			
		}
	}
	
}
