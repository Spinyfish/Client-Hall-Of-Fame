/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 */
package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets;

import com.google.common.base.Joiner;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.ParticleMapping;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.TabCompleteStorage;
import com.viaversion.viabackwards.utils.ChatUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.InventoryPackets;
import com.viaversion.viaversion.rewriter.CommandRewriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerPacket1_13
extends RewriterBase<Protocol1_12_2To1_13> {
    private final CommandRewriter commandRewriter;

    public PlayerPacket1_13(Protocol1_12_2To1_13 protocol) {
        super(protocol);
        this.commandRewriter = new CommandRewriter(this.protocol){};
    }

    @Override
    protected void registerPackets() {
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(State.LOGIN, 4, -1, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(final PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.cancel();
                        packetWrapper.create(2, new PacketHandler(){

                            @Override
                            public void handle(PacketWrapper newWrapper) throws Exception {
                                newWrapper.write(Type.VAR_INT, packetWrapper.read(Type.VAR_INT));
                                newWrapper.write(Type.BOOLEAN, false);
                            }
                        }).sendToServer(Protocol1_12_2To1_13.class);
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.PLUGIN_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        String channel = wrapper.read(Type.STRING);
                        if (channel.equals("minecraft:trader_list")) {
                            wrapper.write(Type.STRING, "MC|TrList");
                            wrapper.passthrough(Type.INT);
                            int size = wrapper.passthrough(Type.UNSIGNED_BYTE).shortValue();
                            for (int i = 0; i < size; ++i) {
                                Item input = wrapper.read(Type.FLAT_ITEM);
                                wrapper.write(Type.ITEM, ((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol).getItemRewriter().handleItemToClient(input));
                                Item output = wrapper.read(Type.FLAT_ITEM);
                                wrapper.write(Type.ITEM, ((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol).getItemRewriter().handleItemToClient(output));
                                boolean secondItem = wrapper.passthrough(Type.BOOLEAN);
                                if (secondItem) {
                                    Item second = wrapper.read(Type.FLAT_ITEM);
                                    wrapper.write(Type.ITEM, ((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol).getItemRewriter().handleItemToClient(second));
                                }
                                wrapper.passthrough(Type.BOOLEAN);
                                wrapper.passthrough(Type.INT);
                                wrapper.passthrough(Type.INT);
                            }
                        } else {
                            String oldChannel = InventoryPackets.getOldPluginChannelId(channel);
                            if (oldChannel == null) {
                                if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                                    ViaBackwards.getPlatform().getLogger().warning("Ignoring outgoing plugin message with channel: " + channel);
                                }
                                wrapper.cancel();
                                return;
                            }
                            wrapper.write(Type.STRING, oldChannel);
                            if (oldChannel.equals("REGISTER") || oldChannel.equals("UNREGISTER")) {
                                String[] channels = new String(wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8).split("\u0000");
                                ArrayList<String> rewrittenChannels = new ArrayList<String>();
                                for (String s : channels) {
                                    String rewritten = InventoryPackets.getOldPluginChannelId(s);
                                    if (rewritten != null) {
                                        rewrittenChannels.add(rewritten);
                                        continue;
                                    }
                                    if (Via.getConfig().isSuppressConversionWarnings() && !Via.getManager().isDebug()) continue;
                                    ViaBackwards.getPlatform().getLogger().warning("Ignoring plugin channel in outgoing REGISTER: " + s);
                                }
                                wrapper.write(Type.REMAINING_BYTES, Joiner.on((char)'\u0000').join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
                            }
                        }
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.SPAWN_PARTICLE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.BOOLEAN);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ParticleMapping.ParticleData old = ParticleMapping.getMapping(wrapper.get(Type.INT, 0));
                        wrapper.set(Type.INT, 0, old.getHistoryId());
                        int[] data = old.rewriteData((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol, wrapper);
                        if (data != null) {
                            if (old.getHandler().isBlockHandler() && data[0] == 0) {
                                wrapper.cancel();
                                return;
                            }
                            for (int i : data) {
                                wrapper.write(Type.VAR_INT, i);
                            }
                        }
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.PLAYER_INFO, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        TabCompleteStorage storage = packetWrapper.user().get(TabCompleteStorage.class);
                        int action = packetWrapper.passthrough(Type.VAR_INT);
                        int nPlayers = packetWrapper.passthrough(Type.VAR_INT);
                        for (int i = 0; i < nPlayers; ++i) {
                            UUID uuid = packetWrapper.passthrough(Type.UUID);
                            if (action == 0) {
                                String name = packetWrapper.passthrough(Type.STRING);
                                storage.usernames().put(uuid, name);
                                int nProperties = packetWrapper.passthrough(Type.VAR_INT);
                                for (int j = 0; j < nProperties; ++j) {
                                    packetWrapper.passthrough(Type.STRING);
                                    packetWrapper.passthrough(Type.STRING);
                                    if (!packetWrapper.passthrough(Type.BOOLEAN).booleanValue()) continue;
                                    packetWrapper.passthrough(Type.STRING);
                                }
                                packetWrapper.passthrough(Type.VAR_INT);
                                packetWrapper.passthrough(Type.VAR_INT);
                                if (!packetWrapper.passthrough(Type.BOOLEAN).booleanValue()) continue;
                                packetWrapper.passthrough(Type.COMPONENT);
                                continue;
                            }
                            if (action == 1) {
                                packetWrapper.passthrough(Type.VAR_INT);
                                continue;
                            }
                            if (action == 2) {
                                packetWrapper.passthrough(Type.VAR_INT);
                                continue;
                            }
                            if (action == 3) {
                                if (!packetWrapper.passthrough(Type.BOOLEAN).booleanValue()) continue;
                                packetWrapper.passthrough(Type.COMPONENT);
                                continue;
                            }
                            if (action != 4) continue;
                            storage.usernames().remove(uuid);
                        }
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.SCOREBOARD_OBJECTIVE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.map(Type.BYTE);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        byte mode = wrapper.get(Type.BYTE, 0);
                        if (mode == 0 || mode == 2) {
                            String value = wrapper.read(Type.COMPONENT).toString();
                            if ((value = ChatRewriter.jsonToLegacyText(value)).length() > 32) {
                                value = value.substring(0, 32);
                            }
                            wrapper.write(Type.STRING, value);
                            int type = wrapper.read(Type.VAR_INT);
                            wrapper.write(Type.STRING, type == 1 ? "hearts" : "integer");
                        }
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.TEAMS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.map(Type.BYTE);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        byte action = wrapper.get(Type.BYTE, 0);
                        if (action == 0 || action == 2) {
                            String prefix;
                            String displayName = wrapper.read(Type.STRING);
                            displayName = ChatRewriter.jsonToLegacyText(displayName);
                            if ((displayName = ChatUtil.removeUnusedColor(displayName, 'f')).length() > 32) {
                                displayName = displayName.substring(0, 32);
                            }
                            wrapper.write(Type.STRING, displayName);
                            byte flags = wrapper.read(Type.BYTE);
                            String nameTagVisibility = wrapper.read(Type.STRING);
                            String collisionRule = wrapper.read(Type.STRING);
                            int colour = wrapper.read(Type.VAR_INT);
                            if (colour == 21) {
                                colour = -1;
                            }
                            JsonElement prefixComponent = wrapper.read(Type.COMPONENT);
                            JsonElement suffixComponent = wrapper.read(Type.COMPONENT);
                            String string = prefix = prefixComponent == null || prefixComponent.isJsonNull() ? "" : ChatRewriter.jsonToLegacyText(prefixComponent.toString());
                            if (ViaBackwards.getConfig().addTeamColorTo1_13Prefix()) {
                                prefix = prefix + "\u00a7" + (colour > -1 && colour <= 15 ? Integer.toHexString(colour) : "r");
                            }
                            if ((prefix = ChatUtil.removeUnusedColor(prefix, 'f', true)).length() > 16) {
                                prefix = prefix.substring(0, 16);
                            }
                            if (prefix.endsWith("\u00a7")) {
                                prefix = prefix.substring(0, prefix.length() - 1);
                            }
                            String suffix = suffixComponent == null || suffixComponent.isJsonNull() ? "" : ChatRewriter.jsonToLegacyText(suffixComponent.toString());
                            if ((suffix = ChatUtil.removeUnusedColor(suffix, '\u0000')).length() > 16) {
                                suffix = suffix.substring(0, 16);
                            }
                            if (suffix.endsWith("\u00a7")) {
                                suffix = suffix.substring(0, suffix.length() - 1);
                            }
                            wrapper.write(Type.STRING, prefix);
                            wrapper.write(Type.STRING, suffix);
                            wrapper.write(Type.BYTE, flags);
                            wrapper.write(Type.STRING, nameTagVisibility);
                            wrapper.write(Type.STRING, collisionRule);
                            wrapper.write(Type.BYTE, (byte)colour);
                        }
                        if (action == 0 || action == 3 || action == 4) {
                            wrapper.passthrough(Type.STRING_ARRAY);
                        }
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.DECLARE_COMMANDS, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    wrapper.cancel();
                    TabCompleteStorage storage = wrapper.user().get(TabCompleteStorage.class);
                    if (!storage.commands().isEmpty()) {
                        storage.commands().clear();
                    }
                    int size = wrapper.read(Type.VAR_INT);
                    boolean initialNodes = true;
                    for (int i = 0; i < size; ++i) {
                        byte flags = wrapper.read(Type.BYTE);
                        wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
                        if ((flags & 8) != 0) {
                            wrapper.read(Type.VAR_INT);
                        }
                        byte nodeType = (byte)(flags & 3);
                        if (initialNodes && nodeType == 2) {
                            initialNodes = false;
                        }
                        if (nodeType == 1 || nodeType == 2) {
                            String name = wrapper.read(Type.STRING);
                            if (nodeType == 1 && initialNodes) {
                                storage.commands().add('/' + name);
                            }
                        }
                        if (nodeType == 2) {
                            PlayerPacket1_13.this.commandRewriter.handleArgument(wrapper, wrapper.read(Type.STRING));
                        }
                        if ((flags & 0x10) == 0) continue;
                        wrapper.read(Type.STRING);
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.TAB_COMPLETE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        TabCompleteStorage storage = wrapper.user().get(TabCompleteStorage.class);
                        if (storage.lastRequest() == null) {
                            wrapper.cancel();
                            return;
                        }
                        if (storage.lastId() != wrapper.read(Type.VAR_INT).intValue()) {
                            wrapper.cancel();
                        }
                        int start = wrapper.read(Type.VAR_INT);
                        int length = wrapper.read(Type.VAR_INT);
                        int lastRequestPartIndex = storage.lastRequest().lastIndexOf(32) + 1;
                        if (lastRequestPartIndex != start) {
                            wrapper.cancel();
                        }
                        if (length != storage.lastRequest().length() - lastRequestPartIndex) {
                            wrapper.cancel();
                        }
                        int count = wrapper.passthrough(Type.VAR_INT);
                        for (int i = 0; i < count; ++i) {
                            String match = wrapper.read(Type.STRING);
                            wrapper.write(Type.STRING, (start == 0 && !storage.isLastAssumeCommand() ? "/" : "") + match);
                            if (!wrapper.read(Type.BOOLEAN).booleanValue()) continue;
                            wrapper.read(Type.STRING);
                        }
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerServerbound(ServerboundPackets1_12_1.TAB_COMPLETE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    TabCompleteStorage storage = wrapper.user().get(TabCompleteStorage.class);
                    ArrayList<String> suggestions = new ArrayList<String>();
                    String command = wrapper.read(Type.STRING);
                    boolean assumeCommand = wrapper.read(Type.BOOLEAN);
                    wrapper.read(Type.OPTIONAL_POSITION);
                    if (!assumeCommand && !command.startsWith("/")) {
                        String buffer = command.substring(command.lastIndexOf(32) + 1);
                        for (String value : storage.usernames().values()) {
                            if (!PlayerPacket1_13.startsWithIgnoreCase(value, buffer)) continue;
                            suggestions.add(value);
                        }
                    } else if (!storage.commands().isEmpty() && !command.contains(" ")) {
                        for (String value : storage.commands()) {
                            if (!PlayerPacket1_13.startsWithIgnoreCase(value, command)) continue;
                            suggestions.add(value);
                        }
                    }
                    if (!suggestions.isEmpty()) {
                        wrapper.cancel();
                        PacketWrapper response = wrapper.create(ClientboundPackets1_12_1.TAB_COMPLETE);
                        response.write(Type.VAR_INT, suggestions.size());
                        for (String value : suggestions) {
                            response.write(Type.STRING, value);
                        }
                        response.scheduleSend(Protocol1_12_2To1_13.class);
                        storage.setLastRequest(null);
                        return;
                    }
                    if (!assumeCommand && command.startsWith("/")) {
                        command = command.substring(1);
                    }
                    int id = ThreadLocalRandom.current().nextInt();
                    wrapper.write(Type.VAR_INT, id);
                    wrapper.write(Type.STRING, command);
                    storage.setLastId(id);
                    storage.setLastAssumeCommand(assumeCommand);
                    storage.setLastRequest(command);
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerServerbound(ServerboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    String channel;
                    switch (channel = wrapper.read(Type.STRING)) {
                        case "MC|BSign": 
                        case "MC|BEdit": {
                            wrapper.setId(11);
                            Item book = wrapper.read(Type.ITEM);
                            wrapper.write(Type.FLAT_ITEM, ((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol).getItemRewriter().handleItemToServer(book));
                            boolean signing = channel.equals("MC|BSign");
                            wrapper.write(Type.BOOLEAN, signing);
                            break;
                        }
                        case "MC|ItemName": {
                            wrapper.setId(28);
                            break;
                        }
                        case "MC|AdvCmd": {
                            byte type = wrapper.read(Type.BYTE);
                            if (type == 0) {
                                wrapper.setId(34);
                                wrapper.cancel();
                                ViaBackwards.getPlatform().getLogger().warning("Client send MC|AdvCmd custom payload to update command block, weird!");
                                break;
                            }
                            if (type == 1) {
                                wrapper.setId(35);
                                wrapper.write(Type.VAR_INT, wrapper.read(Type.INT));
                                wrapper.passthrough(Type.STRING);
                                wrapper.passthrough(Type.BOOLEAN);
                                break;
                            }
                            wrapper.cancel();
                            break;
                        }
                        case "MC|AutoCmd": {
                            String mode;
                            wrapper.setId(34);
                            int x = wrapper.read(Type.INT);
                            int y = wrapper.read(Type.INT);
                            int z = wrapper.read(Type.INT);
                            wrapper.write(Type.POSITION, new Position(x, (short)y, z));
                            wrapper.passthrough(Type.STRING);
                            byte flags = 0;
                            if (wrapper.read(Type.BOOLEAN).booleanValue()) {
                                flags = (byte)(flags | 1);
                            }
                            int modeId = (mode = wrapper.read(Type.STRING)).equals("SEQUENCE") ? 0 : (mode.equals("AUTO") ? 1 : 2);
                            wrapper.write(Type.VAR_INT, modeId);
                            if (wrapper.read(Type.BOOLEAN).booleanValue()) {
                                flags = (byte)(flags | 2);
                            }
                            if (wrapper.read(Type.BOOLEAN).booleanValue()) {
                                flags = (byte)(flags | 4);
                            }
                            wrapper.write(Type.BYTE, flags);
                            break;
                        }
                        case "MC|Struct": {
                            wrapper.setId(37);
                            int x = wrapper.read(Type.INT);
                            int y = wrapper.read(Type.INT);
                            int z = wrapper.read(Type.INT);
                            wrapper.write(Type.POSITION, new Position(x, (short)y, z));
                            wrapper.write(Type.VAR_INT, wrapper.read(Type.BYTE) - 1);
                            String mode = wrapper.read(Type.STRING);
                            int modeId = mode.equals("SAVE") ? 0 : (mode.equals("LOAD") ? 1 : (mode.equals("CORNER") ? 2 : 3));
                            wrapper.write(Type.VAR_INT, modeId);
                            wrapper.passthrough(Type.STRING);
                            wrapper.write(Type.BYTE, wrapper.read(Type.INT).byteValue());
                            wrapper.write(Type.BYTE, wrapper.read(Type.INT).byteValue());
                            wrapper.write(Type.BYTE, wrapper.read(Type.INT).byteValue());
                            wrapper.write(Type.BYTE, wrapper.read(Type.INT).byteValue());
                            wrapper.write(Type.BYTE, wrapper.read(Type.INT).byteValue());
                            wrapper.write(Type.BYTE, wrapper.read(Type.INT).byteValue());
                            String mirror = wrapper.read(Type.STRING);
                            int mirrorId = mode.equals("NONE") ? 0 : (mode.equals("LEFT_RIGHT") ? 1 : 2);
                            String rotation = wrapper.read(Type.STRING);
                            int rotationId = mode.equals("NONE") ? 0 : (mode.equals("CLOCKWISE_90") ? 1 : (mode.equals("CLOCKWISE_180") ? 2 : 3));
                            wrapper.passthrough(Type.STRING);
                            byte flags = 0;
                            if (wrapper.read(Type.BOOLEAN).booleanValue()) {
                                flags = (byte)(flags | 1);
                            }
                            if (wrapper.read(Type.BOOLEAN).booleanValue()) {
                                flags = (byte)(flags | 2);
                            }
                            if (wrapper.read(Type.BOOLEAN).booleanValue()) {
                                flags = (byte)(flags | 4);
                            }
                            wrapper.passthrough(Type.FLOAT);
                            wrapper.passthrough(Type.VAR_LONG);
                            wrapper.write(Type.BYTE, flags);
                            break;
                        }
                        case "MC|Beacon": {
                            wrapper.setId(32);
                            wrapper.write(Type.VAR_INT, wrapper.read(Type.INT));
                            wrapper.write(Type.VAR_INT, wrapper.read(Type.INT));
                            break;
                        }
                        case "MC|TrSel": {
                            wrapper.setId(31);
                            wrapper.write(Type.VAR_INT, wrapper.read(Type.INT));
                            break;
                        }
                        case "MC|PickItem": {
                            wrapper.setId(21);
                            break;
                        }
                        default: {
                            String newChannel = InventoryPackets.getNewPluginChannelId(channel);
                            if (newChannel == null) {
                                if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                                    ViaBackwards.getPlatform().getLogger().warning("Ignoring incoming plugin message with channel: " + channel);
                                }
                                wrapper.cancel();
                                return;
                            }
                            wrapper.write(Type.STRING, newChannel);
                            if (!newChannel.equals("minecraft:register") && !newChannel.equals("minecraft:unregister")) break;
                            String[] channels = new String(wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8).split("\u0000");
                            ArrayList<String> rewrittenChannels = new ArrayList<String>();
                            for (String s : channels) {
                                String rewritten = InventoryPackets.getNewPluginChannelId(s);
                                if (rewritten != null) {
                                    rewrittenChannels.add(rewritten);
                                    continue;
                                }
                                if (Via.getConfig().isSuppressConversionWarnings() && !Via.getManager().isDebug()) continue;
                                ViaBackwards.getPlatform().getLogger().warning("Ignoring plugin channel in incoming REGISTER: " + s);
                            }
                            if (!rewrittenChannels.isEmpty()) {
                                wrapper.write(Type.REMAINING_BYTES, Joiner.on((char)'\u0000').join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                            wrapper.cancel();
                            return;
                        }
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.STATISTICS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int size;
                        int newSize = size = wrapper.get(Type.VAR_INT, 0).intValue();
                        block4: for (int i = 0; i < size; ++i) {
                            int categoryId = wrapper.read(Type.VAR_INT);
                            int statisticId = wrapper.read(Type.VAR_INT);
                            String name = "";
                            switch (categoryId) {
                                case 0: 
                                case 1: 
                                case 2: 
                                case 3: 
                                case 4: 
                                case 5: 
                                case 6: 
                                case 7: {
                                    wrapper.read(Type.VAR_INT);
                                    --newSize;
                                    continue block4;
                                }
                                case 8: {
                                    name = (String)((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol).getMappingData().getStatisticMappings().get(statisticId);
                                    if (name == null) {
                                        wrapper.read(Type.VAR_INT);
                                        --newSize;
                                        continue block4;
                                    }
                                }
                                default: {
                                    wrapper.write(Type.STRING, name);
                                    wrapper.passthrough(Type.VAR_INT);
                                }
                            }
                        }
                        if (newSize != size) {
                            wrapper.set(Type.VAR_INT, 0, newSize);
                        }
                    }
                });
            }
        });
    }

    private static boolean startsWithIgnoreCase(String string, String prefix) {
        if (string.length() < prefix.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}

