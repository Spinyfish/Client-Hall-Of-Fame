/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.clickgui.impl;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import lodomir.dev.November;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.ui.clickgui.impl.Button;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.render.RenderUtils;

public class Panel {
    public ArrayList<Button> buttons = new ArrayList();
    public double x;
    public double y;
    public double offsetX;
    public double offsetY;
    public boolean dragging;
    public boolean collapsed;
    public Category category;
    public int width = 100;
    public int barHeight = 15;
    private double totalHeight;
    private double scrolled = 0.0;
    private double fixedHeight = 150.0;
    private TTFFontRenderer fr;
    private TTFFontRenderer icons;
    public double settingsHeight;

    public Panel(int x, Category c) {
        this.fr = November.INSTANCE.fm.getFont("SFR 18");
        this.icons = November.INSTANCE.fm.getFont("ICONS 18");
        this.settingsHeight = 0.0;
        this.x = x;
        this.y = 5.0;
        this.category = c;
        int count = 0;
        for (Module mod : November.INSTANCE.moduleManager.getModulesByCategory(this.category)) {
            this.buttons.add(new Button(this, mod, count));
            ++count;
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.dragging) {
            this.x = (double)mouseX + this.offsetX;
            this.y = (double)mouseY + this.offsetY;
        }
        RenderUtils.drawRect(this.x, this.y, this.x + (double)this.width, this.y + (double)this.barHeight + this.fixedHeight, new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()).darker().getRGB());
        if (this.fixedHeight > 1.0) {
            RenderUtils.drawRoundedRect(this.x, this.y + (double)this.barHeight, this.width, this.fixedHeight, 5.0, -15527149);
        }
        this.drawIcon();
        double tempTotalHeight = 0.0;
        this.fr.drawStringWithShadow(this.category.getName(), this.x + 17.0, this.y + 0.5 + (double)(((float)this.barHeight - this.fr.getHeight(this.category.getName())) / 2.0f), -1);
        for (Button b : this.buttons) {
            tempTotalHeight += b.getHeight();
        }
        this.settingsHeight = 0.0;
        this.fixedHeight = 0.0;
        if (!this.collapsed) {
            for (Button b : this.buttons) {
                this.settingsHeight += b.drawScreen(mouseX, mouseY, partialTicks, this.settingsHeight);
            }
            this.fixedHeight = this.totalHeight;
        }
        if (this.fixedHeight > 1.0) {
            this.totalHeight = tempTotalHeight;
        }
    }

    public void drawIcon() {
        switch (this.category) {
            case COMBAT: {
                this.icons.drawStringWithShadow("H", this.x + 3.0, this.y + 1.0 + (double)(((float)this.barHeight - this.icons.getHeight("H")) / 2.0f), -1);
                break;
            }
            case MOVEMENT: {
                this.icons.drawStringWithShadow("G", this.x + 4.5, this.y + 1.0 + (double)(((float)this.barHeight - this.icons.getHeight("G")) / 2.0f), -1);
                break;
            }
            case PLAYER: {
                this.icons.drawStringWithShadow("F", this.x + 5.5, this.y + 1.0 + (double)(((float)this.barHeight - this.icons.getHeight("F")) / 2.0f), -1);
                break;
            }
            case RENDER: {
                this.icons.drawStringWithShadow("D", this.x + 4.0, this.y + 1.0 + (double)(((float)this.barHeight - this.icons.getHeight("D")) / 2.0f), -1);
                break;
            }
            case OTHER: {
                this.icons.drawStringWithShadow("I", this.x + 4.5, this.y + 1.0 + (double)(((float)this.barHeight - this.icons.getHeight("I")) / 2.0f), -1);
                break;
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        for (Button b : this.buttons) {
            b.keyTyped(typedChar, keyCode);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (RenderUtils.isHovered(mouseX, mouseY, this.x, this.y, this.x + (double)this.width, this.y + (double)this.barHeight)) {
            if (mouseButton == 0) {
                this.dragging = true;
                this.offsetX = this.x - (double)mouseX;
                this.offsetY = this.y - (double)mouseY;
            } else if (mouseButton == 1) {
                this.collapsed = !this.collapsed;
            }
        }
        for (Button b : this.buttons) {
            if (this.collapsed) continue;
            b.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragging = false;
        this.offsetY = 0.0;
        this.offsetX = 0.0;
        for (Button b : this.buttons) {
            b.mouseReleased(mouseX, mouseY, state);
        }
    }

    public void scroll(int i, int lastMouseX, int lastMouseY) {
        if (this.scrolled >= 0.0 && i >= 0) {
            return;
        }
        if (-this.scrolled >= this.totalHeight - this.fixedHeight / 2.0 && i <= 0) {
            return;
        }
        if (RenderUtils.isHovered(lastMouseX, lastMouseY, this.x, this.y, this.x + (double)this.width, this.y + this.fixedHeight + (double)this.barHeight)) {
            this.scrolled += (double)i;
        }
    }

    public void onOpenGUI() {
        if (!this.collapsed) {
            this.fixedHeight = 0.0;
        }
        for (Button button : this.buttons) {
            button.onOpenGUI();
        }
    }
}

