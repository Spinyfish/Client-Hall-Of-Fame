/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.EmptyArrays;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class FingerprintTrustManagerFactory
extends SimpleTrustManagerFactory {
    private static final Pattern FINGERPRINT_PATTERN = Pattern.compile("^[0-9a-fA-F:]+$");
    private static final Pattern FINGERPRINT_STRIP_PATTERN = Pattern.compile(":");
    private static final int SHA1_BYTE_LEN = 20;
    private static final int SHA1_HEX_LEN = 40;
    private static final FastThreadLocal<MessageDigest> tlmd = new FastThreadLocal<MessageDigest>(){

        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("SHA1");
            }
            catch (NoSuchAlgorithmException e) {
                throw new Error(e);
            }
        }
    };
    private final TrustManager tm = new X509TrustManager(){

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String s) throws CertificateException {
            this.checkTrusted("client", chain);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String s) throws CertificateException {
            this.checkTrusted("server", chain);
        }

        private void checkTrusted(String type, X509Certificate[] chain) throws CertificateException {
            X509Certificate cert = chain[0];
            byte[] fingerprint = this.fingerprint(cert);
            boolean found = false;
            for (byte[] allowedFingerprint : FingerprintTrustManagerFactory.this.fingerprints) {
                if (!Arrays.equals(fingerprint, allowedFingerprint)) continue;
                found = true;
                break;
            }
            if (!found) {
                throw new CertificateException(type + " certificate with unknown fingerprint: " + cert.getSubjectDN());
            }
        }

        private byte[] fingerprint(X509Certificate cert) throws CertificateEncodingException {
            MessageDigest md = (MessageDigest)tlmd.get();
            md.reset();
            return md.digest(cert.getEncoded());
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return EmptyArrays.EMPTY_X509_CERTIFICATES;
        }
    };
    private final byte[][] fingerprints;

    public FingerprintTrustManagerFactory(Iterable<String> fingerprints) {
        this(FingerprintTrustManagerFactory.toFingerprintArray(fingerprints));
    }

    public FingerprintTrustManagerFactory(String ... fingerprints) {
        this(FingerprintTrustManagerFactory.toFingerprintArray(Arrays.asList(fingerprints)));
    }

    public FingerprintTrustManagerFactory(byte[] ... fingerprints) {
        if (fingerprints == null) {
            throw new NullPointerException("fingerprints");
        }
        ArrayList<Object> list = new ArrayList<Object>();
        for (byte[] f : fingerprints) {
            if (f == null) break;
            if (f.length != 20) {
                throw new IllegalArgumentException("malformed fingerprint: " + ByteBufUtil.hexDump(Unpooled.wrappedBuffer(f)) + " (expected: SHA1)");
            }
            list.add(f.clone());
        }
        this.fingerprints = (byte[][])list.toArray((T[])new byte[list.size()][]);
    }

    private static byte[][] toFingerprintArray(Iterable<String> fingerprints) {
        if (fingerprints == null) {
            throw new NullPointerException("fingerprints");
        }
        ArrayList list = new ArrayList();
        for (String f : fingerprints) {
            if (f == null) break;
            if (!FINGERPRINT_PATTERN.matcher(f).matches()) {
                throw new IllegalArgumentException("malformed fingerprint: " + f);
            }
            if ((f = FINGERPRINT_STRIP_PATTERN.matcher(f).replaceAll("")).length() != 40) {
                throw new IllegalArgumentException("malformed fingerprint: " + f + " (expected: SHA1)");
            }
            byte[] farr = new byte[20];
            for (int i = 0; i < farr.length; ++i) {
                int strIdx = i << 1;
                farr[i] = (byte)Integer.parseInt(f.substring(strIdx, strIdx + 2), 16);
            }
        }
        return (byte[][])list.toArray((T[])new byte[list.size()][]);
    }

    @Override
    protected void engineInit(KeyStore keyStore) throws Exception {
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{this.tm};
    }
}

