package de.fanta.events.listeners;

import de.fanta.events.Event;
import net.minecraft.network.Packet;

public class EventPacket extends Event {
    public static Packet packet;
    private boolean cancel;
    private Action eventAction;

    public EventPacket(Action action, Packet packet) {
        this.eventAction = action;
        EventPacket.packet = packet;
    }

    public static Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        EventPacket.packet = packet;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public Action getEventAction() {
        return eventAction;
    }

    public boolean isSend() {
        return getEventAction() == Action.SEND;
    }

    public boolean isReceive() {
        return getEventAction() == Action.RECEIVE;
    }

    public enum Action {
        SEND, RECEIVE
    }
}



