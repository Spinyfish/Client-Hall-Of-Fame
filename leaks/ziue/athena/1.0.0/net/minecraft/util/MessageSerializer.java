package net.minecraft.util;

import io.netty.handler.codec.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import io.netty.util.*;
import java.io.*;
import net.minecraft.network.*;
import org.apache.logging.log4j.*;

public class MessageSerializer extends MessageToByteEncoder<Packet>
{
    private static final Logger logger;
    private static final Marker RECEIVED_PACKET_MARKER;
    private final EnumPacketDirection direction;
    
    public MessageSerializer(final EnumPacketDirection direction) {
        this.direction = direction;
    }
    
    protected void encode(final ChannelHandlerContext p_encode_1_, final Packet p_encode_2_, final ByteBuf p_encode_3_) throws IOException, Exception {
        final Integer integer = ((EnumConnectionState)p_encode_1_.channel().attr((AttributeKey)NetworkManager.attrKeyConnectionState).get()).getPacketId(this.direction, p_encode_2_);
        if (MessageSerializer.logger.isDebugEnabled()) {
            MessageSerializer.logger.debug(MessageSerializer.RECEIVED_PACKET_MARKER, "OUT: [{}:{}] {}", new Object[] { p_encode_1_.channel().attr((AttributeKey)NetworkManager.attrKeyConnectionState).get(), integer, p_encode_2_.getClass().getName() });
        }
        if (integer == null) {
            throw new IOException("Can't serialize unregistered packet");
        }
        final PacketBuffer packetbuffer = new PacketBuffer(p_encode_3_);
        packetbuffer.writeVarIntToBuffer(integer);
        try {
            p_encode_2_.writePacketData(packetbuffer);
        }
        catch (Throwable throwable) {
            MessageSerializer.logger.error((Object)throwable);
        }
    }
    
    static {
        logger = LogManager.getLogger();
        RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_SENT", NetworkManager.logMarkerPackets);
    }
}
