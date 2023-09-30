package intentions.ui;

import intentions.modules.chat.Watermark;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiWatermark extends GuiScreen {
  private String status;
  
  private GuiTextField loginField;
  
  public void initGui() {
    this.status = "" + Watermark.watermarkText;
    int width = 100, height = 20, offset = 2;
    this.buttonList.add(new GuiButton(0, this.width / 2 - width / 2, this.height - height - offset, width, height, "Use"));
    this.buttonList.add(new GuiButton(1, this.width / 2 - width / 2, this.height - (height + offset) * 2, width, height, I18n.format("gui.back", new Object[0])));
    this.loginField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, this.height / 4, 200, 20);
    this.loginField.setFocused(true);
    this.loginField.setText("");
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    this.loginField.textboxKeyTyped(typedChar, keyCode);
    if (keyCode == 1)
      actionPerformed((GuiButton) this.buttonList.get(1)); 
    if (keyCode == 28)
      actionPerformed((GuiButton) this.buttonList.get(0)); 
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button.id == 0) {
      Watermark.watermarkText = this.loginField.getText();
      this.status = "" + Watermark.watermarkText;
    } else if (button.id == 1) {
      this.mc.displayGuiScreen(null);
    } 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    drawCenteredString(this.fontRendererObj, "Watermark", (this.width / 2), 10.0F, -1);
    drawCenteredString(this.fontRendererObj, "" + Watermark.watermarkText, (this.width / 2), 20.0F, -1);
    this.loginField.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  
  public void updateScreen() {
    this.loginField.updateCursorCounter();
  }
}
