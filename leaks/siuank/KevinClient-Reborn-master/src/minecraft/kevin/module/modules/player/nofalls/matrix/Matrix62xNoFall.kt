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

object Matrix62xNoFall : NoFallMode("Matrix6.2.X") {
    private var matrixCanSpoof = false
    private var matrixFallTicks = 0
    private var matrixIsFall = false
    private var matrixLastMotionY = 0.0
    override fun onEnable() {
        matrixCanSpoof = false
        matrixFallTicks = 0
        matrixIsFall = false
        matrixLastMotionY = 0.0
    }
    override fun onNoFall(event: UpdateEvent) {
        if(matrixIsFall) {
            mc.thePlayer.motionX=0.0
            mc.thePlayer.jumpMovementFactor=0f
            mc.thePlayer.motionZ=0.0
            if(mc.thePlayer.onGround) matrixIsFall = false
        }
        if(mc.thePlayer.fallDistance-mc.thePlayer.motionY>3) {
            matrixIsFall = true
            if(matrixFallTicks==0) matrixLastMotionY=mc.thePlayer.motionY
            mc.thePlayer.motionY=0.0
            mc.thePlayer.motionX=0.0
            mc.thePlayer.jumpMovementFactor=0f
            mc.thePlayer.motionZ=0.0
            mc.thePlayer.fallDistance=3.2f
            if(matrixFallTicks in 8..9) matrixCanSpoof=true
            matrixFallTicks++
        }
        if(matrixFallTicks>12 && !mc.thePlayer.onGround) {
            mc.thePlayer.motionY=matrixLastMotionY
            mc.thePlayer.fallDistance = 0f
            matrixFallTicks=0
            matrixCanSpoof=false
        }
    }

    override fun onPacket(event: PacketEvent) {
        if(event.packet is C03PacketPlayer && matrixCanSpoof) {
            event.packet.onGround = true
            matrixCanSpoof = false
        }
    }
}