package me.eldodebug.soar.gui.clickgui.category.impl;

import java.awt.Color;
import java.io.File;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.gui.GuiTransparentField;
import me.eldodebug.soar.gui.clickgui.category.Category;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.utils.ClientUtils;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils.Scroll;
import me.eldodebug.soar.utils.render.RoundedUtils;

public class ConfigCategory extends Category {

	private GuiTransparentField configNameField;
    private SimpleAnimation scrollAnimation = new SimpleAnimation(0.0F);
    private boolean canToggle;
    
    private File removeFile;
    private boolean removed;
    
	public ConfigCategory() {
		super("Config");
	}

	@Override
	public void initGui() {
		configNameField = new GuiTransparentField(1, mc.fontRendererObj, (int) this.getX() + 95, (int) this.getY() + 207, 135, 20, ColorUtils.getFontColor(1).getRGB());
		canToggle = false;
		removed = true;
		removeFile = null;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		int offsetY = 15;
		
		Soar.instance.configManager.loadConfigs();
		
    	//Draw config list
    	for(File f : Soar.instance.configManager.getConfigs()) {
    		
    		String configName = f.getName();
    		int MAX_CHAR = 34;
    		int maxLength = (configName.length() < MAX_CHAR) ? configName.length() : MAX_CHAR;
    		configName = configName.substring(0, maxLength).replace(".txt", "");
			
    		RoundedUtils.drawRound((float) this.getX() + 95, (float) (this.getY() + offsetY + scrollAnimation.getValue()), 200, 26, 8, ColorUtils.getBackgroundColor(4));
    		FontUtils.regular20.drawString(configName, this.getX() + 105, this.getY() + offsetY + 11 + scrollAnimation.getValue(), ColorUtils.getFontColor(2).getRGB());

            FontUtils.icon20.drawString("M", this.getX() + 275, this.getY() + offsetY + 11, new Color(255, 20, 20).getRGB());
    		offsetY+= 35;
    	}
    	
		//Under bar
    	RoundedUtils.drawRound((float) this.getX() + 80, (float) this.getY() + 200, 225, 30 + 0.5F, 6, ColorUtils.getBackgroundColor(1));
    	RoundedUtils.drawRound((float) this.getX() + 80, (float) this.getY() + 200, 228, 30F, 0, ColorUtils.getBackgroundColor(1));

    	//Search bar
    	RoundedUtils.drawRound(this.getX() + 95, this.getY() + 205, 135, 21, 6, ColorUtils.getBackgroundColor(4));
    	configNameField.drawTextBox();
    	
		configNameField.xPosition = (int) (this.getX() + 95);
		configNameField.yPosition = (int) (this.getY() + 207);
		
    	//Save bar
    	RoundedUtils.drawRound(this.getX() + 235, this.getY() + 205, 60, 21, 6, ColorUtils.getBackgroundColor(4));
    	FontUtils.regular20.drawString("Save", this.getX() + 254, this.getY() + 213, ColorUtils.getFontColor(1).getRGB());
    	
        final Scroll scroll = MouseUtils.scroll();

        if(scroll != null && Soar.instance.configManager.getConfigs().size() > 5) {
        	switch (scroll) {
        	case DOWN:
        		if(Soar.instance.configManager.getScrollY() > -((Soar.instance.configManager.getConfigs().size() - 5.5) * 35)) {
        			Soar.instance.configManager.setScrollY(Soar.instance.configManager.getScrollY() - 20);
        		}
        		
        		if(Soar.instance.configManager.getScrollY() < -((Soar.instance.configManager.getConfigs().size() - 6) * 35)) {
        			Soar.instance.configManager.setScrollY(-((Soar.instance.configManager.getConfigs().size() - 5.2) * 35));
        		}
        		break;
            case UP:
        		if(Soar.instance.configManager.getScrollY() < -10) {
        			Soar.instance.configManager.setScrollY(Soar.instance.configManager.getScrollY() + 20);
        		}else {
            		if(Soar.instance.configManager.getConfigs().size() > 5) {
            			Soar.instance.configManager.setScrollY(0);
            		}
        		}
        		break;
        	}
        }
        
        scrollAnimation.setAnimation((float) Soar.instance.configManager.getScrollY(), 16);
        
    	if(MouseUtils.isInside(mouseX, mouseY, this.getX() + 80, this.getY(), 220, 199)) {
    		canToggle = true;
    	}else {
    		canToggle = false;
    	}
    	
    	if(!removed && removeFile != null) {
    		removed = true;
    		removeFile.delete();
    		removeFile = null;
    	}
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
    	int offsetY = 15;
    	
		configNameField.mouseClicked(mouseX, mouseY, mouseButton);
		
    	for(File f : Soar.instance.configManager.getConfigs()) {
    		
    		if(MouseUtils.isInside(mouseX, mouseY, (float) this.getX() + 95, (float) (this.getY() + offsetY + scrollAnimation.getValue()), 165, 26) && mouseButton == 0 && canToggle) {
    			
        		for(Mod m : Soar.instance.modManager.getMods()) {
        			m.setToggled(false);
        		}
        		
    			Soar.instance.configManager.load(f);
    			ClientUtils.showNotification("Config", "Config has been successfully loaded!");
    		}
    		if(MouseUtils.isInside(mouseX, mouseY, (float) this.getX() + 260, (float) (this.getY() + offsetY + scrollAnimation.getValue()), 35, 26) && mouseButton == 0) {
    			removed = false;
    			removeFile = f;
    		}
    		offsetY+= 35;
    	}
		
		if(MouseUtils.isInside(mouseX, mouseY, this.getX() + 235, this.getY() + 205, 60, 21) && mouseButton == 0) {
			
			String configName = configNameField.getText();
			
			Soar.instance.configManager.save(new File(Soar.instance.fileManager.getConfigDir(), configName + ".txt"));
			
			configNameField.setText("");
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		
	}
	
	@Override
	public void updateScreen() {
		configNameField.updateCursorCounter();
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		configNameField.textboxKeyTyped(typedChar, keyCode);
	}
}
