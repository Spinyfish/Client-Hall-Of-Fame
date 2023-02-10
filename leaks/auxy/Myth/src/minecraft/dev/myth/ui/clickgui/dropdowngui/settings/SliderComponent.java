/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 20:23
 */
package dev.myth.ui.clickgui.dropdowngui.settings;

import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.features.display.ClickGuiFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.NumberSetting;
import dev.myth.ui.clickgui.Component;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

public class SliderComponent extends Component {

    private final NumberSetting setting;
    private boolean dragging;
    private double mouseX, mouseY;

    public SliderComponent(NumberSetting setting) {
        this.setting = setting;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        RenderUtil.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorUtil.GRAY_17);
        FontLoaders.BOLD.drawString(this.setting.getDisplayName(), (float) (this.getX() + 4), (float) (this.getY() + 4), -1);
        ClickGuiFeature clickGui = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ClickGuiFeature.class);

        double x = this.getX() + 4;
        double width = this.getWidth() - 8;
        if(dragging) {
            double value = (mouseX - x) / width;
            if(value < 0) value = 0;
            if(value > 1) value = 1;
            setting.setValue(MathUtil.round(value * (setting.getMax() - setting.getMin()) + setting.getMin(), setting.getInc()));
        }

        FontLoaders.BOLD.drawString(setting.getValueDisplayString() + "", (float) (this.getX() + this.getWidth() - FontLoaders.BOLD.getStringWidth(setting.getValueDisplayString() + "") - 4), (float) (this.getY() + 4), -1);

        RenderUtil.drawRect(x, this.getY() + getHeight() - 5, width, 2, ColorUtil.GRAY_45);
        RenderUtil.drawRect(x, this.getY() + getHeight() - 5, ((setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin())) * width, 2, clickGui.colorSetting.getValue());
        RenderUtil.drawCircle(x + ((setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin())) * width, this.getY() + getHeight() - 4, 3, ColorUtil.GRAY_17.getRGB(), clickGui.colorSetting.getColor(), 1.5f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(MouseUtil.isHovered(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            if(mouseButton == 0) {
                dragging = true;
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);

        if(MouseUtil.isHovered(this.mouseX, this.mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            double newValue = setting.getValue();
            switch (keyCode) {
                case Keyboard.KEY_RIGHT:
                    newValue += setting.getInc();
                    break;
                case Keyboard.KEY_LEFT:
                    newValue -= setting.getInc();
                    break;
            }
            setting.setValue(MathHelper.clamp_double(MathUtil.round(newValue, setting.getInc()), setting.getMin(), setting.getMax()));
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY) {
        dragging = false;
        return false;
    }

    @Override
    public void guiClosed() {
        dragging = false;
    }

    @Override
    public boolean isVisible() {
        return setting.isVisible();
    }
}