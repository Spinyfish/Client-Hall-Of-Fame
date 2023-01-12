package dev.eternal.client.ui.alt;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.util.client.Alt;
import dev.eternal.client.util.client.MouseUtils;
import dev.eternal.client.util.render.RenderUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AltPart {

  private int x, y;
  @Getter
  private final Alt alt;
  private final Minecraft mc = Minecraft.getMinecraft();
  public boolean selfDestruct, viewable;
  private final TrueTypeFontRenderer icelFont = FontManager.getFontRenderer(FontType.ICIEL, 20);
  private final Executor altLoginExecutor = Executors.newSingleThreadExecutor();

  public AltPart(Alt alt) {
    this.alt = alt;
  }

  public void render(int x, int y) {
    this.x = x;
    this.y = y;
    Gui.drawRect(x + 1, y, x + 200, y + 35, 0x66000000);
    RenderUtil.drawRoundedRect(x + 1, y, x + 2, y + 35, 0, Client.singleton().scheme().getPrimary());
//    RenderUtil.drawRoundedRect(x + 1, y, x + 180, y + 35, -1, 0x66000000);
    try {
      RenderUtil.drawPlayerFaceOther(alt.username(), x + 7, y + 5, 24, 24);
    } catch (IOException e) {
      e.printStackTrace();
    }
    icelFont.drawStringWithShadow(alt.username(), x + 36, y + 4, -1);
    icelFont.drawStringWithShadow(alt.email(), x + 36, y + 14, 0xFF9C9C9C);
    icelFont.drawStringWithShadow(viewable ? alt.password() : alt.password().replaceAll("[a-zA-Z0-9!-_]", "*"), x + 36, y + 24, 0xFF9C9C9C);

    GlStateManager.enableBlend();
    GlStateManager.color(1, 1, 1, 1);
    icelFont.drawStringWithShadow("x", x + 192, y, -1);
    icelFont.drawStringWithShadow(viewable ? "-" : "o", x + 192, y + 10, -1);
  }

  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    ScaledResolution sr = new ScaledResolution(mc);
    if (MouseUtils.isInArea(mouseX, mouseY, x + 192, y, 8, 8))
      selfDestruct = true;
    else if (MouseUtils.isInArea(mouseX, mouseY, x + 192, y + 10, 8, 8))
      viewable = !viewable;
    else if (MouseUtils.isInArea(mouseX, mouseY, x, y, 200, 38) && mouseButton == 0)
      altLoginExecutor.execute(alt::login);
  }

  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

  }

  public boolean shouldRemove() {
    return selfDestruct;
  }

}
