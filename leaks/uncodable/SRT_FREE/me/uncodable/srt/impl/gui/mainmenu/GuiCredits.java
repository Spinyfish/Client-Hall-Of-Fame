package me.uncodable.srt.impl.gui.mainmenu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class GuiCredits extends GuiScreen {
   private static final String[] MAJOR_CREDITS = new String[]{
      "Tecnio - Helping me with GCD fix",
      "igs, imf, Lennox - Helping me with the Click GUI",
      "Dort - Giving me tips for the AntiGamingChair combat disabler",
      "cedo - Providing me assistance with a new Click GUI (added as a mode, to be finished)",
      "auth - Providing me details on how the full AntiGamingChair disabler functioned",
      "PhoenixHaven - Providing me with tips on how to make a latest AntiGamingChair speed bypass",
      "Rhys - Providing me with tips on how to properly disable BlocksMC",
      "Lemon - Making Lemon GUI (I used it as reference, NO CODE WAS TAKEN)"
   };
   private static final String[] MINOR_CREDITS = new String[]{
      "Hexeption - Releasing OptiFine source code",
      "ModCoderPack Team - Making this possible and accessible",
      "Rise Development Team - Finding a method for an AntiGamingChair fly (recreated from scratch, from watching a demo)",
      "saggy, Aidan - Helping me with the Lunar Client spoofer (no longer functioning)",
      "saggy - Helping me find the vector interception method (for Bypass II rotations)",
      "Dort, Aristhena - Providing utility classes",
      "Newb - Providing the event bus",
      "igs - Showing me Java comparators (I never used them until now)",
      "simon - Making my sprint bypass MinemenClub (providing me with EventLivingUpdate)",
      "Solastis - Helping with F5 animations (I had no idea where the fuck to look)",
      "Newb - Providing me with tips on where Minecraft hit-box code is",
      "Reddit - Literally giving me the WorldEdit crash command",
      "Tutorial's Point, trq, Aether - Actually showing me how to use GSON (never touched this shit in my life)",
      "Milse113, Easings - Providing Click GUI easing animations",
      "Milse113 - Helping me render ClickGUI animations by seconds instead of by frames"
   };

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      int y = 0;
      this.drawDefaultBackground();
      this.fontRendererObj.drawStringWithShadow("Credits", (float)this.width / 2.0F, 2.0F, 16777215);
      GlStateManager.pushMatrix();
      GlStateManager.scale(0.5, 0.5, 0.0);
      GlStateManager.translate((float)this.width - 60.0F, 24.0F, 0.0F);
      this.fontRendererObj.drawStringWithShadow("I wish I could just do this all by myself...", 0.0F, 0.0F, 16777215);
      GlStateManager.popMatrix();
      this.fontRendererObj.drawStringWithShadow("Major Credits:", 2.0F, 22.0F, 16777215);

      for(int i = 0; i < MAJOR_CREDITS.length; ++i) {
         this.fontRendererObj.drawStringWithShadow(MAJOR_CREDITS[i], 2.0F, (float)(y = 32 + 10 * i), 16777215);
      }

      y += 20;
      this.fontRendererObj.drawStringWithShadow("Minor Credits:", 2.0F, (float)y, 16777215);

      for(int i = 0; i < MINOR_CREDITS.length; ++i) {
         this.fontRendererObj.drawStringWithShadow(MINOR_CREDITS[i], 2.0F, (float)(y + 10 + 10 * i), 16777215);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void initGui() {
      this.buttonList.add(new GuiButton(Integer.MAX_VALUE, this.width - 110, this.height - 32, 98, 20, "Back"));
   }

   @Override
   protected void actionPerformed(GuiButton button) {
      if (button.id == Integer.MAX_VALUE) {
         this.mc.displayGuiScreen(new GuiMainMenu());
      }
   }
}
