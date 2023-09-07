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

//import kevin.event.UpdateState

import kevin.event.EventTarget
import kevin.event.MoveEvent
import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.ChatUtils
import kevin.utils.MSTimer
import net.minecraft.item.ItemBucketMilk
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S19PacketEntityStatus
import java.util.*

class FastUse : Module("FastUse", "Allows you to use items faster.", category = ModuleCategory.PLAYER) {
    private val modeValue = ListValue("Mode", arrayOf("Instant", "NCP", "AAC", "Matrix", "Semi", "Custom"), "NCP")

    private val noMoveValue = BooleanValue("NoMove", false)

    private val delayValue = IntegerValue("CustomDelay", 0, 0, 300)
    private val customSpeedValue = IntegerValue("CustomSpeed", 2, 1, 35)
    private val customTimer = FloatValue("CustomTimer", 1.1f, 0.5f, 2f)

    private val debug = BooleanValue("Debug", false)

    private val msTimer = MSTimer()
    private var usedTimer = false
    private var debugTimer = 0L
    private var first = true

    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        //if (event.eventState == UpdateState.OnUpdate) return

        val thePlayer = mc.thePlayer ?: return

        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }

        if (!thePlayer.isUsingItem) {
            msTimer.reset()
            first = true
            return
        }

        val usingItem = thePlayer.itemInUse!!.item

        if ((usingItem)is ItemFood || (usingItem)is ItemBucketMilk || (usingItem)is ItemPotion) {
            if (first) {
                first = false
                debugTimer = System.currentTimeMillis()
            }
            when (modeValue.get().lowercase(Locale.getDefault())) {
                "instant" -> {
                    repeat(35) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(thePlayer.onGround))
                    }

                    mc.playerController.onStoppedUsingItem(thePlayer)
                }

                "ncp" -> if (thePlayer.itemInUseDuration > 14) {
                    repeat(20) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(thePlayer.onGround))
                    }

                    mc.playerController.onStoppedUsingItem(thePlayer)
                }

                "aac" -> {
                    mc.timer.timerSpeed = 1.22F
                    usedTimer = true
                }

                "matrix" -> {
                    mc.timer.timerSpeed = 0.5F
                    usedTimer = true
                    mc.netHandler.addToSendQueue(C03PacketPlayer(thePlayer.onGround))
                }

                "semi" -> {
                    mc.timer.timerSpeed = if (usedTimer) mc.timer.timerSpeed + 0.002F else return
                    usedTimer = true
                }

                "custom" -> {
                    mc.timer.timerSpeed = customTimer.get()
                    usedTimer = true

                    if (!msTimer.hasTimePassed(delayValue.get().toLong()))
                        return

                    repeat(customSpeedValue.get()) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(thePlayer.onGround))
                    }

                    msTimer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent?) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || event == null)
            return
        if (!state || !thePlayer.isUsingItem || !noMoveValue.get())
            return

        val usingItem = thePlayer.itemInUse!!.item

        if ((usingItem)is ItemFood || (usingItem)is ItemBucketMilk || (usingItem)is ItemPotion)
            event.zero()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (debug.get() && packet is S19PacketEntityStatus && packet.opCode.toInt() == 9 && packet.getEntity(mc.theWorld) == mc.thePlayer) {
            ChatUtils.messageWithStart("§7[§bFastUse§7] §aUsed ${System.currentTimeMillis()-debugTimer} MS.")
            debugTimer = System.currentTimeMillis()
        }
    }

    override fun onDisable() {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
    }

    override val tag: String
        get() = modeValue.get()
}