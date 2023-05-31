package de.lirium.impl.events;

import best.azura.eventbus.events.CancellableEvent;
import lombok.AllArgsConstructor;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

@AllArgsConstructor
public class PacketEvent extends CancellableEvent {
    public Packet<?> packet;
    public State state;

    public enum State {
        SEND, RECEIVE;
    }
}