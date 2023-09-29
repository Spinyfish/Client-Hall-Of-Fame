package com.alan.clients.security.impl;

import com.alan.clients.security.SecurityFeature;
import com.alan.clients.util.packet.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class ResourcePackCheck extends SecurityFeature {

    public ResourcePackCheck() {
        super("Resource Pack Check", "Server attempted to view files on the computer");
    }

    @Override
    public boolean handle(final Packet<?> packet) {
        if (packet instanceof S48PacketResourcePackSend) {
            final S48PacketResourcePackSend wrapper = ((S48PacketResourcePackSend) packet);

            final String url = wrapper.getURL();
            final String hash = wrapper.getHash();

            if (url.toLowerCase().startsWith("level://")) {
                return check(url, hash);
            }
        }

        return false;
    }

    private boolean check(String url, final String hash) {
        try {
            final URI uri = new URI(url);

            final String scheme = uri.getScheme();
            final boolean isLevelProtocol = "level".equals(scheme);

            if (!("http".equals(scheme) || "https".equals(scheme) || isLevelProtocol)) {
                throw new URISyntaxException(url, "Wrong protocol");
            }

            url = URLDecoder.decode(url.substring("level://".length()), StandardCharsets.UTF_8.toString());

            if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip"))) {
                System.out.println("Server tried to access the path: " + url);

                throw new URISyntaxException(url, "Invalid levelstorage resource pack path");
            }

            return false;
        } catch (final Exception e) {
            PacketUtil.sendNoEvent(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            return true;
        }
    }
}
