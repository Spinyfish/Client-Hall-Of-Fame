package me.kansio.client.modules.impl.visuals;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.Render3DEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Sphere;

@ModuleData(
        name = "Penis ESP",
        category = ModuleCategory.VISUALS,
        description = "Shows a cock on players"
)
public class PenisESP extends Module {

    private float pspin, pcumsize, pamount;

    public void onEnable() {
        pspin = 0;
        pcumsize = 0;
        pamount = 0;
    }

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        for (final Object o : mc.theWorld.loadedEntityList) {
            if (o instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer) o;
                final double n = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks;
                if (lineOfSight(player) && !player.noClip) {
                    mc.getRenderManager();
                    final double x = n - mc.renderManager.renderPosX;
                    final double n2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks;
                    mc.getRenderManager();
                    final double y = n2 - mc.renderManager.renderPosY;
                    final double n3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks;
                    mc.getRenderManager();
                    final double z = n3 - mc.renderManager.renderPosZ;
                    GL11.glPushMatrix();
                    RenderHelper.disableStandardItemLighting();
                    this.esp(player, x, y, z);
                    RenderHelper.enableStandardItemLighting();
                    GL11.glPopMatrix();
                }
            }

            ++pamount;
            if (pamount > 25) {
                ++pspin;
                if (pspin > 50.0f) {
                    pspin = -50.0f;
                } else if (pspin < -50.0f) {
                    pspin = 50.0f;
                }
                pamount = 0;
            }
            ++pcumsize;
            if (pcumsize > 180.0f) {
                pcumsize = -180.0f;
            } else {
                if (pcumsize >= -180.0f) {
                    continue;
                }
                pcumsize = 180.0f;
            }

        }
    }

    public void esp(final EntityPlayer player, final double x, final double y, final double z) {
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(true);
        GL11.glLineWidth(1.0f);
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(-player.rotationYaw, 0.0f, player.height, 0.0f);
        GL11.glTranslated(-x, -y, -z);
        GL11.glTranslated(x, y + player.height / 2.0f - 0.22499999403953552, z);
        GL11.glColor4f(1.38f, 0.55f, 2.38f, 1.0f);
        GL11.glRotated((player.isSneaking() ? 35 : 0) + pspin, 1.0f + pspin, 0.0f, pcumsize);
        GL11.glTranslated(0.0, 0.0, 0.07500000298023224);
        final Cylinder shaft = new Cylinder();
        shaft.setDrawStyle(100013);
        shaft.draw(0.1f, 0.11f, 0.4f, 25, 20);
        GL11.glColor4f(1.38f, 0.85f, 1.38f, 1.0f);
        GL11.glTranslated(0.0, 0.0, -0.12500000298023223);
        GL11.glTranslated(-0.09000000074505805, 0.0, 0.0);
        final Sphere right = new Sphere();
        right.setDrawStyle(100013);
        right.draw(0.14f, 10, 20);
        GL11.glTranslated(0.16000000149011612, 0.0, 0.0);
        final Sphere left = new Sphere();
        left.setDrawStyle(100013);
        left.draw(0.14f, 10, 20);
        GL11.glColor4f(1.35f, 0.0f, 0.0f, 1.0f);
        GL11.glTranslated(-0.07000000074505806, 0.0, 0.589999952316284);
        final Sphere tip = new Sphere();
        tip.setDrawStyle(100013);
        tip.draw(0.13f, 15, 20);
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
    }

    private static boolean lineOfSight(EntityPlayer otherPlayer) {
        // todo collision check
        Minecraft mc = Minecraft.getMinecraft();
        return mc.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3(otherPlayer.posX, otherPlayer.posY + (double) otherPlayer.getEyeHeight(), otherPlayer.posZ), false, true, false) == null;
    }
}
