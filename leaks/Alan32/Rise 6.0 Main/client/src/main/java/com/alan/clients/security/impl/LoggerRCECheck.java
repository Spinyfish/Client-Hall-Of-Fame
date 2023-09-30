package com.alan.clients.security.impl;

import com.alan.clients.security.SecurityFeature;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.IChatComponent;

import java.util.regex.Pattern;

public final class LoggerRCECheck extends SecurityFeature {

    private static final Pattern PATTERN = Pattern.compile(".*\\$\\{[^}]*\\}.*");

    public LoggerRCECheck() {
        super("Log4J RCE Check", "Someone attempted to utilize the Log4J exploit");
    }

    @Override
    public boolean handle(final Packet<?> packet) {
        if (packet instanceof S29PacketSoundEffect) {
            final S29PacketSoundEffect wrapper = (S29PacketSoundEffect) packet;
            final String name = wrapper.getSoundName();

            return PATTERN.matcher(name).matches();
        }

        if (packet instanceof S02PacketChat) {
            final S02PacketChat wrapper = ((S02PacketChat) packet);
            final IChatComponent component = wrapper.getChatComponent();

            return PATTERN.matcher(component.getUnformattedText()).matches()
                    || PATTERN.matcher(component.getFormattedText()).matches();
        }

        return false;
    }
}
