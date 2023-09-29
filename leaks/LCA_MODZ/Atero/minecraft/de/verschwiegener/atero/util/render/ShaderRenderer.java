package de.verschwiegener.atero.util.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import de.verschwiegener.atero.Management;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

public class ShaderRenderer {
    private int glProgram = ARBShaderObjects.glCreateProgramObjectARB();
    Minecraft mc = Minecraft.getMinecraft();
    public ShaderRenderer(String fragmentShader) {
	initShader(fragmentShader);
    }

    private void initShader(String fragmentShader) {
	try {
	    int vertex = ARBShaderObjects.glCreateShaderObjectARB(35633);
		int fragment = ARBShaderObjects.glCreateShaderObjectARB(35632);
		ARBShaderObjects.glShaderSourceARB(vertex, readFile("shader/vertex.vert"));
		ARBShaderObjects.glShaderSourceARB(fragment, readFile("shader/" + fragmentShader));
		ARBShaderObjects.glValidateProgramARB(this.glProgram);
		ARBShaderObjects.glCompileShaderARB(vertex);
		ARBShaderObjects.glCompileShaderARB(fragment);
		ARBShaderObjects.glAttachObjectARB(this.glProgram, vertex);
		ARBShaderObjects.glAttachObjectARB(this.glProgram, fragment);
		ARBShaderObjects.glLinkProgramARB(this.glProgram);
	}catch(Exception e) {
	    e.printStackTrace();
	}
    }

    public final void prepareRender() {
	GL11.glClear(16640);
	
	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableTexture2D();
        GlStateManager.bindTexture(0);
	
	ARBShaderObjects.glUseProgramObjectARB(this.glProgram);
	ARBShaderObjects.glUniform2fARB(getUniformLocation("resolution"), this.mc.displayWidth,this.mc.displayHeight);
	EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
	GL11.glBindTexture(GL11.GL_TEXTURE_2D, Minecraft.getMinecraft().getFramebuffer().framebufferTexture);  
	ARBShaderObjects.glUniform1iARB(getUniformLocation("u_texture"), 0);
	
    }
    public int getUniformLocation(String name) {
	return ARBShaderObjects.glGetUniformLocationARB(this.glProgram, name);
    }

    public final void renderShader(ScaledResolution scaledResolution) {
	GL11.glBlendFunc(770, 771);
	GL11.glBegin(7);
	GL11.glTexCoord2d(0.0D, 1.0D);
	GL11.glVertex2d(0.0D, 0.0D);
	GL11.glTexCoord2d(0.0D, 0.0D);
	GL11.glVertex2d(0.0D, mc.displayHeight);
	GL11.glTexCoord2d(1.0D, 0.0D);
	GL11.glVertex2d(mc.displayWidth, mc.displayHeight);
	GL11.glTexCoord2d(1.0D, 1.0D);
	GL11.glVertex2d(mc.displayWidth, 0.0D);
	GL11.glEnd();
	ARBShaderObjects.glUseProgramObjectARB(0);
    }

    private String readFile(String paramString) {
	StringBuilder stringBuilder = new StringBuilder();
	try {
	    System.out.println("String: " + paramString);
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(paramString)));
	    String str;
	    while ((str = bufferedReader.readLine()) != null)
		stringBuilder.append(str).append(System.lineSeparator());
	    bufferedReader.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	System.out.println("Shader: " + stringBuilder.toString());
	return stringBuilder.toString();
    }
}
