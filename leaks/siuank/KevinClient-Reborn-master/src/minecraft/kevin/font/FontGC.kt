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
package kevin.font

import kevin.event.EventTarget
import kevin.event.Listenable
import kevin.event.TickEvent

object FontGC : Listenable {
    private val activeFontRenderers: ArrayList<GameFontRenderer> = ArrayList()

    private var gcTicks: Int = 0
    const val GC_TICKS = 600 // Start garbage collection every 600 ticks (30s)
    const val CACHED_FONT_REMOVAL_TIME = 30000 // Remove cached texts after 30s of not being used

    @EventTarget
    fun onTick(event: TickEvent) {
        if (gcTicks++ > GC_TICKS) {
            activeFontRenderers.forEach { it.collectGarbage() }
            gcTicks = 0
        }
    }

    fun register(fontRender: GameFontRenderer) {
        activeFontRenderers.add(fontRender)
    }

    fun unregister(fontRender: GameFontRenderer) {
        if (!activeFontRenderers.contains(fontRender))
            return
        fontRender.close()
        activeFontRenderers.remove(fontRender)
    }

    override fun handleEvents() = true
}