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

import kevin.event.PacketEvent
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S12PacketEntityVelocity

object AAC44XFlagNoFall : NoFallMode("AAC4.4.X-Flag") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is S12PacketEntityVelocity && mc.thePlayer.fallDistance > 1.8) {
            event.packet.motionY = (event.packet.motionY * -0.1).toInt()
        }
        if(event.packet is C03PacketPlayer) {
            if (mc.thePlayer.fallDistance > 1.6) {
                event.packet.onGround = true
            }
        }
    }
}