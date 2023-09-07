/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kevin.hud.element.elements

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.hud.element.Side
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.IntegerValue
import kevin.module.ListValue
import kevin.module.modules.misc.NoScoreboard
import kevin.utils.ColorUtils
import kevin.font.RainbowFontShader
import kevin.utils.RenderUtils
import net.minecraft.scoreboard.*
import net.minecraft.util.EnumChatFormatting
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * CustomHUD scoreboard
 *
 * Allows to move and customize minecraft scoreboard
 */
@ElementInfo(name = "Scoreboard")
class ScoreboardElement(x: Double = 5.0, y: Double = 0.0, scale: Float = 1F,
                        side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.MIDDLE)) : Element(x, y, scale, side) {

    private val textRedValue = IntegerValue("Text-R", 255, 0, 255)
    private val textGreenValue = IntegerValue("Text-G", 255, 0, 255)
    private val textBlueValue = IntegerValue("Text-B", 255, 0, 255)

    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255)
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255)
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255)
    private val backgroundColorAlphaValue = IntegerValue("Background-Alpha", 95, 0, 255)

    private val rectValue = BooleanValue("Rect", false)
    private val rectColorModeValue = ListValue("Rect-Color", arrayOf("Custom", "Rainbow"), "Custom")
    private val rectColorRedValue = IntegerValue("Rect-R", 0, 0, 255)
    private val rectColorGreenValue = IntegerValue("Rect-G", 111, 0, 255)
    private val rectColorBlueValue = IntegerValue("Rect-B", 255, 0, 255)
    private val rectColorBlueAlpha = IntegerValue("Rect-Alpha", 255, 0, 255)

    private val clientFont = BooleanValue("ClientFont", false)

    private val shadowValue = BooleanValue("Shadow", false)

    private val clientName = BooleanValue("ClientName", false)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        if (NoScoreboard.state)
            return null

        val textColor = textColor().rgb
        val backColor = backgroundColor().rgb

        val rectColorMode = rectColorModeValue.get()
        val rectCustomColor = Color(rectColorRedValue.get(), rectColorGreenValue.get(), rectColorBlueValue.get(),
            rectColorBlueAlpha.get()).rgb

        val worldScoreboard = mc.theWorld!!.scoreboard
        var currObjective: ScoreObjective? = null
        val playerTeam = worldScoreboard.getPlayersTeam(mc.thePlayer!!.name)

        if (playerTeam != null) {
            val colorIndex = playerTeam.chatFormat.colorIndex

            if (colorIndex >= 0)
                currObjective = worldScoreboard.getObjectiveInDisplaySlot(3 + colorIndex)
        }

        val objective = currObjective ?: worldScoreboard.getObjectiveInDisplaySlot(1) ?: return null

        val scoreboard = objective.scoreboard
        var scoreCollection = scoreboard.getSortedScores(objective)
        val scores = Lists.newArrayList(Iterables.filter(scoreCollection) { input ->
            input?.playerName != null && !input.playerName.startsWith("#")
        })

        fun clientname(): Score {
            val s = Score(Scoreboard(), ScoreObjective(Scoreboard(),"", IScoreObjectiveCriteria.health),"§3§l§nKevin§6§l§nClient")
            s.scorePoints = -114514
            return s
        }

        if(clientName.get()) scores.add(0,clientname())

        scoreCollection = if (scores.size > 15)
            Lists.newArrayList(Iterables.skip(scores, scoreCollection.size - 15))
        else
            scores

        var maxWidth = getStringWidth(objective.displayName)

        for (score in scoreCollection) {
            val scorePlayerTeam = scoreboard.getPlayersTeam(score.playerName)
            val width = "${scoreboardFormatPlayerName(scorePlayerTeam, score.playerName)}: ${EnumChatFormatting.RED}${score.scorePoints}"
            maxWidth = maxWidth.coerceAtLeast(getStringWidth(width))
        }

        val maxHeight = scoreCollection.size * FONT_HEIGHT
        val l1 = -maxWidth - 3 - if (rectValue.get()) 3 else 0



        RenderUtils.drawRect(l1 - 2, -2, 5, (maxHeight + FONT_HEIGHT), backColor)

        scoreCollection.forEachIndexed { index, score ->
            val team = scoreboard.getPlayersTeam(score.playerName)

            val name = scoreboardFormatPlayerName(team, score.playerName)
            val scorePoints = "${EnumChatFormatting.RED}${score.scorePoints}"

            val width = 5 - if (rectValue.get()) 4 else 0
            val height = maxHeight - index * FONT_HEIGHT.toFloat()

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

            //if(clientName.get()&&name.toLowerCase().contains(".net")) name = name + " §3§l§nKevin§6§l§nClient"

            if(name != "§3§l§nKevin§6§l§nClient"){
                drawString(name, l1.toFloat(), height, textColor, shadowValue.get())
            }else{
                RainbowFontShader.begin(true,1.0F / 1000,1.0F / 1000,System.currentTimeMillis() % 10000 / 10000F).use {
                    drawString("§lKevin§lClient", l1.toFloat(), height, 0, shadowValue.get())
                }
            }

            drawString(scorePoints, (width - getStringWidth(scorePoints)).toFloat(), height, textColor, shadowValue.get())

            if (index == scoreCollection.size - 1) {
                val displayName = objective.displayName

                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

                drawString(displayName, (l1 + maxWidth / 2 - getStringWidth(displayName) / 2).toFloat(), (height -
                        FONT_HEIGHT), textColor, shadowValue.get())
            }

            if (rectValue.get()) {
                val rectColor = when {
                    rectColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(400000000L * index).rgb
                    else -> rectCustomColor
                }

                RenderUtils.drawRect(2F, if (index == scoreCollection.size - 1) -2F else height, 5F, if (index == 0) FONT_HEIGHT.toFloat() else height + FONT_HEIGHT * 2F, rectColor)
            }
        }

        return Border(-maxWidth.toFloat() - 5 - if (rectValue.get()) 3 else 0, -2F, 5F, maxHeight + FONT_HEIGHT.toFloat())
    }

    private fun backgroundColor() = Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(),
        backgroundColorBlueValue.get(), backgroundColorAlphaValue.get())

    private fun textColor() = Color(textRedValue.get(), textGreenValue.get(),
        textBlueValue.get())

    private fun scoreboardFormatPlayerName(scorePlayerTeam: Team?, playerName: String) =
        ScorePlayerTeam.formatPlayerName(scorePlayerTeam, playerName)

    private fun drawString(string: String?, x: Float, y: Float, color: Int, shadow: Boolean) =
        if (clientFont.get()) KevinClient.fontManager.font35!!.drawString(string, x, y, color, shadow) else KevinClient.fontManager.minecraftFont.drawString(string, x, y, color, shadow)

    private val FONT_HEIGHT
    get() = if (clientFont.get()) KevinClient.fontManager.font35!!.fontHeight else KevinClient.fontManager.minecraftFont.FONT_HEIGHT

    private fun getStringWidth(string: String) =
        if (clientFont.get()) KevinClient.fontManager.font35!!.getStringWidth(string) else KevinClient.fontManager.minecraftFont.getStringWidth(string)
}