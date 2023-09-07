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
package kevin.utils.proxy

import io.netty.bootstrap.ChannelFactory
import io.netty.channel.socket.oio.OioSocketChannel
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.Socket

object ProxyManager {
    var isEnable = false
    var proxy = "127.0.0.1:10808"  // V2Ray VPN Default Port
    var proxyType = Proxy.Type.SOCKS

    val proxyInstance: Proxy
        get() = proxy.split(":").let { Proxy(proxyType, InetSocketAddress(it.first(), it.last().toInt())) }

    class ProxyOioChannelFactory(proxy: Proxy) : ChannelFactory<OioSocketChannel> {
        private val proxy: Proxy

        init {
            this.proxy = proxy
        }

        override fun newChannel(): OioSocketChannel {
            return OioSocketChannel(Socket(this.proxy))
        }
    }
}