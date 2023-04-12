package markgg.alts;

import java.io.IOException;
import java.net.Proxy;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import markgg.Client;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

import org.lwjgl.input.Keyboard;

public class GuiAddAlt extends GuiScreen{

	private final GuiAltManager manager;
	private String status = EnumChatFormatting.GRAY + "Idle...";
	private GuiTextField username;

	public GuiAddAlt(GuiAltManager manager) {
		this.manager = manager;
	}

	protected void actionPerformed(GuiButton button) {
		AddAltThread login;
		switch (button.id) {
		case 0:
			login = new AddAltThread(username.getText());
			login.start();
			break;

		case 1:
			mc.displayGuiScreen(manager);
			break;
		} 
	}


	public void drawScreen(int i2, int j2, float f2) {
		drawDefaultBackground();
		username.drawTextBox();
		drawCenteredString(fontRendererObj, "Add Alt", (width / 2), 20.0F, -1);
		if (username.getText().isEmpty()) {
			drawString(mc.fontRendererObj, "Username", width / 2 - 96, 66, -7829368);
		}
		drawCenteredString(fontRendererObj, status, (width / 2), 30.0F, -1);
		super.drawScreen(i2, j2, f2);
	}


	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 92 + 12, "Add & Login"));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 116 + 12, "Back"));
		username = new GuiTextField(eventButton, mc.fontRendererObj, width / 2 - 100, 60, 200, 20);
	}


	protected void keyTyped(char par1, int par2) {
		username.textboxKeyTyped(par1, par2);
		if (par1 == '\t' && (username.isFocused())) {
			username.setFocused(!username.isFocused());
		} 
		if (par1 == '\r') {
			actionPerformed((GuiButton) buttonList.get(0));
		}
	}


	protected void mouseClicked(int par1, int par2, int par3) {
		try {
			super.mouseClicked(par1, par2, par3);
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		username.mouseClicked(par1, par2, par3);
	}


	private class AddAltThread
	extends Thread
	{
		private final String username;

		public AddAltThread(String username) {
			this.username = username;
			GuiAddAlt.this.status = EnumChatFormatting.GRAY + "Idle...";
		}

		public void run() {
			AltManager altManager = Client.altManager;
			AltManager.registry.add(new Alt(username, ""));
			GuiAddAlt.this.status = EnumChatFormatting.GREEN + "Alt added. (" + username + " - offline alt)";
			mc.session = new Session(username, "", "", "legacy");
		}
	}
}
