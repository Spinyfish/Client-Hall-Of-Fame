package rip.athena.client.gui.menu.altmanager.panels;

import rip.athena.client.gui.menu.altmanager.*;
import rip.athena.client.gui.menu.altmanager.button.*;
import rip.athena.client.utils.animations.impl.*;
import rip.athena.client.utils.font.*;
import java.awt.*;
import rip.athena.client.*;
import rip.athena.client.account.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import rip.athena.client.utils.animations.*;
import rip.athena.client.utils.render.*;
import java.util.*;
import fr.litarvan.openauth.microsoft.*;

public class LoginPanel extends Panel
{
    private final List<AltButton> actionButtons;
    public final List<TextField> textFields;
    private String status;
    public static boolean cracked;
    private boolean hoveringMicrosoft;
    private final Animation hoverMicrosoftAnim;
    
    public LoginPanel() {
        this.actionButtons = new ArrayList<AltButton>();
        this.textFields = new ArrayList<TextField>();
        this.status = "";
        this.hoveringMicrosoft = false;
        this.hoverMicrosoftAnim = new DecelerateAnimation(250, 1.0);
        this.setHeight(200.0f);
        this.actionButtons.add(new AltButton("Offline Login"));
        this.textFields.add(new TextField());
        this.textFields.add(new TextField());
    }
    
    @Override
    public void initGui() {
    }
    
