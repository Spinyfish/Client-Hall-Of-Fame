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
import kevin.utils.BlockUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.BlockPos

object AAC5014NoFall : NoFallMode("AAC5.0.14") {
    private var aac5Check = false
    private var aac5doFlag = false
    private var aac5Timer = 0
    override fun onEnable() {
        aac5Check = false
        aac5Timer = 0
        aac5doFlag = false
    }
    override fun onNoFall(event: UpdateEvent) {
        var offsetYs = 0.0
        aac5Check = false
        while (mc.thePlayer.motionY - 1.5 < offsetYs) {
            val blockPos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + offsetYs, mc.thePlayer.posZ)
            val block = BlockUtils.getBlock(blockPos)
            val axisAlignedBB = block!!.getCollisionBoundingBox(mc.theWorld, blockPos, BlockUtils.getState(blockPos))
            if (axisAlignedBB != null) {
                offsetYs = -999.9
                aac5Check = true
            }
            offsetYs -= 0.5
        }
        if (mc.thePlayer.onGround) {
            mc.thePlayer.fallDistance = -2f
            aac5Check = false
        }
        if (aac5Timer > 0) {
            aac5Timer -= 1
        }
        if (aac5Check && mc.thePlayer.fallDistance > 2.5 && !mc.thePlayer.onGround) {
            aac5doFlag = true
            aac5Timer = 18
        } else {
            if (aac5Timer < 2) aac5doFlag = false
        }
        if (aac5doFlag) {
            if (mc.thePlayer.onGround) {
                mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ, true))
            } else {
                mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ, true))
            }
        }
    }
}