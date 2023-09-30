package net.minecraft.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.AltManager.GuiAltManager;
import me.wavelength.baseclient.account.Account;
import me.wavelength.baseclient.event.events.ServerLeaveEvent;
import me.wavelength.baseclient.overlay.GuiClientLogin;
import me.wavelength.baseclient.thealtening.AlteningAlt;
import me.wavelength.baseclient.thealtening.TheAltening;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

public class GuiDisconnected extends GuiScreen {

	private String reason;
	private IChatComponent message;
	private List<String> multilineMessage;
	private final GuiScreen parentScreen;
	private int field_175353_i;

	public static ServerData serverData;

	public static boolean useTheAltening;

	private String status = "&bWhat do you want to do?";

	public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
		this.parentScreen = screen;
		this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
		this.message = chatComp;

		this.serverData = Minecraft.getMinecraft().getCurrentServerData();
		serverData.setConnected(false);

		BaseClient.instance.getEventManager().call(new ServerLeaveEvent(serverData, reason, message));
	}

	public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp,  boolean minecraft) {
		this.parentScreen = screen;
		this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
		this.message = chatComp;

		this.serverData = Minecraft.getMinecraft().getCurrentServerData();
		serverData.setConnected(false);

		if (minecraft)
			BaseClient.instance.getEventManager().call(new ServerLeaveEvent(serverData, reason, message));
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		this.buttonList.clear();
		this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
		this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu", new Object[0])));
		this.buttonList.add(new GuiButton(1, this.width / 2 + 103, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, 90, 20, "Go to AltManager"));
		this.buttonList.add(new GuiButton(8, this.width / 2 + 103, this.height / 2 + this.field_175353_i / 2 + 81, 90, 20, "Relog"));
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		/**
		 * If we click the button 0 or 1 (so back to server list or go to the Alt
		 * Manager) we are leaving this screen, so we unset the serverData
		 */

		if (button.id == 8) {
			this.mc.displayGuiScreen(this.parentScreen);
		}
		
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiClientLogin());
		}
		
		
		if (button.id == 1)
			this.mc.displayGuiScreen(new GuiAltManager());


		

		if (button.id == 8 && serverData != null && !(serverData.isConnected()))
			this.mc.displayGuiScreen((GuiScreen) new GuiConnecting(new GuiMainMenu(), this.mc, serverData));
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, Strings.translateColors(status), this.width / 2, 10, 0xffffff);
		this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
		int i = this.height / 2 - this.field_175353_i / 2;

		if (this.multilineMessage != null) {
			for (String s : this.multilineMessage) {
				this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
				i += this.fontRendererObj.FONT_HEIGHT;
			}
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}