package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import java.awt.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.client.resources.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.modules.*;
import org.lwjgl.opengl.*;
import rip.athena.client.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.gui.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.events.types.client.*;
import rip.athena.client.events.*;
import net.minecraft.client.renderer.texture.*;

public class PackDisplay extends Module
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
    private IResourcePack pack;
    private ResourceLocation currentPack;
    private List<ResourcePackRepository.Entry> packs;
    ResourcePackRepository resourcePackRepository;
    private HUDElement hud;
    private int width;
    private int height;
    
    public PackDisplay() {
        super("PackDisplay", Category.RENDER);
        this.backgroundMode = "Circle";
        this.backGround = true;
        this.background = new Color(0, 0, 0, 150);
        this.transparency = 255;
        this.color = Color.WHITE;
        this.customFont = false;
        this.isUsingStaticChroma = false;
        this.isUsingWaveChroma = false;
        this.width = 66;
        this.height = 48;
        (this.hud = new HUDElement("packdisplay", this.width, this.height) {
            @Override
            public void onRender() {
                PackDisplay.this.render();
            }
        }).setX(1);
        this.hud.setY(175);
        this.addHUD(this.hud);
    }
    
    public void render() {
        if (PackDisplay.mc.gameSettings.showDebugInfo) {
            return;
        }
        GL11.glPushMatrix();
        this.resourcePackRepository = PackDisplay.mc.getResourcePackRepository();
        this.packs = this.resourcePackRepository.getRepositoryEntries();
        if (this.pack == null) {
            this.pack = this.getCurrentPack();
            this.loadTexture();
        }
        final int width = this.hud.getWidth();
        final int height = this.hud.getHeight();
        if (this.backGround) {
            if (this.backgroundMode.equalsIgnoreCase("Modern")) {
                if (Athena.INSTANCE.getThemeManager().getTheme().isTriColor()) {
                    RoundedUtils.drawGradientRound((float)(this.hud.getX() - 10), (float)this.hud.getY(), (float)(this.hud.getWidth() + FontManager.getProductSansRegular(25).width(this.convertNormalText(this.pack.getPackName())) / 2), (float)(this.hud.getHeight() + 20), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getThirdColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor());
                }
                else {
                    RoundedUtils.drawGradientRound((float)(this.hud.getX() - 10), (float)this.hud.getY(), (float)(this.hud.getWidth() + FontManager.getProductSansRegular(25).width(this.convertNormalText(this.pack.getPackName())) / 2), (float)(this.hud.getHeight() + 20), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor());
                }
            }
            else if (this.backgroundMode.equalsIgnoreCase("Circle")) {
                RoundedUtils.drawGradientRound((float)(this.hud.getX() - 10), (float)this.hud.getY(), (float)(this.hud.getWidth() + FontManager.getProductSansRegular(25).width(this.convertNormalText(this.pack.getPackName())) / 2), (float)(this.hud.getHeight() + 20), 6.0f, ColorUtil.getClientColor(0, this.transparency), ColorUtil.getClientColor(90, this.transparency), ColorUtil.getClientColor(180, this.transparency), ColorUtil.getClientColor(270, this.transparency));
            }
            else if (this.backgroundMode.equalsIgnoreCase("Fade")) {
                RoundedUtils.drawRoundedRect((float)(this.hud.getX() - 10), (float)this.hud.getY(), (float)(this.hud.getX() + width + FontManager.getProductSansRegular(20).width(this.convertNormalText(this.pack.getPackName())) / 2), (float)(this.hud.getY() + height + 20), 12.0f, Athena.INSTANCE.getThemeManager().getTheme().getAccentColor().getRGB());
            }
            else {
                Gui.drawRect(this.hud.getX() - 10, this.hud.getY(), this.hud.getX() + width + FontManager.getProductSansRegular(20).width(this.convertNormalText(this.pack.getPackName())) / 2, this.hud.getY() + height + 20, this.background.getRGB());
            }
        }
        this.hud.setHeight(this.hud.getWidth() + FontManager.getProductSansRegular(25).width(this.convertNormalText(this.pack.getPackName())) / 2);
        this.hud.setWidth(this.hud.getHeight() + 20);
        PackDisplay.mc.getTextureManager().bindTexture(this.currentPack);
        RoundedUtils.drawRoundTextured((float)this.hud.getX(), this.hud.getY() + 4.5f, 29.0f, 29.0f, 2.0f, 225.0f);
        final float posY = (float)(this.hud.getY() + 10);
        final float posX = (float)(this.hud.getX() + 33);
        if (this.customFont) {
            this.hud.setWidth(FontManager.getProductSansRegular(25).width(this.convertNormalText(this.pack.getPackName())) + 16);
            this.hud.setHeight((int)rip.athena.client.font.FontManager.baloo17.getHeight(this.convertNormalText(this.pack.getPackName())) + 7);
            if (this.isUsingStaticChroma) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), this.convertNormalText(this.pack.getPackName()), (int)posX + 1, (int)posY + 2, true, true);
            }
            else if (this.isUsingWaveChroma) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), this.convertNormalText(this.pack.getPackName()), (int)(posX + 1.0f), (int)posY + 2, false, true);
            }
            else {
                FontManager.getProductSansRegular(25).drawString(this.convertNormalText(this.pack.getPackName()), (int)posX + 1, (int)posY + 2, this.color.getRGB());
            }
        }
        else {
            this.hud.setWidth(PackDisplay.mc.fontRendererObj.getStringWidth(this.convertNormalText(this.pack.getPackName())) + 16);
            this.hud.setHeight(PackDisplay.mc.fontRendererObj.FONT_HEIGHT + 9);
            if (this.isUsingStaticChroma) {
                DrawUtils.drawChromaString(this.convertNormalText(this.pack.getPackName()), posX, posY + 3.0f, true, true);
            }
            else if (this.isUsingWaveChroma) {
                DrawUtils.drawChromaString(this.convertNormalText(this.pack.getPackName()), posX, posY + 3.0f, false, true);
            }
            else {
                PackDisplay.mc.fontRendererObj.drawStringWithShadow(this.convertNormalText(this.pack.getPackName()), posX, posY + 3.0f, this.color.getRGB());
            }
        }
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    @SubscribeEvent
    public void onSwitchTexture(final SwitchTextureEvent event) {
        this.packs = this.resourcePackRepository.getRepositoryEntries();
        this.pack = this.getCurrentPack();
        this.loadTexture();
    }
    
    private String convertNormalText(final String text) {
        return text.replaceAll("\\u00a71", "").replaceAll("\\u00a72", "").replaceAll("\\u00a73", "").replaceAll("\\u00a74", "").replaceAll("\\u00a75", "").replaceAll("\\u00a76", "").replaceAll("\\u00a77", "").replaceAll("\\u00a78", "").replaceAll("\\u00a79", "").replaceAll("\\u00a7a", "").replaceAll("\\u00a7b", "").replaceAll("\\u00a7c", "").replaceAll("\\u00a7d", "").replaceAll("\\u00a7e", "").replaceAll("\\u00a7f", "").replaceAll("\\u00a7g", "").replaceAll("\\u00a7k", "").replaceAll("\\u00a7l", "").replaceAll("\\u00a7m", "").replaceAll("\\u00a7n", "").replaceAll("\\u00a7o", "").replaceAll("\\u00a7r", "").replace(".zip", "");
    }
    
    private void loadTexture() {
        DynamicTexture dynamicTexture;
        try {
            dynamicTexture = new DynamicTexture(this.getCurrentPack().getPackImage());
        }
        catch (Exception e) {
            dynamicTexture = TextureUtil.missingTexture;
        }
        this.currentPack = PackDisplay.mc.getTextureManager().getDynamicTextureLocation("texturepackicon", dynamicTexture);
    }
    
    private IResourcePack getCurrentPack() {
        if (this.packs != null && !this.packs.isEmpty()) {
            final IResourcePack last = this.packs.get(this.packs.size() - 1).getResourcePack();
            return last;
        }
        return PackDisplay.mc.mcDefaultResourcePack;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
}
