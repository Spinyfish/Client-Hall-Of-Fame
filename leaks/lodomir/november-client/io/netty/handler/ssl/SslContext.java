/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.JdkSslClientContext;
import io.netty.handler.ssl.JdkSslServerContext;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslServerContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import java.io.File;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

public abstract class SslContext {
    public static SslProvider defaultServerProvider() {
        if (OpenSsl.isAvailable()) {
            return SslProvider.OPENSSL;
        }
        return SslProvider.JDK;
    }

    public static SslProvider defaultClientProvider() {
        return SslProvider.JDK;
    }

    public static SslContext newServerContext(File certChainFile, File keyFile) throws SSLException {
        return SslContext.newServerContext(null, certChainFile, keyFile, null, null, null, 0L, 0L);
    }

    public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
        return SslContext.newServerContext(null, certChainFile, keyFile, keyPassword, null, null, 0L, 0L);
    }

    public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext(null, certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
    }

    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile) throws SSLException {
        return SslContext.newServerContext(provider, certChainFile, keyFile, null, null, null, 0L, 0L);
    }

    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword) throws SSLException {
        return SslContext.newServerContext(provider, certChainFile, keyFile, keyPassword, null, null, 0L, 0L);
    }

    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        if (provider == null) {
            provider = OpenSsl.isAvailable() ? SslProvider.OPENSSL : SslProvider.JDK;
        }
        switch (provider) {
            case JDK: {
                return new JdkSslServerContext(certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
            }
            case OPENSSL: {
                return new OpenSslServerContext(certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
            }
        }
        throw new Error(provider.toString());
    }

    public static SslContext newClientContext() throws SSLException {
        return SslContext.newClientContext(null, null, null, null, null, 0L, 0L);
    }

    public static SslContext newClientContext(File certChainFile) throws SSLException {
        return SslContext.newClientContext(null, certChainFile, null, null, null, 0L, 0L);
    }

    public static SslContext newClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext(null, null, trustManagerFactory, null, null, 0L, 0L);
    }

    public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext(null, certChainFile, trustManagerFactory, null, null, 0L, 0L);
    }

    public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newClientContext(null, certChainFile, trustManagerFactory, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
    }

    public static SslContext newClientContext(SslProvider provider) throws SSLException {
        return SslContext.newClientContext(provider, null, null, null, null, 0L, 0L);
    }

    public static SslContext newClientContext(SslProvider provider, File certChainFile) throws SSLException {
        return SslContext.newClientContext(provider, certChainFile, null, null, null, 0L, 0L);
    }

    public static SslContext newClientContext(SslProvider provider, TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext(provider, null, trustManagerFactory, null, null, 0L, 0L);
    }

    public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext(provider, certChainFile, trustManagerFactory, null, null, 0L, 0L);
    }

    public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        if (provider != null && provider != SslProvider.JDK) {
            throw new SSLException("client context unsupported for: " + (Object)((Object)provider));
        }
        return new JdkSslClientContext(certChainFile, trustManagerFactory, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
    }

    SslContext() {
    }

    public final boolean isServer() {
        return !this.isClient();
    }

    public abstract boolean isClient();

    public abstract List<String> cipherSuites();

    public abstract long sessionCacheSize();

    public abstract long sessionTimeout();

    public abstract List<String> nextProtocols();

    public abstract SSLEngine newEngine(ByteBufAllocator var1);

    public abstract SSLEngine newEngine(ByteBufAllocator var1, String var2, int var3);

    public final SslHandler newHandler(ByteBufAllocator alloc) {
        return SslContext.newHandler(this.newEngine(alloc));
    }

    public final SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort) {
        return SslContext.newHandler(this.newEngine(alloc, peerHost, peerPort));
    }

    private static SslHandler newHandler(SSLEngine engine) {
        return new SslHandler(engine);
    }
}

