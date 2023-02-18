/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.WorldRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.layout.altMgr.Alt
 *  vip.astroline.client.layout.altMgr.GuiAltMgr
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.layout.altMgr;

import java.awt.Color;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.layout.altMgr.Alt;
import vip.astroline.client.layout.altMgr.GuiAltMgr;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class AltScrollList {
    public float x;
    public float y;
    public float width;
    public float height;
    public int mouseX;
    public int mouseY;
    public Consumer<Alt> onSelected;
    public Consumer<Alt> onDoubleClicked;
    public int selectedAlt;
    public long lastClicked;
    public GuiAltMgr parent;
    public float scrollY = 0.0f;
    public float scrollAni = 0.0f;
    public float minY = -100.0f;

    public AltScrollList(GuiAltMgr parent, Consumer<Alt> onSelected, Consumer<Alt> onDoubleClicked) {
        this.parent = parent;
        this.onSelected = onSelected;
        this.onDoubleClicked = onDoubleClicked;
    }

    public void doDraw(float x, float y, float width, float height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        GuiRenderUtils.drawRoundedRect((float)x, (float)y, (float)width, (float)height, (float)2.0f, (int)new Color(0, 0, 0, 88).getRGB(), (float)0.5f, (int)-13684426);
        if (RenderUtil.isHoveringBound((float)mouseX, (float)mouseY, (float)x, (float)y, (float)width, (float)height)) {
            this.minY = height - 4.0f;
        }
        this.scrollAni = AnimationUtils.smoothAnimation((float)this.scrollAni, (float)this.scrollY, (float)50.0f, (float)0.3f);
        GL11.glEnable((int)3089);
        RenderUtil.doGlScissor((float)((int)x + 2), (float)((int)y + 4), (float)(width - 4.0f), (float)(height - 8.0f));
        float startY = y + 4.0f + this.scrollAni;
        float buttonHeight = 30.0f;
        float totalY = 0.0f;
        for (int i = 0; i < GuiAltMgr.alts.size(); startY += buttonHeight + 5.0f, totalY += buttonHeight + 5.0f, ++i) {
            Alt alt = (Alt)GuiAltMgr.alts.get(i);
            if (!(y < startY + buttonHeight + 5.0f) || !(startY < y + height)) continue;
            boolean highlight = this.selectedAlt == i;
            boolean drawHover = RenderUtil.isHoveringBound((float)mouseX, (float)mouseY, (float)(x + 4.0f), (float)startY, (float)(width - 8.0f), (float)buttonHeight);
            GuiRenderUtils.drawRoundedRect((float)(x + 4.0f), (float)startY, (float)(width - 8.0f), (float)buttonHeight, (float)2.0f, (int)(highlight ? Hud.hudColor1.getColorInt() : -13486789), (float)0.5f, (int)-13486789);
            this.drawAltFace(alt.getNameOrEmail(), x + 8.0f, startY + 3.0f, 24.0f, 24.0f);
            FontManager.sans18.drawLimitedString(alt.getNameOrEmail(), x + 34.0f, startY + 2.0f, -1, 375.0f);
            FontManager.sans18.drawLimitedString((alt.isCracked() ? "Cracked" : "Microsoft") + (alt.isStarred() ? ", Starred" : ""), x + 34.0f, startY + 14.0f, -1, 375.0f);
            if (!drawHover) continue;
            GuiRenderUtils.drawRoundedRect((float)(x + 4.0f), (float)startY, (float)(width - 8.0f), (float)buttonHeight, (float)2.0f, (int)0x33000000, (float)0.5f, (int)0x33000000);
        }
        GL11.glDisable((int)3089);
        if (RenderUtil.isHoveringBound((float)mouseX, (float)mouseY, (float)x, (float)y, (float)width, (float)height)) {
            this.minY -= totalY;
        }
        if (!(totalY > this.height - 8.0f)) return;
        float viewable = this.height;
        float progress = MathHelper.clamp_float((float)(-this.scrollAni / -this.minY), (float)0.0f, (float)1.0f);
        float ratio = viewable / totalY * viewable;
        float barHeight = Math.max(ratio, 20.0f);
        float position = progress * (viewable - barHeight);
        GuiRenderUtils.drawRoundedRect((float)(this.x + this.width + 4.0f), (float)this.y, (float)4.0f, (float)this.height, (float)2.0f, (int)-13749448, (float)0.5f, (int)-13749448);
        GuiRenderUtils.drawRoundedRect((float)(this.x + this.width + 4.0f), (float)(this.y + position), (float)4.0f, (float)barHeight, (float)2.0f, (int)-14671323, (float)0.5f, (int)-14671323);
    }

    public void handleMouseInput() {
        if (!RenderUtil.isHoveringBound((float)this.mouseX, (float)this.mouseY, (float)this.x, (float)this.y, (float)this.width, (float)this.height)) return;
        this.scrollY += (float)Mouse.getEventDWheel() / 5.0f;
        if (this.scrollY <= this.minY) {
            this.scrollY = this.minY;
        }
        if (!(this.scrollY >= 0.0f)) return;
        this.scrollY = 0.0f;
    }

    /*
     * Unable to fully structure code
     */
    public void onClick(int mouseX, int mouseY, int mouseButton) {
        startY = this.y + 4.0f + this.scrollAni;
        if (RenderUtil.isHoveringBound((float)mouseX, (float)mouseY, (float)this.x, (float)this.y, (float)this.width, (float)this.height) == false) return;
        buttonHeight = 30.0f;
        i = 0;
        while (i < GuiAltMgr.alts.size()) {
            block2: {
                alt = (Alt)GuiAltMgr.alts.get(i);
                isHovered = RenderUtil.isHoveringBound((float)mouseX, (float)mouseY, (float)(this.x + 4.0f), (float)startY, (float)(this.width - 8.0f), (float)buttonHeight);
                if (!isHovered) break block2;
                ** break block3
            }
            startY += buttonHeight + 5.0f;
            ++i;
        }
        return;
lbl-1000:
        // 1 sources

        {
            isDoubleClicked = i == this.selectedAlt && Minecraft.getSystemTime() - this.lastClicked < 250L;
        }
        this.onSelected.accept(alt);
        if (isDoubleClicked) {
            this.onDoubleClicked.accept(alt);
        }
        this.selectedAlt = i;
        this.lastClicked = Minecraft.getSystemTime();
    }

    public int getSelectedIndex() {
        return this.selectedAlt;
    }

    public void drawAltFace(String name, float x, float y, float w, float h) {
        try {
            AbstractClientPlayer.getDownloadImageSkin((ResourceLocation)AbstractClientPlayer.getLocationSkin((String)name), (String)name).loadTexture(Minecraft.getMinecraft().getResourceManager());
            Minecraft.getMinecraft().getTextureManager().bindTexture(AbstractClientPlayer.getLocationSkin((String)name));
            Tessellator var3 = Tessellator.getInstance();
            WorldRenderer var4 = var3.getWorldRenderer();
            GL11.glEnable((int)3042);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            double fw = 32.0;
            double fh = 32.0;
            double u = 32.0;
            double v = 32.0;
            var4.begin(7, DefaultVertexFormats.POSITION_TEX);
            var4.pos((double)x + 0.0, (double)y + (double)h, 0.0).tex((double)((float)(u + 0.0) * 0.00390625f), (double)((float)(v + fh) * 0.00390625f)).endVertex();
            var4.pos((double)x + (double)w, (double)y + (double)h, 0.0).tex((double)((float)(u + fw) * 0.00390625f), (double)((float)(v + fh) * 0.00390625f)).endVertex();
            var4.pos((double)x + (double)w, (double)y + 0.0, 0.0).tex((double)((float)(u + fw) * 0.00390625f), (double)((float)(v + 0.0) * 0.00390625f)).endVertex();
            var4.pos((double)x + 0.0, (double)y + 0.0, 0.0).tex((double)((float)(u + 0.0) * 0.00390625f), (double)((float)(v + 0.0) * 0.00390625f)).endVertex();
            var3.draw();
            fw = 32.0;
            fh = 32.0;
            u = 160.0;
            v = 32.0;
            var4.begin(7, DefaultVertexFormats.POSITION_TEX);
            var4.pos((double)x + 0.0, (double)y + (double)h, 0.0).tex((double)((float)(u + 0.0) * 0.00390625f), (double)((float)(v + fh) * 0.00390625f)).endVertex();
            var4.pos((double)x + (double)w, (double)y + (double)h, 0.0).tex((double)((float)(u + fw) * 0.00390625f), (double)((float)(v + fh) * 0.00390625f)).endVertex();
            var4.pos((double)x + (double)w, (double)y + 0.0, 0.0).tex((double)((float)(u + fw) * 0.00390625f), (double)((float)(v + 0.0) * 0.00390625f)).endVertex();
            var4.pos((double)x + 0.0, (double)y + 0.0, 0.0).tex((double)((float)(u + 0.0) * 0.00390625f), (double)((float)(v + 0.0) * 0.00390625f)).endVertex();
            var3.draw();
            GL11.glDisable((int)3042);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
