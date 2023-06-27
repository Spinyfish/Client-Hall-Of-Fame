package rip.athena.client.requests;

import org.json.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.io.*;

public class WebRequest
{
    private URL url;
    private String requestMethod;
    private ContentType contentType;
    private Map<String, Object> arguments;
    private Map<String, String> headers;
    private boolean useCookies;
    private StringBuilder cookieData;
    private String rawData;
    private String boundary;
    
    public WebRequest(final String url, final String requestMethod, final ContentType contentType, final boolean useCookies) throws MalformedURLException {
        this.url = new URL(url);
        this.requestMethod = requestMethod;
        this.contentType = contentType;
        this.arguments = new HashMap<String, Object>();
        this.headers = new HashMap<String, String>();
        this.useCookies = useCookies;
        this.cookieData = new StringBuilder();
        this.rawData = "";
        this.boundary = "";
    }
    
    public void setURL(final String url) throws MalformedURLException {
        this.url = new URL(url);
    }
    
    public URL getURL() {
        return this.url;
    }
    
    public void clearHeaders() {
        this.headers.clear();
    }
    
    public void clearArguments() {
        this.arguments.clear();
    }
    
    public void setHeader(final String key, final String value) {
        this.headers.put(key, value);
    }
    
    public void setArguement(final String key, final Object value) {
        this.arguments.put(key, value);
    }
    
    public WebRequestResult connect() throws IOException, JSONException, NoSuchElementException {
        return this.connect(null);
    }
    
    public WebRequestResult connect(final File file) throws IOException, JSONException, NoSuchElementException {
        String data = "";
        if (this.contentType == ContentType.JSON) {
            final JSONObject jsonObject = new JSONObject();
            for (final String key : this.arguments.keySet()) {
                final Object object = this.arguments.get(key);
                jsonObject.put(key, object);
            }
            data = jsonObject.toString();
        }
        else if (this.contentType == ContentType.FORM) {
            final StringBuilder builder = new StringBuilder();
            for (final String key : this.arguments.keySet()) {
                final Object object = this.arguments.get(key);
                if (!builder.toString().isEmpty()) {
                    builder.append("&");
                }
                builder.append(URLEncoder.encode(key.toString(), "UTF-8") + "=" + URLEncoder.encode(object.toString(), "UTF-8"));
            }
            data = builder.toString();
        }
        if (!this.rawData.isEmpty()) {
            data = this.rawData;
        }
        final HttpURLConnection conn = (HttpURLConnection)this.url.openConnection();
        conn.setDoOutput(true);
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod(this.requestMethod);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        for (final String header : this.headers.keySet()) {
            conn.setRequestProperty(header, this.headers.get(header));
        }
        if (this.cookieData.toString().length() > 0) {
            conn.setRequestProperty("Cookie", this.cookieData.toString());
        }
        if (!this.requestMethod.equalsIgnoreCase("GET")) {
            conn.setRequestProperty("Content-Type", this.contentType.toString() + (this.boundary.isEmpty() ? "" : ("; boundary=----" + this.boundary)));
            conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
            final OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
        }
        conn.connect();
        if (file != null && conn.getInputStream() != null) {
            try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                final byte[] buffer = new byte[8192];
                int read;
                while ((read = conn.getInputStream().read(buffer)) > 0) {
                    baos.write(buffer, 0, read);
                }
                try (final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                    Files.copy(bais, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        catch (IOException e) {
            if (conn.getErrorStream() != null) {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
        }
        if (this.useCookies) {
            final List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
            if (cookies != null) {
                for (final String cookie : conn.getHeaderFields().get("Set-Cookie")) {
                    final String cookieData = cookie.substring(0, cookie.indexOf(";"));
                    this.appendCookie(cookieData);
                }
            }
        }
        String newData = "";
        if (in != null) {
            final StringBuilder output = new StringBuilder();
            String input = "";
            while ((input = in.readLine()) != null) {
                if (!output.toString().isEmpty()) {
                    output.append(System.getProperty("line.separator"));
                }
                output.append(input);
            }
            newData = output.toString();
        }
        return new WebRequestResult(this, newData, conn.getHeaderFields(), conn.getResponseCode());
    }
    
    public String getRequestMethod() {
        return this.requestMethod;
    }
    
    public ContentType getContentType() {
        return this.contentType;
    }
    
    public void setRequestMethod(final String requestMethod) {
        this.requestMethod = requestMethod;
    }
    
    public void setContentType(final ContentType contentType) {
        this.contentType = contentType;
    }
    
    private void appendCookie(final String cookie) {
        if (!this.cookieData.toString().isEmpty()) {
            final StringBuilder newCookie = new StringBuilder();
            for (final String data : this.cookieData.toString().split("; ")) {
                final String key = data.split("=")[0];
                if (!key.equalsIgnoreCase(cookie.split("=")[0])) {
                    if (!newCookie.toString().isEmpty()) {
                        newCookie.append("; ");
                    }
                    newCookie.append(data);
                }
            }
            (this.cookieData = newCookie).append("; ");
        }
        this.cookieData.append(cookie);
    }
    
    public String getCookies() {
        return this.cookieData.toString();
    }
    
    public void setRawData(final String string) {
        this.rawData = string;
    }
    
    public void setBoundary(final String boundary) {
        this.boundary = boundary;
    }
}
