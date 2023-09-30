package club.pulsive.impl.util.entity.impl;

import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.util.entity.ICheck;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public final class DistanceCheck implements ICheck {
    private final DoubleProperty distance;

    public DistanceCheck(DoubleProperty distance) {
        this.distance = distance;
    }

    @Override
    public boolean validate(Entity entity) {
        return Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity) <= distance.getValue().floatValue();
    }
}

