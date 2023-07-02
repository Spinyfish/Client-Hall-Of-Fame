package me.eldodebug.soar.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;

import com.mojang.util.UUIDTypeAdapter;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.account.Account;
import me.eldodebug.soar.management.account.AccountType;
import me.eldodebug.soar.management.mods.impl.ClientMod;
import me.eldodebug.soar.utils.DayEventUtils;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.SkinUtils;
import me.eldodebug.soar.utils.TimerUtils;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.impl.EaseBackIn;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.interfaces.IMixinMinecraft;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils.Scroll;
import me.eldodebug.soar.utils.render.ClickEffect;
import me.eldodebug.soar.utils.render.RoundedUtils;
import me.eldodebug.soar.utils.render.StencilUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import openauth.microsoft.MicrosoftAuthResult;
import openauth.microsoft.MicrosoftAuthenticationException;
import openauth.microsoft.MicrosoftAuthenticator;

public class GuiAccountManager extends GuiScreen{

	private GuiScreen prevGuiScreen;
	
	private Animation showAccountAnimation;
	private boolean closeAccountManager;
	private boolean showAddAccount;
	
	private SimpleAnimation clickAnimation = new SimpleAnimation(0.0F);
	private boolean click;
	private TimerUtils clickTimer = new TimerUtils();
	
    public ResourceLocation faceTexture;
    
    private SimpleAnimation showAddAccountAnimation = new SimpleAnimation(0.0F);
    
    private boolean delete;
    private Account deleteAccount;
    
    private double scrollY;
    private SimpleAnimation scrollAnimation = new SimpleAnimation(0.0F);
    
    private SimpleAnimation addOpacityAnimation = new SimpleAnimation(0.0F);
    
    private GuiTransparentField usernameField;
    private SimpleAnimation selectAnimation = new SimpleAnimation(0.0F);
    
    private List<ClickEffect> clickEffects = new ArrayList<>();
    
    public GuiAccountManager(GuiScreen prevGuiScreen) {
    	this.prevGuiScreen = prevGuiScreen;
    }
    
