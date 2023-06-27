package net.minecraft.client.network;

import net.minecraft.client.multiplayer.*;
import net.minecraft.network.status.*;
import org.apache.commons.lang3.*;
import net.minecraft.client.*;
import com.mojang.authlib.*;
import net.minecraft.network.status.server.*;
import net.minecraft.network.*;
import net.minecraft.network.handshake.client.*;
import net.minecraft.network.status.client.*;
import java.net.*;
import io.netty.bootstrap.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.util.concurrent.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import net.minecraft.util.*;
import io.netty.channel.socket.nio.*;
import java.util.*;
import org.apache.logging.log4j.*;

public class OldServerPinger
{
    private static final Splitter PING_RESPONSE_SPLITTER;
    private static final Logger logger;
    private final List<NetworkManager> pingDestinations;
    
    public OldServerPinger() {
        this.pingDestinations = Collections.synchronizedList((List<NetworkManager>)Lists.newArrayList());
    }
    
    public void ping(final ServerData server) throws UnknownHostException {
        final ServerAddress serveraddress = ServerAddress.func_78860_a(server.serverIP);
        final NetworkManager networkmanager = NetworkManager.func_181124_a(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
        this.pingDestinations.add(networkmanager);
        server.serverMOTD = "Pinging...";
        server.pingToServer = -1L;
        server.playerList = null;
        networkmanager.setNetHandler(new INetHandlerStatusClient() {
            private boolean field_147403_d = false;
            private boolean field_183009_e = false;
            private long field_175092_e = 0L;
            
            @Override
            public void handleServerInfo(final S00PacketServerInfo packetIn) {
                if (this.field_183009_e) {
                    networkmanager.closeChannel(new ChatComponentText("Received unrequested status"));
                }
                else {
                    this.field_183009_e = true;
                    final ServerStatusResponse serverstatusresponse = packetIn.getResponse();
                    if (serverstatusresponse.getServerDescription() != null) {
                        server.serverMOTD = serverstatusresponse.getServerDescription().getFormattedText();
                    }
                    else {
                        server.serverMOTD = "";
                    }
                    if (serverstatusresponse.getProtocolVersionInfo() != null) {
                        server.gameVersion = serverstatusresponse.getProtocolVersionInfo().getName();
                        server.version = serverstatusresponse.getProtocolVersionInfo().getProtocol();
                    }
                    else {
                        server.gameVersion = "Old";
                        server.version = 0;
                    }
                    if (serverstatusresponse.getPlayerCountData() != null) {
                        server.populationInfo = EnumChatFormatting.GRAY + "" + serverstatusresponse.getPlayerCountData().getOnlinePlayerCount() + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + serverstatusresponse.getPlayerCountData().getMaxPlayers();
                        if (ArrayUtils.isNotEmpty((Object[])serverstatusresponse.getPlayerCountData().getPlayers())) {
                            final StringBuilder stringbuilder = new StringBuilder();
                            for (final GameProfile gameprofile : serverstatusresponse.getPlayerCountData().getPlayers()) {
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append("\n");
                                }
                                stringbuilder.append(gameprofile.getName());
                            }
                            if (serverstatusresponse.getPlayerCountData().getPlayers().length < serverstatusresponse.getPlayerCountData().getOnlinePlayerCount()) {
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append("\n");
                                }
                                stringbuilder.append("... and ").append(serverstatusresponse.getPlayerCountData().getOnlinePlayerCount() - serverstatusresponse.getPlayerCountData().getPlayers().length).append(" more ...");
                            }
                            server.playerList = stringbuilder.toString();
                        }
                    }
                    else {
                        server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                    }
                    if (serverstatusresponse.getFavicon() != null) {
                        final String s = serverstatusresponse.getFavicon();
                        if (s.startsWith("data:image/png;base64,")) {
                            server.setBase64EncodedIconData(s.substring("data:image/png;base64,".length()));
                        }
                        else {
                            OldServerPinger.logger.error("Invalid server icon (unknown format)");
                        }
                    }
                    else {
                        server.setBase64EncodedIconData(null);
                    }
                    this.field_175092_e = Minecraft.getSystemTime();
                    networkmanager.sendPacket(new C01PacketPing(this.field_175092_e));
                    this.field_147403_d = true;
                }
            }
            
            @Override
            public void handlePong(final S01PacketPong packetIn) {
                final long i = this.field_175092_e;
                final long j = Minecraft.getSystemTime();
                server.pingToServer = j - i;
                networkmanager.closeChannel(new ChatComponentText("Finished"));
            }
            
            @Override
            public void onDisconnect(final IChatComponent reason) {
                if (!this.field_147403_d) {
                    OldServerPinger.logger.error("Can't ping " + server.serverIP + ": " + reason.getUnformattedText());
                    server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                    server.populationInfo = "";
                    OldServerPinger.this.tryCompatibilityPing(server);
                }
            }
        });
        try {
            networkmanager.sendPacket(new C00Handshake(47, serveraddress.getIP(), serveraddress.getPort(), EnumConnectionState.STATUS));
            networkmanager.sendPacket(new C00PacketServerQuery());
        }
        catch (Throwable throwable) {
            OldServerPinger.logger.error((Object)throwable);
        }
    }
    
    private void tryCompatibilityPing(final ServerData server) {
        final ServerAddress serveraddress = ServerAddress.func_78860_a(server.serverIP);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)NetworkManager.CLIENT_NIO_EVENTLOOP.getValue())).handler((ChannelHandler)new ChannelInitializer<Channel>() {
            protected void initChannel(final Channel p_initChannel_1_) throws Exception {
                try {
                    p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
                }
                catch (ChannelException ex) {}
                p_initChannel_1_.pipeline().addLast(new ChannelHandler[] { (ChannelHandler)new SimpleChannelInboundHandler<ByteBuf>() {
                        public void channelActive(final ChannelHandlerContext p_channelActive_1_) throws Exception {
                            super.channelActive(p_channelActive_1_);
                            final ByteBuf bytebuf = Unpooled.buffer();
                            try {
                                bytebuf.writeByte(254);
                                bytebuf.writeByte(1);
                                bytebuf.writeByte(250);
                                char[] achar = "MC|PingHost".toCharArray();
                                bytebuf.writeShort(achar.length);
                                for (final char c0 : achar) {
                                    bytebuf.writeChar((int)c0);
                                }
                                bytebuf.writeShort(7 + 2 * serveraddress.getIP().length());
                                bytebuf.writeByte(127);
                                achar = serveraddress.getIP().toCharArray();
                                bytebuf.writeShort(achar.length);
                                for (final char c2 : achar) {
                                    bytebuf.writeChar((int)c2);
                                }
                                bytebuf.writeInt(serveraddress.getPort());
                                p_channelActive_1_.channel().writeAndFlush((Object)bytebuf).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
                            }
                            finally {
                                bytebuf.release();
                            }
                        }
                        
                        protected void channelRead0(final ChannelHandlerContext p_channelRead0_1_, final ByteBuf p_channelRead0_2_) throws Exception {
                            final short short1 = p_channelRead0_2_.readUnsignedByte();
                            if (short1 == 255) {
                                final String s = new String(p_channelRead0_2_.readBytes(p_channelRead0_2_.readShort() * 2).array(), Charsets.UTF_16BE);
                                final String[] astring = (String[])Iterables.toArray(OldServerPinger.PING_RESPONSE_SPLITTER.split((CharSequence)s), (Class)String.class);
                                if ("§1".equals(astring[0])) {
                                    final int i = MathHelper.parseIntWithDefault(astring[1], 0);
                                    final String s2 = astring[2];
                                    final String s3 = astring[3];
                                    final int j = MathHelper.parseIntWithDefault(astring[4], -1);
                                    final int k = MathHelper.parseIntWithDefault(astring[5], -1);
                                    server.version = -1;
                                    server.gameVersion = s2;
                                    server.serverMOTD = s3;
                                    server.populationInfo = EnumChatFormatting.GRAY + "" + j + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + k;
                                }
                            }
                            p_channelRead0_1_.close();
                        }
                        
                        public void exceptionCaught(final ChannelHandlerContext p_exceptionCaught_1_, final Throwable p_exceptionCaught_2_) throws Exception {
                            p_exceptionCaught_1_.close();
                        }
                    } });
            }
        })).channel((Class)NioSocketChannel.class)).connect(serveraddress.getIP(), serveraddress.getPort());
    }
    
    public void pingPendingNetworks() {
        synchronized (this.pingDestinations) {
            final Iterator<NetworkManager> iterator = this.pingDestinations.iterator();
            while (iterator.hasNext()) {
                final NetworkManager networkmanager = iterator.next();
                if (networkmanager.isChannelOpen()) {
                    networkmanager.processReceivedPackets();
                }
                else {
                    iterator.remove();
                    networkmanager.checkDisconnected();
                }
            }
        }
    }
    
    public void clearPendingNetworks() {
        synchronized (this.pingDestinations) {
            final Iterator<NetworkManager> iterator = this.pingDestinations.iterator();
            while (iterator.hasNext()) {
                final NetworkManager networkmanager = iterator.next();
                if (networkmanager.isChannelOpen()) {
                    iterator.remove();
                    networkmanager.closeChannel(new ChatComponentText("Cancelled"));
                }
            }
        }
    }
    
    static {
        PING_RESPONSE_SPLITTER = Splitter.on('\0').limit(6);
        logger = LogManager.getLogger();
    }
}
