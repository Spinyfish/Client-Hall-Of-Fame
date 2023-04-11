package byron.Mono.module.impl.movement;

import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInterface(name = "NoFall", description = "You don't take fall damage anymore.", category = Category.Movement)
public class NoFall extends Module {

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if(mc.thePlayer.fallDistance > 3.0F) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
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
