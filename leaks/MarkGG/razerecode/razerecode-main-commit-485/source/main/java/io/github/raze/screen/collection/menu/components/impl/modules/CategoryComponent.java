package io.github.raze.screen.collection.menu.components.impl.modules;

import io.github.raze.Raze;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.screen.collection.menu.components.api.BaseComponent;
import io.github.raze.utilities.collection.fonts.CFontUtil;
import io.github.raze.utilities.collection.visual.MouseUtil;
import io.github.raze.utilities.collection.visual.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public class CategoryComponent extends BaseComponent {

    public ModuleCategory category;
    public List<ModuleComponent> components;

    public CategoryComponent(ModuleCategory category, double x, double y, double width, double height) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.components = new ArrayList<ModuleComponent>();
        List<BaseModule> modules = Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModulesByCategory(category);

        for (int index = 0; index < modules.size(); index += 1) {
            BaseModule module = modules.get(index);
            ModuleComponent component = new ModuleComponent(module, this, getX(), getY() + getHeight() + getHeight() / 1.5 * index, getWidth(), getHeight() / 1.5, index);
            this.components.add(component);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        if (isDragging()) {
            setX(mouseX - getDragX());
            setY(mouseY - getDragY());
        }

        RenderUtil.rectangle(
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                Raze.INSTANCE.MANAGER_REGISTRY.THEME_REGISTRY.getCurrentTheme().getDarkBackground()
        );

        CFontUtil.Jello_Light_24.getRenderer().drawString(
                category.getName(),
                getX() + getHeight() / 2 - CFontUtil.Jello_Light_24.getRenderer().getHeight() / 2.0D,
                getY() + getHeight() / 2 - CFontUtil.Jello_Light_24.getRenderer().getHeight() / 2.0D,
                Raze.INSTANCE.MANAGER_REGISTRY.THEME_REGISTRY.getCurrentTheme().getDarkForeground()
        );

        if (isOpen()) {
            for (BaseComponent component : components) {
                component.render(mouseX, mouseY, partialTicks);
            }
        }


        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) {

        if (MouseUtil.isInside(mouseX, mouseY, x, y, width, height)) {
            switch (mouseButton) {
                case 0: {
                    setDragging(true);
                    setDragX(mouseX - x);
                    setDragY(mouseY - y);
                    break;
                }
                case 1: {
                    setOpen(!isOpen());
                    break;
                }
                default: {
                    break;
                }
            }
        }

        if (isOpen()) {
            for (BaseComponent component : components) {
                component.click(mouseX, mouseY, mouseButton);
            }
        }

        super.click(mouseX, mouseY, mouseButton);
    }

    @Override
    public void release(int mouseX, int mouseY, int state) {
        setDragging(false);

        if (isOpen()) {
            for (BaseComponent component : components) {
                component.release(mouseX, mouseY, state);
            }
        }

        super.release(mouseX, mouseY, state);
    }

    @Override
    public void type(char typedChar, int keyCode) {

        if (isOpen()) {
            for (BaseComponent component : components) {
                component.type(typedChar, keyCode);
            }
        }

        super.type(typedChar, keyCode);
    }
}
