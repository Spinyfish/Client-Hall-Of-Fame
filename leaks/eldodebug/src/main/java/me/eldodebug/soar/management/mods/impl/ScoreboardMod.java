package me.eldodebug.soar.management.mods.impl;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRenderScoreboard;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

public class ScoreboardMod extends Mod{

	public static int shadowX = 0, shadowY = 0, shadowWidth = 0, shadowHeight = 0;
	
	public ScoreboardMod() {
		super("Scoreboard", "Customize scoreboard", ModCategory.HUD);
	}
	
	@Override
	public void setup() {
		this.addBooleanSetting("Background", this, true);
		this.addBooleanSetting("Shadow", this, true);
		this.addBooleanSetting("ShowNumber", this, true);
	}

	@EventTarget
	public void onRenderScoreboard(EventRenderScoreboard event) {
		
		boolean showNumber = Soar.instance.settingsManager.getSettingByName(this, "ShowNumber").getValBoolean();

		Scoreboard scoreboard = event.getObjective().getScoreboard();
		Collection<Score> collection = scoreboard.getSortedScores(event.getObjective());
		List<Score> list = Lists.newArrayList(Iterables.filter(collection,
				p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")));

		event.setCancelled(true);
		
		if(list.size() > 15) {
			collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
		}
		else {
			collection = list;
		}

		int i = mc.fontRendererObj.getStringWidth(event.getObjective().getDisplayName());

		for(Score score : collection) {
			ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
			String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
			i = Math.max(i, mc.fontRendererObj.getStringWidth(s));
		}

		int i1 = collection.size() * mc.fontRendererObj.FONT_HEIGHT;
		int j1 = this.getY() + i1 / 3;
		int k1 = 3;
		int l1 = this.getX() - i - k1;
		int j = 0;
		
		for(Score score1 : collection) {
			++j;
			ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
			String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
			String s2 = "" + EnumChatFormatting.RED + score1.getScorePoints();
			int k = j1 - j * mc.fontRendererObj.FONT_HEIGHT;
			int l = this.getX() - k1 + 2;

			if(Soar.instance.settingsManager.getSettingByName(this, "Background").getValBoolean()) {
				Gui.drawRect(l1 - 2, k, l - (showNumber? 0 : 13), k + mc.fontRendererObj.FONT_HEIGHT, 1342177280);
			}
			mc.fontRendererObj.drawString(s1, l1, k, 553648127);

			if(showNumber == true) {
				mc.fontRendererObj.drawString(s2, l - mc.fontRendererObj.getStringWidth(s2), k, 553648127);
			}

			if(j == collection.size()) {
				String s3 = event.getObjective().getDisplayName();
				
				if(Soar.instance.settingsManager.getSettingByName(this, "Background").getValBoolean()) {
					Gui.drawRect(l1 - 2, k - mc.fontRendererObj.FONT_HEIGHT - 1, l - (showNumber? 0 : 13), k - 1, 1610612736);
					Gui.drawRect(l1 - 2, k - 1, l - (showNumber? 0 : 13), k, 1342177280);
				}
				mc.fontRendererObj.drawString(s3, l1 + i / 2 - mc.fontRendererObj.getStringWidth(s3) / 2, k - mc.fontRendererObj.FONT_HEIGHT, 553648127);
			}
		}

		int top = ((j1 - j * mc.fontRendererObj.FONT_HEIGHT) - mc.fontRendererObj.FONT_HEIGHT) - 2;
		
		shadowX = l1 - 3;
		shadowY = top;
		shadowWidth = this.getX() - k1 + 2 - (showNumber? 0 : 13);
		shadowHeight = top + mc.fontRendererObj.FONT_HEIGHT + 3 + i1;
		
		this.setWidth(shadowWidth);
		this.setHeight(shadowHeight);
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective scoreobjective = null;
        ScoreObjective scoreobjective1 = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
		
        if (scoreobjective1 != null && Soar.instance.settingsManager.getSettingByName(this, "Shadow").getValBoolean())
        {
			RenderUtils.drawRect(shadowX + 1, shadowY + 1, shadowWidth - shadowX - 1, shadowHeight - 2 - shadowY, new Color(0, 0, 0).getRGB());
        }
	}
}
