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
package kevin.module.modules.combat

import kevin.event.AttackEvent
import kevin.event.EventTarget
import kevin.event.StrafeEvent
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.TickTimer
import kevin.utils.getDistanceToEntityBox
import net.minecraft.client.settings.GameSettings
import net.minecraft.entity.player.EntityPlayer

class KeepRange: Module("KeepDistance", "Keep yourself out of a range with your target", category=ModuleCategory.COMBAT) {
    private val mode = ListValue("Mode", arrayOf("ReleaseKey", "CancelMove"), "ReleaseKey")
    private val minDistance = FloatValue("MinDistance", 2.3F, 0F, 4F)
    private val maxDistance = FloatValue("MaxDistance", 4.0F, 3F, 7F)
    private val onlyForward = BooleanValue("OnlyForward", true)
    private val onlyNoHurt = BooleanValue("OnlyNoHurt", true)
    // bypass / wTap
    private val keepTick = IntegerValue("KeepTick", 10, 0, 40)
    private val restTick = IntegerValue("RestTick", 4, 0, 40)

    private val ticks = TickTimer()
    var target: EntityPlayer? = null
    private val binds = arrayOf(
        mc.gameSettings.keyBindForward,
        mc.gameSettings.keyBindBack,
        mc.gameSettings.keyBindRight,
        mc.gameSettings.keyBindLeft
    )

    @EventTarget fun onAttack(event: AttackEvent) {
        target = if (event.targetEntity is EntityPlayer) event.targetEntity else return
    }
    @EventTarget fun onStrafe(event: StrafeEvent) {
        if (mode equal "CancelMove") {
            target?.let {
                if (mc.thePlayer.getDistanceToEntityBox(it) <= minDistance.get() && !ticks.hasTimePassed(keepTick.get())) {
                    if (!onlyForward.get() || event.forward > 0F) {
                        event.cancelEvent()
                    }
                }
            }
        }
    }
    @EventTarget fun onUpdate(event: UpdateEvent) {
        if (target == null) return
        if (onlyNoHurt.get() && mc.thePlayer.hurtTime > 0) return
        if (ticks.hasTimePassed(keepTick.get() + restTick.get())) ticks.reset()
        ticks.update()
        val distance = mc.thePlayer.getDistanceToEntityBox(target!!)
        if (target!!.isDead || distance >= maxDistance.get()) {
            target = null
            for (bind in binds) bind.pressed = GameSettings.isKeyDown(bind)
            return
        }
        if (mode equal "ReleaseKey") {
            if (distance <= minDistance.get() && !ticks.hasTimePassed(keepTick.get())) {
                if (onlyForward.get()) mc.gameSettings.keyBindForward.pressed = false
                else for (bind in binds) bind.pressed = false
            } else {
                for (bind in binds) bind.pressed = GameSettings.isKeyDown(bind)
            }
        }
    }
}