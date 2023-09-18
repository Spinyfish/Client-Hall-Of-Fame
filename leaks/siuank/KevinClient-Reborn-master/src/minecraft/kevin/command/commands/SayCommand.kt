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
import net.minecraft.client.Minecraft
import net.minecraft.network.play.client.C01PacketChatMessage

class SayCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args == null || args.isEmpty()) {
            ChatUtils.messageWithStart("§cUsage: .say <Message>")
            return
        }
        var tmp = ""
        for (t in args) {
           tmp += " $t"
        }
        tmp = tmp.removePrefix(" ")
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage(tmp))
    }
}