package dev.eternal.client.ui.alt;

import dev.eternal.client.Client;
import dev.eternal.client.util.client.Alt;
import dev.eternal.client.util.client.SessionChanger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;

public class GuiAddAccount extends GuiScreen {

  private GuiTextField username;
  private GuiTextField password;
  private GuiButton login;

  @Override
  public void initGui() {
    username = new GuiTextField(0, fontRendererObj, this.width / 2 - 100, this.height / 3, 200, 25);
    password = new GuiTextField(1, fontRendererObj, this.width / 2 - 100, this.height / 3 + 24, 200, 25);
    this.buttonList.add(login = new GuiButton(2, this.width / 2 - 100, this.height / 3 + 48, "Add account"));
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    username.drawTextBox();
    password.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    this.password.mouseClicked(mouseX, mouseY, mouseButton);
    this.username.mouseClicked(mouseX, mouseY, mouseButton);

    if (login.mousePressed(mc, mouseX, mouseY)) {
      if (password.getText().equals("") && username.getText().length() > 2) {
        SessionChanger.getInstance().setUserOffline(username.getText());
      } else {
        Client.singleton().altManager().alts().add(new AltPart(new Alt(username.getText(), password.getText())));
      }
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (keyCode == 1)
      mc.displayGuiScreen(Client.singleton().altManager());
    if (this.password.isFocused()) {
      this.password.textboxKeyTyped(typedChar, keyCode);
    }
    if (this.username.isFocused()) {
      this.username.textboxKeyTyped(typedChar, keyCode);
    }
  }
}
