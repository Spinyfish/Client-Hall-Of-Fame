package dev.africa.pandaware.api.module.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    VISUAL("Visual"),
    PLAYER("Player"),
    MISC("Misc");

    private final String label;
}
