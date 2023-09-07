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
package kevin.module.modules.movement

import kevin.event.AttackEvent
import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.IntegerValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MSTimer
import kevin.utils.TimeUtils
import net.minecraft.entity.EntityLivingBase

class KeepSprint : Module("KeepSprint","Keep sprint when you attack entity.",category = ModuleCategory.MOVEMENT) {
    private val maxDelay: IntegerValue = object : IntegerValue("MaxDelay", 200, 0, 500) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelay.get()
            if (i > newValue) set(i)

            delay = TimeUtils.randomDelay(minDelay.get(), this.get())
        }
    }

    private val minDelay: IntegerValue = object : IntegerValue("MinDelay", 50, 0, 500) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelay.get()
            if (i < newValue) set(i)

            delay = TimeUtils.randomDelay(this.get(), maxDelay.get())
        }
    }

    var delay = 0L
    var stopSprint = false
    val stopTimer = MSTimer()
    private var isHit = false
    private val attackTimer = MSTimer()

    override fun onEnable() {
        isHit = false
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase && !isHit) {
            isHit = true
            attackTimer.reset()
            delay = TimeUtils.randomDelay(minDelay.get(), maxDelay.get())
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (isHit && attackTimer.hasTimePassed(delay/2)) {
            isHit = false
            mc.thePlayer.isSprinting = false
            stopSprint = true
            stopTimer.reset()
        }
    }
}