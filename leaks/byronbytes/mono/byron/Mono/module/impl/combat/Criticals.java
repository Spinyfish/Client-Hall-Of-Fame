package byron.Mono.module.impl.combat;


import java.util.ArrayList;
import com.google.common.eventbus.Subscribe;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.EventAttack;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.utils.MovementUtils;
import byron.Mono.utils.TimeUtil;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

@ModuleInterface(name = "Criticals", description = "damn age.", category = Category.Combat)
public class Criticals extends Module {

    
    @Override
    public void setup() {
        super.setup();
        ArrayList<String> options = new ArrayList<>();
        options.add("Packet");
        options.add("Jump");
        rSetting(new Setting("Packet Mode", this, "Packet", options));
    }
    
    @Subscribe
    public void onAttack (EventAttack e)
    {
        if (getSetting("Packet Mode").getValString() == "Jump")
        {
            if (mc.thePlayer.onGround)
            {
                mc.thePlayer.jump();
            }
        }
        else
        {
            mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.25, mc.thePlayer.posZ, false));
            mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }

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
