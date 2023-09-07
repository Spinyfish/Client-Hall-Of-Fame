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
import kevin.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

object MatrixNewNoFall : NoFallMode("MatrixNew") {
    override fun onPacket(event: PacketEvent) {
        if(event.packet is C03PacketPlayer) {
            if(!mc.thePlayer.onGround) {
                if(mc.thePlayer.fallDistance > 2.69f){
                    mc.timer.timerSpeed = 0.3f
                    event.packet.onGround = true
                    mc.thePlayer.fallDistance = 0f
                }
                if(mc.thePlayer.fallDistance > 3.5){
                    mc.timer.timerSpeed = 0.3f
                }else {
                    mc.timer.timerSpeed = 1F
                }
            }
            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.entityBoundingBox.offset(0.0, mc.thePlayer.motionY, 0.0))
                    .isNotEmpty()) {
                if(!event.packet.isOnGround && mc.thePlayer.motionY < -0.6) {
                    event.packet.onGround = true
                }
            }

        }
    }
}