package haptic.ui.click.vestige;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import haptic.Haptic;
import haptic.font.FontUtil;
import haptic.font.MinecraftFontRenderer;
import haptic.module.Category;
import haptic.module.Module;
import haptic.module.impl.render.ClickGuiModule;
import haptic.setting.Setting;
import haptic.setting.impl.BooleanSetting;
import haptic.setting.impl.KeybindSetting;
import haptic.setting.impl.ModeSetting;
import haptic.setting.impl.NumberSetting;
import haptic.util.base.BaseUtil;
import haptic.util.misc.TimerUtil;
import haptic.util.render.ColorUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class HapticClickGUI extends GuiScreen {
	
	public final int categoryOffset = 20;
	public final int moduleOffset = 18;
	public final int settingOffset = 14;
	public final int settingShadowOffset = 0;
	
	private boolean holdingMouseButton;
	
	private Module keybindFocusedModule;
	
	private TimerUtil timer;
	
	public HapticClickGUI() {
		timer = new TimerUtil();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution sr = new ScaledResolution(mc);
		
		for(Category c : Category.values()) {
			
			int startX = c.getX();
			int startY = c.getY();
			
			int endX = startX + 100;
			int endY = startY + categoryOffset;
			
			drawCategory(c, startX, startY, endX, endY);
			
			int currentOffsetY = startY + categoryOffset;
			
			boolean firstModule = true;
			
			if(c.isOpen()) {
				for(Module m : Haptic.getModuleManager().getModules()) {
					if(m.getCategory() == c) {
						
						int moduleStartX = startX;
						int moduleEndX = endX;
						
						int moduleStartY = currentOffsetY;
						int moduleEndY = currentOffsetY + moduleOffset;
						
						drawModule(m, moduleStartX, moduleStartY, moduleEndX, moduleEndY);
						
						if(firstModule && m.isEnabled()) {
							this.drawGradientRect(moduleStartX, moduleStartY, moduleEndX, moduleStartY + 3, 0x60000000, 0x05000000);
						}
						
						currentOffsetY += moduleOffset;
						
						if(m.getSettings() != null && m.isSettingsShowed()) {
							int settingIndex = 0;
							
							for(Setting s : m.getSettings()) {
								if(!holdingMouseButton) {
									s.setHoldingMouseButton(false);
								}
								
								if(s.isShowed()) {
									int settingStartX = startX + 1;
									int settingEndX = endX - 1;
									
									int settingStartY = currentOffsetY;
									int settingEndY = currentOffsetY + settingOffset;
									
									if(settingIndex == 0) {
										Gui.drawRect(settingStartX, settingStartY, settingEndX, settingStartY + settingShadowOffset, new Color(50, 50, 50).getRGB());
										currentOffsetY += settingShadowOffset;
										
										settingStartY = currentOffsetY;
										settingEndY = currentOffsetY + settingOffset;
									}
									
									if(s instanceof KeybindSetting) {
										KeybindSetting k = (KeybindSetting) s;
										
										drawKeybindSetting(k, settingStartX, settingStartY, settingEndX, settingEndY);
									} else if(s instanceof BooleanSetting) {
										BooleanSetting b = (BooleanSetting) s;
										
										drawBooleanSetting(b, settingStartX, settingStartY, settingEndX, settingEndY);
									} else if(s instanceof ModeSetting) {
										ModeSetting mode = (ModeSetting) s;
										
										drawModeSetting(mode, settingStartX, settingStartY, settingEndX, settingEndY);
									} else if(s instanceof NumberSetting) {
										NumberSetting n = (NumberSetting) s;
										
										drawNumberSetting(n, settingStartX, settingStartY, settingEndX, settingEndY, mouseX, mouseY);
									}
									
									currentOffsetY += settingOffset;
									
									settingIndex++;
								} else {
									s.setHoldingMouseButton(false);
								}
							}
						}
						firstModule = false;
					}
				}
			}
		}
		
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		holdingMouseButton = true;
		
		for(Category c : Category.values()) {
			
			int startX = c.getX();
			int startY = c.getY();
			
			int endX = startX + 100;
			int endY = startY + categoryOffset;
			
			if(mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY) {
				onCategoryClicked(c, mouseX, mouseY, button);
			}
			
			int currentOffsetY = startY + categoryOffset;
			
			if(c.isOpen()) {
				for(Module m : Haptic.getModuleManager().getModules()) {
					if(m.getCategory() == c) {
						int moduleStartX = startX;
						int moduleEndX = startX + 100;
						
						int moduleStartY = currentOffsetY;
						int moduleEndY = currentOffsetY + moduleOffset;
						
						if(mouseX >= moduleStartX && mouseX <= moduleEndX && mouseY >= moduleStartY && mouseY <= moduleEndY) {
							onModuleClicked(m, mouseX, mouseY, button);
						}
						
						currentOffsetY += moduleOffset;
						
						if(m.getSettings() != null && m.isSettingsShowed()) {
							int settingIndex = 0;
							
							for(Setting s : m.getSettings()) {
								if(s.isShowed()) {
									int settingStartX = startX + 1;
									int settingEndX = endX - 1;
									
									int settingStartY = currentOffsetY;
									int settingEndY = currentOffsetY + settingOffset;
									
									if(settingIndex == 0) {
										currentOffsetY += settingShadowOffset;
										
										settingStartY = currentOffsetY;
										settingEndY = currentOffsetY + settingOffset;
									}
									
									if(mouseX >= settingStartX && mouseX <= settingEndX && mouseY >= settingStartY && mouseY <= settingEndY) {
										s.setHoldingMouseButton(true);
										if(s instanceof KeybindSetting) {
											KeybindSetting k = (KeybindSetting) s;
											onKeybindSettingClicked(m, k, button);
										} else if(s instanceof BooleanSetting) {
											BooleanSetting b = (BooleanSetting) s;
											
											onBooleanSettingClicked(b);
										} else if(s instanceof ModeSetting) {
											ModeSetting mode = (ModeSetting) s;
											
											onModeSettingClicked(mode, button);
										}
									}
									
									currentOffsetY += settingOffset;
									
									settingIndex++;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void drawCategory(Category c, int startX, int startY, int endX, int endY) {
		MinecraftFontRenderer fr = FontUtil.product_sans;
		
		for(int i = startX; i < endX; i++) {
			Gui.drawRect(i, startY, i + 1, endY, Haptic.getClientColor());
		}
		
		fr.drawStringWithShadow(c.getName(), startX + 5, startY + 6, new Color(240, 240, 240).getRGB());
	}
	
	private void onCategoryClicked(Category c, int mouseX, int mouseY, int button) {
		if(button == 1) {
			c.setOpen(!c.isOpen());
		}
	}
	
	private void drawModule(Module m, int startX, int startY, int endX, int endY) {
		MinecraftFontRenderer fr = FontUtil.product_sans;
		
		if(m.isEnabled()) {
			for(int i = startX; i < endX; i++) {
				Gui.drawRect(i, startY, i + 1, endY, Haptic.getClientColor());
			}
		} else {
			Gui.drawRect(startX, startY, endX, endY, new Color(42, 42, 42).getRGB());
		}
		
		if(m.getSettings().size() > 1) {
			double Xstart = endX - 11;
			double Xend = endX - 5;
			double Ystart = startY + 7;
			double Yend = startY + 12;
			Gui.drawRect(Xstart, Ystart, Xend, Ystart + 0.5, new Color(225, 225, 225).getRGB());
			Gui.drawRect(Xstart + 0.5, Ystart + 0.5, Xend - 0.5, Ystart + 1, new Color(225, 225, 225).getRGB());
			Gui.drawRect(Xstart + 1, Ystart + 1, Xend - 1, Ystart + 1.5, new Color(225, 225, 225).getRGB());
			Gui.drawRect(Xstart + 1.5, Ystart + 1.5, Xend - 1.5, Ystart + 2, new Color(225, 225, 225).getRGB());
			Gui.drawRect(Xstart + 2, Ystart + 2, Xend - 2, Ystart + 2.5, new Color(225, 225, 225).getRGB());
			Gui.drawRect(Xstart + 2.5, Ystart + 2.5, Xend - 2.5, Ystart + 3, new Color(225, 225, 225).getRGB());
		}
		
		fr.drawStringWithShadow(m.getName(), startX + 4, startY + 5, new Color(240, 240, 240).getRGB());
	}
	
	private void onModuleClicked(Module m, int mouseX, int mouse, int button) {
		if(button == 0) {
			if(mc.thePlayer != null) {
				m.toggle();
			} else {
				m.toggleSilently();
			}
		} else if(button == 1) {
			m.setSettingsShowed(!m.isSettingsShowed());
		}
	}
	
	private void drawBooleanSetting(BooleanSetting s, int startX, int startY, int endX, int endY) {
		MinecraftFontRenderer fr = FontUtil.product_sans;
		
		Gui.drawRect(startX, startY, endX, endY, new Color(50, 50, 50).getRGB());
		
		int color = s.isEnabled() ? new Color(20, 210, 20).getRGB() : new Color(240, 240, 240).getRGB();
		
		fr.drawStringWithShadow(s.getName(), startX + 5, startY + 3, color);
	}
	
	private void onBooleanSettingClicked(BooleanSetting s) {
		s.setEnabled(!s.isEnabled());
	}
	
	private void drawModeSetting(ModeSetting s, int startX, int startY, int endX, int endY) {
		MinecraftFontRenderer fr = FontUtil.product_sans;
		
		Gui.drawRect(startX, startY, endX, endY, new Color(50, 50, 50).getRGB());
		
		int color = new Color(240, 240, 240).getRGB();
		
		fr.drawStringWithShadow(s.getName() + " : " + s.getMode(), startX + 5, startY + 3, color);
	}
	
	private void onModeSettingClicked(ModeSetting s, int button) {
		if(button == 0) {
			s.increment();
		} else if(button == 1) {
			s.decrement();
		}
	}
	
	private void drawNumberSetting(NumberSetting s, int startX, int startY, int endX, int endY, int mouseX, int mouseY) {
		MinecraftFontRenderer fr = FontUtil.product_sans;
		
		double lenght = endX - startX;
		
		double numberX = startX + ((s.getValue() - s.getMinimum()) * lenght / (s.getMaximum() - s.getMinimum()));
		
		Gui.drawRect(startX, startY, endX, endY, new Color(50, 50, 50).getRGB());
		
		Gui.drawRect(startX, startY, (int) numberX, endY, new Color(25, 25, 25).getRGB());
		
		fr.drawStringWithShadow(s.getName() + " : " + s.getValue(), startX + 4, startY + 3, new Color(240, 240, 240).getRGB());
		
		if(s.isHoldingMouseButton()) {
			onNumberSettingHold(s, startX, endX, mouseX, mouseY);
		}
	}
	
	private void onNumberSettingHold(NumberSetting s, int startX, int endX, int mouseX, int mouseY) {
		double lenght = endX - startX;
		
		double mousePos = mouseX - startX;
		double thing = (mousePos / lenght);
		
		s.setValue(thing * (s.getMaximum() - s.getMinimum()) + s.getMinimum());
	}
	
	private void drawKeybindSetting(KeybindSetting s, int startX, int startY, int endX, int endY) {
		MinecraftFontRenderer fr = FontUtil.product_sans;
		
		Gui.drawRect(startX, startY, endX, endY, new Color(50, 50, 50).getRGB());
		
		fr.drawStringWithShadow(s.getName() + " : " + s.getKeyName(), startX + 5, startY + 3, new Color(240, 240, 240).getRGB());
	}
	
	private void onKeybindSettingClicked(Module m, KeybindSetting s, int button) {
		if(keybindFocusedModule == null) {
			keybindFocusedModule = m;
		} else {
			keybindFocusedModule = null;
			if(button == 2) {
				s.setKey(0);
			}
		}
	}
	
	private void onKeyboardPressed(int keyCode) {
		if(keybindFocusedModule != null) {
        	keybindFocusedModule.getKeybind().setKey(keyCode);
        	keybindFocusedModule = null;
        }
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1) {
            this.mc.displayGuiScreen((GuiScreen)null);
            if(this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
            onClickGuiDisabled();
        }
        
        onKeyboardPressed(keyCode);
    }
	
	@Override
	public void onGuiClosed() {
		onClickGuiDisabled();
    }
	
	private void onClickGuiDisabled() {
		Haptic.getModuleManager().getModuleByName("ClickGUI").setEnabled(false);
        keybindFocusedModule = null;
        
        holdingMouseButton = false;
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		holdingMouseButton = false;
    }
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
}