/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.platform.WrappedChannelInitializer;
import com.viaversion.viaversion.util.Pair;
import com.viaversion.viaversion.util.ReflectionUtil;
import com.viaversion.viaversion.util.SynchronizedListWrapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class LegacyViaInjector
implements ViaInjector {
    protected final List<ChannelFuture> injectedFutures = new ArrayList<ChannelFuture>();
    protected final List<Pair<Field, Object>> injectedLists = new ArrayList<Pair<Field, Object>>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void inject() throws ReflectiveOperationException {
        Object connection = this.getServerConnection();
        if (connection == null) {
            throw new RuntimeException("Failed to find the core component 'ServerConnection'");
        }
        for (Field field : connection.getClass().getDeclaredFields()) {
            if (!List.class.isAssignableFrom(field.getType()) || !field.getGenericType().getTypeName().contains(ChannelFuture.class.getName())) continue;
            field.setAccessible(true);
            List list = (List)field.get(connection);
            SynchronizedListWrapper<Object> wrappedList = new SynchronizedListWrapper<Object>(list, o -> {
                try {
                    this.injectChannelFuture((ChannelFuture)o);
                }
                catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            });
            List list2 = list;
            synchronized (list2) {
                for (ChannelFuture future : list) {
                    this.injectChannelFuture(future);
                }
                field.set(connection, wrappedList);
            }
            this.injectedLists.add(new Pair<Field, Object>(field, connection));
        }
    }

    private void injectChannelFuture(ChannelFuture future) throws ReflectiveOperationException {
        List<String> names = future.channel().pipeline().names();
        ChannelHandler bootstrapAcceptor = null;
        for (String name : names) {
            ChannelHandler handler = future.channel().pipeline().get(name);
            try {
                ReflectionUtil.get(handler, "childHandler", ChannelInitializer.class);
                bootstrapAcceptor = handler;
                break;
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
            }
        }
        if (bootstrapAcceptor == null) {
            bootstrapAcceptor = future.channel().pipeline().first();
        }
        try {
            ChannelInitializer oldInitializer = ReflectionUtil.get(bootstrapAcceptor, "childHandler", ChannelInitializer.class);
            ReflectionUtil.set(bootstrapAcceptor, "childHandler", this.createChannelInitializer(oldInitializer));
            this.injectedFutures.add(future);
        }
        catch (NoSuchFieldException ignored) {
            this.blame(bootstrapAcceptor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void uninject() throws ReflectiveOperationException {
        for (ChannelFuture channelFuture : this.injectedFutures) {
            List<String> names = channelFuture.channel().pipeline().names();
            ChannelHandler bootstrapAcceptor = null;
            for (String name : names) {
                ChannelHandler handler = channelFuture.channel().pipeline().get(name);
                try {
                    if (!(ReflectionUtil.get(handler, "childHandler", ChannelInitializer.class) instanceof WrappedChannelInitializer)) continue;
                    bootstrapAcceptor = handler;
                    break;
                }
                catch (ReflectiveOperationException reflectiveOperationException) {
                }
            }
            if (bootstrapAcceptor == null) {
                bootstrapAcceptor = channelFuture.channel().pipeline().first();
            }
            try {
                ChannelInitializer initializer = ReflectionUtil.get(bootstrapAcceptor, "childHandler", ChannelInitializer.class);
                if (!(initializer instanceof WrappedChannelInitializer)) continue;
                ReflectionUtil.set(bootstrapAcceptor, "childHandler", ((WrappedChannelInitializer)((Object)initializer)).original());
            }
            catch (Exception e) {
                Via.getPlatform().getLogger().severe("Failed to remove injection handler, reload won't work with connections, please reboot!");
                e.printStackTrace();
            }
        }
        this.injectedFutures.clear();
        for (Pair pair : this.injectedLists) {
            try {
                List originalList;
                Field field = (Field)pair.key();
                Object o = field.get(pair.value());
                if (!(o instanceof SynchronizedListWrapper)) continue;
                List list = originalList = ((SynchronizedListWrapper)o).originalList();
                synchronized (list) {
                    field.set(pair.value(), originalList);
                }
            }
            catch (ReflectiveOperationException e) {
                Via.getPlatform().getLogger().severe("Failed to remove injection, reload won't work with connections, please reboot!");
            }
        }
        this.injectedLists.clear();
    }

    @Override
    public boolean lateProtocolVersionSetting() {
        return true;
    }

    @Override
    public JsonObject getDump() {
        JsonObject data = new JsonObject();
        JsonArray injectedChannelInitializers = new JsonArray();
        data.add("injectedChannelInitializers", injectedChannelInitializers);
        for (ChannelFuture future : this.injectedFutures) {
            JsonObject futureInfo = new JsonObject();
            injectedChannelInitializers.add(futureInfo);
            futureInfo.addProperty("futureClass", future.getClass().getName());
            futureInfo.addProperty("channelClass", future.channel().getClass().getName());
            JsonArray pipeline = new JsonArray();
            futureInfo.add("pipeline", pipeline);
            for (String pipeName : future.channel().pipeline().names()) {
                JsonObject handlerInfo = new JsonObject();
                pipeline.add(handlerInfo);
                handlerInfo.addProperty("name", pipeName);
                ChannelHandler channelHandler = future.channel().pipeline().get(pipeName);
                if (channelHandler == null) {
                    handlerInfo.addProperty("status", "INVALID");
                    continue;
                }
                handlerInfo.addProperty("class", channelHandler.getClass().getName());
                try {
                    ChannelInitializer child = ReflectionUtil.get(channelHandler, "childHandler", ChannelInitializer.class);
                    handlerInfo.addProperty("childClass", child.getClass().getName());
                    if (!(child instanceof WrappedChannelInitializer)) continue;
                    handlerInfo.addProperty("oldInit", ((WrappedChannelInitializer)((Object)child)).original().getClass().getName());
                }
                catch (ReflectiveOperationException reflectiveOperationException) {}
            }
        }
        JsonObject wrappedLists = new JsonObject();
        JsonObject currentLists = new JsonObject();
        try {
            for (Pair<Field, Object> pair : this.injectedLists) {
                Field field = pair.key();
                Object list = field.get(pair.value());
                currentLists.addProperty(field.getName(), list.getClass().getName());
                if (!(list instanceof SynchronizedListWrapper)) continue;
                wrappedLists.addProperty(field.getName(), ((SynchronizedListWrapper)list).originalList().getClass().getName());
            }
            data.add("wrappedLists", wrappedLists);
            data.add("currentLists", currentLists);
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
        return data;
    }

    @Override
    public String getEncoderName() {
        return "encoder";
    }

    @Override
    public String getDecoderName() {
        return "decoder";
    }

    protected abstract @Nullable Object getServerConnection() throws ReflectiveOperationException;

    protected abstract WrappedChannelInitializer createChannelInitializer(ChannelInitializer<Channel> var1);

    protected abstract void blame(ChannelHandler var1) throws ReflectiveOperationException;
}

