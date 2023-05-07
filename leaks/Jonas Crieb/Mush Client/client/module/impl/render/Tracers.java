package client.module.impl.render;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.render.Render3DEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.util.player.MathUtils;
import client.util.player.PlayerUtil;
import client.util.player.RenderUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

@ModuleInfo(name = "Tracers", description = "", category = Category.RENDER)
public class Tracers extends Module {
    @EventLink()
    public final Listener<Render3DEvent> onRender3D = event -> {
        for (Object o : this.mc.theWorld.loadedEntityList) {
            Entity entity = (Entity)o;
            RenderManager renderManager = this.mc.getRenderManager();
            if (entity.isEntityAlive() && entity instanceof net.minecraft.entity.player.EntityPlayer && entity != this.mc.thePlayer) {
                double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks -
                        renderManager.renderPosX;
                double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks -
                        renderManager.renderPosY;
                double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks-
                        renderManager.renderPosZ;
                boolean old = this.mc.gameSettings.viewBobbing;
                RenderUtil.startDrawing();
                this.mc.gameSettings.viewBobbing = false;
                this.mc.entityRenderer.setupCameraTransform(this.mc.timer.renderPartialTicks, 2);
                this.mc.gameSettings.viewBobbing = old;
                float color = (float)Math.round(255.0D - this.mc.thePlayer.getDistanceSqToEntity(entity) * 255.0D /
                        MathUtils.square(this.mc.gameSettings.renderDistanceChunks * 2.5D)) / 255.0F;
                (new double[3])[0] = 0.0D;
                (new double[3])[1] = 1.0D;
                (new double[3])[2] = 1.0D;
                (new double[3])[0] = color;
                (new double[3])[1] = (1.0F - color);
                (new double[3])[2] = 0.0D;
                drawLine(entity, PlayerUtil.isOnSameTeam((EntityLivingBase)entity) ? new double[3] : new double[3], posX, posY, posZ);
                RenderUtil.stopDrawing();
            }
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    };
    private void drawLine(Entity entity, double[] color, double x, double y, double z) {
        GL11.glEnable(2848);
        if (color.length >= 4) {
            if (color[3] <= 0.1D)
                return;
            GL11.glColor4d(color[0], color[1], color[2], color[3]);
        } else {
            GL11.glColor3d(color[0], color[1], color[2]);
        }
        GL11.glLineWidth(1.0F);
        GL11.glBegin(1);
        GL11.glVertex3d(0.0D, this.mc.thePlayer.getEyeHeight(), 0.0D);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GL11.glDisable(2848);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
