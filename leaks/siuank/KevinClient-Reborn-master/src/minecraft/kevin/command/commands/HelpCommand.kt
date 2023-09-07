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
import kevin.utils.ChatUtils

class HelpCommand : ICommand {
    override fun run(args: Array<out String>?) {
        ChatUtils.message("§b<Help>")
        ChatUtils.message("§a.t/.toggle <ModuleName> <on/off> §9Enable/Disable module.")
        ChatUtils.message("§a.h/.help §9Show this message.")
        ChatUtils.message("§a.binds §9Show binds.")
        ChatUtils.message("§a.bind <Module> <Key> §9Bind Module To a Key.")
        ChatUtils.message("§a.binds clear §9Clear binds.")
        ChatUtils.message("§a.login <Name> <Password?> §9Login.")
        ChatUtils.message("§a.say §9Say.")
        ChatUtils.message("§a.modulestate §9Show module state.")
        ChatUtils.message("§a.<ModuleName> <Option> <Value> §9Set module option value.")
        ChatUtils.message("§a.config <save/forceSave/load/delete/rename/copy/reload/list> <From?> \\END <To?> §9Config commands.")
        ChatUtils.message("§a.skin <Set/Clear/List/Reload/Mode> <Value> §9Change your skin.")
        ChatUtils.message("§a.hide <ModuleName> §9Hide a module.")
        ChatUtils.message("§a.AutoDisableSet <ModuleName> <add/remove> <World/SetBack/All> §9Add/Remove a module to AutoDisable List.")
        ChatUtils.message("§a.reloadScripts §9Reload Scripts.")
        ChatUtils.message("§a.reloadScript §9Reload Scripts.")
        ChatUtils.message("§a.Admin <Add/Remove> <Name> §9Add admin name to detect list.")
        ChatUtils.message("§a.DisableAllModule §9Disable all modules.")
        ChatUtils.message("§a.ClearMainConfig §9Clear main config.")
        ChatUtils.message("§a.Font/Fonts <Reload/List> §9Reload/List fonts.")
        ChatUtils.message("§a.BindCommand <Bind/List/ChangeKey/ChangeCommand/remove> <Value1?> <Value2?> §9Bind a command to a key.")
    }
}