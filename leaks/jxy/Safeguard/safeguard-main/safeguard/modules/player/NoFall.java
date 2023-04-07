package intentions.modules.player;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import intentions.settings.BooleanSetting;
import intentions.settings.ModeSetting;
import intentions.settings.Setting;
import intentions.util.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class NoFall extends Module {
  
  public static boolean shouldWork = true;
  private BooleanSetting steppatch = new BooleanSetting("StepPatch", false);
  public static ModeSetting mode = new ModeSetting("Mode", "Default", new String[] {"ACD", "Default", "Hypixel"});
  public boolean NoFall, doJump=true;
  
  public NoFall() {
    super("NoFall", 38, Module.Category.PLAYER, "Prevents fall damage", true);
    addSettings(new Setting[] { (Setting)this.steppatch, (Setting)this.mode });
  }
  
  public void onEnable() {
    NoFall = true;
  }
  
  public void onDisable() {
    NoFall = false;
  }
  
  public void onEvent(Event e) {
    if (e instanceof intentions.events.listeners.EventUpdate && 
      e.isPre()
    	&& NoFall && shouldWork) {
	    
    	if(mode.getMode().equalsIgnoreCase("ACD")) {
    		
    		double a = mc.thePlayer.fallDistance * 0.01;
    		if(a > 1.5) a = 1.5;
    		
    		if(PlayerUtil.getPlayerHeightDouble() <= 1.5 + a && mc.thePlayer.fallDistance > 3) {
    			mc.thePlayer.motionY = 0.08f;
    			mc.thePlayer.fallDistance = 0;
    		}
    		
    		return;
    	}
    	
    	boolean stepEnabled = false;
	      for(Module module : Client.modules) {
	    	  if(module.name.equalsIgnoreCase("Step") && module.isEnabled()) {
	    		  stepEnabled = true;
	    		  break;
	    	  }
	      }
	      if (this.mc.thePlayer.fallDistance >= 2 || (this.steppatch.isEnabled() && stepEnabled)) {
	        this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer(true));
	      }
    
    }
  } 

  public boolean a(int i) {
	  return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - i, mc.thePlayer.posZ)).getBlock() != Blocks.air;
  }
  
}
