package dev.tenacity.module.impl.misc;

import dev.tenacity.event.impl.network.message.ChatReceivedEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.module.settings.impl.StringSetting;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.misc.Multithreading;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.util.StringUtils;

import java.util.concurrent.TimeUnit;

public class AutoHypixel extends Module {

    private final BooleanSetting autoGG = new BooleanSetting("AutoGG", true);
    private final StringSetting autoGGMessage = new StringSetting("AutoGG Message", "gg");
    private final BooleanSetting autoPlay = new BooleanSetting("AutoPlay", true);
    private final NumberSetting autoPlayDelay = new NumberSetting("AutoPlay Delay", 2.5, 8, 1, 0.5);
    private final BooleanSetting autoHubOnBan = new BooleanSetting("Auto /l on ban", false);

    public AutoHypixel() {
        super("AutoHypixel", Category.MISC, "stuff for hypixel");
        autoGGMessage.addParent(autoGG, ParentAttribute.BOOLEAN_CONDITION);
        autoPlayDelay.addParent(autoPlay, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(autoGG, autoGGMessage, autoPlay, autoPlayDelay, autoHubOnBan);
    }

    @Override
    public void onChatReceivedEvent(ChatReceivedEvent event) {
        String message = event.message.getUnformattedText(), strippedMessage = StringUtils.stripControlCodes(message);
        if (autoHubOnBan.isEnabled() && strippedMessage.equals("A player has been removed from your game.")) {
            ChatUtil.send("/lobby");
            NotificationManager.post(NotificationType.WARNING, "AutoHypixel", "A player in your lobby got banned.");
        }
        String m = event.message.toString();
        if (m.contains("ClickEvent{action=RUN_COMMAND, value='/play ")) {
            if (autoGG.isEnabled() && !strippedMessage.startsWith("You died!")) {
                ChatUtil.send("/ac " + autoGGMessage.getString());
            }
            if (autoPlay.isEnabled()) {
                sendToGame(m.split("action=RUN_COMMAND, value='")[1].split("'}")[0]);
            }
        }
    }

    private void sendToGame(String mode) {
        float delay = autoPlayDelay.getValue().floatValue();
        NotificationManager.post(NotificationType.INFO, "AutoPlay",
                "Sending you to a new game" + (delay > 0 ? " in " + delay + "s" : "") + "!", delay);
        Multithreading.schedule(() -> ChatUtil.send(mode), (long) delay, TimeUnit.SECONDS);
    }

}
