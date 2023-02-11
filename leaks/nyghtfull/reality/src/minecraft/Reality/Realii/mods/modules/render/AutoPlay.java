package Reality.Realii.mods.modules.render;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.misc.EventChat;
import Reality.Realii.event.value.Option;
import Reality.Realii.guis.notification.Notification;
import Reality.Realii.guis.notification.NotificationsManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class AutoPlay extends Module {
    public Option gg = new Option("GG", "GG", true);
    public Option autoplay = new Option("Autoplay", "Autoplay", true);

    public AutoPlay() {
        super("AutoPlay", ModuleType.Player);
        addValues(gg, autoplay);
    }

    @EventHandler
    public void onChat(EventChat c) {
        if (c.getMessage().contains("Winner - ") || c.getMessage().contains("ʤ����")) {
            NotificationsManager.addNotification(new Notification("The game will start in 3 seconds.", Notification.Type.Alert,3));
            mc.thePlayer.sendChatMessage("/play solo_normal");
        }
    }

}
