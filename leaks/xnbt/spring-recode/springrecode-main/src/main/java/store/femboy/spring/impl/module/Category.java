package store.femboy.spring.impl.module;

public enum Category {
    PLAYER("B"),
    COMBAT("D"),
    MOVEMENT("A"),
    EXPLOIT("L"),
    VISUAL("C"),
    CLIENT("M");

    public String name;
    Category(String name){
        this.name = name;
    }
}
