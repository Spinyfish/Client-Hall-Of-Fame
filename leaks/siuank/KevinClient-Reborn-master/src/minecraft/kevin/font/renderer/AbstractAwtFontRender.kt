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
package kevin.font.renderer

import kevin.font.FontGC
import kevin.font.renderer.renderers.glyph.GlyphFontRenderer
import kevin.font.renderer.renderers.vector.VectorFontRenderer
import kevin.main.KevinClient
import kevin.utils.RenderUtils
import kevin.utils.StringUtils
import kevin.utils.fixToChar
import org.lwjgl.opengl.GL11
import java.awt.Canvas
import java.awt.Font
import java.awt.FontMetrics

abstract class AbstractAwtFontRender(val font: Font) {
    protected val replaceFont = KevinClient.fontManager.getFont("misans.ttf", font.size)
    protected val replaceFontMetrics: FontMetrics = Canvas().getFontMetrics(replaceFont)
    protected val replaceFont2 = KevinClient.fontManager.getFont("unifont-14.0.04.ttf", font.size)
    protected val replaceFontMetrics2: FontMetrics = Canvas().getFontMetrics(replaceFont2)
    protected val fontMetrics: FontMetrics = Canvas().getFontMetrics(font)
    protected open val fontHeight = if (fontMetrics.height <= 0) { font.size } else { fontMetrics.height + 3 }
    protected open val replaceFontHeight = if (replaceFontMetrics.height <= 0) { replaceFont.size } else { replaceFontMetrics.height + 3 }
    protected open val replaceFontHeight2 = if (replaceFontMetrics2.height <= 0) { replaceFont2.size } else { replaceFontMetrics2.height + 3 }
    open val height: Int
        get() = (fontHeight - 8) / 2

    protected val cachedChars = mutableMapOf<String, AbstractCachedFont>()

    /**
     * Allows you to draw a string with the target font
     *
     * @param text  to render
     * @param x     location for target position
     * @param y     location for target position
     * @param color of the text
     */
    open fun drawString(text: String, x: Double, y: Double, color: Int) {
        val scale = 0.25

        GL11.glPushMatrix()
        GL11.glScaled(scale, scale, scale)
        GL11.glTranslated(x * 2F, y * 2.0 - 2.0, 0.0)
        RenderUtils.glColor(color)

        /*text.forEach { // this is faster than toCharArray()
            GL11.glTranslatef(drawChar(it.toString()).toFloat(), 0f, 0f)
        }*/

        text.fixToChar.forEach {
            GL11.glTranslatef(drawChar(it).toFloat(), 0f, 0f)
        }

        GL11.glPopMatrix()
    }

    /**
     * Draw char from texture to display
     *
     * @param char target font char to render
     * @param x        target position x to render
     * @param y        target position y to render
     */
    abstract fun drawChar(char: String): Int

    /**
     * Get the width of a string
     */
    open fun getStringWidth(text: String): Int {
        var l = 0
        val stream = text.codePoints()
        for (codePoint in stream)
            l += if (font.canDisplay(codePoint))
                fontMetrics.charWidth(codePoint)
            else if (replaceFont.canDisplay(codePoint))
                replaceFontMetrics.charWidth(codePoint)
            else
                replaceFontMetrics2.charWidth(codePoint)
                /*{
                val chars = Character.toChars(codePoint)
                var str = ""
                chars.forEach { str += it }
                KevinClient.fontManager.minecraftFont.getStringWidth(str) * 4
            }*/
        stream.close()
        return l / 2
    }// = fontMetrics.stringWidth(text) / 2

//    /**
//     * Get the width of a char
//     */
//    open fun getCharWidth(char: String) = fontMetrics.stringWidth(char) / 2

    /**
     * prepare gl hints before render
     */
    abstract fun preGlHints()

    /**
     * prepare gl hints after render
     */
    abstract fun postGlHints()

    /**
     * collect useless garbage to save memory
     */
    open fun collectGarbage() {
        val currentTime = System.currentTimeMillis()

        cachedChars.filter { currentTime - it.value.lastUsage > FontGC.CACHED_FONT_REMOVAL_TIME }.forEach {
            it.value.finalize()

            cachedChars.remove(it.key)
        }
    }

    /**
     * delete all cache
     */
    open fun close() {
        cachedChars.forEach { (_, cachedFont) -> cachedFont.finalize() }
        cachedChars.clear()
    }

    companion object {
        fun buildGlyphFontRenderer(font: Font): AbstractAwtFontRender {
            return GlyphFontRenderer(font)
        }
        fun buildVectorFontRenderer(font: Font): AbstractAwtFontRender {
            return VectorFontRenderer(font)
        }
    }
}