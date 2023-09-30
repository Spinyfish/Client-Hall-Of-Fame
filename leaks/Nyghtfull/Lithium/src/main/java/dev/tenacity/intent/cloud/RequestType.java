package dev.tenacity.intent.cloud;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestType {
    UPLOAD("post"),
    DELETE("delete"),
    LIST("public"),
    RETRIEVE("get"),
    UPDATE("update");

    private final String extension;
}

