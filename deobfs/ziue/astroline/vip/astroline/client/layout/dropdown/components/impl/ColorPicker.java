/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.WorldRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.layout.dropdown.components.Component
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.service.module.value.ColorValue
 *  vip.astroline.client.storage.utils.other.TimeHelper
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.layout.dropdown.components.impl;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.Component;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.service.module.value.ColorValue;
import vip.astroline.client.storage.utils.other.TimeHelper;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class ColorPicker
extends Component {
    TimeHelper clickTimer = new TimeHelper();
    float hue;
    float saturation;
    float brightness;
    boolean expanded;
    boolean draggingHue;
    boolean draggingSaturation;
    boolean draggingBrightness;
    float expandedPosX = 0.0f;
    float expandedPosY = 0.0f;
    ColorValue colorValue;
    boolean heldMouse;
    boolean held;

    private int getColor() {
        return this.colorValue.getColorInt();
    }

    public ColorPicker(ColorValue colorValue, Panel parent, int offX, int offY, String title) {
        super(parent, offX, offY, title);
        this.width = 36;
        this.height = 10;
        this.type = "Colour Picker";
        this.colorValue = colorValue;
    }

    private float[] getColourPos() {
        return new float[]{this.x + 78, (float)this.y + 3.5f};
    }

    private float[] getExpandedPos() {
        return new float[]{(float)this.x + this.expandedPosX, (float)this.y + this.expandedPosY};
    }

    public void render(int mouseX, int mouseY) {
        float colourPosX = this.getColourPos()[0];
        float colourPosY = this.getColourPos()[1];
        FontManager.tiny.drawString(this.colorValue.getKey(), (float)(this.x + 10), (float)(this.y + 3), Hud.isLightMode.getValue() != false ? ColorUtils.GREY.c : -1);
        int BorderColor = new Color(this.getColor()).darker().darker().getRGB();
        if (this == Astroline.INSTANCE.getDropdown().getCurrentColourPicker() && this.expanded) {
            BorderColor = -1;
        }
        Gui.drawRect((double)(colourPosX - 6.5f), (double)(colourPosY - 0.5f), (double)(colourPosX + 5.25f), (double)(colourPosY + 5.5f), (int)BorderColor);
        Gui.drawRect((double)(colourPosX - 6.0f), (double)colourPosY, (double)(colourPosX + 5.25f), (double)(colourPosY + 5.0f), (int)this.getColor());
    }

    public void mouseUpdates(int mouseX, int mouseY, boolean var3) {
        if (var3) {
            if (this.isHoveredColour(mouseX, mouseY) && !this.held) {
                this.clickTimer.reset();
                boolean bl = this.expanded = !this.expanded;
                if (this.expanded) {
                    if (Keyboard.isKeyDown((int)42)) {
                        this.colorValue.setRainbow(!this.colorValue.isRainbow());
                    } else {
                        Astroline.INSTANCE.getDropdown().setCurrentColourPicker(this);
                        this.expandedPosX = mouseX - this.x;
                        this.expandedPosY = mouseY - this.y;
                    }
                }
            }
            this.held = true;
        } else {
            this.held = false;
        }
    }

    public boolean isHoveredPicker(int mouseX, int mouseY) {
        float[] expandedPos = this.getExpandedPos();
        float x = expandedPos[0];
        float y = expandedPos[1];
        float width = 100.0f;
        float height = 65.0f;
        if (!((float)mouseX >= x)) return false;
        if (!((float)mouseX <= x + width)) return false;
        if (!((float)mouseY >= y)) return false;
        if (!((float)mouseY <= y + height)) return false;
        return true;
    }

    public void renderColourPicker() {
        float[] scissorData = GuiRenderUtils.getScissor();
        if (scissorData[0] == -1.0f) {
            GuiRenderUtils.endCrop();
        }
        this.updateValues();
        float[] expandedPos = this.getExpandedPos();
        float x = expandedPos[0];
        float y = expandedPos[1];
        RenderUtil.drawRoundedRect((float)x, (float)y, (float)(x + 100.0f), (float)(y + 65.0f), (float)3.0f, (int)ClickGUI.backgroundColor);
        this.drawGradientRect(x + 5.0f, y + 5.0f, x + 35.0f, y + 20.0f, true, -65536, -16711936);
        this.drawGradientRect(x + 35.0f, y + 5.0f, x + 65.0f, y + 20.0f, true, -16711936, -16776961);
        this.drawGradientRect(x + 65.0f, y + 5.0f, x + 95.0f, y + 20.0f, true, -16776961, -65536);
        this.drawGradientRect(x + 5.0f, y + 25.0f, x + 95.0f, y + 40.0f, true, -1, this.getColor());
        this.drawGradientRect(x + 5.0f, y + 45.0f, x + 95.0f, y + 60.0f, true, -16777216, this.getColor());
        this.updateValues();
        GuiRenderUtils.drawBorderedRect((float)(x + 5.0f + this.hue * 90.0f - 1.0f), (float)(y + 5.0f), (float)2.0f, (float)15.0f, (float)1.0f, (int)-1, (int)-16777216);
        GuiRenderUtils.drawBorderedRect((float)(x + 5.0f + this.saturation * 90.0f - 1.0f), (float)(y + 25.0f), (float)2.0f, (float)15.0f, (float)1.0f, (int)-1, (int)-16777216);
        GuiRenderUtils.drawBorderedRect((float)(x + 5.0f + this.brightness * 90.0f - 1.0f), (float)(y + 45.0f), (float)2.0f, (float)15.0f, (float)1.0f, (int)-1, (int)-16777216);
        if (scissorData[0] != -1.0f) return;
        GuiRenderUtils.beginCrop((float)scissorData[0], (float)scissorData[1], (float)scissorData[2], (float)scissorData[3], (float)scissorData[4]);
    }

    public void mouseHeldColour(int mouseX, int mouseY) {
        if (!this.expanded) return;
        float[] pos = this.getExpandedPos();
        float PosX = pos[0];
        float PosY = pos[1];
        float HuePosY = PosY + 5.0f;
        float SatPosY = PosY + 25.0f;
        float BriPosY = PosY + 45.0f;
        if ((float)mouseX >= PosX + 5.0f && (float)mouseX <= PosX + 95.0f) {
            if ((float)mouseY >= HuePosY && (float)mouseY <= HuePosY + 15.0f) {
                this.draggingHue = true;
                this.draggingSaturation = false;
                this.draggingBrightness = false;
            } else if ((float)mouseY >= BriPosY && (float)mouseY <= BriPosY + 15.0f) {
                this.draggingBrightness = true;
                this.draggingHue = false;
                this.draggingSaturation = false;
            } else if ((float)mouseY >= SatPosY && (float)mouseY <= SatPosY + 15.0f) {
                this.draggingSaturation = true;
                this.draggingHue = false;
                this.draggingBrightness = false;
            }
            float deltaX = (float)mouseX - (PosX + 5.0f);
            float currentPercentage = 0.0f;
            currentPercentage = Math.min(Math.max(0.0f, deltaX), 90.0f) / 90.0f;
            if (this.draggingHue) {
                this.hue = currentPercentage;
            } else if (this.draggingSaturation) {
                this.saturation = currentPercentage;
            } else {
                if (!this.draggingBrightness) return;
                this.brightness = currentPercentage;
            }
            this.setColorValue(this.hue, this.saturation, this.brightness);
            this.updateValues();
        } else {
            this.draggingBrightness = false;
            this.draggingSaturation = false;
            this.draggingHue = false;
        }
    }

    private boolean isHoveredColour(int mouseX, int mouseY) {
        if (!((float)mouseX >= this.getColourPos()[0] - 0.5f)) return false;
        if (!((double)mouseX <= (double)this.getColourPos()[0] + 12.5)) return false;
        if (!((float)mouseY >= this.getColourPos()[1] - 0.5f)) return false;
        if (!((double)mouseY <= (double)this.getColourPos()[1] + 5.5)) return false;
        return true;
    }

    public void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.shadeModel((int)7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)right, (double)top, 0.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawGradientRect(double left, double top, double right, double bottom, boolean sideways, int startColor, int endColor) {
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glShadeModel((int)7425);
        GL11.glBegin((int)7);
        RenderUtil.color((int)startColor);
        if (sideways) {
            GL11.glVertex2d((double)left, (double)top);
            GL11.glVertex2d((double)left, (double)bottom);
            RenderUtil.color((int)endColor);
            GL11.glVertex2d((double)right, (double)bottom);
            GL11.glVertex2d((double)right, (double)top);
        } else {
            GL11.glVertex2d((double)left, (double)top);
            RenderUtil.color((int)endColor);
            GL11.glVertex2d((double)left, (double)bottom);
            GL11.glVertex2d((double)right, (double)bottom);
            RenderUtil.color((int)startColor);
            GL11.glVertex2d((double)right, (double)top);
        }
        GL11.glEnd();
        GL11.glDisable((int)3042);
        GL11.glShadeModel((int)7424);
        GL11.glEnable((int)3553);
    }

    public void mouseReleased() {
        this.draggingBrightness = false;
        this.draggingSaturation = false;
        this.draggingHue = false;
    }

    private void updateValues() {
        float[] colour = this.colorValue.getColorHSB();
        this.hue = colour[0];
        this.saturation = colour[1];
        this.brightness = colour[2];
    }

    private void setColorValue(float hue, float saturation, float brightness) {
        this.colorValue.setValueInt(Color.getHSBColor(hue, saturation, brightness).getRGB());
    }

    public boolean isExpanded() {
        return this.expanded;
    }
}
