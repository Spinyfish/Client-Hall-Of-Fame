/**
 * @project Myth
 * @author CodeMan
 * @at 24.09.22, 22:10
 */
package dev.myth.ui.clickgui.skeetgui;

import dev.myth.api.feature.Feature;
import dev.myth.api.setting.Setting;
import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.*;
import dev.myth.ui.clickgui.ChildComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MainPanel extends ChildComponent {

    private boolean dragging, scaling;
    private double dragX, dragY;
    private ResourceLocation chainmail = new ResourceLocation("myth/skeetchainmail.png");
    private String[] tabicons = {"A", "G", "J", "D", "F", "I"};
    private Color outlineColor = new Color(8, 8, 9);
    private Color outlineColor2 = new Color(60, 60, 61);
    private Color outlineColor3 = new Color(40, 40, 41);
    private int tabIndex = 0;
    private TabComponent tabComponent;

    public MainPanel() {
        super(null, 100, 100, 331, 281);

        for (Feature.Category category : Feature.Category.values()) {
            TabComponent tabComponent = new TabComponent(this, 32 + 3, 4.5, getWidth() - 32 - 6, getHeight() - 3 - 4.5);

            for (Feature feature : ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeaturesInCategory(category)) {
                GroupBoxComponent groupBoxComponent = new GroupBoxComponent(tabComponent, feature.getName(), 0, 0, 50, 50);
                CheckBoxComponent checkBoxComponent = new CheckBoxComponent(groupBoxComponent, "Enabled", 5, 7, feature::isEnabled, (toggled) -> {
                    if (toggled != feature.isEnabled()) feature.toggle();
                }, () -> true);
                KeyBindComponent keyBindComponent = new KeyBindComponent(checkBoxComponent, checkBoxComponent.getWidth() + 20, -1, feature::getKeyBind, feature::setKeyBind, () -> true);
                checkBoxComponent.getChildren().add(keyBindComponent);
                groupBoxComponent.getChildren().add(checkBoxComponent);

                for (Setting<?> setting : feature.getSettings()) {
                    if (setting instanceof BooleanSetting) {
                        BooleanSetting booleanSetting = (BooleanSetting) setting;
                        CheckBoxComponent checkBoxComponent1 = new CheckBoxComponent(groupBoxComponent, booleanSetting.getDisplayName(), 5, 7, booleanSetting::getValue, booleanSetting::setValue, setting::isVisible);
                        groupBoxComponent.getChildren().add(checkBoxComponent1);
                    }
                    if (setting instanceof NumberSetting) {
                        NumberSetting numberSetting = (NumberSetting) setting;
                        SliderComponent sliderComponent = new SliderComponent(groupBoxComponent, numberSetting.getDisplayName(), 12, 7, numberSetting::getValue, numberSetting::setValue, numberSetting::getValueDisplayString, numberSetting::getMin, numberSetting::getMax, numberSetting::getInc, setting::isVisible);
                        groupBoxComponent.getChildren().add(sliderComponent);
                    }
                    if (setting instanceof EnumSetting<?>) {
                        EnumSetting<?> enumSetting = (EnumSetting<?>) setting;
                        DropdownComponent enumComponent = new DropdownComponent(groupBoxComponent, enumSetting.getDisplayName(), 12, 7, false, enumSetting::getValue, enumSetting::setValueByIndex, enumSetting::getConstants, () -> null, setting::isVisible);
                        groupBoxComponent.getChildren().add(enumComponent);
                    }
                    if (setting instanceof ListSetting<?>) {
                        ListSetting<?> listSetting = (ListSetting<?>) setting;
                        DropdownComponent listComponent = new DropdownComponent(groupBoxComponent, listSetting.getDisplayName(), 12, 7, true, () -> null, listSetting::setValueByIndex, listSetting::getAddons, listSetting::getSelected, setting::isVisible);
                        groupBoxComponent.getChildren().add(listComponent);
                    }
                    if(setting instanceof TextBoxSetting) {
                        TextBoxSetting textBoxSetting = (TextBoxSetting) setting;
                        TextBoxComponent textBoxComponent = new TextBoxComponent(groupBoxComponent, textBoxSetting.getDisplayName(), 12, 7, textBoxSetting::getValue, textBoxSetting::setValue, setting::isVisible);
                        groupBoxComponent.getChildren().add(textBoxComponent);
                    }
                }

                tabComponent.getChildren().add(groupBoxComponent);
            }

            getChildren().add(tabComponent);
        }
        tabComponent = (TabComponent) getChildren().get(tabIndex);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        ScaledResolution sr = new ScaledResolution(MC);
        if (dragging) {
            setX(MathHelper.clamp_double(mouseX - dragX, 0, sr.getScaledWidth() - getWidth()));
            setY(MathHelper.clamp_double(mouseY - dragY, 0, sr.getScaledHeight() - getHeight()));
        }
        if (scaling) {
            setHeight(tabicons.length * 32 + 50);
            setWidth(250);

            setWidth(MathHelper.clamp_double(MathUtil.round(mouseX - getX(), 2), 250, 650));
            doLog("Width: " + getWidth());
            setHeight(MathHelper.clamp_double(mouseY - getY(), tabicons.length * 32 + 50, 550));
        }
        double outlineWidth = 3;
        Gui.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), SkeetGui.INSTANCE.getColor(outlineColor.getRGB()));
        Gui.drawRect(getX() + 0.5, getY() + 0.5, getX() + getWidth() - 0.5, getY() + getHeight() - 0.5, SkeetGui.INSTANCE.getColor(outlineColor2.getRGB()));
        Gui.drawRect(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, SkeetGui.INSTANCE.getColor(outlineColor3.getRGB()));
        Gui.drawRect(getX() + 2.5, getY() + 2.5, getX() + getWidth() - 2.5, getY() + getHeight() - 2.5, SkeetGui.INSTANCE.getColor(outlineColor2.getRGB()));

        if (false) {
            for (double i = 0; i < getWidth() - 6; i += 0.5) {
                Gui.drawRect(getX() + 3 + i, getY() + 5, getX() + 3 + i + 0.5, getY() + getHeight() - 4, ColorUtil.rainbow((int) (i * 2)).darker().darker().getRGB());
            }
        } else {
            Gui.drawRect(getX() + 3, getY() + 3, getX() + getWidth() - 3, getY() + getHeight() - 3, SkeetGui.INSTANCE.getColor(0x151515));
        }

        if (SkeetGui.INSTANCE.getAlpha() > 40) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(getX() + 3, getY() + 3, getWidth() - 6, getHeight() - 6);
            RenderUtil.drawImage(getX() + outlineWidth, getY() + outlineWidth, 325, 275, chainmail);

            if (getWidth() - 6 > 325) {
                RenderUtil.drawImage(getX() + outlineWidth + 325, getY() + outlineWidth, 325, 275, chainmail);
            }
            if (getHeight() - 6 > 275) {
                RenderUtil.drawImage(getX() + outlineWidth, getY() + outlineWidth + 275, 325, 275, chainmail);
            }
            if (getWidth() - 6 > 325 && getHeight() - 6 > 275) {
                RenderUtil.drawImage(getX() + outlineWidth + 325, getY() + outlineWidth + 275, 325, 275, chainmail);
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        double selectorSize = 32;
        double selectorY = 10 + outlineWidth + (selectorSize * tabIndex);
        Gui.drawRect(getX() + outlineWidth, getY() + outlineWidth, getX() + outlineWidth + selectorSize, getY() + selectorY, SkeetGui.INSTANCE.getColor(outlineColor3.getRGB()));
        Gui.drawRect(getX() + outlineWidth, getY() + outlineWidth, getX() + outlineWidth + selectorSize - 0.5, getY() + selectorY - 0.5, SkeetGui.INSTANCE.getColor(outlineColor.getRGB()));
        Gui.drawRect(getX() + outlineWidth, getY() + outlineWidth, getX() + outlineWidth + selectorSize - 1, getY() + selectorY - 1, SkeetGui.INSTANCE.getColor(0x0C0C0C));

        Gui.drawRect(getX() + outlineWidth, getY() + selectorY + selectorSize, getX() + outlineWidth + selectorSize, getY() + getHeight() - outlineWidth, SkeetGui.INSTANCE.getColor(outlineColor3.getRGB()));
        Gui.drawRect(getX() + outlineWidth, getY() + selectorY + selectorSize + 0.5, getX() + outlineWidth + selectorSize - 0.5, getY() + getHeight() - outlineWidth, SkeetGui.INSTANCE.getColor(outlineColor.getRGB()));
        Gui.drawRect(getX() + outlineWidth, getY() + selectorY + selectorSize + 1, getX() + outlineWidth + selectorSize - 1, getY() + getHeight() - outlineWidth, SkeetGui.INSTANCE.getColor(0x0C0C0C));

        Gui.drawGradientRectSideways(getX() + outlineWidth + 0.5, getY() + outlineWidth + 0.5, getX() + getWidth() / 2, getY() + outlineWidth + 1.5, SkeetGui.INSTANCE.getColor(0x319ABD), SkeetGui.INSTANCE.getColor(0xC34FB8));
        Gui.drawGradientRectSideways(getX() + getWidth() / 2, getY() + outlineWidth + 0.5, getX() + getWidth() - outlineWidth - 0.5, getY() + outlineWidth + 1.5, SkeetGui.INSTANCE.getColor(0xC34FB8), SkeetGui.INSTANCE.getColor(0xC3D833));

        outlineWidth += 1.5;

        if (SkeetGui.INSTANCE.getAlpha() > 60) {
            double tabY = 10 + outlineWidth;
            for (int i = 0; i < tabicons.length; i++) {
                String string = tabicons[i];
                boolean isHovered = MouseUtil.isHovered(mouseX, mouseY, getX() + outlineWidth, getY() + tabY, selectorSize, selectorSize);
                FontLoaders.SKEET_ICONS.drawStringWithShadow(string, (float) (getX() + outlineWidth + 16 - FontLoaders.SKEET_ICONS.getStringWidth(string) / 2) - 1, (float) (getY() + tabY + 16 - FontLoaders.SKEET_ICONS.getHeight() / 2), SkeetGui.INSTANCE.getColor(i == tabIndex || isHovered ? 0xC9C9C9 : 0x808080));
                tabY += selectorSize;
            }
        }

        tabComponent.drawComponent(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (MouseUtil.isHovered(mouseX, mouseY, getX() + 3, getY() + 13, 32, getChildren().size() * 32)) {
                double y = mouseY - getY() - 13;
                tabIndex = (int) (y / 32);
                tabComponent = (TabComponent) getChildren().get(tabIndex);
                return true;
            }
            if (MouseUtil.isHovered(mouseX, mouseY, getX() + getWidth() - 6, getY() + getHeight() - 6, 6, 6)) {
                scaling = true;
                return true;
            }
            if (tabComponent.mouseClicked(mouseX, mouseY, mouseButton)) return true;
            if (MouseUtil.isHovered(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
                dragging = true;
                dragX = mouseX - getX();
                dragY = mouseY - getY();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY) {
        dragging = false;
        scaling = false;

        if (tabComponent.mouseReleased(mouseX, mouseY)) return true;
        return false;
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        return tabComponent.keyTyped(typedChar, keyCode);
    }

    @Override
    public void guiClosed() {
        tabComponent.guiClosed();
        dragging = false;
        scaling = false;
        super.guiClosed();
    }
}
