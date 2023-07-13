package me.r.touchgrass.altmanager;

import java.io.IOException;

import com.thealtening.auth.AltService;
import me.r.touchgrass.touchgrass;
import me.r.touchgrass.altmanager.account.Account;
import me.r.touchgrass.altmanager.components.GuiAccountList;
import me.r.touchgrass.altmanager.impl.GuiAddAlt;
import me.r.touchgrass.altmanager.impl.GuiAltLogin;
import me.r.touchgrass.altmanager.impl.GuiAlteningLogin;
import me.r.touchgrass.altmanager.thread.AccountLoginThread;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiAltManager extends GuiScreen {

	public static GuiAltManager INSTANCE;

	private GuiAccountList accountList;

	public Account currentAccount;

	public AccountLoginThread loginThread;

	private String status = "§eWaiting for login...";

	public GuiAltManager() {
		INSTANCE = this;
	}

	public void initGui() {
		this.accountList = new GuiAccountList(this);
		this.accountList.registerScrollButtons(7, 8);
		this.accountList.elementClicked(-1, false, 0, 0);
		this.buttonList.add(new GuiButton(0, this.width / 2 + 54, this.height - 24, 100, 20, "Cancel"));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 154, this.height - 48, 100, 20, "Login"));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 50, this.height - 24, 100, 20, "Remove"));
		this.buttonList.add(new GuiButton(3, this.width / 2 - 154, this.height - 24, 100, 20, "Add"));
		this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 48, 100, 20, "Direct Login"));
		this.buttonList.add(new GuiButton(6, this.width / 2 + 4 + 50, this.height - 48, 100, 20, "TheAltening"));
	}

	public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
		ScaledResolution scaledResolution = new ScaledResolution(this.mc);
		drawDefaultBackground();
		this.accountList.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
		super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);

		if (this.loginThread != null)
			status = this.loginThread.getStatus();
		if (touchgrass.getClient().altService.getCurrentService().equals(AltService.EnumAltService.MOJANG)) {
			drawCenteredString(this.mc.fontRendererObj, status, scaledResolution.getScaledWidth() / 2, 6, -3158065);
			drawCenteredString(this.mc.fontRendererObj, "Accounts: " + touchgrass.getClient().accountManager.getAccounts().size(), this.width / 2, 20, -1);
		} else
			drawCenteredString(this.mc.fontRendererObj, "Logged in with TheAltening §7(" + this.mc.getSession().getUsername() + ")", this.width / 2, 20, -1);
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.accountList.handleMouseInput();
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			if (this.loginThread == null || !this.loginThread.getStatus().contains("Logging in"))
				this.mc.displayGuiScreen(new GuiMainMenu());
			break;
		case 1:
			if (this.accountList.selected == -1)
				return;
			this.loginThread = new AccountLoginThread(this.accountList.getSelectedAccount().getEmail(), this.accountList.getSelectedAccount().getPassword());
			this.loginThread.start();
			break;
		case 2:
			this.accountList.removeSelected();
			this.accountList.selected = -1;
			break;
		case 3:
			if (this.loginThread != null)
				this.loginThread = null;
			this.mc.displayGuiScreen(new GuiAddAlt(this));
			break;
		case 4:
			if (this.loginThread != null)
				this.loginThread = null;
			this.mc.displayGuiScreen(new GuiAltLogin(this));
			break;
		case 6:
			this.mc.displayGuiScreen(new GuiAlteningLogin(this));
			break;
		}
	}

	public void login(Account account) {
		this.loginThread = new AccountLoginThread(account.getEmail(), account.getPassword());
		this.loginThread.start();
	}

}