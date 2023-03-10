/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import java.util.Collections;

public class PlayerPackets {
    public static void register(final Protocol protocol) {
        protocol.registerClientbound(ClientboundPackets1_13.OPEN_SIGN_EDITOR, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION, Type.POSITION1_14);
            }
        });
        protocol.registerServerbound(ServerboundPackets1_14.QUERY_BLOCK_NBT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.POSITION1_14, Type.POSITION);
            }
        });
        protocol.registerServerbound(ServerboundPackets1_14.EDIT_BOOK, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ListTag listTag;
                        Item item = wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                        protocol.getItemRewriter().handleItemToServer(item);
                        if (item == null) {
                            return;
                        }
                        CompoundTag tag = item.tag();
                        if (tag == null) {
                            return;
                        }
                        Object pages = tag.get("pages");
                        if (pages == null) {
                            tag.put("pages", new ListTag(Collections.singletonList(new StringTag())));
                        }
                        if (Via.getConfig().isTruncate1_14Books() && pages instanceof ListTag && (listTag = (ListTag)pages).size() > 50) {
                            listTag.setValue(listTag.getValue().subList(0, 50));
                        }
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_14.PLAYER_DIGGING, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.POSITION1_14, Type.POSITION);
            }
        });
        protocol.registerServerbound(ServerboundPackets1_14.RECIPE_BOOK_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int type = wrapper.get(Type.VAR_INT, 0);
                        if (type == 0) {
                            wrapper.passthrough(Type.STRING);
                        } else if (type == 1) {
                            wrapper.passthrough(Type.BOOLEAN);
                            wrapper.passthrough(Type.BOOLEAN);
                            wrapper.passthrough(Type.BOOLEAN);
                            wrapper.passthrough(Type.BOOLEAN);
                            wrapper.read(Type.BOOLEAN);
                            wrapper.read(Type.BOOLEAN);
                            wrapper.read(Type.BOOLEAN);
                            wrapper.read(Type.BOOLEAN);
                        }
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_14.UPDATE_COMMAND_BLOCK, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION1_14, Type.POSITION);
            }
        });
        protocol.registerServerbound(ServerboundPackets1_14.UPDATE_STRUCTURE_BLOCK, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION1_14, Type.POSITION);
            }
        });
        protocol.registerServerbound(ServerboundPackets1_14.UPDATE_SIGN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION1_14, Type.POSITION);
            }
        });
        protocol.registerServerbound(ServerboundPackets1_14.PLAYER_BLOCK_PLACEMENT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int hand = wrapper.read(Type.VAR_INT);
                        Position position = wrapper.read(Type.POSITION1_14);
                        int face = wrapper.read(Type.VAR_INT);
                        float x = wrapper.read(Type.FLOAT).floatValue();
                        float y = wrapper.read(Type.FLOAT).floatValue();
                        float z = wrapper.read(Type.FLOAT).floatValue();
                        wrapper.read(Type.BOOLEAN);
                        wrapper.write(Type.POSITION, position);
                        wrapper.write(Type.VAR_INT, face);
                        wrapper.write(Type.VAR_INT, hand);
                        wrapper.write(Type.FLOAT, Float.valueOf(x));
                        wrapper.write(Type.FLOAT, Float.valueOf(y));
                        wrapper.write(Type.FLOAT, Float.valueOf(z));
                    }
                });
            }
        });
    }
}

