package dev.eternal.client.module.impl.render;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventBlockingAnimation;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.MathHelper;

@ModuleInfo(name = "Animations", description = "Blockhit Animations.", category = Module.Category.RENDER)
public class Animations extends Module {

  public final EnumSetting<Mode> modeSetting = new EnumSetting<>(this, "Mode", Mode.values());

  private void blockTransformations() {
    GlStateManager.translate(-0.5F, 0.2F, 0.0F);
    GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
    GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
  }

  @Subscribe
  @SuppressWarnings("unused")
  public void handleAnimations(EventBlockingAnimation event) {
    event.cancelled(true);
    ItemRenderer itemRenderer = event.itemRenderer();
    var equipProgress = event.useProgress();
    var swingProgress = event.swingProgress();
    switch (modeSetting.value()) {
      case VANILLA -> itemRenderer.transformFirstPersonItem(event.useProgress(), event.swingProgress());
      case EXHIBITION -> {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f1 * -50, 1, 0, 1);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
      }
      case ETERNAL -> {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f1 * -60.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
      }
      case SLIDE -> {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f1 * -40.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
      }
    }
    blockTransformations();
    event.cancelled(true);
  }

  @Getter
  @AllArgsConstructor
  public enum Mode implements INameable {
    VANILLA("Vanilla"),
    EXHIBITION("Exhibition"),
    ETERNAL("Eternal"),
    SLIDE("Slide");
    private final String getName;
  }
}
