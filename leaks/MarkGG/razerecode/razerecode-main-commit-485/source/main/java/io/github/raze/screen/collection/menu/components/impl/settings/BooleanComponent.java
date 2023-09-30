package io.github.raze.screen.collection.menu.components.impl.settings;

import io.github.raze.Raze;
import io.github.raze.screen.collection.menu.components.api.BaseComponent;
import io.github.raze.settings.collection.BooleanSetting;
import io.github.raze.utilities.collection.fonts.CFontUtil;
import io.github.raze.utilities.collection.visual.MouseUtil;

public class BooleanComponent extends BaseComponent {

    public BooleanSetting setting;

    public BooleanComponent(BooleanSetting setting, double x, double y, double width, double height) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        CFontUtil.Jello_Medium_16.getRenderer().drawString(
                setting.getName() + ": " + setting.get(),
                getX() + getHeight() / 2 - CFontUtil.Jello_Medium_16.getRenderer().getHeight() / 2.0D,
                getY() + getHeight() / 2 - CFontUtil.Jello_Medium_16.getRenderer().getHeight() / 2.0D,
                Raze.INSTANCE.MANAGER_REGISTRY.THEME_REGISTRY.getCurrentTheme().getForeground()
        );

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) {

        if (MouseUtil.isInside(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            switch (mouseButton) {
                case 0: {
                    setting.change(!setting.get());
                    break;
                }
            }
        }

        super.click(mouseX, mouseY, mouseButton);
    }
}
