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
package kevin.module.modules.movement.speeds.aac

import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.modules.movement.Strafe
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils

object AAC5Fast : SpeedMode("AAC5Fast") {
    override fun onUpdate(event: UpdateEvent){
        if (!MovementUtils.isMoving) return
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isOnLadder || mc.thePlayer.isInWeb) return
        if (mc.thePlayer.onGround) {
            val strafe = KevinClient.moduleManager.getModule(Strafe::class.java)
            if (strafe.state && strafe.allDirectionsJumpValue.get()) {
                val yaw = mc.thePlayer.rotationYaw
                mc.thePlayer.rotationYaw = strafe.getMoveYaw()
                mc.thePlayer.jump()
                mc.thePlayer.rotationYaw = yaw
            } else {
                mc.thePlayer.jump()
            }
            mc.thePlayer.speedInAir = 0.0201F
            mc.timer.timerSpeed = 0.94F
        }
        if (mc.thePlayer.fallDistance > 0.7 && mc.thePlayer.fallDistance < 1.3) {
            mc.thePlayer.speedInAir = 0.02F
            mc.timer.timerSpeed = 1.8F
        }
    }
}