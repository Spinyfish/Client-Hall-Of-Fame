/*
 * Decompiled with CFR 0.152.
 */
package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.UniqueName;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class ServerBootstrap
extends AbstractBootstrap<ServerBootstrap, ServerChannel> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ServerBootstrap.class);
    private final Map<ChannelOption<?>, Object> childOptions = new LinkedHashMap();
    private final Map<AttributeKey<?>, Object> childAttrs = new LinkedHashMap();
    private volatile EventLoopGroup childGroup;
    private volatile ChannelHandler childHandler;

    public ServerBootstrap() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ServerBootstrap(ServerBootstrap bootstrap) {
        super(bootstrap);
        this.childGroup = bootstrap.childGroup;
        this.childHandler = bootstrap.childHandler;
        Map<UniqueName, Object> map = bootstrap.childOptions;
        synchronized (map) {
            this.childOptions.putAll(bootstrap.childOptions);
        }
        map = bootstrap.childAttrs;
        synchronized (map) {
            this.childAttrs.putAll(bootstrap.childAttrs);
        }
    }

    @Override
    public ServerBootstrap group(EventLoopGroup group) {
        return this.group(group, group);
    }

    public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        super.group(parentGroup);
        if (childGroup == null) {
            throw new NullPointerException("childGroup");
        }
        if (this.childGroup != null) {
            throw new IllegalStateException("childGroup set already");
        }
        this.childGroup = childGroup;
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value) {
        if (childOption == null) {
            throw new NullPointerException("childOption");
        }
        if (value == null) {
            Map<ChannelOption<?>, Object> map = this.childOptions;
            synchronized (map) {
                this.childOptions.remove(childOption);
            }
        }
        Map<ChannelOption<?>, Object> map = this.childOptions;
        synchronized (map) {
            this.childOptions.put(childOption, value);
        }
        return this;
    }

    public <T> ServerBootstrap childAttr(AttributeKey<T> childKey, T value) {
        if (childKey == null) {
            throw new NullPointerException("childKey");
        }
        if (value == null) {
            this.childAttrs.remove(childKey);
        } else {
            this.childAttrs.put(childKey, value);
        }
        return this;
    }

    public ServerBootstrap childHandler(ChannelHandler childHandler) {
        if (childHandler == null) {
            throw new NullPointerException("childHandler");
        }
        this.childHandler = childHandler;
        return this;
    }

    public EventLoopGroup childGroup() {
        return this.childGroup;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void init(Channel channel) throws Exception {
        Map.Entry[] currentChildAttrs;
        Map.Entry[] currentChildOptions;
        Map<AttributeKey<?>, Object> attrs;
        Map<ChannelOption<?>, Object> options;
        Map<ChannelOption<?>, Object> map = options = this.options();
        synchronized (map) {
            channel.config().setOptions(options);
        }
        Map<AttributeKey<?>, Object> map2 = attrs = this.attrs();
        synchronized (map2) {
            for (Map.Entry<AttributeKey<?>, Object> e : attrs.entrySet()) {
                AttributeKey<?> key = e.getKey();
                channel.attr(key).set(e.getValue());
            }
        }
        ChannelPipeline p = channel.pipeline();
        if (this.handler() != null) {
            p.addLast(this.handler());
        }
        final EventLoopGroup currentChildGroup = this.childGroup;
        final ChannelHandler currentChildHandler = this.childHandler;
        Map<UniqueName, Object> map3 = this.childOptions;
        synchronized (map3) {
            currentChildOptions = this.childOptions.entrySet().toArray(ServerBootstrap.newOptionArray(this.childOptions.size()));
        }
        map3 = this.childAttrs;
        synchronized (map3) {
            currentChildAttrs = this.childAttrs.entrySet().toArray(ServerBootstrap.newAttrArray(this.childAttrs.size()));
        }
        p.addLast(new ChannelInitializer<Channel>(){

            @Override
            public void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new ServerBootstrapAcceptor(currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
            }
        });
    }

    @Override
    public ServerBootstrap validate() {
        super.validate();
        if (this.childHandler == null) {
            throw new IllegalStateException("childHandler not set");
        }
        if (this.childGroup == null) {
            logger.warn("childGroup is not set. Using parentGroup instead.");
            this.childGroup = this.group();
        }
        return this;
    }

    private static Map.Entry<ChannelOption<?>, Object>[] newOptionArray(int size) {
        return new Map.Entry[size];
    }

    private static Map.Entry<AttributeKey<?>, Object>[] newAttrArray(int size) {
        return new Map.Entry[size];
    }

    @Override
    public ServerBootstrap clone() {
        return new ServerBootstrap(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(super.toString());
        buf.setLength(buf.length() - 1);
        buf.append(", ");
        if (this.childGroup != null) {
            buf.append("childGroup: ");
            buf.append(StringUtil.simpleClassName(this.childGroup));
            buf.append(", ");
        }
        Map<UniqueName, Object> map = this.childOptions;
        synchronized (map) {
            if (!this.childOptions.isEmpty()) {
                buf.append("childOptions: ");
                buf.append(this.childOptions);
                buf.append(", ");
            }
        }
        map = this.childAttrs;
        synchronized (map) {
            if (!this.childAttrs.isEmpty()) {
                buf.append("childAttrs: ");
                buf.append(this.childAttrs);
                buf.append(", ");
            }
        }
        if (this.childHandler != null) {
            buf.append("childHandler: ");
            buf.append(this.childHandler);
            buf.append(", ");
        }
        if (buf.charAt(buf.length() - 1) == '(') {
            buf.append(')');
        } else {
            buf.setCharAt(buf.length() - 2, ')');
            buf.setLength(buf.length() - 1);
        }
        return buf.toString();
    }

    private static class ServerBootstrapAcceptor
    extends ChannelInboundHandlerAdapter {
        private final EventLoopGroup childGroup;
        private final ChannelHandler childHandler;
        private final Map.Entry<ChannelOption<?>, Object>[] childOptions;
        private final Map.Entry<AttributeKey<?>, Object>[] childAttrs;

        ServerBootstrapAcceptor(EventLoopGroup childGroup, ChannelHandler childHandler, Map.Entry<ChannelOption<?>, Object>[] childOptions, Map.Entry<AttributeKey<?>, Object>[] childAttrs) {
            this.childGroup = childGroup;
            this.childHandler = childHandler;
            this.childOptions = childOptions;
            this.childAttrs = childAttrs;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            final Channel child = (Channel)msg;
            child.pipeline().addLast(this.childHandler);
            for (Map.Entry<ChannelOption<?>, Object> entry : this.childOptions) {
                try {
                    if (child.config().setOption(entry.getKey(), entry.getValue())) continue;
                    logger.warn("Unknown channel option: " + entry);
                }
                catch (Throwable t) {
                    logger.warn("Failed to set a channel option: " + child, t);
                }
            }
            for (Map.Entry<UniqueName, Object> entry : this.childAttrs) {
                child.attr((AttributeKey)entry.getKey()).set(entry.getValue());
            }
            try {
                this.childGroup.register(child).addListener(new ChannelFutureListener(){

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            ServerBootstrapAcceptor.forceClose(child, future.cause());
                        }
                    }
                });
            }
            catch (Throwable t) {
                ServerBootstrapAcceptor.forceClose(child, t);
            }
        }

        private static void forceClose(Channel child, Throwable t) {
            child.unsafe().closeForcibly();
            logger.warn("Failed to register an accepted channel: " + child, t);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final ChannelConfig config = ctx.channel().config();
            if (config.isAutoRead()) {
                config.setAutoRead(false);
                ctx.channel().eventLoop().schedule(new Runnable(){

                    @Override
                    public void run() {
                        config.setAutoRead(true);
                    }
                }, 1L, TimeUnit.SECONDS);
            }
            ctx.fireExceptionCaught(cause);
        }
    }
}

