package client.module;

import lombok.Getter;

@Getter
public enum Category {
    SEARCH("nigga"),
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    RENDER("Render"),
    EXPLOIT("Exploit"),
    OTHER("Other");

    private final String name;

    Category(final String name) {
        this.name = name;
    }
}
