package me.eldodebug.soar.management.mods.impl;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender3D;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class ChunkBordersMod extends Mod{

	public ChunkBordersMod() {
		super("Chunk Borders", "Render chunk borders", ModCategory.RENDER);
	}

	@EventTarget
	public void onRender3D(EventRender3D event) {
        EntityPlayerSP entity = Minecraft.getMinecraft().thePlayer;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GL11.glPushMatrix();
        float frame = event.getPartialTicks();
        double inChunkPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) frame;
        double inChunkPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) frame;
        double inChunkPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) frame;

        GL11.glTranslated(-inChunkPosX, -inChunkPosY, -inChunkPosZ);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1F);
        worldrenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        GL11.glTranslatef((float) (entity.chunkCoordX * 16), 0.0F, (float) (entity.chunkCoordZ * 16));
        double x = 0.0D;
        double z = 0.0D;
        
        Color color = Color.BLUE;
        Color color2 = Color.YELLOW;
        
        int eyeHeightBlock;

        for (int eyeHeight = -2; eyeHeight <= 2; ++eyeHeight) {
            for (eyeHeightBlock = -2; eyeHeightBlock <= 2; ++eyeHeightBlock) {
                if (Math.abs(eyeHeightBlock) != 2 || Math.abs(eyeHeight) != 2) {
                    x = (double) (eyeHeightBlock * 16);
                    z = (double) (eyeHeight * 16);
                    
                    worldrenderer.pos(x, 0.0D, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    worldrenderer.pos(x, 256.0D, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    worldrenderer.pos(x + 16.0D, 0.0D, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    worldrenderer.pos(x + 16.0D, 256.0D, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    worldrenderer.pos(x, 0.0D, z + 16.0D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    worldrenderer.pos(x, 256.0D, z + 16.0D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    worldrenderer.pos(x + 16.0D, 0.0D, z + 16.0D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                    worldrenderer.pos(x + 16.0D, 256.0D, z + 16.0D).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                }
            }
        }

        z = 0.0D;
        x = 0.0D;

        float f = (float) ((double) entity.getEyeHeight() + entity.posY);

        eyeHeightBlock = (int) Math.floor((double) f);

        for (int y = 0; y < 257; ++y) {
            if (y < 256) {
                for (int n = 1; n < 16; ++n) {
                    worldrenderer.pos((double) n, (double) y, z).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
                    worldrenderer.pos((double) n, (double) (y + 1), z).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
                    worldrenderer.pos(x, (double) y, (double) n).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
                    worldrenderer.pos(x, (double) (y + 1), (double) n).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
                    worldrenderer.pos((double) n, (double) y, z + 16.0D).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
                    worldrenderer.pos((double) n, (double) (y + 1), z + 16.0D).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
                    worldrenderer.pos(x + 16.0D, (double) y, (double) n).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
                    worldrenderer.pos(x + 16.0D, (double) (y + 1), (double) n).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
                }
            }

            worldrenderer.pos(0.0D, (double) y, 0.0D).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
            worldrenderer.pos(16.0D, (double) y, 0.0D).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
            worldrenderer.pos(0.0D, (double) y, 0.0D).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
            worldrenderer.pos(0.0D, (double) y, 16.0D).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
            worldrenderer.pos(16.0D, (double) y, 0.0D).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
            worldrenderer.pos(16.0D, (double) y, 16.0D).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
            worldrenderer.pos(0.0D, (double) y, 16.0D).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
            worldrenderer.pos(16.0D, (double) y, 16.0D).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
        }

        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
	}
}
