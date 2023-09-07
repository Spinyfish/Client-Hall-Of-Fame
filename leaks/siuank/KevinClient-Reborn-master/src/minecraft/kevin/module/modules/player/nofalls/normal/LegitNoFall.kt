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
import kevin.module.modules.player.nofalls.NoFallMode
import kevin.utils.FallingPlayer

object LegitNoFall: NoFallMode("Legit") {
    var working = false
    override fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return
        if (mc.thePlayer.fallDistance > 3) {
            val fallingPlayer = FallingPlayer(
                thePlayer.posX,
                thePlayer.posY,
                thePlayer.posZ,
                thePlayer.motionX,
                thePlayer.motionY,
                thePlayer.motionZ,
                thePlayer.rotationYaw,
                thePlayer.moveStrafing,
                thePlayer.moveForward
            )

            if (fallingPlayer.findCollision(1) != null) {
                working = true
            }
        }
        if (working && mc.thePlayer.onGround) {
            mc.gameSettings.keyBindSneak.pressed = false
            working = false
        }
        if (working) mc.gameSettings.keyBindSneak.pressed = true
    }
}