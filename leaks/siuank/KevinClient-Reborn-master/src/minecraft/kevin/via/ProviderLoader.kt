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
package kevin.via

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.platform.ViaPlatformLoader
import com.viaversion.viaversion.api.protocol.version.VersionProvider
import com.viaversion.viaversion.bungee.providers.BungeeMovementTransmitter
import com.viaversion.viaversion.protocols.base.BaseVersionProvider
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider


class ProviderLoader : ViaPlatformLoader {
    override fun load() {
        Via.getManager().providers.use(MovementTransmitterProvider::class.java, BungeeMovementTransmitter())
        Via.getManager().providers.use(VersionProvider::class.java, object : BaseVersionProvider() {
            @Throws(Exception::class)
            override fun getClosestServerProtocol(connection: UserConnection): Int {
                return if (connection.isClientSide) ViaVersion.nowVersion
                else super.getClosestServerProtocol(connection)
            }
        })
    }
    override fun unload() {}
}
