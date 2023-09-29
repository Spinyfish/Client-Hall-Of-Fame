package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.Cipher;

public class NettyEncryptingEncoder extends MessageToByteEncoder<ByteBuf> {
    private final NettyEncryptionTranslator encryptionCodec;

    public NettyEncryptingEncoder(final Cipher cipher) {
        this.encryptionCodec = new NettyEncryptionTranslator(cipher);
    }

    protected void encode(final ChannelHandlerContext p_encode_1_, final ByteBuf p_encode_2_, final ByteBuf p_encode_3_) throws Exception {
        this.encryptionCodec.cipher(p_encode_2_, p_encode_3_);
    }
}
