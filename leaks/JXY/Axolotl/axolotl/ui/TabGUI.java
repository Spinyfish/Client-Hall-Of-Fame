package axolotl.ui;

import org.lwjgl.input.Keyboard;

import axolotl.Axolotl;
import axolotl.cheats.modules.Module;
import axolotl.cheats.modules.Module.Category;
import axolotl.cheats.settings.KeybindSetting;
import axolotl.cheats.settings.Setting;
import axolotl.cheats.settings.Settings;
import axolotl.cheats.settings.SpecialSettings;
import font.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.Display;

//import viamcp.ViaMCP;
//import viamcp.protocols.ProtocolCollection;
@SuppressWarnings("all")
public class TabGUI {

	public int categoryValue = 0, inCategoryValue = 0, inCategoryAmount = 0, inSettingsValue = 0, inSettingsAmount = 0;
	public boolean expanded = false, inSettings = false;
	public Module hoveredModule;
	public Setting hoveredSetting;
	public Minecraft mc = Minecraft.getMinecraft();
	public int textColor = 0xFFFFFFFF, backgroundColor = 0xFF000000, focusedColor = 0xFFC0CB;
	public String var1 = "se";
	
	public void draw(CFontRenderer fr2, ScaledResolution sr) {
		textColor = 0x80331000;
		backgroundColor = 0x90ff5757;
		focusedColor = 0x60000000;
		int selectedColor = 0xF0ff6666;
		int focusedText = 0xFFC2CB;

		if(Axolotl.INSTANCE.hud.hud == null)return;

		if(!Axolotl.INSTANCE.hud.hud.toggled)return;

		int w = Axolotl.INSTANCE.width;
		int h = Axolotl.INSTANCE.height;

		int length = 0;
		int height = 21 + (Category.values().length * 22);
		for(Category c : Category.values()) {

			if(fr2.getStringWidth(c.name) > length) {

				length = fr2.getStringWidth(c.name);
				
			}
			
		}
		length += 8;
		Gui.drawRect(5, 75, length, height, textColor);
		Gui.drawRect(6, 78 + (categoryValue * 12), length - 1, 78 + (categoryValue * 12) + 12, focusedColor);
		
		if(expanded) {
			Category c = null;
			int count = 0;
			int count2 = 0;
			int length2 = 0;
			for(Category categories : Category.values()) {
				if(count == categoryValue) {
					c = categories;
					break;
				}
				count++;
			}
			if(c == null)return;
			for(Module m : Axolotl.INSTANCE.moduleManager.modules) {
				
				if(fr2.getStringWidth(m.name) > length2 && m.category == c) {
					
					length2 = fr2.getStringWidth(m.name);
					
				}
				
			}
			length2 += 25 + length;
			Gui.drawRect(length + 1, 78 + (inCategoryValue * 12), length2 - 1, 78 + (inCategoryValue * 12) + 12, focusedColor);
			for(Module m : Axolotl.INSTANCE.moduleManager.modules) {
				if(m.category == c) {
					int height2 = 91 + (count2 * 12);
					Gui.drawRect(length, 78 + (count2 * 12), length2, height2, textColor);
					fr2.drawString(m.name, length + 6, 80 + (count2 * 12), focusedText);
					if(count2 == inCategoryValue) {
						hoveredModule = m;
					}
					count2++;
				}
			}
			inCategoryAmount = count2;
			int height2 = 81 + (count2 * 12);
			Gui.drawRect(length, 75 + (count2 * 12), length2, height2, textColor);

		}
		
		int count = 0;
		
		for(Category c : Category.values()) {
			GlStateManager.color(255, 194, 203);
			fr2.drawString(c.name, 7, 80 + (count * 12), focusedText);
			count++;
			
		}
		
		/*int w2 = image.getWidth();
		int h2 = image.getHeight();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glImageID);
		GL11.glTranslatef(0, 0, 0);
		GL11.glColor4f(0, 0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(-w2 / 2, -h2 / 2);
			
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(w2 / 2, -h2 / 2);

			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(w2 / 2, h2 / 2);
			
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(-w2 / 2, h2 / 2);
			
		GL11.glEnd();
		GL11.glFlush();
		GL11.glTranslatef(0, 0, 0);*/
	}

	private int specialSettingAmount(Settings list) {
		int amount = 0;
		for(Setting s : list.getSettings()) {
			if(s instanceof SpecialSettings)
				amount++;
		}
		return amount;
	}

	public void keyPress(int key) {
		
		switch(key) {
		
			case Keyboard.KEY_RETURN:
				if(expanded && hoveredModule != null) {
					try {
						hoveredModule.toggle();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				break;
		
			case Keyboard.KEY_RIGHT:
				if(!expanded) {
					expanded = true;
					inCategoryValue = 0;
				}
				break;
			case Keyboard.KEY_LEFT:
				expanded = false;
				break;
		
			case Keyboard.KEY_UP:
				if(expanded) {
					if(--inCategoryValue < 0) inCategoryValue = inCategoryAmount - 1;
					break;
				}
				if(--categoryValue < 0) categoryValue = Category.values().length - 1;
				break;
		
			case Keyboard.KEY_DOWN:
				if(expanded) {
					if(++inCategoryValue >= inCategoryAmount) inCategoryValue = 0;
					break;
				}
				if(++categoryValue >= Category.values().length) categoryValue = 0;
				break;
				
			case Keyboard.KEY_F12:
				Axolotl.INSTANCE.sendMessage(Axolotl.INSTANCE.clientOn ? "Self destructed axolotl. (F12)" : "Re built axolotl. (F12)");
				Axolotl.INSTANCE.clientOn = !Axolotl.INSTANCE.clientOn;
				Display.setTitle(Axolotl.INSTANCE.clientOn ? Axolotl.INSTANCE.full_name + " - " + mc.session.getUsername() + " - 1.8": "Minecraft 1.8");
				for(Module m : Axolotl.INSTANCE.moduleManager.modules) {
					m.toggled = false;
				}
				break;
				
			default:
				
				if(inSettings) {
					if(hoveredSetting != null && hoveredSetting.focused) {
						if(hoveredSetting instanceof KeybindSetting) {
							hoveredSetting.focused = false;
							((KeybindSetting)hoveredSetting).setCode(key);
						}
					}
				}
				
				break;
		
		}
		
	}
	
}
