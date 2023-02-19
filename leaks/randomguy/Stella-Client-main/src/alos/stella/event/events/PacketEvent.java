package alos.stella.event.events;

import kotlin.jvm.internal.Intrinsics;
import alos.stella.event.CancellableEvent;
import net.minecraft.network.Packet;
import org.jetbrains.annotations.NotNull;

public final class PacketEvent extends CancellableEvent {
    @NotNull
    private final Packet packet;

    @NotNull
    public final Packet getPacket() {
            return this.packet;
        }

    public PacketEvent(@NotNull Packet packet) {
            super();
            Intrinsics.checkNotNullParameter(packet, "packet");
            this.packet = packet;
    }
}