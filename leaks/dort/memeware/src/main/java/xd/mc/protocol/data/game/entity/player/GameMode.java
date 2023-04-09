package niggerlib.mc.protocol.data.game.entity.player;

import niggerlib.mc.protocol.data.game.world.notify.ClientNotificationValue;

public enum GameMode implements ClientNotificationValue {
    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SPECTATOR;
}
