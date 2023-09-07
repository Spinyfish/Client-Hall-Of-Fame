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
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.RenderUtils
import net.minecraft.entity.item.EntityTNTPrimed
import java.awt.Color

class TNTESP : Module("TNTESP","Allows you to see ignited TNT blocks through walls.", category = ModuleCategory.RENDER) {
    @EventTarget
    fun onRender3D(event : Render3DEvent) {
        mc.theWorld!!.loadedEntityList.filter{it is EntityTNTPrimed}.forEach { RenderUtils.drawEntityBox(it, Color.RED, false) }
    }
}