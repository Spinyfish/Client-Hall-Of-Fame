/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.group;

import io.netty.channel.Channel;

public interface ChannelMatcher {
    public boolean matches(Channel var1);
}

