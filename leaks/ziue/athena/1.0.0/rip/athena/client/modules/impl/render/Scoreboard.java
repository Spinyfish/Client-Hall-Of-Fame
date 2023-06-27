package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import java.awt.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.modules.*;
import rip.athena.client.events.types.render.*;
import rip.athena.client.events.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import net.minecraft.scoreboard.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;
import net.minecraft.util.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;
import java.util.*;

public class Scoreboard extends Module
{
    @ConfigValue.List(name = "Display Mode", values = { "Modern", "Fade", "Custom", "Default" }, description = "Chose display of background")
    private String backgroundMode;
    @ConfigValue.Color(name = "Background Color")
    private Color backgroundColor;
    @ConfigValue.Boolean(name = "Background")
    private boolean background;
    @ConfigValue.Boolean(name = "Show Numbers")
    private boolean showNumbers;
    @ConfigValue.Boolean(name = "Free Move")
    private boolean freeMove;
    private HUDElement hud;
    private int width;
    private int height;
    
    public Scoreboard() {
        super("Scoreboard", Category.RENDER, "Athena/gui/mods/scoreboard.png");
        this.backgroundMode = "Default";
        this.backgroundColor = new Color(0, 0, 0, 150);
        this.background = true;
        this.showNumbers = true;
        this.freeMove = false;
        this.width = -1;
        this.height = -1;
        this.addHUD(this.hud = new HUDElement("scoreboard", this.width, this.height) {
            @Override
            public void onRender() {
                Scoreboard.this.render();
            }
        });
    }
    
    @SubscribeEvent
    public void onRender(final RenderEvent event) {
        if (event.getRenderType() != RenderType.SCOREBOARD) {
            return;
        }
        event.setCancelled(true);
    }
    
    public void render() {
        final ScaledResolution scaledresolution = new ScaledResolution(Scoreboard.mc);
        final net.minecraft.scoreboard.Scoreboard scoreboard = Scoreboard.mc.theWorld.getScoreboard();
        ScoreObjective scoreobjective = null;
        final ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(Scoreboard.mc.thePlayer.getName());
        if (scoreplayerteam != null) {
            final int i1 = scoreplayerteam.getChatFormat().getColorIndex();
            if (i1 >= 0) {
                scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i1);
            }
        }
        final ScoreObjective scoreobjective2 = (scoreobjective != null) ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
        if (scoreobjective2 != null) {
            this.renderScoreboard(scoreobjective2, scaledresolution);
        }
    }
    
    private void renderScoreboard(final ScoreObjective p_180475_1_, final ScaledResolution p_180475_2_) {
        final net.minecraft.scoreboard.Scoreboard scoreboard = p_180475_1_.getScoreboard();
        Collection<Score> collection = scoreboard.getSortedScores(p_180475_1_);
        final List<Score> list = (List<Score>)Lists.newArrayList(Iterables.filter((Iterable)collection, (Predicate)new Predicate<Score>() {
            public boolean apply(final Score p_apply_1_) {
                return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
        }));
        if (list.size() > 15) {
            collection = (Collection<Score>)Lists.newArrayList(Iterables.skip((Iterable)list, collection.size() - 15));
        }
        else {
            collection = list;
        }
        int i = Scoreboard.mc.fontRendererObj.getStringWidth(p_180475_1_.getDisplayName());
        for (final Score score : collection) {
            final ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName());
            if (this.showNumbers) {
                s = s + ": " + score.getScorePoints();
            }
            else {
                s += ":";
            }
            i = Math.max(i, Scoreboard.mc.fontRendererObj.getStringWidth(s));
        }
        final int i2 = collection.size() * Scoreboard.mc.fontRendererObj.FONT_HEIGHT;
        final int j1 = p_180475_2_.getScaledHeight() / 2 + i2 / 3;
        final int k1 = 3;
        int l1 = j1;
        int m = 0;
        int yPos = 0;
        if (this.freeMove) {
            l1 = this.hud.getX() + 2;
            yPos += this.hud.getY();
        }
        else {
            l1 = p_180475_2_.getScaledWidth() - i;
            yPos = p_180475_2_.getScaledHeight() / 2 - i2 / 2;
            this.hud.setX(l1 - 2);
            this.hud.setY(yPos);
        }
        if (this.background) {
            if (this.backgroundMode.equalsIgnoreCase("Modern")) {
                if (Athena.INSTANCE.getThemeManager().getTheme().isTriColor()) {
                    RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getThirdColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor());
                }
                else {
                    RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor());
                }
            }
            else if (this.backgroundMode.equalsIgnoreCase("Circle")) {
                RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
            }
            else if (this.backgroundMode.equalsIgnoreCase("Fade")) {
                RoundedUtils.drawRoundedRect((float)this.hud.getX(), (float)this.hud.getY(), (float)(this.hud.getX() + this.width), (float)(this.hud.getY() + this.height), 8.0f, Athena.INSTANCE.getThemeManager().getTheme().getAccentColor().getRGB());
            }
        }
        for (final Score score2 : collection) {
            ++m;
            final ScorePlayerTeam scoreplayerteam2 = scoreboard.getPlayersTeam(score2.getPlayerName());
            final String s2 = ScorePlayerTeam.formatPlayerName(scoreplayerteam2, score2.getPlayerName());
            final String s3 = EnumChatFormatting.RED + "" + score2.getScorePoints();
            final int k2 = yPos + (i2 + 1 - (m - 1) * Scoreboard.mc.fontRendererObj.FONT_HEIGHT);
            final int l2 = this.hud.getX() + this.hud.getWidth();
            if (this.background && this.backgroundMode.equalsIgnoreCase("Default")) {
                Gui.drawRectangle(l1 - 2, k2, l2, k2 + Scoreboard.mc.fontRendererObj.FONT_HEIGHT, 1342177280);
            }
            Scoreboard.mc.fontRendererObj.drawString(s2, l1, k2, 553648127);
            if (this.showNumbers) {
                Scoreboard.mc.fontRendererObj.drawString(s3, l2 - Scoreboard.mc.fontRendererObj.getStringWidth(s3), k2, 553648127);
            }
            if (m == collection.size()) {
                final String s4 = p_180475_1_.getDisplayName();
                if (this.background && this.backgroundMode.equalsIgnoreCase("Default")) {
                    Gui.drawRectangle(l1 - 2, k2 - Scoreboard.mc.fontRendererObj.FONT_HEIGHT - 1, l2, k2 - 1, 1610612736);
                    Gui.drawRectangle(l1 - 2, k2 - 1, l2, k2, 1342177280);
                }
                Scoreboard.mc.fontRendererObj.drawString(s4, l1 + i / 2 - Scoreboard.mc.fontRendererObj.getStringWidth(s4) / 2, k2 - Scoreboard.mc.fontRendererObj.FONT_HEIGHT, 553648127);
            }
        }
        final int height = (m + 1) * Scoreboard.mc.fontRendererObj.FONT_HEIGHT;
        this.hud.setWidth(i);
        this.hud.setHeight(height + 1);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
