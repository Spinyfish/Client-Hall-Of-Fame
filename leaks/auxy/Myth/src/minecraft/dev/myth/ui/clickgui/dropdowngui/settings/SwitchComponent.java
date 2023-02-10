/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 20:05
 */
package dev.myth.ui.clickgui.dropdowngui.settings;

import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.animation.Animation;
import dev.myth.api.utils.render.animation.Easings;
import dev.myth.features.display.ClickGuiFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.BooleanSetting;
import dev.myth.ui.clickgui.Component;

public class SwitchComponent extends Component {

    private final BooleanSetting setting;
    private final Animation animation;

    public SwitchComponent(BooleanSetting setting) {
        this.setting = setting;

        animation = new Animation();

        if(setting.getValue()) animation.setValue(1);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        animation.updateAnimation();
        ClickGuiFeature clickGui = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ClickGuiFeature.class);
        RenderUtil.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorUtil.GRAY_17);
        FontLoaders.BOLD.drawStringWithShadow(this.setting.getDisplayName(), (float) (this.getX() + 4), (float) (this.getY() + 6), -1);

        double switchWidth = 20;

        RenderUtil.drawRect(this.getX() + this.getWidth() - switchWidth - 5, this.getY() + getHeight() / 3, switchWidth, this.getHeight() / 3, setting.getValue() ? clickGui.colorSetting.getValue().darker() : ColorUtil.GRAY_45);
        RenderUtil.drawRect(this.getX() + this.getWidth() - 30 + switchWidth * animation.getValue(), this.getY() + getHeight() / 3 - 3, 5 + 2, this.getHeight() / 3 + 6, setting.getValue() ? clickGui.colorSetting.getValue() : ColorUtil.GRAY_45);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(MouseUtil.isHovered(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            if(mouseButton == 0) {
                setting.setValue(!setting.getValue());
                animation.animate(setting.getValue() ? 1 : 0, 500, Easings.BOUNCE_OUT);
            }
        }
        return false;
    }

    @Override
    public boolean isVisible() {
        return setting.isVisible();
    }
}
