package net.minecraft.network;

import java.net.*;
import com.google.common.base.*;
import net.minecraft.server.*;
import io.netty.channel.*;
import io.netty.util.concurrent.*;
import io.netty.buffer.*;
import org.apache.logging.log4j.*;

public class PingResponseHandler extends ChannelInboundHandlerAdapter
{
    private static final Logger logger;
    private NetworkSystem networkSystem;
    
    public PingResponseHandler(final NetworkSystem networkSystemIn) {
        this.networkSystem = networkSystemIn;
    }
    
    public void channelRead(final ChannelHandlerContext p_channelRead_1_, final Object p_channelRead_2_) throws Exception {
        final ByteBuf bytebuf = (ByteBuf)p_channelRead_2_;
        bytebuf.markReaderIndex();
        boolean flag = true;
        try {
            if (bytebuf.readUnsignedByte() == 254) {
                final InetSocketAddress inetsocketaddress = (InetSocketAddress)p_channelRead_1_.channel().remoteAddress();
                final MinecraftServer minecraftserver = this.networkSystem.getServer();
                final int i = bytebuf.readableBytes();
                switch (i) {
                    case 0: {
                        PingResponseHandler.logger.debug("Ping: (<1.3.x) from {}:{}", new Object[] { inetsocketaddress.getAddress(), inetsocketaddress.getPort() });
                        final String s2 = String.format("%s§%d§%d", minecraftserver.getMOTD(), minecraftserver.getCurrentPlayerCount(), minecraftserver.getMaxPlayers());
                        this.writeAndFlush(p_channelRead_1_, this.getStringBuffer(s2));
                        break;
                    }
                    case 1: {
                        if (bytebuf.readUnsignedByte() != 1) {
                            return;
                        }
                        PingResponseHandler.logger.debug("Ping: (1.4-1.5.x) from {}:{}", new Object[] { inetsocketaddress.getAddress(), inetsocketaddress.getPort() });
                        final String s3 = String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, minecraftserver.getMinecraftVersion(), minecraftserver.getMOTD(), minecraftserver.getCurrentPlayerCount(), minecraftserver.getMaxPlayers());
                        this.writeAndFlush(p_channelRead_1_, this.getStringBuffer(s3));
                        break;
                    }
                    default: {
                        boolean flag2 = bytebuf.readUnsignedByte() == 1;
                        flag2 &= (bytebuf.readUnsignedByte() == 250);
                        flag2 &= "MC|PingHost".equals(new String(bytebuf.readBytes(bytebuf.readShort() * 2).array(), Charsets.UTF_16BE));
                        final int j = bytebuf.readUnsignedShort();
                        flag2 &= (bytebuf.readUnsignedByte() >= 73);
                        flag2 &= (3 + bytebuf.readBytes(bytebuf.readShort() * 2).array().length + 4 == j);
                        flag2 &= (bytebuf.readInt() <= 65535);
                        flag2 &= (bytebuf.readableBytes() == 0);
                        if (!flag2) {
                            return;
                        }
                        PingResponseHandler.logger.debug("Ping: (1.6) from {}:{}", new Object[] { inetsocketaddress.getAddress(), inetsocketaddress.getPort() });
                        final String s4 = String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, minecraftserver.getMinecraftVersion(), minecraftserver.getMOTD(), minecraftserver.getCurrentPlayerCount(), minecraftserver.getMaxPlayers());
                        final ByteBuf bytebuf2 = this.getStringBuffer(s4);
                        try {
                            this.writeAndFlush(p_channelRead_1_, bytebuf2);
                        }
                        finally {
                            bytebuf2.release();
                        }
                        break;
                    }
                }
                bytebuf.release();
                flag = false;
            }
        }
        catch (RuntimeException var21) {}
        finally {
            if (flag) {
                bytebuf.resetReaderIndex();
                p_channelRead_1_.channel().pipeline().remove("legacy_query");
                p_channelRead_1_.fireChannelRead(p_channelRead_2_);
            }
        }
    }
    
    private void writeAndFlush(final ChannelHandlerContext ctx, final ByteBuf data) {
        ctx.pipeline().firstContext().writeAndFlush((Object)data).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
    }
    
    private ByteBuf getStringBuffer(final String string) {
        final ByteBuf bytebuf = Unpooled.buffer();
        bytebuf.writeByte(255);
        final char[] achar = string.toCharArray();
        bytebuf.writeShort(achar.length);
        for (final char c0 : achar) {
            bytebuf.writeChar((int)c0);
        }
        return bytebuf;
    }
    
    static {
        logger = LogManager.getLogger();
    }
}
