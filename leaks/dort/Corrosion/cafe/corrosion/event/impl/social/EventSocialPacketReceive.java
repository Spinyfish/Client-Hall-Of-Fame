package cafe.corrosion.event.impl.social;

import cafe.corrosion.event.Event;
import cafe.corrosion.social.packet.SocialPacketIn;

public class EventSocialPacketReceive extends Event {
    private final SocialPacketIn packet;

    public SocialPacketIn getPacket() {
        return this.packet;
    }

    public EventSocialPacketReceive(SocialPacketIn packet) {
        this.packet = packet;
    }
}
