package me.eldodebug.soar.management.gui;

import me.eldodebug.soar.gui.GuiQuickPlay;
import me.eldodebug.soar.gui.clickgui.ClickGUI;
import me.eldodebug.soar.gui.mainmenu.GuiSoarMainMenu;
import me.eldodebug.soar.gui.screenshot.GuiScreenshotViewer;

public class GuiManager {

	private ClickGUI clickGUI;
	private GuiSoarMainMenu guiMainMenu;
	private GuiQuickPlay guiQuickPlay;
	private GuiScreenshotViewer guiScreenshotViewer;
	
	public GuiManager() {
		clickGUI = new ClickGUI();
		guiMainMenu = new GuiSoarMainMenu();
		guiQuickPlay = new GuiQuickPlay();
		guiScreenshotViewer = new GuiScreenshotViewer();
	}

	public ClickGUI getClickGUI() {
		return clickGUI;
	}

	public GuiSoarMainMenu getGuiMainMenu() {
		return guiMainMenu;
	}

	public GuiQuickPlay getGuiQuickPlay() {
		return guiQuickPlay;
	}

	public GuiScreenshotViewer getGuiScreenshotViewer() {
		return guiScreenshotViewer;
	}
}
