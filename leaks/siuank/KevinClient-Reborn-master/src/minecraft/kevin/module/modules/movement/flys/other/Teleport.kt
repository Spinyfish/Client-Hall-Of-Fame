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
package kevin.module.modules.movement.flys.other

import kevin.event.MotionEvent
import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.modules.movement.flys.FlyMode
import net.minecraft.network.play.client.C03PacketPlayer
import kotlin.math.cos
import kotlin.math.sin

object Teleport : FlyMode("Teleport") {
    private val teleportLongValue = FloatValue("TeleportLong",10F,0.1F,20F)
    private val teleportTimer = FloatValue("TeleportTimer",0.1F,0.1F,1F)
    private val teleportHighPacket = BooleanValue("TeleportHPacket",true)
    private val teleportHigh = FloatValue("TeleportHigh",-1F,-4F,4F)
    private val teleportYMotion = FloatValue("TeleportYMotion",-0.05F,-1F,1F)
    private val teleportMotion = FloatValue("TeleportMotion",2F,0F,5F)
    private val teleportResetMotion = BooleanValue("TeleportResetMotion",true)
    private val teleportSetPos = BooleanValue("TeleportSetPos",false)

    override fun onMotion(event: MotionEvent) {
        mc.thePlayer!!.jumpMovementFactor = 0F
        mc.thePlayer!!.motionY = teleportYMotion.get().toDouble()
        mc.timer.timerSpeed = teleportTimer.get()
        if (mc.thePlayer!!.ticksExisted % 2 == 0){
            val playerYaw = mc.thePlayer!!.rotationYaw * Math.PI / 180
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer!!.posX + teleportLongValue.get() * -sin(playerYaw),
                    mc.thePlayer!!.posY,
                    mc.thePlayer!!.posZ + teleportLongValue.get() * cos(playerYaw),
                    false
                )
            )
            if(teleportHighPacket.get()) {
                mc.netHandler.addToSendQueue(
                    C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + teleportHigh.get(),
                        mc.thePlayer!!.posZ,
                        false
                    )
                )
            }
            mc.thePlayer!!.motionX = teleportMotion.get() * -sin(playerYaw)
            mc.thePlayer!!.motionZ = teleportMotion.get() * cos(playerYaw)
            if (teleportSetPos.get())
                mc.thePlayer.setPosition(
                    mc.thePlayer!!.posX + teleportLongValue.get() * -sin(playerYaw),
                    mc.thePlayer!!.posY,
                    mc.thePlayer!!.posZ + teleportLongValue.get() * cos(playerYaw)
                )
        }else{
            if (teleportResetMotion.get()) mc.thePlayer!!.motionY = .0
            mc.thePlayer!!.motionX = .0
            mc.thePlayer!!.motionZ = .0
        }
    }
    override fun onDisable() {
        mc.timer.timerSpeed = 1F
    }
}