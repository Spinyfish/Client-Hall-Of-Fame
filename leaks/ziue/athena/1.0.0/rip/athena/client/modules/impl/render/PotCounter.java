package rip.athena.client.modules.impl.render;

import java.awt.*;
import rip.athena.client.config.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.modules.*;
import rip.athena.client.*;
import net.minecraft.client.gui.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.utils.render.*;
import net.minecraft.item.*;

public class PotCounter extends Module
{
    @ConfigValue.Color(name = "Color")
    private Color color;
    @ConfigValue.List(name = "Display Mode", values = { "Circle", "Modern", "Fade", "Old" }, description = "Chose display of background")
    private String backgroundMode;
    @ConfigValue.Boolean(name = "Background")
    private boolean background;
    @ConfigValue.Color(name = "Background Color")
    private Color backgroundColor;
    @ConfigValue.Integer(name = "Transparency", min = 0, max = 255)
    private int transparency;
    @ConfigValue.List(name = "Potion Type", values = { "Instant Health", "Instant Health II", "Soup" })
    private String potType;
    @ConfigValue.Boolean(name = "Custom Font")
    private boolean customFont;
    @ConfigValue.Boolean(name = "Static Chroma")
    private boolean isUsingStaticChroma;
    @ConfigValue.Boolean(name = "Wave Chroma")
    private boolean isUsingWaveChroma;
    private HUDElement hud;
    private int width;
    private int height;
    private int counter;
    
    public PotCounter() {
        super("Pot Counter", Category.RENDER, "Athena/gui/mods/potion.png");
        this.color = Color.WHITE;
        this.backgroundMode = "Circle";
        this.background = true;
        this.backgroundColor = new Color(0, 0, 0, 150);
        this.transparency = 255;
        this.potType = "Instant Health II";
        this.customFont = false;
        this.isUsingStaticChroma = false;
        this.isUsingWaveChroma = false;
        this.width = 56;
        this.height = 18;
        this.counter = 0;
        (this.hud = new HUDElement("potioncounter", this.width, this.height) {
            @Override
            public void onRender() {
                PotCounter.this.render();
            }
        }).setX(1);
        this.hud.setY(200);
        this.addHUD(this.hud);
    }
    
    public void render() {
        if (PotCounter.mc.gameSettings.showDebugInfo) {
            return;
        }
        this.counter = 0;
        if (PotCounter.mc.thePlayer != null && PotCounter.mc.theWorld != null) {
            for (int i = 0; i < PotCounter.mc.thePlayer.inventory.getSizeInventory(); ++i) {
                final ItemStack stack = PotCounter.mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null) {
                    if (this.potType.contains("Instant Health II") && Item.getItemById(373) == stack.getItem() && stack.getMetadata() == 16421) {
                        this.counter += stack.stackSize;
                    }
                    else if (this.potType.contains("Soup") && Item.getItemById(282) == stack.getItem()) {
                        this.counter += stack.stackSize;
                    }
                    else if (this.potType.equalsIgnoreCase("Instant Health") && Item.getItemById(373) == stack.getItem() && stack.getMetadata() == 16453) {
                        this.counter += stack.stackSize;
                    }
                }
            }
        }
        final String str = this.counter + (this.potType.contains("Instant") ? " Pots" : " Soup");
        final int width = this.hud.getWidth();
        final int height = this.hud.getHeight();
        if (this.background) {
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
                Gui.drawRect(this.hud.getX(), this.hud.getY(), this.hud.getX() + width, this.hud.getY() + height, this.backgroundColor.getRGB());
            }
        }
        final float posY = (float)(this.hud.getY() + 2);
        final float posX = (float)(this.hud.getX() + 9);
        if (this.customFont) {
            this.hud.setWidth(FontManager.getProductSansRegular(25).width(str) + 16);
            this.hud.setHeight((int)(rip.athena.client.font.FontManager.baloo17.getHeight(str) + 7.0f));
            if (this.isUsingStaticChroma) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), str, (int)posX, (int)posY + 1, true, true);
            }
            else if (this.isUsingWaveChroma) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), str, (int)posX, (int)posY + 1, false, true);
            }
            else {
                FontManager.getProductSansRegular(25).drawString(str, (int)posX, (int)posY + 1, this.color.getRGB());
            }
        }
        else {
            this.hud.setWidth(PotCounter.mc.fontRendererObj.getStringWidth(str) + 16);
            this.hud.setHeight(PotCounter.mc.fontRendererObj.FONT_HEIGHT + 9);
            if (this.isUsingStaticChroma) {
                DrawUtils.drawChromaString(str, posX, posY + 3.0f, true, true);
            }
            else if (this.isUsingWaveChroma) {
                DrawUtils.drawChromaString(str, posX, posY + 3.0f, false, true);
            }
            else {
                PotCounter.mc.fontRendererObj.drawString(str, (int)posX, (int)posY + 3, this.color.getRGB());
            }
        }
    }
}
