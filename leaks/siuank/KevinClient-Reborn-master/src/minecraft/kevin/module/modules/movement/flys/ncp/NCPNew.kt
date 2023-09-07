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

import kevin.event.MoveEvent
import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MovementUtils
import kevin.utils.PacketUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.MathHelper

object NCPNew : FlyMode("NCPNew") {
    override fun onEnable() {
        mc.thePlayer.motionX = .0
        mc.thePlayer.motionY = .0
        mc.thePlayer.motionZ = .0
    }

    override fun onDisable() {
        mc.thePlayer.speedInAir = .02F
    }

    override fun onMove(event: MoveEvent) {
        mc.thePlayer.onGround = true
        val direction = MovementUtils.direction.toFloat()
        val x = -MathHelper.sin(direction) * 0.2873
        val z = MathHelper.cos(direction) * 0.2873
        PacketUtils.sendPacketNoEvent(
            C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX + x,
                mc.thePlayer.posY,
                mc.thePlayer.posZ + z,
                false
            )
        )
        PacketUtils.sendPacketNoEvent(
            C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX + x,
                mc.thePlayer.posY - 999,
                mc.thePlayer.posZ + z,
                true
            )
        )
        event.x += x * 0.7f
        event.z += z * 0.7f
        event.y = 0.0
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer) event.cancelEvent()
    }
}