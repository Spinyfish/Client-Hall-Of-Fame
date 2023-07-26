package com.craftworks.pearclient.util.blur;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class BlurUtils {

	private static int fogColour = 0;
    private static boolean registered = false;

    private boolean shouldBlur = true;

    private static Shader blurShaderHorz = null;
    private static Framebuffer blurOutputHorz = null;
    private static Shader blurShaderVert = null;
    public static Framebuffer blurOutputVert = null;
    private static RounededShader roundedShader = new RounededShader();

    private static Matrix4f createProjectionMatrix(int width, int height) {
        Matrix4f projMatrix  = new Matrix4f();
        projMatrix.setIdentity();
        projMatrix.m00 = 2.0F / (float)width;
        projMatrix.m11 = 2.0F / (float)(-height);
        projMatrix.m22 = -0.0020001999F;
        projMatrix.m33 = 1.0F;
        projMatrix.m03 = -1.0F;
        projMatrix.m13 = 1.0F;
        projMatrix.m23 = -1.0001999F;
        return projMatrix;
    }

    private static double lastBgBlurFactor = -1;
    public static void blurBackground() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
       // if(!OpenGlHelper.isFramebufferEnabled()) return;

        int width = Minecraft.getMinecraft().displayWidth;
        int height = Minecraft.getMinecraft().displayHeight;

        if(blurOutputHorz == null) {
            blurOutputHorz = new Framebuffer(width, height, false);
            blurOutputHorz.setFramebufferFilter(GL11.GL_NEAREST);
        }
        if(blurOutputVert == null) {
            blurOutputVert = new Framebuffer(width, height, false);
            blurOutputVert.setFramebufferFilter(GL11.GL_NEAREST);
        }
        if(blurOutputHorz == null || blurOutputVert == null) {
            return;
        }
        if(blurOutputHorz.framebufferWidth != width || blurOutputHorz.framebufferHeight != height) {
            blurOutputHorz.createBindFramebuffer(width, height);
            blurShaderHorz.setProjectionMatrix(createProjectionMatrix(width, height));
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        }
        if(blurOutputVert.framebufferWidth != width || blurOutputVert.framebufferHeight != height) {
            blurOutputVert.createBindFramebuffer(width, height);
            blurShaderVert.setProjectionMatrix(createProjectionMatrix(width, height));
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        }

        if(blurShaderHorz == null) {
            try {
                blurShaderHorz = new Shader(Minecraft.getMinecraft().getResourceManager(), "blur",
                        blurOutputVert, blurOutputHorz);
                blurShaderHorz.getShaderManager().getShaderUniform("BlurDir").set(1, 0);
                blurShaderHorz.setProjectionMatrix(createProjectionMatrix(width, height));
            } catch(Exception e) { }
        }
        if(blurShaderVert == null) {
            try {
                blurShaderVert = new Shader(Minecraft.getMinecraft().getResourceManager(), "blur",
                        blurOutputHorz, blurOutputVert);
                blurShaderVert.getShaderManager().getShaderUniform("BlurDir").set(0, 1);
                blurShaderVert.setProjectionMatrix(createProjectionMatrix(width, height));
            } catch(Exception e) { }
        }
        if(blurShaderHorz != null && blurShaderVert != null) {
            if(blurShaderHorz.getShaderManager().getShaderUniform("Radius") == null) {
                return;
            }
            if(15 != lastBgBlurFactor) {
                blurShaderHorz.getShaderManager().getShaderUniform("Radius").set((float)6);
                blurShaderVert.getShaderManager().getShaderUniform("Radius").set((float)6);
                lastBgBlurFactor = 6;
            }
            GL11.glPushMatrix();
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, blurOutputVert.framebufferObject);
            GL30.glBlitFramebuffer(0, 0, width, height,
                    0, 0, width, height,
                    GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);

            blurShaderHorz.loadShader(0);
            blurShaderVert.loadShader(0);
            GlStateManager.enableDepth();
            GL11.glPopMatrix();

            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        }
    }

    public static void renderBlurredBackground(int screenWidth, int screenHeight, int x, int y, int blurWidth, int blurHeight, int radius) {
        if(!OpenGlHelper.isFramebufferEnabled()) return;

		blurBackground();
        float uMin = x/(float)screenWidth;
        float uMax = (x+blurWidth)/(float)screenWidth;
        float vMin = (screenHeight-y)/(float)screenHeight;
        float vMax = (screenHeight-y-blurHeight)/(float)screenHeight;

        GlStateManager.depthMask(false);
        Gui.drawRect(x, y, x+blurWidth, y+blurHeight, fogColour);
        blurOutputVert.bindFramebufferTexture();
        GlStateManager.color(1f, 1f, 1f, 1f);
        roundedShader.drawRoundedTexturedRect(x, y, blurWidth, blurHeight, uMin, uMax, vMin, vMax, radius);
        blurOutputVert.unbindFramebufferTexture();
        GlStateManager.depthMask(true);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax) {
        drawTexturedRect(x, y, width, height, uMin, uMax, vMin , vMax, GL11.GL_NEAREST);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawTexturedRectNoBlend(x, y, width, height, uMin, uMax, vMin, vMax, filter);

        GlStateManager.disableBlend();
    }

    public static void drawTexturedRectNoBlend(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableTexture2D();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer
                .pos(x, y+height, 0.0D)
                .tex(uMin, vMax).endVertex();
        worldrenderer
                .pos(x+width, y+height, 0.0D)
                .tex(uMax, vMax).endVertex();
        worldrenderer
                .pos(x+width, y, 0.0D)
                .tex(uMax, vMin).endVertex();
        worldrenderer
                .pos(x, y, 0.0D)
                .tex(uMin, vMin).endVertex();
        tessellator.draw();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    public static class RounededShader {

        private final ResourceLocation fragLocation;
        private final ResourceLocation vertexLocation;
        private Minecraft mc = Minecraft.getMinecraft();
        private int programID;

        public RounededShader(){
            this.fragLocation = new ResourceLocation("pearclient/shaders/rounded_texture_frag.fsh");
            this.vertexLocation = new ResourceLocation("pearclient/shaders/rounded_texture_vert.vsh");
            programID = glCreateProgram();
            int fragID, vertexID;
            try {
                fragID = createShader(mc.getResourceManager().getResource(fragLocation).getInputStream(), GL_FRAGMENT_SHADER);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                vertexID = createShader(Minecraft.getMinecraft().getResourceManager()      .getResource(vertexLocation).getInputStream(), GL_VERTEX_SHADER);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            glAttachShader(programID, fragID); //attach fragment shader to program
            glAttachShader(programID, vertexID); //attach vertex shader to program
            glLinkProgram(programID); //link program

            int status = glGetProgrami(programID, GL_LINK_STATUS);
            if (status == 0) {
                throw new IllegalStateException("Shader failed to link!");
            }
        }

        public void drawRoundedTexturedRect(int x, int y, int width, int height, float uMin, float uMax, float vMin, float vMax, float radius){
            GlStateManager.resetColor();
            GlStateManager.enableBlend();
            glUseProgram(programID);
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            setUniformFloat( "position", x * sr.getScaleFactor(),(Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
            setUniformFloat("size", width * sr.getScaleFactor(), height * sr.getScaleFactor());
            setUniformFloat( "radius", radius * sr.getScaleFactor());

            glBegin(GL_QUADS);
            glTexCoord2f(uMin, vMin);
            glVertex2f(x - 1, y - 1);
            glTexCoord2f(uMin, vMax);
            glVertex2f(x - 1, y + height + 1);
            glTexCoord2f(uMax, vMax);
            glVertex2f(x + width + 1, y + height + 1);
            glTexCoord2f(uMax, vMin);
            glVertex2f(x + width + 1, y - 1);
            glEnd();
            glUseProgram(0);
            GlStateManager.disableBlend();
            GlStateManager.resetColor();
        }

        private int createShader(InputStream inputStream, int shaderType) throws IOException {
            int shader = glCreateShader(shaderType);
            glShaderSource(shader, readStreamToString(inputStream));
            glCompileShader(shader);

            int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
            // If compilation failed
            if (compiled == 0) {
                System.err.println(glGetShaderInfoLog(shader, glGetShaderi(shader, GL_INFO_LOG_LENGTH)));
                throw new IllegalStateException("Failed to compile shader");
            }
            return shader;
        }

        private String readStreamToString(InputStream inputStream) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int read;
            while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, read);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }

        public void setUniformFloat(String name, float... arguments) {
            int loc = glGetUniformLocation(programID, name);
            switch (arguments.length) {
                case 1:
                    glUniform1f(loc, arguments[0]);
                    break;
                case 2:
                    glUniform2f(loc, arguments[0], arguments[1]);
                    break;
                case 3:
                    glUniform3f(loc, arguments[0], arguments[1], arguments[2]);
                    break;
                case 4:
                    glUniform4f(loc, arguments[0], arguments[1], arguments[2], arguments[3]);
                    break;
            }
        }

    }


}