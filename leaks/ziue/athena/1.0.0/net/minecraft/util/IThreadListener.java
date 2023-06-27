package net.minecraft.util;

import com.google.common.util.concurrent.*;

public interface IThreadListener
{
    ListenableFuture<Object> addScheduledTask(final Runnable p0);
    
    boolean isCallingFromMinecraftThread();
}
