/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.handlers;

import com.viaversion.viaversion.handlers.ViaCodecHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import java.net.SocketAddress;

public class ChannelHandlerContextWrapper
implements ChannelHandlerContext {
    private final ChannelHandlerContext base;
    private final ViaCodecHandler handler;

    public ChannelHandlerContextWrapper(ChannelHandlerContext base, ViaCodecHandler handler) {
        this.base = base;
        this.handler = handler;
    }

    @Override
    public Channel channel() {
        return this.base.channel();
    }

    @Override
    public EventExecutor executor() {
        return this.base.executor();
    }

    @Override
    public String name() {
        return this.base.name();
    }

    @Override
    public ChannelHandler handler() {
        return this.base.handler();
    }

    @Override
    public boolean isRemoved() {
        return this.base.isRemoved();
    }

    @Override
    public ChannelHandlerContext fireChannelRegistered() {
        this.base.fireChannelRegistered();
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelUnregistered() {
        this.base.fireChannelUnregistered();
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelActive() {
        this.base.fireChannelActive();
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelInactive() {
        this.base.fireChannelInactive();
        return this;
    }

    @Override
    public ChannelHandlerContext fireExceptionCaught(Throwable throwable) {
        this.base.fireExceptionCaught(throwable);
        return this;
    }

    @Override
    public ChannelHandlerContext fireUserEventTriggered(Object o) {
        this.base.fireUserEventTriggered(o);
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelRead(Object o) {
        this.base.fireChannelRead(o);
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelReadComplete() {
        this.base.fireChannelReadComplete();
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelWritabilityChanged() {
        this.base.fireChannelWritabilityChanged();
        return this;
    }

    @Override
    public ChannelFuture bind(SocketAddress socketAddress) {
        return this.base.bind(socketAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress) {
        return this.base.connect(socketAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1) {
        return this.base.connect(socketAddress, socketAddress1);
    }

    @Override
    public ChannelFuture disconnect() {
        return this.base.disconnect();
    }

    @Override
    public ChannelFuture close() {
        return this.base.close();
    }

    @Override
    public ChannelFuture deregister() {
        return this.base.deregister();
    }

    @Override
    public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise channelPromise) {
        return this.base.bind(socketAddress, channelPromise);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, ChannelPromise channelPromise) {
        return this.base.connect(socketAddress, channelPromise);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
        return this.base.connect(socketAddress, socketAddress1, channelPromise);
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise channelPromise) {
        return this.base.disconnect(channelPromise);
    }

    @Override
    public ChannelFuture close(ChannelPromise channelPromise) {
        return this.base.close(channelPromise);
    }

    @Override
    public ChannelFuture deregister(ChannelPromise channelPromise) {
        return this.base.deregister(channelPromise);
    }

    @Override
    public ChannelHandlerContext read() {
        this.base.read();
        return this;
    }

    @Override
    public ChannelFuture write(Object o) {
        if (o instanceof ByteBuf && this.transform((ByteBuf)o)) {
            return this.base.newFailedFuture(new Throwable());
        }
        return this.base.write(o);
    }

    @Override
    public ChannelFuture write(Object o, ChannelPromise channelPromise) {
        if (o instanceof ByteBuf && this.transform((ByteBuf)o)) {
            return this.base.newFailedFuture(new Throwable());
        }
        return this.base.write(o, channelPromise);
    }

    public boolean transform(ByteBuf buf) {
        try {
            this.handler.transform(buf);
            return false;
        }
        catch (Exception e) {
            try {
                this.handler.exceptionCaught(this.base, e);
            }
            catch (Exception e1) {
                this.base.fireExceptionCaught(e1);
            }
            return true;
        }
    }

    @Override
    public ChannelHandlerContext flush() {
        this.base.flush();
        return this;
    }

    @Override
    public ChannelFuture writeAndFlush(Object o, ChannelPromise channelPromise) {
        ChannelFuture future = this.write(o, channelPromise);
        this.flush();
        return future;
    }

    @Override
    public ChannelFuture writeAndFlush(Object o) {
        ChannelFuture future = this.write(o);
        this.flush();
        return future;
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.base.pipeline();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.base.alloc();
    }

    @Override
    public ChannelPromise newPromise() {
        return this.base.newPromise();
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return this.base.newProgressivePromise();
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return this.base.newSucceededFuture();
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable throwable) {
        return this.base.newFailedFuture(throwable);
    }

    @Override
    public ChannelPromise voidPromise() {
        return this.base.voidPromise();
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
        return this.base.attr(attributeKey);
    }
}

