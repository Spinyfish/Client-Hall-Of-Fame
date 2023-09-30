package me.nekoWare.client.module.player;

import me.hippo.api.lwjeb.annotation.Handler;
import me.nekoWare.client.event.events.UpdateEvent;
import me.nekoWare.client.module.Module;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.function.Consumer;

public class FastUse extends Module {

    public FastUse(final String name, final int key, final Module.Category category) {
        super(name, key, category);
    }

    @Handler
    public Consumer<UpdateEvent> onUpdate = (event) -> {
        if (mc.thePlayer.isEating() && !(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) { // ItemSword check because it broke for some reason - ???
            for (int i = 0; i < 35; i++)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
        }
    };
}
