/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.jni.Pool
 *  org.apache.tomcat.jni.SSL
 *  org.apache.tomcat.jni.SSLContext
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslEngine;
import io.netty.handler.ssl.OpenSslSessionStats;
import io.netty.handler.ssl.SslContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.apache.tomcat.jni.Pool;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.jni.SSLContext;

public final class OpenSslServerContext
extends SslContext {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSslServerContext.class);
    private static final List<String> DEFAULT_CIPHERS;
    private final long aprPool;
    private final List<String> ciphers = new ArrayList<String>();
    private final List<String> unmodifiableCiphers = Collections.unmodifiableList(this.ciphers);
    private final long sessionCacheSize;
    private final long sessionTimeout;
    private final List<String> nextProtocols;
    private final long ctx;
    private final OpenSslSessionStats stats;

    public OpenSslServerContext(File certChainFile, File keyFile) throws SSLException {
        this(certChainFile, keyFile, null);
    }

    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
        this(certChainFile, keyFile, keyPassword, null, null, 0L, 0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        OpenSsl.ensureAvailability();
        if (certChainFile == null) {
            throw new NullPointerException("certChainFile");
        }
        if (!certChainFile.isFile()) {
            throw new IllegalArgumentException("certChainFile is not a file: " + certChainFile);
        }
        if (keyFile == null) {
            throw new NullPointerException("keyPath");
        }
        if (!keyFile.isFile()) {
            throw new IllegalArgumentException("keyPath is not a file: " + keyFile);
        }
        if (ciphers == null) {
            ciphers = DEFAULT_CIPHERS;
        }
        if (keyPassword == null) {
            keyPassword = "";
        }
        if (nextProtocols == null) {
            nextProtocols = Collections.emptyList();
        }
        for (String c : ciphers) {
            if (c == null) break;
            this.ciphers.add(c);
        }
        ArrayList<String> nextProtoList = new ArrayList<String>();
        for (String p : nextProtocols) {
            if (p == null) break;
            nextProtoList.add(p);
        }
        this.nextProtocols = Collections.unmodifiableList(nextProtoList);
        this.aprPool = Pool.create((long)0L);
        boolean success = false;
        try {
            Class<OpenSslServerContext> clazz = OpenSslServerContext.class;
            synchronized (OpenSslServerContext.class) {
                String error;
                try {
                    this.ctx = SSLContext.make((long)this.aprPool, (int)6, (int)1);
                }
                catch (Exception e) {
                    throw new SSLException("failed to create an SSL_CTX", e);
                }
                SSLContext.setOptions((long)this.ctx, (int)4095);
                SSLContext.setOptions((long)this.ctx, (int)0x1000000);
                SSLContext.setOptions((long)this.ctx, (int)0x400000);
                SSLContext.setOptions((long)this.ctx, (int)524288);
                SSLContext.setOptions((long)this.ctx, (int)0x100000);
                SSLContext.setOptions((long)this.ctx, (int)65536);
                try {
                    StringBuilder cipherBuf = new StringBuilder();
                    for (String c : this.ciphers) {
                        cipherBuf.append(c);
                        cipherBuf.append(':');
                    }
                    cipherBuf.setLength(cipherBuf.length() - 1);
                    SSLContext.setCipherSuite((long)this.ctx, (String)cipherBuf.toString());
                }
                catch (SSLException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new SSLException("failed to set cipher suite: " + this.ciphers, e);
                }
                SSLContext.setVerify((long)this.ctx, (int)0, (int)10);
                try {
                    if (!SSLContext.setCertificate((long)this.ctx, (String)certChainFile.getPath(), (String)keyFile.getPath(), (String)keyPassword, (int)0)) {
                        throw new SSLException("failed to set certificate: " + certChainFile + " and " + keyFile + " (" + SSL.getLastError() + ')');
                    }
                }
                catch (SSLException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new SSLException("failed to set certificate: " + certChainFile + " and " + keyFile, e);
                }
                if (!SSLContext.setCertificateChainFile((long)this.ctx, (String)certChainFile.getPath(), (boolean)true) && !(error = SSL.getLastError()).startsWith("error:00000000:")) {
                    throw new SSLException("failed to set certificate chain: " + certChainFile + " (" + SSL.getLastError() + ')');
                }
                if (!nextProtoList.isEmpty()) {
                    StringBuilder nextProtocolBuf = new StringBuilder();
                    for (String p : nextProtoList) {
                        nextProtocolBuf.append(p);
                        nextProtocolBuf.append(',');
                    }
                    nextProtocolBuf.setLength(nextProtocolBuf.length() - 1);
                    SSLContext.setNextProtos((long)this.ctx, (String)nextProtocolBuf.toString());
                }
                if (sessionCacheSize > 0L) {
                    this.sessionCacheSize = sessionCacheSize;
                    SSLContext.setSessionCacheSize((long)this.ctx, (long)sessionCacheSize);
                } else {
                    this.sessionCacheSize = sessionCacheSize = SSLContext.setSessionCacheSize((long)this.ctx, (long)20480L);
                    SSLContext.setSessionCacheSize((long)this.ctx, (long)sessionCacheSize);
                }
                if (sessionTimeout > 0L) {
                    this.sessionTimeout = sessionTimeout;
                    SSLContext.setSessionCacheTimeout((long)this.ctx, (long)sessionTimeout);
                } else {
                    this.sessionTimeout = sessionTimeout = SSLContext.setSessionCacheTimeout((long)this.ctx, (long)300L);
                    SSLContext.setSessionCacheTimeout((long)this.ctx, (long)sessionTimeout);
                }
                // ** MonitorExit[var12_11] (shouldn't be in output)
                success = true;
            }
        }
        finally {
            if (!success) {
                this.destroyPools();
            }
        }
        {
            this.stats = new OpenSslSessionStats(this.ctx);
            return;
        }
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public List<String> cipherSuites() {
        return this.unmodifiableCiphers;
    }

    @Override
    public long sessionCacheSize() {
        return this.sessionCacheSize;
    }

    @Override
    public long sessionTimeout() {
        return this.sessionTimeout;
    }

    @Override
    public List<String> nextProtocols() {
        return this.nextProtocols;
    }

    public long context() {
        return this.ctx;
    }

    public OpenSslSessionStats stats() {
        return this.stats;
    }

    @Override
    public SSLEngine newEngine(ByteBufAllocator alloc) {
        if (this.nextProtocols.isEmpty()) {
            return new OpenSslEngine(this.ctx, alloc, null);
        }
        return new OpenSslEngine(this.ctx, alloc, this.nextProtocols.get(this.nextProtocols.size() - 1));
    }

    @Override
    public SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
        throw new UnsupportedOperationException();
    }

    public void setTicketKeys(byte[] keys) {
        if (keys == null) {
            throw new NullPointerException("keys");
        }
        SSLContext.setSessionTicketKeys((long)this.ctx, (byte[])keys);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void finalize() throws Throwable {
        super.finalize();
        Class<OpenSslServerContext> clazz = OpenSslServerContext.class;
        synchronized (OpenSslServerContext.class) {
            if (this.ctx != 0L) {
                SSLContext.free((long)this.ctx);
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            this.destroyPools();
            return;
        }
    }

    private void destroyPools() {
        if (this.aprPool != 0L) {
            Pool.destroy((long)this.aprPool);
        }
    }

    static {
        ArrayList ciphers = new ArrayList();
        Collections.addAll(ciphers, "ECDHE-RSA-AES128-GCM-SHA256", "ECDHE-RSA-RC4-SHA", "ECDHE-RSA-AES128-SHA", "ECDHE-RSA-AES256-SHA", "AES128-GCM-SHA256", "RC4-SHA", "RC4-MD5", "AES128-SHA", "AES256-SHA", "DES-CBC3-SHA");
        DEFAULT_CIPHERS = Collections.unmodifiableList(ciphers);
        if (logger.isDebugEnabled()) {
            logger.debug("Default cipher suite (OpenSSL): " + ciphers);
        }
    }
}

