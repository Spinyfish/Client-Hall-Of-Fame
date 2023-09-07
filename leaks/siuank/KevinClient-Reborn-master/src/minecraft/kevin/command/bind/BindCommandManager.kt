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
package kevin.command.bind

import kevin.command.ICommand
import kevin.event.EventTarget
import kevin.event.KeyEvent
import kevin.event.Listenable
import kevin.main.KevinClient
import kevin.utils.ChatUtils
import kevin.utils.MinecraftInstance
import org.lwjgl.input.Keyboard

object BindCommandManager : MinecraftInstance(),Listenable,ICommand {
    val bindCommandList = arrayListOf<BindCommand>()
    @EventTarget
    fun onKey(event: KeyEvent) {
        val player = mc.thePlayer ?: return
        bindCommandList.forEach {
            if (it.key == event.key)
                player.sendChatMessage(it.command)
        }
    }
    override fun handleEvents() = true
    //bind list changeKey changeCommand remove
    override fun run(args: Array<out String>?) {
        if (args.isNullOrEmpty()) {
            usageMessage()
            return
        }
        val action = args[0]
        when {
            action.equals("list",true) -> {
                ChatUtils.messageWithStart("§b<BindCommands>")
                bindCommandList.forEachIndexed { index, bindCommand ->
                    ChatUtils.messageWithStart("§cID:${index + 1} §aKey:${Keyboard.getKeyName(bindCommand.key)} §9Command:${bindCommand.command}")
                }
            }
            action.equals("bind",true) -> {
                when(args.size) {
                    1 -> ChatUtils.messageWithStart("§cUsage: §9.§6bindCommand §b${args[0]} §c<Key> <Command>")
                    2 -> ChatUtils.messageWithStart("§cUsage: §9.§6bindCommand §b${args[0]} ${args[1]} §c<Command>")
                    else -> {
                        val key = Keyboard.getKeyIndex(args[1].uppercase())
                        if (key == Keyboard.KEY_NONE) {
                            ChatUtils.messageWithStart("§cError: Key '${args[1]}' does not exist.")
                        } else {
                            var command = ""
                            val list = args.toMutableList()
                            repeat(2) { list.removeFirst() }
                            list.forEachIndexed { index, s ->
                                command += if (index+1==list.size) s else "$s "
                            }
                            add(BindCommand(key, command))
                            ChatUtils.messageWithStart("§aSuccessfully bind command \"$command\" to key '${Keyboard.getKeyName(key)}'.")
                        }
                    }
                }
            }
            action.equals("remove",true) -> {
                if (args.size == 1) {
                    ChatUtils.messageWithStart("§cUsage: §9.§6bindCommand §b${args[0]} §c<ID>")
                } else {
                    val id = try {
                        args[1].toInt()
                    } catch (e: Exception) {
                        ChatUtils.messageWithStart("§cError: Cannot convert '${args[1]}' to a number.")
                        -1
                    }
                    if (id < 1) {
                        ChatUtils.messageWithStart("§cError: ID must > 0.")
                    } else {
                        if (remove(id))
                            ChatUtils.messageWithStart("§aSuccessfully remove bind command id '$id'.")
                        else
                            ChatUtils.messageWithStart("§cError: No bind command id == '$id'.")
                    }
                }
            }
            action.equals("changeKey",true) -> {
                when(args.size) {
                    1 -> ChatUtils.messageWithStart("§cUsage: §9.§6bindCommand §b${args[0]} §c<ID> <Key>")
                    2 -> ChatUtils.messageWithStart("§cUsage: §9.§6bindCommand §b${args[0]} ${args[1]} §c<Key>")
                    else -> {
                        val id = try {
                            args[1].toInt()
                        } catch (e: Exception) {
                            ChatUtils.messageWithStart("§cError: Cannot convert '${args[1]}' to a number.")
                            -1
                        }
                        if (id < 1) {
                            ChatUtils.messageWithStart("§cError: ID must > 0.")
                        } else {
                            val key = Keyboard.getKeyIndex(args[2].uppercase())
                            if (key == Keyboard.KEY_NONE) {
                                ChatUtils.messageWithStart("§cError: Key '${args[2]}' does not exist.")
                            } else {
                                changeKey(id, key)
                                ChatUtils.messageWithStart("§aSuccessfully changed the key of bind command with ID $id to ${Keyboard.getKeyName(key)}.")
                            }
                        }
                    }
                }
            }
            action.equals("changeCommand",true) -> {
                when(args.size) {
                    1 -> ChatUtils.messageWithStart("§cUsage: §9.§6bindCommand §b${args[0]} §c<ID> <Command>")
                    2 -> ChatUtils.messageWithStart("§cUsage: §9.§6bindCommand §b${args[0]} ${args[1]} §c<Command>")
                    else -> {
                        val id = try {
                            args[1].toInt()
                        } catch (e: Exception) {
                            ChatUtils.messageWithStart("§cError: Cannot convert '${args[1]}' to a number.")
                            -1
                        }
                        if (id < 1) {
                            ChatUtils.messageWithStart("§cError: ID must > 0.")
                        } else {
                            var command = ""
                            val list = args.toMutableList()
                            repeat(2) { list.removeFirst() }
                            list.forEachIndexed { index, s ->
                                command += if (index+1==list.size) s else "$s "
                            }
                            changeCommand(id, command)
                            ChatUtils.messageWithStart("§aSuccessfully changed the command of bind command with ID $id to \"$command\".")
                        }
                    }
                }
            }
            else -> usageMessage()
        }
    }
    private fun usageMessage() = ChatUtils.messageWithStart("§cUsage: .bindCommand <bind/list/changeKey/changeCommand/remove> <Value1?> <Value2?>")
    fun add(bindCommand: BindCommand) {
        bindCommandList.add(bindCommand)
        KevinClient.fileManager.saveConfig(KevinClient.fileManager.bindCommandConfig)
    }
    fun remove(id: Int): Boolean {
        if (id < 1 || id > bindCommandList.size) {
            return false
        }
        bindCommandList.removeAt(id-1)
        KevinClient.fileManager.saveConfig(KevinClient.fileManager.bindCommandConfig)
        return true
    }
    fun changeKey(id: Int, key: Int): Boolean {
        if (id < 1 || id > bindCommandList.size) {
            return false
        }
        bindCommandList[id-1].key = key
        KevinClient.fileManager.saveConfig(KevinClient.fileManager.bindCommandConfig)
        return true
    }
    fun changeCommand(id: Int, command: String): Boolean {
        if (id < 1 || id > bindCommandList.size) {
            return false
        }
        bindCommandList[id-1].command = command
        KevinClient.fileManager.saveConfig(KevinClient.fileManager.bindCommandConfig)
        return true
    }
}