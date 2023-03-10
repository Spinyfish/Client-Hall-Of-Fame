/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.openauth.model;

public class AuthProfile {
    private String name;
    private String id;

    public AuthProfile() {
        this.name = "";
        this.id = "";
    }

    public AuthProfile(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }
}

