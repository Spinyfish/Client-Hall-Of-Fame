/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.click.comp;

import java.awt.Color;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.ui.click.Clickgui;
import lodomir.dev.ui.click.comp.Comp;
import lodomir.dev.utils.render.RenderUtils;

public class CheckBox
extends Comp {
    private BooleanSetting setting;

    public CheckBox(double x, double y, Clickgui parent, Module module, BooleanSetting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.module = module;
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        RenderUtils.drawRect(this.parent.posX + this.x + 40.0, this.parent.posY + this.y, this.parent.posX + this.x - 10.0 + 40.0, this.parent.posY + this.y + 10.0, this.setting.isEnabled() ? this.parent.color : new Color(30, 30, 30).getRGB());
        this.parent.fr.drawStringWithShadow(this.setting.getName(), (int)(this.parent.posX + this.x + 43.0), (int)(this.parent.posY + this.y + 1.0), new Color(200, 200, 200).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isInside(mouseX, mouseY, this.parent.posX + this.x, this.parent.posY + this.y, this.parent.width + this.x - 10.0 + 40.0, this.parent.posY + this.y + 10.0) && mouseButton == 0) {
            this.setting.toggle();
        }
    }
}

