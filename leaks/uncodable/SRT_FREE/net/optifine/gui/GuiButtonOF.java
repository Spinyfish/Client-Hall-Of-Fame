package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

@Exclude({Strategy.NAME_REMAPPING, Strategy.STRING_ENCRYPTION, Strategy.FLOW_OBFUSCATION, Strategy.NUMBER_OBFUSCATION, Strategy.REFERENCE_OBFUSCATION, Strategy.PARAMETER_OBFUSCATION})
public class GuiButtonOF extends GuiButton {
   public GuiButtonOF(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
      super(buttonId, x, y, widthIn, heightIn, buttonText);
   }

   public GuiButtonOF(int buttonId, int x, int y, String buttonText) {
      super(buttonId, x, y, buttonText);
   }
}
