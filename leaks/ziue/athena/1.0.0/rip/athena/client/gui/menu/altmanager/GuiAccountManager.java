package rip.athena.client.gui.menu.altmanager;

import rip.athena.client.utils.animations.*;
import rip.athena.client.utils.animations.simple.*;
import rip.athena.client.utils.time.*;
import rip.athena.client.gui.menu.altmanager.button.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import rip.athena.client.utils.animations.impl.*;
import rip.athena.client.*;
import net.minecraft.client.renderer.*;
import java.awt.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.account.*;
import rip.athena.client.utils.*;
import java.util.*;
import org.apache.commons.lang3.*;
import net.minecraft.util.*;
import fr.litarvan.openauth.microsoft.*;

public class GuiAccountManager extends GuiScreen
{
    private GuiScreen prevGuiScreen;
    private Animation showAccountAnimation;
    private boolean closeAccountManager;
    private boolean showAddAccount;
    private SimpleAnimation clickAnimation;
    private boolean click;
    private TimerUtil clickTimer;
    public ResourceLocation faceTexture;
    private SimpleAnimation showAddAccountAnimation;
    private boolean delete;
    private Account deleteAccount;
    private double scrollY;
    private SimpleAnimation scrollAnimation;
    private SimpleAnimation addOpacityAnimation;
    private AltTextField usernameField;
    private SimpleAnimation selectAnimation;
    private List<ClickEffect> clickEffects;
    
    public GuiAccountManager(final GuiScreen prevGuiScreen) {
        this.clickAnimation = new SimpleAnimation(0.0f);
        this.clickTimer = new TimerUtil();
        this.showAddAccountAnimation = new SimpleAnimation(0.0f);
        this.scrollAnimation = new SimpleAnimation(0.0f);
        this.addOpacityAnimation = new SimpleAnimation(0.0f);
        this.selectAnimation = new SimpleAnimation(0.0f);
        this.clickEffects = new ArrayList<ClickEffect>();
        this.prevGuiScreen = prevGuiScreen;
    }
    
