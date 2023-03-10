/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.base;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;

public enum ClientboundLoginPackets implements ClientboundPacketType
{
    LOGIN_DISCONNECT,
    HELLO,
    GAME_PROFILE,
    LOGIN_COMPRESSION,
    CUSTOM_QUERY;


    @Override
    public final int getId() {
        return this.ordinal();
    }

    @Override
    public final String getName() {
        return this.name();
    }

    @Override
    public final State state() {
        return State.LOGIN;
    }
}

