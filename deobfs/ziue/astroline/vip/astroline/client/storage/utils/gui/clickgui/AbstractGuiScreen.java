/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 */
package vip.astroline.client.storage.utils.gui.clickgui;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class AbstractGuiScreen
extends GuiScreen {
    public float scale;
    public float curWidth = 0.0f;
    public float curHeight = 0.0f;

    public AbstractGuiScreen() {
        this(2);
    }

    public AbstractGuiScreen(int scale) {
        this.scale = scale;
    }

    public void doInit() {
    }

    public void drawScr(int mouseX, int mouseY, float partialTicks) {
    }

    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseRelease(int mouseX, int mouseY, int state) {
    }

    public void initGui() {
        this.doInit();
        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.curWidth = (float)this.mc.displayWidth / this.scale;
        this.curHeight = (float)this.mc.displayHeight / this.scale;
        this.drawScr(this.getRealMouseX(), this.getRealMouseY(), partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.mouseClick(this.getRealMouseX(), this.getRealMouseY(), mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.mouseRelease(this.getRealMouseX(), this.getRealMouseY(), state);
        super.mouseReleased(mouseX, mouseY, state);
    }

    public int getRealMouseX() {
        return (int)((float)Mouse.getX() * ((float)this.mc.displayWidth / this.scale) / (float)this.mc.displayWidth);
    }

    public int getRealMouseY() {
        float scaleHeight = (float)this.mc.displayHeight / this.scale;
        return (int)(scaleHeight - (float)Mouse.getY() * scaleHeight / (float)this.mc.displayHeight);
    }

    public void doGlScissor(int x, int y, float width, float height) {
        int scaleFactor = 1;
        float sc = this.scale;
        while ((float)scaleFactor < sc && this.mc.displayWidth / (scaleFactor + 1) >= 320 && this.mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((int)(x * scaleFactor), (int)((int)((float)this.mc.displayHeight - ((float)y + height) * (float)scaleFactor)), (int)((int)(width * (float)scaleFactor)), (int)((int)(height * (float)scaleFactor)));
    }
}
