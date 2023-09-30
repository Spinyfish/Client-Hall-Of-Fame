package dev.eternal.client.module.impl.render;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventRender3D;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

@ModuleInfo(name = "Tracers", category = Module.Category.RENDER)
public class Tracers extends Module {

  @Subscribe
  public void handleRender3D(EventRender3D eventRender3D) {
    Color color = new Color(client.scheme().getPrimary());
    GlStateManager.pushMatrix();
    GlStateManager.disableTexture2D();
    GL11.glBegin(3);
    boolean boobs = mc.gameSettings.viewBobbing;
    mc.gameSettings.viewBobbing = false;
    for (Entity entity : mc.theWorld.loadedEntityList) {
      if (entity == mc.thePlayer)
        continue;
      if (!(entity instanceof EntityPlayer))
        continue;
      if (!(entity.isEntityAlive()))
        continue;
      final float x = (float) ((float) (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * eventRender3D.partialTicks())
          - RenderManager.renderPosX);
      final float y = (float) ((float) (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * eventRender3D.partialTicks())
          - RenderManager.renderPosY);
      final float z = (float) ((float) (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * eventRender3D.partialTicks())
          - RenderManager.renderPosZ);
      GL11.glColor3d(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);

      GL11.glVertex3d(0, mc.thePlayer.getEyeHeight(), 0);
      GL11.glVertex3d(x, y + entity.getEyeHeight(), z);
    }
    mc.gameSettings.viewBobbing = boobs;
    GL11.glEnd();
    GlStateManager.enableTexture2D();
    GlStateManager.popMatrix();
  }

}