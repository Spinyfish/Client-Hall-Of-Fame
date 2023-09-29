package de.verschwiegener.atero.ui.ingame;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.util.Util;
import de.verschwiegener.atero.util.chat.ChatFontRenderer;
import de.verschwiegener.atero.util.chat.ChatRenderer;
import de.verschwiegener.atero.util.components.CustomGuiButton;
import de.verschwiegener.atero.util.components.CustomGuiTextField;
import de.verschwiegener.atero.util.files.config.Config;
import de.verschwiegener.atero.util.files.config.ConfigType;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ConfigUI extends GuiScreen {

    CustomGuiTextField nametxt;
    CustomGuiTextField descriptiontxt;
    CustomGuiTextField servertxt;
    Font titlefont;
    Font font;
    ArrayList<ConfigSettingItem> items = new ArrayList<ConfigUI.ConfigSettingItem>();
    private int scrollOffset;
    private int xPos;
    private int yPos;

    public ConfigUI() {
	titlefont = new Font("FontBold", Util.getFontByName("Inter-ExtraLight"), 6F, true, false);
	font = new Font("FontBold", Util.getFontByName("Inter-ExtraLight"), 5F, false, false);
	System.out.println("Test");

    }

    @Override
    public void initGui() {
	super.initGui();
	xPos = this.width / 2 - 200;
	yPos = this.height / 4 + 24;
	Font buttonfont = new Font("FontBold", Util.getFontByName("Inter-ExtraLight"), 5F, false, false);
	nametxt = new CustomGuiTextField(0, fontRendererObj, xPos + 100, yPos + 25, 280, 20, true);
	descriptiontxt = new CustomGuiTextField(0, fontRendererObj, xPos + 100, yPos + 50, 280, 20, true);
	servertxt = new CustomGuiTextField(0, fontRendererObj, xPos + 100, yPos + 75, 280, 20, true);
	this.buttonList.add(new CustomGuiButton(1, xPos + 350, yPos - 2, 40, 20, "Save", true).setFont(buttonfont));
	this.buttonList.add(new CustomGuiButton(2, xPos + 20, yPos - 2, 40, 20, "Cancel", true).setFont(buttonfont));

	ArrayList<String> settingsNames = new ArrayList<>();
	for (Module m : Management.instance.modulemgr.modules) {
	    settingsNames.add(m.getName());
	}
	for (Setting s : Management.instance.settingsmgr.getSettings()) {
	    if ((s.getItems() != null || s.getItems().isEmpty()) && s.getModule().getCategory() != Category.Render) {
		// settingsNames.add(s.getName());
	    }
	}
	items.clear();
	settingsNames.sort(String::compareToIgnoreCase);
	int settingItemyPos = yPos + ((mc.isSingleplayer()) ? 100 : 75);
	for (String s : settingsNames) {
	    items.add(new ConfigSettingItem(s, settingItemyPos, buttonfont));
	    settingItemyPos += 25;
	}
    }
	int time= 0;
	float beginning= 0;
	float change = 400;
	float duration = 10;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
	
	float animationOffset = change;
	if(time < duration) {
	    animationOffset = easeIn(time, beginning, change, duration);
	    time++;
	    
	}
	
	xPos = this.width / 2 - 200;
	yPos = (int) (this.height / 4 + 24 + (400 - animationOffset));
	RenderUtil.drawRectRound(xPos, yPos, 400, this.height - (this.height / 4 + 24), 5,
		Management.instance.colorBlack);
	titlefont.drawString("Config", (this.width / 2) - (titlefont.getStringWidth("Config") / 4), yPos, Color.WHITE);
	font.drawString("Name:", xPos + 20, yPos + 25, Color.WHITE);
	font.drawString("ServerIP:", xPos + 20, yPos + 75, Color.WHITE);
	font.drawString("Description:", xPos + 20, yPos + 50, Color.WHITE);
	
	nametxt.setyPosition(yPos + 25);
	descriptiontxt.setyPosition(yPos + 50);
	servertxt.setyPosition(yPos + 75);
	buttonList.get(0).yPosition = yPos -2;
	buttonList.get(1).yPosition = yPos -2;
	
	nametxt.drawTextBox();
	descriptiontxt.drawTextBox();
	int settingItemyPos = yPos + 100;
	if (!mc.isSingleplayer()) {
	    servertxt.setVisible(false);
	    settingItemyPos = yPos + 75;
	}
	servertxt.drawTextBox();
	super.drawScreen(mouseX, mouseY, partialTicks);

	GL11.glEnable(GL11.GL_SCISSOR_TEST);
	final int scaleFactor = mc.gameSettings.guiScale;
	RenderUtil.getScissor(xPos, height, 400, 273, scaleFactor);
	int fontOffset = (25 / 2) - (font.getBaseStringHeight() / 2);
	int yOffset = (int) (scrollOffset - (change - animationOffset));
	for (int i = 0; i < items.size(); i++) {
	    ConfigSettingItem item = items.get(i);
	    item.drawItem(xPos, (int) (scrollOffset - (400 - animationOffset)));
	    ChatFontRenderer.drawString(item.name, xPos + 5, item.yPos - yOffset + fontOffset, Color.WHITE);
	}
	GL11.glDisable(GL11.GL_SCISSOR_TEST);	

    }

    @Override
    public void onGuiClosed() {
	super.onGuiClosed();
	System.out.println("Close");
    }
    
    public static float easeIn(float t, float b, float c, float d) {
	return c * t / d + b;
    }

    @Override
    protected boolean closeGUI() {
	System.out.println("CLose1");
	return super.closeGUI();
	//return false;
    }

    public void close() {
	if (closeGUI()) {
	    this.mc.displayGuiScreen((GuiScreen) null);

	    if (this.mc.currentScreen == null) {
		this.mc.setIngameFocus();
	    }
	}
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
	super.actionPerformed(button);
	switch (button.id) {
	case 1:
	    String name = nametxt.getText();
	    String description = descriptiontxt.getText();
	    String server = servertxt.getText();
	    if (!mc.isSingleplayer()) {
		server = Minecraft.getMinecraft().getCurrentServerData().serverIP;
	    }
	    ArrayList<String> selectedModules = new ArrayList<>();
	    for (ConfigSettingItem item : items) {
		if (item.selected) {
		    selectedModules.add(item.name);
		}
	    }
	    System.out.println("Selected: " + selectedModules);
	    Management.instance.configmgr.configs
		    .add(new Config(name, description, server, ConfigType.locale, selectedModules));
	    // mc.displayGuiScreen(null);
	    close();
	    break;
	case 2:
	    // mc.displayGuiScreen(null);
	    close();
	    break;

	default:
	    break;
	}
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
	super.mouseClicked(mouseX, mouseY, mouseButton);
	nametxt.mouseClicked(mouseX, mouseY, mouseButton);
	descriptiontxt.mouseClicked(mouseX, mouseY, mouseButton);
	if (servertxt.getVisible()) {
	    servertxt.mouseClicked(mouseX, mouseY, mouseButton);
	}
	items.forEach(item -> item.mouseClicked(mouseX, mouseY));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
	super.keyTyped(typedChar, keyCode);
	nametxt.textboxKeyTyped(typedChar, keyCode);
	descriptiontxt.textboxKeyTyped(typedChar, keyCode);
	if (servertxt.getVisible()) {
	    servertxt.textboxKeyTyped(typedChar, keyCode);
	}
    }

    @Override
    public void handleMouseInput() throws IOException {
	super.handleMouseInput();
	int settingItemyPos = yPos + 100;
	if (!mc.isSingleplayer()) {
	    settingItemyPos = yPos + 75;
	}
	int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
	int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
	// System.out.println("MouseX: " + Mouse.getEventX() + " Pos: " + xPos);
	if (mouseX > xPos && mouseX < (xPos + 400) && mouseY > settingItemyPos) {
	    System.err.println("Scroll");
	    // TODO implement scroll in Items and make panel open animation
	    int wheelD = Mouse.getEventDWheel() / 10;
	    if (wheelD != 0 && items.size() * 25 > height - 60) {
		scrollOffset += -wheelD;
		if (scrollOffset < 0) {
		    scrollOffset = 0;
		}
		if (height + scrollOffset > items.size() * 25 + 60 && scrollOffset > 0) {
		    scrollOffset = items.size() * 25 + 60 - height;
		}
	    }
	}
    }

    class ConfigSettingItem {

	private String name;
	private Font font;
	private boolean selected = false;
	private int xPos, yOffset, yPos;

	public ConfigSettingItem(String name, int yPos, Font font) {
	    this.name = name;
	    this.yPos = yPos;
	    this.font = font;
	}

	public void drawItem(int xPos, int yOffset) {
	    this.xPos = xPos;
	    this.yOffset = yOffset;
	    RenderUtil.fillRect(xPos, yPos - yOffset, 400, 25, Management.instance.colorGray);
	    RenderUtil.drawRect(xPos, yPos - yOffset, 400, 25, Management.instance.colorBlack, 1F);
	    //ChatFontRenderer.drawString(name, xPos + 5, yPos - yOffset + (25 / 2) - (font.getBaseStringHeight() / 2), Color.WHITE);
	    //font.drawString(name, xPos + 5, yPos - yOffset + (25 / 2) - (font.getBaseStringHeight() / 2), Color.WHITE);
	     //RenderUtil.fillRect(xPos + 370, yPos + (25 / 2) - 6, 12, 12,
	     //Management.instance.colorBlack);
	    Color infillcolor = (selected ? Color.GREEN : Color.ORANGE);
	    RenderUtil.fillRect(xPos + 370, yPos - yOffset + (25 / 2) - 6, 12, 12, infillcolor);
	}

	public void mouseClicked(int mouseX, int mouseY) {
	    if (mouseX > (xPos + 370) && mouseX < (xPos + 382) && mouseY > (yPos - yOffset + (25 / 2) - 6)
		    && mouseY < (yPos - yOffset + (25 / 2) + 6)) {
		selected = !selected;
	    }
	}

    }

}
