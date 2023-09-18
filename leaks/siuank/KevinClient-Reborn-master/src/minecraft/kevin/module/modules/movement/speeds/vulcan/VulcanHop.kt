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
package kevin.module.modules.movement.speeds.vulcan

import kevin.event.UpdateEvent
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils
import net.minecraft.client.settings.GameSettings
import kotlin.math.abs

object VulcanHop : SpeedMode("VulcanHop") { // from FDP

    private var wasTimer = false

    override fun onUpdate(event: UpdateEvent) {
        if (wasTimer) {
            mc.timer.timerSpeed = 1.00f
            wasTimer = false
        }
        if (abs(mc.thePlayer.movementInput.moveStrafe) < 0.1f) {
            mc.thePlayer.jumpMovementFactor = 0.026499f
        }else {
            mc.thePlayer.jumpMovementFactor = 0.0244f
        }
        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)

        if (MovementUtils.speed < 0.215f && !mc.thePlayer.onGround) {
            MovementUtils.strafe(0.215f)
        }
        if (mc.thePlayer.onGround && MovementUtils.isMoving) {
            mc.gameSettings.keyBindJump.pressed = false
            mc.thePlayer.jump()
            if (!mc.thePlayer.isAirBorne) {
                return //Prevent flag with Fly
            }
            mc.timer.timerSpeed = 1.25f
            wasTimer = true
            MovementUtils.strafe()
            if(MovementUtils.speed < 0.5f) {
                MovementUtils.strafe(0.4849f)
            }
        }else if (!MovementUtils.isMoving) {
            mc.timer.timerSpeed = 1.00f
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.00f
        wasTimer = false
    }
}