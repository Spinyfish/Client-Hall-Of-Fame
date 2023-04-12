package markgg.modules.render;

import java.awt.Color;
import java.util.List;

import org.lwjgl.input.Keyboard;

import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventKey;
import markgg.events.listeners.EventRenderGUI;
import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.KeybindSetting;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.settings.Setting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class TabGUI
extends Module
{
	public int currentTab;
	public boolean expanded;

	public TabGUI() {
		super("TabGUI", "The TabGUI", 0, Module.Category.RENDER);
	}

	public static int getRainbow(float seconds, float saturation, float brightness) {
		float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000);
		int color = Color.HSBtoRGB(hue, saturation, brightness);
		return color;
	}
	
	public void onEvent(Event e) {
		if (e instanceof EventRenderGUI) {
			FontRenderer fr = this.mc.fontRendererObj;

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


			Gui.drawRect(5.0D, 30.5D, 70.0D, (30 + (Module.Category.values()).length * 16) + 1.5D, -1879048192);
			Gui.drawRect(7.0D, (33 + this.currentTab * 16), 68.0D, (33 + this.currentTab * 16 + 12), primaryColor);

			int count = 0; byte b; int i; Module.Category[] arrayOfCategory;
			for (i = (arrayOfCategory = Module.Category.values()).length, b = 0; b < i; ) { Module.Category c = arrayOfCategory[b];
			fr.drawStringWithShadow(c.name, 11.0D, (35 + count * 16), -1);

			count++;
			b++; }

			if (this.expanded) {
				Module.Category category = Module.Category.values()[this.currentTab];
				List<Module> modules = Client.getModulesByCategory(category);

				if (modules.size() == 0) {
					return;
				}
				Gui.drawRect(70.0D, 30.5D, 138.0D, (30 + modules.size() * 16) + 1.5D, -1879048192);
				Gui.drawRect(70.0D, (33 + category.moduleIndex * 16), 136.0D, (33 + category.moduleIndex * 16 + 12), primaryColor);

				count = 0;
				for (Module m : modules) {
					fr.drawStringWithShadow(m.name, 73.0D, (35 + count * 16), -1);

					if (count == category.moduleIndex && m.expanded) {

						int index = 0, maxLength = 0;
						for (Setting setting : m.settings) {

							if (setting instanceof BooleanSetting) {
								BooleanSetting bool = (BooleanSetting)setting;
								if(maxLength < fr.getStringWidth(String.valueOf(setting.name) + ": " + bool.enabled)) {
									maxLength = fr.getStringWidth(String.valueOf(setting.name) + ": " + bool.enabled);
								}
							}

							if (setting instanceof NumberSetting) {
								NumberSetting number = (NumberSetting)setting;
								if(maxLength < fr.getStringWidth(String.valueOf(setting.name) + ": " + number.getValue())){
									maxLength = fr.getStringWidth(String.valueOf(setting.name) + ": " + number.getValue());
								}
							}

							if (setting instanceof ModeSetting) {
								ModeSetting mode = (ModeSetting)setting;
								if(maxLength < fr.getStringWidth(String.valueOf(setting.name) + ": " + mode.getMode())) {
									maxLength = fr.getStringWidth(String.valueOf(setting.name) + ": " + mode.getMode());
								}
							}

							if (setting instanceof KeybindSetting) {
								KeybindSetting keyBind = (KeybindSetting)setting;
								if(maxLength < fr.getStringWidth(String.valueOf(setting.name) + ": " + Keyboard.getKeyName(keyBind.code))) {
									maxLength = fr.getStringWidth(String.valueOf(setting.name) + ": " + Keyboard.getKeyName(keyBind.code));
								}
							}

							index++;
						} 

						if (!m.settings.isEmpty()) {
							Gui.drawRect(138.0D, 30.5D, (138 + maxLength + 9), (30 + m.settings.size() * 16) + 1.5D, -1879048192);
							Gui.drawRect(140.0D, (33 + m.index * 16), (136 + maxLength + 9), (33 + m.index * 16 + 12), ((Setting)m.settings.get(m.index)).focused ? secondaryColor : primaryColor);
							index = 0;
							for (Setting setting : m.settings) {

								if (setting instanceof BooleanSetting) {
									BooleanSetting bool = (BooleanSetting)setting;
									fr.drawStringWithShadow(String.valueOf(setting.name) + ": " + bool.enabled, 142.0D, (35 + index * 16), -1);
								}
								if (setting instanceof NumberSetting) {
									NumberSetting number = (NumberSetting)setting;
									fr.drawStringWithShadow(String.valueOf(setting.name) + ": " + number.getValue(), 142.0D, (35 + index * 16), -1);
								}
								if (setting instanceof ModeSetting) {
									ModeSetting mode = (ModeSetting)setting;
									fr.drawStringWithShadow(String.valueOf(setting.name) + ": " + mode.getMode(), 142.0D, (35 + index * 16), -1);
								}
								if (setting instanceof KeybindSetting) {
									KeybindSetting keyBind = (KeybindSetting)setting;
									fr.drawStringWithShadow(String.valueOf(setting.name) + ": " + Keyboard.getKeyName(keyBind.code), 142.0D, (35 + index * 16), -1);
								}
								index++;
							} 
						} 
					} 

					count++;
				} 
			} 
		} 

		if (e instanceof EventKey) {
			int code = ((EventKey)e).code;

			Module.Category category = Module.Category.values()[this.currentTab];
			List<Module> modules = Client.getModulesByCategory(category);

			if (this.expanded && !modules.isEmpty() && ((Module)modules.get(category.moduleIndex)).expanded) {
				Module module = modules.get(category.moduleIndex);
				if (!module.settings.isEmpty() && ((Setting)module.settings.get(module.index)).focused && module.settings.get(module.index) instanceof KeybindSetting && code != 28 && code != 200 && code != 208 && code != 203 && code != 205 && code != 1) {
					KeybindSetting keyBind = (KeybindSetting) module.settings.get(module.index);

					keyBind.code = code;
					keyBind.focused = false;


					return;
				}
			} 

			if (code == 200) {
				if (this.expanded) {
					if (this.expanded && !modules.isEmpty() && ((Module)modules.get(category.moduleIndex)).expanded) {
						Module module = modules.get(category.moduleIndex);

						if (!module.settings.isEmpty()) {
							if (((Setting)module.settings.get(module.index)).focused) {
								Setting setting = module.settings.get(module.index);

								if (setting instanceof NumberSetting) {
									((NumberSetting)setting).increment(true);
								}
							}
							else if (module.index <= 0) {
								module.index = module.settings.size() - 1;
							} else {
								module.index--;
							}

						}
					} else if (category.moduleIndex <= 0) {
						category.moduleIndex = modules.size() - 1;
					} else {
						category.moduleIndex--;
					}

				} else if (this.currentTab <= 0) {
					this.currentTab = (Module.Category.values()).length - 1;
				} else {
					this.currentTab--;
				} 
			}


			if (code == 208) {
				if (this.expanded) {
					if (this.expanded && !modules.isEmpty() && ((Module)modules.get(category.moduleIndex)).expanded) {
						Module module = modules.get(category.moduleIndex);
						if (!module.settings.isEmpty()) {
							if (((Setting)module.settings.get(module.index)).focused) {
								Setting setting = module.settings.get(module.index);

								if (setting instanceof NumberSetting) {
									((NumberSetting)setting).increment(false);
								}
							}
							else if (module.index >= module.settings.size() - 1) {
								module.index = 0;
							} else {
								module.index++;
							}

						}
					} else if (category.moduleIndex >= modules.size() - 1) {
						category.moduleIndex = 0;
					} else {
						category.moduleIndex++;
					}

				} else if (this.currentTab >= (Module.Category.values()).length - 1) {
					this.currentTab = 0;
				} else {
					this.currentTab++;
				} 
			}


			if (code == 28 && 
					this.expanded && modules.size() != 0) {
				Module module = modules.get(category.moduleIndex);

				if (!module.expanded && !module.settings.isEmpty()) {
					module.expanded = true;
				} else if (module.expanded && !module.settings.isEmpty()) {
					((Setting)module.settings.get(module.index)).focused = !((Setting)module.settings.get(module.index)).focused;
				} 
			} 


			if (code == 205) {
				if (this.expanded && modules.size() != 0) {
					Module module = modules.get(category.moduleIndex);

					if (this.expanded && !modules.isEmpty() && module.expanded) {
						if (!module.settings.isEmpty()) {
							Setting setting = module.settings.get(module.index);

							if (setting instanceof BooleanSetting) {
								((BooleanSetting)setting).toggle();
							}
							if (setting instanceof ModeSetting) {
								((ModeSetting)setting).cycle();
							}
						}

					} else if (!module.name.equals("TabGUI")) {
						module.toggle();
					} 
				} else {

					this.expanded = true;
				} 
			}

			if (code == 203)
				if (this.expanded && !modules.isEmpty() && ((Module)modules.get(category.moduleIndex)).expanded) {
					Module module = modules.get(category.moduleIndex);

					if (!module.settings.isEmpty() && 
							!((Setting)module.settings.get(module.index)).focused)
					{

						((Module)modules.get(category.moduleIndex)).expanded = false;
					}
				} else {

					this.expanded = false;
				}  
		} 
	}
}
