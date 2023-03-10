/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;

public class SoundRewriter
extends com.viaversion.viaversion.rewriter.SoundRewriter {
    private final BackwardsProtocol protocol;

    public SoundRewriter(BackwardsProtocol protocol) {
        super(protocol);
        this.protocol = protocol;
    }

    public void registerNamedSound(ClientboundPacketType packetType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.handler(SoundRewriter.this.getNamedSoundHandler());
            }
        });
    }

    public void registerStopSound(ClientboundPacketType packetType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(SoundRewriter.this.getStopSoundHandler());
            }
        });
    }

    public PacketHandler getNamedSoundHandler() {
        return wrapper -> {
            String soundId = wrapper.get(Type.STRING, 0);
            String mappedId = this.protocol.getMappingData().getMappedNamedSound(soundId);
            if (mappedId == null) {
                return;
            }
            if (!mappedId.isEmpty()) {
                wrapper.set(Type.STRING, 0, mappedId);
            } else {
                wrapper.cancel();
            }
        };
    }

    public PacketHandler getStopSoundHandler() {
        return wrapper -> {
            byte flags = wrapper.passthrough(Type.BYTE);
            if ((flags & 2) == 0) {
                return;
            }
            if ((flags & 1) != 0) {
                wrapper.passthrough(Type.VAR_INT);
            }
            String soundId = wrapper.read(Type.STRING);
            String mappedId = this.protocol.getMappingData().getMappedNamedSound(soundId);
            if (mappedId == null) {
                wrapper.write(Type.STRING, soundId);
                return;
            }
            if (!mappedId.isEmpty()) {
                wrapper.write(Type.STRING, mappedId);
            } else {
                wrapper.cancel();
            }
        };
    }
}

