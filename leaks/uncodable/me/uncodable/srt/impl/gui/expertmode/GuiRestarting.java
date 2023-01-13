package me.uncodable.srt.impl.gui.expertmode;

import me.uncodable.srt.impl.utils.Timer;
import net.minecraft.client.gui.GuiScreen;

public class GuiRestarting extends GuiScreen {
   private final Timer timer = new Timer();

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.fontRendererObj.drawStringWithShadow("Shutting down in three seconds (restart Minecraft)...", (float)(mouseX + 7), (float)(mouseY - 9), 16777215);
      if (this.timer.elapsed(3000L)) {
         this.mc.shutdown();
      }
   }

   @Override
   public void onGuiClosed() {
      this.mc.shutdown();
   }
}
