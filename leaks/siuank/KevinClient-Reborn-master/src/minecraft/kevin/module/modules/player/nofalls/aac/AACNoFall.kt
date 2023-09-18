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
package kevin.module.modules.player.nofalls.aac

import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object AACNoFall : NoFallMode("AAC") {
    private var currentState = 0
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer!!.fallDistance > 2f) {
            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
            currentState = 2
        } else if (currentState == 2 && mc.thePlayer!!.fallDistance < 2) {
            mc.thePlayer!!.motionY = 0.1
            currentState = 3
            return
        }
        when (currentState) {
            3 -> {
                mc.thePlayer!!.motionY = 0.1
                currentState = 4
            }
            4 -> {
                mc.thePlayer!!.motionY = 0.1
                currentState = 5
            }
            5 -> {
                mc.thePlayer!!.motionY = 0.1
                currentState = 1
            }
        }
    }
}