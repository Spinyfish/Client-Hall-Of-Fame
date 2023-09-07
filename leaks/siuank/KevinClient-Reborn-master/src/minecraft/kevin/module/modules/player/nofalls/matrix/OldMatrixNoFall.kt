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

import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook

object OldMatrixNoFall : NoFallMode("OldMatrix") {
    private var isDmgFalling = false
    private var matrixFlagWait = 0
    override fun onEnable() {
        isDmgFalling = false
        matrixFlagWait = 0
    }

    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance > 3) {
            isDmgFalling = true
        }
    }

    override fun onPacket(event: PacketEvent) {
        if(event.packet is S08PacketPlayerPosLook && matrixFlagWait > 0) {
            matrixFlagWait = 0
            mc.timer.timerSpeed = 1.00f
            event.cancelEvent()
        }
        if(event.packet is C03PacketPlayer && isDmgFalling) {
            if (event.packet.onGround && mc.thePlayer.onGround) {
                matrixFlagWait = 2
                isDmgFalling = false
                event.cancelEvent()
                mc.thePlayer.onGround = false
                mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y - 256, event.packet.z, false))
                mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, (-10).toDouble() , event.packet.z, true))
                mc.timer.timerSpeed = 0.18f
            }
        }
    }
}