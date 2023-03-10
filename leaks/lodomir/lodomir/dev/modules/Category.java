/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.modules;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    RENDER("Render"),
    OTHER("Other"),
    CONFIGS("Configs");

    public String name;
    public int moduleIndex;

    public String getName() {
        return this.name;
    }

    private Category(String name) {
        this.name = name;
    }
}

