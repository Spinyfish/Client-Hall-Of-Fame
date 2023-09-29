// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.events.listeners;

import net.minecraft.network.Packet;
import today.getbypass.events.Event;

public class EventPacket extends Event<EventPacket>
{
    public static Packet packet;
    public boolean cancelled;
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public EventPacket(final Packet packet) {
        EventPacket.packet = packet;
    }
    
    public static Packet getPacket() {
        return EventPacket.packet;
    }
    
    public void setPacket(final Packet packet) {
        EventPacket.packet = packet;
    }
}
