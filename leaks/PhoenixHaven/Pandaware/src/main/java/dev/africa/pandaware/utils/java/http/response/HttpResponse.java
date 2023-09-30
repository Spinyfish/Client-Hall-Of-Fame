package dev.africa.pandaware.utils.java.http.response;

import dev.africa.pandaware.utils.java.http.proprieties.header.HttpHeaderContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HttpResponse {
    private final ResponseStatus responseStatus;
    private final HttpHeaderContainer headers;
    private final String body;
}
