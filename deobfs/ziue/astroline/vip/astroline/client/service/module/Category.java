/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.service.module;

public enum Category {
    Combat("a"),
    Movement("b"),
    Render("c"),
    Player("d"),
    Global("m");

    public String icon;

    private Category(String string2) {
        this.icon = string2;
    }
}
