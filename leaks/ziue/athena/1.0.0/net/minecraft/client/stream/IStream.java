package net.minecraft.client.stream;

import tv.twitch.broadcast.*;
import tv.twitch.chat.*;
import tv.twitch.*;

public interface IStream
{
    void shutdownStream();
    
    void func_152935_j();
    
    void func_152922_k();
    
    boolean func_152936_l();
    
    boolean isReadyToBroadcast();
    
    boolean isBroadcasting();
    
    void func_152911_a(final Metadata p0, final long p1);
    
    void func_176026_a(final Metadata p0, final long p1, final long p2);
    
    boolean isPaused();
    
    void requestCommercial();
    
    void pause();
    
    void unpause();
    
    void updateStreamVolume();
    
    void func_152930_t();
    
    void stopBroadcasting();
    
    IngestServer[] func_152925_v();
    
    void func_152909_x();
    
    IngestServerTester func_152932_y();
    
    boolean func_152908_z();
    
    int func_152920_A();
    
    boolean func_152927_B();
    
    String func_152921_C();
    
    ChatUserInfo func_152926_a(final String p0);
    
    void func_152917_b(final String p0);
    
    boolean func_152928_D();
    
    ErrorCode func_152912_E();
    
    boolean func_152913_F();
    
    void muteMicrophone(final boolean p0);
    
    boolean func_152929_G();
    
    AuthFailureReason func_152918_H();
    
    public enum AuthFailureReason
    {
        ERROR, 
        INVALID_TOKEN;
    }
}
