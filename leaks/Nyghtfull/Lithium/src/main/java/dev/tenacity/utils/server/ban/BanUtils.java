package dev.tenacity.utils.server.ban;

import dev.tenacity.ui.altmanager.helpers.Alt;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.List;

public class BanUtils {

    public static void processDisconnect(Alt alt, IChatComponent message) {
        List<IChatComponent> siblings = message.getSiblings();
        if (siblings.size() > 1 && siblings.get(0) instanceof ChatComponentText) {
            String firstLine = siblings.get(0).getUnformattedText().trim();
            String msg = message.toString().toLowerCase();
            HypixelBan.Reason reason = null;
            String duration = null;
            if (firstLine.equals("You are permanently banned from this server!")) {
                if (msg.contains("suspicious activity")) {
                    reason = HypixelBan.Reason.SECURITY_ALERT;
                } else if (msg.contains("cheat")) {
                    reason = HypixelBan.Reason.CHEATING;
                } else {
                    reason = HypixelBan.Reason.MISC;
                }
            } else if (firstLine.equals("You are temporarily banned for")) {
                duration = siblings.get(1).getUnformattedText();
                if (msg.contains("security appeal was processed")) {
                    reason = HypixelBan.Reason.SECURITY_ALERT_PROCCESSED;
                } else if (msg.contains("cheat")) {
                    reason = HypixelBan.Reason.CHEATING;
                } else {
                    reason = HypixelBan.Reason.MISC;
                }
            }
            alt.hypixelBan = new HypixelBan(reason, duration);
            NotificationManager.post(NotificationType.INFO, "Ban Tracker", "Alt marked as banned for " + (duration == null ? "PERMANENT" : duration), 5);
        }
    }

}
