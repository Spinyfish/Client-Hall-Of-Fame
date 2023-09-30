package felix.module.impl.world;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketReceive;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.world.World;

public class Ambience extends Module {

    public EnumValue<AmbienceMode> ambienceMode = new EnumValue<>("Ambience Mode", AmbienceMode.Night);

    public Ambience() {
        super("Ambience", 0, ModuleCategory.WORLD);
        addValues(ambienceMode);
        setHidden(true);
    }

    @Handler
    public void onPlayerUpdate(final EventMotionUpdate event) {
    	World world = mc.theWorld;
        switch(ambienceMode.getValue()) {
            case Night:
                world.setWorldTime(18000L);
                break;
            case Day:
                world.setWorldTime(6000L);
                break;
            case Morning:
                world.setWorldTime(0L);
                break;
            case Sunrise:
                world.setWorldTime(13000L);
                break;
        }
    }
    
    @Handler
    public void onReceivePacket(final EventPacketReceive event) {
    	if (event.getPacket() instanceof S03PacketTimeUpdate) {
    		event.setCancelled(true);
    	}
    }

    public enum AmbienceMode {
        Night, Day, Morning, Sunrise;
    }
}
