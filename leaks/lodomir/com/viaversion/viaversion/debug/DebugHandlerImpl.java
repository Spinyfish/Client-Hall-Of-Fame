/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.debug;

import com.viaversion.viaversion.api.debug.DebugHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import java.util.HashSet;
import java.util.Set;

public final class DebugHandlerImpl
implements DebugHandler {
    private final Set<String> packetTypesToLog = new HashSet<String>();
    private boolean logPostPacketTransform = true;
    private boolean enabled;

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void addPacketTypeNameToLog(String packetTypeName) {
        this.packetTypesToLog.add(packetTypeName);
    }

    @Override
    public boolean removePacketTypeNameToLog(String packetTypeName) {
        return this.packetTypesToLog.remove(packetTypeName);
    }

    @Override
    public void clearPacketTypesToLog() {
        this.packetTypesToLog.clear();
    }

    @Override
    public boolean logPostPacketTransform() {
        return this.logPostPacketTransform;
    }

    @Override
    public void setLogPostPacketTransform(boolean logPostPacketTransform) {
        this.logPostPacketTransform = logPostPacketTransform;
    }

    @Override
    public boolean shouldLog(PacketWrapper wrapper) {
        return this.packetTypesToLog.isEmpty() || wrapper.getPacketType() != null && this.packetTypesToLog.contains(wrapper.getPacketType().getName());
    }
}

