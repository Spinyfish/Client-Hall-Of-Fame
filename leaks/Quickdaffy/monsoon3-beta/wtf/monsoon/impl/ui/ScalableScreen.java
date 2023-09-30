/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.ui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public abstract class ScalableScreen
extends GuiScreen {
    protected ScaledResolution resolution;
    protected float scaledWidth;
    protected float scaledHeight;

    public abstract void init();

    public abstract void render(float var1, float var2);

    public abstract void click(float var1, float var2, int var3);

    @Override
    public final void initGui() {
        this.init();
        super.initGui();
    }

    @Override
    public final void setWorldAndResolution(Minecraft mc, int displayWidth, int displayHeight) {
        this.mc = mc;
        this.fontRendererObj = mc.fontRendererObj;
        this.width = displayWidth;
        this.height = displayHeight;
        this.buttonList.clear();
        this.resolution = new ScaledResolution(this.mc);
        float scaleFactor = this.getScaleFactor();
        this.scaledWidth = (float)this.width / scaleFactor;
        this.scaledHeight = (float)this.height / scaleFactor;
        this.initGui();
    }

    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float scaleFactor = this.getScaleFactor();
        GL11.glPushMatrix();
        GL11.glScalef((float)scaleFactor, (float)scaleFactor, (float)scaleFactor);
        this.render((float)mouseX / scaleFactor, (float)mouseY / scaleFactor);
        GL11.glPopMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int state) throws IOException {
        float scaleFactor = this.getScaleFactor();
        this.click((float)mouseX / scaleFactor, (float)mouseY / scaleFactor, state);
        super.mouseClicked(mouseX, mouseY, state);
    }

    public float getScaleFactor() {
        return 1.0f / ((float)this.resolution.getScaleFactor() * 0.5f);
    }

    public float getScaledWidth() {
        return this.scaledWidth;
    }

    public float getScaledHeight() {
        return this.scaledHeight;
    }

    public ScaledResolution getResolution() {
        return this.resolution;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

