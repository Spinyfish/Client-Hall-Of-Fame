/**
 * @project Myth
 * @author CodeMan
 * @at 24.09.22, 22:08
 */
package dev.myth.ui.clickgui.dropdowngui;

import dev.myth.api.feature.Feature;
import dev.myth.api.setting.Setting;
import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.shader.list.DropShadowUtil;
import dev.myth.features.display.ClickGuiFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.*;
import dev.myth.ui.clickgui.Component;
import dev.myth.ui.clickgui.dropdowngui.settings.*;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class ModuleButton extends dev.myth.ui.clickgui.Component {

    /** Feature */
    private final Feature feature;


    public ModuleButton(Feature feature) {
        this.feature = feature;

        for(Setting<?> setting : feature.getSettings()) {
            if(setting instanceof BooleanSetting) {
                getChildren().add(new SwitchComponent((BooleanSetting) setting));
            } else if(setting instanceof NumberSetting) {
                getChildren().add(new SliderComponent((NumberSetting) setting));
            } else if(setting instanceof EnumSetting) {
                getChildren().add(new EnumComponent((EnumSetting<?>) setting));
            }else if (setting instanceof ColorSetting) {
                getChildren().add(new ColorComponent((ColorSetting) setting));
            }else if (setting instanceof ListSetting) {
                getChildren().add(new ListComponent((ListSetting) setting));
            }
        }
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        GlStateManager.resetColor();
        ClickGuiFeature clickGui = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ClickGuiFeature.class);
        int xLOL = clickGui.enabledButton.is(ClickGuiFeature.EnabledButton.TEXT) ? MathUtil.calculateMiddle(feature.getName(), FontLoaders.SFUI_16, 51, 0) : 0;
        RenderUtil.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), feature.isEnabled() && clickGui.enabledButton.is(ClickGuiFeature.EnabledButton.FULL) ? clickGui.colorSetting.getValue() : new Color(30, 30, 30));
        FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
        if (clickGui.shaderSettings.isEnabled("Glow") && feature.isEnabled() && clickGui.enabledButton.is(ClickGuiFeature.EnabledButton.TEXT)) {
            DropShadowUtil.start();
            FontLoaders.BOLD.drawString(this.feature.getName(), (float) (this.getX() + 4 + xLOL), (float) (this.getY() + 6), clickGui.enabledButton.is(ClickGuiFeature.EnabledButton.TEXT) ? clickGui.colorSetting.getColor() : -1);
            DropShadowUtil.stop(10, clickGui.colorSetting.getValue());
        }
        FontLoaders.BOLD.drawString(this.feature.getName(), (float) (this.getX() + 4 + xLOL), (float) (this.getY() + 6), clickGui.enabledButton.is(ClickGuiFeature.EnabledButton.TEXT) && feature.isEnabled() ? clickGui.colorSetting.getColor() : -1);

        if(isExtended()) {
            super.drawComponent(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(MouseUtil.isHovered(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            if(mouseButton == 0) {
                feature.toggle();
            } else if(mouseButton == 1) {
                setExtended(!isExtended());
            }
        }
        if(!isExtended()) return false;
        getChildren().forEach(child -> child.mouseClicked(mouseX, mouseY, mouseButton));
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY) {
        getChildren().forEach(child -> child.mouseReleased(mouseX, mouseY));
        return false;
    }

    @Override
    public void guiClosed() {
        getChildren().forEach(dev.myth.ui.clickgui.Component::guiClosed);
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        getChildren().forEach(child -> child.keyTyped(typedChar, keyCode));
        return false;
    }

    @Override
    public double getFullHeight() {
        double height = this.getHeight();
        if(isExtended()) {
            height += getChildren().stream().filter(dev.myth.ui.clickgui.Component::isVisible).mapToDouble(Component::getFullHeight).sum();
        }
        return height;
    }

    @Override
    public boolean isExtendable() {
        return true;
    }
}
