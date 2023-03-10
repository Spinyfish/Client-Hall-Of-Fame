/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.protocol;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Protocol<C1 extends ClientboundPacketType, C2 extends ClientboundPacketType, S1 extends ServerboundPacketType, S2 extends ServerboundPacketType> {
    default public void registerServerbound(State state, int oldPacketID, int newPacketID) {
        this.registerServerbound(state, oldPacketID, newPacketID, null);
    }

    default public void registerServerbound(State state, int oldPacketID, int newPacketID, PacketRemapper packetRemapper) {
        this.registerServerbound(state, oldPacketID, newPacketID, packetRemapper, false);
    }

    public void registerServerbound(State var1, int var2, int var3, PacketRemapper var4, boolean var5);

    @Deprecated
    public void cancelServerbound(State var1, int var2, int var3);

    public void cancelServerbound(State var1, int var2);

    default public void registerClientbound(State state, int oldPacketID, int newPacketID) {
        this.registerClientbound(state, oldPacketID, newPacketID, null);
    }

    default public void registerClientbound(State state, int oldPacketID, int newPacketID, PacketRemapper packetRemapper) {
        this.registerClientbound(state, oldPacketID, newPacketID, packetRemapper, false);
    }

    @Deprecated
    public void cancelClientbound(State var1, int var2, int var3);

    public void cancelClientbound(State var1, int var2);

    public void registerClientbound(State var1, int var2, int var3, PacketRemapper var4, boolean var5);

    public void registerClientbound(C1 var1, @Nullable PacketRemapper var2);

    default public void registerClientbound(C1 packetType, @Nullable C2 mappedPacketType) {
        this.registerClientbound(packetType, mappedPacketType, null);
    }

    default public void registerClientbound(C1 packetType, @Nullable C2 mappedPacketType, @Nullable PacketRemapper packetRemapper) {
        this.registerClientbound(packetType, mappedPacketType, packetRemapper, false);
    }

    public void registerClientbound(C1 var1, @Nullable C2 var2, @Nullable PacketRemapper var3, boolean var4);

    public void cancelClientbound(C1 var1);

    default public void registerServerbound(S2 packetType, @Nullable S1 mappedPacketType) {
        this.registerServerbound(packetType, mappedPacketType, null);
    }

    public void registerServerbound(S2 var1, @Nullable PacketRemapper var2);

    default public void registerServerbound(S2 packetType, @Nullable S1 mappedPacketType, @Nullable PacketRemapper packetRemapper) {
        this.registerServerbound(packetType, mappedPacketType, packetRemapper, false);
    }

    public void registerServerbound(S2 var1, @Nullable S1 var2, @Nullable PacketRemapper var3, boolean var4);

    public void cancelServerbound(S2 var1);

    public boolean hasRegisteredClientbound(C1 var1);

    public boolean hasRegisteredServerbound(S2 var1);

    public boolean hasRegisteredClientbound(State var1, int var2);

    public boolean hasRegisteredServerbound(State var1, int var2);

    public void transform(Direction var1, State var2, PacketWrapper var3) throws Exception;

    public <T> @Nullable T get(Class<T> var1);

    public void put(Object var1);

    public void initialize();

    public boolean hasMappingDataToLoad();

    public void loadMappingData();

    default public void register(ViaProviders providers) {
    }

    default public void init(UserConnection userConnection) {
    }

    default public @Nullable MappingData getMappingData() {
        return null;
    }

    default public @Nullable EntityRewriter getEntityRewriter() {
        return null;
    }

    default public @Nullable ItemRewriter getItemRewriter() {
        return null;
    }

    default public boolean isBaseProtocol() {
        return false;
    }
}

