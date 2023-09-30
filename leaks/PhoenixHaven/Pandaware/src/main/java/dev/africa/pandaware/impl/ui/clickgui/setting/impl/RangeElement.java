package dev.africa.pandaware.impl.ui.clickgui.setting.impl;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.setting.NumberRangeSetting;
import dev.africa.pandaware.impl.ui.clickgui.setting.api.Element;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import dev.africa.pandaware.utils.render.RenderUtils;
import dev.africa.pandaware.utils.render.StencilUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class RangeElement extends Element<NumberRangeSetting> {
    public RangeElement(Module module, ModuleMode<?> moduleMode, NumberRangeSetting setting) {
        super(module, moduleMode, setting);
    }

    private boolean draggingLeft, draggingRight;
    private double animationLeft, animationRight;

    @Override
    public void handleRender(Vec2i mousePosition, float pTicks) {
        Fonts.getInstance().getComfortaMedium().drawString(
                this.getSetting().getName(),
                this.getPosition().getX(),
                this.getPosition().getY(),
                -1
        );

        if (this.draggingLeft || this.draggingRight) {
            double difference = this.getSetting().getMax().doubleValue() - this.getSetting().getMin().doubleValue();

            double length = 125;
            double x = this.getPosition().getX() + this.getSize().getX() - (length + 8);

            double percentage = (mousePosition.getX() - x) / length;
            percentage = MathHelper.clamp_double(percentage, 0, 1);

            double value = this.getSetting().getMin().doubleValue() + percentage * difference;

            if (this.getSetting().getIncrement() != null) {
                value = MathUtils.roundToIncrement(value, this.getSetting().getIncrement().doubleValue());
            }

            if (this.draggingLeft) {
                value = Math.min(value, this.getSetting().getSecondValue().doubleValue());

                this.getSetting().setValue(new Number[]{value, this.getSetting().getSecondValue()});
            } else {
                value = Math.max(value, this.getSetting().getFirstValue().doubleValue());

                this.getSetting().setValue(new Number[]{this.getSetting().getFirstValue(), value});
            }
        }

        String text = MathUtils.roundToDecimal(this.getSetting().getFirstValue().doubleValue(), 1)
                + " §7- §f" + MathUtils.roundToDecimal(this.getSetting().getSecondValue().doubleValue(), 1);
        Fonts.getInstance().getProductSansSmall().drawString(
                text,
                this.getPosition().getX() + this.getSize().getX() - 6 -
                        Fonts.getInstance().getProductSansSmall().getStringWidth(text),
                this.getPosition().getY() - 8,
                -1
        );

        drawSlider();
    }

    void drawSlider() {
        double maxValue = this.getSetting().getMax().doubleValue();
        double minValue = this.getSetting().getMin().doubleValue();

        double percentageLeft = (this.getSetting().getFirstValue().doubleValue() - minValue) / (maxValue - minValue);
        double percentageRight = (this.getSetting().getSecondValue().doubleValue() - minValue) / (maxValue - minValue);

        percentageLeft = MathHelper.clamp_double(percentageLeft, 0, 1);
        percentageRight = MathHelper.clamp_double(percentageRight, 0, 1);

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
                this.animationLeft * length, this.getPosition().getY() + 2.5,
                (this.animationRight * length), this.getPosition().getY() + 8,
                Color.WHITE.getRGB()
        );
        StencilUtils.stencilStage(StencilUtils.StencilStage.DISABLE);

        RenderUtils.drawCircleArc(
                this.animationRight * (length - 7),
                this.getPosition().getY() + 2.5 - 1,
                6, 6, new Color(59, 59, 59, 255)
        );
        RenderUtils.drawCircleOutline(
                this.animationRight * (length - 7),
                this.getPosition().getY() + 2.5 - 1,
                6, 6, 2, Color.WHITE
        );

        GlStateManager.disableBlend();
        RenderUtils.drawCircleArc(
                this.animationLeft * (length - 7),
                this.getPosition().getY() + 2.5 - 1,
                6, 6, new Color(59, 59, 59, 255)
        );
        RenderUtils.drawCircleOutline(
                this.animationLeft * (length - 7),
                this.getPosition().getY() + 2.5 - 1,
                6, 6, 2, Color.WHITE
        );


        this.animationRight += ((percentageRight - this.animationRight) / 3) * RenderUtils.fpsMultiplier();
        this.animationLeft += ((percentageLeft - this.animationLeft) / 3) * RenderUtils.fpsMultiplier();

        GlStateManager.popMatrix();
    }

    @Override
    public void handleClick(Vec2i mousePosition, int button) {
        if (this.getPosition() != null && this.getSize() != null) {
            if (button == 0 && this.isLeftHovered(mousePosition.getX(), mousePosition.getY())) {
                this.draggingLeft = true;
            }

            if (button == 0 && this.isRightHovered(mousePosition.getX(), mousePosition.getY())) {
                this.draggingRight = true;
            }
        }
    }

    @Override
    public void handleRelease(Vec2i mousePosition, int state) {
        if (state == 0) {
            this.draggingRight = false;
            this.draggingLeft = false;
        }
    }

    @Override
    public void handleGuiClose() {
        this.draggingRight = false;
        this.draggingLeft = false;
    }

    boolean isRightHovered(int mouseX, int mouseY) {
        double percentBar1 = (this.getSetting().getFirstValue().doubleValue() - this.getSetting().getMin().doubleValue())
                / (this.getSetting().getMax().doubleValue() - this.getSetting().getMin().doubleValue());
        double percentBar2 = (this.getSetting().getSecondValue().doubleValue() - this.getSetting().getMin().doubleValue())
                / (this.getSetting().getMax().doubleValue() - this.getSetting().getMin().doubleValue());
        percentBar1 = MathHelper.clamp_double(percentBar1, 0, 1);
        percentBar2 = MathHelper.clamp_double(percentBar2, 0, 1);

        double length = 125;
        double x = this.getPosition().getX() + this.getSize().getX() - (length + 8);

        double left = (x + (percentBar1 * length));
        double right = (x + (percentBar2 * length));

        return mouseX >= (right + (((left - x) - (right - x)) / 2)) &&
                mouseX <= x + length &&
                mouseY >= this.getPosition().getY() &&
                mouseY <= this.getPosition().getY() + 10;
    }

    boolean isLeftHovered(int mouseX, int mouseY) {
        double percentBar1 = (this.getSetting().getFirstValue().doubleValue() - this.getSetting().getMin().doubleValue())
                / (this.getSetting().getMax().doubleValue() - this.getSetting().getMin().doubleValue());
        double percentBar2 = (this.getSetting().getSecondValue().doubleValue() - this.getSetting().getMin().doubleValue())
                / (this.getSetting().getMax().doubleValue() - this.getSetting().getMin().doubleValue());
        percentBar1 = MathHelper.clamp_double(percentBar1, 0, 1);
        percentBar2 = MathHelper.clamp_double(percentBar2, 0, 1);

        double length = 125;
        double x = this.getPosition().getX() + this.getSize().getX() - (length + 8);

        double left = (x + (percentBar1 * length));
        double right = (x + (percentBar2 * length));

        return mouseX >= x &&
                mouseX <= (left + (((right - x) - (left - x)) / 2)) &&
                mouseY >= this.getPosition().getY() &&
                mouseY <= this.getPosition().getY() + 10;
    }
}
