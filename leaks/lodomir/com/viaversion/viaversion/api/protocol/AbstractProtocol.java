/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.protocol;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.exception.CancelException;
import com.viaversion.viaversion.exception.InformativeException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractProtocol<C1 extends ClientboundPacketType, C2 extends ClientboundPacketType, S1 extends ServerboundPacketType, S2 extends ServerboundPacketType>
implements Protocol<C1, C2, S1, S2> {
    private final Map<Packet, ProtocolPacket> serverbound = new HashMap<Packet, ProtocolPacket>();
    private final Map<Packet, ProtocolPacket> clientbound = new HashMap<Packet, ProtocolPacket>();
    private final Map<Class<?>, Object> storedObjects = new HashMap();
    protected final Class<C1> oldClientboundPacketEnum;
    protected final Class<C2> newClientboundPacketEnum;
    protected final Class<S1> oldServerboundPacketEnum;
    protected final Class<S2> newServerboundPacketEnum;
    private boolean initialized;

    protected AbstractProtocol() {
        this(null, null, null, null);
    }

    protected AbstractProtocol(@Nullable Class<C1> oldClientboundPacketEnum, @Nullable Class<C2> clientboundPacketEnum, @Nullable Class<S1> oldServerboundPacketEnum, @Nullable Class<S2> serverboundPacketEnum) {
        this.oldClientboundPacketEnum = oldClientboundPacketEnum;
        this.newClientboundPacketEnum = clientboundPacketEnum;
        this.oldServerboundPacketEnum = oldServerboundPacketEnum;
        this.newServerboundPacketEnum = serverboundPacketEnum;
    }

    @Override
    public final void initialize() {
        Preconditions.checkArgument((!this.initialized ? 1 : 0) != 0);
        this.initialized = true;
        this.registerPackets();
        if (this.oldClientboundPacketEnum != null && this.newClientboundPacketEnum != null && this.oldClientboundPacketEnum != this.newClientboundPacketEnum) {
            this.registerClientboundChannelIdChanges();
        }
        if (this.oldServerboundPacketEnum != null && this.newServerboundPacketEnum != null && this.oldServerboundPacketEnum != this.newServerboundPacketEnum) {
            this.registerServerboundChannelIdChanges();
        }
    }

    protected void registerClientboundChannelIdChanges() {
        ClientboundPacketType[] newConstants = (ClientboundPacketType[])this.newClientboundPacketEnum.getEnumConstants();
        HashMap<String, ClientboundPacketType> newClientboundPackets = new HashMap<String, ClientboundPacketType>(newConstants.length);
        for (ClientboundPacketType newConstant : newConstants) {
            newClientboundPackets.put(newConstant.getName(), newConstant);
        }
        for (ClientboundPacketType packet : (ClientboundPacketType[])this.oldClientboundPacketEnum.getEnumConstants()) {
            ClientboundPacketType mappedPacket = (ClientboundPacketType)newClientboundPackets.get(packet.getName());
            if (mappedPacket == null) {
                Preconditions.checkArgument((boolean)this.hasRegisteredClientbound(packet), (Object)("Packet " + packet + " in " + this.getClass().getSimpleName() + " has no mapping - it needs to be manually cancelled or remapped!"));
                continue;
            }
            if (this.hasRegisteredClientbound(packet)) continue;
            this.registerClientbound(packet, mappedPacket);
        }
    }

    protected void registerServerboundChannelIdChanges() {
        ServerboundPacketType[] oldConstants = (ServerboundPacketType[])this.oldServerboundPacketEnum.getEnumConstants();
        HashMap<String, ServerboundPacketType> oldServerboundConstants = new HashMap<String, ServerboundPacketType>(oldConstants.length);
        for (ServerboundPacketType oldConstant : oldConstants) {
            oldServerboundConstants.put(oldConstant.getName(), oldConstant);
        }
        for (ServerboundPacketType packet : (ServerboundPacketType[])this.newServerboundPacketEnum.getEnumConstants()) {
            ServerboundPacketType mappedPacket = (ServerboundPacketType)oldServerboundConstants.get(packet.getName());
            if (mappedPacket == null) {
                Preconditions.checkArgument((boolean)this.hasRegisteredServerbound(packet), (Object)("Packet " + packet + " in " + this.getClass().getSimpleName() + " has no mapping - it needs to be manually cancelled or remapped!"));
                continue;
            }
            if (this.hasRegisteredServerbound(packet)) continue;
            this.registerServerbound(packet, mappedPacket);
        }
    }

    protected void registerPackets() {
    }

    @Override
    public final void loadMappingData() {
        this.getMappingData().load();
        this.onMappingDataLoaded();
    }

    protected void onMappingDataLoaded() {
    }

    protected void addEntityTracker(UserConnection connection, EntityTracker tracker) {
        connection.addEntityTracker(this.getClass(), tracker);
    }

    @Override
    public void registerServerbound(State state, int oldPacketID, int newPacketID, PacketRemapper packetRemapper, boolean override) {
        ProtocolPacket protocolPacket = new ProtocolPacket(state, oldPacketID, newPacketID, packetRemapper);
        Packet packet = new Packet(state, newPacketID);
        if (!override && this.serverbound.containsKey(packet)) {
            Via.getPlatform().getLogger().log(Level.WARNING, packet + " already registered! If this override is intentional, set override to true. Stacktrace: ", new Exception());
        }
        this.serverbound.put(packet, protocolPacket);
    }

    @Override
    public void cancelServerbound(State state, int oldPacketID, int newPacketID) {
        this.registerServerbound(state, oldPacketID, newPacketID, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(PacketWrapper::cancel);
            }
        });
    }

    @Override
    public void cancelServerbound(State state, int newPacketID) {
        this.cancelServerbound(state, -1, newPacketID);
    }

    @Override
    public void cancelClientbound(State state, int oldPacketID, int newPacketID) {
        this.registerClientbound(state, oldPacketID, newPacketID, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(PacketWrapper::cancel);
            }
        });
    }

    @Override
    public void cancelClientbound(State state, int oldPacketID) {
        this.cancelClientbound(state, oldPacketID, -1);
    }

    @Override
    public void registerClientbound(State state, int oldPacketID, int newPacketID, PacketRemapper packetRemapper, boolean override) {
        ProtocolPacket protocolPacket = new ProtocolPacket(state, oldPacketID, newPacketID, packetRemapper);
        Packet packet = new Packet(state, oldPacketID);
        if (!override && this.clientbound.containsKey(packet)) {
            Via.getPlatform().getLogger().log(Level.WARNING, packet + " already registered! If override is intentional, set override to true. Stacktrace: ", new Exception());
        }
        this.clientbound.put(packet, protocolPacket);
    }

    @Override
    public void registerClientbound(C1 packetType, @Nullable PacketRemapper packetRemapper) {
        this.checkPacketType((PacketType)packetType, this.oldClientboundPacketEnum == null || packetType.getClass() == this.oldClientboundPacketEnum);
        Object mappedPacket = this.oldClientboundPacketEnum == this.newClientboundPacketEnum ? packetType : (ClientboundPacketType)Arrays.stream((ClientboundPacketType[])this.newClientboundPacketEnum.getEnumConstants()).filter(en -> en.getName().equals(packetType.getName())).findAny().orElse(null);
        Preconditions.checkNotNull(mappedPacket, (Object)("Packet type " + packetType + " in " + packetType.getClass().getSimpleName() + " could not be automatically mapped!"));
        this.registerClientbound(packetType, mappedPacket, packetRemapper);
    }

    @Override
    public void registerClientbound(C1 packetType, @Nullable C2 mappedPacketType, @Nullable PacketRemapper packetRemapper, boolean override) {
        this.register(this.clientbound, (PacketType)packetType, (PacketType)mappedPacketType, (Class<? extends PacketType>)this.oldClientboundPacketEnum, (Class<? extends PacketType>)this.newClientboundPacketEnum, packetRemapper, override);
    }

    @Override
    public void cancelClientbound(C1 packetType) {
        this.registerClientbound(packetType, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(PacketWrapper::cancel);
            }
        });
    }

    @Override
    public void registerServerbound(S2 packetType, @Nullable PacketRemapper packetRemapper) {
        this.checkPacketType((PacketType)packetType, this.newServerboundPacketEnum == null || packetType.getClass() == this.newServerboundPacketEnum);
        Object mappedPacket = this.oldServerboundPacketEnum == this.newServerboundPacketEnum ? packetType : (ServerboundPacketType)Arrays.stream((ServerboundPacketType[])this.oldServerboundPacketEnum.getEnumConstants()).filter(en -> en.getName().equals(packetType.getName())).findAny().orElse(null);
        Preconditions.checkNotNull(mappedPacket, (Object)("Packet type " + packetType + " in " + packetType.getClass().getSimpleName() + " could not be automatically mapped!"));
        this.registerServerbound(packetType, mappedPacket, packetRemapper);
    }

    @Override
    public void registerServerbound(S2 packetType, @Nullable S1 mappedPacketType, @Nullable PacketRemapper packetRemapper, boolean override) {
        this.register(this.serverbound, (PacketType)packetType, (PacketType)mappedPacketType, (Class<? extends PacketType>)this.newServerboundPacketEnum, (Class<? extends PacketType>)this.oldServerboundPacketEnum, packetRemapper, override);
    }

    @Override
    public void cancelServerbound(S2 packetType) {
        this.registerServerbound(packetType, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(PacketWrapper::cancel);
            }
        });
    }

    private void register(Map<Packet, ProtocolPacket> packetMap, PacketType packetType, @Nullable PacketType mappedPacketType, Class<? extends PacketType> unmappedPacketEnum, Class<? extends PacketType> mappedPacketEnum, @Nullable PacketRemapper remapper, boolean override) {
        this.checkPacketType(packetType, unmappedPacketEnum == null || packetType.getClass() == unmappedPacketEnum);
        this.checkPacketType(mappedPacketType, mappedPacketType == null || mappedPacketEnum == null || mappedPacketType.getClass() == mappedPacketEnum);
        Preconditions.checkArgument((mappedPacketType == null || packetType.state() == mappedPacketType.state() ? 1 : 0) != 0, (Object)"Packet type state does not match mapped packet type state");
        ProtocolPacket protocolPacket = new ProtocolPacket(packetType.state(), packetType, mappedPacketType, remapper);
        Packet packet = new Packet(packetType.state(), packetType.getId());
        if (!override && packetMap.containsKey(packet)) {
            Via.getPlatform().getLogger().log(Level.WARNING, packet + " already registered! If override is intentional, set override to true. Stacktrace: ", new Exception());
        }
        packetMap.put(packet, protocolPacket);
    }

    @Override
    public boolean hasRegisteredClientbound(C1 packetType) {
        return this.hasRegisteredClientbound(packetType.state(), packetType.getId());
    }

    @Override
    public boolean hasRegisteredServerbound(S2 packetType) {
        return this.hasRegisteredServerbound(packetType.state(), packetType.getId());
    }

    @Override
    public boolean hasRegisteredClientbound(State state, int unmappedPacketId) {
        Packet packet = new Packet(state, unmappedPacketId);
        return this.clientbound.containsKey(packet);
    }

    @Override
    public boolean hasRegisteredServerbound(State state, int unmappedPacketId) {
        Packet packet = new Packet(state, unmappedPacketId);
        return this.serverbound.containsKey(packet);
    }

    @Override
    public void transform(Direction direction, State state, PacketWrapper packetWrapper) throws Exception {
        Packet statePacket;
        Map<Packet, ProtocolPacket> packetMap = direction == Direction.CLIENTBOUND ? this.clientbound : this.serverbound;
        ProtocolPacket protocolPacket = packetMap.get(statePacket = new Packet(state, packetWrapper.getId()));
        if (protocolPacket == null) {
            return;
        }
        int unmappedId = packetWrapper.getId();
        if (protocolPacket.isMappedOverTypes()) {
            packetWrapper.setPacketType(protocolPacket.getMappedPacketType());
        } else {
            int mappedId;
            int n = mappedId = direction == Direction.CLIENTBOUND ? protocolPacket.getNewId() : protocolPacket.getOldId();
            if (unmappedId != mappedId) {
                packetWrapper.setId(mappedId);
            }
        }
        PacketRemapper remapper = protocolPacket.getRemapper();
        if (remapper != null) {
            try {
                remapper.remap(packetWrapper);
            }
            catch (InformativeException e) {
                this.throwRemapError(direction, state, unmappedId, packetWrapper.getId(), e);
                return;
            }
            if (packetWrapper.isCancelled()) {
                throw CancelException.generate();
            }
        }
    }

    private void throwRemapError(Direction direction, State state, int oldId, int newId, InformativeException e) throws InformativeException {
        Class<C1> packetTypeClass;
        if (state == State.HANDSHAKE) {
            throw e;
        }
        Class<Object> clazz = state == State.PLAY ? (direction == Direction.CLIENTBOUND ? this.oldClientboundPacketEnum : this.newServerboundPacketEnum) : (packetTypeClass = null);
        if (packetTypeClass != null) {
            PacketType[] enumConstants = (PacketType[])packetTypeClass.getEnumConstants();
            PacketType packetType = oldId < enumConstants.length && oldId >= 0 ? enumConstants[oldId] : null;
            Via.getPlatform().getLogger().warning("ERROR IN " + this.getClass().getSimpleName() + " IN REMAP OF " + packetType + " (" + this.toNiceHex(oldId) + ")");
        } else {
            Via.getPlatform().getLogger().warning("ERROR IN " + this.getClass().getSimpleName() + " IN REMAP OF " + this.toNiceHex(oldId) + "->" + this.toNiceHex(newId));
        }
        throw e;
    }

    private String toNiceHex(int id) {
        String hex = Integer.toHexString(id).toUpperCase();
        return (hex.length() == 1 ? "0x0" : "0x") + hex;
    }

    private void checkPacketType(PacketType packetType, boolean isValid) {
        if (!isValid) {
            throw new IllegalArgumentException("Packet type " + packetType + " in " + packetType.getClass().getSimpleName() + " is taken from the wrong enum");
        }
    }

    @Override
    public <T> @Nullable T get(Class<T> objectClass) {
        return (T)this.storedObjects.get(objectClass);
    }

    @Override
    public void put(Object object) {
        this.storedObjects.put(object.getClass(), object);
    }

    @Override
    public boolean hasMappingDataToLoad() {
        return this.getMappingData() != null;
    }

    public String toString() {
        return "Protocol:" + this.getClass().getSimpleName();
    }

    public static final class ProtocolPacket {
        private final State state;
        private final int oldId;
        private final int newId;
        private final PacketType unmappedPacketType;
        private final PacketType mappedPacketType;
        private final PacketRemapper remapper;

        @Deprecated
        public ProtocolPacket(State state, int oldId, int newId, @Nullable PacketRemapper remapper) {
            this.state = state;
            this.oldId = oldId;
            this.newId = newId;
            this.remapper = remapper;
            this.unmappedPacketType = null;
            this.mappedPacketType = null;
        }

        public ProtocolPacket(State state, PacketType unmappedPacketType, @Nullable PacketType mappedPacketType, @Nullable PacketRemapper remapper) {
            this.state = state;
            this.unmappedPacketType = unmappedPacketType;
            if (unmappedPacketType.direction() == Direction.CLIENTBOUND) {
                this.oldId = unmappedPacketType.getId();
                this.newId = mappedPacketType != null ? mappedPacketType.getId() : -1;
            } else {
                this.oldId = mappedPacketType != null ? mappedPacketType.getId() : -1;
                this.newId = unmappedPacketType.getId();
            }
            this.mappedPacketType = mappedPacketType;
            this.remapper = remapper;
        }

        public State getState() {
            return this.state;
        }

        @Deprecated
        public int getOldId() {
            return this.oldId;
        }

        @Deprecated
        public int getNewId() {
            return this.newId;
        }

        public @Nullable PacketType getUnmappedPacketType() {
            return this.unmappedPacketType;
        }

        public @Nullable PacketType getMappedPacketType() {
            return this.mappedPacketType;
        }

        public boolean isMappedOverTypes() {
            return this.unmappedPacketType != null;
        }

        public @Nullable PacketRemapper getRemapper() {
            return this.remapper;
        }
    }

    public static final class Packet {
        private final State state;
        private final int packetId;

        public Packet(State state, int packetId) {
            this.state = state;
            this.packetId = packetId;
        }

        public State getState() {
            return this.state;
        }

        public int getPacketId() {
            return this.packetId;
        }

        public String toString() {
            return "Packet{state=" + (Object)((Object)this.state) + ", packetId=" + this.packetId + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Packet that = (Packet)o;
            return this.packetId == that.packetId && this.state == that.state;
        }

        public int hashCode() {
            int result = this.state != null ? this.state.hashCode() : 0;
            result = 31 * result + this.packetId;
            return result;
        }
    }
}

