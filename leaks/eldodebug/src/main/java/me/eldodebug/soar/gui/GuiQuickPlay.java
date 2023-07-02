package me.eldodebug.soar.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.quickplay.QuickPlay;
import me.eldodebug.soar.management.quickplay.QuickPlayCommand;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.impl.EaseBackIn;
import me.eldodebug.soar.utils.animation.normal.impl.EaseInOutQuad;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils.Scroll;
import me.eldodebug.soar.utils.render.ClickEffect;
import me.eldodebug.soar.utils.render.RoundedUtils;
import me.eldodebug.soar.utils.render.StencilUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class GuiQuickPlay extends GuiScreen{

    private Animation introAnimation;
    private Animation changeAnimation;
    
	private boolean loaded = false;
	private boolean firstClick;
	
	private boolean close;
	
	private boolean openSelectCommand;
	private QuickPlay selectedGame;
	
	private double scrollY;
	private SimpleAnimation scrollAnimation = new SimpleAnimation(0.0F);
	
	private double mainScrollY;
	private SimpleAnimation mainScrollAnimation = new SimpleAnimation(0.0F);
	
    private List<ClickEffect> clickEffects = new ArrayList<>();
    
	@Override
	public void initGui() {
        introAnimation = new EaseBackIn(450, 1, 2);
        close = false;
        openSelectCommand = false;
		changeAnimation = new EaseInOutQuad(350, 350);
		firstClick = true;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        
		if(!loaded) {
			loaded = true;
			Soar.instance.eventManager.register(this);
		}
		
        if(close) {
        	introAnimation.setDirection(Direction.BACKWARDS);
        	if(introAnimation.isDone(Direction.BACKWARDS)) {
            	mc.displayGuiScreen(null);
        	}
        }
        
        if(changeAnimation.isDone(Direction.BACKWARDS)) {
			scrollY = 0;
    		openSelectCommand = false;
        }
        
		ScaledResolution sr = new ScaledResolution(mc);
		int x = sr.getScaledWidth() / 2 - 170;
		int y = sr.getScaledHeight() / 2 - 110;
		int offsetX = 0;
		int offsetY = 0;
		int index = 0;
		int index2 = 1;
		
		int offsetSelectCommandX = 0;
		int offsetSelectCommandY = 0;
		int offsetSelectCommandIndex = 0;
		int offsetSelectCommandIndex2 = 1;
        
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		GlUtils.startScale(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, (float) (introAnimation.getValue()));
		
		RoundedUtils.drawRound(x - 0.2F, y - 0.2F, (170 * 2) + 0.4F, (110 * 2) + 0.4F, 6, ColorUtils.getBackgroundColor(2));
		
        StencilUtils.initStencilToWrite();
		RoundedUtils.drawRound(x, y, 170 * 2, 110 * 2, 6, new Color(238, 240, 245));
        StencilUtils.readStencilBuffer(1);
        
        if(changeAnimation != null) {
            
            mainScrollAnimation.setAnimation((float) mainScrollY, 12);
            
        	if(selectedGame != null) {
        		GlUtils.startTranslate(openSelectCommand ? (float) -changeAnimation.getValue() + 350 : 350, 0);
        		
        		RoundedUtils.drawRound(x + 8, y + 8, 68, 82, 6, ColorUtils.getBackgroundColor(4));
        		
        		FontUtils.regular20.drawCenteredString(selectedGame.getName(), x + 41, y + 77, ColorUtils.getFontColor(2).getRGB());
        		
        		mc.getTextureManager().bindTexture(selectedGame.getIcon());
        		
        		RoundedUtils.drawRoundTextured(x + 15, y + 15, 53, 53, 6, 1.0F);
                
        		for(QuickPlayCommand cmd : selectedGame.getCommands()) {
        			RoundedUtils.drawRound(x + 95 + offsetSelectCommandX, y + 15 + offsetSelectCommandY + scrollAnimation.getValue(), 100, 18, 6, ColorUtils.getBackgroundColor(4));
        			
        			FontUtils.regular20.drawCenteredString(cmd.getName(), x + 145 + offsetSelectCommandX, y + 21 + offsetSelectCommandY + scrollAnimation.getValue(), ColorUtils.getFontColor(2).getRGB());
        			offsetSelectCommandX+=120;
        			offsetSelectCommandIndex++;
        			
        			if(offsetSelectCommandIndex == offsetSelectCommandIndex2 * 2) {
        				offsetSelectCommandIndex2++;
        				offsetSelectCommandX = 0;
        				offsetSelectCommandY+=30;
        			}
        		}

        		if(openSelectCommand) {
                    Scroll scroll = MouseUtils.scroll();
                    
                    if(scroll != null) {
                    	switch (scroll) {
                    	case DOWN:
                    		if(scrollY > -((offsetSelectCommandIndex2 - 6.5) * 30)) {
                        		scrollY -=20;
                    		}
                    		
                    		if(offsetSelectCommandIndex2 > 6) {
                        		if(scrollY < -((offsetSelectCommandIndex2 - 9) * 30)) {
                        			scrollY = -((offsetSelectCommandIndex2 - 7.9) * 30);
                        		}
                    		}
                    		break;
                        case UP:
                    		if(scrollY < -10) {
                        		scrollY +=20;
                    		}else {
                        		if(offsetSelectCommandIndex2 > 6) {
                        			scrollY = 0;
                        		}
                    		}
                    		break;
                    	}
                    }
                    
                    scrollAnimation.setAnimation((float) scrollY, 12);
        		}
                
                GlUtils.stopTranslate();
        	}
            
            GlUtils.startTranslate(openSelectCommand ? (float) -changeAnimation.getValue() : 0, 0);
            
    		for(QuickPlay q : Soar.instance.quickPlayManager.getQuickPlays()) {
    			
    			q.animation.setAnimation(MouseUtils.isInside(mouseX, mouseY, x + 8 + offsetX, y + 8 + offsetY + mainScrollAnimation.getValue(), 68, 82) ? 1 : 0, 100);
    			
    			RoundedUtils.drawRound(x + 8 + offsetX, y + 8 + offsetY + mainScrollAnimation.getValue(), 68, 82, 6, ColorUtils.getBackgroundColor(4));
    			
    			mc.getTextureManager().bindTexture(q.getIcon());
        		GlStateManager.enableBlend();
    			RoundedUtils.drawRoundTextured(x + 15 + offsetX - q.animation.getValue(), y + 15 + offsetY - q.animation.getValue() + mainScrollAnimation.getValue(), 53 + (q.animation.getValue() * 2), 53 + (q.animation.getValue() * 2), 6, 1.0F);
            	GlStateManager.disableBlend();
    			FontUtils.regular20.drawCenteredString(q.getName(), x + offsetX + 41, y + offsetY + 77 + mainScrollAnimation.getValue(), ColorUtils.getFontColor(2).getRGB());
    			
    			offsetX+=85;
    			index++;
    			
    			if(index == index2 * 4) {
    				offsetY+=90;
    				offsetX = 0;
    				index2++;
    			}
    		}

    		if(!openSelectCommand) {
                Scroll scroll = MouseUtils.scroll();
                
                if(scroll != null) {
                	switch (scroll) {
                	case DOWN:
                		if(mainScrollY > -((index2 - 3.4) * 90)) {
                			mainScrollY -=20;
                		}
                		break;
                    case UP:
                		if(mainScrollY < -10) {
                			mainScrollY +=20;
                		}
                		break;
                	}
                }
                
                mainScrollAnimation.setAnimation((float) mainScrollY, 12);
    		}
    		
    		GlUtils.stopTranslate();
        }
        
        StencilUtils.uninitStencilBuffer();
        
		GlUtils.stopScale();
		
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
		
		ScaledResolution sr = new ScaledResolution(mc);
		int x = sr.getScaledWidth() / 2 - 170;
		int y = sr.getScaledHeight() / 2 - 110;
		int offsetX = 0;
		int offsetY = 0;
		int index = 0;
		int index2 = 1;
		
		int offsetSelectCommandX = 0;
		int offsetSelectCommandY = 0;
		int offsetSelectCommandIndex = 0;
		int offsetSelectCommandIndex2 = 1;
		
        ClickEffect clickEffect = new ClickEffect(mouseX, mouseY);
        clickEffects.add(clickEffect);
        
		if(mouseButton == 0) {
			
			if(openSelectCommand) {
				for(QuickPlayCommand cmd : selectedGame.getCommands()) {

					if(MouseUtils.isInside(mouseX, mouseY, x + 95 + offsetSelectCommandX, y + 15 + offsetSelectCommandY + scrollAnimation.getValue(), 100, 18)) {
						mc.thePlayer.sendChatMessage(cmd.getCommand());
					}
					
					offsetSelectCommandX+=120;
					offsetSelectCommandIndex++;
					
					if(offsetSelectCommandIndex == offsetSelectCommandIndex2 * 2) {
						offsetSelectCommandIndex2++;
						offsetSelectCommandX = 0;
						offsetSelectCommandY+=30;
					}
				}
			}else {
				for(QuickPlay q : Soar.instance.quickPlayManager.getQuickPlays()) {
					
					if(MouseUtils.isInside(mouseX, mouseY, x + 8 + offsetX, y + 8 + offsetY + mainScrollAnimation.getValue(), 68, 82)) {
						
						openSelectCommand = true;
						selectedGame = q;
						if(firstClick) {
							firstClick = false;
							changeAnimation.reset();
						}
						
						changeAnimation.setDirection(Direction.FORWARDS);
					}
					
					offsetX+=85;
					index++;
					
					if(index == index2 * 4) {
						offsetY+=90;
						offsetX = 0;
						index2++;
					}
				}
			}
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		
	}
	
    @Override
    public void keyTyped(char typedChar, int keyCode) {
    	
        if(keyCode == 1) {
        	if(openSelectCommand) {
        		changeAnimation.setDirection(Direction.BACKWARDS);
        	}else {
            	close = true;
        	}
        }
    }
    
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		ScaledResolution sr = new ScaledResolution(mc);
		int x = sr.getScaledWidth() / 2 - 170;
		int y = sr.getScaledHeight() / 2 - 110;
		GlUtils.startScale(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, (float) (introAnimation.getValue()));
		
		if(mc.currentScreen instanceof GuiQuickPlay) {
			RoundedUtils.drawRound(x, y, 170 * 2, 110 * 2, 6, new Color(238, 240, 245));
		}
		
		GlUtils.stopScale();
	}
	
	
    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }
}
