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
package kevin.module.modules.movement.flys.ncp

import kevin.event.MoveEvent
import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.modules.movement.Strafe
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import kotlin.math.cos
import kotlin.math.sin

object NCPPacket : FlyMode("NCPPacket") {
    private val timerValue = FloatValue("${valuePrefix}Timer", 1.0f, 1.0f, 1.3f)
    private val useSpeedEffect = BooleanValue("${valuePrefix}UseSpeedEffect", true)
    private val smart = BooleanValue("${valuePrefix}smart", true)
    private var flag = false
    override fun onEnable() {
        if (mc.thePlayer.onGround && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,mc.thePlayer.entityBoundingBox.offset(.0, .2, .0).expand(.0, .0, .0)).isEmpty()) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + .2, mc.thePlayer.posZ)
        }
        flag = true
    }
    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }
    override fun onUpdate(event: UpdateEvent) {
        if(!MovementUtils.isMoving) {
            mc.timer.timerSpeed = 1f
            return
        }
        if (smart.get() && !flag) return
        val radiansYaw = Math.toRadians(KevinClient.moduleManager.getModule(Strafe::class.java).getMoveYaw().toDouble())
        val speed = if (useSpeedEffect.get()) MovementUtils.getBaseMoveSpeed() // speed effect base
                    else 0.2873
        val x = -sin(radiansYaw) * speed
        val z = cos(radiansYaw) * speed
        mc.timer.timerSpeed = timerValue.get()
        mc.netHandler.addToSendQueue(
            C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX + x,
                mc.thePlayer.posY,
                mc.thePlayer.posZ + z,
                false
            )
        )
        mc.netHandler.addToSendQueue(
            C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX + x,
                mc.thePlayer.posY - 999,
                mc.thePlayer.posZ + z,
                true
            )
        )
        flag = false
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is S08PacketPlayerPosLook) flag = true
    }
    override fun onMove(event: MoveEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        event.zero()
    }
}