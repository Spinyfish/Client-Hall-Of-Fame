package dev.myth.builder.items;

import dev.myth.builder.BuilderTab;
import lombok.Getter;
import lombok.Setter;

public class Item {

    @Getter @Setter
    public int x, y;

    @Getter @Setter
    public String name;

    @Getter @Setter
    public double width, height;

    @Getter @Setter
    public BuilderTab type;

    public float dragX, dragY;


    public Item(final String name, final int x, final int y, final BuilderTab type) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public Item(final String name, final int x, final int y, final double width, final double height, final BuilderTab type) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    public void draw() {}


}
