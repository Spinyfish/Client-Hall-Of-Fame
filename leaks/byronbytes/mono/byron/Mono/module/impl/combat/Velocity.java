package byron.Mono.module.impl.combat;

import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.entity.EntityPlayerSP;

@ModuleInterface(name = "Velocity", description = "Reduce your knockback.", category = Category.Combat)
public class Velocity extends Module {

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime && mc.thePlayer.maxHurtTime > 0) {
            EntityPlayerSP var10000 = mc.thePlayer;
            var10000.motionX *= 0.0D;
            var10000 = mc.thePlayer;
            var10000.motionY *= 0.0D;
            var10000 = mc.thePlayer;
            var10000.motionZ *= 0.0D;
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
