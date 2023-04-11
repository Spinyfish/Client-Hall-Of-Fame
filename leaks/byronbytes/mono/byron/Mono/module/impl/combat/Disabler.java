package byron.Mono.module.impl.combat;

import byron.Mono.Mono;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.EventPacket;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;

import java.io.Console;
import java.util.ArrayList;

import javax.swing.DebugGraphics;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.optifine.Log;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;


@ModuleInterface(name = "Disabler", description = "Disable it lol.", category = Category.Combat)
public class Disabler extends Module
{
	  @Override
	    public void setup()
	    {
		  ArrayList<String> options = new ArrayList<>();
	        super.setup();
	        options.add("Default");
	        options.add("OnlyMC");
	        options.add("Old Redesky");
	        options.add("Verus");
	        rSetting(new Setting("Disabler Mode", this, "Default", options));
	    }

	  
	  
   
    @Subscribe
    public void onPacket (EventPacket e)
    {
    	        switch(getSetting("Disabler Mode").getValString())
    	        {
    	            case "Default":
    	                defaultD(e);
    	                break;
    	            case "OnlyMC":
    	                onlymc(e);
    	                break;
    	            case "Verus":
    	            	verus(e);
    	            	break;
    	            case "Old Redesky":
    	            oldredesky(e);
    	            break;
    	        }
    	    }


    private void defaultD(EventPacket e)
    {
        if (e.getPacket() instanceof C03PacketPlayer)
        {
            mc.getNetHandler().addToSendQueue(new C0CPacketInput());

        }
        
        if (e.getPacket() instanceof C00PacketKeepAlive)
        {
            e.setCancelled(true);
        }
        
        
        if (e.getPacket() instanceof C03PacketPlayer||  e.getPacket() instanceof C04PacketPlayerPosition ||  e.getPacket() instanceof C05PacketPlayerLook || e.getPacket() instanceof C06PacketPlayerPosLook)
        {
            C0CPacketInput c0cPacketInput = (C0CPacketInput) e.getPacket();
            c0cPacketInput.jumping = true;

        }
        
        // Apple's Verus Fly disabler
        if (e.getPacket() instanceof C03PacketPlayer)
        {
            C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) e.getPacket();
            c03PacketPlayer.isOnGround();
        }

        
        
    }
    ArrayList<Packet> transactions = new ArrayList<Packet>();
    int currentTransaction = 0;
    private void onlymc(EventPacket e)
    {
    	   if(e.getPacket() instanceof C0FPacketConfirmTransaction) {
               transactions.add(e.getPacket());
               e.setCancelled(true);
           }
           
           if(e.getPacket()  instanceof C00PacketKeepAlive) {
               ((C00PacketKeepAlive)e.getPacket()).key -= RandomUtils.nextInt(1, 2147483647);
           }
           
           if(e.getPacket()  instanceof C03PacketPlayer) {
               mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
           }
           
           if(e.getPacket()  instanceof C03PacketPlayer.C04PacketPlayerPosition) {
               mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
           }
           
           if(e.getPacket()  instanceof C03PacketPlayer.C05PacketPlayerLook) {
               mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
           }
           
           if(e.getPacket()  instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
               mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
           }
           
           
    }
    
    private void verus(EventPacket e)
    {
    	
         if(mc.thePlayer.ticksExisted % 120 == 0 && (transactions.size()-1) > currentTransaction) {
            mc.thePlayer.sendQueue.addToSendQueue(transactions.get(++currentTransaction));
         }
         
         // Apple's Verus Fly disabler
         if (e.getPacket() instanceof C03PacketPlayer)
         {
             C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) e.getPacket();
             c03PacketPlayer.isOnGround();
         }
         
         if (e.getPacket() instanceof C03PacketPlayer||  e.getPacket() instanceof C04PacketPlayerPosition ||  e.getPacket() instanceof C05PacketPlayerLook || e.getPacket() instanceof C06PacketPlayerPosLook)
         {
             C0CPacketInput c0cPacketInput = (C0CPacketInput) e.getPacket();
             c0cPacketInput.jumping = true;

         }

    }
    
    private void oldredesky(EventPacket e)
    {
    	// this is totally not skidded wdym??!
            if(e.getPacket() instanceof C0FPacketConfirmTransaction) {
                e.setCancelled(true);
            }
            else if(e.getPacket() instanceof C0BPacketEntityAction) {
                e.setCancelled(true);
            }
            else {
                final boolean b = e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook;
            }
        }
    
    @Override
    public void onEnable()
    { 
    super.onEnable(); 
    mc.thePlayer.ticksExisted = 0;
    }
    @Override
    public void onDisable()
    {
    super.onDisable(); 
    transactions.clear();
    currentTransaction = 0;
    }
}
