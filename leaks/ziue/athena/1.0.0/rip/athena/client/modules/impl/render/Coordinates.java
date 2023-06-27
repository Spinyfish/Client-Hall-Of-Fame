package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import java.awt.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.modules.*;
import net.minecraft.util.*;
import rip.athena.client.events.types.input.*;
import rip.athena.client.events.*;
import org.lwjgl.opengl.*;
import rip.athena.client.*;
import net.minecraft.client.gui.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.utils.render.*;
import net.minecraft.item.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.*;

public class Coordinates extends Module
{
    @ConfigValue.Boolean(name = "Show Compass")
    private boolean showCompass;
    @ConfigValue.Boolean(name = "Show Label", description = "Show the X, Y, Z labels")
    private boolean showLabel;
    @ConfigValue.Boolean(name = "Show Direction", description = "Show the +- direction your going")
    private boolean showDirection;
    @ConfigValue.Boolean(name = "Show Biome")
    private boolean showBiome;
    @ConfigValue.Boolean(name = "Biome Preset Color", description = "Use the minecraft color for that biome")
    private boolean biomePresetColor;
    @ConfigValue.Boolean(name = "Background")
    private boolean shadedCoords;
    @ConfigValue.Boolean(name = "Custom Font")
    private boolean customFont;
    @ConfigValue.Boolean(name = "Show Avatar", description = "Shows your player head on the hud")
    private boolean showAvatar;
    @ConfigValue.Keybind(name = "Shout Coordinates Key", description = "Sends coordinates in chat")
    private int keyBind;
    @ConfigValue.Text(name = "Chat Format", description = "How the coordinates should be sent {x}, {y}, {z} are place holders")
    private String chatFormat;
    @ConfigValue.List(name = "Display Mode", values = { "Horizontal", "Vertical" }, description = "How the hud should be displayed")
    private String displayMode;
    @ConfigValue.List(name = "Display Mode", values = { "Circle", "Modern", "Fade", "Old" }, description = "Chose display of background")
    private String backgroundMode;
    @ConfigValue.Color(name = "Background Color")
    private Color backgroundColor;
    @ConfigValue.Integer(name = "Transparency", min = 0, max = 255)
    private int transparency;
    @ConfigValue.Color(name = "Label Color", description = "Color of the X, Y, Z labels")
    private Color color;
    @ConfigValue.Color(name = "Value Color", description = "Color of the X, Y, Z coordinates")
    private Color vColor;
    @ConfigValue.Color(name = "Compass Color")
    private Color tColor;
    @ConfigValue.Color(name = "Biome Color")
    private Color bColor;
    int myPosX;
    int myPosY;
    int myPosZ;
    int myAngle;
    int myDir;
    private static final String[] myCardinalPoint;
    private HUDElement hud;
    
    public Coordinates() {
        super("Coordinates", Category.RENDER, "Athena/gui/mods/coordinates.png");
        this.showCompass = true;
        this.showLabel = true;
        this.showDirection = true;
        this.showBiome = true;
        this.biomePresetColor = true;
        this.shadedCoords = true;
        this.customFont = false;
        this.showAvatar = false;
        this.keyBind = 0;
        this.chatFormat = "X: {x}, Y: {y}, Z: {z}";
        this.displayMode = "Vertical";
        this.backgroundMode = "Circle";
        this.backgroundColor = new Color(0, 0, 0, 150);
        this.transparency = 255;
        this.color = Color.WHITE;
        this.vColor = Color.WHITE;
        this.tColor = Color.WHITE;
        this.bColor = Color.WHITE;
        (this.hud = new HUDElement("coordinates", 70, 30) {
            @Override
            public void onRender() {
                Coordinates.this.render();
            }
        }).setX(0);
        this.hud.setY(150);
        this.addHUD(this.hud);
    }
    
    private int getCardinalPoint(final float par0) {
        double myPoint = MathHelper.wrapAngleTo180_float(par0) + 180.0;
        myPoint += 22.5;
        myPoint %= 360.0;
        myPoint /= 45.0;
        return MathHelper.floor_double(myPoint);
    }
    
    @SubscribeEvent
    public void onKeyPress(final KeyDownEvent event) {
        if (this.keyBind == 0) {
            return;
        }
        if (event.getKey() == this.keyBind) {
            final String toSend = this.chatFormat.replace("{x}", this.myPosX + "").replace("{y}", this.myPosY + "").replace("{z}", this.myPosZ + "");
            Coordinates.mc.thePlayer.sendChatMessage(toSend);
        }
    }
    
