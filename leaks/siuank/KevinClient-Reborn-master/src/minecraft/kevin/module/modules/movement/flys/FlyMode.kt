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
package kevin.module.modules.movement.flys

import kevin.event.*
import kevin.main.KevinClient
import kevin.module.Value
import kevin.module.modules.movement.Fly
import kevin.utils.ClassUtils
import kevin.utils.MinecraftInstance

abstract class FlyMode(val modeName: String): MinecraftInstance() {
    protected val valuePrefix = "$modeName-"
    protected val fly by lazy { KevinClient.moduleManager.getModule(Fly::class.java) }
    open val values: List<Value<*>>
        get() = ClassUtils.getValues(this.javaClass,this)
    open fun onEnable() {}
    open fun onDisable() {}

    open fun onMotion(event: MotionEvent) {}
    open fun onRender3D(event: Render3DEvent) {}
    open fun onWorld(event: WorldEvent) {}
    open fun onBB(event: BlockBBEvent) {}
    open fun onStep(event: StepEvent) {}
    open fun onJump(event: JumpEvent) {}
    open fun onUpdate(event: UpdateEvent) {}
    open fun onPacket(event: PacketEvent) {}
    open fun onMove(event: MoveEvent) {}
    open val tagV: String? = null
}