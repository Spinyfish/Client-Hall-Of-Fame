package dev.africa.pandaware.impl.ui.clickgui.setting.impl;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.clickgui.setting.api.Element;
import dev.africa.pandaware.utils.client.MouseUtils;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import dev.africa.pandaware.utils.render.RenderUtils;
import dev.africa.pandaware.utils.render.StencilUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class NumberElement extends Element<NumberSetting> {
    public NumberElement(Module module, ModuleMode<?> moduleMode, NumberSetting setting) {
        super(module, moduleMode, setting);
    }

    private boolean dragging;
    private double animation;

    @Override
    public void handleRender(Vec2i mousePosition, float pTicks) {
        Fonts.getInstance().getComfortaMedium().drawString(
                this.getSetting().getName(),
                this.getPosition().getX(),
                this.getPosition().getY(),
                -1
        );

        String text = String.valueOf(MathUtils.roundToDecimal(this.getSetting().getValue().doubleValue(), 2));
        Fonts.getInstance().getProductSansSmall().drawString(
                text,
                this.getPosition().getX() + this.getSize().getX() - 6 -
                        Fonts.getInstance().getProductSansSmall().getStringWidth(text),
                this.getPosition().getY() - 8,
                -1
        );

        if (this.dragging) {
            double difference = this.getSetting().getMax().doubleValue() - this.getSetting().getMin().doubleValue();

            double length = 125;
            double x = this.getPosition().getX() + this.getSize().getX() - (length + 8);

            double percentage = (mousePosition.getX() - x) / length;
            percentage = MathHelper.clamp_double(percentage, 0, 1);

            double value = this.getSetting().getMin().doubleValue() + percentage * difference;

            if (this.getSetting().getIncrement() != null) {
                value = MathUtils.roundToIncrement(value, this.getSetting().getIncrement().doubleValue());
            }

            this.getSetting().setValue(value);
        }

        this.drawSlider();
    }

    void drawSlider() {
        double value = this.getSetting().getValue().doubleValue();
        double maxValue = this.getSetting().getMax().doubleValue();
        double minValue = this.getSetting().getMin().doubleValue();

        double percentage = ((value - minValue) / (maxValue - minValue));
        percentage = MathHelper.clamp_double(percentage, 0, 1);

        double length = 125;
        GlStateManager.pushMatrix();
        GlStateManager.translate(
                this.getPosition().getX() + this.getSize().getX() - (length + 8),
                0, 0
        );

        StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_MASK);
        RenderUtils.drawRoundedRect(
                0, this.getPosition().getY() + 2.5, length, 4,
                2, new Color(0, 0, 0, 100)
        );
        StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_DRAW);
        RenderUtils.drawRect(
                0, this.getPosition().getY() + 2.5,
                (this.animation * length) - this.animation, this.getPosition().getY() + 8,
                Color.WHITE.getRGB()
        );
        StencilUtils.stencilStage(StencilUtils.StencilStage.DISABLE);

        RenderUtils.drawCircleArc(
                this.animation * (length - 7),
                this.getPosition().getY() + 2.5 - 1,
                6, 6, new Color(59, 59, 59, 255)
        );
        RenderUtils.drawCircleOutline(
                this.animation * (length - 7),
                this.getPosition().getY() + 2.5 - 1,
                6, 6, 2, Color.WHITE
        );

        this.animation += ((percentage - this.animation) / 3) * RenderUtils.fpsMultiplier();

        GlStateManager.popMatrix();
    }

    @Override
    public void handleClick(Vec2i mousePosition, int button) {
        if (button == 0 && this.getPosition() != null && this.getSize() != null) {
            double length = 125;
            double x = this.getPosition().getX() + this.getSize().getX() - (length + 8);

            if (MouseUtils.isMouseInBounds(mousePosition,
                    new Vec2i((int) x, this.getPosition().getY()), new Vec2i((int) length, 10))) {
                this.dragging = true;
            }
        }
    }

    @Override
    public void handleRelease(Vec2i mousePosition, int state) {
        if (state == 0) {
            this.dragging = false;
        }
    }

    @Override
    public void handleGuiClose() {
        this.dragging = false;
    }
}
