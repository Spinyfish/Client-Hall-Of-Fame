package net.minecraft.client.gui;

import intent.AquaDev.aqua.Aqua;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import org.lwjgl.opengl.GL11;

public class GuiDisconnected extends GuiScreen {
   private String reason;
   private IChatComponent message;
   private List<String> multilineMessage;
   private final GuiScreen parentScreen;
   private int field_175353_i;

   public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
      this.parentScreen = screen;
      this.reason = I18n.format(reasonLocalizationKey);
      this.message = chatComp;
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), width - 50);
      this.field_175353_i = this.multilineMessage.size() * FontRenderer.FONT_HEIGHT;
      this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 + this.field_175353_i / 2 + FontRenderer.FONT_HEIGHT, I18n.format("gui.toMenu")));
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (!GL11.glGetString(7937).contains("AMD") && !GL11.glGetString(7937).contains("RX ")) {
         Aqua.INSTANCE.shaderBackgroundMM.renderShader();
      } else {
         this.drawDefaultBackground();
      }

      if (GuiConnecting.sentinelChecker) {
         this.mc
            .fontRendererObj
            .drawString("Staff Ban!", width / 2 - 22, height / 2 - this.field_175353_i / 2 - FontRenderer.FONT_HEIGHT * 2, Color.red.getRGB());
      } else {
         this.drawCenteredString(this.fontRendererObj, this.reason, width / 2, height / 2 - this.field_175353_i / 2 - FontRenderer.FONT_HEIGHT * 2, 11184810);
      }

      int i = height / 2 - this.field_175353_i / 2;
      if (this.multilineMessage != null) {
         for(String s : this.multilineMessage) {
            this.drawCenteredString(this.fontRendererObj, s, width / 2, i, 16777215);
            i += FontRenderer.FONT_HEIGHT;
         }
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
