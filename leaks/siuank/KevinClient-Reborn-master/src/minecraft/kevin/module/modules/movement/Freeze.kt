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

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.BooleanValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.PacketUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook

class Freeze : Module("Freeze", "Allows you to stay stuck in mid air.", category = ModuleCategory.MOVEMENT) {
    private val mode = ListValue("Mode", arrayOf("SetDead","NoMove", "NoPacket"),"NoPacket")
    private val resetMotionValue = BooleanValue("ResetMotion",false)
    private val lockRotation = BooleanValue("LockRotation",true)

    private var motionX = .0
    private var motionY = .0
    private var motionZ = .0
    private var rotationYaw = .0F
    private var rotationPitch = .0F

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer!!

        when(mode.get()){
            "SetDead" -> {
                thePlayer.isDead = true
                thePlayer.rotationYaw = thePlayer.cameraYaw
                thePlayer.rotationPitch = thePlayer.cameraPitch
            }
            "NoMove", "NoPacket" -> {
                mc.thePlayer.motionX = .0
                mc.thePlayer.motionY = .0
                mc.thePlayer.motionZ = .0
                mc.thePlayer.speedInAir = .0F
            }
        }
    }
    @EventTarget fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (mode.get() == "NoMove") {
            if (packet is C03PacketPlayer&&(packet is C03PacketPlayer.C05PacketPlayerLook || packet is C03PacketPlayer.C06PacketPlayerPosLook)) {
                if (!lockRotation.get()) return
                packet.yaw = rotationYaw
                packet.pitch = rotationPitch
                packet.rotating = false
            }
        }
        if (mode equal "NoPacket") {
            if (packet is C03PacketPlayer) event.cancelEvent()
            else if (packet is S08PacketPlayerPosLook) {
                if (!mc.netHandler.isDoneLoadingTerrain) return
                motionX = 0.0
                motionY = 0.0
                motionZ = 0.0
                var d0: Double = packet.x
                var d1: Double = packet.y
                var d2: Double = packet.z
                var f: Float = packet.getYaw()
                var f1: Float = packet.getPitch()
                if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
                    d0 += mc.thePlayer.posX
                } else {
                    motionX = 0.0
                }

                if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
                    d1 += mc.thePlayer.posY
                } else {
                    motionY = 0.0
                }

                if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
                    d2 += mc.thePlayer.posZ
                } else {
                    motionZ = 0.0
                }

                if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
                    f1 += mc.thePlayer.rotationPitch
                }

                if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
                    f += mc.thePlayer.rotationYaw
                }
                mc.thePlayer.setPositionAndRotation(d0, d1, d2, f, f1)
                PacketUtils.sendPacketNoEvent(C03PacketPlayer.C06PacketPlayerPosLook(d0, d1, d2, f, f1, false))
                event.cancelEvent()
            }
        }
    }

    override fun onDisable() {
        when(mode.get()){
            "SetDead" -> mc.thePlayer?.isDead = false
            "NoMove", "NoPacket" -> mc.thePlayer.speedInAir = .02F
        }
        if (resetMotionValue.get()) {
            mc.thePlayer.motionX = .0
            mc.thePlayer.motionY = .0
            mc.thePlayer.motionZ = .0
        } else {
            mc.thePlayer.motionX = motionX
            mc.thePlayer.motionY = motionY
            mc.thePlayer.motionZ = motionZ
        }
    }
    override fun onEnable() {
        val thePlayer = mc.thePlayer!!
        motionX = thePlayer.motionX
        motionY = thePlayer.motionY
        motionZ = thePlayer.motionZ
        rotationYaw = thePlayer.rotationYaw
        rotationPitch = thePlayer.rotationPitch
    }
}