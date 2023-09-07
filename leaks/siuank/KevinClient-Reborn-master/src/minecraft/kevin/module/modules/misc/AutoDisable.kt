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

import kevin.command.ICommand
import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.event.WorldEvent
import kevin.hud.element.elements.ConnectNotificationType
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.utils.ChatUtils
import kevin.utils.MSTimer
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import java.util.*

object AutoDisable : Module("AutoDisable","Auto disable modules.(Use Command .AutoDisableSet <ModuleName> <add/remove> <World/SetBack/All>)"),ICommand {
    override fun run(args: Array<out String>?) {
        if (args.isNullOrEmpty()||args.size<2) {
            usageMessage()
            return
        }
        val module = KevinClient.moduleManager.getModuleByName(args[0])
        if (module==null){
            ChatUtils.messageWithStart("§cNo module called ${args[0]}")
            return
        }
        if (args.size == 2) {
            if (args[1].equals("remove",true)) {
                remove(module)
            } else if (args[1].equals("add",true))
                addUsageMessage(args)
            else usageMessage()
        } else {
            if (args[1].equals("remove",true)) {
                remove(module)
            } else if (args[1].equals("add",true)) {
                if (args[2].equals("World",true)||
                    args[2].equals("Setback",true)||
                    args[2].equals("All",true)) add(module,args[2])
                else addUsageMessage(args)
            } else usageMessage()
        }
    }
    private fun add(module: Module,mode: String){
        module.autoDisable = true to mode.lowercase(Locale.getDefault())
        ChatUtils.messageWithStart("§aModule successfully added to list.")
        if (!this.state) ChatUtils.messageWithStart("§eDon't forget to open AutoDisable module!")
        KevinClient.fileManager.saveConfig(KevinClient.fileManager.modulesConfig)
    }
    private fun remove(module: Module) {
        module.autoDisable = false to ""
        ChatUtils.messageWithStart("§aModule successfully removed from list.")
        KevinClient.fileManager.saveConfig(KevinClient.fileManager.modulesConfig)
    }
    private fun addUsageMessage(args: Array<out String>) = ChatUtils.messageWithStart("§cUsage: §9.autodisable §a${args[0]} ${args[1]} §c<World/SetBack/All>")
    private fun usageMessage() = ChatUtils.messageWithStart("§cUsage: .autodisable <ModuleName> <add/remove> <World/SetBack/All>")
    private val timer = MSTimer()
    private val notification = BooleanValue("Notification",true)
    @EventTarget fun onWorld(event: WorldEvent){
        KevinClient.moduleManager.getModules()
            .filter { it.autoDisable.first&&(it.autoDisable.second=="world"||it.autoDisable.second=="all")&&it.state }
            .forEach {
                it.state = false
                if (notification.get()) KevinClient.hud.addNotification(Notification("Auto disabled module ${it.name}.", "AutoDisable", ConnectNotificationType.OK))
            }
        timer.reset()
    }
    @EventTarget fun onPacket(event: PacketEvent) {
        if (event.packet !is S08PacketPlayerPosLook || !timer.hasTimePassed(3000)) return
        KevinClient.moduleManager.getModules()
            .filter { it.autoDisable.first&&(it.autoDisable.second=="setback"||it.autoDisable.second=="all")&&it.state }
            .forEach {
                it.state = false
                if (notification.get()) KevinClient.hud.addNotification(Notification("Auto disabled module ${it.name}.", "AutoDisable", ConnectNotificationType.OK))
            }
    }
}