/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.data;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.Protocol1_11_1To1_12;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ClientboundPackets1_12;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;

public class ShoulderTracker
extends StoredObject {
    private int entityId;
    private String leftShoulder;
    private String rightShoulder;

    public ShoulderTracker(UserConnection user) {
        super(user);
    }

    public void update() {
        PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_12.CHAT_MESSAGE, null, this.getUser());
        wrapper.write(Type.COMPONENT, Protocol1_9To1_8.fixJson(this.generateString()));
        wrapper.write(Type.BYTE, (byte)2);
        try {
            wrapper.scheduleSend(Protocol1_11_1To1_12.class);
        }
        catch (Exception e) {
            ViaBackwards.getPlatform().getLogger().severe("Failed to send the shoulder indication");
            e.printStackTrace();
        }
    }

    private String generateString() {
        StringBuilder builder = new StringBuilder();
        builder.append("  ");
        if (this.leftShoulder == null) {
            builder.append("\u00a74\u00a7lNothing");
        } else {
            builder.append("\u00a72\u00a7l").append(this.getName(this.leftShoulder));
        }
        builder.append("\u00a78\u00a7l <- \u00a77\u00a7lShoulders\u00a78\u00a7l -> ");
        if (this.rightShoulder == null) {
            builder.append("\u00a74\u00a7lNothing");
        } else {
            builder.append("\u00a72\u00a7l").append(this.getName(this.rightShoulder));
        }
        return builder.toString();
    }

    private String getName(String current) {
        if (current.startsWith("minecraft:")) {
            current = current.substring(10);
        }
        String[] array = current.split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : array) {
            builder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
        }
        return builder.toString();
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getLeftShoulder() {
        return this.leftShoulder;
    }

    public void setLeftShoulder(String leftShoulder) {
        this.leftShoulder = leftShoulder;
    }

    public String getRightShoulder() {
        return this.rightShoulder;
    }

    public void setRightShoulder(String rightShoulder) {
        this.rightShoulder = rightShoulder;
    }

    public String toString() {
        return "ShoulderTracker{entityId=" + this.entityId + ", leftShoulder='" + this.leftShoulder + '\'' + ", rightShoulder='" + this.rightShoulder + '\'' + '}';
    }
}

