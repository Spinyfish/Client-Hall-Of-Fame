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

import com.viaversion.viaversion.configuration.AbstractViaConfig
import java.io.File

class ViaConfig(configFile: File?) : AbstractViaConfig(configFile) {
    override fun getDefaultConfigURL() = javaClass.classLoader.getResource("assets/viaversion/config.yml")!!
    override fun handleConfig(config: Map<String, Any>) {}
    override fun getUnsupportedOptions() = UNSUPPORTED
    override fun isAntiXRay() = false
    override fun isNMSPlayerTicking() = false
    override fun is1_12QuickMoveActionFix() = false
    override fun getBlockConnectionMethod() = "packet"
    override fun is1_9HitboxFix() = false
    override fun is1_14HitboxFix() = false
    companion object {
        private val UNSUPPORTED = listOf(
            "anti-xray-patch", "bungee-ping-interval",
            "bungee-ping-save", "bungee-servers", "quick-move-action-fix", "nms-player-ticking",
            "velocity-ping-interval", "velocity-ping-save", "velocity-servers",
            "blockconnection-method", "change-1_9-hitbox", "change-1_14-hitbox"
        )
    }
    init {
        reloadConfig()
    }
}