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
package kevin.module.modules.world

import kevin.event.*
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.ChatUtils
import kevin.utils.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer
import kotlin.math.pow

class Timer : Module("Timer", "Changes the speed of the entire game.", category = ModuleCategory.WORLD) {
    private val speedValue = FloatValue("Speed", 2F, 0.1F, 30F)
    private val balanceValue by BooleanValue("BalanceTimer", false)
    private val balanceWaitTimer by FloatValue("BalanceWaitTimer", 0.05f, 0.05f, 0.99f)
    private val onMoveValue = BooleanValue("OnlyOnMove", true)
    private val balanceDebug by BooleanValue("BalanceDebug", false)
    //private val autoDisable = BooleanValue("AutoDisable",true)
    private var balance = 0L
    private var balanceState = false
    private var lastSent = System.currentTimeMillis()

    override fun onDisable() {
        if (mc.thePlayer == null)
            return
        mc.timer.timerSpeed = 1F
    }

    override fun onEnable() {
        if (balanceValue) {
            balanceState = true
            balance = 0
            lastSent = System.currentTimeMillis()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (balanceState) {
            if (mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ <= 0.0005) {
                mc.timer.timerSpeed = balanceWaitTimer
                return
            } else if (balance < -50) {
                balanceState = false
            }
        }
        if((MovementUtils.isMoving || !onMoveValue.get()) && (!balanceState || !balanceValue)) {
            mc.timer.timerSpeed = speedValue.get()
            return
        }
        mc.timer.timerSpeed = 1F
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (!balanceValue) return
        if (event.packet is C03PacketPlayer && !event.isCancelled) {
            balance += 50
            if (balance > 0) {
                balanceState = true
            }
            val time = System.currentTimeMillis()
            balance -= time - lastSent
            lastSent = time
        }
    }
/**
    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (event.worldClient != null) return
        if (!autoDisable.get()) return
        this.toggle(false)
    }
**/
    override val tag: String
        get() = if (balanceValue && balanceDebug) "Balance:$balance" else "Speed:${speedValue.get()}"
}