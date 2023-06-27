package net.minecraft.network;

import io.netty.channel.nio.*;
import net.minecraft.server.*;
import com.google.common.collect.*;
import io.netty.channel.epoll.*;
import io.netty.channel.socket.nio.*;
import io.netty.bootstrap.*;
import io.netty.handler.timeout.*;
import net.minecraft.server.network.*;
import io.netty.channel.*;
import io.netty.channel.socket.*;
import java.io.*;
import java.net.*;
import net.minecraft.client.network.*;
import io.netty.channel.local.*;
import java.util.*;
import java.util.concurrent.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.*;
import io.netty.util.concurrent.*;
import net.minecraft.crash.*;
import org.apache.logging.log4j.*;
import com.google.common.util.concurrent.*;

public class NetworkSystem
{
    private static final Logger logger;
    public static final LazyLoadBase<NioEventLoopGroup> eventLoops;
    public static final LazyLoadBase<EpollEventLoopGroup> field_181141_b;
    public static final LazyLoadBase<LocalEventLoopGroup> SERVER_LOCAL_EVENTLOOP;
    private final MinecraftServer mcServer;
    public volatile boolean isAlive;
    private final List<ChannelFuture> endpoints;
    private final List<NetworkManager> networkManagers;
    
    public NetworkSystem(final MinecraftServer server) {
        this.endpoints = Collections.synchronizedList((List<ChannelFuture>)Lists.newArrayList());
        this.networkManagers = Collections.synchronizedList((List<NetworkManager>)Lists.newArrayList());
        this.mcServer = server;
        this.isAlive = true;
    }
    
    public void addLanEndpoint(final InetAddress address, final int port) throws IOException {
        synchronized (this.endpoints) {
            Class<? extends ServerSocketChannel> oclass;
            LazyLoadBase<? extends EventLoopGroup> lazyloadbase;
            if (Epoll.isAvailable() && this.mcServer.func_181035_ah()) {
                oclass = (Class<? extends ServerSocketChannel>)EpollServerSocketChannel.class;
                lazyloadbase = (LazyLoadBase<? extends EventLoopGroup>)NetworkSystem.field_181141_b;
                NetworkSystem.logger.info("Using epoll channel type");
            }
            else {
                oclass = (Class<? extends ServerSocketChannel>)NioServerSocketChannel.class;
                lazyloadbase = (LazyLoadBase<? extends EventLoopGroup>)NetworkSystem.eventLoops;
                NetworkSystem.logger.info("Using default channel type");
            }
            this.endpoints.add(((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel((Class)oclass)).childHandler((ChannelHandler)new ChannelInitializer<Channel>() {
                protected void initChannel(final Channel p_initChannel_1_) throws Exception {
                    try {
                        p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
                    }
                    catch (ChannelException ex) {}
                    p_initChannel_1_.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("legacy_query", (ChannelHandler)new PingResponseHandler(NetworkSystem.this)).addLast("splitter", (ChannelHandler)new MessageDeserializer2()).addLast("decoder", (ChannelHandler)new MessageDeserializer(EnumPacketDirection.SERVERBOUND)).addLast("prepender", (ChannelHandler)new MessageSerializer2()).addLast("encoder", (ChannelHandler)new MessageSerializer(EnumPacketDirection.CLIENTBOUND));
                    final NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.SERVERBOUND);
                    NetworkSystem.this.networkManagers.add(networkmanager);
                    p_initChannel_1_.pipeline().addLast("packet_handler", (ChannelHandler)networkmanager);
                    networkmanager.setNetHandler(new NetHandlerHandshakeTCP(NetworkSystem.this.mcServer, networkmanager));
                }
            }).group((EventLoopGroup)lazyloadbase.getValue()).localAddress(address, port)).bind().syncUninterruptibly());
        }
    }
    
    public SocketAddress addLocalEndpoint() {
        final ChannelFuture channelfuture;
        synchronized (this.endpoints) {
            channelfuture = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel((Class)LocalServerChannel.class)).childHandler((ChannelHandler)new ChannelInitializer<Channel>() {
                protected void initChannel(final Channel p_initChannel_1_) throws Exception {
                    final NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.SERVERBOUND);
                    networkmanager.setNetHandler(new NetHandlerHandshakeMemory(NetworkSystem.this.mcServer, networkmanager));
                    NetworkSystem.this.networkManagers.add(networkmanager);
                    p_initChannel_1_.pipeline().addLast("packet_handler", (ChannelHandler)networkmanager);
                }
            }).group((EventLoopGroup)NetworkSystem.eventLoops.getValue()).localAddress((SocketAddress)LocalAddress.ANY)).bind().syncUninterruptibly();
            this.endpoints.add(channelfuture);
        }
        return channelfuture.channel().localAddress();
    }
    
    public void terminateEndpoints() {
        this.isAlive = false;
        for (final ChannelFuture channelfuture : this.endpoints) {
            try {
                channelfuture.channel().close().sync();
            }
            catch (InterruptedException var4) {
                NetworkSystem.logger.error("Interrupted whilst closing channel");
            }
        }
    }
    
    public void networkTick() {
        synchronized (this.networkManagers) {
            final Iterator<NetworkManager> iterator = this.networkManagers.iterator();
            while (iterator.hasNext()) {
                final NetworkManager networkmanager = iterator.next();
                if (!networkmanager.hasNoChannel()) {
                    if (!networkmanager.isChannelOpen()) {
                        iterator.remove();
                        networkmanager.checkDisconnected();
                    }
                    else {
                        try {
                            networkmanager.processReceivedPackets();
                        }
                        catch (Exception exception) {
                            if (networkmanager.isLocalChannel()) {
                                final CrashReport crashreport = CrashReport.makeCrashReport(exception, "Ticking memory connection");
                                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Ticking connection");
                                crashreportcategory.addCrashSectionCallable("Connection", new Callable<String>() {
                                    @Override
                                    public String call() throws Exception {
                                        return networkmanager.toString();
                                    }
                                });
                                throw new ReportedException(crashreport);
                            }
                            NetworkSystem.logger.warn("Failed to handle packet for " + networkmanager.getRemoteAddress(), (Throwable)exception);
                            final ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");
                            networkmanager.sendPacket(new S40PacketDisconnect(chatcomponenttext), (GenericFutureListener<? extends Future<? super Void>>)new GenericFutureListener<Future<? super Void>>() {
                                public void operationComplete(final Future<? super Void> p_operationComplete_1_) throws Exception {
                                    networkmanager.closeChannel(chatcomponenttext);
                                }
                            }, (GenericFutureListener<? extends Future<? super Void>>[])new GenericFutureListener[0]);
                            networkmanager.disableAutoRead();
                        }
                    }
                }
            }
        }
    }
    
    public MinecraftServer getServer() {
        return this.mcServer;
    }
    
    static {
        logger = LogManager.getLogger();
        eventLoops = new LazyLoadBase<NioEventLoopGroup>() {
            @Override
            protected NioEventLoopGroup load() {
                return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build());
            }
        };
        field_181141_b = new LazyLoadBase<EpollEventLoopGroup>() {
            @Override
            protected EpollEventLoopGroup load() {
                return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
            }
        };
        SERVER_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
            @Override
            protected LocalEventLoopGroup load() {
                return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
            }
        };
    }
}
