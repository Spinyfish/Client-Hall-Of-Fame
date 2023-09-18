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
import kevin.module.Module
import kevin.module.ModuleCategory
import net.minecraft.network.play.client.C19PacketResourcePackStatus
import net.minecraft.network.play.server.S48PacketResourcePackSend
import java.net.URI
import java.net.URISyntaxException

class ResourcePackSpoof : Module("ResourcePackSpoof", "Prevents servers from forcing you to download their resource pack.", category = ModuleCategory.MISC) {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if ((event.packet)is S48PacketResourcePackSend) {
            val packet = event.packet

            val url = packet.url
            val hash = packet.hash

            try {
                val scheme = URI(url).scheme
                val isLevelProtocol = "level" == scheme

                if ("http" != scheme && "https" != scheme && !isLevelProtocol)
                    throw URISyntaxException(url, "Wrong protocol")

                if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip")))
                    throw URISyntaxException(url, "Invalid levelstorage resourcepack path")

                mc.netHandler.addToSendQueue(C19PacketResourcePackStatus(packet.hash,
                    C19PacketResourcePackStatus.Action.ACCEPTED))
                mc.netHandler.addToSendQueue(C19PacketResourcePackStatus(packet.hash,
                    C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED))
            } catch (e: URISyntaxException) {
                println("Failed to handle resource pack $e")
                mc.netHandler.addToSendQueue(C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD))
            }
        }
    }
}