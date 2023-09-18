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
import kevin.main.KevinClient
import kevin.utils.ChatUtils

class StateCommand : ICommand {
    override fun run(args: Array<out String>?) {
        ChatUtils.messageWithStart("§9Modules State")
        KevinClient.moduleManager.getModules().forEach {
            if (it.name!="Targets") ChatUtils.messageWithStart("§6${it.name} §9State: ${if (it.state) "§aOn" else "§cOff"}")
        }
    }
}