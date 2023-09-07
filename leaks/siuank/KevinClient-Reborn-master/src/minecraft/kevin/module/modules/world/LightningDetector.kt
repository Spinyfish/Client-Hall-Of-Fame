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
package kevin.module.modules.world

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.ChatUtils
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity

object LightningDetector : Module("LightningDetector","Detect lightning.",category = ModuleCategory.WORLD) {
    private val mode = ListValue("MessageMode", arrayOf("Chat","Notification"),"Notification")
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S2CPacketSpawnGlobalEntity) {
            val packet = event.packet
            if(packet.func_149053_g() != 1) return
            when(mode.get()){
                "Chat" -> ChatUtils.messageWithStart("§eLightning §9at §cX:${packet.func_149051_d()/32} §cY:${packet.func_149050_e()/32} §cZ:${packet.func_149049_f()/32}")
                "Notification" -> KevinClient.hud.addNotification(Notification("Lightning at X:${packet.func_149051_d()/32} Y:${packet.func_149050_e()/32} Z:${packet.func_149049_f()/32}", name))
            }
        }
    }
}