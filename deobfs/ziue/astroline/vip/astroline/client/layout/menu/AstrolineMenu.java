/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiLanguage
 *  net.minecraft.client.gui.GuiMultiplayer
 *  net.minecraft.client.gui.GuiOptions
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.GuiSelectWorld
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.WorldRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.util.ResourceLocation
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.altMgr.GuiAltMgr
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.gui.buttons.AstroButton
 *  vip.astroline.client.storage.utils.other.DiscordThread
 */
package vip.astroline.client.layout.menu;

import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.altMgr.GuiAltMgr;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.gui.buttons.AstroButton;
import vip.astroline.client.storage.utils.other.DiscordThread;

public class AstrolineMenu
extends GuiScreen {
    public static int BUTTON_COUNT = 5;
    public float totalHeight = BUTTON_COUNT * 20 + 20 + BUTTON_COUNT * 3;
    public float halfTotalHeight = this.totalHeight / 2.0f;
    public float fraction = 0.0f;
    float hHeight = 540.0f;
    float hWidth = 960.0f;
    private final Color purpleish = new Color(230, 0, 255);
    private final Color purple = new Color(10551551);
    AstroButton singleplayerworlds = new AstroButton(1, (int)this.hWidth - 70, (int)this.hHeight - (int)this.halfTotalHeight + 10, 140, 20, "Singleplayer");
    AstroButton multiplayer = new AstroButton(2, (int)this.hWidth - 70, (int)this.hHeight - (int)this.halfTotalHeight + 33, 140, 20, "Multiplayer");
    AstroButton alts = new AstroButton(1337, (int)this.hWidth - 70, (int)this.hHeight - (int)this.halfTotalHeight + 56, 140, 20, "Alts");
    AstroButton options = new AstroButton(0, (int)this.hWidth - 70, (int)this.hHeight - (int)this.halfTotalHeight + 79, 140, 20, "Options");
    AstroButton shutdown = new AstroButton(4, (int)this.hWidth - 70, (int)this.hHeight - (int)this.halfTotalHeight + 102, 140, 20, "Shutdown");

    public AstrolineMenu() {
        DiscordThread.update((String)"User: ziue");
    }

    public void initGui() {
        this.buttonList.add(this.singleplayerworlds);
        this.buttonList.add(this.multiplayer);
        this.buttonList.add(this.alts);
        this.buttonList.add(this.options);
        this.buttonList.add(this.shutdown);
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        this.hWidth = scaledResolution.getScaledWidth() / 2;
        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.singleplayerworlds.setColor(this.interpolateColor(this.purple, this.purpleish, this.fraction));
        this.multiplayer.setColor(this.interpolateColor(this.purple, this.purpleish, this.fraction));
        this.alts.setColor(this.interpolateColor(this.purple, this.purpleish, this.fraction));
        this.options.setColor(this.interpolateColor(this.purple, this.purpleish, this.fraction));
        this.shutdown.setColor(this.interpolateColor(this.purple, this.purpleish, this.fraction));
        this.singleplayerworlds.setColor(this.interpolateColor(this.singleplayerworlds.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), this.singleplayerworlds.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), this.fraction));
        this.multiplayer.setColor(this.interpolateColor(this.multiplayer.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), this.multiplayer.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), this.fraction));
        this.alts.setColor(this.interpolateColor(this.alts.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), this.alts.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), this.fraction));
        this.options.setColor(this.interpolateColor(this.options.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), this.options.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), this.fraction));
        this.shutdown.setColor(this.interpolateColor(this.shutdown.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), this.shutdown.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), this.fraction));
        this.singleplayerworlds.updateCoordinates(this.hWidth - 70.0f, this.hHeight - this.halfTotalHeight + 10.0f);
        this.multiplayer.updateCoordinates(this.hWidth - 70.0f, this.hHeight - this.halfTotalHeight + 33.0f);
        this.alts.updateCoordinates(this.hWidth - 70.0f, this.hHeight - this.halfTotalHeight + 56.0f);
        this.options.updateCoordinates(this.hWidth - 70.0f, this.hHeight - this.halfTotalHeight + 79.0f);
        this.shutdown.updateCoordinates(this.hWidth - 70.0f, this.hHeight - this.halfTotalHeight + 102.0f);
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int scaledWidthScaled = scaledResolution.getScaledWidth();
        int scaledHeightScaled = scaledResolution.getScaledHeight();
        this.hHeight += ((float)(scaledHeightScaled / 2) - this.hHeight) * 0.32f;
        this.hWidth = scaledWidthScaled / 2;
        AstrolineMenu.drawImg(new ResourceLocation("astroline/images/bg.jpg"), 0.0, 0.0, this.width, this.height);
        FontManager.vision30.drawString(Astroline.INSTANCE.getCLIENT().toUpperCase(), this.hWidth - FontManager.vision30.getStringWidth(Astroline.INSTANCE.getCLIENT().toUpperCase()) / 2.0f, (float)((int)(this.hHeight - 90.0f)), Hud.isLightMode.getValue() != false ? -11204908 : new Color(255, 255, 255).getRGB());
        this.singleplayerworlds.drawButton(this.mc, mouseX, mouseY);
        this.multiplayer.drawButton(this.mc, mouseX, mouseY);
        this.alts.drawButton(this.mc, mouseX, mouseY);
        this.options.drawButton(this.mc, mouseX, mouseY);
        this.shutdown.drawButton(this.mc, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1337: {
                this.mc.displayGuiScreen((GuiScreen)new GuiAltMgr((GuiScreen)this));
                break;
            }
            case 0: {
                this.mc.displayGuiScreen((GuiScreen)new GuiOptions((GuiScreen)this, this.mc.gameSettings));
                break;
            }
            case 5: {
                this.mc.displayGuiScreen((GuiScreen)new GuiLanguage((GuiScreen)this, this.mc.gameSettings, this.mc.getLanguageManager()));
                break;
            }
            case 1: {
                this.mc.displayGuiScreen((GuiScreen)new GuiSelectWorld((GuiScreen)this));
                break;
            }
            case 2: {
                this.mc.displayGuiScreen((GuiScreen)new GuiMultiplayer((GuiScreen)this));
                break;
            }
            case 4: {
                this.mc.shutdown();
                break;
            }
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private int interpolateColor(Color color1, Color color2, float fraction) {
        int red = (int)((float)color1.getRed() + (float)(color2.getRed() - color1.getRed()) * fraction);
        int green = (int)((float)color1.getGreen() + (float)(color2.getGreen() - color1.getGreen()) * fraction);
        int blue = (int)((float)color1.getBlue() + (float)(color2.getBlue() - color1.getBlue()) * fraction);
        int alpha = (int)((float)color1.getAlpha() + (float)(color2.getAlpha() - color1.getAlpha()) * fraction);
        try {
            return new Color(red, green, blue, alpha).getRGB();
        }
        catch (Exception ex) {
            return -1;
        }
    }

    public static void drawImg(ResourceLocation loc, double posX, double posY, double width, double height) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc((int)770, (int)771);
        Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
        float f = 1.0f / (float)width;
        float f1 = 1.0f / (float)height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(posX, posY + height, 0.0).tex((double)(0.0f * f), (double)((0.0f + (float)height) * f1)).endVertex();
        worldrenderer.pos(posX + width, posY + height, 0.0).tex((double)((0.0f + (float)width) * f), (double)((0.0f + (float)height) * f1)).endVertex();
        worldrenderer.pos(posX + width, posY, 0.0).tex((double)((0.0f + (float)width) * f), (double)(0.0f * f1)).endVertex();
        worldrenderer.pos(posX, posY, 0.0).tex((double)(0.0f * f), (double)(0.0f * f1)).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }
}
