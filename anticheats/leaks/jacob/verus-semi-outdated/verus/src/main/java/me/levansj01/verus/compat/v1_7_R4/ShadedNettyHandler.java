package me.levansj01.verus.compat.v1_7_R4;

import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import me.levansj01.verus.compat.netty.NettyHandler;
import me.levansj01.verus.data.bytes.ByteData;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelOutboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelPipeline;
import net.minecraft.util.io.netty.channel.ChannelPromise;
import net.minecraft.util.io.netty.util.concurrent.DefaultThreadFactory;

public class ShadedNettyHandler
implements NettyHandler {
    public void inject(Channel channel, Consumer<Object> consumer) {
        channel.eventLoop().execute(() -> {
            ChannelPipeline channelPipeline = channel.pipeline();
            if (channelPipeline.get("decoder") != null && channelPipeline.get("packet_handler") != null) {
                channelPipeline.addAfter("decoder", "verus_handler_in", (ChannelHandler)new PacketHandlerIn(consumer));
                channelPipeline.addBefore("packet_handler", "verus_handler_out", (ChannelHandler)new PacketHandlerOut(consumer));
            }
        });
    }

    public void uninject(Channel channel) {
        channel.eventLoop().execute(() -> {
            ChannelPipeline channelPipeline = channel.pipeline();
            if (channelPipeline.get("verus_handler_in") != null) {
                channelPipeline.remove("verus_handler_in");
            }
            if (channelPipeline.get("verus_handler_out") != null) {
                channelPipeline.remove("verus_handler_out");
            }
        });
    }

    @Override
    public ByteData wrap(byte[] byArray, int n, int n2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ThreadFactory newThreadFactory(String string, boolean bl, int n) {
        return new DefaultThreadFactory(string, bl, n);
    }

    @Override
    public ByteData of(int n) {
        throw new UnsupportedOperationException();
    }

    public static class PacketHandlerIn
    extends ChannelInboundHandlerAdapter {
        private final Consumer<Object> listener;

        public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
            super.channelRead(channelHandlerContext, object);
            this.listener.accept(object);
        }

        public PacketHandlerIn(Consumer<Object> consumer) {
            this.listener = consumer;
        }
    }

    public static class PacketHandlerOut
    extends ChannelOutboundHandlerAdapter {
        private final Consumer<Object> listener;

        public void write(ChannelHandlerContext channelHandlerContext, Object object, ChannelPromise channelPromise) throws Exception {
            super.write(channelHandlerContext, object, channelPromise);
            this.listener.accept(object);
        }

        public PacketHandlerOut(Consumer<Object> consumer) {
            this.listener = consumer;
        }
    }
}

