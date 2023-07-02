package me.eldodebug.soar.gui.credit;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.ClientMod;
import me.eldodebug.soar.utils.DayEventUtils;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.impl.EaseBackIn;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils.Scroll;
import me.eldodebug.soar.utils.render.RoundedUtils;
import me.eldodebug.soar.utils.render.StencilUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiCredit extends GuiScreen{

	private ArrayList<Credit> credits = new ArrayList<Credit>();
	
	private Animation introAnimation;
	
	private boolean close;
	
	private double scrollY;
	private SimpleAnimation scrollAnimation = new SimpleAnimation(0.0F);
	
	public GuiCredit() {
		credits.add(new Credit("SpongePowered", "Creator of bytecode weaving framework (Mixin)", "https://github.com/SpongePowered", "S"));
		credits.add(new Credit("Sk1er LLC", "Some bug fixes and performance improvements", "https://github.com/Sk1erLLC", "S"));
		credits.add(new Credit("TheKodeToad", "Motion blur on Soar and some code help", "https://github.com/TheKodeToad", "S"));
		credits.add(new Credit("Anshg_", "Minecraft client-side account authentication system", "https://github.com/AnshGulavani", "S"));
		credits.add(new Credit("sp614x", "Creator of Optifine", "https://github.com/sp614x", "S"));
		credits.add(new Credit("Lumien", "Creator of Chunk Animator", "https://github.com/lumien231", "S"));
		credits.add(new Credit("Eric Golde", "Referring to the pvp client tutorial", "https://www.youtube.com/c/egold555", "U"));
		credits.add(new Credit("Cedo", "The client's design was used as a reference", "https://www.youtube.com/channel/UC2tPaPIMGeDETMTr1FQuMSA", "U"));
		credits.add(new Credit("canelex", "Creator of Dragon wings", "https://www.youtube.com/c/canelex", "U"));
		credits.add(new Credit("xCuri0", "Creator of Raw Input", "https://github.com/xCuri0", "S"));
		credits.add(new Credit("wcaleniekubaa", "Help with obfuscation and code", "https://github.com/wcaleniekubaa", "S"));
	}
	
	@Override
	public void initGui() {
		close = false;
        introAnimation = new EaseBackIn(450, 1, 1.5F);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		
		int addX = 140;
		int addY = 85;
		int x = sr.getScaledWidth() / 2 - addX;
		int y =  sr.getScaledHeight() / 2 - addY;
		int width = addX * 2;
		int height = addY * 2;
		int offsetY = 45;
		int index = 1;
		
		Color bg1Color = ColorUtils.getBackgroundColor(1);
		Color bg3Color = ColorUtils.getBackgroundColor(3);
		Color bg4Color = ColorUtils.getBackgroundColor(4);
		Color font1Color = ColorUtils.getFontColor(1);
		Color font2Color = ColorUtils.getFontColor(2);
		
		if(DayEventUtils.isHalloween()) {
			bg1Color = ColorUtils.getHalloweenColor().getLightPurple();
			bg3Color = ColorUtils.getHalloweenColor().getLightOrange();
			bg4Color = ColorUtils.getHalloweenColor().getOrange();
			font1Color = ColorUtils.getHalloweenColor().getBlack();
			font2Color = ColorUtils.getHalloweenColor().getBlack();
		}
		
		if(DayEventUtils.isChristmas()) {
			bg1Color = ColorUtils.getChristmasColor().getGreen();
			bg3Color = ColorUtils.getChristmasColor().getDarkRed();
			bg4Color = ColorUtils.getChristmasColor().getRed();
			font1Color = Color.WHITE;
			font2Color = Color.WHITE;
		}
		
		if(close) {
			introAnimation.setDirection(Direction.BACKWARDS);
			
			if(introAnimation.isDone(Direction.BACKWARDS)) {
				mc.displayGuiScreen(Soar.instance.guiManager.getGuiMainMenu());
			}
		}
		
		//draw background
		RoundedUtils.drawRound(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, bg1Color);
		
		FontUtils.icon24.drawString("N", 10, 10, font1Color.getRGB());
		FontUtils.icon24.drawString("Q", sr.getScaledWidth() - 20, 10, font1Color.getRGB());
		
		GlUtils.startScale(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, (float) introAnimation.getValue());
		
		RoundedUtils.drawRound(x, y, width, height, 6, bg3Color);
		
		RoundedUtils.drawRound(x, y, width, 26, 6, bg4Color);
		RoundedUtils.drawRound(x, y + 10, width, 16, 0, bg4Color);
		
		FontUtils.regular22.drawString("Credits", x + 10, y + 10, font1Color.getRGB());

        StencilUtils.initStencilToWrite();
        RoundedUtils.drawRound(x, y + 28, width, height - 28.5F, 6, Color.WHITE);
        StencilUtils.readStencilBuffer(1);
        
		for(Credit c : credits) {
			
			RoundedUtils.drawRound(x + 7, y + offsetY - 8 + scrollAnimation.getValue(), 265, 35, 6, bg4Color);
			
			FontUtils.regular22.drawString(c.getName(), x + 12, y + offsetY + scrollAnimation.getValue(), font2Color.getRGB());
			
			FontUtils.icon20.drawString(c.getIcon(), x + FontUtils.regular22.getStringWidth(c.getName()) + 15, y + offsetY + 1.5F + scrollAnimation.getValue(), font2Color.getRGB());
			
			FontUtils.regular20.drawString(c.getDescription(), x + 17, y + offsetY + 14 + scrollAnimation.getValue(), font2Color.getRGB());
			
			index++;
			offsetY+=50;
		}
		
		StencilUtils.uninitStencilBuffer();
		
		GlUtils.stopScale();
		
        //Copyright
        FontUtils.regular20.drawString("Copyright Mojang AB. Do not distribute!", sr.getScaledWidth() - FontUtils.regular_bold20.getStringWidth("Copyright Mojang AB. Do not distribute!") + 4, sr.getScaledHeight() - FontUtils.regular_bold20.getHeight() - 3, font2Color.getRGB());
        
        final Scroll scroll = MouseUtils.scroll();

        if(scroll != null) {
        	switch (scroll) {
        	case DOWN:
        		if(scrollY > -((index - 3.9) * 50)) {
            		scrollY -=20;
        		}
        		
        		if(scrollY < -((index - 3.7) * 50)) {
        			scrollY = -((index - 3.7) * 50);
        		}
        		break;
            case UP:
        		if(scrollY < -10) {
            		scrollY +=20;
        		}else {
        			scrollY = 0;
        		}
        		break;
        	}
        }
        
        scrollAnimation.setAnimation((float) scrollY, 16);
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		int offsetY = 45;
		
		int addX = 140;
		int addY = 85;
		int x = sr.getScaledWidth() / 2 - addX;
		int y =  sr.getScaledHeight() / 2 - addY;
		
		if(mouseButton == 0) {
			
			if(MouseUtils.isInside(mouseX, mouseY, 2, 2, 30, 30)) {
				Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "DarkMode").setValBoolean(!Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "DarkMode").getValBoolean());
			}
			
    		for(Credit c : credits) {
    			
    			if(MouseUtils.isInside(mouseX, mouseY, x + (float) FontUtils.regular22.getStringWidth(c.getName()) + 15, y + offsetY - 1 + scrollAnimation.getValue(), 10, 10)) {
    				Desktop desktop = Desktop.getDesktop();
    				try {
						desktop.browse(new URI(c.getUrl()));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
    			}
    			
    			offsetY+=50;
    		}
    		
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if(keyCode == 1) {
			close = true;
		}
	}
}
