/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import java.util.Set;

public interface ChannelGroup
extends Set<Channel>,
Comparable<ChannelGroup> {
    public String name();

    public ChannelGroupFuture write(Object var1);

    public ChannelGroupFuture write(Object var1, ChannelMatcher var2);

    public ChannelGroup flush();

    public ChannelGroup flush(ChannelMatcher var1);

    public ChannelGroupFuture writeAndFlush(Object var1);

    @Deprecated
    public ChannelGroupFuture flushAndWrite(Object var1);

    public ChannelGroupFuture writeAndFlush(Object var1, ChannelMatcher var2);

    @Deprecated
    public ChannelGroupFuture flushAndWrite(Object var1, ChannelMatcher var2);

    public ChannelGroupFuture disconnect();

    public ChannelGroupFuture disconnect(ChannelMatcher var1);

    public ChannelGroupFuture close();

    public ChannelGroupFuture close(ChannelMatcher var1);

    @Deprecated
    public ChannelGroupFuture deregister();

    @Deprecated
    public ChannelGroupFuture deregister(ChannelMatcher var1);
}

