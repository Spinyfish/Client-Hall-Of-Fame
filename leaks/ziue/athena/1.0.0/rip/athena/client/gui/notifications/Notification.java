package rip.athena.client.gui.notifications;

import java.awt.*;
import net.minecraft.client.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;
import rip.athena.client.utils.render.*;
import net.minecraft.client.gui.*;

public class Notification
{
    private static Color BACKGROUND_COLOR;
    private static Color TEXT_COLOR;
    private static Color LINE_COLOR;
    private static int FADEOUT_TIME;
    private static long MAX_TIME;
    private String text;
    private Color color;
    private long start;
    
    public Notification(final String text, final Color color) {
        this.text = text;
        this.color = color;
        this.start = System.currentTimeMillis();
    }
    
    public String getText() {
        return this.text;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public long getStart() {
        return this.start;
    }
    
    public boolean isDead() {
        return System.currentTimeMillis() - this.start > Notification.MAX_TIME;
    }
    
    public int draw(final int y) {
        if (this.isDead()) {
            return y;
        }
        int x = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
        final int stringWidth = FontManager.getProductSansRegular(30).width(this.text) + 10;
        final int width = 30;
        final int height = 20;
        final float textReady = this.getDeltaByTime(1900, 600);
        final float fadeOut = this.getDeltaByTime((int)Notification.MAX_TIME - Notification.FADEOUT_TIME, Notification.FADEOUT_TIME);
        if (fadeOut > 0.0f) {
            x += Math.round((stringWidth + width) * fadeOut);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        float delta = this.getDeltaByTime(0, 500);
        this.drawRect(Math.round(x - width * delta), y, x, y - height, Notification.BACKGROUND_COLOR);
        if (delta == 1.0f) {
            final float oldDelta;
            delta = (oldDelta = this.getDeltaByTime(500, 1000));
            if (delta == 1.0f) {
                delta = this.getDeltaByTime(1500, 400);
                this.drawRect(x - width + 2, y - 2, x - width, y - 2 - Math.round((height - 4) * delta), Notification.LINE_COLOR);
                if (delta == 1.0f) {
                    delta = textReady;
                    this.drawRect(x - width - Math.round(stringWidth * delta), y - height, x - width, y, Notification.BACKGROUND_COLOR);
                }
            }
            final float textOutDelta;
            delta = (textOutDelta = this.getDeltaByTime(2500, 1500));
            if (textReady == 1.0f) {
                FontManager.getProductSansRegular(30).drawString(this.text, x - width - Math.round(stringWidth * delta) + 5, y - 14, Notification.TEXT_COLOR.getRGB());
                GL11.glColor4f(Notification.BACKGROUND_COLOR.getRed() / 255.0f, Notification.BACKGROUND_COLOR.getGreen() / 255.0f, Notification.BACKGROUND_COLOR.getBlue() / 255.0f, Notification.BACKGROUND_COLOR.getAlpha() / 255.0f);
                this.drawRect(x - width, y, x, y - height, Notification.BACKGROUND_COLOR);
                this.drawRect(x - width + 2, y - 2, x - width, y - 2 - height + 4, Notification.LINE_COLOR);
            }
            DrawUtils.drawImage(NotificationManager.LOGO, x - 24, y - 20, 20, 20);
            this.drawRect(x, y, x, y - Math.round(20.0f * oldDelta), Notification.BACKGROUND_COLOR);
            if (textOutDelta == 1.0f) {
                final float timeLeft = this.getDeltaByTime(4000, (int)Notification.MAX_TIME - 4000 - Notification.FADEOUT_TIME);
                this.drawRect(x - width - stringWidth, y, x - Math.round((width + stringWidth) * (1.0f - timeLeft)), y - 1, this.color);
            }
        }
        return y - height;
    }
    
    private void drawRect(final int x, final int y, final int x2, final int y2, final Color color) {
        Gui.drawRectangle(x, y, x2, y2, color.getRGB());
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private float getDeltaByTime(final int start, final int end) {
        float delta = (System.currentTimeMillis() - this.start - start) / (float)end;
        if (delta > 1.0f) {
            delta = 1.0f;
        }
        else if (delta < 0.0f) {
            delta = 0.0f;
        }
        return delta;
    }
    
    static {
        Notification.BACKGROUND_COLOR = new Color(35, 35, 35, 255);
        Notification.TEXT_COLOR = new Color(255, 255, 255, 255);
        Notification.LINE_COLOR = new Color(255, 255, 255, 255);
        Notification.FADEOUT_TIME = 750;
        Notification.MAX_TIME = 10000L;
    }
}
