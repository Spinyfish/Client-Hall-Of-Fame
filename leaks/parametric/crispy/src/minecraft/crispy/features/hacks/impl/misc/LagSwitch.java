package crispy.features.hacks.impl.misc;

import crispy.features.event.Event;
import crispy.features.event.EventDirection;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

import java.util.ArrayList;

@HackInfo(name = "LagSwitch", category = Category.MISC)
public class LagSwitch extends Hack {

    ArrayList<Packet> packets = new ArrayList<>();


    @Override
    public void onEnable() {

        super.onEnable();
    }

    @Override
    public void onDisable() {

        super.onDisable();
    }


    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket && Minecraft.theWorld != null) {
            if(((EventPacket) e).getDirection() == EventDirection.Outgoing) {
                e.setCancelled(true);
                packets.add(((EventPacket) e).getPacket());
            }
        } else if(e instanceof EventUpdate) {
            if(mc.thePlayer.ticksExisted % 20 == 0) {
                if(!packets.isEmpty()) {
                    packets.forEach(packet -> mc.thePlayer.sendQueue.addToSendNoEvent(packet));
                    packets.clear();
                }
            }
        }
    }
}
