package dev.africa.pandaware.utils.java.http.request;

import dev.africa.pandaware.utils.java.http.proprieties.header.HttpHeader;
import dev.africa.pandaware.utils.java.http.proprieties.params.HttpParam;

import java.util.ArrayList;
import java.util.List;

public class RequestBuilder {
    private final List<HttpHeader> httpHeaders = new ArrayList<>();
    private final List<HttpParam> httpParams = new ArrayList<>();
    private String url;
    private String body;

    public RequestBuilder header(HttpHeader httpHeader) {
        this.httpHeaders.add(httpHeader);

        return this;
    }

    public RequestBuilder param(HttpParam httpParam) {
        this.httpParams.add(httpParam);

        return this;
    }

    public RequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public RequestBuilder body(String body) {
        this.body = body;

        return this;
    }

    public HttpRequest build() {
        return new HttpRequest(this.url, this.httpHeaders, this.httpParams, this.body);
    }
}
