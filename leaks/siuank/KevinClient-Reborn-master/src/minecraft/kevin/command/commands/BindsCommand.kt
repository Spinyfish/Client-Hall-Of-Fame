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
import org.lwjgl.input.Keyboard

class BindsCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args != null && args.isNotEmpty()){
            if (args[0].equals("clear",true)){
                for (module in KevinClient.moduleManager.getModules()) module.keyBind = Keyboard.KEY_NONE
                ChatUtils.messageWithStart("§9Removed All Binds!")
                return
            }
        }
        ChatUtils.messageWithStart("§9Binds:")
        KevinClient.moduleManager.getModules().filter { it.keyBind != Keyboard.KEY_NONE }.forEach {
            ChatUtils.messageWithStart("§b> §9${it.name}: §a§l${Keyboard.getKeyName(it.keyBind)}")
        }
    }
}