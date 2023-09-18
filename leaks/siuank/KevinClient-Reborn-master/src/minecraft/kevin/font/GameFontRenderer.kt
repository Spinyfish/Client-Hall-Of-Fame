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
package kevin.font

import kevin.event.TextEvent
import kevin.font.renderer.AbstractAwtFontRender
import kevin.main.KevinClient
import kevin.module.modules.render.RenderSettings
import kevin.utils.ColorUtils
import kevin.utils.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.awt.Color
import java.awt.Font

class GameFontRenderer(font: Font): FontRenderer(
        Minecraft.getMinecraft().gameSettings, ResourceLocation("textures/font/ascii.png"),
        Minecraft.getMinecraft().textureManager,
        false
    ) {

    val fontHeight: Int
    private val defaultFontGlyph = AbstractAwtFontRender.buildGlyphFontRenderer(font)
    private val boldFontGlyph = AbstractAwtFontRender.buildGlyphFontRenderer(font.deriveFont(Font.BOLD))
    private val italicFontGlyph = AbstractAwtFontRender.buildGlyphFontRenderer(font.deriveFont(Font.ITALIC))
    private val boldItalicFontGlyph = AbstractAwtFontRender.buildGlyphFontRenderer(font.deriveFont(Font.BOLD or Font.ITALIC))
    private val defaultFontVector = AbstractAwtFontRender.buildVectorFontRenderer(font)
    private val boldFontVector = AbstractAwtFontRender.buildVectorFontRenderer(font.deriveFont(Font.BOLD))
    private val italicFontVector = AbstractAwtFontRender.buildVectorFontRenderer(font.deriveFont(Font.ITALIC))
    private val boldItalicFontVector = AbstractAwtFontRender.buildVectorFontRenderer(font.deriveFont(Font.BOLD or Font.ITALIC))

    private val defaultFont
        get() = if (RenderSettings.useGlyphFontRenderer) defaultFontGlyph else defaultFontVector
    private val boldFont
        get() = if (RenderSettings.useGlyphFontRenderer) boldFontGlyph else boldFontVector
    private val italicFont
        get() = if (RenderSettings.useGlyphFontRenderer) italicFontGlyph else italicFontVector
    private val boldItalicFont
        get() = if (RenderSettings.useGlyphFontRenderer) boldItalicFontGlyph else boldItalicFontVector

    val height: Int
        get() = defaultFont.height / 2

    val size: Int
        get() = defaultFont.font.size

    init {
        FONT_HEIGHT = height
        fontHeight = height
        FontGC.register(this)
    }

    fun drawString(s: String?, x: Float, y: Float, color: Int) = drawString(s, x, y, color, false)

    override fun drawStringWithShadow(text: String?, x: Float, y: Float, color: Int) = drawString(text, x, y, color, true)

    override fun drawString(text: String?, x: Float, y: Float, color: Int, shadow: Boolean): Int {
        var currentText = text
        val event = TextEvent(currentText)
        KevinClient.eventManager.callEvent(event)
        currentText = event.text ?: return 0

        val currY = y - 3F

        val rainbow = RainbowFontShader.isInUse

        if (shadow) {
            GL20.glUseProgram(0)

            drawText(currentText, x + 1f, currY + 1f, Color(0, 0, 0, 150).rgb, true)
        }

        return drawText(currentText, x, currY, color, false, rainbow)
    }

    fun drawCenteredString(s: String, x: Float, y: Float, color: Int, shadow: Boolean) = drawString(s, x - getStringWidth(s) / 2F, y, color, shadow)

    fun drawCenteredString(s: String, x: Float, y: Float, color: Int) =
        drawStringWithShadow(s, x - getStringWidth(s) / 2F, y, color)

    private fun drawText(text: String?, x: Float, y: Float, color: Int, ignoreColor: Boolean, rainbow: Boolean = false): Int {
        if (text == null)
            return 0
        if (text.isEmpty())
            return x.toInt()

        val rainbowShaderId = RainbowFontShader.programId

        if (rainbow)
            GL20.glUseProgram(rainbowShaderId)

        GL11.glTranslated(x - 1.5, y + 0.5, 0.0)

        defaultFont.preGlHints()

        /*GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GlStateManager.enableTexture2D()*/

        var currentColor = color

        if (currentColor and -0x4000000 == 0)
            currentColor = currentColor or -16777216

        val defaultColor = currentColor

        val alpha: Int = (currentColor shr 24 and 0xff)

        if (text.contains("§")) {
            val parts = text.split("§")

            var currentFont = defaultFont

            var width = 0.0

            // Color code states
            var randomCase = false
            var bold = false
            var italic = false
            var strikeThrough = false
            var underline = false

            parts.forEachIndexed { index, part ->
                if (part.isEmpty())
                    return@forEachIndexed

                if (index == 0) {
                    currentFont.drawString(part, width, 0.0, currentColor)
                    width += currentFont.getStringWidth(part)
                } else {
                    val words = part.substring(1)
                    val type = part[0]

                    when (val colorIndex = getColorIndex(type)) {
                        in 0..15 -> {
                            if (!ignoreColor) {
                                currentColor = ColorUtils.hexColors[colorIndex] or (alpha shl 24)

                                if (rainbow)
                                    GL20.glUseProgram(0)
                            }

                            bold = false
                            italic = false
                            randomCase = false
                            underline = false
                            strikeThrough = false
                        }
                        16 -> randomCase = true
                        17 -> bold = true
                        18 -> strikeThrough = true
                        19 -> underline = true
                        20 -> italic = true
                        21 -> {
                            currentColor = color

                            if (currentColor and -67108864 == 0)
                                currentColor = currentColor or -16777216

                            if (rainbow)
                                GL20.glUseProgram(rainbowShaderId)

                            bold = false
                            italic = false
                            randomCase = false
                            underline = false
                            strikeThrough = false
                        }
                    }

                    currentFont = if (bold && italic)
                        boldItalicFont
                    else if (bold)
                        boldFont
                    else if (italic)
                        italicFont
                    else
                        defaultFont

                    currentFont.drawString(if (randomCase) ColorUtils.randomMagicText(words) else words, width, 0.0, currentColor)

                    if (strikeThrough)
                        RenderUtils.drawLine(
                            width / 2.0 + 1, currentFont.height / 3.0,
                            (width + currentFont.getStringWidth(words)) / 2.0 + 1, currentFont.height / 3.0,
                            fontHeight / 16F
                        )

                    if (underline)
                        RenderUtils.drawLine(
                            width / 2.0 + 1, currentFont.height / 2.0,
                            (width + currentFont.getStringWidth(words)) / 2.0 + 1, currentFont.height / 2.0,
                            fontHeight / 16F
                        )

                    width += currentFont.getStringWidth(words)
                }
            }
        } else {
            // Color code states
            defaultFont.drawString(text, 0.0, 0.0, currentColor)
        }

        defaultFont.postGlHints()
        //GlStateManager.disableBlend()
        GlStateManager.translate(-(x - 1.5), -(y + 0.5), 0.0)
        GlStateManager.resetColor()
        GlStateManager.color(1f, 1f, 1f, 1f)

        return (x + getStringWidth(text)).toInt()
    }

    override fun getColorCode(charCode: Char) =
        ColorUtils.hexColors[getColorIndex(charCode)]

    override fun getStringWidth(text: String?): Int {
        var currentText = text

        val event = TextEvent(currentText)
        KevinClient.eventManager.callEvent(event)
        currentText = event.text ?: return 0

        return if (currentText.contains("§")) {
            val parts = currentText.split("§")

            var currentFont = defaultFont
            var width = 0
            var bold = false
            var italic = false

            parts.forEachIndexed { index, part ->
                if (part.isEmpty())
                    return@forEachIndexed

                if (index == 0) {
                    width += currentFont.getStringWidth(part)
                } else {
                    val words = part.substring(1)
                    val type = part[0]
                    val colorIndex = getColorIndex(type)
                    when {
                        colorIndex < 16 -> {
                            bold = false
                            italic = false
                        }
                        colorIndex == 17 -> bold = true
                        colorIndex == 20 -> italic = true
                        colorIndex == 21 -> {
                            bold = false
                            italic = false
                        }
                    }

                    currentFont = if (bold && italic)
                        boldItalicFont
                    else if (bold)
                        boldFont
                    else if (italic)
                        italicFont
                    else
                        defaultFont

                    width += currentFont.getStringWidth(words)
                }
            }

            width / 2
        } else
            defaultFont.getStringWidth(currentText) / 2
    }

    override fun getCharWidth(character: Char) = getStringWidth(character.toString())

    override fun onResourceManagerReload(resourceManager: IResourceManager) {}

    override fun bindTexture(location: ResourceLocation?) {}

    fun collectGarbage() {
        defaultFontGlyph.collectGarbage()
        defaultFontVector.collectGarbage()
        boldFontGlyph.collectGarbage()
        boldFontVector.collectGarbage()
        italicFontGlyph.collectGarbage()
        italicFontVector.collectGarbage()
        boldItalicFontGlyph.collectGarbage()
        boldItalicFontVector.collectGarbage()
    }

    fun close() {
        defaultFontGlyph.close()
        defaultFontVector.close()
        boldFontGlyph.close()
        boldFontVector.close()
        italicFontGlyph.close()
        italicFontVector.close()
        boldItalicFontGlyph.close()
        boldItalicFontVector.close()
    }

    companion object {
        @JvmStatic
        fun getColorIndex(type: Char): Int {
            return when (type) {
                in '0'..'9' -> type - '0'
                in 'a'..'f' -> type - 'a' + 10
                in 'k'..'o' -> type - 'k' + 16
                'r' -> 21
                else -> -1
            }
        }
    }
}