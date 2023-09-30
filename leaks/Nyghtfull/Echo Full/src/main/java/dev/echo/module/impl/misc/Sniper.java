package dev.echo.module.impl.misc;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.WorldEvent;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.module.settings.impl.StringSetting;
import dev.echo.ui.notifications.NotificationManager;
import dev.echo.ui.notifications.NotificationType;
import dev.echo.utils.misc.Multithreading;
import dev.echo.utils.player.ChatUtil;
import dev.echo.utils.server.ServerUtils;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.concurrent.TimeUnit;

/**
 * @author cedo
 * @since 04/20/2022
 */
public class Sniper extends Module {

    public final StringSetting username = new StringSetting("Target");

    private final ModeSetting gameType = new ModeSetting("Game Type", "Skywars", "Skywars", "Bedwars");
    private final ModeSetting skywarsMode = new ModeSetting("Skywars Mode", "Solo", "Solo", "Doubles");
    private final ModeSetting skywarsType = new ModeSetting("Skywars Type", "Normal", "Normal", "Insane", "Ranked");
    private final ModeSetting bedwarsMode = new ModeSetting("Bedwars Mode", "Solo", "Solo", "Doubles", "Triples", "Quads");
    private final NumberSetting joinDelay = new NumberSetting("Join Delay", 3, 10, 2, .5);

    private final TimerUtil timer = new TimerUtil();
    boolean reset = false;

    public Sniper() {
        super("Sniper", Category.MISC, "Joins new games until you join a game with the specified username in it.");
        skywarsMode.addParent(gameType, modeSetting -> modeSetting.is("Skywars"));
        skywarsType.addParent(gameType, modeSetting -> modeSetting.is("Skywars"));
        bedwarsMode.addParent(gameType, modeSetting -> modeSetting.is("Bedwars"));
        addSettings(username, joinDelay, gameType, skywarsMode, skywarsType, bedwarsMode);
    }


    @Link
    public Listener<MotionEvent> motionEventListener = event -> {
        if (event.isPre()) {
            if(!ServerUtils.isGeniuneHypixel() || mc.isSingleplayer()) {
                NotificationManager.post(NotificationType.WARNING, "Error", "This module only works on Hypixel servers.");
                toggleSilent();
                return;
            }

            for (NetworkPlayerInfo netPlayer : mc.thePlayer.sendQueue.getPlayerInfoMap()) {
                if (netPlayer.getGameProfile().getName() == null) continue;

                String name = netPlayer.getGameProfile().getName();
                if (name.equalsIgnoreCase(username.getString())) {
                    NotificationManager.post(NotificationType.SUCCESS, "Success", "Found target!");
                    toggle();
                    return;
                }
            }

            if(reset) {
                Multithreading.schedule(() -> ChatUtil.send(getJoinCommand()), joinDelay.getValue().longValue(), TimeUnit.SECONDS);
                reset = false;
            }
        }
    };

    @Link
    public Listener<WorldEvent> onWorldEvent = event -> {
        if(event instanceof WorldEvent.Load) {
            reset = true;
        }
    };

    private String getJoinCommand() {
        switch (gameType.getMode()) {
            case "Skywars":
                switch (skywarsMode.getMode()) {
                    case "Solo":
                        return "/play solo_" + skywarsType.getMode().toLowerCase();

                    case "Doubles":
                        return "/play teams_" + skywarsType.getMode().toLowerCase();

                    case "Ranked":
                        return "/play ranked_normal";
                }
                break;

            case "Bedwars":
                switch (bedwarsMode.getMode()) {
                    case "Solo":
                        return "/play bedwars_eight_one";

                    case "Doubles":
                        return "/play bedwars_eight_two";

                    case "Triples":
                        return "/play bedwars_four_three";

                    case "Quads":
                        return "/play bedwars_four_four";
                }
                break;
        }
        return "/l";
    }


}
