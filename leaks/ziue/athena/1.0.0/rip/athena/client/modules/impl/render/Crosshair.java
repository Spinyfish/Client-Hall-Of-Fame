package rip.athena.client.modules.impl.render;

import java.awt.*;
import rip.athena.client.config.*;
import net.minecraft.util.*;
import rip.athena.client.modules.*;
import rip.athena.client.events.types.client.*;
import rip.athena.client.events.*;
import rip.athena.client.events.types.render.*;
import java.awt.image.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.gui.*;
import org.lwjgl.input.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.*;

public class Crosshair extends Module
{
    @ConfigValue.Color(name = "Draw color")
    private Color color;
    @ConfigValue.Integer(name = "Brush size", min = 2, max = 10)
    private int brushSize;
    @ConfigValue.Boolean(name = "Rainbow color")
    private boolean rainbow;
    @ConfigValue.Boolean(name = "Delete mode")
    private boolean deleteMode;
    public boolean stoppedMouse1;
    public static int rainbowSpeedNerf;
    public static int rainbowSpeedNerfProg;
    public static Color rainbowColor;
    public static int rainbowR;
    public static int rainbowG;
    public static int rainbowB;
    public static int[][] crosshair;
    public ResourceLocation crosshairResource;
    public boolean shouldUpdateCrosshair;
    int containerColor;
    int parentContainerColor;
    int innerContainerColor;
    int groupHeaderColor;
    int textColor;
    int groupContainerColor;
    
    public Crosshair() {
        super("Crosshair", Category.RENDER, "Athena/gui/mods/crosshair.png");
        this.color = Color.RED;
        this.brushSize = 2;
        this.shouldUpdateCrosshair = false;
        this.containerColor = new Color(50, 50, 50, 255).getRGB();
        this.parentContainerColor = new Color(0, 0, 0, 255).getRGB();
        this.innerContainerColor = new Color(25, 25, 25, 255).getRGB();
        this.groupHeaderColor = new Color(20, 20, 20, 255).getRGB();
        this.textColor = new Color(255, 255, 255, 255).getRGB();
        this.groupContainerColor = new Color(35, 35, 35, 255).getRGB();
        Crosshair.crosshair = new int[84][74];
    }
    
    @SubscribeEvent
    public void onTick(final ClientTickEvent event) {
        if (Crosshair.rainbowSpeedNerfProg >= Crosshair.rainbowSpeedNerf) {
            if (Crosshair.rainbowR == 255 && Crosshair.rainbowG != 255 && Crosshair.rainbowB == 0) {
                ++Crosshair.rainbowG;
            }
            else if (Crosshair.rainbowR != 0 && Crosshair.rainbowG == 255 && Crosshair.rainbowB == 0) {
                --Crosshair.rainbowR;
            }
            else if (Crosshair.rainbowR == 0 && Crosshair.rainbowG == 255 && Crosshair.rainbowB != 255) {
                ++Crosshair.rainbowB;
            }
            else if (Crosshair.rainbowR == 0 && Crosshair.rainbowG != 0 && Crosshair.rainbowB == 255) {
                --Crosshair.rainbowG;
            }
            else if (Crosshair.rainbowR != 255 && Crosshair.rainbowG == 0 && Crosshair.rainbowB == 255) {
                ++Crosshair.rainbowR;
            }
            else if (Crosshair.rainbowR == 255 && Crosshair.rainbowG == 0 && Crosshair.rainbowB != 0) {
                --Crosshair.rainbowB;
            }
            Crosshair.rainbowColor = new Color(Crosshair.rainbowR, Crosshair.rainbowG, Crosshair.rainbowB);
            Crosshair.rainbowSpeedNerfProg = 0;
        }
        else {
            ++Crosshair.rainbowSpeedNerfProg;
        }
    }
    
    @SubscribeEvent
    public void onRender(final RenderEvent event) {
        if (event.getRenderType() != RenderType.CROSSHAIR) {
            return;
        }
        event.setCancelled(true);
        this.drawCrosshair();
    }
    
