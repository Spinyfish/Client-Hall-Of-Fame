package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

public class GuiGameOver extends GuiScreen implements GuiYesNoCallback {
   private int enableButtonsTimer;

   @Override
   public void initGui() {
      this.buttonList.clear();
      if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
         if (this.mc.isIntegratedServerRunning()) {
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.deleteWorld")));
         } else {
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.leaveServer")));
         }
      } else {
         this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72, I18n.format("deathScreen.respawn")));
         this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.titleScreen")));
         if (this.mc.getSession() == null) {
            this.buttonList.get(1).enabled = false;
         }
      }

      for(GuiButton guibutton : this.buttonList) {
         guibutton.enabled = false;
      }
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) {
   }

   @Override
   protected void actionPerformed(GuiButton button) {
      switch(button.id) {
         case 0:
            this.mc.thePlayer.respawnPlayer();
            this.mc.displayGuiScreen(null);
            break;
         case 1:
            if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
               this.mc.displayGuiScreen(new GuiMainMenu());
            } else {
               GuiYesNo guiyesno = new GuiYesNo(
                  this, I18n.format("deathScreen.quit.confirm"), "", I18n.format("deathScreen.titleScreen"), I18n.format("deathScreen.respawn"), 0
               );
               this.mc.displayGuiScreen(guiyesno);
               guiyesno.setButtonDelay(20);
            }
      }
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      if (result) {
         this.mc.theWorld.sendQuittingDisconnectingPacket();
         this.mc.loadWorld(null);
         this.mc.displayGuiScreen(new GuiMainMenu());
      } else {
         this.mc.thePlayer.respawnPlayer();
         this.mc.displayGuiScreen(null);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
      GlStateManager.pushMatrix();
      GlStateManager.scale(2.0F, 2.0F, 2.0F);
      boolean flag = this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled();
      String s = flag ? I18n.format("deathScreen.title.hardcore") : I18n.format("deathScreen.title");
      this.drawCenteredString(this.fontRendererObj, s, this.width / 2 / 2, 30, 16777215);
      GlStateManager.popMatrix();
      if (flag) {
         this.drawCenteredString(this.fontRendererObj, I18n.format("deathScreen.hardcoreInfo"), this.width / 2, 144, 16777215);
      }

      this.drawCenteredString(
         this.fontRendererObj,
         I18n.format("deathScreen.score") + ": " + EnumChatFormatting.YELLOW + this.mc.thePlayer.getScore(),
         this.width / 2,
         100,
         16777215
      );
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      ++this.enableButtonsTimer;
      if (this.enableButtonsTimer == 20) {
         for(GuiButton guibutton : this.buttonList) {
            guibutton.enabled = true;
         }
      }
   }
}
