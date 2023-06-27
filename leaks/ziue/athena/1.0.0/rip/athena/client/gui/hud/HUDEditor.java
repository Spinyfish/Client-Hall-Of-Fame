package rip.athena.client.gui.hud;

import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.framework.*;
import net.minecraft.util.*;
import rip.athena.client.modules.*;
import java.io.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import rip.athena.client.*;
import rip.athena.client.modules.impl.render.*;
import org.lwjgl.input.*;
import java.util.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.config.save.*;
import net.minecraft.client.*;
import rip.athena.client.utils.render.*;
import java.awt.*;

public class HUDEditor extends MinecraftMenuImpl implements DrawImpl
{
    private static final Menu menu;
    private static final ResourceLocation SETTINGS;
    private static final int HELPER;
    private static final int BACKGROUND;
    private static final int BORDER;
    private static final int RESIZE;
    private static final int TEXT_COLOR;
    private static final int BACKGROUND_HIDDEN;
    private static final int BORDER_HIDDEN;
    private static final int HOVER_TEXT;
    private static final int HOVER_BACKGROUND;
    private static final int HOVER_BORDER;
    private static final int SCALE = 2;
    private static final int HELPER_THICKNESS = 2;
    private static final int RESIZE_SIZE = 10;
    private static final int SETTINGS_SIZE = 16;
    private static final int SNAP_SENS = 3;
    private HUDElement selected;
    private boolean resizing;
    private boolean moving;
    private boolean mouseDown;
    private boolean mouseDownCache;
    private boolean rightMouseDown;
    private boolean mouseDownRightCache;
    private int cachedX;
    private int cachedY;
    private double originalScale;
    
    public HUDEditor(final Module feature) {
        super(feature, HUDEditor.menu);
    }
    
    @Override
    public void initGui() {
        HUDEditor.menu.setLocation(0, 0);
        HUDEditor.menu.setWidth(this.mc.displayWidth);
        HUDEditor.menu.setHeight(this.mc.displayHeight);
        super.initGui();
    }
    
    @Override
    protected void mouseClicked(final int mX, final int mY, final int mouseButton) throws IOException {
        if (mouseButton == 0) {
            this.mouseDown = true;
        }
        else if (mouseButton == 1) {
            this.rightMouseDown = true;
        }
        super.mouseClicked(mX, mY, mouseButton);
    }
    
