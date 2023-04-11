package byron.Mono.module.impl.combat;

import byron.Mono.Mono;
import byron.Mono.event.impl.EventAttack;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import net.minecraft.network.play.client.C0BPacketEntityAction;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

@ModuleInterface(name = "WTap", description = "Makes you give more KB.", category = Category.Combat)
public class WTap extends Module{

	   @Subscribe
	    public void onAttack (EventAttack e)
	    {
	        if (mc.thePlayer.isSprinting())
	        {
	            mc.thePlayer.setSprinting(true);
	        }

	        mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
	        mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING)); 
	    }
	   
	   @Override
	    public void onEnable() {
	        super.onEnable();
	    }

	    @Override
	    public void onDisable() {
	        super.onDisable();
	    }
	
	
}
