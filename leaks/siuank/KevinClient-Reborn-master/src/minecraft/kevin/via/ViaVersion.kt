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

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.viaversion.viaversion.ViaManagerImpl
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.data.MappingDataLoader
import io.netty.channel.EventLoop
import io.netty.channel.local.LocalEventLoopGroup
import kevin.main.KevinClient
import org.apache.logging.log4j.LogManager
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger

object ViaVersion {
    @JvmStatic
    val CLIENT_VERSION = 47
    var nowVersion = CLIENT_VERSION
    val jLogger: Logger = JLoggerToLog4j(LogManager.getLogger("Via"))
    val initFuture = CompletableFuture<Void>()
    var asyncExecutor: ExecutorService? = null
    var eventLoop: EventLoop? = null
    val versions: Array<ProtocolCollection>
    init {
        val value = ProtocolCollection.values()
        value.sortBy { it.protocolVersion.version }
        versions = value
    }

    fun start() {
        val factory = ThreadFactoryBuilder().setDaemon(true).setNameFormat("Via-%d").build()
        asyncExecutor = Executors.newFixedThreadPool(8, factory)
        eventLoop = LocalEventLoopGroup(1, factory).next()
        eventLoop!!.submit(Callable { initFuture.join() })
        Via.init(
            ViaManagerImpl.builder()
                .injector(Injector())
                .loader(ProviderLoader())
                .platform(Platform(KevinClient.fileManager.via))
                .build()
        )
        MappingDataLoader.enableMappingsCache()
        (Via.getManager() as ViaManagerImpl).init()
        BackwardsLoader(KevinClient.fileManager.via)
        RewindLoader(KevinClient.fileManager.via)
        initFuture.complete(null)
    }
}