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
package kevin.module.modules.render

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.misc.AntiBot
import kevin.utils.ColorUtils
import kevin.utils.EntityUtils
import kevin.utils.RenderUtils.drawBorderedRect
import kevin.utils.RenderUtils.drawRect
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.util.*
import kotlin.math.roundToInt

class NameTags : Module("NameTags", "Changes the scale of the nametags so you can always read them.", category = ModuleCategory.RENDER) {
    private val modeValue = ListValue("Mode", arrayOf("Simple","Liquid","Jello"),"Simple")
    private val healthValue = BooleanValue("Health", true)
    private val pingValue = BooleanValue("Ping", true)
    private val distanceValue = BooleanValue("Distance", false)
    private val armorValue = BooleanValue("Armor", true)
    private val clearNamesValue = BooleanValue("ClearNames", true)
    private val fontValue = KevinClient.fontManager.font40!!
    private val borderValue = BooleanValue("Border", true)
    private val jelloColorValue = BooleanValue("JelloHPColor", true)
    private val jelloAlphaValue = IntegerValue("JelloAlpha", 170, 0, 255)
    private val scaleValue = FloatValue("Scale", 1F, 1F, 4F)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        for(entity in mc.theWorld.loadedEntityList) {
            if(EntityUtils.isSelected(entity, false)) {
                renderNameTag(entity as EntityLivingBase,
                     if(!modeValue.get().equals("Liquid",ignoreCase = true) && AntiBot.isBot(entity)){ "§e" }else{ "" }
                            + if (clearNamesValue.get()){ entity.name } else { entity.getDisplayName().unformattedText })
            }
        }
    }

    private fun renderNameTag(entity: EntityLivingBase, tag: String) {
        // Set fontrenderer local
        val fontRenderer = fontValue

        // Push
        glPushMatrix()

        // Translate to player position
        val renderManager = mc.renderManager
        val timer = mc.timer

        glTranslated( // Translate to player position with render pos and interpolate it
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX,
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY + entity.eyeHeight.toDouble() + 0.55,
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ
        )

        // Rotate view to player
        glRotatef(-mc.renderManager.playerViewY, 0F, 1F, 0F)
        glRotatef(mc.renderManager.playerViewX, 1F, 0F, 0F)

        // Scale
        var distance = mc.thePlayer.getDistanceToEntity(entity) / 4F

        if (distance < 1F)
            distance = 1F

        val scale = (distance / 150F) * scaleValue.get()

        // Disable lightning and depth test
        glDisable(GL_LIGHTING)
        glDisable(GL_DEPTH_TEST)

        // Enable blend
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        // Draw nametag
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "simple" -> {
                val healthPercent = (entity.health / entity.maxHealth).coerceAtMost(1F)
                val width = fontRenderer.getStringWidth(tag).coerceAtLeast(30) / 2
                val maxWidth = width * 2 + 12F

                glScalef(-scale * 2, -scale * 2, scale * 2)
                drawRect(
                    -width - 6F,
                    -fontRenderer.fontHeight * 1.7F,
                    width + 6F,
                    -2F,
                    Color(0, 0, 0, jelloAlphaValue.get()).rgb
                )
                drawRect(
                    -width - 6F, -2F, -width - 6F + (maxWidth * healthPercent), 0F,
                    ColorUtils.healthColor(entity.health, entity.maxHealth, jelloAlphaValue.get()).rgb
                )
                drawRect(
                    -width - 6F + (maxWidth * healthPercent),
                    -2F,
                    width + 6F,
                    0F,
                    Color(0, 0, 0, jelloAlphaValue.get()).rgb
                )
                fontRenderer.drawString(
                    tag,
                    (-fontRenderer.getStringWidth(tag) * 0.5F),
                    (-fontRenderer.fontHeight * 1.4F),
                    Color.WHITE.rgb
                )
            }

            "liquid" -> {
                // Modify tag
                val bot = AntiBot.isBot(entity)
                val nameColor =
                    if (bot) "§3" else if (entity.isInvisible) "§6" else if (entity.isSneaking) "§4" else "§7"
                val ping = if (entity is EntityPlayer) EntityUtils.getPing(entity) else 0

                val distanceText =
                    if (distanceValue.get()) "§7 [§a${mc.thePlayer.getDistanceToEntity(entity).roundToInt()}§7]" else ""
                val pingText =
                    if (pingValue.get() && entity is EntityPlayer) (if (ping > 200) "§c" else if (ping > 100) "§e" else "§a") + ping + "ms §7" else ""
                val healthText =
                    if (healthValue.get()) "§7 [§c${entity.health.toInt()}❤${if (entity.absorptionAmount != 0F) "§7 + §e${entity.absorptionAmount.toInt()}❤" else ""}§7]" else ""
                val botText = if (bot) " §7[§6§lBot§7]" else ""

                val text = "$distanceText$pingText$nameColor$tag$healthText$botText"

                glScalef(-scale, -scale, scale)
                val width = fontRenderer.getStringWidth(text) / 2
                if (borderValue.get())
                    drawBorderedRect(
                        -width - 2F,
                        -2F,
                        width + 4F,
                        fontRenderer.fontHeight + 2F,
                        2F,
                        Color(255, 255, 255, 90).rgb,
                        Integer.MIN_VALUE
                    )
                else
                    drawRect(-width - 2F, -2F, width + 4F, fontRenderer.fontHeight + 2F, Integer.MIN_VALUE)

                fontRenderer.drawString(text, 1F + -width, 1.5F, 0xFFFFFF, true)

                if (armorValue.get() && entity is EntityPlayer) {
                    for (index in 0..4) {
                        if (entity.getEquipmentInSlot(index) == null)
                            continue

                        mc.renderItem.zLevel = -147F
                        mc.renderItem.renderItemAndEffectIntoGUI(
                            entity.getEquipmentInSlot(index),
                            -50 + index * 20,
                            -22
                        )
                    }

                    enableAlpha()
                    disableBlend()
                    enableTexture2D()
                }
            }

            "jello" -> {
                //colors
                var hpBarColor = Color(255, 255, 255, jelloAlphaValue.get())
                val name = entity.displayName.unformattedText
                if (jelloColorValue.get() && name.startsWith("§")) {
                    hpBarColor = ColorUtils.colorCode(name.substring(1, 2), jelloAlphaValue.get())
                }
                val bgColor = Color(50, 50, 50, jelloAlphaValue.get())
                val width = fontRenderer.getStringWidth(tag) / 2
                val maxWidth = (width + 4F) - (-width - 4F)
                var healthPercent = entity.health / entity.maxHealth

                //render bg
                glScalef(-scale * 2, -scale * 2, scale * 2)
                drawRect(-width - 4F, -fontRenderer.fontHeight * 3F, width + 4F, -3F, bgColor.rgb)

                //render hp bar
                if (healthPercent > 1) {
                    healthPercent = 1F
                }

                drawRect(-width - 4F, -3F, (-width - 4F) + (maxWidth * healthPercent), 1F, hpBarColor.rgb)
                drawRect((-width - 4F) + (maxWidth * healthPercent), -3F, width + 4F, 1F, bgColor.rgb)

                //string
                fontRenderer.drawString(tag, -width.toFloat(), -fontRenderer.fontHeight * 2 - 4F, Color.WHITE.rgb)
                glScalef(0.5F, 0.5F, 0.5F)
                fontRenderer.drawString(
                    "Health: " + entity.health.toInt(),
                    -width * 2F,
                    -fontRenderer.fontHeight * 2F,
                    Color.WHITE.rgb
                )
            }
        }
        // Reset caps

        glEnable(GL_DEPTH_TEST)
        glDisable(GL_BLEND)

        // Reset color
        resetColor()
        glColor4f(1F, 1F, 1F, 1F)

        // Pop
        glPopMatrix()
    }
}