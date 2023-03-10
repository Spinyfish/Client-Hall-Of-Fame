/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_17_1to1_17;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.StringType;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_17_1to1_17.ClientboundPackets1_17_1;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;

public final class Protocol1_17_1To1_17
extends AbstractProtocol<ClientboundPackets1_17, ClientboundPackets1_17_1, ServerboundPackets1_17, ServerboundPackets1_17> {
    private static final StringType PAGE_STRING_TYPE = new StringType(8192);
    private static final StringType TITLE_STRING_TYPE = new StringType(128);

    public Protocol1_17_1To1_17() {
        super(ClientboundPackets1_17.class, ClientboundPackets1_17_1.class, ServerboundPackets1_17.class, ServerboundPackets1_17.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPackets1_17.REMOVE_ENTITY, ClientboundPackets1_17_1.REMOVE_ENTITIES, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int entityId = wrapper.read(Type.VAR_INT);
                    wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{entityId});
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_17.SET_SLOT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.create(Type.VAR_INT, 0);
            }
        });
        this.registerClientbound(ClientboundPackets1_17.WINDOW_ITEMS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.create(Type.VAR_INT, 0);
                this.handler(wrapper -> {
                    wrapper.write(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT, wrapper.read(Type.FLAT_VAR_INT_ITEM_ARRAY));
                    wrapper.write(Type.FLAT_VAR_INT_ITEM, null);
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_17.CLICK_WINDOW, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.read(Type.VAR_INT);
            }
        });
        this.registerServerbound(ServerboundPackets1_17.EDIT_BOOK, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    CompoundTag tag = new CompoundTag();
                    DataItem item = new DataItem(942, 1, 0, tag);
                    wrapper.write(Type.FLAT_VAR_INT_ITEM, item);
                    int slot = wrapper.read(Type.VAR_INT);
                    int pages = wrapper.read(Type.VAR_INT);
                    ListTag pagesTag = new ListTag(StringTag.class);
                    for (int i = 0; i < pages; ++i) {
                        String page = wrapper.read(PAGE_STRING_TYPE);
                        pagesTag.add(new StringTag(page));
                    }
                    if (pagesTag.size() == 0) {
                        pagesTag.add(new StringTag(""));
                    }
                    tag.put("pages", pagesTag);
                    if (wrapper.read(Type.BOOLEAN).booleanValue()) {
                        String title = wrapper.read(TITLE_STRING_TYPE);
                        tag.put("title", new StringTag(title));
                        tag.put("author", new StringTag(wrapper.user().getProtocolInfo().getUsername()));
                        wrapper.write(Type.BOOLEAN, true);
                    } else {
                        wrapper.write(Type.BOOLEAN, false);
                    }
                    wrapper.write(Type.VAR_INT, slot);
                });
            }
        });
    }
}

