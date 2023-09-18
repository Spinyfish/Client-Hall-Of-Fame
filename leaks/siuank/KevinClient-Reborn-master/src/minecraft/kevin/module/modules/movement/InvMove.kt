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
package kevin.module.modules.movement

import kevin.event.*
import kevin.module.BooleanValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MovementUtils
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C16PacketClientStatus
import org.lwjgl.input.Keyboard

class InvMove : Module("InvMove","Allows you to walk while an inventory is opened.",Keyboard.KEY_NONE,ModuleCategory.MOVEMENT){
    private val sprintMode = ListValue("Sprint", arrayOf("Ignore", "AlwaysFake", "StopWhenOpen", "FakeWhenOpen"), "Ignore")
    private val allowSneak = BooleanValue("AllowSneak", true)
    private val bypass = BooleanValue("Bypass",false)
    private val noMoveClicksValue = BooleanValue("NoMoveClicks", false)
    private val alwaysActiveWithClickGui = BooleanValue("AlwaysActiveInClickGUI", true)

    val needFakeSprint: Boolean
    get() = this.state && (sprintMode equal "AlwaysFake" || (sprintMode equal "FakeWhenOpen" && mc.currentScreen != null))
    val needStopSprint: Boolean
    get() = this.state && sprintMode equal "StopWhenOpen" && mc.currentScreen != null

    private val affectedBindings = arrayOf(
        mc.gameSettings.keyBindForward,
        mc.gameSettings.keyBindBack,
        mc.gameSettings.keyBindRight,
        mc.gameSettings.keyBindLeft,
        mc.gameSettings.keyBindSneak,
        mc.gameSettings.keyBindJump,
        mc.gameSettings.keyBindSprint
    )

    @EventTarget
    fun onPacket(event: PacketEvent){
        if(bypass.get() && event.packet is C16PacketClientStatus
            && event.packet.status == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT){
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){

        //if (event.eventState == UpdateState.OnLivingUpdate){
        if (((state && mc.currentScreen !is GuiChat) || (alwaysActiveWithClickGui.get() && (mc.currentScreen is kevin.hud.ClickGui))) && mc.currentScreen !is GuiIngameMenu)
            for (affectedBinding in affectedBindings) {
                if (affectedBinding != mc.gameSettings.keyBindSneak || allowSneak.get()) affectedBinding.pressed = GameSettings.isKeyDown(affectedBinding)
            }
        //}
    }

    @EventTarget
    fun onClick(event: ClickWindowEvent) {
        if (noMoveClicksValue.get() && MovementUtils.isMoving)
            event.cancelEvent()
    }

    override fun onDisable() {
        val isIngame = mc.currentScreen != null
        for (affectedBinding in affectedBindings) {
            if (!GameSettings.isKeyDown(affectedBinding) || isIngame)
                affectedBinding.pressed = false
        }
    }

    override fun handleEvents(): Boolean = state || (alwaysActiveWithClickGui.get() && (mc.currentScreen is kevin.hud.ClickGui))


    override val tag: String
        get() = "Sprint:${sprintMode.get()}" + if (bypass.get()) " & NoPacket" else ""
//if (sprintMode.get()&&bypass.get()) "FakeSprint & NoPacket" else if (sprintMode.get()) "FakeSprint" else if (bypass.get()) "NoPacket" else null
}