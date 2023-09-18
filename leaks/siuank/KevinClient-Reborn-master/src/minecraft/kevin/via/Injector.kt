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

import com.viaversion.viaversion.api.platform.ViaInjector
import com.viaversion.viaversion.libs.gson.JsonObject


class Injector : ViaInjector {
    override fun inject() {}
    override fun uninject() {}
    override fun getServerProtocolVersion(): Int {
        return ViaVersion.CLIENT_VERSION
    }
    override fun getEncoderName(): String {
        return CommonTransformer.HANDLER_ENCODER_NAME
    }
    override fun getDecoderName(): String {
        return CommonTransformer.HANDLER_DECODER_NAME
    }
    override fun getDump(): JsonObject {
        return JsonObject()
    }
}