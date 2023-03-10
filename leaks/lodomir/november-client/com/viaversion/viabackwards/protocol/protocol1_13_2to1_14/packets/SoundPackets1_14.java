/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.Protocol1_13_2To1_14;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage.EntityPositionStorage1_14;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;

public class SoundPackets1_14
extends RewriterBase<Protocol1_13_2To1_14> {
    public SoundPackets1_14(Protocol1_13_2To1_14 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        SoundRewriter soundRewriter = new SoundRewriter((BackwardsProtocol)this.protocol);
        soundRewriter.registerSound(ClientboundPackets1_14.SOUND);
        soundRewriter.registerNamedSound(ClientboundPackets1_14.NAMED_SOUND);
        soundRewriter.registerStopSound(ClientboundPackets1_14.STOP_SOUND);
        ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.ENTITY_SOUND, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    EntityPositionStorage1_14 entityStorage;
                    wrapper.cancel();
                    int soundId = wrapper.read(Type.VAR_INT);
                    int newId = ((Protocol1_13_2To1_14)SoundPackets1_14.this.protocol).getMappingData().getSoundMappings().getNewId(soundId);
                    if (newId == -1) {
                        return;
                    }
                    int category = wrapper.read(Type.VAR_INT);
                    int entityId = wrapper.read(Type.VAR_INT);
                    StoredEntityData storedEntity = wrapper.user().getEntityTracker(((Protocol1_13_2To1_14)SoundPackets1_14.this.protocol).getClass()).entityData(entityId);
                    if (storedEntity == null || (entityStorage = storedEntity.get(EntityPositionStorage1_14.class)) == null) {
                        ViaBackwards.getPlatform().getLogger().warning("Untracked entity with id " + entityId);
                        return;
                    }
                    float volume = wrapper.read(Type.FLOAT).floatValue();
                    float pitch = wrapper.read(Type.FLOAT).floatValue();
                    int x = (int)(entityStorage.getX() * 8.0);
                    int y = (int)(entityStorage.getY() * 8.0);
                    int z = (int)(entityStorage.getZ() * 8.0);
                    PacketWrapper soundPacket = wrapper.create(ClientboundPackets1_13.SOUND);
                    soundPacket.write(Type.VAR_INT, newId);
                    soundPacket.write(Type.VAR_INT, category);
                    soundPacket.write(Type.INT, x);
                    soundPacket.write(Type.INT, y);
                    soundPacket.write(Type.INT, z);
                    soundPacket.write(Type.FLOAT, Float.valueOf(volume));
                    soundPacket.write(Type.FLOAT, Float.valueOf(pitch));
                    soundPacket.send(Protocol1_13_2To1_14.class);
                });
            }
        });
    }
}

