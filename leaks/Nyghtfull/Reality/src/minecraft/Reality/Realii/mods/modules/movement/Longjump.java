package Reality.Realii.mods.modules.movement;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.player.PlayerUtils;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.MathUtil;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.player.PlayerUtils;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.MathUtil;

public class Longjump extends Module{
	private Mode mode = new Mode("Mode", "mode", new String[]{"Vulcan", "Hycraft","HypixelBow"}, "Vulcan");
	private Option<Boolean> sufix = new Option<Boolean>("ShowSufix", "ShowSufix", false);
	private int stage;
	public Longjump(){
		super("Longjump", ModuleType.Movement);
		this.addValues(this.mode, this.sufix);
		
	}

    @Override
    public void onEnable() {
        super.onEnable();
       
        
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.mc.timer.timerSpeed = 1.0f;
        
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
    if (this.mode.getValue().equals("Hycraft")) {
    	mc.thePlayer.motionY = 0.0;	
    }
   
    
 
    	
    
    
    if (this.sufix.getValue().booleanValue()) {
        this.setSuffix(mode.getModeAsString());
        
    }
    
    
    else if (this.mode.getValue().equals("Vulcan")) {
    		
    	
    }
        }
    
    public void onEvent(EventPreUpdate e) {
    	 if (this.mode.getValue().equals("HypixelBow")) {
    	if (mc.thePlayer.hurtTime > 2) {
            mc.thePlayer.motionY = this.mc.thePlayer.motionY + 5.0;
            mc.thePlayer.jump();
            mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.50, this.mc.thePlayer.posZ);
            mc.thePlayer.moveStrafing = 2f;
    	}
    	 }
    	
    	
    }
    

    @EventHandler
    public void onMove(EventMove e) {
    if (this.mode.getValue().equals("Hycraft")) {
        if (PlayerUtils.isMoving()) {
        mc.thePlayer.setMoveSpeed(e, 3);
        this.mc.timer.timerSpeed = 0.3f;
        e.setY(mc.thePlayer.motionY = 0.17F);
        }
        
        else if (this.mode.getValue().equals("Vulcan")) {
        	 
        	
        }
        
        
        }
    }
    
   
    
    

}
