/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.connection;

import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketTracker;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface UserConnection {
    public <T extends StorableObject> @Nullable T get(Class<T> var1);

    public boolean has(Class<? extends StorableObject> var1);

    public void put(StorableObject var1);

    public Collection<EntityTracker> getEntityTrackers();

    public <T extends EntityTracker> @Nullable T getEntityTracker(Class<? extends Protocol> var1);

    public void addEntityTracker(Class<? extends Protocol> var1, EntityTracker var2);

    public void clearStoredObjects();

    public void sendRawPacket(ByteBuf var1);

    public void scheduleSendRawPacket(ByteBuf var1);

    public ChannelFuture sendRawPacketFuture(ByteBuf var1);

    public PacketTracker getPacketTracker();

    public void disconnect(String var1);

    public void sendRawPacketToServer(ByteBuf var1);

    public void scheduleSendRawPacketToServer(ByteBuf var1);

    public boolean checkServerboundPacket();

    public boolean checkClientboundPacket();

    default public boolean checkIncomingPacket() {
        return this.isClientSide() ? this.checkClientboundPacket() : this.checkServerboundPacket();
    }

    default public boolean checkOutgoingPacket() {
        return this.isClientSide() ? this.checkServerboundPacket() : this.checkClientboundPacket();
    }

    public boolean shouldTransformPacket();

    public void transformClientbound(ByteBuf var1, Function<Throwable, Exception> var2) throws Exception;

    public void transformServerbound(ByteBuf var1, Function<Throwable, Exception> var2) throws Exception;

    default public void transformOutgoing(ByteBuf buf, Function<Throwable, Exception> cancelSupplier) throws Exception {
        if (this.isClientSide()) {
            this.transformServerbound(buf, cancelSupplier);
        } else {
            this.transformClientbound(buf, cancelSupplier);
        }
    }

    default public void transformIncoming(ByteBuf buf, Function<Throwable, Exception> cancelSupplier) throws Exception {
        if (this.isClientSide()) {
            this.transformClientbound(buf, cancelSupplier);
        } else {
            this.transformServerbound(buf, cancelSupplier);
        }
    }

    public long getId();

    public @Nullable Channel getChannel();

    public ProtocolInfo getProtocolInfo();

    public Map<Class<?>, StorableObject> getStoredObjects();

    public boolean isActive();

    public void setActive(boolean var1);

    public boolean isPendingDisconnect();

    public void setPendingDisconnect(boolean var1);

    public boolean isClientSide();

    public boolean shouldApplyBlockProtocol();

    public boolean isPacketLimiterEnabled();

    public void setPacketLimiterEnabled(boolean var1);

    public UUID generatePassthroughToken();
}

