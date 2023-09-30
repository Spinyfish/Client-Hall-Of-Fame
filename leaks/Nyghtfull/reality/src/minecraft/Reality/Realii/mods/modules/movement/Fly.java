package Reality.Realii.mods.modules.movement;

import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPacketSend;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.player.Helper;
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

import java.util.ArrayList;
import java.util.List;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPacketSend;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class Fly extends Module{
	private Mode mode = new Mode("Mode", "mode", new String[]{"Motion", "Glide","VerusDamage","CubeCraft","HycraftTnt","IntaveBoat"}, "Motion");
	private Option<Boolean> sufix = new Option<Boolean>("ShowSufix", "ShowSufix", false);
	 public static Numbers<Number>  speed = new Numbers<>("MotionFlySpeed", 1f, 5f, 30f, 10f);
    private int stage;
    private double movementFly;
    private double distance;
    private TimerUtil timer = new TimerUtil();
    private List<Packet> packetList = new ArrayList<Packet>();

    public Fly() {
        super("Fly", ModuleType.Movement);
        this.addValues(this.mode, this.sufix, speed);
        
	
		
	}

    @Override
    public void onEnable() {
    	if (this.mode.getValue().equals("VerusDamage")) {
    		mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
    	Helper.sendMessage("wait until mesagge to fly");
    	Helper.sendMessage("Some Verus checks are now a sleep");
    	}
    	if (this.mode.getValue().equals("IntaveBoat")) {
    		Helper.sendMessage("You Must be in a boat to fly otherwise it will not work just saying");
    		
    	}
    	
    	
        super.onEnable();
        mc.timer.timerSpeed = 1.0f;
        

        	
        }        
        
       
        
        	
    

    @Override
    public void onDisable() {
    	if (this.mode.getValue().equals("CubeCraft")) {
    	
        for (Packet packet : this.packetList) {
            this.mc.getNetHandler().addToSendQueue(packet);
        }
        this.packetList.clear();
    	}
    	
    	if (this.mode.getValue().equals("HycraftTnt")) {
        	
            for (Packet packet : this.packetList) {
                this.mc.getNetHandler().addToSendQueue(packet);
            }
            this.packetList.clear();
        	}
    	
        if (this.mode.getValue().equals("IntaveBoat")) {
        	mc.timer.timerSpeed = 1.4f;
        	
        	
        }
        if (this.mode.getValue().equals("VerusDamage")) {
        	mc.timer.timerSpeed = 1.0f;
        	
        }
        
    
        super.onDisable();

        
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
    	if (this.sufix.getValue().booleanValue()) {
            this.setSuffix(mode.getModeAsString());
            
        }
    	if (this.mode.getValue().equals("Motion")) {
    	mc.thePlayer.motionY = 0.0;
        if (mc.gameSettings.keyBindJump.isPressed()) {
            mc.thePlayer.motionY = 5.000;
        } else if (mc.gameSettings.keyBindSneak.isPressed()) {
            mc.thePlayer.motionY = -5.000;
        }
        
        }
        if (this.mode.getValue().equals("CubeCraft")) {
        	mc.thePlayer.motionY = 0.0;
        	
    	}
        
    
    if (this.mode.getValue().equals("HycraftTnt")) {
    	mc.thePlayer.motionY = 0.0;
    	
	}
    	
    	
    	
    	if (this.mode.getValue().equals("VerusDamage")) {
    		if (mc.thePlayer.hurtTime > -1) {
    			if(mc.thePlayer.motionY < 0.4) {
    				
    				mc.timer.timerSpeed = 1.0f;
    			}
    			
    			if(mc.thePlayer.motionY < 0.0) {
    				mc.thePlayer.onGround = true;
    				mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
    				mc.timer.timerSpeed = 0.7f;
    				
    					
    				
    			}
    			if(mc.thePlayer.motionY < -0.4) {
    				mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
    				mc.thePlayer.jump();
    				
    			}
    		

    	        
    			
    			
    		}
    		
    		
    		 
    		 }
    	if (this.mode.getValue().equals("IntaveBoat")) {
    		mc.thePlayer.motionY = 0.31;
    		mc.timer.timerSpeed = 0.6f;
    		
    		
    		
    	}
    	
    	    	
    	
    			
    			
    		    
    		
    	}
    
    
    
    public void onEvent(EventMove e) {
        if(e instanceof EventMove) {
            mc.thePlayer.onGround = true;
        }
    }
    
    @EventHandler
    private void onPacketSend(EventPacketSend event) {
    	if (this.mode.getValue().equals("CubeCraft")) {
        if (event.getPacket() instanceof C04PacketPlayerPosition || event.getPacket() instanceof C06PacketPlayerPosLook || event.getPacket() instanceof  C0BPacketEntityAction || event.getPacket() instanceof C02PacketUseEntity|| event.getPacket() instanceof S08PacketPlayerPosLook ) {
            this.packetList.add(event.getPacket());
            event.setCancelled(true);
            
            
        }
        
    	}
    	
    	if (this.mode.getValue().equals("HycraftTnt")) {
            if (event.getPacket() instanceof C04PacketPlayerPosition || event.getPacket() instanceof C06PacketPlayerPosLook || event.getPacket() instanceof  C0BPacketEntityAction || event.getPacket() instanceof C02PacketUseEntity|| event.getPacket() instanceof S08PacketPlayerPosLook ) {
                this.packetList.add(event.getPacket());
                event.setCancelled(true);
                
                
            }
            
        	}
    }
    


    	
    	
    

    @EventHandler
    public void onMove(EventMove e) {
    	if (this.mode.getValue().equals("Motion")) {
        if (PlayerUtils.isMoving()) {
        mc.thePlayer.motionY = 0.0;
            mc.thePlayer.setMoveSpeed(e, speed.getValue().intValue());
        }
        
    	}
        
        if (this.mode.getValue().equals("VerusDamage")) {
        	 if (mc.thePlayer.hurtTime > 6) {
        		 mc.thePlayer.motionY = 0.0;
        		 
        		 mc.timer.timerSpeed = 0.3f;
        	mc.thePlayer.setMoveSpeed(e, 6);
        	Helper.sendMessage("Funny Packets Boost =) =) ");
        	 }
        	 
        	 else if (mc.thePlayer.hurtTime > -1) {
        		 mc.timer.timerSpeed = 1.0f;
        		 mc.thePlayer.setMoveSpeed(e, 0.4);

    		 }
        	
        }
        
          if (this.mode.getValue().equals("CubeCraft")) {
        	  
        	  mc.thePlayer.setMoveSpeed(e, 4);
        	  mc.timer.timerSpeed = 1.03f;
    		
    	}
          
          if (this.mode.getValue().equals("HycraftTnt")) {
        	  
        	  mc.thePlayer.setMoveSpeed(e, 6);
        	  mc.timer.timerSpeed = 1.03f;
    		
    	}
          
      	if (this.mode.getValue().equals("IntaveBoat")) {
      		 mc.thePlayer.setMoveSpeed(e, 0.4);
      		
      	}
    }
    

    
   
    
    

}
