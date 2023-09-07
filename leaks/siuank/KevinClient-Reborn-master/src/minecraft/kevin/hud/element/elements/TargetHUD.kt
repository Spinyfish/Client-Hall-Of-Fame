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

import blur.GaussianBlur
import kevin.persional.milk.utils.StencilUtil
import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.main.KevinClient
import kevin.module.FloatValue
import kevin.module.ListValue
import kevin.module.modules.combat.KillAura
import kevin.utils.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.pow

@ElementInfo(name = "TargetHUD")
class TargetHUD : Element() {

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    private val fadeSpeed = FloatValue("FadeSpeed", 2F, 1F, 9F)
    private val mode = ListValue("Mode", arrayOf("Liquid", "AnotherLiquid","Kevin"/*, "Exhibition"*/,"Milks","MilkJello"),"Milks")

    private var easingHealth: Float = 0F
    private var healthDiff: Float = 0F
    private var lastTarget: Entity? = null

    override fun drawElement(): Border? {
        var target =
            (KevinClient.moduleManager.getModule(KillAura::class.java)).target ?:
            (KevinClient.moduleManager.getModule(KillAura::class.java)).sTarget
        if(mc.currentScreen is GuiChat)
            target = mc.thePlayer
        when(mode.get()){
            "Liquid" -> {
                if ((target) is EntityPlayer) {
                    if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth ||
                        abs(easingHealth - target.health) < 0.01) {
                        easingHealth = target.health
                    }
                    val width = (38 + (target.name?.let(KevinClient.fontManager.font40::getStringWidth) ?: 0))
                        .coerceAtLeast(118)
                        .toFloat()
                    // Draw rect box
                    RenderUtils.drawBorderedRect(0F, 0F, width, 36F, 3F, Color.BLACK.rgb, Color.BLACK.rgb)
                    // Damage animation
                    if (easingHealth > target.health)
                        RenderUtils.drawRect(0F, 34F, (easingHealth / target.maxHealth) * width,
                            36F, Color(252, 185, 65).rgb)
                    // Health bar
                    RenderUtils.drawRect(0F, 34F, (target.health / target.maxHealth) * width,
                        36F, Color(252, 96, 66).rgb)
                    // Heal animation
                    if (easingHealth < target.health)
                        RenderUtils.drawRect((easingHealth / target.maxHealth) * width, 34F,
                            (target.health / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb)
                    easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
                    target.name?.let { KevinClient.fontManager.font40.drawString(it, 36f, 3f, 0xffffff) }
                    KevinClient.fontManager.font35.drawString("Distance: ${decimalFormat.format(mc.thePlayer!!.getDistanceToEntityBox(target))}", 36f, 15f, 0xffffff)
                    // Draw info
                    val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
                    if (playerInfo != null) {
                        KevinClient.fontManager.font35.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                            36f, 24f, 0xffffff)
                    // Draw head
                        val locationSkin = playerInfo.locationSkin
                        drawHead(locationSkin, 30, 30)
                    }
                }
                lastTarget = target
                return Border(0F, 0F, 120F, 36F)
            }
            "Milks" -> {
                if ((target) is EntityPlayer) {
                    if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth || target.hurtTime <= 0) {
                        easingHealth = target.health
                    }
                    val width = (38 + (target.name?.let(KevinClient.fontManager.font40::getStringWidth) ?: 0))
                        .coerceAtLeast(118)
                        .toFloat()
                    // Draw rect box
                    RenderUtils.drawRectRoundedCorners(0.0 - 3, 0.0 - 3, width.toDouble() + 3, 36.0 + 3, 3.0,
                        Color(59, 59, 59, 255))
                    // Damage animation
                    healthDiff = abs(easingHealth - target.health)
                    if (easingHealth > target.health && target.hurtTime > 0)
                        RenderUtils.drawRect(0F, 34F, ((target.health + healthDiff * (0.1F * target.hurtTime)) / target.maxHealth) * width,
                            36F, Color(255, 200, 200 , 255).rgb)
                    // Health bar
                    RenderUtils.drawGradientSideways(0.0, 34.0, ((target.health / target.maxHealth) * width).toDouble(),
                        36.0, Color(44, 230, 144,255).rgb,
                        Color(255, 255, 255).rgb)
                    // Heal animation
                    if (easingHealth < target.health)
                        RenderUtils.drawRect((easingHealth / target.maxHealth) * width, 34F,
                            (target.health / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb)
                    target.name?.let { KevinClient.fontManager.font40.drawString(it, 36f, 3f, 0xffffff) }
                    KevinClient.fontManager.font35.drawString("HurtTime: ${decimalFormat.format(target.hurtTime)}", 36f, 15f, 0xffffff)
                    // Draw info
                    val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
                    if (playerInfo != null) {
                        KevinClient.fontManager.font35.drawString(
                            if(mc.thePlayer!!.health > target.health) "Win" else "Lose",
                            36f, 24f, 0xffffff)
                        // Draw head
                        val locationSkin = playerInfo.locationSkin
                        drawHead(locationSkin, 30, 30)
                    }
                }
                lastTarget = target
                return Border(0F, 0F, 120F, 36F)
            }
            "MilkJello" -> {
                if ((target) is EntityPlayer) {
                    if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth || target.hurtTime <= 0) {
                        easingHealth = target.health
                    }
                    val width = (38 + (target.name?.let(KevinClient.fontManager.font40::getStringWidth) ?: 0))
                        .coerceAtLeast(118)
                        .toFloat()
                    // Draw rect box
                    GL11.glTranslated(-renderX, -renderY, 0.0)
                    StencilUtil.initStencilToWrite()
                    RenderUtils.drawRectRoundedCorners(0.0 - 3 + renderX, 0.0 - 3 + renderY,
                        width.toDouble() + 3 + renderX, 36.0 + 3 + renderY, 3.0,
                        Color(0, 0, 0, 255))
                    StencilUtil.readStencilBuffer(1)
                    GaussianBlur.renderBlur(10F)
                    StencilUtil.uninitStencilBuffer()
                    GL11.glTranslated(renderX, renderY, 0.0)
                    // Damage animation
                    healthDiff = abs(easingHealth - target.health)
                    if (easingHealth > target.health && target.hurtTime > 0)
                        RenderUtils.drawRect(0F, 34F, ((target.health + healthDiff * (0.1F * target.hurtTime)) / target.maxHealth) * width,
                            36F, Color(255, 200, 200 , 255).rgb)
                    // Health bar
                    RenderUtils.drawGradientSideways(0.0, 34.0, ((target.health / target.maxHealth) * width).toDouble(),
                        36.0, Color(44, 230, 144,255).rgb,
                        Color(255, 255, 255).rgb)
                    // Heal animation
                    if (easingHealth < target.health)
                        RenderUtils.drawRect((easingHealth / target.maxHealth) * width, 34F,
                            (target.health / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb)
                    target.name?.let { KevinClient.fontManager.font40.drawString(it, 36f, 3f, 0xffffff) }
                    KevinClient.fontManager.font35.drawString("HurtTime: ${decimalFormat.format(target.hurtTime)}", 36f, 15f, 0xffffff)
                    // Draw info
                    val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
                    if (playerInfo != null) {
                        KevinClient.fontManager.font35.drawString(
                            if(mc.thePlayer!!.health > target.health) "Win" else "Lose",
                            36f, 24f, 0xffffff)
                        // Draw head
                        val locationSkin = playerInfo.locationSkin
                        drawHead(locationSkin, 30, 30)
                    }
                }
                lastTarget = target
                return Border(0F, 0F, 120F, 36F)
            }
            "AnotherLiquid" -> {
                if ((target) is EntityPlayer) {
                    if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth || target.hurtTime <= 0) {
                        easingHealth = target.health
                    }
                    val width = (38 + (target.name?.let(KevinClient.fontManager.font40::getStringWidth) ?: 0))
                        .coerceAtLeast(118)
                        .toFloat()
                    // Draw rect box
                    RenderUtils.drawRect(0F, 0F, width, 36F, Color(0, 0, 0, 100).rgb)
                    // Damage animation
                    healthDiff = abs(easingHealth - target.health)
                    if (easingHealth > target.health && target.hurtTime > 0)
                        RenderUtils.drawRect(0F, 34F, ((target.health + healthDiff * (0.1F * target.hurtTime)) / target.maxHealth) * width,
                            36F, Color(252, 65, 65 , 100).rgb)
                    // Health bar
                    RenderUtils.drawGradientSideways(0.0, 34.0, ((target.health / target.maxHealth) * width).toDouble(),
                        36.0, Color(196, 10, 196,200).rgb,
                        Color(196, 100, 100).rgb)
                    // Heal animation
                    if (easingHealth < target.health)
                        RenderUtils.drawRect((easingHealth / target.maxHealth) * width, 34F,
                            (target.health / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb)
                    target.name?.let { KevinClient.fontManager.font40.drawString(it, 36f, 3f, 0xffffff) }
                    KevinClient.fontManager.font35.drawString("HurtTime: ${decimalFormat.format(target.hurtTime)}", 36f, 15f, 0xffffff)
                    // Draw info
                    val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
                    if (playerInfo != null) {
                        KevinClient.fontManager.font35.drawString(
                            if(mc.thePlayer!!.health > target.health) "You win" else "You lose",
                            36f, 24f, 0xffffff)
                        // Draw head
                        val locationSkin = playerInfo.locationSkin
                        drawHead(locationSkin, 30, 30)
                    }
                }
                lastTarget = target
                return Border(0F, 0F, 120F, 36F)
            }
            "Kevin" -> {
                if (target!=null){
                    val health = if (target is EntityLivingBase) String.format("%.2f",target.health).toFloat() else 1F
                    val maxHealth = if (target is EntityLivingBase) String.format("%.2f",target.maxHealth).toFloat() else 1F
                    val healthPercent = (health/maxHealth)*100f

                    val hurtTime = if (target is EntityLivingBase) target.hurtTime else 0
                    val ping = if (target is EntityPlayer) target.getPing() else 0
                    val yaw = String.format("%.2f",target.rotationYaw).toFloat()
                    val pitch = String.format("%.2f",target.rotationPitch).toFloat()
                    val distance = String.format("%.2f",mc.thePlayer.getDistanceToEntityBox(target)).toFloat()
                    val onGround = target.onGround

                    val nameText = "Name: ${target.name}"
                    val IDText = "ID: ${target.uniqueID}"
                    val healthText = "Health: $health/$maxHealth  $healthPercent%"
                    val hurtTimeText = "HurtTime: $hurtTime"
                    val pingText = "Ping: $ping"
                    val rotationText = "Yaw: $yaw | Pitch: $pitch"
                    val distanceOnGroundText = "Distance: $distance | OnGround: $onGround"

                    val itemInHand = if (target is EntityLivingBase) target.heldItem else null
                    val armor1 = if (target is EntityLivingBase) target.getCurrentArmor(0) else null
                    val armor2 = if (target is EntityLivingBase) target.getCurrentArmor(1) else null
                    val armor3 = if (target is EntityLivingBase) target.getCurrentArmor(2) else null
                    val armor4 = if (target is EntityLivingBase) target.getCurrentArmor(3) else null

                    val textList = arrayListOf(nameText,healthText,hurtTimeText,pingText,rotationText,distanceOnGroundText)
                    val textListSorted = textList.toMutableList()
                    textListSorted.sortBy{ KevinClient.fontManager.font40.getStringWidth(it)}
                    val width = KevinClient.fontManager.font35.getStringWidth(textListSorted.last())
                    val x2 = if (0.25F+width/2+3F>18*5)0.25F+width/2+3F else 18*5F
                    val text = if (target is EntityLivingBase) "A:${target.totalArmorValue} ${(target.totalArmorValue/20F)*100}%" else "A:? ?%"

                    RenderUtils.drawBorderedRect(-8.5F,-12.5F,14.75F+x2,54.5F,1F,Color.white.rgb,Color(0,0,0,150).rgb)
                    drawEntityOnScreen(3.0,20.0,15F,target)

                    linesStart(1F,Color.white)
                    GL11.glVertex2d(14.75, -12.5)
                    GL11.glVertex2d(14.75,46.5)

                    GL11.glVertex2d(-8.5,23.0)
                    GL11.glVertex2d(14.75+x2,23.0)

                    GL11.glVertex2d(-8.5,46.5)
                    GL11.glVertex2d(14.75+x2,46.5)

                    GL11.glVertex2d(14.75,47-19.0)
                    GL11.glVertex2d(14.75+x2,47-19.0)

                    var xO = 18
                    repeat(4) {
                        GL11.glVertex2d(14.75 + xO, 47 - 19.0)
                        GL11.glVertex2d(14.75 + xO, 46.5)
                        xO += 18
                    }

                    GL11.glVertex2d((14.75+x2+6.5-(KevinClient.fontManager.font35.getStringWidth(text))*0.8),46.5)
                    GL11.glVertex2d((14.75+x2+6.5-(KevinClient.fontManager.font35.getStringWidth(text))*0.8),54.5)
                    linesEnd()
                    linesStart(5F,Color(255,0,0))

                    val h = x2 * (hurtTime / 10F)
                    val hel = (14.0+x2+6.5-(KevinClient.fontManager.font35.getStringWidth(text))*0.8)*(healthPercent/100)

                    GL11.glVertex2d(14.75,25.5)
                    GL11.glVertex2d(14.75+h,25.5)

                    GL11.glVertex2d(-7.75,48.5)
                    GL11.glVertex2d(-7.75+hel,48.5)
                    linesEnd()
                    linesStart(5F,Color(0,111,255))

                    val arv = if (target is EntityLivingBase) (14.0+x2+6.5-(KevinClient.fontManager.font35.getStringWidth(text))*0.8)*(target.totalArmorValue/20F) else .0

                    GL11.glVertex2d(-7.75,52.5)
                    GL11.glVertex2d(-7.75+arv,52.5)
                    linesEnd()

                    GL11.glPushMatrix()
                    GL11.glScaled(0.5,0.5,0.5)
                    KevinClient.fontManager.font35.drawString(text,(14.75F+x2-2F)/0.5F- KevinClient.fontManager.font35.getStringWidth(text),48.5F/0.5F,Color(0,111,255).rgb)
                    GL11.glPopMatrix()

                    if (!(mc.netHandler.getPlayerInfo(target.uniqueID) == null || mc.netHandler.getPlayerInfo(target.uniqueID).locationSkin == null)) {
                        GL11.glPushMatrix()
                        GL11.glTranslated(-10.36, 21.25, 0.0)
                        drawHead(mc.netHandler.getPlayerInfo(target.uniqueID).locationSkin, 23, 23)
                        GL11.glPopMatrix()
                    }

                    var yO = 0.0
                    textList.forEach{
                        yO += if (it == textList.first()){
                            GL11.glPushMatrix()
                            GL11.glScaled(0.6,0.6,0.6)
                            KevinClient.fontManager.font40.drawString(it,16F/0.6F,(-11F+yO.toFloat())/0.6F,Color.white.rgb)
                            GL11.glPopMatrix()
                            KevinClient.fontManager.font40.fontHeight * 0.6
                        }else{
                            GL11.glPushMatrix()
                            GL11.glScaled(0.5,0.5,0.5)
                            KevinClient.fontManager.font35.drawString(it,16F/0.5F,(-11F+yO.toFloat())/0.5F,Color.white.rgb)
                            GL11.glPopMatrix()
                            KevinClient.fontManager.font35.fontHeight * 0.5
                        }
                    }
                    val itemList = arrayListOf(itemInHand,armor1,armor2,armor3,armor4)
                    var x = 1
                    GL11.glPushMatrix()
                    RenderHelper.enableGUIStandardItemLighting()
                    itemList.forEach {
                        x += if (it != null){
                            mc.renderItem.renderItemAndEffectIntoGUI(it, 15+x, 47-18)
                            mc.renderItem.renderItemOverlays(mc.fontRendererObj, it, 15+x, 47-18)
                            18
                        }else{
                            18
                        }
                    }
                    RenderHelper.disableStandardItemLighting()
                    GlStateManager.enableAlpha()
                    GlStateManager.disableBlend()
                    GlStateManager.disableLighting()
                    GlStateManager.disableCull()
                    GL11.glPopMatrix()
                }
                lastTarget = target
                return Border(-8.5F,-12.5F,14.75F+(18*5F),54.5F)
            }
            "Exhibition" -> { // skid from Rise (lol)
                if (target is EntityPlayer) try {
                    GlStateManager.pushMatrix()
                    // Width and height
                    val width = mc.displayWidth / (mc.gameSettings.guiScale * 2).toFloat() + 680
                    val height = mc.displayHeight / (mc.gameSettings.guiScale * 2).toFloat() + 280
                    GlStateManager.translate(width - 660, height - 160.0f - 90.0f, 0.0f)
                    // Draws the skeet rectangles.
                    RenderUtils.skeetRect(
                        0.0,
                        -2.0,
                        if (KevinClient.fontManager.font40.getStringWidth(target.name) > 70.0f) (124.0 + KevinClient.fontManager.font40.getStringWidth(target.name) - 70.0) else 124.0,
                        38.0,
                        1.0
                    )
                    RenderUtils.skeetRectSmall(0.0, -2.0, 124.0, 38.0, 1.0)
                    // Draws name.
                    KevinClient.fontManager.font40.drawStringWithShadow(target.name, 42.3f, 0.3f, -1)
                    // Gets health.
                    val health = target.health
                    // Gets health and absorption
                    val healthWithAbsorption = target.health + target.absorptionAmount
                    // Color stuff for the healthBar.
                    val fractions = floatArrayOf(0.0f, 0.5f, 1.0f)
                    val colors = arrayOf(Color.RED, Color.YELLOW, Color.GREEN)
                    // Max health.
                    val progress = health / target.maxHealth
                    // Color.
                    val healthColor =
                        if (health >= 0.0f) ColorUtils.blendColors(fractions, colors, progress)?.brighter()?: Color.RED else Color.RED
                    // Round.
                    var cockWidth = 0.0
                    cockWidth = MathUtils.round(cockWidth, 5.0.toInt())
                    if (cockWidth < 50.0) {
                        cockWidth = 50.0
                    }
                    // Healthbar + absorption
                    val healthBarPos = cockWidth * progress.toDouble()
                    RenderUtils.rectangle(42.5, 10.3, 103.0, 13.5, healthColor.darker().darker().darker().darker().rgb)
                    RenderUtils.rectangle(42.5, 10.3, 53.0 + healthBarPos + 0.5, 13.5, healthColor.rgb)
                    if (target.absorptionAmount > 0.0f) {
                        RenderUtils.rectangle(
                            97.5 - target.absorptionAmount.toDouble(),
                            10.3,
                            103.5,
                            13.5,
                            Color(137, 112, 9).rgb
                        )
                    }
                    // Draws rect around health bar.
                    RenderUtils.rectangleBordered(42.0, 9.8, 54.0 + cockWidth, 14.0, 0.5, 0, Color.BLACK.rgb)
                    // Draws the lines between the healthbar to make it look like boxes.
                    for (dist in 1..9) {
                        val cock = cockWidth / 8.5 * dist.toDouble()
                        RenderUtils.rectangle(43.5 + cock, 9.8, 43.5 + cock + 0.5, 14.0, Color.BLACK.rgb)
                    }
                    // Draw targets hp number and distance number.
                    GlStateManager.scale(0.5, 0.5, 0.5)
                    val distance = mc.thePlayer.getDistanceToEntity(target).toInt()
                    val nice = "HP: " + healthWithAbsorption.toInt() + " | Dist: " + distance
                    mc.fontRendererObj.drawString(nice, 85.3f, 32.3f, -1, true)
                    GlStateManager.scale(2.0, 2.0, 2.0)
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                    GlStateManager.enableAlpha()
                    GlStateManager.enableBlend()
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                    // Draw targets armor and tools and weapons and shows the enchants.
                    drawEquippedShit(28, 20)
                    GlStateManager.disableAlpha()
                    GlStateManager.disableBlend()
                    // Draws targets model.
                    GlStateManager.scale(0.31, 0.31, 0.31)
                    GlStateManager.translate(73.0f, 102.0f, 40.0f)
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                    RenderUtils.drawModel(target.rotationYaw, target.rotationPitch, target as EntityLivingBase?)
                    GlStateManager.popMatrix()
                } catch (_: Exception) {}
                lastTarget = target
                return Border(0.0f, 0.0f, 124f, 38f)
            }
            else -> return null
        }
    }

    private fun linesStart(width: Float,color: Color){
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        RenderUtils.glColor(color)
        GL11.glLineWidth(width)
        GL11.glBegin(GL11.GL_LINES)
    }
    private fun linesEnd(){
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    private fun drawHead(skin: ResourceLocation, width: Int, height: Int) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        drawScaledCustomSizeModalRect(2, 2, 8F, 8F, 8, 8, width, height,
            64F, 64F)
    }

    private fun drawEntityOnScreen(X: Double, Y: Double, S: Float, entity: Entity){
        GlStateManager.enableColorMaterial()
        GlStateManager.pushMatrix()
        GlStateManager.translate(X, Y, 50.0)
        GlStateManager.scale((-S), S, S)
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F)
        val renderYawOffset = if (entity is EntityLivingBase) entity.renderYawOffset else 0F
        val rotationYaw = entity.rotationYaw
        val rotationPitch = entity.rotationPitch
        val prevRotationYawHead = if (entity is EntityLivingBase) entity.prevRotationYawHead else 0F
        val rotationYawHead = entity.rotationYawHead
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F)
        RenderHelper.enableStandardItemLighting()
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F)

        if (entity is EntityLivingBase) entity.renderYawOffset = atan(entity.rotationYaw / 40F) * 20F
        entity.rotationYaw = atan(entity.rotationYaw / 40F) * 40F
        entity.rotationPitch = -atan((if (entity.rotationPitch > 0) -entity.rotationPitch else abs(entity.rotationPitch)) / 40F) * 20F
        entity.rotationYawHead = entity.rotationYaw
        if (entity is EntityLivingBase) entity.prevRotationYawHead = entity.rotationYaw

        GlStateManager.translate(0.0, 0.0, 0.0)

        val renderManager = mc.renderManager
        renderManager.playerViewY = 180.0F
        renderManager.isRenderShadow = false
        renderManager.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F)
        renderManager.isRenderShadow = true

        if (entity is EntityLivingBase) entity.renderYawOffset = renderYawOffset
        entity.rotationYaw = rotationYaw
        entity.rotationPitch = rotationPitch
        if (entity is EntityLivingBase) entity.prevRotationYawHead = prevRotationYawHead
        entity.rotationYawHead = rotationYawHead

        GlStateManager.popMatrix()
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GlStateManager.disableTexture2D()
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
    }

    private fun drawEquippedShit(x: Int, y: Int) {
        val target = (KevinClient.moduleManager.getModule(KillAura::class.java)).target ?: (KevinClient.moduleManager.getModule(KillAura::class.java)).sTarget
        if (target == null || target !is EntityPlayer) return
        GL11.glPushMatrix()
        val stuff: MutableList<ItemStack> = ArrayList()
        var cock = -2
        for (geraltOfNigeria in 3 downTo 0) {
            val armor = (target as EntityPlayer).getCurrentArmor(geraltOfNigeria)
            if (armor != null) {
                stuff.add(armor)
            }
        }
        if ((target as EntityPlayer).heldItem != null) {
            stuff.add((target as EntityPlayer).heldItem)
        }
        for (yes in stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting()
                cock += 16
            }
            GlStateManager.pushMatrix()
            GlStateManager.disableAlpha()
            GlStateManager.clear(256)
            GlStateManager.enableBlend()
            Minecraft.getMinecraft().renderItem.renderItemIntoGUI(yes, cock + x, y)
            Minecraft.getMinecraft().renderItem.renderItemOverlays(
                Minecraft.getMinecraft().fontRendererObj,
                yes,
                cock + x,
                y
            )
            RenderUtils.renderEnchantText(yes, cock + x, y + 0.5f)
            GlStateManager.disableBlend()
            GlStateManager.scale(0.5, 0.5, 0.5)
            GlStateManager.disableDepth()
            GlStateManager.disableLighting()
            GlStateManager.enableDepth()
            GlStateManager.scale(2.0f, 2.0f, 2.0f)
            GlStateManager.enableAlpha()
            GlStateManager.popMatrix()
            yes.enchantmentTagList
        }
        GL11.glPopMatrix()
    }
}