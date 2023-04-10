package crispy.features.hacks.impl.misc;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.superblaubeere27.valuesystem.NumberValue;

@HackInfo(name = "TimeChanger", category = Category.RENDER)
public class TimeChanger extends Hack {
    NumberValue<Integer> time = new NumberValue<>("Time", 20000, 20000, 28000);

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventUpdate) {
            setDisplayName(getName() + " \2477" + time.getObject());
            if (Minecraft.theWorld != null) {
                Minecraft.theWorld.setWorldTime(time.getObject());
            }
        }
        if (e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();
            if (packet instanceof S03PacketTimeUpdate) {
                e.setCancelled(true);
            }
        }
    }
}
