/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.packets;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.util.ChatColorUtil;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10TO1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.Scoreboard;
import de.gerrygames.viarewind.utils.PacketUtil;
import java.util.ArrayList;
import java.util.Optional;

public class ScoreboardPackets {
    public static void register(Protocol1_7_6_10TO1_8 protocol) {
        protocol.registerClientbound(ClientboundPackets1_8.SCOREBOARD_OBJECTIVE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    String name = packetWrapper.passthrough(Type.STRING);
                    if (name.length() > 16) {
                        name = name.substring(0, 16);
                        packetWrapper.set(Type.STRING, 0, name);
                    }
                    byte mode = packetWrapper.read(Type.BYTE);
                    Scoreboard scoreboard = packetWrapper.user().get(Scoreboard.class);
                    if (mode == 0) {
                        if (scoreboard.objectiveExists(name)) {
                            packetWrapper.cancel();
                            return;
                        }
                        scoreboard.addObjective(name);
                    } else if (mode == 1) {
                        String sidebar;
                        String username;
                        Optional<Byte> color;
                        if (!scoreboard.objectiveExists(name)) {
                            packetWrapper.cancel();
                            return;
                        }
                        if (scoreboard.getColorIndependentSidebar() != null && (color = scoreboard.getPlayerTeamColor(username = packetWrapper.user().getProtocolInfo().getUsername())).isPresent() && name.equals(sidebar = scoreboard.getColorDependentSidebar().get(color.get()))) {
                            PacketWrapper sidebarPacket = PacketWrapper.create(61, null, packetWrapper.user());
                            sidebarPacket.write(Type.BYTE, (byte)1);
                            sidebarPacket.write(Type.STRING, scoreboard.getColorIndependentSidebar());
                            PacketUtil.sendPacket(sidebarPacket, Protocol1_7_6_10TO1_8.class);
                        }
                        scoreboard.removeObjective(name);
                    } else if (mode == 2 && !scoreboard.objectiveExists(name)) {
                        packetWrapper.cancel();
                        return;
                    }
                    if (mode == 0 || mode == 2) {
                        String displayName = packetWrapper.passthrough(Type.STRING);
                        if (displayName.length() > 32) {
                            packetWrapper.set(Type.STRING, 1, displayName.substring(0, 32));
                        }
                        packetWrapper.read(Type.STRING);
                    } else {
                        packetWrapper.write(Type.STRING, "");
                    }
                    packetWrapper.write(Type.BYTE, mode);
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.UPDATE_SCORE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    Scoreboard scoreboard = packetWrapper.user().get(Scoreboard.class);
                    String name = packetWrapper.passthrough(Type.STRING);
                    byte mode = packetWrapper.passthrough(Type.BYTE);
                    name = mode == 1 ? scoreboard.removeTeamForScore(name) : scoreboard.sendTeamForScore(name);
                    if (name.length() > 16 && (name = ChatColorUtil.stripColor(name)).length() > 16) {
                        name = name.substring(0, 16);
                    }
                    packetWrapper.set(Type.STRING, 0, name);
                    String objective = packetWrapper.read(Type.STRING);
                    if (objective.length() > 16) {
                        objective = objective.substring(0, 16);
                    }
                    if (mode != 1) {
                        int score = packetWrapper.read(Type.VAR_INT);
                        packetWrapper.write(Type.STRING, objective);
                        packetWrapper.write(Type.INT, score);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.DISPLAY_SCOREBOARD, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.BYTE);
                this.map(Type.STRING);
                this.handler(packetWrapper -> {
                    int position = packetWrapper.get(Type.BYTE, 0).byteValue();
                    String name = packetWrapper.get(Type.STRING, 0);
                    Scoreboard scoreboard = packetWrapper.user().get(Scoreboard.class);
                    if (position > 2) {
                        byte receiverTeamColor = (byte)(position - 3);
                        scoreboard.getColorDependentSidebar().put(receiverTeamColor, name);
                        String username = packetWrapper.user().getProtocolInfo().getUsername();
                        Optional<Byte> color = scoreboard.getPlayerTeamColor(username);
                        position = color.isPresent() && color.get() == receiverTeamColor ? 1 : -1;
                    } else if (position == 1) {
                        scoreboard.setColorIndependentSidebar(name);
                        String username = packetWrapper.user().getProtocolInfo().getUsername();
                        Optional<Byte> color = scoreboard.getPlayerTeamColor(username);
                        if (color.isPresent() && scoreboard.getColorDependentSidebar().containsKey(color.get())) {
                            position = -1;
                        }
                    }
                    if (position == -1) {
                        packetWrapper.cancel();
                        return;
                    }
                    packetWrapper.set(Type.BYTE, 0, (byte)position);
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.TEAMS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.handler(packetWrapper -> {
                    String team = packetWrapper.get(Type.STRING, 0);
                    if (team == null) {
                        packetWrapper.cancel();
                        return;
                    }
                    byte mode = packetWrapper.passthrough(Type.BYTE);
                    Scoreboard scoreboard = packetWrapper.user().get(Scoreboard.class);
                    if (mode != 0 && !scoreboard.teamExists(team)) {
                        packetWrapper.cancel();
                        return;
                    }
                    if (mode == 0 && scoreboard.teamExists(team)) {
                        scoreboard.removeTeam(team);
                        PacketWrapper remove = PacketWrapper.create(62, null, packetWrapper.user());
                        remove.write(Type.STRING, team);
                        remove.write(Type.BYTE, (byte)1);
                        PacketUtil.sendPacket(remove, Protocol1_7_6_10TO1_8.class, true, true);
                    }
                    if (mode == 0) {
                        scoreboard.addTeam(team);
                    } else if (mode == 1) {
                        scoreboard.removeTeam(team);
                    }
                    if (mode == 0 || mode == 2) {
                        packetWrapper.passthrough(Type.STRING);
                        packetWrapper.passthrough(Type.STRING);
                        packetWrapper.passthrough(Type.STRING);
                        packetWrapper.passthrough(Type.BYTE);
                        packetWrapper.read(Type.STRING);
                        byte color = packetWrapper.read(Type.BYTE);
                        if (mode == 2 && scoreboard.getTeamColor(team).get() != color) {
                            String username = packetWrapper.user().getProtocolInfo().getUsername();
                            String sidebar = scoreboard.getColorDependentSidebar().get(color);
                            PacketWrapper sidebarPacket = packetWrapper.create(61);
                            sidebarPacket.write(Type.BYTE, (byte)1);
                            sidebarPacket.write(Type.STRING, sidebar == null ? "" : sidebar);
                            PacketUtil.sendPacket(sidebarPacket, Protocol1_7_6_10TO1_8.class);
                        }
                        scoreboard.setTeamColor(team, color);
                    }
                    if (mode == 0 || mode == 3 || mode == 4) {
                        byte color = scoreboard.getTeamColor(team).get();
                        String[] entries = packetWrapper.read(Type.STRING_ARRAY);
                        ArrayList<String> entryList = new ArrayList<String>();
                        for (int i = 0; i < entries.length; ++i) {
                            String entry = entries[i];
                            String username = packetWrapper.user().getProtocolInfo().getUsername();
                            if (mode == 4) {
                                if (!scoreboard.isPlayerInTeam(entry, team)) continue;
                                scoreboard.removePlayerFromTeam(entry, team);
                                if (entry.equals(username)) {
                                    PacketWrapper sidebarPacket = packetWrapper.create(61);
                                    sidebarPacket.write(Type.BYTE, (byte)1);
                                    sidebarPacket.write(Type.STRING, scoreboard.getColorIndependentSidebar() == null ? "" : scoreboard.getColorIndependentSidebar());
                                    PacketUtil.sendPacket(sidebarPacket, Protocol1_7_6_10TO1_8.class);
                                }
                            } else {
                                scoreboard.addPlayerToTeam(entry, team);
                                if (entry.equals(username) && scoreboard.getColorDependentSidebar().containsKey(color)) {
                                    PacketWrapper displayObjective = packetWrapper.create(61);
                                    displayObjective.write(Type.BYTE, (byte)1);
                                    displayObjective.write(Type.STRING, scoreboard.getColorDependentSidebar().get(color));
                                    PacketUtil.sendPacket(displayObjective, Protocol1_7_6_10TO1_8.class);
                                }
                            }
                            entryList.add(entry);
                        }
                        packetWrapper.write(Type.SHORT, (short)entryList.size());
                        for (String entry : entryList) {
                            packetWrapper.write(Type.STRING, entry);
                        }
                    }
                });
            }
        });
    }
}

