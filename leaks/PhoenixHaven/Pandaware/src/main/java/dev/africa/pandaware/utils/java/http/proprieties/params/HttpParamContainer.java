package dev.africa.pandaware.utils.java.http.proprieties.params;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HttpParamContainer {
    private final List<HttpParam> httpParams;

    public String get(String key) {
        HttpParam httpParam = this.httpParams.stream()
                .filter(hd -> hd.getKey().equals(key))
                .findFirst().orElse(null);

        if (httpParam == null) return "";

        return httpParam.getValue();
    }
}
