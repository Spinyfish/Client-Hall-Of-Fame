package Reality.Realii.mods.modules.render;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.rendering.EventRender2D;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class ScreenRader extends Module {
    Option players = new Option("Players","Players", true);
    Option invisibles = new Option("Invisibles","Invisibles", false);

    public ScreenRader() {
        super("ScreenRader", ModuleType.Render);

        this.addValues(players, invisibles);
    }

    @EventHandler
    public void onRender(EventRender2D e) {
        ScaledResolution a = new ScaledResolution(mc);
        GlStateManager.pushMatrix();
        double size = 50;
        double xOffset = a.getScaledWidth() / 2 - 24.5;
        double yOffset = a.getScaledHeight() / 2 - 25.2;
        double playerOffsetX = mc.thePlayer.posX;
        double playerOffSetZ = mc.thePlayer.posZ;
        for (int i = 0; i < mc.theWorld.loadedEntityList.size(); ++i) {
            Entity gay = mc.theWorld.loadedEntityList.get(i);
            if (((((Boolean) players.getValue()) && (gay instanceof EntityPlayer && gay != mc.thePlayer))) && (((Boolean) invisibles.getValue()) || !gay.isInvisible())) {
                double loaddist = 0.2;
                float pTicks = mc.timer.renderPartialTicks;
                double pos1 = (((gay.posX + (gay.posX - gay.lastTickPosX) * pTicks) - playerOffsetX) * loaddist);
                double pos2 = (((gay.posZ + (gay.posZ - gay.lastTickPosZ) * pTicks) - playerOffSetZ) * loaddist);
                double cos = Math.cos(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                double sin = Math.sin(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                double rotY = -(pos2 * cos - pos1 * sin);
                double rotX = -(pos1 * cos + pos2 * sin);
                double var7 = 0 - rotX;
                double var9 = 0 - rotY;
                if (MathHelper.sqrt_double(var7 * var7 + var9 * var9) < size / 2 - 4) {
                    float angle = (float) (Math.atan2(rotY - 0, rotX - 0) * 180 / Math.PI);
                    double x = ((size / 2) * Math.cos(Math.toRadians(angle))) + xOffset + size / 2;
                    double y = ((size / 2) * Math.sin(Math.toRadians(angle))) + yOffset + size / 2;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x, y, 0);
                    GlStateManager.rotate(angle, 0, 0, 1);
                    GlStateManager.scale(1.5, 1.0, 1.0);
                    drawESPCircle(0, 0, 3.2, 3);
                    drawESPCircle(0, 0, 3.0, 3);
                    drawESPCircle(0, 0, 2.5, 3);
                    drawESPCircle(0, 0, 2.0, 3);
                    drawESPCircle(0, 0, 1.5, 3);
                    drawESPCircle(0, 0, 1.0, 3);
                    drawESPCircle(0, 0, 0.5, 3);
                    GlStateManager.popMatrix();
                }
            }
        }
        GlStateManager.popMatrix();
    }


    public void glColor() {
        GlStateManager.color(1, 1, 1, 0.8f);
    }

    public void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public void drawESPCircle(double cx, double cy, double r, double n) {
        GL11.glPushMatrix();
        cx *= 2.0;
        cy *= 2.0;
        double b = 6.2831852 / n;
        double p = Math.cos(b);
        double s = Math.sin(b);
        double x = r *= 2.0;
        double y = 0.0;
        enableGL2D();
        GL11.glScaled(0.5, 0.5, 0.5);
        GlStateManager.color(0, 0, 0);
        GlStateManager.resetColor();
        glColor();
        GL11.glBegin(2);
        double ii = 0;
        while (ii < n) {
            GL11.glVertex2d(x + cx, y + cy);
            double t = x;
            x = p * x - s * y;
            y = s * t + p * y;
            ii++;
        }
        GL11.glEnd();
        GL11.glScaled(2.0, 2.0, 2.0);
        disableGL2D();

        GlStateManager.disableBlend();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.enableAlpha();

        GlStateManager.color(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

}
