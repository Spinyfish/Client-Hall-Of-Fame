package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.font.FontUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class CoordsMod extends Mod{

	public CoordsMod() {
		super("Coords", "Display player coordinates", ModCategory.HUD);
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {

		String biome = "";
		Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(new BlockPos(mc.thePlayer));
		int maxWidth = 100;
		biome = chunk.getBiome(new BlockPos(mc.thePlayer), this.mc.theWorld.getWorldChunkManager()).biomeName;

		if(maxWidth < (FontUtils.regular_bold20.getStringWidth("Biome: " + biome))) {
			maxWidth = (int) (FontUtils.regular_bold20.getStringWidth("Biome: " + biome) + 5);
		}else {
			maxWidth = 100;
		}

		this.drawBackground(this.getX(), this.getY(), maxWidth + 4.5F, 46.5F);

		FontUtils.regular_bold20.drawString("X: " + (int) (mc.thePlayer.posX), this.getX() + 4.5F, this.getY() + 4.5F, this.getFontColor().getRGB());
		FontUtils.regular_bold20.drawString("Y: " + (int) (mc.thePlayer.posY), this.getX() + 4.5F, this.getY() + 14.5F, this.getFontColor().getRGB());
		FontUtils.regular_bold20.drawString("Z: " + (int) (mc.thePlayer.posZ), this.getX() + 4.5F, this.getY() + 24.5F, this.getFontColor().getRGB());
		FontUtils.regular_bold20.drawString("Biome: " + biome, this.getX() + 4.5F, this.getY() + 34.5F, this.getFontColor().getRGB());
		
		this.setWidth((int) (maxWidth + 5F));
		this.setHeight((int) 47F);
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		this.drawShadow(this.getX(), this.getY(), (this.getWidth() - this.getX()) - 0.5F, 46.5F);
	}
}