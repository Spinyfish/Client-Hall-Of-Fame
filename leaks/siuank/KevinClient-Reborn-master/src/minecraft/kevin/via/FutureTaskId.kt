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

import com.viaversion.viaversion.api.platform.PlatformTask
import java.util.concurrent.Future

class FutureTaskId(`object`: Future<*>) : PlatformTask<Future<*>> {
    private val `object`: Future<*>
    override fun getObject(): Future<*> {
        return `object`
    }
    override fun cancel() {
        `object`.cancel(false)
    }
    init {
        this.`object` = `object`
    }
}