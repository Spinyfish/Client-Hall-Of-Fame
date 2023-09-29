package com.alan.clients.util.shader.base;

import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.shader.ShaderUtil;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class RiseShaderProgram implements InstanceAccess {

    private final int programId;

    public RiseShaderProgram(final String fragmentPath, final String vertexPath) {
        this.programId = ShaderUtil.createShader(fragmentPath, vertexPath);
    }

    public static void drawQuad(final double x, final double y, final double width, final double height) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2d(x + width, y);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
    }

    public static void drawQuad() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        drawQuad(0.0, 0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double());
    }

    public void start() {
        GL20.glUseProgram(programId);
    }

    public static void stop() {
        GL20.glUseProgram(0);
    }

    public int getProgramId() {
        return programId;
    }
}
