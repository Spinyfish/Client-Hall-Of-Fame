package crispy.features.hacks.impl.player;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@HackInfo(name = "NoRotate", category = Category.MISC)
public class NoRotate extends Hack {
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventPacket) {
            Packet p = ((EventPacket) e).getPacket();
            if(p instanceof S08PacketPlayerPosLook) {
                S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) p;
                s08PacketPlayerPosLook.setYaw(mc.thePlayer.rotationYaw);
                s08PacketPlayerPosLook.setPitch(mc.thePlayer.rotationPitch);
            }
        }
    }
}
