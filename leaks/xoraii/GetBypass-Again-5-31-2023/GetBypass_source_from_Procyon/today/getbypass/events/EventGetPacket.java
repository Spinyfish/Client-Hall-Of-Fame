// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.events;

import net.minecraft.network.Packet;

public class EventGetPacket extends Event<EventGetPacket>
{
    Packet packet;
    
    public EventGetPacket(final Packet packet) {
        this.packet = packet;
    }
    
    public Packet getPacket() {
        return this.packet;
    }
}
