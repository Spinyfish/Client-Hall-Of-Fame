package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;

import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.blur.BlurUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;

public class CoordsMod extends HudMod {
	
	public CoordsMod() {
		super("Coords", "", 36, 36);
	}
	
    private String biome;
    
    @Override
    public void onRender2D() {
    	ScaledResolution sr = new ScaledResolution(mc);
    	DrawUtil.drawRoundedOutline(getX() - 2.4f, getY() - 2, getWidth() + 3.3f, getHeight() + 2, 3, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
		//DrawUtil.drawRoundedRectangle(getX() - 2, getY() - 1, getWidth() + 2, getHeight(), 3, new Color(255, 255, 255, 50));
		//this.drawShadow(getX() - 2.4f, getY() - 2, getWidth() + 3.3f, getHeight() + 2, 4.0f);


		setonRenderBackground(getX() - 2, getY() - 1, getWidth() + 2, getHeight(), 3.0f, new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5));
		BlurUtils.renderBlurredBackground(sr.getScaledWidth(), sr.getScaledHeight(), getX() - 2, getY() - 1, getWidth() + 2, getHeight(), 3);
        fr.drawString("X: " + MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posX), (int) getX(), (int) getY(), -1);
        fr.drawString("Y: " + MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posY), (int) getX(), (int) getY() + 10, -1);
        fr.drawString("Z: " + MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posZ), (int) getX(), (int) getY() + 20, -1);
        Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(new BlockPos(Minecraft.getMinecraft().thePlayer));

        this.biome = chunk.getBiome(new BlockPos(mc.thePlayer), this.mc.theWorld.getWorldChunkManager()).biomeName;
        fr.drawString("Biome: " + biome, (int) getX(), (int) getY() + 30, -1);
        super.onRender2D();
    }
    
    @Override
    public void onRenderDummy() {
        fr.drawString("X: " + MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posX), (int) getX(), (int) getY(), -1);
        fr.drawString("Y: " + MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posY), (int) getX(), (int) getY() + 10, -1);
        fr.drawString("Z: " + MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posZ), (int) getX(), (int) getY() + 20, -1);
        Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(new BlockPos(Minecraft.getMinecraft().thePlayer));
        
        this.biome = chunk.getBiome(new BlockPos(mc.thePlayer), this.mc.theWorld.getWorldChunkManager()).biomeName;
        fr.drawString("Biome: " + biome, (int) getX(), (int) getY() + 30, -1);
        super.onRenderDummy();
    }

    @Override
    public int getWidth() {
        return 40 + fr.getStringWidth(biome);
    }

    @Override
    public int getHeight() {
        return 40;
    }

}
