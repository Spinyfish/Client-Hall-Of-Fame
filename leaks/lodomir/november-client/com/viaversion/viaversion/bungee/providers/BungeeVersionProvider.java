/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.md_5.bungee.api.ProxyServer
 */
package com.viaversion.viaversion.bungee.providers;

import com.google.common.collect.Lists;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import com.viaversion.viaversion.util.ReflectionUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.ProxyServer;

public class BungeeVersionProvider
extends BaseVersionProvider {
    private static Class<?> ref;

    @Override
    public int getClosestServerProtocol(UserConnection user) throws Exception {
        if (ref == null) {
            return super.getClosestServerProtocol(user);
        }
        List list = ReflectionUtil.getStatic(ref, "SUPPORTED_VERSION_IDS", List.class);
        ArrayList sorted = new ArrayList(list);
        Collections.sort(sorted);
        ProtocolInfo info = user.getProtocolInfo();
        if (sorted.contains(info.getProtocolVersion())) {
            return info.getProtocolVersion();
        }
        if (info.getProtocolVersion() < (Integer)sorted.get(0)) {
            return BungeeVersionProvider.getLowestSupportedVersion();
        }
        for (Integer protocol : Lists.reverse(sorted)) {
            if (info.getProtocolVersion() <= protocol || !ProtocolVersion.isRegistered(protocol)) continue;
            return protocol;
        }
        Via.getPlatform().getLogger().severe("Panic, no protocol id found for " + info.getProtocolVersion());
        return info.getProtocolVersion();
    }

    public static int getLowestSupportedVersion() {
        try {
            List list = ReflectionUtil.getStatic(ref, "SUPPORTED_VERSION_IDS", List.class);
            return (Integer)list.get(0);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return ProxyServer.getInstance().getProtocolVersion();
    }

    static {
        try {
            ref = Class.forName("net.md_5.bungee.protocol.ProtocolConstants");
        }
        catch (Exception e) {
            Via.getPlatform().getLogger().severe("Could not detect the ProtocolConstants class");
            e.printStackTrace();
        }
    }
}

