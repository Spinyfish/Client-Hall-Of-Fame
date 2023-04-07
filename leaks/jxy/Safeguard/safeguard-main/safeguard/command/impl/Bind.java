package intentions.command.impl;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.command.Command;
import net.minecraft.client.Minecraft;
import intentions.modules.Module;

public class Bind extends Command {

	  private Minecraft mc = Minecraft.getMinecraft();
		
	  public Bind() {
		  super("Bind", "Bind a module to a keybind", "bind", new String[] {"b"});
	  }
	
	  public void onCommand(String[] args, String command) {
		  if(args.length == 0) {
			  Client.addChatMessage("Enter module to bind (or \"clear\" to clear all binds)");
			  return;
		  }
		  if(args.length == 1) {
			  if(args[0].equalsIgnoreCase("clear")) {
				  for(Module module : Client.modules) module.keyCode.code = 0;
				  Client.addChatMessage("Cleared all keybinds");
				  return;
			  }
			  Client.addChatMessage("Enter the key to bind " + args[0] + " to");
		  }
		  Module binding = null;
		  boolean found = false;
		  for(Module module : Client.modules) {
			  if(module.name.equalsIgnoreCase(args[0])) {
				  binding = module;
				  found = true;
				  break;
			  }
		  }
		  if(!found || binding == null) {
			  Client.addChatMessage("Can not find module " + args[0]);
			  return;
		  }
		  
		  binding.keyCode.code = Keyboard.getKeyIndex(args[1].toUpperCase());
		  
		  Client.addChatMessage(String.format("Binded %s to key %s", binding.name, Keyboard.getKeyName(binding.keyCode.code)));
		  
	  }
	
}
