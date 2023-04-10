package crispy.features.hacks.impl.misc;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.network.play.server.S2APacketParticles;

@HackInfo(name = "AntiCrash", category = Category.MISC)
public class AntiCrash extends Hack {
    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
            EventPacket event = (EventPacket) e;
            if (event.getPacket() instanceof S2APacketParticles) {
                e.setCancelled(true);
            }

        }

    }
}
