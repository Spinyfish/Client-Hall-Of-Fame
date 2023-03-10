/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.protocol.packet;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.packet.VersionedPacketTransformer;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.Nullable;

public class VersionedPacketTransformerImpl<C extends ClientboundPacketType, S extends ServerboundPacketType>
implements VersionedPacketTransformer<C, S> {
    private final int inputProtocolVersion;
    private final Class<C> clientboundPacketsClass;
    private final Class<S> serverboundPacketsClass;

    public VersionedPacketTransformerImpl(ProtocolVersion inputVersion, @Nullable Class<C> clientboundPacketsClass, @Nullable Class<S> serverboundPacketsClass) {
        Preconditions.checkNotNull((Object)inputVersion);
        Preconditions.checkArgument((clientboundPacketsClass != null || serverboundPacketsClass != null ? 1 : 0) != 0, (Object)"Either the clientbound or serverbound packets class has to be non-null");
        this.inputProtocolVersion = inputVersion.getVersion();
        this.clientboundPacketsClass = clientboundPacketsClass;
        this.serverboundPacketsClass = serverboundPacketsClass;
    }

    @Override
    public boolean send(PacketWrapper packet) throws Exception {
        this.validatePacket(packet);
        return this.transformAndSendPacket(packet, true);
    }

    @Override
    public boolean send(UserConnection connection, C packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
        return this.createAndSend(connection, (PacketType)packetType, packetWriter);
    }

    @Override
    public boolean send(UserConnection connection, S packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
        return this.createAndSend(connection, (PacketType)packetType, packetWriter);
    }

    @Override
    public boolean scheduleSend(PacketWrapper packet) throws Exception {
        this.validatePacket(packet);
        return this.transformAndSendPacket(packet, false);
    }

    @Override
    public boolean scheduleSend(UserConnection connection, C packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
        return this.scheduleCreateAndSend(connection, (PacketType)packetType, packetWriter);
    }

    @Override
    public boolean scheduleSend(UserConnection connection, S packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
        return this.scheduleCreateAndSend(connection, (PacketType)packetType, packetWriter);
    }

    @Override
    public @Nullable PacketWrapper transform(PacketWrapper packet) throws Exception {
        this.validatePacket(packet);
        this.transformPacket(packet);
        return packet.isCancelled() ? null : packet;
    }

    @Override
    public @Nullable PacketWrapper transform(UserConnection connection, C packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
        return this.createAndTransform(connection, (PacketType)packetType, packetWriter);
    }

    @Override
    public @Nullable PacketWrapper transform(UserConnection connection, S packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
        return this.createAndTransform(connection, (PacketType)packetType, packetWriter);
    }

    private void validatePacket(PacketWrapper packet) {
        Class<Object> expectedPacketClass;
        if (packet.user() == null) {
            throw new IllegalArgumentException("PacketWrapper does not have a targetted UserConnection");
        }
        if (packet.getPacketType() == null) {
            throw new IllegalArgumentException("PacketWrapper does not have a valid packet type");
        }
        Class<Object> clazz = expectedPacketClass = packet.getPacketType().direction() == Direction.CLIENTBOUND ? this.clientboundPacketsClass : this.serverboundPacketsClass;
        if (packet.getPacketType().getClass() != expectedPacketClass) {
            throw new IllegalArgumentException("PacketWrapper packet type is of the wrong packet class");
        }
    }

    private boolean transformAndSendPacket(PacketWrapper packet, boolean currentThread) throws Exception {
        this.transformPacket(packet);
        if (packet.isCancelled()) {
            return false;
        }
        if (currentThread) {
            if (packet.getPacketType().direction() == Direction.CLIENTBOUND) {
                packet.sendRaw();
            } else {
                packet.sendToServerRaw();
            }
        } else if (packet.getPacketType().direction() == Direction.CLIENTBOUND) {
            packet.scheduleSendRaw();
        } else {
            packet.scheduleSendToServerRaw();
        }
        return true;
    }

    private void transformPacket(PacketWrapper packet) throws Exception {
        PacketType packetType = packet.getPacketType();
        UserConnection connection = packet.user();
        boolean clientbound = packetType.direction() == Direction.CLIENTBOUND;
        int serverProtocolVersion = clientbound ? this.inputProtocolVersion : connection.getProtocolInfo().getServerProtocolVersion();
        int clientProtocolVersion = clientbound ? connection.getProtocolInfo().getProtocolVersion() : this.inputProtocolVersion;
        List<ProtocolPathEntry> path = Via.getManager().getProtocolManager().getProtocolPath(clientProtocolVersion, serverProtocolVersion);
        ArrayList<Protocol> protocolList = null;
        if (path != null) {
            protocolList = new ArrayList<Protocol>(path.size());
            for (ProtocolPathEntry entry : path) {
                protocolList.add(entry.protocol());
            }
        } else if (serverProtocolVersion != clientProtocolVersion) {
            throw new RuntimeException("No protocol path between client version " + clientProtocolVersion + " and server version " + serverProtocolVersion);
        }
        if (protocolList != null) {
            packet.resetReader();
            try {
                packet.apply(packetType.direction(), State.PLAY, 0, protocolList, clientbound);
            }
            catch (Exception e) {
                throw new Exception("Exception trying to transform packet between client version " + clientProtocolVersion + " and server version " + serverProtocolVersion + ". Are you sure you used the correct input version and packet write types?", e);
            }
        }
    }

    private boolean createAndSend(UserConnection connection, PacketType packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
        PacketWrapper packet = PacketWrapper.create(packetType, connection);
        packetWriter.accept(packet);
        return this.send(packet);
    }

    private boolean scheduleCreateAndSend(UserConnection connection, PacketType packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
        PacketWrapper packet = PacketWrapper.create(packetType, connection);
        packetWriter.accept(packet);
        return this.scheduleSend(packet);
    }

    private @Nullable PacketWrapper createAndTransform(UserConnection connection, PacketType packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
        PacketWrapper packet = PacketWrapper.create(packetType, connection);
        packetWriter.accept(packet);
        return this.transform(packet);
    }
}

