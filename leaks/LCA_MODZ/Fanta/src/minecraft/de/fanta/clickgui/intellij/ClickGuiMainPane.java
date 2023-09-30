package de.fanta.clickgui.intellij;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.clickgui.intellij.openables.ClickGuiOpenable;
import de.fanta.clickgui.intellij.openables.ClickGuiOpenableConfig;
import de.fanta.clickgui.intellij.openables.ClickGuiOpenableModule;
import de.fanta.gui.font.ClientFont;
import de.fanta.gui.font.GlyphPageFontRenderer;
import de.fanta.module.Module;
import de.fanta.module.Module.Type;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.FileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class ClickGuiMainPane extends Gui {

	private ClickGuiScreen parentScreen;
	private float width, height, x, y;

	public Module openedModule = null;
	private Setting selectedSetting = null;
	private File configDir = new File(
			Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/" + "configs" + "/");

	public ClickGuiMainPane(ClickGuiScreen parentScreen) {
		this.parentScreen = parentScreen;
		this.width = this.parentScreen.width / 1.5F;
		this.height = this.parentScreen.height / 1.5F;
		this.x = this.parentScreen.width / 2 - this.width / 2;
		this.y = this.parentScreen.height / 2 - this.height / 2;
		List<ClickGuiOpenable> categoryOpenables = new ArrayList<>();
		for (Type category : Type.values()) {
			List<ClickGuiOpenable> toCategory = new ArrayList<>();
			for (Module module : Client.INSTANCE.moduleManager.modules) {
				if (module.getType() == category) {
					ClickGuiOpenableModule mo = new ClickGuiOpenableModule(0, 0, 17,
							ClickGuiOpenable.MENU_FONT.getStringWidth(module.getName().replace(" ", "") + ".java") + 2,
							module, this);
					toCategory.add(mo);
				}
			}
			categoryOpenables.add(new ClickGuiOpenable(0, 0, 15,
					ClickGuiOpenable.MENU_FONT.getStringWidth(category.name()), toCategory,
					de.fanta.clickgui.intellij.openables.ClickGuiOpenable.Type.FOLDER, category.name()));
		}
		List<ClickGuiOpenable> configOpenablesOffline = new ArrayList<>();
		if (configDir.isDirectory()) {
			for (File f : configDir.listFiles()) {
				try {
					if(f.exists() && f.isFile() && FileUtils.readLines(f).size() > 0) {
						String string = f.getName();
						if(string.endsWith(".txt")); string = string.substring(0, string.length()-4); 
						ClickGuiOpenableConfig mo = new ClickGuiOpenableConfig(0, 0, 17,
								ClickGuiOpenable.MENU_FONT.getStringWidth(string) + 2,
								string, this, false);
						configOpenablesOffline.add(mo);
					}					
				} catch (Exception e) {
				}
			}
		}
		
		List<ClickGuiOpenable> configOpenablesOnline = new ArrayList<>();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			ChatUtil.sendChatMessageWithPrefix("Loading...");
			ArrayList<String> configs = new ArrayList<>();
			try {
				URLConnection urlConnection = new URL(
						"https://raw.githubusercontent.com/LCAMODZ/Fanta-configs/main/configs.json")
								.openConnection();
				urlConnection.setConnectTimeout(10000);
				urlConnection.connect();
				try (BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()))) {
					String text;
					while ((text = bufferedReader.readLine()) != null) {
						if (text.contains("404: Not Found")) {
							ChatUtil.sendChatMessageWithPrefix(
									"An error occurred while loading the configs from Github.");
							return;
						}
						configs.add(text);
					}
				}
			} catch (IOException e) {
			}
			for (String config : configs) {
				ClickGuiOpenableConfig mo = new ClickGuiOpenableConfig(0, 0, 17,
						ClickGuiOpenable.MENU_FONT.getStringWidth(config) + 2,
						config, this, true);
				configOpenablesOnline.add(mo);
			}
		});
		if(true/*show online configs*/){
			try {
				executor.shutdown();
				executor.awaitTermination(1, TimeUnit.SECONDS);
			} catch (Exception e) {
			}
			finally {
				executor.shutdownNow();
			}
		}
		modulesOpenable = new ClickGuiOpenable(0, 0, 15, ClickGuiOpenable.MENU_FONT.getStringWidth("Modules"),
				categoryOpenables, de.fanta.clickgui.intellij.openables.ClickGuiOpenable.Type.FOLDER, "Modules");
		
		
		List<ClickGuiOpenable> configsOpenables = Arrays.asList(new ClickGuiOpenable(0, 0, 15, ClickGuiOpenable.MENU_FONT.getStringWidth("Offline"), configOpenablesOffline, de.fanta.clickgui.intellij.openables.ClickGuiOpenable.Type.FOLDER, "Offline"), new ClickGuiOpenable(0, 0, 15, ClickGuiOpenable.MENU_FONT.getStringWidth("Online"), configOpenablesOnline, de.fanta.clickgui.intellij.openables.ClickGuiOpenable.Type.FOLDER, "Online"));
		configsOpenable = new ClickGuiOpenable(0, 0, 15, ClickGuiOpenable.MENU_FONT.getStringWidth("Configs"),
				configsOpenables, de.fanta.clickgui.intellij.openables.ClickGuiOpenable.Type.FOLDER, "Configs");
		List<ClickGuiOpenable> openablesMain = Arrays.asList(modulesOpenable, configsOpenable);
		fantaOpenable = new ClickGuiOpenable(x, y, 15, ClickGuiOpenable.MENU_FONT.getStringWidth("Fanta"),
				openablesMain, de.fanta.clickgui.intellij.openables.ClickGuiOpenable.Type.FOLDER, "Fanta");
		changers = new ArrayList<>();
	}

	ClickGuiOpenable fantaOpenable, modulesOpenable, configsOpenable;

	public static final int CLASS_BACKGROUND_COLOR = Color.decode("#2B2B2B").getRGB(),
			TOPBAR_BACKGROUND_COLOR = Color.decode("#3C3F41").getRGB(),
			SELECTED_LIGHT_BACKGROUND_COLOR = Color.decode("#4E5254").getRGB(),
			CLASS_BLUE = Color.decode("#4A88C7").getRGB(), OUTLINE_BACKGROUND_COLOR = Color.decode("#323232").getRGB(),
			MIDDLE_LINE_COLOR = Color.decode("#555555").getRGB(),
			LINE_DISPLAY_BACKGROUND_COLOR = Color.decode("#313335").getRGB(),
			MENU_FONT_COLOR = Color.decode("#606366").getRGB(),
			MODIFIER_AND_TYPE_FONT_COLOR = Color.decode("#CC7832").getRGB(),
			VARIABLE_NAME_FONT_COLOR = Color.decode("#9675A8").getRGB(),
			VALUE_FONT_COLOR = Color.decode("#6694B7").getRGB(),
			TYPE_AS_OBJECT_FONT_COLOR = Color.decode("#A9B7C6").getRGB();

	public static final GlyphPageFontRenderer MENU_FONT = ClientFont.font(18, "JetBrainsMono-Light", true);

	public boolean modulesOpened = false;

	public int index = 0;
	public int horizontalIndex = 0;

	public boolean init = true;
	public List<GuiTextChanger> changers;

	public void draw(float mouseX, float mouseY) {
		/* Variables */
		Minecraft mc = Minecraft.getMinecraft();
		final float TOP_BAR_HEIGHT = this.height / 22.5F;
		final float CLASS_EXPLORER_WIDTH = this.width / 4.5F;
		final float LINE_DISPLAY_WIDTH = this.width / 19;
		final float MIDDLE_LINE_WIDTH = 1; /* LINE_DISPLAY_WIDTH/2 */
		final float MODULE_DISPLAY_WIDTH = (CLASS_EXPLORER_WIDTH / 4) * 3;
		final float MODIFIER = 14F;
		/* Main Classviewer Background */
		drawRect(x, y, x + width, y + height, CLASS_BACKGROUND_COLOR);
		/* Top Class Bar */
		drawRect(x, y, x + width, y + TOP_BAR_HEIGHT, TOPBAR_BACKGROUND_COLOR);
		drawHollowRect(x, y, width, TOP_BAR_HEIGHT, 1, OUTLINE_BACKGROUND_COLOR);
		/* Module Selection Menu */
		drawRect(x, y + TOP_BAR_HEIGHT, x + CLASS_EXPLORER_WIDTH, y + height, TOPBAR_BACKGROUND_COLOR);
		/* Line Display */
		drawRect(x + CLASS_EXPLORER_WIDTH, y + TOP_BAR_HEIGHT, x + CLASS_EXPLORER_WIDTH + LINE_DISPLAY_WIDTH,
				y + height, LINE_DISPLAY_BACKGROUND_COLOR);
		drawRect(x + CLASS_EXPLORER_WIDTH + LINE_DISPLAY_WIDTH, y + TOP_BAR_HEIGHT,
				x + CLASS_EXPLORER_WIDTH + LINE_DISPLAY_WIDTH + MIDDLE_LINE_WIDTH, y + height, MIDDLE_LINE_COLOR);

		int lines = 0;
		for (GuiTextChanger changer : changers) {
			lines += changer.lines;
		}
		for (int i = 0; i < lines; i++) {
			MENU_FONT.drawString("" + (i + 1), x + CLASS_EXPLORER_WIDTH + 6, y + TOP_BAR_HEIGHT + (i * MODIFIER) + 2,
					MENU_FONT_COLOR);
		}

		/* Selected Module Display */
		if (openedModule != null) {
			drawRect(x + 1, y + 1, x + MODULE_DISPLAY_WIDTH, y + TOP_BAR_HEIGHT - 1, SELECTED_LIGHT_BACKGROUND_COLOR);
			MENU_FONT.drawString(openedModule.getName().replace(" ", "") + ".java", x + 1 + 16,
					y + MENU_FONT.getFontHeight() / 4 - 1, Color.white.getRGB());
			mc.getTextureManager().bindTexture(new ResourceLocation("textures/class.png"));
			drawModalRectWithCustomSizedTexture(x + 3, y + ((TOP_BAR_HEIGHT) / 4) - 2, 0, 0, 12, 12, 12, 12);
			drawRect(x + 1, y + TOP_BAR_HEIGHT - 3, x + MODULE_DISPLAY_WIDTH, y + TOP_BAR_HEIGHT - 1, CLASS_BLUE);
		}

		/* Class inner */
		if (init && openedModule != null) {
			update();
			changers.clear();
			for (int i = 0; i < openedModule.getSettings().size(); i++) {
				Setting setting = openedModule.getSettings().get(i);
				GuiTextChanger changer = null;
				if (setting.getSetting() instanceof DropdownBox) {
					changer = new GuiTextChanger("private", "", "String", setting.getName(),
							setting.getSetting().getValue().toString(),
							x + CLASS_EXPLORER_WIDTH + LINE_DISPLAY_WIDTH + 6, y + TOP_BAR_HEIGHT + (i * MODIFIER),
							setting);
				} else if (setting.getSetting() instanceof CheckBox) {
					changer = new GuiTextChanger("private", "", "boolean", setting.getName(),
							setting.getSetting().getValue().toString(),
							x + CLASS_EXPLORER_WIDTH + LINE_DISPLAY_WIDTH + 6, y + TOP_BAR_HEIGHT + (i * MODIFIER),
							setting);
				} else if (setting.getSetting() instanceof Slider) {
					changer = new GuiTextChanger("private", "", "float", setting.getName(),
							setting.getSetting().getValue().toString(),
							x + CLASS_EXPLORER_WIDTH + LINE_DISPLAY_WIDTH + 6, y + TOP_BAR_HEIGHT + (i * MODIFIER),
							setting);
				} else if (setting.getSetting() instanceof ColorValue) {
					changer = new GuiTextChanger("private", "", "int", setting.getName(),
							setting.getSetting().getValue().toString(),
							x + CLASS_EXPLORER_WIDTH + LINE_DISPLAY_WIDTH + 6, y + TOP_BAR_HEIGHT + (i * MODIFIER),
							setting);
				}
				changers.add(changer);
			}
			init = false;
		}
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		sccissor((int) x + (int) CLASS_EXPLORER_WIDTH + (int) LINE_DISPLAY_WIDTH, (int) y + (int) TOP_BAR_HEIGHT,
				(int) width - ((int) CLASS_EXPLORER_WIDTH + (int) LINE_DISPLAY_WIDTH), (int) height, 35);
		for (int i = 0; i < changers.size(); i++) {
			GuiTextChanger changer = changers.get(i);
			changer.draw(mouseX, mouseY);
			update();
			changer.x = x + CLASS_EXPLORER_WIDTH + LINE_DISPLAY_WIDTH + 6 - horizontalIndex;
			changer.y = y + TOP_BAR_HEIGHT + (i * MODIFIER);
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			int mouseD = Mouse.getDWheel();
			if (mouseD < 0) {
				horizontalIndex += 8;
			}
			if (mouseD > 0) {
				if (horizontalIndex > 0)
					horizontalIndex--;
				if (horizontalIndex > 0)
					horizontalIndex--;
				if (horizontalIndex > 0)
					horizontalIndex--;
				if (horizontalIndex > 0)
					horizontalIndex--;
				if (horizontalIndex > 0)
					horizontalIndex--;
				if (horizontalIndex > 0)
					horizontalIndex--;
				if (horizontalIndex > 0)
					horizontalIndex--;
				if (horizontalIndex > 0)
					horizontalIndex--;
			}
		}

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		sccissor((int) x, (int) y, (int) CLASS_EXPLORER_WIDTH + 1, (int) height - (int) TOP_BAR_HEIGHT, 16);
		fantaOpenable.draw(mouseX, mouseY);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		this.fantaOpenable.setX(x + 1);
		this.fantaOpenable.setY(y + TOP_BAR_HEIGHT + index);
		boolean allowScroll = fantaOpenable.getHeight() > height - TOP_BAR_HEIGHT;
		int mouseD = Mouse.getDWheel();
		if (allowScroll) {
			if (mouseD < 0 && index > (height - TOP_BAR_HEIGHT) - fantaOpenable.getHeight()) {
				index -= 8;
			}
		}
		if (mouseD > 0) {
			if (index < 0)
				index++;
			if (index < 0)
				index++;
			if (index < 0)
				index++;
			if (index < 0)
				index++;
			if (index < 0)
				index++;
			if (index < 0)
				index++;
			if (index < 0)
				index++;
			if (index < 0)
				index++;
		}
	}

	public void onClose() {
		changers.forEach(changer -> {
			changer.save();
		});
	}

	public String getStringKeyEvents(String input) {
		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
			input = input.substring(0, input.length() - 1);
		}
		return input;
	}

	public void update() {
		this.width = this.parentScreen.width / 1.25F;
		this.height = this.parentScreen.height / 1.25F;
		this.x = this.parentScreen.width / 2 - this.width / 2;
		this.y = this.parentScreen.height / 2 - this.height / 2;
	}

	public void sccissor(int x, int y, int width, int height, int fixValue) {
		final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		GL11.glScissor(x * sr.getScaleFactor(), (sr.getScaledHeight() - y - height) + fixValue * sr.getScaleFactor(),
				width * sr.getScaleFactor(), height * sr.getScaleFactor());
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (GuiTextChanger changer : changers) {
			changer.keyTyped(typedChar, keyCode);
		}
	}

}
