package dev.hera.client.mods;

public enum Category {
    COMBAT("Combat"), MOVEMENT("Movement"), RENDER("Render"), WORLD("World"), MISC("Misc");

    public String name;
    Category(String name){
        this.name = name;
    }
}