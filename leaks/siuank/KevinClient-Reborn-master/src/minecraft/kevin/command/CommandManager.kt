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
package kevin.command

import kevin.command.bind.BindCommandManager
import kevin.command.commands.*
import kevin.main.KevinClient
import kevin.module.modules.misc.AdminDetector
import kevin.module.modules.misc.AutoDisable
import kevin.script.ScriptManager
import kevin.utils.ChatUtils

class CommandManager {
    val commands = HashMap<Array<String>,ICommand>()

    private val prefix = "."

    fun load(){
        commands[arrayOf("t","toggle")] = ToggleCommand()

        commands[arrayOf("h","help")] = HelpCommand()

        commands[arrayOf("bind")] = BindCommand()

        commands[arrayOf("friend")] = FriendCommand()

        commands[arrayOf("binds")] = BindsCommand()

        val modulesCommand = arrayListOf<String>()
        for (m in KevinClient.moduleManager.getModules()) modulesCommand.add(m.name)
        commands[modulesCommand.toTypedArray()] = ValueCommand()

        commands[arrayOf("say")] = SayCommand()

        commands[arrayOf("login")] = LoginCommand()

        commands[arrayOf("modulestate")] = StateCommand()

        commands[arrayOf("skin")] = SkinCommand()

        commands[arrayOf("cfg", "config")] = ConfigCommand()

        commands[arrayOf("hide")] = HideCommand()

        commands[arrayOf("AutoDisableSet")] = AutoDisable

        commands[arrayOf("ReloadScripts","ReloadScript")] = ScriptManager

        commands[arrayOf("Admin")] = AdminDetector

        commands[arrayOf("DisableAllModule")] = DisableAllCommand()

        commands[arrayOf("ClearMainConfig")] = ClearMainConfigCommand()

        commands[arrayOf("font", "fonts")] = FontCommand()

        commands[arrayOf("bindCommand")] = BindCommandManager
    }

    fun execCommand(message: String): Boolean{
        if (!message.startsWith(prefix)) return false
        val run = message.split(prefix).size > 1
        if (run) {
            val second = message.removePrefix(prefix).split(" ")
            val list = ArrayList<String>()
            list.addAll(second)
            val key = list[0]
            val command = getCommand(key)
            if (command != null) {
                if (command !is ValueCommand) list.remove(key)
                command.run(list.toTypedArray())
            } else {
                ChatUtils.message("${KevinClient.cStart} §l§4Command Not Found! Use .help for help")
            }
        }else {
            ChatUtils.message("${KevinClient.cStart} §l§4Command Not Found! Use .help for help")
        }
        return true
    }

    fun getCommand(key: String): ICommand? {
        for (entry in commands.entries){
            for (s in entry.key){
                if (s.equals(key,ignoreCase = true)) return entry.value
            }
        }
        return null
    }

}