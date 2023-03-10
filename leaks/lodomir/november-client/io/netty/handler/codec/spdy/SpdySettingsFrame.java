/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyFrame;
import java.util.Set;

public interface SpdySettingsFrame
extends SpdyFrame {
    public static final int SETTINGS_MINOR_VERSION = 0;
    public static final int SETTINGS_UPLOAD_BANDWIDTH = 1;
    public static final int SETTINGS_DOWNLOAD_BANDWIDTH = 2;
    public static final int SETTINGS_ROUND_TRIP_TIME = 3;
    public static final int SETTINGS_MAX_CONCURRENT_STREAMS = 4;
    public static final int SETTINGS_CURRENT_CWND = 5;
    public static final int SETTINGS_DOWNLOAD_RETRANS_RATE = 6;
    public static final int SETTINGS_INITIAL_WINDOW_SIZE = 7;
    public static final int SETTINGS_CLIENT_CERTIFICATE_VECTOR_SIZE = 8;

    public Set<Integer> ids();

    public boolean isSet(int var1);

    public int getValue(int var1);

    public SpdySettingsFrame setValue(int var1, int var2);

    public SpdySettingsFrame setValue(int var1, int var2, boolean var3, boolean var4);

    public SpdySettingsFrame removeValue(int var1);

    public boolean isPersistValue(int var1);

    public SpdySettingsFrame setPersistValue(int var1, boolean var2);

    public boolean isPersisted(int var1);

    public SpdySettingsFrame setPersisted(int var1, boolean var2);

    public boolean clearPreviouslyPersistedSettings();

    public SpdySettingsFrame setClearPreviouslyPersistedSettings(boolean var1);
}

