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

import kevin.altmanager.AltManager.drawTexturedModalRect
import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.hud.element.Side
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.ListValue
import kevin.utils.RenderUtils
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.I18n
import net.minecraft.potion.Potion
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_BLEND
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import java.awt.Color

@ElementInfo(name = "Effects")
class Effects(x: Double = 5.0, y: Double = 50.0, scale: Float = 1F,
              side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.MIDDLE)) : Element(x, y, scale, side) {

    private val mode = ListValue("Mode", arrayOf("LiquidBounce", "Kevin-New", "Kevin-Shadow"), "LiquidBounce")
    private val clientFont = BooleanValue("ClientFont", false)
    private val timeColor = BooleanValue("TimeColor", true)
    private val liquidColor = BooleanValue("LiquidColor", true)
    private val progressBar = BooleanValue("ProgressBar", true)
    private val amplifierNumberMode = ListValue("AmplifierNumberMode", arrayOf("Normal", "I-X+", "Number"), "I-X+")

    private val hashMap = HashMap<Int,Pair<Pair<Int,Boolean>,Boolean>>()

    override fun drawElement(): Border {
        var y = 0F
        var width = 0F
        var j = 0.0
        val l = 32.0
        val m = 35.0
        val i = 0.0

        val fontRenderer = mc.fontRendererObj

        val effects = mc.thePlayer.activePotionEffects

        return when (mode.get()) {
            "LiquidBounce" -> {
                //assumeNonVolatile = true

                for (effect in effects) {
                    val potion = Potion.potionTypes[effect.potionID]

                    val number = if (amplifierNumberMode equal "Normal" || amplifierNumberMode equal "I-X+") when {
                        effect.amplifier == 1 -> "II"
                        effect.amplifier == 2 -> "III"
                        effect.amplifier == 3 -> "IV"
                        effect.amplifier == 4 -> "V"
                        effect.amplifier == 5 -> "VI"
                        effect.amplifier == 6 -> "VII"
                        effect.amplifier == 7 -> "VIII"
                        effect.amplifier == 8 -> "IX"
                        effect.amplifier == 9 -> "X"
                        effect.amplifier > 10 -> "X+"
                        else -> "I"
                    } else (effect.amplifier+1).toString()

                    val color = if (timeColor.get()) if (Potion.getDurationString(effect) == "**:**") "§9"
                    else if (Potion.getDurationString(effect).split(":")[1].toInt()<10 && Potion.getDurationString(effect).split(":")[0].toInt() == 0) "§c"
                    else if (Potion.getDurationString(effect).split(":")[0].toInt() == 0) "§e"
                    else "§a" else "§f"

                    val name = "${I18n.format(potion.name)} $number §f: $color${Potion.getDurationString(effect)}"

                    val liquidColor = if (liquidColor.get()) potion.liquidColor else Color.WHITE.rgb

                    y += if (clientFont.get()) {
                        KevinClient.fontManager.font35.drawString(name, 0F, y, liquidColor, true)
                        KevinClient.fontManager.font35.fontHeight
                    } else {
                        fontRenderer.drawString(name, 0F, y, liquidColor, true)
                        fontRenderer.FONT_HEIGHT
                    }

                    if (fontRenderer.getStringWidth(name).toFloat()>width) { width = fontRenderer.getStringWidth(name).toFloat() }
                }

                //assumeNonVolatile = false

                if (width == 0F)
                    width = 40F

                if (y == 0F)
                    y = -10F

                Border(width + 6F, -2F, -2F, y + 2F)
            }
            "Kevin-New" -> {
                if (effects.isNotEmpty()) {
                    hashMap.replaceAll { _,v -> v.first.first to v.first.second to false }
                    GL11.glPushMatrix()
                    for (effect in effects) {
                        RenderUtils.drawRectRoundedCorners(i, j, i + 140.0, j + 30.0, 5.0, Color(192, 192, 192, 128))
                        val percent =
                            if (hashMap[effect.potionID] == null || hashMap[effect.potionID]!!.first.first < effect.duration || (!hashMap[effect.potionID]!!.first.second && effect.isPotionDurationMax)) {
                                hashMap[effect.potionID] = effect.duration to effect.isPotionDurationMax to true
                                1.0
                            } else {
                                val value = hashMap[effect.potionID]!!
                                hashMap[effect.potionID] = value.first.first to value.first.second to true
                                if (value.first.second) 1.0 else effect.duration.toDouble() / value.first.first.toDouble() * 1.0
                            }
                        if (progressBar.get()) {
                            RenderUtils.drawRectRoundedCorners(i, j, i + (140.0*percent), j + 30.0, 5.0, if (timeColor.get()) if(percent > 0.5) Color(0, 192, 0, 128) else if (percent > 0.25) Color(192, 192, 0, 128) else Color(192, 0, 0, 128) else Color(114, 114, 114, 128))
                        }
                        RenderUtils.drawBorderRoundedCorners(i, j, i + 140.0, j + 30.0, 5.0, 3F, Color(50, 50, 50, 128))
                        j += l
                    }
                    GL11.glPopMatrix()
                    hashMap.filter { !it.value.second }.forEach { (t, _) -> hashMap.remove(t) }
                    j = 0.0
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                    GlStateManager.disableLighting()
                    for (effect in effects) {
                        val potion = Potion.potionTypes[effect.potionID]

                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                        mc.textureManager.bindTexture(GuiContainer.inventoryBackground)

                        if (potion.hasStatusIcon()) {
                            GlStateManager.pushMatrix()
                            GL11.glDisable(GL_DEPTH_TEST)
                            GL11.glEnable(GL_BLEND)
                            GL11.glDepthMask(false)
                            OpenGlHelper.glBlendFunc(770, 771, 1, 0)
                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
                            val i1 = potion.statusIconIndex
                            drawTexturedModalRect((i + 6F).toFloat(), (j + 7F).toFloat(), 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18)
                            GL11.glDepthMask(true)
                            GL11.glDisable(GL_BLEND)
                            GL11.glEnable(GL_DEPTH_TEST)
                            GlStateManager.popMatrix()
                        }

                        var s1 = I18n.format(potion.name)

                        if (amplifierNumberMode equal "Normal") when (effect.amplifier) {
                            1 -> s1 = "$s1 " + I18n.format("enchantment.level.2")
                            2 -> s1 = "$s1 " + I18n.format("enchantment.level.3")
                            3 -> s1 = "$s1 " + I18n.format("enchantment.level.4")
                        } else if (amplifierNumberMode equal "I-X+") {
                            s1 = "$s1 " + when {
                                effect.amplifier == 1 -> "II"
                                effect.amplifier == 2 -> "III"
                                effect.amplifier == 3 -> "IV"
                                effect.amplifier == 4 -> "V"
                                effect.amplifier == 5 -> "VI"
                                effect.amplifier == 6 -> "VII"
                                effect.amplifier == 7 -> "VIII"
                                effect.amplifier == 8 -> "IX"
                                effect.amplifier == 9 -> "X"
                                effect.amplifier > 10 -> "X+"
                                else -> "I"
                            }
                        } else {
                            s1 = "$s1 " + (effect.amplifier+1).toString()
                        }

                        (if (clientFont.get()) KevinClient.fontManager.font35 else fontRenderer).drawStringWithShadow(
                            s1,
                            (i + 10 + 18).toFloat(),
                            (j + 6).toFloat(),
                            if (liquidColor.get()) potion.liquidColor else 16777215
                        )
                        val s = Potion.getDurationString(effect)
                        if (clientFont.get())
                            KevinClient.fontManager.font35.drawStringWithShadow(
                                s,
                                (i + 10 + 18).toFloat(),
                                (j + 6 + 10).toFloat(),
                                8355711
                            )
                        else
                            fontRenderer.drawStringWithShadow(
                            s,
                            (i + 10 + 18).toFloat(),
                            (j + 6 + 10).toFloat(),
                            8355711
                        )
                        j += l
                    }
                } else hashMap.clear()
                Border(-2F, -2F, 142F, j.toFloat())
            }
            "Kevin-Shadow" -> {
                if (effects.isNotEmpty()) {
                    hashMap.replaceAll { _,v -> v.first.first to v.first.second to false }
                    for (effect in effects) {
                        val percent =
                            if (hashMap[effect.potionID] == null || hashMap[effect.potionID]!!.first.first < effect.duration || (!hashMap[effect.potionID]!!.first.second && effect.isPotionDurationMax)) {
                                hashMap[effect.potionID] = effect.duration to effect.isPotionDurationMax to true
                                1.0
                            } else {
                                val value = hashMap[effect.potionID]!!
                                hashMap[effect.potionID] = value.first.first to value.first.second to true
                                if (value.first.second) 1.0 else effect.duration.toDouble() / value.first.first.toDouble() * 1.0
                            }
                        if (progressBar.get()) {
                            RenderUtils.drawRect(i, j, i + (140.0*percent), j + 30.0, (if (timeColor.get()) if(percent > 0.5) Color(0, 192, 0, 128) else if (percent > 0.25) Color(192, 192, 0, 128) else Color(192, 0, 0, 128) else Color(50, 50, 50, 128)).rgb)
                        }
                        RenderUtils.drawShadow(i.toFloat(), j.toFloat(), 140F, 30F)
                        j += m
                    }
                    hashMap.filter { !it.value.second }.forEach { (t, _) -> hashMap.remove(t) }
                    j = 0.0
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                    GlStateManager.disableLighting()
                    for (effect in effects) {
                        val potion = Potion.potionTypes[effect.potionID]

                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                        mc.textureManager.bindTexture(GuiContainer.inventoryBackground)

                        if (potion.hasStatusIcon()) {
                            GlStateManager.pushMatrix()
                            GL11.glDisable(GL_DEPTH_TEST)
                            GL11.glEnable(GL_BLEND)
                            GL11.glDepthMask(false)
                            OpenGlHelper.glBlendFunc(770, 771, 1, 0)
                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
                            val i1 = potion.statusIconIndex
                            drawTexturedModalRect((i + 6F).toFloat(), (j + 7F).toFloat(), 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18)
                            GL11.glDepthMask(true)
                            GL11.glDisable(GL_BLEND)
                            GL11.glEnable(GL_DEPTH_TEST)
                            GlStateManager.popMatrix()
                        }

                        var s1 = I18n.format(potion.name)

                        if (amplifierNumberMode equal "Normal") when (effect.amplifier) {
                            1 -> s1 = "$s1 " + I18n.format("enchantment.level.2")
                            2 -> s1 = "$s1 " + I18n.format("enchantment.level.3")
                            3 -> s1 = "$s1 " + I18n.format("enchantment.level.4")
                        } else if (amplifierNumberMode equal "I-X+") {
                            s1 = "$s1 " + when {
                                effect.amplifier == 1 -> "II"
                                effect.amplifier == 2 -> "III"
                                effect.amplifier == 3 -> "IV"
                                effect.amplifier == 4 -> "V"
                                effect.amplifier == 5 -> "VI"
                                effect.amplifier == 6 -> "VII"
                                effect.amplifier == 7 -> "VIII"
                                effect.amplifier == 8 -> "IX"
                                effect.amplifier == 9 -> "X"
                                effect.amplifier > 10 -> "X+"
                                else -> "I"
                            }
                        } else {
                            s1 = "$s1 " + (effect.amplifier+1).toString()
                        }

                        (if (clientFont.get()) KevinClient.fontManager.font35 else fontRenderer).drawStringWithShadow(
                            s1,
                            (i + 10 + 18).toFloat(),
                            (j + 6).toFloat(),
                            if (liquidColor.get()) potion.liquidColor else 16777215
                        )
                        val s = Potion.getDurationString(effect)
                        if (clientFont.get())
                            KevinClient.fontManager.font35.drawStringWithShadow(
                                s,
                                (i + 10 + 18).toFloat(),
                                (j + 6 + 10).toFloat(),
                                8355711
                            )
                        else
                            fontRenderer.drawStringWithShadow(
                                s,
                                (i + 10 + 18).toFloat(),
                                (j + 6 + 10).toFloat(),
                                8355711
                            )
                        j += m
                    }
                } else hashMap.clear()
                Border(-2F, -2F, 142F, j.toFloat()-3F)
            }
            else -> Border(6F, -6F, -6F, 6F)
        }
    }
}