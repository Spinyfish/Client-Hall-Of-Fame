package dev.thread.api.util.render;

import dev.thread.api.util.IWrapper;
import dev.thread.client.Thread;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@UtilityClass
public class RenderUtil implements IWrapper {
    public void drawRoundedRect(float x, float y, float width, float height, float radius, Color color) {
        enable(GL11.GL_BLEND);

        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawArc((x + width - radius), (y + height - radius), radius, 0, 90, 16, color);
        drawArc((x + radius), (y + height - radius), radius, 90, 180, 16, color);
        drawArc(x + radius, y + radius, radius, 180, 270, 16, color);
        drawArc((x + width - radius), (y + radius), radius, 270, 360, 16, color);


        prepare(GL11.GL_TRIANGLES); {
            addVertex(x + width - radius, y, color);
            addVertex(x + radius, y, color);

            addVertex(x + width - radius, y + radius, color);
            addVertex(x + width - radius, y + radius, color);

            addVertex(x + radius, y, color);
            addVertex(x + radius, y + radius, color);

            addVertex(x + width, y + radius, color);
            addVertex(x, y + radius, color);

            addVertex(x, y + height - radius, color);
            addVertex(x + width, y + radius, color);

            addVertex(x, y + height - radius, color);
            addVertex(x + width, y + height - radius, color);

            addVertex(x + width - radius, y + height - radius, color);
            addVertex(x + radius, y + height - radius, color);

            addVertex(x + width - radius, y + height, color);
            addVertex(x + width - radius, y + height, color);

            addVertex(x + radius, y + height - radius, color);
            addVertex(x + radius, y + height, color);
        }

        release();

        glColor4f(1, 1, 1, 1);
    }

    public void prepare(int mode) {
        GL11.glBegin(mode);
    }

    public void addVertex(float x, float y, Color color) {
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        GL11.glVertex2f(x, y);
    }

    public void drawArc(float cx, float cy, float r, float start_angle, float end_angle, float num_segments, Color color) {
        prepare(GL11.GL_TRIANGLES);

        for (int i = (int) (num_segments / (360 / start_angle)) + 1; i <= num_segments / (360 / end_angle); i++) {
            double previousAngle = 2 * Math.PI * (i - 1) / num_segments;
            double angle = 2 * Math.PI * i / num_segments;

            addVertex(cx, cy, color);
            addVertex((float) (cx + Math.cos(angle) * r), (float) (cy + Math.sin(angle) * r), color);
            addVertex((float) (cx + Math.cos(previousAngle) * r), (float) (cy + Math.sin(previousAngle) * r), color);
        }

        release();
    }

    public void release() {
        GL11.glEnd();
    }

    public void drawRoundedBlur(float x, float y, float width, float height, float roundRadius, float blurRadius) {
        StencilUtil.renderStencil(() -> RenderUtil.drawRoundedRect(x, y, width, height, roundRadius, Color.BLACK), () -> Thread.INSTANCE.getShaderManager().getBlurShader().render(blurRadius));
    }
    public void drawRoundedGradient(float x, float y, float width, float height, float radius, int tl, int bl, int tr, int br) {
        StencilUtil.renderStencil(() -> RenderUtil.drawRoundedRect(x, y, width, height, radius, Color.BLACK), () -> drawGradientRect(x, y, width, height, tl, bl, tr, br));
    }
    
    public void renderTexture(final ResourceLocation loc, final double x, final double y, final int width, final int height, final Color color) {
        glPushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        mc.getTextureManager().bindTexture(loc);

        // Minecraft#draw
        float f = 0.00390625f;
        GlStateManager.color(color.getRed() * f, color.getGreen() * f, color.getBlue() * f, color.getAlpha() * f);
        Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, 0, 0, width, height, width, height);

