package dev.africa.pandaware.impl.ui.clickgui.setting.impl;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.setting.ColorSetting;
import dev.africa.pandaware.impl.ui.clickgui.setting.api.Element;
import dev.africa.pandaware.utils.client.MouseUtils;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import dev.africa.pandaware.utils.render.RenderUtils;
import dev.africa.pandaware.utils.render.StencilUtils;
import dev.africa.pandaware.utils.render.VertexUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class ColorElement extends Element<ColorSetting> {
    public ColorElement(Module module, ModuleMode<?> moduleMode, ColorSetting setting) {
        super(module, moduleMode, setting);

        float[] colorList = Color.RGBtoHSB(setting.getValue().getRed(),
                setting.getValue().getGreen(), setting.getValue().getBlue(), null);
        this.hue = colorList[0];
        this.saturation = colorList[1];
        this.brightness = colorList[2];
        this.quadColor = Color.getHSBColor(this.hue, 1f, 1f);

//        double reversedX = (this.getPosition().getX() + this.getSize().getX()) * this.saturation;
//        double reversedY = (((this.getSize().getY() + 70) - 30) * (1f - this.brightness)) + 16;
//
//        this.circlePositionX = reversedX;
//        this.circlePositionY = reversedY;
    }

    private boolean pressedHue;
    private boolean pressedQuad;
    private Color quadColor;

    private float hue;

    private float saturation;
    private float brightness;

    @Override
    public void handleRender(Vec2i mousePosition, float pTicks) {
        Fonts.getInstance().getComfortaMedium().drawString(
                this.getSetting().getName(),
                this.getPosition().getX(),
                this.getPosition().getY(),
                -1
        );

        this.getSize().setY(this.getSize().getY() + 30);

        StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_MASK);
        RenderUtils.drawRoundedRect(this.getPosition().getX() + this.getSize().getX() - 11,
                this.getPosition().getY() + 1, 4, this.getSize().getY(), 2, Color.WHITE);
        StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_DRAW);
        for (double i = 0; i < this.getSize().getY(); i++) {
            double y = this.getPosition().getY() + i + 1;
            Color hueColor = Color.getHSBColor((float) (i / this.getSize().getY()), 1, 1);

            RenderUtils.drawRect(
                    this.getPosition().getX() + this.getSize().getX() - 11,
                    y,
                    this.getPosition().getX() + this.getSize().getX() - 7,
                    y + 1,
                    hueColor.getRGB()
            );

            if (this.pressedHue && mousePosition.getY() == Math.round(y)) {
                this.hue = (float) (i / this.getSize().getY());
                this.quadColor = hueColor;

                this.getSetting().setValue(Color.getHSBColor(this.hue, this.saturation, this.brightness));
            }
        }
        StencilUtils.stencilStage(StencilUtils.StencilStage.DISABLE);

        StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_MASK);
        RenderUtils.drawRoundedRect(
                this.getPosition().getX() + this.getSize().getX() - 109,
                this.getPosition().getY() + 1,
                95, this.getSize().getY(), 3, Color.WHITE
        );
        StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_DRAW);
        this.drawColoredQuad(
                this.getPosition().getX() + this.getSize().getX() - 109,
                this.getPosition().getY() + 1,
                95, this.getSize().getY(), this.quadColor
        );
        StencilUtils.stencilStage(StencilUtils.StencilStage.DISABLE);

        StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_MASK);
        RenderUtils.drawRoundedRect(
                this.getPosition().getX() + this.getSize().getX() - 116,
                this.getPosition().getY() + 1,
                4, this.getSize().getY(), 2,
                Color.WHITE
        );
        StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_DRAW);
        RenderUtils.drawRect(
                this.getPosition().getX() + this.getSize().getX() - 116,
                this.getPosition().getY() + 1,
                (this.getPosition().getX() + this.getSize().getX() - 116) + 4,
                (this.getPosition().getY() + 1) + this.getSize().getY(),
                this.getSetting().getValue().getRGB()
        );
        StencilUtils.stencilStage(StencilUtils.StencilStage.DISABLE);

        if (this.pressedQuad) {
            int x = this.getPosition().getX() + this.getSize().getX() - 109;
            int y = this.getPosition().getY() + 1;

            float xPercentage = (mousePosition.getX() - x) / 95f;
            float yPercentage = (mousePosition.getY() - y) / (float) this.getSize().getY();
            this.saturation = MathHelper.clamp_float(xPercentage, 0, 1);
            this.brightness = MathHelper.clamp_float(1f - yPercentage, 0, 1);

            this.getSetting().setValue(Color.getHSBColor(this.hue, this.saturation, this.brightness));
        }
    }

    @Override
    public void handleClick(Vec2i mousePosition, int button) {
        if (button == 0 && this.getPosition() != null && this.getSize() != null) {
            if (MouseUtils.isMouseInBounds(mousePosition,
                    new Vec2i(this.getPosition().getX() + this.getSize().getX() - 11, this.getPosition().getY()),
                    new Vec2i(4, this.getSize().getY()))) {
                this.pressedHue = true;
            }

            if (MouseUtils.isMouseInBounds(mousePosition,
                    new Vec2i(this.getPosition().getX() + this.getSize().getX() - 109, this.getPosition().getY() + 1),
                    new Vec2i(95, this.getSize().getY()))) {
                this.pressedQuad = true;
            }
        }
    }

    @Override
    public void handleRelease(Vec2i mousePosition, int state) {
        if (state == 0) {
            this.pressedHue = false;
            this.pressedQuad = false;
        }
    }

    @Override
    public void handleGuiClose() {
        this.pressedHue = false;
        this.pressedQuad = false;
    }

    void drawColoredQuad(double x, double y, double width, double height, Color color) {
        GlStateManager.pushAttribAndMatrix();
        RenderUtils.preRenderShade();

        height = Math.max(0, height);
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE);

        float x1 = (float) x;
        float y1 = (float) y;
        float width1 = (float) width;
        float height1 = (float) height;

        VertexUtils.start(GL_TRIANGLE_STRIP);
        VertexUtils.add(x1, y1, Color.WHITE); // Top left
        VertexUtils.add(x1, y1 + height1, Color.WHITE); // Bottom left
        VertexUtils.add(x1 + width1, y1, color);// Top right
        VertexUtils.add(x1 + width1, y1 + height1, color);// Bottom right
        VertexUtils.end();

        VertexUtils.start(GL_TRIANGLE_STRIP);
        VertexUtils.add(x1, y1, new Color(0, 0, 0, 0)); // Top left
        VertexUtils.add(x1, y1 + height1, Color.BLACK); // Bottom left
        VertexUtils.add(x1 + width1, y1, new Color(0, 0, 0, 0));// Top right
        VertexUtils.add(x1 + width1, y1 + height1, Color.BLACK);// Bottom right
        VertexUtils.end();

        RenderUtils.postRenderShade();
        GlStateManager.popAttribAndMatrix();
    }
}
