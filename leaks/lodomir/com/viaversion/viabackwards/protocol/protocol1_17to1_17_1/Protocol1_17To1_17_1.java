/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_17to1_17_1;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.protocol.protocol1_17to1_17_1.storage.InventoryStateIds;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_17_1to1_17.ClientboundPackets1_17_1;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;

public final class Protocol1_17To1_17_1
extends BackwardsProtocol<ClientboundPackets1_17_1, ClientboundPackets1_17, ServerboundPackets1_17, ServerboundPackets1_17> {
    private static final int MAX_PAGE_LENGTH = 8192;
    private static final int MAX_TITLE_LENGTH = 128;
    private static final int MAX_PAGES = 200;

    public Protocol1_17To1_17_1() {
        super(ClientboundPackets1_17_1.class, ClientboundPackets1_17.class, ServerboundPackets1_17.class, ServerboundPackets1_17.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPackets1_17_1.REMOVE_ENTITIES, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int[] entityIds = wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
                    wrapper.cancel();
                    for (int entityId : entityIds) {
                        PacketWrapper newPacket = wrapper.create(ClientboundPackets1_17.REMOVE_ENTITY);
                        newPacket.write(Type.VAR_INT, entityId);
                        newPacket.send(Protocol1_17To1_17_1.class);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_17_1.CLOSE_WINDOW, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    short containerId = wrapper.passthrough(Type.UNSIGNED_BYTE);
                    wrapper.user().get(InventoryStateIds.class).removeStateId(containerId);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_17_1.SET_SLOT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    short containerId = wrapper.passthrough(Type.UNSIGNED_BYTE);
                    int stateId = wrapper.read(Type.VAR_INT);
                    wrapper.user().get(InventoryStateIds.class).setStateId(containerId, stateId);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_17_1.WINDOW_ITEMS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    short containerId = wrapper.passthrough(Type.UNSIGNED_BYTE);
                    int stateId = wrapper.read(Type.VAR_INT);
                    wrapper.user().get(InventoryStateIds.class).setStateId(containerId, stateId);
                    wrapper.write(Type.FLAT_VAR_INT_ITEM_ARRAY, wrapper.read(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT));
                    wrapper.read(Type.FLAT_VAR_INT_ITEM);
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_17.CLOSE_WINDOW, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    short containerId = wrapper.passthrough(Type.UNSIGNED_BYTE);
                    wrapper.user().get(InventoryStateIds.class).removeStateId(containerId);
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_17.CLICK_WINDOW, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    short containerId = wrapper.passthrough(Type.UNSIGNED_BYTE);
                    int stateId = wrapper.user().get(InventoryStateIds.class).removeStateId(containerId);
                    wrapper.write(Type.VAR_INT, stateId == Integer.MAX_VALUE ? 0 : stateId);
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_17.EDIT_BOOK, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    ListTag pagesTag;
                    Item item = wrapper.read(Type.FLAT_VAR_INT_ITEM);
                    boolean signing = wrapper.read(Type.BOOLEAN);
                    wrapper.passthrough(Type.VAR_INT);
                    CompoundTag tag = item.tag();
                    StringTag titleTag = null;
                    if (tag == null || (pagesTag = (ListTag)tag.get("pages")) == null || signing && (titleTag = (StringTag)tag.get("title")) == null) {
                        wrapper.write(Type.VAR_INT, 0);
                        wrapper.write(Type.BOOLEAN, false);
                        return;
                    }
                    if (pagesTag.size() > 200) {
                        pagesTag = new ListTag(pagesTag.getValue().subList(0, 200));
                    }
                    wrapper.write(Type.VAR_INT, pagesTag.size());
                    for (Tag pageTag : pagesTag) {
                        String page = ((StringTag)pageTag).getValue();
                        if (page.length() > 8192) {
                            page = page.substring(0, 8192);
                        }
                        wrapper.write(Type.STRING, page);
                    }
                    wrapper.write(Type.BOOLEAN, signing);
                    if (signing) {
                        String title;
                        if (titleTag == null) {
                            titleTag = (StringTag)tag.get("title");
                        }
                        if ((title = titleTag.getValue()).length() > 128) {
                            title = title.substring(0, 128);
                        }
                        wrapper.write(Type.STRING, title);
                    }
                });
            }
        });
    }

    @Override
    public void init(UserConnection connection) {
        connection.put(new InventoryStateIds());
    }
}

