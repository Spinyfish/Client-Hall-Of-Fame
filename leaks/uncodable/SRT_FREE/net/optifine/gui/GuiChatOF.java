package net.optifine.gui;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

@Exclude({Strategy.NAME_REMAPPING, Strategy.STRING_ENCRYPTION, Strategy.FLOW_OBFUSCATION, Strategy.NUMBER_OBFUSCATION, Strategy.REFERENCE_OBFUSCATION, Strategy.PARAMETER_OBFUSCATION})
public class GuiChatOF extends GuiChat {
   public GuiChatOF(GuiChat guiChat) {
      super(GuiVideoSettings.getGuiChatText(guiChat));
   }

   @Override
   public void sendChatMessage(String msg) {
      if (this.checkCustomCommand(msg)) {
         this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
      } else {
         super.sendChatMessage(msg);
      }
   }

   private boolean checkCustomCommand(String msg) {
      if (msg == null) {
         return false;
      } else {
         msg = msg.trim();
         if (msg.equals("/reloadShaders")) {
            if (Config.isShaders()) {
               Shaders.uninit();
               Shaders.loadShaderPack();
            }

            return true;
         } else if (msg.equals("/reloadChunks")) {
            this.mc.renderGlobal.loadRenderers();
            return true;
         } else {
            return false;
         }
      }
   }
}
