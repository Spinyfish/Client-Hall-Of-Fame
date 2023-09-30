package dev.africa.pandaware.impl.module.misc;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.java.ArrayUtils;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.network.GameUtils;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import org.apache.commons.lang3.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ModuleInfo(name = "Kill Sults", description = "stop looking through the src ty <3")
public class KillSultsModule extends Module {
    private final NumberSetting delay = new NumberSetting("Delay", 5000, 0, 0, 50);
    private final BooleanSetting randomization = new BooleanSetting("Randomization", false);
    private final NumberSetting maxRandomization = new NumberSetting("Max Randomization", 5000, 0, 0, 50, this.randomization::getValue);
    private final BooleanSetting shout = new BooleanSetting("Shout", false);
    private File insultsFile;
    private final TimeHelper timer = new TimeHelper();
    private String message;
    private String killMessage;
    private final List<String> killMessages = new ArrayList<>();

    public KillSultsModule() {
        this.registerSettings(
                this.delay,
                this.maxRandomization,
                this.randomization,
                this.shout
        );

        this.insultsFile = new File(new File(mc.mcDataDir, Client.getInstance().getManifest().getClientName()), "/killsults/insults.txt");
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S02PacketChat && mc.thePlayer != null) {
            S02PacketChat packet = event.getPacket();
            message = packet.getChatComponent().getUnformattedText();
            if (message.toLowerCase().contains(mc.thePlayer.getName().toLowerCase())) {
                for (String announcement : GameUtils.KILL_ANNOUNCEMENTS) {
                    if (message.toLowerCase().contains(announcement.toLowerCase()
                            .replace("%player%", mc.thePlayer.getName().toLowerCase())) ||
                            message.toLowerCase().contains("was slain by") && message.toLowerCase().endsWith(mc.thePlayer.getName()) ||
                        message.toLowerCase().contains("you killed %player%".replace("%player%", mc.thePlayer.getName()))) {
                        try (Stream<String> insultsStream = Files.lines(Paths.get(insultsFile.getPath()))) {
                            List<String> insults = ArrayUtils.getArrayListFromStream(insultsStream);
                            String finalMsg = (insults.get(RandomUtils.nextInt(0, insults.size())).replace("%PLAYER%", message.split(" ")[0]));
                            killMessages.add(finalMsg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (event.getPacket() instanceof S01PacketJoinGame) killMessages.clear();
    };

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        if (killMessages.isEmpty()) {
            timer.reset();
            return;
        } else {
            killMessage = killMessages.get(0);
        }
        if (timer.reach(delay.getValue().intValue() + (this.randomization.getValue() ? RandomUtils.nextInt(0, this.maxRandomization.getValue().intValue()) : 0)) && !killMessages.isEmpty()) {
            mc.thePlayer.sendChatMessage((shout.getValue() ? "/shout " : "") + killMessage);
            killMessages.remove(0);
            timer.reset();
        }
    };

    private void checkFiles() {
        File folder = new File(new File(mc.mcDataDir, Client.getInstance().getManifest().getClientName()), "killsults");
        if (!folder.exists()) {
            folder.mkdir();
        }

        if (!this.insultsFile.exists()) {
            try {
                this.insultsFile.createNewFile();
                Client.getInstance().getNotificationManager().addNotification(Notification.Type.INFO,
                        "Killsults file generated", 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEnable() {
        checkFiles();
        killMessage = "";
        message = "";
    }

    @Override
    public void onDisable() {
        killMessages.clear();
    }
}
