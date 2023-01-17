package com.alan.clients.ui.click.clover.button.api;

import com.alan.clients.util.dragging.Mouse;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import util.time.StopWatch;
import com.alan.clients.util.vector.Vector2d;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ButtonEffect {
    public Button button;
    public Color color;
    public Vector2d position = new Vector2d(Mouse.getMouse().x, Mouse.getMouse().y);

    public double alpha;
    public double radius;

    public ButtonEffect(Button button, Color color) {
        this.button = button;
        this.color = color;

        this.alpha = color.getAlpha();
        this.radius = 0.5f;
    }

    StopWatch stopWatch = new StopWatch();

    public void render() {
        if (!button.down) {
            alpha = Math.max(0, alpha - stopWatch.getElapsedTime() / 3f);
        }

        if (alpha == 0) return;

        radius += stopWatch.getElapsedTime() / 3f;
        stopWatch.reset();

        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(button.position.x, button.position.y, button.scale.x, button.scale.y);
        RenderUtil.roundedRectangle(position.x - radius / 2, position.y - radius / 2, radius, radius, radius / 2f, ColorUtil.withAlpha(color, (int) alpha));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }
}
