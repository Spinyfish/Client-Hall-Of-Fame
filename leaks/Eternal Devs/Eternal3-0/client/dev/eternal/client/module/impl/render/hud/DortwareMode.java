package dev.eternal.client.module.impl.render.hud;

import dev.eternal.client.event.Subscribe;;
import com.google.common.util.concurrent.AtomicDouble;
import dev.eternal.client.event.events.EventPostRenderGui;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.module.impl.render.HUD;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.render.ColorUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.text.DecimalFormat;

public class DortwareMode extends Mode {

  public DortwareMode(IToggleable owner, String name) {
    super(owner, name);
  }

  @Subscribe
  public void handleRender2D(EventPostRenderGui eventPostRenderGui) {
    var bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * 20) * mc.timer.timerSpeed;
    var clientSettings = client.clientSettings();
    var color = new Color(255, 50, 50).getRGB();
    var hud = this.<HUD>getOwner();
    var large = hud.large();
    var normal = hud.normal();
    large.drawStringWithShadow(String.join(" ", "E\247fternal", clientSettings.version()), 5, 5, color);
    var fps = "FPS: " + Minecraft.getDebugFPS();
    normal.drawStringWithShadow(fps, 2, eventPostRenderGui.scaledResolution().getScaledHeight() - large.getHeight(fps), color);
    normal.drawStringWithShadow("BPS: " + DecimalFormat.getIntegerInstance().format(bps), 2, eventPostRenderGui.scaledResolution().getScaledHeight() - 24, color);
    int x = eventPostRenderGui.scaledResolution().getScaledWidth();
    AtomicDouble y = new AtomicDouble();
    client.moduleManager().stream()
        .filter(IToggleable::isEnabled)
        .map(mod -> mod.moduleInfo().name() + (mod.getSuffix().isEmpty() ? "" : " \2477- " + mod.getSuffix()))
        .sorted(this::compare)
        .forEach(name -> {
          normal.drawStringWithShadow(name, x - normal.getWidth(name), (float) y.get(), ColorUtil.getColor(ColorUtil.ColorMode.RAINBOW_SIMPLE, hud.colorSetting().value(), (int) y.get() + 1));
          y.set(y.get() + normal.getHeight(name) + 2);
        });
  }

  private int compare(String par0, String par1) {
    HUD hud = this.getOwner();
    TrueTypeFontRenderer normal = hud.normal();
    return (int) (normal.getWidth(par1) - normal.getWidth(par0));
  }
}
