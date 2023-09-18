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
package kevin.module.modules.movement.speeds.verus

import kevin.event.MoveEvent
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils

object VerusYPort : SpeedMode("VerusYPort") {
    override fun onMove(event: MoveEvent) {
        if (MovementUtils.isMoving&&
            !mc.thePlayer.isInWeb&&
            !mc.thePlayer.isInLava&&
            !mc.thePlayer.isInWater&&
            !mc.thePlayer.isOnLadder&&
            mc.thePlayer.ridingEntity == null&&
            !mc.gameSettings.keyBindJump.isKeyDown) {
            mc.gameSettings.keyBindJump.pressed = false
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump()
                mc.thePlayer.motionY = 0.0
                MovementUtils.strafe(0.61F)
                event.y = 0.41999998688698
            }
            MovementUtils.strafe()
        }
    }
}