package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.impl.events.IsOutlineEvent;
import de.lirium.impl.events.Render3DEvent;
import de.lirium.impl.events.ShaderRenderEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.world.BossInfo;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleFeature.Info(name = "Particle ESP", category = ModuleFeature.Category.VISUAL, description = "Highlight particles")
public class ParticleESPFeature extends ModuleFeature {

    @Value(name = "Color Mode")
    private final ComboBox<String> colorMode = new ComboBox<>("White", new String[]{"Texture"});

    @EventHandler
    public final Listener<ShaderRenderEvent> shaderRenderEventListener = e -> {
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, getTexParam());
        GL11.glColor4f(1F, 1F, 1F, 1F);
        mc.effectRenderer.renderParticles(getPlayer(), mc.timer.partialTicks, mc.particleTimer.partialTicks);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
    };

    private int getTexParam() {
        switch (colorMode.getValue()) {
            case "White":
                return GL11.GL_ADD;
            case "Texture":
                return GL11.GL_REPLACE;
        }
        return GL11.GL_ADD;
    }
}
