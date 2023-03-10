/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package lodomir.dev.ui.clickgui.impl;

import java.awt.Color;
import java.util.ArrayList;
import lodomir.dev.November;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.settings.Setting;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.ui.clickgui.animations.Animation;
import lodomir.dev.ui.clickgui.impl.Panel;
import lodomir.dev.ui.clickgui.impl.settings.CheckBox;
import lodomir.dev.ui.clickgui.impl.settings.Mode;
import lodomir.dev.ui.clickgui.impl.settings.SetBase;
import lodomir.dev.ui.clickgui.impl.settings.Slider;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Button {
    public Module mod;
    public Panel parent;
    public int index;
    public int height;
    public double y;
    private ResourceLocation icon;
    public ArrayList<SetBase> settings = new ArrayList();
    private TTFFontRenderer fr;
    private boolean expanded;
    double settingHeight;
    boolean openedOnce;
    Animation guiAnim;

    public Button(Panel parent, Module mod, int index) {
        this.fr = November.INSTANCE.fm.getFont("SFR 18");
        this.settingHeight = 0.0;
        this.openedOnce = false;
        this.guiAnim = new Animation();
        this.parent = parent;
        this.mod = mod;
        this.index = index;
        this.height = 17;
        for (Setting set : mod.getSettings()) {
            if (set instanceof ModeSetting) {
                this.settings.add(new Mode(set, this));
            }
            if (set instanceof NumberSetting) {
                this.settings.add(new Slider(set, this));
            }
            if (!(set instanceof BooleanSetting)) continue;
            this.settings.add(new CheckBox(set, this));
        }
    }

    public boolean hasSettings() {
        return !this.settings.isEmpty();
    }

    public double drawScreen(int mouseX, int mouseY, float partialTicks, double offset) {
        this.settingHeight = 0.0;
        for (SetBase s : this.settings) {
            if (!s.canRender()) continue;
            this.settingHeight += s.getHeight();
        }
        this.y = this.parent.y + (double)this.parent.barHeight + (double)(this.index * this.height) + offset;
        Color bgColor = new Color(-15066598);
        if (RenderUtils.isHovered(mouseX, mouseY, this.parent.x, this.y, this.parent.x + (double)this.parent.width, this.y + (double)this.height)) {
            bgColor = new Color(-14935012);
        }
        if (this.mod.isEnabled()) {
            bgColor = new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()).darker();
        }
        this.icon = this.expanded ? new ResourceLocation("november/up-arrow.png") : new ResourceLocation("november/down-arrow.png");
        RenderUtils.drawRect(this.parent.x, this.y, this.parent.x + (double)this.parent.width, this.y + (double)this.height, bgColor.getRGB());
        this.fr.drawStringWithShadow(this.mod.getName(), this.parent.x + 3.0, this.y + (double)(((float)this.height - this.fr.getHeight(this.mod.getName())) / 2.0f) + 0.5, this.mod.isEnabled() ? -1 : -2130706433);
        if (this.hasSettings()) {
            RenderUtils.drawImg(this.icon, (int)(this.parent.x + (double)this.parent.width - 10.0), (int)(this.y + (double)(((float)this.height - this.fr.getHeight(this.mod.getName())) / 2.0f) + 1.0), 6, 6);
        }
        this.guiAnim.setSpeed(0.5);
        this.guiAnim.setAmount((int)this.settingHeight);
        this.guiAnim.setReverse(!this.expanded);
        this.guiAnim.updateAnimation();
        GL11.glEnable((int)3089);
        RenderUtils.prepareScissorBox((float)this.parent.x, (float)this.y, (float)(this.parent.x + (double)this.parent.width), (float)(this.y + (double)this.height + this.guiAnim.getValue()));
        int tempSettingsHeight = 0;
        for (SetBase s : this.settings) {
            if (!s.canRender()) continue;
            tempSettingsHeight = (int)((double)tempSettingsHeight + s.drawScreen(mouseX, mouseY, partialTicks, tempSettingsHeight));
        }
        GL11.glDisable((int)3089);
        this.settingHeight = this.guiAnim.getValue();
        return this.guiAnim.getValue();
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (this.expanded) {
            for (SetBase s : this.settings) {
                if (s.isHidden()) continue;
                s.keyTyped(typedChar, keyCode);
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtils.isHovered(mouseX, mouseY, this.parent.x, this.y, this.parent.x + (double)this.parent.width, this.y + (double)this.height)) {
            switch (mouseButton) {
                case 0: {
                    this.mod.toggle();
                    break;
                }
                case 1: {
                    this.guiAnim.start();
                    this.openedOnce = true;
                    this.expanded = !this.expanded;
                }
            }
        } else if (this.expanded) {
            for (SetBase s : this.settings) {
                if (s.isHidden()) continue;
                s.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.expanded) {
            for (SetBase s : this.settings) {
                if (s.isHidden()) continue;
                s.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    public double getHeight() {
        return this.settingHeight + (double)this.height;
    }

    public void onOpenGUI() {
    }
}

