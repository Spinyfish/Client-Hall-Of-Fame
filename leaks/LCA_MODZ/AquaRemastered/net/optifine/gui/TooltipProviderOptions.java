package net.optifine.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.optifine.Lang;

public class TooltipProviderOptions implements TooltipProvider {
   @Override
   public Rectangle getTooltipBounds(GuiScreen guiScreen, int x, int y) {
      int i = GuiScreen.width / 2 - 150;
      int j = GuiScreen.height / 6 - 7;
      if (y <= j + 98) {
         j += 105;
      }

      int k = i + 150 + 150;
      int l = j + 84 + 10;
      return new Rectangle(i, j, k - i, l - j);
   }

   @Override
   public boolean isRenderBorder() {
      return false;
   }

   @Override
   public String[] getTooltipLines(GuiButton btn, int width) {
      if (!(btn instanceof IOptionControl)) {
         return null;
      } else {
         IOptionControl ioptioncontrol = (IOptionControl)btn;
         GameSettings.Options gamesettings$options = ioptioncontrol.getOption();
         return getTooltipLines(gamesettings$options.getEnumString());
      }
   }

   public static String[] getTooltipLines(String key) {
      List<String> list = new ArrayList<>();

      for(int i = 0; i < 10; ++i) {
         String s = key + ".tooltip." + (i + 1);
         String s1 = Lang.get(s, (String)null);
         if (s1 == null) {
            break;
         }

         list.add(s1);
      }

      return list.size() <= 0 ? null : list.toArray(new String[list.size()]);
   }
}
