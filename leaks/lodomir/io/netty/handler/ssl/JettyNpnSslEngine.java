/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.npn.NextProtoNego
 *  org.eclipse.jetty.npn.NextProtoNego$ClientProvider
 *  org.eclipse.jetty.npn.NextProtoNego$Provider
 *  org.eclipse.jetty.npn.NextProtoNego$ServerProvider
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JettyNpnSslSession;
import java.nio.ByteBuffer;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.npn.NextProtoNego;

final class JettyNpnSslEngine
extends SSLEngine {
    private static boolean available;
    private final SSLEngine engine;
    private final JettyNpnSslSession session;

    static boolean isAvailable() {
        JettyNpnSslEngine.updateAvailability();
        return available;
    }

    private static void updateAvailability() {
        if (available) {
            return;
        }
        try {
            ClassLoader bootloader = ClassLoader.getSystemClassLoader().getParent();
            if (bootloader == null) {
                bootloader = ClassLoader.getSystemClassLoader();
            }
            Class.forName("sun.security.ssl.NextProtoNegoExtension", true, bootloader);
            available = true;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    JettyNpnSslEngine(SSLEngine engine, final List<String> nextProtocols, boolean server) {
        assert (!nextProtocols.isEmpty());
        this.engine = engine;
        this.session = new JettyNpnSslSession(engine);
        if (server) {
            NextProtoNego.put((SSLEngine)engine, (NextProtoNego.Provider)new NextProtoNego.ServerProvider(){

                public void unsupported() {
                    JettyNpnSslEngine.this.getSession().setApplicationProtocol((String)nextProtocols.get(nextProtocols.size() - 1));
                }

                public List<String> protocols() {
                    return nextProtocols;
                }

                public void protocolSelected(String protocol) {
                    JettyNpnSslEngine.this.getSession().setApplicationProtocol(protocol);
                }
            });
        } else {
            final String[] list = nextProtocols.toArray(new String[nextProtocols.size()]);
            final String fallback = list[list.length - 1];
            NextProtoNego.put((SSLEngine)engine, (NextProtoNego.Provider)new NextProtoNego.ClientProvider(){

                public boolean supports() {
                    return true;
                }

                public void unsupported() {
                    JettyNpnSslEngine.this.session.setApplicationProtocol(null);
                }

                public String selectProtocol(List<String> protocols) {
                    for (String p : list) {
                        if (!protocols.contains(p)) continue;
                        return p;
                    }
                    return fallback;
                }
            });
        }
    }

    @Override
    public JettyNpnSslSession getSession() {
        return this.session;
    }

    @Override
    public void closeInbound() throws SSLException {
        NextProtoNego.remove((SSLEngine)this.engine);
        this.engine.closeInbound();
    }

    @Override
    public void closeOutbound() {
        NextProtoNego.remove((SSLEngine)this.engine);
        this.engine.closeOutbound();
    }

    @Override
    public String getPeerHost() {
        return this.engine.getPeerHost();
    }

    @Override
    public int getPeerPort() {
        return this.engine.getPeerPort();
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws SSLException {
        return this.engine.wrap(byteBuffer, byteBuffer2);
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer[] byteBuffers, ByteBuffer byteBuffer) throws SSLException {
        return this.engine.wrap(byteBuffers, byteBuffer);
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer[] byteBuffers, int i, int i2, ByteBuffer byteBuffer) throws SSLException {
        return this.engine.wrap(byteBuffers, i, i2, byteBuffer);
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws SSLException {
        return this.engine.unwrap(byteBuffer, byteBuffer2);
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers) throws SSLException {
        return this.engine.unwrap(byteBuffer, byteBuffers);
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers, int i, int i2) throws SSLException {
        return this.engine.unwrap(byteBuffer, byteBuffers, i, i2);
    }

    @Override
    public Runnable getDelegatedTask() {
        return this.engine.getDelegatedTask();
    }

    @Override
    public boolean isInboundDone() {
        return this.engine.isInboundDone();
    }

    @Override
    public boolean isOutboundDone() {
        return this.engine.isOutboundDone();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return this.engine.getSupportedCipherSuites();
    }

    @Override
    public String[] getEnabledCipherSuites() {
        return this.engine.getEnabledCipherSuites();
    }

    @Override
    public void setEnabledCipherSuites(String[] strings) {
        this.engine.setEnabledCipherSuites(strings);
    }

    @Override
    public String[] getSupportedProtocols() {
        return this.engine.getSupportedProtocols();
    }

    @Override
    public String[] getEnabledProtocols() {
        return this.engine.getEnabledProtocols();
    }

    @Override
    public void setEnabledProtocols(String[] strings) {
        this.engine.setEnabledProtocols(strings);
    }

    @Override
    public SSLSession getHandshakeSession() {
        return this.engine.getHandshakeSession();
    }

    @Override
    public void beginHandshake() throws SSLException {
        this.engine.beginHandshake();
    }

    @Override
    public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        return this.engine.getHandshakeStatus();
    }

    @Override
    public void setUseClientMode(boolean b) {
        this.engine.setUseClientMode(b);
    }

    @Override
    public boolean getUseClientMode() {
        return this.engine.getUseClientMode();
    }

    @Override
    public void setNeedClientAuth(boolean b) {
        this.engine.setNeedClientAuth(b);
    }

    @Override
    public boolean getNeedClientAuth() {
        return this.engine.getNeedClientAuth();
    }

    @Override
    public void setWantClientAuth(boolean b) {
        this.engine.setWantClientAuth(b);
    }

    @Override
    public boolean getWantClientAuth() {
        return this.engine.getWantClientAuth();
    }

    @Override
    public void setEnableSessionCreation(boolean b) {
        this.engine.setEnableSessionCreation(b);
    }

    @Override
    public boolean getEnableSessionCreation() {
        return this.engine.getEnableSessionCreation();
    }

    @Override
    public SSLParameters getSSLParameters() {
        return this.engine.getSSLParameters();
    }

    @Override
    public void setSSLParameters(SSLParameters sslParameters) {
        this.engine.setSSLParameters(sslParameters);
    }
}

