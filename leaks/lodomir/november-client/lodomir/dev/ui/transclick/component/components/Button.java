/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.transclick.component.components;

import java.awt.Color;
import java.util.ArrayList;
import lodomir.dev.November;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.BooleanSetting;
import lodomir.dev.settings.ModeSetting;
import lodomir.dev.settings.NumberSetting;
import lodomir.dev.settings.Setting;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.ui.transclick.component.Component;
import lodomir.dev.ui.transclick.component.Frame;
import lodomir.dev.ui.transclick.component.components.sub.Checkbox;
import lodomir.dev.ui.transclick.component.components.sub.Keybind;
import lodomir.dev.ui.transclick.component.components.sub.ModeButton;
import lodomir.dev.ui.transclick.component.components.sub.Slider;
import lodomir.dev.utils.render.RenderUtils;

public class Button
extends Component {
    public Module mod;
    public Frame parent;
    public int offset;
    private boolean isHovered;
    private ArrayList<Component> subcomponents;
    public TTFFontRenderer fr;
    public boolean open;
    private int height;

    public Button(Module mod, Frame parent, int offset) {
        this.fr = November.INSTANCE.fm.getFont("ARIAL 18");
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
        this.subcomponents = new ArrayList();
        this.open = false;
        this.height = 12;
        int opY = offset + 12;
        for (Setting setting : mod.getSettings()) {
            if (!setting.isVisible()) continue;
            if (setting instanceof ModeSetting) {
                this.subcomponents.add(new ModeButton((ModeSetting)setting, this, mod, opY));
                opY += 12;
                continue;
            }
            if (setting instanceof NumberSetting) {
                this.subcomponents.add(new Slider((NumberSetting)setting, this, opY));
                opY += 12;
                continue;
            }
            if (!(setting instanceof BooleanSetting)) continue;
            this.subcomponents.add(new Checkbox((BooleanSetting)setting, this, opY));
            opY += 12;
        }
        this.subcomponents.add(new Keybind(this, opY));
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
        int opY = this.offset + 12;
        for (Component comp : this.subcomponents) {
            comp.setOff(opY);
            opY += 12;
        }
    }

    @Override
    public void renderComponent() {
        RenderUtils.drawRect(this.parent.getX(), this.parent.getY() + this.offset, this.parent.getX() + this.parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? new Color(15, 15, 15, 191).getRGB() : new Color(30, 30, 30, 191).getRGB());
        this.fr.drawString(this.mod.getName(), this.parent.getX() + 2 + 2, this.parent.getY() + this.offset + 2 + 1, this.mod.isEnabled() ? -1 : new Color(190, 190, 190).getRGB());
        if (this.subcomponents.size() > 2) {
            this.fr.drawString(this.open ? "-" : "+", this.parent.getX() + this.parent.getWidth() - 10, this.parent.getY() + this.offset + 4, this.mod.isEnabled() ? -1 : new Color(190, 190, 190).getRGB());
        }
        if (this.open && !this.subcomponents.isEmpty()) {
            for (Component comp : this.subcomponents) {
                comp.renderComponent();
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.open) {
            return 12 * (this.subcomponents.size() + 1);
        }
        return 12;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = this.isMouseOnButton(mouseX, mouseY);
        if (!this.subcomponents.isEmpty()) {
            for (Component comp : this.subcomponents) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.mod.toggle();
        }
        if (this.isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.open = !this.open;
            this.parent.refresh();
        }
        for (Component comp : this.subcomponents) {
            comp.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Component comp : this.subcomponents) {
            comp.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        for (Component comp : this.subcomponents) {
            comp.keyTyped(typedChar, key);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.getX() && x < this.parent.getX() + this.parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset;
    }
}

