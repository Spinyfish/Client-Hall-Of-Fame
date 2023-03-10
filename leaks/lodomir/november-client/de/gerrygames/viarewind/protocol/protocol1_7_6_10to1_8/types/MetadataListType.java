/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListTypeTemplate;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.MetaType1_7_6_10;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.MetadataType;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;

public class MetadataListType
extends MetaListTypeTemplate {
    private MetadataType metadataType = new MetadataType();

    @Override
    public List<Metadata> read(ByteBuf buffer) throws Exception {
        Metadata m;
        ArrayList<Metadata> list = new ArrayList<Metadata>();
        do {
            if ((m = (Metadata)Types1_7_6_10.METADATA.read(buffer)) == null) continue;
            list.add(m);
        } while (m != null);
        return list;
    }

    @Override
    public void write(ByteBuf buffer, List<Metadata> metadata) throws Exception {
        for (Metadata meta : metadata) {
            Types1_7_6_10.METADATA.write(buffer, meta);
        }
        if (metadata.isEmpty()) {
            Types1_7_6_10.METADATA.write(buffer, new Metadata(0, MetaType1_7_6_10.Byte, (byte)0));
        }
        buffer.writeByte(127);
    }
}

