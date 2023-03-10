package de.datasecs.hydra.shared.protocol.packets.serialization;

import de.datasecs.hydra.shared.protocol.HydraProtocol;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created with love by DataSecs on 29.09.2017.
 */
public class PacketEncoder extends MessageToMessageEncoder<Packet> {

    private HydraProtocol protocol;

    public PacketEncoder(HydraProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void encode(ChannelHandlerContext context, Packet packet, List<Object> out) {
        ByteBuf byteBuf = context.alloc().buffer();
        byteBuf.writeByte(protocol.getPacketId(packet));
        packet.write(byteBuf);
        out.add(byteBuf);
    }
}