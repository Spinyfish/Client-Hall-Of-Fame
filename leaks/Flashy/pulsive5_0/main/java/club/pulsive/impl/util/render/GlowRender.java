package club.pulsive.impl.util.render;


import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.render.Render2DEvent;
import club.pulsive.impl.event.render.Render3DEvent;
import club.pulsive.impl.module.impl.visual.GlowESP;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;


public class GlowRender {

    private Minecraft mc = Minecraft.getMinecraft();

    private int programId = ARBShaderObjects.glCreateProgramObjectARB(), vsh, fsh;
    private HashMap<String, Integer> uniformsMap = new HashMap();
    Framebuffer fbVertical, fbHorizontal;

    private int createShader(String glsl, int type) {
        int shader = ARBShaderObjects.glCreateShaderObjectARB(type);
        ARBShaderObjects.glShaderSourceARB(shader, glsl);
        ARBShaderObjects.glCompileShaderARB(shader);
        return shader;
    }

    public GlowRender() {
        vsh = createShader("void main() {\n" +
                "    gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
                "    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
                "}", ARBVertexShader.GL_VERTEX_SHADER_ARB);

        fsh = createShader("#version 120\n" +
                "uniform sampler2D texture;\n" +
                "uniform sampler2D texture2;\n" +
                "uniform vec2 texelSize;\n" +
                "uniform vec2 direction;\n" +
                "uniform float alpha;\n" +
                "uniform vec3 color;\n" +
                "uniform int radius;\n" +
                "\n" +
                "float gaussian(float x, float sigma) {\n" +
                "    float power_2 = x / sigma;\n" +
                "    return (1.0 / (sigma * 2.50662827463)) * exp(-0.5 * (power_2 * power_2));\n" +
                "}\n" +
                "\n" +
                "void main() {\n" +
                "    vec2 texCoord = gl_TexCoord[0].st;\n" +
                "\n" +
                "    if (direction.y == 1)\n" +
                "        if (texture2D(texture2, texCoord).a != 0.0) return;\n" +
                "\n" +
                "    vec4 blurred_color = vec4(0.0);\n" +
                "\n" +
                "    for (float r = -radius; r <= radius; r++) {\n" +
                "        blurred_color += texture2D(texture, texCoord + r * texelSize * direction) * gaussian(r, radius / 2);\n" +
                "    }\n" +
                "\n" +
                "    if (blurred_color.a > 0) {\n" +
                "        if (direction.x == 0) {\n" +
                "            gl_FragColor = vec4(color.rgb / 255.0, blurred_color.a * alpha);\n" +
                "        } else {\n" +
                "            gl_FragColor = vec4(color.rgb / 255.0, blurred_color.a);\n" +
                "        }\n" +
                "    }\n" +
                "}", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

        OpenGlHelper.glAttachShader(programId, vsh);
        OpenGlHelper.glAttachShader(programId, fsh);
        OpenGlHelper.glLinkProgram(programId);

        uniformsMap.put("texture", GL20.glGetUniformLocation(programId, "texture"));
        uniformsMap.put("texture2", GL20.glGetUniformLocation(programId, "texture2"));
        uniformsMap.put("texelSize", GL20.glGetUniformLocation(programId, "texelSize"));
        uniformsMap.put("direction", GL20.glGetUniformLocation(programId, "direction"));
        uniformsMap.put("alpha", GL20.glGetUniformLocation(programId, "alpha"));
        uniformsMap.put("radius", GL20.glGetUniformLocation(programId, "radius"));
        uniformsMap.put("color", GL20.glGetUniformLocation(programId, "color"));
    }

    private Framebuffer setupFramebuffer(Framebuffer framebuffer) {
        if (framebuffer != null) {
            framebuffer.deleteFramebuffer();
        }

        framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        return framebuffer;
    }

    private void drawFramebuffer(Framebuffer framebuffer, ScaledResolution scaledResolution) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2d(0, 0);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2d(0, scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2d(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(scaledResolution.getScaledWidth(), 0);
        GL11.glEnd();
    }

    private void updateUniforms() {
        GL20.glUniform1i(uniformsMap.get("texture"), 0);
        GL20.glUniform1i(uniformsMap.get("texture2"), 8);
        GL20.glUniform2f(uniformsMap.get("texelSize"), 1.0F / mc.displayWidth, 1.0F / mc.displayHeight);
        GL20.glUniform1f(uniformsMap.get("alpha"), 3f / 255 * GlowESP.alpha.getValue().intValue());
        GL20.glUniform1i(uniformsMap.get("radius"), GlowESP.radius.getValue().intValue());

        GL20.glUniform3f(uniformsMap.get("color"), GlowESP.color.getValue().getRed(), GlowESP.color.getValue().getGreen(), GlowESP.color.getValue().getBlue());
    }

    private void updateUniforms(int red, int green, int blue) {
        GL20.glUniform1i(uniformsMap.get("texture"), 0);
        GL20.glUniform1i(uniformsMap.get("texture2"), 8);
        GL20.glUniform2f(uniformsMap.get("texelSize"), 1.0F / mc.displayWidth, 1.0F / mc.displayHeight);
        GL20.glUniform1f(uniformsMap.get("alpha"), 3f);
        GL20.glUniform1i(uniformsMap.get("radius"), 15);
        GL20.glUniform3f(uniformsMap.get("color"), red, green, blue);
    }

    public void onRender(Render3DEvent event) {
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        fbVertical = setupFramebuffer(fbVertical);
        fbHorizontal = setupFramebuffer(fbHorizontal);

        fbVertical.bindFramebuffer(true);
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
        for (Entity entity : getLivingPlayers()) {
            if(entity != mc.thePlayer && isValid(entity)) {
                if(GuiUtil.isInView(entity)) {
                    mc.getRenderManager().renderEntitySimple(entity, event.getPartialTicks());
                }
            }


        }


        mc.entityRenderer.setupOverlayRendering();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        fbHorizontal.bindFramebuffer(true);
        OpenGlHelper.glUseProgram(programId);
        updateUniforms();
        GL20.glUniform2f(uniformsMap.get("direction"), 1, 0);
        drawFramebuffer(fbVertical, new ScaledResolution(mc));

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit + 7);
        drawFramebuffer(fbVertical, new ScaledResolution(mc));
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        mc.getFramebuffer().bindFramebuffer(true);
        GL20.glUniform2f(uniformsMap.get("direction"), 0, 1);
        drawFramebuffer(fbHorizontal, new ScaledResolution(mc));
        OpenGlHelper.glUseProgram(0);
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
    public boolean isValid(Entity entity) {
        return isValidType(entity) && entity.isEntityAlive() && !entity.isInvisible() && entity != this.mc.thePlayer;
    }
    public boolean isValidType(Entity entity) {
        return entity instanceof EntityPlayer; // @on
    }
    public List<EntityPlayer> getLivingPlayers() {
        return Arrays.asList(
                ((List<Entity>) mc.theWorld.loadedEntityList).stream()
                        .filter(entity -> entity instanceof EntityPlayer)
                        .filter(entity -> entity != mc.thePlayer)
                        .filter(entity -> isValid(entity))
                        .filter(entity -> GuiUtil.isInView(entity))
                        .map(entity -> (EntityPlayer) entity)
                        .toArray(EntityPlayer[]::new)
        );
    }
    public void renderSpecificPlayers(Render2DEvent event, List<EntityPlayer> playersList, Color color) {
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        fbVertical = setupFramebuffer(fbVertical);
        fbHorizontal = setupFramebuffer(fbHorizontal);

        fbVertical.bindFramebuffer(true);
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);

        for (EntityPlayer player : playersList) {

        }

        mc.entityRenderer.setupOverlayRendering();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        fbHorizontal.bindFramebuffer(true);
        OpenGlHelper.glUseProgram(programId);
        updateUniforms(color.getRed(), color.getGreen(), color.getBlue());
        GL20.glUniform2f(uniformsMap.get("direction"), 1, 0);
        drawFramebuffer(fbVertical, event.getScaledResolution());

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit + 7);
        drawFramebuffer(fbVertical, event.getScaledResolution());
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        mc.getFramebuffer().bindFramebuffer(true);
        GL20.glUniform2f(uniformsMap.get("direction"), 0, 1);
        drawFramebuffer(fbHorizontal, event.getScaledResolution());
        OpenGlHelper.glUseProgram(0);
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }


    public static boolean glowEnabled() {
        return Pulsive.INSTANCE.getModuleManager().getModule(GlowESP.class).isToggled();
    }
} 
