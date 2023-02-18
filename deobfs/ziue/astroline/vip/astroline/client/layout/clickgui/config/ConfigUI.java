/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.WorldRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.layout.clickgui.ClickGUI
 *  vip.astroline.client.layout.clickgui.config.ConfigSlot
 *  vip.astroline.client.layout.clickgui.config.ConfigUI$MouseInputHandler
 *  vip.astroline.client.layout.clickgui.config.SavePresetScreen
 *  vip.astroline.client.service.config.preset.PresetManager
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.gui.clickgui.AbstractGuiScreen
 *  vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.layout.clickgui.config;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.layout.clickgui.ClickGUI;
import vip.astroline.client.layout.clickgui.config.ConfigSlot;
import vip.astroline.client.layout.clickgui.config.ConfigUI;
import vip.astroline.client.layout.clickgui.config.SavePresetScreen;
import vip.astroline.client.service.config.preset.PresetManager;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.gui.clickgui.AbstractGuiScreen;
import vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class ConfigUI {
    public Minecraft mc = Minecraft.getMinecraft();
    public ClickGUI clickGUI;
    public MouseInputHandler handler = new MouseInputHandler(0);
    public CopyOnWriteArrayList<ConfigSlot> slots = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<ConfigSlot> cloudConfig = new CopyOnWriteArrayList();
    public String currentConfig = "Default";
    public boolean cursorInFrame = false;
    public float minY = -100.0f;
    public float scrollY;
    public float scrollAnimation = 0.0f;
    public float animation = 0.0f;
    public boolean extended = false;
    public int backgroundColor = 0;
    public int fontColor = 0;

    public ConfigUI(ClickGUI clickGUI) {
        this.clickGUI = clickGUI;
        try {
            this.sync();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void init() {
        this.sync();
    }

    public void render(float mouseX, float mouseY, float partialTicks, AbstractGuiScreen parent) {
        float width = parent.curWidth;
        float height = parent.curHeight;
        float target = this.extended ? 100.0f : 0.0f;
        this.animation = AnimationUtils.getAnimationState((float)this.animation, (float)target, (float)(Math.max(10.0f, Math.abs(this.animation - target) * 40.0f) * 0.3f));
        this.backgroundColor = Hud.isLightMode.getValue() != false ? -1184787 : -12565429;
        int n = this.fontColor = Hud.isLightMode.getValue() != false ? -11514032 : -3289651;
        if (this.extended) {
            if (RenderUtil.isHovering((float)mouseX, (float)mouseY, (float)(width - 95.0f), (float)(height - 5.0f - this.animation), (float)(width - 5.0f), (float)(height - 5.0f))) {
                this.cursorInFrame = true;
                if (this.scrollY <= this.minY) {
                    this.scrollY = this.minY;
                }
                if (this.scrollY >= 0.0f) {
                    this.scrollY = 0.0f;
                }
                this.scrollAnimation = AnimationUtils.getAnimationState((float)this.scrollAnimation, (float)this.scrollY, (float)((float)((double)Math.max(10.0f, Math.abs(this.scrollAnimation - this.scrollY) * 40.0f) * 0.3)));
                this.minY = 100.0f;
            } else {
                this.cursorInFrame = false;
            }
        } else {
            this.cursorInFrame = false;
            this.scrollAnimation = 0.0f;
            this.scrollY = 0.0f;
        }
        RenderUtil.drawRect((float)(width - 95.0f), (float)(height - 25.0f - this.animation), (float)(width - 5.0f), (float)(height - 23.0f - this.animation), (int)Hud.hudColor1.getColorInt());
        GL11.glPushMatrix();
        GL11.glEnable((int)3089);
        if (this.animation > 0.0f) {
            parent.doGlScissor((int)width - 195, (int)height - 123, 190.0f, 118.0f);
        } else {
            parent.doGlScissor((int)width - 95, (int)height - 23, 90.0f, 18.0f);
        }
        RenderUtil.drawRect((float)(width - 95.0f), (float)(height - 23.0f - this.animation), (float)(width - 5.0f), (float)(height - 5.0f), (int)this.backgroundColor);
        if (this.animation > 0.0f) {
            float startY = height - this.animation + this.scrollAnimation + 20.0f;
            float moduleYShouldBe = 0.0f;
            if (this.slots.size() > 0) {
                for (ConfigSlot s : this.slots) {
                    if (startY > height - 115.0f && startY < height + 20.0f) {
                        s.render(width - 95.0f, startY);
                    }
                    moduleYShouldBe += 20.0f;
                    startY += 20.0f;
                }
            }
            if (this.cloudConfig.size() > 0) {
                for (ConfigSlot s : this.cloudConfig) {
                    if (startY > height - 115.0f && startY < height + 20.0f) {
                        s.render(width - 95.0f, startY);
                    }
                    moduleYShouldBe += 20.0f;
                    startY += 20.0f;
                }
            }
            this.minY -= moduleYShouldBe;
        }
        RenderUtil.drawRect((float)(width - 95.0f), (float)(height - 23.0f - this.animation), (float)(width - 5.0f), (float)(height - 5.0f - this.animation), (int)this.backgroundColor);
        this.drawGradientRect(width - 95.0f, height - 5.0f - this.animation, width - 5.0f, height - this.animation, RenderUtil.reAlpha((int)ColorUtils.BLACK.c, (float)0.25f), RenderUtil.reAlpha((int)ColorUtils.BLACK.c, (float)0.0f));
        FontManager.sans16.drawString(this.currentConfig, width - 80.0f, height - 20.0f - this.animation, this.fontColor);
        FontManager.icon14.drawString("v", width - 90.0f, height - 18.0f - this.animation, this.fontColor);
        this.drawGradientSideways(width - 40.0f, height - 23.0f - this.animation, width - 22.0f, height - 5.0f - this.animation, RenderUtil.reAlpha((int)this.backgroundColor, (float)0.0f), this.backgroundColor);
        RenderUtil.drawRect((float)(width - 22.0f), (float)(height - 23.0f - this.animation), (float)(width - 5.0f), (float)(height - 5.0f - this.animation), (int)this.backgroundColor);
        FontManager.icon15.drawString("u", width - 18.0f, height - 18.0f - this.animation, this.fontColor);
        GL11.glDisable((int)3089);
        GL11.glPopMatrix();
        if (!this.extended) return;
        if (!RenderUtil.isHovering((float)mouseX, (float)mouseY, (float)(width - 95.0f), (float)(height - 5.0f - this.animation), (float)(width - 5.0f), (float)(height - 5.0f))) return;
        int wheel = Mouse.getDWheel() / 2;
        this.scrollY += (float)wheel;
        if (this.scrollY <= this.minY) {
            this.scrollY = this.minY;
        }
        if (!(this.scrollY >= 0.0f)) return;
        this.scrollY = 0.0f;
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton, AbstractGuiScreen parent) {
        float width = parent.curWidth;
        float height = parent.curHeight;
        if (RenderUtil.isHovering((float)mouseX, (float)mouseY, (float)(width - 95.0f), (float)(height - 23.0f - this.animation), (float)(width - 5.0f), (float)(height - 5.0f - this.animation))) {
            if (RenderUtil.isHovering((float)mouseX, (float)mouseY, (float)(width - 20.0f), (float)(height - 20.0f - this.animation), (float)(width - 8.0f), (float)(height - 8.0f - this.animation))) {
                this.mc.displayGuiScreen((GuiScreen)new SavePresetScreen((GuiScreen)this.clickGUI));
                return;
            }
            this.extended = !this.extended;
        }
        if (!(this.animation > 0.0f)) return;
        if (!RenderUtil.isHovering((float)mouseX, (float)mouseY, (float)(width - 95.0f), (float)(height - 5.0f - this.animation), (float)(width - 5.0f), (float)(height - 5.0f))) return;
        if (this.slots.size() > 0) {
            for (ConfigSlot s : this.slots) {
                s.onClicked((float)mouseX, (float)mouseY, (float)mouseButton);
            }
        }
        if (this.cloudConfig.size() <= 0) return;
        Iterator<ConfigSlot> iterator = this.cloudConfig.iterator();
        while (iterator.hasNext()) {
            ConfigSlot s;
            s = iterator.next();
            s.onClicked((float)mouseX, (float)mouseY, (float)mouseButton);
        }
    }

    public void onClose() {
        for (ConfigSlot s : this.slots) {
            s.selected = false;
        }
        Iterator<ConfigSlot> iterator = this.cloudConfig.iterator();
        while (iterator.hasNext()) {
            ConfigSlot s;
            s = iterator.next();
            s.selected = false;
        }
    }

    public void sync() {
        if (!this.slots.isEmpty()) {
            this.slots.clear();
        }
        Iterator iterator = PresetManager.presets.iterator();
        while (iterator.hasNext()) {
            String p = (String)iterator.next();
            this.slots.add(new ConfigSlot(this, p.replace("#", ""), p.startsWith("#")));
        }
    }

    public void loadPreset(String configName) {
        this.currentConfig = configName;
        PresetManager.loadPreset((String)configName);
        this.sync();
    }

    public void deletePreset(String configName) {
        PresetManager.deletePreset((String)configName);
        this.sync();
    }

    public void drawGradientSideways(float left, float top, float right, float bottom, int startColor, int endColor) {
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
        worldrenderer.pos((double)right, (double)top, 0.0).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)bottom, 0.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
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
}
