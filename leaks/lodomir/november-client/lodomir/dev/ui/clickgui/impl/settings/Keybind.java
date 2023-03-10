/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.clickgui.impl.settings;

import java.awt.Color;
import lodomir.dev.November;
import lodomir.dev.modules.Module;
import lodomir.dev.ui.clickgui.impl.Button;
import lodomir.dev.ui.clickgui.impl.settings.SetBase;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.render.RenderUtils;

public class Keybind
extends SetBase {
    private TTFFontRenderer fr;
    public Button parent;
    public double y;
    public Module module;
    double x;
    double width;
    int height;
    private boolean isTyping;
    private double otherWidth;
    private double otherX;
    private double otherX2;
    private double otherY2;

    public Keybind(Button parent) {
        this.fr = November.INSTANCE.fm.getFont("SFR 18");
        this.isTyping = false;
        this.parent = parent;
    }

    @Override
    public double drawScreen(int mouseX, int mouseY, float partialTicks, double settingHeight) {
        this.height = 15;
        this.x = this.parent.parent.x;
        this.width = this.parent.parent.width;
        this.y = settingHeight + this.parent.y + (double)this.parent.height;
        RenderUtils.drawRect(this.x, this.y, this.x + this.width, this.y + (double)this.height, -14342875);
        this.fr.drawStringWithShadow("Bind", this.x + 3.0, this.y + (double)(((float)this.height - this.fr.getHeight("Bind")) / 2.0f) + 0.5, -1);
        String keyString = this.isTyping ? "..." : String.valueOf(this.module.getKey());
        this.otherWidth = 40.0;
        this.otherX = this.x + this.width - this.otherWidth - 4.0;
        this.otherX2 = this.x + this.width - 4.0;
        this.otherY2 = this.y + (double)(((float)this.height - this.fr.getHeight(keyString)) / 2.0f);
        RenderUtils.drawRoundedRect(this.otherX, this.y + 2.0, this.otherX2, this.y + (double)this.height - 2.0, 8.0, new Color(23, 23, 23, 200).getRGB());
        this.fr.drawStringWithShadow(keyString, this.otherX + this.otherWidth / 2.0 - (double)(this.fr.getStringWidth(keyString) / 2), this.otherY2 + 0.5, -1);
        return this.height;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        switch (mouseButton) {
            case 0: {
                if (!RenderUtils.isHovered(mouseX, mouseY, this.otherX, this.y + 2.0, this.otherX2, this.y + (double)this.height - 2.0)) break;
                this.isTyping = true;
                break;
            }
            case 1: {
                this.isTyping = false;
            }
        }
    }

    @Override
    public boolean isHidden() {
        return !this.setting.isVisible();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.isTyping && keyCode == 1) {
            this.isTyping = false;
            return;
        }
        if (this.isTyping && keyCode == 14) {
            this.isTyping = false;
            this.module.setKey(0);
        }
        if (this.isTyping) {
            this.module.setKey(keyCode);
            this.isTyping = false;
        }
        super.keyTyped(typedChar, keyCode);
    }
}

