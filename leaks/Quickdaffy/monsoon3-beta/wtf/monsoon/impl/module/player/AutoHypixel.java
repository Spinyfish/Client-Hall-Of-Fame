/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.ui.notification.NotificationType;

public class AutoHypixel
extends Module {
    private JSONObject obj;
    private String apiKey;
    private final Setting<Boolean> autoPlay = new Setting<Boolean>("Auto Play", true).describedBy("Whether to enable auto play.");
    private final Setting<AutoPlayMode> autoPlayMode = new Setting<AutoPlayMode>("Auto Play Mode", AutoPlayMode.SOLO_INSANE).describedBy("Auto Play mode").childOf(this.autoPlay);
    private final Setting<Boolean> staffWarns = new Setting<Boolean>("Staff Analyzer", true).describedBy("Whether to enable staff analyzer.");
    private final String[] strings = new String[]{"1st Killer - ", "1st Place - ", "You died! Want to play again? Click here!", " - Damage Dealt - ", "1st - ", "Winning Team - ", "Winners: ", "Winner: ", "Winning Team: ", " win the game!", "1st Place: ", "Last team standing!", "Winner #1 (", "Top Survivors", "Winners - "};
    Timer timer = new Timer();
    @EventLink
    private final Listener<EventUpdate> eventUpdateListener = e -> {};
    @EventLink
    private final Listener<EventPacket> eventPacketListener = e -> {
        if (this.autoPlay.getValue().booleanValue()) {
            if (e.getPacket() instanceof S02PacketChat) {
                try {
                    S02PacketChat packet = (S02PacketChat)e.getPacket();
                    for (String string : this.strings) {
                        if (!packet.getChatComponent().getUnformattedText().contains(string)) continue;
                        Wrapper.getNotifManager().notify(NotificationType.INFO, "Auto Play", "Sending you to the next game...");
                        this.mc.thePlayer.sendChatMessage(this.getAutoPlay());
                    }
                    if (packet.getChatComponent().getUnformattedText().contains("A player has been removed from your lobby.")) {
                        this.mc.thePlayer.sendChatMessage("what is go on? noboline ban?");
                        Wrapper.getNotifManager().notify(NotificationType.WARNING, "Ban detected", "Sending you to the next game to avoid a staff ban...");
                        this.mc.thePlayer.sendChatMessage(this.getAutoPlay());
                    }
                }
                catch (Exception packet) {
                    // empty catch block
                }
            }
            if (e.getPacket() instanceof S45PacketTitle) {
                try {
                    S45PacketTitle packetTitle = (S45PacketTitle)e.getPacket();
                    if (packetTitle.getMessage().getUnformattedText().toLowerCase().contains("died")) {
                        Wrapper.getNotifManager().notify(NotificationType.INFO, "Auto Play", "Sending you to the next game...");
                        this.mc.thePlayer.sendChatMessage(this.getAutoPlay());
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    };

    public AutoHypixel() {
        super("Auto Hypixel", "Automatically Hypixel", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.mc.thePlayer.sendChatMessage("/api new");
    }

    private String getAutoPlay() {
        switch (this.autoPlayMode.getValue()) {
            case SOLO_INSANE: {
                return "/play solo_insane";
            }
            case SOLO_NORMAL: {
                return "/play solo_normal";
            }
            case DOUBLES_INSANE: {
                return "/play doubles_insane";
            }
            case DOUBLES_NORMAL: {
                return "/play doubles_normal";
            }
        }
        return "lol the monsoon dev is incompetent and couldnt make autoplay";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream();){
            JSONObject json;
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = AutoHypixel.readAll(rd);
            JSONObject jSONObject = json = new JSONObject(jsonText);
            return jSONObject;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        int cp;
        StringBuilder sb = new StringBuilder();
        while ((cp = rd.read()) != -1) {
            sb.append((char)cp);
        }
        return sb.toString();
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Setting<AutoPlayMode> getAutoPlayMode() {
        return this.autoPlayMode;
    }

    public Setting<Boolean> getStaffWarns() {
        return this.staffWarns;
    }

    private static enum AutoPlayMode {
        SOLO_INSANE,
        SOLO_NORMAL,
        DOUBLES_INSANE,
        DOUBLES_NORMAL;

    }
}

