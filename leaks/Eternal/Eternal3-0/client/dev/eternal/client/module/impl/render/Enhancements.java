package dev.eternal.client.module.impl.render;

import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.particle.ParticleData;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.NumberSetting;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@ModuleInfo(name = "Enhancements", category = Module.Category.RENDER, description = "The.")
public class Enhancements extends Module {


  private final NumberSetting particleTicks = new NumberSetting(this, "Particle ticks", 10, 1, 10, 1);
  private final BooleanSetting slowParticles = new BooleanSetting(this, "Slow Particles", true);
  private final BooleanSetting distanceBlur = new BooleanSetting(this, "Distance Blur", true);

  private final List<ParticleData> particleDataList = new CopyOnWriteArrayList<>();

  public Enhancements() {
  }

  @Override
  protected void onDisable() {
    particleDataList.clear();
  }
}
