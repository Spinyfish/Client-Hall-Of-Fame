package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.transform.Transformation;
import de.lirium.impl.events.Render2DEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.StencilUtil;
import god.buddy.aot.BCompiler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.atomic.AtomicBoolean;

@ModuleFeature.Info(name = "Galaxy", description = "Der Sieg steht in den Sternen", category = ModuleFeature.Category.VISUAL)
public class GalaxyFeature extends ModuleFeature {

    @Value(name = "Blur")
    final CheckBox blur = new CheckBox(false);

    private final Transformation transformationUtil = new Transformation();

    @EventHandler
    public final Listener<Render2DEvent> render2DEventListener = e -> renderGalaxy();

    private void renderGalaxy() {
        final ScaledResolution resolution = new ScaledResolution(mc);
        mc.getRenderManager().setRenderOutlines(true);
        transformationUtil.collect();
        final AtomicBoolean empty = new AtomicBoolean(true);

        getWorld().loadedEntityList.forEach(entity -> {
            if ((getPlayer() == entity && getGameSettings().thirdPersonView != 0)) {
                mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false);
                empty.set(false);
            }
        });

        if (!empty.get()) {
            StencilUtil.init();
            transformationUtil.draw();
            StencilUtil.readBuffer(1);
            RenderUtil.drawEndPortal(resolution);
            GlStateManager.enableLighting();
            if(blur.getValue())
                RenderUtil.drawAcrylicBlur();
            StencilUtil.uninit();
        } else {
            mc.getFramebuffer().bindFramebuffer(true);
            mc.entityRenderer.setupOverlayRendering();
        }
        mc.getRenderManager().setRenderOutlines(false);
        transformationUtil.release();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }
}
