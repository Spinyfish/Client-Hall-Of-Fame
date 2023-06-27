package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import java.awt.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.utils.*;
import rip.athena.client.modules.*;
import org.lwjgl.opengl.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.events.types.input.*;
import rip.athena.client.events.*;

public class Keystrokes extends Module
{
    @ConfigValue.Boolean(name = "Show CPS")
    private boolean keystrokescps;
    @ConfigValue.Boolean(name = "Show Mouse Buttons")
    private boolean keystrokesmouse;
    @ConfigValue.Boolean(name = "Show Spacebar")
    private boolean spacebar;
    @ConfigValue.Boolean(name = "Custom Font")
    private boolean isCustomFont;
    @ConfigValue.Color(name = "Background Color")
    private Color backgroundColor;
    @ConfigValue.Color(name = "Key Unpressed Color")
    private Color color;
    @ConfigValue.Color(name = "Key Pressed Color")
    private Color color2;
    @ConfigValue.Boolean(name = "Static Chroma")
    private boolean isUsingStaticChroma;
    @ConfigValue.Boolean(name = "Wave Chroma")
    private boolean isUsingWaveChroma;
    private HUDElement hud;
    private int width;
    private int height;
    private final ClickCounter leftClickCounter;
    private final ClickCounter rightClickCounter;
    
    public Keystrokes() {
        super("Key Strokes", Category.RENDER, "Athena/gui/mods/keystrokes.png");
        this.keystrokescps = true;
        this.keystrokesmouse = true;
        this.spacebar = true;
        this.isCustomFont = false;
        this.backgroundColor = new Color(0, 0, 0, 150);
        this.color = Color.WHITE;
        this.color2 = Color.BLACK;
        this.isUsingStaticChroma = false;
        this.isUsingWaveChroma = false;
        this.width = 78;
        this.height = 52;
        this.leftClickCounter = new ClickCounter();
        this.rightClickCounter = new ClickCounter();
        (this.hud = new HUDElement("keystrokes", this.width, this.height) {
            @Override
            public void onRender() {
                Keystrokes.this.render();
            }
        }).setX(0);
        this.hud.setY(120);
        this.addHUD(this.hud);
    }
    
