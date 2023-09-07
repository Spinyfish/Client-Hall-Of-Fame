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

import kevin.font.RainbowFontShader
import kevin.hud.designer.GuiHudDesigner
import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.hud.element.Side
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.IntegerValue
import kevin.module.TextValue
import kevin.utils.*
import net.minecraft.client.Minecraft
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

@ElementInfo(name = "Text")
class Text(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F,
           side: Side = Side.default()) : Element(x, y, scale, side) {

    companion object {

        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")

        val DECIMAL_FORMAT = DecimalFormat("0.00")

        /**
         * Create default element
         */
        fun defaultClient(): Text {
            val text = Text(x = 5.0, y = 15.0, scale = 1F)
            text.displayString.set("%clientName%")
            text.shadow.set(true)
            text.rainbow.set(true)
            return text
        }
        fun defaultClientVersion(): Text {
            val text = Text((KevinClient.fontManager.font40.getStringWidth(KevinClient.name) + 1)/0.6,(11F)/0.6, scale = 0.6F)

            text.displayString.set("%clientVersion%")
            text.shadow.set(true)
            text.rainbow.set(true)
            return text
        }
    }

    private val displayString = TextValue("DisplayText", "")
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val rainbow = BooleanValue("Rainbow", false)
    private val rainbowX = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)
    private val shadow = BooleanValue("Shadow", true)
    private val bigFont = BooleanValue("BigFont", true)
    private val boldFont = BooleanValue("BoldFont", true)
    private val rect = BooleanValue("Rect", true)
    private val rRect = BooleanValue("RoundRect", true)
    private val rRedValue = IntegerValue("rRed", 0, 0, 255)
    private val rGreenValue = IntegerValue("rGreen", 0, 0, 255)
    private val rBlueValue = IntegerValue("rBlue", 0, 0, 255)
    private val rAlphaValue = IntegerValue("rAlpha", 100, 0, 255)
    private var fontValue = KevinClient.fontManager.font35

    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L

    private var displayText = display

    private val display: String
        get() {
            val textContent = if (displayString.get().isEmpty() && !editMode)
                "Text Element"
            else
                displayString.get()


            return multiReplace(textContent)
        }

    private fun getReplacement(str: String): String? {
        val thePlayer = mc.thePlayer

        if (thePlayer != null) {
            when (str.lowercase(Locale.getDefault())) {
                "x" -> return DECIMAL_FORMAT.format(thePlayer.posX.toInt())
                "y" -> return DECIMAL_FORMAT.format(thePlayer.posY.toInt())
                "z" -> return DECIMAL_FORMAT.format(thePlayer.posZ.toInt())
                "xdp" -> return thePlayer.posX.toString()
                "ydp" -> return thePlayer.posY.toString()
                "zdp" -> return thePlayer.posZ.toString()
                "velocity" -> return DECIMAL_FORMAT.format(sqrt(thePlayer.motionX * thePlayer.motionX + thePlayer.motionZ * thePlayer.motionZ) * 20F)
                "ping" -> return thePlayer.getPing().toString()
                "health" -> return DECIMAL_FORMAT.format(thePlayer.health)
                "maxhealth" -> return DECIMAL_FORMAT.format(thePlayer.maxHealth)
                "food" -> return thePlayer.foodStats.foodLevel.toString()
            }
        }

        return when (str.lowercase(Locale.getDefault())) {
            "username" -> mc.session.username
            "clientname" -> KevinClient.name
            "clientversion" -> KevinClient.version
            "fps" -> Minecraft.getDebugFPS().toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverip" -> ServerUtils.getRemoteIp()
            "cps", "lcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()
            else -> null // Null = don't replace
        }
    }

    private fun multiReplace(str: String): String {
        var lastPercent = -1
        val result = StringBuilder()
        for (i in str.indices) {
            if (str[i] == '&') {
                if (lastPercent == -1) {
                    result.append("\u00a7")
                }
            }else
            if (str[i] == '%') {
                if (lastPercent != -1) {
                    if (lastPercent + 1 != i) {
                        val replacement = getReplacement(str.substring(lastPercent + 1, i))

                        if (replacement != null) {
                            result.append(replacement)
                            lastPercent = -1
                            continue
                        }
                    }
                    result.append(str, lastPercent, i)
                }
                lastPercent = i
            } else if (lastPercent == -1) {
                result.append(str[i])
            }
        }

        if (lastPercent != -1) {
            result.append(str, lastPercent, str.length)
        }

        return result.toString()
    }

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val color = Color(redValue.get(), greenValue.get(), blueValue.get()).rgb

        var fontRenderer = fontValue

        fontRenderer = if(bigFont.get())
            KevinClient.fontManager.font60
        else
            KevinClient.fontManager.font40
        if(boldFont.get())
            fontRenderer = KevinClient.fontManager.fontNovo40
        val rainbow = rainbow.get()
        if(rect.get()){
            if(!rRect.get())
                RenderUtils.drawRect(-2F , -2F ,
            fontRenderer.getStringWidth(displayText) + 2F,
                fontRenderer.fontHeight.toFloat(),
                Color(rRedValue.get(),rGreenValue.get(),rBlueValue.get(),rAlphaValue.get())
                )
            else{
                RenderUtils.drawRectRoundedCorners(-2.0 , -2.0 ,
                    fontRenderer.getStringWidth(displayText) + 2.0,
                    fontRenderer.fontHeight.toDouble(),1.0,
                    Color(rRedValue.get(),rGreenValue.get(),rBlueValue.get(),rAlphaValue.get())
                )
                RenderUtils.drawShadow(-2F , -2F , fontRenderer.getStringWidth(displayText).toFloat() , fontRenderer.fontHeight.toFloat())
            }
        }
        RainbowFontShader.begin(rainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
            fontRenderer.drawString(displayText, 0F, 0F, if (rainbow)
                0 else color, shadow.get())

            if (editMode && (mc.currentScreen) is GuiHudDesigner && editTicks <= 40){
                    fontRenderer.drawString("_", fontRenderer.getStringWidth(displayText) + 2F,
                        0F, if (rainbow) ColorUtils.rainbow(400000000L).rgb else color, shadow.get())
            }
        }

        if (editMode && (mc.currentScreen) !is GuiHudDesigner) {
            editMode = false
            updateElement()
        }

        return Border(
            -2F,
            -2F,
            fontRenderer.getStringWidth(displayText) + 2F,
            fontRenderer.fontHeight.toFloat()
        )
    }

    override fun updateElement() {
        editTicks += 5
        if (editTicks > 80) editTicks = 0

        displayText = if (editMode) displayString.get() else display
    }

    override fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {
        if (isInBorder(x, y) && mouseButton == 0) {
            if (System.currentTimeMillis() - prevClick <= 250L)
                editMode = true

            prevClick = System.currentTimeMillis()
        } else {
            editMode = false
        }
    }

    override fun handleKey(c: Char, keyCode: Int) {
        if (editMode && (mc.currentScreen) is GuiHudDesigner) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (displayString.get().isNotEmpty())
                    displayString.set(displayString.get().substring(0, displayString.get().length - 1))

                updateElement()
                return
            }

            if (ColorUtils.isAllowedCharacter(c) || c == '§')
                displayString.set(displayString.get() + c)

            updateElement()
        }
    }

    fun setColor(c: Color): Text {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }

}