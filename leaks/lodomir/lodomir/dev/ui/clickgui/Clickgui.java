/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package lodomir.dev.ui.clickgui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import lodomir.dev.modules.Category;
import lodomir.dev.ui.clickgui.impl.Panel;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class Clickgui
extends GuiScreen {
    public ArrayList<Panel> panels = new ArrayList();
    public int lastMouseX;
    public int lastMouseY;

    public Clickgui() {
        int count = 0;
        for (Category c : Category.values()) {
            this.panels.add(new Panel(10 + count * 105, c));
            ++count;
        }
    }

    public void openClickGUI() {
        this.mc.displayGuiScreen(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        RenderUtils.gradient(height, width, sr.getScaledWidth(), sr.getScaledHeight(), true, new Color(14, 14, 14, 153), new Color(0, 0, 0));
        for (Panel panel : this.panels) {
            panel.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (Panel panel : this.panels) {
            panel.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Panel panel : this.panels) {
            panel.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Panel panel : this.panels) {
            panel.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getDWheel();
        if (i != 0) {
            if (i < 0) {
                i = 1;
            }
            if (i > 0) {
                i = -1;
            }
            if (!Clickgui.isShiftKeyDown()) {
                i *= 7;
            }
            for (Panel panel : this.panels) {
                panel.scroll(i, this.lastMouseX, this.lastMouseY);
            }
        }
    }
}

