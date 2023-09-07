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
package kevin.module.modules

import kevin.event.EventTarget
import kevin.event.TickEvent
//import kevin.event.UpdateEvent
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.utils.EntityUtils

class Targets : Module("Targets") {
    private val players = BooleanValue("Players",true)
    private val mobs = BooleanValue("Mobs",true)
    private val animals = BooleanValue("Animals",false)
    private val invisible = BooleanValue("Invisible",true)
    private val death = BooleanValue("Death",false)
    @EventTarget(true)
    fun onUpdate(event: TickEvent){
        EntityUtils.targetPlayer = players.get()
        EntityUtils.targetMobs = mobs.get()
        EntityUtils.targetAnimals = animals.get()
        EntityUtils.targetInvisible = invisible.get()
        EntityUtils.targetDeath = death.get()
    }
}