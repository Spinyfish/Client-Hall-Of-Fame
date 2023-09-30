package igbt.astolfy.ui.ClickGUI.dropDown.impl;

import java.io.IOException;
import java.util.ArrayList;

import igbt.astolfy.Astolfy;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.module.ModuleBase.Category;
import igbt.astolfy.ui.ClickGUI.dropDown.ClickGui;
import igbt.astolfy.utils.AnimationUtils;
import igbt.astolfy.utils.AnimationUtils.AnimationType;
import igbt.astolfy.utils.RenderUtils;
import igbt.astolfy.utils.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class Panel {

	public ClickGui clickGUI;
	public Category category;
	public ArrayList<Button> buttons = new ArrayList<>();
	
	public double x;
	public double y = 5;
	
	public double width = 100;
	public double height = 18;
	private double offsetX = 0;
	private double offsetY = 0;
	
	public boolean dragging;
	public boolean open = true;
	public TimerUtils timer = new TimerUtils();
	public AnimationUtils animate = new AnimationUtils(AnimationType.SCALE);
	public double scale = 0;
	private Panel lastGUI;
	
	public Panel(Panel lastGUI,int x,Category category, ClickGui clickGUI) {
		this.x = x;
		this.lastGUI = lastGUI;
		this.clickGUI = clickGUI;
		this.category = category;
		int count = 0;
		for(ModuleBase mod : Astolfy.i.moduleManager.getModules()) {
			if(mod.getCategory() == category) {
				buttons.add(new Button(height + (count * 15), mod,this));
				count++;
			}
		}
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		if(lastGUI != null) {
			if(lastGUI.timer.getTime() < 50) {
				timer.reset();
			}
		}
		if(timer.getTime() < 100) {
			double scale = (timer.getTime());
			animate.Render(scale / (100), x + (width / 2), y);
		}
		if(dragging) {
			x = mouseX - offsetX;
			y = mouseY - offsetY;
		}
		Minecraft mc = Minecraft.getMinecraft();
		
		if(open) {
			int count = 0;
			double lButHeight = 0;
			double tButHeight = 0;
			for(Button b : buttons) {
				if(count > 0) {
					lButHeight += buttons.get(count - 1).setHeight;
					tButHeight += buttons.get(count).height;
				}
				tButHeight += b.drawScreen(mouseX, mouseY, partialTicks, lButHeight);
				count++;
			}
			RenderUtils.drawOutlineRect(x, y + height - 1, x + width - 1, y + height + tButHeight + 16,1, category.color.darker().darker().getRGB());
			RenderUtils.drawOutlineRect(x + 1, y + height, x + width - 2, y + height + tButHeight + 15,1, 0xFF000000);
		}
		
		Gui.drawRect(x, y, x + width, y + height,  category.color.darker().darker().getRGB());
		mc.customFont.drawStringWithShadow(category.name, x + 5, y + ((height - mc.customFont.getHeight()) / 2), -1);
		GlStateManager.popMatrix();
	}

	public void keyTyped(char typedChat, int keyCode) {
		if(open)
			for(Button b : buttons) {
				b.keyTyped(typedChat, keyCode);
			}
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(isHovered(mouseX,mouseY)) {
			switch(mouseButton) {
				case 0:
					dragging = true;
					offsetX = mouseX - x;
					offsetY = mouseY - y;
					break;
				case 1:
					open = !open;
					break;
			}
		}
		if(open)
			for(Button b : buttons) {
				b.mouseClicked(mouseX, mouseY, mouseButton);
			}
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		if(open)
			for(Button b : buttons) {
				b.mouseReleased(mouseX, mouseY, state);
			}
		dragging = false;
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		return (mouseX > x && mouseX < x + width)
				&& (mouseY > y && mouseY < y + height);
	}

	public void drawFinish(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		if(timer.getTime() < 100) {
			double scale = (timer.getTime());
			animate.Render(scale / (100), x + (width / 2), y);
		}
		if(open)
			RenderUtils.drawDrop(x + 1,y + height,x + width - 1, 5);
		GlStateManager.popMatrix();
	}
}
