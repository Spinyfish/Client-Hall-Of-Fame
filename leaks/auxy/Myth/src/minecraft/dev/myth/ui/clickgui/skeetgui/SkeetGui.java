/**
 * @project Myth
 * @author CodeMan
 * @at 24.09.22, 22:07
 */
package dev.myth.ui.clickgui.skeetgui;

import dev.myth.api.utils.render.animation.Animation;
import dev.myth.api.utils.render.animation.Easings;
import dev.myth.features.display.ClickGuiFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.ui.clickgui.Component;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class SkeetGui extends GuiScreen {

    public static SkeetGui INSTANCE;
    private MainPanel mainPanel;
    private Animation animation = new Animation();
    private boolean closing;
    private ClickGuiFeature clickGuiFeature;
    @Setter @Getter private Component focusedComponent;

    public SkeetGui() {
        INSTANCE = this;
        mainPanel = new MainPanel();
    }

    @Override
    public void initGui() {
        clickGuiFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ClickGuiFeature.class);
        closing = false;
        animation.setValue(0);
        animation.animate(1, 200, Easings.NONE);
        Keyboard.enableRepeatEvents(true);

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!animation.updateAnimation()) {
            if (closing) {
                mc.setIngameFocus();
                return;
            }
        }

        mainPanel.drawComponent(mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        mainPanel.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        mainPanel.mouseReleased(mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(mainPanel.keyTyped(typedChar, keyCode)) return;

        if((keyCode == 1 || keyCode == clickGuiFeature.getKeyBind()) && !closing) {
            closing = true;
            animation.animate(0, 200, Easings.NONE);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        mainPanel.guiClosed();
        Keyboard.enableRepeatEvents(false);

    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public int getAlpha() {
        return (int) (255 * animation.getValue());
    }

    public int getColor(int color) {
        return color & 0x00FFFFFF | getAlpha() << 24;
    }

    public int getColor() {
        return clickGuiFeature.colorSetting.getColor();
    }
}
