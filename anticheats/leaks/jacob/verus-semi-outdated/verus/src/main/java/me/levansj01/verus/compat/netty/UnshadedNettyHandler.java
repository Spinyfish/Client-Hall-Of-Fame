package me.levansj01.verus.compat.netty;

import java.util.function.*;
import me.levansj01.verus.data.bytes.*;
import io.netty.buffer.*;
import java.util.concurrent.*;
import io.netty.util.concurrent.*;
import io.netty.channel.*;

public class UnshadedNettyHandler implements NettyHandler
{
    @Override
    public void uninject(final Object o) {
        this.uninject((Channel)o);
    }
    
    @Override
    public void inject(final Object o, final Consumer consumer) {
        this.inject((Channel)o, consumer);
    }
    
    public void uninject(final Channel channel) {
        channel.eventLoop().execute(UnshadedNettyHandler::lambda.uninject.1);
    }
    
    @Override
    public ByteData wrap(final byte[] array, final int n, final int n2) {
        return (ByteData)new UnshadedByteData(Unpooled.wrappedBuffer(array, n, n2));
    }
    
    @Override
    public ByteData of(final int n) {
        return (ByteData)new UnshadedByteData(Unpooled.directBuffer(n, n).setZero(0, n));
    }
    
    public void inject(final Channel channel, final Consumer consumer) {
        channel.eventLoop().execute(UnshadedNettyHandler::lambda.inject.0);
    }
    
    private static void lambda.uninject.1(final Channel channel) {
        final ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get("verus_handler_in") != null) {
            pipeline.remove("verus_handler_in");
        }
        if (pipeline.get("verus_handler_out") != null) {
            pipeline.remove("verus_handler_out");
        }
    }
    
    @Override
    public ThreadFactory newThreadFactory(final String s, final boolean b, final int n) {
        return (ThreadFactory)new DefaultThreadFactory(s, b, n);
    }
    
    private static void lambda.inject.0(final Channel channel, final Consumer consumer) {
        final ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get("decoder") != null && pipeline.get("packet_handler") != null) {
            pipeline.addAfter("decoder", "verus_handler_in", (ChannelHandler)new PacketHandlerIn(consumer));
            pipeline.addBefore("packet_handler", "verus_handler_out", (ChannelHandler)new PacketHandlerOut(consumer));
        }
    }
    
    public static class PacketHandlerIn extends ChannelInboundHandlerAdapter
    {
        private final Consumer listener;
        
        public void channelRead(final ChannelHandlerContext channelHandlerContext, final Object o) throws Exception {
            super.channelRead(channelHandlerContext, o);
            this.listener.accept(o);
        }
        
        public PacketHandlerIn(final Consumer listener) {
            this.listener = listener;
        }
    }
    
    public static class PacketHandlerOut extends ChannelOutboundHandlerAdapter
    {
        private final Consumer listener;
        
        public PacketHandlerOut(final Consumer listener) {
            this.listener = listener;
        }
        
        public void write(final ChannelHandlerContext channelHandlerContext, final Object o, final ChannelPromise channelPromise) throws Exception {
            super.write(channelHandlerContext, o, channelPromise);
            this.listener.accept(o);
        }
    }
}
