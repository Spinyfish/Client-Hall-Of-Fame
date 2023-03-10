/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.dropclick;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.render.HUD;
import lodomir.dev.settings.BooleanSetting;
import lodomir.dev.settings.ModeSetting;
import lodomir.dev.settings.NumberSetting;
import lodomir.dev.settings.Setting;
import lodomir.dev.ui.dropclick.Frame;
import lodomir.dev.ui.dropclick.comps.Component;
import lodomir.dev.ui.dropclick.comps.impl.CheckBox;
import lodomir.dev.ui.dropclick.comps.impl.ModeButton;
import lodomir.dev.ui.dropclick.comps.impl.Slider;
import lodomir.dev.utils.render.RenderUtils;

public class ModuleButton {
    public Module module;
    public Frame parent;
    public int offset;
    public List<Component> components;
    public boolean extended;
    public Setting setting;

    public ModuleButton(Module module, Frame parent, int offset) {
        this.module = module;
        this.parent = parent;
        this.offset = offset;
        this.components = new ArrayList<Component>();
        this.extended = false;
        int setOffset = parent.height;
        for (Setting setting : module.getSettings()) {
            if (setting instanceof BooleanSetting) {
                this.components.add(new CheckBox(setting, this, setOffset));
                setOffset += 12;
                continue;
            }
            if (setting instanceof ModeSetting) {
                this.components.add(new ModeButton(setting, this, setOffset));
                setOffset += 12;
                continue;
            }
            if (!(setting instanceof NumberSetting)) continue;
            this.components.add(new Slider(setting, this, setOffset));
            setOffset += 12;
        }
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRect(this.parent.x, this.parent.y + this.offset, this.parent.x + this.parent.width, this.parent.y + this.offset + this.parent.height, this.module.isEnabled() ? HUD.getColor() : new Color(35, 35, 35).getRGB());
        if (this.isHovered(mouseX, mouseY)) {
            RenderUtils.drawRect(this.parent.x, this.parent.y + this.offset, this.parent.x + this.parent.width, this.parent.y + this.offset + this.parent.height, this.module.isEnabled() ? HUD.getColor() : new Color(40, 40, 40).getRGB());
        }
        this.parent.fr.drawStringWithShadow(this.module.getName(), this.parent.x + 2, this.parent.y + this.offset + 2, -1);
        if (this.extended) {
            for (Component component : this.components) {
                component.render(mouseX, mouseY, partialTicks);
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) throws IOException {
        if (this.isHovered(mouseX, mouseY)) {
            if (button == 0) {
                this.module.toggle();
            } else if (button == 1) {
                this.extended = !this.extended;
                this.parent.updateButtons();
            }
        }
        for (Component component : this.components) {
            component.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        for (Component component : this.components) {
            component.mouseReleased(mouseX, mouseY, button);
        }
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX > (double)this.parent.x && mouseX < (double)(this.parent.x + this.parent.width) && mouseY > (double)(this.parent.y + this.offset) && mouseY < (double)(this.parent.y + this.offset + this.parent.height);
    }
}

