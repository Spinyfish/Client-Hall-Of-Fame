package dev.eternal.client.particle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;

@AllArgsConstructor
@Getter
public class ParticleData {
  private Entity entity;
  private final EnumParticleTypes particleType;
  private final boolean ignoreRange;
  private final double x, y, z, xOffset, yOffset, zOffset;
  @Setter
  private int age;

}
