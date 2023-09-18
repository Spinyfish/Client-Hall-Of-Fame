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
package kevin.module.modules.movement.speeds.matrix

import kevin.event.UpdateEvent
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils

object MatrixNew : SpeedMode("MatrixNew") { //from FDP
    override fun onUpdate(event: UpdateEvent) {
        mc.timer.timerSpeed = 1.0f

        if (!MovementUtils.isMoving || mc.thePlayer.isInWater || mc.thePlayer.isInLava ||
            mc.thePlayer.isOnLadder || mc.thePlayer.isRiding) return

        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump()
            mc.timer.timerSpeed = 0.9f
        } else {
            if (mc.thePlayer.fallDistance <= 0.1) {
                mc.timer.timerSpeed = 1.5f
            } else if (mc.thePlayer.fallDistance < 1.3) {
                mc.timer.timerSpeed = 0.7f
            } else {
                mc.timer.timerSpeed = 1.0f
            }
        }
    }
}