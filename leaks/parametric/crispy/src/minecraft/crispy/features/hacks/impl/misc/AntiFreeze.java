package crispy.features.hacks.impl.misc;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

@HackInfo(name = "AntiFreeze", category = Category.MISC)
public class AntiFreeze extends Hack {

    private boolean frozen;

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();
            if (packet instanceof S2DPacketOpenWindow) {
                S2DPacketOpenWindow packetOpenWindow = (S2DPacketOpenWindow) packet;
                if (packetOpenWindow.getWindowTitle().getUnformattedText().contains("frozen")) {
                    if (!frozen) NotificationPublisher.queue("Anti Freeze", "You have been frozen by a staff member!", NotificationType.ERROR, 5000);
                    frozen = true;
                    e.setCancelled(true);
                }
            }
        }
    }


}