    public void render() {
        if (Keystrokes.mc.gameSettings.showDebugInfo) {
            return;
        }
        GL11.glPushMatrix();
        final int posX = this.hud.getX();
        final int posY = this.hud.getY();
        int shiftY = 0;
        int shiftX = 26;
        int shiftXFont = 0;
        if (this.isCustomFont) {
            shiftXFont += 2;
        }
        if (Keystrokes.mc.gameSettings.keyBindForward.isKeyDown()) {
            DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 25 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(true), this.getBackGroundColor(true));
            GL11.glPushMatrix();
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            this.drawString("W", (posX + shiftX + 7) / 1.005f, (posY + shiftY + 7) / 1.005f, this.color2);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        else {
            DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 25 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(false), this.getBackGroundColor(false));
            GL11.glPushMatrix();
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            this.drawString("W", (posX + shiftX + 7) / 1.005f, (posY + shiftY + 7) / 1.005f, this.color);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        shiftX = 0;
        shiftY += 26;
        if (Keystrokes.mc.gameSettings.keyBindLeft.isKeyDown()) {
            DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 25 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(true), this.getBackGroundColor(true));
            GL11.glPushMatrix();
            GL11.glScalef(1.005f, 1.0f, 1.0f);
            this.drawString("A", (posX + shiftX + shiftXFont + 7) / 1.005f, (posY + shiftY + 7) / 1.005f, this.color2);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        else {
            DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 25 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(false), this.getBackGroundColor(false));
            GL11.glPushMatrix();
            GL11.glScalef(1.005f, 1.0f, 1.0f);
            this.drawString("A", (posX + shiftX + shiftXFont + 7) / 1.005f, (posY + shiftY + 7) / 1.005f, this.color);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        shiftX += 26;
        if (Keystrokes.mc.gameSettings.keyBindBack.isKeyDown()) {
            DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 25 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(true), this.getBackGroundColor(true));
            GL11.glPushMatrix();
            GL11.glScalef(1.005f, 1.0f, 1.0f);
            this.drawString("S", (posX + shiftX + shiftXFont + 7) / 1.005f, (posY + shiftY + 7) / 1.005f, this.color2);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        else {
            DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 25 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(false), this.getBackGroundColor(false));
            GL11.glPushMatrix();
            GL11.glScalef(1.005f, 1.0f, 1.0f);
            this.drawString("S", (posX + shiftX + shiftXFont + 7) / 1.005f, (posY + shiftY + 7) / 1.005f, this.color);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        shiftX += 26;
        if (Keystrokes.mc.gameSettings.keyBindRight.isKeyDown()) {
            DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 25 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(true), this.getBackGroundColor(true));
            GL11.glPushMatrix();
            GL11.glScalef(1.005f, 1.0f, 1.0f);
            this.drawString("D", (posX + shiftX + shiftXFont + 7) / 1.005f, (posY + shiftY + 7) / 1.005f, this.color2);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        else {
            DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 25 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(false), this.getBackGroundColor(false));
            GL11.glPushMatrix();
            GL11.glScalef(1.005f, 1.0f, 1.0f);
            this.drawString("D", (posX + shiftX + shiftXFont + 7) / 1.005f, (posY + shiftY + 7) / 1.005f, this.color);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        shiftY += 26;
        int mShift = 0;
        int shift = 3;
        if (this.isCustomFont) {
            shift = 6;
            mShift = 1;
        }
        if (this.keystrokesmouse) {
            shiftX = 0;
            if (this.keystrokescps) {
                if (Keystrokes.mc.gameSettings.keyBindAttack.isKeyDown()) {
                    DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 38 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(true), this.getBackGroundColor(true));
                    GL11.glPushMatrix();
                    if (this.isCustomFont) {
                        this.drawString("LMB", (float)(posX + mShift + shiftX + 17 - this.getStringWidth("LMB") / 2), (float)(posY + shiftY + 3), this.color2);
                    }
                    else {
                        this.drawString("LMB", (float)(posX + mShift + shiftX + 19 - this.getStringWidth("LMB") / 2), (float)(posY + shiftY + 3), this.color2);
                    }
                    GL11.glScalef(0.75f, 0.75f, 0.75f);
                    if (this.isCustomFont) {
                        this.drawString(this.leftClickCounter.getCps() + " CPS", (posX + shift + shiftX + 17 - this.getStringWidth(this.leftClickCounter.getCps() + " CPS") / 2) / 0.75f, (posY + shiftY + 15) / 0.75f, this.color2);
                    }
                    else {
                        this.drawString(this.leftClickCounter.getCps() + " CPS", (posX + shift + shiftX + 19 - this.getStringWidth(this.leftClickCounter.getCps() + " CPS") / 2) / 0.75f, (posY + shiftY + 15) / 0.75f, this.color2);
                    }
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
                else {
                    DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 38 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(false), this.getBackGroundColor(false));
                    GL11.glPushMatrix();
                    if (this.isCustomFont) {
                        this.drawString("LMB", (float)(posX + mShift + shiftX + 17 - this.getStringWidth("LMB") / 2), (float)(posY + shiftY + 3), this.color);
                    }
                    else {
                        this.drawString("LMB", (float)(posX + mShift + shiftX + 19 - this.getStringWidth("LMB") / 2), (float)(posY + shiftY + 3), this.color);
                    }
                    GL11.glScalef(0.75f, 0.75f, 0.75f);
                    if (this.isCustomFont) {
                        this.drawString(this.leftClickCounter.getCps() + " CPS", (posX + shift + shiftX + 17 - this.getStringWidth(this.leftClickCounter.getCps() + " CPS") / 2) / 0.75f, (posY + shiftY + 15) / 0.75f, this.color);
                    }
                    else {
                        this.drawString(this.leftClickCounter.getCps() + " CPS", (posX + shift + shiftX + 19 - this.getStringWidth(this.leftClickCounter.getCps() + " CPS") / 2) / 0.75f, (posY + shiftY + 15) / 0.75f, this.color);
                    }
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
                shiftX += 39;
                if (Keystrokes.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 38 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(true), this.getBackGroundColor(true));
                    GL11.glPushMatrix();
                    if (this.isCustomFont) {
                        this.drawString("RMB", (float)(posX + mShift + shiftX + 17 - this.getStringWidth("RMB") / 2), (float)(posY + shiftY + 3), this.color2);
                    }
                    else {
                        this.drawString("RMB", (float)(posX + mShift + shiftX + 19 - this.getStringWidth("RMB") / 2), (float)(posY + shiftY + 3), this.color2);
                    }
                    GL11.glScalef(0.75f, 0.75f, 0.75f);
                    if (this.isCustomFont) {
                        this.drawString(this.rightClickCounter.getCps() + " CPS", (posX + shift + shiftX + 17 - this.getStringWidth(this.rightClickCounter.getCps() + " CPS") / 2) / 0.75f, (posY + shiftY + 15) / 0.75f, this.color2);
                    }
                    else {
                        this.drawString(this.rightClickCounter.getCps() + " CPS", (posX + shift + shiftX + 19 - this.getStringWidth(this.rightClickCounter.getCps() + " CPS") / 2) / 0.75f, (posY + shiftY + 15) / 0.75f, this.color2);
                    }
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
                else {
                    GL11.glPushMatrix();
                    DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 38 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(false), this.getBackGroundColor(false));
                    if (this.isCustomFont) {
                        this.drawString("RMB", (float)(posX + mShift + shiftX + 17 - this.getStringWidth("RMB") / 2), (float)(posY + shiftY + 3), this.color);
                    }
                    else {
                        this.drawString("RMB", (float)(posX + mShift + shiftX + 19 - this.getStringWidth("RMB") / 2), (float)(posY + shiftY + 3), this.color);
                    }
                    GL11.glScalef(0.75f, 0.75f, 0.75f);
                    if (this.isCustomFont) {
                        this.drawString(this.rightClickCounter.getCps() + " CPS", (posX + shift + shiftX + 17 - this.getStringWidth(this.rightClickCounter.getCps() + " CPS") / 2) / 0.75f, (posY + shiftY + 15) / 0.75f, this.color);
                    }
                    else {
                        this.drawString(this.rightClickCounter.getCps() + " CPS", (posX + shift + shiftX + 19 - this.getStringWidth(this.rightClickCounter.getCps() + " CPS") / 2) / 0.75f, (posY + shiftY + 15) / 0.75f, this.color);
                    }
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
                shiftY += 26;
            }
            else {
                if (Keystrokes.mc.gameSettings.keyBindAttack.isKeyDown()) {
                    DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 38 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(true), this.getBackGroundColor(true));
                    GL11.glPushMatrix();
                    GL11.glScalef(1.005f, 1.0f, 1.0f);
                    this.drawString("LMB", (float)(posX + shiftX + 7), (float)(posY + shiftY + 7), this.color2);
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
                else {
                    DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 38 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(false), this.getBackGroundColor(false));
                    GL11.glPushMatrix();
                    GL11.glScalef(1.005f, 1.0f, 1.0f);
                    this.drawString("LMB", (float)(posX + shiftX + 7), (float)(posY + shiftY + 7), this.color);
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
                shiftX += 39;
                if (Keystrokes.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 38 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(true), this.getBackGroundColor(true));
                    GL11.glPushMatrix();
                    GL11.glScalef(1.005f, 1.0f, 1.0f);
                    this.drawString("RMB", (posX + shiftX + 7) / 1.005f, (posY + shiftY + 7) / 1.005f, this.color2);
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
                else {
                    DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 38 + shiftX, posY + 25 + shiftY, this.getBackGroundColor(false), this.getBackGroundColor(false));
                    GL11.glPushMatrix();
                    GL11.glScalef(1.005f, 1.0f, 1.0f);
                    this.drawString("RMB", (posX + shiftX + 7) / 1.005f, (posY + shiftY + 7) / 1.005f, this.color);
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
                shiftY += 26;
            }
        }
        if (this.spacebar) {
            shiftX = 0;
            if (Keystrokes.mc.gameSettings.keyBindJump.isKeyDown()) {
                DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 77 + shiftX, posY + 13 + shiftY, this.getBackGroundColor(true), this.getBackGroundColor(true));
                DrawUtils.drawGradientRect(posX + (float)shiftX + 23.0f, posY + (float)shiftY + 4.0f, posX + (float)shiftX + 55.0f, posY + (float)shiftY + 5.0f, this.color2.getRGB(), this.color2.getRGB());
            }
            else {
                DrawUtils.drawGradientRect(posX + shiftX, posY + shiftY, posX + 77 + shiftX, posY + 13 + shiftY, this.getBackGroundColor(false), this.getBackGroundColor(false));
                DrawUtils.drawGradientRect(posX + (float)shiftX + 23.0f, posY + (float)shiftY + 4.0f, posX + (float)shiftX + 55.0f, posY + (float)shiftY + 5.0f, this.color.getRGB(), this.color.getRGB());
            }
            shiftY += 14;
        }
        this.hud.setHeight(shiftY);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    public int getStringWidth(final String text) {
        if (this.isCustomFont) {
            return FontManager.getProductSansRegular(30).width(text);
        }
        return Keystrokes.mc.fontRendererObj.getStringWidth(text);
    }
    
