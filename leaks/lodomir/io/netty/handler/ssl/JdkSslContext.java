/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.JettyNpnSslEngine;
import io.netty.handler.ssl.SslContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;

public abstract class JdkSslContext
extends SslContext {
    private static final InternalLogger logger;
    static final String PROTOCOL = "TLS";
    static final String[] PROTOCOLS;
    static final List<String> DEFAULT_CIPHERS;
    private final String[] cipherSuites;
    private final List<String> unmodifiableCipherSuites;

    private static void addIfSupported(String[] supported, List<String> enabled, String ... names) {
        block0: for (String n : names) {
            for (String s : supported) {
                if (!n.equals(s)) continue;
                enabled.add(s);
                continue block0;
            }
        }
    }

    JdkSslContext(Iterable<String> ciphers) {
        this.cipherSuites = JdkSslContext.toCipherSuiteArray(ciphers);
        this.unmodifiableCipherSuites = Collections.unmodifiableList(Arrays.asList(this.cipherSuites));
    }

    public abstract SSLContext context();

    public final SSLSessionContext sessionContext() {
        if (this.isServer()) {
            return this.context().getServerSessionContext();
        }
        return this.context().getClientSessionContext();
    }

    @Override
    public final List<String> cipherSuites() {
        return this.unmodifiableCipherSuites;
    }

    @Override
    public final long sessionCacheSize() {
        return this.sessionContext().getSessionCacheSize();
    }

    @Override
    public final long sessionTimeout() {
        return this.sessionContext().getSessionTimeout();
    }

    @Override
    public final SSLEngine newEngine(ByteBufAllocator alloc) {
        SSLEngine engine = this.context().createSSLEngine();
        engine.setEnabledCipherSuites(this.cipherSuites);
        engine.setEnabledProtocols(PROTOCOLS);
        engine.setUseClientMode(this.isClient());
        return this.wrapEngine(engine);
    }

    @Override
    public final SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
        SSLEngine engine = this.context().createSSLEngine(peerHost, peerPort);
        engine.setEnabledCipherSuites(this.cipherSuites);
        engine.setEnabledProtocols(PROTOCOLS);
        engine.setUseClientMode(this.isClient());
        return this.wrapEngine(engine);
    }

    private SSLEngine wrapEngine(SSLEngine engine) {
        if (this.nextProtocols().isEmpty()) {
            return engine;
        }
        return new JettyNpnSslEngine(engine, this.nextProtocols(), this.isServer());
    }

    private static String[] toCipherSuiteArray(Iterable<String> ciphers) {
        if (ciphers == null) {
            return DEFAULT_CIPHERS.toArray(new String[DEFAULT_CIPHERS.size()]);
        }
        ArrayList<String> newCiphers = new ArrayList<String>();
        for (String c : ciphers) {
            if (c == null) break;
            newCiphers.add(c);
        }
        return newCiphers.toArray(new String[newCiphers.size()]);
    }

    static {
        SSLContext context;
        logger = InternalLoggerFactory.getInstance(JdkSslContext.class);
        try {
            context = SSLContext.getInstance(PROTOCOL);
            context.init(null, null, null);
        }
        catch (Exception e) {
            throw new Error("failed to initialize the default SSL context", e);
        }
        SSLEngine engine = context.createSSLEngine();
        String[] supportedProtocols = engine.getSupportedProtocols();
        ArrayList<String> protocols = new ArrayList<String>();
        JdkSslContext.addIfSupported(supportedProtocols, protocols, "TLSv1.2", "TLSv1.1", "TLSv1", "SSLv3");
        PROTOCOLS = !protocols.isEmpty() ? protocols.toArray(new String[protocols.size()]) : engine.getEnabledProtocols();
        String[] supportedCiphers = engine.getSupportedCipherSuites();
        ArrayList<String> ciphers = new ArrayList<String>();
        JdkSslContext.addIfSupported(supportedCiphers, ciphers, "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_RC4_128_SHA", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_128_GCM_SHA256", "SSL_RSA_WITH_RC4_128_SHA", "SSL_RSA_WITH_RC4_128_MD5", "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA", "SSL_RSA_WITH_DES_CBC_SHA");
        DEFAULT_CIPHERS = !ciphers.isEmpty() ? Collections.unmodifiableList(ciphers) : Collections.unmodifiableList(Arrays.asList(engine.getEnabledCipherSuites()));
        if (logger.isDebugEnabled()) {
            logger.debug("Default protocols (JDK): {} ", (Object)Arrays.asList(PROTOCOLS));
            logger.debug("Default cipher suites (JDK): {}", (Object)DEFAULT_CIPHERS);
        }
    }
}

