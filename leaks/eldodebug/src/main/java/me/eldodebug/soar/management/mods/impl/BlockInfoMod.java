package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.gui.GuiEditHUD;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.impl.EaseBackIn;
import me.eldodebug.soar.utils.font.FontUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class BlockInfoMod extends Mod{

	private boolean loaded = false;
    public Animation introAnimation;
    
    private BlockPos pos;
    private IBlockState state;
    private Block block;
    
	public BlockInfoMod() {
		super("Block Info", "View details of the block you saw", ModCategory.HUD);
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {
		
		String harvest = "";
		
		if((mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) || mc.currentScreen instanceof GuiEditHUD) {
			
			pos = mc.objectMouseOver.getBlockPos();
			state = mc.theWorld.getBlockState(pos);
			block = state.getBlock();
			
			if(!loaded) {
				loaded = true;
		        introAnimation = new EaseBackIn(450, 1, 2);
			}
			
			introAnimation.setDirection(Direction.FORWARDS);
		}else {
			if(introAnimation != null) {
				introAnimation.setDirection(Direction.BACKWARDS);
			}
		}
		
		if(introAnimation != null) {
			GlUtils.startScale(this.getX(), this.getY(), this.getWidth() - this.getX(), this.getHeight() - this.getY(), (float) introAnimation.getValue());
			
			if(mc.currentScreen instanceof GuiEditHUD) {
				block = Blocks.grass;
			}

			if(!block.equals(Blocks.portal) && !block.equals(Blocks.end_portal)) {
				this.drawBackground(this.getX(), this.getY(), 80, 80);
				
				FontUtils.regular_bold20.drawStringWithUnicode(block.getLocalizedName(), this.getX() + (80 / 2) - (FontUtils.regular_bold20.getStringWidth(block.getLocalizedName()) / 2), this.getY() + 4.5F, this.getFontColor().getRGB(), false);
				
				GlUtils.startScale(this.getX(), this.getY(), 80, 80, 1.8F);
				RenderHelper.enableGUIStandardItemLighting();
				mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(block), this.getX() + (80 / 2) - 8, this.getY() + (80 / 2) - 8);
				RenderHelper.disableStandardItemLighting();
				GlUtils.stopScale();
				
				if(mc.thePlayer.inventory.getCurrentItem() != null && (!(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBlock))) {
					harvest = "Harvest: " + (mc.thePlayer.inventory.getCurrentItem().getStrVsBlock(block) > 1 ? EnumChatFormatting.GREEN + "true" : EnumChatFormatting.RED + "false");
				}else {
					harvest = "Harvest: " + EnumChatFormatting.RED + "false";
				}
				
				FontUtils.regular_bold20.drawCenteredString(harvest, this.getX() + (80 / 2), this.getHeight() - 14, this.getFontColor().getRGB());
			}
			
			GlUtils.stopScale();
		}

		this.setWidth(80);
		this.setHeight(80);
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		
		if(introAnimation != null) {
			GlUtils.startScale(this.getX(), this.getY(), this.getWidth() - this.getX(), this.getHeight() - this.getY(), (float) introAnimation.getValue());
			
			if(!block.equals(Blocks.portal) && !block.equals(Blocks.end_portal)) {
				this.drawShadow(this.getX(), this.getY(), this.getWidth() - this.getX(), this.getHeight() - this.getY());
			}
			
			GlUtils.stopScale();
		}
	}
}