    @Override
    public void initGui() {
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final int addX = 340;
        final int addY = 85;
        final int x = sr.getScaledWidth() / 2 - addY;
        final int y = sr.getScaledHeight() / 2 - addY;
        this.showAddAccount = false;
        this.closeAccountManager = false;
        this.showAccountAnimation = new EaseBackIn(450, 1.0, 1.5f);
        this.usernameField = new AltTextField(1, this.mc.fontRendererObj, x + 38, y + 65, 220, 22, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        this.click = false;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final int addX = 140;
        final int addY = 85;
        final int x = sr.getScaledWidth() / 2 - addX;
        final int y = sr.getScaledHeight() / 2 - addY;
        final int width = addX * 2;
        final int height = addY * 2;
        int offsetY = 36;
        int index = 1;
        if (this.closeAccountManager) {
            this.mc.displayGuiScreen(this.prevGuiScreen);
        }
        if (this.click) {
            if (this.clickTimer.hasTimeElapsed(150L)) {
                this.click = false;
            }
        }
        else {
            this.clickTimer.reset();
        }
        DrawUtils.drawImage(new ResourceLocation("Athena/menu/wallpaper3.png"), 0, 0, sr.getScaledWidth(), sr.getScaledHeight());
        GlStateManager.pushMatrix();
        RoundedUtils.drawGradientRound((float)x, (float)y, (float)width, (float)height, 6.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
        RoundedUtils.drawRound((float)(x + 1), (float)(y + 1), (float)(width - 2), (float)(height - 2), 6.0f, new Color(Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor()));
        FontManager.getProductSansRegular(22).drawString(("Account Manager " + Athena.INSTANCE.getAccountManager().getCurrentAccount() != null) ? ("Account Manager | " + Athena.INSTANCE.getAccountManager().getCurrentAccount().getUsername()) : "Account Manager | No Account", x + 10, y + 10, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        RoundedUtils.drawGradientRound((float)x, (float)(y + 179), (float)width, (float)(height - 148), 6.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
        RoundedUtils.drawRound((float)(x + 1), (float)(y + 180), (float)(width - 2), (float)(height - 150), 6.0f, new Color(Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor()));
        FontManager.getProductSansRegular(22).drawString("Add Account", x + 5, y + 186, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        RoundedUtils.drawGradientRound((float)x, (float)(y + 206), (float)width, (float)(height - 148), 6.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
        RoundedUtils.drawRound((float)(x + 1), (float)(y + 207), (float)(width - 2), (float)(height - 150), 6.0f, new Color(Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor()));
        FontManager.getProductSansRegular(22).drawString("Go back", x + 5, y + 213, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        StencilUtils.initStencilToWrite();
        RoundedUtils.drawRound((float)x, (float)(y + 28), (float)width, height - 28.5f, 6.0f, Color.WHITE);
        StencilUtils.readStencilBuffer(1);
        this.showAddAccountAnimation.setAnimation(this.showAddAccount ? 0.0f : 140.0f, 16.0);
        GLUtils.startTranslate(0.0f, 140.0f - this.showAddAccountAnimation.getValue());
        if (Athena.INSTANCE.getAccountManager().getAccounts().isEmpty()) {
            FontManager.getProductSansRegular(22).drawString("Empty...", sr.getScaledWidth() / 2 - FontManager.getProductSansRegular(22).height() / 2.0f, sr.getScaledHeight() / 2 - FontManager.getProductSansRegular(22).height() / 2.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        for (final Account a : Athena.INSTANCE.getAccountManager().getAccounts()) {
            RoundedUtils.drawRound((float)(x + 9), y + offsetY + this.scrollAnimation.getValue() - 1.0f, (float)(width - 18), 37.0f, 4.0f, new Color(Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor()));
            RoundedUtils.drawRound((float)(x + 10), y + offsetY + this.scrollAnimation.getValue(), (float)(width - 20), 35.0f, 4.0f, new Color(Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor()));
            RoundedUtils.drawGradientRound((float)(x + width - 36), y + offsetY + 7 + this.scrollAnimation.getValue(), 20.0f, 20.0f, 4.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
            if (a.getAccountType().equals(AccountType.MICROSOFT)) {
                this.mc.getTextureManager().bindTexture(new ResourceLocation("Athena/menu/head.png"));
            }
            else {
                this.mc.getTextureManager().bindTexture(new ResourceLocation("Athena/menu/head.png"));
            }
            GlStateManager.enableBlend();
            RoundedUtils.drawRoundTextured((float)(x + 17), y + offsetY + 6 + this.scrollAnimation.getValue(), 24.0f, 24.0f, 4.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.pushMatrix();
            DrawUtils.drawImage(new ResourceLocation("Athena/menu/exit.png"), x + width - 31, (int)(y + offsetY + 12 + this.scrollAnimation.getValue()), 10, 10);
            GlStateManager.popMatrix();
            FontManager.getProductSansRegular(22).drawString(a.getUsername(), x + 50, y + offsetY + 15 + this.scrollAnimation.getValue(), Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
            a.opacityAnimation.setAnimation(a.isDone ? 0.0f : 255.0f, 16.0);
            FontManager.getProductSansRegular(22).drawCenteredString(a.getInfo(), x + width - 64, y + 14.5f + offsetY + this.scrollAnimation.getValue(), Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
            if (a.getInfo().equals("Success") || a.getInfo().equals("Error")) {
                if (a.getTimer().hasTimeElapsed(3500L)) {
                    a.isDone = true;
                    a.getTimer().reset();
                }
            }
            else {
                a.getTimer().reset();
            }
            offsetY += 45;
            ++index;
        }
        GLUtils.stopTranslate();
        StencilUtils.uninitStencilBuffer();
        GLUtils.stopScale();
        final MouseUtils.Scroll scroll = MouseUtils.scroll();
        if (scroll != null) {
            switch (scroll) {
                case DOWN: {
                    if (index <= 4) {
                        this.scrollY = 0.0;
                        break;
                    }
                    if (this.scrollY > -((index - 3.5) * 45.0)) {
                        this.scrollY -= 20.0;
                    }
                    if (index > 4 && this.scrollY < -((index - 3.8) * 45.0)) {
                        this.scrollY = -((index - 3.9) * 45.0);
                        break;
                    }
                    break;
                }
                case UP: {
                    if (this.scrollY > 0.0) {
                        this.scrollY = -18.0;
                    }
                    if (this.scrollY < 0.0) {
                        this.scrollY += 20.0;
                        break;
                    }
                    if (index > 5) {
                        this.scrollY = 10.0;
                        break;
                    }
                    break;
                }
            }
        }
        this.scrollAnimation.setAnimation((float)this.scrollY, 16.0);
        if (this.delete) {
            Athena.INSTANCE.getAccountManager().getAccounts().remove(this.deleteAccount);
            this.scrollY = 0.0;
            this.delete = false;
        }
        if (this.clickEffects.size() > 0) {
            final Iterator<ClickEffect> clickEffectIterator = this.clickEffects.iterator();
            while (clickEffectIterator.hasNext()) {
                final ClickEffect clickEffect = clickEffectIterator.next();
                clickEffect.draw();
                if (clickEffect.canRemove()) {
                    clickEffectIterator.remove();
                }
            }
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final int addX = 140;
        final int addY = 85;
        final int x = sr.getScaledWidth() / 2 - addX;
        final int y = sr.getScaledHeight() / 2 - addY;
        final int width = addX * 2;
        int offsetY = 36;
        final ClickEffect clickEffect = new ClickEffect((float)mouseX, (float)mouseY);
        this.clickEffects.add(clickEffect);
        if (mouseButton == 0) {
            if (MouseUtils.isInside(mouseX, mouseY, x + width - 50, y, 50.0, 26.0)) {
                this.showAddAccount = true;
            }
            if (MouseUtils.isInside(mouseX, mouseY, x, y + 206, width, 25.0)) {
                this.mc.displayGuiScreen(this.prevGuiScreen);
            }
            if (MouseUtils.isInside(mouseX, mouseY, x, y + 179, width, 25.0)) {
                this.mc.displayGuiScreen(new GuiAltManager());
            }
            if (this.showAddAccount) {
                this.usernameField.mouseClicked(mouseX, mouseY, mouseButton);
                if (MouseUtils.isInside(mouseX, mouseY, x + 35, y + 120, 210.0, 30.0)) {
                    this.click = true;
                    new Thread() {
                        @Override
                        public void run() {
                            final MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                        }
                    }.start();
                }
                if (MouseUtils.isInside(mouseX, mouseY, x + 35, y + 91, 100.0, 20.0)) {
                    final Random random = new Random();
                    final int randomValue = random.nextInt(8) + 3;
                    final String username = RandomStringUtils.randomAlphabetic(randomValue);
                    Athena.INSTANCE.getAccountManager().getAccounts().add(new Account(AccountType.CRACKED, username, "0", "0"));
                    this.mc.session = new Session(username, "0", "0", "legacy");
                    this.showAddAccount = false;
                }
                if (MouseUtils.isInside(mouseX, mouseY, x + width - 135, y + 91, 100.0, 20.0) && !this.usernameField.getText().isEmpty()) {
                    Athena.INSTANCE.getAccountManager().getAccounts().add(new Account(AccountType.CRACKED, this.usernameField.getText(), "0", "0"));
                    this.mc.session = new Session(this.usernameField.getText(), "0", "0", "legacy");
                    this.showAddAccount = false;
                    this.usernameField.setText("");
                }
            }
            for (final Account a : Athena.INSTANCE.getAccountManager().getAccounts()) {
                if (MouseUtils.isInside(mouseX, mouseY, x + width - 36, y + offsetY + 7 + this.scrollAnimation.getValue(), 20.0, 20.0)) {
                    this.deleteAccount = a;
                    this.delete = true;
                }
                if (!this.showAddAccount && MouseUtils.isInside(mouseX, mouseY, x + 10, y + offsetY + this.scrollAnimation.getValue(), width - 50, 35.0)) {
                    a.isDone = false;
                    if (a.getAccountType().equals(AccountType.MICROSOFT)) {
                        new Thread() {
                            @Override
                            public void run() {
                                final MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                                a.setInfo("Loading...");
                                try {
                                    final MicrosoftAuthResult acc = authenticator.loginWithRefreshToken(a.getToken());
                                    GuiAccountManager.this.mc.session = new Session(acc.getProfile().getName(), acc.getProfile().getId(), acc.getAccessToken(), "mojang");
                                    Athena.INSTANCE.getAccountManager().setCurrentAccount(Athena.INSTANCE.getAccountManager().getAccountByUsername(acc.getProfile().getName()));
                                    a.setInfo("Success!");
                                }
                                catch (MicrosoftAuthenticationException e) {
                                    e.printStackTrace();
                                    a.setInfo("Error");
                                }
                            }
                        }.start();
                    }
                    if (a.getAccountType().equals(AccountType.SESSION)) {
                        a.setInfo("Loading...");
                        try {
                            this.mc.session = new Session(a.getUsername(), a.getUuid(), a.getToken(), "mojang");
                            Athena.INSTANCE.getAccountManager().setCurrentAccount(Athena.INSTANCE.getAccountManager().getAccountByUsername(a.getUsername()));
                            a.setInfo("Success!");
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            a.setInfo("Error");
                        }
                    }
                    if (a.getAccountType().equals(AccountType.CRACKED)) {
                        a.setInfo("Success!");
                        this.mc.session = new Session(a.getUsername(), "0", "0", "legacy");
                        Athena.INSTANCE.getAccountManager().setCurrentAccount(Athena.INSTANCE.getAccountManager().getAccountByUsername(a.getUsername()));
                    }
                }
                offsetY += 45;
            }
        }
    }
    
    public void keyTyped(final char typedChar, final int keyCode) {
        if (this.showAddAccount) {
            this.usernameField.textboxKeyTyped(typedChar, keyCode);
        }
        if (keyCode == 1) {
            if (this.showAddAccount) {
                this.showAddAccount = false;
            }
            else {
                Athena.INSTANCE.getAccountManager().save();
                this.closeAccountManager = true;
            }
        }
    }
}
