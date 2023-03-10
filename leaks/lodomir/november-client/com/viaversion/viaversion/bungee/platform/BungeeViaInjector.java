/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ProxyServer
 */
package com.viaversion.viaversion.bungee.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.bungee.handlers.BungeeChannelInitializer;
import com.viaversion.viaversion.libs.fastutil.ints.IntLinkedOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.util.ReflectionUtil;
import com.viaversion.viaversion.util.SetWrapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.md_5.bungee.api.ProxyServer;

public class BungeeViaInjector
implements ViaInjector {
    private static final Field LISTENERS_FIELD;
    private final List<Channel> injectedChannels = new ArrayList<Channel>();

    @Override
    public void inject() throws ReflectiveOperationException {
        Set listeners = (Set)LISTENERS_FIELD.get(ProxyServer.getInstance());
        SetWrapper<Channel> wrapper = new SetWrapper<Channel>(listeners, channel -> {
            try {
                this.injectChannel((Channel)channel);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        LISTENERS_FIELD.set(ProxyServer.getInstance(), wrapper);
        for (Channel channel2 : listeners) {
            this.injectChannel(channel2);
        }
    }

    @Override
    public void uninject() {
        Via.getPlatform().getLogger().severe("ViaVersion cannot remove itself from Bungee without a reboot!");
    }

    private void injectChannel(Channel channel) throws ReflectiveOperationException {
        List<String> names = channel.pipeline().names();
        Object bootstrapAcceptor = null;
        for (String name : names) {
            ChannelHandler handler = channel.pipeline().get(name);
            try {
                ReflectionUtil.get(handler, "childHandler", ChannelInitializer.class);
                bootstrapAcceptor = handler;
            }
            catch (Exception exception) {}
        }
        if (bootstrapAcceptor == null) {
            bootstrapAcceptor = channel.pipeline().first();
        }
        if (bootstrapAcceptor.getClass().getName().equals("net.md_5.bungee.query.QueryHandler")) {
            return;
        }
        try {
            ChannelInitializer oldInit = ReflectionUtil.get(bootstrapAcceptor, "childHandler", ChannelInitializer.class);
            BungeeChannelInitializer newInit = new BungeeChannelInitializer(oldInit);
            ReflectionUtil.set(bootstrapAcceptor, "childHandler", newInit);
            this.injectedChannels.add(channel);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException("Unable to find core component 'childHandler', please check your plugins. issue: " + bootstrapAcceptor.getClass().getName());
        }
    }

    @Override
    public int getServerProtocolVersion() throws Exception {
        return this.getBungeeSupportedVersions().get(0);
    }

    @Override
    public IntSortedSet getServerProtocolVersions() throws Exception {
        return new IntLinkedOpenHashSet(this.getBungeeSupportedVersions());
    }

    private List<Integer> getBungeeSupportedVersions() throws Exception {
        return ReflectionUtil.getStatic(Class.forName("net.md_5.bungee.protocol.ProtocolConstants"), "SUPPORTED_VERSION_IDS", List.class);
    }

    @Override
    public JsonObject getDump() {
        JsonObject data = new JsonObject();
        JsonArray injectedChannelInitializers = new JsonArray();
        for (Channel channel : this.injectedChannels) {
            JsonObject channelInfo = new JsonObject();
            channelInfo.addProperty("channelClass", channel.getClass().getName());
            JsonArray pipeline = new JsonArray();
            for (String pipeName : channel.pipeline().names()) {
                JsonObject handlerInfo = new JsonObject();
                handlerInfo.addProperty("name", pipeName);
                ChannelHandler channelHandler = channel.pipeline().get(pipeName);
                if (channelHandler == null) {
                    handlerInfo.addProperty("status", "INVALID");
                    continue;
                }
                handlerInfo.addProperty("class", channelHandler.getClass().getName());
                try {
                    ChannelInitializer child = ReflectionUtil.get(channelHandler, "childHandler", ChannelInitializer.class);
                    handlerInfo.addProperty("childClass", child.getClass().getName());
                    if (child instanceof BungeeChannelInitializer) {
                        handlerInfo.addProperty("oldInit", ((BungeeChannelInitializer)child).getOriginal().getClass().getName());
                    }
                }
                catch (ReflectiveOperationException reflectiveOperationException) {
                    // empty catch block
                }
                pipeline.add(handlerInfo);
            }
            channelInfo.add("pipeline", pipeline);
            injectedChannelInitializers.add(channelInfo);
        }
        data.add("injectedChannelInitializers", injectedChannelInitializers);
        try {
            Object list = LISTENERS_FIELD.get(ProxyServer.getInstance());
            data.addProperty("currentList", list.getClass().getName());
            if (list instanceof SetWrapper) {
                data.addProperty("wrappedList", ((SetWrapper)((Object)list)).originalSet().getClass().getName());
            }
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
        return data;
    }

    static {
        try {
            LISTENERS_FIELD = ProxyServer.getInstance().getClass().getDeclaredField("listeners");
            LISTENERS_FIELD.setAccessible(true);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to access listeners field.", e);
        }
    }
}

