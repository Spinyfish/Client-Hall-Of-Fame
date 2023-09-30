package dev.africa.pandaware.api.manifest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Manifest {
    private final String clientName;
    private final String clientVersion;
    private String username;
    private String userId;
    private boolean devMode;
}
