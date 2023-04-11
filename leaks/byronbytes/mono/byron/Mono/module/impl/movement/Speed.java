package byron.Mono.module.impl.movement;

import byron.Mono.Mono;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.EventPacket;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.utils.MovementUtils;
import byron.Mono.utils.TimeUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S06PacketUpdateHealth;

import com.google.common.eventbus.Subscribe;

import java.sql.Date;
import java.util.ArrayList;

@ModuleInterface(name = "Speed", description = "Go faster around the map.", category = Category.Movement)
public class Speed extends Module {


    @Override
    public void setup() {
        super.setup();
        ArrayList<String> options = new ArrayList<>();
        options.add("Watchdog");
        options.add("NCP");
        options.add("AAC");
        options.add("Vanilla");
        options.add("Verus");
        options.add("BlocksMC");
        options.add("LowHop");
        rSetting(new Setting("Vanilla Speed Modifier", this, 0.3, 0.2, 1.5, false));
        rSetting(new Setting("Disable on Death", this, true));
        rSetting(new Setting("Speed Mode", this, "Watchdog", options));
    }

    private TimeUtil timeUtil = new TimeUtil();

    @Subscribe
    public void onUpdate(EventUpdate e)
    {

    	 
        switch(getSetting("Speed Mode").getValString())
        {
       
        
            case "Watchdog":
                watchdog();
                break;
            case "Vanilla":
                vanilla();
                break;
            case "NCP":
                ncp();
                break;
            case "Verus":
                verus();
                break;
                
            case "BlocksMC":
            	blocksmc();
            	break;
            	
            case "AAC":
            	aac();
            	break;
            	
            case "LowHop":
            	lowhop();
            	break;
        }
        
   	 if (mc.thePlayer.getHealth() <= 0) { 
		 this.setToggled(false);
		 Mono.INSTANCE.sendAlert("Speed was disabled due to death.");
     }

        }
    
    
    private final void watchdog() {
      //  MovementUtils.setMotion(0.15025644D);
       // MovementUtils.strafe(0.30F);
       
        if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && !mc.thePlayer.isSneaking() && mc.thePlayer.onGround) {
            mc.thePlayer.jump();
    
        }
        
        if(mc.thePlayer.onGround)
        {
        	MovementUtils.defaultSpeed();
        	MovementUtils.strafe(MovementUtils.defaultMoveSpeed());
        	mc.timer.timerSpeed = 0.5F;
        }
        else
        {
        	MovementUtils.setMotion(0.20D);
        	MovementUtils.strafe(0.2D);
        	mc.timer.timerSpeed = 1.0F;
        }
    }
    private final void aac() {
    	
     	if (timeUtil.hasTimePassed(350))
    	{
    		mc.timer.timerSpeed = 0.20F;
    		
    		if (timeUtil.hasTimePassed(700))
        	{
        		mc.timer.timerSpeed = 1.45F;
        	}
    	}
     	
		if (timeUtil.hasTimePassed(800))
    	{
    		timeUtil.reset();
    	}
	
    	   if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && !mc.thePlayer.isSneaking() && mc.thePlayer.onGround) {
	            mc.thePlayer.jump();
	        }
    	
    	
    }
    
    private final void lowhop()
    {
    	   
        if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && !mc.thePlayer.isSneaking() && mc.thePlayer.onGround) {
            mc.thePlayer.jump();
            mc.thePlayer.motionY = 0.0;
    
        }
        
        if(!mc.thePlayer.isInWater() & !mc.thePlayer.isInLava())
        {
        	   if(mc.thePlayer.onGround)
               {
               	MovementUtils.defaultSpeed();
               	MovementUtils.strafe(MovementUtils.defaultMoveSpeed());
               	 mc.thePlayer.jump();
         
               }
               else
               {
               	MovementUtils.setMotion(0.30D);
               	MovementUtils.strafe(0.4D);
                	 mc.thePlayer.motionY = 0.1;
               }
        }
     
    
    }
    
    private final void blocksmc()
    {
    	  if(MovementUtils.isMoving()) {
              if (mc.thePlayer.onGround){
                  MovementUtils.strafe(0.356767f);
                  MovementUtils.setMotion(0.410D);
                  mc.thePlayer.jump();
              }
              else mc.thePlayer.posY -= 0.1;
             // MovementUtils.strafe(0.376767);
          }
    	  
    	   MovementUtils.setMotion(0.350D);
    
    }

    private final void vanilla() {
        MovementUtils.strafe(getSetting("Vanilla Speed Modifier").getValDouble());
        MovementUtils.setMotion(getSetting("Vanilla Speed Modifier").getValDouble());
        MovementUtils.setSpeed(getSetting("Vanilla Speed Modifier").getValDouble());
        if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && !mc.thePlayer.isSneaking() && mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }
     // MovementUtils.setMotion(0.95D);
    }

    private final void verus() {
        MovementUtils.setMotion(0.10D);
        MovementUtils.strafe(0.45F);
        if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && !mc.thePlayer.isSneaking() && mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }

    }
    private final void ncp() {
        MovementUtils.setMotion(0.4044D);
        MovementUtils.strafe(0.26F);
        if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && !mc.thePlayer.isSneaking() && mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }
        
        if(mc.thePlayer.isSneaking())
        {
        	 MovementUtils.strafe(0.26F);
        	MovementUtils.setMotion(0.2044D);
        }
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        mc.thePlayer.setSprinting(false);
        mc.timer.timerSpeed = 1.0F;
        MovementUtils.setSpeed(MovementUtils.defaultMoveSpeed());
        MovementUtils.setMotion(MovementUtils.defaultMoveSpeed());
    }

}
