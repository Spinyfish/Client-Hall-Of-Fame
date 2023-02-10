/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 20:34
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
import dev.myth.settings.EnumSetting;
import dev.myth.ui.clickgui.Component;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class EnumComponent extends Component {

    private final EnumSetting<?> setting;

    public EnumComponent(EnumSetting<?> setting) {
        this.setting = setting;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        ClickGuiFeature clickGui = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ClickGuiFeature.class);
        RenderUtil.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorUtil.GRAY_17);
        Gui.drawRect(this.getX() + 4.5f - 1.5 + 110, getY() + 13.5, getX() + 117, getY() + 3, new Color(55, 57, 61).getRGB());
        Gui.drawRect(this.getX() + 4.5f - 1.5, getY() + 13.5, getX() + 7, getY() + 3, new Color(55, 57, 61).getRGB());
        Gui.drawRect(getX() + 2.5, getY() + 3, getX() + this.getWidth() - 2.5, (getY() + 10 + 3), new Color(27, 28, 29, 100).getRGB());

        FontLoaders.BOLD.drawString(this.setting.getDisplayName(), (float) (this.getX() + 10), (float) (this.getY() + 4.7), -1);
        FontLoaders.BOLD.drawString(this.setting.getValue().toString(), (float) (this.getX() + 112 - FontLoaders.BOLD.getStringWidth(this.setting.getValue().toString())), (float) (this.getY() + 4.7), clickGui.colorSetting.getColor());

        if(isExtended()) {
            final String[] values = this.setting.getValues();
            RenderUtil.drawRect(this.getX(), this.getY() + this.getHeight(), this.getWidth(), this.getFullHeight() - getHeight(), ColorUtil.GRAY_17);
            Gui.drawRect(getX() + 3.5, getY() + 13.5, getX() + 117, this.getY() + this.getFullHeight(), ColorUtil.GRAY_29.getRGB());
            Gui.drawGradientRect((int) (getX() + 3.5), (int) (getY() + 13.5), (int) (getX() + 117), (int) (this.getY() + this.getHeight() - 3), Color.BLACK.getRGB(), new Color(0, 0, 0, 30).getRGB());
            for(int i = 0; i < values.length; i++) {
                final String value = values[i];
                FontLoaders.BOLD.drawString(value, (float) (this.getX() + 4 + MathUtil.calculateMiddle(value, FontLoaders.SFUI_16, 51, 0)), (float) (this.getY() + i * MC.fontRendererObj.FONT_HEIGHT + this.getHeight()), value.equalsIgnoreCase(setting.getValue().toString()) ? clickGui.colorSetting.getColor() : -1);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(MouseUtil.isHovered(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            if(mouseButton == 1) {
                this.setExtended(!this.isExtended());
            }
        }
        if(MouseUtil.isHovered(mouseX, mouseY, this.getX(), this.getY() + this.getHeight(), this.getWidth(), this.getFullHeight() - this.getHeight())) {
            if (this.isExtended() && mouseButton == 0) {
                final String[] values = this.setting.getValues();
                for (int i = 0; i < values.length; i++) {
                    if (MouseUtil.isHovered(mouseX, mouseY, this.getX(), this.getY() + i * MC.fontRendererObj.FONT_HEIGHT + this.getHeight(), this.getWidth(), MC.fontRendererObj.FONT_HEIGHT)) {
                        setting.setValueByIndex(i);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public double getFullHeight() {
        return this.isExtended() ? this.setting.getValues().length * MC.fontRendererObj.FONT_HEIGHT + this.getHeight() : this.getHeight();
    }

    @Override
    public boolean isExtendable() {
        return true;
    }

    @Override
    public boolean isVisible() {
        return setting.isVisible();
    }
}
