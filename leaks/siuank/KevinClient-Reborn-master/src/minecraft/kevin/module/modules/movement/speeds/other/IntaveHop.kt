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
package kevin.module.modules.movement.speeds.other

import kevin.module.BooleanValue
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.utils.MovementUtils

object IntaveHop: SpeedMode("IntaveHop") { // Rise base
    private val strafeValue = BooleanValue("IntaveStrafe", true)
    private var offGroundTicks = 0
    private var jumped = false

    override fun onPreMotion() {
//        offGroundTicks++
        if (mc.thePlayer.onGround) {
            if (jumped) MovementUtils.strafe((MovementUtils.getBaseMoveSpeed() * 1.2).toFloat()) // ignore it in first jump
            offGroundTicks = 0
            mc.thePlayer.jump()
            jumped = true
        } else if (strafeValue.get()) {
            MovementUtils.strafe()
        }
//        else if (offGroundTicks == 4) { mc.thePlayer.motionY = -0.0784000015258789 }
    }

    override fun onEnable() {
        jumped = !mc.thePlayer!!.onGround
    }
}