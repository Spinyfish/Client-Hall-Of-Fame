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
package kevin.module.modules.player.nofalls.matrix

import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object Matrix62xPacketNoFall : NoFallMode("Matrix6.2.X-Packet") {
    override fun onNoFall(event: UpdateEvent) {
        if(mc.thePlayer.onGround) {
            //mc.timer.timerSpeed = 1f
        } else if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3f){
            noFall.wasTimer = true
            mc.timer.timerSpeed = (mc.timer.timerSpeed * if(mc.timer.timerSpeed < 0.6) { 0.25f } else { 0.5f }).coerceAtLeast(0.2f)
            mc.netHandler.addToSendQueue(C03PacketPlayer(false))
            mc.netHandler.addToSendQueue(C03PacketPlayer(false))
            mc.netHandler.addToSendQueue(C03PacketPlayer(false))
            mc.netHandler.addToSendQueue(C03PacketPlayer(false))
            mc.netHandler.addToSendQueue(C03PacketPlayer(false))
            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
            mc.netHandler.addToSendQueue(C03PacketPlayer(false))
            mc.netHandler.addToSendQueue(C03PacketPlayer(false))
            mc.thePlayer.fallDistance = 0f
        }
    }
}