/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.client.audio.SoundHandler
 *  net.minecraft.util.ResourceLocation
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.layout.altMgr;

import java.awt.Color;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class AltButton {
    public int buttonID;
    public float x;
    public float y;
    public float width;
    public float height;
    public String displayString;
    public boolean isEnabled;
    public boolean isHovered = false;

    public AltButton(int buttonID, float x, float y, float width, float height, String displayStr) {
        this.buttonID = buttonID;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.displayString = displayStr;
        this.isEnabled = true;
    }

    public void drawButton(int mouseX, int mouseY) {
        this.isHovered = RenderUtil.isHovering((float)mouseX, (float)mouseY, (float)this.x, (float)this.y, (float)(this.x + this.width), (float)(this.y + this.height));
        int buttonColor = !this.isEnabled ? RenderUtil.darker((int)-6596170, (int)45) : -6596170;
        GuiRenderUtils.drawBorderedRect((float)this.x, (float)this.y, (float)this.width, (float)this.height, (float)0.5f, (int)new Color(-2013265920, true).getRGB(), (int)Hud.hudColor1.getColorInt());
        FontManager.sans18.drawString(this.displayString, this.x + this.width / 2.0f - FontManager.sans18.getStringWidth(this.displayString) / 2.0f, this.y + this.height / 2.0f - 7.0f, !this.isEnabled ? -6185828 : (this.isHovered && this.isEnabled ? Hud.hudColor1.getColor().brighter().getRGB() : -1));
        if (!this.isEnabled) return;
        if (!this.isHovered) return;
        GuiRenderUtils.drawBorderedRect((float)this.x, (float)this.y, (float)this.width, (float)this.height, (float)0.5f, (int)new Color(0x22000000, true).getRGB(), (int)Hud.hudColor1.getColor().brighter().getRGB());
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound((ISound)PositionedSoundRecord.create((ResourceLocation)new ResourceLocation("gui.button.press"), (float)1.0f));
    }

    public boolean isHovered() {
        return this.isHovered;
    }
}
