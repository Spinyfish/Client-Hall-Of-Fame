package de.datasecs.hydra.shared.handler;

import de.datasecs.hydra.shared.protocol.HydraProtocol;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.StandardPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.Serializable;
import java.net.SocketAddress;

/**
 * Created with love by DataSecs on 29.09.2017.
 */
public class HydraSession extends SimpleChannelInboundHandler<Packet> implements Session {

    private Channel channel;

    private HydraProtocol protocol;

    public HydraSession(Channel channel, HydraProtocol protocol) {
        this.channel = channel;
        this.protocol = protocol;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Packet packet) {
        if (protocol.getPacketListener() != null) {
            protocol.callPacketListener(packet, this);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        if (protocol.getSessionListener() != null) {
            protocol.callSessionListener(false, this);
        }

        protocol.removeSession(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void send(Packet packet) {
        channel.writeAndFlush(packet);
    }

    @Override
    public <T extends Serializable> void send(T object) {
        channel.writeAndFlush(new StandardPacket(object));
    }

    @Override
    public void close() {
        channel.disconnect();
    }

    @Override
    public boolean isConnected() {
        return channel.isActive();
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public SocketAddress getAddress() {
        SocketAddress address = channel.remoteAddress();
        return address == null ? channel.localAddress() : address;
    }
}