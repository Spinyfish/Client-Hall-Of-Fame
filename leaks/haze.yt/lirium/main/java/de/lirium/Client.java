package de.lirium;

import best.azura.eventbus.core.EventBus;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.protocol.ProtocolManagerImpl;
import com.viaversion.viaversion.util.ChatColorUtil;
import de.lirium.base.auth.AuthManager;
import de.lirium.base.background.BackgroundManager;
import de.lirium.base.changelog.ChangelogManager;
import de.lirium.base.drag.DragHandler;
import de.lirium.base.profile.Profile;
import de.lirium.base.profile.ProfileManager;
import de.lirium.base.setting.SettingRegistry;
import de.lirium.impl.command.CommandManager;
import de.lirium.impl.module.ModuleManager;
import de.lirium.util.CustomFontLoader;
import de.lirium.util.FontLoader;
import de.lirium.util.feature.BlinkUtil;
import de.lirium.util.interfaces.Logger;
import de.lirium.util.weather.WeatherManager;
import god.buddy.aot.BCompiler;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import me.felix.clickgui.ClickGUI;
import me.felix.clickgui.renderables.RenderableCategory;
import me.felix.tabgui.TabGui;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;

import java.awt.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.virtual.JvmSpec;
import java.virtual.Modifier;
import java.virtual.State;

@Getter
@JvmSpec(state = State.HEAVY, modifiers = {Modifier.JVM_PRIVATE, Modifier.JVM_FINAL, Modifier.JDK_PATCH})
public enum Client {
    INSTANCE;

    private CommandManager commandManager;
    private ModuleManager moduleManager;
    private ProfileManager profileManager;
    private WeatherManager weatherManager;
    private ChangelogManager changelogManager;
    private AuthManager authManager;
    private BackgroundManager backgroundManager;
    private EventBus eventBus;

    public FontLoader fontLoader;

    public CustomFontLoader customFontLoader;

    public ClickGUI clickGUI;

    public static final String NAME = "Lirium";
    public static final String VERSION = "1.9";
    public static final String SNAPSHOT = "23w08a";

    public static final String PREFIX = "§aLirium §7>> §f";

    public static final String CODE_NAME = "Purple Sprite";

    public Color clientColor = new Color(157, 66, 142);

    public TabGui tabGui;

    public static final File DIR = new File(Minecraft.getMinecraft().mcDataDir, "Lirium");

    private Thread shutdownThread;

    public long initTime;

