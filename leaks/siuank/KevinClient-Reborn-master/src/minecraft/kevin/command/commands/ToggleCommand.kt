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
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation

class ToggleCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args == null || args.isEmpty()) {
            ChatUtils.message("${KevinClient.cStart} §cUsage: §c.t §c<ModuleName> §c<on/off> §cor §c.toggle §c<ModuleName> §c<on/off>")
            return
        }
        for (module in KevinClient.moduleManager.getModules()){
            if (module.name.equals(args[0],ignoreCase = true)){
                if (args.size > 1){
                    if (args[1].equals("on",ignoreCase = true)){
                        module.state = true
                        ChatUtils.message("${KevinClient.cStart} §aEnable §e${module.name} §9Module")
                        return
                    }else if (args[1].equals("off",ignoreCase = true)){
                        module.state = false
                        ChatUtils.message("${KevinClient.cStart} §cDisable §e${module.name} §9Module")
                        return
                    }else {
                        module.toggle()
                        ChatUtils.message("${KevinClient.cStart} §9${if (module.state) "§aEnable" else "§cDisable"} §e${module.name} §9Module")
                        return
                    }
                }else{
                    module.toggle()
                    ChatUtils.message("${KevinClient.cStart} §9${if (module.state) "§aEnable" else "§cDisable"} §e${module.name} §9Module")
                    return
                }
            }
        }
        ChatUtils.message("${KevinClient.cStart} §cNo module called ${args[0]}")
    }
}