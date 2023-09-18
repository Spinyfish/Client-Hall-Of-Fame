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

import kevin.event.*
import kevin.module.*
import kevin.module.modules.movement.speeds.SpeedMode
import kevin.module.modules.movement.speeds.aac.*
import kevin.module.modules.movement.speeds.matrix.MatrixNew
import kevin.module.modules.movement.speeds.other.*
import kevin.module.modules.movement.speeds.verus.*
import kevin.module.modules.movement.speeds.vulcan.*
import kevin.utils.MovementUtils
import net.minecraft.network.play.server.S12PacketEntityVelocity

class Speed : Module("Speed","Allows you to move faster.", category = ModuleCategory.MOVEMENT) {
    private val speeds = arrayListOf(
        Custom, // from FDP
        AAC5Long,
        AAC5Fast,
        YPort,
        LegacyNCP, // from Rise
        NCPStable, // from FDP
        Hypixel, // from IDK where
        AutoJump,
        VerusYPort,
        VerusHop,
        MatrixNew, //from FDP
        VulcanHop, //from FDP
        VulcanGround, // from FDP
        VulcanLowHop, // from FDP
        VulcanYPort, // from FDP
        VulcanYPort2, // coded by myself (ys_)
        Spartan, // from Rise
        Prediction, // from Rise
        IntaveHop // from Rise
    )

    private val names: Array<String>
    init {
        val arrayList = arrayListOf<String>()
        speeds.forEach { arrayList.add(it.modeName) }
        names = arrayList.toTypedArray()
    }

    private val mode = ListValue("Mode",names,names.first())
    private val keepSprint = BooleanValue("KeepSprint",false)
    private val antiKnockback = BooleanValue("AntiKnockBack",false)
    private val antiKnockbackLong = FloatValue("AntiKnockBackLong",0F,0.00F,1.00F)
    private val antiKnockbackHigh = FloatValue("AntiKnockBackHigh",1F,0.00F,1.00F)

    override val tag: String
        get() = mode.get()

    private val nowMode: SpeedMode
    get() = speeds.find { mode equal it.modeName }!!

    override fun onDisable() {
        mc.timer.timerSpeed = 1F
        mc.thePlayer.speedInAir = 0.02F
        nowMode.onDisable()
    }
    override fun onEnable() = nowMode.onEnable()
    @EventTarget fun onMove(event: MoveEvent) = nowMode.onMove(event)
    @EventTarget fun onUpdate(event: UpdateEvent) = nowMode.onUpdate(event)
    @EventTarget fun onMotion(event: MotionEvent) {
        if (mc.thePlayer.isSneaking || event.eventState != EventState.PRE) return
        if (MovementUtils.isMoving && keepSprint.get()) mc.thePlayer.isSprinting = true
        nowMode.onPreMotion()
    }
    @EventTarget
    fun onPacket(event: PacketEvent){
        if (event.packet is S12PacketEntityVelocity && antiKnockback.get()) {
            val thePlayer = mc.thePlayer ?: return
            val packet = event.packet
            if ((mc.theWorld?.getEntityByID(packet.entityID) ?: return) != thePlayer) return
            val horizontal = antiKnockbackLong.get()
            val vertical = antiKnockbackHigh.get()
            if (horizontal == 0F && vertical == 0F) event.cancelEvent()
            packet.motionX = (packet.motionX * horizontal).toInt()
            packet.motionY = (packet.motionY * vertical).toInt()
            packet.motionZ = (packet.motionZ * horizontal).toInt()
        }
        nowMode.onPacket(event)
    }
    override val values: List<Value<*>> = super.values.toMutableList().also { list -> speeds.forEach { speedMode -> list.addAll(speedMode.values) } }
}