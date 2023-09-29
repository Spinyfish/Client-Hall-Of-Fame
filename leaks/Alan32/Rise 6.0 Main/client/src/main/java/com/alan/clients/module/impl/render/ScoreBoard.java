package com.alan.clients.module.impl.render;

import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.TickEvent;
import com.alan.clients.newevent.impl.render.LimitedRender2DEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2i;
import com.alan.clients.value.impl.DragValue;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "module.render.scoreboard.name", description = "module.render.scoreboard.description", category = Category.RENDER, autoEnabled = true)
public final class ScoreBoard extends Module {
    private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));

    private Collection<Score> collection;
    private ScoreObjective scoreObjective;
    private int maxWidth, lastMaxWidth;

    @EventLink()
    public final Listener<LimitedRender2DEvent> onLimitedRender2D = event -> {

        if (this.scoreObjective == null) return;

        final Vector2i position = new Vector2i((int) this.position.position.x, (int) this.position.position.y);

        if (this.scoreObjective != null) {
            LIMITED_PRE_RENDER_RUNNABLES.add(() -> this.renderScoreboard(position.x, position.y, new Color(0, 0, 0, 120), true));
        }
    };

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {
        if (this.scoreObjective == null) return;

        final Vector2i position = new Vector2i((int) this.position.position.x, (int) this.position.position.y);

        NORMAL_BLUR_RUNNABLES.add(() -> this.renderScoreboard(position.x, position.y, Color.WHITE, false));

        NORMAL_POST_BLOOM_RUNNABLES.add(() -> this.renderScoreboard(position.x, position.y, getTheme().getDropShadow(), false));
    };

    /**
     * Updates the scoreboard each tick.
     */
    @EventLink()
    public final Listener<TickEvent> onTick = event -> {

        this.scoreObjective = this.getScoreObjective();
        if (this.scoreObjective == null) return;

        final Collection<Score> collection = this.scoreObjective.getScoreboard().getSortedScores(this.scoreObjective);
        final List<Score> list = collection.stream()
                .filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());

        if (list.size() > 15) {
            this.collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            this.collection = list;
        }

        this.maxWidth = mc.fontRendererObj.width(scoreObjective.getDisplayName());

        for (final Score score : collection) {
            final ScorePlayerTeam scoreplayerteam = this.scoreObjective.getScoreboard().getPlayersTeam(score.getPlayerName());
            final String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName());
            this.maxWidth = Math.max(this.maxWidth, mc.fontRendererObj.width(s));
        }

        this.maxWidth += 2;
    };

    private ScoreObjective getScoreObjective() {
        final Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective scoreobjective = null;
        final ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.thePlayer.getCommandSenderName());

        if (scoreplayerteam != null) {
            final int colorIndex = scoreplayerteam.getChatFormat().getColorIndex();

            if (colorIndex > -1) {
                scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + colorIndex);
            }
        }

        return scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
    }

    private void renderScoreboard(int x, int y, final Color backgroundColor, final boolean font) {
        final FontRenderer fontRenderer = mc.fontRendererObj;
        final int fontHeight = FontRenderer.FONT_HEIGHT;
        final int size = collection.size();
        final int scoreboardHeight = size * fontHeight;
        final int padding = 3;
        final int height = fontHeight * size + padding;
        final int width = maxWidth;

        this.position.setScale(new Vector2d(width + padding * 4, height + fontHeight + padding));
        RenderUtil.roundedRectangle(x, y, width + padding * 4, height + fontHeight + padding,
                10, backgroundColor);

        if (!font) {
            return;
        }

        final int fontColor = 553648127;
        x += padding * 2;
        y += padding + 1.5;

        // draws title of scoreboard
        final String objective = scoreObjective.getDisplayName();
        fontRenderer.drawString(objective, x + maxWidth / 2.0F - fontRenderer.width(objective) / 2.0F, y, fontColor);

        // draws strings
        for (final Score score1 : collection) {
            y += fontHeight;
            final ScorePlayerTeam scorePlayerTeam = this.scoreObjective.getScoreboard().getPlayersTeam(score1.getPlayerName());
            final String s1 = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score1.getPlayerName());
            fontRenderer.drawString(s1, x, y, fontColor);
        }
        // draws title of scoreboard
//        fontRenderer.drawString(objective, x - fontRenderer.getStringWidth(objective) / 2f + width / 2f, y, fontColor);
//        y += FontRenderer.FONT_HEIGHT;
//
//        for (final Score score : collection) {
//            final ScorePlayerTeam scorePlayerTeam = this.scoreObjective.getScoreboard().getPlayersTeam(score.getPlayerName());
//            final String text = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score.getPlayerName());
//
//            fontRenderer.drawString(text, x + 1, y, fontColor);
//            y += FontRenderer.FONT_HEIGHT;
//        }
    }

}