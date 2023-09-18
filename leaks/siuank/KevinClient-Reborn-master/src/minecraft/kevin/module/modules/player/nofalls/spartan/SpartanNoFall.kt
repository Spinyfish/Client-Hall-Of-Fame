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
package kevin.module.modules.player.nofalls.spartan

import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import kevin.utils.TickTimer
import net.minecraft.network.play.client.C03PacketPlayer

object SpartanNoFall : NoFallMode("Spartan") {
    private val spartanTimer = TickTimer()
    override fun onNoFall(event: UpdateEvent) {
        spartanTimer.update()
        if (mc.thePlayer!!.fallDistance > 1.5 && spartanTimer.hasTimePassed(10)) {
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer!!.posX,
                mc.thePlayer!!.posY + 10, mc.thePlayer!!.posZ, true))
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer!!.posX,
                mc.thePlayer!!.posY - 10, mc.thePlayer!!.posZ, true))
            spartanTimer.reset()
        }
    }
}