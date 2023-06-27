package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import java.awt.*;
import java.text.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.modules.*;
import rip.athena.client.events.types.entity.*;
import net.minecraft.util.*;
import rip.athena.client.events.*;
import org.lwjgl.opengl.*;
import rip.athena.client.*;
import net.minecraft.client.gui.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.utils.render.*;

public class ReachDisplay extends Module
{
    @ConfigValue.List(name = "Display Mode", values = { "Circle", "Modern", "Fade", "Old" }, description = "Chose display of background")
    private String backgroundMode;
    @ConfigValue.Boolean(name = "Background")
    private boolean backGround;
    @ConfigValue.Color(name = "Background Color")
    private Color background;
    @ConfigValue.Integer(name = "Transparency", min = 0, max = 255)
    private int transparency;
    @ConfigValue.Color(name = "Color")
    private Color color;
    @ConfigValue.Boolean(name = "Custom Font")
    private boolean customFont;
    @ConfigValue.Boolean(name = "Static Chroma")
    private boolean isUsingStaticChroma;
    @ConfigValue.Boolean(name = "Wave Chroma")
    private boolean isUsingWaveChroma;
    private DecimalFormat format;
    private double distance;
    private long hitTime;
    private String reach;
    HUDElement hud;
    private int width;
    private int height;
    
    public ReachDisplay() {
        super("Reach Display", Category.RENDER, "Athena/gui/mods/reach.png");
        this.backgroundMode = "Circle";
        this.backGround = true;
        this.background = new Color(0, 0, 0, 150);
        this.transparency = 255;
        this.color = Color.WHITE;
        this.customFont = false;
        this.isUsingStaticChroma = false;
        this.isUsingWaveChroma = false;
        this.format = new DecimalFormat("0.##");
        this.distance = 0.0;
        this.hitTime = -1L;
        this.width = 56;
        this.height = 18;
        (this.hud = new HUDElement("reachdisplay", this.width, this.height) {
            @Override
            public void onRender() {
                ReachDisplay.this.render();
            }
        }).setX(1);
        this.hud.setY(175);
        this.addHUD(this.hud);
    }
    
    @SubscribeEvent
    public void onDamageEntity(final AttackEntityEvent e) {
        if (ReachDisplay.mc.objectMouseOver != null && ReachDisplay.mc.objectMouseOver.hitVec != null && ReachDisplay.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            this.distance = ReachDisplay.mc.objectMouseOver.hitVec.distanceTo(ReachDisplay.mc.thePlayer.getPositionEyes(1.0f));
            this.hitTime = System.currentTimeMillis();
        }
    }
    
    public void render() {
        if (ReachDisplay.mc.gameSettings.showDebugInfo) {
            return;
        }
        GL11.glPushMatrix();
        if (System.currentTimeMillis() - this.hitTime > 5000L) {
            this.distance = 0.0;
        }
        if (this.distance == 0.0) {
            this.reach = "Hasn't attacked";
        }
        else {
            this.reach = this.format.format(this.distance) + " blocks";
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
            this.hud.setWidth(FontManager.getProductSansRegular(25).width(String.valueOf(this.reach)) + 16);
            this.hud.setHeight((int)rip.athena.client.font.FontManager.baloo17.getHeight(String.valueOf(this.reach)) + 7);
            if (this.isUsingStaticChroma) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), String.valueOf(this.reach), (int)posX, (int)posY + 1, true, true);
            }
            else if (this.isUsingWaveChroma) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), String.valueOf(this.reach), (int)posX, (int)posY + 1, false, true);
            }
            else {
                FontManager.getProductSansRegular(25).drawString(String.valueOf(this.reach), (int)posX + 1, (int)posY + 2, this.color.getRGB());
            }
        }
        else {
            this.hud.setWidth(ReachDisplay.mc.fontRendererObj.getStringWidth(String.valueOf(this.reach)) + 16);
            this.hud.setHeight(ReachDisplay.mc.fontRendererObj.FONT_HEIGHT + 9);
            if (this.isUsingStaticChroma) {
                DrawUtils.drawChromaString(String.valueOf(this.reach), posX, posY + 3.0f, true, true);
            }
            else if (this.isUsingWaveChroma) {
                DrawUtils.drawChromaString(String.valueOf(this.reach), posX, posY + 3.0f, false, true);
            }
            else {
                ReachDisplay.mc.fontRendererObj.drawStringWithShadow(String.valueOf(this.reach), posX, posY + 3.0f, this.color.getRGB());
            }
        }
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
}
