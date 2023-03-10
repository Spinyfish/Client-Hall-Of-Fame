/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.clickgui.impl.settings;

import lodomir.dev.November;
import lodomir.dev.settings.Setting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.ui.clickgui.impl.Button;
import lodomir.dev.ui.clickgui.impl.settings.SetBase;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.render.RenderUtils;

public class Mode
extends SetBase {
    private TTFFontRenderer fr;
    public Button parent;
    public double y;
    double x;
    double width;
    public ModeSetting mode;
    int height;

    public Mode(Setting set, Button parent) {
        this.fr = November.INSTANCE.fm.getFont("SFR 18");
        this.setting = set;
        this.parent = parent;
    }

    @Override
    public double drawScreen(int mouseX, int mouseY, float partialTicks, double settingHeight) {
        this.height = 15;
        this.mode = (ModeSetting)this.setting;
        this.x = this.parent.parent.x;
        this.width = this.parent.parent.width;
        this.y = settingHeight + this.parent.y + (double)this.parent.height;
        RenderUtils.drawRect(this.x, this.y, this.x + this.width, this.y + (double)this.height, -14342875);
        this.fr.drawStringWithShadow(this.mode.getName(), this.x + 3.0, this.y + (double)(((float)this.height - this.fr.getHeight(this.mode.getName())) / 2.0f), -1);
        this.fr.drawStringWithShadow(this.mode.getMode(), this.x + this.width - 3.0 - (double)this.fr.getStringWidth(this.mode.getMode()), this.y + (double)(((float)this.height - this.fr.getHeight(this.mode.getMode())) / 2.0f), -1);
        return this.height;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtils.isHovered(mouseX, mouseY, this.x, this.y, this.x + this.width, this.y + (double)this.height)) {
            switch (mouseButton) {
                case 0: {
                    this.mode.cycle(false);
                    break;
                }
                case 1: {
                    this.mode.cycle(true);
                }
            }
        }
    }

    @Override
    public boolean isHidden() {
        return !this.setting.isVisible();
    }
}

