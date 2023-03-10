/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.rewriter.CommandRewriter;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CommandRewriter1_14
extends CommandRewriter {
    public CommandRewriter1_14(Protocol protocol) {
        super(protocol);
    }

    @Override
    protected @Nullable String handleArgumentType(String argumentType) {
        if (argumentType.equals("minecraft:nbt")) {
            return "minecraft:nbt_compound_tag";
        }
        return super.handleArgumentType(argumentType);
    }
}

