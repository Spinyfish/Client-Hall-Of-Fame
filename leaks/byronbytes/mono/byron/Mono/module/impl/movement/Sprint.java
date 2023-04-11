package byron.Mono.module.impl.movement;

import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;

@ModuleInterface(name = "Sprint", description = "Keep sprinting while not clicking.", category = Category.Movement)
public class Sprint extends Module {

    @Subscribe
    public void onUpdate(EventUpdate e)
    {
        if(!mc.thePlayer.isCollidedHorizontally && mc.thePlayer.moveForward > 0) {
            mc.thePlayer.setSprinting(true);
        }
    }


    @Override
    public void onDisable()
    {
        super.onDisable();
        mc.thePlayer.setSprinting(false);
    }
}
