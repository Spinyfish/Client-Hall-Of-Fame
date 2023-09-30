package dev.africa.pandaware.impl.module.misc.disabler.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import dev.africa.pandaware.utils.client.Printer;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class CancelDmgPacketDisabler extends ModuleMode<DisablerModule> {
    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S06PacketUpdateHealth) {
            event.cancel();
            Printer.chat("Alan (dev of rise): \"What if I delay transactions to pretend I didnt receive the damage packet\"");
        }
    };

    public CancelDmgPacketDisabler(String name, DisablerModule parent) {
        super(name, parent);
    }
}
