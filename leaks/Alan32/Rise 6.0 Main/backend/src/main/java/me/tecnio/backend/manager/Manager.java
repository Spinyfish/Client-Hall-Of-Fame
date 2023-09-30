package me.tecnio.backend.manager;

import me.tecnio.backend.network.Client;
import packet.Packet;

public abstract class Manager {
    public abstract void packet(final Packet<?> packet, final Client client);
}
