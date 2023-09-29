package me.wavelength.baseclient.module.modules.movement;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.events.PacketSentEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.MovementUtils;
import me.wavelength.baseclient.utils.Timer;
import me.wavelength.baseclient.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class SpeedWalk extends Module {

    public SpeedWalk() {
        super("Speed", "Speed up player Movement bps!", Keyboard.KEY_V, Category.MOVEMENT, AntiCheat.VANILLA, AntiCheat.VERUS, AntiCheat.VULCAN, AntiCheat.NCP, AntiCheat.HYPIXEL, AntiCheat.HYPIXELZOOM, AntiCheat.HYPIXELFAST, AntiCheat.HYPIXELLOW, AntiCheat.GHOSTLY, AntiCheat.MATRIX, AntiCheat.SPARTAN, AntiCheat.MORGAN);
    }

    @Override
    public void setup() {
        moduleSettings.addDefault("speed", 1.0D);
        moduleSettings.addDefault("TimerSpeed", 1.0D);
    }

    int VulcanSpeedDelay;
    int VulcanTimerPulse;

    @Override
    public void onEnable() {
    	
        VulcanTimerPulse = 10;
        VulcanSpeedDelay = 0;
        mc.timer.timerSpeed = 1f;
    }
    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
    }
    
    public void onPacketSent(PacketSentEvent event) {
        if(this.antiCheat == AntiCheat.HYPIXELFAST) {
        	if(mc.thePlayer.onGround && mc.thePlayer.isCollided) {
        		//mc.thePlayer.jump();

        		mc.timer.timerSpeed = 1;
        	}else {
        		mc.timer.timerSpeed = 1.05;
        	}

        	
        	Utils.message = "" + ((mc.timer.timerSpeed));
        	//Utils.print();
        }
    }
    
    
    @Override
    public void onUpdate(UpdateEvent event) {
    	
    	if(mc.getCurrentServerData().serverIP.equalsIgnoreCase("hypixel.net") || mc.getCurrentServerData().serverIP.equalsIgnoreCase("hypixel.io") ||  mc.getCurrentServerData().serverIP.equalsIgnoreCase("mc.hypixel.net") || mc.getCurrentServerData().serverIP.equalsIgnoreCase("mc.hypixel.io")) {
    		Utils.message = "This module has been disabled due to unstable hypixel bypasses.";
    		Utils.print();
    		toggle();
    	}
    	
        double speed = moduleSettings.getDouble("speed");
        
        if(this.antiCheat == AntiCheat.SPARTAN) {
        	mc.timer.timerSpeed = this.moduleSettings.getDouble("TimerSpeed");
        }
        
        if(this.antiCheat == AntiCheat.MORGAN) {
        	if(mc.thePlayer.onGround) {
        		if(mc.thePlayer.isSprinting()) {
            		if(mc.gameSettings.keyBindForward.isKeyDown()) {
                			MovementUtils.setMotion(0.5);
                			mc.timer.timerSpeed = 1;
                		}
                    	mc.thePlayer.motionX *= 1.2;
                    	mc.thePlayer.motionZ *= 1.2;
        		}else {
        			MovementUtils.setMotion(0.3);
        		}
        	}
        }


        
        //Hypixel lowhop that uses almost no timer, don't edit this pls
        if(this.antiCheat == AntiCheat.HYPIXELLOW) {
            if(MovementUtils.isMoving() && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
            	mc.thePlayer.jump();
            }else {
            	mc.thePlayer.motionX *= 1.01;
            	mc.thePlayer.motionZ *= 1.01;
            }
        }

        
        //Fast groundspeed that loks like a lowhop lol
        if(this.antiCheat == AntiCheat.HYPIXELFAST) {
        	
        	VulcanSpeedDelay++;
        	if(mc.thePlayer.onGround) {
            	if(VulcanSpeedDelay >= 5) {
                    mc.timer.timerSpeed = 1.1290000016142 + (Math.random()/2);
                    VulcanSpeedDelay = 0;
            	}else {
                    
            	}
        	}else {
                mc.timer.timerSpeed = 1;
        	}



            
            
        }

        //use HYPIXELFAST for better yes
        if(this.antiCheat == AntiCheat.HYPIXELZOOM) {
        	mc.thePlayer.onGround = true;
                if(mc.thePlayer.isCollided) {
                	mc.thePlayer.jump();
                    mc.timer.timerSpeed = 25;

                }else {
                    mc.timer.timerSpeed = 1;
                }
            
            
        }

        if(this.antiCheat == AntiCheat.VERUS) {

            if(MovementUtils.isMoving() && mc.thePlayer.isCollidedVertically) {
                MovementUtils.setMotion(0.36);
                mc.thePlayer.jump();
            }

            MovementUtils.setMotion(0.32);
        }
        
        if(this.antiCheat == AntiCheat.GHOSTLY) {

            if(MovementUtils.isMoving() && mc.thePlayer.isCollidedVertically) {
            	if(mc.thePlayer.onGround) {
            		mc.thePlayer.jump();
                	MovementUtils.setMotion(0.36);
            	}
            }else {
            	MovementUtils.setMotion(0.35);
            }
        }

        //whoever put movementUtil shit here, you're fucking retarded.
        if(this.antiCheat == AntiCheat.NCP) {
        	mc.thePlayer.onGround = true;
            if(MovementUtils.isMoving() && mc.thePlayer.isCollidedVertically) {
            	mc.thePlayer.jump();
            	VulcanSpeedDelay = 0;
            }else {
            	


            }
        }
        
        //idk if this silents or not
        if(this.antiCheat == AntiCheat.MATRIX) {
        	
        	if(mc.gameSettings.keyBindJump.isKeyDown()) {
        		mc.gameSettings.keyBindJump.setPressed(false);
        	}
        	VulcanSpeedDelay++;
            if(mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
            	mc.timer.timerSpeed = 1.299381;
            	mc.thePlayer.jump();
            	VulcanSpeedDelay = 0;
            }else {

            	
            	
            	if(VulcanSpeedDelay <= 8 && VulcanSpeedDelay > 4) {
                	mc.timer.timerSpeed = 0.92831;
                    mc.thePlayer.onGround = false;
            	}else {
                	mc.timer.timerSpeed =  1.129821;
                    mc.thePlayer.onGround = false;
            	}
            }
        }

        if(this.antiCheat == AntiCheat.VULCAN) {

            VulcanTimerPulse++;
            VulcanSpeedDelay++;

            if(MovementUtils.isMoving() && mc.thePlayer.isCollidedVertically)
                mc.thePlayer.jump();

            if(mc.thePlayer.isCollided) {
                mc.thePlayer.onGround = true;
            }else {
                mc.thePlayer.onGround = false;
            }


            if(VulcanTimerPulse <= 2) {
                mc.timer.timerSpeed = 0.5f;
            }

            if(VulcanTimerPulse > 3) {
                mc.timer.timerSpeed = 250f;
            }

            if(VulcanTimerPulse >= 6) {
                VulcanTimerPulse = 0;
            }
        }







        if(this.antiCheat == AntiCheat.VANILLA) {
            mc.timer.timerSpeed = 1f;
            	MovementUtils.setMotion(speed);

            if(MovementUtils.isMoving() && mc.thePlayer.isCollided && mc.thePlayer.onGround)
                mc.thePlayer.jump();
        }

        
        //unpatch hot
        if(this.antiCheat == AntiCheat.HYPIXEL) {
        	mc.thePlayer.onGround = true;
                if(MovementUtils.isMoving() && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
                	mc.thePlayer.jump();
                	mc.timer.timerSpeed = 1f;
                }
                	mc.timer.timerSpeed = this.moduleSettings.getDouble("timerspeed");


                
        }
    }

    public static float getDirection() {
        Minecraft mc = Minecraft.getMinecraft();
        float var1 = mc.thePlayer.rotationYaw;

        if(mc.thePlayer.moveForward > 0.0F) { // if the player walks backward !
            var1 += 180.0F;
        }

        float forward = 1.0F;

        if(mc.thePlayer.moveForward  < 0.0F) {
            forward = -0.5F;
        }else  if(mc.thePlayer.moveForward > 0.0F){
            forward = 0.5F;
        }

        if(mc.thePlayer.moveStrafing > 0.0F) {
            var1 -= 90.0F * forward;
        }

        if(mc.thePlayer.moveStrafing < 0.0F) {
            var1 += 90.0F * forward;
        }

        var1 *= 0.017453292F;
        return var1;

    }

}