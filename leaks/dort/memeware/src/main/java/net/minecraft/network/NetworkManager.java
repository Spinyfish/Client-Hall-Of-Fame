package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.proxy.MinecraftProxyHandler;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.enums.PacketDirection;

import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Queue;

@SuppressWarnings("all")
public class NetworkManager extends SimpleChannelInboundHandler {
    public static final Marker logMarkerNetwork = MarkerManager.getMarker("NETWORK");
    public static final Marker logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", logMarkerNetwork);
    public static final AttributeKey attrKeyConnectionState = AttributeKey.valueOf("protocol");
    public static final LazyLoadBase CLIENT_NIO_EVENTLOOP = new LazyLoadBase() {
        // private static final String __OBFID = "CL_00001241";
        protected NioEventLoopGroup genericLoad() {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }

        protected Object load() {
            return this.genericLoad();
        }
    };
    public static final LazyLoadBase CLIENT_LOCAL_EVENTLOOP = new LazyLoadBase() {
        // private static final String __OBFID = "CL_00001242";
        protected LocalEventLoopGroup genericLoad() {
            return new LocalEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
        }

        protected Object load() {
            return this.genericLoad();
        }
    };
    private static final Logger logger = LogManager.getLogger();
    private static boolean useProxies;
    private final EnumPacketDirection direction;
    /**
     * The queue for packets that require transmission
     */
    private final Queue outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
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
    // private static final String __OBFID = "CL_00001240";
    private boolean disconnected;

    public NetworkManager(EnumPacketDirection packetDirection) {
        this.direction = packetDirection;
    }

    public static void setUseProxies(boolean useProxies) {
        NetworkManager.useProxies = useProxies;
    }

    public static boolean canUseProxies() {
        return useProxies;
    }

