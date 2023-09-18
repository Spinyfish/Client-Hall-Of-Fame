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
package kevin.module.modules.movement.speeds

import kevin.event.MoveEvent
import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.Value
import kevin.module.modules.movement.Speed
import kevin.utils.ClassUtils
import kevin.utils.MinecraftInstance

abstract class SpeedMode(val modeName: String): MinecraftInstance() {
    protected val valuePrefix = "$modeName-"
    protected val speed by lazy { KevinClient.moduleManager.getModule(Speed::class.java) }
    open val values: List<Value<*>>
        get() = ClassUtils.getValues(this.javaClass,this)
    open fun onEnable() {}
    open fun onDisable() {}

    open fun onMove(event: MoveEvent) {}
    open fun onUpdate(event: UpdateEvent) {}
    open fun onPacket(event: PacketEvent) {}
    open fun onPreMotion() {}
}