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
package kevin.module.modules.player.nofalls.vulcan

import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.modules.player.nofalls.NoFallMode
import kevin.utils.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer
import kotlin.math.roundToInt

object VulcanNoFall : NoFallMode("Vulcan") {
    private var vulCanNoFall = false
    private var vulCantNoFall = false
    private var nextSpoof = false
    private var doSpoof = false
    override fun onEnable() {
        vulCanNoFall = false
        vulCantNoFall = false
        nextSpoof = false
        doSpoof = false
    }
    override fun onNoFall(event: UpdateEvent) {
        if(!vulCanNoFall && mc.thePlayer.fallDistance > 3.25) {
            vulCanNoFall = true
        }
        if(vulCanNoFall && mc.thePlayer.onGround && vulCantNoFall) {
            vulCantNoFall = false
        }
        if(vulCantNoFall) return
        if(nextSpoof) {
            mc.thePlayer.motionY = -0.1
            mc.thePlayer.fallDistance = -0.1f
            MovementUtils.strafe(0.3f)
            nextSpoof = false
        }
        if(mc.thePlayer.fallDistance > 3.0f) {
            mc.thePlayer.fallDistance = 0.0f
            doSpoof = true
            nextSpoof = true
        }
    }
    override fun onPacket(event: PacketEvent) {
        if(event.packet is C03PacketPlayer && doSpoof) {
            event.packet.onGround = true
            doSpoof = false
            event.packet.y = (mc.thePlayer.posY * 2).roundToInt().toDouble() / 2
            mc.thePlayer.setPosition(mc.thePlayer.posX, event.packet.y, mc.thePlayer.posZ)
        }
    }
}