    private void renderPlayerCoords() {
        final Color color1 = this.color;
        final Color color2 = this.vColor;
        final Color color3 = this.tColor;
        final float posX = (float)this.hud.getX();
        final float posY = (float)this.hud.getY();
        if (this.displayMode.equalsIgnoreCase("Horizontal")) {
            GL11.glPushMatrix();
            if (Coordinates.mc.thePlayer.posX > 0.0) {
                this.myPosX = MathHelper.floor_double(Coordinates.mc.thePlayer.posX);
            }
            else {
                this.myPosX = MathHelper.ceiling_double_int(Coordinates.mc.thePlayer.posX);
            }
            this.myPosY = MathHelper.floor_double(Coordinates.mc.thePlayer.getEntityBoundingBox().minY);
            if (Coordinates.mc.thePlayer.posZ > 0.0) {
                this.myPosZ = MathHelper.floor_double(Coordinates.mc.thePlayer.posZ);
            }
            else {
                this.myPosZ = MathHelper.ceiling_double_int(Coordinates.mc.thePlayer.posZ);
            }
            this.myAngle = this.getCardinalPoint(Coordinates.mc.thePlayer.rotationYaw);
            this.myDir = (MathHelper.floor_double(Coordinates.mc.thePlayer.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3);
            String med = "X: " + this.myPosX + ", Y: " + this.myPosY + ", Z: " + this.myPosZ;
            if (this.showDirection) {
                med = "";
                if (!this.getDirectionX(this.myAngle).isEmpty()) {
                    med = med + "X: " + this.myPosX + " (" + this.getDirectionX(this.myAngle) + "), ";
                }
                else {
                    med = med + "X: " + this.myPosX + ", ";
                }
                med = med + "Y: " + this.myPosY + ", ";
                if (!this.getDirectionZ(this.myAngle).isEmpty()) {
                    med = med + "Z: " + this.myPosZ + " (" + this.getDirectionZ(this.myAngle) + ")";
                }
                else {
                    med = med + "Z: " + this.myPosZ;
                }
            }
            if (this.showCompass) {
                med = med + " " + this.getDirectionY(this.myAngle);
            }
            if (!this.showLabel) {
                med = med.replace("X:", "").replace("Y:", "").replace("Z:", "");
            }
            med = med.trim();
            final int width = this.getStringWidth(med) + 12;
            final int height = 21;
            if (this.shadedCoords) {
                if (this.backgroundMode.equalsIgnoreCase("Modern")) {
                    if (Athena.INSTANCE.getThemeManager().getTheme().isTriColor()) {
                        RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getThirdColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor());
                    }
                    else {
                        RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor());
                    }
                }
                else if (this.backgroundMode.equalsIgnoreCase("Circle")) {
                    RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
                }
                else if (this.backgroundMode.equalsIgnoreCase("Fade")) {
                    RoundedUtils.drawRoundedRect((float)this.hud.getX(), (float)this.hud.getY(), (float)(this.hud.getX() + width), (float)(this.hud.getY() + height), 8.0f, Athena.INSTANCE.getThemeManager().getTheme().getAccentColorWave().getRGB());
                }
                else {
                    RoundedUtils.drawRoundedRect((float)this.hud.getX(), (float)this.hud.getY(), (float)(this.hud.getX() + width), (float)(this.hud.getY() + height), 12.0f, this.backgroundColor.getRGB());
                }
            }
            this.drawString(med, posX - 2.0f + (width / 2 - this.getStringWidth(med) / 2), posY + 5.0f, this.vColor);
            this.hud.setWidth(width);
            this.hud.setHeight(height);
            GL11.glScaled(1.0, 1.0, 1.0);
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        if (this.displayMode.equalsIgnoreCase("Vertical")) {
            GL11.glPushMatrix();
            GL11.glEnable(3008);
            if (Coordinates.mc.thePlayer.posX > 0.0) {
                this.myPosX = MathHelper.floor_double(Coordinates.mc.thePlayer.posX);
            }
            else {
                this.myPosX = MathHelper.ceiling_double_int(Coordinates.mc.thePlayer.posX);
            }
            this.myPosY = MathHelper.floor_double(Coordinates.mc.thePlayer.getEntityBoundingBox().minY);
            if (Coordinates.mc.thePlayer.posZ > 0.0) {
                this.myPosZ = MathHelper.floor_double(Coordinates.mc.thePlayer.posZ);
            }
            else {
                this.myPosZ = MathHelper.ceiling_double_int(Coordinates.mc.thePlayer.posZ);
            }
            this.myAngle = this.getCardinalPoint(Coordinates.mc.thePlayer.rotationYaw);
            this.myDir = (MathHelper.floor_double(Coordinates.mc.thePlayer.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3);
            int stringWidth = 0;
            if (Math.abs(this.myPosX) > Math.abs(this.myPosZ) && Math.abs(this.myPosX) > Math.abs(this.myPosY)) {
                stringWidth = this.getStringWidth(this.myPosX + "");
            }
            else if (Math.abs(this.myPosZ) > Math.abs(this.myPosX) && Math.abs(this.myPosZ) > Math.abs(this.myPosY)) {
                stringWidth = this.getStringWidth(this.myPosZ + "");
            }
            else {
                stringWidth = this.getStringWidth(this.myPosY + "");
            }
            int width = 50;
            int height = 42;
            if (this.showAvatar) {
                width += 5;
            }
            int shift = 0;
            if (this.showBiome) {
                if (stringWidth < this.getStringWidth("Biome: " + Coordinates.mc.thePlayer.worldObj.getBiomeGenForCoords(Coordinates.mc.thePlayer.getPosition()).biomeName)) {
                    stringWidth = this.getStringWidth("Biome: " + Coordinates.mc.thePlayer.worldObj.getBiomeGenForCoords(Coordinates.mc.thePlayer.getPosition()).biomeName);
                }
                height = 54;
            }
            width += stringWidth;
            if (this.shadedCoords) {
                if (this.backgroundMode.equalsIgnoreCase("Modern")) {
                    RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor());
                }
                else if (this.backgroundMode.equalsIgnoreCase("Fade")) {
                    RoundedUtils.drawRoundedRect((float)this.hud.getX(), (float)this.hud.getY(), (float)(this.hud.getX() + width), (float)(this.hud.getY() + height), 8.0f, Athena.INSTANCE.getThemeManager().getTheme().getAccentColor().getRGB());
                }
                else if (this.backgroundMode.equalsIgnoreCase("Circle")) {
                    RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, ColorUtil.getClientColor(0, this.transparency), ColorUtil.getClientColor(90, this.transparency), ColorUtil.getClientColor(180, this.transparency), ColorUtil.getClientColor(270, this.transparency));
                }
                else {
                    Gui.drawRect(this.hud.getX(), this.hud.getY(), this.hud.getX() + width, this.hud.getY() + height, this.backgroundColor.getRGB());
                }
            }
            this.drawString("X: ", posX + 3.0f, posY + 3.0f + shift, this.color);
            this.drawString(this.myPosX + "", posX + 16.0f, posY + 3.0f + shift, this.vColor);
            this.drawString(this.getDirectionX(this.myAngle), posX + width - 8.0f - this.getStringWidth(this.getDirectionX(this.myAngle)), posY + 3.0f + shift, this.tColor);
            shift += 12;
            this.drawString("Y: ", posX + 3.0f, posY + 3.0f + shift, this.color);
            this.drawString(this.myPosY + "", posX + 16.0f, posY + 3.0f + shift, this.vColor);
            if (this.showAvatar) {
                Coordinates.mc.getTextureManager().bindTexture(Coordinates.mc.thePlayer.getLocationSkin());
                Gui.drawScaledCustomSizeModalRect((int)(posX + width - 20.0f - this.getStringWidth(this.getDirectionY(this.myAngle))), (int)(posY + 5.0f + shift), 8.0f, 8.0f, 8, 8, 8, 8, 64.0f, 64.0f);
            }
            this.drawString(this.getDirectionY(this.myAngle), posX + width - 8.0f - this.getStringWidth(this.getDirectionY(this.myAngle)), posY + 3.0f + shift, this.tColor);
            shift += 12;
            this.drawString("Z: ", posX + 3.0f, posY + 3.0f + shift, this.color);
            this.drawString(this.myPosZ + "", posX + 16.0f, posY + 3.0f + shift, this.vColor);
            this.drawString(this.getDirectionZ(this.myAngle), posX + width - 8.0f - this.getStringWidth(this.getDirectionZ(this.myAngle)), posY + 3.0f + shift, this.tColor);
            if (this.showBiome) {
                shift += 12;
                this.drawString("Biome: ", posX + 3.0f, posY + 3.0f + shift, this.color);
                this.drawString(Coordinates.mc.thePlayer.worldObj.getBiomeGenForCoords(Coordinates.mc.thePlayer.getPosition()).biomeName, posX + 3.0f + this.getStringWidth("Biome: "), posY + 3.0f + shift, this.biomePresetColor ? new Color(Coordinates.mc.thePlayer.worldObj.getBiomeGenForCoords(Coordinates.mc.thePlayer.getPosition()).color) : this.bColor);
            }
            this.hud.setWidth(width);
            this.hud.setHeight(height);
            GL11.glScaled(1.0, 1.0, 1.0);
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }
    
