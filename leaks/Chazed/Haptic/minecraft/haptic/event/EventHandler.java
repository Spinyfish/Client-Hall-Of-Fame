package haptic.event;

import haptic.Haptic;
import haptic.anticheat.AnticheatEventHandler;
import haptic.event.impl.EventChat;
import haptic.event.impl.EventKeyPressed;
import haptic.event.impl.EventReceivePacket;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.processor.impl.PacketsProcessor;
import haptic.processor.impl.servers.HypixelProcessor;
import haptic.util.network.ServerUtils;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;

public class EventHandler {
	
	public static void onEvent(Event e) {
		if(e instanceof EventChat) {
			EventChat event = (EventChat) e;
			Haptic.getCommandManager().handleChat(event);
		}
		
		for(Module m : Haptic.getModuleManager().getModules()) {
			if(e instanceof EventKeyPressed) {
				EventKeyPressed event = (EventKeyPressed) e;
				
				if(m.getKeybind().getKey() == event.getKey()) {
					m.toggle();
				}
			}
			
			if((m.getListeningType() == ListeningType.ALWAYS || m.isEnabled()) && m.getListeningType() != ListeningType.NEVER) {
				m.onEvent(e);
			}
		}
		
		//AnticheatEventHandler.handle(e); // The anticheat is broken, don't use it
		handleProcessors(e);
	}
	
	private static void handleProcessors(Event e) {
		if(ServerUtils.isOnHypixel()) {
			Haptic.getHypixelProcessor().onEvent(e);
		} else if(ServerUtils.isOnRedesky()) {
			Haptic.getRedeskyProcessor().onEvent(e);
		}
		
		Haptic.getPacketsProcessor().onEvent(e);
	}
	
}