/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.ssl.JdkSslContext;
import io.netty.handler.ssl.JettyNpnSslEngine;
import io.netty.handler.ssl.PemReader;
import java.io.File;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.x500.X500Principal;

public final class JdkSslClientContext
extends JdkSslContext {
    private final SSLContext ctx;
    private final List<String> nextProtocols;

    public JdkSslClientContext() throws SSLException {
        this(null, null, null, null, 0L, 0L);
    }

    public JdkSslClientContext(File certChainFile) throws SSLException {
        this(certChainFile, null);
    }

    public JdkSslClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
        this(null, trustManagerFactory);
    }

    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        this(certChainFile, trustManagerFactory, null, null, 0L, 0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super(ciphers);
        if (nextProtocols != null && nextProtocols.iterator().hasNext()) {
            if (!JettyNpnSslEngine.isAvailable()) {
                throw new SSLException("NPN/ALPN unsupported: " + nextProtocols);
            }
            ArrayList<String> nextProtoList = new ArrayList<String>();
            for (String p : nextProtocols) {
                if (p == null) break;
                nextProtoList.add(p);
            }
            this.nextProtocols = Collections.unmodifiableList(nextProtoList);
        } else {
            this.nextProtocols = Collections.emptyList();
        }
        try {
            if (certChainFile == null) {
                this.ctx = SSLContext.getInstance("TLS");
                if (trustManagerFactory == null) {
                    this.ctx.init(null, null, null);
                } else {
                    trustManagerFactory.init((KeyStore)null);
                    this.ctx.init(null, trustManagerFactory.getTrustManagers(), null);
                }
            } else {
                KeyStore ks = KeyStore.getInstance("JKS");
                ks.load(null, null);
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                ByteBuf[] certs = PemReader.readCertificates(certChainFile);
                try {
                    for (ByteBuf buf : certs) {
                        X509Certificate cert = (X509Certificate)cf.generateCertificate(new ByteBufInputStream(buf));
                        X500Principal principal = cert.getSubjectX500Principal();
                        ks.setCertificateEntry(principal.getName("RFC2253"), cert);
                    }
                }
                finally {
                    for (ByteBuf buf : certs) {
                        buf.release();
                    }
                }
                if (trustManagerFactory == null) {
                    trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                }
                trustManagerFactory.init(ks);
                this.ctx = SSLContext.getInstance("TLS");
                this.ctx.init(null, trustManagerFactory.getTrustManagers(), null);
            }
            SSLSessionContext sessCtx = this.ctx.getClientSessionContext();
            if (sessionCacheSize > 0L) {
                sessCtx.setSessionCacheSize((int)Math.min(sessionCacheSize, Integer.MAX_VALUE));
            }
            if (sessionTimeout > 0L) {
                sessCtx.setSessionTimeout((int)Math.min(sessionTimeout, Integer.MAX_VALUE));
            }
        }
        catch (Exception e) {
            throw new SSLException("failed to initialize the server-side SSL context", e);
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public List<String> nextProtocols() {
        return this.nextProtocols;
    }

    @Override
    public SSLContext context() {
        return this.ctx;
    }
}

