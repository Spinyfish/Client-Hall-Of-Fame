package dev.eternal.client.module.impl.render.hud;

import com.google.common.util.concurrent.AtomicDouble;
import dev.eternal.client.Client;
import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventPostRenderGui;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.mode.Mode;
import dev.eternal.client.util.render.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;
import scheme.Scheme;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class VaziakMode extends Mode {

  private final List<Double> bpsList = new ArrayList<>(Arrays.asList(0d, 1d));
  private final TrueTypeFontRenderer fr =
      FontManager.getFontRenderer(FontType.ICIEL, 18);

  public VaziakMode(IToggleable toggleable, String name) {
    super(toggleable, name);
  }

  private double nigger;


  @Subscribe
  public void handleRender2D(EventPostRenderGui eventPostRenderGui) {
    final Scheme scheme = client.scheme();
    var sr = new ScaledResolution(mc);
    String name = Client.singleton().clientSettings().name();
    name = name.charAt(0) + "\247f" + name.substring(1);
    String watermark = name;
    GL11.glPushMatrix();
    nigger += 0.55f;
    GL11.glTranslated(45, 45, 0);
    GL11.glScaled(1, 1.3, 1);
    GL11.glRotated(nigger, 0, 0, 1);
    Gui.drawRect(1, 1, (int) (3 + fr.getWidth(watermark)), 14, 0x66000000);
    Gui.drawRect(1, 1, (int) (3 + fr.getWidth(watermark)) + 30, 2, scheme.getPrimary());
    fr.drawStringWithShadow(watermark, 2, 5, ColorUtil.pulse(scheme.getPrimary(), 10));
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glScaled(1, 1.3, 1);
    fr.drawStringWithShadow("fPS ; " + Minecraft.getDebugFPS(), 2, sr.getScaledHeight() - 180, new Color(255, 255, 0).getRGB());
    fr.drawStringWithShadow("bpS _ " + Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ), 2, sr.getScaledHeight() - 130, new Color(0, 255, 2).getRGB());
    GL11.glPopMatrix();

    AtomicDouble x = new AtomicDouble(sr.getScaledWidth());
    AtomicDouble y = new AtomicDouble();
    Client.singleton()
        .moduleManager()
        .getEnabledModules()
        .map(mod -> mod.moduleInfo().name() + mod.getSuffix())
        .sorted(Comparator.comparingDouble(Object::hashCode).reversed())
        .forEach(mname -> {
          GL11.glPushMatrix();
          x.getAndAdd(-115);
          GL11.glScaled(1.56, 1.356, 2.3);
          GL11.glTranslated(-(sr.getScaledWidth() / 2.76), 0, 0);
          GL11.glRotated(2, 0, 0, 1);
          Gui.drawRect(x.get() - 75, y.get(), x.get(), y.get() + 11, Color.blue.getRGB());
          Gui.drawRect(x.get() - 95, y.get(), x.get() - 75, y.get() + 11, Color.red.getRGB());
          Gui.drawRect(x.get(), y.get(), x.get() - fr.getWidth(mname) - 4,
              (float) y.get() + 4, new Color(135, 65, 8).getRGB());
          Gui.drawRect(x.get() - fr.getWidth(mname) - 4, y.get() + 4, x.get(),
              (float) y.get() + 8, new Color(18, 82, 28).getRGB());
          fr.drawStringWithShadow(mname, x.get() - fr.getWidth(mname) - 4, (float) y.get() + 4, new Color(131, 70, 0).getRGB());
          x.getAndAdd(115);
          y.getAndAdd(11);
          GL11.glPopMatrix();
        });
//    GL11.glRotated(1, 1, 2, 1);
//    GL11.glScaled(1.05, 1.45, 1);
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
