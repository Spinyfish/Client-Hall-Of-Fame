/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.dropclick;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lodomir.dev.November;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.ui.dropclick.ModuleButton;
import lodomir.dev.ui.dropclick.comps.Component;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.render.RenderUtils;

public class Frame {
    public int x;
    public int y;
    public int width;
    public int height;
    public int dragX;
    public int dragY;
    public Category category;
    public boolean dragging;
    public boolean extended;
    public TTFFontRenderer fr;
    private List<ModuleButton> buttons;

    public Frame(Category category, int x, int y, int width, int height) {
        this.fr = November.INSTANCE.fm.getFont("PRODUCTSANS 18");
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dragging = false;
        this.extended = true;
        this.buttons = new ArrayList<ModuleButton>();
        int offset = height;
        for (Module mod : November.INSTANCE.moduleManager.getModulesByCategory(category)) {
            this.buttons.add(new ModuleButton(mod, this, offset));
            offset += height;
        }
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, new Color(24, 24, 24).getRGB());
        this.fr.drawStringWithShadow(this.category.getName(), this.x + (this.width / 2 - this.fr.getStringWidth(this.category.getName()) / 2), (float)this.y + ((float)(this.height / 2) - this.fr.getHeight(this.category.getName()) / 2.0f), -1);
        this.fr.drawStringWithShadow(this.extended ? "-" : "+", (float)(this.x + this.width) - ((float)(this.height / 2) - this.fr.getHeight(this.extended ? "-" : "+") / 2.0f) - 8.0f, (float)this.y + ((float)(this.height / 2) - this.fr.getHeight(this.extended ? "-" : "+") / 2.0f), -1);
        if (this.extended) {
            for (ModuleButton button : this.buttons) {
                button.render(mouseX, mouseY, partialTicks);
            }
        }
    }

    protected void mouseClicked(double mouseX, double mouseY, int button) throws IOException {
        if (this.isHovered(mouseX, mouseY)) {
            if (button == 0) {
                this.dragging = true;
                this.dragX = (int)(mouseX - (double)this.x);
                this.dragY = (int)(mouseY - (double)this.y);
            } else if (button == 1) {
                boolean bl = this.extended = !this.extended;
            }
        }
        if (this.extended) {
            for (ModuleButton mb : this.buttons) {
                mb.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.dragging) {
            this.dragging = false;
        }
        for (ModuleButton mb : this.buttons) {
            mb.mouseReleased(mouseX, mouseY, button);
        }
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX > (double)this.x && mouseX < (double)(this.x + this.width) && mouseY > (double)this.y && mouseY < (double)(this.y + this.height);
    }

    public void updatePos(double mouseX, double mouseY) {
        if (this.dragging) {
            this.x = (int)(mouseX - (double)this.dragX);
            this.y = (int)(mouseY - (double)this.dragY);
        }
    }

    public void updateButtons() {
        int offset = this.height;
        for (ModuleButton button : this.buttons) {
            button.offset = offset;
            offset += this.height;
            if (!button.extended) continue;
            for (Component component : button.components) {
                if (!component.setting.isVisible()) continue;
                offset += this.height;
            }
        }
    }
}