    @Override
    public void drawScreen(final int mX, final int mY, final float partialTicks) {
        GlStateManager.pushMatrix();
        final float value = this.guiScale / new ScaledResolution(this.mc).getScaleFactor();
        GlStateManager.scale(value, value, value);
        DrawImpl.drawRect(HUDEditor.menu.getX() + HUDEditor.menu.getWidth() / 2 - 1, 0, 2, HUDEditor.menu.getHeight(), HUDEditor.HELPER);
        DrawImpl.drawRect(0, HUDEditor.menu.getHeight() / 2 - 1, HUDEditor.menu.getWidth(), 2, HUDEditor.HELPER);
        final String line1 = "Left click to control components.".toUpperCase();
        final String line2 = "Right click a component to toggle visibility.".toUpperCase();
        final int line1Width = this.getStringWidth(line1);
        final int line2Width = this.getStringWidth(line2);
        final int line1Height = this.getStringHeight(line1);
        final int line2Height = this.getStringHeight(line2);
        final int boxWidth = Math.max(line1Width, line2Width) + 20;
        final int boxHeight = line1Height + line2Height + 4;
        final int heightOffset = 10;
        final int boxX = HUDEditor.menu.getX() + HUDEditor.menu.getWidth() / 2 - boxWidth / 2;
        final int boxY = heightOffset;
        this.drawHorizontalLine(boxX, boxY, boxWidth + 1, 1, HUDEditor.HOVER_BORDER);
        this.drawVerticalLine(boxX, boxY + 1, boxHeight - 1, 1, HUDEditor.HOVER_BORDER);
        this.drawHorizontalLine(boxX, boxY + boxHeight, boxWidth + 1, 1, HUDEditor.HOVER_BORDER);
        this.drawVerticalLine(boxX + boxWidth, boxY + 1, boxHeight - 1, 1, HUDEditor.HOVER_BORDER);
        DrawImpl.drawRect(boxX + 1, boxY + 1, boxWidth - 1, boxHeight - 1, HUDEditor.HOVER_BACKGROUND);
        this.drawText(line1, boxX + boxWidth / 2 - line1Width / 2, boxY + 2, HUDEditor.HOVER_TEXT);
        this.drawText(line2, boxX + boxWidth / 2 - line2Width / 2, boxY + line1Height + 2, HUDEditor.HOVER_TEXT);
        final int mouseX = Math.round(mX / value);
        final int mouseY = Math.round(mY / value);
        if (this.selected != null || !this.mouseDownCache) {
            for (final HUDElement element : Athena.INSTANCE.getHudManager().getElements()) {
                if (!element.getParent().isToggled()) {
                    continue;
                }
                final int x = Math.round((float)(element.getX() * 2));
                final int y = Math.round((float)(element.getY() * 2));
                final int width = Math.round(element.getWidth() * 2 * (float)element.getScale());
                final int height = Math.round(element.getHeight() * 2 * (float)element.getScale());
                final int border = element.isVisible() ? HUDEditor.BORDER : HUDEditor.BORDER_HIDDEN;
                final int background = element.isVisible() ? HUDEditor.BACKGROUND : HUDEditor.BACKGROUND_HIDDEN;
                this.drawHorizontalLine(x, y, width + 1, 1, border);
                this.drawVerticalLine(x, y + 1, height - 1, 1, border);
                this.drawHorizontalLine(x, y + height, width + 1, 1, border);
                this.drawVerticalLine(x + width, y + 1, height - 1, 1, border);
                DrawImpl.drawRect(x, y, width, height, background);
                final boolean b = mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height;
                if (b && this.selected != element) {
                    continue;
                }
                if (this.selected != null && this.selected != element) {
                    continue;
                }
                if (height > 16) {
                    this.drawImage(new ResourceLocation("Athena/menu/exit.png"), x + 1, y + height - 16, 16, 16);
                }
                int resizeSize = 10;
                if (height < 10) {
                    resizeSize = height - 1;
                }
                this.drawBottomRect(x + width - resizeSize, y + height - resizeSize, resizeSize, resizeSize, HUDEditor.RESIZE);
                final double dScale = element.getScale() * 100.0;
                final String scale = (Math.round(dScale) / 100.0f + "x").replace(".0x", "x");
                final String text = element.getParent().getName().toUpperCase() + "(" + scale + ")";
                final int tHeight = Math.round((float)(this.getStringHeight(text) * 2));
                final int tWidth = Math.round((float)(this.getStringWidth(text) * 2));
                final int tX = x + width / 2 - this.getStringWidth(text) / 2;
                int tY = y;
                if (tY - tHeight >= 0) {
                    tY -= tHeight - 5;
                }
                else if (tY + height + tHeight < HUDEditor.menu.getHeight()) {
                    tY += height + 5;
                }
                this.drawText(text, tX, tY, HUDEditor.TEXT_COLOR);
                for (final HUDElement e : Athena.INSTANCE.getHudManager().getElements()) {
                    if (!e.getParent().isToggled()) {
                        continue;
                    }
                    if (e == element) {
                        continue;
                    }
                    final int sX = Math.round((float)(e.getX() * 2));
                    final int sY = Math.round((float)(e.getY() * 2));
                    final int sWidth = Math.round(e.getWidth() * 2 * (float)e.getScale());
                    final int sHeight = Math.round(e.getHeight() * 2 * (float)e.getScale());
                    if (!this.moving) {
                        continue;
                    }
                    if (Math.abs(sX - x) <= 3 || Math.abs(sX - (x + width)) <= 3) {
                        DrawImpl.drawRect(sX - 1, 0, 2, HUDEditor.menu.getHeight(), HUDEditor.HELPER);
                    }
                    else if (Math.abs(x - (sX + sWidth)) <= 3 || Math.abs(sX + sWidth - (x + width)) <= 3) {
                        DrawImpl.drawRect(sX + sWidth - 1, 0, 2, HUDEditor.menu.getHeight(), HUDEditor.HELPER);
                    }
                    if (sY == y || Math.abs(sY - (y + height)) <= 3) {
                        DrawImpl.drawRect(0, sY - 1, HUDEditor.menu.getWidth(), 2, HUDEditor.HELPER);
                    }
                    else {
                        if (Math.abs(y - (sY + sHeight)) > 3 && Math.abs(sY + sHeight - (y + height)) > 3) {
                            continue;
                        }
                        DrawImpl.drawRect(0, sY + sHeight - 1, HUDEditor.menu.getWidth(), 2, HUDEditor.HELPER);
                    }
                }
                if (this.rightMouseDown && !this.mouseDownRightCache) {
                    element.setVisible(!element.isVisible());
                }
                else {
                    if (!this.mouseDown || this.mouseDownCache) {
                        continue;
                    }
                    this.selected = element;
                    if (mouseX < x + 16 && mouseY > y + height - 16) {
                        if (height <= 16) {
                            continue;
                        }
                        final GUIMod gui = (GUIMod)Athena.INSTANCE.getModuleManager().get(GUIMod.class);
                        gui.menuImpl.openSettings(element.getParent());
                        gui.setEnabled(true);
                    }
                    else if (mouseX > x + width - 10 && mouseY > y + height - 10) {
                        this.cachedX = mouseX / 2;
                        this.cachedY = mouseY / 2;
                        this.originalScale = this.selected.getScale();
                        this.resizing = true;
                    }
                    else {
                        this.cachedX = this.selected.getX() - mouseX / 2;
                        this.cachedY = this.selected.getY() - mouseY / 2;
                        this.moving = true;
                    }
                }
            }
        }
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.mouseDownCache = this.mouseDown;
        this.mouseDownRightCache = this.rightMouseDown;
        if (this.mouseDown) {
            this.mouseDown = Mouse.isButtonDown(0);
        }
        if (this.rightMouseDown) {
            this.rightMouseDown = Mouse.isButtonDown(1);
        }
        if (this.mouseDown) {
            if (this.selected != null) {
                final int maxPosX = (int)Math.round((HUDEditor.menu.getWidth() - this.selected.getWidth() * this.selected.getScale()) / 2.0);
                final int maxPosY = (int)Math.round((HUDEditor.menu.getHeight() - this.selected.getHeight() * this.selected.getScale()) / 2.0);
                if (this.resizing) {
                    final int xDelta = mouseX / 2 - this.cachedX;
                    final int yDelta = mouseY / 2 - this.cachedY;
                    final double maxDelta = 1.0 + Math.max(xDelta, yDelta) / 30.0;
                    double delta = maxDelta * this.originalScale;
                    if (delta > 3.0) {
                        delta = 3.0;
                    }
                    else if (delta < 0.5) {
                        delta = 0.5;
                    }
                    this.selected.setScale(delta);
                }
                else if (this.moving) {
                    int xDelta = mouseX / 2 + this.cachedX;
                    int yDelta = mouseY / 2 + this.cachedY;
                    final int x2 = xDelta;
                    final int y2 = yDelta;
                    final int width2 = Math.round(this.selected.getWidth() * (float)this.selected.getScale());
                    final int height2 = Math.round(this.selected.getHeight() * (float)this.selected.getScale());
                    for (final HUDElement e2 : Athena.INSTANCE.getHudManager().getElements()) {
                        if (!e2.getParent().isToggled()) {
                            continue;
                        }
                        if (e2 == this.selected) {
                            continue;
                        }
                        final int sX2 = Math.round((float)e2.getX());
                        final int sY2 = Math.round((float)e2.getY());
                        final int sWidth2 = Math.round(e2.getWidth() * (float)e2.getScale());
                        final int sHeight2 = Math.round(e2.getHeight() * (float)e2.getScale());
                        if (Math.abs(sX2 - x2) <= 3) {
                            xDelta = sX2;
                        }
                        else if (Math.abs(sX2 - (x2 + width2)) <= 3) {
                            xDelta = sX2 - width2;
                        }
                        else if (Math.abs(x2 - (sX2 + sWidth2)) <= 3) {
                            xDelta = sX2 + sWidth2;
                        }
                        else if (Math.abs(sX2 + sWidth2 - (x2 + width2)) <= 3) {
                            xDelta = sX2 + sWidth2 - width2;
                        }
                        if (Math.abs(sY2 - y2) <= 3) {
                            yDelta = sY2;
                        }
                        else if (Math.abs(sY2 - (y2 + height2)) <= 3) {
                            yDelta = sY2 - height2;
                        }
                        else if (Math.abs(y2 - (sY2 + sHeight2)) <= 3) {
                            yDelta = sY2 + sHeight2;
                        }
                        else {
                            if (Math.abs(sY2 + sHeight2 - (y2 + height2)) > 3) {
                                continue;
                            }
                            yDelta = sY2 + sHeight2 - height2;
                        }
                    }
                    if (xDelta < 0) {
                        xDelta = 0;
                    }
                    else if (xDelta + this.selected.getWidth() * this.selected.getScale() > HUDEditor.menu.getWidth() / 2) {
                        xDelta = Math.round(HUDEditor.menu.getWidth() / 2 - this.selected.getWidth() * (float)this.selected.getScale());
                    }
                    if (yDelta < 0) {
                        yDelta = 0;
                    }
                    else if (yDelta + this.selected.getHeight() * this.selected.getScale() > HUDEditor.menu.getHeight() / 2) {
                        yDelta = Math.round(HUDEditor.menu.getHeight() / 2 - this.selected.getHeight() * (float)this.selected.getScale());
                    }
                    this.selected.setX(xDelta);
                    this.selected.setY(yDelta);
                }
            }
        }
        else {
            this.selected = null;
            this.resizing = false;
            this.moving = false;
        }
    }
    