    public int getStringHeight(final String text) {
        if (this.isCustomFont) {
            return FontManager.getProductSansRegular(30).width(text);
        }
        return Keystrokes.mc.fontRendererObj.getStringWidth(text);
    }
    
    public void drawString(final String text, final float posX, final float posY, final Color color) {
        if (this.isCustomFont) {
            if (this.isUsingStaticChroma) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), text, (int)posX + 1, (int)posY, true, true);
            }
            else if (this.isUsingWaveChroma) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), text, (int)posX + 1, (int)posY, false, true);
            }
            else {
                FontManager.getProductSansRegular(30).drawString(text, (int)posX + 1, (int)posY, color.getRGB());
            }
        }
        else if (this.isUsingStaticChroma) {
            DrawUtils.drawChromaString(text, (int)posX + 2, (int)posY + 2, true, true);
        }
        else if (this.isUsingWaveChroma) {
            DrawUtils.drawChromaString(text, (int)posX + 2, (int)posY + 2, false, true);
        }
        else {
            Keystrokes.mc.fontRendererObj.drawString(text, posX + 2.0f, posY + 2.0f, color.getRGB(), false);
        }
    }
    
    public int getBackGroundColor(final boolean pressed) {
        return this.backgroundColor.getRGB();
    }
    
    @SubscribeEvent
    public void onClick(final MouseDownEvent e) {
        if (e.getButton() == 0) {
            this.leftClickCounter.onClick();
        }
        if (e.getButton() == 1) {
            this.rightClickCounter.onClick();
        }
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
}
