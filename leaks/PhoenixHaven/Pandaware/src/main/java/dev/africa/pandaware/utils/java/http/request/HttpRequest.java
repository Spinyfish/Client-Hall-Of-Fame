package dev.africa.pandaware.utils.java.http.request;

import dev.africa.pandaware.utils.java.http.proprieties.header.HttpHeader;
import dev.africa.pandaware.utils.java.http.proprieties.header.HttpHeaderContainer;
import dev.africa.pandaware.utils.java.http.proprieties.params.HttpParam;
import dev.africa.pandaware.utils.java.http.response.HttpResponse;
import dev.africa.pandaware.utils.java.http.response.ResponseStatus;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@AllArgsConstructor
public class HttpRequest {
    private final String url;
    private final List<HttpHeader> httpHeaders;
    private final List<HttpParam> httpParams;
    private final String body;

    public void postAsync(AsyncResponse asyncResponse) {
        Executors.newSingleThreadScheduledExecutor().execute(() -> asyncResponse.complete(this.post()));
    }

    public void getAsync(AsyncResponse asyncResponse) {
        Executors.newSingleThreadScheduledExecutor().execute(() -> asyncResponse.complete(this.get()));
    }

    public HttpResponse post() {
        return send("POST");
    }

    public HttpResponse get() {
        return send("GET");
    }

    private HttpResponse send(String method) {
        if (this.url != null) {
            try {
                StringBuilder finalUrl = new StringBuilder(this.url);

                if (this.httpParams != null && !this.httpParams.isEmpty()) {
                    finalUrl.append("?");

                    this.httpParams.forEach(param -> {
                        finalUrl.append(param.getKey())
                                .append("=")
                                .append(param.getValue());

                        if (param != this.httpParams.get(this.httpParams.size() - 1)) {
                            finalUrl.append("&");
                        }
                    });
                }

                HttpURLConnection connection = (HttpURLConnection) new URL(finalUrl.toString()).openConnection();
                connection.setRequestMethod(method);

                if (this.httpHeaders != null && !this.httpHeaders.isEmpty()) {
                    this.httpHeaders.forEach(httpHeader ->
                            connection.setRequestProperty(httpHeader.getKey(), httpHeader.getValue()));
                }

                String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36";
                connection.setRequestProperty("User-Agent", USER_AGENT);

                if (method.equals("POST") && this.body != null) {
                    connection.setDoOutput(true);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = this.body.getBytes(StandardCharsets.UTF_8);

                        os.write(input, 0, input.length);
                    }
                }

                int responseCode = connection.getResponseCode();
                List<HttpHeader> responseHttpHeaders = new ArrayList<>();
                connection.getHeaderFields().forEach((key, value) ->
                        responseHttpHeaders.add(new HttpHeader(key, value.get(0))));
                StringBuilder message = new StringBuilder();

                BufferedReader buff = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                        StandardCharsets.UTF_8));
                String line;
                while ((line = buff.readLine()) != null) {
                    message.append(line).append("\n");
                }
                buff.close();

                if (message.toString().endsWith("\n")) {
                    message.deleteCharAt(message.length() - 1);
                }

                return new HttpResponse(
                        ResponseStatus.byCode(responseCode),
                        new HttpHeaderContainer(responseHttpHeaders),
                        message.toString()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public interface AsyncResponse {
        void complete(HttpResponse response);
    }
}
