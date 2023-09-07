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
package kevin.skin

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage
import java.util.*

class Skin(val name: String,val image: BufferedImage) {
    val resource = ResourceLocation("kevin/skin/${name.lowercase(Locale.getDefault()).replace(" ", "_")}")
    init {
        val mc = Minecraft.getMinecraft()
        mc.addScheduledTask {
            mc.textureManager.loadTexture(resource, DynamicTexture(image))
        }
    }
}