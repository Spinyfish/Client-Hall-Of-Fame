package net.minecraft.network;

import io.netty.handler.codec.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import java.util.*;
import javax.crypto.*;

public class NettyEncryptingDecoder extends MessageToMessageDecoder<ByteBuf>
{
    private final NettyEncryptionTranslator decryptionCodec;
    
    public NettyEncryptingDecoder(final Cipher cipher) {
        this.decryptionCodec = new NettyEncryptionTranslator(cipher);
    }
    
    protected void decode(final ChannelHandlerContext p_decode_1_, final ByteBuf p_decode_2_, final List<Object> p_decode_3_) throws ShortBufferException, Exception {
        p_decode_3_.add(this.decryptionCodec.decipher(p_decode_1_, p_decode_2_));
    }
}
