package me.wavelength.baseclient.module.modules.player;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import net.minecraft.network.play.server.S02PacketChat;

public class Insults extends Module {

	public Insults() {
		super("Insults", "Insults a player on death.", 0, Category.PLAYER, AntiCheat.HYPIXEL);
	}

	@Override
	public void setup() {
	}
	
	private int rand;

	@Override
	public void onEnable() {
		rand = 0;
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onUpdate(UpdateEvent event) {
	}

	@Override
	public void onPacketReceived(PacketReceivedEvent event) {
		
		if(event.getPacket() instanceof S02PacketChat) {
			S02PacketChat S02 = (S02PacketChat)event.getPacket();
			if(S02.getChatComponent().toString().contains(mc.thePlayer.getName())){
				
				if(rand == 1) {
					mc.thePlayer.sendChatMessage("I don't cheat, I just z y t h! [" + Math.random()*1000000000 + "]");
				}
				
				if(rand == 2) {
					mc.thePlayer.sendChatMessage("You are a n i gg er! Get z y t h   to   unn ig ge r! [" + Math.random()*1000000000 + "]");
				}

				if(rand == 3) {
					mc.thePlayer.sendChatMessage("Get good, get z y t h. [" + Math.random()*1000000000 + "]");
				}
				
				if(rand == 4) {
					mc.thePlayer.sendChatMessage("z y t h > you [" + Math.random()*1000000000 + "]");
				}
				
				if(rand == 5) {
					mc.thePlayer.sendChatMessage("Ever checked out z y t h   c l i e n t? [" + Math.random()*1000000000 + "]");
				}

				rand++;
				
				if(rand == 6) {
					mc.thePlayer.sendChatMessage("Seems like z y t h took away your left clcik perms! [" + Math.random()*1000000000 + "]");
					rand = 0;
				}
			}
			
			if(S02.getChatComponent().toString().contains("cheat") || S02.getChatComponent().toString().contains("Cheat")){
				mc.thePlayer.sendChatMessage("I'm not cheating, I just use z y t h   c l i e n t!");
			}
			
			if(S02.getChatComponent().toString().contains(mc.thePlayer.getName()) && S02.getChatComponent().toString().contains("hack")){
				mc.thePlayer.sendChatMessage("I'm not cheating, what do you mean bruh?");
			}
			
			if(S02.getChatComponent().toString().contains("fly") || S02.getChatComponent().toString().contains("Fly")){
				mc.thePlayer.sendChatMessage("I'm not fly hacking! I'm using z y t h's insane hover move!");
			}
			
			if(S02.getChatComponent().toString().contains("stop") && S02.getChatComponent().toString().contains("Stop")) {
				mc.thePlayer.sendChatMessage("Your girl said that last night- wait you don't have one though?");	
			}
			
			if(S02.getChatComponent().toString().contains("why") && S02.getChatComponent().toString().contains("why")) {
				mc.thePlayer.sendChatMessage("Why not use z y t h?");	
			}
		}
		
	}

}