    public int getStringWidth(final String text) {
        if (this.customFont) {
            return FontManager.getProductSansRegular(25).width(text);
        }
        return Coordinates.mc.fontRendererObj.getStringWidth(text);
    }
    
    public int getStringHeight(final String text) {
        if (this.customFont) {
            return (int)rip.athena.client.font.FontManager.baloo17.getHeight(text);
        }
        return Coordinates.mc.fontRendererObj.FONT_HEIGHT;
    }
    
    public String getDirectionX(final int myAngle) {
        String toReturn = "";
        if (!this.showDirection) {
            return "";
        }
        switch (this.myAngle) {
            case 1: {
                toReturn = "+";
                break;
            }
            case 2: {
                toReturn = "+";
                break;
            }
            case 3: {
                toReturn = "+";
                break;
            }
            case 5: {
                toReturn = "-";
                break;
            }
            case 6: {
                toReturn = "-";
                break;
            }
            case 7: {
                toReturn = "-";
                break;
            }
            default: {
                toReturn = "";
                break;
            }
        }
        return toReturn;
    }
    
    public String getDirectionY(final int myAngle) {
        if (!this.showCompass) {
            return "";
        }
        return Coordinates.myCardinalPoint[myAngle];
    }
    
    public String getDirectionZ(final int myAngle) {
        String toReturn = "";
        if (!this.showDirection) {
            return "";
        }
        switch (this.myAngle) {
            case 0: {
                toReturn = "-";
                break;
            }
            case 1: {
                toReturn = "-";
                break;
            }
            case 3: {
                toReturn = "+";
                break;
            }
            case 4: {
                toReturn = "+";
                break;
            }
            case 5: {
                toReturn = "+";
                break;
            }
            case 7: {
                toReturn = "-";
                break;
            }
            default: {
                toReturn = "";
                break;
            }
        }
        return toReturn;
    }
    
