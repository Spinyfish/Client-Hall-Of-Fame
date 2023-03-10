/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.protocol;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;

public interface SimpleProtocol
extends Protocol<DummyPacketTypes, DummyPacketTypes, DummyPacketTypes, DummyPacketTypes> {

    public static enum DummyPacketTypes implements ClientboundPacketType,
    ServerboundPacketType
    {


        @Override
        public int getId() {
            return this.ordinal();
        }

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public Direction direction() {
            throw new UnsupportedOperationException();
        }
    }
}

