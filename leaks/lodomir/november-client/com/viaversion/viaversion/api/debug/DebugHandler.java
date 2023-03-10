/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.debug;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;

public interface DebugHandler {
    public boolean enabled();

    public void setEnabled(boolean var1);

    public void addPacketTypeNameToLog(String var1);

    public boolean removePacketTypeNameToLog(String var1);

    public void clearPacketTypesToLog();

    public boolean logPostPacketTransform();

    public void setLogPostPacketTransform(boolean var1);

    public boolean shouldLog(PacketWrapper var1);
}

