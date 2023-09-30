package com.alan.clients.ui.click.dropdown;

import com.alan.clients.Client;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.impl.render.ClickGUI;
import com.alan.clients.ui.click.dropdown.components.CategoryComponent;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DropdownClickGUI extends GuiScreen {

    private final List<CategoryComponent> categoryComponents = new ArrayList<>();

    @Getter
    private final Color mainColor = new Color(30, 30, 30);

    public DropdownClickGUI() {
        double posX = 20;

        for (final Category category : Category.values()) {
            switch (category) {
                case SEARCH:
                    continue;
            }

            this.categoryComponents.add(new CategoryComponent(category, posX));
            posX += 110;
        }
    }

    public void render() {
        for (final CategoryComponent category : categoryComponents) {
            category.render();
        }
    }

    public void bloom() {
        categoryComponents.forEach(CategoryComponent::bloom);
    }

    @Override
    public void onGuiClosed() {
        Client.INSTANCE.getModuleManager().get(ClickGUI.class).toggle();
    }
}
