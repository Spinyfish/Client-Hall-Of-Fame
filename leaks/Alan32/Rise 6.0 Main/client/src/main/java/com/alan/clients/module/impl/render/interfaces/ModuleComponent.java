package com.alan.clients.module.impl.render.interfaces;

import com.alan.clients.module.Module;
import com.alan.clients.util.vector.Vector2d;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public final class ModuleComponent {

    public Module module;
    public Vector2d position = new Vector2d(5000, 0), targetPosition = new Vector2d(5000, 0);
    public float animationTime;
    public String tag = "";
    public float nameWidth = 0, tagWidth;
    public Color color = Color.WHITE;
    public String translatedName = "";
    public ModuleComponent(final Module module) {
        this.module = module;
    }
}