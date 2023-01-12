package dev.eternal.client.ui.mainmenu;

import dev.eternal.client.Client;
import dev.eternal.client.ClientSettings;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.module.impl.render.ModuleBlur;
import dev.eternal.client.util.render.RenderUtil;
import dev.eternal.client.util.shader.Shader;

import java.io.IOException;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import scheme.Scheme;

/**
 * basic main menu class, change as needed.
 *
 * @author Dort & Eternal & (Sorta) Kalamiahu
 */
public class GUIMainMenu extends GuiScreen {

  private Shader shader;
  private final ModuleBlur blur;
  private final TrueTypeFontRenderer frSmall = FontManager.getFontRenderer(FontType.ICIEL, 20);
  private final TrueTypeFontRenderer label = FontManager.getFontRenderer(FontType.LEMONMILK_BOLD, 84);

  public GUIMainMenu() {
    blur = Client.singleton().moduleManager().getByClass(ModuleBlur.class);
    if(!blur.enabled()) blur.toggle();
  }

  public boolean doesGuiPauseGame() {
    return false;
  }

  protected void keyTyped(char typedChar, int keyCode) throws IOException {
  }

  public void initGui() {
    int j = this.height / 4 + 48;
    this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j, "Single Player"));
    this.buttonList.add(new GuiButton(1, this.width / 2 - 100, j + 24, "Multi Player"));
    this.buttonList.add(new GuiButton(2, this.width / 2 - 100, j + 48, "Account Manager"));
    this.buttonList.add(new GuiButton(3, this.width / 2 - 100, j + 72, "Settings"));
    this.buttonList.add(new GuiButton(4, this.width / 2 - 100, j + 96, "Exit"));

    shader = new Shader("menu/menu.glsl");
  }

  protected void actionPerformed(GuiButton button) throws IOException {
    switch (button.id) {
      case 0 -> this.mc.displayGuiScreen(new GuiSelectWorld(this));
      case 1 -> this.mc.displayGuiScreen(new GuiMultiplayer(this));
      case 2 -> mc.displayGuiScreen(Client.singleton().altManager());
      case 3 -> this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
      case 4 -> this.mc.shutdown();
    }
  }

  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    final ScaledResolution scaledResolution = new ScaledResolution(mc);

    drawShader(mouseX, mouseY);

    final ClientSettings clientSettings = Client.singleton().clientSettings();
    final int halfWidth = scaledResolution.getScaledWidth() / 2;
    int j = this.height / 4 + 48;

    final String nameVersion = String.format("%s - %s", clientSettings.name(), clientSettings.version());
    frSmall.drawString(nameVersion, 2, scaledResolution.getScaledHeight() - frSmall.getHeight(nameVersion), -1);

    blur.offset = 8;
    blur.quality = 3;
    blur.onRender2D(null);
    ModuleBlur.drawBlurredRect(halfWidth - 115, j - 15, halfWidth + 115, j + 131, -1);
    ModuleBlur.drawBlurredRect(5, 5, 115, 35, -1);
    blur.onPostRender2D(null);

    frSmall.drawStringWithShadow("Eternal", 35, 10, -1);
    frSmall.drawStringWithShadow("Logged in", 35, 20, -1);

    try {
      RenderUtil.drawPlayerFaceOther("null", 8, 8, 24, 24);
    } catch (IOException e) {
      e.printStackTrace();
    }

    drawLabel();
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  private void drawLabel() {
    final int halfWidth = width / 2;
    final int j = this.height / 4 + 48;
    label.drawCenteredString(Client.singleton().clientSettings().name(), halfWidth, j - 40, -1);
  }

  private void drawShader(int mouseX, int mouseY) {
    shader.useShader();
    shader.setUniform1f("time", (System.currentTimeMillis() - Client.singleton().initTime()) / 2000F);
    shader.setUniform2f("resolution", mc.displayWidth, mc.displayHeight);
    shader.setUniform2f("mouse", mouseX, mouseY);
    RenderUtil.drawRect(0, 0, mc.displayWidth, mc.displayHeight, -1);
    shader.stopShader();
  }

  public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  public void onGuiClosed() {

  }
}