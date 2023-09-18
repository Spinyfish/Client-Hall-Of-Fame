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
package kevin.module.modules.combat

import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory

class HitBox : Module("HitBox", "Makes hitboxes of targets bigger.", category = ModuleCategory.COMBAT) {
    val sizeValue = FloatValue("Size", 0.4F, 0F, 1F)
    override val tag: String
        get() = "Size:${sizeValue.get()}"
}