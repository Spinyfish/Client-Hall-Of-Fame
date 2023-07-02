package me.eldodebug.soar.gui.clickgui.category.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.media.MediaPlayer.Status;
import me.eldodebug.soar.Soar;
import me.eldodebug.soar.gui.clickgui.ClickGUI;
import me.eldodebug.soar.gui.clickgui.category.Category;
import me.eldodebug.soar.gui.clickgui.category.setting.SettingSlider;
import me.eldodebug.soar.management.mods.impl.ClientMod;
import me.eldodebug.soar.management.music.Music;
import me.eldodebug.soar.management.settings.Setting;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils.Scroll;
import me.eldodebug.soar.utils.render.RoundedUtils;

public class MusicPlayerCategory extends Category{

    private SimpleAnimation scrollAnimation = new SimpleAnimation(0.0F);
	private SettingSlider volumeSlider = new SettingSlider();
	private boolean canToggle;
	
	public MusicPlayerCategory() {
		super("Music Player");
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	int offsetY = 15;
    	Setting volumeSetting = Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "Volume");
    	boolean isRandom = Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "Random").getValBoolean();
    	boolean isLoop = Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "Loop").getValBoolean();
        ClickGUI clickGUI = Soar.instance.guiManager.getClickGUI();
        
    	Soar.instance.musicManager.loadMusic();
    	
    	for(Music m : Soar.instance.musicManager.getMusics()) {
    		
    		String musicName = m.getName();
    		int MAX_CHAR = 34;
    		int maxLength = (musicName.length() < MAX_CHAR) ? musicName.length() : MAX_CHAR;
			musicName = musicName.substring(0, maxLength);
			
			if(clickGUI.searchMode ? StringUtils.containsIgnoreCase(m.getName(), clickGUI.searchWord.getText()) : true) {
	    		RoundedUtils.drawRound((float) this.getX() + 95, (float) (this.getY() + offsetY + scrollAnimation.getValue()), 200, 26, 8, ColorUtils.getBackgroundColor(4));
	    		FontUtils.regular20.drawString(musicName, this.getX() + 105, this.getY() + offsetY + 11 + scrollAnimation.getValue(), ColorUtils.getFontColor(2).getRGB());
	    		offsetY+= 35;
			}
    	}
    	
    	RoundedUtils.drawRound((float) this.getX() + 80, (float) this.getY() + 200, 225, 30 + 0.5F, 6, ColorUtils.getBackgroundColor(1));
    	RoundedUtils.drawRound((float) this.getX() + 80, (float) this.getY() + 200, 228, 30F, 0, ColorUtils.getBackgroundColor(1));
    	
    	RoundedUtils.drawRound((float) (this.getX() + (this.getX() + this.getWidth())) / 2 + 33, (float) this.getY() + 205, 20, 20, 10, ColorUtils.getBackgroundColor(4));
    	
    	String icon = "B";
    	
    	if(Soar.instance.musicManager.getCurrentMusic() != null && Soar.instance.musicManager.getCurrentMusic().mediaPlayer != null) {
			if(Soar.instance.musicManager.getCurrentMusic().mediaPlayer.getStatus().equals(Status.PLAYING)) {
				icon = "C";
				Soar.instance.musicManager.getCurrentMusic().setVolume();
			}else {
				icon = "B";
			}
    	}
    	
		FontUtils.icon24.drawString(icon, (this.getX() + (this.getX() + this.getWidth())) / 2 + (icon.equals("B") ? 38 : 37), this.getY() + 212, ColorUtils.getFontColor(2).getRGB());
		
    	RoundedUtils.drawRound((float) (this.getX() + (this.getX() + this.getWidth())) / 2 + 63, (float) this.getY() + 205, 20, 20, 10, ColorUtils.getBackgroundColor(4));
    	FontUtils.icon24.drawString("G", (this.getX() + (this.getX() + this.getWidth())) / 2 + 68, this.getY() + 212, isRandom ? ColorUtils.getClientColor(0).getRGB() : ColorUtils.getFontColor(2).getRGB());
    	
    	RoundedUtils.drawRound((float) (this.getX() + (this.getX() + this.getWidth())) / 2 + 93, (float) this.getY() + 205, 20, 20, 10, ColorUtils.getBackgroundColor(4));
    	FontUtils.icon24.drawString("F", (this.getX() + (this.getX() + this.getWidth())) / 2 + 97, this.getY() + 212, isLoop ? ColorUtils.getClientColor(0).getRGB() : ColorUtils.getFontColor(2).getRGB());
    	
    	String volumeIcon = "";
    	
    	if(volumeSetting.getValDouble() < 0.5 && volumeSetting.getValDouble() != 0.0) {
    		volumeIcon = "J";
    	}else if(volumeSetting.getValDouble() > 0.5) {
    		volumeIcon = "K";
    	}else if(volumeSetting.getValDouble() == 0.0) {
    		volumeIcon = "I";
    	}
    	
		FontUtils.icon24.drawString(volumeIcon, this.getX() + 95, (this.getY() + this.getHeight()) - 18, ColorUtils.getFontColor(2).getRGB());
    	
    	RoundedUtils.drawRound((float) (this.getX() + (this.getX() + this.getWidth())) / 2 + 123, (float) this.getY() + 205, 20, 20, 10, ColorUtils.getBackgroundColor(4));
    	FontUtils.icon24.drawString("L", (this.getX() + (this.getX() + this.getWidth())) / 2 + 127, this.getY() + 212, ColorUtils.getFontColor(2).getRGB());
    	
        final Scroll scroll = MouseUtils.scroll();

        if(scroll != null && Soar.instance.musicManager.getMusics().size() > 5) {
        	switch (scroll) {
        	case DOWN:
        		if(Soar.instance.musicManager.getScrollY() > -((Soar.instance.musicManager.getMusics().size() - 5.5) * 35)) {
        			Soar.instance.musicManager.setScrollY(Soar.instance.musicManager.getScrollY() - 20);
        		}
        		
        		if(Soar.instance.musicManager.getScrollY() < -((Soar.instance.musicManager.getMusics().size() - 6) * 35)) {
        			Soar.instance.musicManager.setScrollY(-((Soar.instance.musicManager.getMusics().size() - 5.2) * 35));
        		}
        		break;
            case UP:
        		if(Soar.instance.musicManager.getScrollY() < -10) {
        			Soar.instance.musicManager.setScrollY(Soar.instance.musicManager.getScrollY() + 20);
        		}else {
            		if(Soar.instance.musicManager.getMusics().size() > 5) {
            			Soar.instance.musicManager.setScrollY(0);
            		}
        		}
        		break;
        	}
        }
        
        scrollAnimation.setAnimation((float) Soar.instance.musicManager.getScrollY(), 16);
        
    	volumeSlider.setPosition(this.getX() + 188, (this.getY() + this.getHeight()) - 30, 55, volumeSetting);
    	volumeSlider.drawScreen(mouseX, mouseY);
    	
    	if(MouseUtils.isInside(mouseX, mouseY, this.getX() + 80, this.getY(), 220, 199)) {
    		canToggle = true;
    	}else {
    		canToggle = false;
    	}
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
    	int offsetY = 15;
        ClickGUI clickGUI = Soar.instance.guiManager.getClickGUI();
        
    	for(Music m : Soar.instance.musicManager.getMusics()) {
			if(clickGUI.searchMode ? StringUtils.containsIgnoreCase(m.getName(), clickGUI.searchWord.getText()) : true) {
	    		if(MouseUtils.isInside(mouseX, mouseY, this.getX() + 95, this.getY() + offsetY + scrollAnimation.getValue(),  200, 26) && mouseButton == 0 && canToggle) {

					m.playAsyncMusic();
					
					if(Soar.instance.musicManager.getCurrentMusic() == null) {
						Soar.instance.musicManager.setCurrentMusic(m);
					}
					
					if(Soar.instance.musicManager.getCurrentMusic() != m || Soar.instance.musicManager.getCurrentMusic().mediaPlayer != null) {
						Soar.instance.musicManager.getCurrentMusic().mediaPlayer.stop();
						Soar.instance.musicManager.setCurrentMusic(m);
					}
	    		}
	        	offsetY+=35;
			}
    	}
    	
		if(Soar.instance.musicManager.getCurrentMusic() != null) {
			
			if(MouseUtils.isInside(mouseX, mouseY, (this.getX() + (this.getX() + this.getWidth())) / 2 + 33, this.getY() + 205, 20, 20) && mouseButton == 0) {
				if(Soar.instance.musicManager.getCurrentMusic().mediaPlayer.getStatus().equals(Status.PLAYING)) {
					Soar.instance.musicManager.getCurrentMusic().mediaPlayer.pause();
				}else {
					Soar.instance.musicManager.getCurrentMusic().mediaPlayer.play();
				}

			}
		}
		
		if(MouseUtils.isInside(mouseX, mouseY, (this.getX() + (this.getX() + this.getWidth())) / 2 + 63, this.getY() + 205, 20, 20) && mouseButton == 0) {
			Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "Random").setValBoolean(!Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "Random").getValBoolean());
			Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "Loop").setValBoolean(false);
		}
		
		if(MouseUtils.isInside(mouseX, mouseY, (this.getX() + (this.getX() + this.getWidth())) / 2 + 93, this.getY() + 205, 20, 20) && mouseButton == 0) {
			Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "Loop").setValBoolean(!Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "Loop").getValBoolean());
			Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "Random").setValBoolean(false);
		}
		
		if(MouseUtils.isInside(mouseX, mouseY, (this.getX() + (this.getX() + this.getWidth())) / 2 + 123, this.getY() + 205,  20, 20)) {
			try {
				Desktop.getDesktop().open(new File(mc.mcDataDir, "soar/music"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		volumeSlider.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		volumeSlider.mouseReleased(mouseX, mouseY, mouseButton);
	}
}
