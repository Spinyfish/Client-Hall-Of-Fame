/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.openauth.microsoft;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MicrosoftPatchedHttpURLConnection
extends HttpURLConnection {
    private final HttpURLConnection inner;

    public MicrosoftPatchedHttpURLConnection(URL url, HttpURLConnection inner) {
        super(url);
        this.inner = inner;
    }

    @Override
    public void setRequestMethod(String method) throws ProtocolException {
        this.inner.setRequestMethod(method);
    }

    @Override
    public void setInstanceFollowRedirects(boolean followRedirects) {
        this.inner.setInstanceFollowRedirects(followRedirects);
    }

    @Override
    public boolean getInstanceFollowRedirects() {
        return this.inner.getInstanceFollowRedirects();
    }

    @Override
    public String getRequestMethod() {
        return this.inner.getRequestMethod();
    }

    @Override
    public int getResponseCode() throws IOException {
        return this.inner.getResponseCode();
    }

    @Override
    public String getResponseMessage() throws IOException {
        return this.inner.getResponseMessage();
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        return this.inner.getHeaderFields();
    }

    @Override
    public String getHeaderField(String name) {
        return this.inner.getHeaderField(name);
    }

    @Override
    public String getHeaderField(int n) {
        return this.inner.getHeaderField(n);
    }

    @Override
    public void disconnect() {
        this.inner.disconnect();
    }

    @Override
    public void setDoOutput(boolean dooutput) {
        this.inner.setDoOutput(dooutput);
    }

    @Override
    public boolean usingProxy() {
        return this.inner.usingProxy();
    }

    @Override
    public void connect() throws IOException {
        this.inner.connect();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (InputStream in = this.inner.getInputStream();){
            int n;
            byte[] data = new byte[8192];
            while ((n = in.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, n);
            }
        }
        byte[] patched = buffer.toString("UTF-8").replaceAll("integrity ?=", "integrity.disabled=").replaceAll("setAttribute\\(\"integrity\"", "setAttribute(\"integrity.disabled\"").getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(patched);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.inner.getOutputStream();
    }

    @Override
    public InputStream getErrorStream() {
        return this.inner.getErrorStream();
    }
}

