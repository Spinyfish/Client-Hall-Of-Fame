package com.alan.clients.newevent.impl.packet;

import com.alan.clients.newevent.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@Setter
@AllArgsConstructor
public final class PacketReceiveEvent extends CancellableEvent {

    private Packet<?> packet;
}
