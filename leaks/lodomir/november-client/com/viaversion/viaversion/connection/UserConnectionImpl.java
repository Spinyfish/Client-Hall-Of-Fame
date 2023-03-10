/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.connection;

import com.google.common.cache.CacheBuilder;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketTracker;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.connection.ProtocolInfoImpl;
import com.viaversion.viaversion.exception.CancelException;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import com.viaversion.viaversion.util.ChatColorUtil;
import com.viaversion.viaversion.util.PipelineUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UserConnectionImpl
implements UserConnection {
    private static final AtomicLong IDS = new AtomicLong();
    private final long id = IDS.incrementAndGet();
    private final Map<Class<?>, StorableObject> storedObjects = new ConcurrentHashMap();
    private final Map<Class<? extends Protocol>, EntityTracker> entityTrackers = new HashMap<Class<? extends Protocol>, EntityTracker>();
    private final PacketTracker packetTracker = new PacketTracker(this);
    private final Set<UUID> passthroughTokens = Collections.newSetFromMap(CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.SECONDS).build().asMap());
    private final ProtocolInfo protocolInfo = new ProtocolInfoImpl(this);
    private final Channel channel;
    private final boolean clientSide;
    private boolean active = true;
    private boolean pendingDisconnect;
    private boolean packetLimiterEnabled = true;

    public UserConnectionImpl(@Nullable Channel channel, boolean clientSide) {
        this.channel = channel;
        this.clientSide = clientSide;
    }

    public UserConnectionImpl(@Nullable Channel channel) {
        this(channel, false);
    }

    @Override
    public <T extends StorableObject> @Nullable T get(Class<T> objectClass) {
        return (T)this.storedObjects.get(objectClass);
    }

    @Override
    public boolean has(Class<? extends StorableObject> objectClass) {
        return this.storedObjects.containsKey(objectClass);
    }

    @Override
    public void put(StorableObject object) {
        this.storedObjects.put(object.getClass(), object);
    }

    @Override
    public Collection<EntityTracker> getEntityTrackers() {
        return this.entityTrackers.values();
    }

    @Override
    public <T extends EntityTracker> @Nullable T getEntityTracker(Class<? extends Protocol> protocolClass) {
        return (T)this.entityTrackers.get(protocolClass);
    }

    @Override
    public void addEntityTracker(Class<? extends Protocol> protocolClass, EntityTracker tracker) {
        this.entityTrackers.put(protocolClass, tracker);
    }

    @Override
    public void clearStoredObjects() {
        this.storedObjects.clear();
        this.entityTrackers.clear();
    }

    @Override
    public void sendRawPacket(ByteBuf packet) {
        this.sendRawPacket(packet, true);
    }

    @Override
    public void scheduleSendRawPacket(ByteBuf packet) {
        this.sendRawPacket(packet, false);
    }

    private void sendRawPacket(ByteBuf packet, boolean currentThread) {
        Runnable act = this.clientSide ? () -> this.getChannel().pipeline().context(Via.getManager().getInjector().getDecoderName()).fireChannelRead(packet) : () -> this.channel.pipeline().context(Via.getManager().getInjector().getEncoderName()).writeAndFlush(packet);
        if (currentThread) {
            act.run();
        } else {
            try {
                this.channel.eventLoop().submit(act);
            }
            catch (Throwable e) {
                packet.release();
                e.printStackTrace();
            }
        }
    }

    @Override
    public ChannelFuture sendRawPacketFuture(ByteBuf packet) {
        if (this.clientSide) {
            this.getChannel().pipeline().context(Via.getManager().getInjector().getDecoderName()).fireChannelRead(packet);
            return this.getChannel().newSucceededFuture();
        }
        return this.channel.pipeline().context(Via.getManager().getInjector().getEncoderName()).writeAndFlush(packet);
    }

    @Override
    public PacketTracker getPacketTracker() {
        return this.packetTracker;
    }

    @Override
    public void disconnect(String reason) {
        if (!this.channel.isOpen() || this.pendingDisconnect) {
            return;
        }
        this.pendingDisconnect = true;
        Via.getPlatform().runSync(() -> {
            if (!Via.getPlatform().disconnect(this, ChatColorUtil.translateAlternateColorCodes(reason))) {
                this.channel.close();
            }
        });
    }

    @Override
    public void sendRawPacketToServer(ByteBuf packet) {
        if (this.clientSide) {
            this.sendRawPacketToServerClientSide(packet, true);
        } else {
            this.sendRawPacketToServerServerSide(packet, true);
        }
    }

    @Override
    public void scheduleSendRawPacketToServer(ByteBuf packet) {
        if (this.clientSide) {
            this.sendRawPacketToServerClientSide(packet, false);
        } else {
            this.sendRawPacketToServerServerSide(packet, false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendRawPacketToServerServerSide(ByteBuf packet, boolean currentThread) {
        block9: {
            ByteBuf buf = packet.alloc().buffer();
            try {
                ChannelHandlerContext context = PipelineUtil.getPreviousContext(Via.getManager().getInjector().getDecoderName(), this.channel.pipeline());
                if (this.shouldTransformPacket()) {
                    try {
                        Type.VAR_INT.writePrimitive(buf, 1000);
                        Type.UUID.write(buf, this.generatePassthroughToken());
                    }
                    catch (Exception shouldNotHappen) {
                        throw new RuntimeException(shouldNotHappen);
                    }
                }
                buf.writeBytes(packet);
                Runnable act = () -> {
                    if (context != null) {
                        context.fireChannelRead(buf);
                    } else {
                        this.channel.pipeline().fireChannelRead(buf);
                    }
                };
                if (currentThread) {
                    act.run();
                    break block9;
                }
                try {
                    this.channel.eventLoop().submit(act);
                }
                catch (Throwable t) {
                    buf.release();
                    throw t;
                }
            }
            finally {
                packet.release();
            }
        }
    }

    private void sendRawPacketToServerClientSide(ByteBuf packet, boolean currentThread) {
        Runnable act = () -> this.getChannel().pipeline().context(Via.getManager().getInjector().getEncoderName()).writeAndFlush(packet);
        if (currentThread) {
            act.run();
        } else {
            try {
                this.getChannel().eventLoop().submit(act);
            }
            catch (Throwable e) {
                e.printStackTrace();
                packet.release();
            }
        }
    }

    @Override
    public boolean checkServerboundPacket() {
        if (this.pendingDisconnect) {
            return false;
        }
        return !this.packetLimiterEnabled || !this.packetTracker.incrementReceived() || !this.packetTracker.exceedsMaxPPS();
    }

    @Override
    public boolean checkClientboundPacket() {
        this.packetTracker.incrementSent();
        return true;
    }

    @Override
    public boolean shouldTransformPacket() {
        return this.active;
    }

    @Override
    public void transformClientbound(ByteBuf buf, Function<Throwable, Exception> cancelSupplier) throws Exception {
        this.transform(buf, Direction.CLIENTBOUND, cancelSupplier);
    }

    @Override
    public void transformServerbound(ByteBuf buf, Function<Throwable, Exception> cancelSupplier) throws Exception {
        this.transform(buf, Direction.SERVERBOUND, cancelSupplier);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void transform(ByteBuf buf, Direction direction, Function<Throwable, Exception> cancelSupplier) throws Exception {
        if (!buf.isReadable()) {
            return;
        }
        int id = Type.VAR_INT.readPrimitive(buf);
        if (id == 1000) {
            if (!this.passthroughTokens.remove(Type.UUID.read(buf))) {
                throw new IllegalArgumentException("Invalid token");
            }
            return;
        }
        PacketWrapperImpl wrapper = new PacketWrapperImpl(id, buf, (UserConnection)this);
        try {
            this.protocolInfo.getPipeline().transform(direction, this.protocolInfo.getState(), wrapper);
        }
        catch (CancelException ex) {
            throw cancelSupplier.apply(ex);
        }
        ByteBuf transformed = buf.alloc().buffer();
        try {
            wrapper.writeToBuffer(transformed);
            buf.clear().writeBytes(transformed);
        }
        finally {
            transformed.release();
        }
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public @Nullable Channel getChannel() {
        return this.channel;
    }

    @Override
    public ProtocolInfo getProtocolInfo() {
        return this.protocolInfo;
    }

    @Override
    public Map<Class<?>, StorableObject> getStoredObjects() {
        return this.storedObjects;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isPendingDisconnect() {
        return this.pendingDisconnect;
    }

    @Override
    public void setPendingDisconnect(boolean pendingDisconnect) {
        this.pendingDisconnect = pendingDisconnect;
    }

    @Override
    public boolean isClientSide() {
        return this.clientSide;
    }

    @Override
    public boolean shouldApplyBlockProtocol() {
        return !this.clientSide;
    }

    @Override
    public boolean isPacketLimiterEnabled() {
        return this.packetLimiterEnabled;
    }

    @Override
    public void setPacketLimiterEnabled(boolean packetLimiterEnabled) {
        this.packetLimiterEnabled = packetLimiterEnabled;
    }

    @Override
    public UUID generatePassthroughToken() {
        UUID token = UUID.randomUUID();
        this.passthroughTokens.add(token);
        return token;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserConnectionImpl that = (UserConnectionImpl)o;
        return this.id == that.id;
    }

    public int hashCode() {
        return Long.hashCode(this.id);
    }
}

