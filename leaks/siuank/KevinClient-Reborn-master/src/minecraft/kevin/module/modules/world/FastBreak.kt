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

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory

class FastBreak : Module("FastBreak", "Allows you to break blocks faster.", category = ModuleCategory.WORLD) {

    private val breakDamage = FloatValue("BreakDamage", 0.8F, 0.1F, 1F)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.playerController.blockHitDelay = 0

        if (mc.playerController.curBlockDamageMP > breakDamage.get())
            mc.playerController.curBlockDamageMP = 1F

        val breaker = KevinClient.moduleManager.getModule(Breaker::class.java)
        val nuker = KevinClient.moduleManager.getModule(Nuker::class.java)

        if (breaker.currentDamage > breakDamage.get())
            breaker.currentDamage = 1F

        if (nuker.currentDamage > breakDamage.get())
            nuker.currentDamage = 1F
    }
}