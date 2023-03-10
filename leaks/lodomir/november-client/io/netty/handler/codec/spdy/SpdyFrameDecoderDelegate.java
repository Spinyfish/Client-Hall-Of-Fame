/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;

public interface SpdyFrameDecoderDelegate {
    public void readDataFrame(int var1, boolean var2, ByteBuf var3);

    public void readSynStreamFrame(int var1, int var2, byte var3, boolean var4, boolean var5);

    public void readSynReplyFrame(int var1, boolean var2);

    public void readRstStreamFrame(int var1, int var2);

    public void readSettingsFrame(boolean var1);

    public void readSetting(int var1, int var2, boolean var3, boolean var4);

    public void readSettingsEnd();

    public void readPingFrame(int var1);

    public void readGoAwayFrame(int var1, int var2);

    public void readHeadersFrame(int var1, boolean var2);

    public void readWindowUpdateFrame(int var1, int var2);

    public void readHeaderBlock(ByteBuf var1);

    public void readHeaderBlockEnd();

    public void readFrameError(String var1);
}

