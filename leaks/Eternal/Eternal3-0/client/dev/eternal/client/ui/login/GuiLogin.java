package dev.eternal.client.ui.login;

import dev.eternal.client.Client;
import dev.eternal.client.ui.alts.GuiPasswordField;
import dev.eternal.client.ui.mainmenu.GUIMainMenu;
import dev.eternal.client.util.render.RenderUtil;
import dev.eternal.client.util.shader.impl.LoginMenuShader;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;

public class GuiLogin extends GuiScreen {

  private GuiTextField usernameField;
  private GuiPasswordField passwordField;
  private GuiButton login;

  private final LoginMenuShader loginMenuShader = new LoginMenuShader();

  @Override
  public void initGui() {
    usernameField = new GuiTextField(0, fontRendererObj, this.width / 2 - 100, this.height / 3 + 12, 200, 25);
    passwordField = new GuiPasswordField(1, fontRendererObj, this.width / 2 - 100, this.height / 3 + 48, 200, 25);
    this.buttonList.add(login = new GuiButton(2, this.width / 2 - 100, this.height / 3 + 84, 200, 25, "Login"));
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawShader();
    usernameField.drawTextBox();
    passwordField.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  private void drawShader() {
    RenderUtil.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0xFF000000);
    loginMenuShader.useShader();
    RenderUtil.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0xFF000000);
    loginMenuShader.stopShader();
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button.id == 2) {
      mc.displayGuiScreen(new GUIMainMenu());
    }
  }
}