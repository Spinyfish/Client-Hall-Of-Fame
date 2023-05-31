package de.lirium.impl.command.impl;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.event.EventListener;
import de.lirium.impl.command.CommandFeature;
import de.lirium.impl.events.PacketEvent;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketTabComplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@CommandFeature.Info(name = "plugins", alias = "pl")
public class PluginsFeature extends CommandFeature {

    private boolean requested;
    private final static List<String> plugins = new ArrayList<>();

    public PluginsFeature() {
        init();
    }

    @Override
    public boolean execute(String[] args) {
        sendPacketUnlogged(new CPacketTabComplete("/", null, false));
        requested = true;
        plugins.clear();
        return true;
    }

    @EventHandler
    private final Listener<PacketEvent> packetEventListener = e -> {
        if(e.packet instanceof SPacketJoinGame) {
            requested = false;
        }
        if(e.packet instanceof SPacketTabComplete) {
            final SPacketTabComplete tabComplete = (SPacketTabComplete) e.packet;
            if(requested) {
                final StringJoiner joiner = new StringJoiner("§7, ");
                Arrays.stream(tabComplete.getMatches()).forEach(s -> {
                    if(s.contains(":")) {
                        final String[] split = s.replace("/", "").split(":");
                        if(!plugins.contains(split[0])) {
                            plugins.add(split[0]);
                            joiner.add("§a" + split[0]);
                        }
                    }
                });
                requested = false;
                if(plugins.isEmpty())
                    sendMessage("§e0 §cplugins found!");
                else
                    sendMessage("§fPlugins (" + plugins.size() + "): " + joiner);
            }
        }
    };
}
