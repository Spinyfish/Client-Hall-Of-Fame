package io.github.raze.screen.collection.menu.components.impl.modules;

import io.github.raze.Raze;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.screen.collection.menu.UISettingManager;
import io.github.raze.screen.collection.menu.components.api.BaseComponent;
import io.github.raze.settings.system.BaseSetting;
import io.github.raze.utilities.collection.fonts.CFontUtil;
import io.github.raze.utilities.collection.visual.MouseUtil;
import io.github.raze.utilities.collection.visual.RenderUtil;

import java.util.List;

public class ModuleComponent extends BaseComponent {

    public int index;

    public BaseModule module;
    public CategoryComponent parent;

    public boolean binding;

    List<BaseSetting> list;

    public ModuleComponent(BaseModule module, CategoryComponent parent, double x, double y, double width, double height, int index) {
        this.module = module;
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.index = index;
        this.list = Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.getSettingsByModule(module);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        if (parent.isDragging()) {
            setX(parent.getX());
            setY(parent.getY() + parent.getHeight() + getHeight() * index);
        }

        RenderUtil.rectangle(
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                module.isEnabled() ? Raze.INSTANCE.MANAGER_REGISTRY.THEME_REGISTRY.getCurrentTheme().getAccent()
                                   : Raze.INSTANCE.MANAGER_REGISTRY.THEME_REGISTRY.getCurrentTheme().getBackground()
        );

        CFontUtil.Jello_Light_20.getRenderer().drawString(
                !isBinding() ? module.getName() : "Waiting...",
                getX() + getHeight() / 2 - CFontUtil.Jello_Light_20.getRenderer().getHeight() / 2.0D,
                getY() + getHeight() / 2 - CFontUtil.Jello_Light_20.getRenderer().getHeight() / 2.0D,
                module.isEnabled() ? Raze.INSTANCE.MANAGER_REGISTRY.THEME_REGISTRY.getCurrentTheme().getBackground()
                                   : Raze.INSTANCE.MANAGER_REGISTRY.THEME_REGISTRY.getCurrentTheme().getForeground()
        );

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.isInside(mouseX, mouseY, x, y, width, height)) {
            switch (mouseButton) {
                case 0: {
                    module.toggle();
                    break;
                }
                case 1: {
                    if (list.isEmpty()) {
                        return;
                    }

                    mc.displayGuiScreen(new UISettingManager(Raze.INSTANCE.SCREEN_REGISTRY.uiModuleManager, module));
                    break;
                }
                case 2: {
                    setBinding(true);
                    break;
                }
                default: {
                    break;
                }
            }
        }

        super.click(mouseX, mouseY, mouseButton);
    }

    @Override
    public void release(int mouseX, int mouseY, int button) {
        super.release(mouseX, mouseY, button);
    }

    @Override
    public void type(char typed, int keyCode) {

        if (isBinding()) {
            module.setKeyCode(keyCode);
            setBinding(false);
        }

        super.type(typed, keyCode);
    }

    public boolean isBinding() {
        return binding;
    }

    public void setBinding(boolean binding) {
        this.binding = binding;
    }
}
