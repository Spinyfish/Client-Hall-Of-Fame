/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.WorldRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  optifine.Config
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.input.Cursor
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.clickgui.ClickGUI$CategoryButton
 *  vip.astroline.client.layout.clickgui.ClickGUI$CategoryModuleList
 *  vip.astroline.client.layout.clickgui.ClickGUI$MouseHandler
 *  vip.astroline.client.layout.clickgui.config.ConfigUI
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.gui.clickgui.AbstractGuiScreen
 *  vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils
 *  vip.astroline.client.storage.utils.gui.clickgui.BlurUtil
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.layout.clickgui;

import java.awt.Color;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import optifine.Config;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.clickgui.ClickGUI;
import vip.astroline.client.layout.clickgui.config.ConfigUI;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.gui.clickgui.AbstractGuiScreen;
import vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils;
import vip.astroline.client.storage.utils.gui.clickgui.BlurUtil;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class ClickGUI
extends AbstractGuiScreen {
    public static int config_X = -1;
    public static int config_Y = -1;
    public static float config_W = -1.0f;
    public static float config_H = -1.0f;
    public int x = -1;
    public int y = -1;
    public int x2;
    public int y2;
    public boolean drag = false;
    public boolean drag_resize = false;
    public boolean hoveredResizeArea = false;
    private final MouseHandler handler = new MouseHandler(0);
    public float windowWidth = -1.0f;
    public float windowHeight = -1.0f;
    public float categorySelectorY = -1.0f;
    public float modeButtonAnimation = 0.0f;
    public Module lastModule = null;
    public CategoryButton currentCatButton = null;
    public Module currentModule = null;
    public ArrayList<CategoryButton> categoryButtons = new ArrayList();
    public ArrayList<CategoryModuleList> categoryModuleLists = new ArrayList();
    public ConfigUI cfgui;
    public NumberFormat nf = new DecimalFormat("0000");

    public ClickGUI() {
        Category[] categoryArray = Category.values();
        int n = categoryArray.length;
        int n2 = 0;
        while (true) {
            if (n2 >= n) {
                this.cfgui = new ConfigUI(this);
                return;
            }
            Category c = categoryArray[n2];
            CategoryButton button = new CategoryButton(this, c);
            this.categoryButtons.add(button);
            CategoryModuleList list = new CategoryModuleList(this, c);
            this.categoryModuleLists.add(list);
            ++n2;
        }
    }

    public void initGui() {
        ScaledResolution res = new ScaledResolution(this.mc);
        if (this.windowWidth < 270.0f) {
            if (config_W < 270.0f) {
                config_W = 395.0f;
            }
            this.windowWidth = config_W;
        }
        if (this.windowHeight < 250.0f) {
            if (config_H < 250.0f) {
                config_H = 280.0f;
            }
            this.windowHeight = config_H;
        }
        if (this.x < 0) {
            if (config_X < 0) {
                config_X = (int)(((float)res.getScaledWidth() - this.windowWidth) / 2.0f);
            }
            this.x = config_X;
        }
        if (this.y < 0) {
            if (config_Y < 0) {
                config_Y = (int)(((float)res.getScaledHeight() - this.windowHeight) / 2.0f);
            }
            this.y = config_Y;
        }
        try {
            int min = Cursor.getMinCursorSize();
            IntBuffer tmp = BufferUtils.createIntBuffer((int)(min * min));
            Cursor emptyCursor = new Cursor(min, min, min / 2, min / 2, 1, tmp, null);
            Mouse.setNativeCursor((Cursor)emptyCursor);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        this.cfgui.init();
    }

    public void drawScr(int mouseX, int mouseY, float partialTicks) {
        this.scale = 2.0f;
        if (!Config.isFastRender()) {
            BlurUtil.blurAll((float)1.0f);
        }
        this.cfgui.render((float)mouseX, (float)mouseY, partialTicks, (AbstractGuiScreen)this);
        RenderUtil.drawRoundedRect((float)this.x, (float)this.y, (float)((float)this.x + this.windowWidth), (float)((float)this.y + this.windowHeight), (int)(Hud.isLightMode.getValue() != false ? -921103 : -13486789));
        this.drawRoundedRect(this.x, this.y, this.x + 100, (float)this.y + this.windowHeight, Hud.isLightMode.getValue() != false ? -1 : -13684426);
        FontManager.vision30.drawString(Astroline.INSTANCE.getCLIENT().toUpperCase(), (float)this.x + 10.0f, (float)this.y + 10.0f, Hud.isLightMode.getValue() != false ? Hud.hudColor1.getColorInt() : new Color(255, 255, 255).getRGB());
        FontManager.baloo16.drawString("b" + Astroline.INSTANCE.getVERSION(), (float)this.x + 45.0f, (float)this.y + 20.5f, Hud.isLightMode.getValue() != false ? -3881788 : new Color(220, 221, 222).getRGB());
        this.drawGradientSideways(this.x + 100, (float)this.y - 0.5f, this.x + 110, (float)this.y + this.windowHeight + 0.5f, RenderUtil.reAlpha((int)ColorUtils.BLACK.c, (float)0.15f), RenderUtil.reAlpha((int)ColorUtils.BLACK.c, (float)0.0f));
        if (this.currentCatButton == null) {
            this.currentCatButton = this.categoryButtons.get(0);
        }
        this.categorySelectorY = this.drag || this.categorySelectorY <= -1.0f ? this.currentCatButton.y : AnimationUtils.getAnimationState((float)this.categorySelectorY, (float)this.currentCatButton.y, (float)((float)((double)Math.max(10.0f, Math.abs(this.categorySelectorY - this.currentCatButton.y) * 35.0f) * 0.3)));
        RenderUtil.drawRect((float)((float)this.x - 0.5f), (float)this.categorySelectorY, (float)((float)this.x + 100.0f), (float)(this.categorySelectorY + 25.0f), (int)Hud.hudColor1.getColorInt());
        float startY = this.y + 35;
        for (CategoryButton categoryButton : this.categoryButtons) {
            categoryButton.drawBackground((float)this.x, startY);
            startY += 25.0f;
        }
        float startY2 = this.y + 35;
        GL11.glPushMatrix();
        GL11.glEnable((int)3089);
        this.doGlScissor(this.x, (int)this.categorySelectorY, 99.0f, 25.0f);
        for (CategoryButton catButton : this.categoryButtons) {
            catButton.draw((float)this.x, startY2);
            startY2 += 25.0f;
        }
        GL11.glDisable((int)3089);
        GL11.glPopMatrix();
        int n = Hud.isLightMode.getValue() != false ? -1446931 : -14079185;
        float bottom = (float)this.y + this.windowHeight;
        GL11.glPushMatrix();
        GL11.glEnable((int)3089);
        this.doGlScissor(this.x, (int)(bottom - 25.0f), 100.0f, 25.0f);
        GL11.glDisable((int)3089);
        GL11.glPopMatrix();
        this.cfgui.drawGradientSideways((float)(this.x + 60), bottom - 25.0f, (float)(this.x + 100), bottom, RenderUtil.reAlpha((int)n, (float)0.0f), RenderUtil.reAlpha((int)n, (float)1.0f));
        GL11.glPushMatrix();
        GL11.glEnable((int)3089);
        this.doGlScissor(this.x - 1, this.y, this.windowWidth, this.windowHeight);
        for (CategoryModuleList list : this.categoryModuleLists) {
            if (list.cat != this.currentCatButton.cat) continue;
            list.draw((float)(this.x + 100), (float)this.y, (float)mouseX, (float)mouseY);
        }
        GL11.glDisable((int)3089);
        GL11.glPopMatrix();
        this.move(mouseX, mouseY);
        float xx = (float)mouseX - 0.5f;
        float yy = (float)mouseY - 0.5f;
        float shadowPosX = this.hoveredResizeArea ? xx - 1.8f - 1.0f : xx + 0.8f - 1.0f;
        float shadowPosY = this.hoveredResizeArea ? yy - 4.5f : yy + 0.5f;
        float cursorPosX = this.hoveredResizeArea ? xx - 4.0f : xx - 1.0f;
        float cursurPosY = this.hoveredResizeArea ? yy - 5.0f : yy;
        FontManager.icon18.drawString(this.hoveredResizeArea ? "o" : "p", shadowPosX, shadowPosY, RenderUtil.reAlpha((int)-16777216, (float)0.45f));
        FontManager.icon18.drawString(this.hoveredResizeArea ? "o" : "p", cursorPosX, cursurPosY, Hud.hudColor1.getColorInt());
    }

    private void move(int mouseX, int mouseY) {
        if (!Mouse.isButtonDown((int)0) && (this.drag || this.drag_resize)) {
            this.drag = false;
            this.drag_resize = false;
        }
        FontManager.icon10.drawString("l", (float)this.x + this.windowWidth - 6.0f, (float)this.y + this.windowHeight - 6.0f, ColorUtils.GREY.c);
        if (this.isHovering(mouseX, mouseY, (float)this.x + this.windowWidth - 6.0f, (float)this.y + this.windowHeight - 6.0f, (float)this.x + this.windowWidth, (float)this.y + this.windowHeight)) {
            this.hoveredResizeArea = true;
            if (this.handler.canExcecute()) {
                this.drag_resize = true;
                this.x2 = mouseX;
                this.y2 = mouseY;
            }
        } else {
            this.hoveredResizeArea = false;
        }
        if (this.drag_resize) {
            this.hoveredResizeArea = true;
            this.windowWidth += (float)(mouseX - this.x2);
            if (this.windowWidth < 270.0f) {
                this.windowWidth = 270.0f;
            } else {
                this.x2 = mouseX;
            }
            this.windowHeight += (float)(mouseY - this.y2);
            if (this.windowHeight < 285.0f) {
                this.windowHeight = 285.0f;
            } else {
                this.y2 = mouseY;
            }
        }
        if (this.isHovering(mouseX, mouseY, this.x, this.y, this.x + 99, this.y + 34) && this.handler.canExcecute()) {
            this.drag = true;
            this.x2 = mouseX - this.x;
            this.y2 = mouseY - this.y;
        }
        if (!this.drag) return;
        this.x = mouseX - this.x2;
        this.y = mouseY - this.y2;
    }

    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
        Iterator<CategoryButton> iterator = this.categoryButtons.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                this.cfgui.onMouseClicked(mouseX, mouseY, mouseButton, (AbstractGuiScreen)this);
                return;
            }
            CategoryButton c = iterator.next();
            c.onClick(mouseX, mouseY);
        }
    }

    public void updateScreen() {
        super.updateScreen();
    }

    public void onGuiClosed() {
        try {
            Mouse.setNativeCursor(null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        this.cfgui.onClose();
        super.onGuiClosed();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawRoundedRect(float left, float top, float right, float bottom, int color) {
        RenderUtil.drawRect((float)(left - 0.5f), (float)(top + 0.5f), (float)left, (float)(bottom - 0.5f), (int)color);
        RenderUtil.drawRect((float)(left + 0.5f), (float)(top + 0.5f), (float)(right - 0.5f), (float)top, (int)color);
        RenderUtil.drawRect((float)(left + 0.5f), (float)bottom, (float)(right - 0.5f), (float)(bottom + 0.5f), (int)color);
        RenderUtil.drawRect((float)left, (float)top, (float)right, (float)bottom, (int)color);
    }

    public boolean isHovering(float mouseX, float mouseY, float xLeft, float yUp, float xRight, float yBottom) {
        return mouseX > (float)this.x && mouseX < (float)this.x + this.windowWidth && mouseY > (float)this.y && mouseY < (float)this.y + this.windowHeight && mouseX > xLeft && mouseX < xRight && mouseY > yUp && mouseY < yBottom;
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
        worldrenderer.pos((double)right, (double)top, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double)left, (double)top, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)bottom, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)right, (double)bottom, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static int darker(int color, float factor) {
        int r = (int)((float)(color >> 16 & 0xFF) * factor);
        int g = (int)((float)(color >> 8 & 0xFF) * factor);
        int b = (int)((float)(color & 0xFF) * factor);
        int a = color >> 24 & 0xFF;
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF | (a & 0xFF) << 24;
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
}
