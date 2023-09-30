/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.event;

import lombok.NonNull;
import net.minecraft.network.Packet;
import wtf.monsoon.api.event.Event;

public class EventPacket
extends Event {
    @NonNull
    public Packet packet;
    public boolean cancelled;
    @NonNull
    public Direction direction;

    public EventPacket(@NonNull Packet packet, @NonNull Direction direction) {
        if (packet == null) {
            throw new NullPointerException("packet is marked non-null but is null");
        }
        if (direction == null) {
            throw new NullPointerException("direction is marked non-null but is null");
        }
        this.packet = packet;
        this.direction = direction;
    }

    @NonNull
    public Packet getPacket() {
        return this.packet;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @NonNull
    public Direction getDirection() {
        return this.direction;
    }

    public void setPacket(@NonNull Packet packet) {
        if (packet == null) {
            throw new NullPointerException("packet is marked non-null but is null");
        }
        this.packet = packet;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setDirection(@NonNull Direction direction) {
        if (direction == null) {
            throw new NullPointerException("direction is marked non-null but is null");
        }
        this.direction = direction;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventPacket)) {
            return false;
        }
        EventPacket other = (EventPacket)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isCancelled() != other.isCancelled()) {
            return false;
        }
        Packet this$packet = this.getPacket();
        Packet other$packet = other.getPacket();
        if (this$packet == null ? other$packet != null : !this$packet.equals(other$packet)) {
            return false;
        }
        Direction this$direction = this.getDirection();
        Direction other$direction = other.getDirection();
        return !(this$direction == null ? other$direction != null : !((Object)((Object)this$direction)).equals((Object)other$direction));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventPacket;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isCancelled() ? 79 : 97);
        Packet $packet = this.getPacket();
        result = result * 59 + ($packet == null ? 43 : $packet.hashCode());
        Direction $direction = this.getDirection();
        result = result * 59 + ($direction == null ? 43 : ((Object)((Object)$direction)).hashCode());
        return result;
    }

    public String toString() {
        return "EventPacket(packet=" + this.getPacket() + ", cancelled=" + this.isCancelled() + ", direction=" + (Object)((Object)this.getDirection()) + ")";
    }

    public static enum Direction {
        SEND,
        RECEIVE;

    }
}