    @Override
    public void drawText(final String text, final int x, final int y, final int color) {
        FontManager.getProductSansRegular(25).drawString(text, x, y, color);
    }
    
    @Override
    public int getStringWidth(final String string) {
        return FontManager.getProductSansRegular(25).width(string);
    }
    
    @Override
    public int getStringHeight(final String string) {
        return (int)rip.athena.client.font.FontManager.baloo17.getHeight(string);
    }
    
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        final Config config;
        new Thread(() -> {
            config = Athena.INSTANCE.getConfigManager().getLoadedConfig();
            if (config != null) {
                config.save();
            }
        }).start();
    }
    
    static {
        menu = new Menu("", Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        SETTINGS = AssetUtils.getResource("/gui/settings.png");
        HELPER = new Color(200, 200, 200, 150).getRGB();
        BACKGROUND = new Color(200, 200, 200, 100).getRGB();
        BORDER = new Color(40, 40, 40, 200).getRGB();
        RESIZE = new Color(50, 50, 50, 200).getRGB();
        TEXT_COLOR = new Color(255, 255, 255, 255).getRGB();
        BACKGROUND_HIDDEN = new Color(200, 0, 0, 50).getRGB();
        BORDER_HIDDEN = new Color(225, 0, 0, 100).getRGB();
        HOVER_TEXT = new Color(200, 200, 200, 255).getRGB();
        HOVER_BACKGROUND = new Color(75, 75, 75, 255).getRGB();
        HOVER_BORDER = new Color(0, 0, 0, 255).getRGB();
    }
}
