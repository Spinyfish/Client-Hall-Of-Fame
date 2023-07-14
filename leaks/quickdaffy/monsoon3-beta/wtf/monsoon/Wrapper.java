/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.Display
 */
package wtf.monsoon;

import com.alan.clients.network.NetworkManager;
import com.alan.clients.protection.manager.TargetManager;
import dev.quickprotect.NativeObf;
import io.github.nevalackin.homoBus.bus.impl.EventBus;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;
import wtf.monsoon.Monsoon;
import wtf.monsoon.api.event.Event;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.opengui.IRendererImpl;
import wtf.monsoon.api.sextoy.SexToyManager;
import wtf.monsoon.api.util.Logger;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.font.impl.FontRenderer;
import wtf.monsoon.impl.ui.Pallet;
import wtf.monsoon.impl.ui.notification.NotificationManager;
import wtf.monsoon.misc.protection.MonsoonAccount;
import wtf.opengui.GuiGL;

public class Wrapper {
    public static boolean loggedIn = false;
    private static final EventBus<Event> eventBus = new EventBus();
    private static Minecraft minecraft;
    public static MonsoonAccount monsoonAccount;
    private static Monsoon monsoon;
    private static final Logger logger;
    private static final FontUtil fontUtil;
    private static FontRenderer font;
    private static final NotificationManager notifManager;
    private static Pallet pallet;
    private static SexToyManager sexToyManager;
    private static long initTime;
    private static long sessionTime;
    private static String serverIP;
    private static boolean debugModeEnabled;
    static GuiGL glgui;

    @NativeObf
    public static void init(boolean enableDebugMode) {
        Wrapper.setDebugModeEnabled(enableDebugMode);
        glgui = new GuiGL();
        glgui.setRenderer(new IRendererImpl());
        monsoonAccount = new MonsoonAccount();
        monsoonAccount.setHwid("sex");
        monsoonAccount.setUsername("test_user");
        monsoonAccount.setUid("1337");
        monsoon = new Monsoon();
        Wrapper.getEventBus().subscribe(monsoon);
        Display.setTitle((String)("Monsoon " + Wrapper.getMonsoon().getVersion()));
        long startedAt = System.currentTimeMillis();
        Wrapper.getLogger().info("Starting Monsoon " + Wrapper.getMonsoon().getVersion());
        pallet = new Pallet(Color.decode("#8281E9"), Color.decode("#40407B"), Color.decode("#222242"), Color.decode("#0e0e1c"), Color.decode("#52529D"));
        minecraft = Minecraft.getMinecraft();
        fontUtil.bootstrap();
        Wrapper.getMonsoon().setTargetManager(new TargetManager());
        Wrapper.getEventBus().subscribe(Wrapper.getMonsoon().getTargetManager());
        Wrapper.getMonsoon().getTargetManager().init();
        ViaMCP.getInstance().start();
        ViaMCP.getInstance().initAsyncSlider();
        Wrapper.getMinecraft().gameSettings.guiScale = 2;
        Wrapper.getMinecraft().gameSettings.ofFastRender = false;
        Wrapper.getMinecraft().gameSettings.ofSmartAnimations = true;
        Wrapper.getMinecraft().gameSettings.ofSmoothFps = false;
        Wrapper.getMinecraft().gameSettings.ofFastMath = true;
        Wrapper.getMonsoon().setNetworkManager(new NetworkManager());
        Wrapper.getEventBus().subscribe(Wrapper.getMonsoon().getNetworkManager());
        Wrapper.getLogger().info("Finished starting Monsoon in " + (System.currentTimeMillis() - startedAt) + "ms.");
        initTime = System.currentTimeMillis();
    }

    public static void shutdown() {
        Wrapper.getMonsoon().getConfigSystem().save("current");
        Wrapper.getMonsoon().getConfigSystem().saveAlts(Wrapper.getMonsoon().getAltManager());
        Wrapper.getMonsoon().exit();
    }

    public static <T extends Module> T getModule(Class<T> clas) {
        if (Wrapper.getMonsoon() == null || Wrapper.getMonsoon().getModuleManager() == null) {
            return null;
        }
        return (T)((Module)Wrapper.getMonsoon().getModuleManager().getModules().stream().filter(module -> module.getClass() == clas).findFirst().orElse(null));
    }

    public static EventBus<Event> getEventBus() {
        return eventBus;
    }

    public static Minecraft getMinecraft() {
        return minecraft;
    }

    public static MonsoonAccount getMonsoonAccount() {
        return monsoonAccount;
    }

    public static void setMonsoonAccount(MonsoonAccount monsoonAccount) {
        Wrapper.monsoonAccount = monsoonAccount;
    }

    public static Monsoon getMonsoon() {
        return monsoon;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static FontUtil getFontUtil() {
        return fontUtil;
    }

    public static FontRenderer getFont() {
        return font;
    }

    public static void setFont(FontRenderer font) {
        Wrapper.font = font;
    }

    public static NotificationManager getNotifManager() {
        return notifManager;
    }

    public static Pallet getPallet() {
        return pallet;
    }

    public static SexToyManager getSexToyManager() {
        return sexToyManager;
    }

    public static void setSexToyManager(SexToyManager sexToyManager) {
        Wrapper.sexToyManager = sexToyManager;
    }

    public static long getInitTime() {
        return initTime;
    }

    public static void setInitTime(long initTime) {
        Wrapper.initTime = initTime;
    }

    public static long getSessionTime() {
        return sessionTime;
    }

    public static void setSessionTime(long sessionTime) {
        Wrapper.sessionTime = sessionTime;
    }

    public static String getServerIP() {
        return serverIP;
    }

    public static void setServerIP(String serverIP) {
        Wrapper.serverIP = serverIP;
    }

    public static boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }

    public static void setDebugModeEnabled(boolean debugModeEnabled) {
        Wrapper.debugModeEnabled = debugModeEnabled;
    }

    public static GuiGL getGlgui() {
        return glgui;
    }

    static {
        logger = new Logger();
        fontUtil = new FontUtil();
        notifManager = new NotificationManager();
    }
}

