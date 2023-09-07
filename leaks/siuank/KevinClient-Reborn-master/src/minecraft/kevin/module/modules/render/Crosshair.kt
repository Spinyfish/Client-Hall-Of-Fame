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
import kevin.event.Render2DEvent
import kevin.main.KevinClient
import kevin.module.*
import kevin.utils.ColorUtils
import kevin.utils.MovementUtils
import kevin.utils.RenderUtils
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

object Crosshair : Module("Crosshair",category = ModuleCategory.RENDER) {
    //Color
    private val colorModeValue = ListValue("Color", arrayOf("Custom", "Slowly", "Rainbow"), "Custom")
    private val colorRedValue = IntegerValue("Red", 255, 0, 255)
    private val colorGreenValue = IntegerValue("Green", 255, 0, 255)
    private val colorBlueValue = IntegerValue("Blue", 255, 0, 255)
    private val colorAlphaValue = IntegerValue("Alpha", 255, 0, 255)

    //Rainbow thingy
    private val saturationValue = FloatValue("Saturation", 1f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)

    //Size, width, hitmarker
    private val widthValue = FloatValue("Width", 0.5f, 0.25f, 10f)
    private val sizeValue = FloatValue("Length", 7f, 0.25f, 15f)
    private val gapValue = FloatValue("Gap", 5f, 0.25f, 15f)
    private val dynamicValue = BooleanValue("Dynamic", true)
    private val hitMarkerValue = BooleanValue("HitMarker", true)

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val scaledRes = ScaledResolution(mc)
        val width = widthValue.get()
        val size = sizeValue.get()
        val gap = gapValue.get()
        val isMoving = dynamicValue.get() && MovementUtils.isMoving
        GL11.glPushMatrix()
        RenderUtils.drawBorderedRect(scaledRes.scaledWidth / 2f - width, scaledRes.scaledHeight / 2f - gap - size - if (isMoving) 2 else 0, scaledRes.scaledWidth / 2f + 1.0f + width, scaledRes.scaledHeight / 2f - gap - if (isMoving) 2 else 0, 0.5f, Color(0, 0, 0).rgb, crosshairColor.rgb)
        RenderUtils.drawBorderedRect(scaledRes.scaledWidth / 2f - width, scaledRes.scaledHeight / 2f + gap + 1 + (if (isMoving) 2 else 0) - 0.15f, scaledRes.scaledWidth / 2f + 1.0f + width, scaledRes.scaledHeight / 2f + 1 + gap + size + (if (isMoving) 2 else 0) - 0.15f, 0.5f, Color(0, 0, 0).rgb, crosshairColor.rgb)
        RenderUtils.drawBorderedRect(scaledRes.scaledWidth / 2f - gap - size - (if (isMoving) 2 else 0) + 0.15f, scaledRes.scaledHeight / 2f - width, scaledRes.scaledWidth / 2f - gap - (if (isMoving) 2 else 0) + 0.15f, scaledRes.scaledHeight / 2 + 1.0f + width, 0.5f, Color(0, 0, 0).rgb, crosshairColor.rgb)
        RenderUtils.drawBorderedRect(scaledRes.scaledWidth / 2f + 1 + gap + if (isMoving) 2 else 0, scaledRes.scaledHeight / 2f - width, scaledRes.scaledWidth / 2f + size + gap + 1.0f + if (isMoving) 2 else 0, scaledRes.scaledHeight / 2 + 1.0f + width, 0.5f, Color(0, 0, 0).rgb, crosshairColor.rgb)
        GL11.glPopMatrix()
        GlStateManager.resetColor()
        val target = KevinClient.combatManager.target
        if (hitMarkerValue.get() && target != null && target.hurtTime > 0) {
            GL11.glPushMatrix()
            GlStateManager.enableBlend()
            GlStateManager.disableTexture2D()
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
            GL11.glColor4f(1f, 1f, 1f, target.hurtTime.toFloat() / target.maxHurtTime.toFloat())
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glLineWidth(1f)
            GL11.glBegin(3)
            GL11.glVertex2f(scaledRes.scaledWidth / 2f + gap, scaledRes.scaledHeight / 2f + gap)
            GL11.glVertex2f(scaledRes.scaledWidth / 2f + gap + size, scaledRes.scaledHeight / 2f + gap + size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(scaledRes.scaledWidth / 2f - gap, scaledRes.scaledHeight / 2f - gap)
            GL11.glVertex2f(scaledRes.scaledWidth / 2f - gap - size, scaledRes.scaledHeight / 2f - gap - size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(scaledRes.scaledWidth / 2f - gap, scaledRes.scaledHeight / 2f + gap)
            GL11.glVertex2f(scaledRes.scaledWidth / 2f - gap - size, scaledRes.scaledHeight / 2f + gap + size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(scaledRes.scaledWidth / 2f + gap, scaledRes.scaledHeight / 2f - gap)
            GL11.glVertex2f(scaledRes.scaledWidth / 2f + gap + size, scaledRes.scaledHeight / 2f - gap - size)
            GL11.glEnd()
            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
            GL11.glPopMatrix()
        }
    }

    private val crosshairColor: Color
        get() =
            when (colorModeValue.get().lowercase()) {
                "custom" -> Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), colorAlphaValue.get())
                "slowly" -> ColorUtils.reAlpha(ColorUtils.slowlyRainbow(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get()),
                    colorAlphaValue.get())
                "rainbow" -> ColorUtils.rainbowWithAlpha(colorAlphaValue.get())
                else -> Color.WHITE
            }
}