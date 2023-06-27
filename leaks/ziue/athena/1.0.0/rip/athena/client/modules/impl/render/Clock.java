package rip.athena.client.modules.impl.render;

import java.awt.*;
import rip.athena.client.config.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.modules.*;
import java.time.format.*;
import java.time.*;
import java.time.temporal.*;
import org.lwjgl.opengl.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.*;
import net.minecraft.client.gui.*;
import rip.athena.client.utils.render.*;

public class Clock extends Module
{
    @ConfigValue.Color(name = "Color")
    private Color color;
    @ConfigValue.List(name = "Clock Format", values = { "yyyy/MM/dd HH:mm:ss", "MM/dd/yyyy", "dd/MM/yyyy", "dd/MM/yyyy hh:mm a", "MM/dd/yyyy hh:mm a", "E, MMM dd yyyy", "hh:mm a", "hh:mm:ss a", "yyyy-MM-dd" })
    private String format;
    @ConfigValue.List(name = "Display Mode", values = { "Circle", "Modern", "Fade", "Old" }, description = "Chose display of background")
    private String backgroundMode;
    @ConfigValue.Boolean(name = "Background")
    private boolean backGround;
    @ConfigValue.Color(name = "Background Color")
    private Color background;
    @ConfigValue.Integer(name = "Transparency", min = 0, max = 255)
    private int transparency;
    @ConfigValue.Boolean(name = "Custom Font")
    private boolean customFont;
    @ConfigValue.Boolean(name = "Static Chroma")
    private boolean isUsingStaticChroma;
    @ConfigValue.Boolean(name = "Wave Chroma")
    private boolean isUsingWaveChroma;
    private boolean isClock;
    private int radius;
    private HUDElement hud;
    private int width;
    private int height;
    
    public Clock() {
        super("Clock", Category.RENDER, "Athena/gui/mods/time.png");
        this.color = Color.WHITE;
        this.format = "yyyy/MM/dd HH:mm:ss";
        this.backgroundMode = "Circle";
        this.backGround = true;
        this.background = new Color(0, 0, 0, 150);
        this.transparency = 255;
        this.customFont = false;
        this.isUsingStaticChroma = false;
        this.isUsingWaveChroma = false;
        this.isClock = false;
        this.radius = 12;
        this.width = 56;
        this.height = 18;
        (this.hud = new HUDElement("time", this.width, this.height) {
            @Override
            public void onRender() {
                Clock.this.render();
            }
        }).setX(1);
        this.hud.setY(190);
        this.addHUD(this.hud);
    }
    
    public void render() {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(this.format);
        final LocalDateTime now = LocalDateTime.now();
        final String string = dtf.format(now);
        if (!this.isClock) {
            if (Clock.mc.gameSettings.showDebugInfo) {
                return;
            }
            GL11.glPushMatrix();
            int stringWidth = 0;
            if (this.customFont) {
                stringWidth = FontManager.getProductSansRegular(30).width(string);
            }
            else {
                stringWidth = Clock.mc.fontRendererObj.getStringWidth(string);
            }
            final int width = this.hud.getWidth();
            final int height = this.hud.getHeight();
            if (this.backGround) {
                if (this.backgroundMode.equalsIgnoreCase("Modern")) {
                    if (Athena.INSTANCE.getThemeManager().getTheme().isTriColor()) {
                        RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getThirdColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor());
                    }
                    else {
                        RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor());
                    }
                }
                else if (this.backgroundMode.equalsIgnoreCase("Circle")) {
                    RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, ColorUtil.getClientColor(0, this.transparency), ColorUtil.getClientColor(90, this.transparency), ColorUtil.getClientColor(180, this.transparency), ColorUtil.getClientColor(270, this.transparency));
                }
                else if (this.backgroundMode.equalsIgnoreCase("Fade")) {
                    RoundedUtils.drawRoundedRect((float)this.hud.getX(), (float)this.hud.getY(), (float)(this.hud.getX() + width), (float)(this.hud.getY() + height), 8.0f, Athena.INSTANCE.getThemeManager().getTheme().getAccentColor().getRGB());
                }
                else {
                    Gui.drawRect(this.hud.getX(), this.hud.getY(), this.hud.getX() + width, this.hud.getY() + height, this.background.getRGB());
                }
            }
            final float posY = (float)(this.hud.getY() + 2);
            final float posX = (float)(this.hud.getX() + 9);
            if (this.customFont) {
                this.hud.setWidth(FontManager.getProductSansRegular(25).width(string) + 16);
                this.hud.setHeight((int)FontManager.getProductSansRegular(25).height());
                if (this.isUsingStaticChroma) {
                    DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), string, (int)posX, (int)posY, true, true);
                }
                else if (this.isUsingWaveChroma) {
                    DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), string, (int)posX, (int)posY, false, true);
                }
                else {
                    FontManager.getProductSansRegular(25).drawString(string, (int)posX, (int)posY, this.color.getRGB());
                }
            }
            else {
                this.hud.setWidth(Clock.mc.fontRendererObj.getStringWidth(string) + 16);
                this.hud.setHeight(Clock.mc.fontRendererObj.FONT_HEIGHT + 9);
                if (this.isUsingStaticChroma) {
                    DrawUtils.drawChromaString(string, posX, posY + 3.0f, true, true);
                }
                else if (this.isUsingWaveChroma) {
                    DrawUtils.drawChromaString(string, posX, posY + 3.0f, false, true);
                }
                else {
                    Clock.mc.fontRendererObj.drawStringWithShadow(string, posX, posY + 3.0f, this.color.getRGB());
                }
            }
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        else {
            DrawUtils.drawCircle((float)this.hud.getX(), (float)this.hud.getY(), this.radius, new Color(39, 55, 77).getRGB(), (float)(this.radius * 2), true);
            DrawUtils.drawCircle((float)(this.hud.getX() + 0.1), (float)(this.hud.getY() + 0.1), this.radius - 5, new Color(82, 109, 130).getRGB(), (float)(this.radius - 10), true);
            final double conv = 4.39822971502571;
            GL11.glBegin(1);
            GL11.glVertex2d(0.0, 0.0);
            GL11.glVertex2d(-this.hud.getX() + -this.radius * Math.sin(conv), -this.hud.getY() + this.radius * Math.cos(conv));
            GL11.glEnd();
        }
    }
}
