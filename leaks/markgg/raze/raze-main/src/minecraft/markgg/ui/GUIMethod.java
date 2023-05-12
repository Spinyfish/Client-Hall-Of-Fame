package markgg.ui;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import markgg.Client;
import markgg.modules.Module;
import markgg.modules.Module.Category;
import markgg.settings.BooleanSetting;
import markgg.settings.KeybindSetting;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.settings.Setting;
import markgg.ui.comp.CheckBox;
import markgg.ui.comp.Combo;
import markgg.ui.comp.Comp;
import markgg.ui.comp.Keybind;
import markgg.ui.comp.Slider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class GUIMethod extends GuiScreen {

	public double posX, posY, width, height, dragX, dragY;
	public boolean dragging;
	public Category selectedCategory;

	private Module selectedModule;
	public int modeIndex;
	private int categoryIndex;
	private Properties config;
	public ArrayList<Comp> comps = new ArrayList<>();

	public static int getRainbow(float seconds, float saturation, float brightness) {
		float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000);
		int color = Color.HSBtoRGB(hue, saturation, brightness);
		return color;
	}
	
	public GUIMethod() {
		config = new Properties();
	    try {
	        config.load(new FileInputStream("Raze/config.properties"));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    if (!config.containsKey("category")) {
	        config.setProperty("category", "Combat");
	    }
	    
	    switch (config.getProperty("category")) {
	        case "Combat":
	            categoryIndex = 0;
	            break;
	        case "Movement":
	            categoryIndex = 1;
	            break;
	        case "Render":
	            categoryIndex = 2;
	            break;
	        case "Player":
	            categoryIndex = 3;
	            break;
	        case "Ghost":
	            categoryIndex = 4;
	            break;
	        case "Misc":
	            categoryIndex = 5;
	            break;
	    }
		
		dragging = false;
		posX = getScaledRes().getScaledWidth() / 2 - 230;
		posY = getScaledRes().getScaledHeight() / 2 - 120;
		width = posX + 120 * 2;
		height = height + 200;
		if(categoryIndex == 0)
			selectedCategory = Category.COMBAT;
		if(categoryIndex == 1)
			selectedCategory = Category.MOVEMENT;
		if(categoryIndex == 2)
			selectedCategory = Category.RENDER;
		if(categoryIndex == 3)
			selectedCategory = Category.PLAYER;
		if(categoryIndex == 4)
			selectedCategory = Category.GHOST;
		if(categoryIndex == 5)
			selectedCategory = Category.MISC;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (dragging) {
			posX = mouseX - dragX;
			posY = mouseY - dragY;
		}

		int primaryColor = 0xFFE44964, secondaryColor = 0xFFCD415A;
		if(Client.isModuleToggled("Colors")) {
			if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Rainbow") {
				primaryColor = getRainbow(4, 0.8f, 1);
				secondaryColor = getRainbow(4, 0.8f, 0.8f);
			}else if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Custom") {
				int red1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(1)).getValue();
				int green1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(2)).getValue();
				int blue1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(3)).getValue();
				primaryColor = new Color(red1,green1,blue1).getRGB();
				secondaryColor = new Color(red1,green1,blue1).darker().getRGB();
			}
		}else {
			primaryColor = 0xFFE44964;
			secondaryColor = 0xFFCD415A;
		}
		if(((BooleanSetting)Client.getModuleByName("ClickGUI").settings.get(1)).isEnabled()) {
			this.drawDefaultBackground();
		}
		width = posX + 120 * 2;
		height = posY + 200;
		Gui.drawRect(posX, posY - 15, width, posY, new Color(30,30,30).getRGB());
		Gui.drawRect(posX, posY, width, height, new Color(20,20,20).getRGB());
		fontRendererObj.drawStringWithShadow("Click" + EnumChatFormatting.WHITE + "GUI", posX + 5, posY - 11, primaryColor);
		int offset = 0;
		for (Category category : Category.values()) {
			Gui.drawRect(posX,posY  + offset + 1.7,posX + 60,posY + 15 + offset,category.equals(selectedCategory) ? primaryColor : new Color(30,30,30).getRGB());
			fontRendererObj.drawString(category.name,(int)posX + 5, (int)(posY + 4) + offset + (int)1.7, -1);
			offset += 15;
		}
		offset = 0;
		for (Module m : Client.getModulesByCategory(selectedCategory)) {
			Gui.drawRect(posX + 65, posY + offset + 1.7, posX + 125, posY + 15 + offset,m.isEnabled() ? secondaryColor : new Color(28,28,28).getRGB());
			fontRendererObj.drawString(m.getName(),(int)posX + 67, (int)(posY + 4) + offset + (int)1.7, -1);
			offset += 15;
		}
		for (Comp comp : comps) {
			comp.drawScreen(mouseX, mouseY);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		for (Comp comp : comps) {
			comp.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (isInside(mouseX, mouseY, posX, posY - 10, width, posY) && mouseButton == 0) {
			dragging = true;
			dragX = mouseX - posX;
			dragY = mouseY - posY;
		}
		int offset = 0;
		for (Category category : Category.values()) {
			if (isInside(mouseX, mouseY,posX,posY + 1 + offset,posX + 60,posY + 15 + offset) && mouseButton == 0) {
				selectedCategory = category;
				if(selectedCategory == Category.COMBAT) {
					categoryIndex = 0;
			        config.setProperty("category", "Combat"); 
				} else if(selectedCategory == Category.MOVEMENT) {
					categoryIndex = 1;
					config.setProperty("category", "Movement"); 
				} else if(selectedCategory == Category.RENDER) {
					categoryIndex = 2;
					config.setProperty("category", "Render"); 
				} else if(selectedCategory == Category.PLAYER) {
					categoryIndex = 3;
					config.setProperty("category", "Player"); 
				} else if(selectedCategory == Category.GHOST) {
					categoryIndex = 4;
					config.setProperty("category", "Ghost"); 
				} else if(selectedCategory == Category.MISC) {
					categoryIndex = 5;
					config.setProperty("category", "Misc"); 
				}
			}
			offset += 15;
		}
		offset = 0;
		for (Module m : Client.getModulesByCategory(selectedCategory)) {
			if (isInside(mouseX, mouseY,posX + 65,posY + 1 + offset,posX + 125,posY + 15 + offset)) {
				if (mouseButton == 0) {
					m.toggle();
				}
				if(mouseButton == 1) {
					int sOffset = 3;
					comps.clear();
					if (Setting.getSettingsByMod(m) != null)
						for (Setting setting : Setting.getSettingsByMod(m)) {
							selectedModule = m;
							if (setting instanceof ModeSetting) {
								comps.add(new Combo(200, sOffset, this, selectedModule, setting));
								sOffset += 15;
							}
							if (setting instanceof NumberSetting) {
								comps.add(new Slider(200, sOffset, this, selectedModule, setting));
								sOffset += 25;
							}
							if (setting instanceof BooleanSetting) {
								comps.add(new CheckBox(200, sOffset, this, selectedModule, setting));
								sOffset += 15;
							} 
							if(setting instanceof KeybindSetting) {
								comps.add(new Keybind(200, sOffset, this, selectedModule, setting));
								sOffset += 15;
							}
						}
				}
			}
			offset += 15;
		}
		for (Comp comp : comps) {
			comp.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		dragging = false;
		for (Comp comp : comps) {
			comp.mouseReleased(mouseX, mouseY, state);
		}
	}

	public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
		return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
	}

	public ScaledResolution getScaledRes() {
		return new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
	}
	
	@Override
	public void onGuiClosed() {
		try {
	        config.store(new FileOutputStream("Raze/config.properties"), "Raze ClickGUI");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}
