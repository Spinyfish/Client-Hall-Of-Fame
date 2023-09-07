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

import joptsimple.internal.Strings
import kevin.command.ICommand
import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.hud.element.elements.ConnectNotificationType
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.*
import kevin.utils.ChatUtils
import kevin.utils.TickTimer
import net.minecraft.network.play.client.C14PacketTabComplete
import net.minecraft.network.play.server.S3APacketTabComplete
import java.io.File

object AdminDetector : Module("AdminDetector","Detect server admins."),ICommand {
    private val adminNamesFile:File by lazy {
        if (!KevinClient.fileManager.adminNamesFile.exists()) KevinClient.fileManager.adminNamesFile.createNewFile()
        KevinClient.fileManager.adminNamesFile
    }
    override fun run(args: Array<out String>?) {
        if (args.isNullOrEmpty()||args.size<2){
            usageMessage()
            return
        }
        val command = args[0]
        val argNames = java.util.ArrayList<String>().apply {
            addAll(args)
            removeAt(0)
        }
        val names = adminNamesFile.readLines()
        when{
            "Add".equals(command,true) -> {
                for (name in argNames) {
                    if (name in names) {
                        ChatUtils.messageWithStart("§cName is already in the list!")
                        return
                    }
                    adminNamesFile.appendText("$name\n")
                    ChatUtils.messageWithStart("§aName successfully added to the list!")
                }
            }
            "Remove".equals(command,true) -> {
                for (name in argNames) {
                    if (name !in names){
                        ChatUtils.messageWithStart("§cName is not in the list!")
                        return
                    }
                    adminNamesFile.writeText("")
                    names.forEach {
                        if (it!=name&&it.isNotEmpty()){
                            adminNamesFile.appendText("$it\n")
                        }
                    }
                    ChatUtils.messageWithStart("§aName successfully removed from the list!")
                }
            }
            else -> usageMessage()
        }
    }
    private fun usageMessage() = ChatUtils.messageWithStart("§cUsage: .Admin <Add/Remove> <Name>")

    private val modeValue = ListValue("Mode", arrayOf("Tab"),"Tab")
    private val tabCommand = TextValue("TabCommand","/tell")
    private val waitTicks = IntegerValue("WaitTick",100,0,200)
    private val notificationMode = ListValue("NotificationMode", arrayOf("Chat","Notification"),"Chat")
    private val noNotFindNotification = BooleanValue("NoNotFindNotification",true)

    private val timer = TickTimer()
    private var waiting = false

    @EventTarget fun onUpdate(event: UpdateEvent){
        timer.update()
        if (!timer.hasTimePassed(waitTicks.get()+1)) return
        when(modeValue.get()){
            "Tab" -> {
                mc.netHandler.addToSendQueue(C14PacketTabComplete("${tabCommand.get()} "))
                waiting = true
                timer.reset()
            }
        }
    }
    @EventTarget fun onPacket(event: PacketEvent){
        val packet = event.packet
        when(modeValue.get()){
            "Tab" -> {
                if (!waiting) return
                if (packet is S3APacketTabComplete){
                    KevinClient.pool.execute {
                        val players = packet.func_149630_c()
                        val admins = adminNamesFile.readLines().toMutableList()
                        admins.removeAll { it.isEmpty() }
                        val findAdmins = arrayListOf<String>()
                        players.forEach {
                            if (it in admins) findAdmins.add(it)
                        }
                        n(findAdmins)
                    }
                    waiting = false
                    event.cancelEvent()
                }
            }
        }
    }

    private fun n(findAdmins:ArrayList<String>){
        if (findAdmins.isEmpty()) {
            if (!noNotFindNotification.get()) when(notificationMode.get()){
                "Chat" -> ChatUtils.messageWithStart("[AdminDetector] No admin find.")
                "Notification" -> KevinClient.hud.addNotification(Notification("No admin find.", "Admin Detector", ConnectNotificationType.OK))
            }
            return
        }
        when(notificationMode.get()){
            "Chat" -> ChatUtils.messageWithStart("[AdminDetector] Warning: find ${findAdmins.size} admin(s)![§c${Strings.join(findAdmins.toArray(arrayOfNulls<String>(0)), "§7, §c")}]")
            "Notification" -> KevinClient.hud.addNotification(Notification("Warning: find ${findAdmins.size} admin(s)![§c${Strings.join(findAdmins.toArray(arrayOfNulls<String>(0)), "§7, §c")}]", "Admin Detector", ConnectNotificationType.Warn))
        }
    }
}