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
package kevin.command.commands

import kevin.command.ICommand
import kevin.file.ConfigManager
import kevin.main.KevinClient
import kevin.utils.ChatUtils
import java.io.File

/**
 * save load delete rename copy reload list
 *
 * 0 Values: {reload list}
 * 1 Values: {save forceSave load delete}
 * 2 Values: {rename copy}
**/
class ConfigCommand : ICommand {
    var deleteCheck: String? = null
    override fun run(args: Array<out String>?) {
        if (args.isNullOrEmpty()){
            usageMessage()
            return
        }
        when(args.size){
            1 -> {
                when{
                    args[0].equals("Reload",true) -> {
                        val t = System.currentTimeMillis()
                        ConfigManager.load()
                        ChatUtils.messageWithStart("§aSuccessfully reloaded configs,${System.currentTimeMillis()-t}ms.")
                    }
                    args[0].equals("List",true) -> {
                        ChatUtils.messageWithStart("§b<Configs>")
                        val list = arrayListOf<Pair<String,Pair<Boolean,Boolean>>>()
                        ConfigManager.configList.forEach {
                            list.add(it.name.removeSuffix(".json") to (true to false))
                        }
                        ConfigManager.hudConfigList.forEach {
                            val name = it.name.removeSuffix(".json").removeSuffix("-HUD")
                            val config = list.find { v -> v.first == name }
                            if (config!=null) {
                                val index = list.indexOf(config)
                                list.remove(config)
                                list.add(index, name to (true to true))
                            } else
                                list.add(name to (false to true))
                        }
                        list.forEachIndexed { index, v ->
                            val name = v.first
                            val hasConfig = v.second.first
                            val hasHud = v.second.second
                            when{
                                hasConfig && hasHud ->
                                    ChatUtils.messageWithStart("§c${index+1}.§7Name:§a$name")
                                hasConfig ->
                                    ChatUtils.messageWithStart("§c${index+1}.§7Name:§a$name §c(No HUD Config)")
                                hasHud ->
                                    ChatUtils.messageWithStart("§c${index+1}.§7Name:§a$name §c(No Module Config)")
                                else ->
                                    ChatUtils.messageWithStart("§c${index+1}.§7Name:§a$name §c(Not exists)")
                            }
                        }
                    }
                    args[0].equals("Save",true) || args[0].equals("forceSave",true) || args[0].equals("Load",true) || args[0].equals("Delete",true) -> {
                        ChatUtils.messageWithStart("§cUsage: §9.§6config §b${args[0]} §c<ConfigName>")
                    }
                    args[0].equals("Rename",true) || args[0].equals("Copy",true) -> {
                        ChatUtils.messageWithStart("§cUsage: §9.§6config §b${args[0]} §c<From> \\END <To>")
                    }
                    else -> usageMessage()
                }
            }
            else -> {
                var name = ""
                var name2: String? = null
                val list = args.toMutableList()
                list.removeFirst()
                if (list.contains("\\END")) {
                    val i = list.indexOfFirst { it == "\\END" }
                    val firstList = list.filterIndexed { index,_ -> index < i }
                    val secondList = list.filterIndexed { index,_ -> index > i }
                    if (firstList.isEmpty()) {
                        ChatUtils.messageWithStart("§cFirst value is Empty!!!")
                        return
                    }
                    firstList.forEachIndexed { index, it ->
                        name += if (index+1 == firstList.size) it else "$it "
                    }
                    if (secondList.isNotEmpty()) {
                        name2 = ""
                        secondList.forEachIndexed { index, it ->
                            name2 += if (index+1 == secondList.size) it else "$it "
                        }
                    }
                } else {
                    list.forEachIndexed { index, it ->
                        name += if (index+1 == list.size) it else "$it "
                    }
                }
                when{
                    args[0].equals("Save",true) -> {
                        if (ConfigManager.configList.firstOrNull { it.name == "$name.json" } != null ||
                            ConfigManager.configList.firstOrNull { it.name == "$name-HUD.json" } != null) {
                            ChatUtils.messageWithStart("§cError: Config already exists.")
                        } else try {
                            ConfigManager.saveConfig(name)
                            ChatUtils.messageWithStart("§aSuccessfully saved config §b$name.")
                        }catch (e: Exception){
                            ChatUtils.messageWithStart("§cError: $e")
                        }
                    }
                    args[0].equals("forceSave",true) -> {
                        try {
                            ConfigManager.saveConfig(name)
                            ChatUtils.messageWithStart("§aSuccessfully saved config §b$name.")
                        }catch (e: Exception){
                            ChatUtils.messageWithStart("§cError: $e")
                        }
                    }
                    args[0].equals("Load",true) -> {
                        try {
                            when(ConfigManager.loadConfig(name)){
                                0 -> {
                                    ChatUtils.messageWithStart("§aSuccessfully loaded config §b$name.")
                                }
                                1 -> {
                                    ChatUtils.messageWithStart("§eWarning: §eThe §eModules §econfig §efile §eis §emissing.§eSuccessfully §eloaded §eHUD §econfig §b$name.")
                                }
                                2 -> {
                                    ChatUtils.messageWithStart("§eWarning: §eThe §eHUD §econfig §efile §eis §emissing.§eSuccessfully §eloaded §eModules §econfig §b$name.")
                                }
                                3 -> {
                                    ChatUtils.messageWithStart("§cFailed to load config §b$name.§cFile not found.")
                                }
                            }
                        }catch (e: Exception){
                            ChatUtils.messageWithStart("§cError: $e")
                        }
                    }
                    args[0].equals("delete",true) -> {
                        if (deleteCheck == null || deleteCheck != name) {
                            deleteCheck = name
                            ChatUtils.messageWithStart("§cDeleting is a dangerous operation,run this command again to confirm the operation.")
                        } else try {
                            if (ConfigManager.deleteConfig(name))
                                ChatUtils.messageWithStart("§aSuccessfully delete config §b$name.")
                            else
                                ChatUtils.messageWithStart("§cFailed to delete config §b$name.§cFile not found.")
                        }catch (e: Exception){
                            ChatUtils.messageWithStart("§cError: $e")
                        } finally {
                            deleteCheck = null
                        }
                    }
                    args[0].equals("rename",true) -> {
                        if (name2 == null) {
                            ChatUtils.messageWithStart("§cUsage: §9.§6config §b${args[0]} §b$name \\END §c<To>")
                        } else try {
                            when(ConfigManager.renameConfig(name, name2)) {
                                0 -> ChatUtils.messageWithStart("§aSuccessfully renamed config §b$name §ato §6$name2.")
                                1 -> ChatUtils.messageWithStart("§cError: File does not exist.")
                                2 -> ChatUtils.messageWithStart("§cError: Target config already exists.")
                            }
                        }catch (e: Exception){
                            ChatUtils.messageWithStart("§cError: $e")
                        }
                    }
                    args[0].equals("copy",true) -> {
                        if (name2 == null) {
                            ChatUtils.messageWithStart("§cUsage: §9.§6config §b${args[0]} §b$name \\END §c<To>")
                        } else try {
                            when(ConfigManager.copyConfig(name, name2)) {
                                0 -> ChatUtils.messageWithStart("§aSuccessfully copied config §b$name §ato §6$name2.")
                                1 -> ChatUtils.messageWithStart("§cError: File does not exist.")
                                2 -> ChatUtils.messageWithStart("§cError: Target config already exists.")
                            }
                        }catch (e: Exception){
                            ChatUtils.messageWithStart("§cError: $e")
                        }
                    }
                    else -> usageMessage()
                }
            }
        }
    }
    private fun usageMessage() = ChatUtils.messageWithStart("§cUsage: .config <save/forceSave/load/delete/rename/copy/reload/list> <From?> \\END <To?>")
}