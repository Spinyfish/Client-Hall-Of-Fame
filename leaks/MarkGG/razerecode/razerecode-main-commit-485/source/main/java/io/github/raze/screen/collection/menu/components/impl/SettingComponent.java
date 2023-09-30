package io.github.raze.screen.collection.menu.components.impl;

import io.github.raze.Raze;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.screen.collection.menu.components.api.BaseComponent;
import io.github.raze.screen.collection.menu.components.impl.settings.ArrayComponent;
import io.github.raze.screen.collection.menu.components.impl.settings.BooleanComponent;
import io.github.raze.screen.collection.menu.components.impl.settings.NumberComponent;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.settings.collection.BooleanSetting;
import io.github.raze.settings.collection.NumberSetting;
import io.github.raze.settings.system.BaseSetting;
import io.github.raze.utilities.collection.visual.MouseUtil;
import io.github.raze.utilities.collection.visual.RenderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SettingComponent extends BaseComponent {

    public BaseModule module;
    public List<BaseComponent> settings;

    public SettingComponent(BaseModule module, double x, double y, double width, double height) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.settings = new ArrayList<BaseComponent>();

        update();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        if (isOpen()) {
            return;
        }

        RenderUtil.rectangle(
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                Raze.INSTANCE.MANAGER_REGISTRY.THEME_REGISTRY.getCurrentTheme().getBackground()
        );

        settings.forEach(setting -> {
            setting.render(mouseX, mouseY, partialTicks);
        });

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) {

        if (isOpen()) {
            return;
        }

        if (MouseUtil.isInside(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            update();

            settings.forEach(setting -> {
                setting.click(mouseX, mouseY, mouseButton);
            });

        }

        super.click(mouseX, mouseY, mouseButton);
    }

    @Override
    public void release(int mouseX, int mouseY, int button) {

        if (isOpen()) {
            return;
        }

        if (MouseUtil.isInside(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {

            update();

            settings.forEach(setting -> {
                setting.release(mouseX, mouseY, button);
            });

        }

        super.release(mouseX, mouseY, button);
    }

    @Override
    public void type(char typed, int keyCode) {

        if (isOpen()) {
            return;
        }

        update();

        settings.forEach(setting -> {
            setting.type(typed, keyCode);
        });

        super.type(typed, keyCode);
    }

    private void update() {
        settings.clear();

        List<BaseSetting> list = Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.getSettingsByModule(module)

                .stream()

                .filter(setting -> {
                    return !setting.isHidden();
                })

                .collect(Collectors.toList());

        for (BaseSetting setting : list) {
            int index = list.indexOf(setting);

            if (setting instanceof ArraySetting) {
                ArraySetting arraySetting = (ArraySetting) setting;

                settings.add(new ArrayComponent(arraySetting, getX(), getY() + index * 20, getWidth(), 20));
            }

            if (setting instanceof BooleanSetting) {
                BooleanSetting booleanSetting = (BooleanSetting) setting;

                settings.add(new BooleanComponent(booleanSetting, getX(), getY() + index * 20, getWidth(), 20));
            }

            if (setting instanceof NumberSetting) {
                NumberSetting numberSetting = (NumberSetting) setting;

                settings.add(new NumberComponent(numberSetting, getX(), getY() + index * 20, getWidth(), 20));
            }
        }
    }
}
