package me.felix.clickgui.renderables;

import de.lirium.Client;
import de.lirium.base.setting.ISetting;
import de.lirium.base.setting.SettingRegistry;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.misc.KeyUtil;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.StencilUtil;
import god.buddy.aot.BCompiler;
import me.felix.clickgui.abstracts.ClickGUIHandler;
import me.felix.clickgui.renderables.settings.RenderableCheckbox;
import me.felix.clickgui.renderables.settings.RenderableComboBox;
import me.felix.clickgui.renderables.settings.RenderableSlider;
import me.felix.util.dropshadow.JHLabsShaderRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RenderableModule extends ClickGUIHandler {

    public final ModuleFeature moduleFeature;

    public double x, y;

    public int yAxisAdditional = 0;
    public boolean isHovering, extended;

    private final ArrayList<RenderableCheckbox> renderableCheckboxes;

    private final ArrayList<RenderableSlider> renderableSliders;

    private final ArrayList<RenderableComboBox> renderableComboBoxes;

    public RenderableModule(final ModuleFeature moduleFeature) {
        this.moduleFeature = moduleFeature;
        this.renderableCheckboxes = new ArrayList<>();
        this.renderableSliders = new ArrayList<>();
        this.renderableComboBoxes = new ArrayList<>();

        for (ISetting<?> setting : SettingRegistry.getSettings(moduleFeature)) {
            if (setting instanceof CheckBox) {
                renderableCheckboxes.add(new RenderableCheckbox((CheckBox) setting));
            } else if (setting instanceof SliderSetting) {
                this.renderableSliders.add(new RenderableSlider((SliderSetting<Number>) setting));
            } else if (setting instanceof ComboBox) {
                this.renderableComboBoxes.add(new RenderableComboBox((ComboBox<String>) setting));
            }
        }

    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY) {
        for (RenderableSlider renderableSlider : renderableSliders)
            if (renderableSlider.sliderElement.isVisible())
                renderableSlider.onMouseReleased(mouseX, mouseY);

    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseKey) {

        for (RenderableCheckbox checkBox : renderableCheckboxes) {
            if (checkBox.checkBox.isVisible())
                checkBox.onMouseClicked(mouseX, mouseY, mouseKey);
        }


        for (RenderableComboBox comboBox : renderableComboBoxes) {
            if (comboBox.comboBox.isVisible())
                comboBox.onMouseClicked(mouseX, mouseY, mouseKey);
        }

        for (RenderableSlider renderableSlider : renderableSliders) {
            if (renderableSlider.sliderElement.isVisible())
                renderableSlider.onMouseClicked(mouseX, mouseY, mouseKey);
        }

        if (!isHovering) return;
        switch (mouseKey) {
            case 0:
                this.moduleFeature.setEnabled(!moduleFeature.isEnabled());
                break;
            case 1:
                this.extended = !extended;
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, int x, int y) {
        this.x = x;
        this.y = y;

        this.isHovering = this.isHovered(mouseX, mouseY, x, y, width, height);

        final FontRenderer fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 20);

        JHLabsShaderRenderer.renderShadow(x, y, width, height, 20, isHovering ? new Color(40, 40, 40) : new Color(20, 20, 20));
        Gui.drawRect(x, y, x + width, y + height, isHovering ? new Color(40, 40, 40).getRGB() : new Color(20, 20, 20).getRGB());

        //Module toggled code block
        {
            if (moduleFeature.isEnabled()) {

                Gui.drawRect(x, y, x + width, y + height, new Color(20, 20, 20).getRGB());
                StencilUtil.init();
                Gui.drawRect(x, y, x + width, y + height, isHovering ? new Color(40, 40, 40).getRGB() : new Color(20, 20, 20).getRGB());
                StencilUtil.readBuffer(1);
                RenderUtil.renderEnchantment();
                StencilUtil.uninit();

                GlStateManager.disableBlend();
                GlStateManager.disableLighting();
                GlStateManager.resetColor();
            }
        }
        fontRenderer.drawString(moduleFeature.getName(), calculateMiddle(moduleFeature.getName(), fontRenderer, x, width), y + 2, -1);

        final AtomicInteger yAxis = new AtomicInteger(0);

        if (extended) {
            //Code block for settings
            {
                {
                    for (RenderableCheckbox checkBox : renderableCheckboxes)
                        if (checkBox.checkBox.isVisible())
                            checkBox.drawScreen(mouseX, mouseY, x, y + yAxis.addAndGet(16));

                    for (RenderableSlider renderableSlider : renderableSliders)
                        if (renderableSlider.sliderElement.isVisible())
                            renderableSlider.drawScreen(mouseX, mouseY, x, y + yAxis.addAndGet(16));

                    for (RenderableComboBox comboBox : renderableComboBoxes)
                        if (comboBox.comboBox.isVisible()) {
                            final int addition = comboBox.draw(mouseX, mouseY, x, y + yAxis.addAndGet(16));
                            yAxis.addAndGet(addition);
                        }

                }
            }
        }
        yAxisAdditional = yAxis.get();

    }
}
