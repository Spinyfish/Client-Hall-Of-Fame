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

import kevin.event.ClickBlockEvent
import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MSTimer
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.BlockPos

class AutoTool : Module(name = "AutoTool", description = "Automatically selects the best tool in your inventory to mine a block.", category = ModuleCategory.PLAYER) {
    val silentValue = BooleanValue("Silent", true)
    var nowSlot = 0
    private val switchTimer = MSTimer()
    private var needReset = false

    @EventTarget
    fun onClick(event: ClickBlockEvent) {
        switchSlot(event.clickedBlock ?: return)
    }
    @EventTarget fun onUpdate(event: UpdateEvent){
        if (needReset) {
            if (switchTimer.hasTimePassed(100)){
                needReset = false
                if (nowSlot!=mc.thePlayer!!.inventory.currentItem){
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
                    nowSlot = mc.thePlayer!!.inventory.currentItem
                }
            }
        }
    }
    @EventTarget fun onPacket(event: PacketEvent){
        if (event.packet is C09PacketHeldItemChange) {
            nowSlot = event.packet.slotId
        }
    }

    fun switchSlot(blockPos: BlockPos) {
        var bestSpeed = 1F
        var bestSlot = -1

        val blockState = mc.theWorld!!.getBlockState(blockPos).block

        for (i in 0..8) {
            val item = mc.thePlayer!!.inventory.getStackInSlot(i) ?: continue
            val speed = item.getStrVsBlock(blockState)

            if (speed > bestSpeed) {
                bestSpeed = speed
                bestSlot = i
            }
        }
        if (bestSlot != -1 && bestSlot != nowSlot) {
            if (mc.thePlayer!!.inventory.getStackInSlot(nowSlot)!=null&&mc.thePlayer!!.inventory.getStackInSlot(nowSlot).getStrVsBlock(blockState) >= bestSpeed) return
            if (silentValue.get()) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(bestSlot))
                nowSlot = bestSlot
            } else {
                mc.thePlayer!!.inventory.currentItem = bestSlot
            }
        }
        switchTimer.reset()
        needReset = true
    }
}