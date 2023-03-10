/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.transclick.component;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import lodomir.dev.November;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.ui.transclick.component.Component;
import lodomir.dev.ui.transclick.component.components.Button;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.client.gui.FontRenderer;

public class Frame {
    public ArrayList<Component> components = new ArrayList();
    public Category category;
    private boolean open;
    private int width;
    private int y;
    private int x;
    private int barHeight;
    private boolean isDragging;
    public int dragX;
    public int dragY;

    public Frame(Category cat) {
        this.category = cat;
        this.width = 88;
        this.x = 5;
        this.y = 5;
        this.barHeight = 13;
        this.dragX = 0;
        this.open = true;
        this.isDragging = false;
        int tY = this.barHeight;
        for (Module mod : November.INSTANCE.moduleManager.getModulesByCategory(this.category)) {
            Button modButton = new Button(mod, this, tY);
            this.components.add(modButton);
            tY += 12;
        }
    }

    public ArrayList<Component> getComponents() {
        return this.components;
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public void setDrag(boolean drag) {
        this.isDragging = drag;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void renderFrame(FontRenderer fontRenderer) {
        TTFFontRenderer fr = November.INSTANCE.fm.getFont("ARIAL 18");
        RenderUtils.drawBorderedRect(this.x, this.y, this.x + this.width, this.y + this.barHeight, 0.5, -1, -1);
        fr.drawStringWithShadow(this.category.getName(), this.x, ((float)this.y + 0.0f) * 1.0f + 4.0f, new Color(240, 240, 240).getRGB());
        if (this.open && !this.components.isEmpty()) {
            for (Component component : this.components) {
                component.renderComponent();
            }
        }
    }

    public void refresh() {
        int off = this.barHeight;
        for (Component comp : this.components) {
            comp.setOff(off);
            off += comp.getHeight();
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    protected void mouseClicked(double mouseX, double mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            this.isDragging = true;
            this.dragX = (int)(mouseX - (double)this.x);
            this.dragY = (int)(mouseY - (double)this.y);
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isDragging) {
            this.isDragging = false;
        }
    }

    public void updatePosition(int mouseX, int mouseY) {
        if (this.isDragging) {
            this.setX(mouseX - this.dragX);
            this.setY(mouseY - this.dragY);
        }
    }

    public boolean isWithinHeader(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight;
    }
}

