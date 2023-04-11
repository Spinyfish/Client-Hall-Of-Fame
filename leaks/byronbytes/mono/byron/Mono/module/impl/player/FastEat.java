package byron.Mono.module.impl.player;

import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInterface(name = "FastEat", description = "Eat faster.", category = Category.Player)
public class FastEat extends Module {

    boolean PlayerEat = false;

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (!mc.thePlayer.isBlocking() && mc.thePlayer.isEating()) {
            for(int timer = 0; timer < 10; ++timer) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
            }
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
