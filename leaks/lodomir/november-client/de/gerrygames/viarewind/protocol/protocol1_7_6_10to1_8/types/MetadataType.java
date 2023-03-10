/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.types.minecraft.MetaTypeTemplate;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.MetaType1_7_6_10;
import io.netty.buffer.ByteBuf;

public class MetadataType
extends MetaTypeTemplate {
    @Override
    public Metadata read(ByteBuf buffer) throws Exception {
        byte item = buffer.readByte();
        if (item == 127) {
            return null;
        }
        int typeID = (item & 0xE0) >> 5;
        MetaType1_7_6_10 type = MetaType1_7_6_10.byId(typeID);
        int id = item & 0x1F;
        return new Metadata(id, type, type.type().read(buffer));
    }

    @Override
    public void write(ByteBuf buffer, Metadata meta) throws Exception {
        int item = (meta.metaType().typeId() << 5 | meta.id() & 0x1F) & 0xFF;
        buffer.writeByte(item);
        meta.metaType().type().write(buffer, meta.getValue());
    }
}

