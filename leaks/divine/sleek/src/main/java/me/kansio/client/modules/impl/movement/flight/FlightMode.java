package me.kansio.client.modules.impl.movement.flight;

import me.kansio.client.Client;
import me.kansio.client.event.impl.*;
import me.kansio.client.modules.impl.movement.Flight;
import me.kansio.client.utils.Util;

public abstract class FlightMode extends Util {

    private final String name;

    public FlightMode(String name) {
        this.name = name;
    }

    public void onUpdate(UpdateEvent event) {}
    public void onMove(MoveEvent event) {}
    public void onPacket(PacketEvent event) {}
    public void onCollide(BlockCollisionEvent event) {}
    public void onEnable() {}
    public void onDisable() {}

    public String getName() {
        return name;
    }

    public Flight getFlight() {
        return (Flight) Client.getInstance().getModuleManager().getModuleByName("Flight");
    }

}
