package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;
import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import com.craftworks.pearclient.hud.mods.HudMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderTNTPrimed;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityTNTPrimed;

public class TNTCountdownMod extends HudMod {

	public TNTCountdownMod() {
		super("TNT Countdown", "3 2 1 BOOM!", 0, 0);
	}

	boolean playingBedwars = false;
	DecimalFormat timeFormatter = new DecimalFormat("0.00");

	@Override
	public void onRender2D() {

		//mc.fontRendererObj.drawStringWithShadow("\2476[ \247cT\2474N\247cT \2477Countdown \2476]\247r", getX()+1, getY()+1, -1);
		
		if(timeFormatter == null) {
			timeFormatter = new DecimalFormat("0.00");
		}
		if(Minecraft.getMinecraft().getCurrentServerData() == null ||
				Minecraft.getMinecraft().getCurrentServerData().serverIP == null) {
			return;
		}
		if(!Minecraft.getMinecraft().isSingleplayer()) {
			if(Minecraft.getMinecraft().getCurrentServerData().serverIP.contains("hypixel.net")) {
				playingBedwars = true;
			}else {
				playingBedwars = false;
			}
		}else {
			playingBedwars = false;
		}
		
	}
	
	public void renderTag(RenderTNTPrimed tntRenderer, EntityTNTPrimed tntPrimed, double x, double y, double z, float partialTicks) {
		if(!this.isToggled()) { 
			return;
		}
        // hypixel changes the fuse time in bedwars to explode around the 28th tick,
        // which makes the value of the fuse starting timer presumably 52 instead of 80
        // this can fluctuate between 27 and 28, but 28 seems to be more common, so we can sit with that instead
        final int fuseTimer = this.playingBedwars ? tntPrimed.fuse - 28 : tntPrimed.fuse;
        if (fuseTimer < 1) return;
        double distance = tntPrimed.getDistanceSqToEntity(tntRenderer.getRenderManager().livingPlayer);

        if (distance <= 4096D) {
            float number = (fuseTimer - partialTicks) / 20F;
            String time = this.timeFormatter.format(number);
            FontRenderer fontrenderer = tntRenderer.getFontRendererFromRenderManager();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x + 0.0F, (float) y + tntPrimed.height + 0.5F, (float) z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-tntRenderer.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);

            int xMultiplier = 1; // Nametag x rotations should flip in front-facing 3rd person

            if (mc.gameSettings.thirdPersonView == 2) {
                xMultiplier = -1;
            }

            float scale = 0.02666667f;
            GlStateManager.rotate(tntRenderer.getRenderManager().playerViewX * xMultiplier, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-scale, -scale, scale);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            int stringWidth = fontrenderer.getStringWidth(time) >> 1;
            // refer to the comment at the top of the method
            float green = Math.min(fuseTimer / (this.playingBedwars ? 52 : 80f), 1f);
            Color color = new Color(1f - green, green, 0f);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR /* For you, this may be field_181705_e instead of POSITION_COLOR */);
            
            // For these below, you may need to find the functions yourself.
			
            // worldrender.pos is a function that returns a WorldRenderer with these args: (double p_181662_1_, double p_181662_3_, double p_181662_5_). Find a matching function to use as a replacement.
			
            // worldrender.color is a function that returns a WorldRenderer with these args: (float p_181666_1_, float p_181666_2_, float p_181666_3_, float p_181666_4_). Find a matching function to use as a replacement.
			
            // worldrender.endVertex takes no args, and has this code inside:
            /* ++this.vertexCount;
        this.func_181670_b(this.vertexFormat.func_181719_f());
        this.field_181678_g = 0;
        this.field_181677_f = this.vertexFormat.getElement(this.field_181678_g);

        if (Config.isShaders())
        {
            SVertexBuilder.endAddVertex(this);
        }*/
        
            worldrenderer.pos(-stringWidth - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(-stringWidth - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(stringWidth + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(stringWidth + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            fontrenderer.drawString(time, -fontrenderer.getStringWidth(time) >> 1, 0, color.getRGB());
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
            super.onRender2D();
        }
    }

}