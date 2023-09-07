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
package kevin.font.renderer.renderers.glyph

import kevin.font.renderer.AbstractAwtFontRender
import kevin.main.KevinClient
import kevin.utils.RenderUtils
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class GlyphFontRenderer(font: Font) : AbstractAwtFontRender(font) {
    private fun renderCharImage(char: String, canDisplay: Boolean, canDisplay2: Boolean): CachedGlyphFont {
        val charWidth = if (canDisplay) fontMetrics.stringWidth(char) else if (canDisplay2) replaceFontMetrics.stringWidth(char) else replaceFontMetrics2.stringWidth(char)
        val image: BufferedImage = try {
            BufferedImage(charWidth, if (canDisplay) fontHeight else if (canDisplay2) replaceFontHeight else replaceFontHeight2, BufferedImage.TYPE_INT_ARGB)
        }catch (e:Exception){
            e.printStackTrace()
            BufferedImage(fontMetrics.stringWidth("?"), fontHeight, BufferedImage.TYPE_INT_ARGB)
        }
        val graphics = image.createGraphics()

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        graphics.font = if (canDisplay) font else if (canDisplay2) replaceFont else replaceFont2
        graphics.paint = Color.WHITE
        graphics.drawString(char, 0, if (canDisplay) fontMetrics.ascent else if (canDisplay2) replaceFontMetrics.ascent else replaceFontMetrics2.ascent)
        graphics.dispose()

        return CachedGlyphFont(RenderUtils.loadGlTexture(image), charWidth)
    }

    private fun renderAndCacheTexture(char: String, canDisplay: Boolean, canDisplay2: Boolean): CachedGlyphFont {
        val cached = renderCharImage(char, canDisplay, canDisplay2)
        cachedChars[char] = cached
        return cached
    }

    override fun drawChar(char: String): Int {
        val codePoint = char.codePointAt(0)
        val canDisplay = font.canDisplay(codePoint)
        val canDisplay2 = replaceFont.canDisplay(codePoint)
        /*if (!canDisplay && !replaceFont.canDisplay(codePoint)) {
            val scale = 0.25
            val reverse = 1 / scale
            GL11.glScaled(reverse, reverse, reverse)
            val i = KevinClient.fontManager.minecraftFont.drawStringNoColor(char, 1f, 2f)
            GL11.glScaled(scale, scale, scale)
            return i
        }*/

        val cached = if (cachedChars.containsKey(char)) {
            val cached = cachedChars[char]!! as CachedGlyphFont
            cached.lastUsage = System.currentTimeMillis()
            cached
        } else {
            renderAndCacheTexture(char, canDisplay, canDisplay2)
        }

        val originalTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, cached.tex)
        GL11.glBegin(GL11.GL_QUADS)

        GL11.glTexCoord2d(1.0, 0.0)
        GL11.glVertex2d(cached.width.toDouble(), 0.0)
        GL11.glTexCoord2d(0.0, 0.0)
        GL11.glVertex2d(0.0, 0.0)
        GL11.glTexCoord2d(0.0, 1.0)
        GL11.glVertex2d(0.0, (if (canDisplay) fontHeight else if (canDisplay2) replaceFontHeight else replaceFontHeight2).toDouble())
        GL11.glTexCoord2d(1.0, 1.0)
        GL11.glVertex2d(cached.width.toDouble(), (if (canDisplay) fontHeight else if (canDisplay2) replaceFontHeight else replaceFontHeight2).toDouble())

        GL11.glEnd()

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, originalTex)

        return cached.width
    }

    override fun preGlHints() {
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        RenderUtils.clearCaps()
        RenderUtils.disableGlCap(GL11.GL_DEPTH_TEST)
    }

    override fun postGlHints() {
        RenderUtils.resetCaps()
        GL11.glDisable(GL11.GL_BLEND)
    }
}