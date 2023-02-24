package me.kansio.client.gui.notification;

import me.kansio.client.Client;
import me.kansio.client.modules.impl.visuals.HUD;
import me.kansio.client.utils.font.Fonts;
import me.kansio.client.utils.render.ColorPalette;
import me.kansio.client.utils.render.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import java.awt.*;

/**
 * @author Divine
 * <p>
 * I would paste superblaubeerie27 notification system but that means im not learning
 * <p>
 * ok im pasting superblaurbeerie27 notification system this shit too hard
 */
public class Notification {

    private NotificationType type;
    private String title;
    private String messsage;

    private long start;
    private long fadedIn;
    private long fadeOut;
    private long end;


    public Notification(NotificationType type, String title, String messsage, int length) {
        this.type = type;
        this.title = title;
        this.messsage = messsage;

        fadedIn = 200 * length;
        fadeOut = fadedIn + 500 * length;
        end = fadeOut + fadedIn;
    }

    public enum NotificationType {
        TOGGLEON, TOGGLEOFF, TIME, INFO, WARNING, ERROR;
    }

    public void show() {
        start = System.currentTimeMillis();
    }

    public boolean isShown() {
        return getTime() <= end;
    }

    private long getTime() {
        return System.currentTimeMillis() - start;
    }

    public void render() {
        double offset;
        int width = 120;
        int height = 30;
        long time = getTime();

        if (time < fadedIn) {
            offset = Math.tanh(time / (double) (fadedIn) * 3.0) * width;
        } else if (time > fadeOut) {
            offset = (Math.tanh(3.0 - (time - fadeOut) / (double) (end - fadeOut) * 3.0) * width);
        } else {
            offset = width;
        }

        Color backgroundcolor = new Color(0, 0, 0, 100);
        Color typecolor;
        String icon;

        switch (type) {
            case TOGGLEON:
                typecolor = new Color (ColorPalette.GREEN.getColor().getRGB());
                icon = "E";
                break;
            case TOGGLEOFF:
                typecolor = new Color (ColorPalette.RED.getColor().getRGB());
                icon = "D";
                break;
            case TIME:
                typecolor = new Color(255, 255, 255);
                icon = "F";
                break;
            case INFO:
                typecolor = new Color(255, 255, 255);
                icon = "C";
                break;
            case WARNING:
                typecolor = new Color(ColorPalette.YELLOW.getColor().getRGB());
                icon = "A";
                break;
            case ERROR:
                typecolor = new Color(ColorPalette.RED.getColor().getRGB());
                icon = "B";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        //RenderUtils.drawRect(GuiScreen.width - offset, GuiScreen.height - 5 - height, GuiScreen.width, GuiScreen.height - 5, backgroundcolor.getRGB());
        //RenderUtils.drawRect(GuiScreen.width - offset, GuiScreen.height - 5 - height, 5, GuiScreen.height - 5,  typecolor.getRGB());

        drawRect(GuiScreen.width - offset, GuiScreen.height - 5 - height - 20, GuiScreen.width, GuiScreen.height - 10 - 20, backgroundcolor.getRGB());
        drawRect(GuiScreen.width - offset, GuiScreen.height - 5 - height - 20, GuiScreen.width - offset + 2, GuiScreen.height - 10 - 20, typecolor.getRGB());

        HUD hud = (HUD) Client.getInstance().getModuleManager().getModuleByName("HUD");
        if (hud.font.getValue()) {
            Fonts.Verdana.drawString(title, (int) (GuiScreen.width - offset + 8), GuiScreen.height - 2 - height - 20, typecolor.getRGB());
            Fonts.NotifIcon.drawString(icon, (int) (GuiScreen.width - offset + 200 / 2), GuiScreen.height + 5 / 2 - height - 20, typecolor.getRGB());
            Fonts.Verdana.drawString(messsage, (int) (GuiScreen.width - offset + 8), GuiScreen.height - 40, -1);
        } else {
            fontRenderer.drawString(title, (int) (GuiScreen.width - offset + 8), GuiScreen.height - 2 - height - 20, typecolor.getRGB());
            Fonts.NotifIcon.drawString(icon, (int) (GuiScreen.width - offset + 200 / 2), GuiScreen.height + 5 / 2 - height - 20, typecolor.getRGB());
            fontRenderer.drawString(messsage, (int) (GuiScreen.width - offset + 8), GuiScreen.height - 20 - 20, -1);
        }
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        ColorUtils.glColor(color);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRect(int mode, double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        ColorUtils.glColor(color);
        worldrenderer.begin(mode, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

}