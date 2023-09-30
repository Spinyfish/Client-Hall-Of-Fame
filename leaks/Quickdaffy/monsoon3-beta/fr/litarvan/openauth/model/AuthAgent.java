/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.model;

public class AuthAgent {
    public static final AuthAgent MINECRAFT = new AuthAgent("Minecraft", 1);
    public static final AuthAgent SCROLLS = new AuthAgent("Scrolls", 1);
    private String name;
    private int version;

    public AuthAgent(String name, int version) {
        this.name = name;
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }
}

