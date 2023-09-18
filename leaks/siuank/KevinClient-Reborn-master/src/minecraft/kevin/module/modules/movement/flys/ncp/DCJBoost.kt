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
package kevin.module.modules.movement.flys.ncp

import kevin.utils.MovementUtils.strafe
import kevin.module.modules.movement.flys.FlyMode
import kevin.event.UpdateEvent
import kevin.utils.MinecraftInstance
import kevin.utils.MovementUtils

class DCJBoost : FlyMode("DCJBoost") {
    override fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionZ = 0.0
        mc.thePlayer.capabilities.isFlying = false
        strafe(0.9f * (mc.thePlayer.ticksExisted % 40 / 120f + 0.7666f))
    }
}