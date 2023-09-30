/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.movement;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.MathUtil;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

import java.util.List;

public class Speed
        extends Module {
	private Mode Watchdog = new Mode("WatchdogMode", "WatchdogMode", new String[]{"Hvh","Normal","Smart"}, "Normal");
    private Mode mode = new Mode("Mode", "mode", new String[]{"Bhop", "WatchdogBhop" ,"Lowhop", "Ground" ,"SlowLowhop" ,"SlowBhop", "VulcanYport", "VerusBhop","HycraftBhop", "Intave", "BlocksMc", "VerusLowHop","VerusSafeBhop"}, "WatchdogBhop");
    private int stage;
   
    public static Numbers<Number>  speed2 = new Numbers<>("MotionsSpeed", 1f, 1f, 30f, 10f);
    private double movementSpeed;
    private double distance;
    private TimerUtil timer = new TimerUtil();
	private float setMoveSpeednonstrafe;

    public Speed() {
        super("Speed", ModuleType.Movement);
        this.addValues(this.mode, speed2,Watchdog);
    }

    @Override
    public void onDisable() {
        this.mc.timer.timerSpeed = 1.0f;
    }

    private boolean canZoom() {
        if (this.mc.thePlayer.moving() && this.mc.thePlayer.onGround) {
            return true;
        }
        return false;
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(mode.getModeAsString());
    }


    @EventHandler
    private void onMove(EventMove e) {
        if (this.mode.getValue().equals("WatchdogBhop")) {
       if (this.Watchdog.getValue().equals("Smart")) {
            if (this.canZoom()) {
            	mc.thePlayer.moveStrafing = 0.f;
                 e.setY(mc.thePlayer.motionY = 0.42F);

                 this.movementSpeed = 0.30f;
                 
             
            if (mc.thePlayer.hurtTime > 1) {
            	this.movementSpeed = 0.50f;
                
            	
            }
            
            if (mc.thePlayer.hurtTime > 6) {
            	this.movementSpeed = 0.80f;
                
            	
            }
            
          
            }
        	 }
       
       if (this.Watchdog.getValue().equals("Normal")) {
           if (this.canZoom()) {
           	mc.thePlayer.moveStrafing = 0.f;
                e.setY(mc.thePlayer.motionY = 0.42F);

                this.movementSpeed = 0.30f;
                
           
         
           }
       	 }
       
       if (this.Watchdog.getValue().equals("Hvh")) {
           if (this.canZoom()) {
           	mc.thePlayer.moveStrafing = 0.f;
                e.setY(mc.thePlayer.motionY = 0.42F);

                this.movementSpeed = 0.30f;
                
            
           if (mc.thePlayer.hurtTime > 2) {
           	this.movementSpeed = 0.80f;
               
           	
           }
           
           
         
           }
       	 }
            
            
            this.mc.thePlayer.setMoveSpeed2(e, this.movementSpeed);
          
        } else if (this.mode.getValue().equals("VulcanYport")) {
        	this.movementSpeed = 0.28f;
        	
    		
    			if(mc.thePlayer.motionY < 0.4) {
    				
    				mc.timer.timerSpeed = 1.0f;
    			}
    			
    			if(mc.thePlayer.motionY < 0.0) {

    				
    				mc.timer.timerSpeed = 1.0f;
    				
    					
    				
    			}
    			if(mc.thePlayer.motionY < -0.3) {
    				if(mc.thePlayer.onGround){
    					mc.thePlayer.jump();
    					
    				}
    				
    				
    				mc.timer.timerSpeed = 1.55f;
    			}
    			
            this.mc.thePlayer.setMoveSpeed(e, this.movementSpeed);
        } else if (this.mode.getValue().equals("Bhop")) {
            if (this.canZoom()) {
                e.setY(mc.thePlayer.motionY = 0.42F);
                this.movementSpeed = speed2.getValue().intValue();
            } 
            
           this.mc.thePlayer.setMoveSpeed(e, this.movementSpeed);
            
            
        } else if (this.mode.getValue().equals("Lowhop")) {
            if (this.canZoom()) {
                e.setY(mc.thePlayer.motionY = 0.25F);
                this.movementSpeed = speed2.getValue().intValue();
            } 


            this.mc.thePlayer.setMoveSpeed(e, this.movementSpeed);

            
        
            
        } else if (this.mode.getValue().equals("Ground")) {
            if (this.canZoom()) {
                e.setY(mc.thePlayer.motionY = 0.00F);
                this.movementSpeed = speed2.getValue().intValue();
            }

            
           this.mc.thePlayer.setMoveSpeed(e, this.movementSpeed);
                
              } else if (this.mode.getValue().equals("VerusBhop")) {
                  if (this.canZoom()) {
                      e.setY(mc.thePlayer.motionY = 0.43F);
                      this.movementSpeed = 0.4f;
                  } else {
                      this.movementSpeed = 0.4f;
                  }
                  this.mc.thePlayer.setMoveSpeed(e, this.movementSpeed);
                  
                  
              } else if (this.mode.getValue().equals("VerusBhop")) {
                  if (this.canZoom()) {
                      e.setY(mc.thePlayer.motionY = 0.43F);
                      this.movementSpeed = 0.4f;
                  } else {
                      this.movementSpeed = 0.4f;
                  }
                  if (mc.thePlayer.hurtTime > 3) {
                  	this.movementSpeed = 0.200f;
                  	
                  }
                  
                  
                  this.mc.thePlayer.setMoveSpeed(e, this.movementSpeed);
                  
                  
              } else if (this.mode.getValue().equals("HycraftBhop")) {
                  if (this.canZoom()) {
                            mc.thePlayer.moveStrafing = 0.5f;

                  

                            

                            if (mc.timer.timerSpeed == 10);                                   
                              mc.thePlayer.motionY = -0.3;                    
                              System.out.println("FALLING " + mc.thePlayer.motionY);                
                              System.out.println("RESETTING TIMER " + mc.thePlayer.motionY);                
                              mc.timer.timerSpeed = 1.0f;
                              this.mc.thePlayer.setMoveSpeed(e, this.movementSpeed);
                            
                  

                
                
              }
            
              } else if (this.mode.getValue().equals("Intave")) {
                  if (this.canZoom()) {
                	  mc.thePlayer.moveStrafing = 0.5f;
                      e.setY(mc.thePlayer.motionY = 0.42F);
                      this.movementSpeed = 0.45f;
                  } else {
                      this.movementSpeed = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
                  }
                  this.movementSpeed = Math.max(this.movementSpeed, MathUtil.getBaseMovementSpeed());

                  this.mc.thePlayer.setMoveSpeed(e, this.movementSpeed);
                  
              } else if (this.mode.getValue().equals("BlocksMc")) {
                  if (this.canZoom()) {
                	  mc.thePlayer.moveStrafing = 0.5f;
                      e.setY(mc.thePlayer.motionY = 0.42F);
                      this.movementSpeed = 0.57f;
                  } else {
                      this.movementSpeed = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
                  }
                  this.movementSpeed = Math.max(this.movementSpeed, MathUtil.getBaseMovementSpeed());
                  this.mc.thePlayer.setMoveSpeed(e, this.movementSpeed);
                  
                  
              } else if (this.mode.getValue().equals("VerusSafeBhop")) {
                  if (this.canZoom()) {
                      e.setY(mc.thePlayer.motionY = 0.43F);
                      this.movementSpeed = 0.3f;
                      if (mc.thePlayer.hurtTime > 6) {
                    	  mc.thePlayer.setMoveSpeed(e, 4);
                    	  
                      }
                  } else {
                      this.movementSpeed = 0.3f;
                      if (mc.thePlayer.hurtTime > 6) {
                    	  mc.thePlayer.setMoveSpeed(e, 4);
                    	  
                      }
                  }
                 
                  this.mc.thePlayer.setMoveSpeed(e, this.movementSpeed);
                  
              } else if (this.mode.getValue().equals("VerusLowHop")) {
                  if (this.canZoom()) {
                      e.setY(mc.thePlayer.motionY = 0.18F);
                      this.movementSpeed = 0.3f;
                  } else 
                      this.movementSpeed = 0.3f;
                  }else {
                	  this.movementSpeed = 0.4f;
                  }
        
                  this.mc.thePlayer.setMoveSpeed(e, this.movementSpeed);
                  
                  
                 
                  

            
        
        ((TargetStrafe) ModuleManager.getModuleByClass(TargetStrafe.class)).strafe(e, movementSpeed);
              }   

    }


    



