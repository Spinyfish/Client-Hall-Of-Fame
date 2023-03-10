/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.spongepowered.api.MinecraftVersion
 *  org.spongepowered.api.Sponge
 */
package com.viaversion.viaversion.sponge.platform;

import com.viaversion.viaversion.platform.LegacyViaInjector;
import com.viaversion.viaversion.platform.WrappedChannelInitializer;
import com.viaversion.viaversion.sponge.handlers.SpongeChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Method;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Sponge;

public class SpongeViaInjector
extends LegacyViaInjector {
    @Override
    public int getServerProtocolVersion() throws ReflectiveOperationException {
        MinecraftVersion version = Sponge.getPlatform().getMinecraftVersion();
        return (Integer)version.getClass().getDeclaredMethod("getProtocol", new Class[0]).invoke((Object)version, new Object[0]);
    }

    @Override
    protected @Nullable Object getServerConnection() throws ReflectiveOperationException {
        Class<?> serverClazz = Class.forName("net.minecraft.server.MinecraftServer");
        for (Method method : serverClazz.getDeclaredMethods()) {
            Object connection;
            if (!method.getReturnType().getSimpleName().equals("NetworkSystem") || method.getParameterTypes().length != 0 || (connection = method.invoke((Object)Sponge.getServer(), new Object[0])) == null) continue;
            return connection;
        }
        return null;
    }

    @Override
    protected WrappedChannelInitializer createChannelInitializer(ChannelInitializer<Channel> oldInitializer) {
        return new SpongeChannelInitializer(oldInitializer);
    }

    @Override
    protected void blame(ChannelHandler bootstrapAcceptor) {
        throw new RuntimeException("Unable to find core component 'childHandler', please check your plugins. Issue: " + bootstrapAcceptor.getClass().getName());
    }
}

