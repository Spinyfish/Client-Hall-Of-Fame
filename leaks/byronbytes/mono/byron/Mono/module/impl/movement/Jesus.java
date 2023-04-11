package byron.Mono.module.impl.movement;

import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;

@ModuleInterface(name = "Jesus", description = "jesus christ.", category = Category.Movement)
public class Jesus extends Module {

    @Subscribe
    public void onUpdate(EventUpdate e) {
        mc.thePlayer.motionY = 0.099999997D;
        mc.thePlayer.onGround = true;
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
