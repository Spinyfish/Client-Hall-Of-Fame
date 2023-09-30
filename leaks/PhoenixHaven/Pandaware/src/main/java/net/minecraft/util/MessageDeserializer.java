package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecraft.network.*;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.List;

public class MessageDeserializer extends ByteToMessageDecoder {
    private final EnumPacketDirection direction;

    public MessageDeserializer(EnumPacketDirection direction) {
        this.direction = direction;
    }

    protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List<Object> p_decode_3_) throws Exception {
        if (p_decode_2_.readableBytes() != 0) {
            PacketBuffer packetbuffer = new PacketBuffer(p_decode_2_);
            int i = packetbuffer.readVarIntFromBuffer();
            Packet packet = p_decode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacket(this.direction, i);

            if (packet == null) {
                throw new IOException("Bad packet id " + i);
            } else {
                packet.readPacketData(packetbuffer);

                if (packetbuffer.readableBytes() > 0) {
                    throw new IOException("Packet " + p_decode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get().getId() + "/" + i + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + i);
                } else {
                    p_decode_3_.add(packet);

                    if (Logger.isDebugEnabled()) {
                        Logger.debug(" IN: [{}:{}] {}", p_decode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get(), Integer.valueOf(i), packet.getClass().getName());
                    }
                }
            }
        }
    }
}
