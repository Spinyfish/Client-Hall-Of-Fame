package byron.Mono.module.impl.visual;

import byron.Mono.event.impl.Event3D;
import byron.Mono.event.impl.EventRenderPlayer;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.font.FontUtil;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.module.impl.combat.Killaura;
import byron.Mono.utils.AuraUtil;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInterface(name = "Tracers", description = "Track everyone down.", category = Category.Visual)
public class Tracers extends Module {

    @Subscribe
    public void onUpdate(Event3D event)
    {
    	 for (final EntityPlayer playerEntity : mc.theWorld.playerEntities) {
             if (playerEntity != mc.thePlayer && !playerEntity.isDead && !playerEntity.isInvisible()) // Distance check to fix a bug where it renders players far away that have been rendered before
                 drawToPlayer(playerEntity);
         }
    }
    

    public void drawToPlayer(final EntityLivingBase entity) {
        final Color color = new Color(250, 250, 250);

        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;

        final double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks) - mc.getRenderManager().viewerPosX;
        final double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks) - mc.getRenderManager().viewerPosY;
        final double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks) - mc.getRenderManager().viewerPosZ;

        render(red, green, blue, xPos, yPos, zPos);
    }
    
    public void render(final float red, final float green, final float blue,
            final double x, final double y, final double z) {
drawTracerLine(x, y, z, red, green, blue, 0.5F, 1.5F);
}

public static void drawTracerLine(final double x, final double y, final double z,
                           final float red, final float green, final float blue,
                           final float alpha, final float lineWidth) {
GL11.glPushMatrix();
GL11.glLoadIdentity();
mc.entityRenderer.orientCamera(mc.timer.renderPartialTicks);
GL11.glEnable(GL11.GL_BLEND);
GL11.glEnable(GL11.GL_LINE_SMOOTH);
GL11.glDisable(GL11.GL_DEPTH_TEST);
 GL11.glDisable(GL11.GL_LIGHTING);
GL11.glDisable(GL11.GL_TEXTURE_2D);
GL11.glBlendFunc(770, 771);
GL11.glEnable(GL11.GL_BLEND);
GL11.glLineWidth(lineWidth);
GL11.glColor4f(red, green, blue, alpha);
GL11.glBegin(2);
GL11.glVertex3d(0.0D, mc.thePlayer.getEyeHeight(), 0.0D);
GL11.glVertex3d(x, y, z);
GL11.glEnd();
GL11.glDisable(GL11.GL_BLEND);
GL11.glEnable(GL11.GL_TEXTURE_2D);
GL11.glEnable(GL11.GL_DEPTH_TEST);
GL11.glDisable(GL11.GL_LINE_SMOOTH);
GL11.glDisable(GL11.GL_BLEND);
 GL11.glEnable(GL11.GL_LIGHTING);
GL11.glPopMatrix();
}


    @Override
    public void onEnable()
    {
        super.onEnable();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }
}
