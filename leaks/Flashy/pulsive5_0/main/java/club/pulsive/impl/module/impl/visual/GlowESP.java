package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.render.Render3DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.ColorProperty;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.util.render.GlowRender;

import java.awt.*;

@ModuleInfo(name = "GlowESP", renderName = "GlowESP", description = "Adds a glow behind players.", aliases = "Camera", category = Category.VISUALS)

public class GlowESP extends Module {
    GlowRender glowRender = new GlowRender();
    public static DoubleProperty radius = new DoubleProperty("Radius", 1, 1, 50, 1);
    public static DoubleProperty alpha = new DoubleProperty("Transparency", 1, 1, 255, 1);
    public static ColorProperty color = new ColorProperty("Color", Color.white);
    
    @EventHandler
    private final Listener<Render3DEvent> render3DEventListener = event -> {
        mc.gameSettings.ofFastRender = false;
        glowRender.onRender(event);
    };
}
