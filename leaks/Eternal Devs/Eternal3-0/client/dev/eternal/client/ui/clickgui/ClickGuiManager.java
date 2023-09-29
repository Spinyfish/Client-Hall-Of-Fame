package dev.eternal.client.ui.clickgui;

import dev.eternal.client.ui.clickgui.eternal.EternalClickGui;
import dev.eternal.client.ui.clickgui.hackinglord.HLClickGui;
import dev.eternal.client.ui.clickgui.primordial.PrimordialPanel;

import java.util.Arrays;
import java.util.List;

public class ClickGuiManager {

  private static final List<ClickGui> clickGuis = Arrays.asList(new PrimordialPanel(), new HLClickGui());

  public static ClickGui getClickGui(int index) {
    if (index >= clickGuis.size())
      throw new IndexOutOfBoundsException();

    return clickGuis.get(1);
  }


}
