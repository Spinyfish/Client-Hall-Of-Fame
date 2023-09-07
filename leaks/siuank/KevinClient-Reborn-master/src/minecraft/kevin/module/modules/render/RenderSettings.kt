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

import kevin.main.KevinClient
import kevin.module.FloatValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory

object RenderSettings : Module("RenderSettings", "Some render settings.", category = ModuleCategory.RENDER) {

    private val fontRendererValue = ListValue("FontRenderer", arrayOf("Glyph", "Vector"), "Glyph")
    val fontEpsilonValue = FloatValue("FontVectorEpsilon", 0.5f, 0f, 1.5f)
    private val getFontRender: String
    get() = if (KevinClient.isStarting) "Glyph" else fontRendererValue.get()
    val useGlyphFontRenderer: Boolean
    get() = getFontRender == "Glyph"

    override fun onEnable() {
        this.state = false
    }
}