/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  org.lwjgl.opengl.GL11
 */
package lodomir.dev.ui.clickgui.impl.settings;

import com.mojang.realmsclient.gui.ChatFormatting;
import lodomir.dev.November;
import lodomir.dev.settings.Setting;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.ui.clickgui.impl.Button;
import lodomir.dev.ui.clickgui.impl.settings.SetBase;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class CheckBox
extends SetBase {
    private TTFFontRenderer fr;
    public Button parent;
    public double y;
    double x;
    double width;
    BooleanSetting mode;
    boolean sliding;
    int height;

    public CheckBox(Setting set, Button parent) {
        this.fr = November.INSTANCE.fm.getFont("SFR 18");
        this.setting = set;
        this.parent = parent;
    }

    @Override
    public double drawScreen(int mouseX, int mouseY, float partialTicks, double settingHeight) {
        this.height = 15;
        this.mode = (BooleanSetting)this.setting;
        this.x = this.parent.parent.x;
        this.width = this.parent.parent.width;
        this.y = settingHeight + this.parent.y + (double)this.parent.height;
        RenderUtils.drawRect(this.x, this.y, this.x + this.width, this.y + (double)this.height, -14342875);
        this.fr.drawStringWithShadow(this.mode.getName(), this.x + 3.0, this.y + (double)(((float)this.height - this.fr.getHeight(this.mode.getName())) / 2.0f) + 0.5, -1);
        RenderUtils.drawRect(this.x + this.width - 3.0 - 11.0, this.y + 0.0 + (double)(this.height / 2) - 5.0, this.x + this.width - 3.0, this.y + 11.0 + (double)(this.height / 2) - 5.0, -12369085);
        RenderUtils.drawRect(this.x + this.width - 3.0 - 10.0, this.y + 1.0 + (double)(this.height / 2) - 5.0, this.x + this.width - 4.0, this.y + 10.0 + (double)(this.height / 2) - 5.0, -14474461);
        if (this.mode.isEnabled()) {
            GL11.glColor4d((double)1.0, (double)1.0, (double)1.0, (double)1.0);
            this.fr.drawStringWithShadow(ChatFormatting.BOLD + "X", this.x + this.width - 2.0 - 10.5, this.y + (double)(this.height / 2) - 3.5, -1);
        }
        return this.height;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtils.isHovered(mouseX, mouseY, this.x + this.width - 3.0 - 11.0, this.y, this.x + this.width - 3.0, this.y + (double)this.height)) {
            this.mode.toggle();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.sliding = false;
    }

    @Override
    public boolean isHidden() {
        return !this.setting.isVisible();
    }
}

