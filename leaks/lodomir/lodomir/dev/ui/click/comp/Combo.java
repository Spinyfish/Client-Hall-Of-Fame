/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.click.comp;

import java.awt.Color;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.ui.click.Clickgui;
import lodomir.dev.ui.click.comp.Comp;

public class Combo
extends Comp {
    private ModeSetting setting;

    public Combo(double x, double y, Clickgui parent, Module module, ModeSetting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.module = module;
        this.setting = setting;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isInside(mouseX, mouseY, this.parent.posX + this.x, this.parent.posY + this.y, this.x + this.parent.width + (double)this.parent.fr.getStringWidth(this.setting.getName()), this.parent.posY + this.y + 10.0) && mouseButton == 0) {
            this.setting.cycle(false);
        } else if (this.isInside(mouseX, mouseY, this.parent.posX + this.x, this.parent.posY + this.y, this.x + this.parent.width + (double)this.parent.fr.getStringWidth(this.setting.getName()), this.parent.posY + this.y + 10.0) && mouseButton == 1) {
            this.setting.cycle(true);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        this.parent.fr.drawStringWithShadow(this.setting.getName() + ": " + this.setting.getMode(), (int)(this.parent.posX + this.x + 30.0), (int)(this.parent.posY + this.y + 1.0), new Color(200, 200, 200).getRGB());
    }
}

