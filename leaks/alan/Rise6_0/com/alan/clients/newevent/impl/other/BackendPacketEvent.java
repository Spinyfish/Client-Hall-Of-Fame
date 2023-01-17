package com.alan.clients.newevent.impl.other;

import com.alan.clients.newevent.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import packet.type.ServerPacket;

@Getter
@RequiredArgsConstructor
public final class BackendPacketEvent implements Event {

    private final ServerPacket packet;
}
