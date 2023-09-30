package dev.tenacity.module.impl.misc;

import dev.tenacity.event.impl.game.world.WorldEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.module.settings.impl.StringSetting;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.misc.Multithreading;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.server.ServerUtils;
import dev.tenacity.utils.time.TimerUtil;
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


    @Override
    public void onMotionEvent(MotionEvent event) {
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
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        if(event instanceof WorldEvent.Load) {
            reset = true;
        }
    }

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
