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
package kevin.module.modules.player

import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory

class Reach : Module("Reach","Increases your reach.", category = ModuleCategory.PLAYER) {
    val combatReachValue = FloatValue("CombatReach", 3.0f, 3f, 7f)
    val buildReachValue = FloatValue("BuildReach", 4.5f, 4.5f, 7f)

    val maxRange: Float
        get() {
            val combatRange = combatReachValue.get()
            val buildRange = buildReachValue.get()

            return if (combatRange > buildRange) combatRange else buildRange
        }

    override val tag: String
        get() = combatReachValue.get().toString()
}