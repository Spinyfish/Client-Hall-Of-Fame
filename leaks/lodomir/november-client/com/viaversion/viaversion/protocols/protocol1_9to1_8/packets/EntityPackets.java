/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.viaversion.viaversion.protocols.protocol1_9to1_8.packets;

import com.google.common.collect.ImmutableList;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.packets.SpawnPackets;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import com.viaversion.viaversion.util.Pair;
import com.viaversion.viaversion.util.Triple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityPackets {
    public static final ValueTransformer<Byte, Short> toNewShort = new ValueTransformer<Byte, Short>((Type)Type.SHORT){

        @Override
        public Short transform(PacketWrapper wrapper, Byte inputValue) {
            return (short)(inputValue * 128);
        }
    };

    public static void register(final Protocol1_9To1_8 protocol) {
        protocol.registerClientbound(ClientboundPackets1_8.ATTACH_ENTITY, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.BOOLEAN, new ValueTransformer<Boolean, Void>((Type)Type.NOTHING){

                    @Override
                    public Void transform(PacketWrapper wrapper, Boolean inputValue) throws Exception {
                        EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        if (!inputValue.booleanValue()) {
                            int passenger = wrapper.get(Type.INT, 0);
                            int vehicle = wrapper.get(Type.INT, 1);
                            wrapper.cancel();
                            PacketWrapper passengerPacket = wrapper.create(ClientboundPackets1_9.SET_PASSENGERS);
                            if (vehicle == -1) {
                                if (!tracker.getVehicleMap().containsKey(passenger)) {
                                    return null;
                                }
                                passengerPacket.write(Type.VAR_INT, tracker.getVehicleMap().remove(passenger));
                                passengerPacket.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[0]);
                            } else {
                                passengerPacket.write(Type.VAR_INT, vehicle);
                                passengerPacket.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{passenger});
                                tracker.getVehicleMap().put(passenger, vehicle);
                            }
                            passengerPacket.send(Protocol1_9To1_8.class);
                        }
                        return null;
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.ENTITY_TELEPORT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.INT, SpawnPackets.toNewDouble);
                this.map(Type.INT, SpawnPackets.toNewDouble);
                this.map(Type.INT, SpawnPackets.toNewDouble);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.BOOLEAN);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        EntityTracker1_9 tracker;
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        if (Via.getConfig().isHologramPatch() && (tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class)).getKnownHolograms().contains(entityID)) {
                            Double newValue = wrapper.get(Type.DOUBLE, 1);
                            newValue = newValue + Via.getConfig().getHologramYOffset();
                            wrapper.set(Type.DOUBLE, 1, newValue);
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.ENTITY_POSITION_AND_ROTATION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.BYTE, toNewShort);
                this.map(Type.BYTE, toNewShort);
                this.map(Type.BYTE, toNewShort);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.BOOLEAN);
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.ENTITY_POSITION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.BYTE, toNewShort);
                this.map(Type.BYTE, toNewShort);
                this.map(Type.BYTE, toNewShort);
                this.map(Type.BOOLEAN);
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.ENTITY_EQUIPMENT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.SHORT, new ValueTransformer<Short, Integer>((Type)Type.VAR_INT){

                    @Override
                    public Integer transform(PacketWrapper wrapper, Short slot) throws Exception {
                        int receiverId;
                        int entityId = wrapper.get(Type.VAR_INT, 0);
                        if (entityId == (receiverId = wrapper.user().getEntityTracker(Protocol1_9To1_8.class).clientEntityId())) {
                            return slot.intValue() + 2;
                        }
                        return slot > 0 ? slot.intValue() + 1 : slot.intValue();
                    }
                });
                this.map(Type.ITEM);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Item stack = wrapper.get(Type.ITEM, 0);
                        ItemRewriter.toClient(stack);
                    }
                });
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        Item stack = wrapper.get(Type.ITEM, 0);
                        if (stack != null && Protocol1_9To1_8.isSword(stack.identifier())) {
                            entityTracker.getValidBlocking().add(entityID);
                            return;
                        }
                        entityTracker.getValidBlocking().remove(entityID);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.ENTITY_METADATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Types1_8.METADATA_LIST, Types1_9.METADATA_LIST);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        List<Metadata> metadataList = wrapper.get(Types1_9.METADATA_LIST, 0);
                        int entityId = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        if (tracker.hasEntity(entityId)) {
                            protocol.get(MetadataRewriter1_9To1_8.class).handleMetadata(entityId, metadataList, wrapper.user());
                        } else {
                            tracker.addMetadataToBuffer(entityId, metadataList);
                            wrapper.cancel();
                        }
                    }
                });
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        List<Metadata> metadataList = wrapper.get(Types1_9.METADATA_LIST, 0);
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        tracker.handleMetadata(entityID, metadataList);
                    }
                });
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        List<Metadata> metadataList = wrapper.get(Types1_9.METADATA_LIST, 0);
                        if (metadataList.isEmpty()) {
                            wrapper.cancel();
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.ENTITY_EFFECT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        boolean showParticles = wrapper.read(Type.BOOLEAN);
                        boolean newEffect = Via.getConfig().isNewEffectIndicator();
                        wrapper.write(Type.BYTE, (byte)(showParticles ? (newEffect ? 2 : 1) : 0));
                    }
                });
            }
        });
        protocol.cancelClientbound(ClientboundPackets1_8.UPDATE_ENTITY_NBT);
        protocol.registerClientbound(ClientboundPackets1_8.COMBAT_EVENT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (wrapper.get(Type.VAR_INT, 0) == 2) {
                            wrapper.passthrough(Type.VAR_INT);
                            wrapper.passthrough(Type.INT);
                            Protocol1_9To1_8.FIX_JSON.write(wrapper, wrapper.read(Type.STRING));
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.ENTITY_PROPERTIES, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (!Via.getConfig().isMinimizeCooldown()) {
                            return;
                        }
                        EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        if (wrapper.get(Type.VAR_INT, 0).intValue() != tracker.getProvidedEntityId()) {
                            return;
                        }
                        int propertiesToRead = wrapper.read(Type.INT);
                        HashMap properties = new HashMap(propertiesToRead);
                        for (int i = 0; i < propertiesToRead; ++i) {
                            String key = wrapper.read(Type.STRING);
                            Double value = wrapper.read(Type.DOUBLE);
                            int modifiersToRead = wrapper.read(Type.VAR_INT);
                            ArrayList<Triple<UUID, Double, Byte>> modifiers = new ArrayList<Triple<UUID, Double, Byte>>(modifiersToRead);
                            for (int j = 0; j < modifiersToRead; ++j) {
                                modifiers.add(new Triple<UUID, Double, Byte>(wrapper.read(Type.UUID), wrapper.read(Type.DOUBLE), wrapper.read(Type.BYTE)));
                            }
                            properties.put(key, new Pair(value, modifiers));
                        }
                        properties.put("generic.attackSpeed", new Pair<Double, ImmutableList>(15.9, ImmutableList.of(new Triple<UUID, Double, Byte>(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), 0.0, (byte)0), new Triple<UUID, Double, Byte>(UUID.fromString("AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3"), 0.0, (byte)2), new Triple<UUID, Double, Byte>(UUID.fromString("55FCED67-E92A-486E-9800-B47F202C4386"), 0.0, (byte)2))));
                        wrapper.write(Type.INT, properties.size());
                        for (Map.Entry entry : properties.entrySet()) {
                            wrapper.write(Type.STRING, (String)entry.getKey());
                            wrapper.write(Type.DOUBLE, (Double)((Pair)entry.getValue()).key());
                            wrapper.write(Type.VAR_INT, ((List)((Pair)entry.getValue()).value()).size());
                            for (Triple modifier : (List)((Pair)entry.getValue()).value()) {
                                wrapper.write(Type.UUID, (UUID)modifier.first());
                                wrapper.write(Type.DOUBLE, (Double)modifier.second());
                                wrapper.write(Type.BYTE, (Byte)modifier.third());
                            }
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.ENTITY_ANIMATION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UNSIGNED_BYTE);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (wrapper.get(Type.UNSIGNED_BYTE, 0) == 3) {
                            wrapper.cancel();
                        }
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_9.ENTITY_ACTION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.VAR_INT);
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int action = wrapper.get(Type.VAR_INT, 1);
                        if (action == 6 || action == 8) {
                            wrapper.cancel();
                        }
                        if (action == 7) {
                            wrapper.set(Type.VAR_INT, 1, 6);
                        }
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_9.INTERACT_ENTITY, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int hand;
                        int type = wrapper.get(Type.VAR_INT, 1);
                        if (type == 2) {
                            wrapper.passthrough(Type.FLOAT);
                            wrapper.passthrough(Type.FLOAT);
                            wrapper.passthrough(Type.FLOAT);
                        }
                        if ((type == 0 || type == 2) && (hand = wrapper.read(Type.VAR_INT).intValue()) == 1) {
                            wrapper.cancel();
                        }
                    }
                });
            }
        });
    }
}

