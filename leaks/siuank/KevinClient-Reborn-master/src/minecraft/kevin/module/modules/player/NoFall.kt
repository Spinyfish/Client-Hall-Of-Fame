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

import kevin.event.*
import kevin.main.KevinClient
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.module.Value
import kevin.module.modules.player.nofalls.NoFallMode
import kevin.module.modules.player.nofalls.aac.*
import kevin.module.modules.player.nofalls.matrix.*
import kevin.module.modules.player.nofalls.normal.*
import kevin.module.modules.player.nofalls.other.*
import kevin.module.modules.player.nofalls.packet.*
import kevin.module.modules.player.nofalls.spartan.*
import kevin.module.modules.player.nofalls.verus.*
import kevin.module.modules.player.nofalls.vulcan.*
import kevin.module.modules.render.FreeCam
import kevin.utils.BlockUtils.collideBlock
import net.minecraft.block.BlockLiquid
import net.minecraft.util.AxisAlignedBB

class NoFall : Module("NoFall","Prevents you from taking fall damage.", category = ModuleCategory.PLAYER) {
    private val noFalls = arrayOf(
        SpoofGroundNoFall,
        SpoofGroundNoFall2,
        SpoofGroundNoFall3,
        NoGroundNoFall,
        DamageNoFall,
        MotionFlagNoFall,
        PhaseNoFall,
        PacketNoFall,
        PacketNoFall2,
        PacketNoFall3,
        PacketNoFall4,
        C03C04NoFall,
        AACNoFall,
        LAACNoFall,
        LegitNoFall,
        AAC3311NoFall,
        AAC3315NoFall,
        AACV4NoFall,
        AAC44XFlagNoFall,
        AAC5014NoFall,
        AAC504NoFall,
        OldMatrixNoFall,
        MatrixCollideNoFall,
        MatrixNewNoFall,
        Matrix62xNoFall,
        Matrix62xPacketNoFall,
        Matrix663NoFall,
        VerusNoFall,
        VerusNoFall2,
        VulcanNoFall,
        VulcanNoFall2,
        BuzzNoFall.INSTANCE,
        SpartanNoFall,
        MedusaNoFall,
        CubeCraftNoFall,
        HypixelNoFall,
        HypSpoofNoFall
    )
    private val names: Array<String> = noFalls.map { it.modeName }.toTypedArray()
    @JvmField
    val modeValue = ListValue("Mode", names, names.first())
    private val nowMode: NoFallMode
        get() = noFalls.first { modeValue equal it.modeName }

    var wasTimer = false

    override fun onEnable() {
        wasTimer = false
        nowMode.onEnable()
    }
    override fun onDisable() {
        //mc.thePlayer.capabilities.isFlying = false
        //mc.thePlayer.capabilities.flySpeed = 0.05f
        //mc.thePlayer.noClip = false
        //mc.thePlayer.speedInAir = 0.02F
        mc.timer.timerSpeed = 1F
        nowMode.onDisable()
    }
    @EventTarget fun onPacket(event: PacketEvent) = nowMode.onPacket(event)
    @EventTarget fun onMotionUpdate(event: MotionEvent) = nowMode.onMotion(event)
    @EventTarget fun onStep(event: StepEvent) = nowMode.onStep(event)
    @EventTarget fun onMove(event: MoveEvent) = nowMode.onMove(event)
    @EventTarget fun onBB(event: BlockBBEvent) = nowMode.onBlockBB(event)
    @EventTarget(ignoreCondition = true) fun onJump(event: JumpEvent) = nowMode.onJump(event)
    @EventTarget(ignoreCondition = true)
    fun onUpdate(event: UpdateEvent) {

        //if (event!!.eventState == UpdateState.OnUpdate) return

        if (wasTimer) {
            mc.timer.timerSpeed = 1.0f
            wasTimer = false
        }
        nowMode.onUpdate(event)
        if (!this.state || KevinClient.moduleManager.getModule(FreeCam::class.java).state)
            return
        if (collideBlock(mc.thePlayer!!.entityBoundingBox, fun(block: Any?) = block is BlockLiquid) ||
            collideBlock(AxisAlignedBB(mc.thePlayer!!.entityBoundingBox.maxX, mc.thePlayer!!.entityBoundingBox.maxY, mc.thePlayer!!.entityBoundingBox.maxZ, mc.thePlayer!!.entityBoundingBox.minX, mc.thePlayer!!.entityBoundingBox.minY - 0.01, mc.thePlayer!!.entityBoundingBox.minZ), fun(block: Any?) = block is BlockLiquid))
            return
        nowMode.onNoFall(event)
    }

    override val values: List<Value<*>> = super.values.toMutableList().also { list -> noFalls.forEach { noFallMode -> list.addAll(noFallMode.values) } }

    override val tag: String
        get() = modeValue.get()
}