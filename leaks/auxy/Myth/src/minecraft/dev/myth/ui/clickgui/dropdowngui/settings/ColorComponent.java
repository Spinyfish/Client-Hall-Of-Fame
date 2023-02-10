package dev.myth.ui.clickgui.dropdowngui.settings;

import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.settings.ColorSetting;
import dev.myth.ui.clickgui.Component;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorComponent extends Component {

    private final ColorSetting colorProperty;

    public static Tessellator tessellator;
    public static WorldRenderer worldrenderer;
    private float hue;
    private float saturation;
    private float brightness;
    private float alpha;
    private boolean colorSelectorDragging, hueSelectorDragging, alphaSelectorDragging;

    public ColorComponent(ColorSetting setting) {
        this.colorProperty = setting;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        final double x = this.getX();
        final double y = this.getY();
        final double width = this.getWidth();
        final double height = this.getHeight();
        final int textColor = -1;
        RenderUtil.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorUtil.GRAY_17);
        FontLoaders.BOLD.drawString(colorProperty.getName(), (float) (x + 6.5), (float) (y + height / 2.0f - 3.0f - 1), textColor);
        final float left = (float) (x + width - 13);
        final double top = y + height / 2.0f - 2.0f;
        final float right = (float) (x + width - 2);
        final double bottom = y + height / 2.0f + 2.0f;
        Gui.drawRect(left - 9, top - 1, right - 2, bottom + 3, this.colorProperty.getValue().getRGB());
        if (isExtended()) {
            RenderUtil.drawRect(this.getX(), this.getY() + this.getHeight(), this.getWidth(), 100 - getHeight(), ColorUtil.GRAY_17);
            final float cpLeft = (float) (x + 2);
            final float cpTop = (float) (y + height + 2);
            final float cpRight = (float) (x + 80 - 2);
            final float cpBottom = (float) (y + height + 80 - 2);

            float colorSelectorX = this.saturation * (cpRight - cpLeft);
            float colorSelectorY = (1.0f - this.brightness) * (cpBottom - cpTop);
            if (this.colorSelectorDragging) {
                final float wWidth = cpRight - cpLeft;
                final float xDif = MathHelper.clamp_float(mouseX, cpLeft, cpRight) - cpLeft;
                this.saturation = xDif / wWidth;
                colorSelectorX = xDif;
                final float hHeight = cpBottom - cpTop;
                final float yDif = MathHelper.clamp_float(mouseY, cpTop, cpBottom) - cpTop;
                this.brightness = 1.0f - yDif / hHeight;
                colorSelectorY = yDif;
                this.updateColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness), false);
            }

            this.drawColorPickerRect(cpLeft + 0.5f, cpTop + 0.5f, cpRight - 0.5f, cpBottom - 0.5f);
            final float selectorWidth = 2.0f;
            final float outlineWidth = 0.5f;
            final float half = selectorWidth / 2.0f;
            final float csLeft = cpLeft + colorSelectorX - half;
            final float csTop = cpTop + colorSelectorY - half;
            final float csRight = cpLeft + colorSelectorX + half;
            final float csBottom = cpTop + colorSelectorY + half;
            Gui.drawRect(csLeft - outlineWidth, csTop - outlineWidth, csRight + outlineWidth, csBottom + outlineWidth, -16777216);
            Gui.drawRect(csLeft, csTop, csRight, csBottom, Color.HSBtoRGB(this.hue, this.saturation, this.brightness));
            float sLeft = (float) (x + 80 - 1);
            float sTop = (float) (y + height + 2);
            float sRight = sLeft + 5.0f;
            float sBottom = (float) (y + height + 80 - 2);

            float hueSelectorY = this.hue * (sBottom - sTop);
            if (this.hueSelectorDragging) {
                final float hsHeight = sBottom - sTop;
                final float yDif2 = MathHelper.clamp_float(mouseY, sTop, sBottom) - sTop;
                this.hue = yDif2 / hsHeight;
                hueSelectorY = yDif2;
                this.updateColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness), false);
            }

            final float inc = 0.2f;
            final float times = 1.0f / inc;
            final float sHeight = sBottom - sTop;
            float sY = sTop + 0.5f;
            float size = sHeight / times;
            for (int i = 0; i < times; ++i) {
                final boolean last = i == times - 1.0f;
                if (last) {
                    --size;
                }
                Gui.drawGradientRect((int) (sLeft + 0.5f), (int) sY, (int) (sRight - 0.5f), (int) (sY + size), Color.HSBtoRGB(inc * i, 1.0f, 1.0f), Color.HSBtoRGB(inc * (i + 1), 1.0f, 1.0f));
                if (!last) {
                    sY += size;
                }
            }
            final float selectorHeight = 2.0f;
            final float outlineWidth2 = 0.5f;
            final float half2 = selectorHeight / 2.0f;
            final float csTop2 = sTop + hueSelectorY - half2;
            final float csBottom2 = sTop + hueSelectorY + half2;
            Gui.drawRect(sLeft - outlineWidth2, csTop2 - outlineWidth2, sRight + outlineWidth2, csBottom2 + outlineWidth2, -16777216);
            Gui.drawRect(sLeft, csTop2, sRight, csBottom2, Color.HSBtoRGB(this.hue, 1.0f, 1.0f));
            sLeft = (float) (x + 80 + 6);
            sTop = (float) (y + height + 2);
            sRight = sLeft + 5.0f;
            sBottom = (float) (y + height + 80 - 2);

            final int color = Color.HSBtoRGB(this.hue, this.saturation, this.brightness);
            final int r = color >> 16 & 0xFF;
            final int g = color >> 8 & 0xFF;
            final int b = color & 0xFF;
            if (this.alphaSelectorDragging) {
                final float hsHeight2 = sBottom - sTop;
                final float yDif3 = MathHelper.clamp_float(mouseY, sTop, sBottom) - sTop;
                this.alpha = yDif3 / hsHeight2;
                this.updateColor(new Color(r, g, b, (int) this.alpha * 255).getRGB(), true);
            }

        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (MouseUtil.isHovered(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            if (mouseButton == 1) {
                this.setExtended(!this.isExtended());
            }
        }

        if (isExtended() && mouseButton == 0) {
            final double x = this.getX();
            final double y = this.getY();
            final float cpLeft = (float) (x + 2);
            final float cpTop = (float) (y + this.getHeight() + 2);
            final float cpRight = (float) (x + 80 - 2);
            final float cpBottom = (float) (y + this.getHeight() + 80 - 2);
            final float sLeft = (float) (x + 80 - 1);
            final float sTop = (float) (y + this.getHeight() + 2);
            final float sRight = sLeft + 5.0f;
            final float sBottom = (float) (y + this.getHeight() + 80 - 2);
            final float asLeft = (float) (x + 80 + 6);
            final float asTop = (float) (y + this.getHeight() + 2);
            final float asRight = asLeft + 5.0f;
            final float asBottom = (float) (y + this.getHeight() + 80 - 2);
            this.colorSelectorDragging = (!this.colorSelectorDragging && mouseX > cpLeft && mouseY > cpTop && mouseX < cpRight && mouseY < cpBottom);
            this.hueSelectorDragging = (!this.hueSelectorDragging && mouseX > sLeft && mouseY > sTop && mouseX < sRight && mouseY < sBottom);
            this.alphaSelectorDragging = (!this.alphaSelectorDragging && mouseX > asLeft && mouseY > asTop && mouseX < asRight && mouseY < asBottom);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY) {
        super.mouseReleased(mouseX, mouseY);

        this.colorSelectorDragging = false;
        this.hueSelectorDragging = false;
        this.alphaSelectorDragging = false;
        return false;
    }

    @Override
    public boolean isExtendable() {
        return true;
    }

    @Override
    public double getFullHeight() {
        return (isExtended() ? 80 : 0) + getHeight();
    }

    @Override
    public boolean isVisible() {
        return colorProperty.isVisible();
    }

    private void updateColor(final int hex, final boolean hasAlpha) {
        if (hasAlpha) {
            this.colorProperty.setValue(hex);
        } else {
            this.colorProperty.setValue(new Color(hex >> 16 & 0xFF, hex >> 8 & 0xFF, hex & 0xFF, (int) (this.alpha * 255.0f)).getRGB());
        }
    }

    public  void drawColorPickerRect(final float left, final float top, final float right, final float bottom) {
        final int hueBasedColor = Color.HSBtoRGB(this.hue, 1.0f, 1.0f);
        GL11.glDisable(3553);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(7425);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double) right, top, 0.0).color(hueBasedColor).endVertex();
        worldrenderer.pos((double) left, (double) top, 0.0).color(-1).endVertex();
        worldrenderer.pos((double) left, (double) bottom, 0.0).color(-1).endVertex();
        worldrenderer.pos((double) right, (double) bottom, 0.0).color(hueBasedColor).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double) right, (double) top, 0.0).color(402653184).endVertex();
        worldrenderer.pos((double) left, (double) top, 0.0).color(402653184).endVertex();
        worldrenderer.pos((double) left, (double) bottom, 0.0).color(-16777216).endVertex();
        worldrenderer.pos((double) right, (double) bottom, 0.0).color(-16777216).endVertex();
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glShadeModel(7424);
        GL11.glEnable(3553);
    }


    static {
        ColorComponent.tessellator = Tessellator.getInstance();
        ColorComponent.worldrenderer = ColorComponent.tessellator.getWorldRenderer();
    }

}
