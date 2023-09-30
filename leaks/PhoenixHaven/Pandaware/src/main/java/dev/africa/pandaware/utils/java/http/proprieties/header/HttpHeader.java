package dev.africa.pandaware.utils.java.http.proprieties.header;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HttpHeader {
    private final String key;
    private final String value;
}