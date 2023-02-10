/**
 * @project Myth
 * @author CodeMan
 * @at 24.09.22, 22:08
 */
package dev.myth.ui.clickgui.dropdowngui;

import dev.myth.api.feature.Feature;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.animation.*;
import dev.myth.features.display.ClickGuiFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.ui.clickgui.Component;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGui extends GuiScreen {

    private ArrayList<Panel> panels = new ArrayList<>();

    public Animation animation = new Animation();
    public boolean closing;

    public ClickGui() {
        init();
    }

    private void init() {
        FeatureManager featureManager = ClientMain.INSTANCE.manager.getManager(FeatureManager.class);
        panels.clear();
        int i = 0;
        for(Feature.Category category : Feature.Category.values()) {
            Panel panel = new Panel(category.getName(), i * 130 + 10, 20, 120, 20);

            for(Feature feature : featureManager.getFeaturesInCategory(category)) {
                panel.getChildren().add(new ModuleButton(feature));
            }

            panels.add(panel);
            i++;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        closing = false;
        animation.setValue(0);
        animation.animate(1, 1500, Easings.ELASTIC_OUT);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        ClickGuiFeature clickGuiFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ClickGuiFeature.class);

        if (clickGuiFeature.backgroundSettings.isEnabled("Gardient"))
            Gui.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), clickGuiFeature.gardientColor.getColor(), new Color(0, 0, 0, 30).getRGB());
        if (animation.updateAnimation()) {
            GlStateManager.translate(width / 2f, height / 2f, 0);
            GlStateManager.scale(animation.getValue(), animation.getValue(), 0);
            GlStateManager.translate(-width / 2f, -height / 2f, 0);
        } else {
            if (closing) {
                this.mc.displayGuiScreen(null);
                if (this.mc.currentScreen == null) {
                    this.mc.setIngameFocus();
                }
                return;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.resetColor();
        RenderUtil.drawAnime();
        panels.forEach(panel -> panel.drawComponent(mouseX, mouseY));

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        panels.forEach(panel -> panel.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        panels.forEach(panel -> panel.mouseReleased(mouseX, mouseY));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if((keyCode == 1 || keyCode == ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ClickGuiFeature.class).getKeyBind()) && !closing) {
            closing = true;
            animation.animate(0, 300, Easings.BACK_IN);
        }

        panels.forEach(panel -> panel.keyTyped(typedChar, keyCode));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        panels.forEach(Component::guiClosed);
    }

}
