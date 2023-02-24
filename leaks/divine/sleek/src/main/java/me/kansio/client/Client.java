package me.kansio.client;

import com.google.common.eventbus.EventBus;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.common.eventbus.Subscribe;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import lombok.Getter;
import lombok.Setter;
import me.kansio.client.commands.CommandManager;
import me.kansio.client.config.ConfigManager;
import me.kansio.client.event.impl.KeyboardEvent;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.ServerJoinEvent;
import me.kansio.client.friend.FriendManager;
import me.kansio.client.gui.config.ConfigurationGUI;
import me.kansio.client.keybind.KeybindManager;
import me.kansio.client.manager.ValueManager;
import me.kansio.client.modules.ModuleManager;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.modules.impl.player.hackerdetect.CheckManager;
import me.kansio.client.modules.impl.visuals.ClickGUI;
import me.kansio.client.rank.UserRank;
import me.kansio.client.targets.TargetManager;
import me.kansio.client.utils.network.HttpUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String uid;
    @Getter
    @Setter
    private String discordTag;

    @Getter
    private UserRank rank;

    @Getter
    private Map<String, String> users = new HashMap<>();

    @Getter
    private File dir;

    @Getter
    private static Client instance = new Client();

    @Getter
    private EventBus eventBus = new EventBus("Sleek");

    @Getter
    private ModuleManager moduleManager;

    @Getter
    private CommandManager commandManager;

    @Getter
    private ConfigManager configManager;

    @Getter
    private ValueManager valueManager;

    @Getter
    private KeybindManager keybindManager;

    @Getter
    private FriendManager friendManager;

    @Getter
    private CheckManager checkManager;

    @Getter
    private TargetManager targetManager;

    public void onStart() {
        //Set the client file directory
        dir = new File(Minecraft.getMinecraft().mcDataDir, "Sleek");

        //Subscribe to the event bus
        eventBus.register(this);

        //Set the value manager
        valueManager = new ValueManager();

        //Set the module manager variable
        moduleManager = new ModuleManager();

        //Set the command manager
        commandManager = new CommandManager();

        //Set the config manager
        configManager = new ConfigManager(new File(dir, "configs"));

        //Set the keybind manager
        keybindManager = new KeybindManager(dir);
        //load the keybinds
        keybindManager.load();

        //Set the friend manager
        friendManager = new FriendManager();

        //set the target manager
        targetManager = new TargetManager();

        //Set the check manager
        checkManager = new CheckManager();

        //Setup ViaMCP
        try {
            ViaMCP.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            IPCClient client = new IPCClient(937350566886137886L);
            client.setListener(new IPCListener() {
                @Override
                public void onReady(IPCClient client) {
                    RichPresence.Builder builder = new RichPresence.Builder();
                    builder.setState("UID: " + uid)
                            .setDetails("Destroying servers")
                            .setStartTimestamp(OffsetDateTime.now())
                            .setLargeImage("canary-large", "Discord Canary")
                            .setSmallImage("ptb-small", "Discord PTB");
                    client.sendRichPresence(builder.build());
                }
            });
            client.connect();
        } catch (Exception e) {
            System.out.println("Discord not found, not setting rpc.");
        }

        System.out.println("Client has been started.");

        //set the window title
        Display.setTitle("Sleek v0.1");
    }

    public void onShutdown() {
        //leave
        try {
            System.out.println(HttpUtil.delete(MessageFormat.format("http://zerotwoclient.xyz:13337/api/v1/leaveserver?clientname={0}", username)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //save keybinds
        if (keybindManager != null) {
            keybindManager.save();
        }
    }

    @Subscribe
    public void onChat(PacketEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = event.getPacket();
            for (Map.Entry<String, String> user : users.entrySet()) {
                if (packet.getChatComponent().getUnformattedText().contains(user.getKey())) {
                    packet.chatComponent = new ChatComponentText(packet.getChatComponent().getFormattedText().replaceAll(user.getKey(), MessageFormat.format("\247b{0} \2477({1})", user.getValue(), user.getKey())));
                }
            }
        }
    }

    public void setRank(String rank) {
        switch (rank) {
            case "Developer": {
                this.rank = UserRank.DEVELOPER;
                break;
            }
            case "Beta": {
                this.rank = UserRank.BETA;
                break;
            }
            default: {
                this.rank = UserRank.USER;
                break;
            }
        }
    }

    @Subscribe
    public void onKeyboard(KeyboardEvent event) {
        int key = event.getKeyCode();

        if (key == Keyboard.KEY_RSHIFT) {
            ClickGUI clickGUI = (ClickGUI) Client.getInstance().getModuleManager().getModuleByName("Click GUI");
            clickGUI.toggle();
        }

        if (key == Keyboard.KEY_INSERT) {
            Minecraft.getMinecraft().displayGuiScreen(new ConfigurationGUI());
        }

        //This handles keybinds.
        for (Module module : moduleManager.getModules()) {
            //check if the keybind is -1, if it is, just continue.
            if (module.getKeyBind() == -1)
                continue;

            //if the bind == the key, toggle the module
            if (module.getKeyBind() == key) {
                module.toggle();
            }
        }
    }

}
