package dev.eternal.client.ui.clickgui.hackinglord.panel;

import com.google.common.collect.Lists;
import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.ui.clickgui.hackinglord.HLClickGui;
import dev.eternal.client.ui.clickgui.hackinglord.component.pane.HLPane;
import dev.eternal.client.util.animate.Animate;
import dev.eternal.client.util.client.MouseUtils;
import dev.eternal.client.util.render.RenderUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import scheme.Scheme;

import java.awt.*;
import java.util.*;
import java.util.List;

@Setter
@Getter
public abstract class HLPanel {

  protected double x, y;
  protected String name;
  protected double width = HLClickGui.WIDTH, height = 150;
  protected double prevX, prevY;
  protected boolean dragging;
  protected double scroll;
  protected final HLClickGui hlClickGui;
  protected final Minecraft mc = Minecraft.getMinecraft();
  protected final Scheme scheme = Client.singleton().scheme();
  protected final TrueTypeFontRenderer fr = FontManager.getFontRenderer(FontType.ROBOTO_MEDIUM, 16);
  protected final Animate scrollAnim = new Animate(0, 1);
  protected final List<HLPane> panes = new ArrayList<>();

  public HLPanel(HLClickGui hlClickGui) {
    this.hlClickGui = hlClickGui;
  }

  protected void render(int mouseX, int mouseY) {
    setHeight();
    final ScaledResolution sr = new ScaledResolution(mc);
    final Scheme scheme = Client.singleton().scheme();
    final double modHeight = panes.stream().mapToDouble(HLPane::getHeight).sum() + 13;

    if (MouseUtils.isInArea(mouseX, mouseY, x, y, width, height)) {
      scroll += (Mouse.getDWheel() / 10f);
    }

    x = MathHelper.clamp_double(x, 2, sr.getScaledWidth() - width - 2);
    y = MathHelper.clamp_double(y, 2, sr.getScaledHeight() - 14);

    scroll = MathHelper.clamp_double(scroll, height - modHeight, 0);
    scrollAnim.interpolate(scroll);

    RenderUtil.drawRoundedRect(x, y, x + width, y + height, 4, scheme.getScrim());
    RenderUtil.drawRoundedRect(x + 0.5, y + 0.5, x + width - 0.5, y + height - 0.5, 4, scheme.getInverseOnSurface());
    RenderUtil.drawRoundedRect(x + 0.5, y + 0.5, x + width - 0.5, y + 12, 4, scheme.getScrim());
    Gui.drawRect(x, y + 8, x + width, y + 12, scheme.getScrim());
    fr.drawString(name, x + 2, y + 3, -1);

    handleCollision();

    RenderUtil.prepareScissorBox(x, y + 12, x + width, y + height);
    GL11.glEnable(3089);
    double offset = 12;
    for (HLPane pane : panes) {
      if (!pane.visible()) continue;
      final double yPos = y + offset + scrollAnim.getValue();
      if (yPos < y + height)
        pane.drawPane(x, yPos, mouseX, mouseY);
      offset += pane.getHeight();
    }
    GL11.glDisable(3089);

    if (dragging) {
      this.x += (mouseX - prevX);
      this.y += (mouseY - prevY);
    }

    prevX = mouseX;
    prevY = mouseY;
  }

  public abstract void drawPanel(int mouseX, int mouseY);

  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (MouseUtils.isInArea(mouseX, mouseY, x, y, width, 12)) {
      return dragging = true;
    }
    double offset = 12;
    for (HLPane pane : Lists.reverse(panes)) {
      if (!pane.visible()) continue;
      final double yPos = y + offset + scrollAnim.getValue();
      if (yPos < y + height && yPos >= y)
        if (pane.mouseClicked(mouseX, mouseY, mouseButton)) return true;
      offset += pane.getHeight();
    }
    return false;
  }

  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    dragging = false;
    panes.forEach(abstractHLPane -> abstractHLPane.mouseReleased(mouseX, mouseY, mouseButton));
  }

  public void keyTyped(char typedChar, int keyCode) {
    panes.forEach(hlPane -> hlPane.keyTyped(typedChar, keyCode));
  }

  public double getHeight() {
    return height;
  }

  private void setHeight() {
    height = panes.stream().filter(HLPane::visible).mapToDouble(HLPane::getHeight).sum() + 13;

    Optional<HLPanel> optionalHLCategoryPanel = hlClickGui.panels().stream().
        filter(abstractHLPanel -> abstractHLPanel != this)
        .filter(abstractHLPanel ->
            MouseUtils.isInArea(abstractHLPanel.x + 1, abstractHLPanel.y, x, y, width, height)
                || MouseUtils.isInArea(abstractHLPanel.x - 1, abstractHLPanel.y, x, y, width, height)
                || MouseUtils.isInArea(abstractHLPanel.x + width + 1, abstractHLPanel.y, x, y, width, height)
                || MouseUtils.isInArea(abstractHLPanel.x + width - 1, abstractHLPanel.y, x, y, width, height)
        ).min(Comparator.comparingDouble(value -> value.y));

    optionalHLCategoryPanel.ifPresent(abstractHLPanel -> height = abstractHLPanel.y - y - 2);

    height = Math.max(height, 13);
    height = Math.min(height, hlClickGui.height - 2 - y);
  }

  private void handleCollision() {
    if (hlClickGui.panels().stream().anyMatch(hlPanel -> hlPanel.dragging)) return;
    hlClickGui.panels().stream()
        .filter(hlPanel -> !equals(hlPanel))
        .sorted(Comparator.comparingDouble(value -> ((HLPanel) value).y).reversed())
        .forEach(hlPanel -> {
          final Rectangle rectangle = hlPanel.rectangle();
          if (rectangle().intersects(rectangle)) {
            this.y = hlPanel.y + hlPanel.height + 2;
          }
        });
  }

  public Rectangle rectangle() {
    return new Rectangle((int) this.x, (int) this.y, (int) this.width, 12);
  }

}
