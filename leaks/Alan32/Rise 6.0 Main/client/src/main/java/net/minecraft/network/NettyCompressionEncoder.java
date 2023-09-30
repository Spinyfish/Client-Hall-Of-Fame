package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.zip.Deflater;

public class NettyCompressionEncoder extends MessageToByteEncoder<ByteBuf> {
    private final byte[] buffer = new byte[8192];
    private final Deflater deflater;
    private int treshold;

    public NettyCompressionEncoder(final int treshold) {
        this.treshold = treshold;
        this.deflater = new Deflater();
    }

    protected void encode(final ChannelHandlerContext p_encode_1_, final ByteBuf p_encode_2_, final ByteBuf p_encode_3_) throws Exception {
        final int i = p_encode_2_.readableBytes();
        final PacketBuffer packetbuffer = new PacketBuffer(p_encode_3_);

        if (i < this.treshold) {
            packetbuffer.writeVarIntToBuffer(0);
            packetbuffer.writeBytes(p_encode_2_);
        } else {
            final byte[] abyte = new byte[i];
            p_encode_2_.readBytes(abyte);
            packetbuffer.writeVarIntToBuffer(abyte.length);
            this.deflater.setInput(abyte, 0, i);
            this.deflater.finish();

            while (!this.deflater.finished()) {
                final int j = this.deflater.deflate(this.buffer);
                packetbuffer.writeBytes(this.buffer, 0, j);
            }

            this.deflater.reset();
        }
    }

    public void setCompressionTreshold(final int treshold) {
        this.treshold = treshold;
    }
}
