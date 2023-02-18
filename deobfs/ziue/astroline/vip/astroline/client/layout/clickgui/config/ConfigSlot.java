/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.GuiYesNo
 *  net.minecraft.util.EnumChatFormatting
 *  vip.astroline.client.layout.clickgui.config.ConfigUI
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.layout.clickgui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.util.EnumChatFormatting;
import vip.astroline.client.layout.clickgui.config.ConfigUI;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class ConfigSlot {
    public String configName;
    public ConfigUI parent;
    public boolean isCloud;
    public float animation = 0.0f;
    public boolean isHovered = false;
    public boolean selected = false;
    public float x;
    public float y;

    public ConfigSlot(ConfigUI parent, String configName, boolean isCloud) {
        this.parent = parent;
        this.configName = configName;
        this.isCloud = isCloud;
    }

    public void render(float x, float y) {
        this.x = x;
        this.y = y;
        float target = this.selected ? (float)(!this.isCloud ? 15 : 0) : 0.0f;
        this.animation = AnimationUtils.getAnimationState((float)this.animation, (float)target, (float)(Math.max(10.0f, Math.abs(this.animation - target) * 40.0f) * 0.3f));
        FontManager.sans16.drawString(this.configName, x + 15.0f, y - 20.0f, this.parent.fontColor);
        RenderUtil.drawRect((float)(x + 88.0f - this.animation), (float)(y - 23.0f), (float)(x + 90.0f), (float)(y - 5.0f), (int)this.parent.backgroundColor);
        this.parent.drawGradientSideways(x + 68.0f - this.animation, y - 23.0f, x + 88.0f - this.animation, y - 5.0f, RenderUtil.reAlpha((int)this.parent.backgroundColor, (float)0.0f), RenderUtil.reAlpha((int)this.parent.backgroundColor, (float)1.0f));
        FontManager.icon14.drawString(this.configName.equalsIgnoreCase(this.parent.currentConfig) ? "v" : (this.isCloud ? "y" : "x"), x + 5.0f, y - 18.0f, this.parent.fontColor);
        FontManager.icon14.drawString(!this.isCloud ? "w" : "", x + 91.5f - this.animation, y - 18.0f, this.parent.fontColor);
    }

    public void onClicked(float mouseX, float mouseY, float mouseButton) {
        if (mouseButton == 0.0f && RenderUtil.isHovering((float)mouseX, (float)mouseY, (float)(this.x + 15.0f), (float)(this.y - 23.0f), (float)(this.x + 90.0f), (float)(this.y - 5.0f))) {
            if (!this.selected) {
                this.parent.loadPreset((this.isCloud ? "#" : "") + this.configName);
            } else {
                Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiYesNo((result, id) -> {
                    if (result) {
                        this.parent.deletePreset(this.configName);
                    }
                    Minecraft.getMinecraft().displayGuiScreen((GuiScreen)this.parent.clickGUI);
                }, "Are you sure to remove the preset named " + EnumChatFormatting.YELLOW + this.configName + EnumChatFormatting.RESET + " ?", "", 0));
                this.selected = false;
            }
        }
        if (mouseButton != 1.0f) return;
        this.selected = RenderUtil.isHovering((float)mouseX, (float)mouseY, (float)(this.x + 15.0f), (float)(this.y - 23.0f), (float)(this.x + 90.0f), (float)(this.y - 5.0f)) ? !this.selected : false;
    }
}
