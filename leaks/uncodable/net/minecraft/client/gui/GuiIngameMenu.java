package net.minecraft.client.gui;

import me.uncodable.srt.impl.gui.expertmode.GuiRestarting;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.resources.I18n;

public class GuiIngameMenu extends GuiScreen {
   @Override
   public void initGui() {
      this.buttonList.clear();
      int i = -16;
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + i, I18n.format("menu.returnToMenu")));
      if (!this.mc.isIntegratedServerRunning()) {
         this.buttonList.get(0).displayString = I18n.format("menu.disconnect");
      }

      this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + i, I18n.format("menu.returnToGame")));
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + i, 98, 20, I18n.format("menu.options")));
      GuiButton guibutton;
      this.buttonList.add(guibutton = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + i, 98, 20, I18n.format("menu.shareToLan")));
      this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + i, 98, 20, I18n.format("gui.achievements")));
      this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + i, 98, 20, I18n.format("gui.stats")));
      this.buttonList
         .add(
            new GuiButton(
               8, this.width / 2 - 100, this.height / 4 + 72 + i, String.format("%s Expert Mode", this.mc.gameSettings.expertMode ? "Disable" : "Enable")
            )
         );
      guibutton.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
   }

   @Override
   protected void actionPerformed(GuiButton button) {
      switch(button.id) {
         case 0:
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            break;
         case 1:
            boolean flag = this.mc.isIntegratedServerRunning();
            button.enabled = false;
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
            if (flag) {
               this.mc.displayGuiScreen(new GuiMainMenu());
            } else {
               this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
            }
         case 2:
         case 3:
         default:
            break;
         case 4:
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
            break;
         case 5:
            this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
            break;
         case 6:
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
            break;
         case 7:
            this.mc.displayGuiScreen(new GuiShareToLan(this));
            break;
         case 8:
            this.mc.gameSettings.expertMode = !this.mc.gameSettings.expertMode;
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiRestarting());
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, I18n.format("menu.game"), this.width / 2, 40, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