    public void drawCrosshair() {
        if (!this.isToggled()) {
            return;
        }
        final int width = Crosshair.crosshair.length;
        final int height = Crosshair.crosshair[0].length;
        if (this.crosshairResource == null || this.shouldUpdateCrosshair) {
            if (this.crosshairResource != null) {
                Crosshair.mc.getTextureManager().deleteTexture(this.crosshairResource);
                this.crosshairResource = null;
            }
            final BufferedImage bufferedCrosshair = new BufferedImage(width, height, 2);
            for (int x = 0; x < Crosshair.crosshair.length; ++x) {
                for (int y = 0; y < Crosshair.crosshair[x].length; ++y) {
                    int color = Crosshair.crosshair[x][y];
                    if (color != 0 && this.rainbow) {
                        color = Crosshair.rainbowColor.getRGB();
                    }
                    bufferedCrosshair.setRGB(x, y, color);
                }
            }
            final DynamicTexture texture = new DynamicTexture(bufferedCrosshair);
            bufferedCrosshair.getRGB(0, 0, bufferedCrosshair.getWidth(), bufferedCrosshair.getHeight(), texture.getTextureData(), 0, bufferedCrosshair.getWidth());
            final ResourceLocation resource = Crosshair.mc.getTextureManager().getDynamicTextureLocation("custom-crosshair", texture);
            this.crosshairResource = resource;
            this.shouldUpdateCrosshair = false;
        }
        if (this.crosshairResource != null) {
            final ScaledResolution sr = new ScaledResolution(Crosshair.mc);
            final int drawX = sr.getScaledWidth() / 2 - width / 2;
            final int drawY = sr.getScaledHeight() / 2 - height / 2;
            this.drawImage(this.crosshairResource, drawX, drawY, width, height);
            if (this.crosshairResource != null && this.rainbow) {
                this.shouldUpdateCrosshair = true;
            }
        }
    }
    
    public void drawPicker(int drawX, int drawY, final int drawWidth, final int drawHeight, final int mouseX, final int mouseY) {
        Gui.drawRectangle(drawX, drawY, drawX + drawWidth, drawY + drawHeight, this.parentContainerColor);
        Gui.drawRectangle(drawX + drawWidth / 2 - 1, drawY + 1, drawX + drawWidth / 2 + 1, drawY + drawHeight - 1, this.containerColor);
        Gui.drawRectangle(drawX + 1, drawY + drawHeight / 2 - 1, drawX + drawWidth - 1, drawY + drawHeight / 2 + 1, this.containerColor);
        final boolean hovering = true;
        boolean mouseDown = Mouse.isButtonDown(0) && hovering;
        if (!mouseDown && hovering) {
            this.stoppedMouse1 = true;
        }
        else if (!hovering) {
            this.stoppedMouse1 = false;
        }
        if (mouseDown && !this.stoppedMouse1) {
            mouseDown = false;
        }
        final int mX = Math.round((float)mouseX);
        final int mY = Math.round((float)mouseY);
        final int theColor = this.deleteMode ? Color.white.getRGB() : this.color.getRGB();
        ++drawX;
        ++drawY;
        boolean edited = false;
        for (int x = 0; x < Crosshair.crosshair.length; ++x) {
            for (int y = 0; y < Crosshair.crosshair[x].length; ++y) {
                final int color = Crosshair.crosshair[x][y];
                final int theX = drawX + x;
                final int theY = drawY + y;
                if (mX == theX && mY == theY && mouseDown && hovering) {
                    for (int xx = -(this.brushSize / 2) + x; xx < x + this.brushSize / 2; ++xx) {
                        for (int yy = -(this.brushSize / 2) + y; yy < y + this.brushSize / 2; ++yy) {
                            Crosshair.crosshair[(xx < Crosshair.crosshair.length && xx >= 0) ? xx : x][(yy < Crosshair.crosshair[x].length && yy >= 0) ? yy : y] = (this.deleteMode ? 0 : theColor);
                            edited = true;
                        }
                    }
                }
                if (color != 0) {
                    this.drawPixel(theX, theY, this.rainbow ? Crosshair.rainbowColor.getRGB() : color);
                }
            }
        }
        if (edited) {
            this.shouldUpdateCrosshair = true;
        }
        if (hovering) {
            for (int xx2 = -(this.brushSize / 2) + mX; xx2 < mX + this.brushSize / 2; ++xx2) {
                for (int yy2 = -(this.brushSize / 2) + mY; yy2 < mY + this.brushSize / 2; ++yy2) {
                    if (xx2 < Crosshair.crosshair.length + drawX && xx2 >= drawX && yy2 < Crosshair.crosshair[0].length + drawY && yy2 >= drawY) {
                        this.drawPixel(xx2, yy2, theColor);
                    }
                }
            }
        }
    }
    
    public void drawBackground(final int x, final int y, final int width, final int height) {
        Gui.drawRectangle(x, y, x + width, y + height, this.parentContainerColor);
        Gui.drawRectangle(x + 1, y + 1, x + width - 1, y + height - 1, this.containerColor);
        Gui.drawRectangle(x + 2, y + 2, x + width - 2, y + height - 2, this.innerContainerColor);
    }
    
    public void drawPixel(final int x, final int y, final int color) {
        Gui.drawRectangle(x, y, x + 1, y + 1, color);
    }
    
    public void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height) {
        GlStateManager.blendFunc(770, 771);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float)width, (float)height);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);
    }
    
    static {
        Crosshair.rainbowSpeedNerf = 0;
        Crosshair.rainbowSpeedNerfProg = 0;
        Crosshair.rainbowColor = new Color(0, 0, 0, 0);
        Crosshair.rainbowR = 255;
        Crosshair.rainbowG = 0;
        Crosshair.rainbowB = 0;
    }
}
