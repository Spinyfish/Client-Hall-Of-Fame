package byron.Mono.module.impl.combat;

import byron.Mono.Mono;
import byron.Mono.event.impl.EventPacket;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import com.google.common.eventbus.Subscribe;

@ModuleInterface(name = "OtherDisabler", description = "aaa.", category = Category.Combat)
public class OtherDisabler extends Module{
	
	@Subscribe
	private void onPacket(EventPacket e)
	    {
		 Mono.INSTANCE.sendMessage("Disabling " + e.getPacket().toString());
    	
	        if (e.getPacket() instanceof C00PacketKeepAlive)
	        {
	        	 Mono.INSTANCE.sendMessage("Disabling" + e.getPacket().toString());   
	            e.setCancelled(true);
	           
	        }
	        
	        if (e.getPacket() instanceof C03PacketPlayer)
	         {
	        	   Mono.INSTANCE.sendMessage("Disabling" + e.getPacket().toString());
	             C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) e.getPacket();
	             c03PacketPlayer.isOnGround();
	          
	         }
	         
	         if (e.getPacket() instanceof C03PacketPlayer||  e.getPacket() instanceof C04PacketPlayerPosition ||  e.getPacket() instanceof C05PacketPlayerLook || e.getPacket() instanceof C06PacketPlayerPosLook)
	         {
	        	 Mono.INSTANCE.sendMessage("Disabling" + e.getPacket().toString());
	             C0CPacketInput c0cPacketInput = (C0CPacketInput) e.getPacket();
	             c0cPacketInput.jumping = true;
	            

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
		    }

}
