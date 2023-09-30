package dev.eternal.client.module.impl.render.hud;

import dev.eternal.client.Client;
import dev.eternal.client.event.Subscribe;;
import com.google.common.util.concurrent.AtomicDouble;
import dev.eternal.client.event.events.EventPostRenderGui;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.module.impl.render.ModuleBlur;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.render.ColorUtil;
import dev.eternal.client.util.render.RenderUtil;
import dev.eternal.client.util.shader.impl.RainbowShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.EnumChatFormatting;
import scheme.Scheme;

import java.util.*;

public class EternalMode extends Mode {

  private final List<Double> bpsList = new ArrayList<>(Arrays.asList(0d, 1d));
  private final TrueTypeFontRenderer fr = FontManager.getFontRenderer(FontType.LEMONMILK_BOLD, 18);

  public EternalMode(IToggleable toggleable, String name) {
    super(toggleable, name);
  }

  @Subscribe
  public void handleRender2D(EventPostRenderGui eventPostRenderGui) {
    double bps = bpsList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    final Scheme scheme = client.scheme();
    var sr = new ScaledResolution(mc);

    String name = client.clientSettings().name();
    name = name.charAt(0) + "\u00A7f" + name.substring(1);
    String watermark =
        String.format("%s - %s", name, client.clientSettings().version());
    fr.drawStringWithShadow(watermark, 5, 5, ColorUtil.pulse(scheme.getPrimary(), 10));

    fr.drawStringWithShadow("FPS" + EnumChatFormatting.GRAY + ": " + EnumChatFormatting.WHITE + Minecraft.getDebugFPS(), 5, 15, ColorUtil.pulse(scheme.getPrimary(), 10));

    AtomicDouble y = new AtomicDouble();
    client.moduleManager()
        .getEnabledModules()
        .map(
            mod ->
                mod.moduleInfo().name()
                    + (mod.getSuffix().isEmpty() ? "" : " \2477- " + mod.getSuffix()))
        .sorted(Comparator.comparingDouble(m -> fr.getWidth((String) m)).reversed())
        .forEach(
            mname -> {
              ModuleBlur.drawBlurredRect(
                  sr.getScaledWidth() - (int)fr.getWidth(mname) - 6,
                  (int)y.get() + 5,
                  sr.getScaledWidth() - 4,
                  (int)y.get() + 16,
                  0);
              fr.drawStringWithShadow(
                  mname,
                  sr.getScaledWidth() - fr.getWidth(mname) - 5,
                  (float) y.get() + 5,
                  ColorUtil.pulse(
                      scheme.getPrimary(),
                      (int)
                          ((int) y.get()
                              * client.moduleManager().getEnabledModules().count())));
              y.set(y.get() + 11);
            });
    GlStateManager.color(1, 1, 1, 1);
  }

  @Subscribe
  public void onMove(EventUpdate eventUpdate) {
    double xDist = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
    double zDist = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
    double bps = Math.sqrt(xDist * xDist + zDist * zDist) * 20;
    bpsList.add(bps);
    while (bpsList.size() > 20) bpsList.remove(0);
  }
}
