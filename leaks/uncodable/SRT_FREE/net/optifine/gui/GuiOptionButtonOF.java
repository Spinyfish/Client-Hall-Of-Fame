package net.optifine.gui;

import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.settings.GameSettings;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

@Exclude({Strategy.NAME_REMAPPING, Strategy.STRING_ENCRYPTION, Strategy.FLOW_OBFUSCATION, Strategy.NUMBER_OBFUSCATION, Strategy.REFERENCE_OBFUSCATION, Strategy.PARAMETER_OBFUSCATION})
public class GuiOptionButtonOF extends GuiOptionButton implements IOptionControl {
   private final GameSettings.Options option;

   public GuiOptionButtonOF(int id, int x, int y, GameSettings.Options option, String text) {
      super(id, x, y, option, text);
      this.option = option;
   }

   @Override
   public GameSettings.Options getOption() {
      return this.option;
   }
}
