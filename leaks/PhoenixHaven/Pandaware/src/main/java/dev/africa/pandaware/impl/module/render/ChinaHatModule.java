package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.ui.UISettings;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.awt.*;

@ModuleInfo(name = "China Hat", category = Category.VISUAL)
public class ChinaHatModule extends Module {
    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == RenderEvent.Type.RENDER_3D) {
            if (mc.gameSettings.thirdPersonView == 0) return;

            GlStateManager.pushAttribAndMatrix();
            double n = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks;
            double x = n - RenderManager.renderPosX;
            double n2 = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks;
            double y = n2 - RenderManager.renderPosY;
            double n3 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks;
            double z = n3 - RenderManager.renderPosZ;

            float pitch = mc.thePlayer.prevRenderPitchRotation + (mc.thePlayer.renderPitchRotation - mc.thePlayer.prevRenderPitchRotation);
            float yaw = mc.thePlayer.prevRotationYawHead + (mc.thePlayer.rotationYawHead - mc.thePlayer.prevRotationYawHead);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDepthMask(true);
            GL11.glLineWidth(1.3f);
            GL11.glRotatef(-yaw, 0.0f, mc.thePlayer.height, 0.0f);
            GL11.glTranslated(x, y + mc.thePlayer.height / 2.0f - 0.22499999403953552, z);
            float rotPitch = MathHelper.clamp_float(pitch, -90, 90);
            GL11.glRotated(-(90 - rotPitch), 1, 0.0f, 0);
            GL11.glTranslated(0.0, (Math.toRadians(rotPitch) / 2), 1.2 + (rotPitch > 0 ? -(Math.toRadians(rotPitch) / 3) : (Math.toRadians(rotPitch) / 4)));
            Cylinder shaft = new Cylinder();
            shaft.setDrawStyle(100013);
            Color color = new Color(UISettings.CURRENT_COLOR.getRGB());
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1.0f);
            shaft.draw(0.6f, 0, 0.32f, 45, 30);
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            GlStateManager.popAttribAndMatrix();
        }
    };
}