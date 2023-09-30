package markgg.alts;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;

public class GuiAltLogin extends GuiScreen{
	private final GuiScreen previousScreen;
	private AltLoginThread thread;
	private GuiTextField username;

	public GuiAltLogin(GuiScreen previousScreen) {
		this.previousScreen = previousScreen;
	}


	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 1:
			mc.displayGuiScreen(previousScreen);
			break;

		case 0:
			thread = new AltLoginThread(username.getText());
			thread.start();
			break;
		} 
	}


	public void drawScreen(int x2, int y2, float z2) {
		drawDefaultBackground();
		username.drawTextBox();
		drawCenteredString(mc.fontRendererObj, "Alt Login", (width / 2), 20.0F, -1);
		drawCenteredString(mc.fontRendererObj, (thread == null) ? (EnumChatFormatting.GRAY + "Idle...") : thread.getStatus(), (width / 2), 29.0F, -1);
		if (username.getText().isEmpty()) {
			drawString(mc.fontRendererObj, "Username", width / 2 - 96, 66, -7829368);
		}
		super.drawScreen(x2, y2, z2);
	}


	public void initGui() {
		int var3 = height / 4 + 24;
		buttonList.add(new GuiButton(0, width / 2 - 100, var3 + 72 + 12, "Login"));
		buttonList.add(new GuiButton(1, width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
		username = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 60, 200, 20);
		username.setFocused(true);
		Keyboard.enableRepeatEvents(true);
	}


	protected void keyTyped(char character, int key) {
		try {
			super.keyTyped(character, key);
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		if (character == '\t') {
			if (!username.isFocused())
				username.setFocused(true);
		}
		if (character == '\r') {
			actionPerformed((GuiButton) buttonList.get(0));
		}
		username.textboxKeyTyped(character, key);
	}


	protected void mouseClicked(int x2, int y2, int button) {
		try {
			super.mouseClicked(x2, y2, button);
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		username.mouseClicked(x2, y2, button);
	}


	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}


	public void updateScreen() {
		username.updateCursorCounter();
	}
}
