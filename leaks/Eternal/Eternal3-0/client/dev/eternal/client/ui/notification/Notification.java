package dev.eternal.client.ui.notification;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.util.animate.Position;
import dev.eternal.client.util.render.RenderUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

@Getter
public class Notification {

  private final float width;
  private final float height;

  private final int time;
  private final String header;
  private final String content;
  private final Position position;
  private final NotificationType notificationType;
  private final long initTime = System.currentTimeMillis();
  private final TrueTypeFontRenderer title = FontManager.getFontRenderer(FontType.ICIEL, 24);
  private final TrueTypeFontRenderer sub = FontManager.getFontRenderer(FontType.ICIEL, 18);

  public Notification(String header, String content, int time, NotificationType notificationType) {
    this.header = header;
    this.content = content;
    this.time = time;
    this.notificationType = notificationType;
    this.width = 5 + Math.max(title.getWidth(header), sub.getWidth(content));
    this.height = 40;
    this.position = new Position(0, 0, 1);
  }

  public void render() {
    final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    final double x = position.getX();
    final double y = position.getY();
/*    RenderUtil.drawRoundedRect(
        x, y, x + width + 5, y + height, 12, 0xAA000000 + notificationType.getColour());

//    Gui.drawRect(x, y,
//            x + width + 5, y + 1, Client.getSingleton().getScheme().getPrimary());

    title.drawStringWithShadow(header, x + 5, y + 7.5, -1);
    sub.drawStringWithShadow(content, x + 5, y + 25, -1);*/

    float rectWidth = sr.getScaledWidth() - width - 6;

    GL11.glEnable(3089);
    RenderUtil.prepareScissorBox(Math.min(sr.getScaledWidth() - x, sr.getScaledWidth()), sr.getScaledHeight() - 26 - y, sr.getScaledWidth() + 1, sr.getScaledHeight() - position.getY());

    Gui.drawRect(
        sr.getScaledWidth(),
        (sr.getScaledHeight() - y),
        (rectWidth - 2),
        (sr.getScaledHeight() - 26 - y),
        0xFF0C0C0C);

    title.drawStringWithShadow(
        header,
        rectWidth + 2,
        sr.getScaledHeight() - y - 24,
        -1);

    sub.drawStringWithShadow(
        content,
        rectWidth + 2,
        sr.getScaledHeight() - y - 11,
        0xff9C9C9C);

    Gui.drawRect(
        sr.getScaledWidth(),
        sr.getScaledHeight() - y - 1,
        sr.getScaledWidth()
            - width + (width * (System.currentTimeMillis() - initTime) / time),
        sr.getScaledHeight() - y,
        0xff55AA33);

    Gui.drawRect((rectWidth - 26),
        (sr.getScaledHeight() - y - 26),
        (rectWidth),
        (sr.getScaledHeight() - y),
        0xFF0C0C0C);

    GlStateManager.color(1, 1, 1, 1);

    GlStateManager.enableAlpha();
    GlStateManager.enableBlend();

    RenderUtil.drawRoundedRect(
        rectWidth - 20,
        sr.getScaledHeight() - y - 20,
        rectWidth - 5,
        sr.getScaledHeight() - y - 5,
        8, notificationType.colour());

//    Minecraft.getMinecraft().getTextureManager().bindTexture(notificationType.getIcon());
//    Gui.drawModalRectWithCustomSizedTexture((int) (rectWidth - 25),
//        (int) (sr.getScaledHeight() - position.getY() - 25), 0, 0, 24, 24, 24, 24);

    GL11.glDisable(3089);

    GlStateManager.color(1, 1, 1, 1);

  }

}