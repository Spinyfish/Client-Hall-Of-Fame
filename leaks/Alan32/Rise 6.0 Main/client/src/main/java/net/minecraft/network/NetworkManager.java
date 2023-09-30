package net.minecraft.network;

import com.alan.clients.Client;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.util.*;
import net.minecraft.viamcp.ViaMCP;
import net.minecraft.viamcp.handler.CommonTransformer;
import net.minecraft.viamcp.handler.MCPDecodeHandler;
import net.minecraft.viamcp.handler.MCPEncodeHandler;
import net.minecraft.viamcp.utils.NettyUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {
    private static final Logger logger = LogManager.getLogger();
    public static final Marker logMarkerNetwork = MarkerManager.getMarker("NETWORK");
    public static final Marker logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", logMarkerNetwork);
    public static final AttributeKey<EnumConnectionState> attrKeyConnectionState = AttributeKey.valueOf("protocol");
    public static final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP = new LazyLoadBase<NioEventLoopGroup>() {
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }
    };
    public static final LazyLoadBase<EpollEventLoopGroup> field_181125_e = new LazyLoadBase<EpollEventLoopGroup>() {
        protected EpollEventLoopGroup load() {
            return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
        }
    };
    public static final LazyLoadBase<LocalEventLoopGroup> CLIENT_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
        protected LocalEventLoopGroup load() {
            return new LocalEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
        }
    };
    private final EnumPacketDirection direction;
    private final Queue<NetworkManager.InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
    private final ReentrantReadWriteLock field_181680_j = new ReentrantReadWriteLock();

    /**
     * The active channel
     */
    private Channel channel;

    /**
     * The address of the remote party
     */
    private SocketAddress socketAddress;

    /**
     * The INetHandler instance responsible for processing received packets
     */
    private INetHandler packetListener;

    /**
     * A String indicating why the network has shutdown.
     */
    private IChatComponent terminationReason;
    private boolean isEncrypted;
    private boolean disconnected;

    public NetworkManager(final EnumPacketDirection packetDirection) {
        this.direction = packetDirection;
    }

    public void channelActive(final ChannelHandlerContext p_channelActive_1_) throws Exception {
        super.channelActive(p_channelActive_1_);
        this.channel = p_channelActive_1_.channel();
        this.socketAddress = this.channel.remoteAddress();

        try {
            this.setConnectionState(EnumConnectionState.HANDSHAKING);
        } catch (final Throwable throwable) {
            logger.fatal(throwable);
        }
    }

    /**
     * Sets the new connection state and registers which packets this channel may send and receive
     */
    public void setConnectionState(final EnumConnectionState newState) {
        this.channel.attr(attrKeyConnectionState).set(newState);
        this.channel.config().setAutoRead(true);
        logger.debug("Enabled auto read");
    }

    public void channelInactive(final ChannelHandlerContext p_channelInactive_1_) throws Exception {
        this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
    }

    public void exceptionCaught(final ChannelHandlerContext p_exceptionCaught_1_, final Throwable p_exceptionCaught_2_) throws Exception {
        final ChatComponentTranslation chatcomponenttranslation;

        if (p_exceptionCaught_2_ instanceof TimeoutException) {
            chatcomponenttranslation = new ChatComponentTranslation("disconnect.timeout");
        } else {
            chatcomponenttranslation = new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + p_exceptionCaught_2_);
        }

        this.closeChannel(chatcomponenttranslation);
    }

    protected void channelRead0(final ChannelHandlerContext p_channelRead0_1_, final Packet p_channelRead0_2_) throws Exception {
        if (this.channel.isOpen()) {
            try {
                final PacketReceiveEvent event = new PacketReceiveEvent(p_channelRead0_2_);
                Client.INSTANCE.getEventBus().handle(event);

                if (event.isCancelled()) {
                    return;
                }

                p_channelRead0_2_.processPacket(this.packetListener);
            } catch (final ThreadQuickExitException var4) {
            }
        }
    }

    public void receivePacket(final Packet packet) {
        if (this.channel.isOpen()) {
            try {
                if (Client.INSTANCE.getSecurityManager().isInsecure(packet)) {
                    return;
                }

                final PacketReceiveEvent event = new PacketReceiveEvent(packet);
                Client.INSTANCE.getEventBus().handle(event);

                if (event.isCancelled()) {
                    return;
                }

                packet.processPacket(this.packetListener);
            } catch (final ThreadQuickExitException var4) {
            }
        }
    }

    public void receiveUnregisteredPacket(final Packet packet) {
        if (this.channel.isOpen()) {
            try {
                packet.processPacket(this.packetListener);
            } catch (final ThreadQuickExitException var4) {
            }
        }
    }

    /**
     * Sets the NetHandler for this NetworkManager, no checks are made if this handler is suitable for the particular
     * connection state (protocol)
     */
    public void setNetHandler(final INetHandler handler) {
        Validate.notNull(handler, "packetListener");
        logger.debug("Set listener of {} to {}", new Object[]{this, handler});
        this.packetListener = handler;
    }

    public void sendPacket(final Packet packetIn) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, null);
        } else {
            this.field_181680_j.writeLock().lock();

            try {
                this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener[]) null));
            } finally {
                this.field_181680_j.writeLock().unlock();
            }
        }
    }

    public void sendUnregisteredPacket(final Packet packetIn) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchUnregisteredPacket(packetIn, null);
        } else {
            this.field_181680_j.writeLock().lock();

            try {
                this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener[]) null));
            } finally {
                this.field_181680_j.writeLock().unlock();
            }
        }
    }

    public void sendPacket(final Packet packetIn, final GenericFutureListener<? extends Future<? super Void>> listener, final GenericFutureListener<? extends Future<? super Void>>... listeners) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, ArrayUtils.add(listeners, 0, listener));
        } else {
            this.field_181680_j.writeLock().lock();

            try {
                this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, ArrayUtils.add(listeners, 0, listener)));
            } finally {
                this.field_181680_j.writeLock().unlock();
            }
        }
    }

    /**
     * Will commit the packet to the channel. If the current thread 'owns' the channel it will write and flush the
     * packet, otherwise it will add a task for the channel eventloop thread to do that.
     */
    private void dispatchPacket(final Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
        final PacketSendEvent event = new PacketSendEvent(inPacket);
        Client.INSTANCE.getEventBus().handle(event);

        if (event.isCancelled()) {
            return;
        }

        final EnumConnectionState enumconnectionstate = EnumConnectionState.getFromPacket(event.getPacket());
        final EnumConnectionState enumconnectionstate1 = this.channel.attr(attrKeyConnectionState).get();

        if (enumconnectionstate1 != enumconnectionstate) {
            logger.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            if (enumconnectionstate != enumconnectionstate1) {
                this.setConnectionState(enumconnectionstate);
            }

            final ChannelFuture channelfuture = this.channel.writeAndFlush(event.getPacket());

            if (futureListeners != null) {
                channelfuture.addListeners(futureListeners);
            }

            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(new Runnable() {
                public void run() {
                    if (enumconnectionstate != enumconnectionstate1) {
                        NetworkManager.this.setConnectionState(enumconnectionstate);
                    }

                    final ChannelFuture channelfuture1 = NetworkManager.this.channel.writeAndFlush(event.getPacket());

                    if (futureListeners != null) {
                        channelfuture1.addListeners(futureListeners);
                    }

                    channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

    /**
     * Will commit the packet to the channel. If the current thread 'owns' the channel it will write and flush the
     * packet, otherwise it will add a task for the channel eventloop thread to do that.
     */
    private void dispatchUnregisteredPacket(final Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
        final EnumConnectionState enumconnectionstate = EnumConnectionState.getFromPacket(inPacket);
        final EnumConnectionState enumconnectionstate1 = this.channel.attr(attrKeyConnectionState).get();

        if (enumconnectionstate1 != enumconnectionstate) {
            logger.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            if (enumconnectionstate != enumconnectionstate1) {
                this.setConnectionState(enumconnectionstate);
            }

            final ChannelFuture channelfuture = this.channel.writeAndFlush(inPacket);

            if (futureListeners != null) {
                channelfuture.addListeners(futureListeners);
            }

            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(new Runnable() {
                public void run() {
                    if (enumconnectionstate != enumconnectionstate1) {
                        NetworkManager.this.setConnectionState(enumconnectionstate);
                    }

                    final ChannelFuture channelfuture1 = NetworkManager.this.channel.writeAndFlush(inPacket);

                    if (futureListeners != null) {
                        channelfuture1.addListeners(futureListeners);
                    }

                    channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

    /**
     * Will iterate through the outboundPacketQueue and dispatch all Packets
     */
    private void flushOutboundQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            this.field_181680_j.readLock().lock();

            try {
                while (!this.outboundPacketsQueue.isEmpty()) {
                    final NetworkManager.InboundHandlerTuplePacketListener networkmanager$inboundhandlertuplepacketlistener = this.outboundPacketsQueue.poll();
                    this.dispatchPacket(networkmanager$inboundhandlertuplepacketlistener.packet, networkmanager$inboundhandlertuplepacketlistener.futureListeners);
                }
            } finally {
                this.field_181680_j.readLock().unlock();
            }
        }
    }

    /**
     * Checks timeouts and processes all packets received
     */
    public void processReceivedPackets() {
        this.flushOutboundQueue();

        if (this.packetListener instanceof ITickable) {
            ((ITickable) this.packetListener).update();
        }

        this.channel.flush();
    }

    /**
     * Returns the socket address of the remote side. Server-only.
     */
    public SocketAddress getRemoteAddress() {
        return this.socketAddress;
    }

    /**
     * Closes the channel, the parameter can be used for an exit message (not certain how it gets sent)
     */
    public void closeChannel(final IChatComponent message) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.terminationReason = message;
        }
    }

    /**
     * True if this NetworkManager uses a memory connection (single player game). False may imply both an active TCP
     * connection or simply no active connection at all
     */
    public boolean isLocalChannel() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public static NetworkManager func_181124_a(final InetAddress p_181124_0_, final int p_181124_1_, final boolean p_181124_2_, final int timeout) {
        final NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        final Class<? extends SocketChannel> oclass;
        final LazyLoadBase<? extends EventLoopGroup> lazyloadbase;

        if (Epoll.isAvailable() && p_181124_2_) {
            oclass = EpollSocketChannel.class;
            lazyloadbase = field_181125_e;
        } else {
            oclass = NioSocketChannel.class;
            lazyloadbase = CLIENT_NIO_EVENTLOOP;
        }

        (new Bootstrap()).group(lazyloadbase.getValue()).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(final Channel p_initChannel_1_) throws Exception {
                try {
                    p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
                    if (timeout != -1) {
                        p_initChannel_1_.config().setOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout);
                    }
                } catch (final ChannelException var3) {
                }

                p_initChannel_1_.pipeline().addLast((String) "timeout", (ChannelHandler) (new ReadTimeoutHandler(30))).addLast((String) "splitter", (ChannelHandler) (new MessageDeserializer2())).addLast((String) "decoder", (ChannelHandler) (new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))).addLast((String) "prepender", (ChannelHandler) (new MessageSerializer2())).addLast((String) "encoder", (ChannelHandler) (new MessageSerializer(EnumPacketDirection.SERVERBOUND))).addLast((String) "packet_handler", (ChannelHandler) networkmanager);
//                p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new MessageDeserializer2()).addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND)).addLast("prepender", new MessageSerializer2()).addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND)).addLast("packet_handler", networkmanager);

                if (p_initChannel_1_ instanceof SocketChannel && ViaMCP.getInstance().getVersion() != ViaMCP.PROTOCOL_VERSION) {
                    UserConnection user = new UserConnectionImpl(p_initChannel_1_, true);
                    new ProtocolPipelineImpl(user);
                    p_initChannel_1_.pipeline().addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new MCPEncodeHandler(user)).addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new MCPDecodeHandler(user));
                }
            }
        }).channel(oclass).connect(p_181124_0_, p_181124_1_).syncUninterruptibly();
        return networkmanager;
    }

    public static NetworkManager func_181124_a(final InetAddress p_181124_0_, final int p_181124_1_, final boolean p_181124_2_) {
        final NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        final Class<? extends SocketChannel> oclass;
        final LazyLoadBase<? extends EventLoopGroup> lazyloadbase;

        if (Epoll.isAvailable() && p_181124_2_) {
            oclass = EpollSocketChannel.class;
            lazyloadbase = field_181125_e;
        } else {
            oclass = NioSocketChannel.class;
            lazyloadbase = CLIENT_NIO_EVENTLOOP;
        }

        (new Bootstrap()).group(lazyloadbase.getValue()).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(final Channel p_initChannel_1_) throws Exception {
                try {
                    p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
                } catch (final ChannelException var3) {
                }

                p_initChannel_1_.pipeline().addLast((String) "timeout", (ChannelHandler) (new ReadTimeoutHandler(30))).addLast((String) "splitter", (ChannelHandler) (new MessageDeserializer2())).addLast((String) "decoder", (ChannelHandler) (new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))).addLast((String) "prepender", (ChannelHandler) (new MessageSerializer2())).addLast((String) "encoder", (ChannelHandler) (new MessageSerializer(EnumPacketDirection.SERVERBOUND))).addLast((String) "packet_handler", (ChannelHandler) networkmanager);
//                p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new MessageDeserializer2()).addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND)).addLast("prepender", new MessageSerializer2()).addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND)).addLast("packet_handler", networkmanager);

                if (p_initChannel_1_ instanceof SocketChannel && ViaMCP.getInstance().getVersion() != ViaMCP.PROTOCOL_VERSION) {
                    UserConnection user = new UserConnectionImpl(p_initChannel_1_, true);
                    new ProtocolPipelineImpl(user);
                    p_initChannel_1_.pipeline().addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new MCPEncodeHandler(user)).addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new MCPDecodeHandler(user));
                }
            }
        }).channel(oclass).connect(p_181124_0_, p_181124_1_).syncUninterruptibly();
        return networkmanager;
    }

    /**
     * Prepares a clientside NetworkManager: establishes a connection to the socket supplied and configures the channel
     * pipeline. Returns the newly created instance.
     */
    public static NetworkManager provideLocalClient(final SocketAddress address) {
        final NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        (new Bootstrap()).group(CLIENT_LOCAL_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(final Channel p_initChannel_1_) throws Exception {
                p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
            }
        }).channel(LocalChannel.class).connect(address).syncUninterruptibly();
        return networkmanager;
    }

    /**
     * Adds an encoder+decoder to the channel pipeline. The parameter is the secret key used for encrypted communication
     */
    public void enableEncryption(final SecretKey key) {
        this.isEncrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, key)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
    }

    public boolean getIsencrypted() {
        return this.isEncrypted;
    }

    /**
     * Returns true if this NetworkManager has an active channel, false otherwise
     */
    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean hasNoChannel() {
        return this.channel == null;
    }

    /**
     * Gets the current handler for processing packets
     */
    public INetHandler getNetHandler() {
        return this.packetListener;
    }

    /**
     * If this channel is closed, returns the exit message, null otherwise.
     */
    public IChatComponent getExitMessage() {
        return this.terminationReason;
    }

    /**
     * Switches the channel to manual reading modus
     */
    public void disableAutoRead() {
        this.channel.config().setAutoRead(false);
    }

    public void setCompressionTreshold(final int treshold) {
        if (treshold >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
                ((NettyCompressionDecoder) this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
            } else {
                NettyUtil.decodeEncodePlacement(channel.pipeline(), "decoder", "decompress", new NettyCompressionDecoder(treshold));
//                this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(treshold));
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
                ((NettyCompressionEncoder) this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
            } else {
                NettyUtil.decodeEncodePlacement(channel.pipeline(), "encoder", "compress", new NettyCompressionEncoder(treshold));
//                this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(treshold));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void checkDisconnected() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (!this.disconnected) {
                this.disconnected = true;

                if (this.getExitMessage() != null) {
                    this.getNetHandler().onDisconnect(this.getExitMessage());
                } else if (this.getNetHandler() != null) {
                    this.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
                }
            } else {
                logger.warn("handleDisconnection() called twice");
            }
        }
    }

    static class InboundHandlerTuplePacketListener {
        private final Packet packet;
        private final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

        public InboundHandlerTuplePacketListener(final Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
            this.packet = inPacket;
            this.futureListeners = inFutureListeners;
        }
    }
}
