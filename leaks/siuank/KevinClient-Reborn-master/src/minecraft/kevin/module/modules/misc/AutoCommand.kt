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
import kevin.event.UpdateEvent
import kevin.hud.element.elements.ConnectNotificationType
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient

//import kevin.event.UpdateState

import kevin.module.*
import kevin.utils.ChatUtils
import kevin.utils.MSTimer
import net.minecraft.event.ClickEvent
import net.minecraft.network.play.server.S02PacketChat

class AutoCommand : Module("AutoCommand","Send commands automatically.",category = ModuleCategory.MISC) {
    private val autoLoginValue = BooleanValue("AutoLogin",true)
    private val autoRegisterValue = BooleanValue("AutoRegister",true)
    private val autoJoin = BooleanValue("AutoJoin",true)
    private val autoJoinMessageMode = ListValue("AutoJoinSendMessageMode", arrayOf("Custom", "FindCommand"), "Custom")
    private val autoJoinDetectMessage = TextValue("AutoJoinDetectMessage","top")
    private val autoJoinDetectMessage2 = TextValue("AutoJoinDetectMessage2","died")
    private val autoJoinDelay = IntegerValue("AutoJoinDelay",5000,50,10000)
    private val autoJoinMessage = TextValue("AutoJoinMessage","/join")
    private val autoJoinNotificationMode = ListValue("AutoJoinNotificationMode", arrayOf("Notification","Chat","None"),"Notification")
    private val registerAndLoginPassword = TextValue("RegisterAndLoginPassword","Password")
    private val autoLoginAndRegisterDelay = IntegerValue("AutoLoginAndRegisterDelay",2500,100,5000)
    private val autoLoginMode = ListValue("AutoLoginMode", arrayOf("/l","/login","Custom"),"/login")
    private val autoLoginCustom = TextValue("AutoLoginCustomCommand","/login")
    private val autoRegisterCommand = TextValue("AutoRegisterCommand","/register")
    private val autoLoginDetectMessage = TextValue("AutoLoginDetectMessage","login")
    private val autoRegisterDetectMessage = TextValue("AutoRegisterDetectMessage","register")

    private val timer = MSTimer()
    private val autoJoinTimer = MSTimer()
    private var command = ""
    private var register = false
    private var login = false
    private var join = false

    override fun onDisable() {
        register = false
        login = false
        super.onDisable()
    }

    override val tag: String
        get() = "Auto${if(autoJoin.get()) " Join" else ""}${if(autoLoginValue.get()) " Login" else ""}${if(autoRegisterValue.get()) " Register" else ""}"

    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet = event.packet
        if (packet !is S02PacketChat) return
        val text = packet.chatComponent.formattedText
        val component = packet.chatComponent
        if (text.contains(autoRegisterDetectMessage.get(),true)&&autoRegisterValue.get()) {register=true;timer.reset();return}
        if (text.contains(autoLoginDetectMessage.get(),true)&&autoLoginValue.get()) {login=true;timer.reset()}
        if ((text.contains(autoJoinDetectMessage.get(),true) || text.contains(autoJoinDetectMessage2.get(),true))&&autoJoin.get()) {
            join=true
            autoJoinTimer.reset()
            when(autoJoinNotificationMode.get()){
                "Notification" -> KevinClient.hud.addNotification(Notification("Send command after ${autoJoinDelay.get()} MS.", "AutoJoin"))
                "Chat" -> ChatUtils.messageWithStart("[AutoJoin] Send command after ${autoJoinDelay.get()} MS.")
            }
            if (autoJoinMessageMode equal "Custom") command = autoJoinMessage.get()
            else {
                component.siblings.forEach { sib ->
                    val clickEvent = sib.chatStyle.chatClickEvent
                    if(clickEvent != null && clickEvent.action == ClickEvent.Action.RUN_COMMAND && clickEvent.value.startsWith("/")) {
                        command = clickEvent.value
                    }
                }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){

        //if (event.eventState == UpdateState.OnUpdate) return

        if (autoJoinTimer.hasTimePassed(autoJoinDelay.get().toLong())&&join) {
            mc.thePlayer.sendChatMessage(autoJoinMessage.get())
            join = false
            when(autoJoinNotificationMode.get()){
                "Notification" -> KevinClient.hud.addNotification(Notification("Auto Join...", "AutoJoin", ConnectNotificationType.OK))
                "Chat" -> ChatUtils.messageWithStart("[AutoJoin] Auto Join...")
            }
        }

        if (!timer.hasTimePassed(autoLoginAndRegisterDelay.get().toLong())) return
        if (register){
            mc.thePlayer.sendChatMessage("${autoRegisterCommand.get()} ${registerAndLoginPassword.get()} ${registerAndLoginPassword.get()}")
            register = false
        }
        if (login){
            val start = when(autoLoginMode.get()){
                "/l" -> "/l"
                "/login" -> "/login"
                "Custom" -> autoLoginCustom.get()
                else -> ""
            }
            val text = "$start ${registerAndLoginPassword.get()}"
            mc.thePlayer.sendChatMessage(text)
            login = false
        }
    }
}