package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import java.awt.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.modules.*;
import org.lwjgl.opengl.*;
import rip.athena.client.*;
import net.minecraft.client.gui.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.utils.render.*;

public class MemoryUsage extends Module
{
    @ConfigValue.Boolean(name = "Percentage", description = "Show memory usage in percentage.")
    private boolean percentage;
    @ConfigValue.List(name = "Display Mode", values = { "Circle", "Modern", "Fade", "Old" }, description = "Chose display of background")
    private String backgroundMode;
    @ConfigValue.Boolean(name = "Background")
    private boolean backGround;
    @ConfigValue.Integer(name = "Transparency", min = 0, max = 255)
    private int transparency;
    @ConfigValue.Color(name = "Color")
    private Color color;
    @ConfigValue.Color(name = "Background Color")
    private Color background;
    @ConfigValue.Boolean(name = "Custom Font")
    private boolean customFont;
    @ConfigValue.Boolean(name = "Static Chroma")
    private boolean isUsingStaticChroma;
    @ConfigValue.Boolean(name = "Wave Chroma")
    private boolean isUsingWaveChroma;
    private HUDElement hud;
    private int width;
    private int height;
    
    public MemoryUsage() {
        super("Memory usage", Category.RENDER, "Athena/gui/mods/memory.png");
        this.percentage = false;
        this.backgroundMode = "Circle";
        this.backGround = true;
        this.transparency = 255;
        this.color = Color.WHITE;
        this.background = new Color(0, 0, 0, 150);
        this.customFont = false;
        this.isUsingStaticChroma = false;
        this.isUsingWaveChroma = false;
        this.width = 56;
        this.height = 18;
        (this.hud = new HUDElement("memory usage", this.width, this.height) {
            @Override
            public void onRender() {
                MemoryUsage.this.render();
            }
        }).setX(1);
        this.hud.setY(175);
        this.addHUD(this.hud);
    }
    
    public void render() {
        if (MemoryUsage.mc.gameSettings.showDebugInfo) {
            return;
        }
        GL11.glPushMatrix();
        final long i = Runtime.getRuntime().maxMemory();
        final long j = Runtime.getRuntime().totalMemory();
        final long k = Runtime.getRuntime().freeMemory();
        final long l = j - k;
        final long usedMemory = bytesToMb(l);
        final long unusedMemory = bytesToMb(i);
        final long percentage = l * 100L / i;
        String string = String.format("Memory: %2d%% %03d/%03dMB", percentage, usedMemory, unusedMemory);
        if (this.percentage) {
            string = "Memory: " + percentage + "%";
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
            this.hud.setHeight((int)rip.athena.client.font.FontManager.baloo17.getHeight(string) + 7);
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
            this.hud.setWidth(MemoryUsage.mc.fontRendererObj.getStringWidth(string) + 16);
            this.hud.setHeight(MemoryUsage.mc.fontRendererObj.FONT_HEIGHT + 9);
            if (this.isUsingStaticChroma) {
                DrawUtils.drawChromaString(string, posX, posY + 3.0f, true, true);
            }
            else if (this.isUsingWaveChroma) {
                DrawUtils.drawChromaString(string, posX, posY + 3.0f, false, true);
            }
            else {
                MemoryUsage.mc.fontRendererObj.drawStringWithShadow(string, posX, posY + 3.0f, this.color.getRGB());
            }
        }
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    private static long bytesToMb(final long bytes) {
        return bytes / 1024L / 1024L;
    }
}
