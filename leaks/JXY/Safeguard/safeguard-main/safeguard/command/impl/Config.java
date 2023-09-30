package intentions.command.impl;

import intentions.Client;
import intentions.command.Command;
import intentions.modules.Module;
import intentions.modules.combat.KillAura;
import intentions.modules.combat.MultiAura;
import intentions.modules.combat.Velocity;
import intentions.modules.movement.Flight;
import intentions.modules.movement.Speed;
import intentions.modules.movement.Step;
import intentions.modules.player.NoFall;
import intentions.modules.render.ESP;
import intentions.modules.world.AntiVoid;
import intentions.modules.world.ChestStealer;
import intentions.modules.world.Scaffold;

public class Config extends Command {
	
  public Config() {
    super("Config", "Load hack config", "config <config>", new String[] {"c", "config"});
  }
  
  public void onCommand(String[] args, String command) {
    if (args.length > 0) {
      if (args[0].equalsIgnoreCase("Redesky")) {
        while (!Flight.type.getMode().equalsIgnoreCase("Redesky"))
          Flight.type.cycle(); 
        Flight.flightSpeed.setValue(1.0D);
        ChestStealer.autoClose.enabled = true;
        ChestStealer.autoSteal.enabled = true;
        ChestStealer.stealSpeed.setValue(1.0D);
        Speed.movementSpeed.setValue(1.3D);
        MultiAura.cps.setValue(16.0D);
        for (Module module : Client.modules) {
          if (module.name.equalsIgnoreCase("Sprint") || module.name.equalsIgnoreCase("FullBright") && !module.toggled)
            module.toggle();
        }
        while (!MultiAura.priority.getMode().equalsIgnoreCase("Players"))
          MultiAura.priority.cycle(); 
        MultiAura.range.setValue(4.8D);
        Client.addChatMessage("Loaded config \"Redesky\"");
      } else if (args[0].equalsIgnoreCase("Rededark")) {
    	  for(Module m : Client.toggledModules) {
    		  m.toggle();
    	  }
    	  while (!Flight.type.getMode().equalsIgnoreCase("Verus"))
              Flight.type.cycle(); 
    	  while (!Speed.bypass.getMode().equalsIgnoreCase("Spartan"))
              Speed.bypass.cycle();
    	  while (!KillAura.sort.getMode().equalsIgnoreCase("Closest"))
              KillAura.sort.cycle();
    	  while (!KillAura.priority.getMode().equalsIgnoreCase("Players"))
              KillAura.priority.cycle();
    	  while (!NoFall.mode.getMode().equalsIgnoreCase("Default"))
    		  NoFall.mode.cycle();
    	  while (!AntiVoid.mode.getMode().equalsIgnoreCase("NCP"))
    		  AntiVoid.mode.cycle();
    	  while (!Scaffold.mode.getMode().equalsIgnoreCase("V1"))
    		  Scaffold.mode.cycle();
    	  
    	  Scaffold.delay.setValue(0.0D);
    	  ChestStealer.autoClose.enabled = true;
    	  ChestStealer.autoSteal.enabled = true;
    	  ChestStealer.stealSpeed.setValue(50.0D);
    	  KillAura.cps.setValue(16.0D);
    	  KillAura.range.setValue(3.0D);
    	  KillAura.esp.setEnabled(true);
    	  KillAura.noSwing.setEnabled(true);
    	  KillAura.invisibles.setEnabled(false);
    	  KillAura.death.setEnabled(true);
    	  ESP.tracers.setEnabled(true);
    	  ESP.box.setEnabled(false);
    	  ESP.player.setEnabled(true);
    	  ESP.other.setEnabled(false);
    	  ESP.mob.setEnabled(false);
    	  ESP.animal.setEnabled(false);
    	  
    	  nn(new String[] {
    			  "FullBright",
    			  "Sprint",
    			  "InvMove",
    			  "NoFall",
    			  "ESP",
    			  "Velocity",
    			  "Criticals",
    			  "NoSlowdown",
    			  "BPS",
    			  "Step",
    			  "AutoTool",
    			  "AutoArmor",
    			  "LiquidInteract",
    			  "AntiVoid"});
    	  Client.addChatMessage("Loaded config \"Rededark\"");
      } else if(args[0].equalsIgnoreCase("Hypixel")) {
    	  while (!Flight.type.getMode().equalsIgnoreCase("Hypixel"))
              Flight.type.cycle(); 
    	  while (!Speed.bypass.getMode().equalsIgnoreCase("Hypixel"))
              Speed.bypass.cycle();
    	  while (!KillAura.sort.getMode().equalsIgnoreCase("Closest"))
              KillAura.sort.cycle();
    	  while (!KillAura.priority.getMode().equalsIgnoreCase("Players"))
              KillAura.priority.cycle();
    	  while (!NoFall.mode.getMode().equalsIgnoreCase("Hypixel"))
    		  NoFall.mode.cycle();
    	  while (!AntiVoid.mode.getMode().equalsIgnoreCase("Hypixel"))
    		  AntiVoid.mode.cycle();
    	  while (!Scaffold.mode.getMode().equalsIgnoreCase("Hypixel"))
    		  Scaffold.mode.cycle();
    	  while (!Step.mode.getMode().equalsIgnoreCase("Hypixel"))
    		  Step.mode.cycle();
    	  while (!Velocity.mode.getMode().equalsIgnoreCase("Normal"))
    		  Velocity.mode.cycle();
    	  Velocity.horizontal.setValue(0);
    	  Velocity.vertical.setValue(0);
    	  Scaffold.delay.setValue(0.0D);
    	  ChestStealer.autoClose.enabled = true;
    	  ChestStealer.autoSteal.enabled = true;
    	  ChestStealer.stealSpeed.setValue(70.0D);
    	  KillAura.cps.setValue(20.0D);
    	  KillAura.range.setValue(4.3D);
    	  KillAura.esp.setEnabled(true);
    	  KillAura.noSwing.setEnabled(false);
    	  KillAura.invisibles.setEnabled(false);
    	  KillAura.death.setEnabled(true);
    	  ESP.tracers.setEnabled(true);
    	  ESP.box.setEnabled(false);
    	  ESP.player.setEnabled(true);
    	  ESP.other.setEnabled(false);
    	  ESP.mob.setEnabled(false);
    	  ESP.animal.setEnabled(false);
    	  ESP.ESPSelf.setEnabled(true);
    	  for(Module m : Client.toggledModules) {
    		  m.toggled = false;
    	  }
    	  nn(new String[] {
    			  "FullBright",
    			  "Sprint",
    			  "NoFall",
    			  "ESP",
    			  "Velocity",
    			  "BPS",
    			  "Step",
    			  "AutoTool",
    			  "AutoArmor",
    			  "AntiVoid",});
    	  Client.addChatMessage("Loaded config \"Hypixel\"");
      } else {
        Client.addChatMessage("Can not find config \"" + args[0] + "\"");
      } 
    } else {
      Client.addChatMessage("Enter config");
    } 
  }
  private static boolean n(Module m, String s) {
	  return m.name.equalsIgnoreCase(s);
  }
  private static void nn(String[] sl) {
	  for(Module m : Client.modules) {
		  for(String s : sl) {
			  if(n(m, s)) {
				  m.toggled = true;
			  }
		  }
	  }
  }
}
