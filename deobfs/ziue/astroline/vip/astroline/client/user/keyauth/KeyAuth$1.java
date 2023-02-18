/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.user.keyauth;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

static final class KeyAuth.1
implements X509TrustManager {
    KeyAuth.1() {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] certs, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] certs, String authType) {
    }
}
