/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.sponge.handlers;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.platform.WrappedChannelInitializer;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import com.viaversion.viaversion.sponge.handlers.SpongeDecodeHandler;
import com.viaversion.viaversion.sponge.handlers.SpongeEncodeHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import java.lang.reflect.Method;

public class SpongeChannelInitializer
extends ChannelInitializer<Channel>
implements WrappedChannelInitializer {
    private static final Method INIT_CHANNEL_METHOD;
    private final ChannelInitializer<Channel> original;

    public SpongeChannelInitializer(ChannelInitializer<Channel> oldInit) {
        this.original = oldInit;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        if (Via.getAPI().getServerVersion().isKnown() && channel instanceof SocketChannel) {
            UserConnectionImpl info = new UserConnectionImpl((SocketChannel)channel);
            new ProtocolPipelineImpl(info);
            INIT_CHANNEL_METHOD.invoke(this.original, channel);
            SpongeEncodeHandler encoder = new SpongeEncodeHandler(info, (MessageToByteEncoder)channel.pipeline().get("encoder"));
            SpongeDecodeHandler decoder = new SpongeDecodeHandler(info, (ByteToMessageDecoder)channel.pipeline().get("decoder"));
            channel.pipeline().replace("encoder", "encoder", (ChannelHandler)encoder);
            channel.pipeline().replace("decoder", "decoder", (ChannelHandler)decoder);
        } else {
            INIT_CHANNEL_METHOD.invoke(this.original, channel);
        }
    }

    public ChannelInitializer<Channel> getOriginal() {
        return this.original;
    }

    @Override
    public ChannelInitializer<Channel> original() {
        return this.original;
    }

    static {
        try {
            INIT_CHANNEL_METHOD = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
            INIT_CHANNEL_METHOD.setAccessible(true);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}

