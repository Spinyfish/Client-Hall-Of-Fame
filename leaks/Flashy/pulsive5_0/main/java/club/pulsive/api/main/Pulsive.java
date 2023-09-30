package club.pulsive.api.main;

import club.pulsive.altmanager.AltManager;
import club.pulsive.api.config.ConfigManager;
import club.pulsive.api.event.eventBus.core.EventBus;
import club.pulsive.api.event.eventBus.core.EventPriority;
import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.api.yoint.shader.ShaderManager;
import club.pulsive.client.ClientInfo;
import club.pulsive.client.intent.IRCConnection;
import club.pulsive.client.intent.yay.account.GetUserInfo;
import club.pulsive.client.intent.yay.account.IntentAccount;
import club.pulsive.client.shader.impls.NewBlur;
import club.pulsive.client.ui.clickgui.clickgui.MainCGUI;
import club.pulsive.impl.event.client.KeyPressEvent;
import club.pulsive.impl.managers.ModuleManager;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.impl.misc.IRC;
import club.pulsive.impl.networking.SocketClient;
import club.pulsive.impl.networking.user.User;
import club.pulsive.impl.util.Blurrer;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.network.BalanceUtil;
import club.pulsive.impl.util.player.PlayerUtil;
import club.pulsive.impl.util.render.DraggablesManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.main.Main;
import net.minecraft.client.network.OldServerPinger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import store.intent.intentguard.annotation.Bootstrap;
import store.intent.intentguard.annotation.Native;
import viamcp.ViaMCP;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter@Native
public enum Pulsive implements MinecraftUtil {
    INSTANCE;

    private final Blurrer blurrer = new Blurrer(false);
    public final ClientInfo clientInfo = new ClientInfo("Pulsive", "5.0", false);
    private final EventBus eventBus = new EventBus();
    public IntentAccount intentAccount;
    public final Path clientDir = Paths.get(mc.mcDataDir.getAbsolutePath(), getClientInfo().getClientName());
    public final Path clientDirConfigs = Paths.get(String.valueOf(clientDir), "configs");
    private final ModuleManager moduleManager = new ModuleManager();
    private AltManager altManager;
    private SocketClient socketClient;

    
    @Setter
    private String ip;
    private MainCGUI strifeClickGUI;
    private final ConfigManager configManager = new ConfigManager();
    private DraggablesManager draggablesManager;
    @Getter
    private ShaderManager shaderManager = new ShaderManager();

    @Setter@Getter
    private User pulsiveUser = new User("Jinthium", "0000", "Developer");
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    @Setter
    private List<User> onlineUsers;
    private OldServerPinger oldServerPinger;

    private final Runnable pingRunnable = () -> {
        try {
            if(mc.isSingleplayer()) return;
            if (mc.getCurrentServerData().pingToServer != -1) {
                oldServerPinger.ping(mc.getCurrentServerData());
                if (mc.theWorld != null) Logger.print("Pinging...");
            }
        } catch (Throwable ignored) {
        }
    };

    @Bootstrap
    @Native
    public void init(){

        if (Main.apiKey != null && !Main.apiKey.isEmpty())
            intentAccount = new GetUserInfo().getIntentAccount(Main.apiKey);

        if (intentAccount == null) intentAccount = new IntentAccount();

        if(!clientDir.toFile().exists()) {
            System.out.println(clientDir);
            clientDir.toFile().mkdir();
        }
        if(!clientDirConfigs.toFile().exists()) {
            System.out.println(clientDirConfigs);
            clientDirConfigs.toFile().mkdir();
        }
        onlineUsers = new ArrayList<>();
        ViaMCP.getInstance().start();
        ViaMCP.getInstance().initAsyncSlider();
        eventBus.register(this);
        Display.setTitle(clientInfo.getClientTitle());
        draggablesManager = new DraggablesManager();
        moduleManager.init();
        configManager.init();
        oldServerPinger = new OldServerPinger();
        Fonts.createFonts();
        altManager = new AltManager();
        strifeClickGUI = new MainCGUI();
        shaderManager.init();
        Pulsive.INSTANCE.getEventBus().register(BalanceUtil.INSTANCE, EventPriority.HIGHEST);
        Pulsive.INSTANCE.getEventBus().register(IRCConnection.instance, EventPriority.HIGHEST);
        IRCConnection.instance.startIRC();
        connectSocket(true);
        scheduledExecutorService.scheduleAtFixedRate(pingRunnable, 0, 100, TimeUnit.MILLISECONDS);
    }

    public void connectSocket(boolean blocking) {
//        try {
//            socketClient = new SocketClient(new URI("ws://127.0.0.1:29155/?uid=0000&hwid=" + LicenceUtil.licence()));
//            if(blocking) socketClient.connectBlocking();
//            else socketClient.connect();
//            socketClient.packetHandler().sendPacket(new CUserUpdatePacket(new SUserUpdatePacket.Value(SUserUpdatePacket.UpdateType.ACCOUNT_USERNAME, Minecraft.getMinecraft().session.getUsername())));
//            socketClient.packetHandler().sendPacket(new CServerCommandPacket(CServerCommandPacket.CommandOperation.LIST_USERS, "", "capes"));
//        } catch (URISyntaxException | InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void reload(){
        for(Module m : moduleManager.getModules()){
            if(m.isToggled()){
                m.toggle();
            }
            moduleManager.getModules().remove(m);
        }
        moduleManager.init();
    }


    @EventHandler
    private final Listener<KeyPressEvent> keyPressEventListener = event -> {
        if (event.getKey() == Keyboard.KEY_RSHIFT) {
            mc.displayGuiScreen(strifeClickGUI);
        }
        getModuleManager().getModules().forEach(module -> {
            if (module.getKeyBind() == event.getKey()) module.toggle();
        });
    };
}