    private String splashText;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void hook() {

        if (!DIR.exists())
            if(DIR.mkdir())
                Logger.print("Created client directory");

        applySplashtext();

        /* Via Version */
        new Thread(() -> {
            try {
                ViaMCP.getInstance().start();
                ViaMCP.getInstance().initAsyncSlider();
                Logger.print("Via Version loaded");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
        this.eventBus = new EventBus();
        this.moduleManager = new ModuleManager();
        this.commandManager = new CommandManager();
        this.weatherManager = new WeatherManager();
        this.customFontLoader = new CustomFontLoader();
        this.fontLoader = new FontLoader();
        this.changelogManager = new ChangelogManager();
        this.changelogManager.init();

        this.authManager = new AuthManager();
        this.authManager.init(DIR);

        this.backgroundManager = new BackgroundManager();
        this.backgroundManager.init();

        this.tabGui = new TabGui();

        SettingRegistry.init();

        this.clickGUI = new ClickGUI();

        this.profileManager = new ProfileManager();

        if (profileManager.getFeatures().stream().noneMatch(profile -> profile.getName().equalsIgnoreCase("Default"))) {
            final Profile profile = new Profile("Default");
            profile.save();
            profileManager.getFeatures().add(profile);
        }

        if (profileManager.getFeatures().stream().noneMatch(profile -> profile.getName().equalsIgnoreCase("CubeCraft"))) {
            final Profile profile = new Profile("CubeCraft");
            profile.save();
            profileManager.getFeatures().add(profile);
        }

        final File profileFile = new File(DIR, "data/profile.txt");
        boolean canChange = false;
        if (profileFile.exists()) {
            try {
                final BufferedReader reader = new BufferedReader(new FileReader(profileFile));
                final StringJoiner joiner = new StringJoiner("");
                for (String line : reader.lines().collect(Collectors.toList()))
                    joiner.add(line);
                final Profile search = profileManager.getFeatures().stream().filter(profile -> profile.getName().equalsIgnoreCase(joiner.toString())).findAny().orElse(null);
                if (search != null) {
                    profileManager.setCurrent(search);
                    canChange = true;
                }
            } catch (FileNotFoundException ignored) {
            }
        }

        final File clickGUIFile = new File(DIR, "data/clickGUI.json");
        if(clickGUIFile.exists()) {
            try {
                final BufferedReader reader = new BufferedReader(new FileReader(clickGUIFile));
                final StringJoiner joiner = new StringJoiner("\n");
                for (String line : reader.lines().collect(Collectors.toList()))
                    joiner.add(line);
                reader.close();
                final JsonObject object = GSON.fromJson(joiner.toString(), JsonObject.class);
                for(RenderableCategory category : clickGUI.renderableCategories) {
                    final JsonObject element = object.get(category.category.name()).getAsJsonObject();
                    if(element != null) {
                        final JsonObject categoryObject = element.getAsJsonObject();
                        category.x = categoryObject.get("x").getAsInt();
                        category.y = categoryObject.get("y").getAsInt();
                        category.expanded = categoryObject.get("expanded").getAsBoolean();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!canChange)
            profileManager.setCurrent(profileManager.getFeatures().get(0));
        profileManager.read();

        new BlinkUtil();

        Display.setTitle(Client.NAME + " v" + Client.VERSION + (Client.SNAPSHOT != null ? " " + Client.SNAPSHOT : "") + ": " + splashText);

        if (shutdownThread == null) {
            Runtime.getRuntime().addShutdownHook(shutdownThread = new Thread(this::unhook));
        }

        DragHandler.loadDraggable();
    }

    public void unhook() {
        if (shutdownThread != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownThread);
            } catch (Exception ignored) {
            }
            shutdownThread = null;
        }
        profileManager.save();
        final File profileFile = new File(DIR, "data/profile.txt");
        final File backgroundFile = new File(DIR, "data/background.txt");
        try {
            if (!profileFile.exists()) {
                if (profileFile.createNewFile())
                    Logger.print("Created profile data file");
            }

            if (!backgroundFile.exists()) {
                if (backgroundFile.createNewFile())
                    Logger.print("Created background data file");
            }
        } catch (Exception e) {
            Logger.print("Files couldn't created");
        }

        DragHandler.save();

        try {
            final BufferedWriter profileWriter = new BufferedWriter(new FileWriter(profileFile));
            profileWriter.write(profileManager.get().getName());
            profileWriter.close();

            final BufferedWriter backgroundWriter = new BufferedWriter(new FileWriter(backgroundFile));
            backgroundWriter.write(backgroundManager.current.getName());
            backgroundWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File clickGUIFile = new File(DIR, "data/clickgui.json");
        try {
            if (!clickGUIFile.exists()) {
                if (clickGUIFile.createNewFile())
                    Logger.print("Created clickgui data file");
            }
            final JsonObject object = new JsonObject();
            for (final RenderableCategory panel : clickGUI.renderableCategories) {
                final JsonObject panelObject = new JsonObject();
                panelObject.addProperty("expanded", panel.expanded);
                panelObject.addProperty("x", panel.x);
                panelObject.addProperty("y", panel.y);
                object.add(panel.category.name(), panelObject);
            }
            final BufferedWriter writer = new BufferedWriter(new FileWriter(clickGUIFile));
            writer.write(GSON.toJson(object));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public boolean isDeveloper() {
        return System.getProperty("java.class.path").contains("idea_rt.jar");
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void applySplashtext() {
        if (splashText == null)
            try {
                final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                final ResourceLocation location = new ResourceLocation("texts/splashes.txt");
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) == 24) this.splashText = "Merry X-mas!";
                else if (calendar.get(Calendar.MONTH) + 1 == 1 && calendar.get(Calendar.DATE) == 1) this.splashText = "Happy new year!";
                else if (calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DATE) == 31) this.splashText = "OOoooOOOoooo! Spooky!";
                else {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream(), Charsets.UTF_8));
                    final List<String> list = reader.lines().collect(Collectors.toList());
                    reader.close();
                    if (!list.isEmpty())
                        splashText = ChatColorUtil.stripColor(list.get(Math.abs(secureRandom.nextInt(list.size()))));
                    else
                        splashText = "missingno";
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                splashText = "nukel an meinen eiern :)";
            }
    }

}
