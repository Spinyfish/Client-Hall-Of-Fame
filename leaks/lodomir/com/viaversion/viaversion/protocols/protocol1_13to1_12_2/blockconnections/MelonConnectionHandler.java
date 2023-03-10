/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.AbstractStempConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;

public class MelonConnectionHandler
extends AbstractStempConnectionHandler {
    public MelonConnectionHandler(String baseStateId) {
        super(baseStateId);
    }

    static ConnectionData.ConnectorInitAction init() {
        return new MelonConnectionHandler("minecraft:melon_stem[age=7]").getInitAction("minecraft:melon", "minecraft:attached_melon_stem");
    }
}

