package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.opengl.GL11;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.Render3DEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.managers.ModuleManager;
import tech.dort.dortware.impl.modules.combat.FastBow;
import tech.dort.dortware.impl.modules.combat.KillAura;
import tech.dort.dortware.impl.modules.player.Derp;
import tech.dort.dortware.impl.modules.player.Nuker;
import tech.dort.dortware.impl.modules.player.Scaffold;
import tech.dort.dortware.impl.utils.render.ColorUtil;

import java.awt.*;

public class ZamboHat extends Module {

    private final NumberValue radius = new NumberValue("Radius", this, 1, 1, 3);

    public ZamboHat(ModuleData moduleData) {
        super(moduleData);
        register(radius);
    }

    @Subscribe
    public void onUpdate(Render3DEvent event) {
        final double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
        final double y = (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY) + mc.thePlayer.getEyeHeight() + 0.6;
        final double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

        if (mc.gameSettings.thirdPersonView == 0)
            return;

        GL11.glDisable(3553);
        GL11.glLineWidth(1);
        GL11.glBegin(3);

        for (int i = 0; i <= 10800; ++i) {
            final Color color = new Color(ColorUtil.getModeColor());
            GL11.glColor3f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x + (radius.getValue() / 2) * Math.cos(i * 6.283185307179586 / 5400), y - 0.3, z + (radius.getValue() / 2) * Math.sin(i * 6.283185307179586 / 5400));
        }


        GL11.glEnd();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }
}