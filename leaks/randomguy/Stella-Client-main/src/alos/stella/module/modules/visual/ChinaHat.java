package alos.stella.module.modules.visual;

import alos.stella.event.EventTarget;
import alos.stella.event.events.Render3DEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.value.FloatValue;
import alos.stella.value.IntegerValue;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "ChinaHat", description = "Renders a hat above the players head.", category = ModuleCategory.VISUAL)
public class ChinaHat extends Module {
    @EventTarget
    public void onUpdate(Render3DEvent event) {
        final double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
        final double y = (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY) + mc.thePlayer.getEyeHeight() + 0.6;

        final double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

        GL11.glDisable(3553);
        GL11.glLineWidth(1);
        GL11.glBegin(3);

        int quality = 360;

        for (int i = 0; i <= quality; ++i) {
            final Color color = new Color(255, 50, 50, 200);
            GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x + (1.7 / 2) * Math.cos(i * 6.283185307179586 / quality), y - 0.3, z + (1.7 / 2) * Math.sin(i * 6.283185307179586 / quality));
        }
        GL11.glEnd();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }
}
