package byron.Mono.command.impl;

import java.util.Collections;
import java.util.List;


import joptsimple.internal.Strings;
import byron.Mono.Mono;
import byron.Mono.command.Command;
import byron.Mono.event.Event;
import byron.Mono.event.impl.EventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import java.util.ArrayList;

public class Plugins extends Command {

	 public Plugins() {
		super("plugins", "pl", "Lists all known plugins in a server.");
	}

	private boolean scan;
	
	@Override
	    public void onCommand(String[] args)
	    {
		  Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C14PacketTabComplete("/"));
	        if(EventPacket.getPacket() instanceof S3APacketTabComplete) {
	            S3APacketTabComplete s3APacketTabComplete = (S3APacketTabComplete) EventPacket.getPacket();
	 
	            List<String> plugins = new ArrayList<>();
	            String[] commands = s3APacketTabComplete.func_149630_c();
	 
	            for(int i = 0; i < commands.length; i++) {
	                String[] command = commands[i].split(":");
	 
	                if(command.length > 1) {
	                    String pluginName = command[0].replace("/", "");
	 
	                    if(!plugins.contains(pluginName))
	                        plugins.add(pluginName);
	                    
		               
	                }
	                Collections.sort(plugins);
			        
		            if(!plugins.isEmpty())
		                Mono.INSTANCE.sendMessage("§aPlugins §7(§8" + plugins.size() + "§7): §c" + Strings.join(plugins.toArray(new String[0]), "§7, §c"));
		            else
		            	Mono.INSTANCE.sendAlert("No plugins found.");
	    }
	 
	           
}
	        
	   
            	
	    }
}