    public void drawString(final String text, final double posX, final double posY, final Color color) {
        if (this.customFont) {
            if (color.getBlue() == 5 && color.getRed() == 5 && color.getGreen() == 5) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), text, (int)posX, (int)posY - 2, true, true);
            }
            else if (color.getBlue() == 6 && color.getRed() == 6 && color.getGreen() == 6) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), text, (int)posX, (int)posY - 2, false, true);
            }
            else {
                FontManager.getProductSansRegular(25).drawString(text, (int)posX, (int)posY - 2, color.getRGB());
            }
        }
        else if (color.getBlue() == 5 && color.getRed() == 5 && color.getGreen() == 5) {
            DrawUtils.drawChromaString(text, (int)posX + 2, (int)posY + 2, true, true);
        }
        else if (color.getBlue() == 6 && color.getRed() == 6 && color.getGreen() == 6) {
            DrawUtils.drawChromaString(text, (int)posX + 2, (int)posY + 2, false, true);
        }
        else {
            Coordinates.mc.fontRendererObj.drawStringWithShadow(text, (float)((int)posX + 2), (float)((int)posY + 2), color.getRGB());
        }
    }
    
    public void renderItemStack(final ItemStack stack, final int x, final int y) {
        final RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
        itemRender.zLevel = -50.0f;
        this.renderIcon(stack, x, y, 8, 8);
    }
    
    private void renderIcon(final ItemStack stack, final int vertexX, final int vertexY, final int intU, final int intV) {
        try {
            final IBakedModel iBakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
            final TextureAtlasSprite textureAtlasSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(iBakedModel.getParticleTexture().getIconName());
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(vertexX, vertexY + intV, 0.0).tex(textureAtlasSprite.getMinU(), textureAtlasSprite.getMaxV()).endVertex();
            worldRenderer.pos(vertexX + intU, vertexY + intV, 0.0).tex(textureAtlasSprite.getMaxU(), textureAtlasSprite.getMaxV()).endVertex();
            worldRenderer.pos(vertexX + intU, vertexY, 0.0).tex(textureAtlasSprite.getMaxU(), textureAtlasSprite.getMinV()).endVertex();
            worldRenderer.pos(vertexX, vertexY, 0.0).tex(textureAtlasSprite.getMinU(), textureAtlasSprite.getMinV()).endVertex();
            tessellator.draw();
        }
        catch (Exception ex) {}
    }
    
    public void render() {
        if (!Coordinates.mc.gameSettings.showDebugInfo) {
            this.renderPlayerCoords();
        }
    }
    
    static {
        myCardinalPoint = new String[] { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };
    }
}