	@Override
	public void initGui() {
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		
		int addX = 140;
		int addY = 85;
		int x = sr.getScaledWidth() / 2 - addX;
		int y =  sr.getScaledHeight() / 2 - addY;
		
        showAddAccount = false;
        closeAccountManager = false;
        showAccountAnimation = new EaseBackIn(450, 1, 1.5F);
        usernameField = new GuiTransparentField(1, mc.fontRendererObj, x + 38, y + 65, 220, 22, getFont2Color(255).getRGB());
        click = false;
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
		int offsetY = 36;
        int index = 1;
        
		Color bg1Color = ColorUtils.getBackgroundColor(1);
		Color bg2Color = ColorUtils.getBackgroundColor(2);
		Color bg3Color = ColorUtils.getBackgroundColor(3);
		Color bg4Color = ColorUtils.getBackgroundColor(4);
		Color font1Color = ColorUtils.getFontColor(1);
		Color font2Color = ColorUtils.getFontColor(2);
		
		if(DayEventUtils.isHalloween()) {
			bg1Color = ColorUtils.getHalloweenColor().getLightPurple();
			bg2Color = ColorUtils.getHalloweenColor().getOrange().brighter();
			bg3Color = ColorUtils.getHalloweenColor().getLightOrange();
			bg4Color = ColorUtils.getHalloweenColor().getOrange();
			font1Color = ColorUtils.getHalloweenColor().getBlack();
			font2Color = ColorUtils.getHalloweenColor().getBlack();
		}
		
		if(DayEventUtils.isChristmas()) {
			bg1Color = ColorUtils.getChristmasColor().getGreen();
			bg2Color = ColorUtils.getChristmasColor().getRed().brighter();
			bg3Color = ColorUtils.getChristmasColor().getDarkRed();
			bg4Color = ColorUtils.getChristmasColor().getRed();
			font1Color = Color.WHITE;
			font2Color = Color.WHITE;
		}
		
        if(closeAccountManager) {
        	showAccountAnimation.setDirection(Direction.BACKWARDS);
        	if(showAccountAnimation.isDone(Direction.BACKWARDS)) {
        		closeAccountManager = false;
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
		
		RoundedUtils.drawRound(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, bg1Color);
		
		FontUtils.icon24.drawString("N", 10, 10, font1Color.getRGB());
		FontUtils.icon24.drawString("Q", sr.getScaledWidth() - 20, 10, font1Color.getRGB());
		
		GlUtils.startScale(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, (float) showAccountAnimation.getValue());
		
		RoundedUtils.drawRound(x, y, width, height, 6, bg3Color);
		
		RoundedUtils.drawRound(x, y, width, 26, 6, bg4Color);
		RoundedUtils.drawRound(x, y + 10, width, 16, 0, bg4Color);
		
		FontUtils.regular22.drawString("Account Manager", x + 10, y + 10, font1Color.getRGB());
		addOpacityAnimation.setAnimation(showAddAccount ? 0 : 255, 16);
		FontUtils.regular22.drawStringWithClientColor("+Add", (x + width) - 35, y + 10, (int) addOpacityAnimation.getValue(), false);
		
        StencilUtils.initStencilToWrite();
        RoundedUtils.drawRound(x, y + 28, width, height - 28.5F, 6, Color.WHITE);
        StencilUtils.readStencilBuffer(1);
		
		showAddAccountAnimation.setAnimation(showAddAccount ? 0 : 140, 16);
		
		if(showAddAccount) {
			
			GlUtils.startTranslate(0, showAddAccountAnimation.getValue());
			
			RoundedUtils.drawRound(x + 15, y + 40, width - 30, height - 50, 6, bg4Color);
			
			FontUtils.regular_bold22.drawString("Offline Login", x + 35, y + 47, font2Color.getRGB());
			RoundedUtils.drawRound(x + 35, y + 64, 210, 20, 4, bg2Color);
			
			selectAnimation.setAnimation(usernameField.isFocused() || (!usernameField.isFocused() && !usernameField.getText().isEmpty()) ? 0 : 255, 16);
			
			FontUtils.regular20.drawString("Username", x + 42, y + 71, getFont2Color((int) selectAnimation.getValue()).getRGB());
			
			RoundedUtils.drawRound((x + width) - 135, y + 91, 100, 20, 4, bg3Color);
			FontUtils.regular_bold22.drawString("Login", x + 182, y + 96, font2Color.getRGB());
			
			RoundedUtils.drawRound(x + 35, y + 91, 100, 20, 4, bg3Color);
			FontUtils.regular_bold22.drawString("Gen Offline", x + 56, y + 96, font2Color.getRGB());
			
			clickAnimation.setAnimation(click ? 0.5F : 0, 100);
			
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
			
			GlUtils.stopTranslate();
		}
		
		if(!showAddAccount) {
			
			GlUtils.startTranslate(0, (140 - showAddAccountAnimation.getValue()));
			
			if(Soar.instance.accountManager.getAccounts().isEmpty()) {
				FontUtils.regular_bold26.drawString("Empty...", sr.getScaledWidth() / 2 - (FontUtils.regular_bold26.getStringWidth("Empty...") / 2), (sr.getScaledHeight() / 2) - (FontUtils.regular_bold26.getHeight() / 2), ColorUtils.getFontColor(2).getRGB());
			}
			
			for(Account a : Soar.instance.accountManager.getAccounts()) {
				
				RoundedUtils.drawRound(x + 10, y + offsetY + scrollAnimation.getValue(), width - 20, 35, 4, bg4Color);
				
				if(a.getAccountType().equals(AccountType.MICROSOFT)) {
					mc.getTextureManager().bindTexture(face(a.getUsername(), UUIDTypeAdapter.fromString(a.getUuid())));
				}else {
					mc.getTextureManager().bindTexture(new ResourceLocation("soar/head.png"));
				}

				GlStateManager.enableBlend();
				RoundedUtils.drawRoundTextured(x + 17, y + offsetY + 6 + scrollAnimation.getValue(), 24, 24, 4, 1.0F);
				GlStateManager.disableBlend();
				
				FontUtils.regular20.drawString(a.getUsername(), x + 50, y + offsetY + 15 + scrollAnimation.getValue(), font1Color.getRGB());
				
				a.opacityAnimation.setAnimation(a.isDone ? 0 : 255, 16);
				
				FontUtils.regular20.drawCenteredString(a.getInfo(), x + width - 54, y + 14.5F + offsetY + scrollAnimation.getValue(), ColorUtils.getFontColor(1, (int) a.opacityAnimation.getValue()).getRGB());
				
				FontUtils.icon20.drawString("M", x + width - 30, y + offsetY + 15 + scrollAnimation.getValue(),  new Color(255, 20, 20).getRGB());
				
				if(a.getInfo().equals(EnumChatFormatting.GREEN + "Success!") || a.getInfo().equals(EnumChatFormatting.RED + "Error :(")) {
					if(a.getTimer().delay(3500)) {
						a.isDone = true;
						a.getTimer().reset();
					}
				}else {
					a.getTimer().reset();
				}
				
				offsetY+=45;
				index++;
			}
			
			GlUtils.stopTranslate();
		}
		
		StencilUtils.uninitStencilBuffer();
		
		GlUtils.stopScale();
		
        final Scroll scroll = MouseUtils.scroll();

        if(scroll != null) {
        	switch (scroll) {
        	case DOWN:
        		if(index > 4){
            		if(scrollY > -((index - 3.5) * 45)) {
                		scrollY -=20;
            		}
            		
            		if(index > 4) {
                		if(scrollY < -((index - 3.8) * 45)) {
                			scrollY = -((index - 3.9) * 45);
                		}
            		}
        		}else {
        			scrollY = 0;
        		}
        		break;
            case UP:
        		if(scrollY > 0) {
        			scrollY = -18;
        		}
        		
        		if(scrollY < -0) {
            		scrollY +=20;
        		}else {
            		if(index > 5) {
            			scrollY = 10;
            		}
        		}
        		break;
        	}
        }
        
        scrollAnimation.setAnimation((float) scrollY, 16);
        
        if(delete) {
        	Soar.instance.accountManager.getAccounts().remove(deleteAccount);
			scrollY = 0;
        	delete = false;
        }
        
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
		
		int addX = 140;
		int addY = 85;
		int x = sr.getScaledWidth() / 2 - addX;
		int y =  sr.getScaledHeight() / 2 - addY;
		int width = addX * 2;
		int offsetY = 36;
		
        ClickEffect clickEffect = new ClickEffect(mouseX, mouseY);
        clickEffects.add(clickEffect);
        
		if(mouseButton == 0) {
			
			if(MouseUtils.isInside(mouseX, mouseY, (x + width) - 50, y, 50, 26)) {
				showAddAccount = true;
			}
			
			if(MouseUtils.isInside(mouseX, mouseY, 2, 2, 30, 30)) {
				Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "DarkMode").setValBoolean(!Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "DarkMode").getValBoolean());
			}
            
            if (MouseUtils.isInside(mouseX, mouseY, sr.getScaledWidth() - 25, 5.0, 20.0, 20.0)) {
            	
            }
			
			if(showAddAccount) {
				
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
								showAddAccount = false;
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
					showAddAccount = false;
				}
				
				if(MouseUtils.isInside(mouseX, mouseY, (x + width) - 135, y + 91, 100, 20)) {
					if(!usernameField.getText().isEmpty()) {
						Soar.instance.accountManager.getAccounts().add(new Account(AccountType.CRACKED, usernameField.getText(), "0", "0"));
						((IMixinMinecraft)mc).setSession(new Session(usernameField.getText(), "0", "0", "legacy"));
						showAddAccount = false;
						usernameField.setText("");
					}
				}
			}
			
			for(Account a : Soar.instance.accountManager.getAccounts()) {
				
				if(MouseUtils.isInside(mouseX, mouseY, x + width - 36, y + offsetY + 7 + scrollAnimation.getValue(), 20, 20)) {
					deleteAccount = a;
					delete = true;
				}
				
				if(!showAddAccount) {
					if(MouseUtils.isInside(mouseX, mouseY, x + 10, y + offsetY + scrollAnimation.getValue(), width - 50, 35)) {
						a.isDone = false;
						
						if(a.getAccountType().equals(AccountType.MICROSOFT)) {
							
							new Thread() {
								@Override
								public void run() {
									MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
									a.setInfo("Loading...");
									try {
										MicrosoftAuthResult acc = authenticator.loginWithRefreshToken(a.getToken());
										((IMixinMinecraft)mc).setSession(new Session(acc.getProfile().getName(), acc.getProfile().getId(), acc.getAccessToken(), "legacy"));
										Soar.instance.accountManager.setCurrentAccount(Soar.instance.accountManager.getAccountByUsername(acc.getProfile().getName()));
										a.setInfo(EnumChatFormatting.GREEN + "Success!");
									} catch (MicrosoftAuthenticationException e) {
										e.printStackTrace();
										a.setInfo(EnumChatFormatting.RED + "Error :(");
									}
								}
							}.start();
						}
						
						if(a.getAccountType().equals(AccountType.CRACKED)) {
							a.setInfo(EnumChatFormatting.GREEN + "Success!");
							((IMixinMinecraft)mc).setSession(new Session(a.getUsername(), "0", "0", "legacy"));
							Soar.instance.accountManager.setCurrentAccount(Soar.instance.accountManager.getAccountByUsername(a.getUsername()));
						}
					}
				}
				
				offsetY+=45;
			}
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		
		if(showAddAccount) {
			usernameField.textboxKeyTyped(typedChar, keyCode);
		}
		
		if(keyCode == 1) {
			if(showAddAccount) {
				showAddAccount = false;
			}else {
				Soar.instance.accountManager.save();
				closeAccountManager = true;
			}
		}
	}
	
	@Override
	public void updateScreen() {
		if(showAddAccount) {
			usernameField.updateCursorCounter();
		}
	}
	
    private ResourceLocation face(String username, UUID uuid) {
    	
        File model = new File(new File(mc.mcDataDir, "soar/cachedImages/models"), username + ".png");
        File face = new File(new File(mc.mcDataDir, "soar/cachedImages/faces"), username + ".png");

        SkinUtils.loadSkin(mc, username, uuid, model, face);

        try {
            BufferedImage t = ImageIO.read(face);
            DynamicTexture nibt = new DynamicTexture(t);

            this.faceTexture = mc.getTextureManager().getDynamicTextureLocation("iasface_" + username.hashCode(), nibt);
        } catch (Throwable throwable) {
            this.faceTexture = new ResourceLocation("iaserror", "skin");
        }

        return this.faceTexture;
    }
    
    private Color getFont2Color(int opacity) {
    	
    	if(DayEventUtils.isHalloween()) {
    		return ColorUtils.getHalloweenColor().getBlack(opacity);
    	}
    	
    	if(DayEventUtils.isChristmas()) {
    		return new Color(255, 255, 255, opacity);
    	}
    	
    	return ColorUtils.getFontColor(2, opacity);
    }
}
