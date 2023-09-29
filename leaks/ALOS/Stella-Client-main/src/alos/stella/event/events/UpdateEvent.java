package alos.stella.event.events;

import alos.stella.event.CancellableEvent;
import alos.stella.event.Event;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.network.Packet;
import org.jetbrains.annotations.NotNull;

public final class UpdateEvent extends CancellableEvent {
    public UpdateEvent() {
        packet = null;
    }

    @NotNull
    private final Packet packet;

    @NotNull
    public final Packet getPacket() {
        return this.packet;
    }

    public UpdateEvent(@NotNull Packet packet) {
        super();
        Intrinsics.checkNotNullParameter(packet, "packet");
        this.packet = packet;
    }
}