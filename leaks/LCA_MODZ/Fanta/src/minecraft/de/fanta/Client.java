package de.fanta;

import java.nio.ByteBuffer;
import java.util.Objects;

import org.lwjgl.opengl.Display;

import com.github.creeper123123321.viafabric.ViaFabric;

import de.fanta.clickgui.astolfo.ClickGuiScreen;
import de.fanta.clickgui.defaultgui.ClickGui;
import de.fanta.command.CommandManager;
import de.fanta.design.AltsSaver;
import de.fanta.design.fonts.FontManager;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventRegisterModule;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.module.Module;
import de.fanta.module.ModuleManager;
import de.fanta.module.impl.combat.TestAura;
import de.fanta.utils.BlurHelper;
import de.fanta.utils.BlurHelper2;
import de.fanta.utils.FileUtil;
import de.fanta.utils.IrcChatListener;
import de.fanta.utils.ShaderBackgrundAPI;
import de.fanta.utils.ShaderBackgrundAPI3;
import de.fanta.utils.UnicodeFontRenderer;
import de.fanta.utils.UnicodeFontRenderer10;
import de.fanta.utils.UnicodeFontRenderer11;
import de.fanta.utils.UnicodeFontRenderer12;
import de.fanta.utils.UnicodeFontRenderer13;
import de.fanta.utils.UnicodeFontRenderer14;
import de.fanta.utils.UnicodeFontRenderer15;
import de.fanta.utils.UnicodeFontRenderer16;
import de.fanta.utils.UnicodeFontRenderer17;
import de.fanta.utils.UnicodeFontRenderer2;
import de.fanta.utils.UnicodeFontRenderer3;
import de.fanta.utils.UnicodeFontRenderer4;
import de.fanta.utils.UnicodeFontRenderer5;
import de.fanta.utils.UnicodeFontRenderer6;
import de.fanta.utils.UnicodeFontRenderer7;
import de.fanta.utils.UnicodeFontRenderer8;
import de.fanta.utils.UnicodeFontRenderer9;
import de.hero.clickgui.ClickGUI;
import de.jo.scripting.base.ScriptEngine;
import de.liquiddev.ircclient.api.IrcClient;
import de.liquiddev.ircclient.client.ClientType;
import de.liquiddev.ircclient.client.IrcClientFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

public class Client {

	public static Client INSTANCE;

	public ModuleManager moduleManager;
	public static BlurHelper blurHelper;
	public static BlurHelper2 blurHelper2;
	public UnicodeFontRenderer unicodeBasicFontRenderer, arial;
	public UnicodeFontRenderer2 unicodeBasicFontRenderer2, arial2;
	public UnicodeFontRenderer3 unicodeBasicFontRenderer3, arial3;
	public UnicodeFontRenderer4 unicodeBasicFontRenderer4, Roboto;
	public UnicodeFontRenderer4 heroTabGui;
	public UnicodeFontRenderer5 unicodeBasicFontRenderer5, arial5;
	public UnicodeFontRenderer6 unicodeBasicFontRenderer6, pepsi;
	public UnicodeFontRenderer7 unicodeBasicFontRenderer7, arialbold;
	public UnicodeFontRenderer8 fluxTabGuiFont;
	public UnicodeFontRenderer8 fluxTabGuiFont2;
	public UnicodeFontRenderer9 fluxicon;
	public UnicodeFontRenderer10 Sigma;
	public UnicodeFontRenderer11 vanta;
	public UnicodeFontRenderer12 ViolenceTabGUi;
	public UnicodeFontRenderer13 Skid;
	public UnicodeFontRenderer14 Violence;
	public UnicodeFontRenderer14 Violence2;
	public UnicodeFontRenderer15 Holo;
	public UnicodeFontRenderer16 Jello;
	public UnicodeFontRenderer16 Jello2;
	public UnicodeFontRenderer16 Jello3;
	public UnicodeFontRenderer17 verdana;
	public UnicodeFontRenderer17 verdana2;
	public static ClickGUI clickgui;
	public static ClickGui clickGui;
	public final String name = "Fanta", version = "13", prefix = "[" + name + "]";
	public static final String[] authors = new String[] {"Command_JO", "LCA_MODZ","Exeos"};
	public CommandManager commandManager;
	public FontManager fontManager;
	public IrcClient ircClient;
	public FileUtil fileUtil;
	public static ShaderBackgrundAPI3 backgrundAPI3;
	public static ShaderBackgrundAPI backgrundAPI;
	public de.fanta.fontrenderer.FontManager ttFontManager;

	public static boolean allowed = false;
	private double current;

	public ScriptEngine scriptEngine;

