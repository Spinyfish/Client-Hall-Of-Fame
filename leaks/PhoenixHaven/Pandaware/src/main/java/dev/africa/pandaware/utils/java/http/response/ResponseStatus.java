package dev.africa.pandaware.utils.java.http.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String name;

    public static ResponseStatus byCode(int code) {
        for (ResponseStatus value : ResponseStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }

        return NOT_FOUND;
    }
}
