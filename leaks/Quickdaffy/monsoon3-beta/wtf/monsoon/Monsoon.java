/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClients
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package wtf.monsoon;

import com.alan.clients.network.NetworkManager;
import com.alan.clients.protection.manager.TargetManager;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.config.ConfigSystem;
import wtf.monsoon.api.manager.CommandManager;
import wtf.monsoon.api.manager.FriendManager;
import wtf.monsoon.api.manager.ModuleManager;
import wtf.monsoon.api.manager.ProcessorManager;
import wtf.monsoon.api.manager.alt.AltManager;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.obj.MonsoonPlayerObject;
import wtf.monsoon.impl.event.EventKey;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.module.hud.HUD;
import wtf.monsoon.impl.module.visual.Accent;
import wtf.monsoon.impl.ui.character.CharacterManager;
import wtf.monsoon.impl.ui.panel.PanelGUI;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.misc.protection.BuildType;
import wtf.monsoon.misc.script.ScriptLoader;
import wtf.monsoon.misc.server.MonsoonServerConnection;
import wtf.monsoon.misc.vantage.VantageCommunity;

public class Monsoon {
    private final String version = "3.0-A6";
    private final BuildType buildType = BuildType.DEVELOPER;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ScriptLoader scriptLoader = new ScriptLoader();
    private AltManager altManager = new AltManager();
    private NetworkManager networkManager;
    private FriendManager friendManager = new FriendManager();
    private ConfigSystem configSystem;
    private WindowGUI windowGUI;
    private PanelGUI panelGUI;
    private MonsoonServerConnection server;
    private TargetManager targetManager;
    private VantageCommunity community;
    private MonsoonPlayerObject player;
    private CharacterManager characterManager;
    private ProcessorManager processorManager;
    public static boolean DEBUG_MODE = false;
    @EventLink
    public final Listener<EventKey> eventKeyListener = event -> {
        for (Module module : this.getModuleManager().getModules()) {
            if (module.getKey() == null || module.getKey().getValue().getButtonCode() != event.getKey()) continue;
            module.toggle();
        }
    };
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = event -> {
        if (!this.getModuleManager().getModule(HUD.class).isEnabled()) {
            this.getModuleManager().getModule(HUD.class).setEnabled(true);
        }
        if (!this.getModuleManager().getModule(Accent.class).isEnabled()) {
            this.getModuleManager().getModule(Accent.class).setEnabled(true);
        }
    };

    public void exit() {
        Wrapper.getLogger().info("Stopping Monsoon");
        this.getConfigSystem().save("current");
    }

    public void downloadFurryPorn() {
        new Thread(() -> {
            try {
                CloseableHttpClient client = HttpClients.createDefault();
                HttpGet request = new HttpGet("https://e621.net/posts.json?limit=500&tags=sylveon+solo+rating%3Aexplicit");
                request.setHeader("User-Agent", "Monsoon/1.0 (by monsoon_development)");
                HttpResponse response = client.execute((HttpUriRequest)request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                String jsonText = sb.toString();
                JSONObject json = new JSONObject(jsonText);
                JSONArray jsonArray = json.getJSONArray("posts");
                String furryPornUrl = jsonArray.getJSONObject((int)MathUtils.randomNumber(319.0, 0.0)).getJSONObject("sample").getString("url");
                InputStream in = new URL(furryPornUrl).openStream();
                Files.copy(in, new File(System.getProperty("user.home") + "/Desktop", "monsooon-client-" + furryPornUrl.substring(furryPornUrl.length() - 35) + furryPornUrl.substring(furryPornUrl.length() - 5)).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    public String getVersion() {
        return this.version;
    }

    public BuildType getBuildType() {
        return this.buildType;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public void setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public ScriptLoader getScriptLoader() {
        return this.scriptLoader;
    }

    public void setScriptLoader(ScriptLoader scriptLoader) {
        this.scriptLoader = scriptLoader;
    }

    public AltManager getAltManager() {
        return this.altManager;
    }

    public void setAltManager(AltManager altManager) {
        this.altManager = altManager;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public FriendManager getFriendManager() {
        return this.friendManager;
    }

    public void setFriendManager(FriendManager friendManager) {
        this.friendManager = friendManager;
    }

    public ConfigSystem getConfigSystem() {
        return this.configSystem;
    }

    public void setConfigSystem(ConfigSystem configSystem) {
        this.configSystem = configSystem;
    }

    public WindowGUI getWindowGUI() {
        return this.windowGUI;
    }

    public void setWindowGUI(WindowGUI windowGUI) {
        this.windowGUI = windowGUI;
    }

    public PanelGUI getPanelGUI() {
        return this.panelGUI;
    }

    public void setPanelGUI(PanelGUI panelGUI) {
        this.panelGUI = panelGUI;
    }

    public MonsoonServerConnection getServer() {
        return this.server;
    }

    public void setServer(MonsoonServerConnection server) {
        this.server = server;
    }

    public TargetManager getTargetManager() {
        return this.targetManager;
    }

    public void setTargetManager(TargetManager targetManager) {
        this.targetManager = targetManager;
    }

    public VantageCommunity getCommunity() {
        return this.community;
    }

    public void setCommunity(VantageCommunity community) {
        this.community = community;
    }

    public MonsoonPlayerObject getPlayer() {
        return this.player;
    }

    public void setPlayer(MonsoonPlayerObject player) {
        this.player = player;
    }

    public CharacterManager getCharacterManager() {
        return this.characterManager;
    }

    public void setCharacterManager(CharacterManager characterManager) {
        this.characterManager = characterManager;
    }

    public ProcessorManager getProcessorManager() {
        return this.processorManager;
    }

    public void setProcessorManager(ProcessorManager processorManager) {
        this.processorManager = processorManager;
    }
}

