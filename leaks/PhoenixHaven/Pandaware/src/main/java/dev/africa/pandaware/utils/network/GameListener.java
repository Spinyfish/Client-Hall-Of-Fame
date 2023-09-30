package dev.africa.pandaware.utils.network;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.event.interfaces.EventListenable;
import dev.africa.pandaware.api.interfaces.Initializable;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.impl.event.game.TickEvent;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.utils.render.ColorUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.var;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GameListener implements MinecraftInstance, Initializable, EventListenable {
    private List<Score> scoreboard = new ArrayList<>();
    private LobbyType lobbyType;
    private LobbyStatus lobbyStatus;

    @Override
    public void init() {
        Client.getInstance().getEventDispatcher().subscribe(this);
    }

    @EventHandler
    EventCallback<TickEvent> onPacket = event -> {
        if (mc.theWorld != null && mc.thePlayer != null && !this.scoreboard.isEmpty()) {
            for (Score score : this.scoreboard) {
                this.lobbyType = this.runLobbyDetection(
                        score.getObjective().getDisplayName().toLowerCase(),
                        score.getPlayerName().toLowerCase()
                );

                if ((this.lobbyStatus = this.runStatusDetection(score.getObjective().getDisplayName().toLowerCase(),
                        score.getPlayerName().toLowerCase())) == LobbyStatus.WAITING) {
                    break;
                }
            }
        } else {
            this.lobbyType = LobbyType.INVALID;
        }
    };

    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == RenderEvent.Type.RENDER_2D && mc.theWorld != null && mc.thePlayer != null) {
            ScoreObjective scoreobjective = null;
            var playerTeam = mc.theWorld.getScoreboard().getPlayersTeam(mc.thePlayer.getName());

            if (playerTeam != null) {
                int i1 = playerTeam.getChatFormat().getColorIndex();

                if (i1 >= 0) {
                    scoreobjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(3 + i1);
                }
            }

            var objective = scoreobjective != null ? scoreobjective :
                    mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);

            if (objective == null) return;

            var scoreboard = objective.getScoreboard();
            var collection = scoreboard.getSortedScores(objective);
            var list = collection.stream().filter(score -> score.getPlayerName() != null &&
                    !score.getPlayerName().startsWith("#")
            ).collect(Collectors.toList());

            if (list.size() > 15) {
                collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
            } else {
                collection = list;
            }

            this.scoreboard = new ArrayList<>(collection);
        }
    };

    @SuppressWarnings("UnusedAssignment")
    LobbyType runLobbyDetection(String title, String line) {
        title = ColorUtils.stripColorCodes(title);
        line = ColorUtils.stripColorCodes(line);

        if (title.contains("skywars") || title.contains("sky wars")) {
            return LobbyType.SKY_WARS;
        } else if (title.contains("bed wars") || title.contains("bedwars")) {
            return LobbyType.BED_WARS;
        } else if (title.contains("hypixel")) {
            return LobbyType.HUB;
        }

        return LobbyType.INVALID;
    }

    @SuppressWarnings("UnusedAssignment")
    LobbyStatus runStatusDetection(String title, String line) {
        title = ColorUtils.stripColorCodes(title);
        line = ColorUtils.stripColorCodes(line);

        if (line.contains("starting in")) {
            return LobbyStatus.WAITING;
        }

        return LobbyStatus.STARTED;
    }

    @Getter
    @AllArgsConstructor
    public enum LobbyType {
        INVALID("Invalid"),
        SKY_WARS("SkyWars"),
        BED_WARS("BedWars"),
        HUB("Hub");

        private final String label;
    }

    @Getter
    @AllArgsConstructor
    public enum LobbyStatus {
        STARTED("Started"),
        WAITING("Waiting");

        private final String label;
    }
}