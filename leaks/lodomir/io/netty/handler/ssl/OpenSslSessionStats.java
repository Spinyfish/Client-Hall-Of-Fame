/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.jni.SSLContext
 */
package io.netty.handler.ssl;

import org.apache.tomcat.jni.SSLContext;

public final class OpenSslSessionStats {
    private final long context;

    OpenSslSessionStats(long context) {
        this.context = context;
    }

    public long number() {
        return SSLContext.sessionNumber((long)this.context);
    }

    public long connect() {
        return SSLContext.sessionConnect((long)this.context);
    }

    public long connectGood() {
        return SSLContext.sessionConnectGood((long)this.context);
    }

    public long connectRenegotiate() {
        return SSLContext.sessionConnectRenegotiate((long)this.context);
    }

    public long accept() {
        return SSLContext.sessionAccept((long)this.context);
    }

    public long acceptGood() {
        return SSLContext.sessionAcceptGood((long)this.context);
    }

    public long acceptRenegotiate() {
        return SSLContext.sessionAcceptRenegotiate((long)this.context);
    }

    public long hits() {
        return SSLContext.sessionHits((long)this.context);
    }

    public long cbHits() {
        return SSLContext.sessionCbHits((long)this.context);
    }

    public long misses() {
        return SSLContext.sessionMisses((long)this.context);
    }

    public long timeouts() {
        return SSLContext.sessionTimeouts((long)this.context);
    }

    public long cacheFull() {
        return SSLContext.sessionCacheFull((long)this.context);
    }
}

