package com.alan.clients.security.impl;

import com.alan.clients.security.SecurityFeature;
import com.alan.clients.util.packet.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S48PacketResourcePackSend;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class TeleportCheck extends SecurityFeature {

    public TeleportCheck() {
        super("Large Teleport", "Detects ridiculous teleports");
    }

    @Override
    public boolean handle(final Packet<?> packet) {
        if (packet instanceof S08PacketPlayerPosLook) {
            final S08PacketPlayerPosLook wrapper = ((S08PacketPlayerPosLook) packet);

            return Math.abs(wrapper.x) > 1E+9 || Math.abs(wrapper.y) > 1E+9 || Math.abs(wrapper.z) > 1E+9;
        }

        return false;
    }
}
