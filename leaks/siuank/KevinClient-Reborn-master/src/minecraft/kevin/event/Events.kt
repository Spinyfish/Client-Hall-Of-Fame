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
package kevin.event

import net.minecraft.block.Block
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.Packet
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

class AttackEvent(val targetEntity: Entity?) : Event()

class BlockBBEvent(blockPos: BlockPos, val block: Block, var boundingBox: AxisAlignedBB?) : Event() {
    val x = blockPos.x
    val y = blockPos.y
    val z = blockPos.z
}

class ClickBlockEvent(val clickedBlock: BlockPos?, val EnumFacing: EnumFacing?) : Event()

class ClientShutdownEvent : Event()

data class EntityMovementEvent(val movedEntity: Entity) : Event()

class JumpEvent(var motion: Float, var yaw: Float) : CancellableEvent()

class KeyEvent(val key: Int) : Event()

object ClickUpdateEvent : CancellableEvent() {
    fun reInit() {
        isCancelled = false;
    }
}

class MotionEvent(var posX: Double, var posY: Double, var posZ: Double,val eventState: EventState) : CancellableEvent()

class SlowDownEvent(var strafe: Float, var forward: Float) : Event()

class MovementInputUpdateEvent(var strafe: Float, var forward: Float, var jump: Boolean, var sneak: Boolean) : Event()

class StrafeEvent(var strafe: Float, var forward: Float, var friction: Float, var yaw: Float) : CancellableEvent()

class MoveEvent(var x: Double, var y: Double, var z: Double) : CancellableEvent() {
    var isSafeWalk = false

    fun zero() {
        x = 0.0
        y = 0.0
        z = 0.0
    }

    fun zeroXZ() {
        x = 0.0
        z = 0.0
    }
}

class PacketEvent(val packet: Packet<*>/**,val eventState: PacketMode**/) : CancellableEvent()

class PushOutEvent : CancellableEvent()

class Render2DEvent(var partialTicks: Float) : Event()

class Render3DEvent(var partialTicks: Float) : Event()

class RenderEntityEvent(val entity: Entity, val x: Double, val y: Double, val z: Double, val entityYaw: Float,
                        val partialTicks: Float) : Event()

class ScreenEvent(val guiScreen: GuiScreen?) : Event()

class StepEvent(var stepHeight: Float) : Event()

class StepConfirmEvent : Event()

class TextEvent(var text: String?) : Event()

class TickEvent : Event()

class UpdateEvent(/**val eventState: UpdateState**/) : Event()

class WorldEvent(val worldClient: WorldClient?) : Event()

class ClickWindowEvent(val windowId: Int, val slotId: Int, val mouseButtonClicked: Int, val mode: Int) : CancellableEvent()

class EntityKilledEvent(val targetEntity: EntityLivingBase) : Event()