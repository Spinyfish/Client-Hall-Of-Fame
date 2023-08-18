package me.jinthium.straight.impl;

import best.azura.irc.utils.Wrapper;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.florianmichael.viamcp.ViaMCP;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import io.mxngo.echo.EventDispatcher;
import io.mxngo.echo.core.EventListener;
import me.jinthium.scripting.api.ScriptManager;
import me.jinthium.straight.api.SpotifyAPI;
import me.jinthium.straight.api.clickgui.CInterface;
import me.jinthium.straight.api.client.ClientInfo;
import me.jinthium.straight.api.client.User;
import me.jinthium.straight.api.client.keyauth.api.KeyAuth;
import me.jinthium.straight.api.command.CommandHandler;
import me.jinthium.straight.api.config.ConfigManager;
import me.jinthium.straight.api.dragging.Dragging;
import me.jinthium.straight.api.irc.IRCClient;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.notification.NotificationType;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.impl.commands.ConfigCommand;
import me.jinthium.straight.impl.commands.FriendCommand;
import me.jinthium.straight.impl.event.keyboard.KeyPressEvent;
import me.jinthium.straight.impl.event.network.ServerChatEvent;
import me.jinthium.straight.impl.manager.*;
import me.jinthium.straight.impl.ui.AltManager;
import me.jinthium.straight.impl.utils.Multithreading;
import me.jinthium.straight.impl.utils.font.FontUtil;
import me.jinthium.straight.impl.utils.render.TaskUtil;
import net.minecraft.util.IChatComponent;
import obfuscation.NativeLib;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjglx.opengl.Display;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Array;
import java.net.http.HttpConnectTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@NativeLib
public enum Client implements MinecraftInstance, EventListener {
    INSTANCE;


    private User user;
    private final EventDispatcher pubSubEventBus = new EventDispatcher();

    private final ClientInfo clientInfo = new ClientInfo("Straightware", "Developer");
    private final ModuleManager moduleManager = new ModuleManager();
    private final CInterface cInterface = new CInterface();
    private final ComponentManager componentManager = new ComponentManager();
    private final File clientDir = new File(mc.mcDataDir, clientInfo.getName());
    private final NotificationManager notificationManager = new NotificationManager();
    private final CommandHandler commandHandler = new CommandHandler();
    private final ConfigManager configManager = new ConfigManager();
    private ScriptManager scriptManager;
    private AltManager altManager;

    private final String url = "https://keyauth.win/api/1.2/";

    private final String ownerid = "imMPtOPNzp"; // You can find out the owner id in the profile settings keyauth.com
    private final String appname = "Straightware"; // Application name
    private final String version = "1.0"; // Application version
    private final KeyAuth keyAuth = new KeyAuth(appname, ownerid, version, url);

    private final HashMap<String, String> playerList = new HashMap<>();


    private final Runnable funnyFuck = () -> {
        HttpResponse<JsonNode> response;
        try{
            response = Unirest.get("https://jinthium.com/updateplayers/").asJson();
            JSONArray jsonArray = response.getBody().getArray();
            for (Object jsonObject : jsonArray) {
                String[] parsedObject = jsonObject.toString()
                        .replaceAll("[\"\\[\\]]", "")
                        .split(",");

                String key = parsedObject[0];
                String value = parsedObject[1];

                if (!playerList.containsKey(key)) {
//                    System.out.println(value + ":" + key);
                    playerList.put(value, key);
                } else {
                    HttpResponse<String> removeResponse;
                    try{
                        removeResponse = Unirest.get("https://jinthium.com/removeplayer/" + key + "/" + value).asString();
                    }catch (UnirestException ex){
                        ex.printStackTrace();
                    }
                    // Handle duplicate entry
                    // You can choose to skip, update, or perform any other desired action
                   // System.out.println("Duplicate key: " + key);
                }
            }

        }catch (UnirestException ex){
            ex.printStackTrace();
        }
    };



    public final void start() {
        if (!clientDir.exists())
            clientDir.mkdirs();

        Wrapper.getIRCConnector();


        try {
            ViaMCP.create();

            // Only use one of the following
            ViaMCP.INSTANCE.initAsyncSlider(); // For top left aligned slider
        } catch (Exception e) {
            e.printStackTrace();
        }

        keyAuth.init();
        pubSubEventBus.subscribe(this);
        pubSubEventBus.subscribe(TaskUtil.INSTANCE);
        pubSubEventBus.subscribe(cInterface);
        Display.setTitle(clientInfo.getClientTitle());
        cInterface.init(Display.Window.handle);
        FontUtil.setupFonts();
        altManager = new AltManager();
        altManager.loadAlts();
        componentManager.init();
        scriptManager = new ScriptManager();
        moduleManager.registerModules();
        commandHandler.commands.addAll(Arrays.asList(
                new FriendCommand(),
                new ConfigCommand()
        ));
        configManager.defaultConfig = new File(mc.mcDataDir + "/Straightware/Config.json");
        configManager.collectConfigs();
        DragManager.loadDragData();
        if (configManager.defaultConfig.exists()) {
            configManager.loadConfig(configManager.readConfigData(configManager.defaultConfig.toPath()), true);
        }




//        Multithreading.RUNNABLE_POOL.scheduleAtFixedRate(funnyFuck, 0,1, TimeUnit.MINUTES);
//        playerUpdateThread.start();
        /*
        Multithreading.schedule(() -> {

        }, 0, 5000, TimeUnit.MILLISECONDS);

         */
    }

    @Callback
    final EventCallback<KeyPressEvent> keyPressEventEventCallback = event -> {
        if (event.isInsideGui())
            return;

        moduleManager.getModules().forEach(module -> {
            if (module.getKey() == event.getKey())
                module.toggle();
        });
    };

    @Callback
    final EventCallback<ServerChatEvent> serverChatEventEventCallback = event -> {
        if (event.getChatComponent().getFormattedText().contains("play again?")) {
            for (IChatComponent iChatComponent : event.getChatComponent().getSiblings()) {
                for (String value : iChatComponent.toString().split("'")) {
                    if (value.startsWith("/play") && !value.contains(".")) {
                        mc.thePlayer.sendChatMessage(value);
                        getNotificationManager().post("Auto Play", "Joined a new game", NotificationType.INFO, 3);
                        break;
                    }
                }
            }
        }
    };

    public Color getClientColor() {
        return new Color(4, 133, 138);
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public Dragging createDrag(Module module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public EventDispatcher getPubSubEventBus() {
        return pubSubEventBus;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CInterface getCInterface() {
        return cInterface;
    }


    public File getClientDir() {
        return clientDir;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    public void setScriptManager(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    public AltManager getAltManager() {
        return altManager;
    }

    public void setAltManager(AltManager altManager) {
        this.altManager = altManager;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public KeyAuth getKeyAuth() {
        return keyAuth;
    }

    public HashMap<String, String> getPlayerList() {
        return playerList;
    }

    public Runnable getFunnyFuck() {
        return funnyFuck;
    }
}
