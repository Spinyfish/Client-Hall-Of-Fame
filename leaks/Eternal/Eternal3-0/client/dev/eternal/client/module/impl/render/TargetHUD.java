package dev.eternal.client.module.impl.render;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventPostRenderGui;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.ui.targethud.EternalTargetHUD;
import dev.eternal.client.util.animate.Transition;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleInfo(
    name = "TargetHUD",
    description = "Displays information about the Killaura target.",
    category = Module.Category.RENDER)
public class TargetHUD extends Module {

  private final List<dev.eternal.client.ui.targethud.TargetHUD> targetHUDList = new ArrayList<>();

  @Subscribe
  public void onRender2D(EventPostRenderGui eventPostRenderGui) {
    final ScaledResolution sr = new ScaledResolution(mc);
    final float xPos = sr.getScaledWidth() / 2f + 5;
    final float yPos = sr.getScaledHeight() / 2f + 50;
    AtomicInteger atomicWidth = new AtomicInteger(0);
    AtomicInteger atomicHeight = new AtomicInteger(0);
    targetHUDList.removeIf(
        targetHUD ->
            targetHUD.transition().posX().getValue() == 0);
    targetHUDList.forEach(
        targetHUD -> {
          GlStateManager.pushMatrix();
          int width = atomicWidth.getAndIncrement();
          int height = atomicHeight.get();
          if (width != 0 && width % 3 == 0) {
            atomicWidth.set(0);
            height = atomicHeight.getAndIncrement();
          }

          final Transition transition = targetHUD.transition();

          if (targetHUD.target().isDead
              || mc.thePlayer.getDistanceToEntity(targetHUD.target()) > 15) {
            transition.transitionOut();
          } else transition.transitionIn();

          transition.centerX((int) targetHUD.hudPosition().getX() + 125 / 2);
          transition.centerY((int) targetHUD.hudPosition().getY() + 45 / 2);
          transition.scale();

          if (targetHUD.hudPosition().getY() == 0) {
            targetHUD.hudPosition().setX(xPos
                + (width * 130)
                - (Math.min(targetHUDList.size() - height * 4, 4f) * 130) / 2f);
            targetHUD.hudPosition().setY(yPos + height * 70);
          }

          targetHUD
              .hudPosition()
              .interpolate(
                  xPos
                      + (width * 130)
                      - (Math.min(targetHUDList.size() - height * 4, 4f) * 130) / 2f,
                  yPos + height * 70);
          targetHUD.render();
          GlStateManager.popMatrix();
        });
  }

  public void addTargetHUD(final EntityLivingBase entity) {
    if (!(entity instanceof EntityPlayer)) return;
    if (targetHUDList.stream()
        .map(dev.eternal.client.ui.targethud.TargetHUD::target)
        .toList()
        .contains(entity)) return;
    targetHUDList.add(new EternalTargetHUD(entity));
  }
}