package me.eldodebug.soar.gui.clickgui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.gui.GuiSearchField;
import me.eldodebug.soar.gui.clickgui.category.Category;
import me.eldodebug.soar.gui.clickgui.category.CategoryManager;
import me.eldodebug.soar.gui.clickgui.category.impl.CosmeticCategory;
import me.eldodebug.soar.gui.clickgui.category.impl.FeatureCategory;
import me.eldodebug.soar.gui.clickgui.category.impl.MusicPlayerCategory;
import me.eldodebug.soar.gui.clickgui.category.impl.SettingsCategory;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.impl.EaseBackIn;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.render.ClickEffect;
import me.eldodebug.soar.utils.render.RoundedUtils;
import me.eldodebug.soar.utils.render.StencilUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class ClickGUI extends GuiScreen{

    public Animation introAnimation;
    
	private boolean loaded;
	private boolean loadedCosmetic;
	private boolean dragging;
	public boolean close;
	public CategoryManager categoryManager;
	public Category selectedCategory;
	
	private float x, y, width, height, dragX, dragY;

	private SimpleAnimation categoryAnimation = new SimpleAnimation(35F);
	private SimpleAnimation categoryOpacity = new SimpleAnimation(0);
	private int currentCategoryY;
	
    public SimpleAnimation searchAnimation = new SimpleAnimation(-30.0F);
    public GuiSearchField searchWord;
    public boolean searchMode;
    
    private SimpleAnimation upAnimation = new SimpleAnimation(0.0F);
    
    private List<ClickEffect> clickEffects = new ArrayList<>();
    
	public ClickGUI() {
		
		loaded = false;
		loadedCosmetic = false;
		this.x = 100;
		this.y = 100;
		
		categoryManager = new CategoryManager();
		selectedCategory = categoryManager.getCategoryByClass(FeatureCategory.class);
		
		Soar.instance.eventManager.register(this);
	}
	
	@Override
	public void initGui() {
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		if(!loadedCosmetic) {
			loadedCosmetic = true;
			categoryManager.getCategoryByClass(CosmeticCategory.class).drawScreen(0, 0, 0);
		}
		
		loaded = true;
        introAnimation = new EaseBackIn(450, 1, 2);
        close = false;
        searchWord = new GuiSearchField(1, mc.fontRendererObj, sr.getScaledWidth() / 2 - 95, -1, (sr.getScaledWidth() / 2 - 95) + 190, 15 + 23);
        
		for(Category c : categoryManager.getCategories()) {
			c.initGui();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		ScaledResolution sr = new ScaledResolution(mc);

		int offset = 35;
		int opacity = (int) categoryOpacity.getValue();
		
		if(dragging) {
			x = mouseX - dragX;
			y = mouseY - dragY;
		}
		
		width = 305;
		height = 230;
		
		if(!dragging) {
			if(this.getX() + this.getWidth() > sr.getScaledWidth()) {
				x = sr.getScaledWidth() - ((this.getWidth() + this.getX()) - this.getX()) - 2;
			}
			if(this.getY() + this.getHeight() > sr.getScaledHeight()) {
				y = sr.getScaledHeight() - ((this.getY() + this.getHeight()) - this.getY()) - 2;
			}
			if(x < 0) {
				x = 2;
			}
			if(y < 0) {
				y = 2;
			}
		}
		
		if(close) {
			introAnimation.setDirection(Direction.BACKWARDS);
			if(introAnimation.isDone(Direction.BACKWARDS)) {
				mc.displayGuiScreen(null);
			}
		}
        
		if(!selectedCategory.equals(categoryManager.getCategoryByClass(FeatureCategory.class))) {
    		FeatureCategory.openModSetting = false;
		}
		
		if(selectedCategory.equals(categoryManager.getCategoryByClass(FeatureCategory.class)) || selectedCategory.equals(categoryManager.getCategoryByClass(MusicPlayerCategory.class))) {
            if(!FeatureCategory.openModSetting && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_F)) {
            	searchMode = true;
                searchWord.setFocused(true);
                searchWord.setText("");
                FeatureCategory.scrollY = 0;
                Soar.instance.musicManager.setScrollY(0);
            }
		}
		
		if(FeatureCategory.openModSetting || (!selectedCategory.equals(categoryManager.getCategoryByClass(FeatureCategory.class)) && !selectedCategory.equals(categoryManager.getCategoryByClass(MusicPlayerCategory.class)))){
        	searchMode = false;
		}
		
    	searchAnimation.setAnimation(searchMode ? 6 : -30, 17);
    	RoundedUtils.drawRound(sr.getScaledWidth() / 2 - 95, searchAnimation.getValue(), 190, 21, 6, ColorUtils.getBackgroundColor(4));
    	
		searchWord.drawTextBox();
		
		GlUtils.startScale(((this.getX()) + (this.getX() + this.getWidth())) / 2, ((this.getY()) + (this.getY() + this.getHeight())) / 2, (float) introAnimation.getValue());
        
		RoundedUtils.drawRound(this.getX() - 0.2F, this.getY() - 0.2F, this.getWidth() + 0.4F, this.getHeight() + 0.4F, 6, ColorUtils.getBackgroundColor(2));
		
		RoundedUtils.drawRound(this.getX(), this.getY(), 85, this.getHeight(), 6, ColorUtils.getBackgroundColor(1));
		RoundedUtils.drawRound(this.getX() + 20, this.getY(), 65, this.getHeight(), 0, ColorUtils.getBackgroundColor(1));
		
        FontUtils.regular_bold36.drawStringWithClientColor("Soar", x + 8, y + 6, false);
        
		categoryAnimation.setAnimation(currentCategoryY, 20);
		categoryOpacity.setAnimation(selectedCategory.equals(categoryManager.getCategoryByClass(SettingsCategory.class)) ? 0: 255, 20);
		RoundedUtils.drawGradientRoundLR(x + 4, y + categoryAnimation.getValue(), 75, 16, 4, ColorUtils.getClientColor(0, opacity), ColorUtils.getClientColor(20, opacity));
		
		for(Category c : categoryManager.getCategories()) {
			
			boolean featureCategory = selectedCategory.equals(categoryManager.getCategoryByClass(FeatureCategory.class));
			boolean settingsCategory = selectedCategory.equals(categoryManager.getCategoryByClass(SettingsCategory.class));
			
			float addX = (featureCategory) ? 88 : settingsCategory ? 95F : 0;
			float addX2 = settingsCategory ? 10 : 0;
			
			c.fontAnimation[0].setAnimation(selectedCategory.equals(c) &&  !selectedCategory.equals(categoryManager.getCategoryByClass(SettingsCategory.class)) ? new Color(255, 255, 255).getRed() : ColorUtils.getFontColor(2).getRed(), 12);
			c.fontAnimation[1].setAnimation(selectedCategory.equals(c) &&  !selectedCategory.equals(categoryManager.getCategoryByClass(SettingsCategory.class)) ? new Color(255, 255, 255).getGreen() : ColorUtils.getFontColor(2).getGreen(), 12);
			c.fontAnimation[2].setAnimation(selectedCategory.equals(c) &&  !selectedCategory.equals(categoryManager.getCategoryByClass(SettingsCategory.class)) ? new Color(255, 255, 255).getBlue() : ColorUtils.getFontColor(2).getBlue(), 12);
			
			if(!c.equals(categoryManager.getCategoryByClass(SettingsCategory.class))) {
				FontUtils.regular20.drawString(c.getName(), x + 12, y + offset + 5, new Color((int) c.fontAnimation[0].getValue(), (int) c.fontAnimation[1].getValue(), (int) c.fontAnimation[2].getValue()).getRGB());
			}else {
				RoundedUtils.drawRound(x, y + height - 26, 85, 1, 0, ColorUtils.getBackgroundColor(2));
				FontUtils.icon20.drawString("A", x + 12, (y + height) - 14, new Color((int) c.fontAnimation[0].getValue(), (int) c.fontAnimation[1].getValue(), (int) c.fontAnimation[2].getValue()).getRGB());
				FontUtils.regular20.drawString(c.getName(), x + 26, (y + height) - 15, new Color((int) c.fontAnimation[0].getValue(), (int) c.fontAnimation[1].getValue(), (int) c.fontAnimation[2].getValue()).getRGB());
			}
			
	        StencilUtils.initStencilToWrite();
			RoundedUtils.drawRound(this.getX() + addX, this.getY(), this.getWidth() - addX - addX2, this.getHeight(), 6, ColorUtils.getBackgroundColor(2));
	        StencilUtils.readStencilBuffer(1);
	        
			c.introAnimation.setAnimation(selectedCategory.equals(c) ? 0 : 50, 20);
			
			GlUtils.startTranslate(0, c.introAnimation.getValue());
			
			if(c.equals(selectedCategory)) {
				if(!c.equals(categoryManager.getCategoryByClass(SettingsCategory.class))) {
					currentCategoryY = offset;
				}
				c.drawScreen(mouseX, mouseY, partialTicks);
			}
			
			GlUtils.stopTranslate();
			StencilUtils.uninitStencilBuffer();
			
			offset+=25;
		}
		
		this.drawModDescription(mouseX, mouseY);
		GlUtils.stopScale();
		
		upAnimation.setAnimation(introAnimation.getValue() == 1.0F && (selectedCategory.equals(categoryManager.getCategoryByClass(FeatureCategory.class)) || selectedCategory.equals(categoryManager.getCategoryByClass(MusicPlayerCategory.class))) ? 6 : -30, 16);
		
		RoundedUtils.drawRound(upAnimation.getValue(), sr.getScaledHeight() - 30, 24, 24, 6, ColorUtils.getBackgroundColor(2));
		FontUtils.icon24.drawString("O", upAnimation.getValue() + 6, sr.getScaledHeight() - 22, ColorUtils.getFontColor(2).getRGB());
		
        if(clickEffects.size() > 0) {
            Iterator<ClickEffect> clickEffectIterator= clickEffects.iterator();
            while(clickEffectIterator.hasNext()){
                ClickEffect clickEffect = clickEffectIterator.next();
                clickEffect.draw();
                if (clickEffect.canRemove()) clickEffectIterator.remove();
            }
        }
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
		int offset = 35;
		ScaledResolution sr = new ScaledResolution(mc);
		
        ClickEffect clickEffect = new ClickEffect(mouseX, mouseY);
        clickEffects.add(clickEffect);
        
        if (MouseUtils.isInside(mouseX, mouseY, x, y, width, 25) && mouseButton == 0) {
            dragging = true;
            dragX = mouseX - x;
            dragY = mouseY - y;
        }
        
		for(Category c : categoryManager.getCategories()) {
			
			if(!c.equals(categoryManager.getCategoryByClass(SettingsCategory.class))) {
				if(MouseUtils.isInside(mouseX, mouseY, x + 4, y + offset, 75, 16) && mouseButton == 0) {
					selectedCategory = c;
				}
			}else {
				if(MouseUtils.isInside(mouseX, mouseY, x, y + height - 30, 85, 30) && mouseButton == 0) {
					selectedCategory = c;
				}
			}
			
			if(c.equals(selectedCategory)) {
				c.mouseClicked(mouseX, mouseY, mouseButton);
			}
			offset+=25;
		}
		
		if(selectedCategory.equals(categoryManager.getCategoryByClass(FeatureCategory.class)) || selectedCategory.equals(categoryManager.getCategoryByClass(MusicPlayerCategory.class))) {
			if(MouseUtils.isInside(mouseX, mouseY, upAnimation.getValue(), sr.getScaledHeight()  - 30, 24, 24)) {
	            if(!FeatureCategory.openModSetting) {
	            	searchMode = true;
	                searchWord.setFocused(true);
	                searchWord.setText("");
	                FeatureCategory.scrollY = 0;
	                Soar.instance.musicManager.setScrollY(0);
	            }
			}
		}
		
		searchWord.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		
		dragging = false;
		
		for(Category c : categoryManager.getCategories()) {
			if(c.equals(selectedCategory)) {
				c.mouseReleased(mouseX, mouseY, mouseButton);
			}
		}
	}
	
    @Override
    public void updateScreen() {
    	
        searchWord.updateCursorCounter();
        
		for(Category c : categoryManager.getCategories()) {
			if(c.equals(selectedCategory)) {
				c.updateScreen();
			}
		}
    }
    
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		
		for(Category c : categoryManager.getCategories()) {
			if(c.equals(selectedCategory)) {
				c.keyTyped(typedChar, keyCode);
			}
		}
		
        if(keyCode == 1) {
        	if(FeatureCategory.openModSetting) {
        		FeatureCategory.openSettingAnimation.setDirection(Direction.BACKWARDS);
        	}else {
        		if(searchMode) {
        			searchMode = false;
        		}else {
                	close = true;
        		}
        	}
        }
        
        searchWord.textboxKeyTyped(typedChar, keyCode);
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		if(loaded && mc.currentScreen instanceof ClickGUI) {
			ScaledResolution sr = new ScaledResolution(mc);
			
			GlUtils.startScale(((this.getX()) + (this.getX() + this.getWidth())) / 2, ((this.getY()) + (this.getY() + this.getHeight())) / 2, (float) introAnimation.getValue());
			RoundedUtils.drawRound(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 6, ColorUtils.getBackgroundColor(3));
			GlUtils.stopScale();
			RoundedUtils.drawRound(upAnimation.getValue(), sr.getScaledHeight() - 30, 24, 24, 6, ColorUtils.getBackgroundColor(2));
			
	    	RoundedUtils.drawRound(sr.getScaledWidth() / 2 - 95, searchAnimation.getValue(), 190, 21, 6, new Color(0, 0, 0));
		}
	}
	
    @Override
    public void onGuiClosed() {
        if(Soar.instance.fileManager != null) {
        	Soar.instance.configManager.save();
        }
    }
    
	private void drawModDescription(int mouseX, int mouseY) {
		
		int offset = 15;
		
        if(MouseUtils.isInside(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
    		if(selectedCategory.equals(categoryManager.getCategoryByClass(FeatureCategory.class))) {
    			for(Mod m : Soar.instance.modManager.getMods()) {
    				if(!m.isHide()) {
    					if(this.searchMode ? (StringUtils.containsIgnoreCase(m.getName(), this.searchWord.getText()) || StringUtils.containsIgnoreCase(m.getDescription(), this.searchWord.getText())) : true) {
        	            	boolean isSelect = MouseUtils.isInside(mouseX, mouseY, this.getX() + 95, this.getY() + offset + FeatureCategory.scrollAnimation.getValue(), 200, 26);
        	    			int opacity = 160;
        	    			
        	            	m.selectAnimation.setAnimation(isSelect && m.selectTimer.delay(1300) && !FeatureCategory.openModSetting ? opacity : 0, 16);
        	            	
        	            	if(!isSelect || FeatureCategory.openModSetting) {
        	            		m.selectTimer.reset();
        	            	}
        	            	
        	            	RoundedUtils.drawGradientRoundLR(mouseX + 6, mouseY, (float) FontUtils.regular20.getStringWidth(m.getDescription()) + 10, (float) FontUtils.regular20.getHeight() + 5, 6, ColorUtils.getClientColor(0, (int) m.selectAnimation.getValue()), ColorUtils.getClientColor(90, (int) m.selectAnimation.getValue()));
        	            	FontUtils.regular20.drawString(m.getDescription(), mouseX + 10.5F, mouseY + 3.5F, new Color(255, 255, 255, (int) m.selectAnimation.getValue() + (m.selectAnimation.getValue() > 109 ? 80 : 0)).getRGB());
        	            	
        					offset+=35;
    					}
    				}
    			}
    		}
        }
	}

    public boolean doesGuiPauseGame() {
    	return false;
    }
    
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
}