        GlStateManager.disableBlend();
        glPopMatrix();
    }

    public void drawGradientRect(double x, double y, double width, double height, int tl, int bl, int tr, int br) {
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.disableTexture2D();

        GlStateManager.shadeModel(GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer buffer = tessellator.getWorldRenderer();

        buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x + width, y, 0.0).color(tr).endVertex();
        buffer.pos(x, y, 0.0).color(tl).endVertex();
        buffer.pos(x, y + height, 0.0).color(bl).endVertex();
        buffer.pos(x + width, y + height, 0.0).color(br).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL_FLAT);

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public boolean isMouseHovered(final int mouseX, final int mouseY, final double x, final double y, final double width, final double height) {
        return mouseX >= x && x + width >= mouseX && mouseY >= y && y + height >= mouseY;
    }

    public void scissor(double x, double y, double width, double height) {
        final ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        glEnable(GL_SCISSOR_TEST);
        glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public void endScissor() {
        glDisable(GL_SCISSOR_TEST);
    }

    public void begin() {
        GlStateManager.pushMatrix();
        glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL_TEXTURE_2D);
    }

    public void end() {
        GL11.glDisable(GL11.GL_BLEND);
        glEnable(GL_TEXTURE_2D);

        GlStateManager.popMatrix();
        Gui.drawRect(0,0,0,0,0);
    }

    public void drawRect(float x, float y, float width, float height, Color color) {
        begin();

        color(color);
        GL11.glBegin(GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();

        end();
    }

    public void push() {
        glPushMatrix();
    }

    public void pop() {
        GL11.glPopMatrix();
    }

    public void enable(int glCap) {
        glEnable(glCap);
    }

    public void disable(int glCap) {
        GL11.glDisable(glCap);
    }

    public void use(int glCap, Runnable runnable) {
        GL11.glBegin(glCap);
        runnable.run();
        GL11.glEnd();
    }

    public void vertex(float x, float y) {
        GL11.glVertex2f(x, y);
    }

    public void vertex(double x, double y) {
        GL11.glVertex2d(x, y);
    }

    public void vertex(int x, int y) {
        GL11.glVertex2i(x, y);
    }

    public void blend(int one, int two) {
        GL11.glBlendFunc(one, two);
    }

    public void lineWidth(float lineWidth) {
        GL11.glLineWidth(lineWidth);
    }

    public void lineWidth(double lineWidth) {
        GL11.glLineWidth((float) lineWidth);
    }

    public void color(Color color) {
        GL11.glColor4f(
                color.getRed() / 255.0F,
                color.getGreen() / 255.0F,
                color.getBlue() / 255.0F,
                color.getAlpha() / 255.0F
        );
    }

    public void color(int color) {
        GL11.glColor4f(
            (color >> 16 & 255) / 255.0F,
            (color >> 8 & 255) / 255.0F,
            (color & 255) / 255.0F,
            (color >> 24 & 255) / 255.0F
        );
    }

    public void start() {
        push();

        enable(GL11.GL_BLEND);

        blend(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA
        );

        disable(GL_TEXTURE_2D);
        disable(GL11.GL_CULL_FACE);

        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public void stop() {
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();

        enable(GL11.GL_CULL_FACE);
        enable(GL_TEXTURE_2D);

        disable(GL11.GL_BLEND);

        color(Color.WHITE);

        pop();
    }

    public void rectangle(double x, double y, double width, double height, double line, boolean filled, Color color) {
        start();

        if (line > 0 && !filled)
            lineWidth(line);

        if (color != null)
            color(color);

        use(filled ? GL_QUADS : GL_LINE_LOOP, () -> {
            vertex(x, y);
            vertex(x + width, y);
            vertex(x + width, y + height);
            vertex(x, y + height);
        });

        stop();
    }

    public void rectangle(double x, double y, double width, double height, double line, Color color) {
        rectangle(x, y, width, height, line, false, color);
    }

    public void rectangle(double x, double y, double width, double height, Color color) {
        rectangle(x, y, width, height, 0, color);
    }

    public void rectangle(double x, double y, double width, double height) {
        rectangle(x, y, width, height, null);
    }

    public void polygon(double x, double y, double diameter, int sides, double line, boolean filled, Color color) {
        start();

        if (++line > 0 && !filled)
            lineWidth(line);

        if (color != null)
            color(color);

        use(filled ? GL_TRIANGLE_FAN : GL_LINE_LOOP, () -> {
            for (int i = 0; i < sides; ++i) {
                double angle = i * (Math.PI * 2) / 360;

                vertex(
                        x + (diameter * Math.cos(angle)) + diameter,
                        y + (diameter * Math.sin(angle)) + diameter
                );
            }
        });

        stop();
    }

    public void polygon(double x, double y, double diameter, int sides, double line, Color color) {
        polygon(x, y, diameter, sides, line, false, color);
    }

    public void polygon(double x, double y, double diameter, int sides, Color color) {
        polygon(x, y, diameter, sides, 0, color);
    }

    public void polygon(double x, double y, double diameter, int sides) {
        polygon(x, y, diameter, sides, null);
    }

    public void circle(double x, double y, double diameter, double line, boolean filled, Color color) {
        polygon(x, y, diameter, 180, line, filled, color);
    }

    public void circle(double x, double y, double diameter, double line, Color color) {
        circle(x, y, diameter, line, false, color);
    }

    public void circle(double x, double y, double diameter, Color color) {
        circle(x, y, diameter, 0, color);
    }

    public void circle(double x, double y, double diameter) {
        circle(x, y, diameter, null);
    }

}
