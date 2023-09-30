package felix.module.impl.visuals;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import felix.api.annotations.Handler;
import felix.events.render.EventRender3D;
import felix.module.Module;
import felix.util.other.MathUtils;
import felix.util.other.PlayerUtil;
import felix.util.visual.RenderUtil;

public class Tracers extends Module {

    public Tracers() {
        super("Tracers", 0, ModuleCategory.VISUALS);
    }

    @Handler
    public void onRender3D(final EventRender3D event) {
        for (Object o : mc.theWorld.loadedEntityList) {
            Entity entity = (Entity) o;
            final RenderManager renderManager = mc.getRenderManager();
            if ((entity.isEntityAlive() && entity instanceof EntityPlayer && entity != mc.thePlayer)) {
                final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks()
                        - renderManager.getRenderPosX();
                final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks()
                        - renderManager.getRenderPosY();
                final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks()
                        - renderManager.getRenderPosZ();
                boolean old = mc.gameSettings.viewBobbing;
                RenderUtil.startDrawing();
                mc.gameSettings.viewBobbing = false;
                mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
                mc.gameSettings.viewBobbing = old;
                float color = Math.round(255D - mc.thePlayer.getDistanceSqToEntity(entity) * 255D
                        / MathUtils.square(mc.gameSettings.renderDistanceChunks * 2.5)) / 255F;
                drawLine(entity, PlayerUtil.isOnSameTeam((EntityLivingBase) entity) ? new double[]{0, 1, 1} : new double[]{color, 1 - color, 0}, posX, posY, posZ);
                RenderUtil.stopDrawing();
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
    }

    private void drawLine(Entity entity, double[] color, double x, double y, double z) {
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        if (color.length >= 4) {
            if (color[3] <= 0.1) {
                return;
            }
            GL11.glColor4d(color[0], color[1], color[2], color[3]);
        } else {
            GL11.glColor3d(color[0], color[1], color[2]);
        }
        GL11.glLineWidth(1);
        GL11.glBegin(1);
        GL11.glVertex3d(0.0D, mc.thePlayer.getEyeHeight(), 0.0D);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }
    
    public void onEnable() {
    	super.onEnable();
    }
    
    public void onDisable() {
    	super.onDisable();
    }
}
