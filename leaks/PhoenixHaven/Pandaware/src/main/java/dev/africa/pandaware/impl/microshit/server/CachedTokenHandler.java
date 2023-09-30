package dev.africa.pandaware.impl.microshit.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CachedTokenHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            Map<String, String> requestParameters = queryToMap(httpExchange.getRequestURI().getQuery());
            if (!requestParameters.containsKey("uid")) {
                String httpResponse = "400 Bad request";
                httpExchange.sendResponseHeaders(400, httpResponse.length());
                httpExchange.getResponseBody().write(httpResponse.getBytes(StandardCharsets.US_ASCII));
                return;
            }

            String client = httpExchange.getRemoteAddress().getHostString();
            if (httpExchange.getRequestHeaders().containsKey("X-Forwarded-For"))
                client = httpExchange.getRequestHeaders().getFirst("X-Forwarded-For").split(",")[0];

            String uid_ = requestParameters.get("uid");
            if (!uid_.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
                String httpResponse = "400 Bad request - Invalid UUID";
                httpExchange.sendResponseHeaders(400, httpResponse.length());
                httpExchange.getResponseBody().write(httpResponse.getBytes(StandardCharsets.US_ASCII));
                return;
            }

            UUID uid = UUID.fromString(uid_);

            //   System.out.println("> Request auth for "+client);

            if (!AuthManagerWebServer.authCache.containsKey(uid)) {
                String _404 = "404 Not found";
                httpExchange.sendResponseHeaders(404, _404.length());
                httpExchange.getResponseBody().write(_404.getBytes(StandardCharsets.US_ASCII));
                return;
            }

            AuthManagerWebServer.AuthInfo authInfo = AuthManagerWebServer.authCache.get(uid);
            if (!authInfo.addr.equals(client)) {
                String _401 = "401 Unauthorized";
                httpExchange.sendResponseHeaders(401, _401.length());
                httpExchange.getResponseBody().write(_401.getBytes(StandardCharsets.US_ASCII));
                return;
            }

            if (System.currentTimeMillis() - authInfo.time > AuthManagerWebServer.TOKEN_STORE_TIME_MS) {
                AuthManagerWebServer.authCache.remove(uid);
                String _404 = "404 Not found";
                httpExchange.sendResponseHeaders(404, _404.length());
                httpExchange.getResponseBody().write(_404.getBytes(StandardCharsets.US_ASCII));
                return;
            }

            AuthManagerWebServer.authCache.remove(uid);

            String resp = authInfo.info;
            httpExchange.sendResponseHeaders(200, resp.length());
            httpExchange.getResponseBody().write(resp.getBytes(StandardCharsets.US_ASCII));
        }
    }

    public Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
