package dev.myth.builder.items.impl;

import dev.myth.api.utils.render.RenderUtil;
import dev.myth.builder.BuilderTab;
import dev.myth.builder.items.Item;
import dev.myth.builder.items.ItemList;

import java.awt.*;

public class ItemRect extends Item {
    public ItemRect(int x, int y, double width, double height, final BuilderTab type) {
        super("rect", x, y, width, height, type);
    }

    @Override
    public void draw() {
        RenderUtil.drawRect(x, y, width, height, Color.BLACK);
        super.draw();
    }
}
