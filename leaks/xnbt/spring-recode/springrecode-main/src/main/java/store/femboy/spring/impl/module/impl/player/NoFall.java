package store.femboy.spring.impl.module.impl.player;

import best.azura.eventbus.core.EventPriority;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.event.impl.EventUpdate;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;

public final class NoFall extends Module {
    public NoFall() {
        super("NoFall", "No damage when u fall", Keyboard.KEY_I, Category.PLAYER);
    }

    @EventHandler(EventPriority.LOWEST)
    public final Listener<EventUpdate> onUpdate = e ->{
        if (mc.thePlayer.fallDistance>0){
            sendSilentPacket(new C03PacketPlayer(true));
            mc.thePlayer.fallDistance = 0;
        }
    };

}
