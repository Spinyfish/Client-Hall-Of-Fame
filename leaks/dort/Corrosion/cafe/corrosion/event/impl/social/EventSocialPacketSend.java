package cafe.corrosion.event.impl.social;

import cafe.corrosion.event.Event;
import cafe.corrosion.event.attribute.EventCancellable;
import cafe.corrosion.social.packet.SocialPacketOut;

public class EventSocialPacketSend extends Event implements EventCancellable {
    private final SocialPacketOut packet;

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public SocialPacketOut getPacket() {
        return this.packet;
    }

    public EventSocialPacketSend(SocialPacketOut packet) {
        this.packet = packet;
    }
}
