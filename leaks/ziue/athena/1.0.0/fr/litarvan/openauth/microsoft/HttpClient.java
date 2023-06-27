package fr.litarvan.openauth.microsoft;

import com.google.gson.*;
import java.util.*;
import java.nio.charset.*;
import java.net.*;
import java.io.*;

public class HttpClient
{
    public static final String MIME_TYPE_JSON = "application/json";
    public static final String MIME_TYPE_URLENCODED_FORM = "application/x-www-form-urlencoded";
    private final Gson gson;
    private final Proxy proxy;
    
    public HttpClient() {
        this(Proxy.NO_PROXY);
    }
    
    public HttpClient(final Proxy proxy) {
        this.gson = new Gson();
        this.proxy = proxy;
    }
    
    public String getText(final String url, final Map<String, String> params) throws MicrosoftAuthenticationException {
        return this.readResponse(this.createConnection(url + '?' + this.buildParams(params)));
    }
    
    public <T> T getJson(final String url, final String token, final Class<T> responseClass) throws MicrosoftAuthenticationException {
        final HttpURLConnection connection = this.createConnection(url);
        connection.addRequestProperty("Authorization", "Bearer " + token);
        connection.addRequestProperty("Accept", "application/json");
        return this.readJson(connection, responseClass);
    }
    
    public HttpURLConnection postForm(final String url, final Map<String, String> params) throws MicrosoftAuthenticationException {
        return this.post(url, "application/x-www-form-urlencoded", "*/*", this.buildParams(params));
    }
    
    public <T> T postJson(final String url, final Object request, final Class<T> responseClass) throws MicrosoftAuthenticationException {
        final HttpURLConnection connection = this.post(url, "application/json", "application/json", this.gson.toJson(request));
        return this.readJson(connection, responseClass);
    }
    
    public <T> T postFormGetJson(final String url, final Map<String, String> params, final Class<T> responseClass) throws MicrosoftAuthenticationException {
        return this.readJson(this.postForm(url, params), responseClass);
    }
    
    protected HttpURLConnection post(final String url, final String contentType, final String accept, final String data) throws MicrosoftAuthenticationException {
        final HttpURLConnection connection = this.createConnection(url);
        connection.setDoOutput(true);
        connection.addRequestProperty("Content-Type", contentType);
        connection.addRequestProperty("Accept", accept);
        try {
            connection.setRequestMethod("POST");
            connection.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new MicrosoftAuthenticationException(e);
        }
        return connection;
    }
    
    protected <T> T readJson(final HttpURLConnection connection, final Class<T> responseType) throws MicrosoftAuthenticationException {
        return (T)this.gson.fromJson(this.readResponse(connection), (Class)responseType);
    }
    
    protected String readResponse(final HttpURLConnection connection) throws MicrosoftAuthenticationException {
        final String redirection = connection.getHeaderField("Location");
        if (redirection != null) {
            return this.readResponse(this.createConnection(redirection));
        }
        final StringBuilder response = new StringBuilder();
        try {
            InputStream inputStream = connection.getInputStream();
            if (this.checkUrl(connection.getURL())) {
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                final byte[] data = new byte[8192];
                int n;
                while ((n = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, n);
                }
                final byte[] patched = buffer.toString("UTF-8").replaceAll("integrity ?=", "integrity.disabled=").replaceAll("setAttribute\\(\"integrity\"", "setAttribute(\"integrity.disabled\"").getBytes(StandardCharsets.UTF_8);
                inputStream = new ByteArrayInputStream(patched);
            }
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line).append('\n');
                }
            }
            catch (IOException e) {
                throw new MicrosoftAuthenticationException(e);
            }
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
        return response.toString();
    }
    
    private boolean checkUrl(final URL url) {
        return ("login.microsoftonline.com".equals(url.getHost()) && url.getPath().endsWith("/oauth2/authorize")) || ("login.live.com".equals(url.getHost()) && "/oauth20_authorize.srf".equals(url.getPath())) || ("login.live.com".equals(url.getHost()) && "/ppsecure/post.srf".equals(url.getPath())) || ("login.microsoftonline.com".equals(url.getHost()) && "/login.srf".equals(url.getPath())) || ("login.microsoftonline.com".equals(url.getHost()) && url.getPath().endsWith("/login")) || ("login.microsoftonline.com".equals(url.getHost()) && url.getPath().endsWith("/SAS/ProcessAuth")) || ("login.microsoftonline.com".equals(url.getHost()) && url.getPath().endsWith("/federation/oauth2")) || ("login.microsoftonline.com".equals(url.getHost()) && url.getPath().endsWith("/oauth2/v2.0/authorize"));
    }
    
    protected HttpURLConnection followRedirects(HttpURLConnection connection) throws MicrosoftAuthenticationException {
        final String redirection = connection.getHeaderField("Location");
        if (redirection != null) {
            connection = this.followRedirects(this.createConnection(redirection));
        }
        return connection;
    }
    
    protected String buildParams(final Map<String, String> params) {
        final StringBuilder query = new StringBuilder();
        final StringBuilder sb;
        params.forEach((key, value) -> {
            if (sb.length() > 0) {
                sb.append('&');
            }
            try {
                sb.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
            }
            catch (UnsupportedEncodingException ex) {}
            return;
        });
        return query.toString();
    }
    
    protected HttpURLConnection createConnection(final String url) throws MicrosoftAuthenticationException {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection)new URL(url).openConnection(this.proxy);
        }
        catch (IOException e) {
            throw new MicrosoftAuthenticationException(e);
        }
        final String userAgent = "Mozilla/5.0 (XboxReplay; XboxLiveAuth/3.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);
        connection.setRequestProperty("Accept-Language", "en-US");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("User-Agent", userAgent);
        return connection;
    }
}
