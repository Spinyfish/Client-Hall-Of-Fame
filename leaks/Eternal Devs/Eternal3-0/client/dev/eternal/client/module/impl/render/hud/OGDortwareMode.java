package dev.eternal.client.module.impl.render.hud;

import dev.eternal.client.event.Subscribe;;
import com.google.common.util.concurrent.AtomicDouble;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventPostRenderGui;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.math.MathUtil;
import dev.eternal.client.util.render.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class OGDortwareMode extends Mode {

  public OGDortwareMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleRender2D(EventPostRenderGui eventPostRenderGui) {
    if (mc.gameSettings.showDebugInfo)
      return;
    var bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * 20) * mc.timer.timerSpeed;
    var clientSettings = client.clientSettings();
    var fontRenderer = mc.fontRendererObj;
    var displayY = 0;

    var displayInfo = new String[]{
        String.format("\247f%s (\247c%s\247f)", clientSettings.name(), clientSettings.version()),
        formatInfoText("X", Double.toString(MathUtil.roundToPlace(mc.thePlayer.posX, 2))),
        formatInfoText("Y", Double.toString(MathUtil.roundToPlace(mc.thePlayer.posY, 2))),
        formatInfoText("Z", Double.toString(MathUtil.roundToPlace(mc.thePlayer.posZ, 2))),
        formatInfoText("FPS", Integer.toString(Minecraft.getDebugFPS())),
        formatInfoText("BPS", Double.toString(MathUtil.roundToPlace(bps, 2)))
    };

    for (var info : displayInfo) {
      fontRenderer.drawStringWithShadow(info, 2, displayY, -1);
      displayY += fontRenderer.FONT_HEIGHT;
    }

    int x = eventPostRenderGui.scaledResolution().getScaledWidth();

    AtomicDouble y = new AtomicDouble();
    Client.singleton().moduleManager().stream()
        .filter(IToggleable::isEnabled)
        .map(mod -> mod.moduleInfo().name() + (mod.getSuffix().isEmpty() ? "" : "\2477 " + mod.getSuffix()))
        .sorted(this::compare)
        .forEach(name -> {
          Gui.drawRect(x, y.get() + fontRenderer.FONT_HEIGHT, x - fontRenderer.getStringWidth(name) - 5, y.get(), new Color(0, 0, 0, 99).getRGB());
          Gui.drawRect(x - 2, y.get() + fontRenderer.FONT_HEIGHT, x, y.get(), ColorUtil.getColor(ColorUtil.ColorMode.RAINBOW_SIMPLE, Color.WHITE, (int) y.get()));
          fontRenderer.drawStringWithShadow(name, x - fontRenderer.getStringWidth(name) - 3, (float) y.get(), ColorUtil.getColor(ColorUtil.ColorMode.RAINBOW_SIMPLE, Color.WHITE, (int) y.get()));
          y.set(y.get() + fontRenderer.FONT_HEIGHT);
        });
  }

  /**
   * Formats things such as on-screen coordinates, etc.
   */
  public String formatInfoText(String name, String value) {
    return String.format("\247f(\247c%s\247f): \2477%s", name, value);
  }

  private int compare(String par0, String par1) {
    var fontRenderer = mc.fontRendererObj;
    return fontRenderer.getStringWidth(par1) - fontRenderer.getStringWidth(par0);
  }

}
