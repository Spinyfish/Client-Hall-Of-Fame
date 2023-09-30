package dev.echo.utils.player;

import dev.echo.utils.Utils;

public class AlwaysUtil implements Utils {

    public static boolean isPlayerInGame() {
        return (mc.thePlayer != null) && (mc.theWorld != null);
    }
}
