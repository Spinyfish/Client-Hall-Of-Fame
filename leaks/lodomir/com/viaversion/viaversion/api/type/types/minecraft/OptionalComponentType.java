/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import io.netty.buffer.ByteBuf;

public class OptionalComponentType
extends Type<JsonElement> {
    public OptionalComponentType() {
        super(JsonElement.class);
    }

    @Override
    public JsonElement read(ByteBuf buffer) throws Exception {
        boolean present = buffer.readBoolean();
        return present ? (JsonElement)Type.COMPONENT.read(buffer) : null;
    }

    @Override
    public void write(ByteBuf buffer, JsonElement object) throws Exception {
        if (object == null) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            Type.COMPONENT.write(buffer, object);
        }
    }
}

