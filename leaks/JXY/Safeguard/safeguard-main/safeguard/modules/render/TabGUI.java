
package intentions.modules.render;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventKey;
import intentions.events.listeners.EventRenderGUI;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import intentions.modules.world.ChestStealer;
import intentions.settings.BooleanSetting;
import intentions.settings.KeybindSetting;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.settings.Setting;
import intentions.ui.GuiDescription;
import intentions.ui.GuiSpamText;
import intentions.ui.GuiWatermark;
import intentions.util.ColorUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class TabGUI extends Module {
	
	public int currentTab, e;
	public boolean expanded, q;
	
	public static boolean openTabGUI = true;
	
	public static ModeSetting color = new ModeSetting("Color", "Normal", new String[] {"RGB", "Normal"});

	public TabGUI() {
		super("TabGUI", Keyboard.KEY_NONE, Category.RENDER, "TabGUI", false);
		toggled = true;
		System.out.println("[" + Client.name + "] Rendering TabGUI");
		this.addSettings(color);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRenderGUI) {
			
			if(mc.thePlayer == null || mc.theWorld == null || !openTabGUI) return;
			
			int primaryColor = color.getMode().equalsIgnoreCase("Normal") ? 0x90ff1100 : ColorUtil.getRainbowColor(), secondaryColor = color.getMode().equalsIgnoreCase("Normal") ? 0x90aa1100 : ColorUtil.getDarkRainbowColor();
			
			FontRenderer fr = mc.fontRendererObj;
			
			Gui.drawRect(2, 16.5f, 78, 27 + Module.Category.values().length * 14 + (Category.values().length > 5 ? 2 : 0), 0x90000011);
			Gui.drawRect(2, 32.5f + (currentTab-1) * 16, 1 + 7.2 + 69.6, 37 + (currentTab-1) * 16 + 12, primaryColor);
			
			int count = 0;
			
			for(Category c : Module.Category.values()) {
				fr.drawStringWithShadow(c.name, 8, 21 + count*16, -1);
				
				count++;
			}
			
			if(!expanded)return;
			
			Category category = Module.Category.values()[currentTab];
			List<Module> modules = Client.getModulesByCategory(category);
			
			if(modules.size() == 0)return;
			
			Gui.drawRect(78, 16.5f, 78 + 78, 2 + 15 + modules.size() * 16, 0x90000011);
			Gui.drawRect(78, 32.5f + (category.moduleIndex-1) * 16, 7.2 + 69 + 80, 2 + 35 + (category.moduleIndex-1) * 16 + 12, primaryColor);
			
			count = 0;
			
			for(Module m : Client.getModulesByCategory(Module.Category.values()[currentTab])) {
				fr.drawStringWithShadow(m.name, 8 + 74, 21 + count*16, -1);
				
				if(count == category.moduleIndex && m.expanded) {

					if(m.settings.isEmpty()) return;
					
					if (m.name.equals("Watermark")) {
			            Gui.drawRect(156, 16.5, 244, (20 + (1 + m.settings.size()) * 15 - 1), 0x90000011);
			            fr.drawStringWithShadow("(CTRL)", 160, (21 + (1 + this.index) * 16), -1);
			          } else if (m.name.equals("Spammer")) {
			            Gui.drawRect(156, 16.5, 244, (21 + (1 + m.settings.size()) * 15 - 1), 0x90000011);
			            fr.drawStringWithShadow("(CTRL)", 160, (21 + (3 + this.index) * 16), -1);
			          } else if (m.settings.size() > 1) {
			            if (m.settings.size() >= 4) {
			              Gui.drawRect(156, 16.5, 244, (m.settings.size() - 3 + 21 + m.settings.size() * 15 - 1), 0x90000011);
			            } else {
			              Gui.drawRect(156, 16.5, 244, (20 + ((m.settings.size() == 3) ? 1 : 0) + m.settings.size() * 15 - 1), 0x90000011);
			            } 
			          }
					
					Gui.drawRect(78 + 78, 34.5f + (m.index-1) * 16 - 2, 10 + 7.2 + 69 + 78 + 78 + 2, 35 + (m.index-1) * 16 + 12 + 2, m.settings.get(m.index).focused ? secondaryColor : primaryColor);
					int index = 0;
					for(Setting setting : m.settings) {
						
						if(setting instanceof BooleanSetting) {
							BooleanSetting bool = (BooleanSetting) setting;
							fr.drawStringWithShadow(setting.name + ": " + bool.isEnabled(), 8 + 74 + 78, 21 + index*16, -1);
						}
						if(setting instanceof NumberSetting) {
							NumberSetting num = (NumberSetting) setting;
							fr.drawStringWithShadow(setting.name + ": " + num.getValue(), 8 + 74 + 78, 21 + index*16, -1);
						}
						if(setting instanceof ModeSetting) {
							ModeSetting mode = (ModeSetting) setting;
							fr.drawStringWithShadow(setting.name + ": " + mode.getMode(), 8 + 74 + 78, 21 + index*16, -1);
						}
						if(setting instanceof KeybindSetting) {
							KeybindSetting code = (KeybindSetting) setting;
							fr.drawStringWithShadow(setting.name + ": " + getKeyName(Keyboard.getKeyName(code.code)), 8 + 74 + 78, 21 + index*16, -1);
						}
						
						index++;
					}
					
				}
				
				count++;
			}
		}
		if (e instanceof EventKey) {
			int code = ((EventKey)e).code;

			Category category = Module.Category.values()[currentTab];
			List<Module> modules = Client.getModulesByCategory(category);
			
			
			
			if(code == Keyboard.KEY_F10)
			{
				openTabGUI = !openTabGUI;
				if(!openTabGUI) {
					q=true;
					this.e=0;
					Display.setTitle("Minecraft 1.8");
					for (Module module : Client.toggledModules) {
						module.toggle();
					}
					ChestStealer.autoSteal = new BooleanSetting("AutoSteal", false);

					Client.addChatMessage("Self destructed (F10)");
				}
			}
			
			if(expanded && !modules.isEmpty() && modules.get(category.moduleIndex).expanded) {
				Module module = modules.get(category.moduleIndex);
				
				if(!module.settings.isEmpty() && module.settings.get(module.index).focused && module.settings.get(module.index) instanceof KeybindSetting) {
					if(code != Keyboard.KEY_RIGHT && code != Keyboard.KEY_UP && code != Keyboard.KEY_DOWN && code != Keyboard.KEY_LEFT && code != Keyboard.KEY_ESCAPE) {
						KeybindSetting keyBind = (KeybindSetting)module.settings.get(module.index);
						
						keyBind.code = (code == Keyboard.KEY_RETURN ? 0 : code);
						keyBind.focused = false;
						
						return;
					}
				}
			}
			
		      if (code == 29 || code == 157)
		      {
		          if (this.expanded && ((Module)modules.get(category.moduleIndex)).expanded && ((Module)modules.get(category.moduleIndex)).name.equalsIgnoreCase("Spammer")) {
		            this.mc.displayGuiScreen(new GuiSpamText());
		          } else if (this.expanded && ((Module)modules.get(category.moduleIndex)).expanded && ((Module)modules.get(category.moduleIndex)).name.equalsIgnoreCase("Watermark")) {
		            this.mc.displayGuiScreen(new GuiWatermark());
		          }
		          return;
		      }
			
			if(code == Keyboard.KEY_UP) {
				if (expanded) {
					if (expanded && !modules.isEmpty() && modules.get(category.moduleIndex).expanded){
						Module module = modules.get(category.moduleIndex);
						
						if(module.settings.isEmpty()) return;
						if (module.settings.get(module.index).focused) {
							Setting setting = module.settings.get(module.index);
							
							if (setting instanceof NumberSetting) {
								((NumberSetting)setting).increment(true);
							}
						}else {
							if(module.index <= 0) {
								module.index = module.settings.size() - 1;
							}else
								module.index--;
						}
					} else {
						if(category.moduleIndex <= 0) {
							category.moduleIndex = modules.size() - 1;
						}else
							category.moduleIndex--;
					}
				}else {
					if(currentTab <= 0) {
						currentTab = Module.Category.values().length - 1;
					}else
						currentTab--;
				}
			}
			
			if (code == Keyboard.KEY_DOWN) {
				if (expanded) {
					if (expanded && !modules.isEmpty() && modules.get(category.moduleIndex).expanded){
						Module module = modules.get(category.moduleIndex);
						
						if(module.settings.isEmpty()) return;
						if (module.settings.get(module.index).focused) {
							Setting setting = module.settings.get(module.index);
							
							if (setting instanceof NumberSetting) {
								((NumberSetting)setting).increment(false);
							}
						} else {
							if(module.index >= module.settings.size() - 1) {
								module.index = 0;
							}else
								module.index++;
						}
					}else {
						if(category.moduleIndex >= modules.size() - 1) {
							category.moduleIndex = 0;
						}else
							category.moduleIndex++;
					}
				}else {
					if(currentTab >= Module.Category.values().length - 1) {
						currentTab = 0;
					}else
						currentTab++;
				}
			}
			
			if(code == Keyboard.KEY_RETURN) {
				if (expanded && modules.size() != 0) {
					Module module = modules.get(category.moduleIndex);
					if (module.expanded && !module.settings.isEmpty()) {
						module.settings.get(module.index).focused = !module.settings.get(module.index).focused;
					}else {
						if(expanded && !modules.isEmpty() && modules.get(category.moduleIndex).expanded) {
							
						} else {
							module = modules.get(category.moduleIndex);
							if (module.name.equals("TabGUI") || module == null || module.name.equals("ChestStealer") || module.name.equals("ClickGui"))return;
							module.toggle();
						}
					}
				}else
					expanded = true;
			}
			if(code == Keyboard.KEY_LCONTROL) {
				if(expanded && modules.size() != 0) {
					Module module = modules.get(category.moduleIndex);
					mc.displayGuiScreen(new GuiDescription(module.description, module.name));
				}
			}
			if(code == Keyboard.KEY_RSHIFT) {
				ClickGui.enable = true;
			}
			
			if(code == Keyboard.KEY_RIGHT) {
				if (expanded && modules.size() != 0) {
					Module module = modules.get(category.moduleIndex);
					
					if(module.settings.isEmpty()) return;
					if(expanded && !modules.isEmpty() && !module.settings.isEmpty() && module.expanded && module.settings.get(module.index).focused) {
						Setting setting = module.settings.get(module.index);
						
						if (setting instanceof BooleanSetting) {
							((BooleanSetting)setting).toggle();
						}
						if (setting instanceof ModeSetting) {
							((ModeSetting)setting).cycle();
						}
					} else {
						if(module.expanded == false)
						module.expanded = true;
					}
				}else
					expanded = true;
			}
			
			if (code == Keyboard.KEY_LEFT) {
				Module module = modules.get(category.moduleIndex);
				
				if(module.settings.isEmpty()) {
					if (expanded && !modules.isEmpty() && modules.get(category.moduleIndex).expanded){
						modules.get(category.moduleIndex).expanded = false;
					}else
						expanded = false;
				} else if(!module.settings.get(module.index).focused) {
					if (expanded && !modules.isEmpty() && modules.get(category.moduleIndex).expanded){
						modules.get(category.moduleIndex).expanded = false;
					}else
						expanded = false;
				}
			}
			
		}
		
		else if (e instanceof EventUpdate) 
		{
			
			if(!TabGUI.openTabGUI) {
				if(Keyboard.isKeyDown(Keyboard.KEY_F10) && !q) {
					Display.setTitle(Client.name + " " + Client.version);
					Client.addChatMessage("Re built (F10)");
					openTabGUI = true;
				}
				this.e++;
				if(this.e > 15) {
					q = false;
				}
				return;
			}
			if(Mouse.isButtonDown(1)) {
				
				if(rightClick == false) {
					rightClick = true;
					for(Module m : Client.toggledModules) {
						m.onRightClick();
					}
				}
				
			} else {
				rightClick = false;
			}
		}
	}
	
	private boolean rightClick = false;
	
	public String getKeyName(String key) {
		key = key.
				replace("SEMICOLON", ";").
				replace("APOSTROPHE", "'").
				replace("SUBTRACT", "-").
				replace("ADD", "+").
				replace("NUMLOCK", "NLOCK").
				replace("BACKSLASH", "\\").
				replace("NUMPAD", "NPAD").
				replace("CONTROL", "CTRL");
		return key;
	}
}
