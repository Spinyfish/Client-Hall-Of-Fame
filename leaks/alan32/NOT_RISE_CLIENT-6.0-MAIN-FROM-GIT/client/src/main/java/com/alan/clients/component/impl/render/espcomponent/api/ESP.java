package com.alan.clients.component.impl.render.espcomponent.api;

import com.alan.clients.module.impl.combat.KillAura;
import com.alan.clients.module.impl.combat.TeleportAura;
import com.alan.clients.util.interfaces.InstanceAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class  ESP implements InstanceAccess {
    public ESPColor espColor;
    public Entity target;
    public int tick;

    public ESP(ESPColor espColor) {
        this.espColor = espColor;
        tick = mc.thePlayer.ticksExisted;
    }

    public void render2D() {

    }

    public void render3D() {

    }

    public void updateTargets() {
        target = getModule(KillAura.class).target;
        TeleportAura teleportAura = getModule(TeleportAura.class);
        if (teleportAura.isEnabled()) target = getModule(TeleportAura.class).target;
    }

    public Color getColor(EntityLivingBase entity) {
        Color color = espColor.getNormalColor();

        if (entity == null) return color;

        if (entity.hurtTime > 0) {
            color = espColor.getDamageColor();
        } else if (target == entity) {
            color = espColor.getTargetColor();
        }

        return color;
    }
}