	public void onStart() {
		INSTANCE = this;
		fileUtil = new FileUtil();
		FileUtil.loadKeys();
		// Api.init();

		try {
			new ViaFabric().onInitialize();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Display.setTitle(getName() + " v" + getVersion());
		unicodeBasicFontRenderer = UnicodeFontRenderer.getFontOnPC("Arial", 22, 0, 0, 1);
		unicodeBasicFontRenderer2 = UnicodeFontRenderer2.getFontOnPC("Arial", 50, 0, 0, 1);
		unicodeBasicFontRenderer3 = UnicodeFontRenderer3.getFontFromAssets("Winter Insight", 22, 0, 0, 1);
		ViolenceTabGUi = UnicodeFontRenderer12.getFontFromAssets("ViolenceTab", 22, 0, 0, 1);
		unicodeBasicFontRenderer4 = UnicodeFontRenderer4.getFontFromAssets("Roboto-Thin", 22, 0, 0, 1);
		heroTabGui = UnicodeFontRenderer4.getFontFromAssets("Roboto-Thin", 45, 0, 0, 1);
		unicodeBasicFontRenderer6 = UnicodeFontRenderer6.getFontFromAssets("pepsi-light", 20, 0, 0, 1);
		unicodeBasicFontRenderer7 = UnicodeFontRenderer7.getFontFromAssets("arial-bold", 20, 0, 0, 1);
		vanta = UnicodeFontRenderer11.getFontFromAssets("VerdanaBold", 15, 0, 0, 1);
		unicodeBasicFontRenderer5 = UnicodeFontRenderer5.getFontOnPC("Arial", 18, 0, 0, 1);
		arial = UnicodeFontRenderer.getFontOnPC("Arial", 24, 0, 4, 4);
		arial2 = UnicodeFontRenderer2.getFontOnPC("Arial", 33, 0, 0, 1);
		fluxTabGuiFont = UnicodeFontRenderer8.getFontFromAssets("Baloo", 20, 0, 0, 1);
		fluxTabGuiFont2 = UnicodeFontRenderer8.getFontFromAssets("Baloo", 50, 0, 0, 1);
		Sigma = UnicodeFontRenderer10.getFontFromAssets("SF-UI-Display-Medium", 10, 0, 0, 1);
		fluxicon = UnicodeFontRenderer9.getFontFromAssets("Icon", 20, 0, 0, 1);
		Skid = UnicodeFontRenderer13.getFontFromAssets("AldotheApache", 20, 0, 0, 1);
		Violence = UnicodeFontRenderer14.getFontFromAssets("V", 25, 0, 0, 1);
		Violence2 = UnicodeFontRenderer14.getFontFromAssets("V", 35, 0, 0, 1);
		Holo = UnicodeFontRenderer15.getFontFromAssets("Holo", 20, 0, 0, 1);
		Jello = UnicodeFontRenderer16.getFontFromAssets("Jello", 18, 0, 0, 1);
		Jello2 = UnicodeFontRenderer16.getFontFromAssets("Jello", 41, 0, 0, 1);
		Jello3 = UnicodeFontRenderer16.getFontFromAssets("Jello", 16, 0, 0, 1);
		verdana = UnicodeFontRenderer17.getFontFromAssets("verdana", 20, 0, 0, 1);
		verdana2 = UnicodeFontRenderer17.getFontFromAssets("verdana", 16, 0, 0, 1);
		ttFontManager = new de.fanta.fontrenderer.FontManager();
		// ViolenceTabGUi = UnicodeFontRenderer12.getfont("big_noodle_titling", 24, 0,
		// 0, 1);

		moduleManager = new ModuleManager();
		fontManager = new FontManager();
		AltsSaver.altsReader();
		blurHelper = new BlurHelper();
		blurHelper.init();
		blurHelper2 = new BlurHelper2();
		Client.blurHelper2.init();
		clickGui = new ClickGui(false, 50, 50);
		clickgui = new ClickGUI();
		commandManager = new CommandManager();
		backgrundAPI3 = new ShaderBackgrundAPI3();
		backgrundAPI = new ShaderBackgrundAPI();

		String ign = Minecraft.getMinecraft().session.getUsername();

		this.ircClient = IrcClientFactory.getDefault().createIrcClient(ClientType.FANTA, "Bekommt ihr auch nd <3", ign,
				version);

		this.ircClient.getApiManager().registerApi(new IrcChatListener());

		this.ircClient.getApiManager().registerCustomDataListener((sender, tag, data) -> {
			final ByteBuffer buffer = ByteBuffer.wrap(data);
			final int id = buffer.getInt();

			if (tag.toLowerCase().startsWith("report_")) {
				Minecraft.getMinecraft().thePlayer
						.sendChatMessage("/report " + tag.split("report_")[1] + " hacking confirm");
			}

		});
		this.scriptEngine = new ScriptEngine();
	}

	public void onStart2() {
		INSTANCE = this;
		fileUtil = new FileUtil();
		FileUtil.loadKeys();

		Display.setTitle(getName() + " v" + getVersion());

		unicodeBasicFontRenderer = UnicodeFontRenderer.getFontOnPC("Arial", 22, 0, 0, 1);
		arial = UnicodeFontRenderer.getFontOnPC("Arial", 24, 0, 4, 4);
		moduleManager = new ModuleManager();
		fontManager = new FontManager();

		clickGui = new ClickGui(false, 50, 50);
		commandManager = new CommandManager();

	}

	public void onEvent(Event e) {
		try {
			if (Minecraft.getMinecraft().thePlayer == null)
				return;
			for (Module mod : moduleManager.modules) {
				if (!Client.INSTANCE.moduleManager.getModule("Flight").isState()
						&& !Client.INSTANCE.moduleManager.getModule("Speed").isState()
						&& !Client.INSTANCE.moduleManager.getModule("Step").isState()) {
					Minecraft.getMinecraft().thePlayer.speedInAir = 0.02F;
				}

				if (!mod.state) {
					continue;
				}
				if (e == null || mod == null)
					return;
				mod.onEvent(e);
			}
		} catch (Exception e2) {
		}
		if (e instanceof EventRender2D) {
			if ((moduleManager.getModule(TestAura.class) == null || !moduleManager.getModule(TestAura.class).isState()) && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
				current = 0.7;
				moduleManager.getModule(TestAura.class).dragging = false;
				return;
			}
			moduleManager.getModule(TestAura.class).drawTargetHud();
		}
	}

	public void shutdown() {
		System.out.print("Exeos is sexy ^^");
	}

	public final String getName() {
		return name;
	}

	public final String getVersion() {
		return version;
	}

	public final String[] getAuthors() {
		return authors;
	}

	public static ShaderBackgrundAPI3 getBackgrundAPI3() {
		return backgrundAPI3;
	}

	public static ShaderBackgrundAPI getBackgrundAPI() {
		return backgrundAPI;
	}

}