    /**
     * Prepares a clientside NetworkManager: establishes a connection to the address and port supplied and configures
     * the channel pipeline. Returns the newly created instance.
     */
    public static NetworkManager provideLanClient(InetAddress serverAddress, int serverPort) {
        try {
//            int clientSideVer = ((Number) ReflectUtils.getField(null, "com.github.creeper123123321.viafabric.ViaFabric", "clientSideVersion")).intValue();
//            String commonTransformerEnc = ReflectUtils.getField(null, "com.github.creeper123123321.viafabric.handler.CommonTransformer", "HANDLER_ENCODER_NAME").toString();
//            String commonTransformerDec = ReflectUtils.getField(null, "com.github.creeper123123321.viafabric.handler.CommonTransformer", "HANDLER_DECODER_NAME").toString();
            NetworkManager networkManager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new OioEventLoopGroup());
            bootstrap.handler(new ChannelInitializer<OioSocketChannel>() {
                protected void initChannel(OioSocketChannel socketChannel) {
                    socketChannel.config().setOption(ChannelOption.IP_TOS, 24);
                    socketChannel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    socketChannel.pipeline().addLast("timeout", new ReadTimeoutHandler(20))
                            .addLast("splitter", new MessageDeserializer2())
                            .addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))
                            .addLast("prepender", new MessageSerializer2())
                            .addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND))
                            .addLast("packet_handler", networkManager);
                }
            });
            bootstrap.channelFactory(new MinecraftProxyHandler(Client.INSTANCE.getProxy() == null ? Proxy.NO_PROXY : Client.INSTANCE.getProxy()));
            bootstrap.connect(serverAddress, serverPort).syncUninterruptibly();
            return networkManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Prepares a clientside NetworkManager: establishes a connection to the socket supplied and configures the channel
     * pipeline. Returns the newly created instance.
     */
    public static NetworkManager provideLocalClient(SocketAddress p_150722_0_) {
        final NetworkManager var1 = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        (new Bootstrap()).group((EventLoopGroup) CLIENT_LOCAL_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel p_initChannel_1_) {
                p_initChannel_1_.pipeline().addLast("packet_handler", var1);
            }
        }).channel(LocalChannel.class).connect(p_150722_0_).syncUninterruptibly();
        return var1;
    }

    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
        super.channelActive(p_channelActive_1_);
        this.channel = p_channelActive_1_.channel();
        this.socketAddress = this.channel.remoteAddress();

        try {
            this.setConnectionState(EnumConnectionState.HANDSHAKING);
        } catch (Throwable var3) {
            logger.fatal(var3);
        }
    }

    /**
     * Sets the new connection state and registers which packets this channel may send and receive
     */
    public void setConnectionState(EnumConnectionState newState) {
        this.channel.attr(attrKeyConnectionState).set(newState);
        this.channel.config().setAutoRead(true);
        logger.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext p_channelInactive_1_) {
        this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
    }

    public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) {
        logger.debug("Disconnecting " + this.getRemoteAddress(), p_exceptionCaught_2_);
        this.closeChannel(new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + p_exceptionCaught_2_));
    }

    protected void channelRead0(Packet p_channelRead0_2_) {
        if (this.channel.isOpen()) {
            try {
                PacketEvent packetEvent = new PacketEvent(PacketDirection.INBOUND, p_channelRead0_2_);
                Client.INSTANCE.getEventBus().post(packetEvent);
                if (packetEvent.isCancelled())
                    return;
                packetEvent.getPacket().processPacket(this.packetListener);
            } catch (ThreadQuickExitException ignored) {
            }
        }
    }

    public void sendPacket(Packet packetIn) {
        if (this.channel != null && this.channel.isOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, null);
        } else {
            this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener[]) null));
        }
    }

    public void sendPacket(Packet packetIn, GenericFutureListener listener, GenericFutureListener... listeners) {
        if (this.channel != null && this.channel.isOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, ArrayUtils.add(listeners, 0, listener));
        } else {
            this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, ArrayUtils.add(listeners, 0, listener)));
        }
    }

    /**
     * Will commit the packet to the channel. If the current thread 'owns' the channel it will write and flush the
     * packet, otherwise it will add a task for the channel eventloop thread to do that.
     */
    private void dispatchPacket(final Packet inPacket, final GenericFutureListener[] futureListeners) {

        final EnumConnectionState var3 = EnumConnectionState.getFromPacket(inPacket);
        final EnumConnectionState var4 = (EnumConnectionState) this.channel.attr(attrKeyConnectionState).get();

        if (var4 != var3) {
            logger.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            if (var3 != var4) {
                this.setConnectionState(var3);
            }

            ChannelFuture var5 = this.channel.writeAndFlush(inPacket);

            if (futureListeners != null) {
                var5.addListeners(futureListeners);
            }

            var5.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(new Runnable() {
                // private static final String __OBFID = "CL_00001243";
                public void run() {
                    if (var3 != var4) {
                        NetworkManager.this.setConnectionState(var3);
                    }

                    ChannelFuture var1 = NetworkManager.this.channel.writeAndFlush(inPacket);

                    if (futureListeners != null) {
                        var1.addListeners(futureListeners);
                    }

                    var1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

    /**
     * Will iterate through the outboundPacketQueue and dispatch all Packets
     */
    private void flushOutboundQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            while (!this.outboundPacketsQueue.isEmpty()) {
                NetworkManager.InboundHandlerTuplePacketListener var1 = (NetworkManager.InboundHandlerTuplePacketListener) this.outboundPacketsQueue.poll();
                this.dispatchPacket(var1.packet, var1.futureListeners);
            }
        }
    }

    /**
     * Checks timeouts and processes all packets received
     */
    public void processReceivedPackets() {
        this.flushOutboundQueue();

        if (this.packetListener instanceof IUpdatePlayerListBox) {
            ((IUpdatePlayerListBox) this.packetListener).update();
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
    public void closeChannel(IChatComponent message) {
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

    /**
     * Adds an encoder+decoder to the channel pipeline. The parameter is the secret key used for encrypted communication
     */
    public void enableEncryption(SecretKey key) {
        this.isEncrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.func_151229_a(2, key)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.func_151229_a(1, key)));
    }

    public boolean func_179292_f() {
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
     * Sets the NetHandler for this NetworkManager, no checks are made if this handler is suitable for the particular
     * connection state (protocol)
     */
    public void setNetHandler(INetHandler handler) {
        Validate.notNull(handler, "packetListener");
        logger.debug("Set listener of {} to {}", this, handler);
        this.packetListener = handler;
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

    public void setCompressionThreshold(int threshold) {
        if (threshold > 0) {
            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
                ((NettyCompressionEncoder) this.channel.pipeline().get("decompress")).setCompressionTreshold(threshold);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(threshold));
            }
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
                ((NettyCompressionDecoder) this.channel.pipeline().get("decompress")).setCompressionTreshold(threshold);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(threshold));
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
        if (!this.hasNoChannel() && !this.isChannelOpen() && !this.disconnected) {
            this.disconnected = true;

            if (this.getExitMessage() != null) {
                this.getNetHandler().onDisconnect(this.getExitMessage());
            } else if (this.getNetHandler() != null) {
                this.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
            }
        }
    }

    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, Object p_channelRead0_2_) {
        this.channelRead0((Packet) p_channelRead0_2_);
    }

    static class InboundHandlerTuplePacketListener {
        private final Packet packet;
        private final GenericFutureListener[] futureListeners;
        // private static final String __OBFID = "CL_00001244";

        public InboundHandlerTuplePacketListener(Packet inPacket, GenericFutureListener... inFutureListeners) {
            this.packet = inPacket;
            this.futureListeners = inFutureListeners;
        }
    }
}
