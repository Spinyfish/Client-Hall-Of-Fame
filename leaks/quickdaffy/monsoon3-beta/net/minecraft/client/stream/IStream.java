/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  tv.twitch.ErrorCode
 *  tv.twitch.broadcast.IngestServer
 *  tv.twitch.chat.ChatUserInfo
 */
package net.minecraft.client.stream;

import net.minecraft.client.stream.IngestServerTester;
import net.minecraft.client.stream.Metadata;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.chat.ChatUserInfo;

public interface IStream {
    public void shutdownStream();

    public void func_152935_j();

    public void func_152922_k();

    public boolean func_152936_l();

    public boolean isReadyToBroadcast();

    public boolean isBroadcasting();

    public void func_152911_a(Metadata var1, long var2);

    public void func_176026_a(Metadata var1, long var2, long var4);

    public boolean isPaused();

    public void requestCommercial();

    public void pause();

    public void unpause();

    public void updateStreamVolume();

    public void func_152930_t();

    public void stopBroadcasting();

    public IngestServer[] func_152925_v();

    public void func_152909_x();

    public IngestServerTester func_152932_y();

    public boolean func_152908_z();

    public int func_152920_A();

    public boolean func_152927_B();

    public String func_152921_C();

    public ChatUserInfo func_152926_a(String var1);

    public void func_152917_b(String var1);

    public boolean func_152928_D();

    public ErrorCode func_152912_E();

    public boolean func_152913_F();

    public void muteMicrophone(boolean var1);

    public boolean func_152929_G();

    public AuthFailureReason func_152918_H();

    public static enum AuthFailureReason {
        ERROR,
        INVALID_TOKEN;

    }
}