    @Override
    public void keyTyped(final char typedChar, final int keyCode) {
        this.textFields.forEach(textField -> textField.keyTyped(typedChar, keyCode));
        if (keyCode == 15) {
            final TextField username = this.textFields.get(0);
            final TextField pass = this.textFields.get(1);
            if (username.isFocused()) {
                username.setFocused(false);
                pass.setFocused(true);
                return;
            }
            if (pass.isFocused()) {
                pass.setFocused(false);
                username.setFocused(true);
            }
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY) {
        super.drawScreen(mouseX, mouseY);
        this.setHeight(180.0f);
        FontManager.getProductSansRegular(28).drawCenteredString("Login", this.getX() + this.getWidth() / 2.0f, this.getY() + 10.0f, ColorUtil.applyOpacity(-1, 0.75f));
        FontManager.getProductSansRegular(12).drawCenteredString(this.status, this.getX() + this.getWidth() / 2.0f, this.getY() + 25.0f, ColorUtil.applyOpacity(-1, 0.75f));
        final Color noColor = ColorUtil.applyOpacity(Color.WHITE, 0.0f);
        int count = 0;
        final int spacing = 8;
        final float diff = 35.0f;
        for (final TextField textField : this.textFields) {
            textField.setXPosition(this.getX() + diff / 2.0f);
            textField.setYPosition(this.getY() + 35.0f + count);
            textField.setWidth(this.getWidth() - diff);
            textField.setHeight(22.0f);
            textField.setBackgroundText((count == 0) ? "Email or username" : "Password");
            textField.setOutline(noColor);
            textField.setFill(ColorUtil.tripleColor(17));
            textField.setTextAlpha(0.35f);
            textField.setMaxStringLength(560);
            textField.drawTextBox();
            count += (int)(textField.getHeight() + spacing);
        }
        final float actionY = this.getY() + 98.0f;
        final float actionWidth = 90.0f;
        final float buttonSpacing = 10.0f;
        final float firstX = this.getX() + this.getWidth() / 2.0f - (this.actionButtons.size() * actionWidth + 10.0f) / 2.0f;
        int seperation = 0;
        for (final AltButton actionButton : this.actionButtons) {
            actionButton.setBypass(true);
            actionButton.setColor(ColorUtil.tripleColor(55));
            actionButton.setAlpha(1.0f);
            actionButton.setX(firstX + seperation);
            actionButton.setY(actionY);
            actionButton.setWidth(actionWidth);
            actionButton.setHeight(20.0f);
            TextField cracked;
            actionButton.setClickAction(() -> {
                if (actionButton.getName().equals("Offline Login")) {
                    cracked = this.textFields.get(0);
                    Athena.INSTANCE.getAccountManager().getAccounts().add(new Account(AccountType.CRACKED, cracked.getText(), "0", "0"));
                    Minecraft.getMinecraft().session = new Session(cracked.getText(), "0", "0", "legacy");
                    this.status = "Logged into " + cracked.getText();
                    Athena.INSTANCE.getAccountManager().setCurrentAccount(Athena.INSTANCE.getAccountManager().getAccountByUsername(cracked.getText()));
                    Athena.INSTANCE.getAccountManager().isFirstLogin = false;
                    Athena.INSTANCE.getAccountManager().save();
                }
                return;
            });
            actionButton.drawScreen(mouseX, mouseY);
            seperation += (int)(actionWidth + buttonSpacing);
        }
        final float microsoftY = actionY + 35.0f;
        final float microWidth = 240.0f;
        final float microHeight = 35.0f;
        final float microX = this.getX() + this.getWidth() / 2.0f - microWidth / 2.0f;
        this.hoveringMicrosoft = HoveringUtil.isHovering(microX - 2.0f, microsoftY - 2.0f, microWidth + 4.0f, microHeight + 4.0f, mouseX, mouseY);
        this.hoverMicrosoftAnim.setDirection(this.hoveringMicrosoft ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtils.drawRound(microX, microsoftY, microWidth, microHeight, 5.0f, ColorUtil.applyOpacity(Color.BLACK, 0.2f + 0.25f * this.hoverMicrosoftAnim.getOutput().floatValue()));
        FontManager.getProductSansRegular(26).drawString("Microsoft Login", microX + 10.0f, microsoftY + 4.0f, -1);
        FontManager.getProductSansRegular(16).drawString("Login by entering login details and clicking here", microX + 10.0f, microsoftY + 23.0f, -1);
        final float logoSize = 22.0f;
        DrawUtils.drawMicrosoftLogo(microX + microWidth - (10.0f + logoSize), microsoftY + microHeight / 2.0f - logoSize / 2.0f, logoSize, 1.5f);
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int button) {
        this.textFields.forEach(textField -> textField.mouseClicked(mouseX, mouseY, button));
        this.actionButtons.forEach(actionButton -> actionButton.mouseClicked(mouseX, mouseY, button));
        if (this.hoveringMicrosoft && button == 0) {
            final MicrosoftAuthenticator authenticator;
            final TextField username;
            String email;
            String password;
            String[] split;
            MicrosoftAuthResult acc;
            new Thread(() -> {
                authenticator = new MicrosoftAuthenticator();
                username = this.textFields.get(0);
                email = username.getText();
                password = this.textFields.get(1).getText();
                if (email.contains(":")) {
                    split = email.split(":");
                    if (split.length != 2) {
                        return;
                    }
                    else {
                        email = split[0];
                        password = split[1];
                    }
                }
                try {
                    Athena.INSTANCE.getLog().info(email + password);
                    acc = authenticator.loginWithCredentials(email, password);
                    Minecraft.getMinecraft().session = new Session(acc.getProfile().getName(), acc.getProfile().getId(), acc.getAccessToken(), "legacy");
                    this.status = "Logged into " + acc.getProfile().getName();
                    Athena.INSTANCE.getAccountManager().getAccounts().add(new Account(AccountType.MICROSOFT, acc.getProfile().getName(), acc.getProfile().getId(), acc.getRefreshToken()));
                    Athena.INSTANCE.getAccountManager().setCurrentAccount(Athena.INSTANCE.getAccountManager().getAccountByUsername(acc.getProfile().getName()));
                    Athena.INSTANCE.getAccountManager().isFirstLogin = false;
                    Athena.INSTANCE.getAccountManager().save();
                    Athena.INSTANCE.getLog().info("Success: Logged into " + acc.getProfile().getName());
                }
                catch (MicrosoftAuthenticationException e) {
                    e.printStackTrace();
                }
                this.resetTextFields();
            }).start();
        }
    }
    
    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int state) {
    }
    
    private void resetTextFields() {
        this.textFields.forEach(textField -> textField.setText(""));
    }
    
    static {
        LoginPanel.cracked = false;
    }
}
