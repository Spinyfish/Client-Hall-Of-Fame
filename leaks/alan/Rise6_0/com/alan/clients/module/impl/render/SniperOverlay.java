package com.alan.clients.module.impl.render;

import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.hypixel.APIKeyComponent;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.render.LimitedRender2DEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.value.impl.DragValue;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.render.RenderUtil;
import util.time.StopWatch;
import com.alan.clients.util.vector.Vector2d;
import lombok.AllArgsConstructor;
import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Rise
@ModuleInfo(name = "Overlay", description = "module.render.sniperoverlay.description", category = Category.RENDER)
public final class SniperOverlay extends Module {

    private DragValue position = new DragValue("Position", this, new Vector2d(0, 0));
    private StopWatch refresh = new StopWatch();
    private ConcurrentLinkedQueue<SniperOverlayPlayer> sniperOverlayPlayers = new ConcurrentLinkedQueue<>();
    private double nameWidth, levelWidth, fKDRWidth;

    @Override
    protected void onEnable() {
        sniperOverlayPlayers.clear();
    }

    @EventLink()
    public final Listener<LimitedRender2DEvent> onLimitedRender2D = event -> {

        if (!APIKeyComponent.receivedKey) {
            return;
        }

        if (sniperOverlayPlayers.isEmpty()) {
            return;
        }

        double x = position.position.x;
        double y = position.position.y;
        double padding = 4;
        double fontHeight = FontRenderer.FONT_HEIGHT + padding;
        double width = padding + nameWidth + padding + levelWidth + padding + fKDRWidth;
        double height = (sniperOverlayPlayers.size() + 1) * fontHeight + padding;

        position.setScale(new Vector2d(width, height));

        NORMAL_RENDER_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(x, y, width, height, getTheme().getRound(), getTheme().getBackgroundShade());

            mc.fontRendererObj.drawString("Name",
                    x + padding, y + padding, getTheme().getFirstColor().getRGB());

            mc.fontRendererObj.drawString("Level",
                    x + padding + nameWidth + padding, y + padding, getTheme().getFirstColor().getRGB());

            mc.fontRendererObj.drawString("FKDR",
                    x + padding + nameWidth + padding + levelWidth + padding, y + padding, getTheme().getFirstColor().getRGB());


            double position = y + padding + fontHeight;
            for (SniperOverlayPlayer sniperOverlayPlayer : sniperOverlayPlayers) {

                mc.fontRendererObj.drawString(sniperOverlayPlayer.name,
                        x + padding, position, Color.WHITE.getRGB());

                mc.fontRendererObj.drawString(String.valueOf(sniperOverlayPlayer.level),
                        x + padding + nameWidth + padding, position, Color.WHITE.getRGB());

                mc.fontRendererObj.drawString(String.valueOf(sniperOverlayPlayer.fkdr),
                        x + padding + nameWidth + padding + levelWidth + padding, position, Color.WHITE.getRGB());

                position += fontHeight;
            }
        });
    };

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (!APIKeyComponent.receivedKey) {
            return;
        }

        if (mc.thePlayer.ticksExisted == 1) {
            sniperOverlayPlayers.clear();
        }

        if (refresh.finished(5000) || mc.thePlayer.ticksExisted == 1) {
            refresh.reset();

            List<NetworkPlayerInfo> list = GuiPlayerTabOverlay.playerList.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());

            for (NetworkPlayerInfo networkPlayerInfo : list) {
                new Thread(() -> {
                    UUID uuid = networkPlayerInfo.getGameProfile().getId();
                    String name = getPlayerName(networkPlayerInfo);

                    this.updateWidth();

                    if (this.existing(uuid)) {
                        return;
                    }

//                    sniperOverlayPlayers.removeIf(sniperOverlayPlayer -> this.duplicate(sniperOverlayPlayer.uuid));

                    String data = this.getData(uuid);

                    if (data == null) {
                        return;
                    }

                    JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();

                    JsonObject achievementsJson = jsonObject.get("player").getAsJsonObject();
                    achievementsJson = achievementsJson.get("stats").getAsJsonObject();
                    achievementsJson = achievementsJson.get("Bedwars").getAsJsonObject();

                    final int experience = achievementsJson.get("Experience").getAsInt();
                    final int finalKills = achievementsJson.get("final_kills_bedwars").getAsInt();
                    final int finalDeaths = achievementsJson.get("final_deaths_bedwars").getAsInt();

                    final int level = (int) Math.floor(getBedWarsLevel(experience));
                    final double finalKillDeathRatio = MathUtil.round((float) finalKills / (float) finalDeaths, 2);

                    sniperOverlayPlayers.add(new SniperOverlayPlayer(name, uuid, level, finalKillDeathRatio));

                    this.updateWidth();
                }).start();
            }
        }
    };

    public boolean existing(UUID uuid) {
        for (SniperOverlayPlayer sniperOverlayPlayer : sniperOverlayPlayers) {
            if (sniperOverlayPlayer.uuid.equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean duplicate(UUID uuid) {
        for (SniperOverlayPlayer sniperOverlayPlayer : sniperOverlayPlayers) {
            if (sniperOverlayPlayer.uuid == uuid) {
                return true;
            }
        }
        return false;
    }

    public void updateWidth() {
        double nameWidth = 35;
        for (SniperOverlayPlayer sniperOverlayPlayer : sniperOverlayPlayers) {
            double possibleNameWidth = mc.fontRendererObj.width(sniperOverlayPlayer.name);
            if (nameWidth < possibleNameWidth) {
                nameWidth = possibleNameWidth;
            }
        }

        this.nameWidth = nameWidth;

        double levelWidth = 30;
        for (SniperOverlayPlayer sniperOverlayPlayer : sniperOverlayPlayers) {
            double possibleLevelWidth = mc.fontRendererObj.width(String.valueOf(sniperOverlayPlayer.level));
            if (nameWidth < possibleLevelWidth) {
                nameWidth = possibleLevelWidth;
            }
        }

        this.levelWidth = levelWidth;

        double fKDRWidth = 30;
        for (SniperOverlayPlayer sniperOverlayPlayer : sniperOverlayPlayers) {
            double possibleFKDRWidth = mc.fontRendererObj.width(String.valueOf(sniperOverlayPlayer.fkdr));
            if (fKDRWidth < possibleFKDRWidth) {
                fKDRWidth = possibleFKDRWidth;
            }
        }

        this.fKDRWidth = fKDRWidth;
    }

    public String getData(UUID uuid) {
        try {
            URL url = new URL("https://api.hypixel.net/player?key=" + APIKeyComponent.apiKey +
                    "&uuid=" + uuid.toString());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            StringBuilder webpageContent;

            if (http.getResponseCode() == 200) {

                try (BufferedReader webpage = new BufferedReader(
                        new InputStreamReader(http.getInputStream()))) {

                    String webpage_line;
                    webpageContent = new StringBuilder();

                    while ((webpage_line = webpage.readLine()) != null) {

                        webpageContent.append(webpage_line);
                        webpageContent.append(System.lineSeparator());
                    }
                }

                http.disconnect();

                return webpageContent.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private static double getBedWarsLevel(double exp) {
        int level = 100 * ((int) (exp / 487000));
        exp = exp % 487000;
        if (exp < 500) return level + exp / 500;
        level++;
        if (exp < 1500) return level + (exp - 500) / 1000;
        level++;
        if (exp < 3500) return level + (exp - 1500) / 2000;
        level++;
        if (exp < 7000) return level + (exp - 3500) / 3500;
        level++;
        exp -= 7000;
        return level + exp / 5000;
    }

    /**
     * Returns the name that should be rendered for the player supplied
     */
    public String getPlayerName(final NetworkPlayerInfo networkPlayerInfoIn) {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {

        double x = position.position.x;
        double y = position.position.y;
        double padding = 4;
        double fontHeight = FontRenderer.FONT_HEIGHT + padding;
        double width = padding + nameWidth + padding + levelWidth + padding + fKDRWidth;
        double height = (sniperOverlayPlayers.size() + 1) * fontHeight + padding;

        NORMAL_BLUR_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(x, y, width, height, getTheme().getRound(), Color.BLACK);
        });

        NORMAL_POST_BLOOM_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(x, y, width, height, getTheme().getRound(), getTheme().getDropShadow());
        });
    };

    @AllArgsConstructor
    public static class SniperOverlayPlayer {
        public String name;
        public UUID uuid;
        public int level;
        public double fkdr;
    }
}