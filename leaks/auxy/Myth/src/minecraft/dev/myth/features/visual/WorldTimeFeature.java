/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 06.09.22, 01:21
 */
package dev.myth.features.visual;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.feature.Feature;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.NumberSetting;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@Feature.Info(name = "WorldTime", category = Feature.Category.VISUAL)
public class WorldTimeFeature extends Feature {

    public final NumberSetting time = new NumberSetting("Time", 10000, 0, 24000, 10);

    @Handler
    public final Listener<UpdateEvent> eventListener = event -> {
        getWorld().setWorldTime(time.getValue().intValue());
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if(getWorld() == null) return;
        if(event.getPacket() instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
            getWorld().setWorldTime(time.getValue().intValue());
        }
    };
}
