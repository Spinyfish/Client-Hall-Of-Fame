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
package kevin.font.renderer.renderers.vector

import kevin.font.renderer.AbstractAwtFontRender
import kevin.module.modules.render.RenderSettings
import kevin.utils.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Font
import java.awt.font.FontRenderContext
import java.awt.geom.AffineTransform

class VectorFontRenderer(font: Font) : AbstractAwtFontRender(font) {

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

        val cached = if (!cachedChars.containsKey(char)) {
            val list = GL11.glGenLists(1)
            // list is faster than buffer
            GL11.glNewList(list, GL11.GL_COMPILE_AND_EXECUTE)
            RenderUtils.directDrawAWTShape(
                (if (canDisplay) font else if (canDisplay2) replaceFont else replaceFont2).createGlyphVector(
                    FontRenderContext(AffineTransform(), true, false), char)
                    .getOutline(0f,
                        (if (canDisplay) fontMetrics else if (canDisplay2) replaceFontMetrics else replaceFontMetrics2).ascent.toFloat()
                    ), RenderSettings.fontEpsilonValue.get().toDouble()
            )
            GL11.glEndList()

            CachedVectorFont(list, (if (canDisplay) fontMetrics else if (canDisplay2) replaceFontMetrics else replaceFontMetrics2).stringWidth(char)).also { cachedChars[char] = it }
        } else {
            cachedChars[char]!! as CachedVectorFont
        }

        val list = cached.list
        GL11.glCallList(list)
        GL11.glCallList(list)
        cached.lastUsage = System.currentTimeMillis()

        return cached.width
    }

    override fun preGlHints() {
        GlStateManager.enableColorMaterial()
        GlStateManager.enableAlpha()
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        RenderUtils.clearCaps("FONT")
        RenderUtils.enableGlCap(GL11.GL_POLYGON_SMOOTH, "FONT")
//        RenderUtils.disableGlCap(GL11.GL_DEPTH_TEST)
//        GL11.glDepthMask(false)
        RenderUtils.disableGlCap(GL11.GL_CULL_FACE, "FONT")
    }

    override fun postGlHints() {
        RenderUtils.resetCaps("FONT")
//        GL11.glEnable(GL11.GL_DEPTH_TEST)
//        GL11.glDepthMask(true)
        GlStateManager.disableBlend()
        GlStateManager.enableTexture2D()
    }
}