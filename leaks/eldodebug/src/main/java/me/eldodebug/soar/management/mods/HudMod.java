package me.eldodebug.soar.management.mods;

import me.eldodebug.soar.utils.font.FontUtils;

public class HudMod extends Mod {

	public HudMod(String name, String description) {
		super(name, description, ModCategory.HUD);
	}

	public void onRender2D() {
		
		this.drawBackground(this.getX(), this.getY(), this.getWidth() - this.getX(), this.getHeight() - this.getY());
		
		FontUtils.regular_bold20.drawString(this.getText(), this.getX() + 4.5F, this.getY() + 4.5F, this.getFontColor().getRGB());
		
		this.setWidth((int) (FontUtils.regular_bold20.getStringWidth(this.getText()) + 10));
		this.setHeight((int) (FontUtils.regular_bold20.getHeight()) + 8);
	}
	
	public void onRenderShadow() {
		this.drawShadow(this.getX(), this.getY(), this.getWidth() - this.getX(), this.getHeight() - this.getY());
	}	
	
	public String getText() {
		return null;
	}
}
