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
package kevin.module.modules.misc

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.event.WorldEvent
import kevin.hud.element.elements.ConnectNotificationType
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.utils.ChatUtils
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import kotlin.math.pow
import kotlin.math.sqrt

class LagBackDetector : Module("LagBackDetector", "Detect lag back from server.") {
    private val countValue = BooleanValue("Count", true)
    private val distanceCheckValue = BooleanValue("DistanceCheck", true)
    private val modeValue = ListValue("Mode", arrayOf("Chat", "Notification"), "Notification")
    var count = 0
    override fun onDisable() {
        count = 0
    }

    override fun onEnable() {
        count = 0
    }

    @Suppress("UNUSED_PARAMETER")
    @EventTarget fun onWorld(event: WorldEvent) {
        count = 0
    }
    @EventTarget fun onPacket(event: PacketEvent) {
        mc.thePlayer ?: return
        mc.theWorld  ?: return
        val packet = if (event.packet is S08PacketPlayerPosLook) event.packet else return
        if (distanceCheckValue.get()) {
            if (sqrt((packet.x - mc.thePlayer.posX).pow(2) + (packet.z - mc.thePlayer.posZ).pow(2)) > 10) return
        }
        val message = "Flag detected${if (countValue.get()) ", count: ${++count}" else ""}"
        when (modeValue.get()) {
            "Chat" -> ChatUtils.messageWithStart("[LagBackDetector] $message")
            "Notification" -> KevinClient.hud.addNotification(Notification(message, "LagBackDetector", ConnectNotificationType.Warn))
        }
    }
}