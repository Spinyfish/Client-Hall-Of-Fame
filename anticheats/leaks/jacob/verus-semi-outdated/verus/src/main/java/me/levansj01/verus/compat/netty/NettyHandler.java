package me.levansj01.verus.compat.netty;

import java.util.function.*;
import java.util.concurrent.*;
import me.levansj01.verus.data.bytes.*;

public interface NettyHandler
{
    void uninject(final Object p0);
    
    void inject(final Object p0, final Consumer p1);
    
    default ThreadFactory newThreadFactory(final String s) {
        return this.newThreadFactory(s, 5);
    }
    
    ByteData wrap(final byte[] p0, final int p1, final int p2);
    
    default ThreadFactory newThreadFactory(final String s, final int n) {
        return this.newThreadFactory(s, false, n);
    }
    
    ThreadFactory newThreadFactory(final String p0, final boolean p1, final int p2);
    
    ByteData of(final int p0);
}
