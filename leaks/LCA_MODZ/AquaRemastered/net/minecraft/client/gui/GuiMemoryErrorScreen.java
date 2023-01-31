package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;

public class GuiMemoryErrorScreen extends GuiScreen {
   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiOptionButton(0, width / 2 - 155, height / 4 + 120 + 12, I18n.format("gui.toTitle")));
      this.buttonList.add(new GuiOptionButton(1, width / 2 - 155 + 160, height / 4 + 120 + 12, I18n.format("menu.quit")));
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.mc.displayGuiScreen(new GuiMainMenu());
      } else if (button.id == 1) {
         this.mc.shutdown();
      }
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, "Out of memory!", width / 2, height / 4 - 60 + 20, 16777215);
      this.drawString(this.fontRendererObj, "Minecraft has run out of memory.", width / 2 - 140, height / 4 - 60 + 60 + 0, 10526880);
      this.drawString(this.fontRendererObj, "This could be caused by a bug in the game or by the", width / 2 - 140, height / 4 - 60 + 60 + 18, 10526880);
      this.drawString(this.fontRendererObj, "Java Virtual Machine not being allocated enough", width / 2 - 140, height / 4 - 60 + 60 + 27, 10526880);
      this.drawString(this.fontRendererObj, "memory.", width / 2 - 140, height / 4 - 60 + 60 + 36, 10526880);
      this.drawString(this.fontRendererObj, "To prevent level corruption, the current game has quit.", width / 2 - 140, height / 4 - 60 + 60 + 54, 10526880);
      this.drawString(this.fontRendererObj, "We've tried to free up enough memory to let you go back to", width / 2 - 140, height / 4 - 60 + 60 + 63, 10526880);
      this.drawString(
         this.fontRendererObj, "the main menu and back to playing, but this may not have worked.", width / 2 - 140, height / 4 - 60 + 60 + 72, 10526880
      );
      this.drawString(this.fontRendererObj, "Please restart the game if you see this message again.", width / 2 - 140, height / 4 - 60 + 60 + 81, 10526880);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
