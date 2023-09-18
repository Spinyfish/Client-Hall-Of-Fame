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
package kevin.module.modules.player

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory
import net.minecraft.network.play.client.C03PacketPlayer

class NoC03 : Module("NoC03", "Cancel C03 packets", category=ModuleCategory.PLAYER) {
    private val packetACBypass by BooleanValue("PacketBaseACBypass", true)
    private var lastGround = false
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer) {
            if (!(packet.isMoving || packet.rotating || mc.thePlayer.isUsingItem)) {
                if (lastGround == packet.onGround || !packetACBypass)
                    event.cancelEvent()
            }
            lastGround = packet.onGround
        }
    }
}