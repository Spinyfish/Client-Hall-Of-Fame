package dev.africa.pandaware.utils.java.http.proprieties.header;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class HttpHeaderContainer {
    private final List<HttpHeader> httpHeaders;

    public String get(String key) {
        HttpHeader httpHeader = this.httpHeaders
                .stream().filter(hd -> hd.getKey().equals(key))
                .findFirst().orElse(null);

        if (httpHeader == null) return "";

        return httpHeader.getValue();
    }
}
