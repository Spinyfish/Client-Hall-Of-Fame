package me.eldodebug.soar.gui;

import java.awt.Color;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.account.Account;
import me.eldodebug.soar.management.account.AccountType;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.TimerUtils;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.impl.EaseBackIn;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.interfaces.IMixinMinecraft;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.render.RoundedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import openauth.microsoft.MicrosoftAuthResult;
import openauth.microsoft.MicrosoftAuthenticationException;
import openauth.microsoft.MicrosoftAuthenticator;

public class GuiPleaseLogin extends GuiScreen{

	private GuiScreen prevGuiScreen;
	
	private Animation introAnimation;
    private GuiTransparentField usernameField;
    
	private SimpleAnimation clickAnimation = new SimpleAnimation(0.0F);
	private boolean click;
	private TimerUtils clickTimer = new TimerUtils();
    private SimpleAnimation selectAnimation = new SimpleAnimation(0.0F);
    private boolean close;
    
    public GuiPleaseLogin(GuiScreen prevGuiScreen) {
    	this.prevGuiScreen = prevGuiScreen;
    }
    
	@Override
	public void initGui() {
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		
		int addX = 140;
		int addY = 85;
		int x = sr.getScaledWidth() / 2 - addX;
		int y =  sr.getScaledHeight() / 2 - addY;
		
		introAnimation = new EaseBackIn(450, 1, 1.5F);
        usernameField = new GuiTransparentField(1, mc.fontRendererObj, x + 38, y + 65, 220, 22, ColorUtils.getFontColor(1).getRGB());
        click = false;
        close = false;
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
		
        if(close) {
        	introAnimation.setDirection(Direction.BACKWARDS);
        	if(introAnimation.isDone(Direction.BACKWARDS)) {
        		close = false;
        		mc.displayGuiScreen(prevGuiScreen);
        	}
        }
        
		if(click) {
			if(clickTimer.delay(150)) {
				click = false;
			}
		}else {
			clickTimer.reset();
		}
		
		//�w�i�̕`��
		RoundedUtils.drawRound(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, ColorUtils.getBackgroundColor(1));
		
		FontUtils.icon24.drawString("N", 10, 10, ColorUtils.getFontColor(1).getRGB());
		
		GlUtils.startScale(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, (float) introAnimation.getValue());
		
		//�w�i�`��
		RoundedUtils.drawRound(x, y, width, height, 6, ColorUtils.getBackgroundColor(3));
		
		//��̃o�[�`��
		RoundedUtils.drawRound(x, y, width, 26, 6, ColorUtils.getBackgroundColor(4));
		RoundedUtils.drawRound(x, y + 10, width, 16, 0, ColorUtils.getBackgroundColor(4));
		
		FontUtils.regular22.drawString("Account Manager - Please Login", x + 10, y + 10, ColorUtils.getFontColor(1).getRGB());
		
		RoundedUtils.drawRound(x + 15, y + 40, width - 30, height - 50, 6, ColorUtils.getBackgroundColor(4));
		
		//Cracked���O�C��
		FontUtils.regular_bold22.drawString("Offline Login", x + 35, y + 47, ColorUtils.getFontColor(2).getRGB());
		RoundedUtils.drawRound(x + 35, y + 64, 210, 20, 4, ColorUtils.getBackgroundColor(2));
		
		selectAnimation.setAnimation(usernameField.isFocused() || (!usernameField.isFocused() && !usernameField.getText().isEmpty()) ? 0 : 255, 16);
		
		FontUtils.regular20.drawString("Username", x + 42, y + 71, ColorUtils.getFontColor(2, (int) selectAnimation.getValue()).getRGB());
		
		RoundedUtils.drawRound((x + width) - 135, y + 91, 100, 20, 4, ColorUtils.getBackgroundColor(3));
		FontUtils.regular_bold22.drawString("Login", x + 182, y + 96, ColorUtils.getFontColor(2).getRGB());
		
		RoundedUtils.drawRound(x + 35, y + 91, 100, 20, 4, ColorUtils.getBackgroundColor(3));
		FontUtils.regular_bold22.drawString("Gen Offline", x + 56, y + 96, ColorUtils.getFontColor(2).getRGB());
		
		clickAnimation.setAnimation(click ? 0.5F : 0, 100);
		
		//Microsoft���O�C��
		mc.getTextureManager().bindTexture(new ResourceLocation("soar/minecraft-background.png"));
		
		GlStateManager.enableBlend();
		RoundedUtils.drawRoundTextured(x + 35 + clickAnimation.getValue(), y + 120 + clickAnimation.getValue(), 210 - (clickAnimation.getValue() * 2), 30 - (clickAnimation.getValue() * 2), 4, 1.0F);
		GlStateManager.disableBlend();
		
		RoundedUtils.drawRound(x + 42, (y + height) - 44, 8, 8, 1, new Color(247, 78, 30));
		RoundedUtils.drawRound(x + 42 + 11, (y + height) - 44, 8, 8, 1, new Color(127, 186, 0));
		RoundedUtils.drawRound(x + 42, (y + height) - 44 + 11, 8, 8, 1, new Color(0, 164, 239));
		RoundedUtils.drawRound(x + 42 + 11, (y + height) - 44 + 11, 8, 8, 1, new Color(255, 185, 0));
		
		FontUtils.regular_bold22.drawString("Microsoft Login", x + 70, (y + height) - 40, Color.WHITE.getRGB());
		
		usernameField.drawTextBox();
		
		GlUtils.stopScale();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		
		int addX = 140;
		int addY = 85;
		int x = sr.getScaledWidth() / 2 - addX;
		int y =  sr.getScaledHeight() / 2 - addY;
		int width = addX * 2;
		
		usernameField.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(MouseUtils.isInside(mouseX, mouseY, x + 35, y + 120, 210, 30)) {
			click = true;
			new Thread() {
				@Override
				public void run() {
					MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
					try {
						MicrosoftAuthResult acc = authenticator.loginWithWebview();
						Soar.instance.accountManager.getAccounts().add(new Account(AccountType.MICROSOFT, acc.getProfile().getName(), acc.getProfile().getId(), acc.getRefreshToken()));
						((IMixinMinecraft)mc).setSession(new Session(acc.getProfile().getName(), acc.getProfile().getId(), acc.getAccessToken(), "legacy"));
						close = true;
						Soar.instance.accountManager.isFirstLogin = false;
						Soar.instance.accountManager.setCurrentAccount(Soar.instance.accountManager.getAccountByUsername(acc.getProfile().getName()));
						Soar.instance.accountManager.save();
					} catch (MicrosoftAuthenticationException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
		
		if(MouseUtils.isInside(mouseX, mouseY, x + 35, y + 91, 100, 20)) {
			Random random = new Random();
			int randomValue = random.nextInt(8) + 3;
			
			String username = RandomStringUtils.randomAlphabetic(randomValue);
			
			Soar.instance.accountManager.getAccounts().add(new Account(AccountType.CRACKED, username, "0", "0"));
			((IMixinMinecraft)mc).setSession(new Session(username, "0", "0", "legacy"));
			close = true;
			Soar.instance.accountManager.isFirstLogin = false;
			Soar.instance.accountManager.setCurrentAccount(Soar.instance.accountManager.getAccountByUsername(username));
			Soar.instance.accountManager.save();
		}
		
		if(MouseUtils.isInside(mouseX, mouseY, (x + width) - 135, y + 91, 100, 20)) {
			if(!usernameField.getText().isEmpty()) {
				
				String username = usernameField.getText();
				
				Soar.instance.accountManager.getAccounts().add(new Account(AccountType.CRACKED, username, "0", "0"));
				((IMixinMinecraft)mc).setSession(new Session(username, "0", "0", "legacy"));
				usernameField.setText("");
				close = true;
				Soar.instance.accountManager.isFirstLogin = false;
				Soar.instance.accountManager.setCurrentAccount(Soar.instance.accountManager.getAccountByUsername(username));
				Soar.instance.accountManager.save();
			}
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		usernameField.textboxKeyTyped(typedChar, keyCode);
	}
	
	@Override
	public void updateScreen() {
		usernameField.updateCursorCounter();
	}
}
