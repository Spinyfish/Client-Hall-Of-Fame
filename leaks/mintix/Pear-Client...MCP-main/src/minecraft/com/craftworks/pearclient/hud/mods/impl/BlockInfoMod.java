package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;

import com.craftworks.pearclient.gui.hud.HudScreen;
import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.GLUtils;
import com.craftworks.pearclient.util.blur.BlurUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class BlockInfoMod extends HudMod {
	
	private boolean loaded = false;
    
    private BlockPos pos;
    private IBlockState state;
    private Block block;
	
	public BlockInfoMod() {
		super("Target Block", "I saw that block", 80, 40);
	}

	@Override
	public void onRender2D() {
		ScaledResolution sr = new ScaledResolution(mc);
		DrawUtil.drawRoundedOutline(getX() - 2.4f, getY() - 2, getWidth() + 3.3f, getHeight() + 2, 3, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));

		setonRenderBackground(getX() - 2, getY() - 1, getWidth() + 2, getHeight(), 3.0f, new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5));
		BlurUtils.renderBlurredBackground(sr.getScaledWidth(), sr.getScaledHeight(), getX() - 2, getY() - 1, getWidth() + 2, getHeight(), 3);
		
		if((mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) || mc.currentScreen instanceof HudScreen) {
			
			pos = mc.objectMouseOver.getBlockPos();
			state = mc.theWorld.getBlockState(pos);
			block = state.getBlock();
			
			if(mc.currentScreen instanceof HudScreen) {
				block = Blocks.grass;
			}

			if(!block.equals(Blocks.portal) && !block.equals(Blocks.end_portal)) {
				fr.drawString(block.getLocalizedName(),  getX() + (80 / 2) - (fr.getStringWidth(block.getLocalizedName()) / 2),  getY() + 4.5F, -1, false);
				
				GLUtils.startScale(getX(), getY() - 2.5f, 80, 80, 2.5F);
				RenderHelper.enableGUIStandardItemLighting();
				mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(block),  getX() + (80 / 2) - 8,  getY() + (80 / 2) - 8);
				RenderHelper.disableStandardItemLighting();
				GLUtils.stopScale();
			}
		}
		super.onRender2D();
	}
	
	@Override
	public void onRenderDummy() {
			
			block = Blocks.grass;
				fr.drawString(block.getLocalizedName(),  getX() + (80 / 2) - (fr.getStringWidth(block.getLocalizedName()) / 2),  getY() + 4.5F, -1, false);
				
				GLUtils.startScale(getX(), getY() - 2.5f, 80, 80, 2.5F);
				RenderHelper.enableGUIStandardItemLighting();
				mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(block),  getX() + (80 / 2) - 8,  getY() + (80 / 2) - 8);
				RenderHelper.disableStandardItemLighting();
				GLUtils.stopScale();
		super.onRenderDummy();
	}
	
	public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
    }
	
	@Override
	public int getHeight() {
		return 80;
	}
	
	@Override
	public int getWidth() {
		return 80;
	}
}
