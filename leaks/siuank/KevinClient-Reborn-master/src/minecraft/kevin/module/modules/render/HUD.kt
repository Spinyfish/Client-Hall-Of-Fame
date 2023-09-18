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
package kevin.module.modules.render

import kevin.event.*
import kevin.hud.designer.GuiHudDesigner
import kevin.hud.element.elements.ScoreboardElement
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.server.S09PacketHeldItemChange

class HUD(currentSlot: Int) : Module("HUD","Toggles visibility of the HUD.",category = ModuleCategory.RENDER) {
    var keepScoreboard = BooleanValue("KeepScoreboard", true)
    private var hotBarShowCurrentSlot = BooleanValue("HotBarShowCurrentSlot", true)
    private var currentPacketSlot = 0

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if ((mc.currentScreen) is GuiHudDesigner)
            return

        KevinClient.hud.render(false)
    }

    @EventTarget(true)
    fun renderScoreboard(event: Render2DEvent) {
        if (!this.state && keepScoreboard.get() && KevinClient.hud.elements.filterIsInstance<ScoreboardElement>().isNotEmpty()) {
            KevinClient.hud.renderScoreboardOnly()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {

        //if (event!!.eventState == UpdateState.OnUpdate) return

        KevinClient.hud.update()
    }

    @EventTarget(true) fun onPacket(event: PacketEvent) {
        currentPacketSlot =
            when (event.packet) {
                is C09PacketHeldItemChange -> event.packet.slotId
                is S09PacketHeldItemChange -> event.packet.heldItemHotbarIndex
                else -> return
            }
    }

    @EventTarget(true)
    fun updateScoreboard(event: UpdateEvent) {
        if (!this.state && keepScoreboard.get() && KevinClient.hud.elements.filterIsInstance<ScoreboardElement>().isNotEmpty()) {
            KevinClient.hud.updateScoreboardOnly()
        }
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        KevinClient.hud.handleKey('a', event.key)
    }

    val currentSlot: Int
        get() = if (hotBarShowCurrentSlot.get()) currentPacketSlot else mc.thePlayer.inventory.currentItem
}