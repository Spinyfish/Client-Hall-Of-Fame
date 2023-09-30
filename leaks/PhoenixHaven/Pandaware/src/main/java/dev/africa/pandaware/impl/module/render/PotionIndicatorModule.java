package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.MouseEvent;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.impl.ui.clickgui.ClickGUI;
import dev.africa.pandaware.utils.client.MouseUtils;
import dev.africa.pandaware.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.PotionEffect;

import java.awt.*;

@ModuleInfo(name = "Potion Indicator", category = Category.VISUAL)
public class PotionIndicatorModule extends Module {
    private final NumberSetting posX = new NumberSetting("Pos X",
            Toolkit.getDefaultToolkit().getScreenSize().width / 2, 0, 0, 1);
    private final NumberSetting posY = new NumberSetting("Pos Y",
            Toolkit.getDefaultToolkit().getScreenSize().height / 2, 0, 0, 1);
    private boolean dragging;
    private int draggingX, draggingY, width, height;
    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == RenderEvent.Type.RENDER_2D) {
            if (mc.currentScreen instanceof ClickGUI) return;
            this.width = 160;
            this.height = 58;

            if (this.dragging) {
                if (!(mc.currentScreen instanceof GuiChat)) {
                    this.dragging = false;
                } else {
                    this.posX.setValue(this.draggingX + event.getMousePosition().getX());
                    this.posY.setValue(this.draggingY + event.getMousePosition().getY());
                }
            }

            this.posX.setValue(Math.min(this.posX.getValue().doubleValue(), event.getResolution().getScaledWidth() - this.width - 1));
            this.posY.setValue(Math.min(this.posY.getValue().doubleValue(), event.getResolution().getScaledHeight() - this.height - 1));
            this.posX.setValue(Math.max(this.posX.getValue().doubleValue(), 1));
            this.posY.setValue(Math.max(this.posY.getValue().doubleValue(), 0.5));

            GlStateManager.pushAttribAndMatrix();

            RenderUtils.drawRoundedRect(this.posX.getValue().doubleValue(), this.posY.getValue().doubleValue(),
                    this.width, this.height, 10.5, UISettings.INTERNAL_COLOR);

            RenderUtils.drawRoundedRectOutline(this.posX.getValue().doubleValue(), this.posY.getValue().doubleValue(),
                    this.width, this.height, 10.5, UISettings.CURRENT_COLOR);

            Fonts.getInstance().getArialBdBig().drawCenteredStringWithShadow("Potions",
                    (float) (this.posX.getValue().doubleValue() + (this.width / 2f)),
                    (float) (this.posY.getValue().doubleValue() + 2), -1);

            int potions = 0;
            for (PotionEffect potion : mc.thePlayer.getActivePotionEffects()) {
                potions++;
                Fonts.getInstance().getArialBdNormal().drawCenteredStringWithShadow(potion.getEffectName() + " " +
                                potion.getAmplifier() + ": " + (potion.getDuration() / 20),
                        (float) (this.posX.getValue().doubleValue() + (this.width / 2f)),
                        (float) (this.posY.getValue().doubleValue() + 2 + (2 * potions)), -1);
            }
        }
    };
    @EventHandler
    EventCallback<MouseEvent> onMouse = event -> {
        if (event.getType() == MouseEvent.Type.CLICK && event.getMouseButton() == 0) {
            if (MouseUtils.isMouseInBounds(event.getMouseX(), event.getMouseY(),
                    this.posX.getValue().intValue(), this.posY.getValue().intValue(),
                    this.posX.getValue().intValue() + this.width,
                    this.posY.getValue().intValue() + this.height)) {
                this.dragging = true;
                this.draggingX = this.posX.getValue().intValue() - event.getMouseX();
                this.draggingY = this.posY.getValue().intValue() - event.getMouseY();
            }
        }

        if (event.getType() == MouseEvent.Type.RELEASED) {
            this.dragging = false;
        }
    };

    public PotionIndicatorModule() {
        this.registerSettings(this.posX, this.posY);
    }
}
