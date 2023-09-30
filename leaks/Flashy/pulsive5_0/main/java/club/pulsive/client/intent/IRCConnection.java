package club.pulsive.client.intent;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.module.impl.misc.IRC;
import club.pulsive.impl.util.client.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ChatComponentText;
import store.intent.irc.client.IntentIRC;
import store.intent.irc.client.authorization.SessionProvider;

import java.util.HashMap;
import java.util.Map;

public class IRCConnection implements MinecraftUtil {
    public static IRCConnection instance = new IRCConnection();
    public static IntentIRC INSTANCE;
    public static Map<String, String> usersMap = new HashMap<>();


    public void startIRC() {
        IntentIRC irc = IntentIRC
                .draft(new SessionProvider(Pulsive.INSTANCE.getIntentAccount().api_key, "NiyZ4PWT"))
                .at("142.44.157.222", 8564)
                .reconnect()
                .addFailListener((e) -> {
                    System.out.println("Failed to connect to IRC");
                })
                .addLostListener((e) -> {
                    System.out.println("Lost connection to IRC");
                })
                .addAuthorizeListener((instance) -> {
                    System.out.println("Authorized to IRC");
                    INSTANCE = instance;

                    instance.setIGN("");
                })
                .addMessageListener((type, message) -> {
                    if (type.equals("chat"))
                        if (mc.thePlayer != null && mc.theWorld != null) {
                            // diablo chat message
                        }

                    if (type.equals("all_users_map"))
                        if (mc.thePlayer != null && mc.theWorld != null) {
                            if (IRC.getInstance() != null && IRC.getInstance().isToggled()) {
                                JsonElement element = new JsonParser().parse(message);
                                if (element.isJsonObject()) {
                                    JsonObject obj = element.getAsJsonObject();
                                    Map<String, String> usersMap = new HashMap<>();
                                    for (Map.Entry<String, JsonElement> user : obj.entrySet()) {
                                        String ign = user.getKey();
                                        String username = user.getValue().getAsString();

                                        usersMap.put(ign, username);
                                    }
                                    this.usersMap = usersMap;
                                }
                            }
                        }

                    if (type.equals("intent_chat"))
                        if (mc.thePlayer != null && mc.theWorld != null) {
                            // intent chat message
                            mc.thePlayer.addChatMessage(new ChatComponentText(message));
                        }

                    if (type.equals("intent_chat_ign_mapped"))
                        if (mc.thePlayer != null && mc.theWorld != null) {
                            JsonElement element = new JsonParser().parse(message);
                            if (element.isJsonObject()) {
                                JsonObject obj = element.getAsJsonObject();
                                if (obj.has("sender") && obj.has("msg"))
                                    onPlayerExclaim(obj.get("sender").getAsString(), obj.get("msg").getAsString(), true);
                            }
                        }

                    if (type.equals("chat_ign_mapped"))
                        if (mc.thePlayer != null && mc.theWorld != null) {
                            JsonElement element = new JsonParser().parse(message);
                            if (element.isJsonObject()) {
                                JsonObject obj = element.getAsJsonObject();
                                if (obj.has("sender") && obj.has("msg"))
                                    onPlayerExclaim(obj.get("sender").getAsString(), obj.get("msg").getAsString(), false);
                            }
                        }
                });
        new Thread(() -> {
            if (!irc.connect())
                return;
        }).start();
    }

    public void onPlayerExclaim(String ign, String message, boolean global) {
    }


    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        if(event.isUpdate()) return;
        onUsernameChange();
        onServerChange();
    };

    public void onServerChange() {
        if(INSTANCE == null)
            return;

        ServerData data = mc.getCurrentServerData();

        if(data != null && IRC.getInstance() != null && IRC.getInstance().shareServer.getValue())
            INSTANCE.setServer(data.serverIP);
        else
            INSTANCE.setServer("");
    }

    public void onUsernameChange() {
        if(INSTANCE == null)
            return;

        if(IRC.getInstance() != null && IRC.getInstance().shareUsername.getValue())
            INSTANCE.setIGN(mc.getSession().getUsername());
        else
            INSTANCE.setIGN("");
    }

    public static void onShutdown() {
        INSTANCE.disconnect();
        INSTANCE = null;
        usersMap = new HashMap<>();
    }
}
