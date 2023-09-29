package wtf.automn.module.impl.world;

import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;

@ModuleInfo(name = "nofall", displayName = "Nofall", category = Category.WORLD)
public class ModuleNofall extends Module {
    @Override
    protected void onDisable() {

    }

    @Override
    protected void onEnable() {

    }

    @EventHandler
    public void onUpdate(final EventPlayerUpdate e) {
        if (this.MC.thePlayer.fallDistance > 0.0F)
            this.MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
    }
}
