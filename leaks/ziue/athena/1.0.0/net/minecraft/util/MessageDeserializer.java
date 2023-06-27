package net.minecraft.util;

import io.netty.handler.codec.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import java.util.*;
import io.netty.util.*;
import java.io.*;
import net.minecraft.network.*;
import org.apache.logging.log4j.*;

public class MessageDeserializer extends ByteToMessageDecoder
{
    private static final Logger logger;
    private static final Marker RECEIVED_PACKET_MARKER;
    private final EnumPacketDirection direction;
    
    public MessageDeserializer(final EnumPacketDirection direction) {
        this.direction = direction;
    }
    
    protected void decode(final ChannelHandlerContext p_decode_1_, final ByteBuf p_decode_2_, final List<Object> p_decode_3_) throws IOException, InstantiationException, IllegalAccessException, Exception {
        if (p_decode_2_.readableBytes() != 0) {
            final PacketBuffer packetbuffer = new PacketBuffer(p_decode_2_);
            final int i = packetbuffer.readVarIntFromBuffer();
            final Packet packet = ((EnumConnectionState)p_decode_1_.channel().attr((AttributeKey)NetworkManager.attrKeyConnectionState).get()).getPacket(this.direction, i);
            if (packet == null) {
                throw new IOException("Bad packet id " + i);
            }
            packet.readPacketData(packetbuffer);
            if (packetbuffer.readableBytes() > 0) {
                throw new IOException("Packet " + ((EnumConnectionState)p_decode_1_.channel().attr((AttributeKey)NetworkManager.attrKeyConnectionState).get()).getId() + "/" + i + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + i);
            }
            p_decode_3_.add(packet);
            if (MessageDeserializer.logger.isDebugEnabled()) {
                MessageDeserializer.logger.debug(MessageDeserializer.RECEIVED_PACKET_MARKER, " IN: [{}:{}] {}", new Object[] { p_decode_1_.channel().attr((AttributeKey)NetworkManager.attrKeyConnectionState).get(), i, packet.getClass().getName() });
            }
        }
    }
    
    static {
        logger = LogManager.getLogger();
        RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.logMarkerPackets);
    }
}
