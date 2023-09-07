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
package kevin.module.modules.player.nofalls.normal

import kevin.event.UpdateEvent
import kevin.module.FloatValue
import kevin.module.modules.player.nofalls.NoFallMode

object MotionFlagNoFall : NoFallMode("MotionFlag") {
    private val flySpeedValue = FloatValue("${valuePrefix}MotionSpeed", -0.01f, -5f, 5f)
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance > 3) {
            mc.thePlayer.motionY = flySpeedValue.get().toDouble()
        }
    }
}