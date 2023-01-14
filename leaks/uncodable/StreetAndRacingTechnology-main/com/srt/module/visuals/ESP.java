package com.srt.module.visuals;

import com.srt.events.Event;
import com.srt.events.listeners.EventRender3D;
import com.srt.module.ModuleBase;
import com.srt.module.combat.AntiBot;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

public class ESP extends ModuleBase {

    public ESP() {
        super("Chams", 0, Category.VISUALS);
    }
    
    public void onEvent(Event event) {
        if(event instanceof EventRender3D) {
            for(Entity entity : AntiBot.getEntities()) {
                if(entity instanceof EntityPlayer) {
                    if(entity != mc.thePlayer)
                        drawEspBox(entity);
                }
            }
        }
    }

    private void drawEspBox(Entity entity)
    {
        GlStateManager.pushMatrix();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableBlend();
        //GlStateManager.lineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth ();
        GlStateManager.depthMask(false);
        GlStateManager.color( Hud.getColor(4).getRGB(), Hud.getColor(4).getBlue(), Hud.getColor(4).getGreen(), 0.5F);
        RenderGlobal.renderAlligned(
                new AxisAlignedBB(
                        entity.boundingBox.minX
                                - 0.05
                                - entity.posX
                                + (entity.posX - mc.getRenderManager().renderPosX),
                        entity.boundingBox.minY
                                - entity.posY
                                + (entity.posY - mc.getRenderManager().renderPosY),
                        entity.boundingBox.minZ
                                - 0.05
                                - entity.posZ
                                + (entity.posZ - mc.getRenderManager().renderPosZ),
                        entity.boundingBox.maxX
                                + 0.05
                                - entity.posX
                                + (entity.posX - mc.getRenderManager().renderPosX),
                        entity.boundingBox.maxY
                                + 0.1
                                - entity.posY
                                + (entity.posY - mc.getRenderManager().renderPosY),
                        entity.boundingBox.maxZ
                                + 0.05
                                - entity.posZ
                                + (entity.posZ - mc.getRenderManager().renderPosZ)
                ));
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
