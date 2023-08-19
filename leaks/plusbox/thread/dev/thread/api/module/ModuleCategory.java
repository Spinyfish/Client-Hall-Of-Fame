package dev.thread.api.module;

import lombok.Getter;

public enum ModuleCategory {
    COMBAT("a"),
    MOVEMENT("b"),
    PLAYER("c"),
    RENDER(""),
    EXPLOIT(""),
    OTHER("");

    @Getter
    private final String icon;

    ModuleCategory(String icon) {
        this.icon = icon;
    }
}
