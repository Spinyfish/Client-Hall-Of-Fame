/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 16:06
 */

package cc.swift.gui.clickgui;

import cc.swift.Swift;
import cc.swift.gui.clickgui.components.*;
import cc.swift.module.Module;
import cc.swift.util.ChatUtil;
import cc.swift.util.render.animation.Animation;
import cc.swift.util.render.animation.Easings;
import cc.swift.value.Value;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class ClickGui extends GuiScreen {

    private final ArrayList<PanelComponent> panels = new ArrayList<>();

    private final Animation animation = new Animation();
    private boolean closing;

    public ClickGui() {
        final double panelWidth = 135, panelHeight = 22, moduleWidth = 135, moduleHeight = 16, settingWidth = 135, settingHeight = 15;

        for (int i = 0; i < Module.Category.values().length; i++) {
            Module.Category category = Module.Category.values()[i];
            PanelComponent panelComponent = new PanelComponent(category.getName(), category.getIcon(), 10 + (panelWidth + 10) * i, 10, panelWidth, panelHeight);
            panels.add(panelComponent);

            for (Module module : Swift.INSTANCE.getModuleManager().getModules().values()) {
                if (module.getCategory() == category) {
                    ModuleComponent moduleComponent = new ModuleComponent(module, moduleWidth, moduleHeight);
                    panelComponent.getChildren().add(moduleComponent);

                    for (Value<?> value : module.getValueList()) {
                        if (value instanceof BooleanValue) {
                            moduleComponent.getChildren().add(new BooleanValueComponent((BooleanValue) value, settingWidth, settingHeight));
                        } else if (value instanceof DoubleValue) {
                            moduleComponent.getChildren().add(new DoubleValueComponent((DoubleValue) value, settingWidth, settingHeight));
                        } else if (value instanceof ModeValue) {
                            moduleComponent.getChildren().add(new ModeValueComponent((ModeValue<?>) value, settingWidth, settingHeight));
                        }
                    }
                }
            }

            panelComponent.getChildren().sort((c1, c2) -> {
                if (c1 instanceof ModuleComponent && c2 instanceof ModuleComponent) {
                    Module m1 = ((ModuleComponent) c1).getModule();
                    Module m2 = ((ModuleComponent) c2).getModule();
                    return m1.getName().compareTo(m2.getName());
                }
                return 0;
            });
        }
    }

    @Override
    public void initGui() {
        animation.animate(1, 300, Easings.BACK_OUT);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean isAnimationAlive = animation.updateAnimation();

        if (!isAnimationAlive && closing) {
            mc.displayGuiScreen(null);
            return;
        }

        double middleX = width / 2D, middleY = height / 2D;

        if (isAnimationAlive) {
            GL11.glPushMatrix();
            GL11.glTranslated(middleX, middleY, 0);
            GL11.glScaled(animation.getValue(), animation.getValue(), 0);
            GL11.glTranslated(-middleX, -middleY, 0);
        }

        panels.forEach(panel -> panel.draw(mouseX, mouseY));

        if (isAnimationAlive) {
            GL11.glTranslated(middleX, middleY, 0);
            GL11.glScaled(-animation.getValue(), -animation.getValue(), 0);
            GL11.glTranslated(-middleX, -middleY, 0);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        panels.forEach(panel -> panel.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        panels.forEach(panel -> panel.mouseReleased(mouseX, mouseY));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        panels.forEach(panel -> panel.keyTyped(typedChar, keyCode));

        if (keyCode == 1 && !closing) {
            animation.animate(0, 200, Easings.BACK_IN);
            closing = true;
            mc.inGameHasFocus = true;
            mc.mouseHelper.grabMouseCursor();
        }
    }

    @Override
    public void onGuiClosed() {
        panels.forEach(GuiComponent::onGuiClosed);
        animation.setValue(0);
        animation.updateAnimation();
        closing = false;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
