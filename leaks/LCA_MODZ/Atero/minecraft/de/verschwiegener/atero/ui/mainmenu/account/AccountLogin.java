package de.verschwiegener.atero.ui.mainmenu.account;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.util.components.CustomGuiButton;
import de.verschwiegener.atero.util.components.CustomGuiTextField;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class AccountLogin extends GuiScreen {
    
    private AccountManagerScreen2 parent;
    private CustomGuiTextField emailField;
    private CustomGuiTextField passwordField;
    private Font font;
    private String mode;
    private String email, passwort;

    private boolean altening;

    public AccountLogin(AccountManagerScreen2 parent, String mode, boolean altening) {
	this.parent = parent;
	this.font = Management.instance.font;
	this.mode = mode;
	email = "";
	passwort = "";
	this.altening = altening;
    }

    public AccountLogin(AccountManagerScreen2 parent, String mode, String email, String passwort) {
	this.parent = parent;
	this.font = Management.instance.font;
	this.mode = mode;
	this.email = email;
	this.passwort = passwort;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        System.err.println("init");
        int xPos = this.width / 2 - 100;
        int yPos = this.height / 2 - 50;
        this.emailField = new CustomGuiTextField(0, fontRendererObj, xPos + 55, yPos + 15, 140, 20);
        emailField.setText(email);
        this.passwordField = new CustomGuiTextField(0, fontRendererObj, xPos + 55, yPos + 35, 140, 20);
        passwordField.setText(passwort);
        this.buttonList.add(new CustomGuiButton(1, xPos + 10, yPos + 57, font.getStringWidth2(mode) + 5, 20, mode, true));
        this.buttonList.add(new CustomGuiButton(2, xPos + 150, yPos + 57, 40, 20, "Cancel", true));
        this.buttonList.add(new CustomGuiButton(3, xPos + 72, yPos + 57, 55, 20, "Clipboard", true));
    }
    
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        parent.drawScreen(mouseX, mouseY, partialTicks);
        int xPos = this.width / 2 - 100;
        int yPos = this.height / 2 - 50;
        RenderUtil.drawRectRound(xPos, yPos, 200, 80, 5, Management.instance.colorBlack);
        emailField.drawTextBox();
        passwordField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        font.drawString("Login", xPos + 100, yPos, Color.WHITE.getRGB());
        font.drawString("Email:", xPos + 5, yPos + 20, Color.WHITE.getRGB());
        font.drawString("Password:", xPos + 5, yPos + 40, Color.WHITE.getRGB());
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        emailField.mouseClicked(mouseX, mouseY, mouseButton);
        passwordField.mouseClicked(mouseX, mouseY, mouseButton);
        if(emailField.isHovered(mouseX, mouseY)) {
            emailField.setTextColor(Color.WHITE.getRGB());
        }
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        emailField.textboxKeyTyped(typedChar, keyCode);
        passwordField.textboxKeyTyped(typedChar, keyCode);
    }
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
	case 1:
	    if(emailField.getText().contains("@")) {
		 parent.handleArgs(mode, emailField.getText(), passwordField.getText(), this.altening);
		 mc.displayGuiScreen(parent);
	    }else {
		emailField.setTextColor(Color.RED.getRGB());
	    }
	    break;
	case 2:
	    mc.displayGuiScreen(parent);
	    break;

	case 3:
	    System.out.println("Clipboard");
	    if (getClipboardString().contains(":")) {
		String[] args = getClipboardString().split(":");
		if (args.length == 2) {
		    parent.handleArgs(mode, args[0], !this.altening ? args[1] : Management.instance.CLIENT_NAME, this.altening);
		    mc.displayGuiScreen(parent);
		}else{
            parent.handleArgs(mode, args[0], Management.instance.CLIENT_NAME, this.altening);
            mc.displayGuiScreen(parent);
        }
	    }else{
            parent.handleArgs(mode, getClipboardString(), Management.instance.CLIENT_NAME, this.altening);
            mc.displayGuiScreen(parent);
        }
	default:
	    break;
	}
    }
    @Override
    protected boolean closeGUI() {
	mc.displayGuiScreen(parent);
	return false;
    }

}
