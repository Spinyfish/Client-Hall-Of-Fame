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
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.via.ViaVersion
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

class AntiInvalidBlockPlacement : Module("AntiInvalidBlockPlacement", "Anti invalid block placement caused by via-version.") {
    private val versionCheck = BooleanValue("VersionCheck", true)
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if ((!versionCheck.get() || ViaVersion.nowVersion > 210) && packet is C08PacketPlayerBlockPlacement) {
            packet.facingX = 0.5F
            packet.facingY = 0.5F
            packet.facingZ = 0.5F
        }
    }
}