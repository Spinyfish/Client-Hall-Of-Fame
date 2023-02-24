package me.kansio.client.modules.impl.movement.speed;

import me.kansio.client.Client;
import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.ModuleManager;
import me.kansio.client.modules.impl.movement.Speed;
import me.kansio.client.utils.Util;

public abstract class SpeedMode extends Util {

    private final String name;

    public SpeedMode(String name) {
        this.name = name;
    }

    public void onUpdate(UpdateEvent event) {}
    public void onMove(MoveEvent event) {}
    public void onPacket(PacketEvent event) {}
    public void onEnable() {}
    public void onDisable() {}

    public String getName() {
        return name;
    }

    public Speed getSpeed() {
        return (Speed) Client.getInstance().getModuleManager().getModuleByName("Speed");
    }

}
