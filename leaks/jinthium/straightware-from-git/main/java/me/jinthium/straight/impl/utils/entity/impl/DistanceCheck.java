package me.jinthium.straight.impl.utils.entity.impl;

import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.entity.ICheck;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public final class DistanceCheck implements ICheck {
    private final NumberSetting distance;

    public DistanceCheck(NumberSetting distance) {
        this.distance = distance;
    }

    @Override
    public boolean validate(Entity entity) {
        return Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity) <= distance.getValue().floatValue();
    }
}