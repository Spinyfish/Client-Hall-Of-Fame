package me.eldodebug.soar.gui.mainmenu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.gui.GuiAccountManager;
import me.eldodebug.soar.gui.GuiPleaseLogin;
import me.eldodebug.soar.gui.credit.GuiCredit;
import me.eldodebug.soar.management.mods.impl.ClientMod;
import me.eldodebug.soar.utils.DayEventUtils;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.impl.EaseBackIn;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.render.ClickEffect;
import me.eldodebug.soar.utils.render.RoundedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class GuiSoarMainMenu extends GuiScreen{

	private ArrayList<SoarMainMenuButton> menus = new ArrayList<SoarMainMenuButton>();
	private Animation introAnimation;
	private boolean closeIntro;
    
    private List<ClickEffect> clickEffects = new ArrayList<>();
    
    private CloseType closeType;
    
	public GuiSoarMainMenu() {
		menus.add(new SoarMainMenuButton("Singleplayer"));
		menus.add(new SoarMainMenuButton("Multiplayer"));
		menus.add(new SoarMainMenuButton("Account Manager"));
		menus.add(new SoarMainMenuButton("Options"));
		menus.add(new SoarMainMenuButton("Quit"));
	}
	
	@Override
	public void initGui() {
		if(Soar.instance.accountManager.isFirstLogin) {
			mc.displayGuiScreen(new GuiPleaseLogin(this));
		}
        introAnimation = new EaseBackIn(450, 1, 1.5F);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

		Color bg1Color = ColorUtils.getBackgroundColor(1);
		Color bg3Color = ColorUtils.getBackgroundColor(3);
		Color font1Color = ColorUtils.getFontColor(1);
		Color font2Color = ColorUtils.getFontColor(2);
		
		if(DayEventUtils.isHalloween()) {
			bg1Color = ColorUtils.getHalloweenColor().getLightPurple();
			bg3Color = ColorUtils.getHalloweenColor().getLightOrange();
			font1Color = ColorUtils.getHalloweenColor().getBlack();
			font2Color = ColorUtils.getHalloweenColor().getBlack();
		}
		
		if(DayEventUtils.isChristmas()) {
			bg1Color = ColorUtils.getChristmasColor().getGreen();
			bg3Color = ColorUtils.getChristmasColor().getRed();
			font1Color = Color.WHITE;
			font2Color = Color.WHITE;
		}
		
        GlStateManager.disableTexture2D();
        
		if(closeIntro) {
			introAnimation.setDirection(Direction.BACKWARDS);
			if(introAnimation.isDone(Direction.BACKWARDS)) {
				closeIntro = false;
				if(closeType.equals(CloseType.ACCOUNT)) {
					mc.displayGuiScreen(new GuiAccountManager(this));
				}else if(closeType.equals(CloseType.CREDIT)) {
					mc.displayGuiScreen(new GuiCredit());
				}
			}
		}
		
		int addX = 65;
		int addY = 85;
        int offsetY = -45;
		
		RoundedUtils.drawRound(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, bg1Color);
		
		FontUtils.icon24.drawString("N", 10, 10, font1Color.getRGB());
		FontUtils.icon24.drawString("Q", sr.getScaledWidth() - 20, 10, font1Color.getRGB());
		
		GlUtils.startScale(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, (float) introAnimation.getValue());
		
		RoundedUtils.drawRound(sr.getScaledWidth() / 2 - addX, sr.getScaledHeight() / 2 - addY, addX * 2, addY * 2, 6, bg3Color);
		
		FontUtils.regular_bold40.drawStringWithClientColor("Soar Client", (sr.getScaledWidth() / 2 - addX) - FontUtils.regular_bold40.getStringWidth("Soar Client") + 115, sr.getScaledHeight() / 2 - 80, false);
		
		for(SoarMainMenuButton b : menus) {
			
			boolean isInside = MouseUtils.isInside(mouseX, mouseY, sr.getScaledWidth() / 2 - addX, sr.getScaledHeight() / 2 - addY + offsetY + 78, addX * 2, 20);
			
			b.opacityAnimation.setAnimation(isInside ? 255 : 0, 10);
			
			RoundedUtils.drawRound(sr.getScaledWidth() / 2 - addX, sr.getScaledHeight() / 2 - addY + offsetY + 78, addX * 2, 20, 6, getSelectButtonColor(((int) b.opacityAnimation.getValue())));
			FontUtils.regular20.drawCenteredString(b.getName(), sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 + offsetY, font2Color.getRGB());
			
			offsetY+=26;
		}
		
		GlUtils.stopScale();
		super.drawScreen(mouseX, mouseY, partialTicks);
		
        //Copyright
        FontUtils.regular20.drawString("Copyright Mojang AB. Do not distribute!", sr.getScaledWidth() - FontUtils.regular_bold20.getStringWidth("Copyright Mojang AB. Do not distribute!") + 4, sr.getScaledHeight() - FontUtils.regular_bold20.getHeight() - 3, font2Color.getRGB());
        
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
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        
        ClickEffect clickEffect = new ClickEffect(mouseX, mouseY);
        clickEffects.add(clickEffect);
        
		if(mouseButton == 0) {
			
			if(MouseUtils.isInside(mouseX, mouseY, 2, 2, 30, 30)) {
				Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "DarkMode").setValBoolean(!Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "DarkMode").getValBoolean());
			}
            
            if (MouseUtils.isInside(mouseX, mouseY, sr.getScaledWidth() - 25, 5.0, 20.0, 20.0)) {
            	closeType = CloseType.CREDIT;
				closeIntro = true;
            }
			
			int addX = 65;
			int addY = 85;
	        int offsetY = -45;
	        
			for(SoarMainMenuButton b : menus) {
				if(MouseUtils.isInside(mouseX, mouseY, sr.getScaledWidth() / 2 - addX, sr.getScaledHeight() / 2 - addY + offsetY + 78, addX * 2, 20)){
					switch(b.getName()) {
						case "Singleplayer":
							mc.displayGuiScreen(new GuiSelectWorld(this));
							break;
						case "Multiplayer":
							mc.displayGuiScreen(new GuiMultiplayer(this));
							break;
						case "Account Manager":
							closeType = CloseType.ACCOUNT;
							closeIntro = true;
							break;
						case "Options":
							mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
							break;
						case "Quit":
							mc.shutdown();	
							break;
					}
				}
				offsetY+=26;
			}
		}
	}
	
	private Color getSelectButtonColor(int opacity) {
		
		if(DayEventUtils.isHalloween()) {
			return ColorUtils.getHalloweenColor().getOrange(opacity);
		}
		
		if(DayEventUtils.isChristmas()) {
			return ColorUtils.getChristmasColor().getRed(opacity).brighter();
		}
		
		return ColorUtils.getBackgroundColor(4,  opacity);
	}
}
